package ru.sandfoxy.horizen.modules.features.misc;

import net.minecraft.client.MinecraftClient;
import ru.sandfoxy.horizen.ModEntryPoint;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.core.Module;

import java.util.Objects;

public class Unhook extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public static boolean cheatUnloaded = false;

    public Unhook() {
        super("Unhook", CATEGORY.MISC, "Unload the client.");
    }


    @Override
    public void onEnable(){
        for (Module modules : ModuleManager.getModules()){
            if (Objects.equals(modules.getName(), "FakeDir")) continue;;
            if (modules.isEnabledRaw()) modules.toggle(false);
        }

        cheatUnloaded = true;
        ModEntryPoint.menuOpened = false;
    }

    @Override
    public void onDisable(){
        cheatUnloaded = true;
        ModEntryPoint.menuOpened = false;
    }
}
