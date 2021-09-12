package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminModel extends Model {
    private AdminDictionary[] adminDictionaries = new AdminDictionary[]{};

    public AdminDictionary getSelectedDictionary() {
        for (Dictionary d : adminDictionaries) {
            if (d.isSelected()) {
                return (AdminDictionary) d;
            }
        }
        return null;
    }

    public boolean isDictionarySelected(String name) {
        for (Dictionary dictionary : adminDictionaries) {
            if (dictionary.getName().equals(name)) {
                if (dictionary.isSelected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
