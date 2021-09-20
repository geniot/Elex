package io.github.geniot.elex.tools.convert;

import java.util.*;

import static io.github.geniot.elex.tools.convert.DslUtils.*;

public class DslLine {
    private List<TextElement> textElementList = new ArrayList<>();
    private int mValue = 1;
    private String baseApiUrl;
    private String dicId;

    public DslLine(String line, int mValue, String baseApiUrl, String dicId) {
        this.mValue = mValue;
        this.baseApiUrl = baseApiUrl;
        this.dicId = dicId;

        String[] tokens = tokenize(line);
        TreeSet<Tag> openTags = new TreeSet<>();
        for (String token : tokens) {
            if (isTag(token)) {
                Tag tag = new Tag(token);
                if (isOpening(token)) {
                    if (tag.name.equals("m")) {
                        this.mValue = tag.mValue;
                    }
                    openTags.add(tag);
                } else {
                    if (openTags.contains(tag)) {
                        openTags.remove(tag);
                    } else {
                        //found a closing tag without a matching opening
                        for (TextElement textElement : textElementList) {
                            textElement.addTag(tag);
                        }
                    }
                }
            } else {
                textElementList.add(new TextElement(token, cloneSet(openTags)));
            }
        }
    }

    private TreeSet<Tag> cloneSet(SortedSet<Tag> orig) {
        TreeSet<Tag> res = new TreeSet<>();
        for (Tag tag : orig) {
            res.add(tag.copy());
        }
        return res;
    }

    public int getMValue() {
        return mValue;
    }

    public String toHtml(String baseApiUrl, String dicId, boolean shouldHighlight, String searchWord, Properties dicProperties) {
        StringBuilder stringBuilder = new StringBuilder();
        if (mValue > 1) {
            stringBuilder.append("<span class=\"m" + mValue + "\">");
        }
        for (TextElement textElement : textElementList) {
            stringBuilder.append(textElement.toHtml(baseApiUrl, dicId, shouldHighlight, searchWord, dicProperties));
        }
        if (mValue > 1) {
            stringBuilder.append("</span>");
        }
        return stringBuilder.toString();
    }
}
