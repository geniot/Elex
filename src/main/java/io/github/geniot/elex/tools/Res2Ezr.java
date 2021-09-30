package io.github.geniot.elex.tools;

import io.github.geniot.elex.CaseInsensitiveComparatorV4;
import io.github.geniot.elex.tools.compile.ByteArrayProvider;
import io.github.geniot.elex.tools.compile.ResourcesPackager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

public class Res2Ezr {
    static Logger logger = LoggerFactory.getLogger(Res2Ezr.class);

    public static void main(String[] args) {
        try {
            String inputFolder = "D:\\torrents\\elex\\LingvoUniversalRuEn\\SoundRu.converted\\";
            String outputPath = "C:\\development\\elex\\data\\LingvoUniversalRuEn.ezr";

            SortedMap<String, ByteArrayProvider> resourcesMap = new TreeMap<>(new CaseInsensitiveComparatorV4());
            File[] files = new File(inputFolder).listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    File[] ffs = file.listFiles();
                    for (File f : ffs) {
                        resourcesMap.put(f.getName(), new ByteArrayProvider(f));
                    }
                } else {
                    resourcesMap.put(file.getName(), new ByteArrayProvider(file));
                }
            }


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ResourcesPackager resourcesPackager = new ResourcesPackager();
            resourcesPackager.pack(resourcesMap, byteArrayOutputStream);

            FileUtils.writeByteArrayToFile(new File(outputPath), byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
