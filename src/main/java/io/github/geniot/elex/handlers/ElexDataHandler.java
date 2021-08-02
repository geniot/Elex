package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.handlers.updaters.*;
import io.github.geniot.elex.model.Model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ElexDataHandler extends BaseHttpHandler {
    private Gson gson = new Gson();

    LanguagesUpdater languagesUpdater = new LanguagesUpdater();
    DictionariesUpdater dictionariesUpdater = new DictionariesUpdater();
    HeadwordsUpdater headwordsUpdater = new HeadwordsUpdater();
    EntriesUpdater entriesUpdater = new EntriesUpdater();
    FullTextHitsUpdater fullTextHitsUpdater = new FullTextHitsUpdater();


    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            long t1 = System.currentTimeMillis();
            InputStream input = httpExchange.getRequestBody();
            StringBuffer stringBuilder = new StringBuffer();

            new BufferedReader(new InputStreamReader(input)).lines().forEach((String s) -> stringBuilder.append(s + "\n"));

            Model model = gson.fromJson(stringBuilder.toString(), Model.class);
            languagesUpdater.updateLanguages(model);
            dictionariesUpdater.updateDictionaries(model);
            headwordsUpdater.updateHeadwords(model);
            entriesUpdater.updateEntries(model);
            fullTextHitsUpdater.updateFullTextHits(model);

            String s = gson.toJson(model);
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));
            long t2 = System.currentTimeMillis();
            Logger.getInstance().log((t2 - t1) + " ms " + model.getSearchResultsFor());
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }


}
