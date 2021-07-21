package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.DictionariesPool;

import java.io.IOException;
import java.util.Map;

public class IconHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String, String> paramsMap = queryToMap(httpExchange.getRequestURI().getQuery());
        String id = paramsMap.get("id");
        IDictionary dictionary = DictionariesPool.getInstance().getDictionaryById(id);
        writeBinary(httpExchange, dictionary.getIcon(), binaryTypes.get(ContentType.PNG.label));
    }
}

