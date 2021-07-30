package io.github.geniot.elex.model;

public class Dictionary {
    private int id;
    private String name;
    private boolean selected = true;
    private boolean current = true;

    public boolean getCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public static int idFromName(String name) {
        return name.hashCode() & 0xfffffff;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

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
        this.id = idFromName(name);
    }
}
