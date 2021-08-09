package io.github.geniot.elex.dao;

import io.github.geniot.elex.DatabaseServer;
import io.github.geniot.elex.model.Availability;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Headword;
import io.github.geniot.elex.model.Model;
import io.github.geniot.elex.util.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class IndexDAO {

    private final String selectIndex = "SELECT id,dictionary_id,header FROM entries;";


    private TreeMap<String, Availability> index = new TreeMap<>();

    /**
     * todo: maybe limit the size, remove old
     */
    private Map<String, TreeSet<Headword>> cachedCombined = new HashMap<>();

    private static IndexDAO instance;

    public static IndexDAO getInstance() {
        if (instance == null) {
            instance = new IndexDAO();
        }
        return instance;
    }

    public void reindex() {
        long t1 = System.currentTimeMillis();
        index.clear();
        Connection connection = null;
        try {
            connection = DatabaseServer.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(selectIndex);
            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer dictionaryId = rs.getInt("dictionary_id");
                String header = rs.getString("header");

                if (header.contains("\n")
                        || header.contains("(")
                        || header.contains(")")
                        || header.contains("\\")
                ) {
                    throw new RuntimeException(header);
                } else {
                    Availability availability = new Availability();
                    if (header.contains("{")
                            || header.contains("}")) {
                        header = header.replaceAll("\\{[^}]+\\}", "");
                        availability.getEntryIds().add(id);
                    }
                    availability.getBitSet().set(dictionaryId);
                    index.put(header, availability);
                }

            }
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
        long t2 = System.currentTimeMillis();
        Logger.getInstance().log("Re-indexing done in: " + (t2 - t1) + " ms");
    }

    public TreeSet<Headword> getIndex(Model model) {
        long t1 = System.currentTimeMillis();
        String shelfKey = model.getShelfSelectedKey();
        TreeSet<Headword> combinedIndex = cachedCombined.get(shelfKey);
        if (combinedIndex == null) {
            combinedIndex = new TreeSet<>();
            for (String hwd : index.keySet()) {
                Availability availability = index.get(hwd);
                Set<Dictionary> shelf = model.getShelfSelected();
                for (Dictionary d : shelf) {
                    if (availability.getBitSet().get(d.getId())) {
                        combinedIndex.add(new Headword(hwd));
                        break;
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            Logger.getInstance().log("Combined index generated in: " + (t2 - t1) + " ms");
            cachedCombined.put(shelfKey, combinedIndex);
        }
        return combinedIndex;
    }
}
