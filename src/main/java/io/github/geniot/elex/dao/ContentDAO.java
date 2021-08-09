package io.github.geniot.elex.dao;

import io.github.geniot.elex.DatabaseServer;
import io.github.geniot.elex.model.Model;
import io.github.geniot.elex.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContentDAO {
    private final String selectArticle = "SELECT article FROM entries WHERE header=?;";
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
}
