package io.github.geniot.elex.tools.convert;

import io.github.geniot.elex.CaseInsensitiveComparatorV4;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static io.github.geniot.elex.tools.convert.DslUtils.getArticleStart;
import static io.github.geniot.elex.tools.convert.DslUtils.noEscape;


public class DslDictionary implements Serializable {
    Logger logger = LoggerFactory.getLogger(DslDictionary.class);
    private Properties properties;
    private Properties abbreviations;
    private SortedMap<String, String> entries;
    private String annotation;
    private byte[] icon;

    private List<String> headersList;

    public DslDictionary(Properties p, Properties a, SortedMap<String, String> e, String an, byte[] ic) {
        this.properties = p;
        this.abbreviations = a;
        this.entries = e;
        this.annotation = an;
        this.icon = ic;
    }

    /**
     * Constructor for abbreviations file
     *
     * @param dslFile
     */
    public DslDictionary(File dslFile, Charset charset) {
        try {
            List<String> lines = FileUtils.readLines(dslFile, charset);
            read(lines, true, true);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public DslDictionary(String abrDsl) {
        try {
            List<String> lines = Arrays.asList(abrDsl.split("\n"));
            read(lines, true, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public DslDictionary(String dsl, String annotation, byte[] icon) {
        try {
            List<String> lines = Arrays.asList(dsl.split("\n"));
            read(lines, false, true);
            this.annotation = annotation;
            this.icon = icon;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public DslDictionary(File repPath) {
        try {
            Collection<File> dslFiles = FileUtils.listFiles(new File(repPath.getAbsolutePath() + File.separator + "data"), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            List<String> lines = new ArrayList<>();
            for (File dslFile : dslFiles) {
                lines.addAll(FileUtils.readLines(dslFile, StandardCharsets.UTF_8));
            }
            read(lines, false, false);

            File propertiesFile = new File(repPath.getAbsolutePath() + File.separator + "properties.xml");
            properties = new Properties();
            properties.loadFromXML(Files.newInputStream(propertiesFile.toPath()));

            annotation = FileUtils.readFileToString(new File(repPath.getAbsolutePath() + File.separator + "annotation.txt"), StandardCharsets.UTF_8);

            icon = FileUtils.readFileToByteArray(new File(repPath.getAbsolutePath() + File.separator + "icon.png"));
            File abbreviationsFile = new File(repPath.getAbsolutePath() + File.separator + "abbreviations.xml");
            if (abbreviationsFile.exists()) {
                abbreviations = new Properties();
                abbreviations.loadFromXML(Files.newInputStream(abbreviationsFile.toPath()));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public DslDictionary(String dslPath, String annotationPath, String iconPath, String abbreviationsPath, Charset charset) {
        try {
            List<String> lines = FileUtils.readLines(new File(dslPath), charset);
            read(lines, false, true);
            annotation = FileUtils.readFileToString(new File(annotationPath), charset);
            icon = FileUtils.readFileToByteArray(new File(iconPath));

            File abbreviationsFile = new File(abbreviationsPath);
            if (abbreviationsFile.exists()) {
                DslDictionary abbreviationsDictionary = new DslDictionary(abbreviationsFile, charset);
                abbreviations = abbreviationsDictionary.entriesToProperties();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public Properties entriesToProperties() {
        Properties props = new Properties();
        for (String key : entries.keySet()) {
            String value = entries.get(key);
            int entryStart = getArticleStart(value);
            if (entryStart < 0) {
                throw new RuntimeException("Couldn't find abbreviation definition");
            } else {
                value = value.substring(entryStart).trim();
            }
            props.setProperty(key, value);
        }
        return props;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (String header : headersList) {
            stringBuffer.append("#");
            stringBuffer.append(header);
            stringBuffer.append("\n");
        }
        for (String key : entries.keySet()) {
            String value = entries.get(key);
            stringBuffer.append(key);
            stringBuffer.append("\n");
            String[] lines = value.split("\n");
            for (String line : lines) {
                stringBuffer.append("\t");
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
        }
        return stringBuffer.toString();
    }

    private void read(List<String> lines, boolean isAbbreviations, boolean readHeaders) {
        Iterator<String> iterator = lines.iterator();
        entries = new TreeMap<>(new CaseInsensitiveComparatorV4());
        String line = null;
        if (readHeaders) {
            headersList = new ArrayList<>();
            //collecting header
            while (iterator.hasNext()) {
                line = iterator.next();
                if (line.startsWith("#")) {
                    headersList.add(line.replaceFirst("#", ""));
                    continue;
                }
                if (StringUtils.isEmpty(line)) {
                    continue;
                } else {
                    break;
                }
            }
            properties = parseHeaders(headersList);
        } else {
            line = iterator.next();
        }
        while (iterator.hasNext()) {
            //collecting headwords
            StringBuilder headwordsStringBuffer = new StringBuilder(line);
            while (iterator.hasNext()) {
                line = iterator.next();
                if (StringUtils.isEmpty(line)) {
                    continue;
                }
                if (!line.startsWith("\t") && !line.startsWith(" ")) {
                    headwordsStringBuffer.append("\n");
                    headwordsStringBuffer.append(line);
                } else {
                    break;
                }
            }

            //collecting entry
            StringBuilder entryString = new StringBuilder(line);
            while (iterator.hasNext()) {
                line = iterator.next();
                if (StringUtils.isEmpty(line)) {
                    continue;
                }
                if (line.startsWith("\t") || line.startsWith(" ")) {
                    if (!line.contains("© 2014 ABBYY")) {
                        entryString.append("\n");
                        entryString.append(line);
//                    entryString.append("\n");
                    }
                } else {
                    break;
                }
            }

            String key = headwordsStringBuffer.toString();
            String value = entryString.toString();
//                    .replaceAll("^\\t", "")
//                    .replaceAll("^\\s+", "")
//                    .replaceAll("\\n\\t", "\n")
//                    .replaceAll("\\r+", "")
//                    .replaceAll("\\n+", "\n")
//                    .replaceAll("\\n$", "");


            Map<String, String> variants = getVariants(key, value);
            if (variants.isEmpty()) {
                throw new RuntimeException("Empty result: " + key);
            }

            for (String k : variants.keySet()) {
                String v = variants.get(k);
                if (entries.containsKey(k)) {
                    throw new RuntimeException("Duplicate headword found: " + k);
                } else {
                    entries.put(k, v);
                }
            }
        }
    }

    protected static String getTitleKey(String key) {
        String indexKey = getIndexKey(key);
        String[] keys = key.split("\n");
        StringBuilder titleKeyBuilder = new StringBuilder();
        for (String k : keys) {
            String titleKey = k
                    .replaceAll(noEscape + "\\{", "")
                    .replaceAll(noEscape + "}", "")
                    .replaceAll("\\s+", " ")
                    .trim();
            if (keys.length != 1 || !titleKey.equals(indexKey)) {
                titleKeyBuilder.append("\t[h]");
                titleKeyBuilder.append(titleKey);
                titleKeyBuilder.append("[/h]\n");
            } else {
                titleKeyBuilder.append(titleKey);
            }
        }

        return titleKeyBuilder.toString().trim();
    }

    protected static String getIndexKey(String key) {
        return key
                .replaceAll(noEscape + "\\{[^}]+" + noEscape + "}", "")
                .replaceAll(noEscape + "\\(", "")
                .replaceAll(noEscape + "\\)", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * https://rcebits.com/lsd2dsl/variant-headings.html
     * <p>
     * dsl header: single | multiple
     * multiple: single | single
     * single: simple | marked-up
     * <p>
     * simple: write as it is
     * marked-up: remove mark-up for index, add [h] tag to the article
     *
     * @param k
     * @param value
     * @return
     */
    protected static SortedMap<String, String> getVariants(String k, String value) {
        SortedMap<String, String> result = new TreeMap<>();
        String titleKey = getTitleKey(k);
        String[] keys = k.split("\n");
        String firstIndexKey = k;
        for (int i = 0; i < keys.length; i++) {
            String indexKey = getIndexKey(keys[i]);
            String updatedValue = value;
            if (i == 0) {
                firstIndexKey = indexKey;
                if (!indexKey.equals(titleKey)) {
                    updatedValue = "\t" + titleKey + "\n" + updatedValue;
                }
            } else {
                updatedValue = "\t[ref]" + firstIndexKey + "[/ref]";
            }
            result.put(indexKey, updatedValue);
        }


//        String[] keys = k.split("\n");
//        String firstKey = null;
//        for (String key : keys) {
//            result.put(key, value);


//            key = key.replaceAll(noEscape + "\\{[^}]+" + noEscape + "}", "");
////            key = key.replaceAll(noEscape + "\\([^)]+" + noEscape + "\\)", "");
//            key = key.replaceAll(noEscape + "\\(", "");
//            key = key.replaceAll(noEscape + "\\)", "");
//            key = key.replaceAll("\\s+", " ");
//            key = key.replaceAll(noEscape + "\\\\", "");
//            key = key.trim();
//
//            if (StringUtils.isEmpty(key)) {
//                continue;
//            }
//
//            if (key.contains("{")
//                    || key.contains("}")
//                    || key.contains("\\")) {
//                throw new RuntimeException(key);
//            }
//            if (result.containsKey(key)) {
//                throw new RuntimeException("Duplicate key: " + key);
//            } else {
//                if (firstKey == null) {
//                    firstKey = key;
//                    result.put(key, value);
//                } else {
//                    String val = "[ref]" + firstKey + "[/ref]";
//                    result.put(key, val);
//                }
//            }
//        }
        return result;
    }

    private Properties parseHeaders(List<String> headersList) {
        Properties properties = new Properties();
        for (String header : headersList) {
            int firstSpace = header.indexOf("\t");
            firstSpace = firstSpace < 0 ? header.indexOf(" ") : firstSpace;
            String key = header.substring(0, firstSpace).trim();
            String value = header.substring(firstSpace).replaceAll("\"", "").trim();

            DslProperty dslProperty = DslProperty.valueOf(key);

            if (dslProperty.equals(DslProperty.INDEX_LANGUAGE) ||
                    dslProperty.equals(DslProperty.CONTENTS_LANGUAGE)) {
                Language language = Language.valueOfLabel(value);
                if (language == null) {
                    throw new RuntimeException(value);
                } else {
                    properties.setProperty(dslProperty.name(), language.name());
                }
            } else {
                properties.setProperty(key, value);
            }

        }
        return properties;
    }

    public SortedMap<String, String> getEntries() {
        return entries;
    }

    public String getAnnotation() {
        return annotation;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] i) {
        this.icon = i;
    }

    public Properties getProperties() {
        return properties;
    }

    public Properties getAbbreviations() {
        return abbreviations;
    }

    public void setAbbreviations(Properties p) {
        this.abbreviations = p;
    }
}
