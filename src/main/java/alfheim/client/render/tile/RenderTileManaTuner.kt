package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.tile.TileManaTuner
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.block.Block
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12
import vazkii.botania.client.core.handler.ClientTickHandler
import java.awt.Color
import kotlin.math.*

object RenderTileManaTuner: TileEntitySpecialRenderer() {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/ManaTuner.obj"))
	
	override fun renderTileEntityAt(tile: TileEntity?, x: Double, y: Double, z: Double, ticks: Float) {
		val tuner = tile as TileManaTuner
		
		glPushMatrix()
		glColor4f(1f, 1f, 1f, 1f)
		glTranslated(x, y, z)
		
		model?.let {
			bindTexture(LibResourceLocations.manaTuner)
			
			glPushMatrix()
			
			if (tile.worldObj != null)
				glTranslated(0.5, 0.0, 0.5)
			else
				glTranslated(0.0, -0.5, 0.0)
			
			model.renderPart("Base")
			
			ASJRenderHelper.setGlow()
			model.renderPart("Crystal")
			ASJRenderHelper.discard()
			
			val angle = ClientTickHandler.total % 360
			val rad = Math.toRadians(angle.D * 4)
			
			glTranslated(0.0, (cos(rad) * 0.5 - 0.5) * 0.0625, 0.0)
			glRotatef(angle, 0f, 1f, 0f)
			model.renderPart("Ring1")
			model.renderPart("Ring3")
			glRotatef(-angle * 2, 0f, 1f, 0f)
			model.renderPart("Ring2")
			model.renderPart("Ring4")
			
			glPopMatrix()
		}
		
		var items = 0
		for (i in 0 until tuner.sizeInventory) if (tuner[i] != null) items++
		
		val angles = FloatArray(tuner.sizeInventory)
		val anglePer = 360f / items
		var totalAngle = 0f
		
		for (i in angles.indices) {
			totalAngle += anglePer
			angles[i] = totalAngle
		}
		
		val time = ClientTickHandler.ticksInGame + ticks
		
		for (i in 0 until tuner.sizeInventory) {
			val stack = tuner[i] ?: continue
			
			glPushMatrix()
			glScaled(0.5)
			glTranslatef(1f, 2.5f, 1f)
			glRotatef(angles[i] + time, 0f, 1f, 0f)
			glTranslatef(2.25f, 0f, 0.5f)
			glRotatef(90f, 0f, 1f, 0f)
			glTranslatef(0f, 0.15f * sin((time + i * 10) / 5), 0f)
			
			mc.renderEngine.bindTexture(if (stack.item is ItemBlock) TextureMap.locationBlocksTexture else TextureMap.locationItemsTexture)
			glEnable(GL12.GL_RESCALE_NORMAL)
			glScalef(2f)
			
			if (ForgeHooksClient.renderEntityItem(EntityItem(tuner.worldObj, tuner.xCoord + 0.5, tuner.yCoord + 1.5, tuner.zCoord + 0.5, stack), stack, 0f, 0f, tuner.worldObj.rand, mc.renderEngine, renderBlocks, 1)) {
				glPopMatrix()
				continue
			}
			
			glScaled(0.5)
			
			if (stack.item is ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.item).renderType)) {
				glScaled(0.5)
				glTranslatef(1f, 1.1f, 0f)
				renderBlocks.renderBlockAsItem(Block.getBlockFromItem(stack.item), stack.getItemDamage(), 1f)
				glTranslatef(-1f, -1.1f, 0f)
				glScalef(2f)
			} else {
				var renderPass = 0
				do {
					val icon = stack.item.getIcon(stack, renderPass)
					if (icon != null) {
						val color = Color(stack.item.getColorFromItemStack(stack, renderPass))
						glColor3ub(color.red.toByte(), color.green.toByte(), color.blue.toByte())
						val f = icon.minU
						val f1 = icon.maxU
						val f2 = icon.minV
						val f3 = icon.maxV
						ItemRenderer.renderItemIn2D(Tessellator.instance, f1, f2, f, f3, icon.iconWidth, icon.iconHeight, 1f / 16f)
						glColor4f(1f, 1f, 1f, 1f)
					}
					renderPass++
				} while (renderPass < stack.item.getRenderPasses(stack.getItemDamage()))
			}
			
			glPopMatrix()
		}
		
		glPopMatrix()
	}
}
