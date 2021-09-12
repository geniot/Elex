package io.github.geniot.elex.ezip.model;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.geniot.elex.ezip.ElexUtils;
import io.github.geniot.elex.tools.convert.CaseInsensitiveComparator;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeMap;

@Getter
public class ElexDictionary extends RandomAccessFile {
    private Header header;
    private String[] starters;
    private CaseInsensitiveComparator caseInsensitiveComparator = new CaseInsensitiveComparator();
    private LoadingCache<Integer, Object> chunksCache;
    private File file;

    public static final int BUFFER_SIZE = 1024 * 64;
    public static final int RESOURCES_BUFFER_SIZE = 1024 * 1024;

    public ElexDictionary(String name, String mode)
            throws IOException {
        super(name, mode);
        this.file = new File(name);
        init(name);
    }

    public ElexDictionary(File file, String mode)
            throws IOException {
        super(file, mode);
        this.file = file;
        init(file.getName());
    }


    private void init(String fileName) throws IOException {
        CacheLoader<Integer, Object> loader = new CacheLoader<Integer, Object>() {
            @Override
            public Object load(Integer pos) throws Exception {
                byte[] chunkBbs = (byte[]) header.sections.get(Section.CONTENT_OFFSETS).getContentsChunk(ElexDictionary.this, pos);
                return ElexUtils.deserialize(ElexUtils.decompressBytes(chunkBbs));
            }
        };
        if (fileName.endsWith(".ezp")) {
            chunksCache = CacheBuilder.newBuilder().maximumSize(100).build(loader);
        } else if (fileName.endsWith(".ezr")) {
            chunksCache = CacheBuilder.newBuilder().maximumSize(1).build(loader);
        } else {
            throw new RuntimeException("Unidentified file type: " + fileName);
        }

        seek(length() - Integer.BYTES);
        byte[] bbs = new byte[Integer.BYTES];
        read(bbs);
        int headerLength = ElexUtils.bytes2int(bbs);
        header = Header.fromBytes(this, headerLength);
        starters = (String[]) header.sections.get(Section.CONTENT_CHUNK_STARTERS).getValue(this);
    }

    public String first() {
        return starters[0];
    }

    public String last() throws IOException {
        int pos = starters.length - 1;
        byte[] chunk = (byte[]) header.sections.get(Section.CONTENT_OFFSETS).getContentsChunk(this, pos);
        TreeMap<String, String> content = (TreeMap<String, String>) ElexUtils.deserialize(ElexUtils.decompressBytes(chunk));
        return content.lastKey();
    }

    public String next(String headword) throws Exception {
        int chunkIndex = getChunkIndexByHeadword(headword);
        TreeMap<String, String> contentsMap = (TreeMap<String, String>) chunksCache.get(chunkIndex);
        String next = contentsMap.higherKey(headword);
        if (next == null) {
            if (chunkIndex == starters.length - 1) {
                return null;
            } else {
                ++chunkIndex;
                contentsMap = (TreeMap<String, String>) chunksCache.get(chunkIndex);
                return contentsMap.higherKey(headword);
            }
        } else {
            return next;
        }
    }

    public String previous(String headword) throws Exception {
        int chunkIndex = getChunkIndexByHeadword(headword);
        TreeMap<String, String> contentsMap = (TreeMap<String, String>) chunksCache.get(chunkIndex);
        String previous = contentsMap.lowerKey(headword);
        if (previous == null) {
            if (chunkIndex == 0) {
                return null;
            } else {
                --chunkIndex;
                contentsMap = (TreeMap<String, String>) chunksCache.get(chunkIndex);
                return contentsMap.lowerKey(headword);
            }
        } else {
            return previous;
        }
    }

    public int getSize() {
        return header.size;
    }

    public String readArticle(String headword) throws Exception {
        int chunkIndex = getChunkIndexByHeadword(headword);
        TreeMap<String, String> content = (TreeMap<String, String>) chunksCache.get(chunkIndex);
        return content.getOrDefault(headword, null);
    }

    public byte[] readResource(String fileName) throws Exception {
        int chunkIndex = getChunkIndexByHeadword(fileName);
        TreeMap<String, byte[]> content = (TreeMap<String, byte[]>) chunksCache.get(chunkIndex);
        return content.get(fileName);
    }

    public byte[] getIcon() throws IOException {
        return (byte[]) header.sections.get(Section.ICON).getRawValue(this);
    }

    public String getAnnotation() throws IOException {
        byte[] bbs = (byte[]) header.sections.get(Section.ANNOTATIONS).getRawValue(this);
        return new String(bbs, StandardCharsets.UTF_8);
    }

    public Properties getProperties() throws IOException {
        return (Properties) header.sections.get(Section.PROPERTIES).getValue(this);
    }

    public Properties getAbbreviations() throws IOException {
        return (Properties) header.sections.get(Section.ABBREVIATIONS).getValue(this);
    }

    private int getChunkIndexByHeadword(String headword) {
        int pos = Arrays.binarySearch(starters, headword, caseInsensitiveComparator);
        if (pos < 0) {
            pos = Math.abs(pos + 2);
        }
        return pos;
    }

}
