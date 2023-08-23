package alfheim.api.crafting.recipe

import alexsocol.asjlib.*
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.oredict.OreDictionary

open class TunerIncantation<out T: Any>(val clazz: Class<@UnsafeVariance T>, val incantation: String, inputs: Array<out Any>, val application: (@UnsafeVariance T) -> Boolean) {
	
	val inputs = inputs.toList()
	val type: EnumTargetType
	
	init {
		type = when {
			Block::class.java.isAssignableFrom(clazz)      -> EnumTargetType.BLOCK
			Entity::class.java.isAssignableFrom(clazz)     -> EnumTargetType.ENTITY
			TileEntity::class.java.isAssignableFrom(clazz) -> EnumTargetType.TILE
			else                                           -> throw IllegalArgumentException("Mana Tuner target class must be either a Block or an Entity")
		}
		
		require(inputs.size <= 16) { "Mana Tuner recipe has ${inputs.size} additional inputs, max is 16" }
		for (obj in inputs) require(obj is String || obj is ItemStack) { "Invalid input $obj" }
	}
	
	open fun matches(inv: IInventory, target: @UnsafeVariance T): Boolean {
		val inputsMissing = ArrayList(getInputs(target))
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: break
			
			var stackIndex = -1
			var oredictIndex = -1
			
			for (j in inputsMissing.indices) {
				val input = inputsMissing[j]
				
				if (input is String) {
					val validStacks: List<ItemStack> = OreDictionary.getOres(input as String?)
					var found = false
					for (ostack in validStacks) {
						val cstack = ostack.copy()
						if (cstack.getItemDamage() == Short.MAX_VALUE.I) cstack.setItemDamage(stack.getItemDamage())
						
						if (stack.isItemEqual(cstack)) {
							oredictIndex = j
							found = true
							break
						}
					}
					
					if (found) break
				} else if (input is ItemStack && simpleAreStacksEqual(input, stack)) {
					stackIndex = j
					break
				}
			}
			
			if (stackIndex != -1) inputsMissing.removeAt(stackIndex) else if (oredictIndex != -1) inputsMissing.removeAt(oredictIndex) else return false
		}
		
		return inputsMissing.isEmpty()
	}
	
	fun simpleAreStacksEqual(stack: ItemStack, stack2: ItemStack) = stack.item === stack2.item && stack.getItemDamage() == stack2.getItemDamage()
	
	open fun getInputs(target: @UnsafeVariance T): List<Any> = ArrayList(inputs)
	
	enum class EnumTargetType {
		BLOCK, TILE, ENTITY
	}
}