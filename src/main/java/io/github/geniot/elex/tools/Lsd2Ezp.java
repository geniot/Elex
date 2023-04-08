package io.github.geniot.elex.tools;

import io.github.geniot.elex.tools.compile.Packager;
import io.github.geniot.elex.tools.convert.DslDictionary;
import io.github.geniot.elex.tools.convert.ImageUtils;
import io.github.geniot.elex.tools.decompile.lsd.LsdFile;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Lsd2Ezp {
    static Logger logger = LoggerFactory.getLogger(Lsd2Ezp.class);

    /**
     * lsd to ezp
     */
    public static void main(String[] args) {
        try {
            String base = args[0];
            String fileName = args[1];
            String lsdFileName = base + fileName + ".lsd";
            String lsdAbbrFileName = base + fileName + "_abrv.lsd";
            String outFileName = base + fileName + ".ezp";
            //decompile
            LsdFile lsdAbrFile = new LsdFile(lsdAbbrFileName, false);
            LsdFile lsdFile = new LsdFile(lsdFileName, false);

            byte[] iconBbs = lsdFile.icon;
            String annotation = lsdFile.read_annotation();

            String abrDsl = lsdAbrFile.getDsl();
            String dsl = lsdFile.getDsl();

            //convert
            iconBbs = ImageUtils.bmp2png(iconBbs);
            DslDictionary dslAbbrDictionary = new DslDictionary(abrDsl);
            DslDictionary dslDictionary = new DslDictionary(dsl, annotation, iconBbs);
            dslDictionary.setAbbreviations(dslAbbrDictionary.getAbbreviations());
//            FixUtils.fixArticles(dslDictionary);

            //compile
//            byte[] ezpBbs = new Packager().pack(dslDictionary);
//
//            //write
//            File outputFile = new File(outFileName);
//            if (outputFile.exists()) {
//                logger.warn("Output file exists, removing: " + outputFile.getAbsolutePath());
//                if (!outputFile.delete()) {
//                    logger.error("Couldn't remove old output file: " + outputFile);
//                }
//            }
//            FileUtils.writeByteArrayToFile(outputFile, ezpBbs);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
