package io.github.geniot.elex.ftindexer.lucene;

import org.apache.lucene.store.BufferedChecksum;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.util.Accountables;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class SerializableRAMOutputStream extends IndexOutput implements Accountable {
    static final int BUFFER_SIZE = 1024;

    private final SerializableRAMFile file;

    private byte[] currentBuffer;
    private int currentBufferIndex;

    private int bufferPosition;
    private long bufferStart;
    private int bufferLength;

    private final Checksum crc;

    /**
     * Construct an empty output buffer.
     */
    public SerializableRAMOutputStream() {
        this("noname", new SerializableRAMFile(), false);
    }

    /**
     * Creates this, with no name.
     */
    public SerializableRAMOutputStream(SerializableRAMFile f, boolean checksum) {
        this("noname", f, checksum);
    }

    /**
     * Creates this, with specified name.
     */
    public SerializableRAMOutputStream(String name, SerializableRAMFile f, boolean checksum) {
        super("RAMOutputStream(name=\"" + name + "\")", name);
        file = f;

        // make sure that we switch to the
        // first needed buffer lazily
        currentBufferIndex = -1;
        currentBuffer = null;
        if (checksum) {
            crc = new BufferedChecksum(new CRC32());
        } else {
            crc = null;
        }
    }

    /**
     * Copy the current contents of this buffer to the provided {@link DataOutput}.
     */
    public void writeTo(DataOutput out) throws IOException {
        flush();
        final long end = file.length;
        long pos = 0;
        int buffer = 0;
        while (pos < end) {
            int length = BUFFER_SIZE;
            long nextPos = pos + length;
            if (nextPos > end) {                        // at the last buffer
                length = (int) (end - pos);
            }
            out.writeBytes(file.getBuffer(buffer++), length);
            pos = nextPos;
        }
    }

    /**
     * Copy the current contents of this buffer to output
     * byte array
     */
    public void writeTo(byte[] bytes, int offset) throws IOException {
        flush();
        final long end = file.length;
        long pos = 0;
        int buffer = 0;
        int bytesUpto = offset;
        while (pos < end) {
            int length = BUFFER_SIZE;
            long nextPos = pos + length;
            if (nextPos > end) {                        // at the last buffer
                length = (int) (end - pos);
            }
            System.arraycopy(file.getBuffer(buffer++), 0, bytes, bytesUpto, length);
            bytesUpto += length;
            pos = nextPos;
        }
    }

    /**
     * Resets this to an empty file.
     */
    public void reset() {
        currentBuffer = null;
        currentBufferIndex = -1;
        bufferPosition = 0;
        bufferStart = 0;
        bufferLength = 0;
        file.setLength(0);
        if (crc != null) {
            crc.reset();
        }
    }

    @Override
    public void close() throws IOException {
        flush();
    }

    @Override
    public void writeByte(byte b) throws IOException {
        if (bufferPosition == bufferLength) {
            currentBufferIndex++;
            switchCurrentBuffer();
        }
        if (crc != null) {
            crc.update(b);
        }
        currentBuffer[bufferPosition++] = b;
    }

    @Override
    public void writeBytes(byte[] b, int offset, int len) throws IOException {
        assert b != null;
        if (crc != null) {
            crc.update(b, offset, len);
        }
        while (len > 0) {
            if (bufferPosition == bufferLength) {
                currentBufferIndex++;
                switchCurrentBuffer();
            }

            int remainInBuffer = currentBuffer.length - bufferPosition;
            int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
            System.arraycopy(b, offset, currentBuffer, bufferPosition, bytesToCopy);
            offset += bytesToCopy;
            len -= bytesToCopy;
            bufferPosition += bytesToCopy;
        }
    }

    private final void switchCurrentBuffer() {
        if (currentBufferIndex == file.numBuffers()) {
            currentBuffer = file.addBuffer(BUFFER_SIZE);
        } else {
            currentBuffer = file.getBuffer(currentBufferIndex);
        }
        bufferPosition = 0;
        bufferStart = (long) BUFFER_SIZE * (long) currentBufferIndex;
        bufferLength = currentBuffer.length;
    }

    private void setFileLength() {
        long pointer = bufferStart + bufferPosition;
        if (pointer > file.length) {
            file.setLength(pointer);
        }
    }

    /**
     * Forces any buffered output to be written.
     */
    protected void flush() throws IOException {
        setFileLength();
    }

    @Override
    public long getFilePointer() {
        return currentBufferIndex < 0 ? 0 : bufferStart + bufferPosition;
    }

    /**
     * Returns byte usage of all buffers.
     */
    @Override
    public long ramBytesUsed() {
        return (long) file.numBuffers() * (long) BUFFER_SIZE;
    }

    @Override
    public Collection<Accountable> getChildResources() {
        return Collections.singleton(Accountables.namedAccountable("file", file));
    }

    @Override
    public long getChecksum() throws IOException {
        if (crc == null) {
            throw new IllegalStateException("internal RAMOutputStream created with checksum disabled");
        } else {
            return crc.getValue();
        }
    }
}
