package io.github.geniot.elex.handlers;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.Consumer;

public class HeadwordIterator<String> implements Iterator<String> {
    private TreeSet<String> index;
    private String from;
    private int increment;

    public HeadwordIterator(TreeSet<String> index, String from, int increment) {
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
    public String next() {
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
    public void forEachRemaining(Consumer<? super String> action) {
        Iterator.super.forEachRemaining(action);
    }
}
