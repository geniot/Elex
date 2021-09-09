package io.github.geniot.elex.ezip.model;

import io.github.geniot.elex.ezip.ElexUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Header implements Serializable {
    public Map<Byte, Section> sections = new HashMap<>();
    public int size = 0;

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (Section section : sections.values()) {
            byteArrayOutputStream.write(section.getBytes());
        }
        byteArrayOutputStream.write(ElexUtils.int2bytes(size));
        return byteArrayOutputStream.toByteArray();
    }

    public static Header fromBytes(RandomAccessFile randomAccessFile, int length) throws IOException {
        Header header = new Header();
        randomAccessFile.seek(randomAccessFile.length() - Integer.BYTES - length);
        int sectionsCount = length / Section.SIZE;
        for (int i = 0; i < sectionsCount; i++) {
            Section section = Section.fromBytes(randomAccessFile);
            header.sections.put(section.type, section);
        }
        header.size = randomAccessFile.readInt();
        return header;
    }
}
