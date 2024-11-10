package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alfheim.common.block.AlfheimBlocks
import cpw.mods.fml.relauncher.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL
import kotlin.math.*

class TileAnimatedTorch: ASJTile() {
	
	var side = 0
		set(value) {
			field = value
			
			val world = worldObj
			val x = xCoord
			val y = yCoord
			val z = zCoord
			
			when (value) {
				0 -> {
					world.notifyBlockOfNeighborChange(x, y, z + 1, blockType)
					world.notifyBlocksOfNeighborChange(x, y, z + 1, blockType, 2)
				}
				1 -> {
					world.notifyBlockOfNeighborChange(x - 1, y, z, blockType)
					world.notifyBlocksOfNeighborChange(x - 1, y, z, blockType, 5)
				}
				2 -> {
					world.notifyBlockOfNeighborChange(x, y, z - 1, blockType)
					world.notifyBlocksOfNeighborChange(x, y, z - 1, blockType, 3)
				}
				3 -> {
					world.notifyBlockOfNeighborChange(x + 1, y, z, blockType)
					world.notifyBlocksOfNeighborChange(x + 1, y, z, blockType, 4)
				}
			}
		}
	
	var rotation = 0.D
	var rotating = false
	var lastTickRotation = 0.D
	var nextRandomRotation = 0
	var currentRandomRotation = 0
	
	private var rotationTicks = 0
	var anglePerTick = 0.D
	
	private var torchMode = TorchMode.TOGGLE
	
	fun handRotate() {
		if (!worldObj.isRemote)
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, AlfheimBlocks.animatedTorch, 0, (side + 1) % 4)
	}
	
	fun onPlace(entity: EntityLivingBase?) {
		if (!worldObj.isRemote)
			nextRandomRotation = worldObj.rand.nextInt(4)
		
		if (entity != null)
			side = (MathHelper.wrapAngleTo180_float(entity.rotationYaw) * 4f / 360f + 0.5).mfloor() and 3
		
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType())
	}
	
	fun toggle() {
		if (worldObj.isRemote) return
		
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, AlfheimBlocks.animatedTorch, 0, torchMode.rotate(this, side))
		nextRandomRotation = worldObj.rand.nextInt(4)
		ASJUtilities.dispatchTEToNearbyPlayers(this)
	}
	
	fun onWanded() {
		torchMode = TorchMode.entries[(torchMode.ordinal + 1) % TorchMode.entries.size]
		ASJUtilities.dispatchTEToNearbyPlayers(this)
	}
	
	override fun receiveClientEvent(id: Int, param: Int): Boolean {
		return if (id == 0) {
			rotateTo(param)
			true
		} else {
			super.receiveClientEvent(id, param)
		}
	}
	
	fun rotateTo(side: Int) {
		if (rotating) return
		
		currentRandomRotation = nextRandomRotation
		val finalRotation = side * 90
		
		var diff = (finalRotation - rotation % 360) % 360
		if (diff < 0)
			diff += 360
		
		rotationTicks = 5
		anglePerTick = diff / rotationTicks
		this.side = side
		rotating = true
	}
	
	@SideOnly(Side.CLIENT)
	fun renderHUD(mc: Minecraft, res: ScaledResolution) {
		val x = res.scaledWidth / 2 + 10
		val y = res.scaledHeight / 2 - 8
		
		RenderHelper.enableGUIStandardItemLighting()
		glEnable(GL_RESCALE_NORMAL)
		RenderItem.getInstance().renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, ItemStack(AlfheimBlocks.animatedTorch), x, y)
		
		mc.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("tile.AnimatedTorch.desc." + torchMode.name.lowercase()), x + 18, y + 6, 0xFF4444)
	}
	
	override fun updateEntity() {
		if (rotating) {
			lastTickRotation = rotation
			rotation = (rotation + anglePerTick) % 360
			rotationTicks--
			
			if (rotationTicks <= 0) {
				rotating = false
				// done rotating, tell neighbors
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType())
			}
			
		} else
			rotation = (side * 90).D
		
		if (worldObj.isRemote) {
			val amt = if (rotating) 3 else if (Math.random() < 0.1) 1 else 0
			val x = xCoord.D + 0.5 + cos((rotation + 90) / 180.0 * Math.PI) * 0.35
			val y = yCoord + 0.2
			val z = zCoord.D + 0.5 + sin((rotation + 90) / 180.0 * Math.PI) * 0.35
			
			for (i in 0 until amt)
				worldObj.spawnParticle("reddust", x, y, z, 0.0, 0.0, 0.0)
		}
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setInteger(TAG_SIDE, side)
		nbt.setBoolean(TAG_ROTATING, rotating)
		nbt.setInteger(TAG_ROTATION_TICKS, rotationTicks)
		nbt.setDouble(TAG_ANGLE_PER_TICK, anglePerTick)
		nbt.setInteger(TAG_TORCH_MODE, torchMode.ordinal)
		nbt.setInteger(TAG_NEXT_RANDOM_ROTATION, nextRandomRotation)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		side = nbt.getInteger(TAG_SIDE)
		rotating = nbt.getBoolean(TAG_ROTATING)
		if (worldObj != null && !worldObj.isRemote)
			rotationTicks = nbt.getInteger(TAG_ROTATION_TICKS)
		anglePerTick = nbt.getDouble(TAG_ANGLE_PER_TICK)
		nextRandomRotation = nbt.getInteger(TAG_NEXT_RANDOM_ROTATION)
		
		val modeOrdinal = nbt.getInteger(TAG_TORCH_MODE)
		torchMode = TorchMode.entries[modeOrdinal % TorchMode.entries.size]
	}
	
	enum class TorchMode {
		TOGGLE, ROTATE, RANDOM;
		
		fun rotate(tile: TileAnimatedTorch, curr: Int): Int {
			return when (this) {
				TOGGLE -> (curr + 2) % 4
				ROTATE -> (curr + 1) % 4
				RANDOM -> tile.currentRandomRotation
			}
		}
	}
	
	companion object {
		
		const val TAG_SIDE = "side"
		const val TAG_ROTATING = "rotating"
		const val TAG_ROTATION_TICKS = "rotationTicks"
		const val TAG_ANGLE_PER_TICK = "anglePerTick"
		const val TAG_TORCH_MODE = "torchMode"
		const val TAG_NEXT_RANDOM_ROTATION = "nextRandomRotation"
		
		val SIDES = arrayOf(ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST)
	}
}