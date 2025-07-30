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
import java.util.List;
import java.util.Objects;

public class ConfigSystem
{
    private static final String CONFIG_DIR = "C:\\Horizen\\";

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
            System.out.println(e);
            NotificationManager.getInstance().addNotification(
                    "Config System",
                    "Failed to load config, " + e,
                    3000
            );
        }
    }

    public static void saveConfigToFile(String configName) {
        try {
            // Create directory if it doesn't exist
            File directory = new File(CONFIG_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save config to file
            String configContent = exportConfig();
            File configFile = new File(CONFIG_DIR + configName + ".json");
            FileWriter writer = new FileWriter(configFile);
            writer.write(configContent);
            writer.close();

            NotificationManager.getInstance().addNotification(
                    "Config System",
                    "Config saved to " + configFile.getAbsolutePath(),
                    3000
            );
        } catch (IOException e) {
            System.out.println("Failed to save config: " + e.getMessage());
            NotificationManager.getInstance().addNotification(
                    "Config System",
                    "Failed to save config!",
                    3000
            );
        }
    }

    public static void loadConfigFromFile(String configName) {
        try {
            File configFile = new File(CONFIG_DIR + configName + ".json");
            if (!configFile.exists()) {
                NotificationManager.getInstance().addNotification(
                        "Config System",
                        "Config file not found!",
                        3000
                );
                return;
            }

            String content = new String(Files.readAllBytes(configFile.toPath()));
            loadConfig(content);
        } catch (IOException e) {
            System.out.println("Failed to load config: " + e.getMessage());
            NotificationManager.getInstance().addNotification(
                    "Config System",
                    "Failed to load config!",
                    3000
            );
        }
    }

    public static List<String> getConfigList() {
        try {
            File directory = new File(CONFIG_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
                return List.of();
            }

            return Files.list(Paths.get(CONFIG_DIR))
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> path.getFileName().toString().replace(".json", ""))
                    .toList();
        } catch (IOException e) {
            System.out.println("Failed to get config list: " + e.getMessage());
            return List.of();
        }
    }
}
