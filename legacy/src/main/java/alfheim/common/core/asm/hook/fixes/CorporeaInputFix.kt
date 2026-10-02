package alfheim.common.core.asm.hook.fixes

import alfheim.common.item.ItemCorporeaRat
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageCorporeaRequest
import codechicken.nei.*
import codechicken.nei.guihook.GuiContainerManager
import gloomyfolken.hooklib.asm.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiContainer
import vazkii.botania.client.integration.nei.*
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex

object CorporeaInputFix {
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun keyTyped(neiih: NEIInputHandler, gui: GuiContainer?, c: Char, i: Int): Boolean {
		val mc = Minecraft.getMinecraft()
		
		// old code:
//		if(TileCorporeaIndex.InputHandler.getNearbyIndexes(mc.thePlayer).isEmpty()) return false
		
		// new code:
		val hasIndex = TileCorporeaIndex.InputHandler.getNearbyIndexes(mc.thePlayer).isNotEmpty()
		val hasQuandex = mc.thePlayer.heldItem?.item is ItemCorporeaRat
		if (!hasIndex && !hasQuandex) return false
		
		val bind = NEIClientConfig.getKeyBinding(NEIBotaniaConfig.CORPOREA_KEY)
		
		if (i != bind) return false
		
		val layoutManager = LayoutManager.instance()
		if (layoutManager == null || LayoutManager.itemPanel == null || NEIClientConfig.isHidden()) return false
		
		val stack = GuiContainerManager.getStackMouseOver(gui)
		if (stack == null || stack.item == null) return false
		
		var count = 1
		val max = stack.maxStackSize
		
		if (GuiScreen.isShiftKeyDown()) {
			count = max
			if (GuiScreen.isCtrlKeyDown()) count /= 4
		} else if (GuiScreen.isCtrlKeyDown()) count = max / 2
		
		if (count <= 0) return false
		
		// old code:
//		val name = CorporeaHelper.stripControlCodes(stack.getDisplayName())
//		val full = "$count $name"
//		mc.ingameGUI.chatGUI.addToSentMessages(full)
//		mc.thePlayer.sendChatMessage(full)
		
		// new code:
		NetworkService.sendToServer(MessageCorporeaRequest(stack, count, !hasIndex))
		
		return true
	}
}