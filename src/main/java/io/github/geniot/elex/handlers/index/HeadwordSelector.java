package io.github.geniot.elex.handlers.index;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Model;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class HeadwordSelector {
    Logger logger = LoggerFactory.getLogger(HeadwordSelector.class);

    @Autowired
    DictionariesPool dictionariesPool;

    public String select(Model model,
                         Set<ElexDictionary> set,
                         IteratorsWrapper forwardIteratorsWrapper,
                         IteratorsWrapper backwardIteratorsWrapper) throws Exception {
        String selectedHeadword = model.getSelectedHeadword();
        selectedHeadword = bestMatch(forwardIteratorsWrapper, backwardIteratorsWrapper, selectedHeadword);

        if (model.getAction().equals(Action.TO_START)) {
            String minHeadword = dictionariesPool.getMinHeadword(set);
            selectedHeadword = scroll(forwardIteratorsWrapper, minHeadword, model.getSelectedIndex());
        } else if (model.getAction().equals(Action.TO_END)) {
            String maxHeadword = dictionariesPool.getMaxHeadword(set);
            selectedHeadword = scroll(backwardIteratorsWrapper, maxHeadword, model.getVisibleSize() - model.getSelectedIndex() - 1);
        } else if (model.getAction().equals(Action.NEXT_WORD)) {
            selectedHeadword = scroll(forwardIteratorsWrapper, selectedHeadword, 1);
            model.selectNext();
        } else if (model.getAction().equals(Action.PREVIOUS_WORD)) {
            selectedHeadword = scroll(backwardIteratorsWrapper, selectedHeadword, 1);
            model.selectPrevious();
        } else if (model.getAction().equals(Action.NEXT_PAGE)) {
            selectedHeadword = scroll(forwardIteratorsWrapper, selectedHeadword, model.getVisibleSize());
        } else if (model.getAction().equals(Action.PREVIOUS_PAGE)) {
            selectedHeadword = scroll(backwardIteratorsWrapper, selectedHeadword, model.getVisibleSize());
        } else if (model.getAction().equals(Action.NEXT_TEN_PAGES)) {
            selectedHeadword = scroll(forwardIteratorsWrapper, selectedHeadword, model.getVisibleSize() * 10);
        } else if (model.getAction().equals(Action.PREVIOUS_TEN_PAGES)) {
            selectedHeadword = scroll(backwardIteratorsWrapper, selectedHeadword, model.getVisibleSize() * 10);
        } else if (model.getAction().equals(Action.SEARCH)) {
            String userInput = model.getUserInput();
            String bestMatch = bestMatch(forwardIteratorsWrapper, backwardIteratorsWrapper, userInput);
            if (StringUtils.isEmpty(bestMatch)) {
                model.setExactMatch(false);
            } else {
                if (bestMatch.equals(userInput)) {
                    model.setExactMatch(true);
                } else {
                    model.setExactMatch(false);
                }
                if (bestMatch.substring(0, 1).equalsIgnoreCase(userInput.substring(0, 1))) {
                    selectedHeadword = bestMatch;
                }
            }
        } else if (model.getAction().equals(Action.FT_LINK) ||
                model.getAction().equals(Action.CONTENT_LINK)) {
            String exact = exact(forwardIteratorsWrapper, backwardIteratorsWrapper, model.getFtLink());
            if (exact != null) {
                selectedHeadword = exact;
                model.setExactMatch(true);
            } else {
                model.setExactMatch(false);
                logger.info("\"" + model.getFtLink() +
                        "\" not found in the current index, displaying \"" +
                        selectedHeadword + "\" instead");
            }
        }
        return selectedHeadword;
    }

    private String exact(IteratorsWrapper forwardIteratorsWrapper,
                         IteratorsWrapper backwardIteratorsWrapper,
                         String selectedHeadword) throws Exception {
        forwardIteratorsWrapper.setFrom(selectedHeadword);
        backwardIteratorsWrapper.setFrom(selectedHeadword);
        if (forwardIteratorsWrapper.contains(selectedHeadword) ||
                backwardIteratorsWrapper.contains(selectedHeadword)) {
            return selectedHeadword;
        } else {
            return null;
        }
    }

    private String bestMatch(IteratorsWrapper forwardIteratorsWrapper,
                             IteratorsWrapper backwardIteratorsWrapper,
                             String selectedHeadword) throws Exception {
        forwardIteratorsWrapper.setFrom(selectedHeadword);
        backwardIteratorsWrapper.setFrom(selectedHeadword);
        if (forwardIteratorsWrapper.contains(selectedHeadword) ||
                backwardIteratorsWrapper.contains(selectedHeadword)) {
            return selectedHeadword;
        } else {
            String bestMatch = forwardIteratorsWrapper.next();
            if (bestMatch == null) {
                bestMatch = backwardIteratorsWrapper.next();
            }
            return bestMatch;
        }
    }

    private String scroll(IteratorsWrapper iteratorsWrapper,
                          String from,
                          int amount) {
        iteratorsWrapper.setFrom(from);
        while (amount-- > 0) {
            String next = iteratorsWrapper.next();
            if (next != null) {
                from = next;
            } else {
                return from;
            }
        }
        return from;
    }
}
