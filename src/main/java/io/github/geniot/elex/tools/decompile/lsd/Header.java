package io.github.geniot.elex.tools.decompile.lsd;

import java.nio.charset.StandardCharsets;

public class Header {
    private final int unk;
    private final int checksum;
    private final int unk1;
    private final short unk2;
    private final short unk3;
    public int version;
    public int dictionary_encoder_offset;
    public int articles_offset;
    public int pages_offset;
    public int annotation_offset;
    public int entries_count;
    public short source_language;
    public short target_language;
    BitStream bstr;
    String magic;

    public Header(BitStream bstr) {
        this.bstr = bstr;
        this.magic = new String(this.bstr.read(8), StandardCharsets.UTF_8).trim();
        this.version = BitStream.toInt(BitStream.reverse32(this.bstr.read_int()));
        this.unk = BitStream.toInt(BitStream.reverse32(this.bstr.read_int()));
        this.checksum = BitStream.toInt(BitStream.reverse32(this.bstr.read_int()));
        this.entries_count = BitStream.toInt(BitStream.reverse32(this.bstr.read_int()));
        this.annotation_offset = BitStream.toInt(BitStream.reverse32(this.bstr.read_int()));
        this.dictionary_encoder_offset = BitStream.toInt(BitStream.reverse32(this.bstr.read_int()));
        this.articles_offset = BitStream.toInt(BitStream.reverse32(this.bstr.read_int()));
        this.pages_offset = BitStream.toInt(BitStream.reverse32(this.bstr.read_int()));
        this.unk1 = BitStream.toInt(BitStream.reverse32(this.bstr.read_int()));
        this.unk2 = BitStream.toShort(BitStream.reverse16(this.bstr.read_word()));
        this.unk3 = BitStream.toShort(BitStream.reverse16(this.bstr.read_word()));
        this.source_language = BitStream.toShort(BitStream.reverse16(this.bstr.read_word()));
        this.target_language = BitStream.toShort(BitStream.reverse16(this.bstr.read_word()));
    }

    int hi_version() {
        return this.version >> 16;
    }

    public void dump() {
        System.out.println("Header:");
        System.out.println("    Magic:             " + this.magic);
        System.out.println("    Checksume:         " + LsdFile.hex(this.checksum));
        System.out.println("    Version:           " + (LsdFile.hex(this.hi_version()) + " " + LsdFile.hex(this.version)));
        System.out.println("    Entries:           " + this.entries_count);
        System.out.println("    AnnotationOffset:  " + LsdFile.hex(this.annotation_offset));
        System.out.println("    DictionaryEncoderOffset: " + LsdFile.hex(this.dictionary_encoder_offset));
        System.out.println("    ArticlesOffset:    " + LsdFile.hex(this.articles_offset));
        System.out.println("    Pages start:       " + LsdFile.hex(this.pages_offset));
        System.out.println("    Source language:   " + (this.source_language + " " + Tools.lang_map.get(String.valueOf(this.source_language))));
        System.out.println("    Target language:   " + (this.target_language + " " + Tools.lang_map.get(String.valueOf(this.target_language))));
    }
}
