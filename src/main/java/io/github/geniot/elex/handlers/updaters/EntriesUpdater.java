package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Entry;
import io.github.geniot.elex.model.Model;
import io.github.geniot.elex.util.HtmlUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntriesUpdater {

    public void updateEntries(Model model) throws Exception {
        List<Entry> entries = new ArrayList<>();
        if (model.getHeadwords().length > 0) {
            Map<String, String> articlesMap = DictionariesPool.getInstance().getArticles(model);
            for (String id : articlesMap.keySet()) {
                String article = articlesMap.get(id);

                article = article.replaceAll("(<<)([^>]+)(>>)", "[ref]$2[/ref]");
                article = article.replaceAll("\\{\\{Roman\\}\\}", "");
                article = article.replaceAll("\\{\\{/Roman\\}\\}", "");

                article = StringEscapeUtils.escapeHtml4(article);
                article = HtmlUtils.toHtml(model.getBaseApiUrl(), id, article);
                if (model.getAction().equals(Action.FT_LINK)) {
                    article = highlight(model, article);
                }
                entries.add(genEntry(id, model.getSelectedHeadword(), article));
            }
        }
        model.setEntries(entries.toArray(new Entry[entries.size()]));
    }

    private String highlight(Model model, String article) throws Exception {
        EnglishAnalyzer analyzer = new EnglishAnalyzer();
        Query q = new QueryParser("content", analyzer).parse(model.getSearchResultsFor());
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>");
        Highlighter highlighter = new Highlighter(formatter, new QueryScorer(q));
        highlighter.setTextFragmenter(new NullFragmenter());
        Document doc = stringToDocument(article);
        fixNode(doc.getDocumentElement(), highlighter, analyzer);
        article = documentToString(doc);
        article = article.replaceAll("&lt;span class=\"highlight\"&gt;", "<span class=\"highlight\">");
        article = article.replaceAll("&lt;/span&gt;", "</span>");
        return article;
    }

    private Document stringToDocument(String html) throws Exception {
        html = "<p>" + html + "</p>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(html));
        return builder.parse(is);
    }

    public String documentToString(Document doc) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
        return output;
    }

    private void fixNode(Node n, Highlighter highlighter, Analyzer analyzer) throws Exception {
        if (n.getNodeType() == Node.TEXT_NODE) {
            String textNode = n.getNodeValue();
            textNode = highlighter.getBestFragment(analyzer, "", textNode);
            if (textNode != null) {
                n.setNodeValue(textNode);
            }
        }
        NodeList nl = n.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            fixNode(nl.item(i), highlighter, analyzer);
        }
    }

    private Entry genEntry(String dicId, String hwd, String article) {
        Entry entry = new Entry();
        entry.setDicId(dicId);
        entry.setHeadword(hwd);
        entry.setBody(article);
        return entry;
    }
}
