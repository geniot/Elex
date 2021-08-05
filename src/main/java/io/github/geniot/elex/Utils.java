package io.github.geniot.elex;

import io.github.geniot.elex.model.lucene.SerializableRAMDirectory;
import io.github.geniot.elex.model.lucene.SerializableRAMFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Utils {

    public static SerializableRAMDirectory deserializeIndex(byte[] bbs) {
        try {
            SerializableRAMDirectory directory = new SerializableRAMDirectory();
            ByteArrayInputStream bais = new ByteArrayInputStream(bbs);
            ObjectInput input = new ObjectInputStream(bais);
            Integer fileMapSize = (Integer) input.readObject();
            directory.fileMap = new ConcurrentHashMap<>();
            for (int i = 0; i < fileMapSize; i++) {
                String key = (String) input.readObject();
                SerializableRAMFile file = new SerializableRAMFile();
                file.directory = directory;
                file.buffers = (ArrayList<byte[]>) input.readObject();
                file.sizeInBytes = (long) input.readObject();
                file.length = (long) input.readObject();
                directory.fileMap.put(key, file);
            }
            directory.sizeInBytes = (AtomicLong) input.readObject();
            return directory;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] serializeIndex(SerializableRAMDirectory directory) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(directory.fileMap.size());
            for (Map.Entry<String, SerializableRAMFile> entry : directory.fileMap.entrySet()) {
                out.writeObject(entry.getKey());
                out.writeObject(entry.getValue().buffers);
                out.writeObject(entry.getValue().sizeInBytes);
                out.writeObject(entry.getValue().length);
            }
            out.writeObject(directory.sizeInBytes);
            return baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
