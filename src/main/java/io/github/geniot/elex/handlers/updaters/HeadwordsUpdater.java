package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.dictiographer.model.Headword;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.handlers.HeadwordIterator;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Model;
import io.github.geniot.indexedtreemap.IndexedTreeSet;

import java.util.SortedSet;
import java.util.TreeSet;

public class HeadwordsUpdater {

    public void updateHeadwords(Model model) {
        IndexedTreeSet<Headword> combinedIndex = DictionariesPool.getInstance().getCombinedIndex(model);
        if (combinedIndex.isEmpty()) {
            model.setHeadwords(new Headword[]{});
            model.setStartReached(true);
            model.setEndReached(true);
            return;
        }

        SortedSet<Headword> headwords = new TreeSet<>();
        String selectedHeadword = model.getCurrentSelectedHeadword();
        if (!combinedIndex.contains(new Headword(selectedHeadword))) {
            Headword bestMatch = combinedIndex.lower(new Headword(selectedHeadword));
            if (bestMatch == null) {
                bestMatch = combinedIndex.higher(new Headword(selectedHeadword));
            }
            selectedHeadword = bestMatch.getName();
        }

        if (model.getAction().equals(Action.TO_START)) {
            selectedHeadword = combinedIndex.first().getName();
        } else if (model.getAction().equals(Action.TO_END)) {
            selectedHeadword = combinedIndex.last().getName();
        } else if (model.getAction().equals(Action.NEXT_WORD)) {
            selectedHeadword = scroll(combinedIndex, selectedHeadword, 1, Direction.FORWARD);
            model.selectNext();
        } else if (model.getAction().equals(Action.PREVIOUS_WORD)) {
            selectedHeadword = scroll(combinedIndex, selectedHeadword, 1, Direction.BACKWARD);
            model.selectPrevious();
        } else if (model.getAction().equals(Action.NEXT_PAGE)) {
            selectedHeadword = scroll(combinedIndex, selectedHeadword, model.getVisibleSize(), Direction.FORWARD);
        } else if (model.getAction().equals(Action.PREVIOUS_PAGE)) {
            selectedHeadword = scroll(combinedIndex, selectedHeadword, model.getVisibleSize(), Direction.BACKWARD);
        } else if (model.getAction().equals(Action.NEXT_TEN_PAGES)) {
            selectedHeadword = scroll(combinedIndex, selectedHeadword, model.getVisibleSize() * 10, Direction.FORWARD);
        } else if (model.getAction().equals(Action.PREVIOUS_TEN_PAGES)) {
            selectedHeadword = scroll(combinedIndex, selectedHeadword, model.getVisibleSize() * 10, Direction.BACKWARD);
        } else if (model.getAction().equals(Action.SEARCH)) {
            String userInput = model.getUserInput();
            if (combinedIndex.contains(userInput)) {
                selectedHeadword = userInput;
            } else {
                String higher = combinedIndex.higher(new Headword(userInput)).getName();
                if (higher != null) {
                    selectedHeadword = higher;
                }
            }
        }

        HeadwordIterator<Headword> tailIterator = new HeadwordIterator(combinedIndex, new Headword(selectedHeadword), -1);
        HeadwordIterator<Headword> headIterator = new HeadwordIterator(combinedIndex, new Headword(selectedHeadword), 1);

        if (combinedIndex.contains(new Headword(selectedHeadword)) && model.getVisibleSize() > 0) {
            headwords.add(new Headword(selectedHeadword));
        }

        for (int i = model.getSelectedIndex(); i < model.getVisibleSize() - 1; i++) {
            if (tailIterator.hasNext() && headwords.size() < model.getVisibleSize()) {
                headwords.add(tailIterator.next());
            }
        }

        for (int i = 0; i < model.getSelectedIndex(); i++) {
            if (headIterator.hasNext() && headwords.size() < model.getVisibleSize()) {
                headwords.add(headIterator.next());
            }
        }

        while (headwords.size() < model.getVisibleSize() &&
                (headIterator.hasNext() || tailIterator.hasNext())) {

            if (headIterator.hasNext() && headwords.size() < model.getVisibleSize()) {
                headwords.add(headIterator.next());
            }
            if (tailIterator.hasNext() && headwords.size() < model.getVisibleSize()) {
                headwords.add(tailIterator.next());
            }
        }

        Headword[] headwordsArray = new Headword[headwords.size()];
        int counter = 0;
        for (Headword hw : headwords) {
            if (hw.getName().equals(selectedHeadword)) {
                hw.setSelected(true);
            }
            headwordsArray[counter++] = hw;
        }

        for (Headword hw : headwordsArray) {
            if (hw.getName().equals(selectedHeadword)) {
                hw.setSelected(true);
            } else {
                hw.setSelected(false);
            }
        }

        if (headwords.first().equals(combinedIndex.first())) {
            model.setStartReached(true);
        } else {
            model.setStartReached(false);
        }

        if (headwords.last().equals(combinedIndex.last())) {
            model.setEndReached(true);
        } else {
            model.setEndReached(false);
        }

        model.setAction(Action.INDEX);
        model.setCurrentSelectedHeadword(selectedHeadword);
        model.setHeadwords(headwordsArray);
    }

    private String scroll(IndexedTreeSet<Headword> combinedIndex, String from, int amount, Direction direction) {
        String next = direction.equals(Direction.FORWARD) ? combinedIndex.higher(new Headword(from)).getName() : combinedIndex.lower(new Headword(from)).getName();
        --amount;
        while (next != null && amount-- > 0) {
            next = direction.equals(Direction.FORWARD) ? combinedIndex.higher(new Headword(next)).getName() : combinedIndex.lower(new Headword(next)).getName();
            if (next != null) {
                from = next;
            }
        }
        return next == null ? from : next;
    }

    enum Direction {
        FORWARD, BACKWARD
    }

}
