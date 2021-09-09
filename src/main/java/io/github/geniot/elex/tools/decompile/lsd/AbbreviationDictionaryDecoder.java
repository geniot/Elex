package io.github.geniot.elex.tools.decompile.lsd;

import java.util.ArrayList;
import java.util.List;

public class AbbreviationDictionaryDecoder extends Decoder {
    public AbbreviationDictionaryDecoder(BitStream bstr) {
        super(bstr);
    }

    @Override
    public void read() {
        this.prefix = this.read_xored_prefix(BitStream.toInt(this.bstr.read_int()));
        this._article_symbols = this.read_xored_symbols();
        this._heading_symbols = this.read_xored_symbols();
        this._ltArticles = new LenTable(this.bstr);
        this._ltHeadings = new LenTable(this.bstr);

        this._ltPrefixLengths = new LenTable(this.bstr);
        this._ltPostfixLengths = new LenTable(this.bstr);

        this._huffman1Number = this.bstr.read_bits(32);
        this._huffman2Number = this.bstr.read_bits(32);
        this._readed = true;
        return;
    }

    List<Integer> read_xored_symbols() {
        int size = this.bstr.read_bits(32);
        int bits_per_symbol = this.bstr.read_bits(8);
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            l.add(this.bstr.read_bits(bits_per_symbol) ^ 0x1325);
        }
        return l;
    }

    String read_xored_prefix(int size) {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < size; i++) {
            int i1 = this.bstr.read_bits(16) ^ 0x879A;
            res.append((char) i1);
        }
        return res.toString();
    }

    @Override
    public void dump() {

    }
}
