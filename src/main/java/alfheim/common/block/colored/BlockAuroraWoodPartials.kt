package alfheim.common.block.colored

// PORT: импорты 1.20.1 (BlockSlab 1.7.10 — Slab1710, MAPPING.md)
import alexsocol.asjlib.block
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.*
import alfheim.port.legacy.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock as BlockSlab
import net.minecraftforge.api.distmarker.*

// PORT: двойная плита — состояние type=double одинарной (BlockSlabMod). КТ-9 — лексикон
class BlockAuroraWoodSlab(full: Boolean, source: Block = AlfheimBlocks.auroraPlanks): BlockSlabMod(full, 0, source, source.unlocalizedName.replace("tile.".toRegex(), "") + "Slab" + (if (full) "Full" else "") + "17"), IFuelHandler {
	
	init {
		setResistance(10f)
		GameRegistry.registerFuelHandler(this)
	}
	
	// PORT: регистрация — та же, что у BlockSlabMod.register; там же старое имя двойной плиты (auroraPlanksSlabFull17)
//	override fun register() {
//		GameRegistry.registerBlock(this, ItemColoredSlabMod::class.java, name)
//	}
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int) = BlockAuroraDirt.getBlockColor(x, y, z)
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	override fun getFullBlock() = AlfheimBlocks.auroraSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimBlocks.auroraSlab as BlockSlab
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = (source as ILexiconable).getEntry(world, x, y, z, player, lexicon)!!
	
	// PORT: field_150004_a (двойная ли плита) — full. Печь 1.7.10 сжигала деревянный блок 300 тиков раньше обработчиков
	// модов, поэтому 150 у одинарной плиты не срабатывало (Fuel1710; BUGS.md)
	override fun getBurnTime(fuel: ItemStack) = if (fuel.block === this) if (full) 300 else 150 else 0
}

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockAuroraWoodStairs(source: Block = AlfheimBlocks.auroraPlanks):
	BlockStairsMod(source, 0, source.unlocalizedName.replace("tile.".toRegex(), "") + "Stairs17")/*, ILexiconable*/ {
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int) = BlockAuroraDirt.getBlockColor(x, y, z)
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = (source as ILexiconable).getEntry(p0, p1, p2, p3, p4, p5)!!
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
}
