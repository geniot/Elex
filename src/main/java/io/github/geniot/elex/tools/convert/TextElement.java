package io.github.geniot.elex.tools.convert;


import java.util.SortedSet;
import java.util.TreeSet;

public class TextElement {
    SortedSet<Tag> tags = new TreeSet<>();
    String text;

    public TextElement(String txt) {
        this.text = txt;
    }

    public void setTags(SortedSet<Tag> tgs) {
        this.tags.clear();
        for (Tag tag : tgs) {
            this.tags.add(tag);
        }
    }

    public void debug() {
        System.out.println(text + ":" + tags);
    }

    public void addTag(Tag t) {
        tags.add(t);
    }

}
