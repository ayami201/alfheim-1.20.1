package alfheim.common.crafting.recipe.tuner

import alexsocol.asjlib.*
import alexsocol.asjlib.get
import alfheim.api.AlfheimAPI
import alfheim.api.AlfheimAPI.set
import alfheim.api.crafting.recipe.TunerIncantation
import alfheim.common.crafting.recipe.tuner.IncantationThaumWandOvercharge.TAG_OVERCHARGE
import alfheim.common.crafting.recipe.tuner.IncantationThaumWandOvercharge.apply
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import thaumcraft.api.aspects.Aspect
import thaumcraft.common.config.ConfigBlocks.blockCrystal
import thaumcraft.common.config.ConfigItems.itemShard
import thaumcraft.common.items.wands.ItemWandCasting
import kotlin.math.*

object IncantationThaumWandOvercharge: TunerIncantation<ItemStack>(ItemStack::class.java, "o kama suli e ijo lili o pana e ijo mama tawa insa ona", Array(4) { TAG_OVERCHARGE }, { apply(it) }) {
	
	const val TAG_OVERCHARGE = "overchargeIngredient_"
	
	const val TAG_ALL_SMALL = "${TAG_OVERCHARGE}all_small"
	const val TAG_AIR_SMALL = "${TAG_OVERCHARGE}aer_small"
	const val TAG_EARTH_SMALL = "${TAG_OVERCHARGE}terra_small"
	const val TAG_ENTROPY_SMALL = "${TAG_OVERCHARGE}perditio_small"
	const val TAG_FIRE_SMALL = "${TAG_OVERCHARGE}ignis_small"
	const val TAG_ORDER_SMALL = "${TAG_OVERCHARGE}ordo_small"
	const val TAG_WATER_SMALL = "${TAG_OVERCHARGE}aqua_small"
	
	const val TAG_AIR_BIG = "${TAG_OVERCHARGE}aer_big"
	const val TAG_EARTH_BIG = "${TAG_OVERCHARGE}terra_big"
	const val TAG_ENTROPY_BIG = "${TAG_OVERCHARGE}perditio_big"
	const val TAG_FIRE_BIG = "${TAG_OVERCHARGE}ignis_big"
	const val TAG_ORDER_BIG = "${TAG_OVERCHARGE}ordo_big"
	const val TAG_WATER_BIG = "${TAG_OVERCHARGE}aqua_big"
	
	var localBuffer = ArrayList<String>()
	var renderTags = ArrayList<String>()
	
	init {
		AlfheimAPI.tunerIncantations[incantation.lowercase()] = this
		
		arrayOf(
			TAG_AIR_SMALL     to ItemStack(itemShard   , 0, 0),
			TAG_FIRE_SMALL    to ItemStack(itemShard   , 0, 1),
			TAG_WATER_SMALL   to ItemStack(itemShard   , 0, 2),
			TAG_EARTH_SMALL   to ItemStack(itemShard   , 0, 3),
			TAG_ORDER_SMALL   to ItemStack(itemShard   , 0, 4),
			TAG_ENTROPY_SMALL to ItemStack(itemShard   , 0, 5),
			TAG_ALL_SMALL     to ItemStack(itemShard   , 0, 6),
			
			TAG_AIR_BIG       to ItemStack(blockCrystal, 0, 0),
			TAG_FIRE_BIG      to ItemStack(blockCrystal, 0, 1),
			TAG_WATER_BIG     to ItemStack(blockCrystal, 0, 2),
			TAG_EARTH_BIG     to ItemStack(blockCrystal, 0, 3),
			TAG_ORDER_BIG     to ItemStack(blockCrystal, 0, 4),
			TAG_ENTROPY_BIG   to ItemStack(blockCrystal, 0, 5),
			TAG_ALL_SMALL     to ItemStack(blockCrystal, 0, 6),
		).forEach {
			OreDictionary.registerOre(it.first, it.second)
			OreDictionary.registerOre(TAG_OVERCHARGE, it.second)
			renderTags += it.first
		}
		renderTags.shuffle()
	}
	
	override fun matches(inv: IInventory, target: ItemStack): Boolean {
		val items = ArrayList<String>()
		
		val result = target.item is ItemWandCasting && (0 until inv.sizeInventory).all { 
			val at = inv[it] ?: return@all true
			
			for (id in OreDictionary.getOreIDs(at)) {
				val ore = OreDictionary.getOreName(id)
				if (!ore.startsWith(TAG_OVERCHARGE)) continue
				
				val variant = ore.replace(TAG_OVERCHARGE, "")
				if (variant.isBlank() || variant.indexOf('_') == -1) continue
				
				repeat(at.stackSize) {
					items += variant
				}
				
				return@all true
			}
			
			return@all false
		}
		
		if (items.isEmpty() || !result) return false
		
		localBuffer = items
		return true
	}
	
	fun apply(stack: ItemStack): Boolean {
		val wand = stack.item as? ItemWandCasting ?: return false
		
		val cap = wand.getMaxVis(stack)
		
		fun addVis(aspect: Aspect, amount: Int) {
			val was = wand.getVis(stack, aspect)
			val mul = min(1.0, cap.D / was.D)
			val add = ceil(amount * 100 * mul).toInt()
			wand.storeVis(stack, aspect, was + add)
		}
		
		localBuffer.forEach { 
			val (tag, size) = it.split("_")
			val amount = if (size == "small") 2 else if (size == "big") 12 else return@forEach
			
			if (tag == "all") {
				for (aspect in Aspect.getPrimalAspects())
					addVis(aspect, amount)
			} else {
				addVis(Aspect.getAspect(tag), amount)
			}
		}
		localBuffer.clear()
		
		return true
	}
	
	override fun getInputsForRender(renderTick: Int): List<Any> {
		val s = renderTick / 20
		return Array(s % 16 + 1) { renderTags[(s + it) % renderTags.size] }.toList()
	}
}