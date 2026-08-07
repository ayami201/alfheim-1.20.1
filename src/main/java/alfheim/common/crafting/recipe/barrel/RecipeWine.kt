package alfheim.common.crafting.recipe.barrel

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alexsocol.asjlib.render.*
import alfheim.api.crafting.recipe.*
import alfheim.client.model.block.*
import alfheim.common.block.tile.*
import alfheim.common.item.material.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.item.*
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraftforge.event.entity.living.*
import net.minecraftforge.event.entity.player.*
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.internal.*
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.lens.*
import kotlin.math.*

open class RecipeWine(initStack: ItemStack, outputStack: ItemStack): RecipeBarrel(initStack, outputStack) {
	
	override fun onInteractedWith(tile: TileBarrel, player: EntityPlayer?, stack: ItemStack): Boolean {
		if (tile.closed) return false
		
		if (acceptsInput(tile, stack)) {
			val toPut = stack.copy()
			toPut.stackSize = 1
			acceptInput(tile, toPut)
			stack.stackSize -= 1 - toPut.stackSize
			
			return true
		}
		
		if (ElvenResourcesMetas.Jug.stack.isItemEqual(stack) && tile.ready && tile.amountLevel > 0) {
			val out = getOutputStack(tile).copy()
			
			player?.let {
				if (!it.inventory.addItemStackToInventory(out))
					it.dropPlayerItemWithRandomChoice(out, false)
			} ?: run {
				val (x, y, z) = Vector3.fromTileEntityCenter(tile)
				EntityItem(tile.worldObj, x, y, z, out).spawn()
			}
			
			--stack.stackSize
			tile.amountLevel -= 4
			
			if (tile.amountLevel <= 0)
				tile.reset()
			
			return true
		}
		
		return false
	}
	
	override fun acceptsInput(tile: TileBarrel, stack: ItemStack?): Boolean {
		stack ?: return false
		
		if (getComparatorValue(tile) == 0 && isInitStack(stack)) return true
		if (getComparatorValue(tile) == 2 && ElvenFoodMetas.Nectar.stack.isItemEqual(stack)) return true
		
		return false
	}
	
	override fun acceptInput(tile: TileBarrel, stack: ItemStack?) {
		stack ?: return
		
		if (isInitStack(stack)) {
			val was = tile.amountLevel
			tile.amountLevel = min(tile.amountLevel + stack.stackSize, MAX_AMOUNT_LEVEL)
			stack.stackSize -= (tile.amountLevel - was)
		} else if (ElvenFoodMetas.Nectar.stack.isItemEqual(stack)) {
			tile.fermented = true
			tile.timer = MAX_FERMENTATION_TICKS
			onFermented(tile)
			--stack.stackSize
		} else
			return
		
		tile.sync()
	}
	
	override fun getStackLimit(tile: TileBarrel) = when(getComparatorValue(tile)) {
		0    -> MAX_AMOUNT_LEVEL - tile.amountLevel
		2    -> 1
		else -> 0
	}
	
	open fun onFermented(tile: TileBarrel) = Unit
	
	override fun serverTick(tile: TileBarrel) {
		if (!tile.fermented || tile.ready) return
		
		if (tile.closed) {
			if (tile.timer > 0) return
			
			tile.ready = true
			tile.sync()
		} else {
			tile.timer += 2
			if (tile.timer < MAX_ROTTING_TICKS) return
			
			tile.reset()
			val (x, y, z) = Vector3.fromTileEntityCenter(tile)
			EntityItem(tile.worldObj, x, y, z, ItemStack(ModItems.fertilizer)).spawn()
		}
	}
	
	override fun getComparatorValue(tile: TileBarrel): Int {
		val closed = tile.closed
		
		if (!tile.ready && tile.amountLevel < MAX_AMOUNT_LEVEL) return if (closed) 15 else 0
		if (tile.stomps < MAX_STOMPS) return if (closed) 15 else 1
		if (!tile.fermented) return if (closed) 15 else 2
		if (tile.fermented && !tile.closed && !tile.ready) return 3
		if (!tile.ready) return if (!closed) 15 else 4
		if (tile.ready && tile.closed) return 5
		if (tile.ready && !tile.closed) return 6
		
		return 0
	}
	
	override fun onBurstCollision(tile: TileBarrel, burst: IManaBurst) {
		if (getComparatorValue(tile) != 1) return
		
		val lens = burst.sourceLens ?: return
		if (lens.item !is ItemLens || ItemLens.getLens(lens.meta) !is LensWeight) return
		
		if (++tile.stomps < MAX_STOMPS) return
		tile.sync()
	}
	
	@SideOnly(Side.CLIENT)
	override fun renderLiquid(tile: TileBarrel, f5: Float) {
		if (tile.amountLevel <= 0) return
		
		val stage = getComparatorValue(tile)
		if (stage == 15) return
		if (stage < 5) getMash().render(f5)
		if (stage < 2) return
		
		glTranslatef(0f, -1 / 64f, 0f)
		
		val a = when (stage) {
			2    -> 0.5f
			3, 4 -> 1f
			5, 6 -> 0.9f
			else -> 0f // should not happen
		}
		
		applyColorToLiquid(tile, a)
		ASJRenderHelper.setBlend()
		getLiquid().render(f5)
		ASJRenderHelper.discard()
		glColor4f(1f, 1f, 1f, 1f)
	}
	
	@SideOnly(Side.CLIENT) open fun getMash()   = ModelBarrel.redMash 
	@SideOnly(Side.CLIENT) open fun getLiquid() = ModelBarrel.redWine
	@SideOnly(Side.CLIENT) open fun applyColorToLiquid(tile: TileBarrel, a: Float) = glColor4f(1f, 1f, 1f, a)
	
	companion object {
		
		private const val MAX_AMOUNT_LEVEL = 12
		private const val MAX_STOMPS = 8
		private const val MAX_FERMENTATION_TICKS = 6000 // 5 min
		private const val MAX_ROTTING_TICKS = MAX_FERMENTATION_TICKS + 666
		
		private const val TAG_STOMPS = "stomps"
		private const val TAG_FERMENTED = "fermented"
		private const val TAG_READY = "ready"
		
		private var TileBarrel.stomps: Int
			get() = data.getInteger(TAG_STOMPS)
			set(value) = data.setInteger(TAG_STOMPS, value)
		
		private var TileBarrel.fermented: Boolean
			get() = data.getBoolean(TAG_FERMENTED)
			set(value) = data.setBoolean(TAG_FERMENTED, value)
		
		private var TileBarrel.ready: Boolean
			get() = data.getBoolean(TAG_READY)
			set(value) = data.setBoolean(TAG_READY, value)
			
		init {
			eventForge()
		}
		
		@SubscribeEvent
		fun onEntityFall(e: LivingFallEvent) {
			onSomeoneFall(e.entity)
		}
		
		@SubscribeEvent
		fun onPlayerFall(e: PlayerFlyableFallEvent) {
			onSomeoneFall(e.entity)
		}
		
		fun onSomeoneFall(entity: Entity) {
			if (!ASJUtilities.isServer) return
			
			val tile = entity.worldObj.getTileEntity(entity) as? TileBarrel ?: return
			val recipe = tile.recipe ?: return
			
			if (recipe is RecipeWine && recipe.getComparatorValue(tile) != 1) return
			if (++tile.stomps < MAX_STOMPS) return
			
			tile.sync()
		}
	}
}