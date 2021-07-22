package io.github.geniot.elex.model;

public class Language {
    private String sourceCode;
    private String[] targetCodes;

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String[] getTargetCodes() {
        return targetCodes;
    }

    public void setTargetCodes(String[] targetCodes) {
        this.targetCodes = targetCodes;
    }
}
