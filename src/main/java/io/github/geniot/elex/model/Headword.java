package io.github.geniot.elex.model;

public class Headword {
    private String text;
    private boolean selected = false;

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
