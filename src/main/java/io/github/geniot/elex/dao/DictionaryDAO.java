package io.github.geniot.elex.dao;

import io.github.geniot.elex.DatabaseServer;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DictionaryDAO {

    private final String iconSelect = "SELECT icon FROM dictionaries WHERE dictionaries.id=?;";

    private final String dictionariesSelect = "SELECT \n" +
            "dictionaries.id,\n" +
            "dictionaries.short_name,\n" +
            "(SELECT languages.2_letter_code FROM languages WHERE languages.id=dictionaries.index_language_id) AS index_language,\n" +
            "(SELECT languages.2_letter_code FROM languages WHERE languages.id=dictionaries.contents_language_id) AS contents_language\n" +
            " FROM dictionaries;";

    public List<Dictionary> getDictionaries(Model model) throws Exception {
        List<Dictionary> dictionaries = new ArrayList<>();
        Connection connection = DatabaseServer.getConnection();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(dictionariesSelect);
            while (rs.next()) {
                Integer dictionaryId = rs.getInt("id");
                String shortName = rs.getString("short_name");
                String indexLanguage = rs.getString("index_language");
                String contentsLanguage = rs.getString("contents_language");

                Dictionary uiDictionary = new Dictionary();
                uiDictionary.setId(dictionaryId);
                uiDictionary.setName(shortName);
                uiDictionary.setIndexLanguageCode(indexLanguage);
                uiDictionary.setContentsLanguageCode(contentsLanguage);
                uiDictionary.setSelected(model.isDictionarySelected(shortName));

                dictionaries.add(uiDictionary);
            }
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        } finally {
            connection.close();
        }
        return dictionaries;
    }

    public byte[] getIcon(int id) throws Exception {
        Connection connection = DatabaseServer.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(iconSelect);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBytes("icon");
            } else {
                return null;
            }
        } catch (SQLException e) {
            Logger.getInstance().log(e);
            return null;
        } finally {
            connection.close();
        }
    }
}
