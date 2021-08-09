package io.github.geniot.elex.model;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class Availability {
    private BitSet bitSet = new BitSet();
    private Set<Integer> entryIds = new HashSet<>();

    public BitSet getBitSet() {
        return bitSet;
    }

    public void setBitSet(BitSet bitSet) {
        this.bitSet = bitSet;
    }

    public Set<Integer> getEntryIds() {
        return entryIds;
    }

    public void setEntryIds(Set<Integer> entryIds) {
        this.entryIds = entryIds;
    }
}
