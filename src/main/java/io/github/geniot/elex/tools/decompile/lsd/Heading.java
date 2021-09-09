package io.github.geniot.elex.tools.decompile.lsd;

import java.util.LinkedHashMap;
import java.util.Map;

public class Heading {
    String text;
    Map<Integer, String> extensions;

    public Heading() {
        this.text = "";
        this.extensions = new LinkedHashMap<>();
    }

    public String ext_text() {
        if (this.extensions.size() == 0) {
            return this.text;
        }
        String res = this.text;
        int offset = 0;
        for (Map.Entry<Integer, String> entry : this.extensions.entrySet()) {
            int idx = entry.getKey();
            String ext = entry.getValue();
            boolean add_braces = !ext.equals("\\");
            if (add_braces) {
                ext = "{" + ext + "}";
            }
            res = res.substring(0, idx + offset) + ext + res.substring(idx + offset);
            if (add_braces) {
                offset += 2;
            }
        }
        return res;
    }
}
