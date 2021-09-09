package io.github.geniot.elex.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Wav2Mp3 {
    static Logger logger = LoggerFactory.getLogger(Wav2Mp3.class);

    static int THREADS = 10;
    private static ExecutorService executor = Executors.newFixedThreadPool(THREADS);

    public static void main(String[] args) {
        try {
            String base = args[0];
            String out = args[1];

            new File(out).mkdirs();

            int counter = 0;

            File[] files = new File(base).listFiles();
            List<Future<Integer>> futures = new ArrayList<>();

            for (File f : files) {

                ++counter;
                if (counter % 100 == 0) {
                    System.out.println(counter + "/" + files.length);
                }

                String outFile = out + File.separator + f.getName();
                if (new File(outFile).exists()) {
                    continue;
                }

                String[] lameCommand = new String[]{
                        "lame/lame.exe",
                        "-V4",
                        base + File.separator + f.getName(),
                        outFile
                };

                Future<Integer> future = convert(lameCommand);
                futures.add(future);
            }

            for (Future<Integer> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static Future<Integer> convert(String[] lameCommand) {
        return executor.submit(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(Arrays.asList(lameCommand));
                pb.redirectErrorStream(true);
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((reader.readLine()) != null) {
                }
                return process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        });
    }
}
