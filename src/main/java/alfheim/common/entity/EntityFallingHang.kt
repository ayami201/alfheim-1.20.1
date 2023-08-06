package alfheim.common.entity

import alexsocol.asjlib.*
import net.minecraft.block.Block
import net.minecraft.entity.*
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import kotlin.math.max

class EntityFallingHang(world: World): Entity(world) {
	
	var meta
		get() = air
		set(value) {
			air = value
		}
	
	var block: Block
		get() = Block.getBlockFromName(dataWatcher.getWatchableObjectString(2)) ?: Blocks.stone
		set(value) = dataWatcher.updateObject(2, Block.blockRegistry.getNameForObject(value))
	
	var blockName: String
		get() = dataWatcher.getWatchableObjectString(2)
		set(value) = dataWatcher.updateObject(2, value)
	
	init {
		setSize(1f, 1f)
	}
	
	override fun onEntityUpdate() {
		super.onEntityUpdate()
		
		motionY -= 0.04
		
		moveEntity(motionX, motionY, motionZ)
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, getBoundingBox(posX, posY, posZ, lastTickPosX, lastTickPosY, lastTickPosZ).expand(0.5)).apply {
			forEach {
				it.attackEntityFrom(DamageSource.fallingBlock, max(fallDistance * 2, 40f))
			}
			
			if (isNotEmpty()) isCollided = true
		}
		
		if (!isCollided) return
		
		setDead()
		worldObj.playAuxSFX(2001, posX.mfloor(), posY.mfloor(), posZ.mfloor(), Block.getIdFromBlock(block) + (meta shl 12))
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, "minecraft:stone")
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey(TAG_BLOCK))
			blockName = nbt.getString(TAG_BLOCK)
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setString(TAG_BLOCK, blockName)
	}
	
	companion object {
		const val TAG_BLOCK = "block"
	}
}
