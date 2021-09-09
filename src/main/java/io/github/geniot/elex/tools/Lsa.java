package io.github.geniot.elex.tools;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lsa {
    static Logger logger = LoggerFactory.getLogger(Lsa.class);

    MyByteArrayInputStream _bstr;
    List<LSAEntry> _entries = new ArrayList<>();
    int _entriesCount;
    long _totalSamples;
    int _oggOffset;


    public Lsa() {
        try {
            long t1 = System.currentTimeMillis();
            byte[] bbs = FileUtils.readFileToByteArray(new File("data/lsa/SoundEn.lsa"));
            _bstr = new MyByteArrayInputStream(bbs);
            String magic = readLSAString(_bstr);
            if (!magic.equals("L9SA")) {
                throw new RuntimeException("not an LSA archive");
            }
            _entriesCount = bytes2int(reverse(read(Integer.BYTES)));
            System.out.println(_entriesCount);
            collectHeadings();
            System.out.println(_entries.size());
            long t2 = System.currentTimeMillis();
            System.out.println("Init and read headers in: " + (t2 - t1) + " ms");

            dump();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void dump() throws IOException {
        for (int i = 0; i < _entries.size(); ++i) {
            LSAEntry entry = _entries.get(i);
            int fileSampleSize = entry.sampleSize;
            if (i != _entries.size() - 1) {
                fileSampleSize = _entries.get(i + 1).sampleOffset - _entries.get(i).sampleOffset;
            }

            String name = entry.name;
            byte[] bbs = new byte[fileSampleSize * 2];
            _bstr.read(bbs);
            byte[] bbsCut = new byte[entry.sampleSize];
            System.arraycopy(bbs, 0, bbsCut, 0, bbsCut.length);

//            VorbisFile vf = new VorbisFile(new OggFile(new ByteArrayInputStream(bbsCut)));
//            List<VorbisAudioData> audio = new ArrayList<>();
//            VorbisAudioData ad;
//            while( (ad = vf.getNextAudioPacket()) != null ) {
//                audio.add(ad);
//            }
//            vf.close();
//            VorbisFile out = new VorbisFile(
//                    new FileOutputStream("data/ogg/" + name),
//                    vf.getSid(),
//                    vf.getInfo(),
//                    vf.getComment(),
//                    vf.getSetup()
//            );
//            for(VorbisAudioData vad : audio) {
//                out.writeAudioData(vad);
//            }
//            out.close();

//            FileUtils.writeByteArrayToFile(new File("data/ogg/" + name), outBbs);
        }
    }

    private byte[] read(int count) throws IOException {
        byte[] bbs = new byte[count];
        _bstr.read(bbs);
        return bbs;
    }

    public static void main(String[] args) {
        new Lsa();
    }

//    private byte[] readEntry(LSAEntry entry){
//        int fileSampleSize = entry.sampleSize;
//        if (i != _entries.size() - 1) {
//            fileSampleSize = _entries[i + 1].sampleOffset - _entries[i].sampleOffset;
//        }
//    }

    private void collectHeadings() throws IOException {
        _totalSamples = 0;
        for (int i = 0; i < _entriesCount; ++i) {
            String name = readLSAString(_bstr).trim();
            int sampleOffset = 0;
            if (i > 0) {
                sampleOffset = bytes2int(reverse(read(Integer.BYTES)));
                int marker = _bstr.read();
                if (marker == 0) {// group
                    continue;
                }
                if (marker != 0xFF) {
                    throw new RuntimeException("bad LSA file");
                }

            }
            int size = bytes2int(reverse(read(Integer.BYTES)));
            _totalSamples += size;
            _entries.add(new LSAEntry(name, sampleOffset, size));
        }
        _oggOffset = _bstr.pos();
    }

    static int bytes2int(byte[] rno) {
        return (rno[0] << 24) & 0xff000000 |
                (rno[1] << 16) & 0x00ff0000 |
                (rno[2] << 8) & 0x0000ff00 |
                (rno[3] << 0) & 0x000000ff;
    }

    static byte[] reverse(byte[] bbs) {
        byte[] newBbs = new byte[bbs.length];
        for (int i = 0; i < newBbs.length; i++) {
            newBbs[i] = bbs[bbs.length - i - 1];
        }
        return newBbs;
    }

    static String readLSAString(ByteArrayInputStream bstr) {
        StringBuilder res = new StringBuilder();
        char chr, nextchr;
        for (; ; ) {
            chr = (char) bstr.read();
            if (chr == 0xFF)
                break;
            nextchr = (char) bstr.read();
            if (nextchr == 0xFF) {
                break;
            }
            res.append((char) (chr | nextchr << 8));
        }
        return res.toString();
    }

    class LSAEntry {
        String name;
        int sampleOffset;
        int sampleSize;

        public LSAEntry(String n, int sO, int sS) {
            this.name = n;
            this.sampleOffset = sO;
            this.sampleSize = sS;
        }
    }
}
