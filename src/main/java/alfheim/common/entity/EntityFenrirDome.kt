package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.patcher.event.RenderSkyEvent
import alfheim.api.entity.IAncientWolf
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.client.event.EntityViewRenderEvent.*
import org.lwjgl.opengl.GL11

class EntityFenrirDome(world: World): Entity(world) {
	
	init {
		setSize(RADIUS * 2, RADIUS)
		
		registerEvents()
	}
	
	override fun onEntityUpdate() {
		super.onEntityUpdate()
		
		if (ticksExisted > 200) return setDead()
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(1)).forEach {
			if (it is IAncientWolf) return@forEach
			val d = Vector3.entityDistance(this, it)
			if (d !in (RADIUS - 1)..(RADIUS + 1)) return@forEach
			
			val (mx, my, mz) = Vector3.fromEntity(this).sub(it).normalize().mul(2)
			it.moveEntity(mx, my, mz)
		}
		
//		if (worldObj.isRemote) repeat(100) {
//			val yOffset = rand.nextDouble() * RADIUS
//			val fx = EntityTornadoFX(worldObj, posX, posY + yOffset, posZ, sqrt(RADIUS * RADIUS - yOffset * yOffset), motionX, motionZ, Blocks.leaves, 0, 0, 20)
//			mc.effectRenderer.addEffect(fx)
//		}
	}
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	override fun entityInit() = Unit
	override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
	
	companion object {
		
		const val RADIUS = 5f
		
		var registered = false
		
		fun registerEvents() {
			if (ASJUtilities.isClient && !registered) {
				eventForge()
				registered = true
			}
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun fogDensity(e: FogDensity) {
			if (!check()) return
			
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR)
			GL11.glFogf(GL11.GL_FOG_START, 4f)
			GL11.glFogf(GL11.GL_FOG_END, 24f)
			
			e.density = 0.5f
			e.isCanceled = true
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun fogColor(e: FogColors) {
			if (!check()) return
			
			e.red = 0.75f
			e.green = 1f
			e.blue = 0.75f
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun noSky(e: RenderSkyEvent) {
			if (!check()) return
			e.isCanceled = true
		}
		
		private fun check(): Boolean {
			val dome = getEntitiesWithinAABB(mc.thePlayer.worldObj, EntityFenrirDome::class.java, mc.thePlayer.boundingBox(RADIUS * 2)).firstOrNull() ?: return false
			return Vector3.entityDistance(mc.thePlayer, dome) <= RADIUS
		}
	}
}
