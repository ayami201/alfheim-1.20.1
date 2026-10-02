package alfheim.common.core.asm.hook.integration

import alexsocol.asjlib.ItemNBTHelper
import alfheim.AlfheimCore
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.integration.tinkersconstruct.TinkersConstructAlfheimConfig
import gloomyfolken.hooklib.asm.*
import net.minecraft.item.ItemStack
import tconstruct.library.modifier.IModifyable
import vazkii.botania.common.core.handler.PixieHandler

object TraitFairySpawner {
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE, floatReturnConstant = 0.1f)
	fun getChance(handler: PixieHandler, stack: ItemStack?): Boolean {
		if (!AlfheimCore.TiCLoaded) return false
		
		if (stack == null) return false
		val tool = stack.item as? IModifyable ?: return false
		val tag = ItemNBTHelper.getCompound(stack, tool.baseTagName, true) ?: return false
		val headMaterial = tag.getInteger("Head")
		return headMaterial == AlfheimConfigHandler.materialIDs[TinkersConstructAlfheimConfig.ELEMENTIUM]
	}
}