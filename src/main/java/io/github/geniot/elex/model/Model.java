package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Model {
    private Language[] sourceLanguages = new Language[]{};
    private Dictionary[] dictionaries = new Dictionary[]{};
    private Headword[] headwords = new Headword[]{};
    private Entry[] entries = new Entry[]{};

    private Map<String, String> selectedHeadwords = new HashMap<>();
    private Map<String, String> userInputs = new HashMap<>();
    private String ftLink;

    private int visibleSize = 0;
    private int selectedIndex = 0;
    private boolean startReached = false;
    private boolean endReached = false;
    private String searchResultsFor;
    private String baseApiUrl;
    private boolean exactMatch = false;

    private Action action = Action.INDEX;

    public String getSelectedSourceLanguage() {
        for (Language sourceLanguage : sourceLanguages) {
            if (sourceLanguage.isSelected()) {
                return sourceLanguage.getSourceCode();
            }
        }
        return "";
    }

    public String getSelectedTargetLanguage() {
        for (Language sourceLanguage : sourceLanguages) {
            if (sourceLanguage.isSelected()) {
                for (Language targetLanguage : sourceLanguage.getTargetLanguages()) {
                    if (targetLanguage.isSelected()) {
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
                if (dictionary.isSelected() || this.action.equals(Action.INIT)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDictionaryCurrent(String name) {
        for (Dictionary dictionary : dictionaries) {
            if (dictionary.getName().equals(name)) {
                if (dictionary.isCurrent()) {
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
            if (hw.isSelected()) {
                return hw.getName();
            }
        }
        return "welcome";
    }

    public void setSelectedHeadword(String hw) {
        String currentKey = getSelectedSourceLanguage();// + "-" + getSelectedTargetLanguage();
        selectedHeadwords.put(currentKey, hw);
    }

    public int getSelectedIndex() {
        for (int i = 0; i < headwords.length; i++) {
            if (headwords[i].isSelected()) {
                return i;
            }
        }
        return selectedIndex;
    }

    public void selectNext() {
        for (int i = 0; i < headwords.length - 1; i++) {
            if (headwords[i].isSelected()) {
                headwords[i].setSelected(false);
                headwords[i + 1].setSelected(true);
                return;
            }
        }
    }

    public void selectPrevious() {
        for (int i = headwords.length - 1; i > 0; i--) {
            if (headwords[i].isSelected()) {
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

}
