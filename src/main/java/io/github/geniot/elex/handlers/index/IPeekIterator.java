package io.github.geniot.elex.handlers.index;

import java.util.Iterator;

public interface IPeekIterator extends Iterator {
    String peek();

    boolean contains(String headword) throws Exception;

    void setFrom(String from);
}
