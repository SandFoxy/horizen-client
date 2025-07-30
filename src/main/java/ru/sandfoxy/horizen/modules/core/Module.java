package ru.sandfoxy.horizen.modules.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import ru.sandfoxy.horizen.events.PacketEvent;
import ru.sandfoxy.horizen.imgui.notifications.NotificationManager;
import ru.sandfoxy.horizen.modules.core.type.Separator;
import ru.sandfoxy.horizen.utils.SoundManager;
import ru.sandfoxy.horizen.utils.others.GLFWKeyMapper;

import java.util.ArrayList;
import java.util.List;

import static ru.sandfoxy.horizen.utils.others.GLFWKeyMapper.GLFW_KEY_CODES;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public abstract class Module {
    private final String name;
    private int keybindButton = -1;
    public KeybindMode keybindMode = KeybindMode.TOGGLE;
    private boolean keybindActive = false;
    private boolean dontLaggKeybind = false;
    private boolean enabled;
    private String searchTag;
    private CATEGORY category;
    private boolean disableBinding = false;
    private String description;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, CATEGORY category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.searchTag = name;
    }

    public Module(String name, CATEGORY category, String description, String searchTags) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.searchTag = name + "\0" + searchTags;
    }
    public Module(String name, CATEGORY category, String description, String searchTags, boolean disableBiding) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.searchTag = name + "\0" + searchTags;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void toggle(boolean value) {
        enabled = value;
        if (enabled) onEnable();
        else onDisable();
    }

    public boolean isEnabledRaw() {
        return this.enabled;
    }

    public boolean isEnabled() {
        return (this.enabled && (!this.isBinded() || this.isKeybindActive()));
    }

    public boolean bindInProgress(){
        return this.keybindButton == -5;
    }

    public boolean isBinded(){
        return this.keybindButton > 0;
    }

    public String getKeybind(){
        return GLFWKeyMapper.getKeyName(this.keybindButton);
    }
    public boolean isKeybindActive(){
        return this.keybindActive;
    }
    public void bindModule(){
        this.keybindButton = -5;
    }
    public String getName() {
        return name;
    }

    public CATEGORY getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    //Ticks
    public void startTick() {}
    public void endTick() {}

    //Packets
    public void packetReceived(PacketEvent.Receive packet) {}
    public void packetSend(PacketEvent.Send packet) {}

    //Render
    public void onRender(DrawContext matrices) {}
    public void onDraw() {}
    public void onGui() {}

    public void onEnable() {}
    public void onDisable() {}

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public void addSeparator(){
        settings.add(new Separator());
    }

    protected <T extends Setting<?>> T addSetting(T setting) {
        settings.add(setting);
        return setting;
    }

    public String getSearchTag() {
        return searchTag;
    }

    public boolean isBindingDisabled() {
        return disableBinding;
    }

    public enum CATEGORY {
        COMBAT("\uf6de COMBAT"),
        RENDER("\uf03e RENDER"),
        PLAYER("\uf007 PLAYER"),
        MISC("\uf085 MISC"),
        SETTINGS("\uf013 SETTINGS");

        private final String description;

        CATEGORY(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public void awaitKeybind() {
        long window = MinecraftClient.getInstance().getWindow().getHandle();

        for (int key : GLFW_KEY_CODES) {
            if (key == GLFW_KEY_ESCAPE && this.keybindButton != -5) {
                this.keybindButton = -1;
                return;
            }

            if (GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS) {
                this.keybindButton = key;
                return;
            }
        }
    }

    public void updateKeybind() {
        if (!this.isEnabledRaw()) return;

        long window = MinecraftClient.getInstance().getWindow().getHandle();

        if (this.keybindButton == GLFW_KEY_ESCAPE) {
            this.keybindButton = -1;
            this.keybindActive = false;
            return;
        }

        if (this.keybindButton == -5) {
            awaitKeybind();
            return;
        }

        boolean isKeyDown = (this.keybindButton != -1) &&
                GLFW.glfwGetKey(window, this.keybindButton) == GLFW.GLFW_PRESS;

        if (this.dontLaggKeybind && !isKeyDown) this.dontLaggKeybind = false;

        if (this.keybindMode == KeybindMode.TOGGLE && isKeyDown && !dontLaggKeybind) {
            this.keybindActive = !this.keybindActive;

            if (this.keybindActive) SoundManager.playEnableSound();
            else SoundManager.playDisableSound();

            NotificationManager.getInstance().addNotification(this.getName(), "Module " + (this.keybindActive ? "enabled" : "disabled"));

            this.dontLaggKeybind = true;
        }

        if (this.keybindMode == KeybindMode.HOLD) {
            this.keybindActive = isKeyDown;
        }
    }

    public String toJson() {
        Gson gson = new Gson();
        JsonObject obj = new JsonObject();

        obj.addProperty("name", name);
        obj.addProperty("enabled", enabled);
        obj.addProperty("keybindButton", keybindButton);
        obj.addProperty("keybindMode", keybindMode.toString());

        JsonObject settingsObj = new JsonObject();
        for (Setting<?> setting : settings) {
            if (setting.shouldBeSaved())
                settingsObj.add(setting.getName(), gson.toJsonTree(setting.get()));
        }

        obj.add("settings", settingsObj);
        return gson.toJson(obj);
    }

    public void fromJson(String json) {
        Gson gson = new Gson();
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        boolean prevStatus = this.enabled;

        this.enabled = obj.has("enabled") && obj.get("enabled").getAsBoolean();
        this.keybindButton = obj.has("keybindButton") ? obj.get("keybindButton").getAsInt() : -1;
        this.keybindMode = obj.has("keybindMode") ? KeybindMode.valueOf(obj.get("keybindMode").getAsString()) : KeybindMode.TOGGLE;

        if (obj.has("settings")) {
            JsonObject settingsObj = obj.getAsJsonObject("settings");

            for (Setting<?> setting : this.settings) {
                if (!setting.shouldBeSaved()) continue;

                JsonElement valueElement = settingsObj.get(setting.getName());

                if (valueElement != null) {
                    Object value = gson.fromJson(valueElement, setting.get().getClass());
                    setting.set(value);
                }
            }
        }

        if (enabled && !prevStatus) onEnable();
        if (!enabled && prevStatus) onDisable();
    }


    public enum KeybindMode {
        TOGGLE,
        HOLD
    }
}
