package ru.sandfoxy.horizen.imgui.utils;

import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGui;

import java.io.IOException;
import java.io.InputStream;

import static ru.sandfoxy.horizen.ModEntryPoint.LOGGER;

public class FontManager {
    public static ImFont StemBold12;
    public static ImFont StemBold14;
    public static ImFont StemBold16;

    public static ImFont StemMedium12;
    public static ImFont StemMedium14;
    public static ImFont StemMedium16;

    public static ImFont StemRegular12;
    public static ImFont StemRegular14;
    public static ImFont StemRegular16;

    public static ImFont FontAwesome;

    public static ImFont LoadFont(String fontName, float size, ImFontConfig config) {
        String resourcePath = "/horizen/fonts/" + fontName;

        try (InputStream inputStream = FontManager.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                LOGGER.error("Font resource not found: {}", resourcePath);
                return null;
            }

            byte[] fontData = inputStream.readAllBytes();
            LOGGER.info("[{} | {}] Loaded font from memory {}", fontName, size, resourcePath);
            return ImGui.getIO().getFonts().addFontFromMemoryTTF(
                    fontData, size, config
            );
        } catch (IOException e) {
            LOGGER.error("Failed to load font {}: {}", fontName, e.getMessage());
            return null;
        }
    }

}
