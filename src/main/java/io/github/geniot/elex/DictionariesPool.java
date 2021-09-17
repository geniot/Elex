package io.github.geniot.elex;

import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.ftindexer.FtServer;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.*;
import io.github.geniot.elex.tools.convert.CaseInsensitiveComparator;
import io.github.geniot.elex.tools.convert.DslProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

@Component
@Getter
@Setter
public class DictionariesPool {
    Logger logger = LoggerFactory.getLogger(DictionariesPool.class);

    private Map<String, ElexDictionary> dictionaries = Collections.synchronizedMap(new HashMap<>());
    private Map<String, ElexDictionary> resources = Collections.synchronizedMap(new HashMap<>());

    @Autowired
    CaseInsensitiveComparator caseInsensitiveComparator;
    @Autowired
    private FtServer ftServer;
    @Autowired
    private WebConfig webConfig;
    @Autowired
    private ServerSettingsManager serverSettingsManager;


    public Map<String, ElexDictionary> getElexDictionaries(Model model) throws IOException {
        Map<String, ElexDictionary> result = new HashMap<>();
        for (String fileName : dictionaries.keySet()) {
            ElexDictionary elexDictionary = dictionaries.get(fileName);
            Properties properties = elexDictionary.getProperties();
            String sl1 = model.getSelectedSourceLanguage();
            String tl1 = model.getSelectedTargetLanguage();
            String sl2 = properties.getProperty(DslProperty.INDEX_LANGUAGE.name());
            String tl2 = properties.getProperty(DslProperty.CONTENTS_LANGUAGE.name());
            String name = properties.getProperty(DslProperty.NAME.name());
            if (sl1.equalsIgnoreCase(sl2) &&
                    tl1.equalsIgnoreCase(tl2) &&
                    model.isDictionarySelected(name)
            ) {
                result.put(fileName, elexDictionary);
            }
        }
        return result;
    }

    public List<Dictionary> getDictionaries(Model model) throws IOException {
        List<Dictionary> result = new ArrayList<>();
        for (String fileName : dictionaries.keySet()) {
            ElexDictionary elexDictionary = dictionaries.get(fileName);
            Dictionary dictionary = new Dictionary();
            dictionary.setId(fileName.hashCode());
            String name = elexDictionary.getProperties().getProperty(DslProperty.NAME.name());
            dictionary.setName(name);
            dictionary.setIndexLanguageCode(elexDictionary.getProperties().getProperty(DslProperty.INDEX_LANGUAGE.name()));
            dictionary.setContentsLanguageCode(elexDictionary.getProperties().getProperty(DslProperty.CONTENTS_LANGUAGE.name()));
            dictionary.setSelected(model.isDictionarySelected(name));
            dictionary.setCurrent(true);
            result.add(dictionary);
        }
        return result;
    }

    public SortedSet<AdminDictionary> getAdminDictionaries(AdminModel model) throws IOException {
        SortedSet<AdminDictionary> result = new TreeSet<>();
        for (String fileName : dictionaries.keySet()) {
            ElexDictionary elexDictionary = dictionaries.get(fileName);

            long totalSize = 0;

            AdminDictionary adminDictionary = new AdminDictionary();
            adminDictionary.setStatus(DictionaryStatus.ENABLED);
            adminDictionary.setId(fileName.hashCode());
            adminDictionary.setFileName(fileName);
            adminDictionary.setDataPath(webConfig.getPathToDataAbsolute());
            adminDictionary.setFileSize(NumberFormat.getInstance().format(elexDictionary.length()));
            totalSize += elexDictionary.length();
            adminDictionary.setEntries(elexDictionary.getSize());

            String resourceFileName = FilenameUtils.removeExtension(fileName) + ".ezr";
            if (resources.containsKey(resourceFileName)) {
                adminDictionary.setResourcesFileName(resourceFileName);
                adminDictionary.setResourcesFileSize(NumberFormat.getInstance().format(resources.get(resourceFileName).length()));
                totalSize += resources.get(resourceFileName).length();
                adminDictionary.setResourcesCount(resources.get(resourceFileName).getSize());
            }

            long ftSize = ftServer.getDirectorySize(fileName);
            adminDictionary.setFtIndexSize(NumberFormat.getInstance().format(ftSize));
            totalSize += ftSize;

            adminDictionary.setTotalSize(NumberFormat.getInstance().format(totalSize));

            String name = elexDictionary.getProperties().getProperty(DslProperty.NAME.name());
            adminDictionary.setName(name);
            adminDictionary.setSelected(model.isDictionarySelected(name));

            adminDictionary.setIndexLanguageCode(elexDictionary.getProperties().getProperty(DslProperty.INDEX_LANGUAGE.name()));
            adminDictionary.setContentsLanguageCode(elexDictionary.getProperties().getProperty(DslProperty.CONTENTS_LANGUAGE.name()));
            result.add(adminDictionary);
        }

        for (String fileName : serverSettingsManager.getServerSettings().getDisabledDictionariesMap().keySet()) {
            String name = serverSettingsManager.getServerSettings().getDisabledDictionariesMap().get(fileName);
            String ezpFileName = fileName + ".ezp";
            String ezrFileName = fileName + ".ezr";
            File ezpFile = new File(webConfig.getPathToDataAbsolute() + ezpFileName);
            if (ezpFile.exists()) {
                AdminDictionary adminDictionary = new AdminDictionary();
                adminDictionary.setId(fileName.hashCode());
                adminDictionary.setFileName(ezpFileName);
                adminDictionary.setStatus(DictionaryStatus.DISABLED);
                adminDictionary.setName(name);
                adminDictionary.setSelected(model.isDictionarySelected(name));
                adminDictionary.setDataPath(webConfig.getPathToDataAbsolute());
                adminDictionary.setFileSize(NumberFormat.getInstance().format(ezpFile.length()));

                File ezrFile = new File(webConfig.getPathToDataAbsolute() + ezrFileName);
                if (ezrFile.exists()) {
                    adminDictionary.setResourcesFileName(ezrFileName);
                    adminDictionary.setResourcesFileSize(NumberFormat.getInstance().format(ezrFile.length()));
                }
                result.add(adminDictionary);
            }
        }
        return result;
    }

    public List<Entry> getArticles(Model model) throws Exception {
        List<Entry> entries = new ArrayList<>();
        for (String fileName : dictionaries.keySet()) {
            ElexDictionary elexDictionary = dictionaries.get(fileName);
            String name = elexDictionary.getProperties().getProperty(DslProperty.NAME.name());
            if (model.isDictionarySelected(name) && model.isDictionaryCurrent(name)) {
                String article = elexDictionary.readArticle(model.getSelectedHeadword());
                if (article != null) {
                    Entry entry = new Entry();
                    entry.setDicId(String.valueOf(fileName.hashCode()));
                    entry.setDicName(name);
                    entry.setHeadword(model.getSelectedHeadword());
                    entry.setBody(article);
                    entries.add(entry);
                }
            }
        }
        return entries;
    }

    public byte[] getIcon(int id) throws IOException {
        for (String fileName : dictionaries.keySet()) {
            if (fileName.hashCode() == id) {
                return dictionaries.get(fileName).getIcon();
            }
        }
        return null;
    }

    public byte[] getResource(int id, String link) throws Exception {
        for (String fileName : dictionaries.keySet()) {
            if (fileName.hashCode() == id) {
                String resourceFileName = FilenameUtils.removeExtension(fileName) + ".ezr";
                if (resources.containsKey(resourceFileName)) {
                    return resources.get(resourceFileName).readResource(link);
                }
            }
        }
        logger.error("Couldn't find resource for: " + id + "; " + link);
        return new byte[]{};
    }

    public String getMinHeadword(Set<ElexDictionary> set) throws Exception {
        String minHw = null;
        for (ElexDictionary cd : set) {
            String hw = cd.first();
            if (minHw == null || caseInsensitiveComparator.compare(minHw, hw) > 0) {
                minHw = hw;
            }
        }
        return minHw;
    }

    public String getMaxHeadword(Set<ElexDictionary> set) throws Exception {
        String maxHw = null;
        for (ElexDictionary cd : set) {
            String hw = cd.last();
            if (maxHw == null || caseInsensitiveComparator.compare(maxHw, hw) < 0) {
                maxHw = hw;
            }
        }
        return maxHw;
    }

    public String getDownloadFilePath(int id, String type) {
        String path = null;
        for (String fn : dictionaries.keySet()) {
            if (fn.hashCode() == id) {
                path = FilenameUtils.removeExtension(fn) + "." + type;
            }
        }
        if (path != null) {
            path = webConfig.getPathToDataAbsolute() + path;
        }
        return path;
    }

    synchronized public void changeState(AdminDictionary selectedDictionary) {
        try {
            String fileName = FilenameUtils.removeExtension(selectedDictionary.getFileName());
            String ezpFileName = fileName + ".ezp";
            String ezrFileName = fileName + ".ezr";
            if (selectedDictionary.getStatus().equals(DictionaryStatus.ENABLED)) {

                ElexDictionary ezp = dictionaries.get(ezpFileName);
                ezp.close();
                dictionaries.remove(ezpFileName);

                ElexDictionary ezr = resources.get(ezrFileName);
                if (ezr != null) {
                    ezr.close();
                    resources.remove(ezrFileName);
                }

                serverSettingsManager.put(fileName, selectedDictionary.getName());

            } else {
                File ezpFile = new File(webConfig.getPathToDataAbsolute() + ezpFileName);
                if (ezpFile.exists()) {
                    dictionaries.put(ezpFile.getName(), new ElexDictionary(ezpFile.getAbsolutePath(), "r"));
                } else {
                    logger.warn("Couldn't enable dictionary with file name: " + ezpFileName);
                }
                File ezrFile = new File(webConfig.getPathToDataAbsolute() + ezrFileName);
                if (ezrFile.exists()) {
                    resources.put(ezrFile.getName(), new ElexDictionary(ezrFile.getAbsolutePath(), "r"));
                }
                serverSettingsManager.remove(fileName);

            }
        } catch (IOException e) {
            logger.error("Couldn't change dictionary state for: " + selectedDictionary.getFileName(), e);
        } finally {
            serverSettingsManager.saveSettings();
        }
    }

    public void close() {
        for (String fileName : dictionaries.keySet()) {
            try {
                ElexDictionary elexDictionary = dictionaries.get(fileName);
                elexDictionary.close();
                logger.info("Closed " + fileName);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        for (String fileName : resources.keySet()) {
            try {
                ElexDictionary elexDictionary = resources.get(fileName);
                elexDictionary.close();
                logger.info("Closed " + fileName);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


}
