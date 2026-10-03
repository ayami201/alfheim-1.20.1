package alfheim.common.item

// PORT: импорты 1.20.1 — первыми; импорты автора закомментированы до КТ, в которых появятся их предметы
import alfheim.api.lib.LibOreDict
import alfheim.common.item.material.*
import alfheim.common.item.material.ElvenResourcesMetas.*
import alfheim.port.legacy.OreDictionary
import net.minecraft.world.item.*
import vazkii.botania.common.block.BotaniaBlocks as ModBlocks
import vazkii.botania.common.item.BotaniaItems as ModItems
//import alfheim.api.ModInfo
//import alfheim.common.block.AlfheimBlocks
//import alfheim.common.core.handler.WorkInProgressItemsHandler.WIP
//import alfheim.common.core.util.AlfheimTab
//import alfheim.common.item.block.*
//import alfheim.common.item.creator.*
//import alfheim.common.item.equipment.armor.*
//import alfheim.common.item.equipment.armor.elemental.*
//import alfheim.common.item.equipment.armor.elvoruim.*
//import alfheim.common.item.equipment.armor.fenrir.*
//import alfheim.common.item.equipment.bauble.*
//import alfheim.common.item.equipment.bauble.faith.ItemRagnarokEmblem
//import alfheim.common.item.equipment.tool.*
//import alfheim.common.item.equipment.tool.rift.*
//import alfheim.common.item.equipment.tool.terrasteel.ItemTerraHoe
//import alfheim.common.item.interaction.thaumcraft.*
//import alfheim.common.item.relic.*
//import alfheim.common.item.rod.*
//import net.minecraft.init.Items
//import net.minecraft.item.*
//import net.minecraftforge.oredict.OreDictionary
//import vazkii.botania.common.Botania
//import vazkii.botania.common.block.ModBlocks
//import vazkii.botania.common.item.ModItems
//import vazkii.botania.common.item.record.ItemModRecord
//import vazkii.botania.common.item.relic.ItemDice

// PORT: строки с «// PORT: КТ-n» в конце — предметы, которые переносит КТ-n; пока они закомментированы.
// Предмет с вариантами metadata — массив предметов, номер варианта — индекс (SPEC, Р-5)
object AlfheimItems {
	
//	val `DEV-NULL`: Item? // PORT: КТ-4
	
//	val akashicRecords: Item // PORT: КТ-4
//	val aesirCloak: Item // PORT: КТ-4
//	val aesirEmblem: Item // PORT: КТ-4
//	val armilla: Item // PORT: КТ-2
//	val astrolabe: Item // PORT: КТ-4
//	val attributionBauble: Item // PORT: КТ-4
//	val auraRingElven: Item // PORT: КТ-4
//	val auraRingGod: Item // PORT: КТ-4
//	val auraRingPink: Item // PORT: КТ-4
//	val balanceCloak: Item // PORT: КТ-4
//	val carver: Item // PORT: КТ-3
//	val chalk: Item // PORT: КТ-2
//	val cloudPendant: Item // PORT: КТ-4
//	val cloudPendantSuper: Item // PORT: КТ-4
//	val coatOfArms: Item // PORT: КТ-4
//	val colorOverride: Item // PORT: КТ-4
//	val corporeaRat: Item // PORT: КТ-3
//	val creativeReachPendant: Item // PORT: КТ-4
//	val crescentMoonAmulet: Item // PORT: КТ-4
//	val daolos: Item // PORT: КТ-4
//	val deathSeed: Item // PORT: КТ-2
//	val discFenrir: Item // PORT: КТ-8
//	val discFlugel: Item // PORT: КТ-8
//	val discFlugelMeme: Item // PORT: КТ-8
//	val discFlugelUltra: Item // PORT: КТ-8
//	val discSurtr: Item // PORT: КТ-8
//	val discThrym: Item // PORT: КТ-8
//	val dodgeRing: Item // PORT: КТ-4
//	val elementalBoots: Item // PORT: КТ-4
//	val elementalChestplate: Item // PORT: КТ-4
//	val elementalHelmet: Item // PORT: КТ-4
//	val elementalHelmetRevealing: Item? // PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//	val elementalLeggings: Item // PORT: КТ-4
//	val elfFirePendant: Item // PORT: КТ-4
//	val elfIcePendant: Item // PORT: КТ-4
//	val elvenChakram: Item // PORT: КТ-4
//	val elvenDisguise: Item // PORT: КТ-4
	val elvenFood: Array<Item>
	val elvenResource: Array<Item>
//	val elvoriumBoots: Item // PORT: КТ-4
//	val elvoriumChestplate: Item // PORT: КТ-4
//	val elvoriumHelmet: Item // PORT: КТ-4
//	val elvoriumHelmetRevealing: Item? // PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//	val elvoriumLeggings: Item // PORT: КТ-4
//	val enlighter: Item // PORT: КТ-4
	val eventResource: Array<Item>
//	val excaliber: Item // PORT: КТ-4
//	val fenrirBoots: Item // PORT: КТ-4
//	val fenrirChestplate: Item // PORT: КТ-4
//	val fenrirClaws: Item // PORT: КТ-4
//	val fenrirCloak: Item // PORT: КТ-4
//	val fenrirGlove: Item // PORT: КТ-4
//	val fenrirHelmet: Item // PORT: КТ-4
//	val fenrirHelmetRevealing: Item? // PORT: КТ-4
//	val fenrirLeggings: Item // PORT: КТ-4
//	val fenrirLoot: Item // PORT: КТ-8
//	val fireGrenade: Item // PORT: КТ-2
//	val floatingIslandGenerator: Item // PORT: КТ-6
//	val flugelHead: Item // PORT: КТ-8
//	val flugelHead2: Item // PORT: КТ-8
//	val flugelSoul: Item // PORT: КТ-4
//	val gaiaSlayer: Item // PORT: КТ-4
//	val goddesCharm: Item // PORT: КТ-4
//	val gjallarhorn: Item // PORT: КТ-4
//	val gleipnir: Item // PORT: КТ-4
//	val gungnir: Item // PORT: КТ-4
//	val hyperBucket: Item // PORT: КТ-2
//	val invisibilityCloak: Item // PORT: КТ-4
//	val invisibleFlameLens: Item // PORT: КТ-3
//	val irisSeeds: Item // PORT: КТ-2
//	val livingrockPickaxe: Item // PORT: КТ-4
//	val lootInterceptor: Item // PORT: КТ-4
//	val manaGlove: Item // PORT: КТ-4
//	val manaMirrorImba: Item // PORT: КТ-4
//	val manaRingElven: Item // PORT: КТ-4
//	val manaRingGod: Item // PORT: КТ-4
//	val manaRingPink: Item // PORT: КТ-4
//	val manaStone: Item // PORT: КТ-4
//	val manaStoneGreater: Item // PORT: КТ-4
//	val mask: Item // PORT: КТ-4
//	val mjolnir: Item // PORT: КТ-4
//	val moonlightBow: Item // PORT: КТ-4
//	val multibauble: Item // PORT: КТ-4
//	val organs: Item // PORT: КТ-8
//	val paperBreak: Item // PORT: КТ-7
//	val paperRace: Item // PORT: КТ-7
//	val peacePipe: Item // PORT: КТ-7
//	val pendantSuperIce: Item // PORT: КТ-4
//	val pixieAttractor: Item // PORT: КТ-4
//	val priestCloak: Item // PORT: КТ-4
//	val priestEmblem: Item // PORT: КТ-4
//	val priestRingHeimdall: Item // PORT: КТ-4
//	val priestRingNjord: Item // PORT: КТ-4
//	val priestRingSif: Item // PORT: КТ-4
//	val ragnarokEmblem: Item // PORT: КТ-4
//	val ragnarokEmblemF: Item // PORT: КТ-4
//	val rationBelt: Item // PORT: КТ-4
//	val realitySword: Item // PORT: КТ-4
//	val resonator: Item // PORT: КТ-4
//	val riftPick: Item // PORT: КТ-4
//	val riftSword: Item // PORT: КТ-4
//	val ringFeedFlower: Item // PORT: КТ-4
//	val ringSpider: Item // PORT: КТ-4
//	val rodBlackHole: Item // PORT: КТ-4
//	val rodColorfulSkyDirt: Item // PORT: КТ-4
//	val rodClicker: Item // PORT: КТ-4
//	val rodFlameStar: Item // PORT: КТ-4
//	val rodGrass: Item // PORT: КТ-4
//	val rodInterdiction: Item // PORT: КТ-4
//	val rodLightning: Item // PORT: КТ-4
//	val rodMuspelheim: Item // PORT: КТ-4
//	val rodNiflheim: Item // PORT: КТ-4
//	val rodPortal: Item // PORT: КТ-4
//	val rodPrismatic: Item // PORT: КТ-4
//	val rodRedstone: Item // PORT: КТ-4
//	val rodSuperExchange: Item // PORT: КТ-4
//	val serenade: Item // PORT: КТ-4
//	val snowSword: Item // PORT: КТ-4
//	val snowHelmet: Item // PORT: КТ-4
//	val snowHelmetRevealing: Item? // PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//	val snowChest: Item // PORT: КТ-4
//	val snowLeggings: Item // PORT: КТ-4
//	val snowBoots: Item // PORT: КТ-4
//	val soulHorn: Item // PORT: КТ-2
//	val soulSword: Item // PORT: КТ-4
//	val spatiotemporalRing: Item // PORT: КТ-4
//	val spawnEgg: Item // PORT: КТ-5
//	val splashPotion: Item // PORT: КТ-2
//	val starPlacer: Item // PORT: КТ-3
//	val starPlacer2: Item // PORT: КТ-3
	//val storyToken: Item
//	val subspaceSpear: Item // PORT: КТ-4
//	val surtrSword: Item // PORT: КТ-4
//	val terraHoe: Item // PORT: КТ-4
//	val thinkingHand: Item // PORT: КТ-4
//	val thrymAxe: Item // PORT: КТ-4
//	val toolbelt: Item // PORT: КТ-4
//	val trisDagger: Item // PORT: КТ-4
//	val triquetrum: Item // PORT: КТ-2
//	val volcanoMace: Item // PORT: КТ-4
//	val volcanoHelmet: Item // PORT: КТ-4
//	val volcanoHelmetRevealing: Item? // PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//	val volcanoChest: Item // PORT: КТ-4
//	val volcanoLeggings: Item // PORT: КТ-4
//	val volcanoBoots: Item // PORT: КТ-4
//	val warBanner: Item // PORT: КТ-4
	val wiltedLotus: Array<Item>
//	val wireAxe: Item // PORT: КТ-4
	
//	val royalStaff: Item // PORT: КТ-4
	
	init {
//		akashicRecords = ItemAkashicRecords() // PORT: КТ-4
//		aesirCloak = ItemAesirCloak() // PORT: КТ-4
//		aesirEmblem = ItemAesirEmblem() // PORT: КТ-4
//		armilla = ItemArmilla() // PORT: КТ-2
//		astrolabe = ItemAstrolabe() // PORT: КТ-4
//		attributionBauble = ItemAttributionBauble() // PORT: КТ-4
//		auraRingElven = ItemAuraRingAlfheim("AuraRingElven") // PORT: КТ-4
//		auraRingGod = ItemAuraRingAlfheim("AuraRingGod", 2) // PORT: КТ-4
//		auraRingPink = ItemAuraRingAlfheim("AuraRingPink", 50, 0.075f) // PORT: КТ-4
//		balanceCloak = ItemBalanceCloak() // PORT: КТ-4
//		carver = ItemCarver() // PORT: КТ-3
//		chalk = ItemChalk() // PORT: КТ-2
//		cloudPendant = ItemCloudPendant() // PORT: КТ-4
//		cloudPendantSuper = ItemCloudPendant("SuperCloudPendant", 3) // PORT: КТ-4
//		coatOfArms = ItemCoatOfArms() // PORT: КТ-4
//		colorOverride = ItemColorOverride() // PORT: КТ-4
//		corporeaRat = ItemCorporeaRat() // PORT: КТ-3
//		creativeReachPendant = ItemCreativeReachPendant() // PORT: КТ-4
//		crescentMoonAmulet = ItemCrescentMoonAmulet() // PORT: КТ-4
//		daolos = ItemDaolos() // PORT: КТ-4
//		deathSeed = ItemDeathSeed() // PORT: КТ-2
//		discFenrir = ItemModRecord("fenrir", "FenrirDisc").setCreativeTab(AlfheimTab) // PORT: КТ-8
//		discFlugel = ItemModRecord("flugel", "FlugelDisc").setCreativeTab(AlfheimTab) // PORT: КТ-8
//		discFlugelMeme = ItemModRecord("miku", "MikuDisc").setCreativeTab(null) // PORT: КТ-8
//		discFlugelUltra = ItemModRecord("flugel_ultra", "FlugelUltraDisc").setCreativeTab(AlfheimTab) // PORT: КТ-8
//		discSurtr = ItemModRecord("surtr", "SurtrDisc").setCreativeTab(AlfheimTab) // PORT: КТ-8
//		discThrym = ItemModRecord("thrym", "ThrymDisc").setCreativeTab(AlfheimTab) // PORT: КТ-8
//		dodgeRing = ItemDodgeRing() // PORT: КТ-4
//		fireGrenade = ItemFireGrenade() // PORT: КТ-2
//		elementalHelmet = ItemElementalWaterHelm() // PORT: КТ-4
//		elementalHelmetRevealing = if (Botania.thaumcraftLoaded) ItemElementalWaterHelmRevealing() else null // PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//		elementalChestplate = ItemElementalEarthChest() // PORT: КТ-4
//		elementalLeggings = ItemElementalFireLeggings() // PORT: КТ-4
//		elementalBoots = ItemElementalAirBoots() // PORT: КТ-4
//		elfFirePendant = ItemPendant("FirePendant") // PORT: КТ-4
//		elfIcePendant = ItemPendant("IcePendant") // PORT: КТ-4
//		elvenChakram = ItemElvenChakram() // PORT: КТ-4
//		elvenDisguise = ItemElvenDisguise() // PORT: КТ-4
		elvenFood = Array(ElvenFoodMetas.entries.size) { ItemElvenFood(it) }
		elvenResource = Array(ElvenResourcesMetas.entries.size) { ItemElvenResource(it) }
//		elvoriumHelmet = ItemElvoriumHelmet() // PORT: КТ-4
//		elvoriumHelmetRevealing = if (Botania.thaumcraftLoaded) ItemElvoriumHelmetRevealing() else null // PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//		elvoriumChestplate = ItemElvoriumArmor(1, "ElvoriumChestplate") // PORT: КТ-4
//		elvoriumLeggings = ItemElvoriumArmor(2, "ElvoriumLeggings") // PORT: КТ-4
//		elvoriumBoots = ItemElvoriumArmor(3, "ElvoriumBoots") // PORT: КТ-4
//		enlighter = ItemEnlighter() // PORT: КТ-4
		eventResource = Array(ItemEventResource.subItems.size) { ItemEventResource(it) }
//		excaliber = ItemExcaliber() // PORT: КТ-4
//		fenrirHelmet = ItemFenrirArmor(0, "FenrirHelmet") // PORT: КТ-4
//		fenrirHelmetRevealing = if (Botania.thaumcraftLoaded) ItemFenrirHelmetRevealing() else null // PORT: КТ-4
//		fenrirChestplate = ItemFenrirArmor(1, "FenrirChestplate") // PORT: КТ-4
//		fenrirLeggings = ItemFenrirArmor(2, "FenrirLeggings") // PORT: КТ-4
//		fenrirBoots = ItemFenrirBoots() // PORT: КТ-4
//		fenrirClaws = ItemFenrirClaws() // PORT: КТ-4
//		fenrirCloak = ItemFenrirCloak() // PORT: КТ-4
//		fenrirGlove = ItemFenrirGlove() // PORT: КТ-4
//		fenrirLoot = ItemFenrirLoot() // PORT: КТ-8
//		floatingIslandGenerator = ItemFloatingIslandGenerator() // PORT: КТ-6
//		flugelHead = ItemHeadFlugel() // PORT: КТ-8
//		flugelHead2 = ItemHeadMiku() // PORT: КТ-8
//		flugelSoul = ItemFlugelSoul() // PORT: КТ-4
//		gaiaSlayer = ItemGaiaSlayer() // PORT: КТ-4
//		goddesCharm = ItemGoddessCharm() // PORT: КТ-4
//		gjallarhorn = ItemGjallarhorn() // PORT: КТ-4
//		gleipnir = ItemGleipnir() // PORT: КТ-4
//		gungnir = ItemGungnir() // PORT: КТ-4
//		hyperBucket = ItemHyperBucket() // PORT: КТ-2
//		invisibilityCloak = ItemInvisibilityCloak() // PORT: КТ-4
//		invisibleFlameLens = ItemLensFlashInvisible() // PORT: КТ-3
//		irisSeeds = ItemColorSeeds() // PORT: КТ-2
//		livingrockPickaxe = ItemLivingrockPickaxe() // PORT: КТ-4
//		lootInterceptor = ItemLootInterceptor() // PORT: КТ-4
//		manaGlove = ItemManaweaveGlove() // PORT: КТ-4
//		manaMirrorImba = ItemManaMirrorImba() // PORT: КТ-4
//		manaRingElven = ItemManaStorageRing("ManaRingElven", 5.0) // PORT: КТ-4
//		manaRingGod = ItemManaStorageRing("ManaRingGod", 10.0) // PORT: КТ-4
//		manaRingPink = ItemManaStorageRing("ManaRingPink", 1.0, 0.075f) // PORT: КТ-4
//		manaStone = ItemManaStorage("ManaStone", 3.0) // PORT: КТ-4
//		manaStoneGreater = ItemManaStorage("ManaStoneGreater", 8.0) // PORT: КТ-4
//		mask = ItemTankMask() // PORT: КТ-4
//		mjolnir = ItemMjolnir() // PORT: КТ-4
//		moonlightBow = ItemMoonlightBow() // PORT: КТ-4
//		multibauble = ItemMultibauble() // PORT: КТ-4
//		organs = ItemOrgans() // PORT: КТ-8
//		paperBreak = ItemPaperBreak() // PORT: КТ-7
//		paperRace = ItemPaperRace() // PORT: КТ-7
//		peacePipe = ItemPeacePipe() // PORT: КТ-7
//		pendantSuperIce = ItemSuperIcePendant() // PORT: КТ-4
//		pixieAttractor = ItemPendant("PixieAttractor") // PORT: КТ-4
//		priestCloak = ItemPriestCloak() // PORT: КТ-4
//		priestEmblem = ItemPriestEmblem() // PORT: КТ-4
//		priestRingHeimdall = ItemHeimdallRing() // PORT: КТ-4
//		priestRingNjord = ItemNjordRing() // PORT: КТ-4
//		priestRingSif = ItemSifRing() // PORT: КТ-4
//		ragnarokEmblem = ItemRagnarokEmblem() // PORT: КТ-4
//		ragnarokEmblemF = ItemRagnarokEmblemF() // PORT: КТ-4
//		rationBelt = ItemRationBelt() // PORT: КТ-4
//		realitySword = ItemRealitySword() // PORT: КТ-4
//		resonator = ItemResonator() // PORT: КТ-4
//		riftPick = ItemRiftPick().WIP() // PORT: КТ-4
//		riftSword = ItemRiftSword().WIP() // PORT: КТ-4
//		ringFeedFlower = ItemFeedFlowerRing() // PORT: КТ-4
//		ringSpider = ItemSpiderRing() // PORT: КТ-4
//		rodBlackHole = ItemRodBlackHole() // PORT: КТ-4
//		rodColorfulSkyDirt = ItemRodIridescent() // PORT: КТ-4
//		rodClicker = ItemRodClicker() // PORT: КТ-4
//		rodGrass = ItemRodGrass() // PORT: КТ-4
//		rodFlameStar = ItemRodFlameStar() // PORT: КТ-4
//		rodInterdiction = ItemRodInterdiction() // PORT: КТ-4
//		rodLightning = ItemRodLightning() // PORT: КТ-4
//		rodMuspelheim = ItemRodElemental("MuspelheimRod") { AlfheimBlocks.redFlame } // PORT: КТ-4
//		rodNiflheim = ItemRodElemental("NiflheimRod") { AlfheimBlocks.poisonIce } // PORT: КТ-4
//		rodPortal = ItemRodPortal() // PORT: КТ-4
//		rodPrismatic = ItemRodPrismatic() // PORT: КТ-4
//		rodRedstone = ItemRedstoneRod() // PORT: КТ-4
//		rodSuperExchange = ItemRodSuperExchange() // PORT: КТ-4
//		serenade = ItemSerenade() // PORT: КТ-4
//		snowSword = ItemSnowSword() // PORT: КТ-4
//		snowHelmet = ItemSnowArmor(0, "SnowHelmet") // PORT: КТ-4
//		snowHelmetRevealing = if (Botania.thaumcraftLoaded) ItemSnowHelmetRevealing() else null // PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//		snowChest = ItemSnowArmor(1, "SnowChest") // PORT: КТ-4
//		snowLeggings = ItemSnowArmor(2, "SnowLeggings") // PORT: КТ-4
//		snowBoots = ItemSnowArmor(3, "SnowBoots") // PORT: КТ-4
//		soulHorn = ItemSoulHorn() // PORT: КТ-2
//		soulSword = ItemSoulSword() // PORT: КТ-4
//		spatiotemporalRing = ItemSpatiotemporalRing() // PORT: КТ-4
//		splashPotion = ItemSplashPotion() // PORT: КТ-2
//		spawnEgg = ItemSpawnEgg() // PORT: КТ-5
//		starPlacer = ItemStarPlacer() // PORT: КТ-3
//		starPlacer2 = ItemStarPlacer2() // PORT: КТ-3
		//storyToken = ItemStoryToken()
//		subspaceSpear = ItemSpearSubspace() // PORT: КТ-4
//		surtrSword = ItemSurtrSword() // PORT: КТ-4
//		terraHoe = ItemTerraHoe() // PORT: КТ-4
//		thinkingHand = ItemThinkingHand() // PORT: КТ-4
//		thrymAxe = ItemThrymAxe() // PORT: КТ-4
//		trisDagger = ItemTrisDagger() // PORT: КТ-4
//		triquetrum = ItemTriquetrum() // PORT: КТ-2
//		toolbelt = ItemToolBelt() // PORT: КТ-4
//		volcanoMace = ItemVolcanoMace() // PORT: КТ-4
//		volcanoHelmet = ItemVolcanoArmor(0, "VolcanoHelmet") // PORT: КТ-4
//		volcanoHelmetRevealing = if (Botania.thaumcraftLoaded) ItemVolcanoHelmetRevealing() else null // PORT: выпало — Thaumcraft отсутствует на 1.20.1 (SPEC, п. 7)
//		volcanoChest = ItemVolcanoArmor(1, "VolcanoChest") // PORT: КТ-4
//		volcanoLeggings = ItemVolcanoArmor(2, "VolcanoLeggings") // PORT: КТ-4
//		volcanoBoots = ItemVolcanoArmor(3, "VolcanoBoots") // PORT: КТ-4
//		warBanner = ItemWarBanner() // PORT: КТ-4
//		wireAxe = ItemWireAxe() // PORT: КТ-4
		wiltedLotus = Array(2) { ItemWiltedLotus(it) }
		
//		royalStaff = ItemRoyalStaff() // PORT: КТ-4
//		`DEV-NULL` = if (ModInfo.DEV) TheRodOfTheDebug() else null // PORT: КТ-4
		
		// PORT: КТ-4 — реликвии в кубике Botania (ItemDice)
//		// that's ok because there is check on first 6 array elements in the dice
//		ItemDice.relicStacks += arrayOf(ItemStack(flugelSoul),
//										ItemStack(mask),
//										ItemStack(excaliber),
//										ItemStack(subspaceSpear),
//										ItemStack(moonlightBow),
//										ItemStack(gleipnir),
//										ItemStack(mjolnir),
//										ItemStack(daolos),
//										ItemStack(gungnir),
//										ItemStack(gjallarhorn),
//										ItemStack(priestRingHeimdall),
//										ItemStack(priestRingNjord),
//										ItemStack(priestRingSif),
//										ItemStack(akashicRecords))
	}
	
	fun regOreDict() {
		OreDictionary.registerOre(LibOreDict.ELVORIUM_INGOT, ElvoriumIngot.stack)
		OreDictionary.registerOre(LibOreDict.MAUFTRIUM_INGOT, MauftriumIngot.stack)
		OreDictionary.registerOre(LibOreDict.MUSPELHEIM_POWER_INGOT, MuspelheimPowerIngot.stack)
		OreDictionary.registerOre(LibOreDict.NIFLHEIM_POWER_INGOT, NiflheimPowerIngot.stack)
		OreDictionary.registerOre(LibOreDict.ELVORIUM_NUGGET, ElvoriumNugget.stack)
		OreDictionary.registerOre(LibOreDict.MAUFTRIUM_NUGGET, MauftriumNugget.stack)
		OreDictionary.registerOre(LibOreDict.MUSPELHEIM_ESSENCE, MuspelheimEssence.stack)
		OreDictionary.registerOre(LibOreDict.NIFLHEIM_ESSENCE, NiflheimEssence.stack)
		OreDictionary.registerOre(LibOreDict.IFFESAL_DUST, IffesalDust.stack)
		OreDictionary.registerOre(LibOreDict.FENRIR_FUR, FenrirFur.stack)
		OreDictionary.registerOre(LibOreDict.ARUNE[0], PrimalRune.stack)
		OreDictionary.registerOre(LibOreDict.ARUNE[1], MuspelheimRune.stack)
		OreDictionary.registerOre(LibOreDict.ARUNE[2], NiflheimRune.stack)
		OreDictionary.registerOre(LibOreDict.INFUSED_DREAM_TWIG, InfusedDreamwoodTwig.stack)
		OreDictionary.registerOre("slimeball", ElementalSlimeBall.stack)
		
		// Iridescense
		
		OreDictionary.registerOre(LibOreDict.TWIG_THUNDERWOOD, ThunderwoodTwig.stack)
		OreDictionary.registerOre(LibOreDict.SPLINTERS_THUNDERWOOD, ThunderwoodSplinters.stack)
		OreDictionary.registerOre(LibOreDict.TWIG_NETHERWOOD, NetherwoodTwig.stack)
		OreDictionary.registerOre(LibOreDict.SPLINTERS_NETHERWOOD, NetherwoodSplinters.stack)
		OreDictionary.registerOre(LibOreDict.COAL_NETHERWOOD, NetherwoodCoal.stack)
		OreDictionary.registerOre(LibOreDict.DYES(LibOreDict.Color.Rainbow), RainbowDust.stack)
		OreDictionary.registerOre(LibOreDict.FLORAL_POWDER, RainbowDust.stack)
		OreDictionary.registerOre(LibOreDict.RAINBOW_PETAL, RainbowPetal.stack)
		OreDictionary.registerOre(LibOreDict.RAINBOW_QUARTZ, RainbowQuartz.stack)
		OreDictionary.registerOre(LibOreDict.PETAL_ANY, RainbowPetal.stack)
		
//		OreDictionary.registerOre(LibOreDict.HOLY_PENDANT, ItemStack(attributionBauble, 1, OreDictionary.WILDCARD_VALUE)) // PORT: КТ-4
		
		OreDictionary.registerOre(LibOreDict.DYES(LibOreDict.Color.Rainbow), ItemStack(ModBlocks.bifrostPerm))
		// PORT: любая metadata (WILDCARD_VALUE) — все 16 предметов; цветочной пыли (ModItems.dye) в Botania 1.20.1 нет — лепестки
		// дают краситель ванилы, он и стал цветочной пылью
		for (color in DyeColor.entries) OreDictionary.registerOre(LibOreDict.FLORAL_POWDER, ItemStack(DyeItem.byColor(color)))
		for (color in DyeColor.entries) OreDictionary.registerOre(LibOreDict.PETAL_ANY, ItemStack(ModItems.getPetal(color)))
//		OreDictionary.registerOre(LibOreDict.FLORAL_POWDER, ItemStack(ModItems.dye, 1, OreDictionary.WILDCARD_VALUE))
//		OreDictionary.registerOre(LibOreDict.PETAL_ANY, ItemStack(ModItems.petal, 1, OreDictionary.WILDCARD_VALUE))
		
		
		
		// PORT: уголь metadata 1 — древесный уголь
		OreDictionary.registerOre("coal", ItemStack(Items.COAL))
		OreDictionary.registerOre("coal", ItemStack(Items.CHARCOAL))
//		OreDictionary.registerOre("coal", ItemStack(Items.coal))
//		OreDictionary.registerOre("coal", ItemStack(Items.coal, 1, 1))
	}
}
