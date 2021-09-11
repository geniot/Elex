package io.github.geniot.elex.model;

public class AdminDictionary extends Dictionary {
    private String fileName;
    private String dataPath;
    private String fileSize;
    private String resourcesFileName;
    private String resourcesFileSize;
    private int resourcesCount;
    private String ftIndexSize;
    private String totalSize;

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public String getFtIndexSize() {
        return ftIndexSize;
    }

    public void setFtIndexSize(String ftIndexSize) {
        this.ftIndexSize = ftIndexSize;
    }

    public int getResourcesCount() {
        return resourcesCount;
    }

    public void setResourcesCount(int resourcesCount) {
        this.resourcesCount = resourcesCount;
    }

    public String getResourcesFileSize() {
        return resourcesFileSize;
    }

    public void setResourcesFileSize(String resourcesFileSize) {
        this.resourcesFileSize = resourcesFileSize;
    }

    public String getResourcesFileName() {
        return resourcesFileName;
    }

    public void setResourcesFileName(String resourcesFileName) {
        this.resourcesFileName = resourcesFileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
}
