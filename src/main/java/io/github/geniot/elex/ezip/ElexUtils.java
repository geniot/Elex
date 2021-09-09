package io.github.geniot.elex.ezip;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ElexUtils {
    static Logger logger = LoggerFactory.getLogger(ElexUtils.class);

    public static byte[] int2bytes(int i) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(i).array();
    }

    public static int bytes2int(byte[] rno) {
        return (rno[0] << 24) & 0xff000000 |
                (rno[1] << 16) & 0x00ff0000 |
                (rno[2] << 8) & 0x0000ff00 |
                (rno[3] << 0) & 0x000000ff;
    }

    public static byte[] compressToBytes(final String data, final String encoding)
            throws IOException {
        if (data == null || data.length() == 0) {
            return null;
        } else {
            byte[] bytes = data.getBytes(encoding);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream os = new GZIPOutputStream(baos);
            os.write(bytes, 0, bytes.length);
            os.close();
            byte[] result = baos.toByteArray();
            return result;
        }
    }

    public static byte[] compressBytes(byte[] bytes)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        GZIPOutputStream os = new GZIPOutputStream(baos);

//        GZIPOutputStream os = new GZIPOutputStream(baos) {
//            {
//                this.def.setLevel(Deflater.BEST_COMPRESSION);
//            }
//        };

        os.write(bytes, 0, bytes.length);
        os.close();
        byte[] result = baos.toByteArray();
        return result;
    }

    public static String decompressString(final byte[] data, final String encoding)
            throws IOException {
        if (data == null || data.length == 0) {
            return null;
        } else {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            GZIPInputStream is = new GZIPInputStream(bais);
            byte[] tmp = new byte[256];
            while (true) {
                int r = is.read(tmp);
                if (r < 0) {
                    break;
                }
                buffer.write(tmp, 0, r);
            }
            is.close();

            byte[] content = buffer.toByteArray();
            return new String(content, 0, content.length, encoding);
        }
    }

    public static byte[] decompressBytes(final byte[] data)
            throws IOException {
        if (data == null || data.length == 0) {
            return null;
        } else {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            GZIPInputStream is = new GZIPInputStream(bais);
            byte[] tmp = new byte[256];
            while (true) {
                int r = is.read(tmp);
                if (r < 0) {
                    break;
                }
                buffer.write(tmp, 0, r);
            }
            is.close();

            return buffer.toByteArray();
        }
    }

    public static byte[] serialize(Serializable serializable) {
        try {
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(serializable);
            out.close();
            fileOut.close();
            return fileOut.toByteArray();
        } catch (IOException i) {
            logger.error(i.getMessage(), i);
            return null;
        }
    }

    public static Serializable deserialize(byte[] bbs) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bbs));
            Serializable d = (Serializable) in.readObject();
            in.close();
            return d;
        } catch (Exception i) {
            logger.error(i.getMessage(), i);
            return null;
        }
    }

}
