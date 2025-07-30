package ru.sandfoxy.horizen.imgui;

import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;
import ru.sandfoxy.horizen.imgui.utils.FontManager;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.utils.animations.Animation;
import ru.sandfoxy.horizen.utils.animations.Direction;
import ru.sandfoxy.horizen.utils.animations.impl.SmoothStepAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.sandfoxy.horizen.utils.render.RenderUtils.ConvertColor;

import java.util.List;
import java.awt.Color;


public class ImGuiUils {
    public static boolean menuCentred = false;
    private static List<Module> moduleList = new ArrayList<>();
    private static Map<String, Animation> moduleAnimations = new HashMap<>();
    private static Animation scaleAnimation = new SmoothStepAnimation(300, 1.0, Direction.FORWARDS);
    private static Animation fadeAnimation = new SmoothStepAnimation(200, 1.0, Direction.FORWARDS);

    public static void centerMenu(float menuSizeX, float menuSizeY){
        if (menuCentred) return;

        ImGui.setNextWindowPos(
                (ImGui.getIO().getDisplaySizeX() / 2) - (menuSizeX / 2),
                (ImGui.getIO().getDisplaySizeY() / 2) - (menuSizeY / 2)
        );
        menuCentred = true;
    }

    private static float lastKeybindsX = 0.f;
    private static float lastKeybindsY = 0.f;
    public static void drawKeybinds() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;

        List<Module> localList = new ArrayList<>(moduleList);
        boolean hasEnabledModules = false;

        for (Module module : ModuleManager.getModules()) {
            if (module.isEnabled() && module.isBinded()) {
                if (!localList.contains(module)) {
                    localList.add(module);
                }
                hasEnabledModules = true;
            } else {
                localList.remove(module);
            }
        }

        if (localList.isEmpty() && mc.inGameHud.getChatHud().isChatFocused()) {
            Module keybindPreview = ModuleManager.getByName("Keybind Preview");
            if (keybindPreview != null) {
                localList.add(keybindPreview);
                hasEnabledModules = true;
            }
        }

        updateModuleAnimations(localList);

        if (hasEnabledModules || !localList.isEmpty()) {
            if (scaleAnimation.getDirection() == Direction.BACKWARDS) {
                scaleAnimation.setDirection(Direction.FORWARDS);
            }
            if (fadeAnimation.getDirection() == Direction.BACKWARDS) {
                fadeAnimation.setDirection(Direction.FORWARDS);
            }
        } else {
            if (scaleAnimation.getDirection() == Direction.FORWARDS) {
                scaleAnimation.setDirection(Direction.BACKWARDS);
            }
            if (fadeAnimation.getDirection() == Direction.FORWARDS) {
                fadeAnimation.setDirection(Direction.BACKWARDS);
            }
        }

        double scaleValue = scaleAnimation.getOutput();
        double fadeValue = fadeAnimation.getOutput();

        if (fadeAnimation.finished(Direction.BACKWARDS) && scaleAnimation.finished(Direction.BACKWARDS)) {
            return;
        }

        if (localList.isEmpty()) return;
        moduleList = localList;

        float itemHeight = 25f;
        float headerHeight = 35f;
        float padding = 10f;
        float windowWidth = 250f;
        float originalWindowHeight = headerHeight + (localList.size() * itemHeight) + padding;
        float windowHeight = (float) (originalWindowHeight * scaleValue);

        ImVec4 frameBgIMGUI = ImGui.getStyle().getColor(ImGuiCol.FrameBg);
        int frameBg = ConvertColor(new Color(frameBgIMGUI.x, frameBgIMGUI.y, frameBgIMGUI.z, (float) (frameBgIMGUI.w * fadeValue)));

        ImVec4 buttonActiveIMGUI = ImGui.getStyle().getColor(ImGuiCol.ButtonActive);
        int buttonActive = ConvertColor(new Color(buttonActiveIMGUI.x, buttonActiveIMGUI.y, buttonActiveIMGUI.z, (float) (buttonActiveIMGUI.w * fadeValue)));

        ImGui.setNextWindowBgAlpha(0f);
        ImGui.setNextWindowSize(windowWidth + 10, windowHeight + 10);


        ImGui.begin("##Keybinds", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoResize);

        ImDrawList draw = ImGui.getWindowDrawList();
        float posX = ImGui.getWindowPosX() + 5;
        float posY = ImGui.getWindowPosY() + 5;

        float centerY = posY + originalWindowHeight / 2;
        float scaledPosY = centerY - windowHeight / 2;

        float scaledHeaderHeight = (float) (headerHeight * scaleValue);

        ImGuiUils.drawGlow(
                ImGui.getBackgroundDrawList(),
                posX, posY,
                windowWidth, windowHeight,
                new Color(buttonActiveIMGUI.x, buttonActiveIMGUI.y, buttonActiveIMGUI.z,(float) (buttonActiveIMGUI.w * fadeValue)),
                10
        );

        draw.addRectFilled(posX, scaledPosY, posX + windowWidth, scaledPosY + windowHeight,
                frameBg, 10f);

        draw.addRectFilled(posX, scaledPosY, posX + windowWidth, scaledPosY + scaledHeaderHeight,
                buttonActive, 10f);

        draw.addRectFilled(posX, scaledPosY + scaledHeaderHeight / 2, posX + windowWidth, scaledPosY + scaledHeaderHeight,
                buttonActive);

        if (scaleValue > 0.3) {
            String headerText = "KEYBINDS";
            ImGui.pushFont(FontManager.StemBold16);
            ImVec2 headerSize = ImGui.calcTextSize(headerText);
            ImGui.popFont();
            float headerX = posX + (windowWidth - headerSize.x) / 2f;
            float headerY = scaledPosY + (scaledHeaderHeight - headerSize.y) / 2f;

            int textColor = ConvertColor(new Color(1f, 1f, 1f, (float) fadeValue));
            draw.addText(FontManager.StemBold16, 16f, headerX, headerY, textColor, headerText);

            float currentY = scaledPosY + scaledHeaderHeight + (float) (10f * scaleValue);
            float scaledItemHeight = (float) (itemHeight * scaleValue);

            for (int i = 0; i < localList.size() && currentY + scaledItemHeight <= scaledPosY + windowHeight; i++) {
                Module module = localList.get(i);
                String moduleName = module.getName();
                String keybind = module.getKeybind();

                Animation moduleAnimation = moduleAnimations.get(moduleName);
                double moduleScale = moduleAnimation != null ? moduleAnimation.getOutput() : 1.0;

                int animatedTextColor = ConvertColor(new Color(1f, 1f, 1f, (float) (fadeValue * moduleScale)));

                float moduleItemHeight = (float) (scaledItemHeight * moduleScale);
                float moduleY = currentY + (scaledItemHeight - moduleItemHeight) / 2f;

                if (moduleScale > 0.1) {
                    draw.addText(FontManager.StemBold14, (float) (14f * moduleScale), posX + 15f, moduleY,
                            animatedTextColor, moduleName);

                    ImVec2 keybindSize = ImGui.calcTextSize(keybind);
                    float keybindX = posX + windowWidth - (keybindSize.x * (float) moduleScale) - 15f;

                    draw.addText(FontManager.StemBold14, (float) (14f * moduleScale), keybindX, moduleY,
                            animatedTextColor, keybind);
                }

                currentY += scaledItemHeight;
            }
        }

        draw.addRect(posX, scaledPosY, posX + windowWidth, scaledPosY + windowHeight,
                buttonActive, 10f, 0, 2f);
        ImGui.end();
    }

    private static void updateModuleAnimations(List<Module> currentModules) {
        java.util.Set<String> currentModuleNames = new java.util.HashSet<>();
        for (Module module : currentModules) {
            currentModuleNames.add(module.getName());
        }

        for (Module module : currentModules) {
            String moduleName = module.getName();
            if (!moduleAnimations.containsKey(moduleName)) {
                Animation newAnimation = new SmoothStepAnimation(200, 1.0, Direction.FORWARDS);
                moduleAnimations.put(moduleName, newAnimation);
            } else {
                Animation existingAnimation = moduleAnimations.get(moduleName);
                if (existingAnimation.getDirection() == Direction.BACKWARDS) {
                    existingAnimation.setDirection(Direction.FORWARDS);
                }
            }
        }

        Iterator<Map.Entry<String, Animation>> iterator = moduleAnimations.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Animation> entry = iterator.next();
            String moduleName = entry.getKey();
            Animation animation = entry.getValue();
            
            if (!currentModuleNames.contains(moduleName)) {
                if (animation.getDirection() == Direction.FORWARDS) {
                    animation.setDirection(Direction.BACKWARDS);
                }
                if (animation.finished(Direction.BACKWARDS)) {
                    iterator.remove();
                }
            }
        }
    }

    public static void resetKeybindAnimations() {
        scaleAnimation.reset();
        fadeAnimation.reset();
        scaleAnimation.setDirection(Direction.BACKWARDS);
        fadeAnimation.setDirection(Direction.BACKWARDS);

        moduleAnimations.clear();
    }

    private static void centerText(String text) {
        float windowWidth = ImGui.getWindowSizeX();
        float textWidth = ImGui.calcTextSize(text).x;
        ImGui.setCursorPosX((windowWidth - textWidth) / 2);
        ImGui.text(text);
    }

    public static void drawTextCentred(String text, float x, float y, float size, int color, float scale, boolean outlined) {
        ImDrawList drawList = ImGui.getBackgroundDrawList();
        ImFont font = FontManager.StemBold16;

        float textWidth = ImGui.calcTextSize(text).x * scale;
        ImVec2 textSize = new ImVec2(textWidth, ImGui.calcTextSize(text).y);

        float textX = x - textSize.x / 2;
        float textY = y - textSize.y / 2;

        Color textColor = new Color(color, true);
        int outlineColor = ConvertColor(new Color(0, 0, 0, textColor.getAlpha()));

        if (outlined) {
            float outline_thickness = 1.0f;

            drawList.addText(font, (int) size, textX - outline_thickness, textY, outlineColor, text);
            drawList.addText(font, (int) size, textX + outline_thickness, textY, outlineColor, text);
            drawList.addText(font, (int) size, textX, textY - outline_thickness, outlineColor, text);
            drawList.addText(font, (int) size, textX, textY + outline_thickness, outlineColor, text);
            drawList.addText(font, (int) size, textX - outline_thickness, textY - outline_thickness, outlineColor, text);
            drawList.addText(font, (int) size, textX + outline_thickness, textY + outline_thickness, outlineColor, text);
            drawList.addText(font, (int) size, textX - outline_thickness, textY + outline_thickness, outlineColor, text);
            drawList.addText(font, (int) size, textX + outline_thickness, textY - outline_thickness, outlineColor, text);
        }

        // Draw main text
        drawList.addText(font, (int) size, textX, textY, color, text);
    }
    public static void drawTextCentredWithColors(String text, float x, float y, float size, float scale, boolean outlined) {
        ImDrawList drawList = ImGui.getBackgroundDrawList();
        ImFont font = FontManager.StemBold16;

        List<TextFragment> fragments = parseColoredText(text);

        float totalWidth = 0f;
        for (TextFragment fragment : fragments) {
            float width = ImGui.calcTextSize(fragment.text).x * scale;
            fragment.width = width;
            totalWidth += width;
        }

        float textY = y - ImGui.calcTextSize("A").y / 2.0f;
        float cursorX = x - totalWidth / 2.0f;

        for (TextFragment fragment : fragments) {
            int outlineColor = ConvertColor(Color.black);

            if (outlined) {
                float offset = 1.0f;
                for (float ox = -1; ox <= 1; ox++) {
                    for (float oy = -1; oy <= 1; oy++) {
                        if (ox != 0 || oy != 0) {
                            drawList.addText(font, (int) size, cursorX + ox, textY + oy, outlineColor, fragment.text);
                        }
                    }
                }
            }

            drawList.addText(font, (int) size, cursorX, textY, fragment.color, fragment.text);
            cursorX += fragment.width;
        }
    }

    static class TextFragment {
        String text;
        int color;
        float width;

        TextFragment(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }

    private static List<TextFragment> parseColoredText(String input) {
        List<TextFragment> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("(#([0-9a-fA-F]{6}))?([^#]+)");
        Matcher matcher = pattern.matcher(input);

        int currentColor = ConvertColor(Color.white);

        while (matcher.find()) {
            String colorCode = matcher.group(2);
            String content = matcher.group(3);
            if (colorCode != null) {
                currentColor = ConvertColor(Color.decode("#" + colorCode));
            }
            result.add(new TextFragment(content, currentColor));
        }

        return result;
    }


    public static void drawTextCentred(String text, float x, float y, float size, int color, boolean outlined) {
        drawTextCentred(text, x, y, size, color, 1.0f, outlined);
    }

    public static void drawTextCentred(String text, float x, float y, float size) {
        drawTextCentred(text, x, y, size, 0xFFFFFF, false);
    }

    public static void drawGlow(ImDrawList draw, float x, float y, float width, float height, Color color, float rounding) {
        int layers = 15;
        float maxExpand = 12f;

        for (int i = 0; i < layers; i++) {
            float t = i / (float) layers;
            float expand = maxExpand * t;
            float alpha = (1.0f - t) * 0.25f;

            int glowColor = ConvertColor(new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    (int) (alpha * 255)
            ));

            draw.addRectFilled(
                    x - expand,
                    y - expand,
                    x + width + expand,
                    y + height + expand,
                    glowColor,
                    rounding + expand
            );
        }

        int borderColor = ConvertColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (0.1f * 255)));
        draw.addRect(x, y, x + width, y + height, borderColor, rounding, 0, 1.0f);
    }


    public static void drawBlur(ImDrawList draw, float x, float y, float width, float height, float rounding) {
        drawBlur(draw, x, y, width, height, rounding, new Color(255, 255, 255), 6.0f);
    }

    public static void drawBlur(ImDrawList draw, float x, float y, float width, float height, float rounding, Color color, float blurStrength) {
        // True blur implementation - draws the same shape multiple times with small offsets
        int samples = 25;
        float maxOffset = blurStrength;
        
        // Calculate alpha per sample to maintain consistent total opacity
        float alphaPerSample = 0.8f / samples;
        
        for (int i = 0; i < samples; i++) {
            // Create random-like offsets for natural blur distribution
            float angle = (float) (i * 2.37 * Math.PI); // Use golden angle for good distribution
            float distance = (float) Math.sqrt(i / (double) samples) * maxOffset;
            
            float offsetX = (float) (Math.cos(angle) * distance);
            float offsetY = (float) (Math.sin(angle) * distance);
            
            // Use the provided color with calculated alpha
            int blurColor = ConvertColor(new Color(
                color.getRed(),
                color.getGreen(), 
                color.getBlue(),
                (int)(alphaPerSample * 255)
            ));
            
            draw.addRectFilled(
                x + offsetX,
                y + offsetY,
                x + width + offsetX,
                y + height + offsetY,
                blurColor,
                rounding
            );
        }
    }
}
