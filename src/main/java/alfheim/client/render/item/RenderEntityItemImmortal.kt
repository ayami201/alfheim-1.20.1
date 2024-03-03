package alfheim.client.render.item

import alexsocol.asjlib.*
import alfheim.common.entity.item.EntityItemImmortal
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.entity.Entity
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraftforge.client.IItemRenderer.*
import net.minecraftforge.client.MinecraftForgeClient
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import java.util.*

@SideOnly(Side.CLIENT)
object RenderEntityItemImmortal: Render() {
	
	private val RES_ITEM_GLINT = ResourceLocation("textures/misc/enchanted_item_glint.png")
	private val renderBlocksRi = RenderBlocks()
	private val random = Random()
	
	init {
		shadowSize = 0.15f
		shadowOpaque = 0.75f
	}
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, noop: Float, ticks: Float) {
		if (entity !is EntityItemImmortal) return
		
		val stack = entity.stack ?: return
		if (stack.item == null) return
		
		bindEntityTexture(entity)
		TextureUtil.func_152777_a(false, false, 1f)
		random.setSeed(187L)
		glPushMatrix()
		val f2 = MathHelper.sin((entity.age.F + ticks) / 10f + entity.hoverStart) * 0.1f + 0.1f
		val f3 = ((entity.age + ticks) / 20f + entity.hoverStart) * (180 / Math.PI.F)
		val b0 = when {
			stack.stackSize > 1  -> 2
			stack.stackSize > 5  -> 3
			stack.stackSize > 20 -> 4
			stack.stackSize > 40 -> 5
			else                 -> 1
		}
		glTranslated(x, y + f2, z)
		glEnable(GL12.GL_RESCALE_NORMAL)
		var f6: Float
		var f7: Float
		var k: Int
		if (aForgeHookForCustomRender(entity, stack, f2, f3, random, renderManager.renderEngine, field_147909_c, b0)) {
			// Forge rendered everything
		} else if (stack.itemSpriteNumber == 0 && stack.item is ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.item).renderType)) {
			val block = Block.getBlockFromItem(stack.item)
			glRotatef(f3, 0f, 1f, 0f)
			var f9 = 0.25f
			k = block.renderType
			if (k == 1 || k == 19 || k == 12 || k == 2) {
				f9 = 0.5f
			}
			if (block.renderBlockPass > 0) {
				glAlphaFunc(GL_GREATER, 0.1f)
				glEnable(GL_BLEND)
				OpenGlHelper.glBlendFunc(770, 771, 1, 0)
			}
			glScalef(f9, f9, f9)
			for (l in 0 until b0) {
				glPushMatrix()
				if (l > 0) {
					f6 = (random.nextFloat() * 2f - 1f) * 0.2f / f9
					f7 = (random.nextFloat() * 2f - 1f) * 0.2f / f9
					val f8 = (random.nextFloat() * 2f - 1f) * 0.2f / f9
					glTranslatef(f6, f7, f8)
				}
				renderBlocksRi.renderBlockAsItem(block, stack.getItemDamage(), 1f)
				glPopMatrix()
			}
			if (block.renderBlockPass > 0) {
				glDisable(GL_BLEND)
			}
		} else {
			var f5: Float
			if ( /*itemstack.getItemSpriteNumber() == 1 &&*/stack.item.requiresMultipleRenderPasses()) {
				glScalef(0.5f, 0.5f, 0.5f)
				for (j in 0 until stack.item.getRenderPasses(stack.getItemDamage())) {
					random.setSeed(187L)
					val iicon1 = stack.item.getIcon(stack, j)
					k = stack.item.getColorFromItemStack(stack, j)
					f5 = (k shr 16 and 255).F / 255f
					f6 = (k shr 8 and 255).F / 255f
					f7 = (k and 255).F / 255f
					glColor4f(f5, f6, f7, 1f)
					renderDroppedItem(entity, iicon1, b0, ticks, f5, f6, f7, j)
				}
			} else {
				if (stack.item is ItemCloth) {
					glAlphaFunc(GL_GREATER, 0.1f)
					glEnable(GL_BLEND)
					OpenGlHelper.glBlendFunc(770, 771, 1, 0)
				}
				glScalef(0.5f, 0.5f, 0.5f)
				val iicon = stack.iconIndex
				val i = stack.item.getColorFromItemStack(stack, 0)
				val f4 = (i shr 16 and 255).F / 255f
				f5 = (i shr 8 and 255).F / 255f
				f6 = (i and 255).F / 255f
				renderDroppedItem(entity, iicon, b0, ticks, f4, f5, f6)
				if (stack.item is ItemCloth) {
					glDisable(GL_BLEND)
				}
			}
		}
		
		glDisable(GL12.GL_RESCALE_NORMAL)
		glPopMatrix()
		bindEntityTexture(entity)
		TextureUtil.func_147945_b()
	}
	
	fun aForgeHookForCustomRender(entity: EntityItemImmortal, item: ItemStack, bobing: Float, rotation: Float, random: Random, engine: TextureManager, renderBlocks: RenderBlocks?, count: Int): Boolean {
		val customRenderer = MinecraftForgeClient.getItemRenderer(item, ItemRenderType.ENTITY) ?: return false
		if (customRenderer.shouldUseRenderHelper(ItemRenderType.ENTITY, item, ItemRendererHelper.ENTITY_ROTATION)) {
			glRotatef(rotation, 0f, 1f, 0f)
		}
		if (!customRenderer.shouldUseRenderHelper(ItemRenderType.ENTITY, item, ItemRendererHelper.ENTITY_BOBBING)) {
			glTranslatef(0f, -bobing, 0f)
		}
		val is3D = customRenderer.shouldUseRenderHelper(ItemRenderType.ENTITY, item, ItemRendererHelper.BLOCK_3D)
		engine.bindTexture(if (item.itemSpriteNumber == 0) TextureMap.locationBlocksTexture else TextureMap.locationItemsTexture)
		val block = if (item.item is ItemBlock) Block.getBlockFromItem(item.item) else null
		if (is3D || block != null && RenderBlocks.renderItemIn3d(block.renderType)) {
			val renderType = block?.renderType ?: 1
			val scale = if (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2) 0.5f else 0.25f
			val blend = block != null && block.renderBlockPass > 0
			if (RenderItem.renderInFrame) {
				glScalef(1.25f, 1.25f, 1.25f)
				glTranslatef(0f, 0.05f, 0f)
				glRotatef(-90f, 0f, 1f, 0f)
			}
			if (blend) {
				glAlphaFunc(GL_GREATER, 0.1f)
				glEnable(GL_BLEND)
				OpenGlHelper.glBlendFunc(770, 771, 1, 0)
			}
			glScalef(scale, scale, scale)
			for (j in 0 until count) {
				glPushMatrix()
				if (j > 0) {
					glTranslatef(
						(random.nextFloat() * 2f - 1f) * 0.2f / scale,
						(random.nextFloat() * 2f - 1f) * 0.2f / scale,
						(random.nextFloat() * 2f - 1f) * 0.2f / scale)
				}
				customRenderer.renderItem(ItemRenderType.ENTITY, item, renderBlocks, entity)
				glPopMatrix()
			}
			if (blend) {
				glDisable(GL_BLEND)
			}
		} else {
			glScalef(0.5f, 0.5f, 0.5f)
			customRenderer.renderItem(ItemRenderType.ENTITY, item, renderBlocks, entity)
		}
		return true
	}
	
	/**
	 * Renders a dropped item
	 */
	fun renderDroppedItem(entity: EntityItemImmortal, dIcon: IIcon?, itemCount: Int, hower: Float, red: Float, green: Float, blue: Float, pass: Int = 0) {
		val tessellator = Tessellator.instance
		
		val icon: IIcon = dIcon ?: (mc.textureManager.getTexture(getEntityTexture(entity)) as TextureMap).getAtlasSprite("missingno")
		
		val f14 = icon.minU
		val f15 = icon.maxU
		val f4 = icon.minV
		val f5 = icon.maxV
		val f6 = 1f
		val f7 = 0.5
		val f8 = 0.25
		var f10: Float
		
		if (renderManager.options.fancyGraphics) {
			glPushMatrix()
			glRotatef(((entity.age.F + hower) / 20f + entity.hoverStart) * (180f / Math.PI.F), 0f, 1f, 0f)
			val f9 = 0.0625f
			f10 = 0.021875f
			val stack = entity.stack
			val j = stack?.stackSize ?: 1
			
			val b0 = when {
				j < 2  -> 1
				j < 16 -> 2
				j < 32 -> 3
				else   -> 4
			}
			
			glTranslated(-f7, -f8, -((f9 + f10) * b0.D / 2))
			for (k in 0 until b0) {
				if (k > 0) {
					val x = (random.nextFloat() * 2f - 1f) * 0.3f / 0.5f
					val y = (random.nextFloat() * 2f - 1f) * 0.3f / 0.5f
//					val z = (random.nextFloat() * 2f - 1f) * 0.3f / 0.5f
					glTranslatef(x, y, f9 + f10)
				} else {
					glTranslatef(0f, 0f, f9 + f10)
				}
				if ((stack?.itemSpriteNumber ?: 1) == 0) {
					bindTexture(TextureMap.locationBlocksTexture)
				} else {
					bindTexture(TextureMap.locationItemsTexture)
				}
				glColor4f(red, green, blue, 1f)
				ItemRenderer.renderItemIn2D(tessellator, f15, f4, f14, f5, icon.iconWidth, icon.iconHeight, f9)
				if (stack?.hasEffect(pass) == true) {
					glDepthFunc(GL_EQUAL)
					glDisable(GL_LIGHTING)
					renderManager.renderEngine.bindTexture(RES_ITEM_GLINT)
					glEnable(GL_BLEND)
					glBlendFunc(GL_SRC_COLOR, GL_ONE)
					val f11 = 0.76f
					glColor4f(0.5f * f11, 0.25f * f11, 0.8f * f11, 1f)
					glMatrixMode(GL_TEXTURE)
					glPushMatrix()
					val f12 = 0.125f
					glScalef(f12, f12, f12)
					var f13 = (Minecraft.getSystemTime() % 3000L).F / 3000f * 8f
					glTranslatef(f13, 0f, 0f)
					glRotatef(-50f, 0f, 0f, 1f)
					ItemRenderer.renderItemIn2D(tessellator, 0f, 0f, 1f, 1f, 255, 255, f9)
					glPopMatrix()
					glPushMatrix()
					glScalef(f12, f12, f12)
					f13 = (Minecraft.getSystemTime() % 4873L).F / 4873f * 8f
					glTranslatef(-f13, 0f, 0f)
					glRotatef(10f, 0f, 0f, 1f)
					ItemRenderer.renderItemIn2D(tessellator, 0f, 0f, 1f, 1f, 255, 255, f9)
					glPopMatrix()
					glMatrixMode(GL_MODELVIEW)
					glDisable(GL_BLEND)
					glEnable(GL_LIGHTING)
					glDepthFunc(GL_LEQUAL)
				}
			}
			glPopMatrix()
		} else {
			for (l in 0 until itemCount) {
				glPushMatrix()
				if (l > 0) {
					f10 = (random.nextFloat() * 2f - 1f) * 0.3f
					val f16 = (random.nextFloat() * 2f - 1f) * 0.3f
					val f17 = (random.nextFloat() * 2f - 1f) * 0.3f
					glTranslatef(f10, f16, f17)
				}
				glRotatef(180f - renderManager.playerViewY, 0f, 1f, 0f)
				glColor4f(red, green, blue, 1f)
				tessellator.startDrawingQuads()
				tessellator.setNormal(0f, 1f, 0f)
				tessellator.addVertexWithUV(-f7, -f8, 0.0, f14.D, f5.D)
				tessellator.addVertexWithUV(f6 - f7, -f8, 0.0, f15.D, f5.D)
				tessellator.addVertexWithUV(f6 - f7, 1 - f8, 0.0, f15.D, f4.D)
				tessellator.addVertexWithUV(-f7, 1 - f8, 0.0, f14.D, f4.D)
				tessellator.draw()
				glPopMatrix()
			}
		}
	}
	
	override fun getEntityTexture(entity: Entity) = getEntityTexture(entity as EntityItemImmortal)
	
	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	fun getEntityTexture(entity: EntityItemImmortal): ResourceLocation {
		return renderManager.renderEngine.getResourceLocation(entity.stack?.itemSpriteNumber ?: 1)
	}
}