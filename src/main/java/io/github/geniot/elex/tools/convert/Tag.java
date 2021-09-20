package io.github.geniot.elex.tools.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static io.github.geniot.elex.tools.convert.DslUtils.tagName;
import static io.github.geniot.elex.tools.convert.HtmlUtils.htmlName;


public class Tag implements Comparable<Tag> {
    Logger logger = LoggerFactory.getLogger(Tag.class);
    public String name;
    public String attr;
    public int mValue = 1;
    List<String> tagsOrdered = Arrays.asList(new String[]{"m", "trn", "*", "ex", "lang", "com", "c", "i", "b", "ref", "p", "t", "sup", "sub"});

    public Tag() {
    }

    public Tag(String token) {
        name = htmlName(tagName(token));
        if (name.matches("m[0-9]")) {
            try {
                mValue = Integer.parseInt(name.substring(1));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            name = "m";
        }
        if (token.contains(" ")) {
            String[] splits = token.replaceAll("\\[|\\]", "").split(" ");
            attr = splits[1];
        }
    }

    @Override
    public int compareTo(Tag n) {
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
        return cp;
    }

    public String toOpeningHtml(String text, String baseApiUrl, String dicId, Properties dicProps) {
        if (name.equals("m")) {
            return "";
        }
        if (name.equals("c")) {
            return "<span style=\"color:" + (attr == null ? "green" : attr) + "\">";
        }
        if (name.equals("b")) {
            return "<b>";
        }
        if (name.equals("i")) {
            return "<i>";
        }
        if (name.equals("ref")) {
            return "<a data-link=\"" + text + "\">";
        }
        if (name.equals("p")) {
            String tooltip = dicProps.containsKey(text) ? dicProps.getProperty(text) : null;
            String title = tooltip == null ? "" : ("title=\"" + tooltip + "\"");
            return "<span class=\"p\" " + title + ">";
        }
        if (name.equals("s")) {
            if (text.endsWith(".wav")) {
                return "<span class=\"sound\" data-id=\"" + dicId + "\" " + "data-link=\"" + text + "\"><span style=\"display:none\">";
            } else {//image?
                return "<img class=\"dicImg\" src=\"" + baseApiUrl + "/img?id=" + dicId + "&link=" + text + "\" /><span style=\"display:none\">";
            }
        }
        return "<span class=\"" + name + "\">";
    }

    public String toClosingHtml() {
        if (name.equals("m")) {
            return "";
        }
        if (name.equals("b")) {
            return "</b>";
        }
        if (name.equals("i")) {
            return "</i>";
        }
        if (name.equals("ref")) {
            return "</a>";
        }
        if (name.equals("s")) {
            return "</span></span>";
        }
        return "</span>";
    }
}

