package io.github.geniot.elex.tools;

import io.github.geniot.elex.tools.compile.Packager;
import io.github.geniot.elex.tools.convert.DslDictionary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Dsl2Ezp {
    static Logger logger = LoggerFactory.getLogger(Dsl2Ezp.class);

    public static void main(String[] args) {
        try {
            String pathToDictionarySources = args[0];
            String pathToOutputFile = args[1];

            DslDictionary dslDictionary = new DslDictionary(new File(pathToDictionarySources));
            //compile
            byte[] ezpBbs = new Packager().pack(dslDictionary);

            File outputFile = new File(pathToOutputFile);
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
