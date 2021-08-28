package io.github.geniot.elex.model;

import java.util.Comparator;

/**
 * headword is unique key but sorting is done by the first highest score in the merged results
 */
public class FullTextHit implements Comparable<FullTextHit> {
    private Headword headword;
    private int[] dictionaryIds = new int[]{};
    private String[] extracts = new String[]{};
    private float[] scores = new float[]{};

    public float[] getScores() {
        return scores;
    }

    public void setScores(float[] scores) {
        this.scores = scores;
    }

    public int[] getDictionaryIds() {
        return dictionaryIds;
    }

    public void setDictionaryIds(int[] dictionaryIds) {
        this.dictionaryIds = dictionaryIds;
    }

    public Headword getHeadword() {
        return headword;
    }

    public void setHeadword(Headword headword) {
        this.headword = headword;
    }

    public String[] getExtracts() {
        return extracts;
    }

    public void setExtracts(String[] extracts) {
        this.extracts = extracts;
    }

    @Override
    public boolean equals(Object anObject) {
        FullTextHit fullTextHit = (FullTextHit) anObject;
        return fullTextHit.getHeadword().getName().equals(this.headword.getName());
    }

    @Override
    public int compareTo(FullTextHit o) {
        return new Float(o.getScores()[0]).compareTo(this.scores[0]);
    }
}
