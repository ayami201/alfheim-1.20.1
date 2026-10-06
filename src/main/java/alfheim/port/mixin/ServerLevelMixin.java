package alfheim.port.mixin;

import alfheim.port.legacy.Explosions1710;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Взрыв, отменённый в {@code ExplosionEvent.Start}, сервер не показывает игрокам, как Forge 1.7.10: пакета взрыва нет
 * ({@link Explosions1710}). Не врезка автора — прослойка порта (MAPPING.md, «Прослойка {@code alfheim.port.legacy}»)
 */
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

	@Inject(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Explosion;interactsWithBlocks()Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void alfheim$canceledExplosion(Entity entity, DamageSource source, ExplosionDamageCalculator calculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction interaction, CallbackInfoReturnable<Explosion> cir, Explosion explosion) {
		if (Explosions1710.wasCanceled(explosion)) cir.setReturnValue(explosion);
	}
}
