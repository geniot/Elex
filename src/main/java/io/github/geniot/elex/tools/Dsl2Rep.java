package io.github.geniot.elex.tools;


import io.github.geniot.elex.tools.convert.DslDictionary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Imports a DSL dictionary into a git repository structure.
 */
public class Dsl2Rep {
    static Logger logger = LoggerFactory.getLogger(Dsl2Rep.class);

    public static void main(String[] args) {
        try {
            String sourceLanguage = args[0];
            String targetLanguage = args[1];
            String dictionaryName = args[2];
            String base = "C:\\dictionaries\\" + sourceLanguage + "\\" + targetLanguage + "\\" + dictionaryName;

            String dsl = FileUtils.readFileToString(new File("data2/" + sourceLanguage + "_" + targetLanguage + "-" + dictionaryName + ".ezp"), StandardCharsets.UTF_8);
            String ann = FileUtils.readFileToString(new File(base + "/annotation.txt"), StandardCharsets.UTF_8);
            byte[] icon = FileUtils.readFileToByteArray(new File(base + "/icon.png"));

            DslDictionary dslDictionary = new DslDictionary(dsl, ann, icon);

            int total = dslDictionary.getEntries().size();
            int counter = 0;
            int tenThousand = 0;
            int thousand = 0;
            int hundred = 0;

            logger.info("TOTAL:" + total);

            StringBuilder content = new StringBuilder();
            for (String key : dslDictionary.getEntries().keySet()) {
                ++counter;
                if (counter % 1000 == 0) {
                    logger.info(String.valueOf(counter));
                }
                content.append(key);
                content.append("\n");
                content.append(dslDictionary.getEntries().get(key));
                content.append("\n");

                if (counter % 100 == 0 || counter == total) {
                    StringBuilder path = new StringBuilder();
                    path.append(base);
                    path.append(File.separator);
                    path.append("data");
                    path.append(File.separator);
                    path.append(tenThousand);
                    path.append(File.separator);
                    path.append(thousand);
                    path.append(File.separator);
                    FileUtils.forceMkdir(new File(path.toString()));

                    String filePath = path.toString() + hundred + ".dsl";
                    String out = content.toString()
                            .replaceAll("\r\n", "\n")
                            .replaceAll("\r", "\n")
                            .replaceAll("\n+", "\n")
                            .replaceAll("\n", "\r\n");
                    FileUtils.writeByteArrayToFile(new File(filePath), out.getBytes(StandardCharsets.UTF_8));

                    content = new StringBuilder();
                    hundred += 1;
                    if (counter % 10000 == 0) {
                        tenThousand += 1;
                    }
                    if (counter % 1000 == 0) {
                        thousand += 1;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

