package alfheim.common.block.mana

import alexsocol.asjlib.*
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileManaTuner
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.api.mana.IManaTrigger
import vazkii.botania.api.wand.IWandHUD
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.tile.*

class BlockManaTuner: BlockContainerMod(Material.rock), ILexiconable, IManaTrigger, IWandHUD {
	
	init {
		setBlockName("ManaTuner")
		setHardness(1f)
		setStepSound(soundTypeStone)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	
	override fun getIcon(side: Int, meta: Int) = ModBlocks.livingrock.getIcon(side, 0)!!
	
	override fun isOpaqueCube() = false
	
	override fun renderAsNormalBlock() = false
	
	override fun getRenderType() = LibRenderIDs.idManaTuner
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val tile = world.getTileEntity(x, y, z) as TileManaTuner
		
		if (player.isSneaking) {
			for (i in tile.sizeInventory - 1 downTo 0) {
				val stackAt = tile[i] ?: continue
				val copy = stackAt.copy()
				
				if (!player.inventory.addItemStackToInventory(copy))
					player.dropPlayerItemWithRandomChoice(copy, false)
				
				tile[i] = null
				world.func_147453_f(x, y, z, this)
				
				ASJUtilities.dispatchTEToNearbyPlayers(tile)
				
				return true
			}
		} else run {
			val stack = player.heldItem ?: return@run
			
			for (i in 0 until tile.sizeInventory) {
				if (tile[i] != null) continue
				val copy = stack.copy()
				copy.stackSize = 1
				tile[i] = copy
				
				if (!player.capabilities.isCreativeMode) {
					stack.stackSize--
					
					if (stack.stackSize == 0)
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null)
				}
				
				ASJUtilities.dispatchTEToNearbyPlayers(tile)
				
				break
			}
			
			return true
		}
		
		return false
	}
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block?, meta: Int) {
		run {
			val tile = world.getTileEntity(x, y, z) as? TileSimpleInventory ?: return@run
			
			for (i in 0 until tile.sizeInventory) {
				val item = tile[i] ?: continue
				if (item.stackSize > 0)
					EntityItem(world, x.D, y.D, z.D, item.copy()).spawn()
			}
		}
		
		super.breakBlock(world, x, y, z, block, meta)
	}
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileManaTuner()
	
	override fun onBurstCollision(burst: IManaBurst, world: World, x: Int, y: Int, z: Int) {
		(world.getTileEntity(x, y, z) as? TileManaTuner)?.onBurstCollision(burst)
	}
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.manaTuner
	
	override fun renderHUD(mc: Minecraft, res: ScaledResolution, world: World, x: Int, y: Int, z: Int) {
		(world.getTileEntity(x, y, z) as? TileManaTuner)?.renderHUD(mc, res)
	}
}
