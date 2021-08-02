package io.github.geniot.elex.handlers;

import io.github.geniot.indexedtreemap.IndexedTreeSet;

import java.util.Iterator;
import java.util.function.Consumer;

public class HeadwordIterator<Headword> implements Iterator<Headword> {
    private IndexedTreeSet<Headword> index;
    private Headword from;
    private int increment;

    public HeadwordIterator(IndexedTreeSet<Headword> index, Headword from, int increment) {
        this.index = index;
        this.from = from;
        this.increment = increment;
    }

    @Override
    public boolean hasNext() {
        if (increment > 0) {
            return index.lower(from) != null;
        } else {
            return index.higher(from) != null;
        }
    }

    @Override
    public Headword next() {
        if (increment > 0) {
            from = index.lower(from);
            return from;
        } else {
            from = index.higher(from);
            return from;
        }
    }

    @Override
    public void remove() {
        Iterator.super.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super Headword> action) {
        Iterator.super.forEachRemaining(action);
    }
}
