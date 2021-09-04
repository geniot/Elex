package io.github.geniot.elex.handlers.index;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.Logger;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class HeadwordSelector {

    @Autowired
    DictionariesPool dictionariesPool;

    public String select(Model model,
                         Set<ElexDictionary> set,
                         IteratorsWrapper forwardIteratorsWrapper,
                         IteratorsWrapper backwardIteratorsWrapper) throws Exception {
        String selectedHeadword = model.getSelectedHeadword();
        selectedHeadword = bestMatch(forwardIteratorsWrapper, backwardIteratorsWrapper, selectedHeadword);

        if (model.getAction().equals(Action.TO_START)) {
            selectedHeadword = dictionariesPool.getMinHeadword(set);
        } else if (model.getAction().equals(Action.TO_END)) {
            selectedHeadword = dictionariesPool.getMaxHeadword(set);
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
            if (bestMatch.equals(userInput)) {
                model.setExactMatch(true);
            } else {
                model.setExactMatch(false);
            }
            selectedHeadword = bestMatch;
        } else if (model.getAction().equals(Action.FT_LINK)) {
            String exact = exact(forwardIteratorsWrapper, backwardIteratorsWrapper, model.getFtLink());
            if (exact != null) {
                selectedHeadword = exact;
                model.setExactMatch(true);
            } else {
                model.setExactMatch(false);
                Logger.getInstance().log("\"" + model.getFtLink() +
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
