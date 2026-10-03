package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); заклинание, его урон и частицы (КТ-7) закомментированы вместе со своими строками
import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
//import alexsocol.asjlib.math.Vector3
//import alfheim.common.core.util.DamageSourceSpell
//import alfheim.common.spell.darkness.SpellSacrifice
//import net.minecraft.entity.boss.IBossDisplayData
//import vazkii.botania.common.Botania

object PotionSacrifice: PotionAlfheim(AlfheimConfigHandler.potionIDSacrifice, "sacrifice", false, 0) {
	
	var timeQueued: Int = 0
	
	override fun isReady(time: Int, mod: Int): Boolean {
		timeQueued = time
		return AlfheimConfigHandler.enableMMO && timeQueued <= 32
	}
	
	override fun performEffect(target: EntityLivingBase, mod: Int) {
		if (!AlfheimConfigHandler.enableMMO) return
		if (timeQueued == 32)
			for (i in 0..7)
				target.worldObj.playSoundEffect(target.posX, target.posY, target.posZ, ModInfo.MODID + ":redexp", 10000f, 0.8f + target.worldObj.rand.nextFloat() * 0.2f)
		else
			particles(target, 32 - timeQueued)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, attributes: BaseAttributeMap, ampl: Int) {
		super.removeAttributesModifiersFromEntity(target, attributes, ampl)
		if (!AlfheimConfigHandler.enableMMO) return
		// PORT: КТ-7 — урон заклинания «Жертва» по всем вокруг (SpellSacrifice, DamageSourceSpell)
		/*
		val l = getEntitiesWithinAABB(target.worldObj, EntityLivingBase::class.java, target.boundingBox.copy().expand(SpellSacrifice.radius))
		for (e in l) {
			if (e is IBossDisplayData && !AlfheimConfigHandler.superSpellBosses) continue
			val dmg = if (e === target) DamageSourceSpell.sacrifice else DamageSourceSpell.sacrifice(target)
			e.attackEntityFrom(dmg, SpellSacrifice.damage)
		}
		*/
	}
	
	fun particles(target: EntityLivingBase, time: Int) {
		// PORT: КТ-7 — огоньки радиусом заклинания (SpellSacrifice); Botania.proxy.wispFX → WispParticleData (MAPPING.md)
		/*
		val v = Vector3()
		for (i in 1..(SpellSacrifice.radius.I * 4)) {
			v.rand().sub(0.5).normalize().mul(time / 32.0 * SpellSacrifice.radius)
			Botania.proxy.wispFX(target.worldObj, target.posX + v.x, target.posY + v.y, target.posZ + v.z, 1f, Math.random().F * 0.5f, Math.random().F * 0.075f, (Math.random() * time + 1).F, 0f, (Math.random() * 3.0 + 2).F)
		}
		*/
	}
	
	// PORT: имена полей 1.7.10
	private val EntityLivingBase.worldObj get() = level()
	private val EntityLivingBase.posX get() = x
	private val EntityLivingBase.posY get() = y
	private val EntityLivingBase.posZ get() = z
	private val net.minecraft.world.level.Level.rand get() = random
}