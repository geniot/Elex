package io.github.geniot.elex.tasks;

import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Task;
import io.github.geniot.elex.model.TaskStatus;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static io.github.geniot.elex.ftindexer.Utils.stripTags;

@Component
@Scope("prototype")
@Getter
@Setter
public class FtIndexTask implements Runnable {
    Logger logger = LoggerFactory.getLogger(FtIndexTask.class);

    private ElexDictionary elexDictionary;
    private Task task;
    @Autowired
    private AsynchronousService asynchronousService;

    @Override
    public void run() {
        try {
            task.setAction(Action.INDEX);
            task.setFileName(elexDictionary.getFile().getName());
            task.setStatus(TaskStatus.RUNNING);

            long t1 = System.currentTimeMillis();
            logger.info("Indexing " + elexDictionary.getFile().getAbsolutePath());

            String path = asynchronousService.getFtFolderPath() + File.separator + FilenameUtils.removeExtension(elexDictionary.getFile().getName());
            new File(path).mkdirs();

            Directory directory = FSDirectory.open(Paths.get(path));
            index(directory, elexDictionary);
            long t2 = System.currentTimeMillis();

            logger.info("Indexing " + elexDictionary.getFile().getAbsolutePath() + " took " + (t2 - t1) + " ms");

            task.setStatus(TaskStatus.SUCCESS);
            task.setFinishedWhen(System.currentTimeMillis());
        } catch (Exception ex) {
            task.setStatus(TaskStatus.FAILURE);
            task.setFinishedWhen(System.currentTimeMillis());
            logger.error(ex.getMessage(), ex);
        } finally {
            if (elexDictionary != null) {
                try {
                    elexDictionary.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private void index(Directory directory, ElexDictionary elexDictionary) throws Exception {
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
                    if (newPercent != percent) {
                        percent = newPercent;
                        task.setProgress(percent);
                    }
                }

            }

            w.close();
        } catch (IOException e) {
            task.setStatus(TaskStatus.FAILURE);
            task.setFinishedWhen(System.currentTimeMillis());
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
