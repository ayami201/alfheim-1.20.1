package alfheim.port.mixin;

import alfheim.port.hook.EffectHooks;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Врезки автора в {@code EntityLivingBase} (HOOKS.md): H-008 {@code isPotionActive}, H-009 {@code getActivePotionEffect}
 * — в 1.20.1 {@code hasEffect} и {@code getEffect}. Код врезок — {@link EffectHooks}
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
	private void alfheim$hasEffect(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(EffectHooks.INSTANCE.isPotionActive((LivingEntity) (Object) this, effect));
	}

	@Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
	private void alfheim$getEffect(MobEffect effect, CallbackInfoReturnable<MobEffectInstance> cir) {
		cir.setReturnValue(EffectHooks.INSTANCE.getActivePotionEffect((LivingEntity) (Object) this, effect));
	}
}
