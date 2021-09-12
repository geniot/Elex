package io.github.geniot.elex.handlers.index;

import io.github.geniot.elex.tools.convert.CaseInsensitiveComparator;
import io.github.geniot.elex.ezip.model.ElexDictionary;

import java.util.HashSet;
import java.util.Set;

public class IteratorsWrapper implements IPeekIterator {
    private Set<IndexIterator> iterators = new HashSet<>();
    Direction direction;
    String searchValue;
    CaseInsensitiveComparator collator = new CaseInsensitiveComparator();

    public IteratorsWrapper(Set<ElexDictionary> set, String sv, Direction d) throws Exception {
        for (ElexDictionary cd : set) {
            IndexIterator si = new IndexIterator(cd, sv, d);
            iterators.add(si);
            direction = d;
            searchValue = sv;
        }
    }

    public void setFrom(String f) {
        this.searchValue = f;
        for (IndexIterator ii : iterators) {
            ii.setFrom(f);
        }
    }

    @Override
    public boolean hasNext() {
        for (IndexIterator ii : iterators) {
            if (ii.hasNext()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String next() {
        String nextCandidate = peek();
        for (IndexIterator ii : iterators) {
            String peek = ii.peek();
            if (peek == null) {
                continue;
            }
            if (direction.equals(Direction.FORWARD)) {
                if (collator.compare(peek, nextCandidate) <= 0) {
                    ii.next();
                }
            } else {
                if (collator.compare(peek, nextCandidate) >= 0) {
                    ii.next();
                }
            }
        }
        return nextCandidate;
    }

    @Override
    public String peek() {
        String nextCandidate = null;
        //find the best candidate
        for (IndexIterator ii : iterators) {
            String peek = ii.peek();

            if (peek == null) {
                continue;
            }

            if (peek.equals(searchValue)) {
                nextCandidate = peek;
                break;
            }

            if (ii.hasNext()) {
                if (direction.equals(Direction.FORWARD)) {
                    if (nextCandidate == null || collator.compare(peek, nextCandidate) < 0) {
                        nextCandidate = peek;
                    }
                } else {
                    if (nextCandidate == null || collator.compare(peek, nextCandidate) > 0) {
                        nextCandidate = peek;
                    }
                }
            }
        }
        return nextCandidate;
    }

    @Override
    public boolean contains(String headword) throws Exception {
        for (IndexIterator ii : iterators) {
            if (ii.contains(headword)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void remove() {
//        throw new NotImplementedException();
    }
}

