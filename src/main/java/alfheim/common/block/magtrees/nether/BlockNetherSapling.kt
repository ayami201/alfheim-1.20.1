package alfheim.common.block.magtrees.nether

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.world.gen.HeartWoodTreeGen
import alfheim.port.legacy.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block

class BlockNetherSapling: BlockColoredSapling("netherSapling") {
	
	init {
		setLightLevel(0.5f)
	}
	
	// PORT: варианты metadata — блоки массива (SPEC, Р-5): бревно — вариант 0, сердцевина — вариант 1, metadata у обоих — 0
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.netherWood[0], 0, AlfheimBlocks.netherWood[1], 0, AlfheimBlocks.netherLeaves, 0, AlfheimBlocks.netherBerry)
//	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.netherWood, 0, AlfheimBlocks.netherWood, 1, AlfheimBlocks.netherLeaves, 0, AlfheimBlocks.netherBerry)
	
	override fun canGrowHere(block: Block) =
		block.material == Material.ground || block.material == Material.grass
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.netherSapling
	
	// PORT: горит ли блок, 1.20.1 решает по таблице огня (FireBlock), её заполняет registerBurnables (AlfheimBlocks):
	// адских блоков в ней нет — они не горят и без этих методов
//	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = false
//
//	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 800 else 0
}
