package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.HtmlUtils;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.*;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class ElexDataHandler extends BaseHttpHandler {
    private Gson gson = new Gson();

    enum Direction {
        FORWARD, BACKWARD
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            long t1 = System.currentTimeMillis();
            InputStream input = httpExchange.getRequestBody();
            StringBuffer stringBuilder = new StringBuffer();

            new BufferedReader(new InputStreamReader(input)).lines().forEach((String s) -> stringBuilder.append(s + "\n"));

            Model model = gson.fromJson(stringBuilder.toString(), Model.class);
            long t2 = System.currentTimeMillis();
            updateLanguages(model);
            long t3 = System.currentTimeMillis();
            updateDictionaries(model);
            long t4 = System.currentTimeMillis();
            updateHeadwords(model);
            long t5 = System.currentTimeMillis();
            updateEntries(model);
            long t6 = System.currentTimeMillis();
//        Logger.getInstance().log(stringBuilder.toString());

            String s = gson.toJson(model);
//            String s = new ObjectMapper().writeValueAsString(model);

            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));
            long t7 = System.currentTimeMillis();
            Logger.getInstance().log((t2 - t1) + "-" + (t3 - t2) + "-" + (t4 - t3) + "-" + (t5 - t4) + "-" + (t6 - t5) + "-" + (t7 - t6));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    private void updateEntries(Model model) {
        String article = null;
        Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
        for (IDictionary dictionary : dictionarySet) {
            Properties properties = dictionary.getProperties();
            String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
            if (model.isDictionaryCurrentSelected(name)) {
                article = dictionary.read(model.getCurrentSelectedHeadword());
                break;
            }
        }

        List<Entry> entries = new ArrayList<>();
        if (article != null) {
            article = HtmlUtils.toHtml(article);
            entries.add(genEntry(model.getCurrentSelectedHeadword(), article));
        }
        model.setEntries(entries.toArray(new Entry[entries.size()]));
    }

    private Entry genEntry(String hwd, String article) {
        Entry entry = new Entry();
        entry.setHeadword(hwd);
        entry.setBody(article);
        return entry;
    }

    private void updateHeadwords(Model model) {
        TreeSet<String> combinedIndex = DictionariesPool.getInstance().getCombinedIndex(model);
        if (combinedIndex.isEmpty()) {
            model.setHeadwords(new Headword[]{});
            return;
        }

        SortedSet<String> headwords = new TreeSet<>();
        String selectedHeadword = model.getCurrentSelectedHeadword();
        if (!combinedIndex.contains(selectedHeadword)) {
            String bestMatch = combinedIndex.lower(selectedHeadword);
            if (bestMatch == null) {
                bestMatch = combinedIndex.higher(selectedHeadword);
            }
            selectedHeadword = bestMatch;
        }

        if (model.getAction().equals(Action.TO_START)) {
            selectedHeadword = combinedIndex.first();
        } else if (model.getAction().equals(Action.TO_END)) {
            selectedHeadword = combinedIndex.last();
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
                selectedHeadword = combinedIndex.higher(userInput);
            }
        }

        HeadwordIterator<String> tailIterator = new HeadwordIterator(combinedIndex, selectedHeadword, -1);
        HeadwordIterator<String> headIterator = new HeadwordIterator(combinedIndex, selectedHeadword, 1);

        if (combinedIndex.contains(selectedHeadword) && model.getVisibleSize() > 0) {
            headwords.add(selectedHeadword);
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
        for (String s : headwords) {
            Headword hw = new Headword(s);
            if (s.equals(selectedHeadword)) {
                hw.setSelected(true);
            }
            headwordsArray[counter++] = hw;
        }

        for (Headword hw : headwordsArray) {
            if (hw.getText().equals(selectedHeadword)) {
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

    private String scroll(TreeSet<String> combinedIndex, String from, int amount, Direction direction) {
        String next = direction.equals(Direction.FORWARD) ? combinedIndex.higher(from) : combinedIndex.lower(from);
        --amount;
        while (next != null && amount-- > 0) {
            next = direction.equals(Direction.FORWARD) ? combinedIndex.higher(next) : combinedIndex.lower(next);
            if (next != null) {
                from = next;
            }
        }
        return next == null ? from : next;
    }

    private void updateDictionaries(Model model) {
        List<Dictionary> dictionaries = new ArrayList<>();
        Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
        for (IDictionary dictionary : dictionarySet) {
            Properties properties = dictionary.getProperties();
            String sourceLanguage = properties.getProperty(IDictionary.DictionaryProperty.INDEX_LANGUAGE.name()).toUpperCase();
            String contentsLanguage = properties.getProperty(IDictionary.DictionaryProperty.CONTENTS_LANGUAGE.name()).toUpperCase();

            String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
            Dictionary uiDictionary = new Dictionary();
            uiDictionary.setName(name);
            uiDictionary.setSelected(model.isDictionarySelected(name));

            if (sourceLanguage.equals(model.getSelectedSourceLanguage()) && contentsLanguage.equals(model.getSelectedTargetLanguage())) {
                uiDictionary.setCurrent(true);
            } else {
                uiDictionary.setCurrent(false);
            }
            dictionaries.add(uiDictionary);
        }
        Dictionary[] dictionariesArray = dictionaries.toArray(new Dictionary[dictionaries.size()]);
        model.setDictionaries(dictionariesArray);
    }

    private void updateLanguages(Model model) {
        SortedMap<String, Language> resultLanguagesMap = new TreeMap<>();
        Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
        for (IDictionary dictionary : dictionarySet) {

            Properties properties = dictionary.getProperties();
            String dictionarySourceLanguage = properties.getProperty(IDictionary.DictionaryProperty.INDEX_LANGUAGE.name()).toUpperCase();
            String dictionaryTargetLanguage = properties.getProperty(IDictionary.DictionaryProperty.CONTENTS_LANGUAGE.name()).toUpperCase();

            Language sourceLanguage = resultLanguagesMap.get(dictionarySourceLanguage);
            if (sourceLanguage == null) {
                sourceLanguage = new Language(dictionarySourceLanguage);
            }
            if (dictionarySourceLanguage.equals(model.getSelectedSourceLanguage())) {
                sourceLanguage.setSelected(true);
            }

            Language targetLanguage = new Language(dictionaryTargetLanguage);
            if (dictionaryTargetLanguage.equals(model.getSelectedTargetLanguage())) {
                targetLanguage.setSelected(true);
            }
            sourceLanguage.getTargetLanguages().add(targetLanguage);
            resultLanguagesMap.put(dictionarySourceLanguage, sourceLanguage);
        }
        //what if there are no selections?
        if (StringUtils.isEmpty(model.getSelectedSourceLanguage())) {
            if (resultLanguagesMap.size() > 0) {
                resultLanguagesMap.values().iterator().next().setSelected(true);
            }
        }
        if (StringUtils.isEmpty(model.getSelectedTargetLanguage())) {
            if (resultLanguagesMap.size() > 0) {
                resultLanguagesMap.values().iterator().next().getTargetLanguages().first().setSelected(true);
            }
        }

        model.setSourceLanguages(resultLanguagesMap.values().toArray(new Language[resultLanguagesMap.size()]));
    }
}
