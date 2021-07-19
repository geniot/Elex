package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.CompressedDictionary;
import io.github.geniot.elex.model.DictionariesPool;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ResourceHandler extends BaseHttpHandler {

    public ResourceHandler(Logger f) {
        super(f);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            Map<String, String> paramsMap = queryToMap(httpExchange.getRequestURI().getQuery());
            String action = paramsMap.get("a");
            if (action == null) {
                throw new Exception(EXCEPTION_EMPTY);
            }

            Set<CompressedDictionary> set = getDics(paramsMap);

            if (action.equals("icon")) {
                Integer id = Integer.parseInt(paramsMap.get("id"));
                CompressedDictionary ed = DictionariesPool.getInstance().getDictionaryById(id);
                writeBinary(httpExchange, ed.getIcon(), "image/png");
            } else if (action.equals("css")) {
                String css = DictionariesPool.getInstance().getDictionariesResourcesByMask(set, ".*\\.css$");
                writeTxt(httpExchange, css, "text/css");
            } else if (action.equals("js")) {
                String js = DictionariesPool.getInstance().getDictionariesResourcesByMask(set, ".*\\.js$");
                writeTxt(httpExchange, js, "application/javascript");
            } else if (action.equals("aux")) {
                String resId = paramsMap.get("resId");
                Integer id = Integer.parseInt(paramsMap.get("id"));
                CompressedDictionary ed = DictionariesPool.getInstance().getDictionaryById(id);
                writeBinary(httpExchange, ed.getResource(resId), "audio/mpeg");
            } else {
                throw new Exception(EXCEPTION_NOT_EXISTS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
