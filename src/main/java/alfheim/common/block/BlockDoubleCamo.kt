package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileDoubleCamo
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.particle.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import vazkii.botania.api.wand.IWandable
import vazkii.botania.common.core.helper.ItemNBTHelper
import kotlin.math.max

abstract class BlockDoubleCamo(material: Material = Material.wood, val info: Boolean = true): BlockContainerMod(material), IWandable {
	
	init {
		setStepSound(soundTypeWood)
	}
	
	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int): Int {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleCamo ?: return 0
		return tile.getLightValue()
	}
	
	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int): Float {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleCamo ?: return 1f
		return max(tile.blockTop.getBlockHardness(world, x, y, z), tile.blockBottom.getBlockHardness(world, x, y, z))
	}
	
	override fun onUsedByWand(player: EntityPlayer, stack: ItemStack?, world: World, x: Int, y: Int, z: Int, side: Int): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleCamo ?: return false
		
		if (player.isSneaking) {
			tile.locked = !tile.locked
		} else {
			if (tile.locked) return false
			
			if (side == topSide(world, x, y, z)) tile.blockTopMeta = (tile.blockTopMeta + 1) % 16
			else tile.blockBottomMeta = (tile.blockBottomMeta + 1) % 16
		}
		
		ASJUtilities.dispatchTEToNearbyPlayers(tile)
		
		return true
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleCamo ?: return false
		
		val stack = player.heldItem ?: return false
		if (tile.locked) return false
		
		val block = stack.block
		if (block === Blocks.air || block is ITileEntityProvider || block.hasTileEntity(stack.meta)) return false
		
		val meta = stack.meta
		
		if (side == topSide(world, x, y, z)) {
			tile.blockTop = block
			tile.blockTopMeta = meta
		} else {
			tile.blockBottom = block
			tile.blockBottomMeta = meta
		}
		
		tile.recalculateLight()
		tile.markDirty()
		
		ASJUtilities.dispatchTEToNearbyPlayers(tile)
		
		return true
	}
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, meta: Int) {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleCamo
		if (tile?.noDrop == false) {
			val stack = ItemStack(block, 1, block.damageDropped(meta))
			tile.writeCustomNBT(ItemNBTHelper.getNBT(stack))
			EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack).spawn()
		}
		super.breakBlock(world, x, y, z, block, meta)
	}
	
	override fun getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int) = arrayListOf<ItemStack>()
	
	@Suppress("OVERRIDE_DEPRECATION", "DEPRECATION") // stupid WAILA uses deprecated method -_-
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int): ItemStack {
		val result = super.getPickBlock(target, world, x, y, z)
		
		(world.getTileEntity(x, y, z) as? TileDoubleCamo)?.writeCustomNBT(ItemNBTHelper.getNBT(result))
		
		return result
	}
	
	override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase, stack: ItemStack) {
		if (ItemNBTHelper.detectNBT(stack)) (world.getTileEntity(x, y, z) as? TileDoubleCamo)?.readCustomNBT(ItemNBTHelper.getNBT(stack))
	}
	
	override fun addHitEffects(world: World, mop: MovingObjectPosition, effectRenderer: EffectRenderer?): Boolean {
		val tile = world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) as? TileDoubleCamo ?: return true
		
		val x = mop.blockX
		val y = mop.blockY
		val z = mop.blockZ
		val side = mop.sideHit
		val ourMeta = world.getBlockMetadata(x, y, z)
		
		val top = world.rand.nextInt(6) == topSide(world, x, y, z)
		val block = if (top) tile.blockTop else tile.blockBottom
		val meta = if (top) tile.blockTopMeta else tile.blockBottomMeta
		
		val f = 0.1
		var i = x + world.rand.nextDouble() * (block.blockBoundsMaxX - block.blockBoundsMinX - f * 2) + f + block.blockBoundsMinX
		var j = y + world.rand.nextDouble() * (block.blockBoundsMaxY - block.blockBoundsMinY - f * 2) + f + block.blockBoundsMinY
		var k = z + world.rand.nextDouble() * (block.blockBoundsMaxZ - block.blockBoundsMinZ - f * 2) + f + block.blockBoundsMinZ
		
		when (side) {
			0 -> j = y + block.blockBoundsMinY - f
			1 -> j = y + block.blockBoundsMaxY + f
			2 -> k = z + block.blockBoundsMinZ - f
			3 -> k = z + block.blockBoundsMaxZ + f
			4 -> i = x + block.blockBoundsMinX - f
			5 -> i = x + block.blockBoundsMaxX + f
		}
		
		world.setBlockMetadataWithNotify(x, y, z, meta, 4)
		
		try {
			mc.effectRenderer.addEffect(EntityDiggingFX(world, i, j, k, 0.0, 0.0, 0.0, block, meta, side).applyColourMultiplier(x, y, z).multiplyVelocity(0.2f).multipleParticleScaleBy(0.6f))
		} catch (ignore: Throwable) {}
		
		world.setBlockMetadataWithNotify(x, y, z, ourMeta, 4)
		
		return true
	}
	
	override fun addDestroyEffects(world: World, x: Int, y: Int, z: Int, ourMeta: Int, effectRenderer: EffectRenderer?): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleCamo ?: return true
		
		val side = world.rand.nextInt(6)
		val top = side == topSide(world, x, y, z)
		val block = if (top) tile.blockTop else tile.blockBottom
		val meta = if (top) tile.blockTopMeta else tile.blockBottomMeta
		
		world.setBlockMetadataWithNotify(x, y, z, meta, 4)
		
		try {
			for (i1 in 0 until 4) {
				for (j1 in 0 until 4) {
					for (k1 in 0 until 4) {
						val i = x + (i1 + 0.5) / 4
						val j = y + (j1 + 0.5) / 4
						val k = z + (k1 + 0.5) / 4
						mc.effectRenderer.addEffect(EntityDiggingFX(world, i, j, k, i - x - 0.5, j - y - 0.5, k - z - 0.5, block, meta, side).applyColourMultiplier(x, y, z))
					}
				}
			}
		} catch (ignore: Throwable) {}
		
		world.setBlockMetadataWithNotify(x, y, z, ourMeta, 4)
		
		return true
	}
	
	override fun canRenderInPass(pass: Int): Boolean {
		ForgeHooksClient.setRenderPass(pass)
		return true
	}
	
	override fun getRenderBlockPass() = if (ForgeHooksClient.getWorldRenderPass() < 0) 0 else 1
	open fun topSide(world: IBlockAccess, x: Int, y: Int, z: Int) = topSide(world.getBlockMetadata(x, y, z))
	abstract fun topSide(meta: Int): Int
	abstract override fun getRenderType(): Int
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun getIcon(side: Int, meta: Int) = null
	override fun getIcon(world: IBlockAccess?, x: Int, y: Int, z: Int, side: Int) = null
	
	companion object {
		
		init {
			if (ASJUtilities.isClient) eventForge()
		}
		
		@SubscribeEvent
		fun provideCompositeDetails(e: ItemTooltipEvent) {
			val block = e.itemStack?.block as? BlockDoubleCamo ?: return
			if (!block.info) return
			
			val tile = TileDoubleCamo()
			tile.readCustomNBT(e.itemStack.tagCompound ?: return)
			
			@Suppress("UNCHECKED_CAST")
			val list = e.toolTip as MutableList<Any?>
			
			if (!GuiScreen.isShiftKeyDown()) {
				addStringToTooltip(list, StatCollector.translateToLocal("botaniamisc.shiftinfo"))
				return
			}
			
			addStringToTooltip(list, ItemStack(tile.blockTop, 1, tile.blockTopMeta).displayName)
			addStringToTooltip(list, ItemStack(tile.blockBottom, 1, tile.blockBottomMeta).displayName)
		}
	}
}