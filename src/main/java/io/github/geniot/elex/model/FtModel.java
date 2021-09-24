package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FtModel {
    private String searchResultsFor;
    private FullTextHit[] searchResults = new FullTextHit[]{};
}
