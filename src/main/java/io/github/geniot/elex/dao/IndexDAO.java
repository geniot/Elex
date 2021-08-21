package io.github.geniot.elex.dao;

import io.github.geniot.elex.model.Headword;
import io.github.geniot.elex.model.Model;

import java.util.TreeSet;

public class IndexDAO {

    private final String selectIndex = "SELECT id,dictionary_id,header FROM entries;";

    private static IndexDAO instance;

    public static IndexDAO getInstance() {
        if (instance == null) {
            instance = new IndexDAO();
        }
        return instance;
    }


    public TreeSet<Headword> getIndex(Model model) {
        long t1 = System.currentTimeMillis();
        TreeSet<Headword> combinedIndex = new TreeSet<>();

        return combinedIndex;
    }
}
