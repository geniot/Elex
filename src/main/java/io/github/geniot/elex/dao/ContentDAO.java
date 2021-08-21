package io.github.geniot.elex.dao;

import io.github.geniot.elex.model.FullTextHit;
import io.github.geniot.elex.model.Model;
import io.github.geniot.elex.util.Logger;

import java.util.List;

public class ContentDAO {

    private static ContentDAO instance;

    public static ContentDAO getInstance() {
        if (instance == null) {
            instance = new ContentDAO();
        }
        return instance;
    }

    public String getArticle(Model model) {

        try {
            return model.getSelectedHeadword();
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
        return null;
    }

    public List<FullTextHit> searchArticle(Model model) {
        return null;
    }
}
