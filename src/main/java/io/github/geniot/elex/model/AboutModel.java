package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class AboutModel {
    private Dictionary dictionary;
    private Map<String, String> abouts = new TreeMap<>();
}
