package io.github.geniot.elex.tools;

import io.github.geniot.elex.tools.convert.CaseInsensitiveComparator;
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
            SortedMap<String, File> resourcesMap = new TreeMap<>(new CaseInsensitiveComparator());
            File[] files = new File(args[0]).listFiles();
            for (File file : files) {
                resourcesMap.put(file.getName(), file);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ResourcesPackager resourcesPackager = new ResourcesPackager();
            resourcesPackager.pack(resourcesMap, byteArrayOutputStream);

            FileUtils.writeByteArrayToFile(new File(args[1]), byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
