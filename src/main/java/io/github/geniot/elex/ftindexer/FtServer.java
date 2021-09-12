package io.github.geniot.elex.ftindexer;

import io.github.geniot.elex.ezip.model.ElexDictionary;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
@Component
public class FtServer extends FileAlterationListenerAdaptor {
    Logger logger = LoggerFactory.getLogger(FtServer.class);

    private FileAlterationObserver observer;
    public static final String DATA_FOLDER_NAME = StringUtils.defaultIfEmpty(System.getProperty("data"), "data");
    public static final String FT_FOLDER_NAME = "ft-index";
    public static final String FT_FOLDER_PATH = new File(DATA_FOLDER_NAME + File.separator + FT_FOLDER_NAME).getAbsolutePath();
    @Autowired
    private Indexer indexer;
    @Autowired
    private Searcher searcher;
    private FileAlterationMonitor monitor;
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
            String pathToIndex = FT_FOLDER_PATH + File.separator + FilenameUtils.removeExtension(fileName) + File.separator + file;
            result += new File(pathToIndex).length();
        }
        return result;
    }

    private Directory getIndexByDictionaryFileName(String fileName) throws IOException {
        Directory directory = directoriesCache.get(fileName);
        if (directory == null) {
//            String pathToIndex = DATA_FOLDER_NAME +
//                    File.separator +
//                    FT_FOLDER_NAME +
//                    File.separator +
//                    fileName +
//                    ".ft";
//            File indexFile = new File(pathToIndex);
//            if (indexFile.exists()) {
//                directory = Utils.deserializeIndex(FileUtils.readFileToByteArray(indexFile));
//            }
            String pathToIndex = DATA_FOLDER_NAME +
                    File.separator +
                    FT_FOLDER_NAME +
                    File.separator +
                    fileName;
            directory = FSDirectory.open(Paths.get(pathToIndex));
            directoriesCache.put(fileName, directory);
        }
        return directory;
    }

    private FtServer() {
        try {
            observer = new FileAlterationObserver(DATA_FOLDER_NAME, pathname -> pathname.getPath().endsWith(".ezp"));
            observer.addListener(this);
            long interval = 1000;
            monitor = new FileAlterationMonitor(interval);
            monitor.addObserver(observer);
            monitor.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        update();
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
        try {
            monitor.stop();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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

            File dataFolder = new File(DATA_FOLDER_NAME);
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                logger.warn("Couldn't create " + dataFolder);
            }
            File ftIndexFolder = new File(DATA_FOLDER_NAME + File.separator + FT_FOLDER_NAME);
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

                    ElexDictionary elexDictionary = null;
                    try {
                        logger.info("Indexing " + dicFile.getAbsolutePath());
                        elexDictionary = new ElexDictionary(dicFile.getAbsolutePath(), "r");

                        String path = DATA_FOLDER_NAME +
                                File.separator +
                                FT_FOLDER_NAME +
                                File.separator +
                                FilenameUtils.removeExtension(dicFile.getName());
                        new File(path).mkdirs();

                        Directory directory = FSDirectory.open(Paths.get(path));
                        indexer.index(dicFile.getName(), directory, elexDictionary);

//                        SerializableRAMDirectory serializableRAMDirectory = new SerializableRAMDirectory();
//                        ramIndexer.index(serializableRAMDirectory, elexDictionary);
//                        byte[] index = Utils.serializeIndex(serializableRAMDirectory);
//
//                        String path = DATA_FOLDER_NAME +
//                                File.separator +
//                                FT_FOLDER_NAME +
//                                File.separator +
//                                FilenameUtils.removeExtension(dicFile.getName()) +
//                                ".ft";
//
//                        FileUtils.writeByteArrayToFile(new File(path), index);
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    } finally {
                        if (elexDictionary != null) {
                            elexDictionary.close();
                        }
                    }
                }
            }

            long t2 = System.currentTimeMillis();

            logger.info("Updated ft index in: " + (t2 - t1) + " ms");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void onFileCreate(final File file) {
        update();
    }

    @Override
    public void onFileChange(final File file) {
        update();
    }

    @Override
    public void onFileDelete(final File file) {
        update();
    }
}
