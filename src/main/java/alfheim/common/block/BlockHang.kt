package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.common.entity.EntityFallingHang
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.World
import java.util.*

open class BlockHang(mat: Material, name: String, sub: Int, val fallable: Boolean = true): BlockModMeta(mat, sub, ModInfo.MODID, name, AlfheimTab, 0.3f) {
	
	init {
		tickRandomly = fallable
	}
	
	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) = checkChange(world, x, y, z)
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		if (fallable) {
			val nextMeta = world.getBlockMetadata(x, y, z) + 1
			
			if (random.nextInt(4 * nextMeta) == 0) {
				if (nextMeta >= subtypes) {
					fall(world, x, y - 1, z)
					world.setBlock(x, y, z, this, 0, 3)
					return
				} else
					world.setBlockMetadataWithNotify(x, y, z, nextMeta, 3)
			}
		}
		
		checkChange(world, x, y, z)
	}
	
	fun checkChange(world: World, x: Int, y: Int, z: Int) {
		if (canBlockStay(world, x, y, z)) return
		fall(world, x, y, z)
	}
	
	fun fall(world: World, x: Int, y: Int, z: Int) {
		if (fallable) EntityFallingHang(world).apply {
			setPosition(x + 0.5, y.D, z + 0.5)
			block = this@BlockHang
			meta = world.getBlockMetadata(x, y, z)
		}.spawn()
		world.setBlockToAir(x, y, z)
	}
	
	override fun canPlaceBlockAt(world: World, x: Int, y: Int, z: Int) = canBlockStay(world, x, y, z)
	
	override fun getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int) = null
	
	override fun isOpaqueCube() = false
	
	override fun renderAsNormalBlock() = false
	
	override fun getRenderType() = 1
}