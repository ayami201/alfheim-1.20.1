package alfheim.common.lexicon.page

import alexsocol.asjlib.*
import alexsocol.asjlib.render.*
import alfheim.api.crafting.recipe.*
import alfheim.common.block.*
import cpw.mods.fml.relauncher.*
import net.minecraft.item.*
import net.minecraft.util.*
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.internal.*
import vazkii.botania.client.core.handler.*
import vazkii.botania.common.block.tile.mana.*
import vazkii.botania.common.lexicon.page.*

class PageTreeCrafting: PagePetalRecipe<RecipeTreeCrafting> {
	
	constructor(unlocalizedName: String, recipes: List<RecipeTreeCrafting>): super(unlocalizedName, recipes)
	
	constructor(unlocalizedName: String, recipes: RecipeTreeCrafting): super(unlocalizedName, recipes)
	
	override fun getMiddleStack() = ItemStack(AlfheimBlocks.treeCrafterBlockRB)
	
	override fun renderRecipe(gui: IGuiLexiconEntry, mx: Int, my: Int) {
		super.renderRecipe(gui, mx, my)
		
		val core = recipes[recipeAt].core
		if (core.block === AlfheimBlocks.irisSapling) return
		
		glPushMatrix()
		glTranslatef(0f, 0f, 16f)
		renderItem(gui, gui.left + 65.0, gui.top + 44.0, core, false)
		glPopMatrix()
	}
	
	@SideOnly(Side.CLIENT)
	override fun renderManaBar(gui: IGuiLexiconEntry, recipe: RecipeTreeCrafting, mx: Int, my: Int) {
		val font = mc.fontRenderer
		ASJRenderHelper.setBlend()
		val manaUsage = StatCollector.translateToLocal("botaniamisc.manaUsage")
		font.drawString(manaUsage, gui.left + gui.width / 2 - font.getStringWidth(manaUsage) / 2, gui.top + 110, 1711276032)
		var ratio = 10
		val x = gui.left + gui.width / 2 - 50
		val y = gui.top + 120
		if (mx > x + 1 && mx <= x + 101 && my > y - 14 && my <= y + 11)
			ratio = 1
		HUDHandler.renderManaBar(x, y, 255, 0.75f, recipe.manaUsage, TilePool.MAX_MANA / ratio)
		val ratioString = StatCollector.translateToLocal("botaniamisc.ratio").format(ratio)
		val stopStr = StatCollector.translateToLocal("botaniamisc.shiftToStopSpin")
		val unicode = font.unicodeFlag
		font.unicodeFlag = true
		font.drawString(stopStr, x + 50 - font.getStringWidth(stopStr) / 2, y + 15, -1728053248)
		font.drawString(ratioString, x + 50 - font.getStringWidth(ratioString) / 2, y + 5, -1728053248)
		font.unicodeFlag = unicode
		ASJRenderHelper.discard()
	}
}
