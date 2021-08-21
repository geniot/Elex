package io.github.geniot.elex.dao;

import io.github.geniot.elex.DatabaseServer;
import io.github.geniot.elex.model.FullTextHit;
import io.github.geniot.elex.model.Headword;
import io.github.geniot.elex.model.Model;
import io.github.geniot.elex.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContentDAO {
    private final String selectArticle = "SELECT article FROM articles WHERE header=?;";
    private final String searchArticle = "SELECT * FROM articles WHERE MATCH(article)\n" +
            "AGAINST(? IN NATURAL LANGUAGE MODE) LIMIT 100;";
    private static ContentDAO instance;

    public static ContentDAO getInstance() {
        if (instance == null) {
            instance = new ContentDAO();
        }
        return instance;
    }

    public String getArticle(Model model) {
        Connection connection = null;
        try {
            connection = DatabaseServer.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(selectArticle);
            ps.setString(1, model.getSelectedHeadword());
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getString("article");
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Logger.getInstance().log(e);
                }
            }
        }
        return null;
    }

    public List<FullTextHit> searchArticle(Model model) {
        List<FullTextHit> hits = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DatabaseServer.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(searchArticle);
            ps.setString(1, model.getUserInput());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FullTextHit hit = new FullTextHit();
                hit.setHeadword(new Headword(rs.getString("header")));
                hit.setDictionaryId(rs.getInt("dictionary_id"));
                hit.setExtract(generateAbstract(model.getUserInput(), rs.getString("article")));
                hits.add(hit);
            }
            return hits;
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Logger.getInstance().log(e);
                }
            }
        }
        return null;
    }

    private String generateAbstract(String userInput, String article) {
        article = stripTags(article);
        String[] splits = userInput.split(" ");
        for (String split : splits) {
            article = article.replaceAll("\\b"+split+"\\b", "<b>" + split + "</b>");
        }
        return article;
    }

    private String stripTags(String entry) {
        entry = entry.replaceAll("\\[[^]]+\\]", " ");
        entry = entry.replaceAll("\t|\r|\n", " ");
        entry = entry.replaceAll("<[^>]+>", " ");
        entry = entry.replaceAll("\\s\\s", " ");
        entry = entry.replaceAll("  ", " ");
        entry = entry.replaceAll("_", " ");
        return entry;
    }
}
