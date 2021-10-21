package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.HistoryItem;
import io.github.geniot.elex.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class HistoryItemsUpdater {
    Logger logger = LoggerFactory.getLogger(HistoryItemsUpdater.class);

    public void updateHistoryItems(Model model) {
        //we do not update the history if the user clicks on a history link
        if (!model.getAction().equals(Action.HISTORY_LINK)) {
            SortedSet<HistoryItem> historyItems = new TreeSet<>();
            historyItems.addAll(Arrays.asList(model.getHistoryItems()));

            if (model.isExactMatch()) {
                HistoryItem historyItem = new HistoryItem();
                historyItem.setTimestamp(System.currentTimeMillis());
                historyItem.setHeadword(model.getSelectedHeadword());
                historyItem.setSourceLanguage(model.getSelectedSourceLanguage());
                historyItem.setTargetLanguage(model.getSelectedTargetLanguage());

                //replacing if necessary, updating time stamp
                historyItems.remove(historyItem);
                historyItems.add(historyItem);
            }

            HistoryItem[] historyItemsArray = historyItems.toArray(new HistoryItem[historyItems.size()]);
            Arrays.sort(historyItemsArray, new Comparator<HistoryItem>() {
                @Override
                public int compare(HistoryItem o1, HistoryItem o2) {
                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                }
            });
            //todo: make 100 a setting
            if (historyItemsArray.length > 100) {
                historyItemsArray = Arrays.copyOfRange(historyItemsArray, 0, 100);
            }

            model.setHistoryItems(historyItemsArray);
        }
    }
}
