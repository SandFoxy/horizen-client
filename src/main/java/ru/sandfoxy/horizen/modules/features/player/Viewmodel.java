package ru.sandfoxy.horizen.modules.features.player;

import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;
import ru.sandfoxy.horizen.modules.core.type.ComboBox;
import ru.sandfoxy.horizen.modules.core.type.Slider;

import java.util.List;

public class Viewmodel extends Module {

    // Main hand settings
    public static final Slider mainPositionX = new Slider(0.0F, -1f, 1f, "Main Pos X", Slider.SliderType.FLOAT);
    public static final Slider mainPositionY = new Slider(0.0F, -1f, 1f, "Main Pos Y", Slider.SliderType.FLOAT);
    public static final Slider mainPositionZ = new Slider(0.0F, -30.0F, 30.0F, "Main Pos Z", Slider.SliderType.FLOAT);
    public static final Slider mainRotationX = new Slider(0.0F, -70.0F, 70.0F, "Main Rot X", Slider.SliderType.FLOAT);
    public static final Slider mainRotationY = new Slider(0.0F, -60.0F, 60.0F, "Main Rot Y", Slider.SliderType.FLOAT);
    public static final Slider mainRotationZ = new Slider(0.0F, -60.0F, 60.0F, "Main Rot Z", Slider.SliderType.FLOAT);

    // Off hand settings
    public static final Slider offPositionX = new Slider(0.0F, -1f, 1f, "Off Pos X", Slider.SliderType.FLOAT);
    public static final Slider offPositionY = new Slider(0.0F, -1f, 1f, "Off Pos Y", Slider.SliderType.FLOAT);
    public static final Slider offPositionZ = new Slider(0.0F, -30.0F, 30.0F, "Off Pos Z", Slider.SliderType.FLOAT);
    public static final Slider offRotationX = new Slider(0.0F, -70.0F, 70.0F, "Off Rot X", Slider.SliderType.FLOAT);
    public static final Slider offRotationY = new Slider(0.0F, -60.0F, 60.0F, "Off Rot Y", Slider.SliderType.FLOAT);
    public static final Slider offRotationZ = new Slider(0.0F, -60.0F, 60.0F, "Off Rot Z", Slider.SliderType.FLOAT);
    
    // Animation and scale settings
    public static final Slider handSpeedSwing = new Slider(4, 0, 5, "Swing Speed", Slider.SliderType.INT);
    public static final Slider mainHandScale = new Slider(1.0F, 0.1F, 5.0F, "Main Scale", Slider.SliderType.FLOAT);
    public static final Slider offHandScale = new Slider(1.0F, 0.1F, 5.0F, "Off Scale", Slider.SliderType.FLOAT);
    
    // Toggle settings
    public static final Checkbox noFoodSwing = new Checkbox(false, "No Food Animation");
    public static final Checkbox noHandRender = new Checkbox(false, "Hide Hand");

    public Viewmodel() {
        super("View model", CATEGORY.PLAYER, "View model changer.");

        this.addSetting(mainPositionX);
        this.addSetting(mainPositionY);
        this.addSetting(mainPositionZ);
        this.addSetting(mainRotationX);
        this.addSetting(mainRotationY);
        this.addSetting(mainRotationZ);
        this.addSeparator();

        this.addSetting(offPositionX);
        this.addSetting(offPositionY);
        this.addSetting(offPositionZ);
        this.addSetting(offRotationX);
        this.addSetting(offRotationY);
        this.addSetting(offRotationZ);
        this.addSeparator();

        this.addSetting(handSpeedSwing);
        this.addSetting(mainHandScale);
        this.addSetting(offHandScale);
        this.addSeparator();

        this.addSetting(noFoodSwing);
        this.addSetting(noHandRender);
    }
}
