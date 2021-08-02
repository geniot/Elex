package io.github.geniot.elex.model;

import io.github.geniot.dictiographer.model.Headword;

public class FullTextHit {
    private int dictionaryId;
    private Headword headword;
    private String extract;
    private float score;

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(int dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public Headword getHeadword() {
        return headword;
    }

    public void setHeadword(Headword headword) {
        this.headword = headword;
    }

    public String getExtract() {
        return extract;
    }

    public void setExtract(String extract) {
        this.extract = extract;
    }
}
