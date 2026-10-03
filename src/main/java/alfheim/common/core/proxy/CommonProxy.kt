package alfheim.common.core.proxy

// PORT: импорты 1.7.10 заменены на 1.20.1; импорты кода, который ещё не перенесён, закомментированы до его КТ
import alexsocol.asjlib.*
import alfheim.api.*
//import alfheim.api.item.*
//import alfheim.common.achievement.AlfheimAchievements
//import alfheim.common.block.AlfheimBlocks
//import alfheim.common.core.asm.hook.extender.RelicNBTSyncHandler
import alfheim.common.core.handler.*
//import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.helper.*
import alfheim.common.core.registry.AlfheimRegistry
//import alfheim.common.crafting.recipe.AlfheimRecipes
//import alfheim.common.entity.SpriteKillHandler
//import alfheim.common.floatingisland.FloatingIslandGenerator
//import alfheim.common.integration.etfuturum.EtFuturumAlfheimConfig
//import alfheim.common.integration.multipart.MultipartAlfheimConfig
//import alfheim.common.integration.thaumcraft.TCHandlerShadowFoxAspects
//import alfheim.common.item.AlfheimItems
//import alfheim.common.lexicon.*
//import alfheim.common.world.dim.alfheim.WorldProviderAlfheim
//import alfheim.common.world.dim.domains.WorldProviderDomains
//import alfheim.common.world.dim.helheim.WorldProviderHelheim
//import alfheim.common.world.dim.niflheim.WorldProviderNiflheim
//import alfheim.common.world.mobspawn.MobSpawnHandler
//import cpw.mods.fml.common.Loader
//import net.minecraft.item.ItemStack
import net.minecraft.world.level.Level as World
//import vazkii.botania.common.Botania
//import vazkii.botania.common.core.handler.ConfigHandler
//import vazkii.botania.common.item.ModItems

open class CommonProxy {
	
	open fun preInit() {
		// PORT: КТ-4 — материалы инструментов в AlfheimAPI
//		AlfheimAPI.RUNEAXE.setRepairItem(ItemStack(ModItems.manaResource, 1, 7)) // Elementium
		
		// PORT: КТ-9 — лексикон; блоки и предметы создаются в событии регистрации (alfheim.port.registry.AlfheimRegisters)
//		AlfheimLexiconData.preInit()
//		AlfheimBlocks
//		AlfheimItems
		AlfheimRegistry.preInit()
		// PORT: КТ-10 — достижения → advancements
//		AlfheimAchievements
		// PORT: раздатчики — в init (AlfheimCore), в очереди основного потока: предметы 1.20.1 создаются в событии
		// регистрации, позже preInit, а реестр поведения раздатчика общий для модов и не потокобезопасный
//		BifrostFlowerDispenserHandler
//		ThrownPotionDispenserHandler
//		ThrownItemDispenserHandler
//		WaterBowlDispenserHandler
		// PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//		if (Botania.thaumcraftLoaded) TCHandlerShadowFoxAspects.initAspects()
		// PORT: КТ-9 — мультиблоки лексикона
//		AlfheimMultiblocks
	}
	
	open fun registerRenderThings() = Unit
	
	open fun registerKeyBinds() = Unit
	
	fun init() {
		// PORT: Ore Dictionary → теги, рецепты → JSON: их строит генерация данных по regOreDict и AlfheimRecipes
		// (alfheim.port.data); в игре этот код не выполняется
//		AlfheimBlocks.regOreDict()
//		AlfheimItems.regOreDict()
//		
//		AlfheimRecipes
		AlfheimRegistry.init()
		
		// PORT: КТ-6 — миры в 1.20.1 задаются данными мода (dimension_type, dimension), номеров измерений нет (MAPPING.md, «Миры»)
//		ASJUtilities.registerDimension(AlfheimConfigHandler.dimensionIDAlfheim, WorldProviderAlfheim::class.java, true)
//		ASJUtilities.registerDimension(AlfheimConfigHandler.dimensionIDNiflheim, WorldProviderNiflheim::class.java, false)
//		ASJUtilities.registerDimension(AlfheimConfigHandler.dimensionIDDomains, WorldProviderDomains::class.java, false)
//		ASJUtilities.registerDimension(AlfheimConfigHandler.dimensionIDHelheim, WorldProviderHelheim::class.java, false)
		// PORT: КТ-2 — топливо
//		AlfheimBlocks.registerBurnables()
		// PORT: выпало — Forge Multipart и Et Futurum отсутствуют на 1.20.1 (SPEC, п. 7)
//		if (Loader.isModLoaded("ForgeMultipart")) MultipartAlfheimConfig.loadConfig()
//		if (Loader.isModLoaded("etfuturum")) EtFuturumAlfheimConfig.loadConfig()
	}
	
	open fun postInit() {
		// PORT: КТ-9 — лексикон
//		AlfheimLexiconData.init()
//		if (ConfigHandler.relicsEnabled) AlfheimLexiconData.initRelics()
		//AlfheimLexiconData.postInit()
		AlfheimRegistry.postInit()
	}
	
	open fun initializeAndRegisterHandlers() {
		// PORT: eventForge() и eventFML() в порту подписывают на одну шину Forge; повторную подписку того же объекта шина пропускает
		EventHandler.eventForge().eventFML()
		// PORT: обработчики подписываются в КТ своих механик: КТ-7 — расы, полёт, CardinalSystem; КТ-4 — HilarityHandler,
		// ElementalDamageHandler, ISpeedUpItem, IStepupItem, RelicNBTSyncHandler; КТ-8 — Рагнарёк, зимнее и летнее события;
		// КТ-3 — SoulRestructuringHandler; КТ-6 — MobSpawnHandler, SheerColdHandler, ChunkLoadingHandler, летающие острова;
		// КТ-5 — SpriteKillHandler
//		ESMHandler.eventForge().eventFML()
//		ElvenFlightHandler.eventForge().eventFML()
//		HilarityHandler
//		RagnarokHandler
//		SoulRestructuringHandler.eventForge()
//		MobSpawnHandler
//		ElementalDamageHandler.eventForge()
//		CardinalSystem.eventForge().eventFML()
//		EventHandlerWinter.eventFML()
//		EventHandlerSummer.eventForge()
//		SpriteKillHandler.eventForge()
//		SheerColdHandler.eventForge().eventFML()
//		ChunkLoadingHandler
//		FloatingIslandGenerator.eventFML().eventForge()
//		ISpeedUpItem.eventForge()
//		IStepupItem.eventForge()
//		RelicNBTSyncHandler.eventForge().eventFML()
		ContributorsPrivacyHelper
		AlfheimConfigHandler.registerChangeHandler(ModInfo.MODID)
	}
	
	open fun bloodFX(world: World, x: Double, y: Double, z: Double, lifetime: Int = 100, size: Float = 1f, gravity: Float = 1f) = Unit
	
	open fun featherFX(world: World, x: Double, y: Double, z: Double, color: Int, size: Float = 1f, lifetime: Float = 1f, distance: Float = 16f, must: Boolean = false, motionX: Double = 0.0, motionY: Double = 0.0, motionZ: Double = 0.0) = Unit
	
	open fun sparkleFX(world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, ageMultiplier: Int = 2, motionX: Double = 0.0, motionY: Double = 0.0, motionZ: Double = 0.0, fake: Boolean = false, noclip: Boolean = false) = Unit
	
	open fun voxelFX(world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float) = Unit
	
	open fun doParticle() = false
}