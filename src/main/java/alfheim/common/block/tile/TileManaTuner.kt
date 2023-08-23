package alfheim.common.block.tile

import alexsocol.asjlib.*
import alfheim.api.AlfheimAPI
import alfheim.api.crafting.recipe.TunerIncantation
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraftforge.common.util.Constants
import org.lwjgl.opengl.GL11.glTranslated
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.mana.IManaReceiver
import vazkii.botania.common.block.tile.TileSimpleInventory
import vazkii.botania.common.item.lens.ItemLens
import kotlin.math.*
import net.minecraft.client.renderer.RenderHelper as VanillaRenderHelper

class TileManaTuner: TileSimpleInventory(), IManaReceiver {
	
	var block = Blocks.air!!
	var entities = HashSet<String>()
	var resetTimer = 0
	var incantation = ""
	
	override fun updateEntity() {
		if (worldObj.isRemote) return
		
		for (it in AlfheimAPI.tunerIncantations[incantation]) {
			resetTimer = 0
			
			@Suppress("UNCHECKED_CAST")
			val target: Any = when (it.type) {
				TunerIncantation.EnumTargetType.BLOCK -> worldObj.getBlock(xCoord, yCoord + 1, zCoord)
				TunerIncantation.EnumTargetType.ENTITY -> getEntitiesWithinAABB(worldObj, it.clazz as Class<out Entity>, boundingBox().offset(0, 1, 0)).firstOrNull(Entity::isEntityAlive) ?: continue
				TunerIncantation.EnumTargetType.TILE -> {
					val tile = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) ?: continue
					if (!tile::class.java.isAssignableFrom(it.clazz)) continue
					tile
				}
			}
			
			if (!it.matches(this, target)) continue
			if (!it.application(target)) continue
			
			for (i in 0 until sizeInventory) {
				val stack = get(i) ?: continue
				set(i, if (stack.item?.hasContainerItem(stack) == true) stack.item?.getContainerItem(stack) else null)
			}
			
			ASJUtilities.dispatchTEToNearbyPlayers(this)
			
			break
		}
		
		if (resetTimer-- <= 0)
			reset()
	}
	
	fun onBurstCollision(burst: IManaBurst) {
		if (worldObj.isRemote) return
		
		val lens = burst.sourceLens
		val colorId = if (lens != null && lens.item is ItemLens) ItemLens.getStoredColor(lens) else -1
		val letter = map.getOrNull(colorId) ?: return reset()
		
		val newEntities = getEntitiesWithinAABB(worldObj, Entity::class.java, boundingBox().offset(0, 1, 0)).map { it.uniqueID.toString() }.toHashSet()
		val newBlock = worldObj.getBlock(xCoord, yCoord + 1, zCoord)
		
		if (incantation.isEmpty()) {
			entities = newEntities
			block = newBlock
		} else if (entities != newEntities || block !== newBlock) {
			incantation = ""
			entities = newEntities
			block = newBlock
		}
		
		incantation += letter
		resetTimer = 200
		
		(burst as? Entity)?.setDead()
		
		if (incantation.length > 256) {
			worldObj.setBlockToAir(xCoord, yCoord, zCoord)
			worldObj.createExplosion(null, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 5f, true)
		}
	}
	
	override fun setInventorySlotContents(slot: Int, stack: ItemStack?) {
		super.setInventorySlotContents(slot, stack)
		reset()
		
		if (!worldObj.isRemote)
			ASJUtilities.dispatchTEToNearbyPlayers(this)
	}
	
	fun reset() {
		incantation = ""
		entities.clear()
		block = Blocks.air
	}
	
	override fun writeToNBT(nbt: NBTTagCompound) {
		super.writeToNBT(nbt)
		
		nbt.setString(TAG_SPELL, incantation)
		nbt.setInteger(TAG_TIMER, resetTimer)
		
		val list = NBTTagList()
		entities.forEach { list.appendTag(NBTTagString(it)) }
		nbt.setTag(TAG_ENTITIES, list)
		
		nbt.setString(TAG_BLOCK, GameRegistry.findUniqueIdentifierFor(block)?.toString() ?: "")
	}
	
	override fun readFromNBT(nbt: NBTTagCompound) {
		super.readFromNBT(nbt)
		
		incantation = nbt.getString(TAG_SPELL)
		resetTimer = nbt.getInteger(TAG_TIMER)
		
		entities.clear()
		val list = nbt.getTagList(TAG_ENTITIES, Constants.NBT.TAG_STRING)
		for (i in 0 until list.tagCount())
			entities.add(list.getStringTagAt(i))
		
		block = Block.getBlockFromName(nbt.getString(TAG_BLOCK)) ?: Blocks.air
	}
	
	override fun getInventoryName() = ""
	
	override fun getSizeInventory() = 16
	
	override fun getInventoryStackLimit() = 1

	override fun getRenderBoundingBox() = boundingBox(1)
	
	fun renderHUD(mc: Minecraft, res: ScaledResolution) {
		val xc = res.scaledWidth / 2
		val yc = res.scaledHeight / 2
		var angle = -90f
		val radius = 24
		var amt = 0
		
		for (i in 0 until getSizeInventory()) {
			if (get(i) == null) break
			amt++
		}
		
		if (amt <= 0) return
		
		val anglePer = 360f / amt
		
		VanillaRenderHelper.enableGUIStandardItemLighting()
		
		for (i in 0 until amt) {
			val xPos = xc + cos(angle * Math.PI / 180) * radius - 8
			val yPos = yc + sin(angle * Math.PI / 180) * radius - 8
			glTranslated(xPos, yPos, 0.0)
			RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, getStackInSlot(i), 0, 0)
			glTranslated(-xPos, -yPos, 0.0)
			angle += anglePer
		}
		
		VanillaRenderHelper.disableStandardItemLighting()
	}
	
	// for display in the spreader
	override fun getCurrentMana() = 0
	override fun isFull() = true
	override fun recieveMana(mana: Int) = Unit
	override fun canRecieveManaFromBursts() = false
	
	companion object {
		
		const val TAG_BLOCK = "block"
		const val TAG_ENTITIES = "entities"
		const val TAG_SPELL = "spell"
		const val TAG_TIMER = "timer"
		
		val map = arrayOf('.', 'a', 'e', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 's', 't', 'u', 'w', ':', ' ')
	}
}