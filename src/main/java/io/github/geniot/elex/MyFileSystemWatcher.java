package io.github.geniot.elex;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.Duration;

@Component
@Getter
@Setter
public class MyFileSystemWatcher extends FileSystemWatcher {

    @Value("${path.data}")
    private String pathToData;

    public MyFileSystemWatcher() {
        super(true, Duration.ofMillis(5000L), Duration.ofMillis(3000L));

    }

    @PostConstruct
    public void init() {
        addSourceDirectory(new File(pathToData));
        addListener(new MyFileChangeListener());
    }
}
