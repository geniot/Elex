package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
public class Dictionary implements Comparator<Dictionary>, Comparable<Dictionary> {
    private int id;
    private String name;
    private String fileName;
    private String indexLanguageCode;
    private String contentsLanguageCode;
    private boolean selected = true;
    private boolean current = true;
    private int entries;


    @Override
    public int compare(Dictionary o1, Dictionary o2) {
        return o1.name.compareTo(o2.name);
    }

    @Override
    public int compareTo(Dictionary o) {
        return this.name.compareTo(o.name);
    }
}
