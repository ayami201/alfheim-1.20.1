package alfheim.port.legacy.botania

import alfheim.port.legacy.*
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.LivingEntity

/**
 * `vazkii.botania.common.brew.potion.PotionMod` r1.8-249 — базовый класс зелий Botania 1.7.10, его наследуют зелья
 * автора (`PotionAlfheim`); в Botania 1.20.1 его нет. Лист иконок Botania 1.7.10 (`textures/gui/potions.png`) в
 * Botania 1.20.1 не сохранился: лист зелий автора — свой (`PotionAlfheim`)
 */
open class PotionMod(id: Int, name: String, badEffect: Boolean, color: Int, iconIndex: Int): Potion1710(id, badEffect, color) {
	
	init {
		setPotionName("botania.potion.$name")
		setIconIndex(iconIndex % 8, iconIndex / 8)
	}
	
	fun hasEffect(entity: LivingEntity) = hasEffect(entity, this)
	
	fun hasEffect(entity: LivingEntity, potion: MobEffect) = entity.getActivePotionEffect(potion) != null
}
