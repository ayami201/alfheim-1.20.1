package alfheim.client.core.proxy

// PORT: импорты 1.7.10 заменены на 1.20.1; импорты кода, который ещё не перенесён, закомментированы до его КТ
import alexsocol.asjlib.*
//import alexsocol.asjlib.render.*
import alfheim.AlfheimCore
//import alfheim.api.ModInfo
//import alfheim.api.event.AlfheimModeChangedEvent
import alfheim.api.item.DoubleBoundItemRender
//import alfheim.api.lib.*
import alfheim.client.core.handler.*
//import alfheim.client.core.handler.CardinalSystemClient.TimeStopSystemClient
//import alfheim.client.core.util.AlfheimBotaniaModifiersClient
//import alfheim.client.gui.*
import alfheim.client.lib.LibResourceLocationsActual
import alfheim.client.render.particle.EntityVoxelFX
//import alfheim.client.render.block.*
//import alfheim.client.render.entity.*
//import alfheim.client.render.item.*
//import alfheim.client.render.particle.*
//import alfheim.client.render.tile.*
//import alfheim.common.block.*
//import alfheim.common.block.tile.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.proxy.CommonProxy
//import alfheim.common.crafting.recipe.AlfheimRecipes
//import alfheim.common.entity.*
//import alfheim.common.entity.boss.*
//import alfheim.common.entity.boss.primal.*
//import alfheim.common.entity.item.EntityItemImmortal
//import alfheim.common.entity.spell.*
//import alfheim.common.floatingisland.EntityFloatingIsland
//import alfheim.common.integration.ThermalFoundationIntegration
//import alfheim.common.integration.travellersgear.TGHandlerBotaniaRenderer
//import alfheim.common.item.AlfheimItems
//import alfheim.common.lexicon.AlfheimLexiconData
//import cpw.mods.fml.client.registry.*
//import net.minecraft.client.renderer.OpenGlHelper
//import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
//import net.minecraft.client.settings.KeyBinding
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.level.Level as World
//import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.MinecraftForge
//import org.lwjgl.input.Keyboard
//import vazkii.botania.client.core.helper.ShaderHelper
import vazkii.botania.client.core.proxy.ClientProxy
//import vazkii.botania.client.fx.FXSparkle
//import vazkii.botania.client.render.item.RenderLens
//import vazkii.botania.client.render.tile.RenderTileFloatingFlower
//import vazkii.botania.common.block.tile.TileFloatingFlower
//import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.xplat.BotaniaConfig
import kotlin.jvm.java

object ClientProxy : CommonProxy() {
	
	override fun preInit() {
		super.preInit()
		LibResourceLocationsActual.init()
	}
	
	override fun registerRenderThings() {
		ClientProxy.jingleTheBells = AlfheimCore.jingleTheBells
		
		/* PORT: рендер, шейдеры и клавиши переносятся в КТ своих блоков, предметов и существ (по описи), клавиши — в КТ-7
		   (KeyBindingHandlerClient). В 1.20.1 это события RegisterKeyMappingsEvent, EntityRenderersEvent.RegisterRenderers,
		   RegisterShadersEvent и IClientItemExtensions вместо регистрации в postInit (MAPPING.md, «Рендер»)
		@Suppress("UnusedExpression") // STFU
		LibRenderIDs
		
		if (ShaderHelper.useShaders()) {
			ASJShaderHelper.crashOnError = ModInfo.DEV
			
			LibShaderIDs.idColor3d = ASJShaderHelper.createProgram("shaders/position.vert", "shaders/color3d.frag")
			LibShaderIDs.idCORE = ASJShaderHelper.createProgram("shaders/Vertex.vert", "shaders/CORE.frag")
			LibShaderIDs.idFresnel = ASJShaderHelper.createProgram("shaders/Vertex.vert", "shaders/Fresnel.frag")
			LibShaderIDs.idGravity = ASJShaderHelper.createProgram(null, "shaders/gravity.frag")
			LibShaderIDs.idNoise = ASJShaderHelper.createProgram("shaders/position.vert", "shaders/noise4d.frag")
			LibShaderIDs.idShadow = ASJShaderHelper.createProgram(null, "shaders/shadow.frag")
			LibShaderIDs.idSun = ASJShaderHelper.createProgram("shaders/position.vert", "shaders/sun.frag")
			LibShaderIDs.idWorley = ASJShaderHelper.createProgram("shaders/position.vert", "shaders/worley.frag")
			
			ASJShaderHelper.crashOnError = true
		}
		
		ClientRegistry.registerKeyBinding(keyAkashic)
		if (AlfheimConfigHandler.mountEnabled)
			ClientRegistry.registerKeyBinding(keyLolicorn)
		
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.akashicRecords, RenderItemAkashicRecords)
		MinecraftForgeClient.registerItemRenderer(AlfheimBlocks.anomaly.toItem(), RenderItemAnomaly)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.fenrirClaws, RenderItemFenrirClaws)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.invisibleFlameLens, RenderLens())
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.moonlightBow, RenderMoonBow)
		MinecraftForgeClient.registerItemRenderer(AlfheimBlocks.manaReflector.toItem(), RenderItemManaReflector)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.organs, RenderItemOrgans)
		MinecraftForgeClient.registerItemRenderer(AlfheimBlocks.rainbowFlowerFloating.toItem(), RenderFloatingFlowerRainbowItem)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.royalStaff, RenderItemRoyalStaff)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.surtrSword, RenderItemSurtrSword)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.thrymAxe, RenderItemThrymAxe)
		MinecraftForgeClient.registerItemRenderer(AlfheimBlocks.yggFlower.toItem(), RenderItemYggFlower)
		
		MinecraftForgeClient.registerItemRenderer(AlfheimFluffBlocks.chair.toItem(), RenderItemDoubleCamo<TileChair>(LibRenderIDs.idChair))
		MinecraftForgeClient.registerItemRenderer(AlfheimFluffBlocks.composite.toItem(), RenderItemDoubleCamo<TileComposite>(LibRenderIDs.idComposite))
		MinecraftForgeClient.registerItemRenderer(AlfheimFluffBlocks.doubleBlock.toItem(), RenderItemDoubleCamo<TileDoubleBlock>(LibRenderIDs.idDoubleBlock))
		MinecraftForgeClient.registerItemRenderer(AlfheimFluffBlocks.table.toItem(), RenderItemDoubleCamo<TileTable>(LibRenderIDs.idTable))
		
		RenderingRegistry.registerBlockHandler(RenderBlockAlfheimPylons)
		RenderingRegistry.registerBlockHandler(RenderBlockAnyavil)
		RenderingRegistry.registerBlockHandler(RenderBlockBarrel)
		RenderingRegistry.registerBlockHandler(RenderBlockChair)
		RenderingRegistry.registerBlockHandler(RenderBlockComposite)
		RenderingRegistry.registerBlockHandler(RenderBlockDomainLobby)
		RenderingRegistry.registerBlockHandler(RenderBlockDoubleBlock)
		RenderingRegistry.registerBlockHandler(RenderBlockFloodlight)
		RenderingRegistry.registerBlockHandler(RenderBlockGrapeRedPlanted)
		RenderingRegistry.registerBlockHandler(RenderBlockGrapeGreen)
		RenderingRegistry.registerBlockHandler(RenderBlockAnomalyHarvester)
		RenderingRegistry.registerBlockHandler(RenderBlockManaAccelerator)
		RenderingRegistry.registerBlockHandler(RenderBlockManaReflector)
		RenderingRegistry.registerBlockHandler(RenderBlockManaTuner)
		RenderingRegistry.registerBlockHandler(RenderBlockNidhoggTooth)
		RenderingRegistry.registerBlockHandler(RenderBlockNiflheimSet)
		RenderingRegistry.registerBlockHandler(RenderBlockOnyx)
		RenderingRegistry.registerBlockHandler(RenderBlockPowerStone)
		RenderingRegistry.registerBlockHandler(RenderSimpleDoubleBlock)
		RenderingRegistry.registerBlockHandler(RenderBlockShrinePanel)
		RenderingRegistry.registerBlockHandler(RenderBlockSpire)
		RenderingRegistry.registerBlockHandler(RenderBlockTable)
		RenderingRegistry.registerBlockHandler(RenderBlockWorldTree)
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileAlfheimPortal::class.java, RenderTileAlfheimPortal)
		ClientRegistry.bindTileEntitySpecialRenderer(TileAlfheimPylon::class.java, RenderTileAlfheimPylons)
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnimatedTorch::class.java, RenderTileAnimatedTorch)
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnomaly::class.java, RenderTileAnomaly)
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnomalyHarvester::class.java, RenderTileAnomalyHarvester)
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnyavil::class.java, RenderTileAnyavil)
		ClientRegistry.bindTileEntitySpecialRenderer(TileBarrel::class.java, RenderTileBarrel)
		ClientRegistry.bindTileEntitySpecialRenderer(TileDomainLobby::class.java, RenderTileDomainLobby)
		ClientRegistry.bindTileEntitySpecialRenderer(TileFloatingFlowerRainbow::class.java, TileEntityRendererDispatcher.instance.mapSpecialRenderers[TileFloatingFlower::class.java] as RenderTileFloatingFlower)
		ClientRegistry.bindTileEntitySpecialRenderer(TileFloodLight::class.java, RenderTileFloodLight)
		ClientRegistry.bindTileEntitySpecialRenderer(TileGaiaButton::class.java, RenderTileGaiaButton)
		ClientRegistry.bindTileEntitySpecialRenderer(TileHeadFlugel::class.java, RenderTileHeadFlugel)
		ClientRegistry.bindTileEntitySpecialRenderer(TileHeadMiku::class.java, RenderTileHeadMiku)
		ClientRegistry.bindTileEntitySpecialRenderer(TileIcyGeyser::class.java, RenderTileIcyGeyser)
		ClientRegistry.bindTileEntitySpecialRenderer(TileItemDisplay::class.java, RenderTileItemDisplay)
		ClientRegistry.bindTileEntitySpecialRenderer(TileItemFrame::class.java, RenderTileItemFrame)
		ClientRegistry.bindTileEntitySpecialRenderer(TileManaAccelerator::class.java, RenderTileManaAccelerator)
		ClientRegistry.bindTileEntitySpecialRenderer(TileManaReflector::class.java, RenderTileManaReflector)
		ClientRegistry.bindTileEntitySpecialRenderer(TileManaTuner::class.java, RenderTileManaTuner)
		ClientRegistry.bindTileEntitySpecialRenderer(TilePowerStone::class.java, RenderTilePowerStone)
		ClientRegistry.bindTileEntitySpecialRenderer(TileRaceSelector::class.java, RenderTileRaceSelector)
		ClientRegistry.bindTileEntitySpecialRenderer(TileSpire::class.java, RenderTileSpire)
		ClientRegistry.bindTileEntitySpecialRenderer(TileStar::class.java, RenderStar)
		ClientRegistry.bindTileEntitySpecialRenderer(TileTradePortal::class.java, RenderTileTradePortal)
		ClientRegistry.bindTileEntitySpecialRenderer(TileWorldTree::class.java, RenderTileWorldTree)
		ClientRegistry.bindTileEntitySpecialRenderer(TileYggFlower::class.java, RenderTileYggFlower)
		
		RenderingRegistry.registerEntityRenderingHandler(EntityAlfheimPixie::class.java, RenderEntityAlfheimPixie)
		RenderingRegistry.registerEntityRenderingHandler(EntityBlackBolt::class.java, RenderEntityBlackBolt)
		RenderingRegistry.registerEntityRenderingHandler(EntityButterfly::class.java, RenderEntityButterfly)
		RenderingRegistry.registerEntityRenderingHandler(EntityDedMoroz::class.java, RenderEntityDedMoroz)
		RenderingRegistry.registerEntityRenderingHandler(EntityElementalSlime::class.java, RenderEntityElementalSlime)
		RenderingRegistry.registerEntityRenderingHandler(EntityElf::class.java, RenderEntityElf)
		RenderingRegistry.registerEntityRenderingHandler(EntityFallingHang::class.java, RenderEntityFallingHang)
		RenderingRegistry.registerEntityRenderingHandler(EntityFenrir::class.java, RenderEntityFenrir)
		RenderingRegistry.registerEntityRenderingHandler(EntityFenrirDome::class.java, RenderEntityFenrirDome)
		RenderingRegistry.registerEntityRenderingHandler(EntityFenrirSlash::class.java, RenderEntityFenrirSlash)
		RenderingRegistry.registerEntityRenderingHandler(EntityFloatingIsland::class.java, RenderEntityFloatingIsland)
		RenderingRegistry.registerEntityRenderingHandler(EntityFlowerBud::class.java, RenderEntityFlowerBud)
		RenderingRegistry.registerEntityRenderingHandler(EntityFlugel::class.java, RenderEntityFlugel)
		RenderingRegistry.registerEntityRenderingHandler(EntityFrozenViking::class.java, RenderEntityFrozenViking)
		RenderingRegistry.registerEntityRenderingHandler(EntityGleipnir::class.java, RenderEntityGleipnir)
		RenderingRegistry.registerEntityRenderingHandler(EntityPrimalMark::class.java, RenderEntityPrimalMark)
		RenderingRegistry.registerEntityRenderingHandler(EntityIcicle::class.java, RenderEntityIcicle)
		RenderingRegistry.registerEntityRenderingHandler(EntityItemImmortal::class.java, RenderEntityItemImmortal)
		RenderingRegistry.registerEntityRenderingHandler(EntityLightningMark::class.java, RenderEntityLightningMark)
		RenderingRegistry.registerEntityRenderingHandler(EntityJellyfish::class.java, RenderEntityJellyfish)
		RenderingRegistry.registerEntityRenderingHandler(EntityMjolnir::class.java, RenderEntityMjolnir)
		RenderingRegistry.registerEntityRenderingHandler(EntityMuspelheimSun::class.java, RenderEntityMuspelheimSun)
		RenderingRegistry.registerEntityRenderingHandler(EntityMuspelheimSunSlash::class.java, RenderEntityMuspelheimSunSlash)
		RenderingRegistry.registerEntityRenderingHandler(EntityMuspelson::class.java, RenderEntityMuspelson)
		RenderingRegistry.registerEntityRenderingHandler(EntityResonance::class.java, RenderEntityResonance)
		RenderingRegistry.registerEntityRenderingHandler(EntityRollingMelon::class.java, RenderEntityRollingMelon)
		RenderingRegistry.registerEntityRenderingHandler(EntityRook::class.java, RenderEntityRook)
		RenderingRegistry.registerEntityRenderingHandler(EntitySniceBall::class.java, RenderEntitySniceBall)
		RenderingRegistry.registerEntityRenderingHandler(EntitySnowSprite::class.java, RenderEntitySnowSprite)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellHarp::class.java, RenderEntityHarp)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellDriftingMine::class.java, RenderEntityDriftingMine)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellGravityTrap::class.java, RenderEntityGravityTrap)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellFenrirStorm::class.java, RenderEntityFenrirStorm)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellMortar::class.java, RenderEntityMortar)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellWindBlade::class.java, RenderEntityWindBlade)
		RenderingRegistry.registerEntityRenderingHandler(EntitySubspace::class.java, RenderEntitySubspace)
		RenderingRegistry.registerEntityRenderingHandler(EntitySubspaceSpear::class.java, RenderEntitySubspaceSpear)
		RenderingRegistry.registerEntityRenderingHandler(EntitySurtr::class.java, RenderEntitySurtr)
		// PORT: перенесены — alfheim.port.client.AlfheimEntityRenderers
		RenderingRegistry.registerEntityRenderingHandler(EntityThrownPotion::class.java, RenderEntityThrownPotion)
		RenderingRegistry.registerEntityRenderingHandler(EntityThrowableItem::class.java, RenderEntityThrownItem)
		RenderingRegistry.registerEntityRenderingHandler(EntityThrym::class.java, RenderEntityThrym)
		RenderingRegistry.registerEntityRenderingHandler(EntityVenusHumanTrap::class.java, RenderEntityVenusHumanTrap)
		RenderingRegistry.registerEntityRenderingHandler(EntityElvenChakram::class.java, RenderEntityElvenChakram)
		RenderingRegistry.registerEntityRenderingHandler(EntityVoidCreeper::class.java, RenderEntityManaCreeper)
		RenderingRegistry.registerEntityRenderingHandler(EntityWarBanner::class.java, RenderEntityWarBanner)
		// PORT: перенесён — alfheim.port.client.AlfheimEntityRenderers
		RenderingRegistry.registerEntityRenderingHandler(FakeLightning::class.java, RenderFakeLightning)
		
		RenderingRegistry.registerBlockHandler(RenderBlockColoredDoubleGrass)
		RenderingRegistry.registerBlockHandler(MultipassRenderer)
		RenderingRegistry.registerBlockHandler(RenderBlockHopper)
		
		if (AlfheimConfigHandler.mountEnabled)
			RenderingRegistry.registerEntityRenderingHandler(EntityLolicorn::class.java, RenderEntityLolicorn)
		
		if (!AlfheimConfigHandler.minimalGraphics) {
			MinecraftForgeClient.registerItemRenderer(AlfheimItems.mjolnir, RenderItemMjolnir)
			MinecraftForgeClient.registerItemRenderer(AlfheimItems.snowSword, RenderItemSnowSword)
			// PORT: перенесён — alfheim.port.client.AlfheimEntityRenderers
			ClientRegistry.bindTileEntitySpecialRenderer(TileTreeBerry::class.java, RenderTileTreeBerry)
		}
		*/
	}
	
	override fun registerKeyBinds() {
		// PORT: КТ-7 — клавиши режимов ESM и MMO
//		if (AlfheimConfigHandler.enableElvenStory) addESMKeyBinds()
//		if (AlfheimConfigHandler.enableMMO) addMMOKeyBinds()
	}
	
	override fun initializeAndRegisterHandlers() {
		super.initializeAndRegisterHandlers()
		EventHandlerClient
		// PORT: КТ-4 — ItemsRemainingRenderHandler; КТ-3 — HUDCorporeaRat
//		ItemsRemainingRenderHandler
//		
//		HUDCorporeaRat.eventForge()
//		
		// PORT: ConfigHandler.boundBlockWireframe Botania 1.7.10 — та же настройка клиента Botania 1.20.1
		if (BotaniaConfig.client().boundBlockWireframe()) DoubleBoundItemRender
//		if (ConfigHandler.boundBlockWireframe) DoubleBoundItemRender
		// PORT: выпало — Travellers Gear и Thermal Foundation отсутствуют на 1.20.1 (SPEC, п. 7)
//		if (AlfheimCore.TravellersGearLoaded) TGHandlerBotaniaRenderer
//		if (ThermalFoundationIntegration.loaded) ThermalFoundationIntegration.eventForge()
		// PORT: КТ-7 — интерфейсы ESM и MMO; КТ-6 — GUIScreenOverlay, GUISheerCold; КТ-8 — GUIBanner;
		// КТ-7 — пост-шейдеры (RenderPostShaders из ASJCore)
//		if (AlfheimConfigHandler.enableElvenStory) enableESMGUIs()
//		if (AlfheimConfigHandler.enableMMO) enableMMOGUIs()
//		
//		GUIScreenOverlay.eventForge()
//		GUISheerCold.eventForge()
//		GUIBanner.eventForge().eventFML()
//		
//		RenderPostShaders.allowShaders = !AlfheimConfigHandler.minimalGraphics && OpenGlHelper.shadersSupported
	}
	
	override fun postInit() {
		super.postInit()
		// PORT: КТ-3
//		AlfheimBotaniaModifiersClient.postInit()
	}
	
	override fun bloodFX(world: World, x: Double, y: Double, z: Double, lifetime: Int, size: Float, gravity: Float) {
		// PORT: КТ-2 — частица EntityBloodFx
//		if (mc.renderViewEntity == null || mc.effectRenderer == null || !doParticle()) return
//		mc.effectRenderer.addEffect(EntityBloodFx(world, x, y, z, size, lifetime, gravity))
	}
	
	override fun featherFX(world: World, x: Double, y: Double, z: Double, color: Int, size: Float, lifetime: Float, distance: Float, must: Boolean, motionX: Double, motionY: Double, motionZ: Double) {
		// PORT: КТ-7 — частица EntityFeatherFx (крылья)
//		if (mc.renderViewEntity == null || mc.effectRenderer == null) return
//		val particle = EntityFeatherFx(world, x, y, z, color, size, lifetime)
//		particle.setMotion(motionX, motionY, motionZ)
//		
//		if (!must) {
//			if (!doParticle()) return
//			val distanceX: Double = mc.renderViewEntity.posX - particle.posX
//			val distanceY: Double = mc.renderViewEntity.posY - particle.posY
//			val distanceZ: Double = mc.renderViewEntity.posZ - particle.posZ
//			if (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ > distance * distance) {
//				return
//			}
//		}
//		
//		mc.effectRenderer.addEffect(particle)
	}
	
	override fun sparkleFX(world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, ageMultiplier: Int, motionX: Double, motionY: Double, motionZ: Double, fake: Boolean, noclip: Boolean) {
		// PORT: КТ-8 — первый, кто вызывает (EntityPrimalBoss); FXSparkle Botania 1.7.10 → SparkleParticleData Botania 1.20.1
//		if (!doParticle() && !fake) return
//		val sparkle = FXSparkle(world, x, y, z, size, r, g, b, ageMultiplier)
//		sparkle.setMotion(motionX, motionY, motionZ)
//		sparkle.fake = fake
//		sparkle.noClip = noclip
////		if (ClientProxy.noclipEnabled) sparkle.noClip = true
////		if (ClientProxy.corruptSparkle) sparkle.corrupt = true
//		mc.effectRenderer.addEffect(sparkle)
	}
	
	// PORT: мир частицы 1.20.1 — мир клиента; effectRenderer → particleEngine
	override fun voxelFX(world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float) {
		if (!doParticle()) return
		val voxel = EntityVoxelFX(world as ClientLevel, x, y, z, r, g, b)
		
		mc.particleEngine.add(voxel)
//		mc.effectRenderer.addEffect(voxel)
	}
	
	// PORT: опции Botania useVanillaParticleLimiter в 1.20.1 нет: частицы Botania там всегда ограничены настройкой игры, как при
	// её значении по умолчанию (true). particleSetting 1.7.10 — номер ParticleStatus: 0 — все, 1 — меньше, 2 — минимум
	override fun doParticle() = Math.random() < 1f - 0.4f * mc.options.particles().get().id
//	override fun doParticle() = if (!ConfigHandler.useVanillaParticleLimiter) true else Math.random() < 1f - 0.4f * mc.gameSettings.particleSetting
	
	/* PORT: КТ-7 — переключение режимов ESM и MMO, их интерфейсы и клавиши (KeyBindingHandlerClient)
	fun toggelModes(b: Boolean, esm: Boolean, mmo: Boolean, esmOld: Boolean, mmoOld: Boolean) {
		MinecraftForge.EVENT_BUS.post(AlfheimModeChangedEvent(esm, mmo, esmOld, mmoOld))
		
		if (b)
			toggleESM(esm, mmo, esmOld, mmoOld)
		else
			toggleMMO(esm, mmo, esmOld, mmoOld)
	}
	
	fun enableESM() {
//		if (AlfheimConfigHandler.enableElvenStory) return
		AlfheimConfigHandler.enableElvenStory = true
		AlfheimLexiconData.reEnableESM()
//		if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.addESMRecipes()
		enableESMGUIs()
		addESMKeyBinds()
//		ESMHandler.checkAddAttrs()
	}
	
	fun disableESM() {
//		if (!AlfheimConfigHandler.enableElvenStory) return
		AlfheimConfigHandler.enableElvenStory = false
		AlfheimLexiconData.disableESM()
//		if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.removeESMRecipes()
		disableESMGUIs()
		removeESMKeyBinds()
		disableMMO()
	}
	
	fun enableMMO() {
//		if (AlfheimConfigHandler.enableMMO) return
		AlfheimConfigHandler.enableMMO = true
		AlfheimLexiconData.reEnableMMO()
		AlfheimRecipes.addMMORecipes()
		enableMMOGUIs()
		addMMOKeyBinds()
		enableESM()
	}
	
	fun disableMMO() {
//		if (!AlfheimConfigHandler.enableMMO) return
		AlfheimConfigHandler.enableMMO = false
		AlfheimLexiconData.disableMMO()
		AlfheimRecipes.removeMMORecipes()
		disableMMOGUIs()
		removeMMOKeyBinds()
		TimeStopSystemClient.clear()
	}
	
	private fun toggleESM(esm: Boolean, mmo: Boolean, esmOld: Boolean, mmoOld: Boolean) {
		if (esmOld == esm) return
		AlfheimConfigHandler.enableElvenStory = esm
		
		if (esm) {
			AlfheimLexiconData.reEnableESM()
			addESMKeyBinds()
		} else {
			AlfheimLexiconData.disableESM()
			removeESMKeyBinds()
			if (mmoOld != mmo) toggleMMO(false, mmo, true, mmoOld)
		}
	}
	
	private fun toggleMMO(esm: Boolean, mmo: Boolean, esmOld: Boolean, mmoOld: Boolean) {
		if (mmoOld == mmo) return
		AlfheimConfigHandler.enableMMO = mmo
		
		if (mmo) {
			AlfheimLexiconData.reEnableMMO()
			enableMMOGUIs()
			addMMOKeyBinds()
			if (esm) toggleESM(true, true, esmOld, false)
		} else {
			AlfheimLexiconData.disableMMO()
			disableMMOGUIs()
			removeMMOKeyBinds()
			TimeStopSystemClient.clear()
		}
	}
	
	private fun enableESMGUIs() {
		ASJUtilities.log("Registering ESM GUIs")
		MinecraftForge.EVENT_BUS.register(GUIRace)
	}
	
	private fun disableESMGUIs() {
		ASJUtilities.log("Unregistering ESM GUIs")
		MinecraftForge.EVENT_BUS.unregister(GUIRace)
	}
	
	private fun enableMMOGUIs() {
		ASJUtilities.log("Registering MMO GUIs")
		MinecraftForge.EVENT_BUS.register(GUIParty)
		MinecraftForge.EVENT_BUS.register(GUISpells)
		
		MinecraftForge.EVENT_BUS.unregister(GUIRace)
	}
	
	private fun disableMMOGUIs() {
		ASJUtilities.log("Unregistering MMO GUIs")
		MinecraftForge.EVENT_BUS.unregister(GUIParty)
		MinecraftForge.EVENT_BUS.unregister(GUISpells)
		
		MinecraftForge.EVENT_BUS.register(GUIRace)
	}
	
	val keyAkashic = KeyBinding("key.akashic.desc", Keyboard.KEY_K, "key.categories.alfheim")
	val keyLolicorn = KeyBinding("key.lolicorn.desc", Keyboard.KEY_L, "key.categories.alfheim")
	val keyESMAbility = KeyBinding("key.esmability.desc", Keyboard.KEY_M, "key.categories.alfheim")
	val keyFlight = KeyBinding("key.flight.desc", Keyboard.KEY_F, "key.categories.alfheim")
	val keyCast = KeyBinding("key.cast.desc", Keyboard.KEY_C, "key.categories.alfheim")
	val keyUnCast = KeyBinding("key.uncast.desc", Keyboard.KEY_X, "key.categories.alfheim")
	val keySelMob = KeyBinding("key.selmob.desc", Keyboard.KEY_R, "key.categories.alfheim")
	val keySelTeam = KeyBinding("key.selteam.desc", if (mc.session.username == "AlexSocol") Keyboard.KEY_T else Keyboard.KEY_Y, "key.categories.alfheim")
	
	init {
		KeyBinding.keybindArray.removeAll(listOf(keyESMAbility, keyFlight, keyCast, keyUnCast, keySelMob, keySelTeam))
		KeyBinding.resetKeyBindingArrayAndHash()
	}
	
	private fun addESMKeyBinds() {
		registerKeyBinding(keyFlight)
		registerKeyBinding(keyESMAbility)
	}
	
	private fun removeESMKeyBinds() {
		unregisterKeyBinding(keyFlight)
		unregisterKeyBinding(keyESMAbility)
		
		KeyBinding.resetKeyBindingArrayAndHash()
	}
	
	private fun addMMOKeyBinds() {
		registerKeyBinding(keyCast)
		registerKeyBinding(keyUnCast)
		registerKeyBinding(keySelMob)
		registerKeyBinding(keySelTeam)
	}
	
	private fun removeMMOKeyBinds() {
		unregisterKeyBinding(keyCast)
		unregisterKeyBinding(keyUnCast)
		unregisterKeyBinding(keySelMob)
		unregisterKeyBinding(keySelTeam)
		
		KeyBinding.resetKeyBindingArrayAndHash()
	}
	
	private fun registerKeyBinding(key: KeyBinding) {
		if (key inl KeyBinding.keybindArray) return
		
		KeyBinding.keybindArray.add(key)
		KeyBinding.hash.addKey(key.keyCode, key)
		ClientRegistry.registerKeyBinding(key)
	}
	
	private fun unregisterKeyBinding(key: KeyBinding) {
		KeyBinding.keybindArray.remove(key)
		mc.gameSettings.keyBindings = mc.gameSettings.keyBindings.filter { it !== key }.toTypedArray()
	}
	*/
}