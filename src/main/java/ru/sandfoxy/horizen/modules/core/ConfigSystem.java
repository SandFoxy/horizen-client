package ru.sandfoxy.horizen.modules.core;

import com.google.gson.*;
import ru.sandfoxy.horizen.imgui.notifications.NotificationManager;
import ru.sandfoxy.horizen.modules.ModuleManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigSystem
{
    private static final Path CONFIG_DIR = Paths.get("C:", "Games");

    public static String exportConfig(){
        StringBuilder config = new StringBuilder("[");

        List<Module> modules = ModuleManager.getModules();

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);

            config.append(module.toJson());

            if (i < modules.size() - 1) {
                config.append(",");
            }
        }

        config.append("]");
        return config.toString();
    }

    public static void loadConfig(String content){
        try {

            JsonArray jsonArray = JsonParser.parseString(content).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();
                String name = obj.get("name").getAsString();

                Module module = ModuleManager.getByName(name);
                if (module != null) {
                    module.fromJson(obj.toString());
                } else {
                    System.out.println("Unknown module: " + name);
                }
            }

            System.out.println("Config loaded from file.");
            NotificationManager.getInstance().addNotification(
                    "Config System",
                    "Config loaded!",
                    3000
            );
        } catch (JsonSyntaxException e) {
            NotificationManager.getInstance().addNotification(
                    "Config System",
                    "Failed to load config, " + content,
                    3000
            );
        }
    }

    public static void saveConfigToFile(String configName) {
        try {
            Path configPath = CONFIG_DIR.resolve(configName + ".json");

            String configContent = exportConfig();
            Files.write(configPath, configContent.getBytes());

            NotificationManager.getInstance().addNotification(
                    "Config System",
                    "Config saved to " + configPath.toAbsolutePath(),
                    3000
            );
        } catch (IOException e) {
            System.out.println("Failed to save config: " + e.getMessage());
            NotificationManager.getInstance().addNotification(
                    "Config System",
                    "Failed to save config: " + e.getMessage(),
                    3000
            );
        }
    }

    public static void loadConfigFromFile(String configName) {
        try {
            Path configPath = CONFIG_DIR.resolve(configName + ".json");

            if (!Files.exists(configPath)) {
                NotificationManager.getInstance().addNotification(
                        "Config System",
                        "Config file not found: " + configPath.getFileName(),
                        3000
                );
                return;
            }

            String content = new String(Files.readAllBytes(configPath));
            loadConfig(content);
        } catch (IOException e) {
            System.out.println("Failed to load config: " + e.getMessage());
            NotificationManager.getInstance().addNotification(
                    "Config System",
                    "Failed to load config: " + e.getMessage(),
                    3000
            );
        }
    }

    public static List<String> getConfigList() {
        try {
            return Files.list(CONFIG_DIR)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.substring(0, fileName.lastIndexOf('.'));
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Failed to get config list: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
