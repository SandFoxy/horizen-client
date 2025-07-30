package ru.sandfoxy.horizen.imgui.utils;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.type.ImInt;

import java.util.List;

public class StyleVars {
    public static void SetupStyles(){
        final ImGuiStyle style = ImGui.getStyle();

        //Colors
        style.setColor(ImGuiCol.Text, ImGui.getColorU32(1.00f, 1.00f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.TextDisabled, ImGui.getColorU32(0.59f, 0.59f, 0.59f, 1.00f));
        style.setColor(ImGuiCol.WindowBg, ImGui.getColorU32(0.05f, 0.05f, 0.07f, 1.00f));
        style.setColor(ImGuiCol.ChildBg, ImGui.getColorU32(0.00f, 0.00f, 0.00f, 0.00f));
        style.setColor(ImGuiCol.PopupBg, ImGui.getColorU32(0.08f, 0.08f, 0.08f, 0.94f));
        style.setColor(ImGuiCol.Border, ImGui.getColorU32(0.16f, 0.18f, 0.21f, 1.00f));
        style.setColor(ImGuiCol.BorderShadow, ImGui.getColorU32(0.00f, 0.00f, 0.00f, 0.00f));
        style.setColor(ImGuiCol.FrameBg, ImGui.getColorU32(0.09f, 0.09f, 0.13f, 1.00f));
        style.setColor(ImGuiCol.FrameBgHovered, ImGui.getColorU32(0.09f, 0.09f, 0.13f, 1.00f));
        style.setColor(ImGuiCol.FrameBgActive, ImGui.getColorU32(0.09f, 0.09f, 0.13f, 1.00f));
        style.setColor(ImGuiCol.TitleBg, ImGui.getColorU32(0.09f, 0.09f, 0.13f, 1.00f));
        style.setColor(ImGuiCol.TitleBgActive, ImGui.getColorU32(0.09f, 0.09f, 0.13f, 1.00f));
        style.setColor(ImGuiCol.TitleBgCollapsed, ImGui.getColorU32(0.00f, 0.00f, 0.00f, 0.51f));
        style.setColor(ImGuiCol.MenuBarBg, ImGui.getColorU32(0.14f, 0.14f, 0.14f, 1.00f));
        style.setColor(ImGuiCol.ScrollbarBg, ImGui.getColorU32(0.02f, 0.02f, 0.02f, 0.53f));
        style.setColor(ImGuiCol.ScrollbarGrab, ImGui.getColorU32(0.31f, 0.31f, 0.31f, 1.00f));
        style.setColor(ImGuiCol.ScrollbarGrabHovered, ImGui.getColorU32(0.41f, 0.41f, 0.41f, 1.00f));
        style.setColor(ImGuiCol.ScrollbarGrabActive, ImGui.getColorU32(0.51f, 0.51f, 0.51f, 1.00f));
        style.setColor(ImGuiCol.CheckMark, ImGui.getColorU32(1.00f, 1.00f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.SliderGrab, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.SliderGrabActive, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.Button, ImGui.getColorU32(0.30f, 0.03f, 0.62f, 1.00f));
        style.setColor(ImGuiCol.ButtonHovered, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.ButtonActive, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.Header, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.HeaderHovered, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.HeaderActive, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.Separator, ImGui.getColorU32(0.43f, 0.43f, 0.50f, 0.50f));
        style.setColor(ImGuiCol.SeparatorHovered, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.SeparatorActive, ImGui.getColorU32(0.40f, 0.08f, 0.72f, 1.00f));
        style.setColor(ImGuiCol.ResizeGrip, ImGui.getColorU32(0.30f, 0.03f, 0.62f, 0.54f));
        style.setColor(ImGuiCol.ResizeGripHovered, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.ResizeGripActive, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.Tab, ImGui.getColorU32(0.30f, 0.03f, 0.62f, 0.54f));
        style.setColor(ImGuiCol.TabHovered, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.TabActive, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.TabUnfocused, ImGui.getColorU32(0.07f, 0.10f, 0.15f, 0.97f));
        style.setColor(ImGuiCol.TabUnfocusedActive, ImGui.getColorU32(0.30f, 0.13f, 0.41f, 0.54f));
        style.setColor(ImGuiCol.DockingPreview, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.DockingEmptyBg, ImGui.getColorU32(0.20f, 0.20f, 0.20f, 1.00f));
        style.setColor(ImGuiCol.PlotLines, ImGui.getColorU32(0.61f, 0.61f, 0.61f, 1.00f));
        style.setColor(ImGuiCol.PlotLinesHovered, ImGui.getColorU32(1.00f, 0.43f, 0.35f, 1.00f));
        style.setColor(ImGuiCol.PlotHistogram, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.PlotHistogramHovered, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.TableHeaderBg, ImGui.getColorU32(0.19f, 0.19f, 0.20f, 1.00f));
        style.setColor(ImGuiCol.TableBorderStrong, ImGui.getColorU32(0.31f, 0.31f, 0.35f, 1.00f));
        style.setColor(ImGuiCol.TableBorderLight, ImGui.getColorU32(0.23f, 0.23f, 0.25f, 1.00f));
        style.setColor(ImGuiCol.TableRowBg, ImGui.getColorU32(0.00f, 0.00f, 0.00f, 0.00f));
        style.setColor(ImGuiCol.TableRowBgAlt, ImGui.getColorU32(1.00f, 1.00f, 1.00f, 0.06f));
        style.setColor(ImGuiCol.TextSelectedBg, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.DragDropTarget, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.NavHighlight, ImGui.getColorU32(0.49f, 0.06f, 0.93f, 1.00f));
        style.setColor(ImGuiCol.NavWindowingHighlight, ImGui.getColorU32(1.00f, 1.00f, 1.00f, 0.70f));
        style.setColor(ImGuiCol.NavWindowingDimBg, ImGui.getColorU32(0.80f, 0.80f, 0.80f, 0.20f));
        style.setColor(ImGuiCol.ModalWindowDimBg, ImGui.getColorU32(0.80f, 0.80f, 0.80f, 0.35f));

        //Vars
        style.setWindowPadding(8, 8);
        style.setFramePadding(4, 3);
        style.setCellPadding(4, 2);
        style.setItemSpacing(8, 4);
        style.setItemInnerSpacing(4, 4);
        style.setTouchExtraPadding(0, 0);
        style.setIndentSpacing(21);
        style.setScrollbarSize(14);
        style.setGrabMinSize(10);

        style.setWindowBorderSize(0);
        style.setChildBorderSize(1);
        style.setPopupBorderSize(1);
        style.setFrameBorderSize(1);
        style.setTabBorderSize(0);

        style.setWindowRounding(5);
        style.setChildRounding(10);
        style.setFrameRounding(5);
        style.setPopupRounding(5);
        style.setScrollbarRounding(5);
        style.setGrabRounding(12);
        style.setTabRounding(12);

        style.setWindowTitleAlign(0.5f, 0.5f);
        style.setButtonTextAlign(0.5f, 0.5f);
        style.setSelectableTextAlign(0.0f, 0.0f);
        style.setDisplaySafeAreaPadding(3, 3);
    }

    private static ImInt style_index = new ImInt();
    public static void ShowStyleSelector(){
        if (ImGui.combo("##MenuStyle", style_index, List.of("Horizen","Blue","Light","Old School").toArray(new String[0]))){
            switch (style_index.get())
            {
                case 0: ImGui.styleColorsDark(); SetupStyles(); break;
                case 1: ImGui.styleColorsDark(); break;
                case 2: ImGui.styleColorsLight(); break;
                case 3: ImGui.styleColorsClassic(); break;
            }
        }
    }
}
