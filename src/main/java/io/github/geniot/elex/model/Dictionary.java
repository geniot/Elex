package io.github.geniot.elex.model;

public class Dictionary {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.id = (this.name.hashCode() & 0xfffffff);
    }
}
