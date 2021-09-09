package io.github.geniot.elex.ftindexer.lucene;

import org.apache.lucene.store.IndexInput;

import java.io.EOFException;
import java.io.IOException;


public class SerializableRAMInputStream extends IndexInput implements Cloneable {

    private final SerializableRAMFile file;
    private final long length;

    private byte[] currentBuffer;
    private int currentBufferIndex;

    private int bufferPosition;
    private int bufferLength;

    public SerializableRAMInputStream(String name, SerializableRAMFile f) throws IOException {
        this(name, f, f.length);
    }

    SerializableRAMInputStream(String name, SerializableRAMFile f, long length) throws IOException {
        super("RAMInputStream(name=" + name + ")");
        this.file = f;
        this.length = length;
        if (length / SerializableRAMOutputStream.BUFFER_SIZE >= Integer.MAX_VALUE) {
            throw new IOException("RAMInputStream too large length=" + length + ": " + name);
        }

        setCurrentBuffer();
    }

    @Override
    public void close() {
        // nothing to do here
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public byte readByte() throws IOException {
        if (bufferPosition == bufferLength) {
            nextBuffer();
        }
        if (currentBuffer == null) {
            throw new EOFException();
        } else {
            return currentBuffer[bufferPosition++];
        }
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
        while (len > 0) {
            if (bufferPosition == bufferLength) {
                nextBuffer();
            }

            if (currentBuffer == null) {
                throw new EOFException();
            }

            int remainInBuffer = bufferLength - bufferPosition;
            int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
            System.arraycopy(currentBuffer, bufferPosition, b, offset, bytesToCopy);
            offset += bytesToCopy;
            len -= bytesToCopy;
            bufferPosition += bytesToCopy;
        }
    }

    @Override
    public long getFilePointer() {
        return (long) currentBufferIndex * SerializableRAMOutputStream.BUFFER_SIZE + bufferPosition;
    }

    @Override
    public void seek(long pos) throws IOException {
        int newBufferIndex = (int) (pos / SerializableRAMOutputStream.BUFFER_SIZE);

        if (newBufferIndex != currentBufferIndex) {
            // we seek'd to a different buffer:
            currentBufferIndex = newBufferIndex;
            setCurrentBuffer();
        }

        bufferPosition = (int) (pos % SerializableRAMOutputStream.BUFFER_SIZE);

        // This is not >= because seeking to exact end of file is OK: this is where
        // you'd also be if you did a readBytes of all bytes in the file
        if (getFilePointer() > length()) {
            throw new EOFException("seek beyond EOF: pos=" + getFilePointer() + " vs length=" + length() + ": " + this);
        }
    }

    private void nextBuffer() throws IOException {
        // This is >= because we are called when there is at least 1 more byte to read:
        if (getFilePointer() >= length()) {
            throw new EOFException("cannot read another byte at EOF: pos=" + getFilePointer() + " vs length=" + length() + ": " + this);
        }
        currentBufferIndex++;
        setCurrentBuffer();
        assert currentBuffer != null;
        bufferPosition = 0;
    }

    private final void setCurrentBuffer() throws IOException {
        if (currentBufferIndex < file.numBuffers()) {
            currentBuffer = file.getBuffer(currentBufferIndex);
            assert currentBuffer != null;
            long bufferStart = (long) SerializableRAMOutputStream.BUFFER_SIZE * (long) currentBufferIndex;
            bufferLength = (int) Math.min(SerializableRAMOutputStream.BUFFER_SIZE, length - bufferStart);
        } else {
            currentBuffer = null;
        }
    }

    @Override
    public IndexInput slice(String sliceDescription, final long offset, final long sliceLength) throws IOException {
        if (offset < 0 || sliceLength < 0 || offset + sliceLength > this.length) {
            throw new IllegalArgumentException("slice() " + sliceDescription + " out of bounds: " + this);
        }
        return new SerializableRAMInputStream(getFullSliceDescription(sliceDescription), file, offset + sliceLength) {
            {
                seek(0L);
            }

            @Override
            public void seek(long pos) throws IOException {
                if (pos < 0L) {
                    throw new IllegalArgumentException("Seeking to negative position: " + this);
                }
                super.seek(pos + offset);
            }

            @Override
            public long getFilePointer() {
                return super.getFilePointer() - offset;
            }

            @Override
            public long length() {
                return sliceLength;
            }

            @Override
            public IndexInput slice(String sliceDescription, long ofs, long len) throws IOException {
                return super.slice(sliceDescription, offset + ofs, len);
            }
        };
    }
}

