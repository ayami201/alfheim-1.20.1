package alfheim.client.render.block

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.*
import alfheim.common.block.tile.TileDoubleCamo
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import net.minecraft.block.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.opengl.GL11.*
import java.awt.Color

// a lot of ~shit~ code copied from Carpenter's Blocks
object RenderSimpleDoubleBlock: ISimpleBlockRenderingHandler {
	
	const val DOWN = 0
	const val UP = 1
	const val NORTH = 2
	const val SOUTH = 3
	const val WEST = 4
	const val EAST = 5
	
	lateinit var renderBlocks: RenderBlocks
	lateinit var srcBlock: Block
	lateinit var tile: TileDoubleCamo
	
	var renderPass = 0
	
	override fun shouldRender3DInInventory(modelId: Int) = true
	
	override fun getRenderId() = LibRenderIDs.idSimpleDoubleBlock
	
	override fun renderInventoryBlock(block: Block, metadata: Int, modelID: Int, renderBlocks: RenderBlocks) {
		glPushMatrix()
		ASJRenderHelper.setBlend()
		glAlphaFunc(GL_GREATER, 0.003921569f)
		
		glRotatef(90f, 0f, 1f, 0f)
		glTranslatef(-0.5f, -0.5f, -0.5f)
		
		val tes = Tessellator.instance
		tes.startDrawingQuads()
		
		val tile = (block as? ITileEntityProvider)?.createNewTileEntity(mc.theWorld, metadata) as? TileDoubleCamo
		
		val blockBottom = tile?.blockBottom ?: Blocks.stained_glass
		val blockBottomMeta = tile?.blockBottomMeta ?: 15
		val blockTop = tile?.blockTop ?: Blocks.stonebrick
		val blockTopMeta = tile?.blockTopMeta ?: 0
		
		var icon: Block
		var meta: Int
		
		val topSide = (block as? BlockDoubleCamo)?.topSide(if (block is BlockSecretGlass) 4 else metadata) ?: 0
		
		if (topSide == DOWN) {
			icon = blockTop
			meta = blockTopMeta
		} else {
			icon = blockBottom
			meta = blockBottomMeta
		}
		tes.setNormal(0f, -1f, 0f)
		renderBlocks.renderFaceYNeg(icon, 0.0, 0.0, 0.0, renderBlocks.getIconSafe(icon.getIcon(DOWN , meta)))
		
		if (topSide == UP) {
			icon = blockTop
			meta = blockTopMeta
		} else {
			icon = blockBottom
			meta = blockBottomMeta
		}
		tes.setNormal(0f, 1f, 0f)
		renderBlocks.renderFaceYPos(icon, 0.0, 0.0, 0.0, renderBlocks.getIconSafe(icon.getIcon(UP   , meta)))
		
		if (topSide == NORTH) {
			icon = blockTop
			meta = blockTopMeta
		} else {
			icon = blockBottom
			meta = blockBottomMeta
		}
		tes.setNormal(0f, 0f, -1f)
		renderBlocks.renderFaceZNeg(icon, 0.0, 0.0, 0.0, renderBlocks.getIconSafe(icon.getIcon(NORTH, meta)))
		
		if (topSide == SOUTH) {
			icon = blockTop
			meta = blockTopMeta
		} else {
			icon = blockBottom
			meta = blockBottomMeta
		}
		tes.setNormal(0f, 0f, 1f)
		renderBlocks.renderFaceZPos(icon, 0.0, 0.0, 0.0, renderBlocks.getIconSafe(icon.getIcon(SOUTH, meta)))
		
		if (topSide == WEST) {
			icon = blockTop
			meta = blockTopMeta
		} else {
			icon = blockBottom
			meta = blockBottomMeta
		}
		tes.setNormal(-1f, 0f, 0f)
		renderBlocks.renderFaceXNeg(icon, 0.0, 0.0, 0.0, renderBlocks.getIconSafe(icon.getIcon(WEST , meta)))
		
		if (topSide == EAST) {
			icon = blockTop
			meta = blockTopMeta
		} else {
			icon = blockBottom
			meta = blockBottomMeta
		}
		tes.setNormal(1f, 0f, 0f)
		renderBlocks.renderFaceXPos(icon, 0.0, 0.0, 0.0, renderBlocks.getIconSafe(icon.getIcon(EAST , meta)))
		
		tes.draw()
		
		glAlphaFunc(GL_GREATER, 0.1f)
		ASJRenderHelper.discard()
		glPopMatrix()
	}
	
	override fun renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelID: Int, rb: RenderBlocks): Boolean {
		renderPass = MinecraftForgeClient.getRenderPass()
		tile = world.getTileEntity(x, y, z) as? TileDoubleCamo ?: return false
		srcBlock = block
		renderBlocks = rb
		
		var did = false
		val oldMeta = tile.worldObj.getBlockMetadata(x, y, z)
		val topSide = (block as BlockDoubleCamo).topSide(rb.blockAccess, x, y, z)
		
		tile.worldObj.setBlockMetadataWithNotify(x, y, z, tile.blockBottomMeta, 4)
		did = did or renderBlock(ItemStack(tile.blockBottom, 1, tile.blockBottomMeta), x, y, z, topSide, false, block is BlockCurtainPlacer && !tile.blockTop.isOpaqueCube)
		
		tile.worldObj.setBlockMetadataWithNotify(x, y, z, tile.blockTopMeta, 4)
		did = did or renderBlock(ItemStack(tile.blockTop, 1, tile.blockTopMeta), x, y, z, topSide, true)
		
		tile.worldObj.setBlockMetadataWithNotify(x, y, z, oldMeta, 4)
		
		return did
	}
	
	fun renderBlock(stack: ItemStack, x: Int, y: Int, z: Int, topSide: Int, drawingTop: Boolean, doubleBottom: Boolean = false): Boolean {
		var did = false
		
		if (doubleBottom || drawingTop && topSide == DOWN || !drawingTop && topSide != DOWN)
			if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(tile.worldObj, x, y - 1, z, DOWN) || renderBlocks.renderMinY > 0) {
				if (doubleBottom) Tessellator.instance.addTranslation(0f, 0.001f, 0f)
				did = renderMultiTexturedSide(stack, x, y, z, DOWN)
				if (doubleBottom) Tessellator.instance.addTranslation(0f, -0.001f, 0f)
			}
		
		if (drawingTop && topSide == UP || !drawingTop && topSide != UP)
			if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(tile.worldObj, x, y + 1, z, UP) || renderBlocks.renderMaxY < 1)
				did = did or renderMultiTexturedSide(stack, x, y, z, UP)
		
		if (drawingTop && topSide == NORTH || !drawingTop && topSide != NORTH)
			if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(tile.worldObj, x, y, z - 1, NORTH) || renderBlocks.renderMinZ > 0)
				did = did or renderMultiTexturedSide(stack, x, y, z, NORTH)
		
		if (drawingTop && topSide == SOUTH || !drawingTop && topSide != SOUTH)
			if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(tile.worldObj, x, y, z + 1, SOUTH) || renderBlocks.renderMaxZ < 1)
				did = did or renderMultiTexturedSide(stack, x, y, z, SOUTH)
		
		if (drawingTop && topSide == WEST || !drawingTop && topSide != WEST)
			if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(tile.worldObj, x - 1, y, z, WEST) || renderBlocks.renderMinX > 0)
				did = did or renderMultiTexturedSide(stack, x, y, z, WEST)
		
		if (drawingTop && topSide == EAST || !drawingTop && topSide != EAST)
			if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(tile.worldObj, x + 1, y, z, EAST) || renderBlocks.renderMaxX < 1)
				did = did or renderMultiTexturedSide(stack, x, y, z, EAST)
		
		return did
	}
	
	fun renderMultiTexturedSide(stack: ItemStack, x: Int, y: Int, z: Int, side: Int): Boolean {
		var did = false
		val block = stack.block
		
		if (block.canRenderInPass(renderPass)) {
			val tempRotation = getTextureRotation(side)
			if (blockRotates(stack))
				setTextureRotationForDirectionalBlock(side, stack.meta)
			
			setColorAndRender(stack, x, y, z, side, getIcon(stack, x, y, z, side))
			setTextureRotation(side, tempRotation)
			did = true
		}
		
		if (renderPass == 0 && block == Blocks.grass && side > 1) {
			if (Minecraft.isFancyGraphicsEnabled()) {
				setColorAndRender(ItemStack(Blocks.grass), x, y, z, side, BlockGrass.getIconSideOverlay())
			} else {
				setColorAndRender(ItemStack(Blocks.dirt), x, y, z, side, BlockSecretGlass.grass_side_overlay_default) // TODO add colored default grass overlay
			}
			
			did = true
		}
		
		return did
	}
	
	fun getTextureRotation(side: Int): Int {
		val rotations = intArrayOf(renderBlocks.uvRotateBottom, renderBlocks.uvRotateTop, renderBlocks.uvRotateNorth, renderBlocks.uvRotateSouth, renderBlocks.uvRotateWest, renderBlocks.uvRotateEast)
		return rotations[side]
	}
	
	fun blockRotates(stack: ItemStack) = stack.block.let { it is BlockQuartz || it is BlockRotatedPillar }
	
	fun setTextureRotationForDirectionalBlock(side: Int, meta: Int) {
		val dir = meta and 12
		
		when (side) {
			DOWN  -> if (meta == 3 || dir == 4) renderBlocks.uvRotateBottom = 1
			UP    -> if (meta == 3 || dir == 4) renderBlocks.uvRotateTop = 1
			NORTH -> if (meta == 3 || dir == 4) renderBlocks.uvRotateNorth = 1
			SOUTH -> if (meta == 3 || dir == 4) renderBlocks.uvRotateSouth = 1
			WEST  -> if (meta == 3 || dir == 8) renderBlocks.uvRotateWest = 1
			EAST  -> if (meta == 3 || dir == 8) renderBlocks.uvRotateEast = 1
		}
	}
	
	fun setTextureRotation(side: Int, rotation: Int) {
		when (side) {
			DOWN  -> renderBlocks.uvRotateBottom = rotation
			UP    -> renderBlocks.uvRotateTop = rotation
			NORTH -> renderBlocks.uvRotateNorth = rotation
			SOUTH -> renderBlocks.uvRotateSouth = rotation
			WEST  -> renderBlocks.uvRotateWest = rotation
			EAST  -> renderBlocks.uvRotateEast = rotation
		}
	}
	
	fun getIcon(stack: ItemStack, x: Int, y: Int, z: Int, side: Int) =
		(if (renderBlocks.hasOverrideBlockTexture())
			renderBlocks.overrideBlockTexture
		else
			renderBlocks.getIconSafe(stack.block.getIcon(renderBlocks.blockAccess, x, y, z, side)))!!
	
	fun setColorAndRender(stack: ItemStack, x: Int, y: Int, z: Int, side: Int, icon: IIcon) {
		val color = getBlockColor(stack.block, x, y, z, side, icon)
		
		val brightness = when (side) {
			DOWN    -> stack.block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, if (renderBlocks.renderMinY > 0.0) y else y - 1, z)
			UP      -> stack.block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, if (renderBlocks.renderMaxY < 1.0) y else y + 1, z)
			NORTH   -> stack.block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, if (renderBlocks.renderMinZ > 0.0) z else z - 1)
			SOUTH   -> stack.block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, if (renderBlocks.renderMaxZ < 1.0) z else z + 1)
			WEST    -> stack.block.getMixedBrightnessForBlock(renderBlocks.blockAccess, if (renderBlocks.renderMinX > 0.0) x else x - 1, y, z)
			EAST    -> stack.block.getMixedBrightnessForBlock(renderBlocks.blockAccess, if (renderBlocks.renderMaxX < 1.0) x else x + 1, y, z)
			else    -> 0
		}
		
		Tessellator.instance.setBrightness(brightness)
		
		val lightness = floatArrayOf(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f)[side]
		val (r, g, b) = Color(color).getRGBColorComponents(null)
		Tessellator.instance.setColorOpaque_F(r * lightness, g * lightness, b * lightness)
		
		render(x, y, z, side, icon)
	}
	
	fun getBlockColor(block: Block, x: Int, y: Int, z: Int, side: Int, icon: IIcon?): Int {
		if (block == Blocks.grass && side != UP && icon != BlockGrass.getIconSideOverlay()) return 0xFFFFFF
		return block.colorMultiplier(tile.worldObj, x, y, z)
	}
	
	fun render(x: Int, y: Int, z: Int, side: Int, icon: IIcon) {
		when (side) {
			DOWN  -> RenderHelper.renderFaceYNeg(renderBlocks, x.D, y.D, z.D, icon)
			UP    -> RenderHelper.renderFaceYPos(renderBlocks, x.D, y.D, z.D, icon)
			NORTH -> RenderHelper.renderFaceZNeg(renderBlocks, x.D, y.D, z.D, icon)
			SOUTH -> RenderHelper.renderFaceZPos(renderBlocks, x.D, y.D, z.D, icon)
			WEST  -> RenderHelper.renderFaceXNeg(renderBlocks, x.D, y.D, z.D, icon)
			EAST  -> RenderHelper.renderFaceXPos(renderBlocks, x.D, y.D, z.D, icon)
		}
	}
}

private object RenderHelper {
	
	var rotation = 0
	var uMin = 0.0
	var uMax = 0.0
	var vMin = 0.0
	var vMax = 0.0
	var xMin = 0.0
	var xMax = 0.0
	var yMin = 0.0
	var yMax = 0.0
	var zMin = 0.0
	var zMax = 0.0
	var uTL = 0.0
	var vTL = 0.0
	var uBL = 0.0
	var vBL = 0.0
	var uBR = 0.0
	var vBR = 0.0
	var uTR = 0.0
	var vTR = 0.0
	var renderOffset = 0.0
	var clearFloat = false
	var floatingIcon = false
	
	fun setCornerUV(t_uTL: Double, t_vTL: Double, t_uBL: Double, t_vBL: Double, t_uBR: Double, t_vBR: Double, t_uTR: Double, t_vTR: Double) {
		uTL = t_uTL
		vTL = t_vTL
		uBL = t_uBL
		vBL = t_vBL
		uBR = t_uBR
		vBR = t_vBR
		uTR = t_uTR
		vTR = t_vTR
	}
	
	fun prepareRender(renderBlocks: RenderBlocks, side: ForgeDirection?, x: Double, y: Double, z: Double, icon: IIcon) {
		if (icon === BlockGrass.getIconSideOverlay() || icon.iconName.contains("overlay/overlay_") && icon.iconName.endsWith("_side")) {
			floatingIcon = true
			clearFloat = true
		}
		
		xMin = x + renderBlocks.renderMinX - renderOffset
		xMax = x + renderBlocks.renderMaxX + renderOffset
		yMin = y + renderBlocks.renderMinY - renderOffset
		yMax = y + renderBlocks.renderMaxY + renderOffset
		zMin = z + renderBlocks.renderMinZ - renderOffset
		zMax = z + renderBlocks.renderMaxZ + renderOffset
		
		when (side) {
			ForgeDirection.DOWN  -> rotation = renderBlocks.uvRotateBottom
			ForgeDirection.UP    -> rotation = renderBlocks.uvRotateTop
			ForgeDirection.NORTH -> rotation = renderBlocks.uvRotateNorth
			ForgeDirection.SOUTH -> rotation = renderBlocks.uvRotateSouth
			ForgeDirection.WEST  -> rotation = renderBlocks.uvRotateWest
			ForgeDirection.EAST  -> rotation = renderBlocks.uvRotateEast
			else                 -> Unit
		}
		
		when (side) {
			ForgeDirection.DOWN  -> when (rotation) {
				0    -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMinX * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMaxX * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMinZ * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMaxZ * 16).D
					setCornerUV(uMax, vMax, uMax, vMin, uMin, vMin, uMin, vMax)
					return
				}
				
				1    -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMaxZ * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMinZ * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMinX * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMaxX * 16).D
					setCornerUV(uMin, vMax, uMax, vMax, uMax, vMin, uMin, vMin)
					return
				}
				
				2    -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMinX * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMaxX * 16).D
					vMin = icon.getInterpolatedV(16 - renderBlocks.renderMinZ * 16).D
					vMax = icon.getInterpolatedV(16 - renderBlocks.renderMaxZ * 16).D
					setCornerUV(uMax, vMax, uMax, vMin, uMin, vMin, uMin, vMax)
					return
				}
				
				3    -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMaxZ * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMinZ * 16).D
					vMin = icon.getInterpolatedV(16 - renderBlocks.renderMinX * 16).D
					vMax = icon.getInterpolatedV(16 - renderBlocks.renderMaxX * 16).D
					setCornerUV(uMin, vMax, uMax, vMax, uMax, vMin, uMin, vMin)
					return
				}
				
				else -> return
			}
			
			ForgeDirection.UP    -> when (rotation) {
				0    -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMinX * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMaxX * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMinZ * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMaxZ * 16).D
					setCornerUV(uMax, vMax, uMax, vMin, uMin, vMin, uMin, vMax)
					return
				}
				
				1    -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMaxZ * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMinZ * 16).D
					vMin = icon.getInterpolatedV(16 - renderBlocks.renderMinX * 16).D
					vMax = icon.getInterpolatedV(16 - renderBlocks.renderMaxX * 16).D
					setCornerUV(uMin, vMax, uMax, vMax, uMax, vMin, uMin, vMin)
					return
				}
				
				2    -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMinX * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMaxX * 16).D
					vMin = icon.getInterpolatedV(16 - renderBlocks.renderMinZ * 16).D
					vMax = icon.getInterpolatedV(16 - renderBlocks.renderMaxZ * 16).D
					setCornerUV(uMax, vMax, uMax, vMin, uMin, vMin, uMin, vMax)
					return
				}
				
				3    -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMaxZ * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMinZ * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMinX * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMaxX * 16).D
					setCornerUV(uMin, vMax, uMax, vMax, uMax, vMin, uMin, vMin)
					return
				}
				
				else -> return
			}
			
			ForgeDirection.NORTH -> when (rotation) {
				0    -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMaxX * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMinX * 16).D
					vMin = icon.getInterpolatedV(16 - (if (floatingIcon) 1 - (renderBlocks.renderMaxY - renderBlocks.renderMinY) else renderBlocks.renderMinY) * 16).D
					vMax = icon.getInterpolatedV(16 - (if (floatingIcon) 1.0 else renderBlocks.renderMaxY) * 16).D
					setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax)
					return
				}
				
				1    -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMaxY * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMinY * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMaxX * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMinX * 16).D
					setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax)
					return
				}
				
				2    -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMaxX * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMinX * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMinY * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMaxY * 16).D
					setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax)
					return
				}
				
				3    -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMaxY * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMinY * 16).D
					vMin = icon.getInterpolatedV(16 - renderBlocks.renderMaxX * 16).D
					vMax = icon.getInterpolatedV(16 - renderBlocks.renderMinX * 16).D
					setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax)
					return
				}
				
				else -> return
			}
			
			ForgeDirection.SOUTH -> when (rotation) {
				0    -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMinX * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMaxX * 16).D
					vMin = icon.getInterpolatedV(16 - (if (floatingIcon) 1 - (renderBlocks.renderMaxY - renderBlocks.renderMinY) else renderBlocks.renderMinY) * 16).D
					vMax = icon.getInterpolatedV(16 - (if (floatingIcon) 1.0 else renderBlocks.renderMaxY) * 16).D
					setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax)
					return
				}
				
				1    -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMaxY * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMinY * 16).D
					vMin = icon.getInterpolatedV(16 - renderBlocks.renderMinX * 16).D
					vMax = icon.getInterpolatedV(16 - renderBlocks.renderMaxX * 16).D
					setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax)
					return
				}
				
				2    -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMinX * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMaxX * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMinY * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMaxY * 16).D
					setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax)
					return
				}
				
				3    -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMaxY * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMinY * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMinX * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMaxX * 16).D
					setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax)
					return
				}
				
				else -> return
			}
			
			ForgeDirection.WEST  -> when (rotation) {
				0    -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMinZ * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMaxZ * 16).D
					vMax = icon.getInterpolatedV(16 - (if (floatingIcon) 1.0 else renderBlocks.renderMaxY) * 16).D
					vMin = icon.getInterpolatedV(16 - (if (floatingIcon) 1 - (renderBlocks.renderMaxY - renderBlocks.renderMinY) else renderBlocks.renderMinY) * 16).D
					setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax)
					return
				}
				
				1    -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMaxY * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMinY * 16).D
					vMin = icon.getInterpolatedV(16 - renderBlocks.renderMinZ * 16).D
					vMax = icon.getInterpolatedV(16 - renderBlocks.renderMaxZ * 16).D
					setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax)
					return
				}
				
				2    -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMinZ * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMaxZ * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMinY * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMaxY * 16).D
					setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax)
					return
				}
				
				3    -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMaxY * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMinY * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMinZ * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMaxZ * 16).D
					setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax)
					return
				}
				
				else -> return
			}
			
			ForgeDirection.EAST  -> when (rotation) {
				0 -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMaxZ * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMinZ * 16).D
					vMax = icon.getInterpolatedV(16 - (if (floatingIcon) 1.0 else renderBlocks.renderMaxY) * 16).D
					vMin = icon.getInterpolatedV(16 - (if (floatingIcon) 1 - (renderBlocks.renderMaxY - renderBlocks.renderMinY) else renderBlocks.renderMinY) * 16).D
					setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax)
				}
				
				1 -> {
					uMin = icon.getInterpolatedU(16 - renderBlocks.renderMaxY * 16).D
					uMax = icon.getInterpolatedU(16 - renderBlocks.renderMinY * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMaxZ * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMinZ * 16).D
					setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax)
				}
				
				2 -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMaxZ * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMinZ * 16).D
					vMin = icon.getInterpolatedV(renderBlocks.renderMinY * 16).D
					vMax = icon.getInterpolatedV(renderBlocks.renderMaxY * 16).D
					setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax)
				}
				
				3 -> {
					uMin = icon.getInterpolatedU(renderBlocks.renderMaxY * 16).D
					uMax = icon.getInterpolatedU(renderBlocks.renderMinY * 16).D
					vMin = icon.getInterpolatedV(16 - renderBlocks.renderMaxZ * 16).D
					vMax = icon.getInterpolatedV(16 - renderBlocks.renderMinZ * 16).D
					setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax)
				}
			}
			else -> Unit
		}
	}
	
	fun renderFaceYNeg(renderBlocks: RenderBlocks, x: Double, y: Double, z: Double, icon: IIcon) {
		prepareRender(renderBlocks, ForgeDirection.DOWN, x, y, z, icon)
		
		Tessellator.instance.addVertexWithUV(xMin, yMin, zMax, uTR, vTR)
		Tessellator.instance.addVertexWithUV(xMin, yMin, zMin, uBR, vBR)
		Tessellator.instance.addVertexWithUV(xMax, yMin, zMin, uBL, vBL)
		Tessellator.instance.addVertexWithUV(xMax, yMin, zMax, uTL, vTL)
	}
	
	fun renderFaceYPos(renderBlocks: RenderBlocks, x: Double, y: Double, z: Double, icon: IIcon) {
		prepareRender(renderBlocks, ForgeDirection.UP, x, y, z, icon)
		
		Tessellator.instance.addVertexWithUV(xMax, yMax, zMax, uTL, vTL)
		Tessellator.instance.addVertexWithUV(xMax, yMax, zMin, uBL, vBL)
		Tessellator.instance.addVertexWithUV(xMin, yMax, zMin, uBR, vBR)
		Tessellator.instance.addVertexWithUV(xMin, yMax, zMax, uTR, vTR)
	}
	
	fun renderFaceZNeg(renderBlocks: RenderBlocks, x: Double, y: Double, z: Double, icon: IIcon) {
		prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon)
		
		Tessellator.instance.addVertexWithUV(xMax, yMax, zMin, uTL, vTL)
		Tessellator.instance.addVertexWithUV(xMax, yMin, zMin, uBL, vBL)
		Tessellator.instance.addVertexWithUV(xMin, yMin, zMin, uBR, vBR)
		Tessellator.instance.addVertexWithUV(xMin, yMax, zMin, uTR, vTR)
	}
	
	fun renderFaceZPos(renderBlocks: RenderBlocks, x: Double, y: Double, z: Double, icon: IIcon) {
		prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon)
		
		Tessellator.instance.addVertexWithUV(xMin, yMax, zMax, uTL, vTL)
		Tessellator.instance.addVertexWithUV(xMin, yMin, zMax, uBL, vBL)
		Tessellator.instance.addVertexWithUV(xMax, yMin, zMax, uBR, vBR)
		Tessellator.instance.addVertexWithUV(xMax, yMax, zMax, uTR, vTR)
	}
	
	fun renderFaceXNeg(renderBlocks: RenderBlocks, x: Double, y: Double, z: Double, icon: IIcon) {
		prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon)
		
		Tessellator.instance.addVertexWithUV(xMin, yMax, zMin, uTL, vTL)
		Tessellator.instance.addVertexWithUV(xMin, yMin, zMin, uBL, vBL)
		Tessellator.instance.addVertexWithUV(xMin, yMin, zMax, uBR, vBR)
		Tessellator.instance.addVertexWithUV(xMin, yMax, zMax, uTR, vTR)
	}
	
	fun renderFaceXPos(renderBlocks: RenderBlocks, x: Double, y: Double, z: Double, icon: IIcon) {
		prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon)
		
		Tessellator.instance.addVertexWithUV(xMax, yMax, zMax, uTL, vTL)
		Tessellator.instance.addVertexWithUV(xMax, yMin, zMax, uBL, vBL)
		Tessellator.instance.addVertexWithUV(xMax, yMin, zMin, uBR, vBR)
		Tessellator.instance.addVertexWithUV(xMax, yMax, zMin, uTR, vTR)
	}
}