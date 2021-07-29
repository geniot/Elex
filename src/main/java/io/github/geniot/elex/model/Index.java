package io.github.geniot.elex.model;

public class Index {
    private Headword[] headwords;
    private boolean needsPagination = false;

    public boolean isNeedsPagination() {
        return needsPagination;
    }

    public void setNeedsPagination(boolean needsPagination) {
        this.needsPagination = needsPagination;
    }

    public Headword[] getHeadwords() {
        return headwords;
    }

    public void setHeadwords(Headword[] headwords) {
        this.headwords = headwords;
    }
}
