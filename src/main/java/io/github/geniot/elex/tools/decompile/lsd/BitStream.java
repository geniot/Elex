package io.github.geniot.elex.tools.decompile.lsd;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class BitStream {
    byte[] record;
    int pos;
    int in_byte_pos;

    public BitStream(byte[] r) {
        this.record = r;
        this.pos = 0;
        this.in_byte_pos = 0;
    }

    public int length() {
        return record.length;
    }

    public boolean seek(int p) {
        this.pos = p;
        in_byte_pos = 0;
        return pos < length();
    }

    public byte[] read(int len) {
        int current = pos;
        pos += len;
        byte[] res = new byte[len];
        System.arraycopy(record, current, res, 0, len);
        return res;
    }

    public void to_nearest_byte() {
        //move to start next byte, if needed
        if (in_byte_pos != 0) {
            in_byte_pos = 0;
            pos += 1;
        }
    }

    public byte[] read_byte() {
        in_byte_pos = 0;
        return new byte[]{record[pos++]};
    }

    public byte[] read_word() {
        in_byte_pos = 0;
        return new byte[]{record[pos++], record[pos++]};
    }

    public byte[] read_int() {
        in_byte_pos = 0;
        return new byte[]{record[pos++], record[pos++], record[pos++], record[pos++]};
    }

    public List<Integer> read_symbols() {
        int size = read_bits(32);
        int bits_per_symbol = read_bits(8);
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            res.add(read_bits(bits_per_symbol));
        }
        return res;
    }

    public boolean read_bit() {
        byte bt = this.record[this.pos];
        bt >>= (7 - this.in_byte_pos);
        if (this.in_byte_pos == 7) {
            this.pos += 1;
            this.in_byte_pos = 0;
        } else {
            this.in_byte_pos += 1;
        }
        return (bt & 1) == 1;
    }

    public int read_bits(int count) {
        return read_bits_o(count);
    }

    //stupid direct implementation
//    public int read_bits_s(int count) {
//        if (count > 32) {
//            throw new RuntimeException("Too many bits for read: " + count);
//        }
//        byte res = 0;
//        for (int i = 0; i < count; i++) {
//            res <<= 1;
//            res += read_bit();
//        }
//        return res;
//    }

    public int read_bits_o(int count) {
        if (count > 32) {
            throw new RuntimeException("Too many bits for read: " + count);
        }
        int[] masks = {1, 3, 7, 0xF, 0x1F, 0x3F, 0x7F, 0xFF};
        int count_bytes = Math.floorDiv(count + in_byte_pos, 8);
        if (count + this.in_byte_pos - 8 * count_bytes > 0) {
            count_bytes += 1;
        }
        // if in single raw byt
        if (count_bytes == 1) {
            if (in_byte_pos + count < 8) {
                int bt = Byte.toUnsignedInt(record[pos]);
                bt >>= 7 - in_byte_pos - count + 1;
                bt &= masks[count - 1];
                in_byte_pos += count;
                return bt;
            }
        }
        /* many raw bytes
        #   inBitPos
        #      |   count = 13    |
        # 01234567 | 01234567 | 0123456
        #
        # inBitPos = 5 count_first = 3 count_las = 2
        */
        int p = pos;
        int count_last = (count + in_byte_pos) % 8;
        int count_first = 8 - in_byte_pos;
        int byte_first = Byte.toUnsignedInt(record[p]);
        p += 1;
        byte_first &= masks[count_first - 1];
        int res = byte_first;
        // full bytes
        int full_bytes = Math.floorDiv(count - count_first, 8);
        if (full_bytes > 0) {
            for (int i = 0; i < full_bytes; i++) {
                res <<= 8;
                res += Byte.toUnsignedInt(record[p]);
                p += 1;
            }
        }
        // last byte
        if (count_last > 0) {
            int bt = Byte.toUnsignedInt(record[p]);
            bt >>= 8 - count_last;
            res <<= count_last;
            res += bt;
        }

        in_byte_pos = count_last;
        pos = p;
        return res;
    }

    public byte[] read_some(int length) {
        if (length == 1) {
            return read_byte();
        } else if (length == 2) {
            return read_word();
        } else if (length == 4) {
            return read_int();
        } else {
            throw new RuntimeException("Allowed read of byte, word and int length");
        }
    }

    public String read_unicode(int size, boolean big_indian) {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < size; i++) {
            byte[] ch = read_some(2);
            if (!big_indian) {
                ch = reverse16(ch);
            }
            res.append(Tools.int2unichr(ch));
            //# res += unichr(self.read_some(2), big_endian))
        }
        return res.toString();
    }

    public static byte[] reverse16(byte[] bbs) {
        if (bbs.length != 2) {
            throw new RuntimeException("Wrong bytes length!");
        }
        return new byte[]{bbs[1], bbs[0]};
    }

    public static byte[] reverse32(byte[] bbs) {
        if (bbs.length != 4) {
            throw new RuntimeException("Wrong bytes length!");
        }
        return new byte[]{bbs[3], bbs[2], bbs[1], bbs[0]};
    }

    public static int toInt(byte[] bbs) {
        if (bbs.length != 4) {
            throw new RuntimeException(String.valueOf(bbs));
        }
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(bbs[0]);
        bb.put(bbs[1]);
        bb.put(bbs[2]);
        bb.put(bbs[3]);
        return bb.getInt(0);
    }

    public static short toShort(byte[] bbs) {
        if (bbs.length != 2) {
            throw new RuntimeException(String.valueOf(bbs));
        }
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(bbs[0]);
        bb.put(bbs[1]);
        return bb.getShort(0);
    }

}

