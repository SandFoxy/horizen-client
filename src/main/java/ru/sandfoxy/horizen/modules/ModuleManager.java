package ru.sandfoxy.horizen.modules;

import ru.sandfoxy.horizen.modules.core.Module;

import ru.sandfoxy.horizen.modules.features.combat.*;
import ru.sandfoxy.horizen.modules.features.hack.ChatHandler;
import ru.sandfoxy.horizen.modules.features.hack.FriendList;
import ru.sandfoxy.horizen.modules.features.hack.GPS;
import ru.sandfoxy.horizen.modules.features.hack.KeybindPreview;
import ru.sandfoxy.horizen.modules.features.player.*;
import ru.sandfoxy.horizen.modules.features.render.*;
import ru.sandfoxy.horizen.modules.features.render.FogCustom;
import ru.sandfoxy.horizen.modules.features.misc.*;
import ru.sandfoxy.horizen.modules.features.misc.ChestStealer;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();

    public static void init() {
        //Combat
        modules.add(new KillAura());
        modules.add(new Triggerbot());
        modules.add(new Hitbox());
        modules.add(new LeaveOnLoot());
        modules.add(new TridentAimbot());
        modules.add(new BowAimbot());

        //Player
        modules.add(new ShowSaturation());
        modules.add(new AntiAFK());
        modules.add(new NoClip());
        modules.add(new AutoSprint());
        modules.add(new AutoTool());
        modules.add(new GhostHand());
        modules.add(new Viewmodel());
        modules.add(new FreeCam());
        modules.add(new NoDelay());

        //Render
        modules.add(new ESP());
        //modules.add(new Chams());
        modules.add(new Arrows());
        modules.add(new Fullbright());
        modules.add(new EntityFullbright());
        modules.add(new SoundESP());
        modules.add(new ItemESP());
        modules.add(new Hud());
        modules.add(new NoRender());
        //modules.add(new Glow());
        modules.add(new BlockESP());
        modules.add(new PearlTrajectory());
        modules.add(new Ambience());
        modules.add(new FogCustom());

        //Misc
        modules.add(new MiddleClickFriend());
        modules.add(new ElytraHelper());
        modules.add(new MaceHelper());
        modules.add(new FastAnchor());
        modules.add(new Pet());
        modules.add(new NFD());
        modules.add(new ChestStealer());
        modules.add(new GPS());
        modules.add(new ServerCrasher());
        modules.add(new FakeDir());
        modules.add(new Unhook());

        //Internals
        modules.add(new FriendList());
        modules.add(new ChatHandler());
        ModuleManager.getByName("ChatHandler").toggle(true);

        modules.add(new KeybindPreview());
    }

    public static List<Module> getModules() {
        return modules;
    }

    public static Module getByName(String name) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
