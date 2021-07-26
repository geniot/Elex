package io.github.geniot.elex.model;

import java.util.SortedSet;
import java.util.TreeSet;

public class Language implements Comparable<Language> {
    private String sourceCode;
    private boolean selected = false;
    private SortedSet<Language> targetLanguages = new TreeSet();

    public Language(String sc) {
        this.sourceCode = sc;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public SortedSet<Language> getTargetLanguages() {
        return targetLanguages;
    }

    public void setTargetLanguages(SortedSet<Language> targetLanguages) {
        this.targetLanguages = targetLanguages;
    }

    @Override
    public int compareTo(Language o) {
        return this.sourceCode.compareTo(o.sourceCode);
    }
}
