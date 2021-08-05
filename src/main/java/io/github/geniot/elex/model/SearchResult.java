package io.github.geniot.elex.model;

public class SearchResult implements Comparable<SearchResult> {
    private Float score;
    private String headword;
    private String text;

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getHeadword() {
        return headword;
    }

    public void setHeadword(String headword) {
        this.headword = headword;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int compareTo(SearchResult o) {
        return score.compareTo(o.score);
    }
}

