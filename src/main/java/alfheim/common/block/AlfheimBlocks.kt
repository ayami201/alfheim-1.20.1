package alfheim.common.block

// PORT: импорты 1.20.1 — первыми; импорты автора закомментированы до КТ, в которых появятся их блоки
import alfheim.api.lib.LibOreDict
import alfheim.port.legacy.*
import alfheim.port.legacy.OreDictionary.WILDCARD_VALUE
import alfheim.port.legacy.botania.altGrass
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import alexsocol.asjlib.ASJUtilities.setBurnable
//import alexsocol.asjlib.capitalized
import alexsocol.asjlib.extendables.block.BlockPattern
import alfheim.api.*
import alfheim.api.lib.LibOreDict.IRIS_WOOD
import alfheim.common.block.alt.*
import alfheim.common.block.base.*
import alfheim.common.block.colored.*
import alfheim.common.block.colored.rainbow.*
//import alfheim.common.block.corporea.*
//import alfheim.common.block.fluid.BlockManaFluid
//import alfheim.common.block.magtrees.BlockTunedSapling
import alfheim.common.block.magtrees.barrier.*
import alfheim.common.block.magtrees.calico.*
import alfheim.common.block.magtrees.circuit.*
import alfheim.common.block.magtrees.lightning.*
import alfheim.common.block.magtrees.nether.*
import alfheim.common.block.magtrees.sealing.*
//import alfheim.common.block.mana.*
//import alfheim.common.block.schema.*
//import alfheim.common.block.tile.sub.flower.*
//import alfheim.common.core.handler.AlfheimConfigHandler
//import alfheim.common.core.handler.WorkInProgressItemsHandler.WIP
import alfheim.common.core.util.AlfheimTab
//import net.minecraft.block.Block
//import net.minecraft.block.material.Material
//import net.minecraft.init.Blocks
//import net.minecraft.item.ItemStack
//import net.minecraftforge.oredict.OreDictionary.*
//import vazkii.botania.api.BotaniaAPI
//import vazkii.botania.api.subtile.SubTileEntity
//import vazkii.botania.common.block.*
//import vazkii.botania.common.lib.LibBlockNames
//import vazkii.botania.common.lib.LibOreDict as BLibOreDict

// PORT: строки с «// PORT: КТ-n» в конце — блоки, которые переносит КТ-n; пока они закомментированы.
// Блок с вариантами metadata — массив блоков, номер варианта — индекс (SPEC, Р-5)
object AlfheimBlocks {
	
//	val airyVirus: Block // PORT: КТ-2
//	val alfheimPortal: Block // PORT: КТ-6
//	val alfheimPylon: Block // PORT: КТ-3
	val alfStorage: Array<Block>
//	val amplifier: Block // PORT: КТ-2
//	val animatedTorch: Block // PORT: КТ-3
//	val anomaly: Block // PORT: КТ-3
//	val anomalyHarvester: Block // PORT: КТ-3
//	val anyavil: Block // PORT: КТ-3
	val auroraDirt: Block
	val auroraLeaves: Block
	val auroraPlanks: Block
	val auroraSlab: Block
	val auroraSlabFull: Block
	val auroraStairs: Block
	val auroraWood: Block
//	val barrel: Block // PORT: КТ-3
//	val barrier: Block // PORT: КТ-2
//	val bottomlessChest: Block // PORT: КТ-3
//	val corporeaAutocrafter: Block // PORT: КТ-3
//	val corporeaInjector: Block // PORT: КТ-3
//	val corporeaRatBase: Block // PORT: КТ-3
//	val corporeaSparkBase: Block // PORT: КТ-3
//	val dirtDissolvable: Block // PORT: КТ-2
//	val domainDoor: Block // PORT: КТ-6
	val dreamSapling: Block
	val elvenOre: Array<Block>
	val elvenSand: Block
//	val enderActuator: Block // PORT: КТ-3
//	val flugelHeadBlock: Block // PORT: КТ-8
//	val flugelHead2Block: Block // PORT: КТ-8
//	val gaiaButton: Block // PORT: КТ-3
//	val grapesRed: Array<Block> // PORT: КТ-2
//	val grapesRedPlanted: Block // PORT: КТ-2
//	val grapesWhite: Block // PORT: КТ-2
	val icicle: Array<Block>
//	val icyGeyser: Block // PORT: КТ-6
//	val itemDisplay: Block // PORT: КТ-3
	val irisDirt: Array<Block>
	val irisGrass: Array<Block>
	val irisLamp: Block
	val irisLeaves0: Array<Block>
	val irisLeaves1: Array<Block>
	val irisPlanks: Array<Block>
	val irisSapling: Block
	val irisSlabs: Array<Block>
	val irisSlabsFull: Array<Block>
	val irisStairs: Array<Block>
	val irisTallGrass0: Array<Block>
	val irisTallGrass1: Array<Block>
	val irisWood0: Array<Block>
	val irisWood1: Array<Block>
	val irisWood2: Array<Block>
	val irisWood3: Array<Block>
//	val itemFrame: Block // PORT: КТ-3
	val helheimBlock: Block
//	val kindling: Block // PORT: КТ-2
//	val kudzuVine: Block // PORT: КТ-3
	val livingcobble: Array<Block>
//	val livingwoodFunnel: Block // PORT: КТ-3
//	val lootbox: Block // PORT: КТ-6
//	val manaAccelerator: Block // PORT: КТ-3
//	val manaFluidBlock: Block // PORT: КТ-2
	val manaIce: Block
//	val manaInfuser: Block // PORT: КТ-3
//	val manaReflector: Block // PORT: КТ-3
//	val manaTuner: Block // PORT: КТ-3
//	val nidhoggTooth: Block // PORT: КТ-2
	// PORT: вариант metadata — блок массива (SPEC, Р-5), индекс — metadata 1.7.10 (BlockNiflheim.create)
	val niflheimBlock: Array<Block>
//	val niflheimBlock: Block
//	val niflheimPortal: Block // PORT: КТ-6
//	val onyx: Block // PORT: КТ-2
//	val poisonIce: Block // PORT: КТ-2
//	val powerStone: Block // PORT: КТ-3
//	val raceSelector: Block // PORT: КТ-7
	val rainbowDirt: Block
//	val rainbowFlame: Block // PORT: КТ-3
//	val rainbowFlowerFloating: Block // PORT: КТ-3
	val rainbowGrass: Array<Block>
	val rainbowLeaves: Block
	val rainbowMushroom: Block
	val rainbowPlanks: Block
	val rainbowSlab: Block
	val rainbowSlabFull: Block
	val rainbowStairs: Block
	val rainbowTallGrass: Array<Block>
	val rainbowTallFlower: Block
	val rainbowWood: Block
//	val realityAnchor: Block // PORT: КТ-3
//	val redFlame: Block // PORT: КТ-2
//	val redstoneAttractor: Block // PORT: КТ-2
//	val redstoneRelay: Block // PORT: КТ-3
//	val redStringObserver: Block // PORT: КТ-3
//	val redStringWatcher: Block // PORT: КТ-3
//	val rift: Block // PORT: КТ-4
//	val rpc: Block // PORT: КТ-2
	val sadOakLeaves: Block
//	val schemaAnnihilator: Block // PORT: КТ-6
//	val schemaController: Block // PORT: КТ-6
//	val schemaFiller: Block // PORT: КТ-6
//	val schemaGenerator: Block // PORT: КТ-6
//	val schemaMarker: Block // PORT: КТ-6
	val shimmerQuartz: Array<Block>
	val shimmerQuartzSlab: Block
	val shimmerQuartzSlabFull: Block
	val shimmerQuartzStairs: Block
//	val snakeBody: Block // PORT: КТ-2
//	val snakeObject: Block // PORT: КТ-2
	val snowGrass: Block
	val snowLayer: Block
	val softStorage: Array<Block>
//	val spire: Block // PORT: КТ-3
//	val starBlock: Block // PORT: КТ-3
//	val starBlock2: Block // PORT: КТ-3
	val stalactite: Array<Block>
	val stalagmite: Array<Block>
//	val subspacian: Block // PORT: КТ-2
//	val tradePortal: Block // PORT: КТ-3
//	val treeCrafterBlock: Block // PORT: КТ-3
//	val treeCrafterBlockRB: Block // PORT: КТ-3
//	val treeCrafterBlockAU: Block // PORT: КТ-3
//	val worldTree: Block // PORT: КТ-3
//	val yggFlower: Block // PORT: КТ-3
	
	// DENDROLOGY
	
	val altLeaves: Array<Block>
	val altPlanks: Array<Block>
	val altSlabs: Array<Block>
	val altSlabsFull: Array<Block>
	val altStairs: Array<Block>
	val altWood0: Array<Block>
	val altWood1: Array<Block>
	
	val barrierLeaves: Block
	val barrierBerry: Block
	val barrierPlanks: Block
	val barrierSapling: Block
	val barrierSlabs: Block
	val barrierSlabFull: Block
	val barrierStairs: Block
	val barrierWood: Array<Block>
	
	val calicoLeaves: Block
	val calicoBerry: Block
	val calicoPlanks: Block
	val calicoSapling: Block
	val calicoSlabs: Block
	val calicoSlabFull: Block
	val calicoStairs: Block
	val calicoWood: Block
	
	val circuitLeaves: Block
	val circuitBerry: Block
	val circuitPlanks: Block
	val circuitSapling: Block
	val circuitSlabs: Block
	val circuitSlabFull: Block
	val circuitStairs: Block
	val circuitWood: Block
	
	val lightningLeaves: Block
	val lightningBerry: Block
	val lightningPlanks: Block
	val lightningSapling: Block
	val lightningSlabs: Block
	val lightningSlabFull: Block
	val lightningStairs: Block
	val lightningWood: Array<Block>
	
	val netherLeaves: Block
	val netherBerry: Block
	val netherPlanks: Block
	val netherSapling: Block
	val netherSlabs: Block
	val netherSlabFull: Block
	val netherStairs: Block
	val netherWood: Array<Block>
	
	val sealingLeaves: Block
	val sealingBerry: Block
	val sealingPlanks: Block
	val sealingSapling: Block
	val sealingSlabs: Block
	val sealingSlabFull: Block
	val sealingStairs: Block
	val sealingWood: Block
	
//	val tunedSapling: Block // PORT: КТ-2
	
	init {
//		airyVirus = BlockAiryVirus() // PORT: КТ-2
//		alfheimPortal = BlockAlfheimPortal() // PORT: КТ-6
//		alfheimPylon = BlockAlfheimPylon() // PORT: КТ-3
		alfStorage = Array(6) { BlockAlfStorage(it) }
//		amplifier = BlockAmplifier() // PORT: КТ-2
//		animatedTorch = BlockAnimatedTorch() // PORT: КТ-3
//		anomaly = BlockAnomaly() // PORT: КТ-3
//		anomalyHarvester = BlockAnomalyHarvester() // PORT: КТ-3
//		anyavil = BlockAnyavil() // PORT: КТ-3
		auroraDirt = BlockAuroraDirt()
		auroraLeaves = BlockAuroraLeaves()
		auroraPlanks = BlockAuroraPlanks()
		auroraSlab = BlockAuroraWoodSlab(false)
		auroraSlabFull = auroraSlab // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		auroraSlabFull = BlockAuroraWoodSlab(true)
		auroraSlab.register()
//		auroraSlabFull.register()
		auroraStairs = BlockAuroraWoodStairs()
		auroraWood = BlockAuroraWood()
//		barrel = BlockBarrel() // PORT: КТ-3
//		barrier = BlockBarrier() // PORT: КТ-2
//		bottomlessChest = BlockBottomlessChest() // PORT: КТ-3
//		corporeaAutocrafter = BlockCorporeaAutocrafter() // PORT: КТ-3
//		corporeaInjector = BlockCorporeaInjector() // PORT: КТ-3
//		corporeaRatBase = BlockCorporeaRat() // PORT: КТ-3
//		corporeaSparkBase = BlockCorporeaSparkBase() // PORT: КТ-3
//		dirtDissolvable = BlockDirtDissolvable() // PORT: КТ-2
//		domainDoor = BlockDomainDoor() // PORT: КТ-6
		dreamSapling = BlockDreamSapling()
		elvenOre = Array(6) { BlockElvenOre(it) }
		elvenSand = BlockElvenSand()
//		enderActuator = BlockEnderActuator() // PORT: КТ-3
//		flugelHeadBlock = BlockHeadFlugel() // PORT: КТ-8
//		flugelHead2Block = BlockHeadMiku() // PORT: КТ-8
//		gaiaButton = BlockGaiaButton() // PORT: КТ-3
//		grapesRed = Array(3) { BlockGrapeRed(it) } // PORT: КТ-2
//		grapesRedPlanted = BlockGrapeRedPlanted() // PORT: КТ-2
//		grapesWhite = BlockGrapeWhite() // PORT: КТ-2
		icicle = Array(4) { BlockIcicle(it) }
//		icyGeyser = BlockIcyGeyser() // PORT: КТ-6
//		itemDisplay = BlockItemDisplay() // PORT: КТ-3
		irisDirt = Array(16) { BlockColoredDirt(it) }
		irisLamp = BlockColoredLamp()
		irisLeaves0 = Array(8) { BlockColoredLeaves(0, it) }
		irisLeaves1 = Array(8) { BlockColoredLeaves(1, it) }
		irisGrass = Array(16) { BlockColoredGrass(it) }
		irisPlanks = Array(16) { BlockColoredPlanks(it) }
		irisSapling = BlockColoredSapling()
		irisSlabs = Array(16) { BlockColoredWoodSlab(false, it) }
		irisSlabsFull = irisSlabs // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		irisSlabsFull = Array(16) { BlockColoredWoodSlab(true, it) }
		irisSlabs.forEach { (it as BlockSlabMod).register() }
//		irisSlabsFull.forEach { (it as BlockSlabMod).register() }
		irisStairs = Array(16) { BlockColoredWoodStairs(it) }
		irisTallGrass0 = Array(8) { BlockColoredDoubleGrass(0, it) }
		irisTallGrass1 = Array(8) { BlockColoredDoubleGrass(1, it) }
		irisWood0 = Array(4) { BlockColoredWood(0, it) }
		irisWood1 = Array(4) { BlockColoredWood(1, it) }
		irisWood2 = Array(4) { BlockColoredWood(2, it) }
		irisWood3 = Array(4) { BlockColoredWood(3, it) }
//		itemFrame = BlockItemFrame().WIP() // PORT: КТ-3
		helheimBlock = BlockPattern(ModInfo.MODID, Material.rock, "HelheimBlock", AlfheimTab, hardness = -1f, harvLvl = Int.MAX_VALUE, resistance = Float.MAX_VALUE)
//		kindling = BlockKindling() // PORT: КТ-2
//		kudzuVine = BlockKudzuVine() // PORT: КТ-3
		livingcobble = Array(4) { BlockLivingCobble(it) }
//		lootbox = BlockLootbox() // PORT: КТ-6
//		livingwoodFunnel = BlockFunnel() // PORT: КТ-3
//		manaAccelerator = BlockManaAccelerator() // PORT: КТ-3
//		manaFluidBlock = BlockManaFluid() // PORT: КТ-2
		manaIce = BlockManaIce()
//		manaInfuser = BlockManaInfuser() // PORT: КТ-3
//		manaReflector = BlockManaReflector() // PORT: КТ-3
//		manaTuner = BlockManaTuner() // PORT: КТ-3
//		nidhoggTooth = BlockNidhoggTooth() // PORT: КТ-2
		niflheimBlock = BlockNiflheim.create()
//		niflheimBlock = BlockNiflheim()
//		niflheimPortal = BlockNiflheimPortal() // PORT: КТ-6
//		onyx = BlockOnyx().WIP() // PORT: КТ-2
//		poisonIce = BlockNiflheimIce() // PORT: КТ-2
//		powerStone = BlockPowerStone() // PORT: КТ-3
//		raceSelector = BlockRaceSelector() // PORT: КТ-7
		rainbowDirt = BlockRainbowDirt()
//		rainbowFlame = BlockRainbowManaFlame() // PORT: КТ-3
//		rainbowFlowerFloating = BlockFloatingFlowerRainbow() // PORT: КТ-3
		rainbowLeaves = BlockRainbowLeaves()
		rainbowGrass = Array(5) { BlockRainbowGrass(it) }
		rainbowMushroom = BlockRainbowMushroom()
		rainbowPlanks = BlockRainbowPlanks()
		rainbowSlab = BlockRainbowWoodSlab(false)
		rainbowSlabFull = rainbowSlab // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		rainbowSlabFull = BlockRainbowWoodSlab(true)
		rainbowSlab.register()
//		rainbowSlabFull.register()
		rainbowStairs = BlockRainbowWoodStairs()
		rainbowTallGrass = Array(2) { BlockRainbowDoubleGrass(it) }
		rainbowTallFlower = BlockRainbowDoubleFlower()
		rainbowWood = BlockRainbowWood()
//		realityAnchor = BlockRealityAnchor() // PORT: КТ-3
//		redFlame = BlockRedFlame() // PORT: КТ-2
//		redstoneAttractor = BlockRedstoneAttractor() // PORT: КТ-2
//		redstoneRelay = BlockRedstoneRelay() // PORT: КТ-3
//		redStringObserver = BlockRedStringObserver() // PORT: КТ-3
//		redStringWatcher = BlockRedStringWatcher() // PORT: КТ-3
//		rift = BlockRift() // PORT: КТ-4
//		rpc = BlockRealmPowerCollector() // PORT: КТ-2
		sadOakLeaves = BlockSadOakLeaves()
//		schemaAnnihilator = BlockSchemaAnnihilator() // PORT: КТ-6
//		schemaController = BlockSchemaContoller() // PORT: КТ-6
//		schemaFiller = BlockSchemaFiller() // PORT: КТ-6
//		schemaGenerator = BlockSchemaGenerator() // PORT: КТ-6
//		schemaMarker = BlockSchemaMarker() // PORT: КТ-6
		// PORT: варианты кварца — массив (SPEC, Р-5); плита и лестница — из варианта 0 (блок), как в 1.7.10
		shimmerQuartz = Array(3) { BlockShimmerQuartz(it) }
		shimmerQuartzSlab = BlockShimmerQuartzSlab(shimmerQuartz[0] as BlockShimmerQuartz, false)
		shimmerQuartzSlabFull = shimmerQuartzSlab // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		shimmerQuartz = BlockShimmerQuartz()
//		shimmerQuartzSlab = BlockShimmerQuartzSlab(shimmerQuartz, false)
//		shimmerQuartzSlabFull = BlockShimmerQuartzSlab(shimmerQuartz, true)
		shimmerQuartzSlab.register()
//		shimmerQuartzSlabFull.register()
		shimmerQuartzStairs = BlockShimmerQuartzStairs(shimmerQuartz[0] as BlockShimmerQuartz)
//		shimmerQuartzStairs = BlockShimmerQuartzStairs(shimmerQuartz)
//		snakeBody = BlockSnakeBody() // PORT: КТ-2
//		snakeObject = BlockSnakeObject() // PORT: КТ-2
		snowGrass = BlockSnowGrass()
		snowLayer = BlockSnowLayer()
		softStorage = Array(4) { BlockSoftStorage(it) }
//		spire = BlockSpire() // PORT: КТ-3
//		starBlock = BlockStar() // PORT: КТ-3
//		starBlock2 = BlockCracklingStar() // PORT: КТ-3
		stalactite = Array(8) { BlockStalactite(it) }
		stalagmite = Array(8) { BlockStalagmite(it) }
//		subspacian = BlockSubspacian() // PORT: КТ-2
//		tradePortal = BlockTradePortal() // PORT: КТ-3
//		treeCrafterBlock = BlockTreeCrafter("treeCrafter", irisPlanks) // PORT: КТ-3
//		treeCrafterBlockRB = BlockTreeCrafter("treeCrafterRB", rainbowPlanks) // PORT: КТ-3
//		treeCrafterBlockAU = BlockTreeCrafter("treeCrafterAU", auroraPlanks) // PORT: КТ-3
//		worldTree = BlockWorldTree() // PORT: КТ-3
//		yggFlower = BlockYggFlower() // PORT: КТ-3
		
		// DENDOROLOGY
		
		altLeaves = Array(LibOreDict.ALT_TYPES.size) { BlockAltLeaves(it) }
//		altLeaves = BlockAltLeaves()
		altPlanks = Array(LibOreDict.ALT_TYPES.size - 1) { BlockAltPlanks(it) }
//		altPlanks = BlockAltPlanks()
		altSlabs = Array(LibOreDict.ALT_TYPES.size - 1) { BlockAltWoodSlab(false, it) }
		altSlabsFull = altSlabs // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		altSlabs = BlockAltWoodSlab(false)
//		altSlabsFull = BlockAltWoodSlab(true)
		altSlabs.forEach { (it as BlockSlabMod).register() }
//		(altSlabs as BlockSlabMod).register()
//		(altSlabsFull as BlockSlabMod).register()
		altStairs = Array(LibOreDict.ALT_TYPES.size - 1) { if (it == BlockAltLeaves.yggMeta) BlockYggStairs() else BlockAltWoodStairs(it) }
		altWood0 = Array(4) { BlockAltWood(0, it) }
		altWood1 = Array(4) { BlockAltWood(1, it) }
//		altWood0 = BlockAltWood(0)
//		altWood1 = BlockAltWood(1)
		
		barrierLeaves = BlockBarrierLeaves()
		barrierBerry = BlockTreeBerry(barrierLeaves, 0)
		barrierPlanks = BlockBarrierPlanks()
		barrierSapling = BlockBarrierSapling()
		barrierSlabs = BlockBarrierWoodSlab(false)
		barrierSlabFull = barrierSlabs // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		barrierSlabFull = BlockBarrierWoodSlab(true)
		barrierSlabs.register()
//		barrierSlabFull.register()
		barrierStairs = BlockBarrierWoodStairs()
		barrierWood = Array(2) { BlockBarrierWood(it) } // PORT: варианты metadata — блоки массива (SPEC, Р-5): 0 — бревно, 1 — сердцевина
//		barrierWood = BlockBarrierWood()
		
		calicoLeaves = BlockCalicoLeaves()
		calicoBerry = BlockTreeBerry(calicoLeaves, 1)
		calicoPlanks = BlockCalicoPlanks()
		calicoSapling = BlockCalicoSapling()
		calicoSlabs = BlockCalicoWoodSlab(false)
		calicoSlabFull = calicoSlabs // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		calicoSlabFull = BlockCalicoWoodSlab(true)
		calicoSlabs.register()
//		calicoSlabFull.register()
		calicoStairs = BlockCalicoWoodStairs()
		calicoWood = BlockCalicoWood()
		
		circuitLeaves = BlockCircuitLeaves()
		circuitBerry = BlockTreeBerry(circuitLeaves, 2)
		circuitPlanks = BlockCircuitPlanks()
		circuitSapling = BlockCircuitSapling()
		circuitSlabs = BlockCircuitWoodSlab(false)
		circuitSlabFull = circuitSlabs // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		circuitSlabFull = BlockCircuitWoodSlab(true)
		circuitSlabs.register()
//		circuitSlabFull.register()
		circuitStairs = BlockCircuitWoodStairs()
		circuitWood = BlockCircuitWood()
		
		lightningLeaves = BlockLightningLeaves()
		lightningBerry = BlockTreeBerry(lightningLeaves, 3)
		lightningPlanks = BlockLightningPlanks()
		lightningSapling = BlockLightningSapling()
		lightningSlabs = BlockLightningWoodSlab(false)
		lightningSlabFull = lightningSlabs // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		lightningSlabFull = BlockLightningWoodSlab(true)
		lightningSlabs.register()
//		lightningSlabFull.register()
		lightningStairs = BlockLightningWoodStairs()
		lightningWood = Array(2) { BlockLightningWood(it) } // PORT: варианты metadata — блоки массива (SPEC, Р-5): 0 — бревно, 1 — сердцевина
//		lightningWood = BlockLightningWood()
		
		netherLeaves = BlockNetherLeaves()
		netherBerry = BlockTreeBerry(netherLeaves, 4)
		netherPlanks = BlockNetherPlanks()
		netherSapling = BlockNetherSapling()
		netherSlabs = BlockNetherWoodSlab(false)
		netherSlabFull = netherSlabs // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		netherSlabFull = BlockNetherWoodSlab(true)
		netherSlabs.register()
//		netherSlabFull.register()
		netherStairs = BlockNetherWoodStairs()
		netherWood = Array(2) { BlockNetherWood(it) } // PORT: варианты metadata — блоки массива (SPEC, Р-5): 0 — бревно, 1 — сердцевина
//		netherWood = BlockNetherWood()
		
		sealingLeaves = BlockSealingLeaves()
		sealingBerry = BlockTreeBerry(sealingLeaves, 5)
		sealingPlanks = BlockSealingPlanks()
		sealingSapling = BlockSealingSapling()
		sealingSlabs = BlockSealingWoodSlab(false)
		sealingSlabFull = sealingSlabs // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		sealingSlabFull = BlockSealingWoodSlab(true)
		sealingSlabs.register()
//		sealingSlabFull.register()
		sealingStairs = BlockSealingWoodStairs()
		sealingWood = BlockSealingWood()
		
//		tunedSapling = BlockTunedSapling() // PORT: КТ-2
		
//		AlfheimAPI.coldBlocks.addAll(arrayOf(poisonIce)) // PORT: КТ-2
//		AlfheimAPI.warmBlocks.addAll(arrayOf(redFlame, ModBlocks.blazeBlock, netherLeaves, netherBerry, netherSapling, netherPlanks, netherSlabs, netherSlabFull, netherStairs, netherWood)) // PORT: КТ-2
		
		registerBurnables()
		registerPaintables()
		registerFlora()
	}
	
	fun registerPaintables() {
//		BotaniaAPI.registerPaintableBlock(irisWood0) // PORT: КТ-3 (линза краски, H-293)
//		BotaniaAPI.registerPaintableBlock(irisWood1) // PORT: КТ-3 (линза краски, H-293)
//		BotaniaAPI.registerPaintableBlock(irisWood2) // PORT: КТ-3 (линза краски, H-293)
//		BotaniaAPI.registerPaintableBlock(irisWood3) // PORT: КТ-3 (линза краски, H-293)
//		BotaniaAPI.registerPaintableBlock(irisPlanks) // PORT: КТ-3 (линза краски, H-293)
		
//		irisStairs.forEach { // PORT: КТ-3 (линза краски, H-293)
//			BotaniaAPI.registerPaintableBlock(it)
//		}
		
//		irisSlabs.forEach { // PORT: КТ-3 (линза краски, H-293)
//			BotaniaAPI.registerPaintableBlock(it)
//		}
		
//		irisSlabsFull.forEach { // PORT: КТ-3 (линза краски, H-293)
//			BotaniaAPI.registerPaintableBlock(it)
//		}
		
//		BotaniaAPI.registerPaintableBlock(rainbowWood) // PORT: КТ-3 (линза краски, H-293)
//		BotaniaAPI.registerPaintableBlock(rainbowPlanks) // PORT: КТ-3 (линза краски, H-293)
//		BotaniaAPI.registerPaintableBlock(rainbowStairs) // PORT: КТ-3 (линза краски, H-293)
//		BotaniaAPI.registerPaintableBlock(rainbowSlab) // PORT: КТ-3 (линза краски, H-293)
//		BotaniaAPI.registerPaintableBlock(rainbowSlabFull) // PORT: КТ-3 (линза краски, H-293)
		
//		BotaniaAPI.registerPaintableBlock(starBlock) // PORT: КТ-3
//		BotaniaAPI.registerPaintableBlock(starBlock2) // PORT: КТ-3
		
//		BotaniaAPI.registerPaintableBlock(Blocks.dirt) // PORT: КТ-2
//		BotaniaAPI.registerPaintableBlock(irisDirt) // PORT: КТ-3 (линза краски, H-293)
//		BotaniaAPI.registerPaintableBlock(rainbowDirt) // PORT: КТ-3 (линза краски, H-293)
		
//		BotaniaAPI.registerPaintableBlock(ModBlocks.livingrock) // PORT: КТ-3
//		BotaniaAPI.registerPaintableBlock(ModBlocks.dreamwood) // PORT: КТ-3
	}
	
	fun regOreDict() {
//		registerOre("endstone", ItemStack(Blocks.end_stone)) // PORT: КТ-3
		registerOre("grassSnow", ItemStack(snowGrass))
		registerOre("snowLayer", ItemStack(snowLayer))
		registerOre("niflStone", ItemStack(niflheimBlock[0]))
//		registerOre("niflStone", ItemStack(niflheimBlock))
		
//		BotaniaAPI.registerSemiDisposableBlock(BLibOreDict.LIVING_ROCK) // PORT: КТ-3
//		BotaniaAPI.registerSemiDisposableBlock("endstone") // PORT: КТ-3
//		BotaniaAPI.registerDisposableBlock("niflStone") // PORT: КТ-3
		
		// PORT: вариант metadata — отдельный блок, ItemStack(elvenOre, 1, n) → ItemStack(elvenOre[n])
		registerOre(LibOreDict.DRAGON_ORE, ItemStack(elvenOre[0]))
		registerOre(LibOreDict.ELEMENTIUM_ORE, ItemStack(elvenOre[1]))
		registerOre(LibOreDict.ELVEN_QUARTZ_ORE, ItemStack(elvenOre[2]))
		registerOre(LibOreDict.GOLD_ORE, ItemStack(elvenOre[3]))
		registerOre(LibOreDict.GOLD_ORE + "Alfheim", ItemStack(elvenOre[3]))
		registerOre(LibOreDict.IFFESAL_ORE, ItemStack(elvenOre[4]))
		registerOre(LibOreDict.LAPIS_ORE, ItemStack(elvenOre[5]))
		registerOre(LibOreDict.LAPIS_ORE + "Alfheim", ItemStack(elvenOre[5]))
		
		registerOre(LibOreDict.NIFLEUR_ORE, BlockNiflheim.NiflheimBlockMetas.ORE.stack)
		
//		val quartzs = arrayOf(ModFluffBlocks.darkQuartz, ModFluffBlocks.manaQuartz, ModFluffBlocks.blazeQuartz, ModFluffBlocks.lavenderQuartz, ModFluffBlocks.redQuartz, ModFluffBlocks.elfQuartz, ModFluffBlocks.sunnyQuartz) // PORT: КТ-2
		
//		BLibOreDict.QUARTZ.forEachIndexed { id, it -> // PORT: КТ-2
//			registerOre("block${it.capitalized()}", ItemStack(quartzs[id] ?: return@forEachIndexed))
//		}
		// PORT: вариант metadata — блок массива (SPEC, Р-5)
		registerOre(LibOreDict.RAINBOW_QUARTZ_BLOCK, ItemStack(shimmerQuartz[0]))
//		registerOre(LibOreDict.RAINBOW_QUARTZ_BLOCK, ItemStack(shimmerQuartz))
		
		registerOre("sand", ItemStack(elvenSand))
		
		// PORT: вариант metadata — блок массива (SPEC, Р-5); 7, 11, 15 — то же бревно мечтаний с поворотом (meta and 12),
		// поворот — состояние блока
		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1[3], 1))
//		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 3))
//		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 7))
//		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 11))
//		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 15))
		
		// ################
		
		// PORT: вариант metadata — блок массива (SPEC, Р-5)
		registerOre(LibOreDict.RAINBOW_FLOWER, ItemStack(rainbowGrass[2], 1))
//		registerOre(LibOreDict.RAINBOW_FLOWER, ItemStack(rainbowGrass, 1, 2))
		registerOre(LibOreDict.RAINBOW_DOUBLE_FLOWER, ItemStack(rainbowTallFlower))
		
//		registerOre(LibOreDict.MUSHROOM, ItemStack(ModBlocks.mushroom, 1, WILDCARD_VALUE)) // PORT: КТ-3
		registerOre(LibOreDict.MUSHROOM, ItemStack(rainbowMushroom))
		
		registerOre("treeSapling", irisSapling)
		
		registerOre("treeLeaves", ItemStack(lightningLeaves))
		registerOre("plankWood", ItemStack(lightningPlanks))
		registerOre("treeSapling", ItemStack(lightningSapling))
		
		registerOre("slabWood", ItemStack(lightningSlabs))
		registerOre("stairWood", ItemStack(lightningStairs))
		
		registerOre("treeLeaves", ItemStack(calicoLeaves))
		registerOre("plankWood", ItemStack(calicoPlanks))
		registerOre("treeSapling", ItemStack(calicoSapling))
		
		registerOre("slabWood", ItemStack(calicoSlabs))
		registerOre("stairWood", ItemStack(calicoStairs))
		
		registerOre("treeLeaves", ItemStack(circuitLeaves))
		registerOre("plankWood", ItemStack(circuitPlanks))
		registerOre("treeSapling", ItemStack(circuitSapling))
		
		registerOre("slabWood", ItemStack(circuitSlabs))
		registerOre("stairWood", ItemStack(circuitStairs))
		
		registerOre("treeLeaves", ItemStack(netherLeaves))
		registerOre("plankWood", ItemStack(netherPlanks))
		registerOre("treeSapling", ItemStack(netherSapling))
		
		registerOre("slabWood", ItemStack(netherSlabs))
		registerOre("stairWood", ItemStack(netherStairs))
		
		registerOre("treeLeaves", ItemStack(sealingLeaves))
		registerOre("plankWood", ItemStack(sealingPlanks))
		registerOre("treeSapling", ItemStack(sealingSapling))
		
		registerOre("slabWood", ItemStack(sealingSlabs))
		registerOre("stairWood", ItemStack(sealingStairs))
		
		registerOre("treeLeaves", ItemStack(barrierLeaves))
		registerOre("plankWood", ItemStack(barrierPlanks))
		registerOre("treeSapling", ItemStack(barrierSapling))
		
		registerOre("slabWood", ItemStack(barrierSlabs))
		registerOre("stairWood", ItemStack(barrierStairs))
		
		// PORT: вариант metadata — блок массива (SPEC, Р-5): ItemStack(x, 1, i) → ItemStack(x[i], 1)
		for (i in 0..3) {
			registerOre(LibOreDict.WOOD[i], ItemStack(irisWood0[i], 1))
			
			registerOre(LibOreDict.WOOD[i + 4], ItemStack(irisWood1[i], 1))
			
			registerOre(LibOreDict.WOOD[i + 8], ItemStack(irisWood2[i], 1))
			
			registerOre(LibOreDict.WOOD[i + 12], ItemStack(irisWood3[i], 1))
		}
			
		registerOre(LibOreDict.WOOD[16], ItemStack(rainbowWood))
		registerOre(LibOreDict.WOOD[17], ItemStack(auroraWood))
			
		for (i in 0..7) {
			registerOre(LibOreDict.LEAVES[i], ItemStack(irisLeaves0[i], 1))
			registerOre(LibOreDict.LEAVES[i + 8], ItemStack(irisLeaves1[i], 1))
		}
			
		registerOre(LibOreDict.LEAVES[16], ItemStack(rainbowLeaves))
		registerOre(LibOreDict.LEAVES[17], ItemStack(auroraLeaves))
		
		// PORT: вариант metadata — блок массива (SPEC, Р-5): ItemStack(x, 1, i) → ItemStack(x[i], 1)
		for (i in 0..5) {
			registerOre("stairWood", ItemStack(altStairs[i], 1))
			
			registerOre("treeLeaves", ItemStack(altLeaves[i], 1))
//			registerOre("treeLeaves", ItemStack(altLeaves, 1, i))
		}
		
		for (i in 0 until LibOreDict.ALT_TYPES.size - 1) {
			registerOre("slabWood", ItemStack(altSlabs[i], 1))
			
			registerOre("slabWood", ItemStack(altSlabsFull[i], 1))
//			registerOre("slabWood", ItemStack(altSlabs, 1, i))
//			
//			registerOre("slabWood", ItemStack(altSlabsFull, 1, i))
		}
		
		// PORT: WILDCARD_VALUE у блока с вариантами — все блоки его массива (SPEC, Р-5)
		irisDirt.forEach { registerOre(LibOreDict.IRIS_DIRT, ItemStack(it, 1, WILDCARD_VALUE)) }
//		registerOre(LibOreDict.IRIS_DIRT, ItemStack(irisDirt, 1, WILDCARD_VALUE))
		
		// PORT: имена 16 и 17 (Rainbow, Aurora) в 1.7.10 получали несуществующие варианты цветной земли 16 и 17; у этих
		// имён свои блоки — радужная и авроровая земля ниже
		LibOreDict.DIRT.forEachIndexed { id, it ->
			if (id < irisDirt.size) registerOre(it, ItemStack(irisDirt[id], 1))
//			registerOre(it, ItemStack(irisDirt, 1, id))
		}
			
		registerOre(LibOreDict.DIRT[16], ItemStack(rainbowDirt))
		registerOre(LibOreDict.IRIS_DIRT, ItemStack(rainbowDirt))
		registerOre(LibOreDict.DIRT[17], ItemStack(auroraDirt))
		registerOre(LibOreDict.IRIS_DIRT, ItemStack(auroraDirt))
		
		var t: ItemStack
		
		// PORT: блоки с вариантами — массивы, вместе — все варианты (SPEC, Р-5)
		(lightningWood + netherWood + sealingWood + calicoWood + circuitWood + barrierWood + altWood0).forEach {
//		arrayOf(lightningWood, netherWood, sealingWood, calicoWood, circuitWood, barrierWood, altWood0).forEach {
			registerOre("logWood", ItemStack(it, 1, WILDCARD_VALUE))
		}
		
		// PORT: блоки с вариантами — массивы, вместе — все варианты (SPEC, Р-5)
		(irisWood0 + irisWood1 + irisWood2 + irisWood3 + rainbowWood + auroraWood).forEach {
//		arrayOf(irisWood0, irisWood1, irisWood2, irisWood3, rainbowWood, auroraWood).forEach {
			t = ItemStack(it, 1, WILDCARD_VALUE)
			registerOre("logWood", t)
			registerOre(IRIS_WOOD, t)
		}
		
		// PORT: блоки с вариантами — массивы, вместе — все варианты (SPEC, Р-5)
		(irisLeaves0 + irisLeaves1 + rainbowLeaves + auroraLeaves).forEach {
//		arrayOf(irisLeaves0, irisLeaves1, rainbowLeaves, auroraLeaves).forEach {
			t = ItemStack(it, 1, WILDCARD_VALUE)
			registerOre("treeLeaves", t)
			registerOre(LibOreDict.IRIS_LEAVES, t)
		}
			
		irisPlanks.forEach { registerOre("plankWood", ItemStack(it, 1, WILDCARD_VALUE)) }
//		registerOre("plankWood", ItemStack(irisPlanks, 1, WILDCARD_VALUE))
		altPlanks.forEach { registerOre("plankWood", ItemStack(it, 1, WILDCARD_VALUE)) }
//		registerOre("plankWood", ItemStack(altPlanks, 1, WILDCARD_VALUE))
		registerOre("plankWood", ItemStack(rainbowPlanks, 1, WILDCARD_VALUE))
		registerOre("plankWood", ItemStack(auroraPlanks, 1, WILDCARD_VALUE))
		
		irisStairs.forEach {
			registerOre("stairWood", it)
		}
		registerOre("stairWood", rainbowStairs)
		registerOre("stairWood", auroraStairs)
		
		irisSlabs.forEach {
			registerOre("slabWood", ItemStack(it, 1, WILDCARD_VALUE))
		}
		registerOre("slabWood", rainbowSlab)
		registerOre("slabWood", auroraSlab)
		
		// PORT: metadata бревна — вариант (meta and 3) и поворот (meta and 12), листвы — вариант и бит опадания (8); вариант —
		// блок массива, поворот и бит — состояние блока (SPEC, Р-5). Вещи листвы с битом опадания в 1.7.10 не бывало: в
		// treeLeaves — листва 0–7, кроме листвы Иггдрасиля
		for (i in 0..15) {
			if (i !in arrayOf(2, 6, 10, 14)) { // Yggdrasil metas
				t = ItemStack(altWood1[i and 3], 1)
//				t = ItemStack(altWood1, 1, i)
				registerOre("logWood", t)
			}
			
			if (i != BlockAltLeaves.yggMeta && i < altLeaves.size) {
//			if (i != BlockAltLeaves.yggMeta) {
				t = ItemStack(altLeaves[i], 1)
//				t = ItemStack(altLeaves, 1, i)
				registerOre("treeLeaves", t)
			}
			
//			registerOre(LibOreDict.IRIS_DIRT, ItemStack(irisDirt, 1, i))
//			registerOre(LibOreDict.DIRT[i], ItemStack(irisDirt, 1, i))
			
//			registerOre("logWood", ItemStack(lightningWood, 1, i))
//			registerOre("logWood", ItemStack(netherWood, 1, i))
//			registerOre("logWood", ItemStack(sealingWood, 1, i))
//			registerOre("logWood", ItemStack(calicoWood, 1, i))
//			registerOre("logWood", ItemStack(circuitWood, 1, i))
			
//			t = ItemStack(irisWood0, 1, i)
//			registerOre("logWood", t)
//			registerOre(LibOreDict.IRIS_WOOD, t)
//
//			t = ItemStack(irisWood1, 1, i)
//			registerOre("logWood", t)
//			registerOre(LibOreDict.IRIS_WOOD, t)
//
//			t = ItemStack(irisWood2, 1, i)
//			registerOre("logWood", t)
//			registerOre(LibOreDict.IRIS_WOOD, t)
//
//			t = ItemStack(irisWood3, 1, i)
//			registerOre("logWood", t)
//			registerOre(LibOreDict.IRIS_WOOD, t)
//
//			t = ItemStack(rainbowWood, 1, i)
//			registerOre("logWood", t)
//			registerOre(LibOreDict.IRIS_WOOD, t)
//
//			t = ItemStack(auroraWood, 1, i)
//			registerOre("logWood", t)
//			registerOre(LibOreDict.IRIS_WOOD, t)
//
//			t = ItemStack(altWood0, 1, i)
//			registerOre("logWood", t)
			
//			t = ItemStack(irisLeaves0, 1, i)
//			registerOre("treeLeaves", t)
//			registerOre(LibOreDict.IRIS_LEAVES, t)
//
//			t = ItemStack(irisLeaves1, 1, i)
//			registerOre("treeLeaves", t)
//			registerOre(LibOreDict.IRIS_LEAVES, t)
//
//			t = ItemStack(rainbowLeaves, 1, i)
//			registerOre("treeLeaves", t)
//			registerOre(LibOreDict.IRIS_LEAVES, t)
//
//			t = ItemStack(auroraLeaves, 1, i)
//			registerOre("treeLeaves", t)
//			registerOre(LibOreDict.IRIS_LEAVES, t)
			
//			t = ItemStack(irisSlabsFull[i], 1)
//			registerOre("slabWood", t)
		}
	}
	
	fun registerBurnables() {
		// PORT: блок с вариантами metadata — массив блоков (SPEC, Р-5): горит каждый вариант; Иггдрасиль не горит —
		// getFlammability его блоков
		altLeaves.forEach { setBurnable(it, 30, 60) }
		altPlanks.forEach { setBurnable(it, 5, 20) }
		altSlabs.forEach { setBurnable(it, 5, 20) }
		altSlabsFull.forEach { setBurnable(it, 5, 20) }
		altStairs.forEach { setBurnable(it, 5, 20) }
		altWood0.forEach { setBurnable(it, 5, 5) }
		altWood1.forEach { setBurnable(it, 5, 5) }
//		setBurnable(altLeaves, 30, 60)
//		setBurnable(altPlanks, 5, 20)
//		setBurnable(altSlabs, 5, 20)
//		setBurnable(altSlabsFull, 5, 20)
//		setBurnable(altWood0, 5, 5)
//		setBurnable(altWood1, 5, 5)
		
//		setBurnable(amplifier, 5, 20) // PORT: КТ-2
		
		setBurnable(auroraPlanks, 5, 20)
		setBurnable(auroraSlab, 5, 20)
		setBurnable(auroraSlabFull, 5, 20)
		setBurnable(auroraStairs, 5, 20)
		setBurnable(auroraWood, 5, 5)
		
		setBurnable(calicoLeaves, 30, 60)
		setBurnable(calicoPlanks, 5, 20)
		setBurnable(calicoSlabs, 5, 20)
		setBurnable(calicoSlabFull, 5, 20)
		setBurnable(calicoStairs, 5, 20)
		setBurnable(calicoWood, 5, 5)
		
		setBurnable(circuitLeaves, 30, 60)
		setBurnable(circuitPlanks, 5, 20)
		setBurnable(circuitSlabs, 5, 20)
		setBurnable(circuitSlabFull, 5, 20)
		setBurnable(circuitStairs, 5, 20)
		setBurnable(circuitWood, 5, 5)
		
		// PORT: блок с вариантами metadata — массив блоков (SPEC, Р-5): горит каждый вариант
		irisGrass.forEach { setBurnable(it, 60, 100) }
		irisLeaves0.forEach { setBurnable(it, 30, 60) }
		irisLeaves1.forEach { setBurnable(it, 30, 60) }
		irisPlanks.forEach { setBurnable(it, 5, 20) }
		irisSlabs.forEach { setBurnable(it, 5, 20) }
		irisSlabsFull.forEach { setBurnable(it, 5, 20) }
		irisStairs.forEach { setBurnable(it, 5, 20) }
		irisTallGrass0.forEach { setBurnable(it, 60, 100) }
		irisTallGrass1.forEach { setBurnable(it, 60, 100) }
		irisWood0.forEach { setBurnable(it, 5, 5) }
		irisWood1.forEach { setBurnable(it, 5, 5) }
		irisWood2.forEach { setBurnable(it, 5, 5) }
		irisWood3.forEach { setBurnable(it, 5, 5) }
		
		setBurnable(lightningLeaves, 30, 60)
		setBurnable(lightningPlanks, 5, 20)
		setBurnable(lightningSlabs, 5, 20)
		setBurnable(lightningSlabFull, 5, 20)
		setBurnable(lightningStairs, 5, 20)
		// PORT: блок с вариантами metadata — массив блоков (SPEC, Р-5): горит каждый вариант
		lightningWood.forEach { setBurnable(it, 5, 5) }
//		setBurnable(lightningWood, 5, 5)
		
		// PORT: блок с вариантами metadata — массив блоков (SPEC, Р-5): горит каждый вариант
		rainbowGrass.forEach { setBurnable(it, 60, 100) }
//		setBurnable(rainbowGrass, 60, 100)
		setBurnable(rainbowLeaves, 30, 60)
		setBurnable(rainbowPlanks, 5, 20)
		setBurnable(rainbowSlab, 5, 20)
		setBurnable(rainbowSlabFull, 5, 20)
		setBurnable(rainbowStairs, 5, 20)
		rainbowTallGrass.forEach { setBurnable(it, 60, 100) }
//		setBurnable(rainbowTallGrass, 60, 100)
		setBurnable(rainbowWood, 5, 5)
		
		setBurnable(sealingLeaves, 30, 60)
		setBurnable(sealingPlanks, 5, 20)
		setBurnable(sealingSlabs, 5, 20)
		setBurnable(sealingSlabFull, 5, 20)
		setBurnable(sealingStairs, 5, 20)
		setBurnable(sealingWood, 5, 5)
	}
	
	fun registerFlora() {
//		if (AlfheimConfigHandler.gourmaryllisDifficulty > 0) { // PORT: КТ-3
//			BotaniaAPI.subTiles[LibBlockNames.SUBTILE_GOURMARYLLIS] =
//				if (AlfheimConfigHandler.gourmaryllisDifficulty == 1)
//					SubTileGourmaryllisHard::class.java
//				else
//					SubTileGourmaryllisUltra::class.java
//		}
		
//		addSubFlower(SubTileAquapanthus::class.java, "aquapanthus") // PORT: КТ-3
//		addSubFlower(SubTileBudOfYggdrasil::class.java, "budOfYggdrasil") // PORT: КТ-3
//		addSubFlower(SubTileCrysanthermum::class.java, "crysanthermum") // PORT: КТ-3
//		addSubFlower(SubTileOrechidEndium::class.java, "orechidEndium") // PORT: КТ-3
//		addSubFlower(SubTileOrechidAlfarem::class.java, "orechidAlfarem") // PORT: КТ-3
//		addSubFlower(SubTilePetronia::class.java, "petronia") // PORT: КТ-3
//		addSubFlower(SubTileRainFlower::class.java, "rainFlower") // PORT: КТ-3
//		addSubFlower(SubTileRattlerose::class.java, "rattlerose") // PORT: КТ-3
//		addSubFlower(SubTileSnowFlower::class.java, "snowFlower") // PORT: КТ-3
//		addSubFlower(SubTileStormFlower::class.java, "stormFlower") // PORT: КТ-3
//		addSubFlower(SubTileTradescantia::class.java, "tradescantia") // PORT: КТ-3
//		addSubFlower(SubTileWindFlower::class.java, "windFlower") // PORT: КТ-3
//		addSubFlower(SubTileWitherAconite::class.java, "witherAconite") // PORT: КТ-3
		
		AlfheimAPI.addTreeVariant(irisDirt, irisWood0, irisLeaves0, 0, 3)
		AlfheimAPI.addTreeVariant(irisDirt, irisWood1, irisLeaves0, 4, 7)
		AlfheimAPI.addTreeVariant(irisDirt, irisWood2, irisLeaves1, 8, 11, 8)
		AlfheimAPI.addTreeVariant(irisDirt, irisWood3, irisLeaves1, 12, 15, 8)
		// PORT: почва, бревно и листва варианта — массивы блоков-вариантов (AlfheimAPI.addTreeVariant); блок без вариантов —
		// массив из одного блока
		AlfheimAPI.addTreeVariant(arrayOf(rainbowDirt), arrayOf(rainbowWood), arrayOf(rainbowLeaves))
		AlfheimAPI.addTreeVariant(arrayOf(auroraDirt), arrayOf(auroraWood), arrayOf(auroraLeaves))
//		AlfheimAPI.addTreeVariant(rainbowDirt, rainbowWood, rainbowLeaves)
//		AlfheimAPI.addTreeVariant(auroraDirt, auroraWood, auroraLeaves)
		// PORT: трава Botania 1.7.10 с вариантами metadata — массив блоков Botania 1.20.1 по номеру (alfheim.port.legacy.botania)
		AlfheimAPI.addTreeVariant(altGrass, altWood0, altLeaves, 0, 3)
		AlfheimAPI.addTreeVariant(altGrass, altWood1, altLeaves, 4, 5)
//		AlfheimAPI.addTreeVariant(ModBlocks.altGrass, altWood0, altLeaves, 0, 3)
//		AlfheimAPI.addTreeVariant(ModBlocks.altGrass, altWood1, altLeaves, 4, 5)
	}
	
//	fun addSubFlower(clazz: Class<out SubTileEntity>, name: String) { // PORT: КТ-3
//		BotaniaAPI.registerSubTile(name, clazz)
//		BotaniaAPI.registerSubTileSignature(clazz, AlfheimSignature(name))
//		BotaniaAPI.addSubTileToCreativeMenu(name)
//		AlfheimTab.subtiles.add(name)
//	}
	
	fun Block.setHarvestLevelI(toolClass: String, level: Int) = also { it.setHarvestLevel(toolClass, level) }
}