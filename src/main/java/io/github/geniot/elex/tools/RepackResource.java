package io.github.geniot.elex.tools;

import io.github.geniot.elex.CaseInsensitiveComparatorV4;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.tools.compile.ByteArrayProvider;
import io.github.geniot.elex.tools.compile.ResourcesPackager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.SortedMap;
import java.util.TreeMap;

public class RepackResource {
    public static void main(String[] args) {
        try {
            File[] files = new File("data").listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".ezr")) {
                    System.out.println("Reading " + file.getName());
                    ElexDictionary elexDictionary = new ElexDictionary(file, "r");

                    SortedMap<String, ByteArrayProvider> resourcesMap = new TreeMap<>(new CaseInsensitiveComparatorV4());
                    String header = elexDictionary.first();
                    while (header != null) {
                        resourcesMap.put(header, new ByteArrayProvider(elexDictionary, header));
                        header = elexDictionary.next(header);
                    }

                    ResourcesPackager resourcesPackager = new ResourcesPackager();
                    resourcesPackager.pack(resourcesMap, new FileOutputStream("data2/" + file.getName()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
