package alfheim.common.crafting.recipe.barrel

import alfheim.client.model.block.ModelBarrel
import alfheim.common.block.tile.TileBarrel
import alfheim.common.item.material.ElvenFoodMetas
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11.glColor4f

object RecipeWineWhite: RecipeWine(ElvenFoodMetas.WhiteGrapes.stack, ElvenFoodMetas.WhiteWine.stack) {
	
	override fun serverTick(tile: TileBarrel) {
		super.serverTick(tile)
		
		if (getComparatorValue(tile) < 5 && tile.dark && !tile.inDark) tile.dark = false
	}
	
	override fun onFermented(tile: TileBarrel) {
		tile.dark = tile.inDark
	}
	
	override fun getOutputStack(tile: TileBarrel) = if (tile.dark) ElvenFoodMetas.Champagne.stack else super.getOutputStack(tile)
	
	@SideOnly(Side.CLIENT) override fun getMash() = ModelBarrel.greenMash
	@SideOnly(Side.CLIENT) override fun getLiquid() = ModelBarrel.greenWine
	@SideOnly(Side.CLIENT) override fun applyColorToLiquid(tile: TileBarrel, a: Float) = if (tile.dark) glColor4f(1f, 1f, 0.5f, a) else super.applyColorToLiquid(tile, a)
	
	private const val TAG_DARK = "dark"
	
	private var TileBarrel.dark: Boolean
		get() = data.getBoolean(TAG_DARK)
		set(value) = data.setBoolean(TAG_DARK, value)
	
	private val TileBarrel.inDark get() = worldObj.getBlockLightValue(xCoord, yCoord, zCoord) <= 4
}