package alfheim.common.block

// PORT: импорты 1.20.1; BlockLivingSlab Botania 1.7.10 — alfheim.port.legacy.botania (MAPPING.md, «Блоки и предметы»)
import alexsocol.asjlib.safeGet
import alfheim.port.legacy.botania.BlockLivingSlab
import net.minecraft.world.level.block.SlabBlock as BlockSlab

/*
 * PORT: двойная плита 1.7.10 (…SlabFull) — состояние type=double той же плиты (SlabBlock 1.20.1): getFullBlock и
 * getSingleBlock возвращают один и тот же блок. Варианты блока-источника — отдельные блоки (SPEC, Р-5): источник —
 * блок варианта, `shrineRock[0]` вместо `shrineRock, 0`
 */

class BlockRockShrineWhiteSlab(full: Boolean): BlockLivingSlab(full, AlfheimFluffBlocks.shrineRock[0], 0) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.shrineRockWhiteSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.shrineRockWhiteSlab as BlockSlab
}

// PORT: у автора getFullBlock и getSingleBlock перепутаны: в 1.7.10 одинарная плита роняла предмет двойной, а двойная —
// два таких же. В 1.20.1 оба метода дают одну и ту же плиту, у двойной своего предмета нет (TASKS.md, «Найдено»)
class BlockRoofTileSlab(full: Boolean, val meta: Int): BlockLivingSlab(full, AlfheimFluffBlocks.roofTile[meta], meta) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.roofTileSlabs[meta] as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.roofTileSlabsFull[meta] as BlockSlab
}

class BlockElvenSandstoneSlab(full: Boolean): BlockLivingSlab(full, AlfheimFluffBlocks.elvenSandstone[0], 0) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.elvenSandstoneSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.elvenSandstoneSlab as BlockSlab
}

class BlockElvenSandstoneSlab2(full: Boolean): BlockLivingSlab(full, AlfheimFluffBlocks.elvenSandstone[2], 2) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.elvenSandstoneSlab2Full as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.elvenSandstoneSlab2 as BlockSlab
}

class BlockDwarfPlanksSlab(full: Boolean): BlockLivingSlab(full, AlfheimFluffBlocks.dwarfPlanks, 0) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.dwarfPlanksSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.dwarfPlanksSlab as BlockSlab
}

class BlockLivingCobbleSlab(full: Boolean): BlockLivingSlab(full, AlfheimBlocks.livingcobble[0], 0) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.livingcobbleSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.livingcobbleSlab as BlockSlab
}

class BlockLivingCobbleSlab1(full: Boolean): BlockLivingSlab(full, AlfheimBlocks.livingcobble[1], 1) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.livingcobbleSlabFull1 as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.livingcobbleSlab1 as BlockSlab
}

class BlockLivingCobbleSlab2(full: Boolean): BlockLivingSlab(full, AlfheimBlocks.livingcobble[2], 2) {
	
	// PORT: иконки → модель (alfheim.port.data.AlfheimBlockStates): бока — LivingCobble2Slab, верх и низ — блока-источника
//	lateinit var sideIcon: IIcon
	
	override fun getFullBlock() = AlfheimFluffBlocks.livingcobbleSlabFull2 as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.livingcobbleSlab2 as BlockSlab
	
	/*
	override fun registerBlockIcons(reg: IIconRegister) {
		sideIcon = IconHelper.forName(reg, "LivingCobble2Slab")
	}
	
	override fun getIcon(side: Int, meta: Int) = if (side > 1) sideIcon else super.getIcon(side, meta)!!
	*/
	
}

class BlockLivingMountainSlab(full: Boolean): BlockLivingSlab(full, AlfheimFluffBlocks.livingMountain, 0) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.livingMountainSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.livingMountainSlab as BlockSlab
	
	// PORT: иконка по координатам — как у BlockLivingMountain, выбирает модель клиента (alfheim.port.client.AlfheimModels)
//	override fun getIcon(world: IBlockAccess?, x: Int, y: Int, z: Int, side: Int) = AlfheimFluffBlocks.livingMountain.getIcon(world, x, y, z, side)!!
}

class BlockLivingrockDarkSlab(full: Boolean, val meta: Int): BlockLivingSlab(full, AlfheimFluffBlocks.livingrockDark[meta], meta) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.livingrockDarkSlabsFull.safeGet(meta) as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.livingrockDarkSlabs.safeGet(meta) as BlockSlab
}
