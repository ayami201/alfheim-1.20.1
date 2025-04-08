package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.*
import alfheim.api.ModInfo
import alfheim.api.lib.*
import alfheim.client.render.world.SpellVisualizations
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.*
import net.minecraft.entity.boss.IBossDisplayData
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import vazkii.botania.client.core.handler.ClientTickHandler
import vazkii.botania.common.Botania
import java.util.*
import kotlin.math.*
import vazkii.botania.common.core.helper.Vector3 as Bector3

class EntityRift(world: World): Entity(world) {
	
	var lifespan
		get() = dataWatcher.getWatchableObjectInt(2)
		set(value) = dataWatcher.updateObject(2, value)
	
	init {
		setSize(0f, 0f)
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, 0)
	}
	
	override fun onUpdate() {
		val active = ticksExisted < (lifespan - 10)
		
		if (worldObj.isRemote) {
			if (active && rand.nextInt(5) == 0)
				Botania.proxy.lightningFX(worldObj, Bector3.fromEntity(this), Bector3.fromEntity(this).add(rand.nextDouble() * 3 - 1.5, rand.nextDouble() * 3 - 1.5, rand.nextDouble() * 3 - 1.5), 1f, rand.nextLong(), 0, 0xBB0000)
			
			return
		}
		
		if (firstUpdate) lifespan = ASJUtilities.randInBounds(60, 120)
		
		if (ticksExisted > lifespan) return setDead()
		
		if (firstUpdate)
			playSound("${ModInfo.MODID}:glass", rand.nextFloat() * 0.25f + 0.75f, rand.nextFloat() * 0.25f + 1f)
		
		if (active)
			getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(3)).forEach {
				val prev = it.hurtResistantTime
				if (firstUpdate) it.hurtResistantTime = 0
				it.attackEntityFrom(DamageSource.outOfWorld, ASJUtilities.randInBounds(4, 8, rand).F)
				if (firstUpdate) it.hurtResistantTime = prev
				
				if (firstUpdate)
					it.setMotion(0.0)
			}
		
		firstUpdate = false
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	companion object {
		
		var useStencil = false
		
		init {
			eventForge()
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		fun renderRifts(e: RenderWorldLastEvent) {
			glPushMatrix()
			ASJRenderHelper.interpolatedTranslationReverse(mc.thePlayer)
			
			mc.theWorld.loadedEntityList.filterIsInstance<EntityRift>().sortedByDescending {
				Vector3.entityDistance(it, mc.renderViewEntity)
			}.forEach {
				val rand = Random(it.entityId.toLong())
				
				glPushMatrix()
				glTranslated(it.posX, it.posY, it.posZ)
				
				if (useStencil) {
					glEnable(GL_STENCIL_TEST)
					glColorMask(false, false, false, false)
					glDepthMask(false)
					glStencilFunc(GL_ALWAYS, 1, 255)
					glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE)
					glStencilMask(255)
					glClear(GL_STENCIL_BUFFER_BIT)
				}
				
				glDisable(GL_CULL_FACE)
				glDisable(GL_TEXTURE_2D)
				renderRift(rand, it)
				glEnable(GL_TEXTURE_2D)
				glEnable(GL_CULL_FACE)
				
				if (useStencil) {
					glDepthMask(true)
					glColorMask(true, true, true, true)
					glStencilMask(0)
					glStencilFunc(GL_EQUAL, 1, 255)
					glEnable(GL_DEPTH_TEST)
				}

				glEnable(GL_BLEND)
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
				renderInside(rand, 1f - max(0f, it.ticksExisted + e.partialTicks - (it.lifespan - 10)) / 10)
				
				if (useStencil) {
					glStencilFunc(GL_NOTEQUAL, 1, 255)
					glDisable(GL_STENCIL_TEST)
					glClear(GL_STENCIL_BUFFER_BIT)
				}

				glPopMatrix()
			}
			
			glPopMatrix()
		}
		
		fun renderRift(rand: Random, it: EntityRift) {
			val growth = min(it.ticksExisted / 2, 5) / 5f
			
			repeat(ASJUtilities.randInBounds(8, 16, rand)) lines@ {
				var w = (rand.nextDouble() * 0.025 + 0.025) * growth
				val pos = Vector3()
				val dir = Vector3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()).sub(0.5).normalize()
				val normal = dir.copy().crossProduct(Vector3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()).sub(0.5).normalize()).normalize()
				val perp = dir.copy().crossProduct(normal).normalize().mul(w)
				var oldDir = dir
				
				glBegin(GL_TRIANGLE_STRIP)
				val bends = ASJUtilities.randInBounds(3, 6, rand)
				
				repeat(bends) bends@{ i ->
					val offset = dir.copy().mul(w)
					val r = pos.copy().add(offset)
					val g = pos.copy().add(offset).add(perp)
					val b = pos.copy().add(offset).sub(perp)
					val midDir = oldDir.copy().add(dir).mul(0.5)
					
					if (i == 0) {
						pos.glVertex()
					}
					
					if (i % 2 == 0) {
						if (i != 0) {
							r.sub(perp).sub(midDir.mul(w)).glVertex()
							b.add(midDir)
							if (i == bends - 1) b.add(dir)
						}
						
						g.glVertex()
						b.glVertex()
					} else {
						r.add(perp).sub(midDir.mul(w)).glVertex()
						
						if (i == bends - 1) g.add(dir)
						
						b.glVertex()
						g.add(midDir).glVertex()
					}
					
					oldDir = dir.copy()
					dir.rotate(Math.toDegrees(rand.nextDouble() * 0.25 + 0.25) * if (i % 2 == 0) -1 else 1, normal)
					pos.add(dir.copy().mul(rand.nextDouble() * 0.5 + 0.5).mul(growth))
					perp.set(dir.copy().crossProduct(normal).normalize().mul(w))
					w -= rand.nextDouble() * 0.01
				}
				
				glEnd()
			}
		}
		
		@Suppress("UNCHECKED_CAST")
		fun renderInside(rand: Random, fade: Float) {
			glColor4f(0f, 0f, 0f, fade)
			
			if (useStencil) glDepthFunc(GL_ALWAYS)
			
			glCullFace(GL_FRONT)
			ASJShaderHelper.useShader(LibShaderIDs.idNoise) { id ->
				GL20.glUniform4f(GL20.glGetUniformLocation(id, "color2"), 0.75f, 0f, 0f, fade)
			}
			SpellVisualizations.renderSphere(16.0)
			ASJShaderHelper.releaseShader()
			glCullFace(GL_BACK)
			
			if (useStencil) glDepthFunc(GL_LEQUAL)
			
			repeat(ASJUtilities.randInBounds(4, 8, rand)) {
				val mob = try {
					(EntityList.stringToClassMapping.values as MutableCollection<Class<out Entity>>).filter {
						EntityLiving::class.java.isAssignableFrom(it) && !IBossDisplayData::class.java.isAssignableFrom(it)
					}.random(rand)!!.getConstructor(World::class.java).newInstance(mc.theWorld) as EntityLiving
				} catch (e: Exception) {
					EntitySkeleton(mc.theWorld).apply { skeletonType = 1 }
				}
				
				glPushMatrix()
				
				glRotated(rand.nextDouble() * 360 + ClientTickHandler.total * (rand.nextDouble() + 0.5) % 360, rand.nextDouble() * 2 - 1, rand.nextDouble() * 2 - 1, rand.nextDouble() * 2 - 1)
				glTranslated(0.0, 0.0, -ASJUtilities.randInBounds(6, 12, rand).D)
				
				glColor4f(1f, 1f, 1f, fade)
				RenderManager.instance.renderEntityWithPosYaw(mob, 0.0, mob.height / -2.0, 0.0, 0f, 0f)
				
				glPopMatrix()
			}
		}
	}
}
