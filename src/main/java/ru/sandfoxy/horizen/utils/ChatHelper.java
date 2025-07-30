package ru.sandfoxy.horizen.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class ChatHelper {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static boolean stopLogging = false;

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})(.)");
    private static final Pattern FORMAT_PATTERN = Pattern.compile("&([0-9a-fk-or])", Pattern.CASE_INSENSITIVE);
    private static final Text ClientPrefix = parse("&#7C0FECH&#740EDFo&#6C0DD2r&#640CC5i&#5C0AB8z&#5409ABe&#4C089En &8» ");

    public static Text parse(String input) {
        MutableText result = Text.literal("");

        Style currentStyle = Style.EMPTY;
        int index = 0;

        while (index < input.length()) {
            if (input.startsWith("&#", index) && index + 8 <= input.length()) {
                String hex = input.substring(index + 2, index + 8);
                char chr = input.charAt(index + 8);

                int rgb = Integer.parseInt(hex, 16);
                Style styled = currentStyle.withColor(rgb);

                result.append(Text.literal(String.valueOf(chr)).setStyle(styled));
                index += 9;
                continue;
            }

            if (input.charAt(index) == '&' && index + 1 < input.length()) {
                char code = Character.toLowerCase(input.charAt(index + 1));
                Formatting formatting = fromCode(code);

                if (formatting != null) {
                    if (formatting == Formatting.RESET) {
                        currentStyle = Style.EMPTY;
                    } else if (formatting.isColor()) {
                        currentStyle = currentStyle.withColor(formatting);
                    } else {
                        currentStyle = applyStyle(currentStyle, formatting);
                    }
                }

                index += 2;
                continue;
            }

            result.append(Text.literal(String.valueOf(input.charAt(index))).setStyle(currentStyle));
            index++;
        }

        return result;
    }

    private static Formatting fromCode(char code) {
        for (Formatting formatting : Formatting.values()) {
            String mcCode = String.valueOf(formatting.getCode());
            if (mcCode.equalsIgnoreCase(String.valueOf(code))) {
                return formatting;
            }
        }
        return null;
    }



    private static Style applyStyle(Style style, Formatting formatting) {
        switch (formatting) {
            case BOLD: return style.withBold(true);
            case ITALIC: return style.withItalic(true);
            case UNDERLINE: return style.withUnderline(true);
            case STRIKETHROUGH: return style.withStrikethrough(true);
            case OBFUSCATED: return style.withObfuscated(true);
            default: return style;
        }
    }

    public static void sendToPlayer(String message){
        MutableText combined = ClientPrefix.copy().append(parse(message));

        ChatHelper.stopLogging = true;
        mc.inGameHud.getChatHud().addMessage(combined);
        ChatHelper.stopLogging = false;
    }
}
