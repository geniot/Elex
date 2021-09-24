package io.github.geniot.elex.tools.convert;


import java.util.List;
import java.util.Properties;

import static io.github.geniot.elex.tools.convert.HtmlUtils.postTag;
import static io.github.geniot.elex.tools.convert.HtmlUtils.preTag;

public class TextElement {
    List<Tag> tags;
    String text;

    public TextElement(String txt, List<Tag> tgs) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            if (c != '\\' && c != '{' && c != '}') {
                stringBuilder.append(c);
            }
        }
        this.text = stringBuilder.toString();

        this.tags = tgs;
    }

    public void addTag(Tag t) {
        tags.add(t);
    }

    public String toHtml(String baseApiUrl, String dicId, boolean shouldHighlight, String searchWord, Properties dicProps) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);
            stringBuilder.append(tag.toOpeningHtml(text, baseApiUrl, dicId, dicProps));
        }

        if (shouldHighlight) {
            text = HtmlUtils.highlight(searchWord, text, preTag, postTag);
        }
        stringBuilder.append(text);

        for (int i = tags.size() - 1; i >= 0; i--) {
            Tag tag = tags.get(i);
            stringBuilder.append(tag.toClosingHtml());
        }
        return stringBuilder.toString();
    }
}
