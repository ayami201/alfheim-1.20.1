package alfheim

// PORT: импорты 1.7.10 заменены на 1.20.1; импорты кода, который ещё не перенесён, закомментированы до его КТ
//import alexsocol.patcher.*
//import alexsocol.patcher.asm.worker.InterfaceAppenderWorker.registerAdditionalInterface
import alfheim.api.ModInfo.MODID
//import alfheim.common.core.command.*
import alfheim.common.core.handler.*
//import alfheim.common.core.handler.ragnarok.*
//import alfheim.common.core.proxy.*
//import alfheim.common.core.util.*
//import alfheim.common.integration.minetweaker.*
//import alfheim.common.integration.thaumcraft.*
//import alfheim.common.integration.tinkersconstruct.*
//import alfheim.common.integration.travellersgear.*
//import alfheim.common.integration.waila.*
//import alfheim.common.network.*
import net.minecraft.world.level.storage.LevelResource
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.*
import net.minecraftforge.server.ServerLifecycleHooks
import thedarkcolour.kotlinforforge.forge.*
//import vazkii.botania.common.*
import java.io.File

@Suppress("UNUSED_PARAMETER")
// PORT: dependencies и useMetadata → META-INF/mods.toml; modLanguageAdapter → modLoader="kotlinforforge" там же; guiFactory (экран настроек) — КТ-1
//@Mod(modid = MODID, dependencies = "required-after:Botania", useMetadata = true, guiFactory = "$MODID.client.gui.GUIFactory", modLanguageAdapter = KotlinAdapter.className)
@Mod(MODID)
object AlfheimCore {
	
	// PORT: КТ-1 — прокси автора; сторона выбирается через DistExecutor (SPEC, Р-11)
//	@KotlinProxy(clientSide = "$MODID.client.core.proxy.ClientProxy", serverSide = "$MODID.common.core.proxy.CommonProxy")
//	lateinit var proxy: CommonProxy
	
//	@KotlinProxy(clientSide = "ab.client.core.proxy.ClientProxy", serverSide = "ab.common.core.proxy.CommonProxy")
//	lateinit var abProxy: ab.common.core.proxy.CommonProxy
	
	// PORT: КТ-1 — нужен InfoLoader; в 1.20.1 сведения о моде — IModInfo из ModList, а не ModMetadata
//	@Metadata(MODID)
//	lateinit var meta: ModMetadata
	
	// PORT: папка мира сервера; LevelResource.ROOT даёт путь с «.» на конце, normalize() его убирает
	val save: String get() = ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize().toString()
	
	var MineTweakerLoaded = false
	var NEILoaded = false
	var stupidMode = false
	var TiCLoaded = false
	var TravellersGearLoaded = false
	var TwilightForestLoaded = false
	
	// PORT: КТ-1 — TimeHandler; ветки RagnarokHandler — КТ-8
//	val jingleTheBells: Boolean
//	
//	// do not reassign this unless you know what you are doing
//	var winter: Boolean
//		get() {
//			return when {
//				RagnarokHandler.winter -> true
//				RagnarokHandler.summer -> false
//				else                   -> field
//			}
//		}
	
	init {
		// PORT: КТ-1 — AlfheimTab, TimeHandler
//		AlfheimTab
//		
//		jingleTheBells = (TimeHandler.month == 12 && TimeHandler.day >= 16 || TimeHandler.month == 1 && TimeHandler.day <= 8)
//		winter = TimeHandler.month in arrayOf(1, 2, 12, 13)
		
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
		
		// PORT: КТ-1 — AlfheimConfigHandler, InfoLoader
//		if (AlfheimConfigHandler.notifications) InfoLoader.start()
		
		// PORT: КТ-1 — сеть
//		NetworkService
		
		// PORT: КТ-1 — прокси
//		proxy.preInit()
		// PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//		if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.preInit()
	}
	
	// PORT: было @EventHandler, FMLInitializationEvent
	fun init(e: FMLCommonSetupEvent) {
//		abProxy.init(e)
		
		// PORT: КТ-1 — прокси
//		proxy.init()
//		proxy.initializeAndRegisterHandlers()
	}
	
	// PORT: было @EventHandler, FMLPostInitializationEvent
	fun postInit(e: InterModProcessEvent) {
//		abProxy.postInit(e)
		
		// PORT: КТ-1 — прокси. Клавиши и рендер в 1.20.1 регистрируются в своих событиях, это решается при переносе прокси
//		proxy.registerKeyBinds()
//		proxy.registerRenderThings()
//		proxy.postInit()
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