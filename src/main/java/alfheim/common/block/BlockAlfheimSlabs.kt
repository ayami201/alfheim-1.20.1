package alfheim.common.block

import alexsocol.asjlib.safeGet
import alfheim.client.core.helper.IconHelper
import net.minecraft.block.BlockSlab
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import vazkii.botania.common.block.decor.slabs.BlockLivingSlab

class BlockRockShrineWhiteSlab(full: Boolean): BlockLivingSlab(full, AlfheimFluffBlocks.shrineRock, 0) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.shrineRockWhiteSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.shrineRockWhiteSlab as BlockSlab
}

class BlockRoofTileSlab(full: Boolean, val meta: Int): BlockLivingSlab(full, AlfheimFluffBlocks.roofTile, meta) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.roofTileSlabs[meta] as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.roofTileSlabsFull[meta] as BlockSlab
}

class BlockElvenSandstoneSlab(full: Boolean): BlockLivingSlab(full, AlfheimFluffBlocks.elvenSandstone, 0) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.elvenSandstoneSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.elvenSandstoneSlab as BlockSlab
}

class BlockElvenSandstoneSlab2(full: Boolean): BlockLivingSlab(full, AlfheimFluffBlocks.elvenSandstone, 2) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.elvenSandstoneSlab2Full as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.elvenSandstoneSlab2 as BlockSlab
}

class BlockDwarfPlanksSlab(full: Boolean): BlockLivingSlab(full, AlfheimFluffBlocks.dwarfPlanks, 0) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.dwarfPlanksSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.dwarfPlanksSlab as BlockSlab
}

class BlockLivingCobbleSlab(full: Boolean): BlockLivingSlab(full, AlfheimBlocks.livingcobble, 0) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.livingcobbleSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.livingcobbleSlab as BlockSlab
}

class BlockLivingCobbleSlab1(full: Boolean): BlockLivingSlab(full, AlfheimBlocks.livingcobble, 1) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.livingcobbleSlabFull1 as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.livingcobbleSlab1 as BlockSlab
}

class BlockLivingCobbleSlab2(full: Boolean): BlockLivingSlab(full, AlfheimBlocks.livingcobble, 2) {
	
	lateinit var sideIcon: IIcon
	
	override fun getFullBlock() = AlfheimFluffBlocks.livingcobbleSlabFull2 as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.livingcobbleSlab2 as BlockSlab
	
	override fun registerBlockIcons(reg: IIconRegister) {
		sideIcon = IconHelper.forName(reg, "LivingCobble2Slab")
	}
	
	override fun getIcon(side: Int, meta: Int) = if (side > 1) sideIcon else super.getIcon(side, meta)!!
	
}

class BlockLivingMountainSlab(full: Boolean): BlockLivingSlab(full, AlfheimFluffBlocks.livingMountain, 0) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.livingMountainSlabFull as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.livingMountainSlab as BlockSlab
	
	override fun getIcon(world: IBlockAccess?, x: Int, y: Int, z: Int, side: Int) = AlfheimFluffBlocks.livingMountain.getIcon(world, x, y, z, side)!!
}

class BlockLivingrockDarkSlab(full: Boolean, val meta: Int): BlockLivingSlab(full, AlfheimFluffBlocks.livingrockDark, meta) {
	
	override fun getFullBlock() = AlfheimFluffBlocks.livingrockDarkSlabsFull.safeGet(meta) as BlockSlab
	
	override fun getSingleBlock() = AlfheimFluffBlocks.livingrockDarkSlabs.safeGet(meta) as BlockSlab
}
