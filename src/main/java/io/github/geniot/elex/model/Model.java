package io.github.geniot.elex.model;

import org.apache.commons.lang3.StringUtils;

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
    private String ftLink;

    private int visibleSize = 0;
    private int selectedIndex = 0;
    private boolean startReached = false;
    private boolean endReached = false;
    private String searchResultsFor;
    private boolean lockFullText;
    private String baseApiUrl;
    private boolean exactMatch = false;

    public boolean isExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    public String getBaseApiUrl() {
        return baseApiUrl;
    }

    public void setBaseApiUrl(String baseApiUrl) {
        this.baseApiUrl = baseApiUrl;
    }

    public boolean getLockFullText() {
        return lockFullText;
    }

    public void setLockFullText(boolean lockFullText) {
        this.lockFullText = lockFullText;
    }

    public String getSearchResultsFor() {
        return searchResultsFor;
    }

    public void setSearchResultsFor(String searchResultsFor) {
        this.searchResultsFor = searchResultsFor;
    }

    private Action action = Action.INDEX;

    public String getSelectedSourceLanguage() {
        for (Language sourceLanguage : sourceLanguages) {
            if (sourceLanguage.getSelected()) {
                return sourceLanguage.getSourceCode();
            }
        }
        return "";
    }

    public void deselectAllSourceLanguages() {
        for (Language sourceLanguage : sourceLanguages) {
            sourceLanguage.setSelected(false);
        }
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
            if (dictionary.getName().equals(name)) {
                if (dictionary.getSelected() || this.action.equals(Action.INIT)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDictionaryCurrent(String name) {
        for (Dictionary dictionary : dictionaries) {
            if (dictionary.getName().equals(name)) {
                if (dictionary.getCurrent()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSelectedHeadword() {
        String currentKey = getSelectedSourceLanguage();// + "-" + getSelectedTargetLanguage();
        for (String key : selectedHeadwords.keySet()) {
            if (key.equals(currentKey)) {
                return selectedHeadwords.get(key);
            }
        }
        for (Headword hw : headwords) {
            if (hw.getSelected()) {
                return hw.getName();
            }
        }
        return "welcome";
    }

    public void setSelectedHeadword(String hw) {
        String currentKey = getSelectedSourceLanguage();// + "-" + getSelectedTargetLanguage();
        selectedHeadwords.put(currentKey, hw);
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

    public int getSelectedIndex() {
        for (int i = 0; i < headwords.length; i++) {
            if (headwords[i].getSelected()) {
                return i;
            }
        }
        return selectedIndex;
    }

    public void selectNext() {
        for (int i = 0; i < headwords.length - 1; i++) {
            if (headwords[i].getSelected()) {
                headwords[i].setSelected(false);
                headwords[i + 1].setSelected(true);
                return;
            }
        }
    }

    public void selectPrevious() {
        for (int i = headwords.length - 1; i > 0; i--) {
            if (headwords[i].getSelected()) {
                headwords[i].setSelected(false);
                headwords[i - 1].setSelected(true);
                return;
            }
        }
    }

    public String getUserInput() {
        String input = userInputs.get(getSelectedSourceLanguage());
        if (StringUtils.isEmpty(input) || !action.equals(Action.SEARCH)) {
            input = getSelectedHeadword();
        }
        return input;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public boolean getStartReached() {
        return startReached;
    }

    public void setStartReached(boolean startReached) {
        this.startReached = startReached;
    }

    public boolean getEndReached() {
        return endReached;
    }

    public void setEndReached(boolean endReached) {
        this.endReached = endReached;
    }

    public String getFtLink() {
        return ftLink;
    }

    public void setFtLink(String ftLink) {
        this.ftLink = ftLink;
    }

}
