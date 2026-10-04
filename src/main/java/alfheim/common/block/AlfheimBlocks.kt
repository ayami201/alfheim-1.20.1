package alfheim.common.block

// PORT: импорты 1.20.1 — первыми; импорты автора закомментированы до КТ, в которых появятся их блоки
import alfheim.api.lib.LibOreDict
import alfheim.port.legacy.*
import alfheim.port.legacy.OreDictionary.WILDCARD_VALUE
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import alexsocol.asjlib.ASJUtilities.setBurnable
//import alexsocol.asjlib.capitalized
//import alexsocol.asjlib.extendables.block.BlockPattern
import alfheim.api.*
import alfheim.api.lib.LibOreDict.IRIS_WOOD
//import alfheim.common.block.alt.*
import alfheim.common.block.base.*
import alfheim.common.block.colored.*
//import alfheim.common.block.colored.rainbow.*
//import alfheim.common.block.corporea.*
//import alfheim.common.block.fluid.BlockManaFluid
//import alfheim.common.block.magtrees.BlockTunedSapling
//import alfheim.common.block.magtrees.barrier.*
//import alfheim.common.block.magtrees.calico.*
//import alfheim.common.block.magtrees.circuit.*
//import alfheim.common.block.magtrees.lightning.*
//import alfheim.common.block.magtrees.nether.*
//import alfheim.common.block.magtrees.sealing.*
//import alfheim.common.block.mana.*
//import alfheim.common.block.schema.*
//import alfheim.common.block.tile.sub.flower.*
//import alfheim.common.core.handler.AlfheimConfigHandler
//import alfheim.common.core.handler.WorkInProgressItemsHandler.WIP
//import alfheim.common.core.util.AlfheimTab
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
//	val auroraDirt: Block // PORT: КТ-2
//	val auroraLeaves: Block // PORT: КТ-2
//	val auroraPlanks: Block // PORT: КТ-2
//	val auroraSlab: Block // PORT: КТ-2
//	val auroraSlabFull: Block // PORT: КТ-2
//	val auroraStairs: Block // PORT: КТ-2
//	val auroraWood: Block // PORT: КТ-2
//	val barrel: Block // PORT: КТ-3
//	val barrier: Block // PORT: КТ-2
//	val bottomlessChest: Block // PORT: КТ-3
//	val corporeaAutocrafter: Block // PORT: КТ-3
//	val corporeaInjector: Block // PORT: КТ-3
//	val corporeaRatBase: Block // PORT: КТ-3
//	val corporeaSparkBase: Block // PORT: КТ-3
//	val dirtDissolvable: Block // PORT: КТ-2
//	val domainDoor: Block // PORT: КТ-6
//	val dreamSapling: Block // PORT: КТ-2
	val elvenOre: Array<Block>
	val elvenSand: Block
//	val enderActuator: Block // PORT: КТ-3
//	val flugelHeadBlock: Block // PORT: КТ-8
//	val flugelHead2Block: Block // PORT: КТ-8
//	val gaiaButton: Block // PORT: КТ-3
//	val grapesRed: Array<Block> // PORT: КТ-2
//	val grapesRedPlanted: Block // PORT: КТ-2
//	val grapesWhite: Block // PORT: КТ-2
//	val icicle: Block // PORT: КТ-2
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
//	val helheimBlock: Block // PORT: КТ-2
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
//	val niflheimBlock: Block // PORT: КТ-2
//	val niflheimPortal: Block // PORT: КТ-6
//	val onyx: Block // PORT: КТ-2
//	val poisonIce: Block // PORT: КТ-2
//	val powerStone: Block // PORT: КТ-3
//	val raceSelector: Block // PORT: КТ-7
//	val rainbowDirt: Block // PORT: КТ-2
//	val rainbowFlame: Block // PORT: КТ-3
//	val rainbowFlowerFloating: Block // PORT: КТ-3
//	val rainbowGrass: Block // PORT: КТ-2
//	val rainbowLeaves: Block // PORT: КТ-2
//	val rainbowMushroom: Block // PORT: КТ-2
//	val rainbowPlanks: Block // PORT: КТ-2
//	val rainbowSlab: Block // PORT: КТ-2
//	val rainbowSlabFull: Block // PORT: КТ-2
//	val rainbowStairs: Block // PORT: КТ-2
//	val rainbowTallGrass: Block // PORT: КТ-2
//	val rainbowTallFlower: Block // PORT: КТ-2
//	val rainbowWood: Block // PORT: КТ-2
//	val realityAnchor: Block // PORT: КТ-3
//	val redFlame: Block // PORT: КТ-2
//	val redstoneAttractor: Block // PORT: КТ-2
//	val redstoneRelay: Block // PORT: КТ-3
//	val redStringObserver: Block // PORT: КТ-3
//	val redStringWatcher: Block // PORT: КТ-3
//	val rift: Block // PORT: КТ-4
//	val rpc: Block // PORT: КТ-2
//	val sadOakLeaves: Block // PORT: КТ-2
//	val schemaAnnihilator: Block // PORT: КТ-6
//	val schemaController: Block // PORT: КТ-6
//	val schemaFiller: Block // PORT: КТ-6
//	val schemaGenerator: Block // PORT: КТ-6
//	val schemaMarker: Block // PORT: КТ-6
//	val shimmerQuartz: Block // PORT: КТ-2
//	val shimmerQuartzSlab: Block // PORT: КТ-2
//	val shimmerQuartzSlabFull: Block // PORT: КТ-2
//	val shimmerQuartzStairs: Block // PORT: КТ-2
//	val snakeBody: Block // PORT: КТ-2
//	val snakeObject: Block // PORT: КТ-2
//	val snowGrass: Block // PORT: КТ-2
//	val snowLayer: Block // PORT: КТ-2
//	val softStorage: Block // PORT: КТ-2
//	val spire: Block // PORT: КТ-3
//	val starBlock: Block // PORT: КТ-3
//	val starBlock2: Block // PORT: КТ-3
//	val stalactite: Block // PORT: КТ-2
//	val stalagmite: Block // PORT: КТ-2
//	val subspacian: Block // PORT: КТ-2
//	val tradePortal: Block // PORT: КТ-3
//	val treeCrafterBlock: Block // PORT: КТ-3
//	val treeCrafterBlockRB: Block // PORT: КТ-3
//	val treeCrafterBlockAU: Block // PORT: КТ-3
//	val worldTree: Block // PORT: КТ-3
//	val yggFlower: Block // PORT: КТ-3
	
	// DENDROLOGY
	
//	val altLeaves: Block // PORT: КТ-2
//	val altPlanks: Block // PORT: КТ-2
//	val altSlabs: Block // PORT: КТ-2
//	val altSlabsFull: Block // PORT: КТ-2
//	val altStairs: Array<Block> // PORT: КТ-2
//	val altWood0: Block // PORT: КТ-2
//	val altWood1: Block // PORT: КТ-2
	
//	val barrierLeaves: Block // PORT: КТ-2
//	val barrierBerry: Block // PORT: КТ-2
//	val barrierPlanks: Block // PORT: КТ-2
//	val barrierSapling: Block // PORT: КТ-2
//	val barrierSlabs: Block // PORT: КТ-2
//	val barrierSlabFull: Block // PORT: КТ-2
//	val barrierStairs: Block // PORT: КТ-2
//	val barrierWood: Block // PORT: КТ-2
	
//	val calicoLeaves: Block // PORT: КТ-2
//	val calicoBerry: Block // PORT: КТ-2
//	val calicoPlanks: Block // PORT: КТ-2
//	val calicoSapling: Block // PORT: КТ-2
//	val calicoSlabs: Block // PORT: КТ-2
//	val calicoSlabFull: Block // PORT: КТ-2
//	val calicoStairs: Block // PORT: КТ-2
//	val calicoWood: Block // PORT: КТ-2
	
//	val circuitLeaves: Block // PORT: КТ-2
//	val circuitBerry: Block // PORT: КТ-2
//	val circuitPlanks: Block // PORT: КТ-2
//	val circuitSapling: Block // PORT: КТ-2
//	val circuitSlabs: Block // PORT: КТ-2
//	val circuitSlabFull: Block // PORT: КТ-2
//	val circuitStairs: Block // PORT: КТ-2
//	val circuitWood: Block // PORT: КТ-2
	
//	val lightningLeaves: Block // PORT: КТ-2
//	val lightningBerry: Block // PORT: КТ-2
//	val lightningPlanks: Block // PORT: КТ-2
//	val lightningSapling: Block // PORT: КТ-2
//	val lightningSlabs: Block // PORT: КТ-2
//	val lightningSlabFull: Block // PORT: КТ-2
//	val lightningStairs: Block // PORT: КТ-2
//	val lightningWood: Block // PORT: КТ-2
	
//	val netherLeaves: Block // PORT: КТ-2
//	val netherBerry: Block // PORT: КТ-2
//	val netherPlanks: Block // PORT: КТ-2
//	val netherSapling: Block // PORT: КТ-2
//	val netherSlabs: Block // PORT: КТ-2
//	val netherSlabFull: Block // PORT: КТ-2
//	val netherStairs: Block // PORT: КТ-2
//	val netherWood: Block // PORT: КТ-2
	
//	val sealingLeaves: Block // PORT: КТ-2
//	val sealingBerry: Block // PORT: КТ-2
//	val sealingPlanks: Block // PORT: КТ-2
//	val sealingSapling: Block // PORT: КТ-2
//	val sealingSlabs: Block // PORT: КТ-2
//	val sealingSlabFull: Block // PORT: КТ-2
//	val sealingStairs: Block // PORT: КТ-2
//	val sealingWood: Block // PORT: КТ-2
	
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
//		auroraDirt = BlockAuroraDirt() // PORT: КТ-2
//		auroraLeaves = BlockAuroraLeaves() // PORT: КТ-2
//		auroraPlanks = BlockAuroraPlanks() // PORT: КТ-2
//		auroraSlab = BlockAuroraWoodSlab(false) // PORT: КТ-2
//		auroraSlabFull = BlockAuroraWoodSlab(true) // PORT: КТ-2
//		auroraSlab.register() // PORT: КТ-2
//		auroraSlabFull.register() // PORT: КТ-2
//		auroraStairs = BlockAuroraWoodStairs() // PORT: КТ-2
//		auroraWood = BlockAuroraWood() // PORT: КТ-2
//		barrel = BlockBarrel() // PORT: КТ-3
//		barrier = BlockBarrier() // PORT: КТ-2
//		bottomlessChest = BlockBottomlessChest() // PORT: КТ-3
//		corporeaAutocrafter = BlockCorporeaAutocrafter() // PORT: КТ-3
//		corporeaInjector = BlockCorporeaInjector() // PORT: КТ-3
//		corporeaRatBase = BlockCorporeaRat() // PORT: КТ-3
//		corporeaSparkBase = BlockCorporeaSparkBase() // PORT: КТ-3
//		dirtDissolvable = BlockDirtDissolvable() // PORT: КТ-2
//		domainDoor = BlockDomainDoor() // PORT: КТ-6
//		dreamSapling = BlockDreamSapling() // PORT: КТ-2
		elvenOre = Array(6) { BlockElvenOre(it) }
		elvenSand = BlockElvenSand()
//		enderActuator = BlockEnderActuator() // PORT: КТ-3
//		flugelHeadBlock = BlockHeadFlugel() // PORT: КТ-8
//		flugelHead2Block = BlockHeadMiku() // PORT: КТ-8
//		gaiaButton = BlockGaiaButton() // PORT: КТ-3
//		grapesRed = Array(3) { BlockGrapeRed(it) } // PORT: КТ-2
//		grapesRedPlanted = BlockGrapeRedPlanted() // PORT: КТ-2
//		grapesWhite = BlockGrapeWhite() // PORT: КТ-2
//		icicle = BlockIcicle() // PORT: КТ-2
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
//		helheimBlock = BlockPattern(ModInfo.MODID, Material.rock, "HelheimBlock", AlfheimTab, hardness = -1f, harvLvl = Int.MAX_VALUE, resistance = Float.MAX_VALUE) // PORT: КТ-2
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
//		niflheimBlock = BlockNiflheim() // PORT: КТ-2
//		niflheimPortal = BlockNiflheimPortal() // PORT: КТ-6
//		onyx = BlockOnyx().WIP() // PORT: КТ-2
//		poisonIce = BlockNiflheimIce() // PORT: КТ-2
//		powerStone = BlockPowerStone() // PORT: КТ-3
//		raceSelector = BlockRaceSelector() // PORT: КТ-7
//		rainbowDirt = BlockRainbowDirt() // PORT: КТ-2
//		rainbowFlame = BlockRainbowManaFlame() // PORT: КТ-3
//		rainbowFlowerFloating = BlockFloatingFlowerRainbow() // PORT: КТ-3
//		rainbowLeaves = BlockRainbowLeaves() // PORT: КТ-2
//		rainbowGrass = BlockRainbowGrass() // PORT: КТ-2
//		rainbowMushroom = BlockRainbowMushroom() // PORT: КТ-2
//		rainbowPlanks = BlockRainbowPlanks() // PORT: КТ-2
//		rainbowSlab = BlockRainbowWoodSlab(false) // PORT: КТ-2
//		rainbowSlabFull = BlockRainbowWoodSlab(true) // PORT: КТ-2
//		rainbowSlab.register() // PORT: КТ-2
//		rainbowSlabFull.register() // PORT: КТ-2
//		rainbowStairs = BlockRainbowWoodStairs() // PORT: КТ-2
//		rainbowTallGrass = BlockRainbowDoubleGrass() // PORT: КТ-2
//		rainbowTallFlower = BlockRainbowDoubleFlower() // PORT: КТ-2
//		rainbowWood = BlockRainbowWood() // PORT: КТ-2
//		realityAnchor = BlockRealityAnchor() // PORT: КТ-3
//		redFlame = BlockRedFlame() // PORT: КТ-2
//		redstoneAttractor = BlockRedstoneAttractor() // PORT: КТ-2
//		redstoneRelay = BlockRedstoneRelay() // PORT: КТ-3
//		redStringObserver = BlockRedStringObserver() // PORT: КТ-3
//		redStringWatcher = BlockRedStringWatcher() // PORT: КТ-3
//		rift = BlockRift() // PORT: КТ-4
//		rpc = BlockRealmPowerCollector() // PORT: КТ-2
//		sadOakLeaves = BlockSadOakLeaves() // PORT: КТ-2
//		schemaAnnihilator = BlockSchemaAnnihilator() // PORT: КТ-6
//		schemaController = BlockSchemaContoller() // PORT: КТ-6
//		schemaFiller = BlockSchemaFiller() // PORT: КТ-6
//		schemaGenerator = BlockSchemaGenerator() // PORT: КТ-6
//		schemaMarker = BlockSchemaMarker() // PORT: КТ-6
//		shimmerQuartz = BlockShimmerQuartz() // PORT: КТ-2
//		shimmerQuartzSlab = BlockShimmerQuartzSlab(shimmerQuartz, false) // PORT: КТ-2
//		shimmerQuartzSlabFull = BlockShimmerQuartzSlab(shimmerQuartz, true) // PORT: КТ-2
//		shimmerQuartzSlab.register() // PORT: КТ-2
//		shimmerQuartzSlabFull.register() // PORT: КТ-2
//		shimmerQuartzStairs = BlockShimmerQuartzStairs(shimmerQuartz) // PORT: КТ-2
//		snakeBody = BlockSnakeBody() // PORT: КТ-2
//		snakeObject = BlockSnakeObject() // PORT: КТ-2
//		snowGrass = BlockSnowGrass() // PORT: КТ-2
//		snowLayer = BlockSnowLayer() // PORT: КТ-2
//		softStorage = BlockSoftStorage() // PORT: КТ-2
//		spire = BlockSpire() // PORT: КТ-3
//		starBlock = BlockStar() // PORT: КТ-3
//		starBlock2 = BlockCracklingStar() // PORT: КТ-3
//		stalactite = BlockStalactite() // PORT: КТ-2
//		stalagmite = BlockStalagmite() // PORT: КТ-2
//		subspacian = BlockSubspacian() // PORT: КТ-2
//		tradePortal = BlockTradePortal() // PORT: КТ-3
//		treeCrafterBlock = BlockTreeCrafter("treeCrafter", irisPlanks) // PORT: КТ-3
//		treeCrafterBlockRB = BlockTreeCrafter("treeCrafterRB", rainbowPlanks) // PORT: КТ-3
//		treeCrafterBlockAU = BlockTreeCrafter("treeCrafterAU", auroraPlanks) // PORT: КТ-3
//		worldTree = BlockWorldTree() // PORT: КТ-3
//		yggFlower = BlockYggFlower() // PORT: КТ-3
		
		// DENDOROLOGY
		
//		altLeaves = BlockAltLeaves() // PORT: КТ-2
//		altPlanks = BlockAltPlanks() // PORT: КТ-2
//		altSlabs = BlockAltWoodSlab(false) // PORT: КТ-2
//		altSlabsFull = BlockAltWoodSlab(true) // PORT: КТ-2
//		(altSlabs as BlockSlabMod).register() // PORT: КТ-2
//		(altSlabsFull as BlockSlabMod).register() // PORT: КТ-2
//		altStairs = Array(LibOreDict.ALT_TYPES.size - 1) { if (it == BlockAltLeaves.yggMeta) BlockYggStairs() else BlockAltWoodStairs(it) } // PORT: КТ-2
//		altWood0 = BlockAltWood(0) // PORT: КТ-2
//		altWood1 = BlockAltWood(1) // PORT: КТ-2
		
//		barrierLeaves = BlockBarrierLeaves() // PORT: КТ-2
//		barrierBerry = BlockTreeBerry(barrierLeaves, 0) // PORT: КТ-2
//		barrierPlanks = BlockBarrierPlanks() // PORT: КТ-2
//		barrierSapling = BlockBarrierSapling() // PORT: КТ-2
//		barrierSlabs = BlockBarrierWoodSlab(false) // PORT: КТ-2
//		barrierSlabFull = BlockBarrierWoodSlab(true) // PORT: КТ-2
//		barrierSlabs.register() // PORT: КТ-2
//		barrierSlabFull.register() // PORT: КТ-2
//		barrierStairs = BlockBarrierWoodStairs() // PORT: КТ-2
//		barrierWood = BlockBarrierWood() // PORT: КТ-2
		
//		calicoLeaves = BlockCalicoLeaves() // PORT: КТ-2
//		calicoBerry = BlockTreeBerry(calicoLeaves, 1) // PORT: КТ-2
//		calicoPlanks = BlockCalicoPlanks() // PORT: КТ-2
//		calicoSapling = BlockCalicoSapling() // PORT: КТ-2
//		calicoSlabs = BlockCalicoWoodSlab(false) // PORT: КТ-2
//		calicoSlabFull = BlockCalicoWoodSlab(true) // PORT: КТ-2
//		calicoSlabs.register() // PORT: КТ-2
//		calicoSlabFull.register() // PORT: КТ-2
//		calicoStairs = BlockCalicoWoodStairs() // PORT: КТ-2
//		calicoWood = BlockCalicoWood() // PORT: КТ-2
		
//		circuitLeaves = BlockCircuitLeaves() // PORT: КТ-2
//		circuitBerry = BlockTreeBerry(circuitLeaves, 2) // PORT: КТ-2
//		circuitPlanks = BlockCircuitPlanks() // PORT: КТ-2
//		circuitSapling = BlockCircuitSapling() // PORT: КТ-2
//		circuitSlabs = BlockCircuitWoodSlab(false) // PORT: КТ-2
//		circuitSlabFull = BlockCircuitWoodSlab(true) // PORT: КТ-2
//		circuitSlabs.register() // PORT: КТ-2
//		circuitSlabFull.register() // PORT: КТ-2
//		circuitStairs = BlockCircuitWoodStairs() // PORT: КТ-2
//		circuitWood = BlockCircuitWood() // PORT: КТ-2
		
//		lightningLeaves = BlockLightningLeaves() // PORT: КТ-2
//		lightningBerry = BlockTreeBerry(lightningLeaves, 3) // PORT: КТ-2
//		lightningPlanks = BlockLightningPlanks() // PORT: КТ-2
//		lightningSapling = BlockLightningSapling() // PORT: КТ-2
//		lightningSlabs = BlockLightningWoodSlab(false) // PORT: КТ-2
//		lightningSlabFull = BlockLightningWoodSlab(true) // PORT: КТ-2
//		lightningSlabs.register() // PORT: КТ-2
//		lightningSlabFull.register() // PORT: КТ-2
//		lightningStairs = BlockLightningWoodStairs() // PORT: КТ-2
//		lightningWood = BlockLightningWood() // PORT: КТ-2
		
//		netherLeaves = BlockNetherLeaves() // PORT: КТ-2
//		netherBerry = BlockTreeBerry(netherLeaves, 4) // PORT: КТ-2
//		netherPlanks = BlockNetherPlanks() // PORT: КТ-2
//		netherSapling = BlockNetherSapling() // PORT: КТ-2
//		netherSlabs = BlockNetherWoodSlab(false) // PORT: КТ-2
//		netherSlabFull = BlockNetherWoodSlab(true) // PORT: КТ-2
//		netherSlabs.register() // PORT: КТ-2
//		netherSlabFull.register() // PORT: КТ-2
//		netherStairs = BlockNetherWoodStairs() // PORT: КТ-2
//		netherWood = BlockNetherWood() // PORT: КТ-2
		
//		sealingLeaves = BlockSealingLeaves() // PORT: КТ-2
//		sealingBerry = BlockTreeBerry(sealingLeaves, 5) // PORT: КТ-2
//		sealingPlanks = BlockSealingPlanks() // PORT: КТ-2
//		sealingSapling = BlockSealingSapling() // PORT: КТ-2
//		sealingSlabs = BlockSealingWoodSlab(false) // PORT: КТ-2
//		sealingSlabFull = BlockSealingWoodSlab(true) // PORT: КТ-2
//		sealingSlabs.register() // PORT: КТ-2
//		sealingSlabFull.register() // PORT: КТ-2
//		sealingStairs = BlockSealingWoodStairs() // PORT: КТ-2
//		sealingWood = BlockSealingWood() // PORT: КТ-2
		
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
		
//		BotaniaAPI.registerPaintableBlock(rainbowWood) // PORT: КТ-2
//		BotaniaAPI.registerPaintableBlock(rainbowPlanks) // PORT: КТ-2
//		BotaniaAPI.registerPaintableBlock(rainbowStairs) // PORT: КТ-2
//		BotaniaAPI.registerPaintableBlock(rainbowSlab) // PORT: КТ-2
//		BotaniaAPI.registerPaintableBlock(rainbowSlabFull) // PORT: КТ-2
		
//		BotaniaAPI.registerPaintableBlock(starBlock) // PORT: КТ-3
//		BotaniaAPI.registerPaintableBlock(starBlock2) // PORT: КТ-3
		
//		BotaniaAPI.registerPaintableBlock(Blocks.dirt) // PORT: КТ-2
//		BotaniaAPI.registerPaintableBlock(irisDirt) // PORT: КТ-3 (линза краски, H-293)
//		BotaniaAPI.registerPaintableBlock(rainbowDirt) // PORT: КТ-2
		
//		BotaniaAPI.registerPaintableBlock(ModBlocks.livingrock) // PORT: КТ-3
//		BotaniaAPI.registerPaintableBlock(ModBlocks.dreamwood) // PORT: КТ-3
	}
	
	fun regOreDict() {
//		registerOre("endstone", ItemStack(Blocks.end_stone)) // PORT: КТ-3
//		registerOre("grassSnow", ItemStack(snowGrass)) // PORT: КТ-2
//		registerOre("snowLayer", ItemStack(snowLayer)) // PORT: КТ-2
//		registerOre("niflStone", ItemStack(niflheimBlock)) // PORT: КТ-2
		
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
		
//		registerOre(LibOreDict.NIFLEUR_ORE, BlockNiflheim.NiflheimBlockMetas.ORE.stack) // PORT: КТ-2
		
//		val quartzs = arrayOf(ModFluffBlocks.darkQuartz, ModFluffBlocks.manaQuartz, ModFluffBlocks.blazeQuartz, ModFluffBlocks.lavenderQuartz, ModFluffBlocks.redQuartz, ModFluffBlocks.elfQuartz, ModFluffBlocks.sunnyQuartz) // PORT: КТ-2
		
//		BLibOreDict.QUARTZ.forEachIndexed { id, it -> // PORT: КТ-2
//			registerOre("block${it.capitalized()}", ItemStack(quartzs[id] ?: return@forEachIndexed))
//		}
//		registerOre(LibOreDict.RAINBOW_QUARTZ_BLOCK, ItemStack(shimmerQuartz)) // PORT: КТ-2
		
		registerOre("sand", ItemStack(elvenSand))
		
//		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 3)) // PORT: КТ-2
//		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 7)) // PORT: КТ-2
//		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 11)) // PORT: КТ-2
//		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 15)) // PORT: КТ-2
		
		// ################
		
//		registerOre(LibOreDict.RAINBOW_FLOWER, ItemStack(rainbowGrass, 1, 2)) // PORT: КТ-2
//		registerOre(LibOreDict.RAINBOW_DOUBLE_FLOWER, ItemStack(rainbowTallFlower)) // PORT: КТ-2
		
//		registerOre(LibOreDict.MUSHROOM, ItemStack(ModBlocks.mushroom, 1, WILDCARD_VALUE)) // PORT: КТ-3
//		registerOre(LibOreDict.MUSHROOM, ItemStack(rainbowMushroom)) // PORT: КТ-2
		
		registerOre("treeSapling", irisSapling)
		
//		registerOre("treeLeaves", ItemStack(lightningLeaves)) // PORT: КТ-2
//		registerOre("plankWood", ItemStack(lightningPlanks)) // PORT: КТ-2
//		registerOre("treeSapling", ItemStack(lightningSapling)) // PORT: КТ-2
		
//		registerOre("slabWood", ItemStack(lightningSlabs)) // PORT: КТ-2
//		registerOre("stairWood", ItemStack(lightningStairs)) // PORT: КТ-2
		
//		registerOre("treeLeaves", ItemStack(calicoLeaves)) // PORT: КТ-2
//		registerOre("plankWood", ItemStack(calicoPlanks)) // PORT: КТ-2
//		registerOre("treeSapling", ItemStack(calicoSapling)) // PORT: КТ-2
		
//		registerOre("slabWood", ItemStack(calicoSlabs)) // PORT: КТ-2
//		registerOre("stairWood", ItemStack(calicoStairs)) // PORT: КТ-2
		
//		registerOre("treeLeaves", ItemStack(circuitLeaves)) // PORT: КТ-2
//		registerOre("plankWood", ItemStack(circuitPlanks)) // PORT: КТ-2
//		registerOre("treeSapling", ItemStack(circuitSapling)) // PORT: КТ-2
		
//		registerOre("slabWood", ItemStack(circuitSlabs)) // PORT: КТ-2
//		registerOre("stairWood", ItemStack(circuitStairs)) // PORT: КТ-2
		
//		registerOre("treeLeaves", ItemStack(netherLeaves)) // PORT: КТ-2
//		registerOre("plankWood", ItemStack(netherPlanks)) // PORT: КТ-2
//		registerOre("treeSapling", ItemStack(netherSapling)) // PORT: КТ-2
		
//		registerOre("slabWood", ItemStack(netherSlabs)) // PORT: КТ-2
//		registerOre("stairWood", ItemStack(netherStairs)) // PORT: КТ-2
		
//		registerOre("treeLeaves", ItemStack(sealingLeaves)) // PORT: КТ-2
//		registerOre("plankWood", ItemStack(sealingPlanks)) // PORT: КТ-2
//		registerOre("treeSapling", ItemStack(sealingSapling)) // PORT: КТ-2
		
//		registerOre("slabWood", ItemStack(sealingSlabs)) // PORT: КТ-2
//		registerOre("stairWood", ItemStack(sealingStairs)) // PORT: КТ-2
		
//		registerOre("treeLeaves", ItemStack(barrierLeaves)) // PORT: КТ-2
//		registerOre("plankWood", ItemStack(barrierPlanks)) // PORT: КТ-2
//		registerOre("treeSapling", ItemStack(barrierSapling)) // PORT: КТ-2
		
//		registerOre("slabWood", ItemStack(barrierSlabs)) // PORT: КТ-2
//		registerOre("stairWood", ItemStack(barrierStairs)) // PORT: КТ-2
		
		// PORT: вариант metadata — блок массива (SPEC, Р-5): ItemStack(x, 1, i) → ItemStack(x[i], 1)
		for (i in 0..3) {
			registerOre(LibOreDict.WOOD[i], ItemStack(irisWood0[i], 1))
			
			registerOre(LibOreDict.WOOD[i + 4], ItemStack(irisWood1[i], 1))
			
			registerOre(LibOreDict.WOOD[i + 8], ItemStack(irisWood2[i], 1))
			
			registerOre(LibOreDict.WOOD[i + 12], ItemStack(irisWood3[i], 1))
		}
			
//		registerOre(LibOreDict.WOOD[16], ItemStack(rainbowWood)) // PORT: КТ-2
//		registerOre(LibOreDict.WOOD[17], ItemStack(auroraWood)) // PORT: КТ-2
			
		for (i in 0..7) {
			registerOre(LibOreDict.LEAVES[i], ItemStack(irisLeaves0[i], 1))
			registerOre(LibOreDict.LEAVES[i + 8], ItemStack(irisLeaves1[i], 1))
		}
			
//		registerOre(LibOreDict.LEAVES[16], ItemStack(rainbowLeaves)) // PORT: КТ-2
//		registerOre(LibOreDict.LEAVES[17], ItemStack(auroraLeaves)) // PORT: КТ-2
		
//		for (i in 0..5) { // PORT: КТ-2
//			registerOre("stairWood", ItemStack(altStairs[i], 1))
//			
//			registerOre("treeLeaves", ItemStack(altLeaves, 1, i))
//		}
		
//		for (i in 0 until LibOreDict.ALT_TYPES.size - 1) { // PORT: КТ-2
//			registerOre("slabWood", ItemStack(altSlabs, 1, i))
//			
//			registerOre("slabWood", ItemStack(altSlabsFull, 1, i))
//		}
		
		// PORT: WILDCARD_VALUE у блока с вариантами — все блоки его массива (SPEC, Р-5)
		irisDirt.forEach { registerOre(LibOreDict.IRIS_DIRT, ItemStack(it, 1, WILDCARD_VALUE)) }
//		registerOre(LibOreDict.IRIS_DIRT, ItemStack(irisDirt, 1, WILDCARD_VALUE))
		
		// PORT: имена 16 и 17 (Rainbow, Aurora) в 1.7.10 получали несуществующие варианты цветной земли 16 и 17; у этих
		// имён свои блоки — радужная и авроровая земля ниже
		LibOreDict.DIRT.forEachIndexed { id, it ->
			if (id < irisDirt.size) registerOre(it, ItemStack(irisDirt[id], 1))
//			registerOre(it, ItemStack(irisDirt, 1, id))
		}
			
//		registerOre(LibOreDict.DIRT[16], ItemStack(rainbowDirt)) // PORT: КТ-2
//		registerOre(LibOreDict.IRIS_DIRT, ItemStack(rainbowDirt)) // PORT: КТ-2
//		registerOre(LibOreDict.DIRT[17], ItemStack(auroraDirt)) // PORT: КТ-2
//		registerOre(LibOreDict.IRIS_DIRT, ItemStack(auroraDirt)) // PORT: КТ-2
		
		var t: ItemStack
		
//		arrayOf(lightningWood, netherWood, sealingWood, calicoWood, circuitWood, barrierWood, altWood0).forEach { // PORT: КТ-2
//			registerOre("logWood", ItemStack(it, 1, WILDCARD_VALUE))
//		}
		
		// PORT: блоки с вариантами — массивы, вместе — все варианты (SPEC, Р-5). КТ-2, партия 8б — rainbowWood, auroraWood
		(irisWood0 + irisWood1 + irisWood2 + irisWood3/* + rainbowWood + auroraWood*/).forEach {
//		arrayOf(irisWood0, irisWood1, irisWood2, irisWood3, rainbowWood, auroraWood).forEach {
			t = ItemStack(it, 1, WILDCARD_VALUE)
			registerOre("logWood", t)
			registerOre(IRIS_WOOD, t)
		}
		
		// PORT: блоки с вариантами — массивы, вместе — все варианты (SPEC, Р-5). КТ-2, партия 8б — rainbowLeaves, auroraLeaves
		(irisLeaves0 + irisLeaves1/* + rainbowLeaves + auroraLeaves*/).forEach {
//		arrayOf(irisLeaves0, irisLeaves1, rainbowLeaves, auroraLeaves).forEach {
			t = ItemStack(it, 1, WILDCARD_VALUE)
			registerOre("treeLeaves", t)
			registerOre(LibOreDict.IRIS_LEAVES, t)
		}
			
		irisPlanks.forEach { registerOre("plankWood", ItemStack(it, 1, WILDCARD_VALUE)) }
//		registerOre("plankWood", ItemStack(irisPlanks, 1, WILDCARD_VALUE))
//		registerOre("plankWood", ItemStack(altPlanks, 1, WILDCARD_VALUE)) // PORT: КТ-2
//		registerOre("plankWood", ItemStack(rainbowPlanks, 1, WILDCARD_VALUE)) // PORT: КТ-2
//		registerOre("plankWood", ItemStack(auroraPlanks, 1, WILDCARD_VALUE)) // PORT: КТ-2

		irisStairs.forEach {
			registerOre("stairWood", it)
		}
//		registerOre("stairWood", rainbowStairs) // PORT: КТ-2
//		registerOre("stairWood", auroraStairs) // PORT: КТ-2

		irisSlabs.forEach {
			registerOre("slabWood", ItemStack(it, 1, WILDCARD_VALUE))
		}
//		registerOre("slabWood", rainbowSlab) // PORT: КТ-2
//		registerOre("slabWood", auroraSlab) // PORT: КТ-2

//		for (i in 0..15) { // PORT: КТ-2
//			if (i !in arrayOf(2, 6, 10, 14)) { // Yggdrasil metas
//				t = ItemStack(altWood1, 1, i)
//				registerOre("logWood", t)
//			}
//			
//			if (i != BlockAltLeaves.yggMeta) {
//				t = ItemStack(altLeaves, 1, i)
//				registerOre("treeLeaves", t)
//			}
//			
////			registerOre(LibOreDict.IRIS_DIRT, ItemStack(irisDirt, 1, i))
////			registerOre(LibOreDict.DIRT[i], ItemStack(irisDirt, 1, i))
//			
////			registerOre("logWood", ItemStack(lightningWood, 1, i))
////			registerOre("logWood", ItemStack(netherWood, 1, i))
////			registerOre("logWood", ItemStack(sealingWood, 1, i))
////			registerOre("logWood", ItemStack(calicoWood, 1, i))
////			registerOre("logWood", ItemStack(circuitWood, 1, i))
//			
////			t = ItemStack(irisWood0, 1, i)
////			registerOre("logWood", t)
////			registerOre(LibOreDict.IRIS_WOOD, t)
////
////			t = ItemStack(irisWood1, 1, i)
////			registerOre("logWood", t)
////			registerOre(LibOreDict.IRIS_WOOD, t)
////
////			t = ItemStack(irisWood2, 1, i)
////			registerOre("logWood", t)
////			registerOre(LibOreDict.IRIS_WOOD, t)
////
////			t = ItemStack(irisWood3, 1, i)
////			registerOre("logWood", t)
////			registerOre(LibOreDict.IRIS_WOOD, t)
////
////			t = ItemStack(rainbowWood, 1, i)
////			registerOre("logWood", t)
////			registerOre(LibOreDict.IRIS_WOOD, t)
////
////			t = ItemStack(auroraWood, 1, i)
////			registerOre("logWood", t)
////			registerOre(LibOreDict.IRIS_WOOD, t)
////
////			t = ItemStack(altWood0, 1, i)
////			registerOre("logWood", t)
//			
////			t = ItemStack(irisLeaves0, 1, i)
////			registerOre("treeLeaves", t)
////			registerOre(LibOreDict.IRIS_LEAVES, t)
////
////			t = ItemStack(irisLeaves1, 1, i)
////			registerOre("treeLeaves", t)
////			registerOre(LibOreDict.IRIS_LEAVES, t)
////
////			t = ItemStack(rainbowLeaves, 1, i)
////			registerOre("treeLeaves", t)
////			registerOre(LibOreDict.IRIS_LEAVES, t)
////
////			t = ItemStack(auroraLeaves, 1, i)
////			registerOre("treeLeaves", t)
////			registerOre(LibOreDict.IRIS_LEAVES, t)
//			
////			t = ItemStack(irisSlabsFull[i], 1)
////			registerOre("slabWood", t)
//		}
	}
	
	fun registerBurnables() {
//		setBurnable(altLeaves, 30, 60) // PORT: КТ-2
//		setBurnable(altPlanks, 5, 20) // PORT: КТ-2
//		setBurnable(altSlabs, 5, 20) // PORT: КТ-2
//		setBurnable(altSlabsFull, 5, 20) // PORT: КТ-2
//		altStairs.forEach { setBurnable(it, 5, 20) } // PORT: КТ-2
//		setBurnable(altWood0, 5, 5) // PORT: КТ-2
//		setBurnable(altWood1, 5, 5) // PORT: КТ-2
		
//		setBurnable(amplifier, 5, 20) // PORT: КТ-2
		
//		setBurnable(auroraPlanks, 5, 20) // PORT: КТ-2
//		setBurnable(auroraSlab, 5, 20) // PORT: КТ-2
//		setBurnable(auroraSlabFull, 5, 20) // PORT: КТ-2
//		setBurnable(auroraStairs, 5, 20) // PORT: КТ-2
//		setBurnable(auroraWood, 5, 5) // PORT: КТ-2
		
//		setBurnable(calicoLeaves, 30, 60) // PORT: КТ-2
//		setBurnable(calicoPlanks, 5, 20) // PORT: КТ-2
//		setBurnable(calicoSlabs, 5, 20) // PORT: КТ-2
//		setBurnable(calicoSlabFull, 5, 20) // PORT: КТ-2
//		setBurnable(calicoStairs, 5, 20) // PORT: КТ-2
//		setBurnable(calicoWood, 5, 5) // PORT: КТ-2
		
//		setBurnable(circuitLeaves, 30, 60) // PORT: КТ-2
//		setBurnable(circuitPlanks, 5, 20) // PORT: КТ-2
//		setBurnable(circuitSlabs, 5, 20) // PORT: КТ-2
//		setBurnable(circuitSlabFull, 5, 20) // PORT: КТ-2
//		setBurnable(circuitStairs, 5, 20) // PORT: КТ-2
//		setBurnable(circuitWood, 5, 5) // PORT: КТ-2
		
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
		
//		setBurnable(lightningLeaves, 30, 60) // PORT: КТ-2
//		setBurnable(lightningPlanks, 5, 20) // PORT: КТ-2
//		setBurnable(lightningSlabs, 5, 20) // PORT: КТ-2
//		setBurnable(lightningSlabFull, 5, 20) // PORT: КТ-2
//		setBurnable(lightningStairs, 5, 20) // PORT: КТ-2
//		setBurnable(lightningWood, 5, 5) // PORT: КТ-2
		
//		setBurnable(rainbowGrass, 60, 100) // PORT: КТ-2
//		setBurnable(rainbowLeaves, 30, 60) // PORT: КТ-2
//		setBurnable(rainbowPlanks, 5, 20) // PORT: КТ-2
//		setBurnable(rainbowSlab, 5, 20) // PORT: КТ-2
//		setBurnable(rainbowSlabFull, 5, 20) // PORT: КТ-2
//		setBurnable(rainbowStairs, 5, 20) // PORT: КТ-2
//		setBurnable(rainbowTallGrass, 60, 100) // PORT: КТ-2
//		setBurnable(rainbowWood, 5, 5) // PORT: КТ-2
		
//		setBurnable(sealingLeaves, 30, 60) // PORT: КТ-2
//		setBurnable(sealingPlanks, 5, 20) // PORT: КТ-2
//		setBurnable(sealingSlabs, 5, 20) // PORT: КТ-2
//		setBurnable(sealingSlabFull, 5, 20) // PORT: КТ-2
//		setBurnable(sealingStairs, 5, 20) // PORT: КТ-2
//		setBurnable(sealingWood, 5, 5) // PORT: КТ-2
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
//		AlfheimAPI.addTreeVariant(rainbowDirt, rainbowWood, rainbowLeaves) // PORT: КТ-2
//		AlfheimAPI.addTreeVariant(auroraDirt, auroraWood, auroraLeaves) // PORT: КТ-2
//		AlfheimAPI.addTreeVariant(ModBlocks.altGrass, altWood0, altLeaves, 0, 3) // PORT: КТ-2
//		AlfheimAPI.addTreeVariant(ModBlocks.altGrass, altWood1, altLeaves, 4, 5) // PORT: КТ-2
	}
	
//	fun addSubFlower(clazz: Class<out SubTileEntity>, name: String) { // PORT: КТ-3
//		BotaniaAPI.registerSubTile(name, clazz)
//		BotaniaAPI.registerSubTileSignature(clazz, AlfheimSignature(name))
//		BotaniaAPI.addSubTileToCreativeMenu(name)
//		AlfheimTab.subtiles.add(name)
//	}
	
	fun Block.setHarvestLevelI(toolClass: String, level: Int) = also { it.setHarvestLevel(toolClass, level) }
}