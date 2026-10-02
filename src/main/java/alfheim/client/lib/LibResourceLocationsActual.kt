package alfheim.client.lib

// PORT: LibResources Botania r1.8 → ResourcesLib Botania 1.20.1 (полные пути botania:…)
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.api.lib.LibResourceLocations.ResourceLocationIL
//import alfheim.common.item.equipment.bauble.ItemPriestEmblem
import vazkii.botania.client.lib.ResourcesLib

object LibResourceLocationsActual {
	
	fun init() {
		LibResourceLocations.babylon = ResourceLocationIL(ResourcesLib.MISC_BABYLON)
		// PORT: «розовый» пилон Botania 1.7.10 — пилон Гайи в 1.20.1
		LibResourceLocations.elvenPylon = ResourceLocationIL(ResourcesLib.MODEL_PYLON_GAIA)
		// PORT: КТ-3 — старых моделей пилонов в Botania 1.20.1 нет; решается при переносе RenderTileAlfheimPylons
//		LibResourceLocations.elvenPylonOld = ResourceLocationIL(LibResources.MODEL_PYLON_PINK_OLD)
		LibResourceLocations.glowCyan = ResourceLocationIL(ResourcesLib.MISC_GLOW_CYAN)
		LibResourceLocations.halo = ResourceLocationIL(ResourcesLib.MISC_HALO)
		// PORT: КТ-7 — текстуры объёмной книги lexica в Botania 1.20.1 нет; решается при переносе RenderContributors
//		LibResourceLocations.lexica = ResourceLocationIL(LibResources.MODEL_LEXICA)
		// PORT: в ResourcesLib 1.20.1 нет константы для этого фона; путь — из ресурсов Botania 1.20.1
		LibResourceLocations.manaInfuserOverlay = ResourceLocationIL("botania", "textures/gui/mana_infusion_overlay.png")
		LibResourceLocations.pixie = ResourceLocationIL(ResourcesLib.MODEL_PIXIE)
		
		// PORT: КТ-4 — ItemPriestEmblem
		/*
		LibResourceLocations.godCloak = Array(ItemPriestEmblem.TYPES) {
			ResourceLocationIL(ModInfo.MODID, "textures/model/armor/cloak/God$it.png")
		}
		
		LibResourceLocations.godCloakGlow = Array(ItemPriestEmblem.TYPES) {
			ResourceLocationIL(ModInfo.MODID, "textures/model/armor/cloak/God${it}_glow.png")
		}
		*/
	}
}