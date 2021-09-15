package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ServerSettings {
    private Map<String, String> disabledDictionariesMap = new HashMap<>();
}
