package io.github.geniot.elex.ezip.model;

import io.github.geniot.elex.ezip.ElexUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class Section implements Serializable {
    public int offset;
    public int length;
    public boolean isCompressed = false;

    //offset, length, isCompressed, type
    public static int SIZE = Integer.BYTES + Integer.BYTES + Byte.BYTES + Byte.BYTES;

    public static byte CONTENT_CHUNKS = 0;
    public static byte CONTENT_OFFSETS = 1;
    public static byte CONTENT_CHUNK_STARTERS = 2;
    public static byte ICON = 3;
    public static byte ANNOTATIONS = 4;
    public static byte PROPERTIES = 5;
    public static byte ABBREVIATIONS = 6;

    transient public byte type;
    transient Object value;

    public Section(byte t, int o, int l, boolean c) {
        this.type = t;
        this.offset = o;
        this.length = l;
        this.isCompressed = c;
    }

    public static Section fromBytes(RandomAccessFile randomAccessFile) throws IOException {
        int offset = randomAccessFile.readInt();
        int length = randomAccessFile.readInt();
        boolean isCompressed = randomAccessFile.readByte() == 1;
        byte type = randomAccessFile.readByte();
        return new Section(type, offset, length, isCompressed);
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(ElexUtils.int2bytes(offset));
        byteArrayOutputStream.write(ElexUtils.int2bytes(length));
        byteArrayOutputStream.write(isCompressed ? (byte) 1 : (byte) 0);
        byteArrayOutputStream.write(type);
        return byteArrayOutputStream.toByteArray();
    }

    public Object getValue(ElexDictionary elexDictionary) throws IOException {
        if (value == null) {
            byte[] bbs = new byte[length];
            elexDictionary.seek(offset);
            elexDictionary.read(bbs);
            value = ElexUtils.deserialize(isCompressed ? ElexUtils.decompressBytes(bbs) : bbs);
        }
        return value;
    }

    public Object getRawValue(ElexDictionary elexDictionary) throws IOException {
        if (value == null) {
            byte[] bbs = new byte[length];
            elexDictionary.seek(offset);
            elexDictionary.read(bbs);
            value = isCompressed ? ElexUtils.decompressBytes(bbs) : bbs;
        }
        return value;
    }

    public Object getContentsChunk(ElexDictionary elexDictionary, int chunkIndex) throws IOException {
        int[] chunkOffsets = (int[]) getValue(elexDictionary);
        int from = (chunkIndex == 0 ? 0 : chunkOffsets[chunkIndex - 1]);
        int to = chunkOffsets[chunkIndex];
        int chunkLength = to - from;
        byte[] bbs = new byte[chunkLength];
        elexDictionary.seek(from);
        elexDictionary.read(bbs);
        return bbs;
    }
}
