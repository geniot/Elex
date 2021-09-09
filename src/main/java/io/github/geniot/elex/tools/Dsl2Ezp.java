package io.github.geniot.elex.tools;

import io.github.geniot.elex.tools.compile.Packager;
import io.github.geniot.elex.tools.convert.DslDictionary;
import io.github.geniot.elex.tools.convert.FixUtils;
import io.github.geniot.elex.tools.convert.ImageUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Dsl2Ezp {
    static Logger logger = LoggerFactory.getLogger(Dsl2Ezp.class);

    public static void main(String[] args) {
        try {
            String base = args[0];
            String name = args[1];
            String pathToDsl = base + name + ".dsl";
            String pathToAnn = base + name + ".ann";
            String pathToIcon = base + name + ".bmp";
            String pathToAbbr = base + name + "_abrv.dsl";
            String output = base + name + ".ezp";

            DslDictionary dslDictionary = new DslDictionary(pathToDsl, pathToAnn, pathToIcon, pathToAbbr);
            FixUtils.fixArticles(dslDictionary);
            dslDictionary.setIcon(ImageUtils.bmp2png(dslDictionary.getIcon()));

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
            FileUtils.writeByteArrayToFile(outputFile, ezpBbs);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
