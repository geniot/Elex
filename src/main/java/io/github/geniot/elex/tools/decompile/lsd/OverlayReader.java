package io.github.geniot.elex.tools.decompile.lsd;

public class OverlayReader {
    BitStream bstr;
    int _entriesCount;

    public OverlayReader(BitStream bstr, int offset) {
        this.bstr = bstr;

        if (this.bstr.seek(offset)) {
            this._entriesCount = this.bstr.read_bits(4);
        } else {
            this._entriesCount = 0;
        }
    }

    public void dump() {
        System.out.println("Overlay:");
        System.out.println("    EntriesCount:      " + this._entriesCount);
    }
}
