package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileItemDisplay
import alfheim.common.item.block.ItemUniqueSubtypedBlockMod
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.IFuelHandler
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.api.lexicon.ILexiconable

class BlockItemDisplay: BlockContainerMod(Material.wood), ILexiconable, IFuelHandler {
	
	lateinit var icons: Array<IIcon>
	lateinit var sideIcons: Array<IIcon>
	
	init {
		isBlockContainer = true
		setBlockName("itemDisplay")
		setHardness(2f)
		setStepSound(soundTypeWood)
		setBlockBounds(0f, 0f, 0f, 1f, 0.5f, 1f)
		
		GameRegistry.registerFuelHandler(this)
	}
	
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (list != null && item != null)
			for (i in 0 until TYPES)
				list.add(ItemStack(item, 1, i))
	}
	
	override fun hasComparatorInputOverride() = true
	
	override fun getComparatorInputOverride(world: World, x: Int, y: Int, z: Int, direction: Int): Int {
		val tile = world.getTileEntity(x, y, z) as? TileItemDisplay ?: return 0
		return if (tile.item != null) 15 else 0
	}
	
	override fun shouldRegisterInNameSet() = false
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemUniqueSubtypedBlockMod::class.java, name, TYPES)
		return super.setBlockName(name)
	}
	
	override fun addCollisionBoxesToList(world: World, x: Int, y: Int, z: Int, axis: AxisAlignedBB, bounds: MutableList<Any?>, entity: Entity?) {
		setBlockBounds(0f, 0f, 0f, 1f, 0.5F, 1f)
		super.addCollisionBoxesToList(world, x, y, z, axis, bounds, entity)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = Array(TYPES) { IconHelper.forBlock(reg, this, it) }
		sideIcons = Array(TYPES) { IconHelper.forBlock(reg, this, "Side$it") }
	}
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileItemDisplay()
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int) =
		if (side == 1 || side == 0) icons[meta % TYPES] else sideIcons[meta % TYPES]
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, meta: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (world.isRemote) return true
		
		val tile = world.getTileEntity(x, y, z) as? TileItemDisplay ?: return false
		
		val stack = tile[0]?.copy()
		if (stack != null) {
			if (!player.inventory.addItemStackToInventory(stack))
				player.dropPlayerItemWithRandomChoice(stack, false)
			else if (player is EntityPlayerMP)
				player.sendContainerToPlayer(player.inventoryContainer)
			
			tile[0] = null
			return true
		}
		
		if (player.heldItem != null) {
			val item = player.heldItem.copy()
			item.stackSize = 1
			tile[0] = item
			
			--player.heldItem.stackSize
			if (player.heldItem.stackSize == 0)
				player.setCurrentItemOrArmor(0, null)
			
			if (player is EntityPlayerMP)
				player.sendContainerToPlayer(player.inventoryContainer)
		}
		
		return true
	}
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	
	override fun damageDropped(meta: Int): Int = meta
	
	override fun isOpaqueCube(): Boolean = false
	
	override fun renderAsNormalBlock(): Boolean = false
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, meta: Int) {
		val tile = world.getTileEntity(x, y, z)
		
		if (tile is TileItemDisplay && tile[0] != null) {
			EntityItem(world, x.D, y.D, z.D, tile[0]).spawn()
			tile[0] = null
		}
		
		super.breakBlock(world, x, y, z, block, meta)
	}
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, stack: ItemStack?) = AlfheimLexiconData.itemDisplay
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 150 else 0
	
	companion object {
		const val TYPES = 5
	}
}
