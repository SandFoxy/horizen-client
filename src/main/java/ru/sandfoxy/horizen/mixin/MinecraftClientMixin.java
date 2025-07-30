package ru.sandfoxy.horizen.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.hack.FriendList;
import ru.sandfoxy.horizen.modules.features.misc.FakeDir;
import ru.sandfoxy.horizen.modules.features.render.ESP;
import ru.sandfoxy.horizen.modules.features.render.ItemESP;

import java.nio.file.Path;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void outlineEntities(Entity entity, CallbackInfoReturnable<Boolean> ci) {
        if (entity instanceof PlayerEntity && ESP.playerGlow.getValue() && ModuleManager.getByName("ESP").isEnabled()) {
            ci.setReturnValue(true);
        }
        if (entity instanceof ItemEntity item && ItemESP.itemGlow.getValue() && ModuleManager.getByName("ItemESP").isEnabled()){
            ItemStack stack = item.getStack();
            Item i = stack.getItem();

            String category = ItemESP.getItemCategory(i);

            if (ItemESP.itemsFilter.getList().contains(category)){
                ci.setReturnValue(true);
            }
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        if (!ModuleManager.getByName("No Friend Damage").isEnabled()) return;

        if (MinecraftClient.getInstance().targetedEntity instanceof PlayerEntity targetPlayer) {
            if (FriendList.isFriend(targetPlayer)) {
                MinecraftClient.getInstance().player.swingHand(Hand.MAIN_HAND);
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "getResourcePackDir", at = @At("HEAD"), cancellable = true)
    private void getResourcePackDir(CallbackInfoReturnable<Path> cir) {
        if (!ModuleManager.getByName("FakeDir").isEnabled()) return;

        if (FakeDir.appdataDir.getValue()) {
            String path = System.getenv("APPDATA") + "\\.minecraft";
            cir.setReturnValue(Path.of(path));
        } else {
            cir.setReturnValue(Path.of(FakeDir.directory.getInput()));
        }
    }
}