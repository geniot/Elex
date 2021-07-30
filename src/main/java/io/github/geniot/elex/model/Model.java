package io.github.geniot.elex.model;

import java.util.HashMap;
import java.util.Map;

public class Model {
    private Language[] sourceLanguages = new Language[]{};
    private Dictionary[] dictionaries = new Dictionary[]{};
    private Headword[] headwords = new Headword[]{};
    private Entry[] entries = new Entry[]{};
    private FullTextHit[] searchResults = new FullTextHit[]{};

    private Map<String, String> selectedHeadwords = new HashMap<>();
    private Map<String, String> userInputs = new HashMap<>();
    private int selectedIndex = 0;
    private int visibleSize = 0;

    private Action action = Action.INDEX;

    public String getSelectedSourceLanguage() {
        for (Language sourceLanguage : sourceLanguages) {
            if (sourceLanguage.getSelected()) {
                return sourceLanguage.getSourceCode();
            }
        }
        return "";
    }

    public String getSelectedTargetLanguage() {
        for (Language sourceLanguage : sourceLanguages) {
            if (sourceLanguage.getSelected()) {
                for (Language targetLanguage : sourceLanguage.getTargetLanguages()) {
                    if (targetLanguage.getSelected()) {
                        return targetLanguage.getSourceCode();
                    }
                }
            }
        }
        return "";
    }

    public boolean isDictionarySelected(String name) {
        for (Dictionary dictionary : dictionaries) {
            if (dictionary.getName().equals(name) && dictionary.getSelected()) {
                return true;
            }
        }
        return false;
    }

    public boolean isDictionaryCurrentSelected(String name) {
        for (Dictionary dictionary : dictionaries) {
            if (dictionary.getName().equals(name) && dictionary.getSelected() && dictionary.getCurrent()) {
                return true;
            }
        }
        return false;
    }

    public String getCurrentSelectedHeadword() {
        String currentKey = getSelectedSourceLanguage();// + "-" + getSelectedTargetLanguage();
        for (String key : selectedHeadwords.keySet()) {
            if (key.equals(currentKey)) {
                return selectedHeadwords.get(key);
            }
        }
        for (Headword hw : headwords) {
            if (hw.getSelected()) {
                return hw.getText();
            }
        }
        return "welcome";
    }

    public void setCurrentSelectedHeadword(String hw) {
        String currentKey = getSelectedSourceLanguage();// + "-" + getSelectedTargetLanguage();
        selectedHeadwords.put(currentKey, hw);
    }

    enum Action {
        INDEX, NEXT_WORD, NEXT_PAGE, NEXT_TEN_PAGES, TO_END,
        SEARCH, PREVIOUS_WORD, PREVIOUS_PAGE, PREVIOUS_TEN_PAGES, TO_START
    }

    public Language[] getSourceLanguages() {
        return sourceLanguages;
    }

    public void setSourceLanguages(Language[] sourceLanguages) {
        this.sourceLanguages = sourceLanguages;
    }

    public Dictionary[] getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(Dictionary[] dictionaries) {
        this.dictionaries = dictionaries;
    }

    public Headword[] getHeadwords() {
        return headwords;
    }

    public void setHeadwords(Headword[] headwords) {
        this.headwords = headwords;
    }

    public Entry[] getEntries() {
        return entries;
    }

    public void setEntries(Entry[] entries) {
        this.entries = entries;
    }

    public FullTextHit[] getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(FullTextHit[] searchResults) {
        this.searchResults = searchResults;
    }

    public Map<String, String> getSelectedHeadwords() {
        return selectedHeadwords;
    }

    public void setSelectedHeadwords(Map<String, String> selectedHeadwords) {
        this.selectedHeadwords = selectedHeadwords;
    }

    public Map<String, String> getUserInputs() {
        return userInputs;
    }

    public void setUserInputs(Map<String, String> userInputs) {
        this.userInputs = userInputs;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public int getVisibleSize() {
        return visibleSize;
    }

    public void setVisibleSize(int visibleSize) {
        this.visibleSize = visibleSize;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
