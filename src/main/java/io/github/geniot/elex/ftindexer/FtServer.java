package io.github.geniot.elex.ftindexer;

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
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
    private Searcher searcher;

    @PostConstruct
    public void init() {
        ftFolderPath = new File(pathToData + File.separator + ftIndexFolderName).getAbsolutePath();
    }

    private Map<String, Directory> directoriesCache = new HashMap<>();

    public SortedMap<Float, String[]> search(String fileName,
                                             String query,
                                             int hitsPerPage,
                                             String indexLanguage,
                                             String contentsLanguage) throws IOException {

        Directory directory = getIndexByDictionaryFileName(FilenameUtils.removeExtension(fileName));
        if (directory != null) {
            SortedMap<Float, String[]> result = searcher.search(directory, query, hitsPerPage, indexLanguage);

            if (!contentsLanguage.equals(indexLanguage)) {
                Map<String, Object[]> headwordToFragmentMap = getHeadwordToFragmentMap(result);
                SortedMap<Float, String[]> result2 = searcher.search(directory, query, hitsPerPage, contentsLanguage);
                //some merging
                for (Float f : result2.keySet()) {
                    String[] val = result2.get(f);
                    String hwd = val[0];
                    String fragment = val[1];
                    if (!headwordToFragmentMap.containsKey(hwd)) {
                        result.put(f, val);
                    } else {
//                        Object[] scoreAndFragment = headwordToFragmentMap.get(hwd);
//                        Float firstScore = (Float) scoreAndFragment[0];
//                        String firstFragment = (String) scoreAndFragment[1];
//                        if (!firstFragment.equals(fragment)) {
//                            String mergedFragment = firstFragment + "... " + fragment;
//                            result.put(firstScore, new String[]{hwd, mergedFragment});
//                        }
                    }
                }
            }
            return result;
        } else {
            return new TreeMap<>();
        }
    }

    private Map<String, Object[]> getHeadwordToFragmentMap(SortedMap<Float, String[]> result) {
        Map<String, Object[]> map = new HashMap<>();
        for (Float f : result.keySet()) {
            String[] val = result.get(f);
            map.put(val[0], new Object[]{f, val[1]});
        }
        return map;
    }

    public long getDirectorySize(String fileName) throws IOException {
        long result = 0;
        Directory directory = getIndexByDictionaryFileName(FilenameUtils.removeExtension(fileName));
        if (directory != null) {
            String[] files = directory.listAll();
            for (String file : files) {
                String pathToIndex = ftFolderPath + File.separator + FilenameUtils.removeExtension(fileName) + File.separator + file;
                result += new File(pathToIndex).length();
            }
        }
        return result;
    }

    private Directory getIndexByDictionaryFileName(String fileName) throws IOException {
        Directory directory = directoriesCache.get(fileName);
        if (directory == null) {
            String pathToIndex = ftFolderPath + File.separator + fileName;
            String[] ff = new File(pathToIndex).list();
            if (ff != null && ff.length > 0) {
                directoriesCache.put(fileName, directory);
                directory = FSDirectory.open(Paths.get(pathToIndex));
            } else {
                return null;
            }
        }
        return directory;
    }
}
