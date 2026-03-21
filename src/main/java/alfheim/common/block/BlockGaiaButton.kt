package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.api.item.ColorOverrideHelper
import alfheim.client.gui.GUIEditGaiaButton
import alfheim.common.block.tile.TileGaiaButton
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.*
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.api.wand.IWandable
import vazkii.botania.common.item.ModItems
import kotlin.math.*

class BlockGaiaButton: BlockButton(false), ITileEntityProvider, IWandable, ILexiconable {
	
	init {
		setBlockName("GaiaButton")
		setCreativeTab(AlfheimTab)
		setHardness(0.5f)
		setStepSound(soundTypeWood)
	}
	
	override fun onUsedByWand(player: EntityPlayer, stack: ItemStack?, world: World, x: Int, y: Int, z: Int, side: Int): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileGaiaButton ?: return false
		tile.delay = max(10, min(tile.delay + if (player.isSneaking) -10 else 10, 1200))
		if (!world.isRemote) ASJUtilities.say(player, "${tile.delay}")
		return true
	}
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.gaiaButton
	
	// TileEntity stuff
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileGaiaButton()
	
	override fun onBlockEventReceived(world: World, x: Int, y: Int, z: Int, eventID: Int, eventParameter: Int) = world.getTileEntity(x, y, z)?.receiveClientEvent(eventID, eventParameter) ?: false
	
	// BlockButton stuff
	
	override fun tickRate(world: World?) = 10 // ignoring this
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val stack = player?.heldItem
		if (stack?.item === AlfheimItems.chalk) {
			val tile = world.getTileEntity(x, y, z) as? TileGaiaButton ?: return false
			
			if (!world.isRemote) {
				tile.color = ColorOverrideHelper.getColor(player, 0xFFD400)
				ASJUtilities.dispatchTEToNearbyPlayers(tile)
			} else {
				mc.displayGuiScreen(GUIEditGaiaButton(tile))
			}
			
			return true
		}
		
		if (stack?.item === ModItems.twigWand) return false
		
		val meta = world.getBlockMetadata(x, y, z)
		val isPressed = 8 - (meta and 8)
		if (isPressed == 0) return true
		
		val metaBase = meta and 7
		world.setBlockMetadataWithNotify(x, y, z, metaBase + isPressed, 3)
		world.markBlockRangeForRenderUpdate(x, y, z, x, y, z)
		world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3f, 0.6f)
		func_150042_a(world, x, y, z, metaBase)
		world.scheduleBlockUpdate(x, y, z, this, (world.getTileEntity(x, y, z) as? TileGaiaButton)?.delay ?: tickRate(world))
		return true
	}
	
	// BlockMod stuff
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
		return super.setBlockName(name)
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	
	override fun getIcon(side: Int, meta: Int) = AlfheimBlocks.sealingPlanks.getIcon(side, meta)!!
	
	override fun getRenderColor(meta: Int) = 0xCCFFCC
	
	override fun colorMultiplier(world: IBlockAccess?, x: Int, y: Int, z: Int) = 0xCCFFCC
}
