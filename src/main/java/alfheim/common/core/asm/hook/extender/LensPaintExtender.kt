package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.*
import alexsocol.patcher.asm.hook.ASJSuperWrapperHandler
import alfheim.api.ModInfo
import alfheim.common.block.AlfheimBlocks.irisDirt
import alfheim.common.block.AlfheimBlocks.irisPlanks
import alfheim.common.block.AlfheimBlocks.irisSlabs
import alfheim.common.block.AlfheimBlocks.irisSlabsFull
import alfheim.common.block.AlfheimBlocks.irisStairs
import alfheim.common.block.AlfheimBlocks.irisWood0
import alfheim.common.block.AlfheimBlocks.irisWood1
import alfheim.common.block.AlfheimBlocks.irisWood2
import alfheim.common.block.AlfheimBlocks.irisWood3
import alfheim.common.block.AlfheimBlocks.rainbowDirt
import alfheim.common.block.AlfheimBlocks.rainbowPlanks
import alfheim.common.block.AlfheimBlocks.rainbowSlab
import alfheim.common.block.AlfheimBlocks.rainbowSlabFull
import alfheim.common.block.AlfheimBlocks.rainbowStairs
import alfheim.common.block.AlfheimBlocks.rainbowWood
import alfheim.common.block.AlfheimBlocks.starBlock
import alfheim.common.block.AlfheimBlocks.starBlock2
import alfheim.common.block.tile.*
import alfheim.common.item.ItemIridescent
import cpw.mods.fml.relauncher.*
import gloomyfolken.hooklib.asm.*
import net.minecraft.client.renderer.entity.RenderSheep
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.opengl.GL11
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.item.lens.*
import java.awt.Color

object LensPaintExtender {
	
	val colorDirt by lazy { arrayOf(Blocks.dirt, irisDirt, rainbowDirt) }
	val colorWoods by lazy { arrayOf(irisWood0, irisWood1, irisWood2, irisWood3, rainbowWood) }
	val colorPlanks by lazy { arrayOf(irisPlanks, rainbowPlanks) }
	val colorStairs by lazy { arrayOf(*irisStairs, rainbowStairs) }
	val colorSlabs by lazy { arrayOf(*irisSlabs, rainbowSlab) }
	val colorSlabsFull by lazy { arrayOf(*irisSlabsFull, rainbowSlabFull) }
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun collideBurst(lens: LensPaint, burst: IManaBurst, entity: EntityThrowable, pos: MovingObjectPosition, isManaBlock: Boolean, dead: Boolean, stack: ItemStack?): Boolean {
		val storedColor = ItemLens.getStoredColor(stack)
		if (burst.isFake || storedColor !in 0..16) return dead
		
		val entityHit = pos.entityHit
		val world = entity.worldObj
		
		if (entityHit is EntitySheep) {
			val r = 20
			val targetColor = if (ASJSuperWrapperHandler.getFlag(entityHit, 6)) 16 else entityHit.fleeceColor
			val sheepList = getEntitiesWithinAABB(world, EntitySheep::class.java, getBoundingBox(entityHit.posX, entityHit.posY, entityHit.posZ).expand(r))
			for (sheep in sheepList) {
				if ((if (ASJSuperWrapperHandler.getFlag(sheep, 6)) 16 else sheep.fleeceColor) != targetColor) continue
				
				if (storedColor == 16) {
					sheep.fleeceColor = 0
					ASJSuperWrapperHandler.setFlag(sheep, 6, true)
				} else {
					sheep.fleeceColor = storedColor
					ASJSuperWrapperHandler.setFlag(sheep, 6, false)
				}
			}
			return true
		}
		
		val block = world.getBlock(pos.blockX, pos.blockY, pos.blockZ)
		if (!BotaniaAPI.paintableBlocks.contains(block)) return dead
		
		val meta = world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ)
		val coordsToPaint = ArrayList<ChunkCoordinates>()
		val coordsFound = ArrayList<ChunkCoordinates>()
		
		coordsFound.add(ChunkCoordinates(pos.blockX, pos.blockY, pos.blockZ))
		
		do {
			val iterCoords = ArrayList<ChunkCoordinates>(coordsFound)
			for (coords in iterCoords) {
				coordsFound.remove(coords)
				coordsToPaint.add(coords)
				for (dir in ForgeDirection.VALID_DIRECTIONS) {
					val coords_ = ChunkCoordinates(coords.posX + dir.offsetX, coords.posY + dir.offsetY, coords.posZ + dir.offsetZ)
					if (coordsFound.contains(coords_) || coordsToPaint.contains(coords_)) continue
					
					val block_ = world.getBlock(coords.posX + dir.offsetX, coords.posY + dir.offsetY, coords.posZ + dir.offsetZ)
					val meta_ = world.getBlockMetadata(coords.posX + dir.offsetX, coords.posY + dir.offsetY, coords.posZ + dir.offsetZ)
					
					if (block === block_ && meta == meta_) coordsFound.add(coords_)
				}
			}
		} while (coordsFound.isNotEmpty() && coordsToPaint.size < 1000)
		
		for (coords in coordsToPaint) {
			val (x, y, z) = coords
			var rainbow = storedColor == 16
			val placeColor = if (rainbow) world.rand.nextInt(16) else storedColor
			
			val metaThere = world.getBlockMetadata(x, y, z)
			
			val done = when {
				block inl colorWoods -> {
					if (rainbow) {
						if (block !== rainbowWood) world.setBlock(x, y, z, rainbowWood, metaThere / 4 * 4, 2) else false
					} else {
						val newBlock = colorWoods[placeColor / 4]
						val newMeta = metaThere / 4 * 4 + placeColor % 4
						world.setBlock(x, y, z, newBlock, newMeta, 2)
						true
					}
				}
				block inl colorPlanks -> {
					if (rainbow) {
						if (block !== rainbowPlanks) world.setBlock(x, y, z, rainbowPlanks) else false
					} else {
						if (block !== irisPlanks || metaThere != placeColor) world.setBlock(x, y, z, irisPlanks, placeColor, 2) else false
					}
				}
				block inl colorStairs -> {
					if (rainbow) {
						if (block !== rainbowStairs) world.setBlock(x, y, z, rainbowStairs, metaThere, 2) else false
					} else {
						if (block !== irisStairs[placeColor]) world.setBlock(x, y, z, irisStairs[placeColor], metaThere, 2) else false
					}
				}
				block inl colorSlabs -> {
					if (rainbow) {
						if (block !== rainbowSlab) world.setBlock(x, y, z, rainbowSlab, metaThere, 2) else false
					} else {
						if (block !== irisSlabs[placeColor]) world.setBlock(x, y, z, irisSlabs[placeColor], metaThere, 2) else false
					}
				}
				block inl colorSlabsFull -> {
					if (rainbow) {
						if (block !== rainbowSlabFull) world.setBlock(x, y, z, rainbowSlabFull, metaThere, 2) else false
					} else {
						if (block !== irisSlabsFull[placeColor]) world.setBlock(x, y, z, irisSlabsFull[placeColor], metaThere, 2) else false
					}
				}
				block inl colorDirt -> {
					if (rainbow) {
						if (block !== rainbowDirt) world.setBlock(x, y, z, rainbowDirt) else false
					} else {
						if (block !== irisDirt || metaThere != placeColor) world.setBlock(x, y, z, irisDirt, placeColor, 2) else false
					}
				}
				block === starBlock -> run {
					val tile = world.getTileEntity(x, y, z) as? TileStar ?: return@run false
					tile.starColor = if (rainbow) -1 else {
						val (r, g, b) = EntitySheep.fleeceColorTable[placeColor]
						Color(r, g, b).rgb
					}
					true
				}
				block === starBlock2 -> run {
					val tile = world.getTileEntity(x, y, z) as? TileCracklingStar ?: return@run false
					tile.color = if (rainbow) -1 else {
						val (r, g, b) = EntitySheep.fleeceColorTable[placeColor]
						Color(r, g, b).rgb
					}
					true
				}
				block === ModBlocks.livingrock && meta == 0 -> world.setBlock(x, y, z, ModBlocks.shimmerrock)
				block === ModBlocks.dreamwood && meta == 1 -> world.setBlock(x, y, z, ModBlocks.shimmerwoodPlanks)
				else -> run {
					if (metaThere == placeColor) return@run false
					if (!world.isRemote) world.setBlockMetadataWithNotify(x, y, z, placeColor, 2)
					rainbow = false
					true
				}
			}
			
			if (!done || !world.isRemote) continue
			
			val (r, g, b) = if (rainbow) Color(ItemIridescent.rainbowColor()).getRGBColorComponents(null) else EntitySheep.fleeceColorTable[placeColor]
			for (i in 0..3) Botania.proxy.sparkleFX(world, x + Math.random(), y + Math.random(), z + Math.random(), r, g, b, 0.6f + Math.random().F * 0.3f, 5)
		}
		return dead
	}
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(injectOnExit = true, isMandatory = false)
	fun shouldRenderPass(render: RenderSheep, sheep: EntitySheep, pass: Int, ticks: Float, @Hook.ReturnValue result: Int): Int {
		if (result != 1 || !ASJSuperWrapperHandler.getFlag(sheep, 6)) return result
		
		val (r, g, b) = Color.getHSBColor((sheep.ticksExisted * 2 + sheep.entityId + ticks) % 360 / 360F, 1F, 1F).getRGBColorComponents(null)
		GL11.glColor3f(r, g, b)
		return result
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, isMandatory = false)
	fun writeEntityToNBT(sheep: EntitySheep, nbt: NBTTagCompound) {
		nbt.setBoolean(TAG_RAINBOW, ASJSuperWrapperHandler.getFlag(sheep, 6))
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, isMandatory = false)
	fun readEntityFromNBT(sheep: EntitySheep, nbt: NBTTagCompound) {
		ASJSuperWrapperHandler.setFlag(sheep, 6, nbt.getBoolean(TAG_RAINBOW))
	}
	
	const val TAG_RAINBOW = "${ModInfo.MODID}:rainbow"
}