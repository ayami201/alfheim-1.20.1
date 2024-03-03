package alfheim.common.block

import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemBlockNidhoggTooth
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.material.Material
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.*

class BlockNidhoggTooth: BlockMod(Material.rock) {
	
	init {
		setBlockBounds(0.25f, 0.25f, 0f, 0.75f, 0.75f, 1f)
		setBlockName("NidhoggTooth")
		setHardness(10f)
		setHarvestLevel("pickaxe", 3)
		
		GameRegistry.registerBlock(this, ItemBlockNidhoggTooth::class.java, "NidhoggTooth")
	}
	
	override fun shouldRegisterInNameSet() = false
	
	override fun isOpaqueCube() = false
	
	override fun renderAsNormalBlock() = false
	
	override fun getRenderType() = LibRenderIDs.idNidhoggTooth
	
	override fun getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
		setBlockBoundsBasedOnState(world, x, y, z)
		return super.getCollisionBoundingBoxFromPool(world, x, y, z)
	}
	
	override fun addCollisionBoxesToList(world: World, x: Int, y: Int, z: Int, aabb: AxisAlignedBB?, list: MutableList<Any?>?, entity: Entity?) {
		setBlockBoundsBasedOnState(world, x, y, z)
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity)
	}
	
	override fun setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int) {
		val meta = world.getBlockMetadata(x, y, z) and 3
		
		when (meta) {
			0, 2 -> {
				setBlockBounds(0.25f, 0.25f, 0f, 0.75f, 0.75f, 1f)
			}
			1, 3 -> {
				setBlockBounds(0f, 0.25f, 0.25f, 1f, 0.75f, 0.75f)
			}
		}
	}
}
