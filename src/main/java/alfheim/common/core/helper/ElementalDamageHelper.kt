@file:Suppress("unused")

package alfheim.common.core.helper

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.api.item.equipment.IElementalItem
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.SheerColdHandler.cold
import alfheim.common.core.helper.ElementalDamage.*
import alfheim.common.core.helper.ElementalDamageBridge.*
import alfheim.common.crafting.recipe.IncantationEquipmentElementalTuning
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.*
import net.minecraft.entity.passive.EntityWaterMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.event.entity.living.*
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.item.*
import java.util.*
import kotlin.math.max

object ElementalDamageHandler {
	
	init {
		for (ed in ElementalDamage.entries) require(ElementalDamageBridge.valueOf("${ed}_").real == ed)
		
		DamageSource.inWall.setTo(EARTH)
		DamageSource.drown.setTo(WATER)
		DamageSource.cactus.setTo(NATURE)
		DamageSource.fall.setTo(EARTH)
		DamageSource.wither.setTo(DARKNESS)
		DamageSource.anvil.setTo(EARTH)
		DamageSource.fallingBlock.setTo(EARTH)
	}
	
	val elementalMobs: Map<String, EnumSet<ElementalDamage>> by lazy {
		AlfheimConfigHandler.mobElements.associate { entry ->
			val (name, eList) = entry.split(":")
			val elements = eList.split(",").mapTo(EnumSet.noneOf(ElementalDamage::class.java), ElementalDamage::valueOf)
			require(COMMON !in elements || elements.size == 1) { "Cannot combine COMMON with any other element" }
			name to elements
		}
	}
	
	val EntityLivingBase.elements: EnumSet<ElementalDamage>
		get() {
			if (this is IElementalEntity) return elements
			
			return elementalMobs[EntityList.getEntityString(this)] ?: EnumSet.of(COMMON)
		}
	
	val EntityLivingBase.appliedElements: EnumSet<ElementalDamage>
		get() {
			val set = EnumSet.noneOf(ElementalDamage::class.java)
			
			if (isBurning && !isImmuneToFire) set.add(FIRE)
			if (isWet && this !is EntityWaterMob) set.add(WATER)
			if (cold > 50) set.add(ICE)
			
			return set
		}
	
	fun calculateElements(source: DamageSource, target: EntityLivingBase, amount: Float): Float {
		val attackEl = source.elements()
		
		val (heldElement, attunementLevel) = getHeldElements(source)
		if (heldElement != null) attackEl += heldElement
		if (attackEl.size == 1 && attackEl.first() == COMMON) return amount
		
		val targetEl = EnumSet.copyOf(target.elements)
		val resistanceModifiers = EnumMap<ElementalDamage, Float>(ElementalDamage::class.java)
		ElementalDamage.entries.forEach { resistanceModifiers[it] = 0f }
		
		if (targetEl.size != 1 || targetEl.first() != COMMON) {
			targetEl.forEach { te ->
				resistanceModifiers[te] = resistanceModifiers[te]!! - (1f / targetEl.size)
				
				te.x05.forEach { x05 ->
					resistanceModifiers[x05.real] = resistanceModifiers[x05.real]!! - 0.5f
				}
			}
		}
		
		if (targetEl.size != 1 || targetEl.first() != COMMON) {
			targetEl.addAll(target.appliedElements)
			targetEl.forEach { te ->
				
				te.x2.forEach { x2 ->
					resistanceModifiers[x2.real] = resistanceModifiers[x2.real]!! + 1f
				}
			}
		}
		
		if (!source.isDamageAbsolute)
			for (i in 1..4) {
				val armor = target.getEquipmentInSlot(i) ?: continue
				val item = armor.item
				
				val element: ElementalDamage
				val level: Int
				
				if (item is IElementalItem) {
					element = item.getElement(armor)
					level = item.getElementLevel(armor)
				} else {
					val name = ItemNBTHelper.getString(armor, IncantationEquipmentElementalTuning.TAG_ELEMENT, COMMON.name)
					element = ElementalDamage.valueOf(name)
					level = ItemNBTHelper.getInt(armor, IncantationEquipmentElementalTuning.TAG_ELEMENT_LEVEL, 0)
				}
				
				resistanceModifiers[element] = resistanceModifiers[element]!! - (0.25f * level)
				
				element.x05.forEach { x05 ->
					resistanceModifiers[x05.real] = resistanceModifiers[x05.real]!! - (0.125f * level)
				}
				
				element.x2.forEach { x2 ->
					resistanceModifiers[x2.real] = resistanceModifiers[x2.real]!! + (0.25f * level)
				}
			}
		
		var elementalDamage = amount
		var commonDamage = 0f
		
		// for mixed damage types
		if (COMMON in attackEl) {
			commonDamage = max(0f, amount * (1f - 0.25f * attunementLevel))
			elementalDamage = amount * (0.25f * attunementLevel) * (attackEl.size - 1)
		}
		
		// no effect for common
		attackEl.remove(COMMON)
		
		val elementalDamages = EnumMap<ElementalDamage, Float>(ElementalDamage::class.java)
		attackEl.forEach { elementalDamages[it] = elementalDamage / attackEl.size }
		
		elementalDamages.keys.forEach {
			elementalDamages[it] = elementalDamages[it]!! * (resistanceModifiers[it]!! + 1f)
		}
		
		return commonDamage + elementalDamages.values.sum()
	}
	
	fun getHeldElements(source: DamageSource): Pair<ElementalDamage?, Int> {
		val attacker = source.entity as? EntityLivingBase ?: return null to 0
		val stack = attacker.heldItem ?: return null to 0
		
		val item = stack.item
		val element: ElementalDamage
		val attunementLevel: Int
		
		if (item is IElementalItem) {
			element = item.getElement(stack)
			attunementLevel = item.getElementLevel(stack)
		} else {
			val name = ItemNBTHelper.getString(stack, IncantationEquipmentElementalTuning.TAG_ELEMENT, COMMON.name)
			element = ElementalDamage.valueOf(name)
			val level = ItemNBTHelper.getInt(stack, IncantationEquipmentElementalTuning.TAG_ELEMENT_LEVEL, 0)
			attunementLevel = if (level == 4) 5 else level
		}
		
		return element to attunementLevel
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onAttacked(e: LivingAttackEvent) {
		val newAmount = calculateElements(e.source, e.entityLiving, e.ammount)
		if (e.ammount > 0 && newAmount <= 0) e.isCanceled = true
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	fun onHurt(e: LivingHurtEvent) {
		e.ammount = calculateElements(e.source, e.entityLiving, e.ammount)
		if (e.ammount <= 0f) e.isCanceled = true
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun drawStatusIcons(e: RenderLivingEvent.Specials.Post) {
		if (mc.theWorld == null || mc.thePlayer == null) return // in-menu render
		
		val monocle = getMonocle(mc.thePlayer) ?: return
		if (!ItemNBTHelper.getBoolean(monocle, TAG_ELEMENTAL_SEER, false)) return
		
		val applied = e.entity.appliedElements
		val elements = e.entity.elements
		val all = elements.plus(applied).filter { it != COMMON }
		if (all.isEmpty()) return
		
		val size = max(e.entity.width.D * 8, 8.0)
		
		val f1 = 0.02666667f
		glPushMatrix()
		glColor4f(1f, 1f, 1f, 1f)
		glTranslated(e.x, e.y + e.entity.height + 0.5 + 0.03125 * size, e.z)
		glNormal3f(0f, 1f, 0f)
		glRotatef(-RenderManager.instance.playerViewY, 0f, 1f, 0f)
		glRotatef(RenderManager.instance.playerViewX, 1f, 0f, 0f)
		glScalef(-f1, -f1, f1)
		glDisable(GL_LIGHTING)
		glEnable(GL_BLEND)
		OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
		
		glTranslatef(all.size * size.F / -2, 0f, 0f)
		
		mc.renderEngine.bindTexture(LibResourceLocations.elements)
		
		val debufSignPoses = mutableListOf<Double>()
		val uOff = 1.0 / (ElementalDamage.entries.size - 1)
		
		val tes = Tessellator.instance
		tes.startDrawingQuads()
		for ((id, element) in all.withIndex()) {
			val x = id * size
			val u = (element.ordinal - 1) * uOff
			
			tes.addVertexWithUV(x       ,  0.0, 0.0, u       , 0.0)
			tes.addVertexWithUV(x       , size, 0.0, u       , 1.0)
			tes.addVertexWithUV(x + size, size, 0.0, u + uOff, 1.0)
			tes.addVertexWithUV(x + size,  0.0, 0.0, u + uOff, 0.0)
			
			if (element !in elements) debufSignPoses.add(x)
		}
		tes.draw()
		
		debufSignPoses.forEach { x ->
			mc.fontRenderer.drawString("-", x.I, (size / 2).I, 0x8B0000)
		}
		
		glEnable(GL_LIGHTING)
		glDisable(GL_BLEND)
		glColor4f(1f, 1f, 1f, 1f)
		glPopMatrix()
	}
	
	fun getMonocle(player: EntityPlayer?): ItemStack? {
		val baubles = PlayerHandler.getPlayerBaubles(player)
		
		for (i in 0..3) {
			val stack = baubles[i] ?: continue
			
			val item = stack.item
			if (item is IBurstViewerBauble) return stack
			if (item !is ICosmeticAttachable) continue
			
			val cosmetic = item.getCosmeticItem(stack) ?: continue
			if (cosmetic.item is IBurstViewerBauble) return cosmetic
		}
		
		return null
	}
	
	const val TAG_ELEMENTAL_SEER = "${ModInfo.MODID}_elementalSeer"
}

enum class ElementalDamageBridge {
	COMMON_, FIRE_, WATER_, AIR_, EARTH_, ICE_, ELECTRIC_, NATURE_, LIGHTNESS_, DARKNESS_, PSYCHIC_;
	val real get() = ElementalDamage.entries[ordinal]
}

enum class ElementalDamage(val x2: Array<ElementalDamageBridge>, val x05: Array<ElementalDamageBridge>, val color: Int) {
	COMMON(arrayOf(), arrayOf(), 0xFFFFFF),
	FIRE(arrayOf(WATER_, EARTH_), arrayOf(AIR_, NATURE_), 0xC5390F),
	WATER(arrayOf(ICE_, ELECTRIC_), arrayOf(FIRE_, NATURE_), 0x207FCC),
	AIR(arrayOf(FIRE_, ICE_), arrayOf(WATER_, EARTH_), 0xFEFBEB),
	EARTH(arrayOf(WATER_, NATURE_), arrayOf(ICE_, ELECTRIC_), 0x71493B),
	ICE(arrayOf(FIRE_, ELECTRIC_), arrayOf(WATER_, NATURE_), 0xA6FCDB),
	ELECTRIC(arrayOf(FIRE_, ICE_), arrayOf(WATER_, AIR_), 0xFFC700),
	NATURE(arrayOf(FIRE_, ICE_), arrayOf(WATER_, ELECTRIC_), 0x14A02E),
	LIGHTNESS(arrayOf(DARKNESS_), arrayOf(), 0xFFFC40),
	DARKNESS(arrayOf(LIGHTNESS_), arrayOf(), 0x221C1A),
	PSYCHIC(arrayOf(DARKNESS_), arrayOf(LIGHTNESS_), 0x793A80);
	
	fun isVulnerable(type: ElementalDamage): Boolean {
		return ElementalDamageBridge.entries[type.ordinal] in x2
	}
	
	fun isResistant(type: ElementalDamage): Boolean {
		return ElementalDamageBridge.entries[type.ordinal] in x05
	}
	
	fun isImmune(type: ElementalDamage): Boolean {
		return if (type == COMMON) false else this == type
	}
}

interface IElementalEntity {
	val elements: EnumSet<ElementalDamage>
}

fun DamageSource.setTo(type: ElementalDamage): DamageSource {
	alfheim_synthetic_elementalFlag = ASJBitwiseHelper.setBit(alfheim_synthetic_elementalFlag, type.ordinal, true)
	return this
}

fun DamageSource.isOf(type: ElementalDamage): Boolean {
	return if (type == COMMON && alfheim_synthetic_elementalFlag == 0) true else {
		val stored = ASJBitwiseHelper.getBit(alfheim_synthetic_elementalFlag, type.ordinal)
		if (type == FIRE) isFireDamage || stored else stored
	}
}

fun DamageSource.elements(): EnumSet<ElementalDamage> {
	return when (damageType) {
		"basalz" -> EnumSet.of(EARTH)
		"blitz"  -> EnumSet.of(ELECTRIC)
		"blizz"  -> EnumSet.of(ICE)
		else     -> EnumSet.copyOf(ElementalDamage.entries.filter { isOf(it) })
	}
}