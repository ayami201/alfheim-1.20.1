package alfheim.common.block.tile.sub.flower

import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.util.IIcon
import net.minecraft.world.World
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.subtile.signature.PassiveFlower
import vazkii.botania.common.block.subtile.generating.SubTilePassiveGenerating
import vazkii.botania.common.block.tile.TileFloatingSpecialFlower
import kotlin.math.roundToInt

abstract class SubTileWeatherFlower: SubTilePassiveGenerating() {
	
	val yO get() = if (supertile is TileFloatingSpecialFlower) 1 else 0
	
	override fun getDelayBetweenPassiveGeneration() = 1
	
	override fun getMaxMana() = 200
	
	override fun getColor() = 0x9CFFFF
	
	override fun canGeneratePassively(): Boolean {
		return canGenerate(supertile.worldObj, supertile.xCoord, supertile.yCoord, supertile.zCoord)
	}
	
	abstract fun canGenerate(world: World, x: Int, y: Int, z: Int): Boolean
}

@PassiveFlower
class SubTileRainFlower: SubTileWeatherFlower() {
	
	var prevMore = false
	
	override fun getValueForPassiveGeneration(): Int {
		prevMore = !prevMore
		return if (prevMore) 1 else 2
	}
	
	override fun canGenerate(world: World, x: Int, y: Int, z: Int): Boolean {
		return world.canLightningStrikeAt(x, y + yO, z)
	}
	
	override fun getEntry() = AlfheimLexiconData.flowerRain
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("rainFlower").getIconForStack(null)
}

@PassiveFlower
class SubTileSnowFlower: SubTileWeatherFlower() {
	
	override fun getValueForPassiveGeneration() = 2
	
	override fun canGenerate(world: World, x: Int, y: Int, z: Int): Boolean {
		return world.isRaining && world.func_147478_e(x, y, z, false) && world.canBlockSeeTheSky(x, y + yO, z)
	}
	
	override fun getEntry() = AlfheimLexiconData.flowerSnow
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("snowFlower").getIconForStack(null)
}

@PassiveFlower
class SubTileWindFlower: SubTileWeatherFlower() {
	
	var newMana = -1
	
	override fun getDelayBetweenPassiveGeneration() = 2
	
	override fun canGenerate(world: World, x: Int, y: Int, z: Int): Boolean {
		val ret = y > 127 && world.canBlockSeeTheSky(x, y + yO, z)
		
		newMana = (if (ret) y / 100 * if (world.canLightningStrikeAt(x, y + yO, z)) 1.5 else 0.75 else 0.0).roundToInt()
		
		return ret
	}
	
	override fun getValueForPassiveGeneration(): Int {
		return newMana
	}
	
	override fun getEntry() = AlfheimLexiconData.flowerWind
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("windFlower").getIconForStack(null)
}