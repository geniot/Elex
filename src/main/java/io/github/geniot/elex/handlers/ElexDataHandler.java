package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.Logger;
import io.github.geniot.elex.handlers.updaters.*;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ElexDataHandler extends BaseHttpHandler {
    private Gson gson = new Gson();

    DictionariesUpdater dictionariesUpdater = new DictionariesUpdater();
    LanguagesUpdater languagesUpdater = new LanguagesUpdater();
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
            List<Dictionary> dictionaryList = DictionariesPool.getInstance().getDictionaries(model);

            languagesUpdater.updateLanguages(model, dictionaryList);
            dictionariesUpdater.updateDictionaries(model, dictionaryList);

            headwordsUpdater.updateHeadwords(model);
            entriesUpdater.updateEntries(model);
            fullTextHitsUpdater.updateFullTextHits(model);

            //default action
            model.setAction(Action.INDEX);

            String s = gson.toJson(model);
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));
            long t2 = System.currentTimeMillis();
            Logger.getInstance().log((t2 - t1) + " ms " + model.getSelectedHeadword());
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }


}
