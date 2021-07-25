package io.github.geniot.elex.model;

public class SearchResult {
    private Headword[] headwords;
    private FullTextHit[] hits;

    public FullTextHit[] getHits() {
        return hits;
    }

    public void setHits(FullTextHit[] hits) {
        this.hits = hits;
    }

    public Headword[] getHeadwords() {
        return headwords;
    }

    public void setHeadwords(Headword[] headwords) {
        this.headwords = headwords;
    }
}
