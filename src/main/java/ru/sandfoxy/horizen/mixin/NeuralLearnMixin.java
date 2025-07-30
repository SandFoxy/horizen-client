//package ru.sandfoxy.horizen.mixin;
//
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.network.OtherClientPlayerEntity;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.damage.DamageSource;
//import net.minecraft.entity.damage.DamageTypes;
//import net.minecraft.util.math.Vec3d;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import ru.sandfoxy.horizen.modules.ModuleManager;
//import ru.sandfoxy.horizen.modules.features.combat.DatasetRecorder;
//import ru.sandfoxy.horizen.utils.math.RotationUtils;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.List;
//import java.util.Locale;
//
//import static net.minecraft.util.math.MathHelper.wrapDegrees;
//
//@Mixin(OtherClientPlayerEntity.class)
//public class NeuralLearnMixin {
//    private static final MinecraftClient mc = MinecraftClient.getInstance();
//
//    @Inject(method = "clientDamage", at = @At("HEAD"))
//    private void hurt(DamageSource source, CallbackInfoReturnable<Boolean> cir) throws IOException {
//        if (!source.isOf(DamageTypes.PLAYER_ATTACK)) return;
//
//        if (mc.player == null || mc.cameraEntity == null) return;
//        if (true) return;
//
//        Entity entity = (Entity) (Object) this;
//
//        float yaw = wrapDegrees(mc.player.getYaw());
//        float pitch = mc.cameraEntity.getPitch();
//
//        Vec3d entPosition = entity.getPos();
//        Vec3d localPosition = mc.player.getPos();
//
//        float distance = mc.player.distanceTo(entity);
//        Vec3d delta = entPosition.subtract(localPosition);
//        Vec3d relPosition = delta.normalize();
//
//        Vec3d targetVelocity = entity.getVelocity();
//        Vec3d localVelocity = mc.player.getVelocity();
//
//        String line = String.format(Locale.US, "%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n",
//                yaw, pitch,
//                entPosition.x, entPosition.y, entPosition.z,
//                localPosition.x, localPosition.y, localPosition.z,
//                relPosition.x, relPosition.y, relPosition.z,
//                targetVelocity.x, targetVelocity.y, targetVelocity.z,
//                localVelocity.x, localVelocity.y, localVelocity.z,
//                distance
//        );
//
//        Path path = Paths.get("rotations_log.csv");
//        Files.write(path, line.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
//        int lineCount = 0;
//        if (Files.exists(path)) {
//            List<String> lines = Files.readAllLines(path);
//            lineCount = lines.size();
//        }
//        DatasetRecorder.wasRecordingStarted = true;
//        DatasetRecorder.linesRecorded = lineCount;
//    }
//}
