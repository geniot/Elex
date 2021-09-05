package io.github.geniot.elex.handlers.index;

import io.github.geniot.elex.ezip.model.ElexDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexIterator implements IPeekIterator {
    Logger logger = LoggerFactory.getLogger(IndexIterator.class);
    //points at next
    String from;
    Direction direction;
    ElexDictionary elexDictionary;

    public IndexIterator(ElexDictionary cd, String f, Direction d) throws Exception {
        direction = d;
        elexDictionary = cd;
        from = f;
    }


    @Override
    public boolean hasNext() {
        try {
            if (direction.equals(Direction.FORWARD)) {
                return elexDictionary.next(from) != null;
            } else {
                return elexDictionary.previous(from) != null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public String next() {
        try {
            if (direction.equals(Direction.FORWARD)) {
                from = elexDictionary.next(from);
            } else {
                from = elexDictionary.previous(from);
            }
            return from;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String peek() {
        try {
            if (!hasNext()) {
                return null;
            }
            if (direction.equals(Direction.FORWARD)) {
                return elexDictionary.next(from);
            } else {
                return elexDictionary.previous(from);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean contains(String headword) throws Exception {
        return elexDictionary.readArticle(headword) != null;
    }

    @Override
    public void setFrom(String f) {
        this.from = f;
    }

    @Override
    public void remove() {
//        throw new NotImplementedException();
    }
}

