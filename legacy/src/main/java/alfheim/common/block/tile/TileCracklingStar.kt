package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alexsocol.asjlib.math.Vector3
import net.minecraft.nbt.NBTTagCompound
import vazkii.botania.common.Botania
import vazkii.botania.common.integration.coloredlights.ColoredLightHelper
import java.awt.Color
import java.util.*

class TileCracklingStar: ASJTile() {
	
	var posAbsolute = vecUnbound.copy()
		set(value) {
			field = value
			posRelative = value.copy().sub(xCoord, yCoord, zCoord)
		}
	
	var posRelative = Vector3()
	
	val rand = Random()
	
	private val TAG_COLOR = "color"
	private val TAG_SIZE = "size"
	var color = -1
	var size = 0.05f
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setInteger(TAG_COLOR, color)
		nbt.setFloat(TAG_SIZE, size)
		nbt.setDouble("relX", posRelative.x)
		nbt.setDouble("relY", posRelative.y)
		nbt.setDouble("relZ", posRelative.z)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		color = nbt.getInteger(TAG_COLOR)
		size = nbt.getFloat(TAG_SIZE)
		posRelative = Vector3(nbt.getDouble("relX"), nbt.getDouble("relY"), nbt.getDouble("relZ"))
		posAbsolute = posRelative.copy().add(xCoord, yCoord, zCoord)
	}
	
	fun getLightColor(): Int {
		val (r, g, b) = Color(color).getRGBColorComponents(null)
		return ColoredLightHelper.makeRGBLightValue(r, g, b, 1f)
	}
	
	override fun updateEntity() {
		if (worldObj.isRemote) {
			val cur = Vector3.fromTileEntity(this)
			
			if (posAbsolute != vecUnbound && posAbsolute != cur) {
				val vec = posAbsolute.copy().sub(Vector3.fromTileEntity(this))
				wispLine(Vector3.fromTileEntity(this).add(0.5 + (Math.random() - 0.5) * 0.05, 0.5 + (Math.random() - 0.5) * 0.05, 0.5 + (Math.random() - 0.5) * 0.05), vec, colorFromInt(color), Math.random() * 6.0, 10)
				wispLine(posAbsolute.copy().add(0.5 + (Math.random() - 0.5) * 0.05, 0.5 + (Math.random() - 0.5) * 0.05, 0.5 + (Math.random() - 0.5) * 0.05), vec.negate(), colorFromInt(color), Math.random() * 6.0, 10)
			} else {
				val c = Color(colorFromIntAndPos(color, cur))
				Botania.proxy.wispFX(worldObj, cur.x + 0.5, cur.y + 0.5, cur.z + 0.5, c.red / 255f, c.green / 255f, c.blue / 255f, 0.25f)
			}
		} else {
			val other = worldObj.getTileEntity(posAbsolute.x.mfloor(), posAbsolute.y.mfloor(), posAbsolute.z.mfloor()) as? TileCracklingStar
			if (other == null) {
				posAbsolute = vecUnbound.copy()
				ASJUtilities.dispatchTEToNearbyPlayers(this)
			}
		}
	}
	
	fun colorFromInt(color: Int): Int = if (color == -1) rainbow(1f) else color
	fun colorFromIntAndPos(color: Int, pos: Vector3) = if (color == -1) rainbow(pos, 1f) else color
	
	fun rainbow(saturation: Float) = Color.HSBtoRGB((Botania.proxy.worldElapsedTicks * 2L % 360L).F / 360f, saturation, 1f)
	fun rainbow(pos: Vector3, saturation: Float): Int {
		val ticks = (Botania.proxy.worldElapsedTicks * 2L % 360L).F / 360f
		val seed = ((pos.x.mfloor() xor pos.y.mfloor() xor pos.z.mfloor()) * 255 xor pos.hashCode()) % 360f / 360f
		return Color.HSBtoRGB(seed + ticks, saturation, 1F)
	}
	
	fun wispLine(start: Vector3, line: Vector3, color: Int, stepsPerBlock: Double, time: Int) {
		val len = line.length()
		val ray = line.copy().mul(1 / len)
		val steps = (len * stepsPerBlock).I
		
		for (i in 0 until steps) {
			val extended = ray.copy().mul(i / stepsPerBlock)
			val x = start.x + extended.x
			val y = start.y + extended.y
			val z = start.z + extended.z
			
			val c = Color(color)
			
			val r = c.red.F / 255f
			val g = c.green.F / 255f
			val b = c.blue.F / 255f
			
			Botania.proxy.wispFX(worldObj, x, y, z, r, g, b, time * 0.0125f)
		}
	}
	
	companion object {
		val vecUnbound = Vector3(0, 1, 0)
	}
}