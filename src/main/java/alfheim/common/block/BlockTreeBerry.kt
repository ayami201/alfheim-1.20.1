package alfheim.common.block

import alexsocol.asjlib.safeGet
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileTreeBerry
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenFoodMetas
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.common.*
import vazkii.botania.api.lexicon.ILexiconable
import java.util.*

class BlockTreeBerry(val leaves: Block, val type: Int): BlockContainerMod(Material.vine), ILexiconable, IPlantable {
	
	lateinit var icons: Array<IIcon>
	
	init {
		setBlockName("TreeBerry$type")
		setHardness(0f)
		setStepSound(soundTypeGrass)
	}
	
	override fun isReplaceable(world: IBlockAccess?, x: Int, y: Int, z: Int) = false
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileTreeBerry()
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		checkChange(world, x, y, z)
		
		if (random.nextInt(10) != 0) return
		
		val meta = world.getBlockMetadata(x, y, z)
		if (meta >= 2) return
		
		world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3)
	}
	
	fun checkChange(world: World, x: Int, y: Int, z: Int) {
		if (canBlockStay(world, x, y, z)) return
		dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0)
		world.setBlockToAir(x, y, z)
	}
	
	override fun getSelectedBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
		setBlockBoundsBasedOnState(world, x, y, z)
		return super.getSelectedBoundingBoxFromPool(world, x, y, z)
	}
	
	override fun setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int) {
		val block = world.getBlock(x, y, z)
		when (world.getBlockMetadata(x, y, z)) {
			0 -> block.setBlockBounds(0.28125f, 0.5625f, 0.28125f, 0.71875f, 1f, 0.71875f)
			1 -> block.setBlockBounds(0.21875f, 0.375f, 0.21875f, 0.78125f, 1f, 0.78125f)
			else -> block.setBlockBounds(0.15625f, 0.125f, 0.15625f, 0.84375f, 1f, 0.84375f)
		}
	}
	
	override fun canBlockStay(world: World, x: Int, y: Int, z: Int) = world.getBlock(x, y + 1, z) === leaves
	
	override fun canPlaceBlockAt(world: World, x: Int, y: Int, z: Int) = canBlockStay(world, x, y, z)
	
	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) = checkChange(world, x, y, z)
	
	override fun getCollisionBoundingBoxFromPool(world: World?, x: Int, y: Int, z: Int) = null
	
	override fun isOpaqueCube() = false
	
	override fun renderAsNormalBlock() = false
	
	override fun getRenderType() = if (AlfheimConfigHandler.minimalGraphics || hasModelErrors[type] == true) 1 else -1
	
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = Array(3) { IconHelper.forBlock(reg, this, it) }
	}
	
	override fun getIcon(side: Int, meta: Int) = icons.safeGet(meta)
	
	override fun getItemDropped(meta: Int, random: Random?, fortune: Int) = if (meta >= 2) AlfheimItems.elvenFood else null
	
	override fun damageDropped(meta: Int) = if (meta >= 2) ElvenFoodMetas.TreeBerryBarrier.ordinal + type else 0
	
	override fun quantityDropped(random: Random?) = 1
	
	override fun quantityDropped(meta: Int, fortune: Int, random: Random?) = if (meta >= 2) 1 else 0
	
	override fun quantityDroppedWithBonus(meta: Int, random: Random?) = if (meta >= 2) 1 else 0
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.treeBerry
	
	override fun getPlantType(world: IBlockAccess?, x: Int, y: Int, z: Int) = EnumPlantType.Plains
	
	override fun getPlant(world: IBlockAccess, x: Int, y: Int, z: Int) = world.getBlock(x, y, z)!!
	
	override fun getPlantMetadata(world: IBlockAccess, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z)
	
	override fun getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): ArrayList<ItemStack> {
		return arrayListOf(ItemStack(getItemDropped(metadata, world.rand, fortune) ?: return arrayListOf(), quantityDropped(metadata, fortune, world.rand), damageDropped(metadata)))
	}
	
	companion object {
		// this is here and not in render class for it to be safely got in `getRenderType`
		val hasModelErrors = hashMapOf(0 to true, 1 to true, 5 to true)
	}
}
