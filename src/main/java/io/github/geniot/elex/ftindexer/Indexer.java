package io.github.geniot.elex.ftindexer;

import io.github.geniot.elex.ezip.model.ElexDictionary;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static io.github.geniot.elex.ftindexer.Utils.stripTags;
@Component
public class Indexer {
    Logger logger = LoggerFactory.getLogger(Indexer.class);

    protected void index(String fileName, Directory directory, ElexDictionary elexDictionary) throws Exception {
        try {
            //todo: select based on index/contents language headers
            EnglishAnalyzer analyzer = new EnglishAnalyzer();

            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter w = new IndexWriter(directory, config);
            w.deleteAll();

            int size = elexDictionary.getSize();
            int counter = 0;

            String key = elexDictionary.first();
            String article = elexDictionary.readArticle(key);
            addDoc(w, key, stripTags(article));
            ++counter;
            int percent = counter * 100 / size;

            while (key != null) {
                key = elexDictionary.next(key);
                if (key != null) {
                    article = elexDictionary.readArticle(key);
                    addDoc(w, key, stripTags(article));
                    ++counter;
                    int newPercent = counter * 100 / size;
                    if (newPercent % 10 == 0 && newPercent != percent) {
                        percent = newPercent;
                        logger.info(newPercent + "% " + fileName);
                    }
                }

            }

            w.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    private void addDoc(IndexWriter w, String headword, String article) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("headword", headword, Field.Store.YES));
        doc.add(new TextField("article", article, Field.Store.YES));
        w.addDocument(doc);
    }


}

