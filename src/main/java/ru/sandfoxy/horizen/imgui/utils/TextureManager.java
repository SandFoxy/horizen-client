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

            // Читаем данные изображения в ByteBuffer напрямую из InputStream
            byte[] imageData = inputStream.readAllBytes();
            ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
            imageBuffer.put(imageData);
            imageBuffer.flip();

            IntBuffer width = BufferUtils.createIntBuffer(1);
            IntBuffer height = BufferUtils.createIntBuffer(1);
            IntBuffer channels = BufferUtils.createIntBuffer(1);

            // Загружаем изображение напрямую из памяти
            ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, width, height, channels, 4); // RGBA

            if (image == null) {
                throw new RuntimeException("Failed to load texture: " + STBImage.stbi_failure_reason());
            }

            int textureId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

            // Setup texture parameters
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

            // Upload texture data
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(0), height.get(0), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

            // Free the image buffer
            STBImage.stbi_image_free(image);

            // Unbind texture
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            LOGGER.info("Resource {} loaded as: {}", resourceLocation, textureId);
            return new ImGuiTexture(textureId, width.get(0), height.get(0));
        } catch (IOException e) {
            LOGGER.error("Failed to load resource {}: {}", resourceLocation, e.getMessage());
            return null;
        }
    }

    public static ImGuiTexture loadTextureFromBytes(byte[] imageData) {
        // Установка флага для переворота изображения по вертикали
        // Копируем данные в ByteBuffer
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
        imageBuffer.put(imageData);
        imageBuffer.flip();

        // Буферы для размеров
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        // Загружаем изображение из памяти (принудительно в формате RGBA)
        ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, width, height, channels, 4);

        if (image == null) {
            throw new RuntimeException("Failed to load texture: " + STBImage.stbi_failure_reason());
        }

        int texWidth = width.get(0);
        int texHeight = height.get(0);

        // Генерируем ID и настраиваем OpenGL
        int textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        // Устанавливаем выравнивание (важно для OpenGL, если ширина не кратна 4)
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // Настройка параметров текстуры
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        // Загрузка изображения в OpenGL
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
                texWidth, texHeight, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image
        );

        // Генерация мипмапов (опционально, если используешь)
        // GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        // Очистка временного буфера
        STBImage.stbi_image_free(image);

        // Отключение текстуры
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        // Возвращаем обёртку
        return new ImGuiTexture(textureId, texWidth, texHeight);
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
                // Читаем данные изображения в ByteBuffer напрямую из InputStream
                byte[] imageData = inputStream.readAllBytes();
                ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
                imageBuffer.put(imageData);
                imageBuffer.flip();
    
                IntBuffer width = BufferUtils.createIntBuffer(1);
                IntBuffer height = BufferUtils.createIntBuffer(1);
                IntBuffer channels = BufferUtils.createIntBuffer(1);
    
                // Загружаем изображение напрямую из памяти
                ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, width, height, channels, 4); // RGBA
                if (image == null) {
                    throw new RuntimeException("Failed to load texture: " + STBImage.stbi_failure_reason());
                }
    
                int textureId = GL11.glGenTextures();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(0), height.get(0), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
                STBImage.stbi_image_free(image);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    
                LOGGER.info("Resource {} loaded as: {}", identifier, textureId);
                return new ImGuiTexture(textureId, width.get(0), height.get(0));
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load resource {}: {}", resourceLocation, e.getMessage());
            return null;
        }
    }
    
    /**
     * Выгружает текстуру по объекту ImGuiTexture
     * @param texture объект текстуры для выгрузки
     */
    public static void unloadTexture(ImGuiTexture texture) {
        if (texture != null && texture.textureId > 0) {
            GL11.glDeleteTextures(texture.textureId);
            LOGGER.info("Texture with ID {} unloaded", texture.textureId);
            texture.textureId = 0; // Обнуляем ID после выгрузки
        }
    }
    
    /**
     * Выгружает текстуру по ID
     * @param textureId ID текстуры для выгрузки
     */
    public static void unloadTexture(int textureId) {
        if (textureId > 0) {
            GL11.glDeleteTextures(textureId);
            LOGGER.info("Texture with ID {} unloaded", textureId);
        }
    }
    
    /**
     * Выгружает все статические текстуры
     */
    public static void unloadAllStaticTextures() {
        unloadTexture(locationMarker);
        unloadTexture(targethud);
        unloadTexture(oofArrow);
        
        // Обнуляем статические переменные
        locationMarker = null;
        targethud = null;
        oofArrow = null;
        
        LOGGER.info("All static textures unloaded");
    }
    
}
