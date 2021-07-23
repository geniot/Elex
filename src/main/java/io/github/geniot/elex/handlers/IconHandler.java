package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import org.apache.commons.io.IOUtils;

import java.util.Map;

public class IconHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            byte[] iconBytes = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/user.png"));
            writeBinary(httpExchange, iconBytes, contentTypesMap.get(ContentType.PNG));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }
}