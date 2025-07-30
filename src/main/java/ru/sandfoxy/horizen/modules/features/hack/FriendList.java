package ru.sandfoxy.horizen.modules.features.hack;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.SaveableList;

public class FriendList extends Module {

    public static SaveableList friendList = new SaveableList("Friends");

    public FriendList() {
        super("FriendList", CATEGORY.SETTINGS, "Hmmm. You shouldn't see this...");
        this.addSetting(friendList);
    }

    public static boolean isFriend(PlayerEntity player){
        return friendList.getList().contains(player.getName().toString());
    }
}
