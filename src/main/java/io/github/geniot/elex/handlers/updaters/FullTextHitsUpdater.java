package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.dao.ContentDAO;
import io.github.geniot.elex.model.FullTextHit;
import io.github.geniot.elex.model.Model;
import io.github.geniot.elex.util.Logger;

import java.util.List;

public class FullTextHitsUpdater {
    public void updateFullTextHits(Model model) {
        if (model.getLockFullText()) {
            return;
        }
        try {
            List<FullTextHit> hits = ContentDAO.getInstance().searchArticle(model);
            model.setSearchResultsFor(model.getUserInput());
            model.setSearchResults(hits.toArray(new FullTextHit[hits.size()]));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }
}
