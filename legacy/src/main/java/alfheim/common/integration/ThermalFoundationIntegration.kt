package alfheim.common.integration

import cofh.core.render.IconRegistry
import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.common.lib.LibOreDict

object ThermalFoundationIntegration {
	
	val loaded = Loader.isModLoaded("ThermalFoundation")
	
	var cryothenumBlock: Block = Blocks.flowing_water!!

	init {
		if (loaded) init()
	}
	
	fun init() {
		cryothenumBlock = GameRegistry.findBlock("ThermalFoundation", "FluidCryotheum")
		
		addOreDict()
	}
	
	fun addOreDict() {
		GameRegistry.findItem("ThermalFoundation", "material")?.let {
			OreDictionary.registerOre(LibOreDict.MANA_STEEL, ItemStack(it, 1, 70))
			OreDictionary.registerOre(LibOreDict.MANASTEEL_NUGGET, ItemStack(it, 1, 102))
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	fun replaceIcons(paramPost: TextureStitchEvent.Pre) {
		if ((paramPost as TextureStitchEvent).map.textureType == 0) {
			IconRegistry.addIcon("FluidPrimalmana", "thermalfoundation:fluid/Fluid_Mana_Still", paramPost.map)
			IconRegistry.addIcon("FluidPrimalmana1", "thermalfoundation:fluid/Fluid_Mana_Flow", paramPost.map)
		}
	}
}
