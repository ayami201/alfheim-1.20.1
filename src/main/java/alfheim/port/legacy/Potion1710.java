package alfheim.port.legacy;

import alfheim.port.registry.LegacyRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.*;
import java.util.function.Consumer;

/**
 * {@code net.minecraft.potion.Potion} 1.7.10 (SPEC, Р-4; MAPPING.md, «Предметы, сущности, эффекты»).
 * <p>
 * В 1.7.10 зелье — {@code Potion} с числовым id из конфига; в 1.20.1 — {@code MobEffect} в реестре по имени. Зелье
 * автора наследует этот класс: id 1.7.10 остаётся (по нему автор ищет зелье — {@link #potionTypes}), методы 1.7.10
 * ({@code isReady}, {@code performEffect}, {@code applyAttributesModifiersToEntity}…) зовёт 1.20.1 вместо своих.
 * Имя в реестре — имя из {@code setPotionName} без приставки, в snake_case ({@code alfheim.potion.whiteWine} →
 * {@code alfheim:white_wine}); регистрирует {@link LegacyRegistration}.
 * <p>
 * Класс написан на Java, как {@code Potion} 1.7.10: переопределения автора объявляют параметры кто nullable, кто нет.
 */
public class Potion1710 extends MobEffect {

	/**
	 * {@code Potion.potionTypes} 1.7.10: зелье по id. Id 1–23 — зелья ванилы 1.7.10, зелья автора — свои id; остальные
	 * эффекты реестра 1.20.1 получают свободные id после регистрации ({@link #assignIds}), как зелья других модов в 1.7.10
	 */
	public static final MobEffect[] potionTypes = new MobEffect[256];

	private static final Map<MobEffect, Integer> ids = new HashMap<>();

	// Зелья ванилы под именами 1.7.10 (`Potion.regeneration` и др.); их id 1.7.10 — `MobEffect.id` прослойки
	public static final MobEffect moveSpeed = MobEffects.MOVEMENT_SPEED;
	public static final MobEffect moveSlowdown = MobEffects.MOVEMENT_SLOWDOWN;
	public static final MobEffect digSpeed = MobEffects.DIG_SPEED;
	public static final MobEffect digSlowdown = MobEffects.DIG_SLOWDOWN;
	public static final MobEffect damageBoost = MobEffects.DAMAGE_BOOST;
	public static final MobEffect heal = MobEffects.HEAL;
	public static final MobEffect harm = MobEffects.HARM;
	public static final MobEffect jump = MobEffects.JUMP;
	public static final MobEffect confusion = MobEffects.CONFUSION;
	public static final MobEffect regeneration = MobEffects.REGENERATION;
	public static final MobEffect resistance = MobEffects.DAMAGE_RESISTANCE;
	public static final MobEffect fireResistance = MobEffects.FIRE_RESISTANCE;
	public static final MobEffect waterBreathing = MobEffects.WATER_BREATHING;
	public static final MobEffect invisibility = MobEffects.INVISIBILITY;
	public static final MobEffect blindness = MobEffects.BLINDNESS;
	public static final MobEffect nightVision = MobEffects.NIGHT_VISION;
	public static final MobEffect hunger = MobEffects.HUNGER;
	public static final MobEffect weakness = MobEffects.WEAKNESS;
	public static final MobEffect poison = MobEffects.POISON;
	public static final MobEffect wither = MobEffects.WITHER;
	/** {@code healthBoost} */
	public static final MobEffect field_76434_w = MobEffects.HEALTH_BOOST;
	/** {@code absorption} */
	public static final MobEffect field_76444_x = MobEffects.ABSORPTION;
	/** {@code saturation} */
	public static final MobEffect field_76443_y = MobEffects.SATURATION;

	public final int id;

	private String name = "";

	private int statusIconIndex = -1;

	/** Лист иконок, из которого клиент рисует иконку зелья ({@link #getStatusIconIndex}); {@code null} — без иконки */
	public ResourceLocation iconSheet;

	public Potion1710(int id, boolean badEffect, int color) {
		super(badEffect ? MobEffectCategory.HARMFUL : MobEffectCategory.BENEFICIAL, color);
		if (potionTypes[id] != null) throw new IllegalArgumentException("Duplicate potion id! " + getClass() + " and " + potionTypes[id].getClass() + " Potion ID:" + id);
		this.id = id;
		put(id, this);
		LegacyRegistration.INSTANCE.effect(this);
	}

	private static void put(int id, MobEffect effect) {
		potionTypes[id] = effect;
		ids.put(effect, id);
	}

	static {
		MobEffect[] vanilla = {moveSpeed, moveSlowdown, digSpeed, digSlowdown, damageBoost, heal, harm, jump, confusion, regeneration, resistance, fireResistance, waterBreathing, invisibility, blindness, nightVision, hunger, weakness, poison, wither, field_76434_w, field_76444_x, field_76443_y};
		for (int i = 0; i < vanilla.length; i++) put(i + 1, vanilla[i]);
	}

	/** Свободные id — эффектам реестра без id, в порядке реестра (он один и тот же на сервере и клиенте) */
	public static void assignIds() {
		int next = 1;
		for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
			if (ids.containsKey(effect)) continue;
			while (next < potionTypes.length && potionTypes[next] != null) next++;
			if (next >= potionTypes.length) return;
			put(next, effect);
		}
	}

	/** id 1.7.10 эффекта: {@code potionID} у {@code PotionEffect}; -1 — эффект без id */
	public static int idOf(MobEffect effect) {
		return ids.getOrDefault(effect, -1);
	}

	/** {@code potionTypes[id]} или {@code null} */
	public static MobEffect byId(int id) {
		return id > 0 && id < potionTypes.length ? potionTypes[id] : null;
	}

	public Potion1710 setPotionName(String name) {
		this.name = name;
		return this;
	}

	/** {@code getName()} 1.7.10 — ключ перевода из {@code setPotionName} */
	public String getName() {
		return name;
	}

	public Potion1710 setIconIndex(int x, int y) {
		statusIconIndex = x + y * 8;
		return this;
	}

	public boolean hasStatusIcon() {
		return statusIconIndex >= 0;
	}

	/** Номер иконки в листе: столбец — {@code index % 8}, строка — {@code index / 8}, клетки 18×18 с высоты 198, как в 1.7.10 */
	public int getStatusIconIndex() {
		return statusIconIndex;
	}

	public boolean isBadEffect() {
		return getCategory() == MobEffectCategory.HARMFUL;
	}

	/** {@code isReady(duration, amplifier)} 1.7.10: работает ли {@link #performEffect} в этот тик */
	public boolean isReady(int duration, int amplifier) {
		return super.isDurationEffectTick(duration, amplifier);
	}

	@Override
	public final boolean isDurationEffectTick(int duration, int amplifier) {
		return isReady(duration, amplifier);
	}

	/** {@code performEffect(entity, amplifier)} 1.7.10 */
	public void performEffect(LivingEntity entity, int amplifier) {
		super.applyEffectTick(entity, amplifier);
	}

	@Override
	public final void applyEffectTick(LivingEntity entity, int amplifier) {
		performEffect(entity, amplifier);
	}

	/** {@code applyAttributesModifiersToEntity} 1.7.10: эффект наложен (на сервере) */
	public void applyAttributesModifiersToEntity(LivingEntity entity, AttributeMap attributes, int amplifier) {
		super.addAttributeModifiers(entity, attributes, amplifier);
	}

	@Override
	public final void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
		applyAttributesModifiersToEntity(entity, attributes, amplifier);
	}

	/** {@code removeAttributesModifiersFromEntity} 1.7.10: эффект снят или закончился (на сервере) */
	public void removeAttributesModifiersFromEntity(LivingEntity entity, AttributeMap attributes, int amplifier) {
		super.removeAttributeModifiers(entity, attributes, amplifier);
	}

	@Override
	public final void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
		removeAttributesModifiersFromEntity(entity, attributes, amplifier);
	}

	/** {@code func_111184_a(attribute, uuid, amount, operation)} 1.7.10: модификатор атрибута на время эффекта */
	public Potion1710 func_111184_a(Attribute attribute, String uuid, double amount, int operation) {
		addAttributeModifier(attribute, uuid, amount, AttributeModifier.Operation.fromValue(operation));
		return this;
	}

	/** {@code func_111183_a(amplifier, modifier)} 1.7.10: величина модификатора при этой силе эффекта */
	public double func_111183_a(int amplifier, AttributeModifier modifier) {
		return super.getAttributeModifierValue(amplifier, modifier);
	}

	@Override
	public final double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
		return func_111183_a(amplifier, modifier);
	}

	/** {@code func_111186_k()} 1.7.10: модификаторы атрибутов эффекта */
	public Map<Attribute, AttributeModifier> func_111186_k() {
		return getAttributeModifiers();
	}

	/**
	 * Иконка 1.7.10 из листа {@link #iconSheet} — рисует клиент ({@code alfheim.port.client.LegacyEffectIcons}). Forge зовёт этот метод
	 * только на клиенте и ещё в конструкторе {@code MobEffect}: иконка читает поля зелья, когда её рисуют
	 */
	@Override
	public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
		consumer.accept(alfheim.port.client.LegacyEffectIcons.INSTANCE.extensions(this));
	}
}
