package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().toString();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.contains("?")) {
                path = path.substring(0, path.indexOf('?'));
            }
            if (path.equals("")) {
                path = "index.html";
            }
            path = "web/" + path;

            byte[] bbs;

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (is == null) {
                throw new Exception("Resource not found: " + path);
            }
            bbs = IOUtils.toByteArray(is);

            String fileExtension = path.substring(path.lastIndexOf(".") + 1);
            if (textTypes.containsKey(fileExtension)) {
                String str = new String(bbs, StandardCharsets.UTF_8);
                writeTxt(httpExchange, str, textTypes.get(fileExtension));
            } else if (binaryTypes.containsKey(fileExtension)) {
                writeBinary(httpExchange, bbs, binaryTypes.get(fileExtension));
            } else {
                throw new Exception("Couldn't find a content type for: " + path);
            }
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
            writeTxt(httpExchange, ExceptionUtils.getStackTrace(ex), ContentType.TEXT.label);
        }
    }
}
