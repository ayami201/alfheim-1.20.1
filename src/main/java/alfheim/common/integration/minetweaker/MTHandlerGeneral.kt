package alfheim.common.integration.minetweaker

import alexsocol.asjlib.block
import alfheim.api.*
import alfheim.common.integration.minetweaker.MinetweakerAlfheimConfig.getStack
import minetweaker.*
import minetweaker.api.item.IItemStack
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.*

@ZenClass("mods.${ModInfo.MODID}.General")
object MTHandlerGeneral {
	
	@ZenMethod
	@JvmStatic
	fun addColdBlock(iblock: IItemStack) {
		val block = getStack(iblock).block
		require(block !== Blocks.air) { "Can't add $iblock to list of cold blocks" }
		AlfheimAPI.coldBlocks += block
	}
	
	@ZenMethod
	@JvmStatic
	fun addWarmBlock(iblock: IItemStack) {
		val block = getStack(iblock).block
		require(block !== Blocks.air) { "Can't add $iblock to list of warm blocks" }
		AlfheimAPI.warmBlocks += block
	}
	
	@ZenMethod
	@JvmStatic
	fun banRetrade(output: IItemStack) {
		AlfheimAPI.banRetrade(getStack(output))
	}
	
	@ZenMethod
	@JvmStatic
	fun pinkify(input: IItemStack, pinkness: Int) {
		MineTweakerAPI.apply(Pinkifier(getStack(input), pinkness))
	}
	
	@ZenMethod
	@JvmStatic
	fun registerPetroniaFuel(name: String, burnTime: Int, manaPerTick: Int) {
		MineTweakerAPI.apply(Register(name, burnTime, manaPerTick))
	}
	
	private class Pinkifier(private val output: ItemStack, private val pinkness: Int): IUndoableAction {
		
		var old = 0
		
		override fun apply() {
			val i = AlfheimAPI.addPink(output, pinkness)
			if (i != null) old = i
		}
		
		override fun canUndo() = true
		
		override fun undo() {
			AlfheimAPI.pinkness[output] = old
		}
		
		override fun describe() = "Mapping new ($pinkness) pinkness weight for ${output.unlocalizedName}"
		
		override fun describeUndo() = "Mapping previous ($old) pinkness weight for ${output.unlocalizedName}"
		
		override fun getOverrideKey() = null
	}
	
	private class Register(val name: String, val burnTime: Int, val manaPerTick: Int): IUndoableAction {
		
		var prev: Pair<Int, Int>? = null
		
		override fun apply() {
			prev = AlfheimAPI.fuelMap.remove(name)
			AlfheimAPI.registerFuel(name, burnTime, manaPerTick)
		}
		
		override fun canUndo() = prev != null
		
		override fun undo() {
			AlfheimAPI.fuelMap[name] = prev!!
		}
		
		override fun describe() = "Setting fuel values for $name"
		
		override fun describeUndo() = "Reverting fuel values for $name"
		
		override fun getOverrideKey() = null
	}
}