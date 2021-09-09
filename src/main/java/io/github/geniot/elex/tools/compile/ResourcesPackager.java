package io.github.geniot.elex.tools.compile;


import io.github.geniot.elex.ezip.ElexUtils;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.ezip.model.Header;
import io.github.geniot.elex.ezip.model.Section;
import io.github.geniot.elex.tools.convert.CaseInsensitiveComparator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ResourcesPackager {
    Logger logger = LoggerFactory.getLogger(ResourcesPackager.class);

    public void pack(SortedMap<String, File> files, OutputStream out) {
        try {
            List<Integer> chunkOffsetsList = new ArrayList<>();
            List<String> chunkStartersList = new ArrayList<>();

            TreeMap<String, byte[]> currentChunk = new TreeMap<>(new CaseInsensitiveComparator());

            int writeOffset = 0;
            int currentChunkLength = 0;
            String chunkStarter = null;
            int counter = 0;

            for (String key : files.keySet()) {
                ++counter;
                if (counter % 1000 == 0) {
                    System.out.println(counter);
                }

                byte[] value = FileUtils.readFileToByteArray(files.get(key));
                currentChunk.put(key, value);
                currentChunkLength += key.length();
                currentChunkLength += value.length;

                if (chunkStarter == null) {
                    chunkStarter = key;
                }

                //flushing
                if (currentChunkLength > ElexDictionary.RESOURCES_BUFFER_SIZE) {
                    byte[] outBbs = ElexUtils.compressBytes(ElexUtils.serialize(currentChunk));

                    out.write(outBbs);
                    writeOffset += outBbs.length;
                    chunkOffsetsList.add(writeOffset);
                    chunkStartersList.add(chunkStarter);

                    currentChunk.clear();
                    currentChunkLength = 0;
                    chunkStarter = null;
                }
            }
            //the last chunk can be quite small, maybe we should append it to the previous one?
            if (currentChunkLength > 0) {
                byte[] outBbs = ElexUtils.compressBytes(ElexUtils.serialize(currentChunk));
                out.write(outBbs);
                writeOffset += outBbs.length;
                chunkOffsetsList.add(writeOffset);
                chunkStartersList.add(chunkStarter);

                currentChunk.clear();
            }


            Header header = new Header();

            //content chunks
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


            byte[] headerBytes = header.getBytes();
            out.write(headerBytes);
            out.write(ElexUtils.int2bytes(headerBytes.length));

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
