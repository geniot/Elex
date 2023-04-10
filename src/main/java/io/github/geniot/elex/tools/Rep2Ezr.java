package io.github.geniot.elex.tools;

import io.github.geniot.elex.CaseInsensitiveComparatorV4;
import io.github.geniot.elex.tools.compile.ByteArrayProvider;
import io.github.geniot.elex.tools.compile.ResourcesPackager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Rep2Ezr {
    static Logger logger = LoggerFactory.getLogger(Rep2Ezr.class);

    public static void main(String[] args) {
        try {
            String inputFolder = args[0];
            String outputPath = args[1];

            SortedMap<String, ByteArrayProvider> resourcesMap = new TreeMap<>(new CaseInsensitiveComparatorV4());
            File[] files = new File(inputFolder).listFiles();
            int counter = 0;
            for (File file : files) {
                ZipFile zipFile = new ZipFile(file.getAbsolutePath());
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ++counter;
                    if (counter % 1000 == 0) {
                        System.out.println(counter);
                    }
                    ZipEntry entry = entries.nextElement();
                    InputStream stream = zipFile.getInputStream(entry);
                    byte[] bytes = IOUtils.toByteArray(stream);
                    resourcesMap.put(entry.getName(), new ByteArrayProvider(bytes));
                    stream.close();
                }
                zipFile.close();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
            ResourcesPackager resourcesPackager = new ResourcesPackager();
            resourcesPackager.pack(resourcesMap, fileOutputStream);
            fileOutputStream.close();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
