package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.Logger;
import org.apache.commons.io.IOUtils;

import java.util.Map;

public class WavHandler extends BaseHttpHandler {

    private static byte[] EXAMPLE_OGG = getDefaultIcon();

    private static byte[] getDefaultIcon() {
        try {
            return IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("Example.ogg"));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
            return new byte[]{};
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            int id = Integer.parseInt(map.get("id"));
            String link = map.get("link");
            byte[] oggBytes = EXAMPLE_OGG;
            byte[] bbs = DictionariesPool.getInstance().getOgg(id, link);
            if (bbs != null) {
                oggBytes = bbs;
            }
            writeBinary(httpExchange, oggBytes, contentTypesMap.get(ContentType.OGG));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }
}
