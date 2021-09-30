package io.github.geniot.elex.tools;

import io.github.geniot.elex.CaseInsensitiveComparatorV4;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.tools.compile.Packager;
import io.github.geniot.elex.tools.convert.DslDictionary;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * When comparator is changed, we need to repack the dictionaries.
 * How to switch to a new comparator?
 * - repack everything using the new comparator
 * - save results to new files
 * - substitute folders with new dictionaries/resources
 * - refactor the code to use the new comparator
 * - start the server, test
 * - if all dictionaries work as expected, remove the old comparator
 */
public class Repack {
    public static void main(String[] args) {
        try {
            File[] files = new File("data").listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".ezp")) {
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

                    DslDictionary dslDictionary = new DslDictionary(properties, abbreviations, entries, annotation, icon);
                    byte[] ezpBbs = new Packager().pack(dslDictionary);
                    System.out.println("Writing " + file.getName());
                    FileUtils.writeByteArrayToFile(new File("data2/" + file.getName()), ezpBbs);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
