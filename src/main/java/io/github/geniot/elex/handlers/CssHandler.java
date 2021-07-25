package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CssHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            String s = ".test{color:red;}";
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.CSS));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }
}
