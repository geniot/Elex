package io.github.geniot.elex.tools.decompile.lsd;

public class CachePage {
    int number;
    int prev;
    int parent;
    int next;
    boolean is_leaf;
    int headings_count;
    BitStream bstr;

    public CachePage(BitStream bstr) {
        this.bstr = bstr;
        this.is_leaf = bstr.read_bit();
        this.number = bstr.read_bits(16);
        this.prev = bstr.read_bits(16);
        this.parent = bstr.read_bits(16);
        this.next = bstr.read_bits(16);
        this.headings_count = bstr.read_bits(16);
        this.bstr.to_nearest_byte();
        return;
    }
}
