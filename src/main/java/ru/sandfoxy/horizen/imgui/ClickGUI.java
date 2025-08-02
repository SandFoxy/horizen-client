package ru.sandfoxy.horizen.imgui;

import imgui.*;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGuiDockNode;
import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import ru.sandfoxy.horizen.ModEntryPoint;
import ru.sandfoxy.horizen.imgui.notifications.NotificationManager;
import ru.sandfoxy.horizen.imgui.screen.WindowScaling;
import ru.sandfoxy.horizen.imgui.utils.FontManager;
import ru.sandfoxy.horizen.imgui.utils.TextureManager;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.core.ConfigSystem;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.Setting;
import ru.sandfoxy.horizen.modules.features.hack.FriendList;
import ru.sandfoxy.horizen.modules.features.hack.GPS;
import ru.sandfoxy.horizen.utils.SoundManager;
import ru.sandfoxy.horizen.utils.others.GLFWKeyMapper;
import ru.sandfoxy.horizen.utils.animations.ContinualAnimation;
import static ru.sandfoxy.horizen.ModEntryPoint.LOGGER;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static ru.sandfoxy.horizen.ModEntryPoint.menuOpened;
import static ru.sandfoxy.horizen.imgui.utils.FontManager.*;
import static ru.sandfoxy.horizen.imgui.utils.StyleVars.SetupStyles;
import static ru.sandfoxy.horizen.imgui.utils.StyleVars.ShowStyleSelector;
import static ru.sandfoxy.horizen.modules.features.misc.Unhook.cheatUnloaded;

public class ClickGUI {
    public static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    public static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static Module.CATEGORY currentCategory = Module.CATEGORY.COMBAT;

    private static boolean isMenuKeyDown = false;
    public static long    windowHandle;
    private static ImString searchInput = new ImString();


    static List<String> configList;
    static Integer selectedConfig = 0;
    private static final ImString configName = new ImString();
    static Integer selectedFriend = 0;
    private static final ImString friendName = new ImString();
    
    static Integer selectedGPSPoint = 0;
    private static final ImString gpsPointName = new ImString();
    private static final ImString gpsPointX = new ImString();
    private static final ImString gpsPointY = new ImString();
    private static final ImString gpsPointZ = new ImString();

    private static final Map<Module, ContinualAnimation> moduleHeightAnimations = new HashMap<>();
    private static final float ANIMATION_DURATION = 25; // ms
    private static final ContinualAnimation menuAlphaAnim = new ContinualAnimation();
    private static final float MENU_ANIMATION_DURATION = 15; // ms

    public static void onGlfwInit(long handle) {
        initializeImGui(handle);
        imGuiGlfw.init(handle,true);
        imGuiGl3.init();
        windowHandle = handle;
    }

    public static void onFrameRender() {
        if (cheatUnloaded) {
            updateScale();
            return;
        }
        //Updating Keybinds
        for (Module module : ModuleManager.getModules()){module.updateKeybind();}

        //Close menu by pressing ESC
        if (ImGui.isKeyDown(GLFWKeyMapper.GLFW_KEY_ESCAPE) && ModEntryPoint.menuOpened){
            mc.mouse.lockCursor();
            ModEntryPoint.menuOpened = false;
            searchInput.set("");
        }

        //Menu visibility toggle
        if (ImGui.isKeyDown(GLFWKeyMapper.GLFW_KEY_RIGHT_SHIFT) && !isMenuKeyDown) {
            ModEntryPoint.menuOpened = !ModEntryPoint.menuOpened;
            isMenuKeyDown = true;
            if (!ModEntryPoint.menuOpened){
                mc.mouse.lockCursor();
                searchInput.set("");
            }
        }
        else if (isMenuKeyDown && !ImGui.isKeyDown(GLFWKeyMapper.GLFW_KEY_RIGHT_SHIFT)) isMenuKeyDown = false;

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        ImGui.pushFont(FontManager.StemBold12);
        ImGui.getIO().setKeysDown(ImGuiKey.Tab, false);
        setupDocking("Menu Overlay", "menu-dockspace");
        updateScale();

        ImGui.getIO().setKeysDown(ImGuiKey.Tab, false);
        if (menuOpened || MinecraftClient.getInstance().inGameHud.getChatHud().isChatFocused()){
            for (Module module : ModuleManager.getModules()){
                try {
                    if (module.isEnabled()) module.onDraw();
                } catch (Exception e) {
                    NotificationManager.getInstance().addNotification("Exception in module " + module.getName() + " (Render)", e.toString(), 15000);
                    LOGGER.error("Exception in module " + module.getName() + " (Render): ", e);
                    SoundManager.playErrorSound();
                    module.toggle(false);
                }
            }

            ImGuiUils.drawKeybinds();
            for (Module module : ModuleManager.getModules()){
                try {
                    if (module.isEnabled()) module.onGui();
                } catch (Exception e) {
                    NotificationManager.getInstance().addNotification("Exception in module " + module.getName() + " (Render)", e.toString(), 15000);
                    LOGGER.error("Exception in module " + module.getName() + " (onGui): ", e);
                    SoundManager.playErrorSound();
                    module.toggle(false);
                }
            }
        }

        //Menu
        if (ModEntryPoint.menuOpened || menuAlphaAnim.getOutput() > 0.01f) {
            if (ModEntryPoint.menuOpened) {
                mc.mouse.unlockCursor();
            };
            // Animate menu alpha
            menuAlphaAnim.animate(ModEntryPoint.menuOpened ? 1f : 0f, (int)MENU_ANIMATION_DURATION);
            float alpha = menuAlphaAnim.getOutput();
            ImVec4 frameBg = ImGui.getStyle().getColor(ImGuiCol.FrameBg);

            if (alpha > 0.01f) {
                ImGuiUils.centerMenu(800, 555);
                ImGui.setNextWindowSize(800, 555);
                ImGui.pushStyleVar(ImGuiStyleVar.Alpha, alpha);
                ImGui.begin("Horizen", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoNavInputs |ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoSavedSettings);

                ImGui.beginChild("##Caterory", 220, 494, true);
                for (Module.CATEGORY category : Module.CATEGORY.values()){
                    if (category == Module.CATEGORY.GPS && !ModuleManager.getByName("GPS").isEnabledRaw()) continue;

                    Module.CATEGORY nextCategory = currentCategory;

                    ImVec4 activeColor = ImGui.getStyle().getColor(ImGuiCol.ButtonActive);
                    if (category == currentCategory) ImGui.pushStyleColor(ImGuiCol.Button, ImGui.getColorU32(activeColor.x, activeColor.y, activeColor.z, activeColor.w));

                    if (ImGui.button(category.name(), 200, 40)){
                        nextCategory = category;
                    }


                    if (category == currentCategory) ImGui.popStyleColor();

                    currentCategory = nextCategory;

                    ImGui.newLine();
                    ImGui.spacing();
                }
                ImGui.endChild();

                //Search Bar
                ImGui.setNextItemWidth(220);
                ImGui.setCursorPosY(527);


                ImGui.inputTextWithHint("##SearchBar", "\uf002 Search...", searchInput);
                ImGui.spacing();
                ImGui.setNextItemWidth(200);

                //Modules Child
                ImGui.setCursorPosY(25);
                ImGui.setCursorPosX(233);
                ImGui.beginChild("##Modules", 558, 520, currentCategory != Module.CATEGORY.SETTINGS && currentCategory != Module.CATEGORY.GPS, ImGuiWindowFlags.NoScrollbar);
                {
                    float leftY = ImGui.getCursorPosY();
                    float rightY = leftY;
                    int visibleIndex = 0;

                    for (int i = 0; i < ModuleManager.getModules().size(); i++) {
                        if ((currentCategory == Module.CATEGORY.SETTINGS || currentCategory == Module.CATEGORY.GPS) && searchInput.isEmpty()){
                            i = ModuleManager.getModules().size();
                            continue;
                        }

                        Module module = ModuleManager.getModules().get(i);
                        if (
                                (module.getCategory() != currentCategory && searchInput.isEmpty()) ||
                                        !module.getSearchTag().toLowerCase().contains(searchInput.get().toLowerCase())
                        ) continue;

                        float moduleHeight = 60f;
                        if (module.isEnabledRaw()) {
                            for (Setting setting : module.getSettings()) {
                                moduleHeight += setting.getHeight() > 0 ? setting.getHeight() + 10f : 0f;
                            }
                        }

                        // Get or create animation for this module
                        ContinualAnimation heightAnim = moduleHeightAnimations.computeIfAbsent(module, k -> new ContinualAnimation());
                        heightAnim.animate(moduleHeight, (int)ANIMATION_DURATION);

                        float animatedHeight = Math.max(60f, heightAnim.getOutput());
                        if (Float.isNaN(animatedHeight)) animatedHeight = moduleHeight;

                        //Position Setup
                        if (visibleIndex % 2 == 0) { ImGui.setCursorPosY(leftY); ImGui.setCursorPosX(10); leftY += animatedHeight + 5f;
                        } else { ImGui.setCursorPosY(rightY); ImGui.setCursorPosX(285); rightY += animatedHeight + 5f;}

                        //Module Box Start

                        ImGui.pushStyleColor(ImGuiCol.ChildBg, ImGui.getColorU32(frameBg.x,frameBg.y,frameBg.z, frameBg.w));
                        ImGui.beginChild("##" + module.getName(), 265f, animatedHeight, true, ImGuiWindowFlags.NoScrollbar);

                        ImGui.pushFont(StemBold16);
                        String bindText = "";
                        if (module.isBinded() || module.bindInProgress()){
                            bindText = " [" + module.getKeybind() + " " + (!module.bindInProgress() ? module.keybindMode : "") + "]";
                        }
                        ImGui.text(module.getName() + bindText);
                        ImGui.popFont();

                        if (ImGui.isItemHovered()) {
                            ImGui.setTooltip(module.getDescription());
                        }

                        if (ImGui.isItemClicked(ImGuiMouseButton.Middle) && !module.isBindingDisabled()) {
                            module.bindModule();
                        }

                        if (ImGui.isItemClicked(ImGuiMouseButton.Right)) {
                            ImGui.openPopup("KeybindModePopup_" + module.getName());
                        }

                        if (ImGui.beginPopup("KeybindModePopup_" + module.getName())) {
                            for (Module.KeybindMode mode : Module.KeybindMode.values()) {
                                boolean selected = module.keybindMode == mode;
                                if (ImGui.selectable(mode.name(), selected)) {
                                    module.keybindMode = mode;
                                }
                            }
                            ImGui.endPopup();
                        }


                        ImGui.spacing();
                        if (ImGui.checkbox("Enabled", module.isEnabledRaw())) {
                            module.toggle();
                            if (module.isEnabledRaw()) SoundManager.playEnableSound();
                            else SoundManager.playDisableSound();
                        }

                        if (module.isEnabledRaw() || heightAnim.getOutput() > 65f) {
                            for (Setting setting : module.getSettings()) {
                                if ((boolean) setting.isVisible.get()){
                                    ImGui.spacing();
                                    setting.render();
                                }
                            }
                        }

                        ImGui.endChild();
                        ImGui.popStyleColor();
                        //Module Box End

                        visibleIndex++;
                    }

                    //GPS Page
                    if (currentCategory == Module.CATEGORY.GPS && searchInput.isEmpty()){
                        ImGui.beginChild("##GPSManager", 275f, 520, true, ImGuiWindowFlags.NoScrollbar);
                        {
                            ImGui.pushFont(StemBold16);
                            ImGui.text("GPS Manager");
                            ImGui.popFont();

                            ImGui.spacing();

                            ImGui.pushStyleColor(ImGuiCol.ChildBg, ImGui.getColorU32(frameBg.x,frameBg.y,frameBg.z, frameBg.w));
                            ImGui.beginChild("##GPSPoints", 259f, 300, true, ImGuiWindowFlags.NoScrollbar);
                            {
                                ImGui.pushFont(StemBold16);

                                List<GPS.GPSPoint> gpsPoints = GPS.getAllGPSPoints();
                                for (int i = 0; i < gpsPoints.size(); i++) {
                                    GPS.GPSPoint point = gpsPoints.get(i);
                                    String displayText = point.name.isEmpty() ? 
                                        String.format("Point %d (%d, %d, %d)", i + 1, point.x, point.y, point.z) :
                                        String.format("%s (%d, %d, %d)", point.name, point.x, point.y, point.z);
                                    
                                    boolean selected = selectedGPSPoint == i;
                                    if (ImGui.selectable(displayText, selected, ImGuiSelectableFlags.DontClosePopups)) {
                                        selectedGPSPoint = i;
                                    }
                                }
                                ImGui.popFont();

                                ImGui.spacing();
                            }
                            ImGui.endChild();
                            ImGui.popStyleColor();

                            if (ImGui.button("Add Current Position", 259f, 30)){
                                if (mc.player != null) {
                                    GPS.addGPSPoint((int)mc.player.getX(), (int)mc.player.getY(), (int)mc.player.getZ(), "Player Position");
                                }
                            }
                            if (ImGui.button("Edit Selected", 259f, 30)){
                                if (selectedGPSPoint >= 0 && selectedGPSPoint < GPS.getAllGPSPoints().size()) {
                                    GPS.GPSPoint point = GPS.getAllGPSPoints().get(selectedGPSPoint);
                                    gpsPointName.set(point.name);
                                    gpsPointX.set(String.valueOf(point.x));
                                    gpsPointY.set(String.valueOf(point.y));
                                    gpsPointZ.set(String.valueOf(point.z));
                                }
                            }
                            if (ImGui.button("Delete Selected", 259f, 30)){
                                if (selectedGPSPoint >= 0 && selectedGPSPoint < GPS.getAllGPSPoints().size()) {
                                    GPS.removeGPSPointByIndex(selectedGPSPoint);
                                    selectedGPSPoint = 0;
                                }
                            }
                            if (ImGui.button("Clear All Points", 259f, 30)){
                                GPS.clearAllGPSPoints();
                                selectedGPSPoint = 0;
                            }
                        }
                        ImGui.endChild();

                        ImGui.sameLine();

                        ImGui.beginChild("##GPSAddPoint", 275f, 520, true, ImGuiWindowFlags.NoScrollbar);
                        {
                            ImGui.pushFont(StemBold16);
                            ImGui.text("Add GPS Point");
                            ImGui.popFont();

                            ImGui.spacing();

                            ImGui.text("Point Name:");
                            ImGui.setNextItemWidth(259f);
                            ImGui.inputTextWithHint("##PointName", "Enter point name", gpsPointName);

                            ImGui.text("X Coordinate:");
                            ImGui.setNextItemWidth(259f);
                            ImGui.inputTextWithHint("##PointX", "Enter X coordinate", gpsPointX);

                            ImGui.text("Y Coordinate:");
                            ImGui.setNextItemWidth(259f);
                            ImGui.inputTextWithHint("##PointY", "Enter Y coordinate", gpsPointY);

                            ImGui.text("Z Coordinate:");
                            ImGui.setNextItemWidth(259f);
                            ImGui.inputTextWithHint("##PointZ", "Enter Z coordinate", gpsPointZ);

                            ImGui.spacing();

                            String buttonText = (selectedGPSPoint >= 0 && selectedGPSPoint < GPS.getAllGPSPoints().size()) ? "Update Point" : "Add Point";
                            if (ImGui.button(buttonText, 259f, 30)){
                                try {
                                    int x = Integer.parseInt(gpsPointX.get());
                                    int y = Integer.parseInt(gpsPointY.get());
                                    int z = Integer.parseInt(gpsPointZ.get());
                                    
                                    if (selectedGPSPoint >= 0 && selectedGPSPoint < GPS.getAllGPSPoints().size()) {
                                        // Редактируем существующую точку
                                        GPS.removeGPSPointByIndex(selectedGPSPoint);
                                        GPS.addGPSPoint(x, y, z, gpsPointName.get());
                                        selectedGPSPoint = 0;
                                    } else {
                                        // Добавляем новую точку
                                        GPS.addGPSPoint(x, y, z, gpsPointName.get());
                                    }
                                    
                                    gpsPointName.set("");
                                    gpsPointX.set("");
                                    gpsPointY.set("");
                                    gpsPointZ.set("");
                                } catch (NumberFormatException e) {
                                    // Ошибка парсинга координат
                                }
                            }
                        }
                        ImGui.endChild();
                    }

                    //Settings Page
                    if (currentCategory == Module.CATEGORY.SETTINGS && searchInput.isEmpty()){
                        ImGui.beginChild("##Settings", 275f, 520, true, ImGuiWindowFlags.NoScrollbar);
                        {

                            ImGui.pushFont(StemBold16);
                            ImGui.text("Config Manager");
                            ImGui.popFont();

                            ImGui.spacing();

                            ImGui.pushStyleColor(ImGuiCol.ChildBg, ImGui.getColorU32(frameBg.x,frameBg.y,frameBg.z, frameBg.w));
                            ImGui.beginChild("##Configs", 259f, 300, true, ImGuiWindowFlags.NoScrollbar);
                            {
                                ImGui.pushFont(StemBold16);

                                List<String> configs = ConfigSystem.getConfigList();
                                for (int i = 0; i < configs.size(); i++) {
                                    boolean selected = selectedConfig == i;
                                    if (configs.get(i) == null || configs.get(i).isEmpty()) continue;

                                    if (ImGui.selectable(configs.get(i), selected, ImGuiSelectableFlags.DontClosePopups)) {
                                        selectedConfig = i;
                                        configName.set(configs.get(i));
                                    }
                                }
                                ImGui.popFont();

                                ImGui.spacing();
                            }
                            ImGui.endChild();
                            ImGui.popStyleColor();


                            ImGui.setNextItemWidth(259f);
                            ImGui.inputTextWithHint("##ConfigName", "Config Name", configName);
                            if (ImGui.button("Save", 259f, 30)){
                                ConfigSystem.saveConfigToFile(configName.get());
                                configList = ConfigSystem.getConfigList();
                            }
                            if (ImGui.button("Load", 259f, 30)){
                                ConfigSystem.loadConfigFromFile(configName.get());
                            }
                            if (ImGui.button("Create", 259f, 30)){
                                ConfigSystem.saveConfigToFile(configName.get());
                                configList = ConfigSystem.getConfigList();
                            }
                            if (ImGui.button("Refresh", 259f, 30)){
                                configList = ConfigSystem.getConfigList();
                            }
                            ImGui.endChild();
                        }

                        ImGui.sameLine();

                        ImGui.beginChild("##FriendManager", 275f, 520, true, ImGuiWindowFlags.NoScrollbar);

                        ImGui.pushFont(StemBold16);
                        ImGui.text("Friends");
                        ImGui.popFont();

                        ImGui.spacing();

                        ImGui.pushStyleColor(ImGuiCol.ChildBg, ImGui.getColorU32(frameBg.x,frameBg.y,frameBg.z, frameBg.w));
                        ImGui.beginChild("##Friends", 259f, 300, true, ImGuiWindowFlags.NoScrollbar);
                        {

                            ImGui.pushFont(StemBold16);

                            // Display config list
                            List<String> friends = FriendList.friendList.getList();
                            for (int i = 0; i < friends.size(); i++) {
                                boolean selected = selectedFriend == i;
                                if (ImGui.selectable(friends.get(i), selected, ImGuiSelectableFlags.DontClosePopups)) {
                                    selectedFriend = i;
                                    friendName.set(friends.get(i));
                                }
                            }
                            ImGui.popFont();

                            ImGui.spacing();
                            ImGui.endChild();
                            ImGui.popStyleColor();

                            ImGui.setNextItemWidth(259f);
                            ImGui.inputTextWithHint("##FriendName", "Friend Name", friendName);
                            if (ImGui.button("Create", 259f, 30)){
                                FriendList.friendList.add(friendName.get());
                            }
                            if (ImGui.button("Delete", 259f, 30)){
                                FriendList.friendList.remove(friends.get(selectedFriend));
                            }
                            ImGui.newLine();
                            ImGui.spacing();
                            ImGui.spacing();
                            ImGui.pushFont(StemBold16);
                            ImGui.text("Theme");
                            ImGui.popFont();

                            ImGui.setNextItemWidth(259f);
                            ShowStyleSelector();
                        }
                        ImGui.endChild();
                    }
                }

                ImGui.endChild();


                ImGui.end();
                ImGui.popStyleVar();
            }
        }

        NotificationManager.getInstance().update();
        NotificationManager.getInstance().render();
        //End Of The Menu

        ImGui.popFont();
        finishDocking();

        ImGui.render();
        endFrame(windowHandle);

    }

    public static void setupDocking(String windowTitle, String dockspaceId) {
        int windowFlags = ImGuiWindowFlags.NoDocking;
        ImGui.getIO().setKeysDown(ImGuiKey.Tab, false);
        Window window = MinecraftClient.getInstance().getWindow();

        ImGui.getIO().removeConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);

        ImGui.setNextWindowPos(window.getX(), window.getY(), ImGuiCond.Always);
        ImGui.setNextWindowSize(window.getWidth(), window.getHeight());
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoBackground |
                ImGuiWindowFlags.NoNavInputs | ImGuiWindowFlags.NoNav;

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.begin(windowTitle, windowFlags);
        ImGui.popStyleVar(2);

        ImGui.getIO().setKeysDown(ImGuiKey.Tab, false);
    }

    public static void updateScale(){
        Window window = MinecraftClient.getInstance().getWindow();


        if (!cheatUnloaded){
            int id = ImGui.dockSpace(ImGui.getID(menuOpened ? "menu-dockspace" : "overlay-dockspace"), 0, 0, ImGuiDockNodeFlags.PassthruCentralNode |
                    ImGuiDockNodeFlags.NoDockingInCentralNode | ImGuiDockNodeFlags.NoSplit | ImGuiDockNodeFlags.NoDockingInCentralNode);

            ImGuiDockNode centre = imgui.internal.ImGui.dockBuilderGetCentralNode(id);

            WindowScaling.X_OFFSET = (int) centre.getPosX() - window.getX();
            WindowScaling.Y_OFFSET = (int) centre.getPosY() - window.getY();
            WindowScaling.Y_TOP_OFFSET = (int) (window.getHeight() - ((centre.getPosY() - window.getY()) + centre.getSizeY()));
            WindowScaling.WIDTH = (int) centre.getSizeX();
            WindowScaling.HEIGHT = (int) centre.getSizeY();
        }else {
            WindowScaling.X_OFFSET = 0;
            WindowScaling.Y_OFFSET = 0;
            WindowScaling.Y_TOP_OFFSET = 0;
            WindowScaling.WIDTH = window.getWidth();
            WindowScaling.HEIGHT = window.getHeight();
        }
        WindowScaling.update();
    }

    public static void finishDocking() {
        ImGui.end();
    }


    private static void initializeImGui(long glHandle) {
        ImGui.createContext();

        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename(null);                               // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);     // Enable Docking

        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic());

        FontManager.StemBold12 = FontManager.LoadFont("STEM-BOLD.ttf", 12f, fontConfig);

        final ImFontConfig iconConfig = new ImFontConfig();
        iconConfig.setMergeMode(true);
        iconConfig.setPixelSnapH(true);
        iconConfig.setGlyphMinAdvanceX(13.0f);
        short[] iconRanges = {(short)0xF000, (short)0xF8FF, 0};
        iconConfig.setGlyphRanges(iconRanges);
        FontManager.FontAwesome = FontManager.LoadFont("FONT-AWESOME.ttf", 12f, iconConfig);
        iconConfig.destroy();

        FontManager.StemBold14 = FontManager.LoadFont("STEM-BOLD.ttf", 14f, fontConfig);
        FontManager.StemBold16 = FontManager.LoadFont("STEM-BOLD.ttf", 16f, fontConfig);

        FontManager.StemMedium12 = FontManager.LoadFont("STEM-MEDIUM.ttf", 12f, fontConfig);
        FontManager.StemMedium14 = FontManager.LoadFont("STEM-MEDIUM.ttf", 14f, fontConfig);
        FontManager.StemMedium16 = FontManager.LoadFont("STEM-MEDIUM.ttf", 16f, fontConfig);

        FontManager.StemRegular12 = FontManager.LoadFont("STEM-REGULAR.ttf", 12f, fontConfig);
        FontManager.StemRegular14 = FontManager.LoadFont("STEM-REGULAR.ttf", 14f, fontConfig);
        FontManager.StemRegular16 = FontManager.LoadFont("STEM-REGULAR.ttf", 16f, fontConfig);

        TextureManager.locationMarker = TextureManager.loadTexture("gps/LocationMarker.png");
        TextureManager.targethud = TextureManager.loadTexture("targethud/target.png");
        TextureManager.oofArrow = TextureManager.loadTexture("arrow.png");
        TextureManager.no_thumbnail = TextureManager.loadTexture("no-thumbnail.png");

        fontAtlas.addFontDefault();

        fontConfig.setMergeMode(true);
        fontConfig.setPixelSnapH(true);

        fontConfig.destroy();

        configList = ConfigSystem.getConfigList();

        SetupStyles();
    }


    public static void endFrame(long windowPtr) {
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }
}
