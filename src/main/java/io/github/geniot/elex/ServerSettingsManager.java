package io.github.geniot.elex;

import com.google.gson.Gson;
import io.github.geniot.elex.model.ServerSettings;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Getter
public class ServerSettingsManager {
    Logger logger = LoggerFactory.getLogger(ServerSettingsManager.class);

    @Autowired
    WebConfig webConfig;
    private String settingsFileName = "serverSettings.json";
    private Gson gson = new Gson();
    private ServerSettings serverSettings;

    @PostConstruct
    public void init() {
        File serverSettingsFile = new File(webConfig.getPathToDataAbsolute() + settingsFileName);
        if (serverSettingsFile.exists()) {
            try {
                String settingsStr = FileUtils.readFileToString(serverSettingsFile, StandardCharsets.UTF_8);
                serverSettings = gson.fromJson(settingsStr, ServerSettings.class);
                if (serverSettings == null) {
                    serverSettings = new ServerSettings();
                }
            } catch (Exception ex) {
                logger.warn("Couldn't read serverSettings file.", ex);
            }
        } else {
            serverSettings = new ServerSettings();
        }
        saveSettings();
    }

    public void saveSettings() {
        try {
            String settingsStr = gson.toJson(serverSettings);
            File serverSettingsFile = new File(webConfig.getPathToDataAbsolute() + settingsFileName);
            FileUtils.writeStringToFile(serverSettingsFile, settingsStr, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Couldn't save server settings to file.", e);
        }
    }

    public void put(String fileName, String name) {
        serverSettings.getDisabledDictionariesMap().put(fileName, name);
    }

    public void remove(String fileName) {
        serverSettings.getDisabledDictionariesMap().remove(fileName);
    }

    public boolean isDisabled(String shortName) {
        return serverSettings.getDisabledDictionariesMap().containsKey(shortName);
    }
}
