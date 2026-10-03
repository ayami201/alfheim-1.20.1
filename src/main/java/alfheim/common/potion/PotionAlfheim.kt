package alfheim.common.potion

// PORT: импорты 1.20.1; PotionMod Botania 1.7.10 — alfheim.port.legacy.botania.PotionMod
import alexsocol.asjlib.*
import alfheim.api.lib.LibResourceLocations
import alfheim.port.legacy.botania.PotionMod
//import cpw.mods.fml.relauncher.*
//import org.lwjgl.opengl.GL11.*

open class PotionAlfheim(id: Int, name: String, badEffect: Boolean, color: Int): PotionMod(id, name, badEffect, color, nextIconID) {
	
	val iconSet = set
	
	init {
		setPotionName("alfheim.potion.$name")
		
		if (ASJUtilities.isClient) LibResourceLocations.potions(iconSet) // just load
		// PORT: лист иконок, из которого getStatusIconIndex ниже рисовал иконку, — его рисует клиент
		// (alfheim.port.client.LegacyEffectIcons)
		iconSheet = LibResourceLocations.potions(iconSet)
	}
	
	/*
	@SideOnly(Side.CLIENT)
	override fun getStatusIconIndex(): Int {
		glEnable(GL_BLEND)
		val id = super.getStatusIconIndex()
		mc.renderEngine.bindTexture(LibResourceLocations.potions(iconSet))
		return id
	}
	*/
	
	companion object {
		
		private var set = 0
		
		private var nextIconID = 0
		get() {
			if (field >= 24) {
				++set
				field -= 24
			}
			return field++
		}
	}
}