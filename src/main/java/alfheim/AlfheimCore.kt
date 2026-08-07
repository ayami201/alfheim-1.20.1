package alfheim

import alexsocol.patcher.*
import alexsocol.patcher.asm.worker.InterfaceAppenderWorker.registerAdditionalInterface
import alfheim.api.ModInfo.MODID
import alfheim.common.core.command.*
import alfheim.common.core.handler.*
import alfheim.common.core.handler.ragnarok.*
import alfheim.common.core.proxy.*
import alfheim.common.core.util.*
import alfheim.common.integration.minetweaker.*
import alfheim.common.integration.thaumcraft.*
import alfheim.common.integration.tinkersconstruct.*
import alfheim.common.integration.travellersgear.*
import alfheim.common.integration.waila.*
import alfheim.common.network.*
import cpw.mods.fml.common.*
import cpw.mods.fml.common.Mod.*
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.*
import net.minecraft.server.*
import vazkii.botania.common.*

@Suppress("UNUSED_PARAMETER")
@Mod(modid = MODID, dependencies = "required-after:Botania", useMetadata = true, guiFactory = "$MODID.client.gui.GUIFactory", modLanguageAdapter = KotlinAdapter.className)
object AlfheimCore {
	
	@KotlinProxy(clientSide = "$MODID.client.core.proxy.ClientProxy", serverSide = "$MODID.common.core.proxy.CommonProxy")
	lateinit var proxy: CommonProxy
	
//	@KotlinProxy(clientSide = "ab.client.core.proxy.ClientProxy", serverSide = "ab.common.core.proxy.CommonProxy")
//	lateinit var abProxy: ab.common.core.proxy.CommonProxy
	
	@Metadata(MODID)
	lateinit var meta: ModMetadata
	
	val save: String get() = MinecraftServer.getServer().entityWorld.saveHandler.worldDirectory.absolutePath
	
	var MineTweakerLoaded = false
	var NEILoaded = false
	var stupidMode = false
	var TiCLoaded = false
	var TravellersGearLoaded = false
	var TwilightForestLoaded = false
	
	val jingleTheBells: Boolean
	
	// do not reassign this unless you know what you are doing
	var winter: Boolean
		get() {
			return when {
				RagnarokHandler.winter -> true
				RagnarokHandler.summer -> false
				else                   -> field
			}
		}
	
	init {
		AlfheimTab
		
		jingleTheBells = (TimeHandler.month == 12 && TimeHandler.day >= 16 || TimeHandler.month == 1 && TimeHandler.day <= 8)
		winter = TimeHandler.month in arrayOf(1, 2, 12, 13)
	}
	
	@EventHandler
	fun construct(e: FMLConstructionEvent) {
		if (!Loader.isModLoaded("Thaumcraft")) return
		// oh no! ... anyway
		registerAdditionalInterface("vazkii/botania/common/item/interaction/thaumcraft/ItemElementiumHelmRevealing", "thaumcraft/api/IVisDiscountGear")
		registerAdditionalInterface("vazkii/botania/common/item/interaction/thaumcraft/ItemManasteelHelmRevealing", "thaumcraft/api/IVisDiscountGear")
		registerAdditionalInterface("vazkii/botania/common/item/interaction/thaumcraft/ItemTerrasteelHelmRevealing", "thaumcraft/api/IVisDiscountGear")
	}
	
	@EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
//		abProxy.preInit(e)
		
		MineTweakerLoaded = Loader.isModLoaded("MineTweaker3")
		NEILoaded = Loader.isModLoaded("NotEnoughItems")
		TiCLoaded = Loader.isModLoaded("TConstruct")
		TravellersGearLoaded = Loader.isModLoaded("TravellersGear")
		TwilightForestLoaded = Loader.isModLoaded("TwilightForest")
		
		stupidMode = Loader.isModLoaded("Avaritia")
		
		if (AlfheimConfigHandler.notifications) InfoLoader.start()
		
		NetworkService
		
		proxy.preInit()
		if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.preInit()
	}
	
	@EventHandler
	fun init(e: FMLInitializationEvent) {
//		abProxy.init(e)
		
		proxy.init()
		proxy.initializeAndRegisterHandlers()
	}
	
	@EventHandler
	fun postInit(e: FMLPostInitializationEvent) {
//		abProxy.postInit(e)
		
		proxy.registerKeyBinds()
		proxy.registerRenderThings()
		proxy.postInit()
		if (MineTweakerLoaded) MinetweakerAlfheimConfig.loadConfig()
		if (Botania.thaumcraftLoaded) {
			ThaumcraftAlfheimConfig.loadConfig()
			ThaumcraftAlfheimModule.postInit()
		}
		if (TravellersGearLoaded) TravellersGearAlfheimConfig.loadConfig()
		if (TiCLoaded) TinkersConstructAlfheimConfig.loadConfig()
		if (Loader.isModLoaded("Waila")) WAILAAlfheimConfig.loadConfig()
	}
	
	@EventHandler
	fun postPostInit(e: FMLLoadCompleteEvent) {
		if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.postPostInit()
	}
	
	@EventHandler
	fun starting(e: FMLServerStartingEvent) {
		if (AlfheimConfigHandler.enableElvenStory) AlfheimConfigHandler.initWorldCoordsForElvenStory(save)
		AlfheimConfigHandler.syncConfig()
		e.registerServerCommand(CommandAlfheim)
		e.registerServerCommand(CommandDebug)
		if (MineTweakerLoaded) e.registerServerCommand(CommandMTSpellInfo)
	}
}