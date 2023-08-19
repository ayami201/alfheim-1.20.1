package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.api.AlfheimAPI
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
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
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.api.wand.*
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.ModItems
import kotlin.math.max

class BlockAnomalyHarvester: BlockContainerMod(Material.iron), IWandable, ILexiconable, IWandHUD {
	
	init {
		setBlockName("AnomalyHarvester")
		setLightOpacity(0)
		setHardness(5f)
		setResistance(2000f)
		setStepSound(Block.soundTypeMetal)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun getIcon(side: Int, meta: Int) = AlfheimBlocks.alfStorage.getIcon(side, 0)!!
	override fun renderAsNormalBlock() = false
	override fun isOpaqueCube() = false
	override fun getRenderType() = LibRenderIDs.idHarvester
	
	override fun createNewTileEntity(world: World, meta: Int) = TileAnomalyHarvester()
	
	override fun onBlockPlaced(world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, meta: Int) = ForgeDirection.VALID_DIRECTIONS.safeGet(side).ordinal
	
	override fun onBlockClicked(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?) {
		if (player?.heldItem?.item !== ModItems.twigWand)
			return super.onBlockClicked(world, x, y, z, player)
		
		var meta = world.getBlockMetadata(x, y, z)
		world.setBlockMetadataWithNotify(x, y, z, ++meta % 6, 3)
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val stack = player.heldItem
		
		if (stack?.item === ModItems.twigWand)
			return stack.item.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ)
		
		val tile = world.getTileEntity(x, y, z) as? TileAnomalyHarvester ?: return false
		
		if (stack?.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.RiftDrive.I) {
			val sub = ItemNBTHelper.getString(stack, TileAnomaly.TAG_SUBTILE_NAME, "")
			if (!AlfheimAPI.anomalyBehaviors.containsKey(sub)) return false
			if (!tile.addSubTile(sub)) return false
			
			if (--stack.stackSize <= 0)
				player.setCurrentItemOrArmor(0, null)
			
			return true
		}
		
		if (stack != null) return false
		
		tile.power = max(0, tile.power + if (player.isSneaking) -1 else 1)
		
		if (!world.isRemote)
			ASJUtilities.say(player, "alfheimmisc.power", tile.power)
		
		ASJUtilities.dispatchTEToNearbyPlayers(tile)
		
		return true
	}
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block?, meta: Int) {
		run {
			val te = world.getTileEntity(x, y, z) as? TileAnomalyHarvester ?: return@run
			te.subTiles.forEach {
				val stack = ElvenResourcesMetas.RiftDrive.stack
				ItemNBTHelper.setString(stack, TileAnomaly.TAG_SUBTILE_NAME, it)
				EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack).spawn()
			}
		}
		
		super.breakBlock(world, x, y, z, block, meta)
	}
	
	override fun onUsedByWand(player: EntityPlayer?, stack: ItemStack, world: World, x: Int, y: Int, z: Int, side: Int): Boolean {
		if (player == null) return false
		val tile = world.getTileEntity(x, y, z) as? TileAnomalyHarvester ?: return false
		val fd = ForgeDirection.getOrientation(side)
		
		if (player.isSneaking)
			tile.radius.set(max(1.0, tile.radius.x + fd.offsetX), max(1.0, tile.radius.y + fd.offsetY), max(1.0, tile.radius.z + fd.offsetZ)).also {
				if (!world.isRemote)
					ASJUtilities.say(player, "alfheimmisc.area", it.x, it.y, it.z)
			}
		else
			tile.offset.add(fd.offsetX * 0.5, fd.offsetY * 0.5, fd.offsetZ * 0.5).also {
				if (!world.isRemote)
					ASJUtilities.say(player, "alfheimmisc.offset", it.x, it.y, it.z)
			}
		
		ASJUtilities.dispatchTEToNearbyPlayers(tile)
		player.playSoundAtEntity("botania:ding", 0.1f, 1f)
		
		return true
	}
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.anomalyHarvester
	
	override fun renderHUD(mc: Minecraft, res: ScaledResolution, world: World, x: Int, y: Int, z: Int) {
		(world.getTileEntity(x, y, z) as TileAnomalyHarvester).renderHUD(res)
	}
}
