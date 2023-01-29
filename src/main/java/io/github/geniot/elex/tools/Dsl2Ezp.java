package io.github.geniot.elex.tools;

import io.github.geniot.elex.tools.compile.Packager;
import io.github.geniot.elex.tools.convert.DslDictionary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
            File outputFile = new File("en_ru-LingvoUniversal.ezp");
            DslDictionary dslDictionary = new DslDictionary(new File("C:\\dictionaries\\en\\ru\\LingvoUniversalEnRu"));
            //compile
            byte[] ezpBbs = new Packager().pack(dslDictionary);

            //write
            if (outputFile.exists()) {
                logger.warn("Output file exists, removing: " + outputFile.getAbsolutePath());
                if (!outputFile.delete()) {
                    logger.error("Couldn't remove old output file: " + outputFile);
                }
            }
            System.out.println("Writing out " + outputFile);
            FileUtils.writeByteArrayToFile(outputFile, ezpBbs);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
