package ru.sandfoxy.horizen.imgui.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;

import static ru.sandfoxy.horizen.ModEntryPoint.LOGGER;

public class TextureManager {
    public static ImGuiTexture locationMarker = null;
    public static ImGuiTexture targethud = null;
    public static ImGuiTexture oofArrow = null;
    public static ImGuiTexture no_thumbnail = null;

    public static ImGuiTexture loadTexture(String resourceLocation) {
        String resourcePath = "/horizen/images/" + resourceLocation;

        try (InputStream inputStream = FontManager.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                LOGGER.error("Resource not found: {}", resourcePath);
                return null;
            }

            return createTextureFromStream(inputStream, resourceLocation);
        } catch (IOException e) {
            LOGGER.error("Failed to load resource {}: {}", resourceLocation, e.getMessage());
            return null;
        }
    }

    public static ImGuiTexture loadTextureFromBytes(byte[] imageData) {
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
        imageBuffer.put(imageData);
        imageBuffer.flip();

        return createTextureFromBuffer(imageBuffer);
    }

    public static ImGuiTexture loadMinecraftTexture(String resourceLocation) {
        try {
            ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
            Identifier identifier = Identifier.of("minecraft", resourceLocation);

            Optional<Resource> optional = resourceManager.getResource(identifier);
            if (optional.isEmpty()) {
                LOGGER.error("Resource not found: {}", identifier);
                return null;
            }

            try (InputStream inputStream = optional.get().getInputStream()) {
                return createTextureFromStream(inputStream, identifier.toString());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load resource {}: {}", resourceLocation, e.getMessage());
            return null;
        }
    }

    private static ImGuiTexture createTextureFromStream(InputStream inputStream, String resourceName) throws IOException {
        byte[] imageData = inputStream.readAllBytes();
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
        imageBuffer.put(imageData);
        imageBuffer.flip();

        return createTextureFromBuffer(imageBuffer, resourceName);
    }

    private static ImGuiTexture createTextureFromBuffer(ByteBuffer imageBuffer) {
        return createTextureFromBuffer(imageBuffer, "unknown");
    }

    private static ImGuiTexture createTextureFromBuffer(ByteBuffer imageBuffer, String resourceName) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, width, height, channels, 4);

        if (image == null) {
            throw new RuntimeException("Failed to load texture: " + STBImage.stbi_failure_reason());
        }

        int texWidth = width.get(0);
        int texHeight = height.get(0);

        int textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, texWidth, texHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

        STBImage.stbi_image_free(image);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        LOGGER.info("Resource {} loaded as: {}", resourceName, textureId);
        return new ImGuiTexture(textureId, texWidth, texHeight);
    }

    public static void unloadTexture(ImGuiTexture texture) {
        if (texture != null && texture.textureId > 0) {
            GL11.glDeleteTextures(texture.textureId);
            LOGGER.info("Texture with ID {} unloaded", texture.textureId);
            texture.textureId = 0;
        }
    }

    public static void unloadTexture(int textureId) {
        if (textureId > 0) {
            GL11.glDeleteTextures(textureId);
            LOGGER.info("Texture with ID {} unloaded", textureId);
        }
    }

    public static void unloadAllStaticTextures() {
        unloadTexture(locationMarker);
        unloadTexture(targethud);
        unloadTexture(oofArrow);

        locationMarker = null;
        targethud = null;
        oofArrow = null;

        LOGGER.info("All static textures unloaded");
    }
}
