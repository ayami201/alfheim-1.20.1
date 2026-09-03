package alfheim.common.compat

import alexsocol.asjlib.render.ASJRenderHelper.glColor1u
import alfheim.common.block.colored.BlockAuroraDirt
import alfheim.common.item.*
import com.KAIIIAK.classManipulators.HookReplacer
import com.KAIIIAK.classManipulators.HookReplacer.Replacer.*
import net.minecraft.client.renderer.entity.RenderWolf
import net.minecraft.entity.passive.*
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.client.event.RenderWorldLastEvent
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glColor3f
import vazkii.botania.client.core.handler.LightningHandler
import vazkii.botania.client.render.tile.RenderTileFloatingFlower
import vazkii.botania.common.block.decor.IFloatingFlower
import java.awt.Color

@HookReplacer
fun shouldRenderPass(render: RenderWolf, wolf: EntityWolf?, pass: Int, ticks: Float): Int {
	startFROM()
	glColor3f(EntitySheep.fleeceColorTable[ILOAD("4")][0], EntitySheep.fleeceColorTable[ILOAD("4")][1], EntitySheep.fleeceColorTable[ILOAD("4")][2])
	startTO()
	applyCollarColor(ILOAD("4"))
	stop()
	
	return -1
}

fun applyCollarColor(colorIndex: Int) {
	if (colorIndex == -1) {
		val color = ItemIridescent.rainbowColor()
		glColor1u(color)
	} else {
		val (r, g, b) = EntitySheep.fleeceColorTable[colorIndex and 15]
		glColor3f(r, g, b)
	}
}

@HookReplacer(targetMethod = "onRenderWorldLast")
fun onRenderWorldLast1(thiz: LightningHandler, event: RenderWorldLastEvent?) {
	startFROM()
	GL11.glPushMatrix()
	startTO()
	GL11.glPushMatrix()
	GL11.glDisable(GL11.GL_CULL_FACE)
	stop()
}

@HookReplacer(targetMethod = "onRenderWorldLast")
fun onRenderWorldLast2(thiz: LightningHandler, event: RenderWorldLastEvent?) {
	startFROM()
	GL11.glPopMatrix()
	startTO()
	GL11.glPopMatrix()
	GL11.glEnable(GL11.GL_CULL_FACE)
	stop()
}

// SOURCES ARE FAKE!!!
@HookReplacer(targetMethod = "renderTileEntityAt")
fun renderTileEntityAt1(thiz: RenderTileFloatingFlower, tile: TileEntity, d0: Double, d1: Double, d2: Double, t: Float) {
	startFROM()
	GL11.glPushMatrix()
	GL11.glTranslatef(0.5f, 1.4f, 0.5f)
	startTO()
	applyColor(tile)
	stop()
} 

fun applyColor(tile: TileEntity) {
	tile as IFloatingFlower
	
	GL11.glPushMatrix()
	GL11.glTranslatef(0.5f, 1.4f, 0.5f)
	
	val rgb = if (tile.islandType === ItemColorSeeds.irisIslandTypes.last())
		BlockAuroraDirt.getBlockColor(tile.xCoord, tile.yCoord, tile.zCoord)
	else
		tile.islandType.color
	
	val (r, g, b) = Color(rgb).getRGBColorComponents(null)
	GL11.glColor4f(r, g, b, 1f)
}

// SOURCES ARE FAKE!!!
@HookReplacer(targetMethod = "renderTileEntityAt", isMandatory = false) // not mandatory for GTNH Botania
fun renderTileEntityAt2(thiz: RenderTileFloatingFlower, tile: TileEntity, d0: Double, d1: Double, d2: Double, t: Float) {
	startFROM()
	GL11.glPopMatrix()
	POP(ALOAD<IFloatingFlower>("9").displayStack)
	startTO()
	GL11.glColor4f(1f, 1f, 1f, 1f)
	GL11.glPopMatrix()
	POP(ALOAD<IFloatingFlower>("9").displayStack)
	stop()
} 