package io.github.geniot.elex.tools;

import io.github.geniot.elex.CaseInsensitiveComparator;
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
            String inputFolder = "D:\\torrents\\elex\\oxford_advanced\\media\\";
            String outputPath = "D:\\torrents\\elex\\oxford_advanced\\en-en_OALD9_v1.1.ezr";

            SortedMap<String, File> resourcesMap = new TreeMap<>(new CaseInsensitiveComparator());
            File[] folders = new File(inputFolder).listFiles();
            for (File folder : folders) {
                if (folder.isDirectory()) {
                    File[] files = folder.listFiles();
                    for (File file : files) {
                        resourcesMap.put(file.getName(), file);
                    }
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
