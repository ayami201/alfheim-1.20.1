package alfheim.common.block

import alexsocol.asjlib.toItem
import alfheim.api.lib.LibOreDict
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.item.block.ItemUniqueSubtypedBlockMod
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.*
import net.minecraft.item.*
import net.minecraft.world.*
import java.util.*

class BlockSadOakLeaves: BlockLeavesMod() {
	
	val SUBTYPES = 1
	
	init {
		setBlockName("leaves")
	}
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemUniqueSubtypedBlockMod::class.java, name, SUBTYPES)
	}
	
	@SideOnly(Side.CLIENT)
	override fun getBlockColor() = ColorizerFoliage.getFoliageColor(0.5, 1.0)
	
	@SideOnly(Side.CLIENT)
	override fun getRenderColor(meta: Int) = if (meta and 7 == 0) ColorizerFoliage.getFoliageColorBasic() else 0
	
	@SideOnly(Side.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int): Int {
		if (world.getBlockMetadata(x, y, z) and 7 != 0) return 0
		
		var r = 0
		var g = 0
		var b = 0
		
		for (k in -1..1) {
			for (j in -1..1) {
				val c = world.getBiomeGenForCoords(x + j, z + k).getBiomeFoliageColor(x + j, y, z + k)
				r += c and 0xFF0000 shr 16
				g += c and 0x00FF00 shr 8
				b += c and 0x0000FF
			}
		}
		
		return r / 9 and 0xFF shl 16 or (g / 9 and 0xFF shl 8) or (b / 9 and 0xFF)
	}
	
	override fun func_150124_c(world: World, x: Int, y: Int, z: Int, meta: Int, chance: Int) {
		if (meta and 7 == 0 && world.rand.nextInt(chance) == 0) dropBlockAsItem(world, x, y, z, ItemStack(Items.apple, 1, 0))
	}
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int) = if (meta and 7 == 0) Blocks.leaves.getIcon(side, 0) else null
	
	override fun getItem(world: World, x: Int, y: Int, z: Int) = if (world.getBlockMetadata(x, y, z) and 7 == 0) Blocks.leaves.toItem() else toItem()
	
	// drop chance
	override fun func_150123_b(meta: Int) = 20
	override fun quantityDropped(random: Random) = if (random.nextInt(func_150123_b(0)) == 0) 1 else 0
	override fun decayBit() = 0x8
	override fun getDecayRange(meta: Int) = if (meta and 7 == 0) 8 else 4
	override fun func_150125_e() = arrayOf("oak")
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = null
}
