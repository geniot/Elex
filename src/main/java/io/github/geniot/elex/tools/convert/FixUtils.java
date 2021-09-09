package io.github.geniot.elex.tools.convert;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixUtils {
    static Logger logger = LoggerFactory.getLogger(FixUtils.class);

    public static void fixArticles(DslDictionary dslDictionary) {
        int skipTo = 69000;
        int counter = 0;
        for (String headword : dslDictionary.getEntries().keySet()) {
            ++counter;
            if (counter % 1000 == 0) {
                logger.debug("Fixing articles: " + counter + "/" + dslDictionary.getEntries().keySet().size());
            }

            if (counter < skipTo) {
                continue;
            }

            String article = dslDictionary.getEntries().get(headword);
            article = article.replaceAll("\n+", "\n").replaceAll("\t", "");
//            if (counter == 1317) {
//                System.out.println("stop");
//            }
            article = fixArticle(article);
            dslDictionary.getEntries().put(headword, article);
        }
    }


    public static String fixArticle(String article) {
        String[] lines = article.split("\n");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
//            stringBuffer.append("\t");
            if (!(lines[i].startsWith("[c gray]©") && lines[i].endsWith("защищены.[/c]"))) {
                stringBuffer.append("\t");
                stringBuffer.append(new DslLine(lines[i]));
                stringBuffer.append("\n");
            }
        }
        return stringBuffer.toString();
    }
}
