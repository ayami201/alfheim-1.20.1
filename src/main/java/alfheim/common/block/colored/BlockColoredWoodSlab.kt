package alfheim.common.block.colored

// PORT: импорты 1.20.1; цвета шерсти 1.7.10 — Sheep1710
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockSlabMod
import alfheim.port.legacy.*
import alfheim.port.legacy.Sheep1710 as EntitySheep
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock as BlockSlab
import net.minecraftforge.api.distmarker.*
import java.awt.Color

// PORT: доски-источник — блок варианта (SPEC, Р-5): irisPlanks[meta] вместо irisPlanks с metadata. КТ-9 — лексикон
// (ILexiconable)
class BlockColoredWoodSlab(full: Boolean, meta: Int, source: Block = AlfheimBlocks.irisPlanks[meta]):
	BlockSlabMod(full, meta, source, source.unlocalizedName.replace("tile.".toRegex(), "") + "Slab" + (if (full) "Full" else "") + meta)/*, ILexiconable*/, IFuelHandler {
	
	init {
		setResistance(10f)
		GameRegistry.registerFuelHandler(this)
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(m: Int): Int {
		if (meta >= EntitySheep.fleeceColorTable.size)
			return 0xFFFFFF
		
		val color = EntitySheep.fleeceColorTable[meta]
		return Color(color[0], color[1], color[2]).rgb
	}
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess?, x: Int, y: Int, z: Int) = getRenderColor(meta)
	
	override fun getFullBlock() = AlfheimBlocks.irisSlabsFull[meta] as BlockSlab
	
	override fun getSingleBlock() = AlfheimBlocks.irisSlabs[meta] as BlockSlab
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.irisSapling
	
	// PORT: field_150004_a (двойная ли плита) — full. Печь 1.7.10 сжигала деревянный блок 300 тиков раньше обработчиков
	// модов, поэтому 150 у одинарной плиты не срабатывало (Fuel1710; BUGS.md)
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) if (full) 300 else 150 else 0
}
