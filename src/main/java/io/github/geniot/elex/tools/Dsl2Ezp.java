package io.github.geniot.elex.tools;

import io.github.geniot.elex.tools.compile.Packager;
import io.github.geniot.elex.tools.convert.DslDictionary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Dsl2Ezp {
    static Logger logger = LoggerFactory.getLogger(Dsl2Ezp.class);
    public static String[] PATHS = new String[]{
//            "D:\\torrents\\elex\\enru\\",
//            "LingvoUniversalEnRu",
//
//            "D:\\torrents\\elex\\collins\\",
//            "CollinsCobuildEnEn",
//
//            "D:\\torrents\\elex\\longman\\",
//            "En-En-Longman_DOCE5",
//
//            "D:\\torrents\\elex\\macmillian\\",
//            "En-En_Macmillan English Dictionary",
//
//            "D:\\torrents\\elex\\macmillian\\",
//            "En-En_Macmillan English Thesaurus",
//
//            "D:\\torrents\\elex\\oxford\\",
//            "OxfordDictionaryEnEn",
//
//            "D:\\torrents\\elex\\oxford_advanced\\",
//            "en-en_OALD9_v1.1",

//            "D:\\torrents\\elex\\TransportEnRu\\",
//            "TransportEnRu",

//            "D:\\torrents\\elex\\GermanyDeRu\\",
//            "GermanyDeRu",

//            "D:\\torrents\\elex\\LingvoUniversalRuEn\\",
//            "LingvoUniversalRuEn",

//            "D:\\torrents\\elex\\vandenbaar\\",
//            "vandenbaar_x3",

            "C:\\development\\converters\\data\\drae\\",
            "es_es-DRAE",
    };

    static String outputBase = "C:\\development\\elex\\data\\";

    public static void main(String[] args) {
        try {
            Charset charset = StandardCharsets.UTF_16;
            for (int i = 0; i < PATHS.length; i += 2) {
                String base = PATHS[i];
                String name = PATHS[i + 1];

                String pathToDsl = base + name + ".dsl";
                String pathToAnn = base + name + ".ann";
                String pathToIcon = base + name + ".png";
                String pathToAbbr = base + name + "_abrv.dsl";
                String output = outputBase + name + ".ezp";


                DslDictionary dslDictionary = new DslDictionary(pathToDsl, pathToAnn, pathToIcon, pathToAbbr, charset);
                //compile
                byte[] ezpBbs = new Packager().pack(dslDictionary);

                //write
                File outputFile = new File(output);
                if (outputFile.exists()) {
                    logger.warn("Output file exists, removing: " + outputFile.getAbsolutePath());
                    if (!outputFile.delete()) {
                        logger.error("Couldn't remove old output file: " + outputFile);
                    }
                }
                System.out.println("Writing out " + outputFile);
                FileUtils.writeByteArrayToFile(outputFile, ezpBbs);
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
