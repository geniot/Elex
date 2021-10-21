package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryItem implements Comparable<HistoryItem> {
    String headword;
    String sourceLanguage;
    String targetLanguage;
    Long timestamp = System.currentTimeMillis();

    @Override
    public int compareTo(HistoryItem o) {
        int comparisonResult = this.headword.compareTo(o.getHeadword());
        if (comparisonResult == 0) {
            comparisonResult = this.sourceLanguage.compareTo(o.getSourceLanguage());
        }
        if (comparisonResult == 0) {
            comparisonResult = this.targetLanguage.compareTo(o.getTargetLanguage());
        }
        return comparisonResult;
    }
}
