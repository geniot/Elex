package io.github.geniot.elex.tools.convert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;

import static io.github.geniot.elex.tools.convert.DslUtils.*;


public class HtmlUtils {
    public static String DSL_STYLE = getDslStyle();
    static String noEscape = "(?<!\\\\)";
    static String validBracketO = noEscape + "\\[";
    static String validBracketC = noEscape + "\\]";
    static String anythingButBracket50 = "[^\\[]{1,50}";
    static String anythingButBracket = "[^\\[]+";
    static String anyTag = "(" + validBracketO + anythingButBracket50 + validBracketC + "|\r\n)";

    public static String getDslStyle() {
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("dsl.css"), writer, StandardCharsets.UTF_8);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHtml(String baseApiUrl, String dicId, String article) {
        String[] lines = article.split("\n");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            stringBuffer.append(toHtml(baseApiUrl, dicId, tokenize(lines[i])));
            stringBuffer.append("<br/>\n");
        }
        return stringBuffer.toString();
    }



    public static String toHtml(String baseApiUrl, String dicId, String[] tokens) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (isTag(token)) {
                if (isOpening(token)) {
                    Tag openingTag = new Tag(token);
                    if (openingTag.name.equals("b")) {
                        stringBuffer.append("<b>");
                    } else if (openingTag.name.equals("c")) {
                        stringBuffer.append("<span style=\"color:" + (openingTag.attr == null ? "green" : openingTag.attr) + "\">");
                    } else if (openingTag.name.equals("i")) {
                        stringBuffer.append("<i>");
                    } else if (openingTag.name.equals("ref")) {
                        String dataLink = collectRefValue(tokens, i + 1);
                        stringBuffer.append("<a data-link=\"" + dataLink + "\">");
                    } else if (openingTag.name.equals("s")) {
                        String dataLink = tokens[i + 1];
                        if (dataLink.endsWith(".wav")) {
                            stringBuffer.append("<span class=\"sound\" data-id=\"" + dicId + "\" " +
                                    "data-link=\"" + dataLink + "\">");
                        } else {//image?
                            stringBuffer.append("<img class=\"dicImg\" src=\"" + baseApiUrl + "/img?id=" + dicId + "&link=" + dataLink + "\" />");
                        }
                        stringBuffer.append("<span style=\"display:none\">");
                    } else {
                        String classes = htmlName(openingTag.name);
                        if (StringUtils.isNotEmpty(openingTag.mValue)) {
                            classes += openingTag.mValue;
                        }
                        if (StringUtils.isNotEmpty(openingTag.attr)) {
                            if (openingTag.attr.contains("\"")) {
                                throw new RuntimeException("Not implemented: " + openingTag.attr);
                            }
                            String attr = openingTag.attr.replaceAll("\\s|=", "");
                            classes += attr;
                        }
                        stringBuffer.append("<span class=\"" + classes + "\">");
                    }
                } else {
                    Tag closing = new Tag(token);
                    if (closing.name.equals("b")) {
                        stringBuffer.append("</b>");
                    } else if (closing.name.equals("i")) {
                        stringBuffer.append("</i>");
                    } else if (closing.name.equals("ref")) {
                        stringBuffer.append("</a>");
                    } else if (closing.name.equals("c")) {
                        stringBuffer.append("</span>");
                    } else if (closing.name.equals("s")) {
                        stringBuffer.append("</span></span>");
                    } else {
                        stringBuffer.append("</span>");
                    }

                }
            } else {
                stringBuffer.append(token.replaceAll("\\\\", ""));
            }
        }
        return stringBuffer.toString();
    }

    private static String collectRefValue(String[] tokens, int i) {
        StringBuffer stringBuffer = new StringBuffer();
        String currentToken = tokens[i];
        while (i < tokens.length - 1 && !currentToken.equals("[/ref]")) {
            if (!isTag(currentToken)) {
                stringBuffer.append(currentToken);
            }
            currentToken = tokens[++i];
        }
        return stringBuffer.toString();
    }

    public static String htmlName(String dslName) {
        if (dslName.equals("*")) {
            return "opt";
        } else if (dslName.equals("!trs")) {
            return "trs";
        } else {
            return dslName;
        }
    }

    public static String entriesToHtml(SortedMap<String, String> entriesSorted) {
        StringBuffer htmlDictionary = new StringBuffer();
        htmlDictionary.append("<html><body><head><style>" + DSL_STYLE + "</style></head>\n");
        for (String headword : entriesSorted.keySet()) {
            htmlDictionary.append("<div class=\"article\">\n");
            htmlDictionary.append("<div class=\"hwds\">\n");
            String[] hwds = headword.split("\n");
            for (String hwd : hwds) {
                htmlDictionary.append("<span class=\"hwd\">" + hwd + "</span>\n");
            }
            htmlDictionary.append("</div>\n");
            htmlDictionary.append("<div class=\"entry\">\n");
            htmlDictionary.append(entriesSorted.get(headword));
            htmlDictionary.append("</div>\n");
            htmlDictionary.append("</div>\n");
        }
        htmlDictionary.append("</body></html>");
        return htmlDictionary.toString();
    }

    public static String entryToHtml(String headword, String dslEntry) {
        StringBuffer htmlDictionary = new StringBuffer();
        htmlDictionary.append("<div class=\"article\">\n");
        htmlDictionary.append("<div class=\"hwds\">\n");
        String[] hwds = headword.split("\n");
        for (String hwd : hwds) {
            htmlDictionary.append("<span class=\"hwd\">" + hwd + "</span>\n");
        }
        htmlDictionary.append("</div>\n");
        htmlDictionary.append("<div class=\"entry\">\n");
        htmlDictionary.append(dslEntry);
        htmlDictionary.append("</div>\n");
        htmlDictionary.append("</div>\n");
        return htmlDictionary.toString();
    }
}
