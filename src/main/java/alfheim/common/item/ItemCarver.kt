package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.client.gui.ItemsRemainingRenderHandler
import alfheim.common.block.*
import alfheim.common.block.tile.TileComposite
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraft.world.World

class ItemCarver: ItemMod("Carver") {
	
	init {
		setFull3D()
		setMaxStackSize(1)
		
		eventForge()
	}
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		addStringToTooltip(list, "${getUnlocalizedNameInefficiently(stack)}.mode.${stack.carverMode}")
	}
	
	override fun doesSneakBypassUse(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?) = true
	
	override fun onUpdate(stack: ItemStack, world: World?, entity: Entity?, slot: Int, inHand: Boolean) {
		if (stack.cooldown > 0) --stack.cooldown
	}
	
	@SubscribeEvent
	fun onItemRightClick(e: PlayerInteractAdequateEvent.RightClick) {
		if (e.action != PlayerInteractAdequateEvent.RightClick.Action.RIGHT_CLICK_AIR) return
		
		val stack = e.player.heldItem ?: return
		if (stack.item !== this) return
		
		stack.carverMode = CarverMode.entries[(stack.carverMode.ordinal + 1) % CarverMode.entries.size]
		
		ItemsRemainingRenderHandler.set(stack, StatCollector.translateToLocal("${getUnlocalizedNameInefficiently(stack)}.mode.${stack.carverMode}"))
	}
	
	@SubscribeEvent
	fun onItemLeftClick(e: PlayerInteractAdequateEvent.LeftClick) {
		val player = e.player
		if (e.action != PlayerInteractAdequateEvent.LeftClick.Action.LEFT_CLICK_BLOCK || ASJUtilities.isClient || player.capabilities.isCreativeMode) return
		
		val stack = player.heldItem ?: return
		if (stack.item !== this || stack.cooldown > 0) return
		stack.cooldown = 2
		
		val world = player.worldObj
		val block = world.getBlock(e.x, e.y, e.z)
		if (block is BlockDoubleCamo || block.isAir(world, e.x, e.y, e.z) || world.getTileEntity(e.x, e.y, e.z) != null || block.getPlayerRelativeBlockHardness(player, world, e.x, e.y, e.z) <= 0) return
		
		val meta = world.getBlockMetadata(e.x, e.y, e.z)
		
		world.setBlock(e.x, e.y, e.z, AlfheimFluffBlocks.composite)
		world.getTileEntity(e.x, e.y, e.z)?.apply {
			if (this !is TileComposite) return@apply
			blockBottom = block
			blockBottomMeta = meta
			composition = initArray()
		}
	}
	
	companion object {
		
		const val TAG_MODE = "mode"
		
		var ItemStack.carverMode: CarverMode
			get() = CarverMode.entries[ItemNBTHelper.getInt(this, TAG_MODE, 0)]
			set(value) = ItemNBTHelper.setInt(this, TAG_MODE, value.ordinal)
		
		enum class CarverMode {
			BIT, LINE, PLANE, ROTATE, FLIP, PUSH
		}
	}
}
