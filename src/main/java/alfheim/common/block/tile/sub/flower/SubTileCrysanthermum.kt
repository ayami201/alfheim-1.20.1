package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.item.EntityItem
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import org.lwjgl.opengl.GL11
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.subtile.*
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModFluffBlocks
import java.awt.Color
import java.util.*
import kotlin.math.*

/**
 * @author WireSegal
 * Created at 8:37 AM on 2/3/16.
 */
class SubTileCrysanthermum: SubTileGenerating() {
	
	companion object {
		
		const val TAG_TEMPERATURE = "flowerHeat"
		const val TAG_DIMINISHING = "diminishing"
		const val DIFFERENTIATION = 4
		const val RANGE = 1
		
		val TYPES = arrayOf(
			+1,   // Forest
			+2,   // Plains
			-4,   // Mountain
			null, // Mushroom, rand [-4; 4]
			-2,   // Swamp
			+4,   // Desert
			-3,   // Taiga
			+3    // Mesa
		)
		
		/**
		 * Map inp from an integer range (x1<->x2) to a second (y1<->y2)
		 */
		private fun map(inp: Float, x1: Float, x2: Float, y1: Float, y2: Float): Float {
			val distance = (inp - x1) / (x2 - x1)
			return (distance * (y2 - y1)) + y1
		}
	}
	
	var temp = 0
		set(temp) {
			field = max(min(temp, 8), -8)
			supertile.markDirty()
		}
	
	var diminishing = 0
	var lastBlocks = LinkedList(IntArray(DIFFERENTIATION) { -1 }.toList())
	
	fun getTemp(meta: Int) = TYPES[meta] ?: ASJUtilities.randInBounds(-4, 4, supertile.worldObj.rand)
	
	val biomeTemp: Int
		get() {
			val t = supertile.worldObj.getBiomeGenForCoordsBody(supertile.xCoord, supertile.zCoord).getFloatTemperature(supertile.xCoord, supertile.yCoord, supertile.zCoord)
			return map(t, -2f, 2f, -6f, 6f).roundToInt()
		}
	
	override fun onUpdate() {
		if (ticksExisted == 0) temp = biomeTemp
		
		super.onUpdate()
		
		if (linkedCollector == null) return
		
		val remote = supertile.worldObj.isRemote
		val biomeStone = ModFluffBlocks.biomeStoneA.toItem()
		val items = getEntitiesWithinAABB(supertile.worldObj, EntityItem::class.java, supertile.boundingBox(1))
		val slowdown = slowdownFactor
		
		if (ticksExisted % 600 == 0) { // 30 seconds
			val bt = biomeTemp
			if (temp > bt) temp--
			else if (temp < bt) temp++
		}
		
		for (item in items) {
			val stack = item.entityItem
			if (stack != null && stack.item === biomeStone && !item.isDead && item.age >= slowdown) {
				val meta = stack.meta % 8
				
				if (!remote && canGeneratePassively()) {
					diminishing = if (lastBlocks.contains(meta)) DIFFERENTIATION else max(0, diminishing - 1)
					if (lastBlocks.size >= DIFFERENTIATION) lastBlocks.removeFirst()
					lastBlocks.addLast(meta)
					temp += getTemp(meta)
					sync()
				}
				
				for (i in 0..9) {
					val m = 0.2
					val mx = (Math.random() - 0.5) * m
					val my = (Math.random() - 0.5) * m
					val mz = (Math.random() - 0.5) * m
					supertile.worldObj.spawnParticle("blockcrack_${stack.item.id}_$meta", item.posX, item.posY, item.posZ, mx, my, mz)
				}
				
				if (!remote) {
					--item.entityItem.stackSize
					
					if (item.entityItem.stackSize <= 0) item.setDead()
				}
			}
		}
		
		val c = Color(color)
		if (ticksExisted % 20 == 0)
			Botania.proxy.wispFX(supertile.worldObj, supertile.xCoord + 0.5, supertile.yCoord + 0.75, supertile.zCoord + 0.5, c.red / 255f, c.green / 255f, c.blue / 255f, 0.25f, -0.025f)
	}
	
	override fun getComparatorInputOverride(side: Int): Int = if (temp == -8 || temp == 8) 0 else temp + 8
	override fun getRadius(): RadiusDescriptor = RadiusDescriptor.Square(toChunkCoordinates(), RANGE)
	override fun getMaxMana() = 800
	override fun getValueForPassiveGeneration() = max(0, (abs(biomeTemp - temp) * if (diminishing > 0) 3 else 6).D.pow(2).div(20).roundToInt())
	override fun canGeneratePassively() = temp in -7..7
	override fun getDelayBetweenPassiveGeneration() = 5
	override fun getColor() = Color.HSBtoRGB(map(temp.F, -8f, 8f, 235f, 360f) / 360f, 1f, 1f)
	override fun getEntry() = AlfheimLexiconData.flowerCrysanthermum
	
	override fun renderHUD(mc: Minecraft, res: ScaledResolution) {
		super.renderHUD(mc, res)
		
		GL11.glEnable(GL11.GL_BLEND)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		val name = StatCollector.translateToLocal("misc.${ModInfo.MODID}:temperature.${temp + 8}")
		val width = 16 + mc.fontRenderer.getStringWidth(name) / 2
		val x = res.scaledWidth / 2 - width
		val y = res.scaledHeight / 2 + 30
		
		mc.fontRenderer.drawStringWithShadow(name, x + 20, y, color)
		
		GL11.glDisable(GL11.GL_LIGHTING)
		GL11.glDisable(GL11.GL_BLEND)
	}
	
	override fun writeToPacketNBT(nbt: NBTTagCompound) {
		super.writeToPacketNBT(nbt)
		nbt.setInteger(TAG_TEMPERATURE, temp)
		nbt.setInteger(TAG_DIMINISHING, diminishing)
		for (i in 0 until DIFFERENTIATION)
			nbt.setInteger("last_$i", lastBlocks[i])
	}
	
	override fun readFromPacketNBT(nbt: NBTTagCompound) {
		super.readFromPacketNBT(nbt)
		temp = nbt.getInteger(TAG_TEMPERATURE)
		diminishing = nbt.getInteger(TAG_DIMINISHING)
		for (i in 0 until DIFFERENTIATION)
			lastBlocks[i] = nbt.getInteger("last_$i")
	}
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("crysanthermum").getIconForStack(null)
}
