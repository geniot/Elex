package io.github.geniot.elex.tools.decompile.lsd;


import static io.github.geniot.elex.tools.decompile.lsd.Tools.bit_length;

public class SystemDictionaryDecoder13 extends Decoder {
    public SystemDictionaryDecoder13(BitStream bstr) {
        super(bstr);
    }

    @Override
    public String decode_article(int size) {
        StringBuffer res = new StringBuffer();
        while (res.length() < size) {
            int sym_idx = this._ltArticles.decode();
            int sym = this._article_symbols.get(sym_idx);
            if (sym <= 0x80) {
                if (sym <= 0x3F) {
                    int start_pref_idx = this.bstr.read_bits(bit_length(this.prefix.length()));
                    int s = sym + 3;
                    res.append(this.prefix, start_pref_idx, start_pref_idx + s);
                } else {
                    int start_idx = this.bstr.read_bits(bit_length(size));
                    int s = sym - 0x3d;
                    res.append(res.substring(start_idx, start_idx + s));
                }
            } else {
                res.append((char) (sym - 0x80));
            }
        }
        return res.toString();
    }

    @Override
    public void read() {
        this.prefix = this.bstr.read_unicode(BitStream.toInt(this.bstr.read_int()), true);
        this._article_symbols = this.bstr.read_symbols();
        this._heading_symbols = this.bstr.read_symbols();
        this._ltArticles = new LenTable(this.bstr);
        this._ltHeadings = new LenTable(this.bstr);

        this._ltPrefixLengths = new LenTable(this.bstr);
        this._ltPostfixLengths = new LenTable(this.bstr);

        this._huffman1Number = this.bstr.read_bits(32);
        this._huffman2Number = this.bstr.read_bits(32);
        this._readed = true;
        return;
    }
}
