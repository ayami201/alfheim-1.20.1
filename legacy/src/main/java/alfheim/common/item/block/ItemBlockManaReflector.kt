package alfheim.common.item.block

import alfheim.api.ModInfo
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.world.World

class ItemBlockManaReflector(block: Block): ItemBlock(block) {
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		getUnlocalizedNameInefficiently_(stack).replace("tile.", "tile.${ModInfo.MODID}:")
	
	fun getUnlocalizedNameInefficiently_(stack: ItemStack) = super.getUnlocalizedNameInefficiently(stack)!!
	
	override fun placeBlockAt(stack: ItemStack?, player: EntityPlayer?, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int): Boolean {
		val result = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)
		if (!result) return false
		
		val meta = when (side) {
			0 -> {
				when (findSector(hitX, hitZ)) {
					1 -> 6
					2 -> 5
					3 -> 4
					4 -> 7
					else -> return true
				}
			}
			1 -> {
				when (findSector(hitX, hitZ)) {
					1 -> 2
					2 -> 1
					3 -> 0
					4 -> 3
					else -> return true
				}
			}
			2 -> {
				when (findSector(hitX, hitY)) {
					1 -> 6
					2 -> 9
					3 -> 2
					4 -> 10
					else -> return true
				}
			}
			3 -> {
				when (findSector(hitX, hitY)) {
					1 -> 4
					2 -> 8
					3 -> 0
					4 -> 11
					else -> return true
				}
			}
			4 -> {
				when (findSector(hitY, hitZ)) {
					1 -> 9
					2 -> 5
					3 -> 8
					4 -> 1
					else -> return true
				}
			}
			5 -> {
				when (findSector(hitY, hitZ)) {
					1 -> 10
					2 -> 7
					3 -> 11
					4 -> 3
					else -> return true
				}
			}
			else -> return true
		}
		
		world.setBlockMetadataWithNotify(x, y, z, meta, 3)
		
		return true
	}
	
	fun findSector(a: Float, b: Float): Int {
		val isAboveFirstDiagonal = b > a
		val isAboveSecondDiagonal = b > 1 - a
		
		return when {
			isAboveFirstDiagonal && isAboveSecondDiagonal -> 1 // Верхний сектор
			!isAboveFirstDiagonal && isAboveSecondDiagonal -> 2 // Левый сектор
			!isAboveFirstDiagonal && !isAboveSecondDiagonal -> 3 // Нижний сектор
			else -> 4 // Правый сектор
		}
	}
}
