package alfheim.common.crafting.recipe.barrel

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alexsocol.asjlib.render.*
import alfheim.api.ModInfo
import alfheim.api.crafting.recipe.*
import alfheim.client.model.block.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.block.tile.*
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.item.material.*
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.item.*
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.item.*
import org.lwjgl.opengl.GL11.*
import vazkii.botania.common.item.*
import kotlin.math.*

object RecipeBeer: RecipeBarrel(ElvenResourcesMetas.KudzuSprout.stack, ElvenFoodMetas.Beer.stack) {

	override fun onInteractedWith(tile: TileBarrel, player: EntityPlayer?, stack: ItemStack): Boolean {
		if (tile.closed) return false
		
		if (acceptsInput(tile, stack)) {
			val toPut = stack.copy()
			toPut.stackSize = 1
			acceptInput(tile, toPut)
			stack.stackSize -= 1 - toPut.stackSize
			
			return true
		}
		
		if ((stack.item === Items.water_bucket || stack.item === ModItems.waterBowl) && getComparatorValue(tile) == 2) {
			tile.water = true
			tile.timer = MAX_HEATING_TICKS
			stack.func_150996_a(if (stack.item === Items.water_bucket) Items.bucket else Items.bowl)
			
			return true
		}
		
		if (ElvenResourcesMetas.Jug.stack.isItemEqual(stack) && getComparatorValue(tile) == 10 && tile.amountLevel > 0) {
			val out = getOutputStack(tile).copy()
			if (tile.name.isNotBlank()) {
				out.setStackDisplayName(tile.name)
				
				if (ItemElvenFood.isCC(out))
					ItemNBTHelper.setBoolean(out, ItemElvenFood.TAG_NO_MEME, true)
			}
			
			player?.let {
				if (!it.inventory.addItemStackToInventory(out))
					it.dropPlayerItemWithRandomChoice(out, false)
				
				if (ItemElvenFood.isCC(out))
					player.playSoundAtEntity("${ModInfo.MODID}:ccj", 10f, 1f)
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
		if (getComparatorValue(tile) == 1 && ItemStack(Items.wheat).isItemEqual(stack)) return true
		if (getComparatorValue(tile) == 6 && ElvenFoodMetas.Nectar.stack.isItemEqual(stack)) return true

		return false
	}

	override fun acceptInput(tile: TileBarrel, stack: ItemStack?) {
		stack ?: return

		if (isInitStack(stack)) {
			val was = tile.amountLevel
			tile.amountLevel = min(tile.amountLevel + stack.stackSize, MAX_KUDZU)
			stack.stackSize -= (tile.amountLevel - was)
		} else if (ItemStack(Items.wheat).isItemEqual(stack)) {
			val was = tile.amountLevel
			tile.amountLevel = min(tile.amountLevel + stack.stackSize, MAX_AMOUNT_LEVEL)
			stack.stackSize -= (tile.amountLevel - was)
		} else if (ElvenFoodMetas.Nectar.stack.isItemEqual(stack)) {
			tile.fermented = true
			tile.timer = MAX_COOLING_TICKS
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

	override fun serverTick(tile: TileBarrel) {
		val (x, y, z) = Vector3.fromTileEntityCenter(tile)
		val hasHeatSource = tile.worldObj.getBlock(x.mfloor(), y.mfloor() - 1, z.mfloor()) === Blocks.lit_furnace
		
		val cmp = getComparatorValue(tile)
		if (hasHeatSource && cmp !in 3..4) run {
			val i = tile.burning++
			if (i % 10 == 0) VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.SMOKE, tile.worldObj.provider.dimensionId, x, y + 0.5, z, 0.0, 0.05, 0.0)
			if (i < MAX_BURNING_TICKS) return@run
			
			if (tile.amountLevel == MAX_AMOUNT_LEVEL) EntityItem(tile.worldObj, x, y, z, ItemStack(Items.coal, 1, 1)).spawn()
			tile.reset()
		}
		
		if (cmp in 5..6 && (tile.worldObj.totalWorldTime % if (tile.closed) 10 else 5) == 0L) {
			val (i, _, k) = Vector3.oZ.copy().mul(Math.random() * 0.375).rotateOY(Math.random() * 360)
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.WISP, tile.worldObj.provider.dimensionId, x + i, y + 0.5, z + k, 0.8, 0.8, 0.8, Math.random() * 0.1 + 0.2, 0.0, 0.01, 0.0, 1.0)
		}
		
		if (cmp !in 3..4 && cmp !in 7..8) return

		if (tile.closed) {
			if (cmp == 4 && !hasHeatSource || cmp == 8 && tile.worldObj.getBlock(x.mfloor(), y.mfloor() + 1, z.mfloor()) !== Blocks.packed_ice)
				tile.timer++
			
			if (tile.timer > 0) return

			if (cmp == 4)
				tile.boiled = true
			else if (cmp == 8)
				tile.cooled = true
			
			tile.sync()
		} else {
			tile.timer += 2
			if (tile.timer < (if (cmp == 3) MAX_HEATING_ROTTING_TICKS else if (cmp == 7) MAX_COOLING_ROTTING_TICKS else -1)) return
			
			tile.reset()
			EntityItem(tile.worldObj, x, y, z, ItemStack(Items.dye, 1, 15)).spawn()
		}
	}

	override fun getComparatorValue(tile: TileBarrel): Int {
		val closed = tile.closed

		if (!tile.cooled && tile.amountLevel < MAX_KUDZU)        return if (closed) 15 else 0 // add kudzu
		if (!tile.cooled && tile.amountLevel < MAX_AMOUNT_LEVEL) return if (closed) 15 else 1 // add wheat
		if (!tile.water)                                         return if (closed) 15 else 2 // add water
		if (!tile.boiled && !closed)                             return 3 // close
		if (!tile.boiled && closed)                              return 4 // heat
		if (!tile.fermented && closed)                           return 5 // open
		if (!tile.fermented && !closed)                          return 6 // ferment
		if (!tile.cooled && !closed)                             return 7 // close
		if (!tile.cooled && closed)                              return 8 // cool
		if (tile.cooled && closed)                               return 9 // open
		if (tile.cooled && !closed)                              return 10 // harvest 

		return 0
	}

	@SideOnly(Side.CLIENT)
	override fun renderLiquid(tile: TileBarrel, f5: Float) {
		if (tile.amountLevel <= 0) return

		val stage = getComparatorValue(tile)
		if (stage == 15) return
		
		if (stage < 9) {
			glColor4f(1f, 0.5f, 1f, 1f)
			ModelBarrel.greenMash.render(f5)
			glColor4f(1f, 1f, 1f, 1f)
		}
		if (stage < 3) return

		glTranslatef(0f, -1 / 64f, 0f)

		when (stage) {
			3, 4  -> glColor4f(0f   , 0.25f, 1f, 0.75f) // water
			5, 6  -> glColor4f(0.75f, 0.5f , 0f, 0.9f ) // brewed
			7, 8  -> glColor4f(1f   , 0.25f, 0f, 0.9f ) // fermented
			9, 10 -> glColor4f(1f   , 0.5f,  0f, 0.95f) // cooled
		}
		
		ASJRenderHelper.setBlend()
		ModelBarrel.greenWine.render(f5)
		ASJRenderHelper.discard()
		glColor4f(1f, 1f, 1f, 1f)
	}

	private const val MAX_WHEAT = 6
	private const val MAX_KUDZU = 6
	private const val MAX_AMOUNT_LEVEL = MAX_WHEAT + MAX_KUDZU
	private const val MAX_HEATING_TICKS = 1800 // 1.5 min
	private const val MAX_COOLING_TICKS = 6000 // 5   min
	private const val MAX_HEATING_ROTTING_TICKS = MAX_HEATING_TICKS + 666
	private const val MAX_COOLING_ROTTING_TICKS = MAX_COOLING_TICKS + 666
	private const val MAX_BURNING_TICKS = 666

	private const val TAG_BOILED = "boiled"
	private const val TAG_BURNING = "burning"
	private const val TAG_COOLED = "cooled"
	private const val TAG_FERMENTED = "fermented"
	private const val TAG_WATER = "water"
	
	private var TileBarrel.boiled: Boolean
		get() = data.getBoolean(TAG_BOILED)
		set(value) = data.setBoolean(TAG_BOILED, value)
	
	private var TileBarrel.burning: Int
		get() = data.getInteger(TAG_BURNING)
		set(value) = data.setInteger(TAG_BURNING, value)
	
	private var TileBarrel.cooled: Boolean
		get() = data.getBoolean(TAG_COOLED)
		set(value) = data.setBoolean(TAG_COOLED, value)
	
	private var TileBarrel.fermented: Boolean
		get() = data.getBoolean(TAG_FERMENTED)
		set(value) = data.setBoolean(TAG_FERMENTED, value)
	
	private var TileBarrel.water: Boolean
		get() = data.getBoolean(TAG_WATER)
		set(value) = data.setBoolean(TAG_WATER, value)
}