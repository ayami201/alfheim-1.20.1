package alfheim.common.integration.minetweaker

import alfheim.api.*
import alfheim.common.integration.minetweaker.MinetweakerAlfheimConfig.getStack
import minetweaker.*
import minetweaker.api.item.IItemStack
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.*

@ZenClass("mods." + ModInfo.MODID + ".TradePortal")
object MTHandlerTradePortal {
	
	@ZenMethod
	@JvmStatic
	fun banRetrade(output: IItemStack) {
		MineTweakerAPI.apply(Ban(getStack(output)))
	}
	
	private class Ban(private val output: ItemStack): IUndoableAction {
		
		override fun apply() {
			AlfheimAPI.banRetrade(output)
		}
		
		override fun canUndo() = false
		
		override fun undo() = Unit
		
		override fun describe() = "Removing ${output.unlocalizedName} from Alfheim trade portal"
		
		override fun describeUndo() = "NO-OP"
		
		override fun getOverrideKey() = null
	}
}
