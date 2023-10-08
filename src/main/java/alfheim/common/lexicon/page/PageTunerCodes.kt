package alfheim.common.lexicon.page

import alexsocol.asjlib.mc
import alfheim.common.block.tile.TileManaTuner
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.util.*
import vazkii.botania.api.internal.IGuiLexiconEntry
import vazkii.botania.api.lexicon.LexiconPage
import vazkii.botania.common.Botania
import java.awt.Color

object PageTunerCodes: LexiconPage("") {
	
	@SideOnly(Side.CLIENT)
	override fun renderScreen(gui: IGuiLexiconEntry, mx: Int, my: Int) {
		val x = gui.left + 18
		val y = gui.top + 12
		
		val font = mc.fontRenderer
		val unicode = font.unicodeFlag
		font.unicodeFlag = true
		
		val lines = TileManaTuner.map.mapIndexed { id, it ->
			val l = if (it == ' ') "\u23B5" else it
			"${EnumChatFormatting.BOLD} $l ${EnumChatFormatting.RESET}- ${StatCollector.translateToLocal("misc.alfheim.color.$id")}"
		}
		
		var yOffset = y
		for ((i, line) in lines.withIndex()) {
			font.drawString(line, x, yOffset, getColor(i), true)
			yOffset += 9
		}
		
		font.unicodeFlag = unicode
	}
	
	fun getColor(i: Int): Int {
		if (i == -1) return 0xFFFFFF
		if (i == 16) return Color.HSBtoRGB(Botania.proxy.worldElapsedTicks * 2 % 360 / 360f, 1f, 1f)
		val (r, g, b) = EntitySheep.fleeceColorTable[i]
		return Color(r, g, b).rgb
	}
	
	override fun getUnlocalizedName() = ""
}
