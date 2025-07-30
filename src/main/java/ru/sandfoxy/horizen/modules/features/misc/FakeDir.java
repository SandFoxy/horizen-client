package ru.sandfoxy.horizen.modules.features.misc;

import net.minecraft.client.MinecraftClient;
import org.w3c.dom.Text;
import ru.sandfoxy.horizen.ModEntryPoint;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;
import ru.sandfoxy.horizen.modules.core.type.TextInput;

public class FakeDir extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public static final Checkbox appdataDir = new Checkbox(true, "Auto Dir (.minecraft)");
    public static final TextInput directory = new TextInput("C:\\Horizen\\Game");
    public static final Checkbox copyLogs = new Checkbox(true, "Copy Logs");
    public static final Checkbox copyResources = new Checkbox(true, "Copy RP's");

    public FakeDir() {
        super("FakeDir", CATEGORY.MISC, "Will swap your game directory for any other.\nCan copy logs and resource packs.");

        this.addSetting(appdataDir);
        this.addSetting(directory);
        this.addSetting(copyLogs);
        this.addSetting(copyResources);

        directory.visibleIf(() -> !appdataDir.getValue());
    }
}
