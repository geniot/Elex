package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.ElexHttpServer;
import io.github.geniot.elex.ezip.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceHandler extends BaseHttpHandler {
    ElexHttpServer elexHttpServer;

    public StaticResourceHandler(ElexHttpServer s) {
        this.elexHttpServer = s;
    }


    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().toString();
            if (path.endsWith("/data")) {
                elexHttpServer.elexDataHandler.handle(httpExchange);
                return;
            }
            if (path.endsWith("/css")) {
                elexHttpServer.cssHandler.handle(httpExchange);
                return;
            }
            if (path.endsWith("/icon")) {
                elexHttpServer.iconHandler.handle(httpExchange);
                return;
            }
            if (path.endsWith("/wav")) {
                elexHttpServer.wavHandler.handle(httpExchange);
                return;
            }
            if (path.endsWith("/img")) {
                elexHttpServer.imgHandler.handle(httpExchange);
                return;
            }

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

            String fileExtension = path.substring(path.lastIndexOf(".") + 1).toUpperCase();

            String contentType = contentTypesMap.get(ContentType.valueOf(fileExtension));
            if (contentType.startsWith("image/")) {
                writeBinary(httpExchange, bbs, contentType);
            } else {
                String str = new String(bbs, StandardCharsets.UTF_8);
                writeTxt(httpExchange, str, contentType);
            }
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
            writeTxt(httpExchange, ExceptionUtils.getStackTrace(ex), contentTypesMap.get(ContentType.TEXT));
        }
    }
}
