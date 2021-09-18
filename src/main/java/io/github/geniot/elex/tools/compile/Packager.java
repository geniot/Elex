package io.github.geniot.elex.tools.compile;


import io.github.geniot.elex.CaseInsensitiveComparator;
import io.github.geniot.elex.ezip.ElexUtils;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.ezip.model.Header;
import io.github.geniot.elex.ezip.model.Section;
import io.github.geniot.elex.tools.convert.DslDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Packager {
    Logger logger = LoggerFactory.getLogger(Packager.class);

    public byte[] pack(DslDictionary dslDictionary) {
        try {
            List<byte[]> contentChunksList = new ArrayList<>();
            List<Integer> chunkOffsetsList = new ArrayList<>();
            List<String> chunkStartersList = new ArrayList<>();

            TreeMap<String, String> currentChunk = new TreeMap<>(new CaseInsensitiveComparator());

            int chunkOffset = 0;
            int currentChunkLength = 0;
            String chunkStarter = null;

            for (String key : dslDictionary.getEntries().keySet()) {
                String value = dslDictionary.getEntries().get(key);
                currentChunk.put(key, value);
                currentChunkLength += key.length();
                currentChunkLength += value.length();

                if (chunkStarter == null) {
                    chunkStarter = key;
                }

                //flushing
                if (currentChunkLength > ElexDictionary.BUFFER_SIZE) {
                    byte[] outBbs = ElexUtils.compressBytes(ElexUtils.serialize(currentChunk));

                    contentChunksList.add(outBbs);
                    chunkOffset += outBbs.length;
                    chunkOffsetsList.add(chunkOffset);
                    chunkStartersList.add(chunkStarter);

                    currentChunk.clear();
                    currentChunkLength = 0;
                    chunkStarter = null;
                }
            }
            //the last chunk can be quite small, maybe we should append it to the previous one?
            if (currentChunkLength > 0) {
                byte[] outBbs = ElexUtils.compressBytes(ElexUtils.serialize(currentChunk));
                contentChunksList.add(outBbs);
                chunkOffset += outBbs.length;
                chunkOffsetsList.add(chunkOffset);
                chunkStartersList.add(chunkStarter);

                currentChunk.clear();
            }

            int writeOffset = 0;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Header header = new Header();

            //content chunks
            for (byte[] chunk : contentChunksList) {
                out.write(chunk);
                writeOffset += chunk.length;
            }
            header.sections.put(Section.CONTENT_CHUNKS, new Section(Section.CONTENT_CHUNKS, 0, writeOffset, true));

            //chunk offsets
            int[] chunkOffsets = ArrayUtils.toPrimitive(chunkOffsetsList.toArray(new Integer[chunkOffsetsList.size()]));
            byte[] chunkOffsetsBbs = ElexUtils.serialize(chunkOffsets);
            out.write(chunkOffsetsBbs);
            header.sections.put(Section.CONTENT_OFFSETS, new Section(Section.CONTENT_OFFSETS, writeOffset, chunkOffsetsBbs.length, false));
            writeOffset += chunkOffsetsBbs.length;

            //headwords
            byte[] headwordsBbs = ElexUtils.compressBytes(ElexUtils.serialize(chunkStartersList.toArray(new String[chunkStartersList.size()])));
            out.write(headwordsBbs);
            header.sections.put(Section.CONTENT_CHUNK_STARTERS, new Section(Section.CONTENT_CHUNK_STARTERS, writeOffset, headwordsBbs.length, true));
            writeOffset += headwordsBbs.length;

            //icon
            byte[] iconBytes = dslDictionary.getIcon();
            out.write(iconBytes);
            header.sections.put(Section.ICON, new Section(Section.ICON, writeOffset, iconBytes.length, false));
            writeOffset += iconBytes.length;

            //annotations
            byte[] annotationBytes = ElexUtils.compressToBytes(dslDictionary.getAnnotation(), StandardCharsets.UTF_8.name());
            out.write(annotationBytes);
            header.sections.put(Section.ANNOTATIONS, new Section(Section.ANNOTATIONS, writeOffset, annotationBytes.length, true));
            writeOffset += annotationBytes.length;

            //properties
            byte[] propsBytes = ElexUtils.compressBytes(ElexUtils.serialize(dslDictionary.getProperties()));
            out.write(propsBytes);
            header.sections.put(Section.PROPERTIES, new Section(Section.PROPERTIES, writeOffset, propsBytes.length, true));
            writeOffset += annotationBytes.length;

            //abbreviations
            byte[] abbrBytes = ElexUtils.compressBytes(ElexUtils.serialize(dslDictionary.getAbbreviations()));
            out.write(abbrBytes);
            header.sections.put(Section.ABBREVIATIONS, new Section(Section.ABBREVIATIONS, writeOffset, abbrBytes.length, true));

            header.size = dslDictionary.getEntries().keySet().size();

            byte[] headerBytes = header.getBytes();
            out.write(headerBytes);
            out.write(ElexUtils.int2bytes(headerBytes.length));

            return out.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public DslDictionary unpack(byte[] bbs) {
        return null;
    }
}

