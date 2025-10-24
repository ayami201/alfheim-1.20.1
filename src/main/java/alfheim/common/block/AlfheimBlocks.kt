package alfheim.common.block

import alexsocol.asjlib.ASJUtilities.setBurnable
import alexsocol.asjlib.capitalized
import alexsocol.asjlib.extendables.block.BlockPattern
import alfheim.api.*
import alfheim.api.lib.LibOreDict
import alfheim.api.lib.LibOreDict.IRIS_WOOD
import alfheim.common.block.alt.*
import alfheim.common.block.base.*
import alfheim.common.block.colored.*
import alfheim.common.block.colored.rainbow.*
import alfheim.common.block.corporea.*
import alfheim.common.block.fluid.BlockManaFluid
import alfheim.common.block.magtrees.BlockTunedSapling
import alfheim.common.block.magtrees.barrier.*
import alfheim.common.block.magtrees.calico.*
import alfheim.common.block.magtrees.circuit.*
import alfheim.common.block.magtrees.lightning.*
import alfheim.common.block.magtrees.nether.*
import alfheim.common.block.magtrees.sealing.*
import alfheim.common.block.mana.*
import alfheim.common.block.schema.*
import alfheim.common.block.tile.sub.flower.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.WorkInProgressItemsHandler.WIP
import alfheim.common.core.util.AlfheimTab
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary.*
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.subtile.SubTileEntity
import vazkii.botania.common.block.*
import vazkii.botania.common.lib.LibBlockNames
import kotlin.collections.forEachIndexed
import kotlin.collections.set
import vazkii.botania.common.lib.LibOreDict as BLibOreDict

object AlfheimBlocks {
	
	val airyVirus: Block
	val alfheimPortal: Block
	val alfheimPylon: Block
	val alfStorage: Block
	val amplifier: Block
	val animatedTorch: Block
	val anomaly: Block
	val anomalyHarvester: Block
	val anyavil: Block
	val auroraDirt: Block
	val auroraLeaves: Block
	val auroraPlanks: Block
	val auroraSlab: Block
	val auroraSlabFull: Block
	val auroraStairs: Block
	val auroraWood: Block
	val barrel: Block
	val barrier: Block
	val bottomlessChest: Block
	val corporeaAutocrafter: Block
	val corporeaInjector: Block
	val corporeaRatBase: Block
	val corporeaSparkBase: Block
	val dirtDissolvable: Block
	val domainDoor: Block
	val dreamSapling: Block
	val elvenOre: Block
	val elvenSand: Block
	val enderActuator: Block
	val flugelHeadBlock: Block
	val flugelHead2Block: Block
	val gaiaButton: Block
	val grapesRed: Array<Block>
	val grapesRedPlanted: Block
	val grapesWhite: Block
	val icicle: Block
	val icyGeyser: Block
	val itemDisplay: Block
	val irisDirt: Block
	val irisGrass: Block
	val irisLamp: Block
	val irisLeaves0: Block
	val irisLeaves1: Block
	val irisPlanks: Block
	val irisSapling: Block
	val irisSlabs: Array<Block>
	val irisSlabsFull: Array<Block>
	val irisStairs: Array<Block>
	val irisTallGrass0: Block
	val irisTallGrass1: Block
	val irisWood0: Block
	val irisWood1: Block
	val irisWood2: Block
	val irisWood3: Block
	val itemFrame: Block
	val helheimBlock: Block
	val kindling: Block
	val livingcobble: Block
	val livingwoodFunnel: Block
	val lootbox: Block
	val manaAccelerator: Block
	val manaFluidBlock: Block
	val manaInfuser: Block
	val manaReflector: Block
	val manaTuner: Block
	val nidhoggTooth: Block
	val niflheimBlock: Block
	val niflheimPortal: Block
	val onyx: Block
	val poisonIce: Block
	val powerStone: Block
	val raceSelector: Block
	val rainbowDirt: Block
	val rainbowFlame: Block
	val rainbowFlowerFloating: Block
	val rainbowGrass: Block
	val rainbowLeaves: Block
	val rainbowMushroom: Block
	val rainbowPlanks: Block
	val rainbowSlab: Block
	val rainbowSlabFull: Block
	val rainbowStairs: Block
	val rainbowTallGrass: Block
	val rainbowTallFlower: Block
	val rainbowWood: Block
	val realityAnchor: Block
	val redFlame: Block
	val redstoneRelay: Block
	val rift: Block
	val rpc: Block
	val sadOakLeaves: Block
	val schemaAnnihilator: Block
	val schemaController: Block
	val schemaFiller: Block
	val schemaGenerator: Block
	val schemaMarker: Block
	val shimmerQuartz: Block
	val shimmerQuartzSlab: Block
	val shimmerQuartzSlabFull: Block
	val shimmerQuartzStairs: Block
	val snakeBody: Block
	val snakeObject: Block
	val snowGrass: Block
	val snowLayer: Block
	val softStorage: Block
	val spire: Block
	val starBlock: Block
	val starBlock2: Block
	val stalactite: Block
	val stalagmite: Block
	val subspacian: Block
	val tradePortal: Block
	val treeCrafterBlock: Block
	val treeCrafterBlockRB: Block
	val treeCrafterBlockAU: Block
	val worldTree: Block
	val yggFlower: Block
	
	// DENDROLOGY
	
	val altLeaves: Block
	val altPlanks: Block
	val altSlabs: Block
	val altSlabsFull: Block
	val altStairs: Array<Block>
	val altWood0: Block
	val altWood1: Block
	
	val barrierLeaves: Block
	val barrierBerry: Block
	val barrierPlanks: Block
	val barrierSapling: Block
	val barrierSlabs: Block
	val barrierSlabFull: Block
	val barrierStairs: Block
	val barrierWood: Block
	
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
	val lightningWood: Block
	
	val netherLeaves: Block
	val netherBerry: Block
	val netherPlanks: Block
	val netherSapling: Block
	val netherSlabs: Block
	val netherSlabFull: Block
	val netherStairs: Block
	val netherWood: Block
	
	val sealingLeaves: Block
	val sealingBerry: Block
	val sealingPlanks: Block
	val sealingSapling: Block
	val sealingSlabs: Block
	val sealingSlabFull: Block
	val sealingStairs: Block
	val sealingWood: Block
	
	val tunedSapling: Block
	
	init {
		airyVirus = BlockAiryVirus()
		alfheimPortal = BlockAlfheimPortal()
		alfheimPylon = BlockAlfheimPylon()
		alfStorage = BlockAlfStorage()
		amplifier = BlockAmplifier()
		animatedTorch = BlockAnimatedTorch()
		anomaly = BlockAnomaly()
		anomalyHarvester = BlockAnomalyHarvester()
		anyavil = BlockAnyavil()
		auroraDirt = BlockAuroraDirt()
		auroraLeaves = BlockAuroraLeaves()
		auroraPlanks = BlockAuroraPlanks()
		auroraSlab = BlockAuroraWoodSlab(false)
		auroraSlabFull = BlockAuroraWoodSlab(true)
		auroraSlab.register()
		auroraSlabFull.register()
		auroraStairs = BlockAuroraWoodStairs()
		auroraWood = BlockAuroraWood()
		barrel = BlockBarrel()
		barrier = BlockBarrier()
		bottomlessChest = BlockBottomlessChest()
		corporeaAutocrafter = BlockCorporeaAutocrafter()
		corporeaInjector = BlockCorporeaInjector()
		corporeaRatBase = BlockCorporeaRat()
		corporeaSparkBase = BlockCorporeaSparkBase()
		dirtDissolvable = BlockDirtDissolvable()
		domainDoor = BlockDomainDoor()
		dreamSapling = BlockDreamSapling()
		elvenOre = BlockElvenOre()
		elvenSand = BlockElvenSand()
		enderActuator = BlockEnderActuator()
		flugelHeadBlock = BlockHeadFlugel()
		flugelHead2Block = BlockHeadMiku()
		gaiaButton = BlockGaiaButton()
		grapesRed = Array(3) { BlockGrapeRed(it) }
		grapesRedPlanted = BlockGrapeRedPlanted()
		grapesWhite = BlockGrapeWhite()
		icicle = BlockIcicle()
		icyGeyser = BlockIcyGeyser()
		itemDisplay = BlockItemDisplay()
		irisDirt = BlockColoredDirt()
		irisLamp = BlockColoredLamp()
		irisLeaves0 = BlockColoredLeaves(0)
		irisLeaves1 = BlockColoredLeaves(1)
		irisGrass = BlockColoredGrass()
		irisPlanks = BlockColoredPlanks()
		irisSapling = BlockColoredSapling()
		irisSlabs = Array(16) { BlockColoredWoodSlab(false, it) }
		irisSlabsFull = Array(16) { BlockColoredWoodSlab(true, it) }
		irisSlabs.forEach { (it as BlockSlabMod).register() }
		irisSlabsFull.forEach { (it as BlockSlabMod).register() }
		irisStairs = Array(16) { BlockColoredWoodStairs(it) }
		irisTallGrass0 = BlockColoredDoubleGrass(0)
		irisTallGrass1 = BlockColoredDoubleGrass(1)
		irisWood0 = BlockColoredWood(0)
		irisWood1 = BlockColoredWood(1)
		irisWood2 = BlockColoredWood(2)
		irisWood3 = BlockColoredWood(3)
		itemFrame = BlockItemFrame().WIP()
		helheimBlock = BlockPattern(ModInfo.MODID, Material.rock, "HelheimBlock", AlfheimTab, hardness = -1f, harvLvl = Int.MAX_VALUE, resistance = Float.MAX_VALUE)
		kindling = BlockKindling()
		livingcobble = BlockLivingCobble()
		lootbox = BlockLootbox()
		livingwoodFunnel = BlockFunnel()
		manaAccelerator = BlockManaAccelerator()
		manaFluidBlock = BlockManaFluid()
		manaInfuser = BlockManaInfuser()
		manaReflector = BlockManaReflector()
		manaTuner = BlockManaTuner()
		nidhoggTooth = BlockNidhoggTooth()
		niflheimBlock = BlockNiflheim()
		niflheimPortal = BlockNiflheimPortal()
		onyx = BlockOnyx().WIP()
		poisonIce = BlockNiflheimIce()
		powerStone = BlockPowerStone()
		raceSelector = BlockRaceSelector()
		rainbowDirt = BlockRainbowDirt()
		rainbowFlame = BlockRainbowManaFlame()
		rainbowFlowerFloating = BlockFloatingFlowerRainbow()
		rainbowLeaves = BlockRainbowLeaves()
		rainbowGrass = BlockRainbowGrass()
		rainbowMushroom = BlockRainbowMushroom()
		rainbowPlanks = BlockRainbowPlanks()
		rainbowSlab = BlockRainbowWoodSlab(false)
		rainbowSlabFull = BlockRainbowWoodSlab(true)
		rainbowSlab.register()
		rainbowSlabFull.register()
		rainbowStairs = BlockRainbowWoodStairs()
		rainbowTallGrass = BlockRainbowDoubleGrass()
		rainbowTallFlower = BlockRainbowDoubleFlower()
		rainbowWood = BlockRainbowWood()
		realityAnchor = BlockRealityAnchor()
		redFlame = BlockRedFlame()
		redstoneRelay = BlockRedstoneRelay()
		rift = BlockRift()
		rpc = BlockRealmPowerCollector()
		sadOakLeaves = BlockSadOakLeaves()
		schemaAnnihilator = BlockSchemaAnnihilator()
		schemaController = BlockSchemaContoller()
		schemaFiller = BlockSchemaFiller()
		schemaGenerator = BlockSchemaGenerator()
		schemaMarker = BlockSchemaMarker()
		shimmerQuartz = BlockShimmerQuartz()
		shimmerQuartzSlab = BlockShimmerQuartzSlab(shimmerQuartz, false)
		shimmerQuartzSlabFull = BlockShimmerQuartzSlab(shimmerQuartz, true)
		shimmerQuartzSlab.register()
		shimmerQuartzSlabFull.register()
		shimmerQuartzStairs = BlockShimmerQuartzStairs(shimmerQuartz)
		snakeBody = BlockSnakeBody()
		snakeObject = BlockSnakeObject()
		snowGrass = BlockSnowGrass()
		snowLayer = BlockSnowLayer()
		softStorage = BlockSoftStorage()
		spire = BlockSpire()
		starBlock = BlockStar()
		starBlock2 = BlockCracklingStar()
		stalactite = BlockStalactite()
		stalagmite = BlockStalagmite()
		subspacian = BlockSubspacian()
		tradePortal = BlockTradePortal()
		treeCrafterBlock = BlockTreeCrafter("treeCrafter", irisPlanks)
		treeCrafterBlockRB = BlockTreeCrafter("treeCrafterRB", rainbowPlanks)
		treeCrafterBlockAU = BlockTreeCrafter("treeCrafterAU", auroraPlanks)
		worldTree = BlockWorldTree()
		yggFlower = BlockYggFlower()
		
		// DENDOROLOGY
		
		altLeaves = BlockAltLeaves()
		altPlanks = BlockAltPlanks()
		altSlabs = BlockAltWoodSlab(false)
		altSlabsFull = BlockAltWoodSlab(true)
		(altSlabs as BlockSlabMod).register()
		(altSlabsFull as BlockSlabMod).register()
		altStairs = Array(LibOreDict.ALT_TYPES.size - 1) { if (it == BlockAltLeaves.yggMeta) BlockYggStairs() else BlockAltWoodStairs(it) }
		altWood0 = BlockAltWood(0)
		altWood1 = BlockAltWood(1)
		
		barrierLeaves = BlockBarrierLeaves()
		barrierBerry = BlockTreeBerry(barrierLeaves, 0)
		barrierPlanks = BlockBarrierPlanks()
		barrierSapling = BlockBarrierSapling()
		barrierSlabs = BlockBarrierWoodSlab(false)
		barrierSlabFull = BlockBarrierWoodSlab(true)
		barrierSlabs.register()
		barrierSlabFull.register()
		barrierStairs = BlockBarrierWoodStairs()
		barrierWood = BlockBarrierWood()
		
		calicoLeaves = BlockCalicoLeaves()
		calicoBerry = BlockTreeBerry(calicoLeaves, 1)
		calicoPlanks = BlockCalicoPlanks()
		calicoSapling = BlockCalicoSapling()
		calicoSlabs = BlockCalicoWoodSlab(false)
		calicoSlabFull = BlockCalicoWoodSlab(true)
		calicoSlabs.register()
		calicoSlabFull.register()
		calicoStairs = BlockCalicoWoodStairs()
		calicoWood = BlockCalicoWood()
		
		circuitLeaves = BlockCircuitLeaves()
		circuitBerry = BlockTreeBerry(circuitLeaves, 2)
		circuitPlanks = BlockCircuitPlanks()
		circuitSapling = BlockCircuitSapling()
		circuitSlabs = BlockCircuitWoodSlab(false)
		circuitSlabFull = BlockCircuitWoodSlab(true)
		circuitSlabs.register()
		circuitSlabFull.register()
		circuitStairs = BlockCircuitWoodStairs()
		circuitWood = BlockCircuitWood()
		
		lightningLeaves = BlockLightningLeaves()
		lightningBerry = BlockTreeBerry(lightningLeaves, 3)
		lightningPlanks = BlockLightningPlanks()
		lightningSapling = BlockLightningSapling()
		lightningSlabs = BlockLightningWoodSlab(false)
		lightningSlabFull = BlockLightningWoodSlab(true)
		lightningSlabs.register()
		lightningSlabFull.register()
		lightningStairs = BlockLightningWoodStairs()
		lightningWood = BlockLightningWood()
		
		netherLeaves = BlockNetherLeaves()
		netherBerry = BlockTreeBerry(netherLeaves, 4)
		netherPlanks = BlockNetherPlanks()
		netherSapling = BlockNetherSapling()
		netherSlabs = BlockNetherWoodSlab(false)
		netherSlabFull = BlockNetherWoodSlab(true)
		netherSlabs.register()
		netherSlabFull.register()
		netherStairs = BlockNetherWoodStairs()
		netherWood = BlockNetherWood()
		
		sealingLeaves = BlockSealingLeaves()
		sealingBerry = BlockTreeBerry(sealingLeaves, 5)
		sealingPlanks = BlockSealingPlanks()
		sealingSapling = BlockSealingSapling()
		sealingSlabs = BlockSealingWoodSlab(false)
		sealingSlabFull = BlockSealingWoodSlab(true)
		sealingSlabs.register()
		sealingSlabFull.register()
		sealingStairs = BlockSealingWoodStairs()
		sealingWood = BlockSealingWood()
		
		tunedSapling = BlockTunedSapling()
		
		AlfheimAPI.coldBlocks.addAll(arrayOf(poisonIce))
		AlfheimAPI.warmBlocks.addAll(arrayOf(redFlame, ModBlocks.blazeBlock, netherLeaves, netherBerry, netherSapling, netherPlanks, netherSlabs, netherSlabFull, netherStairs, netherWood))
		
		registerBurnables()
		registerPaintables()
		registerFlora()
	}
	
	fun registerPaintables() {
		BotaniaAPI.registerPaintableBlock(irisWood0)
		BotaniaAPI.registerPaintableBlock(irisWood1)
		BotaniaAPI.registerPaintableBlock(irisWood2)
		BotaniaAPI.registerPaintableBlock(irisWood3)
		BotaniaAPI.registerPaintableBlock(irisPlanks)
		
		irisStairs.forEach {
			BotaniaAPI.registerPaintableBlock(it)
		}
		
		irisSlabs.forEach {
			BotaniaAPI.registerPaintableBlock(it)
		}
		
		irisSlabsFull.forEach {
			BotaniaAPI.registerPaintableBlock(it)
		}
		
		BotaniaAPI.registerPaintableBlock(rainbowWood)
		BotaniaAPI.registerPaintableBlock(rainbowPlanks)
		BotaniaAPI.registerPaintableBlock(rainbowStairs)
		BotaniaAPI.registerPaintableBlock(rainbowSlab)
		BotaniaAPI.registerPaintableBlock(rainbowSlabFull)
		
		BotaniaAPI.registerPaintableBlock(starBlock)
		BotaniaAPI.registerPaintableBlock(starBlock2)
		
		BotaniaAPI.registerPaintableBlock(Blocks.dirt)
		BotaniaAPI.registerPaintableBlock(irisDirt)
		BotaniaAPI.registerPaintableBlock(rainbowDirt)
		
		BotaniaAPI.registerPaintableBlock(ModBlocks.livingrock)
		BotaniaAPI.registerPaintableBlock(ModBlocks.dreamwood)
	}
	
	fun regOreDict() {
		registerOre("endstone", ItemStack(Blocks.end_stone))
		registerOre("grassSnow", ItemStack(snowGrass))
		registerOre("snowLayer", ItemStack(snowLayer))
		registerOre("niflStone", ItemStack(niflheimBlock))
		
		BotaniaAPI.registerSemiDisposableBlock(BLibOreDict.LIVING_ROCK)
		BotaniaAPI.registerSemiDisposableBlock("endstone")
		BotaniaAPI.registerDisposableBlock("niflStone")
		
		registerOre(LibOreDict.DRAGON_ORE, ItemStack(elvenOre))
		registerOre(LibOreDict.ELEMENTIUM_ORE, ItemStack(elvenOre, 1, 1))
		registerOre(LibOreDict.ELVEN_QUARTZ_ORE, ItemStack(elvenOre, 1, 2))
		registerOre(LibOreDict.GOLD_ORE, ItemStack(elvenOre, 1, 3))
		registerOre(LibOreDict.GOLD_ORE + "Alfheim", ItemStack(elvenOre, 1, 3))
		registerOre(LibOreDict.IFFESAL_ORE, ItemStack(elvenOre, 1, 4))
		registerOre(LibOreDict.LAPIS_ORE, ItemStack(elvenOre, 1, 5))
		registerOre(LibOreDict.LAPIS_ORE + "Alfheim", ItemStack(elvenOre, 1, 5))
		
		registerOre(LibOreDict.NIFLEUR_ORE, BlockNiflheim.NiflheimBlockMetas.ORE.stack)
		
		val quartzs = arrayOf(ModFluffBlocks.darkQuartz, ModFluffBlocks.manaQuartz, ModFluffBlocks.blazeQuartz, ModFluffBlocks.lavenderQuartz, ModFluffBlocks.redQuartz, ModFluffBlocks.elfQuartz, ModFluffBlocks.sunnyQuartz).filterNotNull()
		
		BLibOreDict.QUARTZ.forEachIndexed { id, it ->
			registerOre("block${it.capitalized()}", ItemStack(quartzs[id]))
		}
		registerOre(LibOreDict.RAINBOW_QUARTZ_BLOCK, ItemStack(shimmerQuartz))
		
		registerOre("sand", ItemStack(elvenSand))
		
		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 3))
		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 7))
		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 11))
		registerOre(LibOreDict.DREAM_WOOD_LOG, ItemStack(altWood1, 1, 15))
		
		// ################
		
		registerOre(LibOreDict.RAINBOW_FLOWER, ItemStack(rainbowGrass, 1, 2))
		registerOre(LibOreDict.RAINBOW_DOUBLE_FLOWER, ItemStack(rainbowTallFlower))
		
		registerOre(LibOreDict.MUSHROOM, ItemStack(ModBlocks.mushroom, 1, WILDCARD_VALUE))
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
		
		for (i in 0..3) {
			registerOre(LibOreDict.WOOD[i], ItemStack(irisWood0, 1, i))
			
			registerOre(LibOreDict.WOOD[i + 4], ItemStack(irisWood1, 1, i))
			
			registerOre(LibOreDict.WOOD[i + 8], ItemStack(irisWood2, 1, i))
			
			registerOre(LibOreDict.WOOD[i + 12], ItemStack(irisWood3, 1, i))
		}
		
		registerOre(LibOreDict.WOOD[16], ItemStack(rainbowWood))
		registerOre(LibOreDict.WOOD[17], ItemStack(auroraWood))
		
		for (i in 0..7) {
			registerOre(LibOreDict.LEAVES[i], ItemStack(irisLeaves0, 1, i))
			registerOre(LibOreDict.LEAVES[i + 8], ItemStack(irisLeaves1, 1, i))
		}
		
		registerOre(LibOreDict.LEAVES[16], ItemStack(rainbowLeaves))
		registerOre(LibOreDict.LEAVES[17], ItemStack(auroraLeaves))
		
		for (i in 0..5) {
			registerOre("stairWood", ItemStack(altStairs[i], 1))
			
			registerOre("treeLeaves", ItemStack(altLeaves, 1, i))
		}
		
		for (i in 0 until LibOreDict.ALT_TYPES.size - 1) {
			registerOre("slabWood", ItemStack(altSlabs, 1, i))
			
			registerOre("slabWood", ItemStack(altSlabsFull, 1, i))
		}
		
		registerOre(LibOreDict.IRIS_DIRT, ItemStack(irisDirt, 1, WILDCARD_VALUE))
		
		LibOreDict.DIRT.forEachIndexed { id, it ->
			registerOre(it, ItemStack(irisDirt, 1, id))
		}
		
		registerOre(LibOreDict.DIRT[16], ItemStack(rainbowDirt))
		registerOre(LibOreDict.IRIS_DIRT, ItemStack(rainbowDirt))
		registerOre(LibOreDict.DIRT[17], ItemStack(auroraDirt))
		registerOre(LibOreDict.IRIS_DIRT, ItemStack(auroraDirt))
		
		var t: ItemStack
		
		arrayOf(lightningWood, netherWood, sealingWood, calicoWood, circuitWood, barrierWood, altWood0).forEach {
			registerOre("logWood", ItemStack(it, 1, WILDCARD_VALUE))
		}
		
		arrayOf(irisWood0, irisWood1, irisWood2, irisWood3, rainbowWood, auroraWood).forEach {
			t = ItemStack(it, 1, WILDCARD_VALUE)
			registerOre("logWood", t)
			registerOre(IRIS_WOOD, t)
		}
		
		arrayOf(irisLeaves0, irisLeaves1, rainbowLeaves, auroraLeaves).forEach {
			t = ItemStack(it, 1, WILDCARD_VALUE)
			registerOre("treeLeaves", t)
			registerOre(LibOreDict.IRIS_LEAVES, t)
		}
		
		registerOre("plankWood", ItemStack(irisPlanks, 1, WILDCARD_VALUE))
		registerOre("plankWood", ItemStack(altPlanks, 1, WILDCARD_VALUE))
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
		
		for (i in 0..15) {
			if (i !in arrayOf(2, 6, 10, 14)) { // Yggdrasil metas
				t = ItemStack(altWood1, 1, i)
				registerOre("logWood", t)
			}
			
			if (i != BlockAltLeaves.yggMeta) {
				t = ItemStack(altLeaves, 1, i)
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
		setBurnable(altLeaves, 30, 60)
		setBurnable(altPlanks, 5, 20)
		setBurnable(altSlabs, 5, 20)
		setBurnable(altSlabsFull, 5, 20)
		altStairs.forEach { setBurnable(it, 5, 20) }
		setBurnable(altWood0, 5, 5)
		setBurnable(altWood1, 5, 5)
		
		setBurnable(amplifier, 5, 20)
		
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
		
		setBurnable(irisGrass, 60, 100)
		setBurnable(irisLeaves0, 30, 60)
		setBurnable(irisLeaves1, 30, 60)
		setBurnable(irisPlanks, 5, 20)
		irisSlabs.forEach { setBurnable(it, 5, 20) }
		irisSlabsFull.forEach { setBurnable(it, 5, 20) }
		irisStairs.forEach { setBurnable(it, 5, 20) }
		setBurnable(irisTallGrass0, 60, 100)
		setBurnable(irisTallGrass1, 60, 100)
		setBurnable(irisWood0, 5, 5)
		setBurnable(irisWood1, 5, 5)
		setBurnable(irisWood2, 5, 5)
		setBurnable(irisWood3, 5, 5)
		
		setBurnable(lightningLeaves, 30, 60)
		setBurnable(lightningPlanks, 5, 20)
		setBurnable(lightningSlabs, 5, 20)
		setBurnable(lightningSlabFull, 5, 20)
		setBurnable(lightningStairs, 5, 20)
		setBurnable(lightningWood, 5, 5)
		
		setBurnable(rainbowGrass, 60, 100)
		setBurnable(rainbowLeaves, 30, 60)
		setBurnable(rainbowPlanks, 5, 20)
		setBurnable(rainbowSlab, 5, 20)
		setBurnable(rainbowSlabFull, 5, 20)
		setBurnable(rainbowStairs, 5, 20)
		setBurnable(rainbowTallGrass, 60, 100)
		setBurnable(rainbowWood, 5, 5)
		
		setBurnable(sealingLeaves, 30, 60)
		setBurnable(sealingPlanks, 5, 20)
		setBurnable(sealingSlabs, 5, 20)
		setBurnable(sealingSlabFull, 5, 20)
		setBurnable(sealingStairs, 5, 20)
		setBurnable(sealingWood, 5, 5)
	}
	
	fun registerFlora() {
		if (AlfheimConfigHandler.gourmaryllisDifficulty > 0) {
			BotaniaAPI.subTiles[LibBlockNames.SUBTILE_GOURMARYLLIS] =
				if (AlfheimConfigHandler.gourmaryllisDifficulty == 1)
					SubTileGourmaryllisHard::class.java
				else
					SubTileGourmaryllisUltra::class.java
		}
		
		addSubFlower(SubTileAquapanthus::class.java, "aquapanthus")
		addSubFlower(SubTileBudOfYggdrasil::class.java, "budOfYggdrasil")
		addSubFlower(SubTileCrysanthermum::class.java, "crysanthermum")
		addSubFlower(SubTileOrechidEndium::class.java, "orechidEndium")
		addSubFlower(SubTileOrechidAlfarem::class.java, "orechidAlfarem")
		addSubFlower(SubTilePetronia::class.java, "petronia")
		addSubFlower(SubTileRainFlower::class.java, "rainFlower")
		addSubFlower(SubTileRattlerose::class.java, "rattlerose")
		addSubFlower(SubTileSnowFlower::class.java, "snowFlower")
		addSubFlower(SubTileStormFlower::class.java, "stormFlower")
		addSubFlower(SubTileTradescantia::class.java, "tradescantia")
		addSubFlower(SubTileWindFlower::class.java, "windFlower")
		addSubFlower(SubTileWitherAconite::class.java, "witherAconite")
		
		AlfheimAPI.addTreeVariant(irisDirt, irisWood0, irisLeaves0, 0, 3)
		AlfheimAPI.addTreeVariant(irisDirt, irisWood1, irisLeaves0, 4, 7)
		AlfheimAPI.addTreeVariant(irisDirt, irisWood2, irisLeaves1, 8, 11, 8)
		AlfheimAPI.addTreeVariant(irisDirt, irisWood3, irisLeaves1, 12, 15, 8)
		AlfheimAPI.addTreeVariant(rainbowDirt, rainbowWood, rainbowLeaves)
		AlfheimAPI.addTreeVariant(auroraDirt, auroraWood, auroraLeaves)
		AlfheimAPI.addTreeVariant(ModBlocks.altGrass, altWood0, altLeaves, 0, 3)
		AlfheimAPI.addTreeVariant(ModBlocks.altGrass, altWood1, altLeaves, 4, 5)
	}
	
	fun addSubFlower(clazz: Class<out SubTileEntity>, name: String) {
		BotaniaAPI.registerSubTile(name, clazz)
		BotaniaAPI.registerSubTileSignature(clazz, AlfheimSignature(name))
		BotaniaAPI.addSubTileToCreativeMenu(name)
		AlfheimTab.subtiles.add(name)
	}
	
	fun Block.setHarvestLevelI(toolClass: String, level: Int) = also { it.setHarvestLevel(toolClass, level) }
}