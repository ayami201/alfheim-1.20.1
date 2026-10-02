package alfheim.common.core.registry

// PORT: импорты 1.7.10 заменены на 1.20.1; импорты кода, который ещё не перенесён, закомментированы до его КТ
//import alexsocol.asjlib.ASJUtilities.registerEntity
//import alexsocol.asjlib.eventForge
import alfheim.api.*
//import alfheim.api.AlfheimAPI.addPink
//import alfheim.api.AlfheimAPI.registerAnomaly
//import alfheim.api.block.tile.SubTileAnomalyBase.EnumAnomalyRarity.*
//import alfheim.api.entity.IAlfheimMob
//import alfheim.common.block.*
//import alfheim.common.block.tile.*
//import alfheim.common.block.tile.TileChair.Companion.EntitySit
//import alfheim.common.block.tile.corporea.*
//import alfheim.common.block.tile.sub.anomaly.*
import alfheim.common.core.handler.*
//import alfheim.common.entity.*
//import alfheim.common.entity.boss.*
//import alfheim.common.entity.boss.primal.*
//import alfheim.common.entity.item.*
//import alfheim.common.entity.spell.*
//import alfheim.common.floatingisland.EntityFloatingIsland
//import alfheim.common.item.*
//import alfheim.common.item.material.*
//import alfheim.common.item.material.ElvenResourcesMetas.ElementalSlimeBall
//import alfheim.common.potion.*
//import alfheim.common.potion.berries.*
//import alfheim.common.spell.darkness.*
//import alfheim.common.spell.earth.*
//import alfheim.common.spell.fire.*
//import alfheim.common.spell.illusion.*
//import alfheim.common.spell.nature.*
//import alfheim.common.spell.sound.*
//import alfheim.common.spell.tech.*
//import alfheim.common.spell.water.*
//import alfheim.common.spell.wind.*
//import alfheim.common.world.dim.alfheim.biome.*
//import alfheim.common.world.dim.alfheim.biome.BiomeAlfheim.Companion.addEntry
//import cpw.mods.fml.common.registry.EntityRegistry
//import cpw.mods.fml.common.registry.GameRegistry.registerTileEntity
//import net.minecraft.entity.*
//import net.minecraft.entity.monster.EntitySlime
//import net.minecraft.init.*
//import net.minecraft.item.ItemStack
//import net.minecraft.tileentity.TileEntity
//import net.minecraft.world.biome.BiomeGenBase
//import vazkii.botania.api.BotaniaAPI
//import vazkii.botania.common.Botania
//import vazkii.botania.common.block.*
//import vazkii.botania.common.core.handler.*
//import vazkii.botania.common.item.ModItems
//import vazkii.botania.common.item.block.ItemBlockSpecialFlower
//import vazkii.botania.common.lib.LibBlockNames
import kotlin.jvm.java

object AlfheimRegistry {
	
	fun preInit() {
		// PORT: КТ-2 — зелья; существа и блок-сущности — в КТ каждого (по описи)
//		registerPotions()
//		registerEntities()
//		registerTileEntities()
	}
	
	fun init() {
		// PORT: КТ-5 — сброс предметов существами (shedding); КТ-7 — заклинания; КТ-4 — «розовость» (её читает getPinkness)
//		registerSheddings()
//		registerSpells()
//		loadAllPinkStuff()
	}
	
	fun postInit() {
		// PORT: КТ-3 — Loonium; в Botania 1.20.1 его чёрный список — тег botania:loonium_blacklist
//		if (AlfheimConfigHandler.looniumOverseed)
//			BotaniaAPI.looniumBlacklist.remove(ModItems.overgrowthSeed)
		
		// PORT: КТ-5 — спавн существ
//		registerSpawns()
		registerFlowerOres()
		
		// PORT: КТ-3
//		AnomalyHarvesterBehaviors
	}
	
	/* PORT: КТ-5 — спавн существ: в 1.20.1 через Forge biome modifiers (MAPPING.md, «Миры»)
	private fun registerSpawns() {
		addAllSpawn(EntityElementalSlime::class.java, AlfheimConfigHandler.elementalSlimeRates, AlfheimConfigHandler.elementalSlimeBiomeBlackList)
		addAllSpawn(EntityVoidCreeper::class.java, AlfheimConfigHandler.voidCreeperRates, AlfheimConfigHandler.voidCreeperBiomeBlackList)
		
		if (HELLISH_VACATION) {
			arrayOf(BiomeBeach, BiomeSandbank, BiomeGenBase.jungle, BiomeGenBase.jungleEdge, BiomeGenBase.jungleHills, BiomeGenBase.beach).forEach {
				it.addEntry(EntityRollingMelon::class.java, AlfheimConfigHandler.pixieSpawn.map { v -> v * 4 }.toIntArray())
			}
			
			BiomeGenBase.hell.getSpawnableList(EnumCreatureType.monster).add(BiomeGenBase.SpawnListEntry(EntityMuspelson::class.java, 20, 4, 4))
		}
	}
	
	private fun addAllSpawn(clazz: Class<out EntityLiving>, data: IntArray, blacklist: IntArray, type: EnumCreatureType = EnumCreatureType.monster) {
		val (w, n, x) = data
		EntityRegistry.addSpawn(clazz, w, n, x, type, *BiomeGenBase.getBiomeGenArray().filter { it != null && it.biomeID !in blacklist }.toTypedArray())
	}
	*/
	
	/* PORT: КТ-2 — зелья: MobEffect через DeferredRegister, номера зелий из конфига удалены (MAPPING.md)
	private fun registerPotions() {
		PotionBeastWithin
		PotionBeer
		PotionBerserk
		PotionBleeding
		PotionButterShield
		PotionChampagne
		PotionDeathMark
		PotionAlfheim(AlfheimConfigHandler.potionIDDecay, "decay", true, 0x553355)
		PotionEdgeLife.eventForge()
		PotionEternity.eventForge()
		PotionGoldRush
		PotionHystrix.eventForge()
		PotionIceLens
		PotionLeftFlame
		PotionLightningShield.eventForge()
		PotionManaVoid
		PotionAlfheim(AlfheimConfigHandler.potionIDNineLives, "nineLives", false, 0xDD2222)
		PotionNinja
		PotionNoclip
		if (AlfheimConfigHandler.potionIDOvercold != -1) PotionAlfheim(AlfheimConfigHandler.potionIDOvercold, "overcold", false, 0xBFF4FF)
		if (AlfheimConfigHandler.potionIDOverheat != -1) PotionAlfheim(AlfheimConfigHandler.potionIDOverheat, "overheat", false, 0xFF4D00)
		PotionAlfheim(AlfheimConfigHandler.potionIDOvermage, "overmage", false, 0x88FFFF)
		PotionAlfheim(AlfheimConfigHandler.potionIDPossession, "possession", true, 0xCC0000)
		PotionQuadDamage.eventForge()
		PotionSacrifice
		PotionShowMana
		PotionSoulburn
		PotionAlfheim(AlfheimConfigHandler.potionIDStoneSkin, "stoneSkin", false, 0x593C1F)
		PotionTank
		PotionThrow
		PotionTimeAnchor
		PotionTimeConquest.eventForge()
		PotionVoodooDoll.eventForge()
		PotionVoodooTarget
		PotionPriorityTarget.eventForge()
		PotionWellOLife
		PotionWhiteWine.eventForge()
		PotionAlfheim(AlfheimConfigHandler.potionIDWisdom, "wisdom", false, 0xFFC880)
		PotionWTFBerry0 // barrier
		PotionWTFBerry2 // redstone
		PotionWTFBerry3.eventForge() // lightning
		PotionWTFBerry4.eventForge() // nether
		PotionWTFBerry5.eventForge() // sealing
	}
	*/
	
	/* PORT: существа — в КТ каждого (по описи): EntityType через DeferredRegister, яйца — ForgeSpawnEggItem (MAPPING.md)
	var nextEntityID = 0
		get() = field++
	
	private fun registerEntities() {
		registerEntity(EntityButterfly::class.java, "Butterfly", nextEntityID, 0, -1)
		registerEntity(EntityDedMoroz::class.java, "DedMoroz", nextEntityID)
		registerEntity(EntityElementalSlime::class.java, "ElementalSlime", nextEntityID, -1, 0x7EBF6E)
		registerEntity(EntityElf::class.java, "Elf", nextEntityID, 0x1A660A, 0x4D3422)
		registerEntity(EntityFireSpirit::class.java, "FireSpirit", nextEntityID)
		registerEntity(EntityFenrir::class.java, "Fenrir", nextEntityID)
		registerEntity(EntityFlowerBud::class.java, "FlowerBud", nextEntityID)
		registerEntity(EntityFlugel::class.java, "Flugel", nextEntityID)
		registerEntity(EntityFrozenViking::class.java, "FrozenViking", nextEntityID, 0x26DBFF, 0x2D86B3)
		registerEntity(EntityJellyfish::class.java, "Jellyfish", nextEntityID, 0xFFFFFF, -1)
		registerEntity(EntityMuspelson::class.java, "Muspelson", nextEntityID, 0x3E1900, 0xD05D14)
		registerEntity(EntityAlfheimPixie::class.java, "Pixie", nextEntityID, 0xFF76D6, 0xFFE3FF)
		registerEntity(EntityRollingMelon::class.java, "RollingMelon", nextEntityID, 0xBECB25, 0x5B751A)
		registerEntity(EntityRook::class.java, "Rook", nextEntityID)
		registerEntity(EntitySnowSprite::class.java, "SnowSprite", nextEntityID, 0xEEFFFF, 0xE3F3F3)
		registerEntity(EntitySurtr::class.java, "Surtr", nextEntityID)
		registerEntity(EntityThrym::class.java, "Thrym", nextEntityID)
		registerEntity(EntityVenusHumanTrap::class.java, "VenusHumanTrap", nextEntityID)
		registerEntity(EntityVoidCreeper::class.java, "VoidCreeper", nextEntityID, 0xcc11d3, 0xfb9bff)
		
		if (AlfheimConfigHandler.mountEnabled)
			registerEntity(EntityLolicorn::class.java, "Lolicorn", nextEntityID)
		
		// TODO back
//		registerEntity(EntityAlphirinePortal::class.java, "AlphirinePortal", nextEntityID)
//		registerEntity(EntityAdvancedSpark::class.java, "AdvancedSpark", nextEntityID)
		registerEntity(EntityBlackBolt::class.java, "BlackBolt", nextEntityID)
		registerEntity(EntityCharge::class.java, "Charge", nextEntityID)
		registerEntity(EntityEarthquake::class.java, "Earthquake", nextEntityID)
		registerEntity(EntityEarthquakeFracture::class.java, "EarthquakeFracture", nextEntityID)
		registerEntity(EntityElvenChakram::class.java, "ElvenChakram", nextEntityID)
		registerEntity(EntityFallingHang::class.java, "FallingHang", nextEntityID)
		registerEntity(EntityFenrirDome::class.java, "FenrirDome", nextEntityID)
		registerEntity(EntityFenrirSlash::class.java, "FenrirSlash", nextEntityID)
		registerEntity(EntityFireAura::class.java, "FireAura", nextEntityID)
		registerEntity(EntityFireTornado::class.java, "FireTornado", nextEntityID)
		registerEntity(EntityFloatingIsland::class.java, "FloatingIsland", nextEntityID)
		registerEntity(EntityFracturedSpaceCollector::class.java, "FracturedSpaceCollector", nextEntityID)
		registerEntity(EntityIcicle::class.java, "Icicle", nextEntityID)
		registerEntity(EntityItemImmortal::class.java, "ImmortalItem", nextEntityID)
		registerEntity(EntityItemImmortalRelic::class.java, "ImmortalRelicItem", nextEntityID)
		registerEntity(EntityLightningMark::class.java, "LightningMark", nextEntityID)
//		registerEntity(EntityManaVine::class.java, "ManaVineBall", nextEntityID)
		registerEntity(EntityMeteor::class.java, "Meteor", nextEntityID)
		registerEntity(EntityMuspelheimSun::class.java, "MuspelheimSun", nextEntityID)
		registerEntity(EntityMuspelheimSunSlash::class.java, "MuspelheimSunSlash", nextEntityID)
//		registerEntity(EntityNebulaBlaze::class.java, "NebulaBlaze", nextEntityID)
		registerEntity(EntityPrimalBossChunkAttack::class.java, "ChunkAttack", nextEntityID)
		registerEntity(EntityPrimalMark::class.java, "PrimalMark", nextEntityID)
		registerEntity(EntityResonance::class.java, "Resonance", nextEntityID)
		registerEntity(EntityRift::class.java, "Rift", nextEntityID)
//		registerEntity(EntitySeedshot::class.java, "EntitySeedshot", nextEntityID)
		registerEntity(EntitySit::class.java, "Sit", nextEntityID)
		registerEntity(EntitySniceBall::class.java, "SniceBall", nextEntityID)
//		registerEntity(EntitySpaceSwordBurst::class.java, "SpaceSwordBurst", nextEntityID)
		registerEntity(EntityThrowableItem::class.java, "ThrownItem", nextEntityID)
		registerEntity(EntityThrownPotion::class.java, "ThrownPotion", nextEntityID)
		registerEntity(EntityTornado::class.java, "Tornado", nextEntityID)
		registerEntity(EntityWarBanner::class.java, "WarBanner", nextEntityID)
		
		registerEntity(EntityGleipnir::class.java, "Gleipnir", nextEntityID)
		registerEntity(EntityMjolnir::class.java, "Mjolnir", nextEntityID)
		
		registerEntity(EntityMagicArrow::class.java, "MagicArrow", nextEntityID)
		registerEntity(EntitySubspace::class.java, "Subspace", nextEntityID)
		registerEntity(EntitySubspaceSpear::class.java, "SubspaceSpear", nextEntityID)
		registerEntity(FakeLightning::class.java, "FakeLightning", nextEntityID)
		
		registerEntity(EntitySpellAcidMyst::class.java, "SpellAcidMyst", nextEntityID)
		registerEntity(EntitySpellAquaStream::class.java, "SpellAquaStream", nextEntityID)
		registerEntity(EntitySpellDarkness::class.java, "SpellDarkness", nextEntityID)
		registerEntity(EntitySpellDriftingMine::class.java, "SpellDriftingMine", nextEntityID)
		registerEntity(EntitySpellFenrirStorm::class.java, "SpellFenrirStorm", nextEntityID)
		registerEntity(EntitySpellFireball::class.java, "SpellFireball", nextEntityID)
		registerEntity(EntitySpellFirestar::class.java, "SpellFirestar", nextEntityID)
		registerEntity(EntitySpellFirewall::class.java, "SpellFirewall", nextEntityID)
		registerEntity(EntitySpellGravityTrap::class.java, "SpellGravityTrap", nextEntityID)
		registerEntity(EntitySpellHarp::class.java, "SpellHarp", nextEntityID)
		registerEntity(EntitySpellLeafStorm::class.java, "SpellLeafStorm", nextEntityID)
		registerEntity(EntitySpellIsaacMissile::class.java, "SpellIsaacMissile", nextEntityID)
		registerEntity(EntitySpellMortar::class.java, "SpellMortar", nextEntityID)
		registerEntity(EntitySpellNoteshot::class.java, "SpellNoteshot", nextEntityID)
		registerEntity(EntitySpellWindBlade::class.java, "SpellWindBlade", nextEntityID)
	}
	
	/**
	 * Registers new entity with egg. -1 color is rainbow color
	 * @param entityClass Entity's class file
	 * @param name The name of this entity
	 * @param id Mod-specific entity id
	 * @param color1 Egg color
	 * @param color2 Dots color
	 */
	fun <T> registerEntity(entityClass: Class<T>, name: String, id: Int, color1: Int, color2: Int) where T: Entity, T: IAlfheimMob {
		ItemSpawnEgg.addMapping(entityClass, color1, color2)
		registerEntity(entityClass, name, id)
	}
	*/
	
	/* PORT: блок-сущности — в КТ каждой (по описи), BlockEntityType через DeferredRegister; аномалии — КТ-3
	private fun registerTileEntities() {
		registerTile<TileAlfheimPortal>()
		registerTile<TileAlfheimPylon>()
		registerTile<TileAnimatedTorch>()
		registerTile<TileAnomaly>()
		registerTile<TileAnomalyHarvester>()
		registerTile<TileAnyavil>()
		registerTile<TileBarrel>()
		registerTile<TileBottomlessChest>()
		registerTile<TileChair>()
		registerTile<TileComposite>()
		registerTile<TileCorporeaAutocrafter>()
		registerTile<TileCorporeaInjector>()
		registerTile<TileCorporeaRat>()
		registerTile<TileCorporeaSparkBase>()
		registerTile<TileCurtainPlacer>()
		registerTile<TileDomainLobby>()
		registerTile<TileDoubleBlock>()
		registerTile<TileEnderActuator>()
		registerTile<TileFloatingFlowerRainbow>()
		registerTile<TileFloodLight>()
		registerTile<TileHeadFlugel>()
		registerTile<TileHeadMiku>()
		registerTile<TileGaiaButton>()
		registerTile<TileItemFrame>()
		registerTile<TileIcyGeyser>()
		registerTile<TileKudzuVine>()
		registerTile<TileManaAccelerator>()
		registerTile<TileManaInfuser>()
		registerTile<TileManaReflector>()
		registerTile<TileManaTuner>()
		registerTile<TilePowerStone>()
		registerTile<TileRaceSelector>()
		registerTile<TileRealityAnchor>()
		registerTile<TileRedstoneRelay>()
		registerTile<TileRedStringObserver>()
		registerTile<TileRedStringWatcher>()
		registerTile<TileRift>()
		registerTile<TileSecretGlass>()
		registerTile<TileSpire>()
		registerTile<TileTable>()
		registerTile<TileTradePortal>()
		registerTile<TileTreeBerry>()
		registerTile<TileVafthrudnirSoul>()
		registerTile<TileWorldTree>()
		registerTile<TileYggFlower>()
		
		registerAnomalies()
		
		registerTile<TileCracklingStar>()
		registerTile<TileStar>()
		registerTile<TileItemDisplay>()
		registerTile<TileLightningTreeTop>()
		registerTile<TileLivingwoodFunnel>()
		registerTile<TileRainbowManaFlame>()
		registerTile<TileSchemaController>()
		registerTile<TileSchemaAnnihilator>()
		registerTile<TileTreeCook>()
		registerTile<TileTreeCrafter>()
		registerTile<TileTreeWind>()
	}
	
	private inline fun <reified T: TileEntity> registerTile() {
		val clazz = T::class.java
		registerTileEntity(clazz, "${ModInfo.MODID}:${clazz.simpleName.replace("Tile", "")}")
	}
	
	private fun registerAnomalies() {
		registerAnomaly<SubTileAntigrav>("Antigrav", COMMON, 7, 0x7FE6FF)
		registerAnomaly<SubTileGravity>("Gravity", COMMON, 0, 0xEDEDED)
		registerAnomaly<SubTileKiller>("Killer", EPIC, 5, 0xFF6D6D)
		registerAnomaly<SubTileLightning>("Lightning", COMMON, 1, 0xFF0000)
		registerAnomaly<SubTileManaTornado>("ManaTornado", RARE, 2, -1)
		registerAnomaly<SubTileManaVoid>("ManaVoid", COMMON, 3, 0x03C0FF)
		registerAnomaly<SubTileSpeedUp>("SpeedUp", EPIC, 4, 0x20E020)
		registerAnomaly<SubTileWarp>("Warp", RARE, 6, 0x6020E0)
	}
	*/
	
	/* PORT: КТ-7 — заклинания
	private fun registerSpells() {
		SpellAcidMyst
		SpellAport
//		SpellAquaBind
		SpellAquaStream
		SpellBattleHorn
		SpellBeastWithin
		SpellBlink
		SpellBunnyHop
		SpellButterflyShield
		SpellCall
		SpellConfusion
		SpellDay
		SpellDarkness
		SpellDeathMark
		SpellDecay
		SpellDispel
		SpellDriftingMine
		SpellDragonGrowl
		SpellEcho
		SpellEdgeLife
		SpellFenrirStorm
		SpellFireball
		SpellFirestar
		SpellFirewall
		SpellGravityTrap
		SpellGoldRush
		SpellHammerfall
		SpellHarp
		SpellHealing
		SpellHollowBody
		SpellHystrix
		SpellIceLens
		SpellIgnition
		SpellIsaacStorm
		SpellJoin
		SpellLeafStorm
		SpellLiquification
		SpellMortar
		SpellNight
		SpellNightVision
		SpellNineLives
		SpellNoclip
		SpellNoteshot
		SpellOutdare
		SpellPoisonRoots
		SpellPurifyingSurface
		SpellRain
		SpellRepair
		SpellResurrect
		SpellSacrifice
		SpellShadowVortex
		SpellSmokeScreen
		SpellStoneSkin
		SpellSun
		SpellSwap
		SpellThor
		SpellThrow
		SpellThunder
		SpellTimeAnchor
		SpellTimeConquest
		SpellTimeStop
		SpellTitanHit
		SpellTrueSight
		SpellUphealth
		SpellVoodooDoll
		SpellVoodooTarget
		SpellPriorityTarget
		SpellWallWarp
		SpellWarhood
		SpellWaterBreathing
		SpellWellOLife
		SpellWhisper
		SpellWindBlades
	}
	*/
	
	/* PORT: КТ-4 — «розовость» читает только getPinkness (КТ-4); вещи с metadata — отдельные вещи 1.20.1 (SPEC, Р-5)
	private fun loadAllPinkStuff() {
		addPink(ItemStack(Blocks.wool, 1, 6), 1)
		addPink(ItemStack(Blocks.red_flower, 1, 7), 1)
		addPink(ItemStack(Blocks.stained_hardened_clay, 1, 6), 1)
		addPink(ItemStack(Blocks.stained_glass, 1, 6), 1)
		addPink(ItemStack(Blocks.stained_glass_pane, 1, 6), 1)
		addPink(ItemStack(Blocks.carpet, 1, 6), 1)
		addPink(ItemStack(Blocks.double_plant, 1, 5), 2)
		
		addPink(ItemStack(Items.dye, 1, 9), 1)
		addPink(ItemStack(Items.potionitem, 1, 8193), 2)
		addPink(ItemStack(Items.potionitem, 1, 8225), 3)
		addPink(ItemStack(Items.potionitem, 1, 8257), 3)
		addPink(ItemStack(Items.potionitem, 1, 16385), 2)
		addPink(ItemStack(Items.potionitem, 1, 16417), 3)
		addPink(ItemStack(Items.potionitem, 1, 16449), 3)
		addPink(ItemStack(Items.porkchop), 1)
		
		
		
		addPink(ItemStack(ModBlocks.corporeaCrystalCube), 9)
		addPink(ItemStack(ModBlocks.corporeaFunnel), 9)
		addPink(ItemStack(ModBlocks.corporeaIndex), 27)
		addPink(ItemStack(ModBlocks.corporeaInterceptor), 9)
		addPink(ItemStack(ModBlocks.corporeaRetainer), 9)
		addPink(ItemStack(ModBlocks.flower, 1, 6), 2)
		addPink(ItemStack(ModBlocks.floatingFlower, 1, 6), 2)
		addPink(ItemStack(ModBlocks.doubleFlower1, 1, 6), 4) // upper part
		addPink(ItemStack(ModBlocks.doubleFlower1, 1, 14), 4) // bottom part just in case
		addPink(ItemStack(ModBlocks.manaBeacon, 1, 6), 8)
		addPink(ItemStack(ModBlocks.mushroom, 1, 6), 4)
		addPink(ItemStack(ModBlocks.petalBlock, 1, 6), 9)
		addPink(ItemStack(ModBlocks.shinyFlower, 1, 6), 2)
		addPink(ItemStack(ModBlocks.spawnerClaw), 18)
		addPink(ItemStack(ModBlocks.spreader, 1, 3), 18)
		addPink(ItemStack(ModBlocks.starfield), 45)
		addPink(ItemStack(ModBlocks.storage, 1, 2), 81)
		addPink(ItemStack(ModBlocks.storage, 1, 4), 81)
		addPink(ItemStack(ModBlocks.tinyPotato), 1)
		addPink(ItemStack(ModBlocks.unstableBlock, 1, 6), 2)
		addPink(ItemBlockSpecialFlower.ofType(LibBlockNames.SUBTILE_ARCANE_ROSE), 2) // was 4
		
		addPink(ItemStack(ModFluffBlocks.lavenderQuartz), 4)
		addPink(ItemStack(ModFluffBlocks.lavenderQuartz, 1, 1), 4)
		addPink(ItemStack(ModFluffBlocks.lavenderQuartz, 1, 2), 4)
		addPink(ItemStack(ModFluffBlocks.lavenderQuartzSlab), 2)
		addPink(ItemStack(ModFluffBlocks.lavenderQuartzStairs), 4)
		
//		addPink(ItemStack(ModItems.aesirRing), 6000)
		addPink(ItemStack(ModItems.baubleBox), 5)
		addPink(ItemStack(ModItems.blackHoleTalisman), 36)
		addPink(ItemStack(ModItems.corporeaSpark), 9)
		addPink(ItemStack(ModItems.cosmetic, 1, 8), 4) // was 8
		addPink(ItemStack(ModItems.cosmetic, 1, 30), 1)
		addPink(ItemStack(ModItems.dye, 1, 6), 1)
		for (i in 0..9) addPink(ItemStack(ModItems.flightTiara, 1, i), 88)
		addPink(ItemStack(ModItems.manaResource, 1, 7), 9)
		addPink(ItemStack(ModItems.manaResource, 1, 8), 9)
		addPink(ItemStack(ModItems.manaResource, 1, 9), 9)
		addPink(ItemStack(ModItems.manaResource, 1, 19), 1)
		addPink(ItemStack(ModItems.elementiumAxe), 27)
		addPink(ItemStack(ModItems.elementiumBoots), 36)
		addPink(ItemStack(ModItems.elementiumChest), 72)
		addPink(ItemStack(ModItems.elementiumHelm), 45)
		if (Botania.thaumcraftLoaded) addPink(ItemStack(ModItems.elementiumHelmRevealing), 45)
		addPink(ItemStack(ModItems.elementiumLegs), 63)
		addPink(ItemStack(ModItems.elementiumPick), 27)
		addPink(ItemStack(ModItems.elementiumShears), 18)
		addPink(ItemStack(ModItems.elementiumShovel), 9)
		addPink(ItemStack(ModItems.elementiumSword), 18)
		addPink(ItemStack(ModItems.lens, 1, 14), 18)
//		addPink(ItemStack(ModItems.lokiRing), 1000)
//		addPink(ItemStack(ModItems.odinRing), 1000)
		addPink(ItemStack(ModItems.openBucket), 27)
		addPink(ItemStack(ModItems.petal, 1, 6), 1)
		addPink(ItemStack(ModItems.pinkinator), 100)
		addPink(ItemStack(ModItems.pixieRing), 45)
		addPink(ItemStack(ModItems.quartz, 1, 3), 1)
		addPink(ItemStack(ModItems.rainbowRod), 45)
		addPink(ItemStack(ModItems.reachRing), 36)
		addPink(ItemStack(ModItems.rune, 1, 4), 10)
		addPink(ItemStack(ModItems.spawnerMover), 63)
		addPink(ItemStack(ModItems.slimeBottle), 45)
		addPink(ItemStack(ModItems.starSword), 20)
		addPink(ItemStack(ModItems.superTravelBelt), 27) // was 38
//		addPink(ItemStack(ModItems.thorRing), 1000)
		
		
		
		addPink(ItemStack(AlfheimBlocks.anyavil), 297)
		addPink(ItemStack(AlfheimBlocks.alfheimPylon), 45)
		addPink(ItemStack(AlfheimBlocks.elvenOre), 9)
		addPink(ItemStack(AlfheimBlocks.elvenOre, 1, 1), 9)
		addPink(ItemStack(AlfheimBlocks.irisDirt, 1, 6), 2)
		addPink(ItemStack(AlfheimBlocks.irisTallGrass0, 1, 6), 2)
		addPink(ItemStack(AlfheimBlocks.irisGrass, 1, 6), 1)
		addPink(ItemStack(AlfheimBlocks.irisLeaves0, 1, 6), 1)
		addPink(ItemStack(AlfheimBlocks.irisLeaves0, 1, 14), 1)
		addPink(ItemStack(AlfheimBlocks.irisPlanks, 1, 6), 2)
		addPink(ItemStack(AlfheimBlocks.irisSlabs[6]), 1)
		addPink(ItemStack(AlfheimBlocks.irisStairs[6]), 2)
		addPink(ItemStack(AlfheimBlocks.irisWood1, 1, 2), 2)
		addPink(ItemStack(AlfheimBlocks.itemDisplay, 1, 2), 1)
		addPink(ItemStack(AlfheimBlocks.manaInfuser), 90)
		
		addPink(ItemStack(AlfheimFluffBlocks.shrineRock, 1, 6), 1)
		
		addPink(ItemStack(AlfheimItems.aesirEmblem), 18)
		addPink(ItemStack(AlfheimItems.astrolabe), 54)
		addPink(ItemStack(AlfheimItems.colorOverride), 54)
		addPink(ItemStack(AlfheimItems.cloudPendantSuper), 18)
		addPink(ItemStack(AlfheimItems.elementalBoots), 36)
		addPink(ItemStack(AlfheimItems.elementalChestplate), 72)
		addPink(ItemStack(AlfheimItems.elementalHelmet), 45)
		if (Botania.thaumcraftLoaded) addPink(ItemStack(AlfheimItems.elementalHelmetRevealing), 45)
		addPink(ItemStack(AlfheimItems.elementalLeggings), 63)
		addPink(ElvenResourcesMetas.ManaInfusionCore.stack, 9)
		addPink(ElvenResourcesMetas.ElvenWeed.stack, 8)
		addPink(ItemStack(AlfheimItems.discFlugel), 13)
		addPink(ItemStack(AlfheimItems.flugelHead), 5)
		for (i in 0..6) addPink(ItemStack(AlfheimItems.hyperBucket, 1, i), 27)
		addPink(ItemStack(AlfheimItems.irisSeeds, 1, 6), 2)
		addPink(ItemStack(AlfheimItems.multibauble), 18)
		addPink(ItemStack(AlfheimItems.pixieAttractor), 54)
		addPink(ItemStack(AlfheimItems.priestEmblem, 1, 3), 18)
//		addPink(ItemStack(AlfheimItems.priestRingHeimdall), 1000)
//		addPink(ItemStack(AlfheimItems.priestRingNjord), 1000)
//		addPink(ItemStack(AlfheimItems.priestRingSif), 1000)
		addPink(ItemStack(AlfheimItems.rodClicker), 29)
		addPink(ItemStack(AlfheimItems.rodColorfulSkyDirt), 27)
		addPink(ItemStack(AlfheimItems.spatiotemporalRing), 54)
		addPink(ItemStack(AlfheimItems.trisDagger), 36)
		addPink(ItemStack(AlfheimItems.wireAxe), 81)
	}
	*/
	
	private fun registerFlowerOres() {
		AlfheimConfigHandler.enderOreWeights.forEach {
			val (name, weight) = it.split(':')
			AlfheimAPI.addOreWeightEnd(name, weight.toInt())
		}
		
		AlfheimAPI.addOreWeightAlfheim("oreDragonstone", 940)
		AlfheimAPI.addOreWeightAlfheim("oreElementium", 35230)
		AlfheimAPI.addOreWeightAlfheim("oreQuartzElven", 33307)
		AlfheimAPI.addOreWeightAlfheim("oreGoldAlfheim", 1755)
		AlfheimAPI.addOreWeightAlfheim("oreIffesal", 232)
		AlfheimAPI.addOreWeightAlfheim("oreLapisAlfheim", 5480)
		
//		// Vanilla
//		AlfheimAPI.addOreWeightEnd("oreEndCoal", 9000)
//		AlfheimAPI.addOreWeightEnd("oreEndDiamond", 500)
//		AlfheimAPI.addOreWeightEnd("oreEndEmerald", 500)
//		AlfheimAPI.addOreWeightEnd("oreEndGold", 3635)
//		AlfheimAPI.addOreWeightEnd("oreEndIron", 5790)
//		AlfheimAPI.addOreWeightEnd("oreEndLapis", 3250)
//		AlfheimAPI.addOreWeightEnd("oreEndRedstone", 5600)
//
//		// Common tech ores
//		AlfheimAPI.addOreWeightEnd("oreEndCopper", 4700)
//		AlfheimAPI.addOreWeightEnd("oreEndTin", 3750)
//		AlfheimAPI.addOreWeightEnd("oreEndLead", 2790)
//		AlfheimAPI.addOreWeightEnd("oreEndNickel", 1790)
//		AlfheimAPI.addOreWeightEnd("oreEndPlatinum", 350)
//		AlfheimAPI.addOreWeightEnd("oreEndSilver", 1550)
//		AlfheimAPI.addOreWeightEnd("oreEndSteel", 1690)
//		AlfheimAPI.addOreWeightEnd("oreEndMithril", 1000)
//		AlfheimAPI.addOreWeightEnd("oreEndUranium", 2000)
//		AlfheimAPI.addOreWeightEnd("oreEndOsmium", 1000)
//		AlfheimAPI.addOreWeightEnd("oreEndIridium", 850)
//
//		// Tinker's Construct
//		AlfheimAPI.addOreWeightEnd("oreEndArdite", 1000)
//		AlfheimAPI.addOreWeightEnd("oreEndCobalt", 1000)
//
//		// Applied Energistics
//		AlfheimAPI.addOreWeightEnd("oreEndCertusQuartz", 2000)
//		AlfheimAPI.addOreWeightEnd("oreEndChargedCertusQuartz", 950)
//
//		// idk
//		AlfheimAPI.addOreWeightEnd("oreEndYellorite", 3000)
//		AlfheimAPI.addOreWeightEnd("oreClathrateEnder", 800)
//		AlfheimAPI.addOreWeightEnd("oreEndProsperity", 200)
//		AlfheimAPI.addOreWeightEnd("oreEndInferium", 500)
//		AlfheimAPI.addOreWeightEnd("oreEndBiotite", 500) // OreDictionary.registerOre("oreEndBiotite", Biotite.biotite_ore)
//
//		// Draconic Evolution
//		AlfheimAPI.addOreWeightEnd("oreDraconium", 200)
//
//		// Hardcore Ender Expansion (WRONG WEIGHTS)
//		AlfheimAPI.addOreWeightEnd("oreHeeStardust", 200)
//		AlfheimAPI.addOreWeightEnd("oreHeeInstabilityOrb", 200)
//		AlfheimAPI.addOreWeightEnd("oreHeeEndium", 200)
//		AlfheimAPI.addOreWeightEnd("oreHeeIgneousRock", 200)
//		AlfheimAPI.addOreWeightEnd("oreHeeEndPowder", 200)
	}
	
	/* PORT: КТ-5 — сброс предметов существами. В Botania 1.20.1 нет SheddingHandler, решается вместе с существами
	fun registerSheddings() {
		val slimePattern = SheddingHandler.patterns.find { it.EntityClass === EntitySlime::class.java }
		registerShedding(EntityElementalSlime::class.java, ElementalSlimeBall.stack, slimePattern?.rate ?: 21000, slimePattern?.rate ?: 40)
		
		registerShedding(EntityButterfly::class.java, ElvenFoodMetas.Nectar.stack, 26000, 10)
		registerShedding(EntityAlfheimPixie::class.java, ItemStack(ModItems.manaResource, 1, 8), 26000, 20)
		registerShedding(EntityRollingMelon::class.java, ItemStack(Items.melon_seeds), 21000, 40)
		registerShedding(EntitySnowSprite::class.java, ItemStack(Items.snowball), 12000, 20)
		
		if (ConfigHandler.config.hasChanged())
			ConfigHandler.config.save()
	}
	
	fun registerShedding(targetClass: Class<out Entity>, stack: ItemStack, rate: Int, size: Int) {
		SheddingHandler.defaultPatterns += SheddingHandler.ShedPattern(targetClass, stack, rate, size)
	}
	*/
}
