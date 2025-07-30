package ru.sandfoxy.horizen.utils.others;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GLFWKeyMapper {
    // GLFW key constants
    public static final int GLFW_KEY_SPACE         = 32;
    public static final int GLFW_KEY_APOSTROPHE    = 39;
    public static final int GLFW_KEY_COMMA         = 44;
    public static final int GLFW_KEY_MINUS         = 45;
    public static final int GLFW_KEY_PERIOD        = 46;
    public static final int GLFW_KEY_SLASH         = 47;
    public static final int GLFW_KEY_0             = 48;
    public static final int GLFW_KEY_1             = 49;
    public static final int GLFW_KEY_2             = 50;
    public static final int GLFW_KEY_3             = 51;
    public static final int GLFW_KEY_4             = 52;
    public static final int GLFW_KEY_5             = 53;
    public static final int GLFW_KEY_6             = 54;
    public static final int GLFW_KEY_7             = 55;
    public static final int GLFW_KEY_8             = 56;
    public static final int GLFW_KEY_9             = 57;
    public static final int GLFW_KEY_SEMICOLON     = 59;
    public static final int GLFW_KEY_EQUAL         = 61;
    public static final int GLFW_KEY_A             = 65;
    public static final int GLFW_KEY_B             = 66;
    public static final int GLFW_KEY_C             = 67;
    public static final int GLFW_KEY_D             = 68;
    public static final int GLFW_KEY_E             = 69;
    public static final int GLFW_KEY_F             = 70;
    public static final int GLFW_KEY_G             = 71;
    public static final int GLFW_KEY_H             = 72;
    public static final int GLFW_KEY_I             = 73;
    public static final int GLFW_KEY_J             = 74;
    public static final int GLFW_KEY_K             = 75;
    public static final int GLFW_KEY_L             = 76;
    public static final int GLFW_KEY_M             = 77;
    public static final int GLFW_KEY_N             = 78;
    public static final int GLFW_KEY_O             = 79;
    public static final int GLFW_KEY_P             = 80;
    public static final int GLFW_KEY_Q             = 81;
    public static final int GLFW_KEY_R             = 82;
    public static final int GLFW_KEY_S             = 83;
    public static final int GLFW_KEY_T             = 84;
    public static final int GLFW_KEY_U             = 85;
    public static final int GLFW_KEY_V             = 86;
    public static final int GLFW_KEY_W             = 87;
    public static final int GLFW_KEY_X             = 88;
    public static final int GLFW_KEY_Y             = 89;
    public static final int GLFW_KEY_Z             = 90;
    public static final int GLFW_KEY_LEFT_BRACKET  = 91;
    public static final int GLFW_KEY_BACKSLASH     = 92;
    public static final int GLFW_KEY_RIGHT_BRACKET = 93;
    public static final int GLFW_KEY_GRAVE_ACCENT  = 96;
    public static final int GLFW_KEY_WORLD_1       = 161;
    public static final int GLFW_KEY_WORLD_2       = 162;
    public static final int GLFW_KEY_ESCAPE        = 256;
    public static final int GLFW_KEY_ENTER         = 257;
    public static final int GLFW_KEY_TAB           = 258;
    public static final int GLFW_KEY_BACKSPACE     = 259;
    public static final int GLFW_KEY_INSERT        = 260;
    public static final int GLFW_KEY_DELETE        = 261;
    public static final int GLFW_KEY_RIGHT         = 262;
    public static final int GLFW_KEY_LEFT          = 263;
    public static final int GLFW_KEY_DOWN          = 264;
    public static final int GLFW_KEY_UP            = 265;
    public static final int GLFW_KEY_PAGE_UP       = 266;
    public static final int GLFW_KEY_PAGE_DOWN     = 267;
    public static final int GLFW_KEY_HOME          = 268;
    public static final int GLFW_KEY_END           = 269;
    public static final int GLFW_KEY_CAPS_LOCK     = 280;
    public static final int GLFW_KEY_SCROLL_LOCK   = 281;
    public static final int GLFW_KEY_NUM_LOCK      = 282;
    public static final int GLFW_KEY_PRINT_SCREEN  = 283;
    public static final int GLFW_KEY_PAUSE         = 284;
    public static final int GLFW_KEY_F1            = 290;
    public static final int GLFW_KEY_F2            = 291;
    public static final int GLFW_KEY_F3            = 292;
    public static final int GLFW_KEY_F4            = 293;
    public static final int GLFW_KEY_F5            = 294;
    public static final int GLFW_KEY_F6            = 295;
    public static final int GLFW_KEY_F7            = 296;
    public static final int GLFW_KEY_F8            = 297;
    public static final int GLFW_KEY_F9            = 298;
    public static final int GLFW_KEY_F10           = 299;
    public static final int GLFW_KEY_F11           = 300;
    public static final int GLFW_KEY_F12           = 301;
    public static final int GLFW_KEY_F13           = 302;
    public static final int GLFW_KEY_F14           = 303;
    public static final int GLFW_KEY_F15           = 304;
    public static final int GLFW_KEY_F16           = 305;
    public static final int GLFW_KEY_F17           = 306;
    public static final int GLFW_KEY_F18           = 307;
    public static final int GLFW_KEY_F19           = 308;
    public static final int GLFW_KEY_F20           = 309;
    public static final int GLFW_KEY_F21           = 310;
    public static final int GLFW_KEY_F22           = 311;
    public static final int GLFW_KEY_F23           = 312;
    public static final int GLFW_KEY_F24           = 313;
    public static final int GLFW_KEY_F25           = 314;
    public static final int GLFW_KEY_KP_0          = 320;
    public static final int GLFW_KEY_KP_1          = 321;
    public static final int GLFW_KEY_KP_2          = 322;
    public static final int GLFW_KEY_KP_3          = 323;
    public static final int GLFW_KEY_KP_4          = 324;
    public static final int GLFW_KEY_KP_5          = 325;
    public static final int GLFW_KEY_KP_6          = 326;
    public static final int GLFW_KEY_KP_7          = 327;
    public static final int GLFW_KEY_KP_8          = 328;
    public static final int GLFW_KEY_KP_9          = 329;
    public static final int GLFW_KEY_KP_DECIMAL    = 330;
    public static final int GLFW_KEY_KP_DIVIDE     = 331;
    public static final int GLFW_KEY_KP_MULTIPLY   = 332;
    public static final int GLFW_KEY_KP_SUBTRACT   = 333;
    public static final int GLFW_KEY_KP_ADD        = 334;
    public static final int GLFW_KEY_KP_ENTER      = 335;
    public static final int GLFW_KEY_KP_EQUAL      = 336;
    public static final int GLFW_KEY_LEFT_SHIFT    = 340;
    public static final int GLFW_KEY_LEFT_CONTROL  = 341;
    public static final int GLFW_KEY_LEFT_ALT      = 342;
    public static final int GLFW_KEY_LEFT_SUPER    = 343;
    public static final int GLFW_KEY_RIGHT_SHIFT   = 344;
    public static final int GLFW_KEY_RIGHT_CONTROL = 345;
    public static final int GLFW_KEY_RIGHT_ALT     = 346;
    public static final int GLFW_KEY_RIGHT_SUPER   = 347;
    public static final int GLFW_KEY_MENU          = 348;
    public static final int GLFW_KEY_LAST          = GLFW_KEY_MENU;

    // List of all GLFW key codes
    public static final List<Integer> GLFW_KEY_CODES = Arrays.asList(
            GLFW_KEY_SPACE,
            GLFW_KEY_APOSTROPHE,
            GLFW_KEY_COMMA,
            GLFW_KEY_MINUS,
            GLFW_KEY_PERIOD,
            GLFW_KEY_SLASH,
            GLFW_KEY_0,
            GLFW_KEY_1,
            GLFW_KEY_2,
            GLFW_KEY_3,
            GLFW_KEY_4,
            GLFW_KEY_5,
            GLFW_KEY_6,
            GLFW_KEY_7,
            GLFW_KEY_8,
            GLFW_KEY_9,
            GLFW_KEY_SEMICOLON,
            GLFW_KEY_EQUAL,
            GLFW_KEY_A,
            GLFW_KEY_B,
            GLFW_KEY_C,
            GLFW_KEY_D,
            GLFW_KEY_E,
            GLFW_KEY_F,
            GLFW_KEY_G,
            GLFW_KEY_H,
            GLFW_KEY_I,
            GLFW_KEY_J,
            GLFW_KEY_K,
            GLFW_KEY_L,
            GLFW_KEY_M,
            GLFW_KEY_N,
            GLFW_KEY_O,
            GLFW_KEY_P,
            GLFW_KEY_Q,
            GLFW_KEY_R,
            GLFW_KEY_S,
            GLFW_KEY_T,
            GLFW_KEY_U,
            GLFW_KEY_V,
            GLFW_KEY_W,
            GLFW_KEY_X,
            GLFW_KEY_Y,
            GLFW_KEY_Z,
            GLFW_KEY_LEFT_BRACKET,
            GLFW_KEY_BACKSLASH,
            GLFW_KEY_RIGHT_BRACKET,
            GLFW_KEY_GRAVE_ACCENT,
            GLFW_KEY_WORLD_1,
            GLFW_KEY_WORLD_2,
            GLFW_KEY_ESCAPE,
            GLFW_KEY_ENTER,
            GLFW_KEY_TAB,
            GLFW_KEY_BACKSPACE,
            GLFW_KEY_INSERT,
            GLFW_KEY_DELETE,
            GLFW_KEY_RIGHT,
            GLFW_KEY_LEFT,
            GLFW_KEY_DOWN,
            GLFW_KEY_UP,
            GLFW_KEY_PAGE_UP,
            GLFW_KEY_PAGE_DOWN,
            GLFW_KEY_HOME,
            GLFW_KEY_END,
            GLFW_KEY_CAPS_LOCK,
            GLFW_KEY_SCROLL_LOCK,
            GLFW_KEY_NUM_LOCK,
            GLFW_KEY_PRINT_SCREEN,
            GLFW_KEY_PAUSE,
            GLFW_KEY_F1,
            GLFW_KEY_F2,
            GLFW_KEY_F3,
            GLFW_KEY_F4,
            GLFW_KEY_F5,
            GLFW_KEY_F6,
            GLFW_KEY_F7,
            GLFW_KEY_F8,
            GLFW_KEY_F9,
            GLFW_KEY_F10,
            GLFW_KEY_F11,
            GLFW_KEY_F12,
            GLFW_KEY_F13,
            GLFW_KEY_F14,
            GLFW_KEY_F15,
            GLFW_KEY_F16,
            GLFW_KEY_F17,
            GLFW_KEY_F18,
            GLFW_KEY_F19,
            GLFW_KEY_F20,
            GLFW_KEY_F21,
            GLFW_KEY_F22,
            GLFW_KEY_F23,
            GLFW_KEY_F24,
            GLFW_KEY_F25,
            GLFW_KEY_KP_0,
            GLFW_KEY_KP_1,
            GLFW_KEY_KP_2,
            GLFW_KEY_KP_3,
            GLFW_KEY_KP_4,
            GLFW_KEY_KP_5,
            GLFW_KEY_KP_6,
            GLFW_KEY_KP_7,
            GLFW_KEY_KP_8,
            GLFW_KEY_KP_9,
            GLFW_KEY_KP_DECIMAL,
            GLFW_KEY_KP_DIVIDE,
            GLFW_KEY_KP_MULTIPLY,
            GLFW_KEY_KP_SUBTRACT,
            GLFW_KEY_KP_ADD,
            GLFW_KEY_KP_ENTER,
            GLFW_KEY_KP_EQUAL,
            GLFW_KEY_LEFT_SHIFT,
            GLFW_KEY_LEFT_CONTROL,
            GLFW_KEY_LEFT_ALT,
            GLFW_KEY_LEFT_SUPER,
            GLFW_KEY_RIGHT_SHIFT,
            GLFW_KEY_RIGHT_CONTROL,
            GLFW_KEY_RIGHT_ALT,
            GLFW_KEY_RIGHT_SUPER,
            GLFW_KEY_MENU
    );

    // Map for key code to name lookup
    private static final Map<Integer, String> KEY_NAMES = new HashMap<>();

    static {
        KEY_NAMES.put(GLFW_KEY_SPACE, "Space");
        KEY_NAMES.put(GLFW_KEY_APOSTROPHE, "'");
        KEY_NAMES.put(GLFW_KEY_COMMA, ",");
        KEY_NAMES.put(GLFW_KEY_MINUS, "-");
        KEY_NAMES.put(GLFW_KEY_PERIOD, ".");
        KEY_NAMES.put(GLFW_KEY_SLASH, "/");
        KEY_NAMES.put(GLFW_KEY_0, "0");
        KEY_NAMES.put(GLFW_KEY_1, "1");
        KEY_NAMES.put(GLFW_KEY_2, "2");
        KEY_NAMES.put(GLFW_KEY_3, "3");
        KEY_NAMES.put(GLFW_KEY_4, "4");
        KEY_NAMES.put(GLFW_KEY_5, "5");
        KEY_NAMES.put(GLFW_KEY_6, "6");
        KEY_NAMES.put(GLFW_KEY_7, "7");
        KEY_NAMES.put(GLFW_KEY_8, "8");
        KEY_NAMES.put(GLFW_KEY_9, "9");
        KEY_NAMES.put(GLFW_KEY_SEMICOLON, ";");
        KEY_NAMES.put(GLFW_KEY_EQUAL, "=");
        KEY_NAMES.put(GLFW_KEY_A, "A");
        KEY_NAMES.put(GLFW_KEY_B, "B");
        KEY_NAMES.put(GLFW_KEY_C, "C");
        KEY_NAMES.put(GLFW_KEY_D, "D");
        KEY_NAMES.put(GLFW_KEY_E, "E");
        KEY_NAMES.put(GLFW_KEY_F, "F");
        KEY_NAMES.put(GLFW_KEY_G, "G");
        KEY_NAMES.put(GLFW_KEY_H, "H");
        KEY_NAMES.put(GLFW_KEY_I, "I");
        KEY_NAMES.put(GLFW_KEY_J, "J");
        KEY_NAMES.put(GLFW_KEY_K, "K");
        KEY_NAMES.put(GLFW_KEY_L, "L");
        KEY_NAMES.put(GLFW_KEY_M, "M");
        KEY_NAMES.put(GLFW_KEY_N, "N");
        KEY_NAMES.put(GLFW_KEY_O, "O");
        KEY_NAMES.put(GLFW_KEY_P, "P");
        KEY_NAMES.put(GLFW_KEY_Q, "Q");
        KEY_NAMES.put(GLFW_KEY_R, "R");
        KEY_NAMES.put(GLFW_KEY_S, "S");
        KEY_NAMES.put(GLFW_KEY_T, "T");
        KEY_NAMES.put(GLFW_KEY_U, "U");
        KEY_NAMES.put(GLFW_KEY_V, "V");
        KEY_NAMES.put(GLFW_KEY_W, "W");
        KEY_NAMES.put(GLFW_KEY_X, "X");
        KEY_NAMES.put(GLFW_KEY_Y, "Y");
        KEY_NAMES.put(GLFW_KEY_Z, "Z");
        KEY_NAMES.put(GLFW_KEY_LEFT_BRACKET, "{");
        KEY_NAMES.put(GLFW_KEY_BACKSLASH, "\\");
        KEY_NAMES.put(GLFW_KEY_RIGHT_BRACKET, "}");
        KEY_NAMES.put(GLFW_KEY_GRAVE_ACCENT, "`");
        KEY_NAMES.put(GLFW_KEY_WORLD_1, "WORLD_1");
        KEY_NAMES.put(GLFW_KEY_WORLD_2, "WORLD_2");
        KEY_NAMES.put(GLFW_KEY_ESCAPE, "ESCAPE");
        KEY_NAMES.put(GLFW_KEY_ENTER, "ENTER");
        KEY_NAMES.put(GLFW_KEY_TAB, "TAB");
        KEY_NAMES.put(GLFW_KEY_BACKSPACE, "BACKSPACE");
        KEY_NAMES.put(GLFW_KEY_INSERT, "INSERT");
        KEY_NAMES.put(GLFW_KEY_DELETE, "DELETE");
        KEY_NAMES.put(GLFW_KEY_RIGHT, "RIGHT");
        KEY_NAMES.put(GLFW_KEY_LEFT, "LEFT");
        KEY_NAMES.put(GLFW_KEY_DOWN, "DOWN");
        KEY_NAMES.put(GLFW_KEY_UP, "UP");
        KEY_NAMES.put(GLFW_KEY_PAGE_UP, "PAGE UP");
        KEY_NAMES.put(GLFW_KEY_PAGE_DOWN, "PAGE DOWN");
        KEY_NAMES.put(GLFW_KEY_HOME, "HOME");
        KEY_NAMES.put(GLFW_KEY_END, "END");
        KEY_NAMES.put(GLFW_KEY_CAPS_LOCK, "CAPS LOCK");
        KEY_NAMES.put(GLFW_KEY_SCROLL_LOCK, "SCROLL LOCK");
        KEY_NAMES.put(GLFW_KEY_NUM_LOCK, "NUM LOCK");
        KEY_NAMES.put(GLFW_KEY_PRINT_SCREEN, "PRINT SCREEN");
        KEY_NAMES.put(GLFW_KEY_PAUSE, "PAUSE");
        KEY_NAMES.put(GLFW_KEY_F1, "F1");
        KEY_NAMES.put(GLFW_KEY_F2, "F2");
        KEY_NAMES.put(GLFW_KEY_F3, "F3");
        KEY_NAMES.put(GLFW_KEY_F4, "F4");
        KEY_NAMES.put(GLFW_KEY_F5, "F5");
        KEY_NAMES.put(GLFW_KEY_F6, "F6");
        KEY_NAMES.put(GLFW_KEY_F7, "F7");
        KEY_NAMES.put(GLFW_KEY_F8, "F8");
        KEY_NAMES.put(GLFW_KEY_F9, "F9");
        KEY_NAMES.put(GLFW_KEY_F10, "F10");
        KEY_NAMES.put(GLFW_KEY_F11, "F11");
        KEY_NAMES.put(GLFW_KEY_F12, "F12");
        KEY_NAMES.put(GLFW_KEY_F13, "F13");
        KEY_NAMES.put(GLFW_KEY_F14, "F14");
        KEY_NAMES.put(GLFW_KEY_F15, "F15");
        KEY_NAMES.put(GLFW_KEY_F16, "F16");
        KEY_NAMES.put(GLFW_KEY_F17, "F17");
        KEY_NAMES.put(GLFW_KEY_F18, "F18");
        KEY_NAMES.put(GLFW_KEY_F19, "F19");
        KEY_NAMES.put(GLFW_KEY_F20, "F20");
        KEY_NAMES.put(GLFW_KEY_F21, "F21");
        KEY_NAMES.put(GLFW_KEY_F22, "F22");
        KEY_NAMES.put(GLFW_KEY_F23, "F23");
        KEY_NAMES.put(GLFW_KEY_F24, "F24");
        KEY_NAMES.put(GLFW_KEY_F25, "F25");
        KEY_NAMES.put(GLFW_KEY_KP_0, "KP 0");
        KEY_NAMES.put(GLFW_KEY_KP_1, "KP 1");
        KEY_NAMES.put(GLFW_KEY_KP_2, "KP 2");
        KEY_NAMES.put(GLFW_KEY_KP_3, "KP 3");
        KEY_NAMES.put(GLFW_KEY_KP_4, "KP 4");
        KEY_NAMES.put(GLFW_KEY_KP_5, "KP 5");
        KEY_NAMES.put(GLFW_KEY_KP_6, "KP 6");
        KEY_NAMES.put(GLFW_KEY_KP_7, "KP 7");
        KEY_NAMES.put(GLFW_KEY_KP_8, "KP 8");
        KEY_NAMES.put(GLFW_KEY_KP_9, "KP 9");
        KEY_NAMES.put(GLFW_KEY_KP_DECIMAL, "KP DECIMAL");
        KEY_NAMES.put(GLFW_KEY_KP_DIVIDE, "KP DIVIDE");
        KEY_NAMES.put(GLFW_KEY_KP_MULTIPLY, "KP MULTIPLY");
        KEY_NAMES.put(GLFW_KEY_KP_SUBTRACT, "KP SUBTRACT");
        KEY_NAMES.put(GLFW_KEY_KP_ADD, "KP ADD");
        KEY_NAMES.put(GLFW_KEY_KP_ENTER, "KP ENTER");
        KEY_NAMES.put(GLFW_KEY_KP_EQUAL, "KP EQUAL");
        KEY_NAMES.put(GLFW_KEY_LEFT_SHIFT, "LEFT SHIFT");
        KEY_NAMES.put(GLFW_KEY_LEFT_CONTROL, "LEFT CONTROL");
        KEY_NAMES.put(GLFW_KEY_LEFT_ALT, "LEFT ALT");
        KEY_NAMES.put(GLFW_KEY_LEFT_SUPER, "LEFT SUPER");
        KEY_NAMES.put(GLFW_KEY_RIGHT_SHIFT, "RIGHT SHIFT");
        KEY_NAMES.put(GLFW_KEY_RIGHT_CONTROL, "RIGHT CONTROL");
        KEY_NAMES.put(GLFW_KEY_RIGHT_ALT, "RIGHT ALT");
        KEY_NAMES.put(GLFW_KEY_RIGHT_SUPER, "RIGHT SUPER");
        KEY_NAMES.put(GLFW_KEY_MENU, "MENU");
    }


    public static String getKeyName(int keyCode) {
        if (keyCode == -5) return "...";
        if (keyCode == -1) return "None";

        return KEY_NAMES.getOrDefault(keyCode, "UNKNOWN KEY");
    }
}