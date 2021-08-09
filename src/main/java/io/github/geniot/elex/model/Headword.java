package io.github.geniot.elex.model;

import java.util.HashSet;
import java.util.Set;

public class Headword implements Comparable<Headword> {
    /**
     * Eg. some - what we see in the index
     */
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Headword hw) {
        return name.compareTo(hw.getName());
    }
}
