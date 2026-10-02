package alfheim.client.gui

import alfheim.common.block.tile.TileGaiaButton
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageUpdateGaiaButton
import cpw.mods.fml.relauncher.*
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ChatAllowedCharacters
import org.lwjgl.input.Keyboard

@SideOnly(Side.CLIENT)
class GUIEditGaiaButton(val button: TileGaiaButton): GuiScreen() {
	
	override fun initGui() {
		buttonList.clear()
		Keyboard.enableRepeatEvents(true)
	}
	
	override fun onGuiClosed() {
		Keyboard.enableRepeatEvents(false)
	}
	
	override fun keyTyped(char: Char, key: Int) {
		if (key == Keyboard.KEY_ESCAPE || key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
			mc.displayGuiScreen(null)
			return
		} else if (key == Keyboard.KEY_BACK && button.name.isNotEmpty()) {
			button.name = button.name.substring(0, button.name.length - 1)
		} else if (char == '\u0016') {
			button.name = (button.name + getClipboardString()).take(3)
		} else if (ChatAllowedCharacters.isAllowedCharacter(char) && button.name.length < 3) {
			button.name += char
		}
		
		NetworkService.sendToServer(MessageUpdateGaiaButton(button.xCoord, button.yCoord, button.zCoord, button.name))
	}
	
	override fun drawScreen(x: Int, y: Int, ticks: Float) {
		this.drawDefaultBackground()
	}
	
	override fun doesGuiPauseGame() = false
}