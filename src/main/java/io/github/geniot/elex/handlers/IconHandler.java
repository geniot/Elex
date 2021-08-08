package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.dao.DictionaryDAO;
import org.apache.commons.io.IOUtils;

import java.util.Map;

public class IconHandler extends BaseHttpHandler {

    DictionaryDAO dictionaryDAO = new DictionaryDAO();

    private static byte[] DEFAULT_ICON = getDefaultIcon();

    private static byte[] getDefaultIcon() {
        try {
            return IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/user.png"));
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
            byte[] iconBytes = DEFAULT_ICON;
            byte[] bbs = dictionaryDAO.getIcon(id);
            if (bbs != null) {
                iconBytes = bbs;
            }
            writeBinary(httpExchange, iconBytes, contentTypesMap.get(ContentType.PNG));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }
}