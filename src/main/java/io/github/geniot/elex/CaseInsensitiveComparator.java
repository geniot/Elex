package io.github.geniot.elex;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Comparator;
@Component
public class CaseInsensitiveComparator implements Comparator<String>, Serializable {
    @Override
    public int compare(String o1, String o2) {
        if (StringUtils.equalsIgnoreCase(o1, o2)) {
            for (int i = 0; i < o1.length(); i++) {
                Character c1 = o1.charAt(i);
                Character c2 = o2.charAt(i);
                if (c1 != c2) {
                    return c2.compareTo(c1);
                }
            }
            return o2.compareTo(o1);
        } else {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    }
}