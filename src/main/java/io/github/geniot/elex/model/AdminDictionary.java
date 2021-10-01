package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDictionary extends Dictionary{
    private String dataPath;
    private String fileSize;
    private String resourcesFileName;
    private String resourcesFileSize;
    private int resourcesCount;
    private String ftIndexSize;
    private String totalSize;
    private DictionaryStatus status = DictionaryStatus.ENABLED;

    public AdminDictionary() {
        this.setSelected(false);
    }

}
