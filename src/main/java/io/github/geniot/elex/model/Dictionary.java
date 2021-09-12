package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dictionary {
    private int id;
    private String name;
    private String indexLanguageCode;
    private String contentsLanguageCode;
    private boolean selected = true;
    private boolean current = true;
    private int entries;
}
