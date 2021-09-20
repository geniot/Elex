package io.github.geniot.elex.tools.convert;


import java.util.TreeSet;

import static io.github.geniot.elex.tools.convert.HtmlUtils.postTag;
import static io.github.geniot.elex.tools.convert.HtmlUtils.preTag;

public class TextElement {
    TreeSet<Tag> tags;
    String text;

    public TextElement(String txt, TreeSet<Tag> tgs) {
        this.text = txt.replaceAll("\\\\", "");
        this.tags = tgs;
    }

    public void addTag(Tag t) {
        tags.add(t);
    }

    public String toHtml(String baseApiUrl, String dicId, boolean shouldHighlight, String searchWord) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Tag tag : tags.descendingSet()) {
            stringBuilder.append(tag.toOpeningHtml(text, baseApiUrl, dicId));
        }

        if (shouldHighlight) {
            text = HtmlUtils.highlight(searchWord, text, preTag, postTag);
        }
        stringBuilder.append(text);

        for (Tag tag : tags) {
            stringBuilder.append(tag.toClosingHtml());
        }
        return stringBuilder.toString();
    }
}
