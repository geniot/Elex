package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchResult implements Comparable<SearchResult> {
    private Float score;
    private String headword;
    private String text;

    @Override
    public int compareTo(SearchResult o) {
        return score.compareTo(o.score);
    }
}

