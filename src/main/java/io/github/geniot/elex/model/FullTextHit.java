package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

/**
 * headword is unique key but sorting is done by the first highest score in the merged results
 */
@Getter
@Setter
public class FullTextHit implements Comparable<FullTextHit> {
    private Headword headword;
    private int[] dictionaryIds = new int[]{};
    private String[] extracts = new String[]{};
    private float[] scores = new float[]{};

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
