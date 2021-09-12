package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDictionary extends Dictionary implements Comparable<AdminDictionary> {
    private String fileName;
    private String dataPath;
    private String fileSize;
    private String resourcesFileName;
    private String resourcesFileSize;
    private int resourcesCount;
    private String ftIndexSize;
    private String totalSize;
    private String status = "enabled";

    @Override
    public int compareTo(AdminDictionary o) {
        return this.getName().compareTo(o.getName());
    }
}
