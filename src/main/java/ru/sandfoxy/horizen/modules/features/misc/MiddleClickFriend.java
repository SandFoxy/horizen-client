package ru.sandfoxy.horizen.modules.features.misc;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.SaveableList;
import ru.sandfoxy.horizen.modules.features.hack.FriendList;

public class MiddleClickFriend extends Module {
    private MinecraftClient mc = MinecraftClient.getInstance();
    private boolean mouseLocked;
    public MiddleClickFriend() {
        super("MiddleClickFriend", CATEGORY.MISC, "MiddleClickFriend. Just what it says.");
    }

    @Override
    public void startTick() {
        if (mc.world == null || mc.player == null || mc.crosshairTarget == null || !mc.isWindowFocused()) return;

        long windowHandle = mc.getWindow().getHandle();
        if (GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) != GLFW.GLFW_PRESS){
            mouseLocked = false;
            return;
        }
        if (mouseLocked) return;

        mouseLocked = true;
        HitResult hit = mc.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity target = entityHit.getEntity();

            if (target != mc.player && target.distanceTo(mc.player) <= 1.5f && target instanceof PlayerEntity) {
                String playerName = target.getName().getString();

                if (!FriendList.friendList.getList().contains(playerName)) FriendList.friendList.add(playerName);
                else FriendList.friendList.remove(playerName);
                System.out.println(playerName);
            }
        }
    }


}
