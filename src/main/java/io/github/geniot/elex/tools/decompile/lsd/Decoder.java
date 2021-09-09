package io.github.geniot.elex.tools.decompile.lsd;

import java.util.List;

public abstract class Decoder {

    protected int _dummy;
    BitStream bstr;
    String prefix;
    List<Integer> _article_symbols;
    List<Integer> _heading_symbols;
    LenTable _ltArticles;
    LenTable _ltHeadings;
    LenTable _ltPrefixLengths;
    LenTable _ltPostfixLengths;
    int _huffman1Number;
    int _huffman2Number;
    boolean _readed;

    public Decoder(BitStream bstr) {
        this.bstr = bstr;
        this.prefix = "";
        this._article_symbols = null;
        this._heading_symbols = null;
        this._ltArticles = null;
        this._ltHeadings = null;
        this._ltPrefixLengths = null;
        this._ltPostfixLengths = null;
        this._huffman1Number = 0;
        this._huffman2Number = 0;
        this._readed = false;
    }

    boolean readed() {
        return _readed;
    }

    int decode_prefix_len() {
        return this._ltPrefixLengths.decode();
    }

    int decode_postfix_len() {
        return this._ltPostfixLengths.decode();
    }

    int read_reference1() {
        return this.read_reference(this._huffman1Number);
    }

    int read_reference2() {
        return this.read_reference(this._huffman2Number);
    }

    int read_reference(int huffman_number) {
//        reference = "" ??
        int reference = 0;
        int code = this.bstr.read_bits(2);
        if (code == 3) {
            this.bstr.read_bits(32);
            return reference;
        }
        int size = Tools.bit_length(huffman_number);
        assert (size >= 2);
        return (code << (size - 2)) | this.bstr.read_bits(size - 2);
    }

    public String decode_heading(int size) {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < size; i++) {
            int sym_idx = this._ltHeadings.decode();
            int sym = this._heading_symbols.get(sym_idx);
            assert (sym <= 0xffff);  // LingvoEngine:2EAB84E8
            res.append((char) sym);
        }
        return res.toString();
    }

    public String decode_article(int size) {
        /** """
         decode User and Abrv dict
         """ **/
        StringBuffer res = new StringBuffer();
        while (res.length() < size) {
            int sym_idx = this._ltArticles.decode();
            int sym = this._article_symbols.get(sym_idx);
            if (sym >= 0x10000) {
                if (sym >= 0x10040) {
                    int start_idx = this.bstr.read_bits(Tools.bit_length(size));
                    int s = sym - 0x1003d;
                    res.append(res.substring(start_idx, start_idx + s));
                } else {
                    int prefix_idx = this.bstr.read_bits(Tools.bit_length(this.prefix.length()));
                    int s = sym - 0xfffd;
                    res.append(this.prefix, prefix_idx, prefix_idx + s);
                }
            } else {
                res.append((char) sym);
            }
        }
        return res.toString();
    }

    //need seek(bstr.header.dictionary_encoder_offset) befor call !
    public void read() {
        return;
    }

    public void dump() {
        System.out.println("Decoder:               " + this.getClass().getName());
        if (this.readed()) {
            System.out.println("    ArticleSymbols:   " + this._article_symbols.size());
            System.out.println("    HeadingSymbols:    " + this._heading_symbols.size());
            this._ltArticles.dump("Articles");
            this._ltHeadings.dump("Headings");
            this._ltPrefixLengths.dump("PrefixLengths");
            this._ltPostfixLengths.dump("PostfixLengths");
        }


    }
}
