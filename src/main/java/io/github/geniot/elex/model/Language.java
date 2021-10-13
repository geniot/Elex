package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

import java.util.TreeSet;

@Getter
@Setter
public class Language implements Comparable<Language> {
    private String sourceCode;
    private boolean selected = false;
    private TreeSet<Language> targetLanguages = new TreeSet<>();

    public Language(String sc, boolean s) {
        this.sourceCode = sc;
        this.selected = s;
    }

    @Override
    public int compareTo(Language o) {
        return this.sourceCode.compareTo(o.sourceCode);
    }
}
