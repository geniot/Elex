package io.github.geniot.elex.tools.decompile.lsd;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.github.geniot.elex.tools.decompile.lsd.BitStream.*;
import static io.github.geniot.elex.tools.decompile.lsd.Tools.lang_map;

/**
 * Converted from https://github.com/sv99/lsdreader
 * Particularly: https://github.com/sv99/lsdreader/blob/master/lingvoreader/lsdfile.py
 */
public class LsdFile {
    private static final Logger logger = LoggerFactory.getLogger(LsdFile.class);

    int[] xor_pad = new int[]{
            0x9C, 0xDF, 0x9B, 0xF3, 0xBE, 0x3A, 0x83, 0xD8,
            0xC9, 0xF5, 0x50, 0x98, 0x35, 0x4E, 0x7F, 0xBB,
            0x89, 0xC7, 0xE9, 0x6B, 0xC4, 0xC8, 0x4F, 0x85,
            0x1A, 0x10, 0x43, 0x66, 0x65, 0x57, 0x55, 0x54,
            0xB4, 0xFF, 0xD7, 0x17, 0x06, 0x31, 0xAC, 0x4B,
            0x42, 0x53, 0x5A, 0x46, 0xC5, 0xF8, 0xCA, 0x5E,
            0x18, 0x38, 0x5D, 0x91, 0xAA, 0xA5, 0x58, 0x23,
            0x67, 0xBF, 0x30, 0x3C, 0x8C, 0xCF, 0xD5, 0xA8,
            0x20, 0xEE, 0x0B, 0x8E, 0xA6, 0x5B, 0x49, 0x3F,
            0xC0, 0xF4, 0x13, 0x80, 0xCB, 0x7B, 0xA7, 0x1D,
            0x81, 0x8B, 0x01, 0xDD, 0xE3, 0x4C, 0x9A, 0xCE,
            0x40, 0x72, 0xDE, 0x0F, 0x26, 0xBD, 0x3B, 0xA3,
            0x05, 0x37, 0xE1, 0x5F, 0x9D, 0x1E, 0xCD, 0x69,
            0x6E, 0xAB, 0x6D, 0x6C, 0xC3, 0x71, 0x1F, 0xA9,
            0x84, 0x63, 0x45, 0x76, 0x25, 0x70, 0xD6, 0x8F,
            0xFD, 0x04, 0x2E, 0x2A, 0x22, 0xF0, 0xB8, 0xF2,
            0xB6, 0xD0, 0xDA, 0x62, 0x75, 0xB7, 0x77, 0x34,
            0xA2, 0x41, 0xB9, 0xB1, 0x74, 0xE4, 0x95, 0x1B,
            0x3E, 0xE7, 0x00, 0xBC, 0x93, 0x7A, 0xE8, 0x86,
            0x59, 0xA0, 0x92, 0x11, 0xF7, 0xFE, 0x03, 0x2F,
            0x28, 0xFA, 0x27, 0x02, 0xE5, 0x39, 0x21, 0x96,
            0x33, 0xD1, 0xB2, 0x7C, 0xB3, 0x73, 0xC6, 0xE6,
            0xA1, 0x52, 0xFB, 0xD4, 0x9E, 0xB0, 0xE2, 0x16,
            0x97, 0x08, 0xF6, 0x4A, 0x78, 0x29, 0x14, 0x12,
            0x4D, 0xC1, 0x99, 0xBA, 0x0D, 0x3D, 0xEF, 0x19,
            0xAF, 0xF9, 0x6F, 0x0A, 0x6A, 0x47, 0x36, 0x82,
            0x07, 0x9F, 0x7D, 0xA4, 0xEA, 0x44, 0x09, 0x5C,
            0x8D, 0xCC, 0x87, 0x88, 0x2D, 0x8A, 0xEB, 0x2C,
            0xB5, 0xE0, 0x32, 0xAD, 0xD3, 0x61, 0xAE, 0x15,
            0x60, 0xF1, 0x48, 0x0E, 0x7E, 0x94, 0x51, 0x0C,
            0xEC, 0xDB, 0xD2, 0x64, 0xDC, 0xFC, 0xC2, 0x56,
            0x24, 0xED, 0x2B, 0xD9, 0x1C, 0x68, 0x90, 0x79
    };

    int dummy1;
    int dummy2;
    int pages_end;
    int overlay_data;
    int header_checksum;
    public byte[] icon;
    String filename;
    boolean _readed;
    boolean _parsed;
    boolean verbose;
    BitStream bstr;
    ArticleHeadingList headings;
    OverlayReader overlay;
    public Map<ArticleHeading, String> dict;
    public Header header;
    Decoder decoder;
    int hi_version;
    int version;
    public String name;
    String first_heading;
    String last_heading;
    String capitals;
    short icon_size;

    public LsdFile(String dict_file, boolean verbose) {
        try {
            this.filename = dict_file;
            this._readed = false;
            this._parsed = false;
            this.verbose = verbose;
            this.bstr = new BitStream(FileUtils.readFileToByteArray(new File(dict_file)));

            this.overlay = null;
            this.headings = new ArticleHeadingList();
            this.dict = new LinkedHashMap<>();
            this.header = new Header(this.bstr);
            // check magic
            if (!this.header.magic.equals("LingVo")) {
                throw new RuntimeException("'Allow only Lsd \"LingVo\" ident: " + this.header.magic);
            }

            // initialize decoder
            this.decoder = null;
            this.hi_version = this.header.hi_version();
            this.version = this.header.version;
            if (hi_version == 0x11) {// lingvo 11 dictionary: 0x11001
                this.decoder = new UserDictionaryDecoder(this.bstr);
            } else if (hi_version == 0x12) {// lingvo 12 dictionary: 0x12001
                this.decoder = new UserDictionaryDecoder(this.bstr);
            } else if (hi_version == 0x13) {// x3 dictionary: 0x131001 and 0x132001 if pages count > 1000
                this.decoder = new SystemDictionaryDecoder13(this.bstr);
            } else if (hi_version == 0x14) {// x5 dictionary
                this.decoder = new SystemDictionaryDecoder13(this.bstr);
                if (version == 0x142001) {// user dictionaries
                    this.decoder = new UserDictionaryDecoder(this.bstr);
                } else if (version == 0x141004) {// system dictionaries
                    this.decoder = new SystemDictionaryDecoder14(this.bstr);
                } else if (version == 0x145001) {// abbreviation dictionaries
                    this.decoder = new AbbreviationDictionaryDecoder(this.bstr);
                }
            } else if (hi_version == 0x15) {//x6 dictionary
                if (version == 0x152001) {
                    this.decoder = new UserDictionaryDecoder(this.bstr);
                } else if (version == 0x151005) { //system dictionaries
                    this.xor_block_x6(this.header.dictionary_encoder_offset, this.header.articles_offset);
                    this.decoder = new SystemDictionaryDecoder14(this.bstr);
                } else if (version == 0x155001) {
                    this.decoder = new AbbreviationDictionaryDecoder(this.bstr);
                }
            }

            if (this.decoder == null) {
                this.dump();
                System.out.println("Not supported dictionary version: " + hex(this.header.version));
                throw new RuntimeException("Not supported dict version " + hex(this.header.version));
            }


            byte name_len = this.bstr.read_some(1)[0];
            this.name = this.bstr.read_unicode(name_len, false);
            this.first_heading = this.bstr.read_unicode(this.bstr.read_byte()[0], false);
            this.last_heading = this.bstr.read_unicode(this.bstr.read_byte()[0], false);
            int capitals_len = toInt(reverse32(this.bstr.read_int()));
            this.capitals = this.bstr.read_unicode(capitals_len, false);
            // icon v12 +
            if (this.header.version > 0x120000) {
                this.icon_size = toShort(reverse16(this.bstr.read_word()));
                this.icon = this.bstr.read(this.icon_size);
            } else {
                this.icon_size = 0;
                this.icon = null;
            }

            if (this.header.version > 0x140000) {
                this.header_checksum = toInt(reverse32(this.bstr.read_int()));
            } else {
                this.header_checksum = 0;
            }

            if (this.header.version > 0x120000) {
                this.pages_end = toInt(reverse32(this.bstr.read_int()));
                this.overlay_data = toInt(reverse32(this.bstr.read_int()));
            } else {
                this.pages_end = this.bstr.length();
                this.overlay_data = this.bstr.length();//no overlay
            }

            if (this.header.version > 0x140000) {
                this.dummy1 = toInt(reverse32(this.bstr.read_int()));
                this.dummy2 = toInt(reverse32(this.bstr.read_int()));
            } else {
                this.dummy1 = 0;
                this.dummy2 = 0;
            }

            // set bstr pos for decoding
            this.bstr.seek(this.header.dictionary_encoder_offset);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        parse();
    }

    /**
     * # x6 system dictionary table based xor decoding
     * # each block xored with start key=0x7f
     * # 1. dictionary_encoder_offset -> article_offset
     * #    must by decoded befor decoder.read()
     * # 2. annotation_offset -> dictionary_encoder_offset
     * #    annotation decoded in the read_annotation
     * # 3. each article encoded individully
     * #    articles_offset + heading.reference -> articles_offset + heading.next-reference
     * #    article decoded in the
     *
     * @param start
     * @param end
     */
    void xor_block_x6(int start, int end) {
        int key = 0x7f;
        for (int i = start; i < end; i++) {
            int b = Byte.toUnsignedInt(this.bstr.record[i]);
            this.bstr.record[i] ^= key;
            key = xor_pad[b];
        }
    }

    int pages_count() {
        return Math.floorDiv(this.pages_end - this.header.pages_offset, 512);
    }

    int get_page_offset(int page_number) {
        return this.header.pages_offset + 512 * page_number;
    }

    void read_headings() {
        for (int i = 0; i < pages_count(); i++) {
            read_heading_from_page(i);
        }
        // [-1]
        this.headings.get(this.headings.size() - 1).next_reference = this.header.pages_offset - this.header.articles_offset;
    }

    List<ArticleHeading> merge_headings() {
        List<ArticleHeading> res = new ArrayList<>();
        // fill next_reference in the headings
        ArticleHeading prev = this.headings.get(0);
        res.add(prev);
        for (int i = 1; i < this.headings.size(); i++) {
            ArticleHeading h = this.headings.get(i);
            if (prev.reference == h.reference) {
                // multititle article
                prev.merge(h);
            } else {
                res.get(res.size() - 1).next_reference = h.reference;
                res.add(h);
            }
            prev = h;
        }

        // headings[i].next_reference = headings[i+1].reference
        // set next_reference for last item to the pages_offset
        res.get(res.size() - 1).next_reference = this.header.pages_offset - this.header.articles_offset;
        return res;
    }

    void read_heading_from_page(int page_number) {
        this.bstr.seek(this.get_page_offset(page_number));
        CachePage page = new CachePage(this.bstr);
        if (page.is_leaf) {
            String prefix = "";
            for (int idx = 0; idx < page.headings_count; idx++) {
                ArticleHeading heading = new ArticleHeading();
                prefix = heading.read(this.decoder, this.bstr, prefix);
                this.headings.append(heading);
            }
        }
    }

    String read_article(ArticleHeading heading) {
        this.bstr.seek(this.header.articles_offset + heading.reference);
        if (this.header.version == 0x151005) {
            // xor article
            this.xor_block_x6(this.header.articles_offset + heading.reference,
                    this.header.articles_offset + heading.next_reference);
        }

        int size = this.bstr.read_bits(16);
        if (size == 0xFFFF) {
            size = this.bstr.read_bits(32);
        }

        String res = this.decoder.decode_article(size);
        //assert (res)
        return res;
    }

    public String read_annotation() {
        if (this.header.version == 0x151005) {
            // xor annotation
            this.xor_block_x6(this.header.annotation_offset,
                    this.header.dictionary_encoder_offset);
        }
        String res = "";
        if (this.bstr.seek(this.header.annotation_offset)) {
            int size = this.bstr.read_bits(16);
            res = this.decoder.decode_article(size);
        }
        return res;
    }

    boolean readed() {
        return _readed;
    }

    void read() {
        if (this.verbose) {
            System.out.println("reading dictionary..");
        }
        this.decoder.read();
        this._readed = true;
    }

    boolean parsed() {
        return _parsed;
    }

    public void parse() {
        if (!this.readed()) {
            this.read();
        }

        if (this.verbose) {
            logger.debug("decoding overlay..");
        }

        this.overlay = new OverlayReader(this.bstr, this.overlay_data);

        if (this.verbose) {
            logger.debug("decoding headings: " + this.header.entries_count);
        }

        this.read_headings();

        if (this.headings.appended != this.header.entries_count) {
            System.out.println("Decoded not all " + this.headings.appended + " != " + this.header.entries_count);
        }

        // merge multititle headings
        // this.headings = this.merge_headings()

        if (this.verbose) {
            logger.debug("decoding articles: " + this.headings.size());
        }

        int counter = 0;
        for (ArticleHeading h : this.headings) {
            //h.dump()
            this.dict.put(h, this.read_article(h));
            ++counter;
            if (counter % 10000 == 0) {
                logger.debug("Reading headers: " + counter + "/" + this.headings.size());
            }
        }

        this._parsed = true;
        if (this.verbose) {
            System.out.println("OK");
        }
    }

    public static String hex(int i) {
        return "0x" + Integer.toHexString(i);
    }

    private void write(String path) {
        //""" save decoded dictionary """
        if (!this.parsed()) {
            this.parse();
        }
        if (!StringUtils.isEmpty(path)) {
            path += path.endsWith(File.separator) ? "" : File.separator;
        }
        this.write_icon(path);
        this.write_annotation(path);
        this.write_overlay(path);
        this.write_dsl(path);
        if (this.verbose) {
            this.write_prefix(path);
        }
    }

    void write_icon(String path) {
        try {
            FileUtils.writeByteArrayToFile(new File(path + "out.bmp"), icon);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void write_annotation(String path) {
        String annotation = this.read_annotation();
        if (StringUtils.isEmpty(annotation)) {
            return;
        }
        try {
            FileUtils.writeStringToFile(new File(path + "out.ann"), annotation, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void write_prefix(String path) {
        //throw new NotImplementedException();
    }

    void write_overlay(String path) {

    }

    public static String normalize_article(String article) {
        return article.replaceAll("\n", "\n\t");
    }

    public String getDsl() {
        try {
            StringBuffer dsl = new StringBuffer();
            dsl.append("#NAME\t\"" + this.name + "\"\n");
            dsl.append("#INDEX_LANGUAGE\t\"" + lang_map.get(String.valueOf(this.header.source_language)) + "\"\n");
            dsl.append("#CONTENTS_LANGUAGE\t\"" + lang_map.get(String.valueOf(this.header.target_language)) + "\"\n");
            String fileNameShort = filename.substring(filename.lastIndexOf(File.separator) + 1, filename.lastIndexOf("."));
            if (this.icon_size > 0) {
                dsl.append("#ICON_FILE\t\"" + fileNameShort + ".bmp" + "\"\n");
            }
            dsl.append("\n");
            int counter = 0;
            for (Map.Entry<ArticleHeading, String> entry : dict.entrySet()) {
                ArticleHeading h = entry.getKey();
                String r = entry.getValue();
                if (h.simple()) {
                    dsl.append(h.get_first_ext_text());
                    dsl.append("\n\t");
                } else {
                    for (Heading item : h.headings) {
                        dsl.append(item.ext_text());
                        dsl.append("\n");
                    }
                    dsl.append("\t");
                }
                dsl.append(this.normalize_article(r));
                dsl.append("\n");

                ++counter;
                if (counter % 10000 == 0) {
                    logger.debug("Reading articles: " + counter + "/" + dict.entrySet().size());
                }
            }
            return dsl.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    void write_dsl(String path) {
        if (this.dict.size() == 0) {
            System.out.println("Nothing to write to dsl!");
            return;
        }
        String dsl_file = path + "out.dsl";
        try {
            OutputStreamWriter dsl = new OutputStreamWriter(new FileOutputStream(dsl_file), StandardCharsets.UTF_8);
            dsl.write(getDsl());
            dsl.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void dump() {
        this.header.dump();
        // dump header for not supported versions
        if (this.decoder != null) {
            System.out.println("Name:                  " + this.name);
            System.out.println("First heading:         " + this.first_heading);
            System.out.println("Last heading:          " + this.last_heading);
            System.out.println("Capitals:              " + this.capitals);
            System.out.println("Pages end:             " + hex(this.pages_end));
            System.out.println("Overlay data:          " + hex(this.overlay_data));
            System.out.println("Pages count:           " + Math.floorDiv(this.pages_end - this.header.pages_offset, 512));
            if (this.header.version > 0x140000) {
                System.out.println("dummy1:                " + hex(this.dummy1));
                System.out.println("dummy2:                " + hex(this.dummy2));
            }

            System.out.println("Icon enable:           " + (this.icon_size > 0));
            if (this.readed()) {
                this.decoder.dump();
                this.overlay.dump();
            }

        }

    }

    public static void main(String... args) {
        try {
            args = new String[]{"C:\\ProgramData\\ABBYY\\Lingvo\\16.0\\Dic\\System\\LingvoUniversalEnRu.lsd"};
            long t1 = System.currentTimeMillis();
            LsdFile m = new LsdFile(args[0], false);
            m.parse();
            if (m.verbose) {
                m.dump();
            }
//            m.read_article();
            m.write("out");
            long t2 = System.currentTimeMillis() - t1;
            System.out.println("Converted " + args[0] + " in " + t2 + " ms.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public byte[] toPngBytes() {
        try {
            BufferedImage input_image = null;
            input_image = ImageIO.read(new ByteArrayInputStream(icon));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(input_image, "PNG", baos);
            return baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
