package io.github.geniot.elex;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Comparator;
import java.util.Locale;
@Component
public class CaseInsensitiveComparatorV4 implements Comparator<String>, Serializable {
    transient static Logger logger = LoggerFactory.getLogger(CaseInsensitiveComparatorV4.class);
    transient RuleBasedCollator instance;

    private Collator getInstance() {
        if (instance == null) {
            instance = (RuleBasedCollator) Collator.getInstance(Locale.US);
            String rules = instance.getRules();
            try {
                rules = rules.replaceAll("<'\u005f'", "<'-'<' '<'\u005f'");
                instance = new RuleBasedCollator(rules);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
            instance.setStrength(Collator.IDENTICAL);
        }
        return instance;
    }

    @Override
    public int compare(String o1, String o2) {
        if (StringUtils.equalsIgnoreCase(o1, o2)) {
            for (int i = 0; i < o1.length(); i++) {
                Character c1 = o1.charAt(i);
                Character c2 = o2.charAt(i);
                if (c1.charValue() != c2.charValue()) {
                    return c1.compareTo(c2);
                }
            }
            return getInstance().compare(o1, o2);
        } else {
            return getInstance().compare(o1.toLowerCase(Locale.US), o2.toLowerCase(Locale.US));
        }
    }
}