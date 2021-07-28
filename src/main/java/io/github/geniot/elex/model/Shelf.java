package io.github.geniot.elex.model;

public class Shelf {
    private int length;
    private Dictionary[] dictionaries;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Dictionary[] getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(Dictionary[] dictionaries) {
        this.dictionaries = dictionaries;
    }
}
