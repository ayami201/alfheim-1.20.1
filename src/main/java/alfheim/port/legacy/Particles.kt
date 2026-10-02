package alfheim.port.legacy

import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.level.Level

/**
 * Частица по имени 1.7.10 → тип 1.20.1 (SPEC, Р-4), только для частиц без параметров, у которых смысл скорости тот
 * же. Цветные (`reddust`, `mobSpell`, `mobSpellAmbient`) и частицы с id блока или предмета (`iconcrack_…`,
 * `blockcrack_…`, `blockdust_…`) переписываются на месте вызова: в 1.20.1 цвет и предмет задаются параметрами частицы.
 */
object LegacyParticles {

	private val types: Map<String, ParticleOptions> = mapOf(
		"angryVillager" to ParticleTypes.ANGRY_VILLAGER,
		"bubble" to ParticleTypes.BUBBLE,
		"cloud" to ParticleTypes.CLOUD,
		"crit" to ParticleTypes.CRIT,
		"dripLava" to ParticleTypes.DRIPPING_LAVA,
		"dripWater" to ParticleTypes.DRIPPING_WATER,
		"enchantmenttable" to ParticleTypes.ENCHANT,
		"explode" to ParticleTypes.POOF,
		"fireworksSpark" to ParticleTypes.FIREWORK,
		"flame" to ParticleTypes.FLAME,
		"happyVillager" to ParticleTypes.HAPPY_VILLAGER,
		"heart" to ParticleTypes.HEART,
		"hugeexplosion" to ParticleTypes.EXPLOSION_EMITTER,
		"instantSpell" to ParticleTypes.INSTANT_EFFECT,
		"largeexplode" to ParticleTypes.EXPLOSION,
		"largesmoke" to ParticleTypes.LARGE_SMOKE,
		"lava" to ParticleTypes.LAVA,
		"magicCrit" to ParticleTypes.ENCHANTED_HIT,
		"note" to ParticleTypes.NOTE, // цвет — скорость по x, как в 1.7.10
		"portal" to ParticleTypes.PORTAL,
		"slime" to ParticleTypes.ITEM_SLIME,
		"smoke" to ParticleTypes.SMOKE,
		"snowballpoof" to ParticleTypes.ITEM_SNOWBALL,
		"spell" to ParticleTypes.EFFECT,
		"splash" to ParticleTypes.SPLASH,
		"townaura" to ParticleTypes.MYCELIUM,
		"witchMagic" to ParticleTypes.WITCH,
	)

	operator fun get(name: String): ParticleOptions = types[name] ?: throw IllegalArgumentException("Particle $name of 1.7.10 has parameters in 1.20.1: port it at the call site (MAPPING.md)")
}

/** 1.7.10: на клиенте — частица, на сервере — ничего; так же `addParticle` 1.20.1 */
fun Level.spawnParticle(name: String, x: Double, y: Double, z: Double, motionX: Double, motionY: Double, motionZ: Double) =
	addParticle(LegacyParticles[name], x, y, z, motionX, motionY, motionZ)
