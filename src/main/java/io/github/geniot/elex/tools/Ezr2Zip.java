package io.github.geniot.elex.tools;

import io.github.geniot.elex.ezip.model.ElexDictionary;

import java.io.File;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Ezr2Zip {
    public static void main(String[] args) {
        try {
            File file = new File("data/en_ru-LingvoUniversal.ezr");
            File outFile = new File("data2/SoundEn.zip");
            System.out.println("Reading " + file.getName());
            ElexDictionary elexDictionary = new ElexDictionary(file, "r");

            ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(outFile.toPath()));
            int counter = 0;
            String header = elexDictionary.first();
            while (header != null) {
                ++counter;
                if (counter % 1000 == 0) {
                    System.out.println(counter);
                }
                ZipEntry e = new ZipEntry(header);
                out.putNextEntry(e);
                byte[] data = elexDictionary.readResource(header);
                out.write(data, 0, data.length);
                out.closeEntry();
                header = elexDictionary.next(header);
            }
            out.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
