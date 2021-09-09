package io.github.geniot.elex.tools.decompile.lsd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArticleHeadingList extends ArrayList<ArticleHeading> {
    public int appended;
    Map<Integer, ArticleHeading> references;

    public ArticleHeadingList() {
        this.appended = 0;
        this.references = new LinkedHashMap<>();
    }


    void append(ArticleHeading item) {
        this.appended += 1;
        // append if item.reference not exists
        int ref = item.reference;
        if (this.references.containsKey(ref)) {
            references.get(ref).merge(item);
        } else {
            references.put(ref, item);
            //if not first then update next_reference in the previous item
            if (size() > 0) {
                get(size() - 1).next_reference = ref;
            }
            add(item);
        }
    }
}
