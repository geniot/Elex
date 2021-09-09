package io.github.geniot.elex.ezip.model;

public enum Language {
    EN("English"),
    RU("Russian");

    public final String label;

    private Language(String label) {
        this.label = label;
    }

    public static Language valueOfLabel(String label) {
        for (Language e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
