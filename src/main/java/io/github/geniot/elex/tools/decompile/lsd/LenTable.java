package io.github.geniot.elex.tools.decompile.lsd;

import java.util.ArrayList;
import java.util.List;

import static io.github.geniot.elex.tools.decompile.lsd.Tools.bit_length;


public class LenTable {
    BitStream bstr;
    int _count;
    int _bits_per_len;
    int _idx_bit_size;
    List<Integer> symidx2nodeidx;
    List<HuffmanNode> nodes;
    int next_node_position;

    public LenTable(BitStream bstr) {
        this.bstr = bstr;
        this._count = this.bstr.read_bits(32);
        this._bits_per_len = this.bstr.read_bits(8);
        this._idx_bit_size = bit_length(this._count);

        //this.symidx2nodeidx = [-1 for _ in range(this._count)];
        symidx2nodeidx = new ArrayList<>();
        for (int i = 0; i < this._count; i++) {
            symidx2nodeidx.add(-1);
        }

        //this.nodes = [HuffmanNode(0, 0, -1, -1) for _ in range(this._count - 1)];
        nodes = new ArrayList<>();
        for (int i = 0; i < this._count - 1; i++) {
            nodes.add(new HuffmanNode(0, 0, -1, -1));
        }

        int root_idx = this.nodes.size() - 1;
        this.next_node_position = 0;
        for (int i = 0; i < this._count; i++) {
            int symidx = this.bstr.read_bits(this._idx_bit_size);
            int length = this.bstr.read_bits(this._bits_per_len);
            this.place_sym_idx(symidx, root_idx, length);
        }
    }

    boolean place_sym_idx(int sym_idx, int node_idx, int size) {
        assert size > 0;
        if (size == 1) {  //time to place
            if (this.nodes.get(node_idx).left == 0) {
                this.nodes.get(node_idx).left = -1 - sym_idx;
                this.symidx2nodeidx.set(sym_idx, node_idx);
                return true;
            }
            if (this.nodes.get(node_idx).right == 0) {
                this.nodes.get(node_idx).right = -1 - sym_idx;
                this.symidx2nodeidx.set(sym_idx, node_idx);
                return true;
            }
            return false;
        }

        if (this.nodes.get(node_idx).left == 0) {
            this.nodes.set(this.next_node_position, new HuffmanNode(0, 0, node_idx, -1));
            this.next_node_position += 1;
            this.nodes.get(node_idx).left = this.next_node_position;
        }

        if (this.nodes.get(node_idx).left > 0) {
            if (this.place_sym_idx(sym_idx, this.nodes.get(node_idx).left - 1, size - 1)) {
                return true;
            }
        }

        if (this.nodes.get(node_idx).right == 0) {
            this.nodes.set(this.next_node_position, new HuffmanNode(0, 0, node_idx, -1));
            this.next_node_position += 1;
            this.nodes.get(node_idx).right = this.next_node_position;
        }

        if (this.nodes.get(node_idx).right > 0) {
            if (this.place_sym_idx(sym_idx, this.nodes.get(node_idx).right - 1, size - 1)) {
                return true;
            }
        }

        return false;
    }

    public int decode() {
        HuffmanNode node = this.nodes.get(this.nodes.size() - 1);
        int length = 0;
        while (true) {
            length += 1;
            boolean bit = this.bstr.read_bit();
            if (bit) { //right
                if (node.right < 0) { //leaf
                    int sym_idx = -1 - node.right;
                    return sym_idx;
                }
                node = this.nodes.get(node.right - 1);
            } else { //left
                if (node.left < 0) {  // leaf
                    int sym_idx = -1 - node.left;
                    return sym_idx;
                }
                node = this.nodes.get(node.left - 1);
            }
        }
    }

    public void dump(String name) {
        System.out.println("LenTable:              " + name);
        System.out.println("    Count:             " + this._count);
        System.out.println("    bitsPerLen:        " + this._bits_per_len);
        System.out.println("    IdxBitSize:        " + this._idx_bit_size);
    }
}
