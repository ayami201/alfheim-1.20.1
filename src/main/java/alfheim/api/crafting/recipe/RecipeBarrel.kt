package alfheim.api.crafting.recipe

import alfheim.common.block.tile.*
import net.minecraft.entity.player.*
import net.minecraft.item.*
import vazkii.botania.api.internal.*

abstract class RecipeBarrel(val initStack: ItemStack, private val outputStack: ItemStack) {
	         fun isInitStack(stack: ItemStack) = initStack.isItemEqual(stack)
	abstract fun onInteractedWith(tile: TileBarrel, player: EntityPlayer?, stack: ItemStack): Boolean
	abstract fun acceptsInput(tile: TileBarrel, stack: ItemStack?): Boolean
	abstract fun acceptInput(tile: TileBarrel, stack: ItemStack?)
	abstract fun getStackLimit(tile: TileBarrel): Int
	abstract fun serverTick(tile: TileBarrel)
	abstract fun getComparatorValue(tile: TileBarrel): Int
	open     fun onBurstCollision(tile: TileBarrel, burst: IManaBurst) = Unit
	open     fun getOutputStack(tile: TileBarrel) = outputStack
	abstract fun renderLiquid(tile: TileBarrel, f5: Float)
}