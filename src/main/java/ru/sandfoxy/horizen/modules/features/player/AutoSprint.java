package ru.sandfoxy.horizen.modules.features.player;

import net.minecraft.client.MinecraftClient;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;

public class AutoSprint extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    private final Checkbox keepSprint = new Checkbox(true, "Keep Sprint");
    private final Checkbox ignoreHunger = new Checkbox(false, "Ignore Hunger");
    
    public AutoSprint() {
        super("AutoSprint", CATEGORY.PLAYER, "Automatic Sprint");

        this.addSetting(keepSprint);
        this.addSetting(ignoreHunger);
    }
    
    @Override
    public void startTick() {
        if (mc.player == null || mc.world == null) return;
        

        if (ModuleManager.getByName("FreeCam").isEnabledRaw()) return;

        if (shouldSprint()) {
            mc.options.sprintKey.setPressed(true);
        }
    }
    
    private boolean shouldSprint() {
        if (mc.player == null) return false;

        boolean isMovingForward = mc.options.forwardKey.isPressed();
        boolean hasEnoughHunger = ignoreHunger.getValue() || mc.player.getHungerManager().getFoodLevel() > 6;
        boolean canSprintNow = mc.player.isOnGround() || mc.player.isInFluid() || mc.player.isInLava();
        
        return isMovingForward && hasEnoughHunger && canSprintNow && !mc.player.isSneaking();
    }
    
    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.sprintKey.setPressed(false);
        }
    }

} 