package io.github.geniot.elex.tools;

import io.github.geniot.elex.CaseInsensitiveComparatorV4;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.tools.convert.DslProperty;
import io.github.geniot.elex.tools.convert.Language;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

public class Ezp2Dsl {
    public static void main(String[] args) {
        try {
            File file = new File(args[0]);
            System.out.println("Reading " + file.getName());
            ElexDictionary elexDictionary = new ElexDictionary(file, "r");
            Properties properties = elexDictionary.getProperties();
            Properties abbreviations = elexDictionary.getAbbreviations();

            SortedMap<String, String> entries = new TreeMap<>(new CaseInsensitiveComparatorV4());
            String header = elexDictionary.first();
            while (header != null) {
                entries.put(header, elexDictionary.readArticle(header));
                header = elexDictionary.next(header);
            }

            String annotation = elexDictionary.getAnnotation();
            byte[] icon = elexDictionary.getIcon();
            String entriesStr = collect(entries);


            FileUtils.writeByteArrayToFile(new File("data2/" + file.getName() + ".png"), icon);
            FileUtils.writeByteArrayToFile(new File("data2/" + file.getName()), entriesStr.getBytes(StandardCharsets.UTF_8));
            FileUtils.writeByteArrayToFile(new File("data2/" + file.getName() + ".annotation.txt"), annotation.getBytes(StandardCharsets.UTF_8));

            properties.storeToXML(Files.newOutputStream(new File("data2/" + file.getName() + ".props.xml").toPath()), null);
            abbreviations.store(Files.newOutputStream(new File("data2/" + file.getName() + ".abbreviations.txt").toPath()), null);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("#NAME\t\"Abbrev\"\r\n");
            String sourceLanguage = Language.valueOf(properties.getProperty(DslProperty.INDEX_LANGUAGE.name())).label;
            String targetLanguage = Language.valueOf(properties.getProperty(DslProperty.CONTENTS_LANGUAGE.name())).label;
            stringBuilder.append("#INDEX_LANGUAGE\t\"" + sourceLanguage + "\"\r\n");
            stringBuilder.append("#CONTENTS_LANGUAGE\t\"" + targetLanguage + "\"\r\n\r\n");

            for (Object key : abbreviations.keySet()) {
                stringBuilder.append(key.toString() + "\r\n");
                stringBuilder.append("\t" + abbreviations.get(key).toString() + "\r\n");
            }
            FileUtils.writeByteArrayToFile(new File("data2/" + file.getName() + ".abbreviations.orig.txt"), stringBuilder.toString().getBytes(StandardCharsets.UTF_8));


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String collect(SortedMap<String, String> entries) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : entries.keySet()) {

            stringBuilder.append(entries.get(key));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
