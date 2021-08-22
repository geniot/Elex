package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.model.CaseInsensitiveComparator;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.handlers.index.Direction;
import io.github.geniot.elex.handlers.index.HeadwordSelector;
import io.github.geniot.elex.handlers.index.IteratorsWrapper;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Headword;
import io.github.geniot.elex.model.Model;

import java.util.*;

public class HeadwordsUpdater {
    HeadwordSelector headwordSelector = new HeadwordSelector();
    CaseInsensitiveComparator caseInsensitiveComparator = new CaseInsensitiveComparator();

    public void updateHeadwords(Model model) throws Exception {
        Set<ElexDictionary> set = new HashSet<>(DictionariesPool.getInstance().getElexDictionaries(model).values());

        SortedSet<String> index = new TreeSet<>(caseInsensitiveComparator);

        IteratorsWrapper forwardIteratorsWrapper = new IteratorsWrapper(set, model.getSelectedHeadword(), Direction.FORWARD);
        IteratorsWrapper backwardIteratorsWrapper = new IteratorsWrapper(set, model.getSelectedHeadword(), Direction.BACKWARD);

        String selectedHeadword = headwordSelector.select(model, set, forwardIteratorsWrapper, backwardIteratorsWrapper);
        int pageSize = model.getVisibleSize();
        int viewOffset = model.getSelectedIndex();

        if (forwardIteratorsWrapper.contains(selectedHeadword)) {
            index.add(selectedHeadword);
        }


        forwardIteratorsWrapper.setFrom(selectedHeadword);
        backwardIteratorsWrapper.setFrom(selectedHeadword);

        int forwardCounter = pageSize - viewOffset - 1;
        while (index.size() < pageSize && forwardIteratorsWrapper.hasNext() && forwardCounter > 0) {
            index.add(forwardIteratorsWrapper.next());
            --forwardCounter;
        }
        while (index.size() < pageSize && backwardIteratorsWrapper.hasNext()) {
            index.add(backwardIteratorsWrapper.next());
        }
        while (index.size() < pageSize && forwardIteratorsWrapper.hasNext()) {
            index.add(forwardIteratorsWrapper.next());
        }

        if (index.isEmpty()) {
            model.setHeadwords(new Headword[]{});
            model.setStartReached(true);
            model.setEndReached(true);
            return;
        }

        List<Headword> headwords = new ArrayList<>();
        for (String s : index) {
            Headword hw = new Headword(s);
            if (hw.getName().equals(selectedHeadword)) {
                hw.setSelected(true);
            } else {
                hw.setSelected(false);
            }
            headwords.add(hw);
        }

        if (headwords.get(0).getName().equals(DictionariesPool.getInstance().getMinHeadword(set))) {
            model.setStartReached(true);
        } else {
            model.setStartReached(false);
        }

        if (headwords.get(headwords.size() - 1).getName().equals(DictionariesPool.getInstance().getMaxHeadword(set))) {
            model.setEndReached(true);
        } else {
            model.setEndReached(false);
        }

        model.setAction(Action.INDEX);
        model.setSelectedHeadword(selectedHeadword);
        model.setHeadwords(headwords.toArray(new Headword[headwords.size()]));
    }

}
