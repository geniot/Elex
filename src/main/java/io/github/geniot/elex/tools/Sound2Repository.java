package io.github.geniot.elex.tools;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Sound2Repository {

    static int THREADS = 10;
    private static ExecutorService executor = Executors.newFixedThreadPool(THREADS);

    public static void main(String[] args) {
        try {
            String pathToWavFiles = "C:\\Temp\\SoundEn\\SoundEn.extracted";
            String pathToMp3Files = "C:\\Temp\\SoundEn\\SoundEn.converted";
            String pathToRepository = "C:\\dictionaries\\SoundEn\\data";

            System.out.println("Converting");

            FileUtils.forceMkdir(new File(pathToMp3Files));
            wav2mp3(pathToWavFiles, pathToMp3Files);
            executor.shutdown();

            System.out.println("Zipping");

            File[] files = new File(pathToMp3Files).listFiles();

            int outCounter = 0;

            List<File> batch = new ArrayList<>();
            for (File f : files) {
                batch.add(f);
                if (batch.size() == 100) {
                    System.out.println(outCounter);
                    zip(pathToRepository, outCounter, batch);
                    batch = new ArrayList<>();
                    ++outCounter;
                }
            }
            if (batch.size() > 0) {
                zip(pathToRepository, outCounter, batch);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void zip(String pathToRepository, int fileCounter, List<File> files) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(pathToRepository + File.separator + fileCounter + ".zip"));
        for (File file : files) {
            ZipEntry e = new ZipEntry(file.getName());
            out.putNextEntry(e);
            byte[] data = Files.readAllBytes(file.toPath());
            out.write(data, 0, data.length);
            out.closeEntry();
        }
        out.close();
    }

    public static void wav2mp3(String pathToWavFiles, String pathToMp3Files) {
        try {
            int counter = 0;

            File[] files = new File(pathToWavFiles).listFiles();
            List<Future<Integer>> futures = new ArrayList<>();

            for (File f : files) {
                String outFile = pathToMp3Files + File.separator + f.getName();

                String[] lameCommand = new String[]{
                        "lame/lame.exe",
                        "-V4",
                        pathToWavFiles + File.separator + f.getName(),
                        outFile
                };

                Future<Integer> future = convert(lameCommand);
                futures.add(future);
            }

            for (Future<Integer> future : futures) {
                future.get();
                ++counter;
                if (counter % 1000 == 0) {
                    System.out.println(counter + "/" + files.length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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