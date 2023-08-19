package alfheim.api.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.AlfheimAPI
import alfheim.api.lib.LibResourceLocations
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import java.util.*

// Used for anomalies - TileAnomaly
abstract class SubTileAnomalyBase {
	
	val rand = Random()
	var superTile: TileEntity? = null
	var ticks: Int = 0
	var worldGen = false
	
	abstract val targets: List<Any>
	
	enum class EnumAnomalyRarity {
		COMMON, RARE, EPIC
	}
	
	/** optional update method for particles or other stuff  */
	protected open fun update() {}
	
	fun updateEntity(l: MutableList<Any>?) {
		update()
		
		try {
			if (l.isNullOrEmpty()) return
			for (target in l) performEffect(target)
		} finally {
			ticks++
		}
	}
	
	abstract fun performEffect(target: Any)
	
	open fun onActivated(stack: ItemStack?, player: EntityPlayer, world: World, x: Int, y: Int, z: Int) = false
	
	fun writeToNBT(cmp: NBTTagCompound) {
		cmp.setInteger(TAG_TICKS, ticks)
		writeCustomNBT(cmp)
	}
	
	open fun writeCustomNBT(cmp: NBTTagCompound) {}
	
	fun readFromNBT(cmp: NBTTagCompound) {
		ticks = cmp.getInteger(TAG_TICKS)
		readCustomNBT(cmp)
	}
	
	open fun readCustomNBT(cmp: NBTTagCompound) {}
	
	// ################################ SUPERTILE ################################
	
	val worldObj get() = superTile!!.worldObj!!
	val x get() = superTile!!.xCoord
	val y get() = superTile!!.yCoord
	val z get() = superTile!!.zCoord
	
	// ################################ UTILS ################################
	
	fun findNearestVulnerableEntity(radius: Double): EntityLivingBase? {
		val list = allAround(EntityLivingBase::class.java, radius)
		var entity1: EntityLivingBase? = null
		var d0 = java.lang.Double.MAX_VALUE
		
		for (entity2 in list) {
			if (entity2.isEntityInvulnerable) continue
			if (entity2 is EntityPlayer && entity2.capabilities.disableDamage) continue
			
			val d1 = Vector3.entityTileDistance(entity2, superTile!!)
			
			if (d1 <= d0) {
				entity1 = entity2
				d0 = d1
			}
		}
		
		return entity1
	}
	
	fun findNearestEntity(radius: Double): EntityLivingBase? {
		val list = allAround(EntityLivingBase::class.java, radius)
		var entity1: EntityLivingBase? = null
		var d0 = java.lang.Double.MAX_VALUE
		
		for (entity2 in list) {
			
			val d1 = Vector3.entityTileDistance(entity2, superTile!!)
			
			if (d1 <= d0) {
				entity1 = entity2
				d0 = d1
			}
		}
		return entity1
	}
	
	fun <E: Any> allAround(clazz: Class<E>, radius: Double) = getEntitiesWithinAABB(worldObj, clazz, getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(radius, radius, radius))
	
	fun inWG() = worldGen
	
	// ################################ RENDER ################################
	
	@SideOnly(Side.CLIENT)
	fun bindTexture() {
		mc.renderEngine.bindTexture(LibResourceLocations.anomalies)
	}
	
	companion object {
		
		const val TAG_TICKS = "ticks"
		
		fun forName(name: String): SubTileAnomalyBase? {
			return try {
				AlfheimAPI.getAnomaly(name).subtileClass.newInstance()
			} catch (e: Exception) {
				ASJUtilities.error("Error while getting '$name' anomaly subtile", e)
				null
			}
		}
	}
}
