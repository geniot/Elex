package io.github.geniot.elex.tools.decompile.lsd;


import static io.github.geniot.elex.tools.decompile.lsd.BitStream.toInt;

public class SystemDictionaryDecoder14 extends SystemDictionaryDecoder13 {
    public SystemDictionaryDecoder14(BitStream bstr) {
        super(bstr);

    }

    @Override
    public void read() {
        int prefix_len = toInt(this.bstr.read_int());
        this.prefix = this.bstr.read_unicode(prefix_len, true);
        this._article_symbols = this.bstr.read_symbols();
        this._heading_symbols = this.bstr.read_symbols();
        this._ltArticles = new LenTable(this.bstr);
        this._ltHeadings = new LenTable(this.bstr);

        this._ltPostfixLengths = new LenTable(this.bstr);
        this._dummy = this.bstr.read_bits(32);
        this._ltPrefixLengths = new LenTable(this.bstr);

        this._huffman1Number = this.bstr.read_bits(32);
        this._huffman2Number = this.bstr.read_bits(32);
        this._readed = true;
        return;
    }

    /**
     # def decode_article(self, size):
     #     res = ""
     #     while len(res) < size:
     #         sym_idx = self._ltArticles.decode()
     #         sym = self._article_symbols[sym_idx]
     #         if sym <= 0x80:
     #             if sym <= 0x3F:
     #                 start_pref_idx = self.bstr.read_bits(tools.bit_length(len(self.prefix)))
     #                 s = sym + 3
     #                 res += self.prefix[start_pref_idx:start_pref_idx + s]
     #             else:
     #                 start_idx = self.bstr.read_bits(tools.bit_length(size))
     #                 s = sym - 0x3d
     #                 res += res[start_idx:start_idx + s]
     #         else:
     #             res += unichr(sym - 0x80)
     #     return res
     */

}
