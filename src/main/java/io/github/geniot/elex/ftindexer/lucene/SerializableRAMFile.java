package io.github.geniot.elex.ftindexer.lucene;

import org.apache.lucene.util.Accountable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class SerializableRAMFile implements Accountable, Serializable {
    public ArrayList<byte[]> buffers = new ArrayList<>();
    public long length;
    public SerializableRAMDirectory directory;
    public long sizeInBytes;


    // File used as buffer, in no RAMDirectory
    public SerializableRAMFile() {
    }

    SerializableRAMFile(SerializableRAMDirectory directory) {
        this.directory = directory;
    }

    // For non-stream access from thread that might be concurrent with writing
    public synchronized long getLength() {
        return length;
    }

    protected synchronized void setLength(long length) {
        this.length = length;
    }

    protected final byte[] addBuffer(int size) {
        byte[] buffer = newBuffer(size);
        synchronized (this) {
            buffers.add(buffer);
            sizeInBytes += size;
        }

        if (directory != null) {
            directory.sizeInBytes.getAndAdd(size);
        }
        return buffer;
    }

    protected final synchronized byte[] getBuffer(int index) {
        return buffers.get(index);
    }

    protected final synchronized int numBuffers() {
        return buffers.size();
    }

    /**
     * Expert: allocate a new buffer.
     * Subclasses can allocate differently.
     *
     * @param size size of allocated buffer.
     * @return allocated buffer.
     */
    protected byte[] newBuffer(int size) {
        return new byte[size];
    }

    @Override
    public synchronized long ramBytesUsed() {
        return sizeInBytes;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(length=" + length + ")";
    }

    @Override
    public int hashCode() {
        int h = (int) (length ^ (length >>> 32));
        for (byte[] block : buffers) {
            h = 31 * h + Arrays.hashCode(block);
        }
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SerializableRAMFile other = (SerializableRAMFile) obj;
        if (length != other.length) return false;
        if (buffers.size() != other.buffers.size()) {
            return false;
        }
        for (int i = 0; i < buffers.size(); i++) {
            if (!Arrays.equals(buffers.get(i), other.buffers.get(i))) {
                return false;
            }
        }
        return true;
    }
}
