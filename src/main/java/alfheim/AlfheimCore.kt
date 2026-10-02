package alfheim

// PORT: импорты 1.7.10 заменены на 1.20.1; импорты кода, который ещё не перенесён, закомментированы до его КТ
//import alexsocol.patcher.*
//import alexsocol.patcher.asm.worker.InterfaceAppenderWorker.registerAdditionalInterface
import alfheim.api.ModInfo.MODID
import alfheim.client.core.proxy.ClientProxy
//import alfheim.common.core.command.*
import alfheim.common.core.handler.*
//import alfheim.common.core.handler.ragnarok.*
import alfheim.common.core.proxy.*
import alfheim.common.core.util.*
//import alfheim.common.integration.minetweaker.*
//import alfheim.common.integration.thaumcraft.*
//import alfheim.common.integration.tinkersconstruct.*
//import alfheim.common.integration.travellersgear.*
//import alfheim.common.integration.waila.*
import alfheim.common.network.*
import alfheim.port.registry.AlfheimRegisters
import net.minecraft.world.level.storage.LevelResource
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.*
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.server.ServerLifecycleHooks
import thedarkcolour.kotlinforforge.forge.*
//import vazkii.botania.common.*
import java.io.File
import java.util.function.Supplier

@Suppress("UNUSED_PARAMETER")
// PORT: dependencies и useMetadata → META-INF/mods.toml; modLanguageAdapter → modLoader="kotlinforforge" там же; guiFactory (экран настроек) не переносится: в Forge 1.20.1 встроенного экрана нет, настройки показывает мод Configured
//@Mod(modid = MODID, dependencies = "required-after:Botania", useMetadata = true, guiFactory = "$MODID.client.gui.GUIFactory", modLanguageAdapter = KotlinAdapter.className)
@Mod(MODID)
object AlfheimCore {
	
	// PORT: @KotlinProxy → DistExecutor (SPEC, Р-11): клиентский класс загружается только на клиенте
//	@KotlinProxy(clientSide = "$MODID.client.core.proxy.ClientProxy", serverSide = "$MODID.common.core.proxy.CommonProxy")
	val proxy: CommonProxy = DistExecutor.unsafeRunForDist({ Supplier { ClientProxy } }, { Supplier { CommonProxy() } })
	
//	@KotlinProxy(clientSide = "ab.client.core.proxy.ClientProxy", serverSide = "ab.common.core.proxy.CommonProxy")
//	lateinit var abProxy: ab.common.core.proxy.CommonProxy
	
	// PORT: @Metadata ModMetadata → IModInfo из ModList
	val meta: IModInfo get() = ModList.get().getModContainerById(MODID).get().modInfo
	
	// PORT: папка мира сервера; LevelResource.ROOT даёт путь с «.» на конце, normalize() его убирает
	val save: String get() = ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize().toString()
	
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
				// PORT: КТ-8 — RagnarokHandler
//				RagnarokHandler.winter -> true
//				RagnarokHandler.summer -> false
				else                   -> field
			}
		}
	
	init {
		// PORT: реестры мода — на шину мода до события регистрации (DeferredRegister, SPEC, Р-5)
		AlfheimRegisters.register(MOD_BUS)
		
		AlfheimTab
		
		jingleTheBells = (TimeHandler.month == 12 && TimeHandler.day >= 16 || TimeHandler.month == 1 && TimeHandler.day <= 8)
		winter = TimeHandler.month in arrayOf(1, 2, 12, 13)
		
		// PORT: в 1.20.1 нет событий FML 1.7.10 (@EventHandler). preInit вызывается из конструктора мода:
		// регистрация и конфиг в 1.20.1 возможны только здесь. Остальные — подписки на события Forge,
		// соответствие — в docs/port/MAPPING.md, раздел «События, сеть, конфиг, команды»
		preInit()
		MOD_BUS.addListener(EventPriority.NORMAL, false, FMLCommonSetupEvent::class.java, ::init)
		MOD_BUS.addListener(EventPriority.NORMAL, false, InterModProcessEvent::class.java, ::postInit)
		FORGE_BUS.addListener(EventPriority.NORMAL, false, ServerStartingEvent::class.java, ::starting)
	}
	
	// PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//	@EventHandler
//	fun construct(e: FMLConstructionEvent) {
//		if (!Loader.isModLoaded("Thaumcraft")) return
//		// oh no! ... anyway
//		registerAdditionalInterface("vazkii/botania/common/item/interaction/thaumcraft/ItemElementiumHelmRevealing", "thaumcraft/api/IVisDiscountGear")
//		registerAdditionalInterface("vazkii/botania/common/item/interaction/thaumcraft/ItemManasteelHelmRevealing", "thaumcraft/api/IVisDiscountGear")
//		registerAdditionalInterface("vazkii/botania/common/item/interaction/thaumcraft/ItemTerrasteelHelmRevealing", "thaumcraft/api/IVisDiscountGear")
//	}
	
	// PORT: было @EventHandler fun preInit(e: FMLPreInitializationEvent); вызывается из init, см. выше
	fun preInit() {
		// PORT: core.cfg автор читал в coremod (AlfheimHookLoader), до загрузки модов. Coremod в 1.20.1 нет,
		// поэтому файл читается здесь, первым. Файлы конфига — .toml (alfheim.port.config.Configuration)
		AlfheimPreConfigHandler.loadPreConfig(File("config/Alfheim/core.cfg"))
		AlfheimConfigHandler.loadConfig(File("config/Alfheim/mod.cfg"))
		
//		abProxy.preInit(e)
		
		// PORT: выпало — MineTweaker отсутствует на 1.20.1 (SPEC, п. 7)
//		MineTweakerLoaded = Loader.isModLoaded("MineTweaker3")
		// PORT: КТ-10 — NEI → JEI
//		NEILoaded = Loader.isModLoaded("NotEnoughItems")
		// PORT: КТ-10 — Tinkers' Construct 3
//		TiCLoaded = Loader.isModLoaded("TConstruct")
		// PORT: выпало — Travellers Gear отсутствует на 1.20.1 (SPEC, п. 7)
//		TravellersGearLoaded = Loader.isModLoaded("TravellersGear")
		// PORT: КТ-10 — Twilight Forest
//		TwilightForestLoaded = Loader.isModLoaded("TwilightForest")
		
		// PORT: id модов в 1.20.1 пишутся строчными буквами
		stupidMode = ModList.get().isLoaded("avaritia")
		
		if (AlfheimConfigHandler.notifications) InfoLoader.start()
		
		NetworkService
		
		proxy.preInit()
		// PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//		if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.preInit()
	}
	
	// PORT: было @EventHandler, FMLInitializationEvent
	fun init(e: FMLCommonSetupEvent) {
//		abProxy.init(e)
		
		proxy.init()
		proxy.initializeAndRegisterHandlers()
	}
	
	// PORT: было @EventHandler, FMLPostInitializationEvent
	fun postInit(e: InterModProcessEvent) {
//		abProxy.postInit(e)
		
		// PORT: клавиши и рендер в 1.20.1 регистрируются в своих событиях; здесь остаётся то, что не регистрация (ClientProxy)
		proxy.registerKeyBinds()
		proxy.registerRenderThings()
		proxy.postInit()
		// PORT: выпало — MineTweaker отсутствует на 1.20.1 (SPEC, п. 7)
//		if (MineTweakerLoaded) MinetweakerAlfheimConfig.loadConfig()
		// PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//		if (Botania.thaumcraftLoaded) {
//			ThaumcraftAlfheimConfig.loadConfig()
//			ThaumcraftAlfheimModule.postInit()
//		}
		// PORT: выпало — Travellers Gear отсутствует на 1.20.1 (SPEC, п. 7)
//		if (TravellersGearLoaded) TravellersGearAlfheimConfig.loadConfig()
		// PORT: КТ-10 — Tinkers' Construct 3
//		if (TiCLoaded) TinkersConstructAlfheimConfig.loadConfig()
		// PORT: КТ-10 — WAILA → Jade
//		if (Loader.isModLoaded("Waila")) WAILAAlfheimConfig.loadConfig()
	}
	
	// PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//	@EventHandler
//	fun postPostInit(e: FMLLoadCompleteEvent) {
//		if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.postPostInit()
//	}
	
	// PORT: было @EventHandler, FMLServerStartingEvent
	fun starting(e: ServerStartingEvent) {
		// PORT: КТ-7 — Elven Story
//		if (AlfheimConfigHandler.enableElvenStory) AlfheimConfigHandler.initWorldCoordsForElvenStory(save)
		AlfheimConfigHandler.syncConfig()
		// PORT: КТ-7 — команды; в 1.20.1 они регистрируются в RegisterCommandsEvent
//		e.registerServerCommand(CommandAlfheim)
//		e.registerServerCommand(CommandDebug)
		// PORT: выпало — MineTweaker отсутствует на 1.20.1 (SPEC, п. 7)
//		if (MineTweakerLoaded) e.registerServerCommand(CommandMTSpellInfo)
	}
}