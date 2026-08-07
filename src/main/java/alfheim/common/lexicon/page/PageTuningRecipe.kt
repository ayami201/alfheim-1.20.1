package alfheim.common.lexicon.page

import alexsocol.asjlib.*
import alfheim.api.crafting.recipe.TunerIncantation
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.AlfheimBlocks
import cpw.mods.fml.relauncher.*
import net.minecraft.client.gui.GuiScreen
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraftforge.oredict.OreDictionary
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.internal.IGuiLexiconEntry
import vazkii.botania.client.core.handler.ClientTickHandler
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.lexicon.page.*

open class PageTuningRecipe(unlocalizedName: String, val incantation: TunerIncantation<Any>, val targets: Array<Any>, val results: Array<Any>? = null): PageRecipe(unlocalizedName) {
	
	private var ticksElapsed = 0
	
	private var recipeAt = 0
	
	@Suppress("UNCHECKED_CAST")
	constructor(unlocalizedName: String, incantation: TunerIncantation<Any>, target: Any, result: Any? = null): // some shitty type fuckery down
			this(unlocalizedName, incantation, if (target is Array<*>) target as Array<Any> else arrayOf(target), if (result == null) null else if (result is Array<*>) result as Array<Any> else arrayOf(result))
	
	init {
		val target = targets.getOrNull(0)
		if (target != null && target !is ItemStack && target !is String)
			throw IllegalArgumentException("Targets elements must be either stack or string")
		
		val result = results?.getOrNull(0)
		if (result != null && result !is ItemStack && result !is String)
			throw IllegalArgumentException("Results elements must be either stack or string")
	}
	
	override fun renderScreen(gui: IGuiLexiconEntry, mx: Int, my: Int) {
		mc.renderEngine.bindTexture(PagePetalRecipe.petalOverlay)
		
		val target = targets[recipeAt]
		val result = results?.get(recipeAt)
		
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		glColor4f(1f, 1f, 1f, 1f)
		(gui as GuiScreen).drawTexturedModalRect(gui.left, gui.top, 0, 0, gui.getWidth(), gui.getHeight())
		glDisable(GL_BLEND)
		
		val inCircle = result ?: target
		when (inCircle) {
			is ItemStack -> renderItemAtGridPos(gui, 3, 0, inCircle, false)
			is String    -> {
				val xPos = gui.left + 3 * 29 + 7 + 10
				val yPos = gui.top + 24 - 7
				val text = StatCollector.translateToLocal(inCircle)
				val font = mc.fontRenderer
				val uni = font.unicodeFlag
				font.unicodeFlag = true
				font.drawString(text, xPos - font.getStringWidth(text) / 2, yPos, 0)
				font.unicodeFlag = uni
				glColor4f(1f, 1f, 1f, 1f)
			}
		}
		
		renderItemAtGridPos(gui, 2, 1, ItemStack(AlfheimBlocks.manaTuner), false)
		
		if (result != null && target is ItemStack) {
			glPushMatrix()
			glTranslatef(0f, 0f, 16f)
			renderItem(gui, gui.left + 65.0, gui.top + 41.0, target, false)
			glPopMatrix()
		}
		
		val inputs = incantation.getInputsForRender(ticksElapsed)
		val degreePerInput = (360f / inputs.size).I
		var currentDegree = if (ConfigHandler.lexiconRotatingItems) if (GuiScreen.isShiftKeyDown()) ticksElapsed.F else ticksElapsed + ClientTickHandler.partialTicks else 0f
		
		for (obj in inputs) {
			var input = obj
			if (input is String) {
				val ores = OreDictionary.getOres(input)
				input = ores[ticksElapsed / 20 % ores.size]
			}
			
			renderItemAtAngle(gui, currentDegree, input as ItemStack)
			
			currentDegree += degreePerInput.F
		}
		
		val width = gui.getWidth() - 30
		val height = gui.getHeight()
		val x = gui.left + 16
		val y = gui.top + 100
		PageText.renderText(x, y, width, height, "[${incantation.incantation}]")
		
		val oldName = unlocalizedName
		if (!StatCollector.canTranslate(oldName)) unlocalizedName = ""
		super.renderScreen(gui, mx, my)
		unlocalizedName = oldName
	}
	
	@SideOnly(Side.CLIENT)
	override fun updateScreen() {
		if (GuiScreen.isShiftKeyDown()) return
		
		if (ticksElapsed % 20 == 0) {
			recipeAt++
			
			if (recipeAt == targets.size) recipeAt = 0
		}
		
		++ticksElapsed
	}
}