package alfheim.common.item.rod

import alexsocol.asjlib.*
import alexsocol.asjlib.render.*
import alfheim.api.*
import alfheim.common.item.*
import alfheim.common.item.rod.RedstoneSignal.*
import alfheim.common.network.*
import alfheim.common.network.packet.*
import cpw.mods.fml.common.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.*
import cpw.mods.fml.common.gameevent.TickEvent.*
import net.minecraft.block.*
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.*
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.item.*
import net.minecraft.nbt.*
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.client.event.*
import net.minecraftforge.common.*
import net.minecraftforge.common.util.*
import org.lwjgl.opengl.GL11.*
import vazkii.botania.common.item.equipment.bauble.*
import java.awt.*

// improved version of redstone activator from RandomThings mod
// a lot of shit is going here don't ask me
class ItemRedstoneRod: ItemMod("RodRedstone") {
	
	init {
		setFull3D()
		maxStackSize = 1
	}
	
	override fun onItemUseFirst(stack: ItemStack?, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		player.swingItem()
		
		if (!world.isRemote) {
			RedstoneSignalHandler.get().addSignal(world, x, y, z, 10, 15, if (player.isSneaking) EnumRedstoneType.STRONG else EnumRedstoneType.WEAK)
			return true
		}
		
		return false
	}
	
	companion object {
		
		init {
			if (ASJUtilities.isClient)
				eventForge()
		}
		
		@SubscribeEvent
		fun onWorldRenderLast(event: RenderWorldLastEvent) {
			val world = mc.theWorld ?: return
			val player = mc.thePlayer ?: return
			if (player.heldItem?.item !== AlfheimItems.rodRedstone || !player.isSneaking || !ItemMonocle.hasMonocle(player)) return
			
			glPushMatrix()
			glPushAttrib(GL_LIGHTING)
			glDisable(GL_DEPTH_TEST)
			glDisable(GL_TEXTURE_2D)
			glDisable(GL_LIGHTING)
			glEnable(GL_BLEND)
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
			
			Tessellator.renderingWorldRenderer = false
			for (x in player.posX.mfloor().bidiRange(8))
				for (y in player.posY.mfloor().bidiRange(8))
					for (z in player.posZ.mfloor().bidiRange(8)) {
						val block = world.getBlock(x, y, z)
						if (block === Blocks.air) continue
						
						if (world.getBlockPowerInput(x, y, z) > 0)
							renderBlockOutlineAt(ChunkCoordinates(x, y, z), 0xFF0000, 3F, world, block)
						else if (world.getStrongestIndirectPower(x, y, z) > 0)
							renderBlockOutlineAt(ChunkCoordinates(x, y, z), 0x800000, 1F, world, block)
					}
			
			glEnable(GL_TEXTURE_2D)
			
			val mop = mc.objectMouseOver
			if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) run {
				val x = mop.blockX
				val y = mop.blockY
				val z = mop.blockZ
				
				val colorStrong = 0xFF0000
				val colorWeak = 0x800000
				val powerStrong = world.getBlockPowerInput(x, y, z)
				val powerWeak = world.getStrongestIndirectPower(x, y, z)
				
				if (powerStrong == 0 && powerWeak == 0) return@run
				
				val font = mc.fontRenderer
				
				val textStrong = "${if (powerStrong < 10) " " else ""}$powerStrong    "
				val textWeak = "    $powerWeak${if (powerWeak < 10) " " else ""}"
				val offsetStrong = font.getStringWidth(textStrong) / -2f
				val offsetWeak = font.getStringWidth(textWeak) / -2f
				
				glPushMatrix()
				ASJRenderHelper.interpolatedTranslationReverse(player)
				glTranslated(x + 0.5, y + 0.5, z + 0.5)
				glScalef(-1/32f)
				
				glPushMatrix()
				glTranslatef(offsetStrong, -font.FONT_HEIGHT / 2f, 0f)
				font.drawString(textStrong, 0, 0, colorStrong)
				glTranslatef(-offsetStrong + offsetWeak, 0f, 0f)
				font.drawString(textWeak, 0, 0, colorWeak)
				glPopMatrix()
				
				glRotatef(180f, 0f, 1f, 0f)
				
				glPushMatrix()
				glTranslatef(offsetStrong, -font.FONT_HEIGHT / 2f, 0f)
				font.drawString(textStrong, 0, 0, colorStrong)
				glTranslatef(-offsetStrong + offsetWeak, 0f, 0f)
				font.drawString(textWeak, 0, 0, colorWeak)
				glPopMatrix()
				
				glPopMatrix()
			}
			
			glColor4f(1f, 1f, 1f, 1f)
			glEnable(GL_DEPTH_TEST)
			glDisable(GL_BLEND)
			glPopAttrib()
			glPopMatrix()
		}
		
		private fun renderBlockOutlineAt(pos: ChunkCoordinates, color: Int, thickness: Float, world: World, block: Block) {
			glPushMatrix()
			glTranslated(pos.posX - RenderManager.renderPosX, pos.posY - RenderManager.renderPosY, pos.posZ - RenderManager.renderPosZ + 1)
			
			val (r, g, b) = Color(color).getRGBColorComponents(null)
			glColor4f(r, g, b, 1f)
			
			run drawWireframe@ {
				val axis = block.getSelectedBoundingBoxFromPool(world, pos.posX, pos.posY, pos.posZ) ?: return@drawWireframe
				axis.offset(-pos.posX, -pos.posY, -pos.posZ - 1)
				
				glLineWidth(thickness)
				renderBlockOutline(axis)
				glLineWidth(thickness + 3f)
				glColor4f(r, g, b, 0.25f)
				renderBlockOutline(axis)
			}
			
			glPopMatrix()
		}
		
		private fun renderBlockOutline(aabb: AxisAlignedBB) {
			val tes = Tessellator.instance
			
			val ix = aabb.minX
			val iy = aabb.minY
			val iz = aabb.minZ
			val ax = aabb.maxX
			val ay = aabb.maxY
			val az = aabb.maxZ
			
			tes.startDrawing(GL_LINES)
			
			tes.addVertex(ix, iy, iz)
			tes.addVertex(ix, ay, iz)
			tes.addVertex(ix, ay, iz)
			tes.addVertex(ax, ay, iz)
			tes.addVertex(ax, ay, iz)
			tes.addVertex(ax, iy, iz)
			tes.addVertex(ax, iy, iz)
			tes.addVertex(ix, iy, iz)
			tes.addVertex(ix, iy, az)
			tes.addVertex(ix, ay, az)
			tes.addVertex(ix, iy, az)
			tes.addVertex(ax, iy, az)
			tes.addVertex(ax, iy, az)
			tes.addVertex(ax, ay, az)
			tes.addVertex(ix, ay, az)
			tes.addVertex(ax, ay, az)
			tes.addVertex(ix, iy, iz)
			tes.addVertex(ix, iy, az)
			tes.addVertex(ix, ay, iz)
			tes.addVertex(ix, ay, az)
			tes.addVertex(ax, iy, iz)
			tes.addVertex(ax, iy, az)
			tes.addVertex(ax, ay, iz)
			tes.addVertex(ax, ay, az)
			
			tes.draw()
		}
	}
}

open class RedstoneSignalHandler(datakey: String = ID): WorldSavedData(datakey) {
	
	var redstoneSignals = HashSet<RedstoneSignal>()
	
	private fun updatePosition(world: World, x: Int, y: Int, z: Int) {
		val block = world.getBlock(x, y, z)
		block.onNeighborBlockChange(world, x, y, z, Blocks.redstone_block)
		world.notifyBlocksOfNeighborChange(x, y, z, Blocks.redstone_block)
	}
	
	@Synchronized
	open fun addSignal(worldObj: World, x: Int, y: Int, z: Int, duration: Int, strength: Int, type: EnumRedstoneType = EnumRedstoneType.STRONG): Boolean {
		return if (worldObj.blockExists(x, y, z)) {
			val signal = RedstoneSignal(worldObj.provider.dimensionId, x, y, z, duration, strength, type)
			if (signal in redstoneSignals) redstoneSignals.remove(signal)
			redstoneSignals.add(signal)
			updatePosition(worldObj, x, y, z)
			true
		} else {
			false
		}
	}
	
	@Synchronized
	open fun tick() {
		// CMEs are amazing (c) Vazkii
		redstoneSignals.toMutableSet().forEach { rs ->
			val (dim, x, y, z) = rs
			val world = DimensionManager.getWorld(dim) ?: return@forEach
			if (!world.blockExists(x, y, z)) return@forEach
			if (!rs.tick()) return@forEach
			
			redstoneSignals.remove(rs)
			updatePosition(world, x, y, z)
		}
		
		NetworkService.sendToAll(MessageRedstoneSignalsSync(redstoneSignals))
	}
	
	@Synchronized
	open fun getPower(world: World, x: Int, y: Int, z: Int): Pair<Int, EnumRedstoneType> {
		val signal = redstoneSignals.firstOrNull { (dim, sx, sy, sz) -> dim == world.provider.dimensionId && x == sx && sy == y && sz == z } ?: return 0 to EnumRedstoneType.NONE
		return signal.strength to signal.type
	}
	
	@Synchronized
	override fun readFromNBT(nbt: NBTTagCompound) {
		val list = nbt.getTagList("redstoneSignals", Constants.NBT.TAG_COMPOUND)
		
		repeat(list.tagCount()) {
			redstoneSignals.add(RedstoneSignal.readFromNBT(list.getCompoundTagAt(it)))
		}
	}
	
	@Synchronized
	override fun writeToNBT(nbt: NBTTagCompound) {
		val list = NBTTagList()
		redstoneSignals.forEach {
			list.appendTag(it.writeToNBT(NBTTagCompound()))
		}
		nbt.setTag("redstoneSignals", list)
	}
	
	companion object {
		
		const val ID = "${ModInfo.MODID}_RedstoneSignalHandler"
		
		init {
			eventFML()
		}
		
		fun get(): RedstoneSignalHandler {
			val server = FMLCommonHandler.instance().minecraftServerInstance ?: return RedstoneSignalHandlerClient
			val overWorld = server.worldServers.getOrElse(0) { return RedstoneSignalHandlerDummy }
			
			var handler = overWorld.mapStorage.loadData(RedstoneSignalHandler::class.java, ID) as RedstoneSignalHandler?
			
			if (handler == null) {
				handler = RedstoneSignalHandler()
				overWorld.mapStorage.setData(ID, handler)
			}
			
			return handler
		}
		
		@SubscribeEvent
		fun tick(e: WorldTickEvent) {
			if (e.phase == Phase.END && !e.world.isRemote && e.world.provider.dimensionId == 0)
				get().tick()
		}
	}
	
	override fun isDirty() = true
}

object RedstoneSignalHandlerClient: RedstoneSignalHandler("$ID-Client")

object RedstoneSignalHandlerDummy: RedstoneSignalHandler("$ID-Dummy") {
	override fun addSignal(worldObj: World, x: Int, y: Int, z: Int, duration: Int, strength: Int, type: EnumRedstoneType) = false
	override fun tick() = Unit
	override fun getPower(world: World, x: Int, y: Int, z: Int) = 0 to EnumRedstoneType.NONE
	override fun isDirty() = false
	override fun readFromNBT(nbt: NBTTagCompound) = Unit
	override fun writeToNBT(nbt: NBTTagCompound) = Unit
}

data class RedstoneSignal(var dimension: Int, var x: Int, var y: Int, var z: Int, var duration: Int, var strength: Int, var type: EnumRedstoneType, var age: Int = 0) {
	
	fun tick() = age++ >= duration
	
	fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
		nbt.setInteger("dimension", dimension)
		nbt.setInteger("x", x)
		nbt.setInteger("y", y)
		nbt.setInteger("z", z)
		nbt.setInteger("strength", strength)
		nbt.setInteger("duration", duration)
		nbt.setInteger("type", type.ordinal)
		nbt.setInteger("age", age)
		
		return nbt
	}
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		
		other as RedstoneSignal
		
		if (dimension != other.dimension) return false
		if (x != other.x) return false
		if (y != other.y) return false
		if (z != other.z) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = dimension
		result = 31 * result + x
		result = 31 * result + y
		result = 31 * result + z
		return result
	}
	
	enum class EnumRedstoneType {
		NONE, WEAK, STRONG
	}
	
	companion object {
		
		fun readFromNBT(nbt: NBTTagCompound): RedstoneSignal {
			val dimension = nbt.getInteger("dimension")
			val x = nbt.getInteger("x")
			val y = nbt.getInteger("y")
			val z = nbt.getInteger("z")
			val strength = nbt.getInteger("strength")
			val duration = nbt.getInteger("duration")
			val type = EnumRedstoneType.entries[nbt.getInteger("type")]
			val age = nbt.getInteger("age")
			
			return RedstoneSignal(dimension, x, y, z, duration, strength, type, age)
		}
	}
}
