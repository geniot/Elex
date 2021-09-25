package io.github.geniot.elex.tools.convert;

public enum Language {
    EN("English"),
    RU("Russian"),
    DE("German"),
    NL("Dutch");

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
