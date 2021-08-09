package io.github.geniot.elex.model;

import java.util.Arrays;
import java.util.List;

import static io.github.geniot.elex.util.HtmlUtils.tagName;


public class Tag implements Comparable<Tag> {
    public String name;
    public String attr;
    public String mValue;
    public int weight = 0;
    List<String> tagsOrdered = Arrays.asList(new String[]{"m", "trn", "*", "ex", "lang", "com", "c", "i", "b", "ref", "p", "t", "sup", "sub"});

    public Tag() {
    }

    public Tag(String token) {
        name = tagName(token);
        if (name.matches("m[0-9]")) {
            mValue = name.substring(1);
            name = "m";
        }
        if (token.contains(" ")) {
            String[] splits = token.replaceAll("\\[|\\]", "").split(" ");
            if (splits.length != 2) {
                throw new RuntimeException(token);
            }
            attr = splits[1];
        }
    }

    public String open() {
        String a = attr == null ? "" : (" " + attr);
        return "[" + name + (mValue == null ? "" : mValue) + a + "]";
    }

    public String close() {
        return "[/" + name + "]";
    }

    @Override
    public int compareTo(Tag n) {
        if (n.weight != this.weight) {
            return ((Integer) this.weight).compareTo(n.weight);
        }

        if (tagsOrdered.contains(n.name) && tagsOrdered.contains(name)) {
            Integer thisScore = tagsOrdered.indexOf(name);
            Integer thatScore = tagsOrdered.indexOf(n.name);
            return thisScore.compareTo(thatScore);
        }
        return n.name.compareTo(name);
    }

    @Override
    public String toString() {
        return name;
    }

    public Tag copy() {
        Tag cp = new Tag();
        cp.name = name;
        cp.attr = attr;
        cp.mValue = mValue;
        cp.weight = weight;
        return cp;
    }

}

