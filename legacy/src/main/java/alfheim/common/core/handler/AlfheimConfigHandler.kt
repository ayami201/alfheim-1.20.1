package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.*
import alexsocol.asjlib.math.*
import alfheim.api.*
import net.minecraftforge.common.config.Configuration.*
import java.io.*
import kotlin.math.*

object AlfheimConfigHandler: ASJConfigHandler() {
	
	const val CATEGORY_BALANCE = CATEGORY_GENERAL + CATEGORY_SPLITTER + "balance"
	const val CATEGORY_INTEGRATION = CATEGORY_GENERAL + CATEGORY_SPLITTER + "integration"
	const val CATEGORY_INT_TC = CATEGORY_INTEGRATION + CATEGORY_SPLITTER + "thaumcraft"
	const val CATEGORY_INT_TiC = CATEGORY_INTEGRATION + CATEGORY_SPLITTER + "tconstruct"
	const val CATEGORY_ENTITIES = CATEGORY_GENERAL + CATEGORY_SPLITTER + "entities"
	const val CATEGORY_ALFHEIM = CATEGORY_GENERAL + CATEGORY_SPLITTER + "alfheim"
	const val CATEGORY_NIFLHEIM = CATEGORY_GENERAL + CATEGORY_SPLITTER + "niflheim"
	const val CATEGORY_DOMAINS = CATEGORY_GENERAL + CATEGORY_SPLITTER + "domains"
	const val CATEGORY_WORLDGEN_A = CATEGORY_ALFHEIM + CATEGORY_SPLITTER + "worldgen"
	const val CATEGORY_ENTITIES_A = CATEGORY_WORLDGEN_A + CATEGORY_SPLITTER + "entities"
	const val CATEGORY_WORLDGEN_D = CATEGORY_DOMAINS + CATEGORY_SPLITTER + "worldgen"
	const val CATEGORY_WORLDGEN_N = CATEGORY_NIFLHEIM + CATEGORY_SPLITTER + "worldgen"
	const val CATEGORY_ENTITIES_N = CATEGORY_WORLDGEN_N + CATEGORY_SPLITTER + "entities"
	const val CATEGORY_TEMPERATURE = CATEGORY_GENERAL + CATEGORY_SPLITTER + "temperature"
	const val CATEGORY_POTIONS = CATEGORY_GENERAL + CATEGORY_SPLITTER + "potions"
	const val CATEGORY_ESMODE = CATEGORY_GENERAL + CATEGORY_SPLITTER + "elvenstory"
	const val CATEGORY_MMO = CATEGORY_ESMODE + CATEGORY_SPLITTER + "mmo"
	const val CATEGORY_MMOP = CATEGORY_MMO + CATEGORY_SPLITTER + "potions"
	const val CATEGORY_HUD = CATEGORY_MMO + CATEGORY_SPLITTER + "hud"
	
	var enableElvenStory: Boolean
		get() = AlfheimPreConfigHandler.enableElvenStory
		set(value) {
			AlfheimPreConfigHandler.enableElvenStory = value
			AlfheimPreConfigHandler.preconfig.get(CATEGORY_GENERAL, "enableElvenStory", value, "Set this to true to enable ESM").set(value)
			AlfheimPreConfigHandler.preconfig.save()
		}
	
	var enableMMO: Boolean
		get() = AlfheimPreConfigHandler.enableMMO
		set(value) {
			AlfheimPreConfigHandler.enableMMO = value
			AlfheimPreConfigHandler.preconfig.get(CATEGORY_GENERAL, "enableMMO", value, "Set this to true to enable MMO mode (requires ESM)").set(value)
			AlfheimPreConfigHandler.preconfig.save()
		}
	
	// DIMENSION
	// - ALFHEIM
	var dimensionIDAlfheim = -105
	var enableAlfheimRespawn = true
	var floatingIslandCountMax = 50
	var floatingIslandCountPerPlayer = 5
	var grabMidgardPortal = false
	var increasedSpiritsRange = true
	var overrideDimensionalRestrictions = false
	var rainbowPolys = 360
	var spiritsCountMultiplier = 0.5
	
	// - NIFLHEIM
	var dimensionIDNiflheim = -106
	var enableNiflheimRespawn = true
	var niflheimBiomeIDs = intArrayOf(41, 42, 43)
	
	// - OTHER
	var dimensionIDDomains = -104
	var dimensionIDHelheim = -103
	
	// WORLDGEN
	// - ALFHEIM
	var anomaliesDispersion = 50
	var anomaliesUpdate = 6000
	var citiesDistance = 1000
	var oregenMultiplier = 3
	var winterGrassReadyGen = true
	
	// - DOMAINS
	var domainDistance = 1000
	var domainImmediate = false
	var domainMaxCount = 5
	var domainPlayersRequired = 2
	var domainStartX = -5000
	var domainStartZ = -5000
	
	// ENTITIES
	// - ALFHEIM
	var butterflySpawn = intArrayOf(10, 1, 2)
	var chickSpawn = intArrayOf(10, 4, 4)
	var cowSpawn = intArrayOf(8, 4, 4)
	var elvesSpawn = intArrayOf(10, 2, 4)
	var jellySpawn = intArrayOf(10, 4, 4)
	var pigSpawn = intArrayOf(10, 4, 4)
	var pixieSpawn = intArrayOf(10, 1, 2)
	var sheepSpawn = intArrayOf(12, 4, 4)
	
	var despawnChunks = 2
	var minChunks = 1
	var maxChunks = 6
	var playerGroupDistance = maxChunks
	var tfMobs = true
	
	// - NIFLHEIM
	var vikingSpawn = intArrayOf(100, 4, 4)
	
	// - ALL
	var elementalSlimeRates = intArrayOf(100, 4, 4)
	var elementalSlimeBiomeBlackList = intArrayOf(8, 9, 14, 15)
	var voidCreeperRates = intArrayOf(4, 1, 3)
	var voidCreeperBiomeBlackList = intArrayOf(8, 9, 14, 15)
	
	// TEMPERATURE
	var biomesCold = intArrayOf()
	var biomesColdDimBlacklist = intArrayOf()
	var biomesHot = intArrayOf(8)
	var biomesHotDimBlacklist = intArrayOf()
	var damageAddCold = 0.15
	var damageAddHot = 0.15
	var damageModCold = 0.01
	var damageModHot = 0.03
	var mobTemperature = true
	var mobBlacklistCold = arrayOf("SnowMan")
	var mobBlacklistHot = arrayOf("Blaze", "Ghast", "LavaSlime", "PigZombie", "WitherBoss")
	
	// BALANCE
	var floatingIslandDrops = 0.01
	var gourmaryllisDifficulty = 2
	var looniumOverseed = false
	var kudzuDelay = 20
	var kudzuDropBlacklist = arrayOf("ingotInfernoDiamond")
	var kudzuDropBlocks = false
	var kudzuEasy = true
	var kudzuMutatability = 5
	var kudzuRadius = 127
	var maceModifier = 0.5f
	var moonbowMaxDmg = 20
	var moonbowVelocity = 0.5f
	var mountAlfheimOnly = false
	var mountCost = 1000
	var mountEnabled = true
	var mountLife = 600
	var multibaubleBlacklist = emptyArray<String>()
	var multibaubleCount = 6
	var rattleroseSpeed = 20
	var repairBlackList = emptyArray<String>()
	var soulSwordMaxLvl = Int.MAX_VALUE
	var spreaderCapacityLebe = 64000
	var spreaderCapacityMauf = 24000
	var spreaderSpeedLebe = 8000
	var spreaderSpeedMauf = 2400
	var tradePortalRate = 1200
	var triquetrumManaUsage = intArrayOf(100, 60)
	var triquetrumMaxVolume = 400000
	var triquetrumTiles = true
	var uberBlaster = true
	var yggdrasilFruitMinSpawnDelay = 20 * 60 * 60 * 24
	
	// OTHER
	var alfheimSleepExtraCheck = true
	var authTimeout = 200
	var barrierTreeAllowAnyPlayer = false
	var blackLotusDropRate = 0.05
	var cataclysmCooldown = 600
	var disableShedding = false
	var effectScreenOverlay = true
	var enderOreWeights = arrayOf("oreEndCoal:9000", "oreEndDiamond:500", "oreEndEmerald:500", "oreEndGold:3635", "oreEndIron:5790", "oreEndLapis:3250", "oreEndRedstone:5600", "oreDraconium:200")
	var eventBanner = true
	var fancies = true
	var faultLinePersistence = 3000
	var flagIdSheepRainbow = 31
	var floatingIslandNoCollisionBlocks = arrayOf("Natura:Cloud")
	var floatingIslandPathfinder = true
	var floatingIslandSyncedDataInitLimit = 31
	var floodLightQuality = 10
	var hotControls = 2
	var imPatheticWeakAndScaredDontTouchMyWorlds = false
	var interactEventChecks = false
	var interdimensionalSubspacian = true
	var lexiconSort = false
	var lightningsSpeed = 20
	var longSeasons = true
	var mobElements = arrayOf("Blaze:FIRE", "EnderDragon:DARKNESS", "Enderman:DARKNESS", "Ghast:AIR,PSYCHIC", "LavaSlime:FIRE,EARTH", "MushroomCow:NATURE", "SnowMan:ICE", "Slime:NATURE,WATER", "VillagerGolem:EARTH", "WitherBoss:DARKNESS", "Thaumcraft.EldritchCrab:DARKNESS", "Thaumcraft.EldritchGolem:EARTH", "Thaumcraft.EldritchGuardian:DARKNESS,PSYCHIC", "Thaumcraft.EldritchWarden:DARKNESS,PSYCHIC", "Thaumcraft.Firebat:FIRE", "Thaumcraft.MindSpider:PSYCHIC", "Thaumcraft.ThaumSlime:WATER,DARKNESS", "ThermalFoundation.Blizz:ICE", "ThermalFoundation.Blitz:ELECTRIC", "ThermalFoundation.Basalz:EARTH")
	var minimalGraphics = false
	var mobPriests = true
	var newStorageTexture = true
	var notifications = true
	var numericalMana = true
	var oiiaId = 2
	var realLightning = false
	var relicsProtectionBlackList = emptyArray<String>()
	var renderBooba = true
	var replaceHellFireChance = 5
	var rocketRide = 2
	var searchTabAlfheim = true
	var searchTabBotania = true
	var schemaArray = IntArray(17) { -1 + it }
	var schemaMaxSize = 64
	var shedLifespan = 1200
	var storyLines = 4
	var timelessProtection = true
	var triquetrumBlackList = emptyArray<String>()
	var wireoverpowered = true
	lateinit var worldDestroyConfig: Map<Int, Int>
	
	// INTEGRATION
	var chatLimiters = "%s"
	var poolRainbowCapacity = 1000000 // TilePool.MAX_MANA
	
	// TC INTEGRATION
	var addAspectsToBotania = true
	var addTincturaAspect = true
	var overrideFMCaps = true
	var thaumTreeSuffusion = true
	
	// TiC INTEGRATION
	var materialIDs = intArrayOf(50, 51, 52, 53, 54, 55, 56, 57, 3, 4)
	var modifierIDs = intArrayOf(20)
	
	// POTIONS
	private var potionID___COUNTER = 30
	get() = field++
	
	var potionIDBeastWithin = potionID___COUNTER
	var potionIDBeer = potionID___COUNTER
	var potionIDBerserk = potionID___COUNTER
	var potionIDBleeding = potionID___COUNTER
	var potionIDButterShield = potionID___COUNTER
	var potionIDChampagne = potionID___COUNTER
	var potionIDDeathMark = potionID___COUNTER
	var potionIDDecay = potionID___COUNTER
	var potionIDEdgeLife = potionID___COUNTER
	var potionIDEternity = potionID___COUNTER
	var potionIDGoldRush = potionID___COUNTER
	var potionIDHystrix = potionID___COUNTER
	var potionIDIceLens = potionID___COUNTER
	var potionIDLeftFlame = potionID___COUNTER
	var potionIDLightningShield = potionID___COUNTER
	var potionIDManaVoid = potionID___COUNTER
	var potionIDNineLives = potionID___COUNTER
	var potionIDNinja = potionID___COUNTER
	var potionIDNoclip = potionID___COUNTER
	var potionIDOvercold = potionID___COUNTER
	var potionIDOverheat = potionID___COUNTER
	var potionIDOvermage = potionID___COUNTER
	var potionIDPossession = potionID___COUNTER
	var potionIDPriorityTarget = potionID___COUNTER
	var potionIDQuadDamage = potionID___COUNTER
	var potionIDSacrifice = potionID___COUNTER
	var potionIDShowMana = potionID___COUNTER
	var potionIDSoulburn = potionID___COUNTER
	var potionIDStoneSkin = potionID___COUNTER
	var potionIDTank = potionID___COUNTER
	var potionIDThrow = potionID___COUNTER
	var potionIDTimeAnchor = potionID___COUNTER
	var potionIDTimeConquest = potionID___COUNTER
	var potionIDVoodooDoll = potionID___COUNTER
	var potionIDVoodooTarget = potionID___COUNTER
	var potionIDWellOLife = potionID___COUNTER
	var potionIDWhiteWine = potionID___COUNTER
	var potionIDWisdom = potionID___COUNTER
	var potionIDWtfBerry0 = potionID___COUNTER
	var potionIDWtfBerry2 = potionID___COUNTER
	var potionIDWtfBerry3 = potionID___COUNTER
	var potionIDWtfBerry4 = potionID___COUNTER
	var potionIDWtfBerry5 = potionID___COUNTER
	
	// Elven Story
	var bonusChest = false
	var bothSpawnStructures = false
	var extendedElvenStory = false
	var flightTime = 12000
	var flightRecover = 1.0
	var wingsBlackList = IntArray(0)
	val zones = Array(9) { Vector3() }
	
	// MMO
	var deathScreenAddTime = 1200
	var disabledSpells = emptyArray<String>()
	var disableWireframe = false
	var friendlyFire = false
	var legendarySpells = arrayOf("sacrifice", "isaacstorm", "resurrect", "timestop", "warhood")
	var maxPartyMembers = 5
	var raceManaMult = 2.toByte()
	var spellSortByAffinity = true
	var superSpellBosses = false
	
	// MMO HUD
	var partyHUDScale = 1.0
	var selfHealthUI = true
	var spellsFadeOut = false
	var targetUI = true
	
	override fun addCategories() {
		addCategory(CATEGORY_BALANCE, "Balance settings")
		addCategory(CATEGORY_ALFHEIM, "Alfheim dimension settings")
		addCategory(CATEGORY_NIFLHEIM, "Niflheim dimension settings")
		addCategory(CATEGORY_WORLDGEN_A, "Alfheim worldgen settings")
		addCategory(CATEGORY_ENTITIES_A, "Alfheim entities settings")
		addCategory(CATEGORY_TEMPERATURE, "Temperature settings")
		addCategory(CATEGORY_POTIONS, "Potion IDs")
		addCategory(CATEGORY_INTEGRATION, "Cross-mods and modpacks integration")
		addCategory(CATEGORY_INT_TC, "Thaumcraft integration")
		addCategory(CATEGORY_INT_TiC, "Tinkers' Construct integration")
		addCategory(CATEGORY_ESMODE, "Elvenstory Mode optional features")
		addCategory(CATEGORY_MMO, "MMO Mode optional features")
		addCategory(CATEGORY_HUD, "HUD elements customizations")
		addCategory(CATEGORY_MMOP, "Potion IDs")
	}
	
	override fun readProperties() {
		dimensionIDAlfheim = loadProp(CATEGORY_ALFHEIM, "dimensionIDAlfheim", dimensionIDAlfheim, true, "Dimension ID for Alfheim")
		enableAlfheimRespawn = loadProp(CATEGORY_ALFHEIM, "enableAlfheimRespawn", enableAlfheimRespawn, false, "Set this to false to disable respawning in Alfheim")
		floatingIslandCountMax = loadProp(CATEGORY_ALFHEIM, "floatingIslandCountMax", floatingIslandCountMax, false, "Max count of floating islands in world", 1)
		floatingIslandCountPerPlayer = loadProp(CATEGORY_ALFHEIM, "floatingIslandCountPerPlayer", floatingIslandCountPerPlayer, false, "Max count of floating islands per player in world", 1)
		grabMidgardPortal = loadProp(CATEGORY_ALFHEIM, "grabMidgardPortal", grabMidgardPortal, false, "Set this to true to teleport near existing active loaded portal when leaving Alfheim instead of world spawn")
		increasedSpiritsRange = loadProp(CATEGORY_ALFHEIM, "increasedSpiritsRange", increasedSpiritsRange, false, "Set this to false to reduce nighttime spirits spawn range in Alfheim (may increase FPS)")
		overrideDimensionalRestrictions = loadProp(CATEGORY_ALFHEIM, "overrideDimensionalRestrictions", overrideDimensionalRestrictions, false, "Set this to true to remove dimensional teleportation restrictions for default mode")
		rainbowPolys = loadProp(CATEGORY_ALFHEIM, "rainbowPolys", rainbowPolys, false, "How smooth will rainbow and rays in Alfheim sky be (higher number - more polygons)")
		spiritsCountMultiplier = loadProp(CATEGORY_ALFHEIM, "spiritsCountMultiplier", spiritsCountMultiplier, false, "Affects nighttime spirits spawn count in Alfheim")
		
		dimensionIDNiflheim = loadProp(CATEGORY_NIFLHEIM, "dimensionIDNiflheim", dimensionIDNiflheim, true, "Dimension ID for Niflheim")
		enableNiflheimRespawn = loadProp(CATEGORY_NIFLHEIM, "enableNiflheimRespawn", enableNiflheimRespawn, false, "Set this to false to disable respawning in Niflheim")
		niflheimBiomeIDs = loadProp(CATEGORY_NIFLHEIM, "niflheimBiomeIDs", niflheimBiomeIDs, true, "List of Niflheim biome IDs")
		
		dimensionIDDomains = loadProp(CATEGORY_DOMAINS, "dimensionIDDomains", dimensionIDDomains, true, "Dimension ID for Domains world")
		dimensionIDHelheim = loadProp(CATEGORY_GENERAL, "dimensionIDHelheim", dimensionIDHelheim, true, "Dimension ID for Helheim")
		
		anomaliesDispersion = loadProp(CATEGORY_WORLDGEN_A, "anomaliesDispersion", anomaliesDispersion, false, "How rare anomalies are (one per N chunks)")
		anomaliesUpdate = loadProp(CATEGORY_WORLDGEN_A, "anomaliesUpdate", anomaliesUpdate, false, "How many times anomaly will simulate tick while being generated")
		citiesDistance = loadProp(CATEGORY_WORLDGEN_A, "citiesDistance", citiesDistance, true, "Distance between any elven city and worlds center")
		oregenMultiplier = loadProp(CATEGORY_WORLDGEN_A, "oregenMultiplier", oregenMultiplier, true, "Multiplier for Alfheim oregen")
		winterGrassReadyGen = loadProp(CATEGORY_WORLDGEN_A, "winterGrassReadyGen", winterGrassReadyGen, false, "Set this to false to prevent ready generation snow grass instead of regular")
		
		domainDistance = loadProp(CATEGORY_WORLDGEN_D, "domainDistance", domainDistance, true, "Distance between domains")
		domainImmediate = loadProp(CATEGORY_WORLDGEN_D, "domainImmediate", domainImmediate, true, "Set this to true to immediately generate max domains instead of generating new one in same type line if and only if all previously generated ones are occupied")
		domainMaxCount = loadProp(CATEGORY_WORLDGEN_D, "domainMaxCount", domainMaxCount, true, "Count of domains of the same type")
		domainPlayersRequired = loadProp(CATEGORY_WORLDGEN_D, "domainPlayersRequired", domainPlayersRequired, true, "Min number of players required to enter any domain in MP")
		domainStartX = loadProp(CATEGORY_WORLDGEN_D, "domainStartX", domainStartX, true, "X-position of first domain in different type line")
		domainStartZ = loadProp(CATEGORY_WORLDGEN_D, "domainStartZ", domainStartZ, true, "Z-position of first domain in same type line")
		
		despawnChunks = loadProp(CATEGORY_ENTITIES_A, "despawnChunks", despawnChunks, false, "Additional chunks for if-no-player-nearby despawning")
		minChunks = loadProp(CATEGORY_ENTITIES_A, "minChunks", minChunks, false, "Min distance in chunks from player at which mobs can spawn")
		maxChunks = loadProp(CATEGORY_ENTITIES_A, "maxChunks", maxChunks, false, "Max distance in chunks from player at which mobs can spawn")
		playerGroupDistance = loadProp(CATEGORY_ENTITIES_A, "playerGroupDistance", playerGroupDistance, false, "Distance in chunks for players to be considered as player group (for mob spawning balance)")
		tfMobs = loadProp(CATEGORY_ENTITIES_A, "tfMobs", tfMobs, true, "Set this to false to remove Twilight Forest mobs from Alfheim spawn")
		
		butterflySpawn = loadProp(CATEGORY_ENTITIES_A, "butterflySpawn", butterflySpawn, true, "Butterfly max count per player, min and max group count")
		cowSpawn = loadProp(CATEGORY_ENTITIES_A, "cowSpawn", cowSpawn, true, "Cows max count per player, min and max group count")
		chickSpawn = loadProp(CATEGORY_ENTITIES_A, "chickSpawn", chickSpawn, true, "Chicken max count per player, min and max group count")
		elvesSpawn = loadProp(CATEGORY_ENTITIES_A, "elvesSpawn", elvesSpawn, true, "Elves max count per player, min and max group count")
		jellySpawn = loadProp(CATEGORY_ENTITIES_A, "jellySpawn", jellySpawn, true, "Jellyfish max count per player, min and max group count")
		pigSpawn = loadProp(CATEGORY_ENTITIES_A, "pigSpawn", pigSpawn, true, "Pig max count per player, min and max group count")
		pixieSpawn = loadProp(CATEGORY_ENTITIES_A, "pixieSpawn", pixieSpawn, true, "Pixie max count per player, min and max group count")
		sheepSpawn = loadProp(CATEGORY_ENTITIES_A, "sheepSpawn", sheepSpawn, true, "Sheep max count per player, min and max group count")
		
		elementalSlimeRates = loadProp(CATEGORY_ENTITIES, "elementalSlimeRates", elementalSlimeRates, true, "Elemental Slimes spawn weight (chance), min and max group count")
		elementalSlimeBiomeBlackList = loadProp(CATEGORY_ENTITIES, "elementalSlimeBiomeBlackList", elementalSlimeBiomeBlackList, true, "Biome blacklist for Elemental Slimes", false)
		voidCreeperRates = loadProp(CATEGORY_ENTITIES, "voidCreeperRates", voidCreeperRates, true, "Manaseal Creeper spawn weight (chance), min and max group count")
		voidCreeperBiomeBlackList = loadProp(CATEGORY_ENTITIES, "voidCreeperBiomeBlackList", voidCreeperBiomeBlackList, true, "Biome blacklist for Manaseal Creepers", false)
		
		vikingSpawn = loadProp(CATEGORY_ENTITIES_N, "vikingSpawn", vikingSpawn, true, "Frozen Vikings max count per player, min and max group count")
		
		biomesCold = loadProp(CATEGORY_TEMPERATURE, "biomesCold", biomesCold, false, "List of cold biomes where sheer cold will be accumulating", false)
		biomesColdDimBlacklist = loadProp(CATEGORY_TEMPERATURE, "biomesColdDimBlacklist", biomesColdDimBlacklist, false, "List of dimension IDs where 'biomesCold' won't trigger sheer cold accumulation", false)
		biomesHot = loadProp(CATEGORY_TEMPERATURE, "biomesHot", biomesHot, false, "List of hot biomes where blazing heat will be accumulating", false)
		biomesHotDimBlacklist = loadProp(CATEGORY_TEMPERATURE, "biomesHotDimBlacklist", biomesHotDimBlacklist, false, "List of dimension IDs where 'biomesHot' won't trigger blazing heat accumulation", false)
		damageAddCold = loadProp(CATEGORY_TEMPERATURE, "damageAddCold", damageAddCold, false, "Sheer cold additional damage")
		damageAddHot = loadProp(CATEGORY_TEMPERATURE, "damageAddHot", damageAddHot, false, "Blazing heat additional damage")
		damageModCold = loadProp(CATEGORY_TEMPERATURE, "damageModCold", damageModCold, false, "Sheer cold damage scale of max HP")
		damageModHot = loadProp(CATEGORY_TEMPERATURE, "damageModHot", damageModHot, false, "Blazing heat damage scale of max HP")
		mobTemperature = loadProp(CATEGORY_TEMPERATURE, "mobTemperature", mobTemperature, false, "Set this to false to completely disable mobs getting overcold and overheat effects (may break some mechanics)")
		mobBlacklistCold = loadProp(CATEGORY_TEMPERATURE, "mobBlacklistCold", mobBlacklistCold, false, "List of entity names immune to power of Niflheim", false)
		mobBlacklistHot = loadProp(CATEGORY_TEMPERATURE, "mobBlacklistHot", mobBlacklistHot, false, "List of entity names immune to power of Muspelheim", false)
		
		floatingIslandDrops = loadProp(CATEGORY_BALANCE, "floatingIslandDrops", floatingIslandDrops, false, "Percent of floating islands blocks to be dropped on destruction (0 - none, 0.5 - 50%, 1 - 100%)", 0.0, 1.0)
		gourmaryllisDifficulty = loadProp(CATEGORY_BALANCE, "gourmaryllisDifficulty", gourmaryllisDifficulty, false, "Difficulty of Gourmaryllis functionality: 0 - default, 1 - as in 1.12.2, 2 - hardcore", 0, 2)
		looniumOverseed = loadProp(CATEGORY_BALANCE, "looniumOverseed", looniumOverseed, true, "Set this to true to make loonium spawn overgrowth seeds (for servers with limited dungeons so all players can craft Gaia pylons)")
		kudzuDelay = loadProp(CATEGORY_BALANCE, "kudzuDelay", kudzuDelay, false, "Average delay between kudzu brood ticks, higher values reduces server load", 5, 1200)
		kudzuDropBlacklist = loadProp(CATEGORY_BALANCE, "kudzuDropBlacklist", kudzuDropBlacklist, false, "Blacklist of ore dictionary names kudzu won't drop (MineTweaker can add new oredict names to items)", false)
		kudzuDropBlocks = loadProp(CATEGORY_BALANCE, "kudzuDropBlocks", kudzuDropBlocks, false, "Set this to true to make kudzu drop eaten blocks")
		kudzuEasy = loadProp(CATEGORY_BALANCE, "kudzuEasy", kudzuEasy, false, "Set this to false to make kudzu spread in air")
		kudzuMutatability = loadProp(CATEGORY_BALANCE, "kudzuMutatability", kudzuMutatability, false, "Chance of kudzu vine mutating on spread", 1, 100)
		kudzuRadius = loadProp(CATEGORY_BALANCE, "kudzuRadius", kudzuRadius, false, "Max spread radius for kudzu vine", 15, 255)
		maceModifier = loadProp(CATEGORY_BALANCE, "maceModifier", maceModifier.D, false, "Mace additional damage from fall distance modifier").F
		moonbowMaxDmg = loadProp(CATEGORY_BALANCE, "moonbowMaxDmg", moonbowMaxDmg, false, "Max base damage for Phoebus Catastrophe")
		moonbowVelocity = loadProp(CATEGORY_BALANCE, "moonbowVelocity", moonbowVelocity.D, false, "Phoebus Catastrophe charge speed").F
		mountAlfheimOnly = loadProp(CATEGORY_BALANCE, "mountAlfheimOnly", mountAlfheimOnly, false, "Set this to false to make mounts summonable only in Alfheim")
		mountCost = loadProp(CATEGORY_BALANCE, "mountCost", mountCost, false, "How much mana mount consumes on summoning (not teleporting)")
		mountEnabled = loadProp(CATEGORY_BALANCE, "mountEnabled", mountEnabled, true, "Are mounts available at all")
		mountLife = loadProp(CATEGORY_BALANCE, "mountLife", mountLife, false, "How many ticks mount can stay unmounted")
		multibaubleBlacklist = loadProp(CATEGORY_BALANCE, "multibaubleBlacklist", multibaubleBlacklist, false, "Blacklist for Ring of Elven King [modid:name]", false)
		multibaubleCount = loadProp(CATEGORY_BALANCE, "multibaubleCount", multibaubleCount, false, "How many bauble box slots will be activated by Ring of Elven King")
		rattleroseSpeed = loadProp(CATEGORY_BALANCE, "rattleroseSpeed", rattleroseSpeed, false, "Rattlerose game update speed (one time per N ticks). Set to 0 to switch to manual control")
		repairBlackList = loadProp(CATEGORY_BALANCE, "repairBlackList", repairBlackList, false, "Blacklist of repairable items (ex: for anyavil) [modid:name]", false)
		soulSwordMaxLvl = loadProp(CATEGORY_BALANCE, "soulSwordMaxLvl", soulSwordMaxLvl, false, "Sword of Ragnarok max level")
//		spreaderCapacityLebe = loadProp(CATEGORY_BALANCE, "spreaderCapacityLebe", spreaderCapacityLebe, false, "Lebethron Spreader max mana cap") TODO back
		spreaderCapacityMauf = loadProp(CATEGORY_BALANCE, "spreaderCapacityMauf", spreaderCapacityMauf, false, "Mauftrium Spreader max mana cap")
//		spreaderSpeedLebe = loadProp(CATEGORY_BALANCE, "spreaderSpeedLebe", spreaderSpeedLebe, false, "Lebethron Spreader mana per shot") TODO back
		spreaderSpeedMauf = loadProp(CATEGORY_BALANCE, "spreaderSpeedMauf", spreaderSpeedMauf, false, "Mauftrium Spreader mana per shot")
		tradePortalRate = loadProp(CATEGORY_BALANCE, "tradePortalRate", tradePortalRate, false, "Portal updates every [N] ticks")
		triquetrumBlackList = loadProp(CATEGORY_BALANCE, "triquetrumBlackList", triquetrumBlackList, false, "Blacklist for blocks that triquetrum can't swap [modid:name]", false)
		triquetrumManaUsage = loadProp(CATEGORY_BALANCE, "triquetrumManaUsage", triquetrumManaUsage, false, "Mana usage for triquetrum, 1st is for tiles, 2nd for regular blocks")
		triquetrumMaxVolume = loadProp(CATEGORY_BALANCE, "triquetrumMaxVolume", triquetrumMaxVolume, false, "Change this to limit triquetrum volume of operation")
		triquetrumTiles = loadProp(CATEGORY_BALANCE, "triquetrumTiles", triquetrumTiles, false, "Set this to false to forbid triquetrum to move tiles")
		uberBlaster = loadProp(CATEGORY_BALANCE, "uberBlaster", uberBlaster, false, "Set this to false to nerf blasters")
		yggdrasilFruitMinSpawnDelay = loadProp(CATEGORY_BALANCE, "yggdrasilFruitMinSpawnDelay", yggdrasilFruitMinSpawnDelay, false, "Minimal delay from yggdrasil fruit to appear (max delay will be x3 more)")
		
		alfheimSleepExtraCheck = loadProp(CATEGORY_GENERAL, "alfheimSleepExtraCheck", alfheimSleepExtraCheck, false, "Set this to false if you are skipping whole day while sleeping")
		authTimeout = loadProp(CATEGORY_GENERAL, "authTimeout", authTimeout, false, "Time limit for client to send authentication credentials", 100, 600)
		barrierTreeAllowAnyPlayer = loadProp(CATEGORY_GENERAL, "barrierTreeAllowAnyPlayer", barrierTreeAllowAnyPlayer, false, "Set this to true to allow any player to bypass barrier trees")
		blackLotusDropRate = loadProp(CATEGORY_GENERAL, "blackLotusDropRate", blackLotusDropRate, false, "Rate of black loti dropping from Manaseal Creepers")
		cataclysmCooldown = loadProp(CATEGORY_GENERAL, "cataclysmCooldown", cataclysmCooldown, false, "Average ticks between cataclysms", 100, 6000)
		disableShedding = loadProp(CATEGORY_GENERAL, "disableShedding", disableShedding, false, "Set this to true to completely disable mob shedding from Botania")
		effectScreenOverlay = loadProp(CATEGORY_GENERAL, "effectScreenOverlay", effectScreenOverlay, false, "Set this to false to disable screen overlay for effects like heat/cold")
		enderOreWeights = loadProp(CATEGORY_GENERAL, "enderOreWeights", enderOreWeights, false, "Map of OreDict name to ore weight (more weight - more chace to spawn) for Orechid Endium", false)
		eventBanner = loadProp(CATEGORY_GENERAL, "eventBanner", eventBanner, false, "Set this to false to disable event banner popup")
		fancies = loadProp(CATEGORY_GENERAL, "fancies", fancies, false, "Set this to false to locally disable fancies rendering on you (for contributors only)")
		faultLinePersistence = loadProp(CATEGORY_GENERAL, "faultLinePersistence", faultLinePersistence, false, "Persistence for Fault Lines (lower value - smaller faults)")
		flagIdSheepRainbow = loadProp(CATEGORY_GENERAL, "flagIdSheepRainbow", flagIdSheepRainbow, true, "Flag ID for sheep to be rainbow colored")
		floatingIslandNoCollisionBlocks = loadProp(CATEGORY_GENERAL, "floatingIslandNoCollisionBlocks", floatingIslandNoCollisionBlocks, true, "List of collidable blocks floating islands won't collide with", false)
		floatingIslandNoCollisionBlocks = loadProp(CATEGORY_GENERAL, "floatingIslandNoCollisionBlocks", floatingIslandNoCollisionBlocks, true, "List of collidable blocks floating islands won't collide with", false)
		floatingIslandPathfinder = loadProp(CATEGORY_GENERAL, "floatingIslandPathfinder", floatingIslandPathfinder, false, "Set this to false to disable entity's pathfinding on floating islands. This will make them stand still on islands, but will also lower the server load")
		floatingIslandSyncedDataInitLimit = loadProp(CATEGORY_GENERAL, "floatingIslandSyncedDataInitLimit", floatingIslandSyncedDataInitLimit, false, "Increase that limit ONLY if you have mods that extend DataWatcher IDs and want really large floating island")
		floodLightQuality = loadProp(CATEGORY_GENERAL, "floodLightQuality", floodLightQuality, false, "Determines floodlight raycasting steps (lower values - more quality and CPU load). Must be an integer divisor of 360", 1, 120)
		hotControls = loadProp(CATEGORY_GENERAL, "hotControls", hotControls, false, "High overheat value would mess your controls if set to 2, only on Hard difficulty if set to 1, would not mess completely if set to 0", 0, 2)
		imPatheticWeakAndScaredDontTouchMyWorlds = loadProp(CATEGORY_GENERAL, "imPatheticWeakAndScaredDontTouchMyWorlds", imPatheticWeakAndScaredDontTouchMyWorlds, false, "Set this to true to disable hardcoded world destruction during Ragnarok and affect ONLY Alfheim")
		interactEventChecks = loadProp(CATEGORY_GENERAL, "interactEventChecks", interactEventChecks, false, "Distance checks when firing interaction events, results may be unclear")
		interdimensionalSubspacian = loadProp(CATEGORY_GENERAL, "interdimensionalSubspacian", interdimensionalSubspacian, false, "Set this to false to forbid subspacian sending eater to other dimension")
		lexiconSort = loadProp(CATEGORY_GENERAL, "lexiconSort", lexiconSort, true, "Set this to true to sort Alfheim lexicon entries to vanilla categories")
		lightningsSpeed = loadProp(CATEGORY_GENERAL, "lightningsSpeed", lightningsSpeed, false, "How many ticks it takes between two lightings are spawned in Lightning Anomaly render")
		longSeasons = loadProp(CATEGORY_GENERAL, "longSeasons", longSeasons, true, "Set this to false to make seasons last 1 real day instead of 3")
		minimalGraphics = loadProp(CATEGORY_GENERAL, "minimalGraphics", minimalGraphics, true, "Set this to true to disable .obj models and shaders")
		mobElements = loadProp(CATEGORY_GENERAL, "mobElements", mobElements, true, "Array of mob names to the list of their elements", false)
		mobPriests = loadProp(CATEGORY_GENERAL, "mobPriests", mobPriests, false, "Set this to false so that only players can be priests")
		newStorageTexture = loadProp(CATEGORY_GENERAL, "newStorageTexture", newStorageTexture, true, "Set this to false to disable new storage blocks textures")
		notifications = loadProp(CATEGORY_GENERAL, "notifications", notifications, false, "Set this to false to disable custom notifications and version check")
		numericalMana = loadProp(CATEGORY_GENERAL, "numericalMana", numericalMana, false, "Set this to false to disable numerical mana representation")
		oiiaId = loadProp(CATEGORY_GENERAL, "oiiaId", oiiaId, false, "Change this if you are getting crash 'Duplicate id value for ...' from DataWatcher")
		realLightning = loadProp(CATEGORY_GENERAL, "realLightning", realLightning, false, "Set this to true to make Rod of the Thundering Peaks summon real (weather) lightning")
		relicsProtectionBlackList = loadProp(CATEGORY_GENERAL, "relicsProtectionBlackList", relicsProtectionBlackList, false, "Blacklist for relics protection [modid:name]", false)
		renderBooba = loadProp(CATEGORY_GENERAL, "renderBooba", renderBooba, false, "Set this to false to disable ESM booba render")
		replaceHellFireChance = loadProp(CATEGORY_GENERAL, "replaceHellFireChance", replaceHellFireChance, false, "Chance for Fire Of Eternity to replace regular fire when placed in Muspelheim (x5 for worlgen)", 0, 100)
		rocketRide = loadProp(CATEGORY_GENERAL, "rocketRide", rocketRide, false, "Rocket ride [-1 - not players, 0 - none, 1 - players, 2 - anyone]")
		searchTabAlfheim = loadProp(CATEGORY_GENERAL, "searchTabAlfheim", searchTabAlfheim, false, "Set this to false to disable searchbar in Alfheim Tab")
		searchTabBotania = loadProp(CATEGORY_GENERAL, "searchTabBotania", searchTabBotania, false, "Set this to false to disable searchbar in Botania Tab")
		schemaArray = loadProp(CATEGORY_GENERAL, "schemaArray", schemaArray, false, "Which schemas are allowed to be generated", false)
		schemaMaxSize = loadProp(CATEGORY_GENERAL, "schemaMaxSize", schemaMaxSize, false, "Max schema cuboid side length")
		shedLifespan = loadProp(CATEGORY_GENERAL, "shedLifespan", shedLifespan, false, "Shedded items lifespan in ticks")
		storyLines = loadProp(CATEGORY_GENERAL, "storyLines", storyLines, false, "Number of lines for story token")
		timelessProtection = loadProp(CATEGORY_GENERAL, "timelessProtection", timelessProtection, false, "If true, Timeless Ivy won't allow item to break if you have enough mana (instead of post-regen it)")
		wireoverpowered = loadProp(CATEGORY_GENERAL, "wire.overpowered", wireoverpowered, false, "Allow WireSegal far more power than any one person should have")
		worldDestroyConfig = loadProp(CATEGORY_GENERAL, "worldDestroyConfig", emptyArray(), false, "List of world destruction types during Ragnarok in form of string 'dimID:type' (types: 0 - none, 1 - only while ginnungagap, 2 - all)", false).map {
			val (id, type) = it.split(':')
			id.toInt() to type.toInt()
		}.associate { it }
		
		chatLimiters = loadProp(CATEGORY_INTEGRATION, "chatLimiters", chatLimiters, false, "Chat limiters for formtatting special chat lines when using chat plugins")
		poolRainbowCapacity = loadProp(CATEGORY_INTEGRATION, "poolRainbowCapacity", poolRainbowCapacity, false, "Fabulous manapool capacity (for custom modpacks with A LOT of mana usage. Can be applied only to NEW pools)")
		
		addAspectsToBotania = loadProp(CATEGORY_INT_TC, "TC.botaniaAspects", addAspectsToBotania, true, "Set this to false to disable adding aspects to Botania")
		addTincturaAspect = loadProp(CATEGORY_INT_TC, "TC.tinctura", addTincturaAspect, true, "Set this to false to use Sensus instead of Tinctura aspect")
		overrideFMCaps = loadProp(CATEGORY_INT_TC, "TC.overrideFMCaps", overrideFMCaps, true, "[FM] Set this to false to keep Botania metals wand caps from Forbidden Magic recipe")
		thaumTreeSuffusion = loadProp(CATEGORY_INT_TC, "TC.treeCrafting", thaumTreeSuffusion, true, "Set this to false to remove Thaumcraft plants Dendric Suffusion")
		
		materialIDs = loadProp(CATEGORY_INT_TiC, "TiC.materialIDs", materialIDs, true, "IDs for Elementium, Elvorium, Manasteel, Mauftrium, Terrasteel, Livingwood, Dreamwood, Livingrock, Redstring, Manastring materials respectively")
		modifierIDs = loadProp(CATEGORY_INT_TiC, "TiC.modifierIDs", modifierIDs, true, "IDs for ManaCore modifiers respectively")
		
		potionIDBeastWithin = loadProp(CATEGORY_POTIONS, "potionIDBeastWithin", potionIDBeastWithin, true, "Potion id Beast Within")
		potionIDBeer = loadProp(CATEGORY_POTIONS, "potionIDBeer", potionIDBeer, true, "Potion id for Beer")
		potionIDBerserk = loadProp(CATEGORY_POTIONS, "potionIDBerserk", potionIDBerserk, true, "Potion id for Berserk")
		potionIDBleeding = loadProp(CATEGORY_POTIONS, "potionIDBleeding", potionIDBleeding, true, "Potion id for Bleeding")
		potionIDButterShield = loadProp(CATEGORY_MMOP, "potionIDButterShield", potionIDButterShield, true, "Potion id for Butterfly Shield")
		potionIDChampagne = loadProp(CATEGORY_POTIONS, "potionIDChampagne", potionIDChampagne, true, "Potion id for Champagne")
		potionIDDeathMark = loadProp(CATEGORY_MMOP, "potionIDDeathMark", potionIDDeathMark, true, "Potion id for Death Mark")
		potionIDDecay = loadProp(CATEGORY_MMOP, "potionIDDecay", potionIDDecay, true, "Potion id for Decay")
		potionIDEdgeLife = loadProp(CATEGORY_MMOP, "potionIDEdgeLife", potionIDEdgeLife, true, "Potion id for Edge Life")
		potionIDEternity = loadProp(CATEGORY_POTIONS, "potionIDEternity", potionIDEternity, true, "Potion id for Eternity")
		potionIDGoldRush = loadProp(CATEGORY_MMOP, "potionIDGoldRush", potionIDGoldRush, true, "Potion id for Gold Rush")
		potionIDHystrix = loadProp(CATEGORY_MMOP, "potionIDHystrix", potionIDHystrix, true, "Potion id for Hystrix")
		potionIDIceLens = loadProp(CATEGORY_POTIONS, "potionIDIceLens", potionIDIceLens, true, "Potion id for Ice Lens")
		potionIDLeftFlame = loadProp(CATEGORY_MMOP, "potionIDLeftFlame", potionIDLeftFlame, true, "Potion id for Leftover Flame")
		potionIDLightningShield = loadProp(CATEGORY_POTIONS, "potionIDLightningShield", potionIDLightningShield, true, "Potion id for Lightning Shield")
		potionIDManaVoid = loadProp(CATEGORY_POTIONS, "potionIDManaVoid", potionIDManaVoid, true, "Potion id for Mana Void")
		potionIDNineLives = loadProp(CATEGORY_MMOP, "potionIDNineLives", potionIDNineLives, true, "Potion id for Nine Lives")
		potionIDNinja = loadProp(CATEGORY_POTIONS, "potionIDNinja", potionIDNinja, true, "Potion id for Ninja")
		potionIDNoclip = loadProp(CATEGORY_MMOP, "potionIDNoclip", potionIDNoclip, true, "Potion id for Noclip")
		potionIDOvercold = loadProp(CATEGORY_POTIONS, "potionIDOvercold", potionIDOvercold, true, "Potion id for Overcold (only for visual information, set to -1 to disable)")
		potionIDOverheat = loadProp(CATEGORY_POTIONS, "potionIDOverheat", potionIDOverheat, true, "Potion id for Overheat (only for visual information, set to -1 to disable)")
		potionIDOvermage = loadProp(CATEGORY_POTIONS, "potionIDOvermage", potionIDOvermage, true, "Potion id for Overmage")
		potionIDPossession = loadProp(CATEGORY_POTIONS, "potionIDPossession", potionIDPossession, true, "Potion id for Possession")
		potionIDPriorityTarget = loadProp(CATEGORY_MMOP, "potionIDPriorityTarget", potionIDPriorityTarget, true, "Potion id for Priority Target")
		potionIDQuadDamage = loadProp(CATEGORY_MMOP, "potionIDQuadDamage", potionIDQuadDamage, true, "Potion id for Quad Damage")
		potionIDSacrifice = loadProp(CATEGORY_MMOP, "potionIDSacrifice", potionIDSacrifice, true, "Potion id for Sacrifice")
		potionIDShowMana = loadProp(CATEGORY_MMOP, "potionIDShowMana", potionIDShowMana, true, "Potion id for Mana Showing Effect")
		potionIDSoulburn = loadProp(CATEGORY_POTIONS, "potionIDSoulburn", potionIDSoulburn, true, "Potion id for Soulburn")
		potionIDStoneSkin = loadProp(CATEGORY_MMOP, "potionIDStoneSkin", potionIDStoneSkin, true, "Potion id for Stone Skin")
		potionIDTank = loadProp(CATEGORY_POTIONS, "potionIDTank", potionIDTank, true, "Potion id for Tank")
		potionIDThrow = loadProp(CATEGORY_MMOP, "potionIDThrow", potionIDThrow, true, "Potion id for Throw")
		potionIDTimeAnchor = loadProp(CATEGORY_MMOP, "potionIDTimeAnchor", potionIDTimeAnchor, true, "Potion id for Time Anchor")
		potionIDTimeConquest = loadProp(CATEGORY_MMOP, "potionIDTimeConquest", potionIDTimeConquest, true, "Potion id for Time Conquest")
		potionIDVoodooDoll = loadProp(CATEGORY_MMOP, "potionIDVoodooDoll", potionIDVoodooDoll, true, "Potion id for Voodoo Doll")
		potionIDVoodooTarget = loadProp(CATEGORY_MMOP, "potionIDVoodooTarget", potionIDVoodooTarget, true, "Potion id for Voodoo Target")
		potionIDWellOLife = loadProp(CATEGORY_MMOP, "potionIDWellOLife", potionIDWellOLife, true, "Potion id for Well'o'Life")
		potionIDWhiteWine = loadProp(CATEGORY_POTIONS, "potionIDWhiteWine", potionIDWhiteWine, true, "Potion id for White Wine")
		potionIDWisdom = loadProp(CATEGORY_POTIONS, "potionIDWisdom", potionIDWisdom, true, "Potion id for Wisdom")
		potionIDWtfBerry0 = loadProp(CATEGORY_POTIONS, "potionIDWtfBerry0", potionIDWtfBerry0, true, "Potion id for Barrier Berry")
		potionIDWtfBerry2 = loadProp(CATEGORY_POTIONS, "potionIDWtfBerry2", potionIDWtfBerry2, true, "Potion id for Redstone Berry")
		potionIDWtfBerry3 = loadProp(CATEGORY_POTIONS, "potionIDWtfBerry3", potionIDWtfBerry3, true, "Potion id for Lightning Berry")
		potionIDWtfBerry4 = loadProp(CATEGORY_POTIONS, "potionIDWtfBerry4", potionIDWtfBerry4, true, "Potion id for Nether Berry")
		potionIDWtfBerry5 = loadProp(CATEGORY_POTIONS, "potionIDWtfBerry5", potionIDWtfBerry5, true, "Potion id for Sealing Berry")
		
		bonusChest = loadProp(CATEGORY_ESMODE, "bonusChest", bonusChest, false, "Set this to true to generate bonus chest in ESM")
		bothSpawnStructures = loadProp(CATEGORY_ESMODE, "bothSpawnStructures", bothSpawnStructures, false, "Set this to true to generate both race room inside and portal on top of Yggdrasil on zero coords of Alfheim")
		extendedElvenStory = loadProp(CATEGORY_ESMODE, "extendedElvenStory", extendedElvenStory, true, "Set this to true to enable recipes for extended stay in Alfheim")
		flightTime = loadProp(CATEGORY_ESMODE, "flightTime", flightTime, false, "Elven flight fly points (faster you move - more you spend)")
		flightRecover = loadProp(CATEGORY_ESMODE, "flightRecover", flightRecover, false, "Flight recover efficiency")
		wingsBlackList = loadProp(CATEGORY_ESMODE, "wingsBlackList", wingsBlackList, false, "Wings will be unavailable in this dimension(s)", false)
		
		deathScreenAddTime = loadProp(CATEGORY_MMO, "deathScreenAdditionalTime", deathScreenAddTime, false, "Duration of death screen timer (in ticks)")
		disabledSpells = loadProp(CATEGORY_MMO, "disabledSpells", disabledSpells, true, "List of spell name IDs that won't be registered", false)
		disableWireframe = loadProp(CATEGORY_MMO, "disableWireframe", disableWireframe, false, "Set this to true to disable rendering block wireframe in noclip mode")
		friendlyFire = loadProp(CATEGORY_MMO, "friendlyFire", friendlyFire, false, "Set this to true to enable damage to party members")
		legendarySpells = loadProp(CATEGORY_MMO, "legendarySpells", legendarySpells, false, "Spells that are considered 'epic' thus costing same for all races", false)
		maxPartyMembers = loadProp(CATEGORY_MMO, "maxPartyMembers", maxPartyMembers, false, "How many people can be in single party at the same time")
		raceManaMult = loadProp(CATEGORY_MMO, "raceManaMult", raceManaMult.I, false, "Mana cost multiplier for spells with not your affinity").toByte()
		spellSortByAffinity = loadProp(CATEGORY_MMO, "spellSortByAffinity", spellSortByAffinity, true, "Set this to false to remove affinity spells sorting")
		superSpellBosses = loadProp(CATEGORY_MMO, "superSpellBoss", superSpellBosses, false, "Set this to true to make bosses vulnerable to legendary spells")
		
		partyHUDScale = loadProp(CATEGORY_HUD, "partyHUDScale", partyHUDScale, false, "Party HUD Scale (1 < bigger; 1 > smaller)")
		selfHealthUI = loadProp(CATEGORY_HUD, "selfHealthUI", selfHealthUI, false, "Set this to false to hide player's healthbar")
		spellsFadeOut = loadProp(CATEGORY_HUD, "spellsFadeOut", spellsFadeOut, false, "Set this to true to make spell UI fade out when not active")
		targetUI = loadProp(CATEGORY_HUD, "targethUI", targetUI, false, "Set this to false to hide target's healthbar")
	}
	
	fun initWorldCoordsForElvenStory(save: String) {
		if (save.isBlank()) {
			zones.fill(Vector3(0, 300, 0))
			return
		}
		
		val file = File("$save/data/${ModInfo.MODID}/AlfheimCoords.txt")
		if (!file.exists()) makeDefaultWorldCoords(file)
		
		try {
			val fr = FileReader(file)
			val br = BufferedReader(fr)
			for (i in zones.indices) {
				br.readLine()
				try {
					zones[i] = makeVectorFromString(br.readLine())
				} catch (e: IllegalArgumentException) {
					br.close()
					fr.close()
					throw e
				}
			}
			br.close()
			fr.close()
		} catch (e: IOException) {
			ASJUtilities.error("Unable to read Alfheim Coords data. Creating default.", e)
			makeDefaultWorldCoords(file)
		}
	}
	
	private fun makeDefaultWorldCoords(file: File) {
		try {
			file.parentFile.mkdirs()
			val fw = FileWriter(file)
			
			val s = StringBuilder()
			s.append("Salamander start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(0.000))
			s.append("Sylph start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(40.00))
			s.append("Cait Sith start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(80.00))
			s.append("Puca start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(120.0))
			s.append("Gnome start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(160.0))
			s.append("Leprechaun start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(200.0))
			s.append("Spriggan start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(240.0))
			s.append("Undine start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(280.0))
			s.append("Imp start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(320.0))
			fw.write("$s")
			fw.close()
		} catch (e: IOException) {
			ASJUtilities.error("Unable to generate default Alfheim Coords data. Setting all to [0, 300, 0]...", e)
			zones.fill(Vector3(0, 300, 0)) 
		}
	}
	
	private fun writeStandardCoords(angle: Double): String {
		val v = mkVecLenRotMine(citiesDistance, angle)
		return "${v.x.mfloor()} : 300 : ${v.z.mfloor()}\n"
	}
	
	private fun makeVectorFromString(s: String): Vector3 {
		val ss = s.split(" : ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		require(ss.size == 3) { String.format("Wrong coords count. Expected 3 got %d", ss.size) }
		return Vector3(ss[0].toInt(), ss[1].toInt(), ss[2].toInt())
	}
	
	private fun mkVecLenRotMine(length: Int, angle: Double) =
		makeVectorOfLengthRotated(length, angle + 90)
	
	private fun makeVectorOfLengthRotated(length: Int, angle: Double) =
		Vector3(cos(Math.toRadians(angle)) * length, 64.0, sin(Math.toRadians(angle)) * length)
}
