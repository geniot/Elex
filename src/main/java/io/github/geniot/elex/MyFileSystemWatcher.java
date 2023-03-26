package io.github.geniot.elex;

import io.github.geniot.elex.tasks.AsynchronousService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Set;

@Component
@Getter
@Setter
public class MyFileSystemWatcher extends FileSystemWatcher implements FileChangeListener {

    @Value("${path.data}")
    private String pathToData;
    @Autowired
    AsynchronousService asynchronousService;

    public MyFileSystemWatcher() {
        super(true, Duration.ofMillis(5000L), Duration.ofMillis(3000L));

    }

    @PostConstruct
    public void init() {
        addSourceDirectory(new File(pathToData));
        addListener(this);
        this.start();
    }

    @PreDestroy
    public void onDestroy() {
        this.stop();
    }

    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        for (ChangedFiles cfiles : changeSet) {
            for (ChangedFile cfile : cfiles.getFiles()) {
                if (cfile.getFile().getName().endsWith(".ezp") ||
                        cfile.getFile().getName().endsWith(".ezr")) {
                    asynchronousService.updatePool();
                }

//                if ( /* (cfile.getType().equals(Type.MODIFY)
//                     || cfile.getType().equals(Type.ADD)
//                     || cfile.getType().equals(Type.DELETE) ) && */ !isLocked(cfile.getFile().toPath())) {
//                    logger.info("Operation: " + cfile.getType() + " On file: " + cfile.getFile().getName() + " is done");
//                }
            }
        }
    }

    private boolean isLocked(Path path) {
        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.WRITE); FileLock lock = ch.tryLock()) {
            return lock == null;
        } catch (IOException e) {
            return true;
        }
    }
}
