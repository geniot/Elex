package io.github.geniot.elex.ftindexer.lucene;

import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.util.Accountables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SerializableRAMDirectory extends BaseDirectory implements Accountable, Serializable {
    public Map<String, SerializableRAMFile> fileMap = new ConcurrentHashMap<>();
    public AtomicLong sizeInBytes = new AtomicLong();

    private final AtomicLong nextTempFileCounter = new AtomicLong();

    public SerializableRAMDirectory() {
        this(new SingleInstanceLockFactory());
    }

    public SerializableRAMDirectory(LockFactory lockFactory) {
        super(lockFactory);
    }


    public SerializableRAMDirectory(FSDirectory dir, IOContext context) throws IOException {
        this(dir, false, context);
    }

    private SerializableRAMDirectory(FSDirectory dir, boolean closeDir, IOContext context) throws IOException {
        this();
        for (String file : dir.listAll()) {
            if (!Files.isDirectory(dir.getDirectory().resolve(file))) {
                copyFrom(dir, file, file, context);
            }
        }
        if (closeDir) {
            dir.close();
        }
    }

    @Override
    public final String[] listAll() {
        ensureOpen();
        // NOTE: this returns a "weakly consistent view". Unless we change Dir API, keep this,
        // and do not synchronize or anything stronger. it's great for testing!
        // NOTE: fileMap.keySet().toArray(new String[0]) is broken in non Sun JDKs,
        // and the code below is resilient to map changes during the array population.
        // NOTE: don't replace this with return names.toArray(new String[names.size()]);
        // or some files could be null at the end of the array if files are being deleted
        // concurrently
        Set<String> fileNames = fileMap.keySet();
        List<String> names = new ArrayList<>(fileNames.size());
        for (String name : fileNames) {
            names.add(name);
        }
        String[] namesArray = names.toArray(new String[names.size()]);
        Arrays.sort(namesArray);
        return namesArray;
    }

    public final boolean fileNameExists(String name) {
        ensureOpen();
        return fileMap.containsKey(name);
    }

    /**
     * Returns the length in bytes of a file in the directory.
     *
     * @throws IOException if the file does not exist
     */
    @Override
    public final long fileLength(String name) throws IOException {
        ensureOpen();
        SerializableRAMFile file = fileMap.get(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return file.getLength();
    }

    /**
     * Return total size in bytes of all files in this directory. This is
     * currently quantized to RAMOutputStream.BUFFER_SIZE.
     */
    @Override
    public final long ramBytesUsed() {
        ensureOpen();
        return sizeInBytes.get();
    }

    @Override
    public Collection<Accountable> getChildResources() {
        return Accountables.namedAccountables("file", fileMap);
    }

    @Override
    public void deleteFile(String name) throws IOException {
        ensureOpen();
        SerializableRAMFile file = fileMap.remove(name);
        if (file != null) {
            file.directory = null;
            sizeInBytes.addAndGet(-file.sizeInBytes);
        } else {
            throw new FileNotFoundException(name);
        }
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        ensureOpen();
        SerializableRAMFile file = newRAMFile();
        if (fileMap.putIfAbsent(name, file) != null) {
            throw new FileAlreadyExistsException(name);
        }
        return new SerializableRAMOutputStream(name, file, true);
    }

    @Override
    public IndexOutput createTempOutput(String prefix, String suffix, IOContext context) throws IOException {
        ensureOpen();

        // Make the file first...
        SerializableRAMFile file = newRAMFile();

        // ... then try to find a unique name for it:
        while (true) {
            String name = IndexFileNames.segmentFileName(prefix, suffix + "_" + Long.toString(nextTempFileCounter.getAndIncrement(), Character.MAX_RADIX), "tmp");
            if (fileMap.putIfAbsent(name, file) == null) {
                return new SerializableRAMOutputStream(name, file, true);
            }
        }
    }

    protected SerializableRAMFile newRAMFile() {
        return new SerializableRAMFile(this);
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
    }

    @Override
    public void rename(String source, String dest) throws IOException {
        ensureOpen();
        SerializableRAMFile file = fileMap.get(source);
        if (file == null) {
            throw new FileNotFoundException(source);
        }
        if (fileMap.putIfAbsent(dest, file) != null) {
            throw new FileAlreadyExistsException(dest);
        }
        if (!fileMap.remove(source, file)) {
            throw new IllegalStateException("file was unexpectedly replaced: " + source);
        }
        fileMap.remove(source);
    }

    @Override
    public void syncMetaData() throws IOException {
        // we are by definition not durable!
    }

    /**
     * Returns a stream reading an existing file.
     */
    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        ensureOpen();
        SerializableRAMFile file = fileMap.get(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return new SerializableRAMInputStream(name, file);
    }

    /**
     * Closes the store to future operations, releasing associated memory.
     */
    @Override
    public void close() {
        isOpen = false;
        fileMap.clear();
    }

    @Override
    public Set<String> getPendingDeletions() {
        return Collections.emptySet();
    }

}
