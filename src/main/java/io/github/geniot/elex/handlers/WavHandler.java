package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.Logger;

import java.util.Map;

public class WavHandler extends BaseHttpHandler {


    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            long t1 = System.currentTimeMillis();
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            int id = Integer.parseInt(map.get("id"));
            String link = map.get("link");
            byte[] resourceBytes = DictionariesPool.getInstance().getResource(id, link);
            writeBinary(httpExchange, resourceBytes, contentTypesMap.get(ContentType.WAV));
            long t2 = System.currentTimeMillis();
            Logger.getInstance().log((t2 - t1) + " ms " + link);
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }
}
