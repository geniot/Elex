package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.ezip.Logger;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CssHandler extends BaseHttpHandler {
    public static String DSL_STYLE = getDslStyle();

    public static String getDslStyle() {
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("dsl.css"), writer, StandardCharsets.UTF_8);
            return writer.toString();
        } catch (IOException var1) {
            throw new RuntimeException(var1);
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            String s = "";
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.CSS));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }
}
