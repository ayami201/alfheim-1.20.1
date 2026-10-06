package alfheim.common.block.magtrees.nether

// PORT: импорты 1.20.1
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.magtrees.BlockMagicLeaves
import net.minecraft.util.RandomSource as Random

class BlockNetherLeaves: BlockMagicLeaves("netherLeaves") {
	
	override fun isInterpolated() = true
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = AlfheimBlocks.netherSapling.toItem()
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.netherSapling
	
	// ####
	
	// PORT: горит ли блок, 1.20.1 решает по таблице огня (FireBlock), её заполняет registerBurnables (AlfheimBlocks):
	// адских блоков в ней нет — они не горят и без этих методов
//	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = false
//
//	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
}
