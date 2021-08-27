package io.github.geniot.elex.handlers.index;

import java.io.IOException;
import java.util.Iterator;

public interface IPeekIterator extends Iterator {
    String peek();

    boolean contains(String headword) throws Exception;

    void setFrom(String from);
}
