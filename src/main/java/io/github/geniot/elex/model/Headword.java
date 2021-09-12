package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Headword implements Comparable<Headword> {
    private String name;
    private boolean selected;

    public Headword(String n) {
        this.name = n;
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
