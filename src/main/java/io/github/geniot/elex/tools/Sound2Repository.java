package io.github.geniot.elex.tools;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Sound2Repository {

    static int THREADS = 10;
    private static ExecutorService executor = Executors.newFixedThreadPool(THREADS);

    public static void main(String[] args) {
        try {
            String pathToWavFiles = "C:\\Temp\\SoundEn\\SoundEn.extracted";
            String pathToRepository = "C:\\dictionaries\\SoundEn";

            int counter = 0;
            int tenThousand = 0;
            int thousand = 0;
            int hundred = 0;

            File[] files = new File(pathToWavFiles).listFiles();
            List<Future<Integer>> futures = new ArrayList<>();

            for (File f : files) {

                StringBuilder path = new StringBuilder();
                path.append(pathToRepository);
                path.append(File.separator);
                path.append("data");
                path.append(File.separator);
                path.append(tenThousand);
                path.append(File.separator);
                path.append(thousand);
                path.append(File.separator);
                path.append(hundred);
                FileUtils.forceMkdir(new File(path.toString()));

                String[] lameCommand = new String[]{
                        "lame/lame.exe",
                        "-V4",
                        f.getAbsolutePath(),
                        path + File.separator + f.getName()
                };

                Future<Integer> future = convert(lameCommand);
                futures.add(future);

                ++counter;
                if (counter % 100 == 0) {
                    hundred += 1;
                    if (counter % 10000 == 0) {
                        tenThousand += 1;
                    }
                    if (counter % 1000 == 0) {
                        thousand += 1;
                    }
                }
            }

            counter = 0;
            for (Future<Integer> future : futures) {
                ++counter;
                System.out.println(counter);
                future.get();
            }
            executor.shutdown();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Future<Integer> convert(String[] lameCommand) {
        return executor.submit(() -> {
            try {
                System.out.println(String.join(" ", lameCommand));
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