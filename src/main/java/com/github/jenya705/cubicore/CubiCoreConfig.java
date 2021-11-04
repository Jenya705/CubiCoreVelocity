package com.github.jenya705.cubicore;

import lombok.Cleanup;
import lombok.Data;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

/**
 * @author Jenya705
 */
@Data
public class CubiCoreConfig {

    private static final String paragraph = "§";

    private String colorSuccess = "Your color";
    private String colorNotChosen = "Please choose color";
    private String colorNotValid = "Color is not valid :( Maybe you wanted to write color in hex format?";

    public CubiCoreConfig(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
            @Cleanup Writer writer = new FileWriter(file);
            CubiCoreVelocity.yaml.dump(Map.of(
                    "colorSuccess", colorSuccess,
                    "colorNotChosen", colorNotChosen,
                    "colorNotValid", colorNotValid
            ), writer);
            return;
        }
        @Cleanup Reader reader = new FileReader(file);
        Map<String, Object> yamlConfig = CubiCoreVelocity.yaml.load(reader);
        colorSuccess = get(yamlConfig, "colorSuccess");
        colorNotChosen = get(yamlConfig, "colorNotChosen");
        colorNotValid = get(yamlConfig, "colorNotValid");
    }

    private String get(Map<String, Object> yamlConfig, String key) {
        return yamlConfig
                .get(key)
                .toString()
                .replaceAll("&", paragraph);
    }
}
