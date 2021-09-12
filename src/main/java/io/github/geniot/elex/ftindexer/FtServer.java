package io.github.geniot.elex.ftindexer;

import io.github.geniot.elex.ezip.model.ElexDictionary;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@Component
@Getter
@Setter
public class FtServer extends FileAlterationListenerAdaptor {
    Logger logger = LoggerFactory.getLogger(FtServer.class);

    @Value("${path.data}")
    private String pathToData;
    @Value("${name.folder.ft-index}")
    private String ftIndexFolderName;

    private String ftFolderPath;

    @Autowired
    private Indexer indexer;
    @Autowired
    private Searcher searcher;

    @PostConstruct
    public void init() {
        ftFolderPath = new File(pathToData + File.separator + ftIndexFolderName).getAbsolutePath();
    }

    private Map<String, Directory> directoriesCache = new HashMap<>();

    public SortedMap<Float, String[]> search(String fileName, String query, int hitsPerPage) throws IOException {
        Directory directory = getIndexByDictionaryFileName(FilenameUtils.removeExtension(fileName));
        if (directory != null) {
            SortedMap<Float, String[]> result = searcher.search(directory, query, hitsPerPage);
            return result;
        } else {
            return null;
        }
    }

    public long getDirectorySize(String fileName) throws IOException {
        long result = 0;
        Directory directory = getIndexByDictionaryFileName(FilenameUtils.removeExtension(fileName));
        String[] files = directory.listAll();
        for (String file : files) {
            String pathToIndex = ftFolderPath + File.separator + FilenameUtils.removeExtension(fileName) + File.separator + file;
            result += new File(pathToIndex).length();
        }
        return result;
    }

    private Directory getIndexByDictionaryFileName(String fileName) throws IOException {
        Directory directory = directoriesCache.get(fileName);
        if (directory == null) {
            String pathToIndex = ftFolderPath + File.separator + fileName;
            directory = FSDirectory.open(Paths.get(pathToIndex));
            directoriesCache.put(fileName, directory);
        }
        return directory;
    }

    public void stop() {
        for (String key : directoriesCache.keySet()) {
            Directory value = directoriesCache.get(key);
            try {
                value.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private Set<String> toNames(File[] files) {
        Set<String> set = new HashSet<>();
        for (File f : files) {
            set.add(FilenameUtils.removeExtension(f.getName()));
        }
        return set;
    }

    synchronized public void update() {
        try {
            long t1 = System.currentTimeMillis();

            File dataFolder = new File(pathToData);
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                logger.warn("Couldn't create " + dataFolder);
            }
            File ftIndexFolder = new File(ftFolderPath);
            if (!ftIndexFolder.exists() && !ftIndexFolder.mkdirs()) {
                logger.warn("Couldn't create " + ftIndexFolder);
            }

            File[] dicFiles = dataFolder.listFiles();
            File[] indexFiles = ftIndexFolder.listFiles();

            Set<String> dicFilesSet = toNames(dicFiles);
            Set<String> indexFilesSet = toNames(indexFiles);

            //removing orphan indexes that do not have corresponding dictionary file
            for (File indexFile : indexFiles) {
                if (!dicFilesSet.contains(FilenameUtils.removeExtension(indexFile.getName()))) {
                    indexFile.delete();
                }
            }

            //installing
            for (File dicFile : dicFiles) {
                if (dicFile.isDirectory()) {
                    //skip
                } else if (dicFile.isFile() &&
                        dicFile.getName().endsWith(".ezp") &&
                        !indexFilesSet.contains(FilenameUtils.removeExtension(dicFile.getName()))) {

                    reindex(dicFile);
                }
            }

            long t2 = System.currentTimeMillis();

            logger.info("Updated ft index in: " + (t2 - t1) + " ms");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void reindex(File dicFile) throws IOException {
        ElexDictionary elexDictionary = null;
        try {
            logger.info("Indexing " + dicFile.getAbsolutePath());
            elexDictionary = new ElexDictionary(dicFile.getAbsolutePath(), "r");

            String path = ftFolderPath + File.separator + FilenameUtils.removeExtension(dicFile.getName());
            new File(path).mkdirs();

            Directory directory = FSDirectory.open(Paths.get(path));
            indexer.index(dicFile.getName(), directory, elexDictionary);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (elexDictionary != null) {
                elexDictionary.close();
            }
        }
    }
}
