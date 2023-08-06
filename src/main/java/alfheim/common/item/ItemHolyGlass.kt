package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.WorkInProgressItemsHandler.WIP
import cpw.mods.fml.common.eventhandler.Event
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.*
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.FillBucketEvent
import vazkii.botania.common.core.helper.ItemNBTHelper

class ItemHolyGlass: ItemMod("HolyGlass") {
	
	init {
		setMaxStackSize(1)
		WIP()
	}
	
	override fun onUpdate(stack: ItemStack, world: World, entity: Entity, slot: Int, inHand: Boolean) {
		if (world.isRemote) return
		
		if (stack.meta != 1 || !inHand || !world.isDaytime || entity.getBrightness(1f) <= 0.5f || !world.canBlockSeeTheSky(entity.posX.mfloor(), entity.posY.mfloor(), entity.posZ.mfloor())) {
			stack.timer = 0
			return
		}
		
		if (stack.timer++ >= 6000) {
			stack.timer = 0
			stack.meta = 2
		}
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack? {
		val position = getMovingObjectPositionFromPlayer(world, player, stack.meta == 0) ?: return stack
		
		val event = FillBucketEvent(player, stack, world, position)
		if (MinecraftForge.EVENT_BUS.post(event))
			return stack
		
		if (event.getResult() == Event.Result.ALLOW) {
			if (player.capabilities.isCreativeMode)
				return stack
			
			if (--stack.stackSize <= 0)
				return event.result
			
			if (!player.inventory.addItemStackToInventory(event.result))
				player.dropPlayerItemWithRandomChoice(event.result, false)
			
			return stack
		}
		
		if (position.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			var clickX = position.blockX
			var clickY = position.blockY
			var clickZ = position.blockZ
			
			if (!world.canMineBlock(player, clickX, clickY, clickZ))
				return stack
			
			if (world.getBlockMetadata(clickX, clickY, clickZ) == 0 && stack.meta == 0) {
				if (!player.canPlayerEdit(clickX, clickY, clickZ, position.sideHit, stack))
					return stack
				
				when (world.getBlock(clickX, clickY, clickZ)) {
					Blocks.water ->
						if (world.setBlockToAir(clickX, clickY, clickZ) && !player.capabilities.isCreativeMode)
							return ItemStack(this, 1, 1)
					
					AlfheimBlocks.manaFluidBlock ->
						if (world.setBlockToAir(clickX, clickY, clickZ) && !player.capabilities.isCreativeMode)
							return ItemStack(this, 1, 2)
				}
			}
			
			if (position.sideHit == 0) --clickY
			if (position.sideHit == 1) ++clickY
			if (position.sideHit == 2) --clickZ
			if (position.sideHit == 3) ++clickZ
			if (position.sideHit == 4) --clickX
			if (position.sideHit == 5) ++clickX
			
			if (!player.canPlayerEdit(clickX, clickY, clickZ, position.sideHit, stack))
				return stack
			
			if (tryPlaceContainedLiquid(world, clickX, clickY, clickZ, stack.meta) && !player.capabilities.isCreativeMode)
				return ItemStack(this, 1, 0)
			
		}
		
		return stack
	}
	
	fun tryPlaceContainedLiquid(world: World, clickX: Int, clickY: Int, clickZ: Int, meta: Int): Boolean {
		if (!world.isAirBlock(clickX, clickY, clickZ) && world.getBlock(clickX, clickY, clickZ).material.isSolid) return false
		
		val block = when (meta) {
			1 -> Blocks.flowing_water
			2 -> AlfheimBlocks.manaFluidBlock
			else -> return false
		}
		
		world.setBlock(clickX, clickY, clickZ, block, 0, 3)
		
		return true
	}
	
	lateinit var icons: Array<IIcon>
	
	@SideOnly(Side.CLIENT)
	override fun getIconFromDamage(meta: Int) = icons.safeGet(meta)
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		icons = Array(3) { IconHelper.forItem(reg, this, it) }
	}
	
	companion object {
		private const val TAG_TIMER = "timer"
		
		private var ItemStack.timer
			get() = ItemNBTHelper.getInt(this, TAG_TIMER, 0)
			set(value) = ItemNBTHelper.setInt(this, TAG_TIMER, value)
	}
}
