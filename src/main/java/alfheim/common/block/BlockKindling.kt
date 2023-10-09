package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.common.block.base.BlockMod
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.IFuelHandler
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.lexicon.ILexiconable

class BlockKindling: BlockMod(Material.cloth), IFuelHandler, ILexiconable {
	
	init {
		setBlockName("kindling")
		setHardness(0.2f)
		setStepSound(soundTypeCloth)
		GameRegistry.registerFuelHandler(this)
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, meta: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (player.heldItem != null) return false
		
		if (world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z)) {
			world.setBlock(x, y + 1, z, Blocks.fire)
			world.setBlockMetadataWithNotify(x, y, z, 1, 3)
			world.playSoundEffect(x.D + 0.5, y.D + 0.5, z.D + 0.5, "fire.ignite", 1f, Math.random().F * 0.4F + 0.8F)
			return true
		} else if (world.getBlock(x, y + 1, z) == Blocks.fire) {
			world.setBlock(x, y + 1, z, Blocks.air)
			world.setBlockMetadataWithNotify(x, y, z, 0, 3)
			world.playSoundEffect(x.D + 0.5, y.D + 0.5, z.D + 0.5, "random.fizz", 1f, Math.random().F * 0.4F + 0.8F)
			return true
		}
		
		return false
	}
	
	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block?) {
		world.setBlock(x, y + 1, z, if (world.getBlockMetadata(x, y, z) == 1 || world.getStrongestIndirectPower(x, y, z) == 15) Blocks.fire else Blocks.air)
	}
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 1200 else 0
	
	override fun isFireSource(world: World?, x: Int, y: Int, z: Int, side: ForgeDirection) = side == ForgeDirection.UP
	
	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, side: ForgeDirection) = if (side == ForgeDirection.UP) 500 else 0
	
	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, side: ForgeDirection) = side == ForgeDirection.UP
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.kindling
}
