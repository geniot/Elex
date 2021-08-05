package io.github.geniot.elex.model;

import java.util.HashSet;
import java.util.Set;

public class Headword implements Comparable<Headword> {
    /**
     * Eg. some - what we see in the index
     */
    private String name;
    /**
     * Eg. some{thing} - what we see in the article title, excluding brackets: something, {thing} is unsorted part
     */
    private String extendedName;
    /**
     * Eg. some{thing}\nany{thing} - what we use to find the article content, it's also our primary source
     */
    private String fileName;

    /**
     * used in ui
     */
    private boolean selected;

    public Headword() {
    }

    public Headword(String n) {
        this.name = n;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getExtendedName() {
        return extendedName;
    }

    public void setExtendedName(String extendedName) {
        this.extendedName = extendedName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return extendedName;
    }

    public static Set<Headword> referenceToHeadwords(String reference) {
        Set<Headword> set = new HashSet<>();
        String[] splits = reference.split("\n");
        for (String split : splits) {
            Headword hw = new Headword();
            hw.setFileName(reference);
            String name = split.replaceAll("\\{[^}]*\\}", "")
//                    .replaceAll("\\s+", " ")
                    .trim();
            hw.setName(name);
            hw.setExtendedName(split);
            set.add(hw);
        }
        return set;
    }

    @Override
    public int compareTo(Headword hw) {
        return name.compareTo(hw.getName());
    }
}
