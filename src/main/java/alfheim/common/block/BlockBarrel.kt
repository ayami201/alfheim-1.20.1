package alfheim.common.block

import alfheim.api.lib.*
import alfheim.common.block.base.*
import alfheim.common.block.tile.*
import alfheim.common.lexicon.*
import net.minecraft.block.material.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.entity.*
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.*
import vazkii.botania.api.internal.*
import vazkii.botania.api.lexicon.*
import vazkii.botania.api.mana.*

class BlockBarrel: BlockContainerMod(Material.wood), ILexiconable, IManaTrigger {
	
	init {
		setBlockName("barrel")
		setHardness(1f)
		setLightOpacity(0)
		setStepSound(soundTypeWood)
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (world.isRemote) return true
		
		val tile = world.getTileEntity(x, y, z) as? TileBarrel ?: return false
		val did = onBlockActivated(tile, player)
		if (!did) return false
		
		player.swingItem()
		tile.sync()
		if (player is EntityPlayerMP)
			player.sendContainerToPlayer(player.inventoryContainer)
		
		return true
	}
	
	fun onBlockActivated(tile: TileBarrel, player: EntityPlayer): Boolean {
		val stack = player.heldItem
		
		if (stack == null) {
			if (player.isSneaking) {
				tile.closed = !tile.closed
				return true
			}
			
			return false
		}
		
		return if (!tile.closed) tile.recipe?.onInteractedWith(tile, player, stack) ?: tile.selectRecipeMatchingFirstInput(player, stack) else false
	}
	
	override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase?, stack: ItemStack?) {
		if (stack?.hasDisplayName() != true) return
		val tile = world.getTileEntity(x, y, z) as? TileBarrel ?: return
		tile.name = stack.displayName.trim()
	}
	
	override fun hasComparatorInputOverride() = true
	
	override fun getComparatorInputOverride(world: World, x: Int, y: Int, z: Int, side: Int): Int {
		val tile = world.getTileEntity(x, y, z) as? TileBarrel ?: return 0
		return tile.recipe?.getComparatorValue(tile) ?: 0
	}
	
	override fun onBurstCollision(burst: IManaBurst, world: World, x: Int, y: Int, z: Int) {
		val tile = world.getTileEntity(x, y, z) as? TileBarrel ?: return
		tile.recipe?.onBurstCollision(tile, burst)
	}
	
	override fun createNewTileEntity(world: World, meta: Int) = TileBarrel()
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun getIcon(side: Int, meta: Int) = Blocks.planks.getIcon(side, meta)!!
	override fun renderAsNormalBlock() = false
	override fun isOpaqueCube() = false
	override fun getRenderType() = LibRenderIDs.idBarrel
	
	override fun setBlockBoundsBasedOnState(world: IBlockAccess?, x: Int, y: Int, z: Int) {
		setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f)
		super.setBlockBoundsBasedOnState(world, x, y, z)
	}
	
	override fun addCollisionBoxesToList(world: World?, x: Int, y: Int, z: Int, aabb: AxisAlignedBB?, list: MutableList<Any?>?, entity: Entity?) {
		val tile = world?.getTileEntity(x, y, z) as? TileBarrel
		
		val f = 2f / 16f
		
		setBlockBounds(0f, 0f, 0f, 1f, f, 1f)
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity)
		
		if (tile != null && tile.closed) {
			setBlockBounds(0f, 1 - f, 0f, 1f, 1f, 1f)
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity)
		}
		
		setBlockBounds(0f, 0f, 0f, f, 1f, 1f)
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity)
		
		setBlockBounds(0f, 0f, 0f, 1f, 1f, f)
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity)
		
		setBlockBounds(1 - f, 0f, 0f, 1f, 1f, 1f)
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity)
		
		setBlockBounds(0f, 0f, 1 - f, 1f, 1f, 1f)
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity)
	}
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.winery
}
