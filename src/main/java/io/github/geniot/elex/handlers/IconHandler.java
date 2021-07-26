package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.Logger;
import org.apache.commons.io.IOUtils;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class IconHandler extends BaseHttpHandler {
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
            Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
            byte[] iconBytes = DEFAULT_ICON;
            for (IDictionary dictionary : dictionarySet) {
                Properties properties = dictionary.getProperties();
                String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
                int dicId = name.hashCode() & 0xfffffff;
                if (id == dicId) {
                    iconBytes = dictionary.getIcon();
                }
            }
            writeBinary(httpExchange, iconBytes, contentTypesMap.get(ContentType.PNG));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }
}