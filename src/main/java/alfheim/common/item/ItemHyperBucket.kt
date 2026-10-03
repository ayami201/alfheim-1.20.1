package alfheim.common.item

// PORT: импорты 1.20.1 (MAPPING.md); луч — alfheim.port.legacy.Hit1710; счётчик на экране (ItemsRemainingRenderHandler) — КТ-4
import alexsocol.asjlib.*
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fluids.capability.IFluidHandler
//import alfheim.client.gui.ItemsRemainingRenderHandler
//import net.minecraft.init.Blocks

class ItemHyperBucket: ItemMod("HyperpolatedBucket") {
	
	init {
		creativeTab = AlfheimTab
		maxStackSize = 1
	}
	
	// PORT: onItemRightClick → use; дальность руки — blockReach Forge (у автора — theItemInWorldManager.blockReachDistance)
	override fun use(world: World, player: EntityPlayer, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		if (player.isShiftKeyDown) {
			setRange(stack, (getRange(stack) + 1) % (getMaxRange(stack) + 1))
			val r = getRange(stack) * 2 + 1
			
			// PORT: КТ-4 — счётчик на экране (ItemsRemainingRenderHandler)
//			if (world.isRemote && player === mc.thePlayer)
//				ItemsRemainingRenderHandler.set(stack, "${r}x$r")
			
			return InteractionResultHolder.consume(stack)
		}
		
		if (player !is EntityPlayerMP) return InteractionResultHolder.pass(stack)
		val mop = ASJUtilities.getSelectedBlock(player, player.blockReach, true) ?: return InteractionResultHolder.pass(stack)
		
		if (mop.typeOfHit != MovingObjectType.BLOCK) return InteractionResultHolder.pass(stack)
//		if (player.isSneaking) {
//		if (player !is EntityPlayerMP) return stack
//		val mop = ASJUtilities.getSelectedBlock(player, player.theItemInWorldManager.blockReachDistance, true) ?: return stack
//		if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return stack
		
		val x = mop.blockX
		val y = mop.blockY
		val z = mop.blockZ
		
		val block = world.getBlock(x, y, z)
		val range = getRange(stack)
		
		for (j in y.inRange(range).reversed())
			for (i in x.inRange(range))
				for (k in z.inRange(range)) {
					val at = world.getBlock(i, j, k)
					if (at is IFluidBlock && !at.canDrain(world, BlockPos(i, j, k))) continue
//					if (at is IFluidBlock && !at.canDrain(world, i, j, k)) continue
					
					// PORT: материала 1.7.10 нет — жидкость у блока 1.20.1 — свойство состояния liquid
					if (!world.getBlockState(BlockPos(i, j, k)).liquid()) continue
//					val material = at.material
//					if (!material.isLiquid) continue
					
					// PORT: стоячая и текучая жидкость в 1.20.1 — один блок (уровень — свойство состояния): сравнения
					// автора «вода — текучая вода», «лава — текучая лава» сводятся к одному
					if (at !== block) continue
//					@Suppress("ControlFlowWithEmptyBody")
//					if (block === Blocks.lava && at === Blocks.flowing_lava) ; else
//						if (block === Blocks.flowing_lava && at === Blocks.lava) ; else
//							if (block === Blocks.water && at === Blocks.flowing_water) ; else
//								if (block === Blocks.flowing_water && at === Blocks.water) ; else
//									if (at !== block) continue
					
					if (at is IFluidBlock) at.drain(world, BlockPos(i, j, k), IFluidHandler.FluidAction.EXECUTE)
					else world.setBlockToAir(i, j, k)
//					if (at is IFluidBlock) at.drain(world, i, j, k, true)
					
					for (f in 0..4)
						world.spawnParticle("explode", i + Math.random(), j + Math.random(), k + Math.random(), 0.0, 0.0, 0.0)
				}
		
		return InteractionResultHolder.consume(stack)
	}
	
	// PORT: addInformation → appendHoverText
	override fun appendHoverText(stack: ItemStack, world: World?, tooltip: MutableList<Component>, adv: TooltipFlag) {
		val r = getRange(stack) * 2 + 1
		tooltip.add(Component.literal("$r x $r"))
	}
	
	companion object {
		
		const val TAG_RANGE = "range"
		
		private fun Int.inRange(range: Int) = (this - range)..(this + range)
		
		fun getMaxRange(stack: ItemStack) = stack.meta + 1
		
		fun getRange(stack: ItemStack) = ItemNBTHelper.getInt(stack, TAG_RANGE, getMaxRange(stack))
		
		fun setRange(stack: ItemStack, range: Int) = ItemNBTHelper.setInt(stack, TAG_RANGE, range)
	}
}
