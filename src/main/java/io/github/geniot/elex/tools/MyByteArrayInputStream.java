package io.github.geniot.elex.tools;

import java.io.ByteArrayInputStream;

public class MyByteArrayInputStream extends ByteArrayInputStream {
    public MyByteArrayInputStream(byte[] buf) {
        super(buf);
    }

    public MyByteArrayInputStream(byte[] buf, int offset, int length) {
        super(buf, offset, length);
    }

    public int pos() {
        return this.pos;
    }
}
