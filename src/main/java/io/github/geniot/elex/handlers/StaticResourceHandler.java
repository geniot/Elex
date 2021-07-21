package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {
        String path = t.getRequestURI().toString();
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

        String mode = System.getProperty("mode");
        if (StringUtils.isNotEmpty(mode) && mode.equals("develop")) {
            path = "src/main/resources/" + path;
            bbs = FileUtils.readFileToByteArray(new File(path));
        } else {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (is == null) {
                Logger.getInstance().log("Not found: " + path);
            }
            bbs = IOUtils.toByteArray(is);
        }

        String fileExtension = path.substring(path.lastIndexOf(".") + 1);
        if (textTypes.containsKey(fileExtension)) {
            String str = new String(bbs, StandardCharsets.UTF_8);
            if (path.endsWith("index.html")) {
                str = str.replaceAll("\\$\\{TIMESTAMP\\}", String.valueOf(System.currentTimeMillis()));
            }
            writeTxt(t, str, textTypes.get(fileExtension));
        } else if (binaryTypes.containsKey(fileExtension)) {
            writeBinary(t, bbs, binaryTypes.get(fileExtension));
        } else {
            Logger.getInstance().log("Couldn't find a content type for: " + path);
        }
    }
}
