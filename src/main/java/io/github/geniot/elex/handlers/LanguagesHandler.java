package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Language;

import java.util.ArrayList;
import java.util.List;

public class LanguagesHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Gson gson = new Gson();

            List<Language> languages = new ArrayList<>();

            languages.add(genLanguage("en", 1));
            languages.add(genLanguage("de", 2));
            languages.add(genLanguage("fr", 3));

            String s = gson.toJson(languages.toArray(new Language[languages.size()]));
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    private Language genLanguage(String lang, int num) {
        Language en = new Language();
        en.setSourceCode(lang.toUpperCase());
        en.setTargetCodes(new String[]{"EN" + num, "DE" + num, "FR" + num});
        return en;
    }
}
