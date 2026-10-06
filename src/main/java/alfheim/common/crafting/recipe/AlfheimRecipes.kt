package alfheim.common.crafting.recipe

import alexsocol.asjlib.*
//import alexsocol.asjlib.ASJUtilities.TAG_ASJIGNORENBT // PORT: КТ-3
import alexsocol.asjlib.ASJUtilities.addOreDictRecipe
import alexsocol.asjlib.ASJUtilities.addShapelessOreDictRecipe
import alfheim.*
import alfheim.api.*
//import alfheim.api.AlfheimAPI.addInfuserRecipe // PORT: КТ-3
//import alfheim.api.crafting.recipe.* // PORT: КТ-3
import alfheim.api.lib.*
import alfheim.api.lib.LibOreDict.ALT_TYPES
import alfheim.api.lib.LibOreDict.ARUNE
import alfheim.api.lib.LibOreDict.COAL_NETHERWOOD
import alfheim.api.lib.LibOreDict.Color.*
import alfheim.api.lib.LibOreDict.DREAM_WOOD_LOG
import alfheim.api.lib.LibOreDict.DYES
import alfheim.api.lib.LibOreDict.ELVORIUM_INGOT
import alfheim.api.lib.LibOreDict.ELVORIUM_NUGGET
import alfheim.api.lib.LibOreDict.EMERALD
import alfheim.api.lib.LibOreDict.FENRIR_FUR
import alfheim.api.lib.LibOreDict.FLORAL_POWDER
import alfheim.api.lib.LibOreDict.GLOWSTONE_DUST
import alfheim.api.lib.LibOreDict.HOLY_PENDANT
import alfheim.api.lib.LibOreDict.IFFESAL_DUST
import alfheim.api.lib.LibOreDict.INFUSED_DREAM_TWIG
import alfheim.api.lib.LibOreDict.IRIS_DIRT
import alfheim.api.lib.LibOreDict.LEAVES
import alfheim.api.lib.LibOreDict.MAUFTRIUM_INGOT
import alfheim.api.lib.LibOreDict.MAUFTRIUM_NUGGET
import alfheim.api.lib.LibOreDict.MUSPELHEIM_ESSENCE
import alfheim.api.lib.LibOreDict.MUSPELHEIM_POWER_INGOT
import alfheim.api.lib.LibOreDict.NIFLHEIM_ESSENCE
import alfheim.api.lib.LibOreDict.NIFLHEIM_POWER_INGOT
import alfheim.api.lib.LibOreDict.PETAL_ANY
import alfheim.api.lib.LibOreDict.RAINBOW_DOUBLE_FLOWER
import alfheim.api.lib.LibOreDict.RAINBOW_FLOWER
import alfheim.api.lib.LibOreDict.RAINBOW_PETAL
import alfheim.api.lib.LibOreDict.RAINBOW_QUARTZ
import alfheim.api.lib.LibOreDict.REDSTONE_DUST
import alfheim.api.lib.LibOreDict.SPLINTERS_NETHERWOOD
import alfheim.api.lib.LibOreDict.SPLINTERS_THUNDERWOOD
import alfheim.api.lib.LibOreDict.TWIG_NETHERWOOD
import alfheim.api.lib.LibOreDict.TWIG_THUNDERWOOD
import alfheim.api.lib.LibOreDict.WOOD
//import alfheim.common.achievement.* // PORT: КТ-10
//import alfheim.common.block.AlfheimBlocks.airyVirus // PORT: КТ-2
import alfheim.common.block.AlfheimBlocks.alfStorage
//import alfheim.common.block.AlfheimBlocks.alfheimPortal // PORT: КТ-6
//import alfheim.common.block.AlfheimBlocks.alfheimPylon // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.altPlanks // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.altSlabs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.altStairs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.altWood0 // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.altWood1 // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.amplifier // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.animatedTorch // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.anomalyHarvester // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.anyavil // PORT: КТ-3
import alfheim.common.block.AlfheimBlocks.auroraDirt
import alfheim.common.block.AlfheimBlocks.auroraPlanks
import alfheim.common.block.AlfheimBlocks.auroraSlab
import alfheim.common.block.AlfheimBlocks.auroraStairs
import alfheim.common.block.AlfheimBlocks.auroraWood
//import alfheim.common.block.AlfheimBlocks.barrel // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.barrierPlanks // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.barrierSapling // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.barrierSlabs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.barrierStairs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.barrierWood // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.bottomlessChest // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.calicoPlanks // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.calicoSapling // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.calicoSlabs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.calicoStairs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.calicoWood // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.circuitPlanks // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.circuitSapling // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.circuitSlabs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.circuitStairs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.circuitWood // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.corporeaAutocrafter // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.corporeaInjector // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.corporeaRatBase // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.corporeaSparkBase // PORT: КТ-3
import alfheim.common.block.AlfheimBlocks.elvenOre
import alfheim.common.block.AlfheimBlocks.elvenSand
//import alfheim.common.block.AlfheimBlocks.enderActuator // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.gaiaButton // PORT: КТ-3
import alfheim.common.block.AlfheimBlocks.irisDirt
import alfheim.common.block.AlfheimBlocks.irisGrass
import alfheim.common.block.AlfheimBlocks.irisLamp
import alfheim.common.block.AlfheimBlocks.irisPlanks
//import alfheim.common.block.AlfheimBlocks.irisSapling // PORT: КТ-2
import alfheim.common.block.AlfheimBlocks.irisSlabs
import alfheim.common.block.AlfheimBlocks.irisStairs
import alfheim.common.block.AlfheimBlocks.irisWood0
import alfheim.common.block.AlfheimBlocks.irisWood1
import alfheim.common.block.AlfheimBlocks.irisWood2
import alfheim.common.block.AlfheimBlocks.irisWood3
//import alfheim.common.block.AlfheimBlocks.itemDisplay // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.itemFrame // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.kindling // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.kudzuVine // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.lightningPlanks // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.lightningSapling // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.lightningSlabs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.lightningStairs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.lightningWood // PORT: КТ-2
import alfheim.common.block.AlfheimBlocks.livingcobble
//import alfheim.common.block.AlfheimBlocks.livingwoodFunnel // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.manaAccelerator // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.manaFluidBlock // PORT: КТ-2
import alfheim.common.block.AlfheimBlocks.manaIce
//import alfheim.common.block.AlfheimBlocks.manaInfuser // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.manaReflector // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.manaTuner // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.netherPlanks // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.netherSapling // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.netherSlabs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.netherStairs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.netherWood // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.niflheimBlock // PORT: КТ-2
import alfheim.common.block.AlfheimBlocks.rainbowDirt
//import alfheim.common.block.AlfheimBlocks.rainbowFlowerFloating // PORT: КТ-3
import alfheim.common.block.AlfheimBlocks.rainbowGrass
import alfheim.common.block.AlfheimBlocks.rainbowMushroom
import alfheim.common.block.AlfheimBlocks.rainbowPlanks
import alfheim.common.block.AlfheimBlocks.rainbowSlab
import alfheim.common.block.AlfheimBlocks.rainbowStairs
import alfheim.common.block.AlfheimBlocks.rainbowWood
//import alfheim.common.block.AlfheimBlocks.realityAnchor // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.redStringObserver // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.redStringWatcher // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.redstoneAttractor // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.redstoneRelay // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.rpc // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.sealingPlanks // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.sealingSapling // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.sealingSlabs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.sealingStairs // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.sealingWood // PORT: КТ-2
import alfheim.common.block.AlfheimBlocks.shimmerQuartz
import alfheim.common.block.AlfheimBlocks.shimmerQuartzSlab
import alfheim.common.block.AlfheimBlocks.shimmerQuartzStairs
//import alfheim.common.block.AlfheimBlocks.snakeObject // PORT: КТ-2
import alfheim.common.block.AlfheimBlocks.softStorage
//import alfheim.common.block.AlfheimBlocks.spire // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.subspacian // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.tradePortal // PORT: КТ-3
//import alfheim.common.block.AlfheimBlocks.tunedSapling // PORT: КТ-2
//import alfheim.common.block.AlfheimBlocks.worldTree // PORT: КТ-3
//import alfheim.common.block.AlfheimFluffBlocks.chair // PORT: КТ-3
//import alfheim.common.block.AlfheimFluffBlocks.curtainPlacer // PORT: КТ-3
//import alfheim.common.block.AlfheimFluffBlocks.doubleBlock // PORT: КТ-3
import alfheim.common.block.AlfheimFluffBlocks.dreamwoodBarkFence
import alfheim.common.block.AlfheimFluffBlocks.dreamwoodBarkFenceGate
import alfheim.common.block.AlfheimFluffBlocks.dreamwoodFence
import alfheim.common.block.AlfheimFluffBlocks.dreamwoodFenceGate
import alfheim.common.block.AlfheimFluffBlocks.dwarfLantern
import alfheim.common.block.AlfheimFluffBlocks.dwarfPlanks
import alfheim.common.block.AlfheimFluffBlocks.dwarfPlanksSlab
import alfheim.common.block.AlfheimFluffBlocks.dwarfPlanksStairs
import alfheim.common.block.AlfheimFluffBlocks.dwarfTrapDoor
import alfheim.common.block.AlfheimFluffBlocks.elfQuartzWall
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstone
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneSlab
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneSlab2
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneStairs
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneWalls
//import alfheim.common.block.AlfheimFluffBlocks.floodLight // PORT: КТ-3
import alfheim.common.block.AlfheimFluffBlocks.livingMountain
import alfheim.common.block.AlfheimFluffBlocks.livingMountainSlab
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlab
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlab1
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlab2
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleStairs
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleStairs1
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleStairs2
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleWall
import alfheim.common.block.AlfheimFluffBlocks.livingrockBrickWall
import alfheim.common.block.AlfheimFluffBlocks.livingrockDark
import alfheim.common.block.AlfheimFluffBlocks.livingrockDarkSlabs
import alfheim.common.block.AlfheimFluffBlocks.livingrockDarkStairs
import alfheim.common.block.AlfheimFluffBlocks.livingrockDarkWalls
import alfheim.common.block.AlfheimFluffBlocks.livingwoodBarkFence
import alfheim.common.block.AlfheimFluffBlocks.livingwoodBarkFenceGate
import alfheim.common.block.AlfheimFluffBlocks.livingwoodFence
import alfheim.common.block.AlfheimFluffBlocks.livingwoodFenceGate
import alfheim.common.block.AlfheimFluffBlocks.roofTile
import alfheim.common.block.AlfheimFluffBlocks.roofTileSlabs
import alfheim.common.block.AlfheimFluffBlocks.roofTileStairs
//import alfheim.common.block.AlfheimFluffBlocks.secretGlass // PORT: КТ-3
import alfheim.common.block.AlfheimFluffBlocks.shrineGlass
import alfheim.common.block.AlfheimFluffBlocks.shrineLight
import alfheim.common.block.AlfheimFluffBlocks.shrinePanel
import alfheim.common.block.AlfheimFluffBlocks.shrinePillar
import alfheim.common.block.AlfheimFluffBlocks.shrineRock
import alfheim.common.block.AlfheimFluffBlocks.shrineRockWhiteSlab
import alfheim.common.block.AlfheimFluffBlocks.shrineRockWhiteStairs
//import alfheim.common.block.AlfheimFluffBlocks.table // PORT: КТ-3
import alfheim.common.block.AlfheimFluffBlocks.yggDecor
//import alfheim.common.block.BlockNiflheim.* // PORT: КТ-2
//import alfheim.common.block.tile.* // PORT: КТ-3
//import alfheim.common.core.asm.hook.* // PORT: КТ-3
//import alfheim.common.core.asm.hook.AlfheimHookHandler.ageLocked // PORT: КТ-3
//import alfheim.common.core.asm.hook.AlfheimHookHandler.ageLockedValue // PORT: КТ-3
//import alfheim.common.core.asm.hook.extender.* // PORT: КТ-3
//import alfheim.common.core.asm.hook.extender.ItemLensExtender.EnumAlfheimLens.* // PORT: КТ-3
import alfheim.common.core.handler.*
//import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge.* // PORT: КТ-7
//import alfheim.common.core.handler.HilarityHandler.AttributionNameChecker.getCurrentNickname // PORT: КТ-4
import alfheim.common.core.helper.*
//import alfheim.common.crafting.recipe.barrel.* // PORT: КТ-3
//import alfheim.common.crafting.recipe.tuner.* // PORT: КТ-3
import alfheim.common.crafting.recipe.workbench.*
import alfheim.common.entity.*
//import alfheim.common.integration.thaumcraft.* // PORT: выпало — Thaumcraft (SPEC, п. 7)
//import alfheim.common.integration.tinkersconstruct.* // PORT: КТ-10
import alfheim.common.item.*
//import alfheim.common.item.AlfheimItems.akashicRecords // PORT: КТ-4
import alfheim.common.item.AlfheimItems.armilla
//import alfheim.common.item.AlfheimItems.astrolabe // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.attributionBauble // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.auraRingElven // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.auraRingGod // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.auraRingPink // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.balanceCloak // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.carver // PORT: КТ-3
import alfheim.common.item.AlfheimItems.chalk
//import alfheim.common.item.AlfheimItems.cloudPendant // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.cloudPendantSuper // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.coatOfArms // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.colorOverride // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.corporeaRat // PORT: КТ-3
//import alfheim.common.item.AlfheimItems.crescentMoonAmulet // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.daolos // PORT: КТ-4
import alfheim.common.item.AlfheimItems.deathSeed
//import alfheim.common.item.AlfheimItems.dodgeRing // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elementalBoots // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elementalChestplate // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elementalHelmet // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elementalHelmetRevealing // PORT: выпало — Thaumcraft (SPEC, п. 7)
//import alfheim.common.item.AlfheimItems.elementalLeggings // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elfFirePendant // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elfIcePendant // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elvenChakram // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elvenDisguise // PORT: КТ-4
import alfheim.common.item.AlfheimItems.elvenResource
//import alfheim.common.item.AlfheimItems.elvoriumBoots // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elvoriumChestplate // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elvoriumHelmet // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.elvoriumHelmetRevealing // PORT: выпало — Thaumcraft (SPEC, п. 7)
//import alfheim.common.item.AlfheimItems.elvoriumLeggings // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.enlighter // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.excaliber // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.fenrirBoots // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.fenrirChestplate // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.fenrirCloak // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.fenrirGlove // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.fenrirHelmet // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.fenrirHelmetRevealing // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.fenrirLeggings // PORT: КТ-4
import alfheim.common.item.AlfheimItems.fireGrenade
//import alfheim.common.item.AlfheimItems.flugelSoul // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.gjallarhorn // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.gleipnir // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.goddesCharm // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.gungnir // PORT: КТ-4
import alfheim.common.item.AlfheimItems.hyperBucket
//import alfheim.common.item.AlfheimItems.invisibilityCloak // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.invisibleFlameLens // PORT: КТ-3
//import alfheim.common.item.AlfheimItems.irisSeeds // PORT: КТ-2
//import alfheim.common.item.AlfheimItems.livingrockPickaxe // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.lootInterceptor // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.manaGlove // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.manaMirrorImba // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.manaRingElven // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.manaRingGod // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.manaRingPink // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.manaStone // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.manaStoneGreater // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.mask // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.mjolnir // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.moonlightBow // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.multibauble // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.paperBreak // PORT: КТ-7
//import alfheim.common.item.AlfheimItems.peacePipe // PORT: КТ-7
//import alfheim.common.item.AlfheimItems.pendantSuperIce // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.pixieAttractor // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.priestCloak // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.priestEmblem // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.priestRingHeimdall // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.priestRingNjord // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.priestRingSif // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rationBelt // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.realitySword // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.resonator // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.ringFeedFlower // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.ringSpider // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodBlackHole // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodClicker // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodColorfulSkyDirt // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodFlameStar // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodGrass // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodInterdiction // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodLightning // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodMuspelheim // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodNiflheim // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodPortal // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodPrismatic // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodRedstone // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.rodSuperExchange // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.serenade // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.snowHelmet // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.snowHelmetRevealing // PORT: выпало — Thaumcraft (SPEC, п. 7)
import alfheim.common.item.AlfheimItems.soulHorn
//import alfheim.common.item.AlfheimItems.soulSword // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.spatiotemporalRing // PORT: КТ-4
import alfheim.common.item.AlfheimItems.splashPotion
//import alfheim.common.item.AlfheimItems.starPlacer2 // PORT: КТ-3
//import alfheim.common.item.AlfheimItems.subspaceSpear // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.terraHoe // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.thinkingHand // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.toolbelt // PORT: КТ-4
import alfheim.common.item.AlfheimItems.triquetrum
//import alfheim.common.item.AlfheimItems.volcanoHelmet // PORT: КТ-4
//import alfheim.common.item.AlfheimItems.volcanoHelmetRevealing // PORT: выпало — Thaumcraft (SPEC, п. 7)
//import alfheim.common.item.AlfheimItems.warBanner // PORT: КТ-4
import alfheim.common.item.AlfheimItems.wiltedLotus
import alfheim.common.item.block.*
//import alfheim.common.item.equipment.tool.ItemResonator.Companion.dilated // PORT: КТ-4
//import alfheim.common.item.equipment.tool.ItemResonator.Companion.persistent // PORT: КТ-4
//import alfheim.common.item.equipment.tool.ItemResonator.Companion.unlimited // PORT: КТ-4
import alfheim.common.item.material.*
import alfheim.common.item.material.ElvenFoodMetas.*
import alfheim.common.item.material.ElvenResourcesMetas.*
// PORT: импорты 1.20.1 (MAPPING.md, «Рецепты»). GameRegistry, Ore Dictionary и рецепты 1.7.10 — прослойка
// alfheim.port.legacy (Recipes1710.kt); BotaniaAPI и имена Ore Dictionary Botania 1.7.10 — alfheim.port.legacy.botania;
// поля ванилы 1.20.1 — заглавными, на месте (Items.SUGAR). Импорты для рецептов следующих КТ (сущности, чары, NBT,
// плитки маны и списки рецептов Botania; Tinkers — КТ-10; Thaumcraft выпал, SPEC п. 7) добавляют их КТ
import alfheim.port.legacy.*
import alfheim.port.legacy.OreDictionary.WILDCARD_VALUE
import alfheim.port.legacy.botania.BotaniaAPI
import alfheim.port.legacy.botania.BotaniaBlocks1710
import alfheim.port.legacy.botania.LibOreDict.*
import alfheim.port.legacy.botania.ancientWill
import net.minecraft.world.item.*
import net.minecraft.world.level.block.*
import vazkii.botania.common.block.BotaniaBlocks.*
import vazkii.botania.common.item.BotaniaItems
import vazkii.botania.common.item.BotaniaItems.*
// PORT: так же в Botania 1.20.1 называются предметы кварца; у автора эти имена — блоки (ModFluffBlocks)
import vazkii.botania.common.block.BotaniaBlocks.elfQuartz

object AlfheimRecipes {
	
	/* PORT: КТ-3 — рецепты инфузора маны
	lateinit var recipeElvorium: RecipeManaInfuser
	lateinit var recipeMauftrium: RecipeManaInfuser
	lateinit var recipeManaStone: RecipeManaInfuser
	lateinit var recipeManaStoneGreater: RecipeManaInfuser
	*/
	
	lateinit var recipeAlfheimPortal: IRecipe
	lateinit var recipesAltPlanks: List<IRecipe>
	lateinit var recipesAltPlanksFromSlabs: List<IRecipe>
	lateinit var recipesAltSlabs: List<IRecipe>
	lateinit var recipesAltStairs: List<IRecipe>
	lateinit var recipeAmplifier: IRecipe
	lateinit var recipeAnimatedTorch: IRecipe
	lateinit var recipeAnomalyHarvester: IRecipe
	lateinit var recipeAnyavil: IRecipe
	lateinit var recipesApothecary: List<IRecipe>
	lateinit var recipeAstrolabe: IRecipe
	lateinit var recipeArmilla: IRecipe
	lateinit var recipeAttribution: IRecipe
	lateinit var recipeAuraRingElven: IRecipe
	lateinit var recipeAuraRingGod: IRecipe
	lateinit var recipeAuraRingPink: IRecipe
	lateinit var recipeAuroraDirt: IRecipe
	lateinit var recipeAuroraPlanks: IRecipe
	lateinit var recipeAuroraPlanksFromSlabs: IRecipe
	lateinit var recipeAuroraSlabs: IRecipe
	lateinit var recipeAuroraStairs: IRecipe
	lateinit var recipeAutocrafter: IRecipe
	lateinit var recipeBalanceCloak: IRecipe
	lateinit var recipeBarrel: IRecipe
	lateinit var recipeBarrierPlanks: IRecipe
	lateinit var recipeBarrierSlabs: IRecipe
	lateinit var recipeBarrierStairs: IRecipe
	lateinit var recipeCalicoPlanks: IRecipe
	lateinit var recipeCalicoSlabs: IRecipe
	lateinit var recipeCalicoStairs: IRecipe
	lateinit var recipeCarver: IRecipe
	lateinit var recipeChakramEnder: IRecipe
	lateinit var recipeChakramThunder: IRecipe
	lateinit var recipeCircuitPlanks: IRecipe
	lateinit var recipeCircuitSlabs: IRecipe
	lateinit var recipeCircuitStairs: IRecipe
	lateinit var recipeCleanPylon: IRecipe
	lateinit var recipeCloakHeimdall: IRecipe
	lateinit var recipeCloakLoki: IRecipe
	lateinit var recipeCloakNjord: IRecipe
	lateinit var recipeCloakOdin: IRecipe
	lateinit var recipeCloakSif: IRecipe
	lateinit var recipeCloakThor: IRecipe
	lateinit var recipeCloudPendant: IRecipe
	lateinit var recipeCloudPendantSuper: IRecipe
	lateinit var recipesCoatOfArms: List<IRecipe>
	lateinit var recipeColorOverride: IRecipe
	lateinit var recipesColoredDirt: List<IRecipe>
	lateinit var recipesColoredPlanks: List<IRecipe>
	lateinit var recipesColoredPlanksFromSlabs: List<IRecipe>
	lateinit var recipesColoredSlabs: List<IRecipe>
	lateinit var recipesColoredStairs: List<IRecipe>
	lateinit var recipeCreationPylon: IRecipe
	lateinit var recipeCrescentAmulet: IRecipe
	lateinit var recipeDeathSeed: IRecipe
	lateinit var recipesDecor: List<IRecipe>
	lateinit var recipesDecorCurtain: IRecipe
	lateinit var recipesDecorDouble: IRecipe
	lateinit var recipesDecorGlass: IRecipe
	lateinit var recipesDecorLight: IRecipe
	lateinit var recipeDisguiseBelt: IRecipe
	lateinit var recipeDodgeRing: IRecipe
	lateinit var recipeElementalBoots: IRecipe
	lateinit var recipeElementalChestplate: IRecipe
	lateinit var recipeElementalHelmet: IRecipe
	lateinit var recipeElementalLeggings: IRecipe
	lateinit var recipeElvenPylon: IRecipe
	lateinit var recipeElvoriumBoots: IRecipe
	lateinit var recipeElvoriumChestplate: IRecipe
	lateinit var recipeElvoriumHelmet: IRecipe
	lateinit var recipeElvoriumLeggings: IRecipe
	lateinit var recipeElvoriumPylon: IRecipe
	lateinit var recipeEnderActuator: IRecipe
	lateinit var recipeEnlighter: IRecipe
	lateinit var recipeFenrirBoots: IRecipe
	lateinit var recipeFenrirChestplate: IRecipe
	lateinit var recipeFenrirCloak: IRecipe
	lateinit var recipeFenrirGlove: IRecipe
	lateinit var recipeFenrirHelmet: IRecipe
	lateinit var recipeFenrirLeggings: IRecipe
	lateinit var recipeFurnace: IRecipe
	lateinit var recipeGaiaButton: IRecipe
	lateinit var recipeGrenade: IRecipe
	lateinit var recipeGoddessCharm: IRecipe
	lateinit var recipeHyperBucket: IRecipe
	lateinit var recipeInfernalPlanks: IRecipe
	lateinit var recipeInfernalSlabs: IRecipe
	lateinit var recipeInfernalStairs: IRecipe
	lateinit var recipeInfernalTwig: IRecipe
	lateinit var recipeInjector: IRecipe
	lateinit var recipeInvisibilityCloak: IRecipe
	lateinit var recipesItemDisplay: List<IRecipe>
	lateinit var recipeItemFrame: IRecipe
	lateinit var recipeJellybread: IRecipe
	lateinit var recipeJellyfish: IRecipe
	lateinit var recipeJug: IRecipe
	lateinit var recipeKindling: IRecipe
	lateinit var recipeLamp: IRecipe
	lateinit var recipesLeafDyes: List<IRecipe>
	lateinit var recipeLembas: IRecipe
	lateinit var recipeLensLinkback: IRecipe
	lateinit var recipeLensMessenger: IRecipe
	lateinit var recipeLensPurification: IRecipe
	lateinit var recipeLensPush: IRecipe
	lateinit var recipeLensSmelt: IRecipe
	lateinit var recipeLensSuperconductor: IRecipe
	lateinit var recipeLensTrack: IRecipe
	lateinit var recipeLensTripwire: IRecipe
	lateinit var recipeLensUnlink: IRecipe
	lateinit var recipeLivingcobble: IRecipe
	lateinit var recipeLivingCobbleMossy: IRecipe
	lateinit var recipesLivingDecor: List<IRecipe>
	lateinit var recipeLivingrockPickaxe: IRecipe
	lateinit var recipeLivingwoodFunnel: IRecipe
	lateinit var recipeLootInterceptor: IRecipe
	lateinit var recipeLuminizer2: IRecipe
	lateinit var recipeLuminizer3: IRecipe
	lateinit var recipeManaAccelerator: IRecipe
	lateinit var recipeManaInfuser: IRecipe
	lateinit var recipeManaInfusionCore: IRecipe
	lateinit var recipeManaMirrorImba: IRecipe
	lateinit var recipeManaReflector: IRecipe
	lateinit var recipeManaRingElven: IRecipe
	lateinit var recipeManaRingGod: IRecipe
	lateinit var recipeManaRingPink: IRecipe
	lateinit var recipeManaTuner: IRecipe
	lateinit var recipeManaweaveGlove: IRecipe
	lateinit var recipeMultibauble: IRecipe
	lateinit var recipeMuspelheimPendant: IRecipe
	lateinit var recipeMuspelheimPowerIngot: IRecipe
	lateinit var recipeNiflheimPendant: IRecipe
	lateinit var recipeNiflheimPowerIngot: IRecipe
	lateinit var recipeOpenChest: IRecipe
	lateinit var recipePaperBreak: IRecipe
	lateinit var recipePeacePipe: IRecipe
	lateinit var recipePendantSuperIce: IRecipe
	lateinit var recipePixieAttractor: IRecipe
	lateinit var recipePriestOfHeimdall: IRecipe
	lateinit var recipePriestOfLoki: IRecipe
	lateinit var recipePriestOfNjord: IRecipe
	lateinit var recipePriestOfOdin: IRecipe
	lateinit var recipePriestOfSif: IRecipe
	lateinit var recipePriestOfThor: IRecipe
	lateinit var recipeQuandex: IRecipe
	lateinit var recipeQuandexBase: IRecipe
	lateinit var recipesRainbowPetal: List<IRecipe>
	lateinit var recipeRainbowPetalBlock: IRecipe
	lateinit var recipeRainbowPetalGrinding: IRecipe
	lateinit var recipeRationBelt: IRecipe
	lateinit var recipeRealityAnchor: IRecipe
	lateinit var recipesRealmCore: List<IRecipe>
	lateinit var recipesRealmFrame: List<IRecipe>
	lateinit var recipeRedstoneAttractor: IRecipe
	lateinit var recipesRedstoneRoot: List<IRecipe>
	lateinit var recipeRedStringObserver: IRecipe
	lateinit var recipeRedStringWatcher: IRecipe
	lateinit var recipeRelicCleaner: IRecipe
	lateinit var recipeResonator: IRecipe
	lateinit var recipeRingFeedFlower: IRecipe
	lateinit var recipeRingSpider: IRecipe
	lateinit var recipeRodBlackhole: IRecipe
	lateinit var recipeRodClicker: IRecipe
	lateinit var recipesRodColoredSkyDirt: List<IRecipe>
	lateinit var recipeRodFlame: IRecipe
	lateinit var recipeRodGreen: IRecipe
	lateinit var recipeRodInterdiction: IRecipe
	lateinit var recipeRodLightning: IRecipe
	lateinit var recipeRodMuspelheim: IRecipe
	lateinit var recipeRodNiflheim: IRecipe
	lateinit var recipeRodPortal: IRecipe
	lateinit var recipeRodPrismatic: IRecipe
	lateinit var recipeRodRedstone: IRecipe
	lateinit var recipeRodSuperExchange: IRecipe
	lateinit var recipesRoofTile: List<IRecipe>
	lateinit var recipeRunicChalk: IRecipe
	lateinit var recipeSaveIvy: IRecipe
	lateinit var recipeSealingPlanks: IRecipe
	lateinit var recipeSealingSlabs: IRecipe
	lateinit var recipeSealingStairs: IRecipe
	lateinit var recipeSerenade: IRecipe
	lateinit var recipeShimmerQuartz: IRecipe
	lateinit var recipeSoulHorn: IRecipe
	lateinit var recipeSoulSword: IRecipe
	lateinit var recipeSparkBase: IRecipe
	lateinit var recipeSpatiotemporal: IRecipe
	lateinit var recipeSpire: IRecipe
	lateinit var recipeSplashPotions: IRecipe
	lateinit var recipesStar: List<IRecipe>
	lateinit var recipesStar2: List<IRecipe>
	lateinit var recipeStencil: IRecipe
	lateinit var recipeSword: IRecipe
	lateinit var recipeTerraHarvester: IRecipe
	lateinit var recipeThinkingHand: IRecipe
	lateinit var recipeThunderousPlanks: IRecipe
	lateinit var recipeThunderousSlabs: IRecipe
	lateinit var recipeThunderousStairs: IRecipe
	lateinit var recipeThunderousTwig: IRecipe
	lateinit var recipeToolbelt: IRecipe
	lateinit var recipeTradePortal: IRecipe
	lateinit var recipeTriquetrum: IRecipe
	lateinit var recipeUberSpreader: IRecipe
	lateinit var recipeWarBanner: IRecipe
	lateinit var recipeWorldTree: IRecipe
	
	/* PORT: КТ-3 — рецепты типов Botania и Альфхейма, древесная кузня, тюнер маны
	lateinit var recipeInfusedDreamTwig: RecipeManaInfusion
	lateinit var recipesPastoralSeeds: List<RecipeManaInfusion>
	lateinit var recipeRedstoneRelay: RecipeManaInfusion
	lateinit var recipeRiftShard: RecipeManaInfusion
	
	lateinit var recipeAquapanthus: RecipePetals
	lateinit var recipeBud: RecipePetals
	lateinit var recipeCrysanthermum: RecipePetals
	lateinit var recipeOrechidAlfarem: RecipePetals
	lateinit var recipeOrechidEndium: RecipePetals
	lateinit var recipePetronia: RecipePetals
	lateinit var recipeRainFlower: RecipePetals
	lateinit var recipeRattlerose: RecipePetals
	lateinit var recipeSnowFlower: RecipePetals
	lateinit var recipeStormFlower: RecipePetals
	lateinit var recipeTradescantia: RecipePetals
	lateinit var recipeWitherAconite: RecipePetals
	lateinit var recipeWindFlower: RecipePetals
	
	lateinit var recipeInterdimensional: RecipeElvenTrade
	
	lateinit var recipeDreamwood: RecipePureDaisy
	lateinit var recipeIrisSapling: RecipePureDaisyExclusion
	
	lateinit var recipeMuspelheimRune: RecipeRuneAltar
	lateinit var recipeNiflheimRune: RecipeRuneAltar
	lateinit var recipeRealityRune: RecipeRuneAltar
	lateinit var recipeSnakeEgg: RecipeRuneAltar
	
	lateinit var recipeLightningTree: RecipeTreeCrafting
	lateinit var recipeInfernalTree: RecipeTreeCrafting
	lateinit var recipeSealingTree: RecipeTreeCrafting
	lateinit var recipeCalicoTree: RecipeTreeCrafting
	lateinit var recipeCircuitTree: RecipeTreeCrafting
	lateinit var recipeBarrierTree: RecipeTreeCrafting
	lateinit var recipeKudzu: RecipeTreeCrafting
	
	lateinit var tuningAnomalyStabilization: TunerIncantation<TileAnomaly>
	lateinit var tuningAnomalyPackaging: TunerIncantation<TileAnomaly>
	lateinit var tuningElementalSeer: TunerIncantation<ItemStack>
	lateinit var tuningGrowthStop: TunerIncantation<EntityAgeable>
	lateinit var tuningResonatorDillation: TunerIncantation<ItemStack>
	lateinit var tuningResonatorPersistence: TunerIncantation<ItemStack>
	lateinit var tuningResonatorUnlimit: TunerIncantation<ItemStack>
//	lateinit var tuningCats: List<TunerIncantation<EntityOcelot>>
//	lateinit var tuningCow: TunerIncantation<EntityCow>
	lateinit var tuningSaplings: Array<TunerIncantationIO>
	lateinit var tuningSlimeSize: TunerIncantation<EntityLivingBase>
	lateinit var tuningMagmaSize: TunerIncantation<EntityLivingBase>
	lateinit var tuningElementalSlimeSize: IncantationElementalSlimeGrowth
	var tuningTaintSize: TunerIncantation<EntityLivingBase>? = null
	var tuningGelatSize: TunerIncantation<EntityLivingBase>? = null
	var tuningThaumWand: TunerIncantationIO? = null
	
	lateinit var tuningAkashicRecords: TunerIncantationIO
	lateinit var tuningDaolos: TunerIncantationIO
	lateinit var tuningExcaliber: TunerIncantationIO
	lateinit var tuningFlugelEye: TunerIncantationIO
	lateinit var tuningFlugelSoul: TunerIncantationIO
	lateinit var tuningGjallarhorn: TunerIncantationIO
	lateinit var tuningGleipnir: TunerIncantationIO
	lateinit var tuningGungnir: TunerIncantationIO
	lateinit var tuningHeimdallRing: TunerIncantationIO
	lateinit var tuningInfiniteFruit: TunerIncantationIO
	lateinit var tuningKingKey: TunerIncantationIO
	lateinit var tuningLokiRing: TunerIncantationIO
	lateinit var tuningMjolnir: TunerIncantationIO
	lateinit var tuningMoonlightBow: TunerIncantationIO
	lateinit var tuningNjordRing: TunerIncantationIO
	lateinit var tuningOdinRing: TunerIncantationIO
	lateinit var tuningSifRing: TunerIncantationIO
	lateinit var tuningSpearSubspace: TunerIncantationIO
	lateinit var tuningTankMask: TunerIncantationIO
	lateinit var tuningThorRing: TunerIncantationIO
	*/
	
	init {
		// PORT: в 1.20.1 рецепты — данные: объект выполняет только генерация данных (alfheim.port.data.AlfheimRecipeProvider),
		// вызовы 1.7.10 записывают рецепты в прослойку (Recipes1710.kt). Рецепты вещей, которых ещё нет в порту,
		// закомментированы с номером КТ и включаются вместе с вещами
		registerCraftingRecipes()
		registerShapelessRecipes()
		registerSmeltingRecipes()
//		registerManaInfuserRecipes() // PORT: КТ-3
//		registerDendrology() // PORT: КТ-3
		registerRecipes()
//		registerTuning() // PORT: КТ-3
//		banRetrades() // PORT: КТ-3
//		extendESM() // PORT: КТ-7
	}
	
	private fun registerCraftingRecipes() {
		/* PORT: КТ-2, КТ-3, КТ-4, КТ-6 — рецепты вещей этих КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(airyVirus, 3),
		                 "RGR", "EVE", "AAA",
		                 'R', REDSTONE_DUST,
		                 'G', GLOWSTONE_DUST,
		                 'E', MUSPELHEIM_ESSENCE,
		                 'V', virus,
		                 'A', ItemStack(altGrass, 1, 3))
		
		addOreDictRecipe(ItemStack(airyVirus, 3, 1),
		                 "IMI", "ECE", "AAA",
		                 'I', IFFESAL_DUST,
		                 'M', MANA_POWDER,
		                 'E', NIFLHEIM_ESSENCE,
		                 'C', cellBlock,
		                 'A', ItemStack(altGrass, 1, 4))
		
		addOreDictRecipe(ItemStack(alfheimPortal, 1),
						 "DPD", "GSG", "DTD",
						 'D', DREAM_WOOD,
						 'G', spark,
						 'P', RUNE[8],
						 'S', rainbowRod,
						 'T', ItemStack(lens, 1, 18))
		recipeAlfheimPortal = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(alfheimPylon),
						 " P ", "EDE", " P ",
						 'P', PIXIE_DUST,
						 'E', ELEMENTIUM,
						 'D', DRAGONSTONE)
		recipeElvenPylon = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(alfheimPylon, 1, 1),
						 " E ", "EPE", "III",
						 'E', ELVORIUM_NUGGET,
						 'P', ItemStack(alfheimPylon),
						 'I', IFFESAL_DUST)
		recipeElvoriumPylon = BotaniaAPI.getLatestAddedRecipe()
		
		for (p in (PETAL.plusElement(RAINBOW_PETAL)))
			addOreDictRecipe(ItemStack(altar, 1, 9),
							 "SPS", " C ", "CCC",
							 'S', livingcobbleSlab,
							 'P', p,
							 'C', ItemStack(livingcobble))
		recipesApothecary = BotaniaAPI.getLatestAddedRecipes(17)
		
		addOreDictRecipe(ItemStack(altar),
						 "SPS", " C ", "CCC",
						 'S', "slabCobblestone",
						 'P', RAINBOW_PETAL,
						 'C', "cobblestone")
		ModCraftingRecipes.recipesApothecary?.add(BotaniaAPI.getLatestAddedRecipe())
		
		for (i in 0 until ALT_TYPES.size - 1)
			addRecipe(ItemStack(altSlabs, 6, i),
					  "PPP",
					  'P', ItemStack(altPlanks, 1, i))
		recipesAltSlabs = BotaniaAPI.getLatestAddedRecipes(6)
		
		for (i in 0 until ALT_TYPES.size - 1)
			addOreDictRecipe(ItemStack(altStairs[i], 4), true,
							 "P  ", "PP ", "PPP",
							 'P', ItemStack(altPlanks, 1, i))
		recipesAltStairs = BotaniaAPI.getLatestAddedRecipes(6)
		
		addRecipe(ItemStack(amplifier),
				  " N ", "NRN", " N ",
				  'N', ItemStack(noteblock),
				  'R', ItemStack(sealingPlanks))
		recipeAmplifier = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(animatedTorch),
						 "P", "T",
						 'T', redstone_torch,
						 'P', MANA_POWDER)
		recipeAnimatedTorch = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(anomalyHarvester),
		                 "TCT", "CPC", "TET",
		                 'T', TERRASTEEL_NUGGET,
		                 'C', ManaInfusionCore.stack,
						 'P', ItemStack(pylon),
						 'E', ItemStack(alfStorage))
		recipeAnomalyHarvester = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(anyavil),
						 "BGB", " P ", "EDE",
						 'P', PIXIE_DUST,
						 'E', ELEMENTIUM,
						 'D', DRAGONSTONE,
						 'B', ItemStack(storage, 1, 2),
						 'G', ItemStack(storage, 1, 4))
		recipeAnyavil = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(astrolabe),
						 " ES", "EEE", "SED",
						 'E', ELEMENTIUM,
						 'S', LIFE_ESSENCE,
						 'D', DREAM_WOOD)
		recipeAstrolabe = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addOreDictRecipe(ItemStack(armilla),
		                 "TN ", "NEN", " NT",
		                 'E', ELVORIUM_INGOT,
		                 'N', ELVORIUM_NUGGET,
		                 'T', LIVINGWOOD_TWIG)
		recipeArmilla = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2, КТ-3, КТ-4, КТ-7 — рецепты вещей этих КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(attributionBauble),
						 "S S", "Q Q", " G ",
						 'G', "ingotGold",
						 'Q', "gemQuartz",
						 'S', MANA_STRING)
		recipeAttribution = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(auraRingPink),
		                 "CE ", "E E", " E ",
		                 'C', ManaInfusionCore.stack,
		                 'E', ELEMENTIUM)
		recipeAuraRingPink = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addRecipe(ItemStack(auroraSlab, 6),
				  "PPP",
				  'P', ItemStack(auroraPlanks))
		recipeAuroraSlabs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(auroraStairs, 4), true,
						 "P  ", "PP ", "PPP",
						 'P', ItemStack(auroraPlanks))
		recipeAuroraStairs = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2, КТ-3, КТ-4, КТ-7 — рецепты вещей этих КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(balanceCloak),
						 "WWW", "EWE", "ESE",
						 'W', ItemStack(wool, 1, 8),
						 'E', EMERALD,
						 'S', LIFE_ESSENCE)
		recipeBalanceCloak = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(barrel),
						 "PSP", "P P", "PPP",
						 'P', ItemStack(planks, 1, 5),
						 'S', ItemStack(wooden_slab, 1, 5))
		recipeBarrel = BotaniaAPI.getLatestAddedRecipe()
		
		addRecipe(ItemStack(barrierSlabs, 6),
				  "PPP",
				  'P', ItemStack(barrierPlanks))
		recipeBarrierSlabs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(barrierStairs, 4), true,
						 "P  ", "PP ", "PPP",
						 'P', ItemStack(barrierPlanks))
		recipeBarrierStairs = BotaniaAPI.getLatestAddedRecipe()
		
		addRecipe(ItemStack(calicoSlabs, 6),
				  "PPP",
				  'P', ItemStack(calicoPlanks))
		recipeCalicoSlabs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(calicoStairs, 4), true,
						 "P  ", "PP ", "PPP",
						 'P', ItemStack(calicoPlanks))
		recipeCalicoStairs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(carver),
		                 "  N", " T ", "T  ",
		                 'N', MANASTEEL_NUGGET,
						 'T', LIVINGWOOD_TWIG)
		recipeCarver = BotaniaAPI.getLatestAddedRecipe()
		
		addRecipe(ItemStack(circuitSlabs, 6),
				  "PPP",
				  'P', ItemStack(circuitPlanks))
		recipeCircuitSlabs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(circuitStairs, 4), true,
						 "P  ", "PP ", "PPP",
						 'P', ItemStack(circuitPlanks))
		recipeCircuitStairs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(pylon, 1, 2),
						 "EEE", "EPE", "EEE",
						 'E', LIFE_ESSENCE,
						 'P', ItemStack(alfheimPylon, 1, 2))
		recipeCleanPylon = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(cloudPendant),
						 "US ", "S S", "MSA",
						 'U', RUNE[6],
						 'S', MANA_STRING,
						 'M', MANA_STEEL,
						 'A', RUNE[3])
		recipeCloudPendant = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(cloudPendantSuper),
						 "GEG", "GPG", "WSW",
						 'G', ghast_tear,
						 'E', ELEMENTIUM,
						 'P', ItemStack(cloudPendant),
						 'W', ItemStack(wool),
						 'S', LIFE_ESSENCE)
		recipeCloudPendantSuper = BotaniaAPI.getLatestAddedRecipe()
		
		LEAVES.forEachIndexed { id, it ->
			addOreDictRecipe(ItemStack(coatOfArms, 1, id),
							 "LLL", "LSL", "LLL",
							 'L', it,
							 'S', MANA_STRING)
		}
		recipesCoatOfArms = BotaniaAPI.getLatestAddedRecipes(18)
		
		addOreDictRecipe(ItemStack(colorOverride),
						 "PE ", "E E", " E ",
						 'P', ItemStack(lens, 1, 14), // Paintslinger's Lens
						 'E', ELEMENTIUM)
		recipeColorOverride = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(corporeaAutocrafter),
						 " H ", "RSI", " F ",
						 'H', corporeaInjector,
						 'R', corporeaRetainer,
						 'I', corporeaInterceptor,
						 'F', corporeaFunnel,
						 'S', corporeaSpark)
		recipeAutocrafter = BotaniaAPI.getLatestAddedRecipe()
		
		recipeCreationPylon = ShapedOreRecipeLearnable(ABYSS_TRUTH, ItemStack(alfheimPylon, 1, 3),
		                                               " I ", "MPN", " G ",
		                                               'I', MAUFTRIUM_NUGGET,
		                                               'M', ARUNE[1],
		                                               'N', ARUNE[2],
		                                               'P', ItemStack(pylon, 1, 2),
		                                               'G', ItemStack(manaStoneGreater))
		CraftingManager.getInstance().recipeList.add(recipeCreationPylon)
		
		addOreDictRecipe(ItemStack(crescentMoonAmulet),
						 "  M", "MS ", "RM ",
						 'M', MAUFTRIUM_NUGGET,
						 'R', RUNE[13], // wrath
						 'S', ItemStack(manaResource, 1, 12))
		recipeCrescentAmulet = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		// PORT: череп ванилы 3 (голова игрока) → Items.PLAYER_HEAD
		addOreDictRecipe(ItemStack(deathSeed),
		                 " H ", "GIG", " R ",
		                 'H', ItemStack(Items.PLAYER_HEAD),
		                 'G', LIFE_ESSENCE,
		                 'I', keepIvy,
		                 'R', RUNE[12]) // sloth
		recipeDeathSeed = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2, КТ-3, КТ-4, КТ-7 — рецепты вещей этих КТ, включаются вместе с ними; Thaumcraft выпал (SPEC, п. 7)
		addOreDictRecipe(ItemStack(chair, 3),
		                 "CCC", " W ", " S ",
		                 'C', ItemStack(chair, 1, 7),
		                 'W', ItemStack(log),
		                 'S', ItemStack(wooden_slab))
		
		addOreDictRecipe(ItemStack(chair, 3, 1),
		                 "CCC", "W W", " S ",
		                 'C', ItemStack(chair, 1, 7),
		                 'W', ItemStack(log))
		
		addOreDictRecipe(ItemStack(chair, 1, 3),
		                 "F", "C",
		                 'F', fence_gate,
		                 'C', ItemStack(chair, 1, 1))
		
		addOreDictRecipe(ItemStack(chair, 5, 6),
		                 "C",
		                 'C', ItemStack(chair, 1, 7))
		
		addOreDictRecipe(ItemStack(chair, 3, 7),
		                 "PPP", "WWW",
		                 'P', wooden_pressure_plate,
		                 'W', ItemStack(log))
		
		addOreDictRecipe(ItemStack(table, 3),
		                 "SSS", " W ", " W ",
		                 'W', ItemStack(log),
		                 'S', ItemStack(wooden_slab))
		recipesDecor = BotaniaAPI.getLatestAddedRecipes(6)
		
		addOreDictRecipe(ItemStack(curtainPlacer, 8),
		                 "WWW", "WDW", "WRW",
		                 'W', ItemStack(wool, 1, 15),
		                 'R', ItemStack(wool, 1, 14),
		                 'D', dispenser)
		recipesDecorCurtain = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(doubleBlock, 8),
		                 "SSS", "SPS", "SSS",
		                 'S', secretGlass,
		                 'P', PLACEHOLDER)
		recipesDecorDouble = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(secretGlass, 6),
		                 "GG", "SG", "GG",
		                 'G', "blockGlassBlack",
		                 'S', ItemStack(stonebrick))
		recipesDecorGlass = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(floodLight),
		                 "SSS", " F ", "LLG",
		                 'L', ItemStack(livingwood, 1, 4),
		                 'G', ItemStack(shrineLight, 1, 4),
		                 'F', livingwoodFence,
		                 'S', livingwoodPlankSlab)
		recipesDecorLight = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(dodgeRing),
						 "EM ", "M M", " MR",
						 'E', EMERALD,
						 'M', MANA_STEEL,
						 'R', RUNE[3]) // air
		recipeDodgeRing = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elvenDisguise),
		                 "RL ", "L L", "QLM",
		                 'R', RUNE[9], // lust
		                 'L', leather,
		                 'Q', RAINBOW_QUARTZ,
		                 'M', RUNE[8]) // mana
		recipeDisguiseBelt = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elementalHelmet),
						 "RTR", "DPD", " M ",
						 'R', RUNE[0], // water
						 'T', INFUSED_DREAM_TWIG,
						 'D', IFFESAL_DUST,
						 'P', elementiumHelm,
						 'M', RUNE[8]) // mana
		recipeElementalHelmet = BotaniaAPI.getLatestAddedRecipe()
		
		if (Botania.thaumcraftLoaded)
			addOreDictRecipe(ItemStack(elementalHelmetRevealing),
							 "RTR", "DPD", " M ",
							 'R', RUNE[0],
							 'T', INFUSED_DREAM_TWIG,
							 'D', IFFESAL_DUST,
							 'P', elementiumHelmRevealing,
							 'M', RUNE[8])
		
		addOreDictRecipe(ItemStack(elementalChestplate),
						 "RTR", "DPD", " M ",
						 'R', RUNE[2],
						 'T', INFUSED_DREAM_TWIG,
						 'D', IFFESAL_DUST,
						 'P', elementiumChest,
						 'M', RUNE[8])
		recipeElementalChestplate = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elementalLeggings),
						 "RTR", "DPD", " M ",
						 'R', RUNE[1],
						 'T', INFUSED_DREAM_TWIG,
						 'D', IFFESAL_DUST,
						 'P', elementiumLegs,
						 'M', RUNE[8])
		recipeElementalLeggings = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elementalBoots),
						 "RTR", "DPD", " M ",
						 'R', RUNE[3],
						 'T', INFUSED_DREAM_TWIG,
						 'D', IFFESAL_DUST,
						 'P', elementiumBoots,
						 'M', RUNE[8])
		recipeElementalBoots = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elfFirePendant),
						 "  N", "NP ", "RN ",
						 'N', MAUFTRIUM_NUGGET,
						 'R', ARUNE[1],
						 'P', lavaPendant)
		recipeMuspelheimPendant = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elfIcePendant),
						 "  N", "NP ", "RN ",
						 'N', MAUFTRIUM_NUGGET,
						 'R', ARUNE[2],
						 'P', icePendant)
		recipeNiflheimPendant = BotaniaAPI.getLatestAddedRecipe()
		
		addRecipe(ItemStack(rainbowFlowerFloating),
		          "F", "S", "D",
		          'F', ItemStack(rainbowGrass, 1, 3),
		          'S', ItemStack(grassSeeds),
		          'D', ItemStack(dirt))
		ModCraftingRecipes.recipesMiniIsland?.add(BotaniaAPI.getLatestAddedRecipe())
		
		recipeRealityAnchor = ShapedOreRecipeLearnable(ABYSS_TRUTH, ItemStack(realityAnchor),
		                                               "NBN", "CMC", "NRN",
		                                               'N', MAUFTRIUM_NUGGET,
		                                               'B', rainbowRod,
		                                               'R', ARUNE[0],
		                                               'C', InterdimensionalGatewayCore.stack,
		                                               'M', beacon)
		CraftingManager.getInstance().recipeList.add(recipeRealityAnchor)
		
		recipesRealmCore = listOf(
		ShapedOreRecipeLearnable(ABYSS_TRUTH, ItemStack(rpc),
		                         "IBI", "SFS", "IRI",
		                         'I', IFFESAL_DUST,
		                         'B', rainbowRod,
		                         'S', RiftShardMuspelheim.stack,
		                         'F', ItemStack(rpc, 1, 2),
		                         'R', ARUNE[0])
		,
		ShapedOreRecipeLearnable(ABYSS_TRUTH, ItemStack(rpc, 1, 1),
		                         "IBI", "SFS", "IRI",
		                         'I', IFFESAL_DUST,
		                         'B', rainbowRod,
		                         'S', RiftShardNiflheim.stack,
		                         'F', ItemStack(rpc, 1, 3),
		                         'R', ARUNE[0])
		                         )
		CraftingManager.getInstance().recipeList.addAll(recipesRealmCore)
		
		recipesRealmFrame = listOf(
		ShapedOreRecipeLearnable(ABYSS_TRUTH, ItemStack(rpc, 1, 2),
		                         "NMN", "PQP", "NMN",
		                         'N', netherrack,
		                         'M', MAUFTRIUM_NUGGET,
		                         'Q', "gemQuartz",
		                         'P', MUSPELHEIM_POWER_INGOT)
		,
		ShapedOreRecipeLearnable(ABYSS_TRUTH, ItemStack(rpc, 1, 3),
		                         "NMN", "PQP", "NMN",
		                         'N', ItemStack(niflheimBlock),
		                         'M', MAUFTRIUM_NUGGET,
		                         'Q', Nifleur.stack,
		                         'P', NIFLHEIM_POWER_INGOT)
		                         )
		CraftingManager.getInstance().recipeList.addAll(recipesRealmFrame)
		*/
		
		addOreDictRecipe(DasRheingold.stack,
						 "SCS", "CGC", "SCS",
						 'G', "ingotGold",
						 'S', LIFE_ESSENCE,
						 'C', ItemStack(spellCloth, 1, WILDCARD_VALUE))
		recipeRelicCleaner = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-4 — рецепты вещей этой КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(resonator),
		                 "MEC", "VDE", "LB ",
		                 'M', MAUFTRIUM_INGOT,
		                 'E', ELVORIUM_INGOT,
		                 'C', ModItems.cacophonium,
		                 'V', lever,
		                 'D', DREAMWOOD_TWIG,
		                 'L', LIVINGWOOD_TWIG,
		                 'B', wooden_button)
		recipeResonator = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addOreDictRecipe(ManaInfusionCore.stack,
						 "PGP", "GDG", "PGP",
						 'D', PIXIE_DUST,
						 'G', "ingotGold",
						 'P', IFFESAL_DUST)
		recipeManaInfusionCore = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(MuspelheimPowerIngot.stack,
						 " S ", "SIS", " S ",
						 'S', MUSPELHEIM_ESSENCE,
						 'I', ELVORIUM_INGOT)
		recipeMuspelheimPowerIngot = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(NiflheimPowerIngot.stack,
						 " S ", "SIS", " S ",
						 'S', NIFLHEIM_ESSENCE,
						 'I', ELVORIUM_INGOT)
		recipeNiflheimPowerIngot = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними; Thaumcraft выпал (SPEC, п. 7)
		addOreDictRecipe(ItemStack(bottomlessChest),
						 "DDD", "D D", "D D",
						 'D', ItemStack(dreamwood, 1, 1))
		recipeOpenChest = BotaniaAPI.getLatestAddedRecipe()
		
		for (i in 0..15)
			for (j in 0..15) {
				addOreDictRecipe(ItemTwigWandExtender.forColors(i, j, true),
								 " AS", " SB", "S  ",
								 'A', PETAL[i],
								 'B', PETAL[j],
								 'S', DREAMWOOD_TWIG)
			}
		
		addOreDictRecipe(ItemStack(thornChakram, 2, 1),
		                 "SSS", "CPC", "SSS",
		                 'S', SPLINTERS_NETHERWOOD,
		                 'C', ItemStack(thornChakram),
		                 'P', PIXIE_DUST)
		ModCraftingRecipes.recipeFireChakram?.let { CraftingManager.getInstance().recipeList.remove(it) }
		ModCraftingRecipes.recipeFireChakram = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elvenChakram, 2),
		                 "SSS", "CPC", "SSS",
		                 'S', SPLINTERS_THUNDERWOOD,
		                 'C', ItemStack(thornChakram),
		                 'P', PIXIE_DUST)
		recipeChakramThunder = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elvenChakram, 2, 1),
		                 "EEE", "CPC", "EEE",
		                 'E', ender_pearl,
		                 'C', ItemStack(thornChakram),
		                 'P', PIXIE_DUST)
		recipeChakramEnder = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elvoriumHelmet),
		                 "TRT", "EHE", "CMC",
		                 'T', INFUSED_DREAM_TWIG,
		                 'R', ARUNE[0],
		                 'E', ELVORIUM_INGOT,
		                 'H', elementiumHelm,
		                 'C', ManaInfusionCore.stack,
		                 'M', MAUFTRIUM_INGOT)
		recipeElvoriumHelmet = BotaniaAPI.getLatestAddedRecipe()
		
		if (Botania.thaumcraftLoaded) {
			addOreDictRecipe(ItemStack(elvoriumHelmetRevealing),
			                 "TRT", "EHE", "CMC",
			                 'T', INFUSED_DREAM_TWIG,
			                 'R', ARUNE[0],
			                 'E', ELVORIUM_INGOT,
			                 'H', elementiumHelmRevealing,
			                 'C', ManaInfusionCore.stack,
			                 'M', MAUFTRIUM_INGOT)
			recipeElvoriumHelmet = BotaniaAPI.getLatestAddedRecipe()
		}
		
		addOreDictRecipe(ItemStack(elvoriumChestplate),
						 "TRT", "EPE", "CMC",
						 'T', INFUSED_DREAM_TWIG,
						 'R', ARUNE[0],
						 'E', ELVORIUM_INGOT,
						 'P', elementiumChest,
						 'C', ManaInfusionCore.stack,
						 'M', MAUFTRIUM_INGOT)
		recipeElvoriumChestplate = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elvoriumLeggings),
						 "TRT", "EPE", "CMC",
						 'T', INFUSED_DREAM_TWIG,
						 'R', ARUNE[0],
						 'E', ELVORIUM_INGOT,
						 'P', elementiumLegs,
						 'C', ManaInfusionCore.stack,
						 'M', MAUFTRIUM_INGOT)
		recipeElvoriumLeggings = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(elvoriumBoots),
						 "TRT", "EPE", "CMC",
						 'T', INFUSED_DREAM_TWIG,
						 'R', ARUNE[0],
						 'E', ELVORIUM_INGOT,
						 'P', elementiumBoots,
						 'C', ManaInfusionCore.stack,
						 'M', MAUFTRIUM_INGOT)
		recipeElvoriumBoots = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(enderActuator),
						 "PBE", "BCB", "ELP",
						 'P', MANA_PEARL,
						 'B', DYES[15],
						 'E', ender_eye,
						 'L', ItemStack(wiltedLotus, 1, 1),
						 'C', ender_chest)
		recipeEnderActuator = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(enlighter),
						 "GGG", "GSG", "EEE",
						 'G', managlassPane,
						 'S', starPlacer2,
						 'E', ELVORIUM_INGOT)
		recipeEnlighter = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(flowerBag),
						 "WPW", "W W", " W ",
						 'P', PETAL_ANY,
						 'W', ItemStack(wool, 1, 32767))
		ModCraftingRecipes.recipeFlowerBag?.let { CraftingManager.getInstance().recipeList.remove(it) }
		ModCraftingRecipes.recipeFlowerBag = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(fenrirBoots),
						 "G G", "F F", "FBF",
						 'G', "dustGlowstone",
						 'F', FENRIR_FUR,
						 'B', leather_boots)
		recipeFenrirBoots = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(fenrirChestplate),
						 "G G", "FCF", "FFF",
						 'G', "dustGlowstone",
						 'F', FENRIR_FUR,
						 'C', leather_chestplate)
		recipeFenrirChestplate = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(fenrirCloak),
		                "WSW", "FSF", "GFG",
		                'G', "dustGlowstone",
		                'F', FENRIR_FUR,
		                'W', ItemStack(wool, 1, 7),
						'S', sugar)
		recipeFenrirCloak = BotaniaAPI.getLatestAddedRecipe()
		
		if (AlfheimCore.TravellersGearLoaded) {
			addOreDictRecipe(ItemStack(fenrirGlove),
			                 "FG ", "GVF", " FG",
			                 'G', "dustGlowstone",
			                 'F', FENRIR_FUR,
							 'V', ItemStack(findItem("TravellersGear", "simpleGear"), 1, 5))
		} else {
			addOreDictRecipe(ItemStack(fenrirGlove),
			                 "  F", "FFG", " GF",
			                 'G', "dustGlowstone",
			                 'F', FENRIR_FUR)
		}
		recipeFenrirGlove = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(fenrirHelmet),
						 "GFG", "FHF", " F ",
						 'G', "dustGlowstone",
						 'F', FENRIR_FUR,
						 'H', leather_helmet)
		recipeFenrirHelmet = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(fenrirLeggings),
						 "FFF", "GLG", "F F",
						 'G', "dustGlowstone",
						 'F', FENRIR_FUR,
						 'L', leather_leggings)
		recipeFenrirLeggings = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(furnace, 1, 8),
						 "CCC", "C C", "CCC",
						 'C', ItemStack(livingcobble))
		recipeFurnace = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		// PORT: ModItems.manaResource 14 (слиток Гайи) → BotaniaItems.gaiaIngot (MAPPING.md, «Botania»)
		addOreDictRecipe(ItemStack(gaiaIngot),
		                 " S ", "SES", " S ",
		                 'S', LIFE_ESSENCE,
		                 'E', ELVORIUM_INGOT)
		
		addOreDictRecipe(ItemStack(hyperBucket),
						 "III", "EBE", "MMM",
						 'B', openBucket,
						 'E', ELVORIUM_INGOT,
						 'I', IFFESAL_DUST,
						 'M', MAUFTRIUM_NUGGET)
		recipeHyperBucket = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2, КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(goddesCharm),
		                 " P ", " P ", "WDS",
		                 'P', PETAL[6],
		                 'W', RUNE[0],
		                 'D', MANA_DIAMOND,
		                 'S', RUNE[4])
		recipeGoddessCharm = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(invisibilityCloak),
						 "PWP", "GWG", "GJG",
						 'P', PRISMARINE_SHARD,
						 'W', ItemStack(wool),
						 'G', manaGlass,
						 'J', MANA_PEARL)
		recipeInvisibilityCloak = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		// PORT: поля ванилы 1.20.1 — заглавными (Blocks.REDSTONE_LAMP)
		addOreDictRecipe(ItemStack(irisLamp),
						 " B ", "BLB", " B ",
						 'L', ItemStack(Blocks.REDSTONE_LAMP),
						 'B', DYES[16])
		recipeLamp = BotaniaAPI.getLatestAddedRecipe()
		
		// PORT: вариант metadata — блок массива (SPEC, Р-5): ItemStack(irisPlanks, 1, i) → ItemStack(irisPlanks[i], 1)
		for (i in 0..15)
			addRecipe(ItemStack(irisPlanks[i], 1), "P", "P", 'P', ItemStack(irisSlabs[i], 1))
		addRecipe(ItemStack(rainbowPlanks), "P", "P", 'P', ItemStack(rainbowSlab))
		recipesColoredPlanksFromSlabs = BotaniaAPI.getLatestAddedRecipes(17)
		
		addRecipe(ItemStack(auroraPlanks), "P", "P", 'P', ItemStack(auroraSlab))
		recipeAuroraPlanksFromSlabs = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2 — рецепты вещей этой КТ, включаются вместе с ними
		for (i in 0..6)
			addRecipe(ItemStack(altPlanks, 1, i), "P", "P", 'P', ItemStack(altSlabs, 1, i))
		recipesAltPlanksFromSlabs = BotaniaAPI.getLatestAddedRecipes(6)
		
		*/
		
		for (i in 0..15)
			addRecipe(ItemStack(irisSlabs[i], 6),
					  "PPP",
					  'P', ItemStack(irisPlanks[i], 1))
		addRecipe(ItemStack(rainbowSlab, 6),
				  "PPP",
				  'P', ItemStack(rainbowPlanks))
		recipesColoredSlabs = BotaniaAPI.getLatestAddedRecipes(17)
		
		for (i in 0..15)
			addOreDictRecipe(ItemStack(irisStairs[i], 4), true,
							 "P  ", "PP ", "PPP",
							 'P', ItemStack(irisPlanks[i], 1))
		addOreDictRecipe(ItemStack(rainbowStairs, 4), true,
						 "P  ", "PP ", "PPP",
						 'P', ItemStack(rainbowPlanks))
		recipesColoredStairs = BotaniaAPI.getLatestAddedRecipes(17)
		
		/* PORT: КТ-3 — рецепты вещей этой КТ, включаются вместе с ними
		arrayOf(MANASTEEL_NUGGET, TERRASTEEL_NUGGET).forEachIndexed { id, it ->
			addOreDictRecipe(ItemStack(itemDisplay, 1, id),
							 "N", "W",
							 'N', it,
							 'W', ItemStack(livingwoodSlab))
		}
		recipesItemDisplay = BotaniaAPI.getLatestAddedRecipes(2)
		
		arrayOf(ELEMENTIUM_NUGGET, ELVORIUM_NUGGET, MAUFTRIUM_NUGGET).forEachIndexed { id, it ->
			addOreDictRecipe(ItemStack(itemDisplay, 1, 2 + id),
			                 "N", "W",
			                 'N', it,
			                 'W', ItemStack(dreamwoodSlab))
		}
		*/
		
		addOreDictRecipe(Jug.stack,
						 "B B", "B B", " B ",
						 'B', Items.BRICK)
		recipeJug = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-3 — рецепты вещей этой КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(laputaShard),
						 "SFS", "PDP", "ASE",
						 'S', LIFE_ESSENCE,
						 'D', DRAGONSTONE,
						 'F', ItemStack(rainbowFlowerFloating),
						 'P', PRISMARINE_SHARD,
						 'A', RUNE[3],
						 'E', RUNE[2])
		ModCraftingRecipes.recipesLaputaShard?.add(BotaniaAPI.getLatestAddedRecipe())
		
		addOreDictRecipe(ItemStack(lens, 1, MESSENGER.meta),
		                 " P ", "PLP", " P ",
		                 'P', paper,
		                 'L', ItemStack(lens))
		recipeLensMessenger = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(lens, 1, SUPERCONDUCTOR.meta),
						 "IWI", "RLR", "IWI",
						 'L', ItemStack(lens),
						 'W', RUNE[0], // water
						 'R', RUNE[13], // wrath
						 'I', IFFESAL_DUST)
		recipeLensSuperconductor = BotaniaAPI.getLatestAddedRecipe()
		
		CraftingManager.getInstance().recipeList.add(RecipeLensPurification)
		recipeLensPurification = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addOreDictRecipe(Lembas.stack,
						 " LB", "NBN", "BL ",
						 'N', Nectar.stack,
						 'L', GrapeLeaf.stack,
						 'B', Items.BREAD)
		recipeLembas = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2, КТ-3, КТ-4, КТ-7 — рецепты вещей этих КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(kindling),
						 " S ", "SBS", " S ",
						 'B', "powderBlaze",
						 'S', MANA_STRING)
		recipeKindling = BotaniaAPI.getLatestAddedRecipe()
		
		addRecipe(ItemStack(lightningSlabs, 6),
				  "PPP",
				  'P', ItemStack(lightningPlanks))
		recipeThunderousSlabs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(lightningStairs, 4), true,
						 "P  ", "PP ", "PPP",
						 'P', ItemStack(lightningPlanks))
		recipeThunderousStairs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(livingrockPickaxe),
						 "LLL", " S ", " S ",
						 'L', ItemStack(livingcobble),
						 'S', "stickWood")
		recipeLivingrockPickaxe = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(livingwoodFunnel),
						 "L L", "LCL", " L ",
						 'L', LIVING_WOOD, 'C', ItemStack(chest))
		
		recipeLivingwoodFunnel = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(lootInterceptor),
						 "IHI", "DID",
						 'I', IFFESAL_DUST,
						 'H', blackHoleTalisman,
						 'D', DREAM_WOOD)
		recipeLootInterceptor = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(manaAccelerator),
						 "MLM", "LDL",
						 'D', MANA_DIAMOND,
						 'L', LIVING_ROCK,
						 'M', MANA_PEARL)
		recipeManaAccelerator = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(manaInfuser),
						 "DCD", "IRI", "SSS",
						 'C', ManaInfusionCore.stack,
						 'D', DRAGONSTONE,
						 'I', ELEMENTIUM,
						 'R', rainbowRod,
						 'S', ItemStack(livingrock, 1, 4))
		recipeManaInfuser = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(manaMirrorImba),
						 "IMI", "EWE", "IMI",
						 'M', MAUFTRIUM_INGOT,
						 'E', ELVORIUM_INGOT,
						 'I', IFFESAL_DUST,
						 'W', ItemStack(lens, 1, 18))
		recipeManaMirrorImba = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(manaReflector, 2),
		                 "MG ", "TDG", "WTM",
		                 'M', MANA_STEEL,
		                 'D', MANA_DIAMOND,
		                 'G', manaGlass,
		                 'T', DREAMWOOD_TWIG,
		                 'W', DREAM_WOOD)
		recipeManaReflector = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(manaRingElven),
		                 "IS ", "S S", " S ",
		                 'S', ELVORIUM_INGOT,
		                 'I', manaStone)
		recipeManaRingElven = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(manaRingPink),
		                 "DEI", "E E", "IE ",
		                 'E', ELEMENTIUM,
		                 'D', DRAGONSTONE,
		                 'I', IFFESAL_DUST)
		recipeManaRingPink = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(manaTuner),
		                 "ISI", " P ", "MRM",
		                 'I', IFFESAL_DUST,
						 'S', livingrockSlab,
						 'P', ItemStack(alfheimPylon),
						 'M', MAUFTRIUM_INGOT,
						 'R', runeAltar)
		recipeManaTuner = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(manaGlove),
						 "MM ", "MPM", " M ",
						 'M', MANAWEAVE_CLOTH,
						 'P', MANA_PEARL)
		recipeManaweaveGlove = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(multibauble),
						 "QT ", "T E", " E ",
						 'E', ELEMENTIUM,
						 'T', TERRA_STEEL,
						 'Q', ItemStack(manaquartz, 1, 5))
		recipeMultibauble = BotaniaAPI.getLatestAddedRecipe()
		
		addRecipe(ItemStack(netherSlabs, 6),
				  "PPP",
				  'P', ItemStack(netherPlanks))
		recipeInfernalSlabs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(netherStairs, 4), true,
						 "P  ", "PP ", "PPP",
						 'P', ItemStack(netherPlanks))
		recipeInfernalStairs = BotaniaAPI.getLatestAddedRecipe()
		
		addRecipe(NetherwoodTwig.stack,
				  "P", "P",
				  'P', ItemStack(netherWood))
		recipeInfernalTwig = BotaniaAPI.getLatestAddedRecipe()
		
		recipePaperBreak = ShapelessOreRecipe(ItemStack(paperBreak, 4), leather, ItemStack(wooden_sword, 1, WILDCARD_VALUE))
		
		recipePeacePipe = ShapedOreRecipe(ItemStack(peacePipe),
										  "  P", " SD", "S  ",
										  'S', stick,
										  'D', DYES[1],
										  'P', ItemStack(planks, 1, 5))
		
		if (AlfheimConfigHandler.enableMMO) addMMORecipes()
		
		addOreDictRecipe(ItemStack(superLavaPendant),
						 "MMM", "MPM", "ISI",
						 'M', blaze_rod,
						 'P', lavaPendant,
						 'I', nether_brick,
						 'S', MUSPELHEIM_ESSENCE)
		ModCraftingRecipes.recipeSuperLavaPendant?.let { CraftingManager.getInstance().recipeList.remove(it) }
		ModCraftingRecipes.recipeSuperLavaPendant = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(pendantSuperIce),
						 "MMM", "MPM", "ISI",
						 'M', MANA_STEEL,
						 'P', icePendant,
						 'I', packed_ice,
						 'S', NIFLHEIM_ESSENCE)
		recipePendantSuperIce = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(pixieAttractor),
						 "EDE", "EPE", " S ",
						 'D', DRAGONSTONE,
						 'E', ELEMENTIUM,
						 'P', PIXIE_DUST,
						 'S', RUNE[2])
		recipePixieAttractor = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		// PORT: ModBlocks.platform 1 (спектральная платформа), ModBlocks.dreamwood 3 и 4 (обрамлённое и узорчатое сонное
		// дерево) → spectralPlatform, dreamwoodFramed, dreamwoodPatternFramed; ModBlocks.livingwood 0 → LIVING_WOOD — тег, как
		// в рецептах Botania 1.20.1 (MAPPING.md, «Botania»)
		addOreDictRecipe(ItemStack(spectralPlatform, 2),
		                 "343", "0E0",
		                 '0', LIVING_WOOD,
		                 '3', ItemStack(dreamwoodFramed),
		                 '4', ItemStack(dreamwoodPatternFramed),
		                 'E', LIFE_ESSENCE)
		
		/* PORT: КТ-2, КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(priestCloak),
						 "WGW", "TWT", "ITI",
						 'W', ItemStack(wool, 1, 15),
						 'G', ItemStack(wool, 1, 5),
						 'T', TERRASTEEL_NUGGET,
						 'I', TWIG_THUNDERWOOD)
		recipeCloakThor = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestCloak, 1, 1),
						 "WGW", "NLN", "TOT",
						 'W', ItemStack(wool, 1, 15),
						 'G', ItemStack(wool, 1, 4),
						 'N', "nuggetGold",
						 'L', ItemStack(livingwood, 1, 5),
						 'O', overgrowthSeed,
						 'T', LIVINGWOOD_TWIG)
		recipeCloakSif = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestCloak, 1, 2),
						 "WGW", "MPM", "TST",
						 'W', ItemStack(wool),
						 'G', ItemStack(wool, 1, 3),
						 'P', PRISMARINE_SHARD,
						 'M', MANASTEEL_NUGGET,
						 'S', ItemStack(potionitem, 1, 16418),
						 'T', INFUSED_DREAM_TWIG)
		recipeCloakNjord = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestCloak, 1, 3),
						 "WGW", "NKN", "TNT",
						 'W', ItemStack(wool, 1, 15),
						 'G', ItemStack(wool, 1, 1),
						 'N', "nuggetGold",
						 'K', kindling,
						 'T', TWIG_NETHERWOOD)
		recipeCloakLoki = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestCloak, 1, 4),
						 "RGB", "QPQ", "IQI",
						 'R', ItemStack(wool, 1, 14),
						 'G', ItemStack(wool, 1, 13),
						 'B', ItemStack(wool, 1, 11),
						 'P', ender_eye,
						 'Q', RAINBOW_QUARTZ,
						 'I', bifrostPerm)
		recipeCloakHeimdall = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestCloak, 1, 5),
						 "WWW", "MGM", "IMI",
						 'W', ItemStack(wool, 1, 14),
						 'G', DasRheingold.stack,
						 'M', MAUFTRIUM_NUGGET,
						 'I', IFFESAL_DUST)
		recipeCloakOdin = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestEmblem),
						 "EGE", "TAT", " W ",
						 'E', ENDER_AIR_BOTTLE,
						 'T', TERRASTEEL_NUGGET,
						 'G', LIFE_ESSENCE,
						 'W', RUNE[13], // Wrath
						 'A', HOLY_PENDANT)
		recipePriestOfThor = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(corporeaRat),
		                 " SM", " DH", "I  ",
		                 'S', ItemStack(corporeaSpark),
		                 'M', manaMirrorImba,
		                 'D', INFUSED_DREAM_TWIG,
		                 'H', enderHand,
		                 'I', corporeaIndex)
		recipeQuandex = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(corporeaRatBase),
		                 "OAO", "DSD", "OFO",
		                 'O', obsidian,
		                 'A', ItemStack(manaResource, 1, 15),
		                 'D', DRAGONSTONE,
		                 'S', ItemStack(corporeaSpark),
		                 'F', corporeaFunnel)
		recipeQuandexBase = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestEmblem, 1, 1),
						 "DGD", "NAN", " P ",
						 'D', DRAGONSTONE,
						 'N', "nuggetGold",
						 'G', LIFE_ESSENCE,
						 'P', RUNE[2], // Earth
						 'A', HOLY_PENDANT)
		recipePriestOfSif = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestEmblem, 1, 2),
						 "RGR", "NAN", " P ",
						 'P', RUNE[15], // Pride
						 'N', MANASTEEL_NUGGET,
						 'G', LIFE_ESSENCE,
						 'R', RUNE[3], // Air
						 'A', HOLY_PENDANT)
		recipePriestOfNjord = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestEmblem, 1, 3),
						 "WGO", "NAN", " P ",
						 'O', RUNE[6],
						 'W', RUNE[7],
						 'N', ELEMENTIUM_NUGGET,
						 'G', LIFE_ESSENCE,
						 'P', RUNE[8], // Mana
						 'A', HOLY_PENDANT)
		recipePriestOfLoki = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestEmblem, 1, 4),
						 "BGB", "NAN", " P ",
						 'B', bifrostPerm,
						 'G', LIFE_ESSENCE,
						 'N', ELVORIUM_NUGGET,
						 'P', RUNE[14], // Envy
						 'A', HOLY_PENDANT)
		recipePriestOfHeimdall = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(priestEmblem, 1, 5),
						 "RGR", "NAN", " P ",
						 'R', DasRheingold.stack,
						 'G', LIFE_ESSENCE,
						 'N', MAUFTRIUM_NUGGET,
						 'P', RUNE[9], // Lust
						 'A', HOLY_PENDANT)
		recipePriestOfOdin = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(pylon, 1, 2),
						 " E ", "TPT", " E ",
						 'T', TERRASTEEL_NUGGET,
						 'E', overgrowthSeed,
						 'P', ItemStack(alfheimPylon))
		ModCraftingRecipes.recipeGaiaPylon?.let { CraftingManager.getInstance().recipeList.remove(it) }
		ModCraftingRecipes.recipeGaiaPylon = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rationBelt),
						 "GL ", "L L", "ELS",
						 'G', RUNE[10],
						 'L', leather,
						 'E', ELEMENTIUM,
						 'S', RUNE[12])
		recipeRationBelt = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(realitySword),
						 " M ", "MRM", " S ",
						 'M', MAUFTRIUM_INGOT,
						 'R', ARUNE[0],
						 'S', ItemStack(manaResource, 1, 3))
		recipeSword = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(terraHoe),
		                 "TTO", " LN", "L  ",
		                 'T', TERRA_STEEL,
		                 'O', overgrowthSeed,
		                 'L', LIVINGWOOD_TWIG,
		                 'N', TERRASTEEL_NUGGET)
		recipeTerraHarvester = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(ringFeedFlower),
						 "D  ", " R ", "  P",
						 'D', distributor,
						 'R', manaRing,
						 'P', pump)
		recipeRingFeedFlower = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(ringSpider),
						 "RMW", "M M", "WM ",
						 'R', RUNE[11], // greed
						 'M', MANA_STEEL,
						 'W', web)
		recipeRingSpider = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodBlackHole),
						 "NID", " HI", "T N",
						 'N', ELEMENTIUM_NUGGET,
						 'I', ELEMENTIUM,
						 'D', DRAGONSTONE,
						 'H', blackHoleTalisman,
						 'T', INFUSED_DREAM_TWIG)
		recipeRodBlackhole = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodClicker),
						 "GEG", " TE", "T G",
						 'G', DRAGONSTONE,
						 'T', INFUSED_DREAM_TWIG,
						 'E', ELEMENTIUM_NUGGET)
		recipeRodClicker = BotaniaAPI.getLatestAddedRecipe()
		
		val dirts = Array(16) { ItemStack(irisDirt, 1, it) } + ItemStack(rainbowDirt) + ItemStack(auroraDirt)
		dirts.forEachIndexed { id, it ->
			addOreDictRecipe(ItemStack(rodColorfulSkyDirt, 1, id),
							 " PD", " RP", "S  ",
							 'D', it,
							 'R', ItemStack(skyDirtRod, 1),
							 'P', PIXIE_DUST,
							 'S', DRAGONSTONE)
		}
		recipesRodColoredSkyDirt = BotaniaAPI.getLatestAddedRecipes(18)
		
		addOreDictRecipe(ItemStack(rodInterdiction),
						 " AS", " DA", "P  ",
						 'P', RUNE[15], // Pride
						 'A', RUNE[3], // Air
						 'S', ItemStack(tornadoRod),
						 'D', DREAMWOOD_TWIG)
		recipeRodInterdiction = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodMuspelheim),
						 " MR", " BM", "B  ",
						 'M', MAUFTRIUM_INGOT,
						 'R', ARUNE[1],
						 'B', blaze_rod)
		recipeRodMuspelheim = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodFlameStar, 1),
						 " EW", " SD", "S  ",
						 'E', SPLINTERS_NETHERWOOD,
						 'D', COAL_NETHERWOOD,
						 'S', TWIG_NETHERWOOD,
						 'W', RUNE[1]) // Fire
		
		recipeRodFlame = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodGrass),
						 "  R", " D ", "S  ",
						 'D', dirtRod,
						 'R', RUNE[4],
						 'S', grassSeeds)
		recipeRodGreen = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodNiflheim),
						 " MR", " BM", "B  ",
						 'M', MAUFTRIUM_INGOT,
						 'R', ARUNE[2],
						 'B', blaze_rod)
		recipeRodNiflheim = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodLightning, 1),
						 " EW", " SD", "S  ",
						 'E', SPLINTERS_THUNDERWOOD,
						 'D', DRAGONSTONE,
						 'S', TWIG_THUNDERWOOD,
						 'W', RUNE[13]) // Wrath
		
		recipeRodLightning = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodPortal),
						 "IER", " FN", "B I",
						 'E', end_stone,
						 'R', ARUNE[0],
						 'F', rainbowRod,
						 'N', netherrack,
						 'I', IFFESAL_DUST,
						 'B', blaze_rod)
		recipeRodPortal = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodPrismatic),
						 " GB", " DG", "D  ",
						 'G', "glowstone",
						 'B', DYES[16],
						 'D', DREAMWOOD_TWIG)
		recipeRodPrismatic = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodRedstone),
						 " TC", " LT", "R  ",
						 'T', redstone_torch,
						 'C', comparator,
						 'L', LIVINGWOOD_TWIG,
						 'R', RED_STRING)
		recipeRodRedstone = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(rodSuperExchange),
						 " LT", "SRD", "WE ",
						 'L', elvenSand,
						 'T', TERRA_STEEL,
						 'S', soul_sand,
						 'R', exchangeRod,
						 'D', ItemStack(niflheimBlock),
						 'W', INFUSED_DREAM_TWIG,
						 'E', end_stone)
		recipeRodSuperExchange = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addOreDictRecipe(ItemStack(chalk),
						 "  M", " Q ", "Q  ",
						 'M', MAUFTRIUM_NUGGET,
						 'Q', QUARTZ[0])
		recipeRunicChalk = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		addRecipe(ItemStack(sealingSlabs, 6),
				  "PPP",
				  'P', ItemStack(sealingPlanks))
		recipeSealingSlabs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(sealingStairs, 4), true,
						 "P  ", "PP ", "PPP",
						 'P', ItemStack(sealingPlanks))
		recipeSealingStairs = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(serenade), true,
		                 " GS", "GSL", "SL ",
		                 'G', LIFE_ESSENCE,
						 'S', MANA_STRING,
						 'L', livingwoodPlankSlab)
		recipeSerenade = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addOreDictRecipe(ItemStack(soulHorn),
						 "MIM", "EIE", " E ",
						 'M', MAUFTRIUM_INGOT,
						 'E', ELVORIUM_INGOT,
						 'I', IFFESAL_DUST)
		recipeSoulHorn = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2, КТ-3, КТ-4, КТ-7 — рецепты вещей этих КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(soulSword),
						 "  G", "TG ", "DT ",
						 'G', GAIA_INGOT,
						 'T', TERRA_STEEL,
						 'D', INFUSED_DREAM_TWIG)
		recipeSoulSword = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(spark),
						 " P ", "BNB", " P ",
						 'B', PIXIE_DUST,
						 'P', PETAL_ANY,
						 'N', "nuggetGold")
		
		addOreDictRecipe(ItemStack(spark),
		                 " P ", "BNB", " P ",
		                 'B', ItemStack(blaze_powder),
		                 'P', PETAL_ANY,
		                 'N', "nuggetGold")
		
		ModCraftingRecipes.recipesSpark?.let {
			it.forEach(CraftingManager.getInstance().recipeList::remove)
			it.clear()
			it.addAll(BotaniaAPI.getLatestAddedRecipes(2))
		}
		
		addOreDictRecipe(ItemStack(spatiotemporalRing),
						 "GES", "E E", "SE ",
						 'G', hourglass,
						 'E', ELEMENTIUM,
						 'S', LIFE_ESSENCE)
		recipeSpatiotemporal = BotaniaAPI.getLatestAddedRecipe()
		
		recipeSpire = ShapedOreRecipeLearnable(ABYSS_TRUTH, ItemStack(spire),
		                                       "SGS", "SGS", "CTC",
		                                       'S', RiftShardGinnungagap.stack,
		                                       'G', GAIA_INGOT,
		                                       'C', InterdimensionalGatewayCore.stack,
		                                       'T', ItemStack(storage, 1, 1))
		CraftingManager.getInstance().recipeList.add(recipeSpire)
		
		addOreDictRecipe(ItemStack(spreader),
						 "WWW", "GP ", "WWW",
						 'W', LIVING_WOOD,
						 'P', RAINBOW_PETAL,
						 'G', if (Botania.gardenOfGlassLoaded) LIVING_WOOD else "ingotGold")
		ModCraftingRecipes.recipesSpreader?.add(BotaniaAPI.getLatestAddedRecipe())
		
		addOreDictRecipe(ItemStack(spreader, 1, 2),
						 "WWW", "EP ", "WWW",
						 'W', DREAM_WOOD,
						 'P', RAINBOW_PETAL,
						 'E', ELEMENTIUM)
		ModCraftingRecipes.recipesDreamwoodSpreader?.add(BotaniaAPI.getLatestAddedRecipe())
		
		for (i in 0..16) {
			val stack = ItemStarPlacer.forColor(i)
			stack.stackSize = 3
			addOreDictRecipe(stack,
							 " E ", "GDG", " G ",
							 'E', ENDER_AIR_BOTTLE,
							 'G', "dustGlowstone",
							 'D', DYES[i])
		}
		recipesStar = BotaniaAPI.getLatestAddedRecipes(17)
		
		for (i in 0..16) {
			val stack = ItemStarPlacer2.forColor(i)
			stack.stackSize = 6
			addOreDictRecipe(stack,
							 " E ", "GDG", " G ",
							 'E', ENDER_AIR_BOTTLE,
							 'G', MANA_PEARL,
							 'D', DYES[i])
		}
		recipesStar2 = BotaniaAPI.getLatestAddedRecipes(17)
		
		addOreDictRecipe(ItemStack(thinkingHand),
						 "PPP", "PSP", "PPP",
						 'P', tinyPotato,
						 'S', MANA_STRING)
		recipeThinkingHand = BotaniaAPI.getLatestAddedRecipe()
		
		addRecipe(ThunderwoodTwig.stack,
				  "P", "P",
				  'P', ItemStack(lightningWood))
		recipeThunderousTwig = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addRecipe(ItemStack(Blocks.TORCH, 6),
				  "C", "S",
				  'C', NetherwoodCoal.stack,
				  'S', ItemStack(Items.STICK))
		
		/* PORT: КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(toolbelt),
		                 "CL ", "L L", "PLR",
		                 'C', chest,
		                 'L', leather,
		                 'P', PIXIE_DUST,
		                 'R', RUNE[12])
		recipeToolbelt = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(tradePortal),
						 "LEL", "LEL", "LEL",
						 'L', LIVING_ROCK,
						 'E', ELVORIUM_NUGGET)
		recipeTradePortal = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addOreDictRecipe(ItemStack(triquetrum),
						 "NLN", " NL", " II",
						 'N', TERRASTEEL_NUGGET,
						 'L', LIVINGWOOD_TWIG,
						 'I', TERRA_STEEL)
		recipeTriquetrum = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2, КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		val s = AlfheimCore.stupidMode
		
		// if no TiC || if Avaritia loaded || if molten Mauftrium is disabled
		if (!AlfheimCore.TiCLoaded || s || AlfheimConfigHandler.materialIDs[TinkersConstructAlfheimConfig.MAUFTRIUM] == -1) {
			addOreDictRecipe(ItemStack(spreader, 1, 4),
							 "MMM", "ESI", "MMM",
							 'M', if (s) ItemStack(alfStorage, 1, 1) else MAUFTRIUM_INGOT,
							 'E', if (s) ItemStack(alfStorage) else ELVORIUM_INGOT,
							 'S', ItemStack(spreader, 1, 3),
							 'I', if (s) ManaInfusionCore.stack else IFFESAL_DUST)
			recipeUberSpreader = BotaniaAPI.getLatestAddedRecipe()
		}
		
		addOreDictRecipe(ItemStack(warBanner),
						 "TWT", "DWD", " T ",
						 'T', LIVINGWOOD_TWIG,
						 'W', ItemStack(wool, 1, 14),
						 'D', DYES[4])
		recipeWarBanner = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(warBanner, 1, 1),
						 "TWT", "DWD", " T ",
						 'T', DREAMWOOD_TWIG,
						 'W', ItemStack(wool, 1, 9),
						 'D', DYES[4])
		
		addOreDictRecipe(ItemStack(worldTree),
		                 "LLL", "AYA", "WDW",
		                 'L', LEAVES(Aurora),
		                 'A', apple,
		                 'Y', ItemStack(altWood1, 1, 2),
		                 'W', DREAM_WOOD,
		                 'D', "dirt")
		recipeWorldTree = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		// #############################################################################################################
		// ################################################ DECO BLOCKS ################################################
		// #############################################################################################################
		
		// PORT: вариант metadata — отдельный блок: ItemStack(x, n, meta) → ItemStack(x[meta], n) (MAPPING.md, «Имена и metadata»)
		addRecipe(ItemStack(elvenSandstone[0]), "SS", "SS", 'S', elvenSand)
		
		//addRecipe(ItemStack(elvenSandstone), "S", "S", 'S', elvenSandstoneSlab)
		
		addRecipe(ItemStack(elvenSandstone[1], 2), "S", "S", 'S', elvenSandstoneSlab)
		
		//addRecipe(ItemStack(elvenSandstone, 1, 2), "S", "S", 'S', elvenSandstoneSlab2)
		
		addRecipe(ItemStack(elvenSandstone[2], 4), "SS", "SS", 'S', ItemStack(elvenSandstone[0]))
		
		addRecipe(ItemStack(elvenSandstone[3], 4), "SS", "SS", 'S', ItemStack(elvenSandstone[2]))
		
		addOreDictRecipe(ItemStack(elvenSandstoneStairs[0], 4), true, "S  ", "SS ", "SSS", 'S', ItemStack(elvenSandstone[0]))
		
		addOreDictRecipe(ItemStack(elvenSandstoneStairs[1], 4), true, "S  ", "SS ", "SSS", 'S', ItemStack(elvenSandstone[2]))
		
		addOreDictRecipe(ItemStack(elvenSandstoneSlab, 6), "SSS", 'S', ItemStack(elvenSandstone[0]))
		
		addOreDictRecipe(ItemStack(elvenSandstoneSlab2, 6), "SSS", 'S', ItemStack(elvenSandstone[2]))
		
		addOreDictRecipe(ItemStack(elvenSandstoneWalls[0], 6), "SSS", "SSS", 'S', ItemStack(elvenSandstone[0]))
		
		addOreDictRecipe(ItemStack(elvenSandstoneWalls[1], 6), "SSS", "SSS", 'S', ItemStack(elvenSandstone[2]))
		
		// PORT: ItemStack(irisDirt, 8, i) → ItemStack(irisDirt[i], 8) (SPEC, Р-5); поля ванилы 1.20.1 — заглавными (Blocks.DIRT)
		for (i in 0..15)
			addOreDictRecipe(ItemStack(irisDirt[i], 8), "DDD", "DPD", "DDD", 'P', DYES[i], 'D', ItemStack(Blocks.DIRT, 1))
		
		addOreDictRecipe(ItemStack(rainbowDirt, 8), "DDD", "DPD", "DDD", 'P', DYES[16], 'D', ItemStack(Blocks.DIRT, 1))
		
		recipesColoredDirt = BotaniaAPI.getLatestAddedRecipes(17)
		
		addOreDictRecipe(ItemStack(auroraDirt, 8), "DDD", "DPD", "DDD", 'P', MANA_PEARL, 'D', ItemStack(Blocks.DIRT, 1))
		
		recipeAuroraDirt = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(livingcobbleStairs, 4), true, "L  ", "LL ", "LLL", 'L', ItemStack(livingcobble[0]))
		
		addOreDictRecipe(ItemStack(livingcobbleStairs1, 4), true, "L  ", "LL ", "LLL", 'L', ItemStack(livingcobble[1]))
		
		addOreDictRecipe(ItemStack(livingcobbleStairs2, 4), true, "L  ", "LL ", "LLL", 'L', ItemStack(livingcobble[2]))
		
		addRecipe(ItemStack(livingcobbleSlab, 6), "LLL", 'L', ItemStack(livingcobble[0]))
		
		addRecipe(ItemStack(livingcobbleSlab1, 6), "LLL", 'L', ItemStack(livingcobble[1]))
		
		addRecipe(ItemStack(livingcobbleSlab2, 6), "LLL", 'L', ItemStack(livingcobble[2]))
		
		addRecipe(ItemStack(livingcobble[0]), "L", "L", 'L', ItemStack(livingcobbleSlab))
		
		addRecipe(ItemStack(livingcobble[1]), "L", "L", 'L', ItemStack(livingcobbleSlab1))
		
		addRecipe(ItemStack(livingcobble[2]), "L", "L", 'L', ItemStack(livingcobbleSlab2))
		
		addRecipe(ItemStack(livingcobbleWall, 6), "LLL", "LLL", 'L', ItemStack(livingcobble[0]))
		
		// PORT: ModBlocks.livingrock 1 (кирпичи), ModBlocks.livingwood и ModBlocks.dreamwood 1 (доски) → livingrockBrick,
		// livingwoodPlanks, dreamwoodPlanks Botania 1.20.1 (MAPPING.md, «Botania»). Стены автора из кирпичей нет: её заменила
		// стена Botania 1.20.1 с тем же рецептом (AlfheimFluffBlocks)
//		addRecipe(ItemStack(livingrockBrickWall, 6), "LLL", "LLL", 'L', ItemStack(livingrockBrick))
		
		addRecipe(ItemStack(livingwoodFence, 6), "LLL", "LLL", 'L', ItemStack(livingwoodPlanks))
		
		addOreDictRecipe(ItemStack(livingwoodFenceGate, 1), "LPL", "LPL", 'L', LIVINGWOOD_TWIG, 'P', ItemStack(livingwoodPlanks))
		
		addOreDictRecipe(ItemStack(livingwoodBarkFence, 6), "LLL", "LLL", 'L', LIVINGWOOD_TWIG)
		
		// PORT: ModBlocks.livingwood 0 (живое дерево) → LIVING_WOOD — тег, как в рецептах Botania 1.20.1 (MAPPING.md, «Botania»)
		addOreDictRecipe(ItemStack(livingwoodBarkFenceGate, 1), "LPL", "LPL", 'L', LIVINGWOOD_TWIG, 'P', LIVING_WOOD)
		
		addRecipe(ItemStack(dreamwoodFence, 6), "LLL", "LLL", 'L', ItemStack(dreamwoodPlanks))
		
		addOreDictRecipe(ItemStack(dreamwoodFenceGate, 1), "LPL", "LPL", 'L', DREAMWOOD_TWIG, 'P', ItemStack(dreamwoodPlanks))
		
		addOreDictRecipe(ItemStack(dreamwoodBarkFence, 6), "LLL", "LLL", 'L', DREAMWOOD_TWIG)
		
		// PORT: ModBlocks.dreamwood 0 (сонное дерево) → DREAM_WOOD — тег, как в рецептах Botania 1.20.1
		addOreDictRecipe(ItemStack(dreamwoodBarkFenceGate, 1), "LPL", "LPL", 'L', DREAMWOOD_TWIG, 'P', DREAM_WOOD)
		
		// PORT: доски ванилы 5 (тёмный дуб) → Blocks.DARK_OAK_PLANKS
		addOreDictRecipe(ItemStack(dwarfPlanks, 4), " P ", "PMP", " P ", 'P', ItemStack(Blocks.DARK_OAK_PLANKS), 'M', MANA_POWDER)
		
		addStairsAndSlabs(dwarfPlanks, 0, dwarfPlanksStairs, dwarfPlanksSlab)
		
		// PORT: ModBlocks.livingrock 4 (резные кирпичи) → livingrockBrickChiseled
		addOreDictRecipe(ItemStack(dwarfLantern, 8),
						 "LCL", "CSC", "LCL",
						 'L', ItemStack(livingrockBrick),
						 'C', ItemStack(livingrockBrickChiseled),
						 'S', ItemStack(shrineLight[1]))
		
		for (i in (0..15) - 5 - 9 - 10 - 11 - 13) {
			addOreDictRecipe(ItemStack(shrineRock[i], 8),
							 "LLL", "LDL", "LLL",
							 'L', LIVING_ROCK,
							 'D', DYES[i])
		}
		
		addOreDictRecipe(ItemStack(shrineRock[5], 8),
						 "LLL", "LSL", "LLL",
						 'L', LIVING_ROCK,
						 'S', Items.SUGAR)
		
		addOreDictRecipe(ItemStack(shrineRock[9], 8),
						 "LL", "LL",
						 'L', ItemStack(shrineRock[0], 8))
		
		// PORT: ModBlocks.mushroom 0 (белый) и 14 (красный) — грибы Botania 1.20.1 по цвету
		addOreDictRecipe(ItemStack(shrineRock[10], 8),
						 "LLL", "LML", "LLL",
						 'L', LIVING_ROCK,
						 'M', ItemStack(whiteMushroom))
		
		addOreDictRecipe(ItemStack(shrineRock[11], 8),
						 "LLL", "LML", "LLL",
						 'L', LIVING_ROCK,
						 'M', ItemStack(redMushroom))
		
		addOreDictRecipe(ItemStack(shrineRock[13], 8),
						 "LLL", "LDL", "LLL",
						 'L', LIVING_ROCK,
						 'D', DYES[16])
		
		// ################################################################
		
		addRecipe(ItemStack(livingrockDark[1], 4),
				  "LL", "LL",
				  'L', ItemStack(livingrockDark[0]))
		
		addRecipe(ItemStack(livingrockDark[2], 4),
				  "LL", "LL",
				  'L', ItemStack(livingrockDark[1]))
		
		addRecipe(ItemStack(livingrockDark[3], 4),
				  "LL", "LL",
				  'L', ItemStack(livingrockDark[2]))
		
		addRecipe(ItemStack(livingrockDark[0]),
				  "L", "L",
				  'L', ItemStack(livingrockDarkSlabs[0]))
		
		addRecipe(ItemStack(livingrockDark[1]),
				  "L", "L",
				  'L', ItemStack(livingrockDarkSlabs[1]))
		
		addRecipe(ItemStack(livingrockDark[3]),
				  "L", "L",
				  'L', ItemStack(livingrockDarkSlabs[2]))
		
		addRecipe(ItemStack(livingrockDarkStairs[0], 4),
				  "L  ", "LL ", "LLL",
				  'L', ItemStack(livingrockDark[0]))
		
		addRecipe(ItemStack(livingrockDarkStairs[1], 4),
				  "L  ", "LL ", "LLL",
				  'L', ItemStack(livingrockDark[1]))
		
		addRecipe(ItemStack(livingrockDarkStairs[2], 4),
				  "L  ", "LL ", "LLL",
				  'L', ItemStack(livingrockDark[3]))
		
		addRecipe(ItemStack(livingrockDarkSlabs[0], 6),
				  "LLL",
				  'L', ItemStack(livingrockDark[0]))
		
		addRecipe(ItemStack(livingrockDarkSlabs[1], 6),
				  "LLL",
				  'L', ItemStack(livingrockDark[1]))
		
		addRecipe(ItemStack(livingrockDarkSlabs[2], 6),
				  "LLL",
				  'L', ItemStack(livingrockDark[3]))
		
		addRecipe(ItemStack(livingrockDarkWalls[0], 6),
				  "LLL", "LLL",
				  'L', ItemStack(livingrockDark[0]))
		
		addRecipe(ItemStack(livingrockDarkWalls[1], 6),
				  "LLL", "LLL",
				  'L', ItemStack(livingrockDark[1]))
		
		// ################################################################
		
		addRecipe(ItemStack(shrineRockWhiteStairs, 4),
				  "L  ", "LL ", "LLL",
				  'L', ItemStack(shrineRock[0]))
		
		addRecipe(ItemStack(shrineRockWhiteSlab, 6),
				  "LLL",
				  'L', ItemStack(shrineRock[0]))
		
		for (i in 0..5) {
			addOreDictRecipe(ItemStack(shrineLight[i], 8),
							 "LLL", "LDL", "LLL",
							 'L', "glowstone",
							 'D', DYES[if (i == 0) 14 else i])
		}
		
		addRecipe(ItemStack(shrinePillar, 2), "S", "S", 'S', ItemStack(shrineRock[0]))
		
		addOreDictRecipe(ItemStack(shrineGlass[0], 8),
						 "GGG", "GDG", "GGG",
						 'G', elfGlass,
						 'D', DYES[0])
		
		addOreDictRecipe(ItemStack(shrineGlass[1], 8),
						 "GGG", "GDG", "GGG",
						 'G', elfGlass,
						 'D', DYES[14])
		
		addOreDictRecipe(ItemStack(shrineGlass[2], 8),
						 "GGG", "GDG", "GGG",
						 'G', ItemStack(shrineGlass[0], 8),
						 'D', DYES[9])
		
		addOreDictRecipe(ItemStack(shrineGlass[3], 8),
						 "GGG", "GDG", "GGG",
						 'G', ItemStack(shrineGlass[0], 8),
						 'D', DYES[5])
		
		addOreDictRecipe(ItemStack(shrineGlass[4], 8),
						 "GGG", "GDG", "GGG",
						 'G', ItemStack(shrineGlass[0], 8),
						 'D', DYES[14])
		
		addRecipe(ItemStack(livingcobble[1], 4),
				  "LL", "LL",
				  'L', ItemStack(livingcobble[2]))
		
		addOreDictRecipe(ItemStack(livingcobble[2], 8),
						 "LLL", "L L", "LLL",
						 'L', LIVING_ROCK)
		
		addRecipe(ItemStack(livingMountain, 9),
		          "CRC", "RCR", "CRC",
		          'C', ItemStack(livingcobble[0]),
		          'R', ItemStack(livingrock))
		
		recipesLivingDecor = BotaniaAPI.getLatestAddedRecipes(3)
		
		addRecipe(ItemStack(livingMountainSlab, 6),
		          "MMM",
		          'M', ItemStack(livingMountain))
		
		addShapelessOreDictRecipe(ItemStack(livingcobble[3]), ItemStack(livingcobble[0]), vineBall)
		recipeLivingCobbleMossy = BotaniaAPI.getLatestAddedRecipe()
		
		// PORT: ModBlocks.customBrick 3 (черепица Botania 1.7.10) — блок порта: в Botania 1.20.1 черепицы нет
		// (alfheim.port.legacy.botania.BotaniaBlocks1710); вариант черепицы автора — блок массива (SPEC, Р-5)
		addShapelessOreDictRecipe(ItemStack(roofTile[0]), ItemStack(BotaniaBlocks1710.roofTile), DYES[10], DYES[7])
		addShapelessOreDictRecipe(ItemStack(roofTile[1]), ItemStack(BotaniaBlocks1710.roofTile), DYES[13], DYES[11], DYES[7])
		addShapelessOreDictRecipe(ItemStack(roofTile[2]), ItemStack(BotaniaBlocks1710.roofTile), DYES[13])
		
		recipesRoofTile = BotaniaAPI.getLatestAddedRecipes(3)
		
		roofTileSlabs.forEachIndexed { meta, slab ->
			addRecipe(ItemStack(slab, 6), "RRR", 'R', ItemStack(roofTile[meta]))
			addRecipe(ItemStack(roofTile[meta]), "R", "R", 'R', ItemStack(slab))
		}
		
		roofTileStairs.forEachIndexed { meta, stair ->
			addRecipe(ItemStack(stair, 4), "R  ", "RR ", "RRR", 'R', ItemStack(roofTile[meta]))
		}
		
		val dyes = arrayOf(4, 1, 14, 11)
		for (i in 0..3) {
			addOreDictRecipe(ItemStack(shrinePanel[i], 16),
							 "GGG", "DDD", "GGG",
							 'G', ItemStack(shrineGlass[0]),
							 'D', DYES[dyes[i]])
		}
		
		addRecipe(ItemStack(dwarfTrapDoor),
				  "WWW", "WWW",
				  'W', ItemStack(dwarfPlanks))
		
		/* PORT: КТ-2, КТ-3 — рецепты вещей этих КТ, включаются вместе с ними
		// darkQuartz may be null
		val quartzs = arrayOf(quartz_block, blazeQuartz, darkQuartz, elfQuartz, lavenderQuartz, manaQuartz, redQuartz, sunnyQuartz, shimmerQuartz).filterNotNull()
		for (q in quartzs) {
			addShapelessOreDictRecipe(ItemStack(q, 1, 5), ItemStack(q), if (q === darkQuartz) DYES[0] else DYES[15])
			addOreDictRecipe(ItemStack(q, 4, 6), "QQ", "QQ", 'Q', ItemStack(q))
		}
		
		addShapelessOreDictRecipe(ItemStack(elfQuartz, 1, 1), ItemStack(elfQuartz, 1, 7))
		addShapelessOreDictRecipe(ItemStack(elfQuartz, 1, 7), ItemStack(elfQuartz, 1, 1))
		addOreDictRecipe(ItemStack(elfQuartz, 2, 8), "S", "P", "S", 'S', elfQuartzSlab, 'P', ItemStack(elfQuartz, 1, 9))
		addShapelessOreDictRecipe(ItemStack(elfQuartz, 1, 9), ItemStack(elfQuartz))
		addOreDictRecipe(ItemStack(elfQuartz, 1, 10), "PP", "PP", 'P', ItemStack(elfQuartz, 1, 9))
		*/
		addOreDictRecipe(ItemStack(elfQuartzWall, 16), "QQQ", "QQQ", 'Q', ItemStack(elfQuartz))
		
		/* PORT: КТ-2 — рецепты вещей этой КТ, включаются вместе с ними
		addShapelessRecipe(ItemStack(yggDecor), ItemStack(altPlanks, 1, 6), wheat_seeds)
		addOreDictRecipe(ItemStack(yggDecor, 4, 1), "WW", "WW", 'W', ItemStack(altPlanks, 1, 6))
		addOreDictRecipe(ItemStack(yggDecor, 4, 2), " W ", "W W", " W ", 'W', ItemStack(altPlanks, 1, 6))
		
		addShapedRecipe(NiflheimBlockMetas.BRICKS.stack(4), "SS", "SS", 'S', NiflheimBlockMetas.STONE.stack)
		addShapedRecipe(NiflheimBlockMetas.CHISELED.stack(4), "BB", "BB", 'B', NiflheimBlockMetas.BRICKS.stack)
		addShapedRecipe(NiflheimBlockMetas.POLISHED.stack(8), "SSS", "S S", "SSS", 'S', NiflheimBlockMetas.STONE.stack)
		addShapedRecipe(NiflheimBlockMetas.PILLAR.stack(2), "S", "S", 'S', NiflheimBlockMetas.STONE.stack)
		addShapedRecipe(NiflheimBlockMetas.RUNIC.stack(2), "C", "C", 'C', NiflheimBlockMetas.CHISELED.stack)
		*/
	}
	
	private fun registerShapelessRecipes() {
		arrayOf(ELVORIUM_INGOT, MAUFTRIUM_INGOT, MUSPELHEIM_POWER_INGOT, NIFLHEIM_POWER_INGOT, GAIA_INGOT, MANA_PEARL).forEachIndexed { id, ingot ->
			addShapelessOreDictRecipe(ItemStack(alfStorage[id]), *Array(9) { ingot })
		}
		
		/* PORT: КТ-2, КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		val woods = Array(4) { ItemStack(altWood0, 1, it) } + Array(3) { ItemStack(altWood1, 1, it) }
		woods.forEachIndexed { id, it -> addShapelessOreDictRecipe(ItemStack(altPlanks, 4, id), it) }
		recipesAltPlanks = BotaniaAPI.getLatestAddedRecipes(6)
		
		addShapelessOreDictRecipe(ItemStack(auraRingElven), ELVORIUM_INGOT, auraRingPink)
		recipeAuraRingElven = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(auraRingGod), MAUFTRIUM_INGOT, auraRingElven)
		recipeAuraRingGod = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addShapelessOreDictRecipe(ItemStack(auroraPlanks, 4), auroraWood)
		recipeAuroraPlanks = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2, КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		addShapelessOreDictRecipe(ItemStack(barrierPlanks, 4), barrierWood)
		recipeBarrierPlanks = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(calicoPlanks, 4), calicoWood)
		recipeCalicoPlanks = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(circuitPlanks, 4), circuitWood)
		recipeCircuitPlanks = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(gaiaButton), wooden_button, LIFE_ESSENCE)
		recipeGaiaButton = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(corporeaInjector), hopper, corporeaSpark)
		recipeInjector = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		// PORT: ModItems.dye (цветочная пыль) → краситель ванилы (MAPPING.md, «Botania»); ступку убирает прослойка
		// (Recipes1710.kt). Цвета 16 и 17 (радужная и авроровая листва) — не цвета пыли: у автора эти рецепты давали пыль без
		// цвета и имени, и радужная листва не давала радужной пыли из рецепта ниже (BUGS.md, «Уже не воспроизводится в порту»)
		for (i in LibOreDict.Color.entries) if (i.ordinal < 16)
			addShapelessOreDictRecipe(ItemStack(DyeItem.byColor(DyeColor.byId(i.ordinal))), LEAVES(i), PESTLE_AND_MORTAR)
//		for (i in LibOreDict.Color.entries)
//			addShapelessOreDictRecipe(ItemStack(dye, 1, i.ordinal), LEAVES(i), PESTLE_AND_MORTAR)
		addShapelessOreDictRecipe(RainbowDust.stack, LEAVES(Rainbow), PESTLE_AND_MORTAR)
		recipesLeafDyes = BotaniaAPI.getLatestAddedRecipes(17)
		
		/* PORT: Thaumcraft выпал (SPEC, п. 7)
		if (Botania.thaumcraftLoaded) {
			addRecipe(RecipeHelmRevealingAlfheim(elementalHelmetRevealing, elementalHelmet))
			addRecipe(RecipeHelmRevealingAlfheim(elvoriumHelmetRevealing, elvoriumHelmet))
			addRecipe(RecipeHelmRevealingAlfheim(fenrirHelmetRevealing, fenrirHelmet))
//			addRecipe(RecipeHelmRevealingAlfheim(ItemListAB.itemNebulaHelmReveal, ItemListAB.itemNebulaHelm)) TODO back
			addRecipe(RecipeHelmRevealingAlfheim(snowHelmetRevealing, snowHelmet))
			addRecipe(RecipeHelmRevealingAlfheim(volcanoHelmetRevealing, volcanoHelmet))
		}
		*/
		
		addShapelessOreDictRecipe(ElvoriumNugget.stack(9), ELVORIUM_INGOT)
		addShapelessOreDictRecipe(MauftriumNugget.stack(9), MAUFTRIUM_INGOT)
		
		addShapelessRecipe(ElvoriumIngot.stack(9), ItemStack(alfStorage[0]))
		addShapelessRecipe(MauftriumIngot.stack(9), ItemStack(alfStorage[1]))
		addShapelessRecipe(MuspelheimPowerIngot.stack(9), ItemStack(alfStorage[2]))
		addShapelessRecipe(NiflheimPowerIngot.stack(9), ItemStack(alfStorage[3]))
		// PORT: ModItems.manaResource 14 (слиток Гайи), 1 (жемчуг маны) → gaiaIngot, manaPearl
		addShapelessRecipe(ItemStack(gaiaIngot, 9), ItemStack(alfStorage[4]))
		addShapelessRecipe(ItemStack(manaPearl, 9), ItemStack(alfStorage[5]))
		
		addShapelessOreDictRecipe(ElvoriumIngot.stack, *Array(9) { ELVORIUM_NUGGET })
		addShapelessOreDictRecipe(MauftriumIngot.stack, *Array(9) { MAUFTRIUM_NUGGET })
		
		// PORT: ступку убирает прослойка (Recipes1710.kt)
		addShapelessOreDictRecipe(ItemStack(Items.GLOWSTONE_DUST), PESTLE_AND_MORTAR, DreamCherry.stack, DreamCherry.stack, DreamCherry.stack)
		
		/* PORT: КТ-3 — рецепты вещей этой КТ, включаются вместе с ними
		addShapelessOreDictRecipe(ItemStack(fertilizer, if (Botania.gardenOfGlassLoaded) 3 else 1), ItemStack(justDye, 1, 15), FLORAL_POWDER, FLORAL_POWDER, FLORAL_POWDER, FLORAL_POWDER)
		ModCraftingRecipes.recipeFertilizerPowder?.let { CraftingManager.getInstance().recipeList.remove(it) }
		ModCraftingRecipes.recipeFertilizerPowder = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		for (i in 0..5) {
			val enh: Any = if (i < 3) MAUFTRIUM_INGOT else ItemStack(alfStorage[1])
			addShapelessOreDictRecipe(ItemStack(hyperBucket, 1, i + 1), ItemStack(hyperBucket, 1, i), enh)
		}
		
		/* PORT: КТ-3 — рецепты вещей этой КТ, включаются вместе с ними
		addShapelessOreDictRecipe(ItemStack(invisibleFlameLens),
								  ItemStack(lens, 1, 17), phantomInk)
		
		addShapelessOreDictRecipe(ItemStack(lens, 1, 17),
		                          ItemStack(invisibleFlameLens), phantomInk)
		
		addShapelessRecipe(ItemStack(itemFrame), item_frame, sign)
		recipeItemFrame = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addShapelessRecipe(JellyBread.stack, Items.BREAD, JellyBottle.stack)
		recipeJellybread = BotaniaAPI.getLatestAddedRecipe()
		
		// PORT: приготовленная рыба ванилы 0 (треска) → Items.COOKED_COD
		addShapelessRecipe(JellyCod.stack, ItemStack(Items.COOKED_COD), JellyBottle.stack)
		recipeJellyfish = BotaniaAPI.getLatestAddedRecipe()
		
		// PORT: ItemStack(irisPlanks, 4, i) → ItemStack(irisPlanks[i], 4) (SPEC, Р-5)
		for (i in 0..15)
			addShapelessOreDictRecipe(ItemStack(irisPlanks[i], 4), WOOD[i])
		addShapelessOreDictRecipe(ItemStack(rainbowPlanks, 4), rainbowWood)
		recipesColoredPlanks = BotaniaAPI.getLatestAddedRecipes(17)
		
		/* PORT: КТ-3 — рецепты вещей этой КТ, включаются вместе с ними
		addShapelessOreDictRecipe(ItemStack(lens, 1, LINKBACK.meta), ItemStack(lens), RUNE[8], MANA_POWDER, RED_STRING)
		recipeLensLinkback = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(lens, 1, PUSH.meta), ItemStack(lens), RUNE[2], MANA_POWDER)
		recipeLensPush = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(lens, 1, SMELT.meta), ItemStack(lens), RUNE[1], MANA_POWDER)
		recipeLensSmelt = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(lens, 1, TRACK.meta), ItemStack(lens), RUNE[11], MANA_POWDER)
		recipeLensTrack = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(lens, 1, TRIPWIRE.meta), ItemStack(lens), tripwire_hook, ELEMENTIUM)
		recipeLensTripwire = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(lens, 1, UNLINK.meta), ItemStack(lens), RUNE[8], manasteelShears, RED_STRING)
		recipeLensUnlink = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addShapelessOreDictRecipe(SaveIvy.stack, Blocks.VINE, DRAGONSTONE, MAUFTRIUM_NUGGET)
		recipeSaveIvy = BotaniaAPI.getLatestAddedRecipe()
		
		/* PORT: КТ-2 — рецепты вещей этой КТ, включаются вместе с ними
		addShapelessOreDictRecipe(ItemStack(lightningPlanks, 4), lightningWood)
		recipeThunderousPlanks = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		addShapelessOreDictRecipe(ItemStack(livingcobble[0]), LIVING_ROCK)
		recipeLivingcobble = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(livingrockDark[0]), livingrock, "coal")
		addShapelessOreDictRecipe(ItemStack(livingrockDark[1]), ItemStack(livingrockBrick), "coal")
		addShapelessOreDictRecipe(ItemStack(livingrockDark[2]), ItemStack(livingrockBrickChiseled), "coal")
		
		addShapelessOreDictRecipe(ItemStack(livingrockDarkStairs[0]), ItemStack(livingrockStairs), "coal")
		addShapelessOreDictRecipe(ItemStack(livingrockDarkStairs[1]), ItemStack(livingrockBrickStairs), "coal")
		
		addShapelessOreDictRecipe(ItemStack(livingrockDarkSlabs[0]), ItemStack(livingrockSlab), "coal")
		addShapelessOreDictRecipe(ItemStack(livingrockDarkSlabs[1]), ItemStack(livingrockBrickSlab), "coal")
		
		addShapelessOreDictRecipe(ItemStack(livingrockDarkWalls[0]), ItemStack(livingrockWall), "coal")
		// PORT: стена из кирпичей живого камня — стена Botania 1.20.1 (AlfheimFluffBlocks)
		addShapelessOreDictRecipe(ItemStack(livingrockDarkWalls[1]), ItemStack(livingrockBrickWall), "coal")
		
		// PORT: ModItems.manaResource 5 (эссенция жизни) → lifeEssence; ModItems.ancientWill 0–5 — шесть предметов
		// Botania 1.20.1, по номеру — ancientWill[i] (BotaniaItems1710.kt, MAPPING.md, «Botania»)
		for (i in 0..5)
			addShapelessOreDictRecipe(ItemStack(lifeEssence, 4), ItemStack(ancientWill[i]))
		
		/* PORT: КТ-2, КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(redstoneAttractor),
			"R", "L",
			'L', LIVING_ROCK,
			'R', REDSTONE_DUST)
		recipeRedstoneAttractor = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		// PORT: вариант metadata — блок массива (SPEC, Р-5); ModItems.manaResource 6 (корень красного камня) → redstoneRoot
		val grasses = Array(16) { ItemStack(irisGrass[it], 1) } + Array(2) { ItemStack(rainbowGrass[it], 1) }
		grasses.forEach { addShapelessOreDictRecipe(ItemStack(redstoneRoot), "dustRedstone", it) }
//		val grasses = Array(16) { ItemStack(irisGrass, 1, it) } + Array(2) { ItemStack(rainbowGrass, 1, it) }
//		grasses.forEach { addShapelessOreDictRecipe(ItemStack(manaResource, 1, 6), "dustRedstone", it) }
		recipesRedstoneRoot = BotaniaAPI.getLatestAddedRecipes(18)
		
		/* PORT: КТ-2, КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		addOreDictRecipe(ItemStack(redStringObserver),
			"LLL", "LRS", "LLL",
			'L', LIVING_ROCK,
			'S', RED_STRING,
			'R', repeater)
		recipeRedStringObserver = BotaniaAPI.getLatestAddedRecipe()
		
		addOreDictRecipe(ItemStack(redStringWatcher),
			"LLL", "LRS", "LLL",
			'L', LIVING_ROCK,
			'S', RED_STRING,
			'R', redstone_torch)
		recipeRedStringWatcher = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(manaRingGod, 1, WILDCARD_VALUE), MAUFTRIUM_INGOT, manaStoneGreater)
		recipeManaRingGod = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		// PORT: поля ванилы 1.20.1 — заглавными
		addShapelessOreDictRecipe(ItemStack(Items.MUSHROOM_STEW), rainbowMushroom, rainbowMushroom, ItemStack(Items.BOWL))
//		addShapelessOreDictRecipe(ItemStack(mushroom_stew), rainbowMushroom, rainbowMushroom, ItemStack(bowl))
		
		/* PORT: КТ-2, КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		addShapelessOreDictRecipe(ItemStack(netherPlanks, 4), netherWood)
		recipeInfernalPlanks = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		// PORT: ступку убирает прослойка (Recipes1710.kt)
		addShapelessOreDictRecipe(RainbowDust.stack, RAINBOW_PETAL, PESTLE_AND_MORTAR)
		recipeRainbowPetalGrinding = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(RainbowPetal.stack(2), RAINBOW_FLOWER)
		addShapelessOreDictRecipe(RainbowPetal.stack(4), RAINBOW_DOUBLE_FLOWER)
		recipesRainbowPetal = BotaniaAPI.getLatestAddedRecipes(2)
		
		addShapelessOreDictRecipe(ItemStack(fireGrenade), vial, Items.FIRE_CHARGE, Items.GUNPOWDER)
		recipeGrenade = BotaniaAPI.getLatestAddedRecipe()
		
		// PORT: вариант metadata — блок массива (SPEC, Р-5)
		addShapelessOreDictRecipe(ItemStack(rainbowGrass[3]), "dustGlowstone", "dustGlowstone", ItemStack(rainbowGrass[2]))
//		addShapelessOreDictRecipe(ItemStack(rainbowGrass, 1, 3), "dustGlowstone", "dustGlowstone", ItemStack(rainbowGrass, 1, 2))
		// PORT: КТ-9 — рецепты страницы лексикона Botania «Мерцающие цветы»
//		ModCraftingRecipes.recipesShinyFlowers?.add(BotaniaAPI.getLatestAddedRecipe())
		
		// PORT: поля ванилы 1.20.1 — заглавными
		addShapelessRecipe(ItemStack(rainbowMushroom), ItemStack(Blocks.RED_MUSHROOM), RainbowDust.stack)
		addShapelessRecipe(ItemStack(rainbowMushroom), ItemStack(Blocks.BROWN_MUSHROOM), RainbowDust.stack)
//		addShapelessRecipe(ItemStack(rainbowMushroom), ItemStack(red_mushroom), RainbowDust.stack)
//		addShapelessRecipe(ItemStack(rainbowMushroom), ItemStack(brown_mushroom), RainbowDust.stack)
		// PORT: КТ-9 — рецепты страницы лексикона Botania «Мерцающие грибы»
//		ModCraftingRecipes.recipesMushrooms?.addAll(BotaniaAPI.getLatestAddedRecipes(2))
		
		// PORT: вариант metadata — блок массива (SPEC, Р-5)
		addOreDictRecipe(ItemStack(softStorage[0]), "PPP", "PPP", "PPP", 'P', RAINBOW_PETAL)
		recipeRainbowPetalBlock = BotaniaAPI.getLatestAddedRecipe()
		addOreDictRecipe(ItemStack(softStorage[1], 1), "PPP", "PPP", "PPP", 'P', PIXIE_DUST)
		addOreDictRecipe(ItemStack(softStorage[2], 1), "PPP", "PPP", "PPP", 'P', IFFESAL_DUST)
		addOreDictRecipe(ItemStack(softStorage[3], 1), "PPP", "PPP", "PPP", 'P', LIFE_ESSENCE)
//		addOreDictRecipe(ItemStack(softStorage), "PPP", "PPP", "PPP", 'P', RAINBOW_PETAL)
//		recipeRainbowPetalBlock = BotaniaAPI.getLatestAddedRecipe()
//		addOreDictRecipe(ItemStack(softStorage, 1, 1), "PPP", "PPP", "PPP", 'P', PIXIE_DUST)
//		addOreDictRecipe(ItemStack(softStorage, 1, 2), "PPP", "PPP", "PPP", 'P', IFFESAL_DUST)
//		addOreDictRecipe(ItemStack(softStorage, 1, 3), "PPP", "PPP", "PPP", 'P', LIFE_ESSENCE)
		
		/* PORT: КТ-2, КТ-3 — рецепты вещей этих КТ, включаются вместе с ними
		addShapelessOreDictRecipe(ItemStack(sealingPlanks, 4), sealingWood)
		recipeSealingPlanks = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(shimmerrock), "livingrock", DYES[16])
		ModCraftingRecipes.recipeShimmerrock?.let { CraftingManager.getInstance().recipeList.remove(it) }
		ModCraftingRecipes.recipeShimmerrock = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		recipeShimmerQuartz = addQuartzRecipes(shimmerQuartz, shimmerQuartzStairs, shimmerQuartzSlab)
		
		/* PORT: КТ-2, КТ-3 — рецепты вещей этих КТ, включаются вместе с ними
		addShapelessOreDictRecipe(ItemStack(shimmerwoodPlanks), ItemStack(dreamwood, 1, 1), DYES[16])
		ModCraftingRecipes.recipeShimmerwoodPlanks?.let { CraftingManager.getInstance().recipeList.remove(it) }
		ModCraftingRecipes.recipeShimmerwoodPlanks = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(corporeaSparkBase), fence, corporeaSpark)
		recipeSparkBase = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(lightRelay, 1, 2), lightRelay, lever)
		recipeLuminizer2 = BotaniaAPI.getLatestAddedRecipe()
		
		addShapelessOreDictRecipe(ItemStack(lightRelay, 1, 3), lightRelay, animatedTorch)
		recipeLuminizer3 = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		// PORT: рецептов «гриб Botania → гриб ванилы» нет по решению автора: в Botania 1.20.1 тот же гриб даёт краситель
		// (TASKS.md, журнал решений); ModBlocks.mushroom 12 (коричневый) и 14 (красный) → brownMushroom, redMushroom
//		addShapelessRecipe(ItemStack(Blocks.BROWN_MUSHROOM), ItemStack(brownMushroom))
//		addShapelessRecipe(ItemStack(Blocks.RED_MUSHROOM), ItemStack(redMushroom))
		
		/* PORT: КТ-2, КТ-3 — рецепты вещей этих КТ, включаются вместе с ними
		addShapelessRecipe(NiflheimBlockMetas.COBBLESTONE.stack, NiflheimBlockMetas.STONE.stack)
		
		// change to shapeless, same parts
		addShapelessOreDictRecipe(ItemStack(spreader, 1, 3), LIFE_ESSENCE, ItemStack(spreader, 1, 2), DRAGONSTONE)
		ModCraftingRecipes.recipeUltraSpreader?.let { CraftingManager.getInstance().recipeList.remove(it) }
		ModCraftingRecipes.recipeUltraSpreader = BotaniaAPI.getLatestAddedRecipe()
		*/
		
		/* PORT: не переносится — в Botania 1.20.1 этот рецепт есть: блок лепестков → 9 лепестков (botania:conversions/<цвет>_petal_block_deconstruct)
		repeat(16) {
			addShapelessRecipe(ItemStack(petal, 9, it), ItemStack(petalBlock, 1, it))
		}
		*/
		
		// PORT: вариант metadata — блок массива (SPEC, Р-5); ModItems.manaResource 8 (пыльца фей), 5 (эссенция жизни) →
		// pixieDust, lifeEssence
		addShapelessRecipe(RainbowPetal.stack(9), ItemStack(softStorage[0]))
		addShapelessRecipe(ItemStack(pixieDust, 9), ItemStack(softStorage[1], 1))
		addShapelessRecipe(IffesalDust.stack(9), ItemStack(softStorage[2], 1))
		addShapelessRecipe(ItemStack(lifeEssence, 9), ItemStack(softStorage[3], 1))
//		addShapelessRecipe(RainbowPetal.stack(9), ItemStack(softStorage))
//		addShapelessRecipe(ItemStack(manaResource, 9, 8), ItemStack(softStorage, 1, 1))
//		addShapelessRecipe(IffesalDust.stack(9), ItemStack(softStorage, 1, 2))
//		addShapelessRecipe(ItemStack(manaResource, 9, 5), ItemStack(softStorage, 1, 3))
		
		/* PORT: КТ-2, КТ-3 — рецепты вещей этих КТ, включаются вместе с ними
		addShapelessRecipe(Stencil.stack, paper, carver)
		recipeStencil = BotaniaAPI.getLatestAddedRecipe()
		*/
	}
	
	private fun registerSmeltingRecipes() {
		/* PORT: КТ-2 — рецепты вещей этой КТ, включаются вместе с ними
		for (i in 0..15) {
			addSmelting(ItemStack(altWood0, 1, i), ItemStack(coal, 1, 1), 0.15f)
			
			if (i % 4 != 2)
				addSmelting(ItemStack(altWood1, 1, i), ItemStack(coal, 1, 1), 0.15f)
		}
		*/
		
		// PORT: Items.coal 1 → Items.CHARCOAL (вариант ванилы 1.7.10 — отдельный предмет 1.20.1); брёвна — массивы вариантов
		addSmelting(irisWood0, ItemStack(Items.CHARCOAL), 0.15f)
		addSmelting(irisWood1, ItemStack(Items.CHARCOAL), 0.15f)
		addSmelting(irisWood2, ItemStack(Items.CHARCOAL), 0.15f)
		addSmelting(irisWood3, ItemStack(Items.CHARCOAL), 0.15f)
		addSmelting(rainbowWood, ItemStack(Items.CHARCOAL), 0.15f)
		addSmelting(auroraWood, ItemStack(Items.CHARCOAL), 0.15f)
		/* PORT: КТ-2 — рецепты вещей этой КТ, включаются вместе с ними
		addSmelting(lightningWood, ItemStack(coal, 1, 1), 0.15f)
		addSmelting(sealingWood, ItemStack(coal, 1, 1), 0.15f)
		addSmelting(netherWood, NetherwoodCoal.stack, 0.15f)
		addSmelting(calicoWood, ItemStack(coal, 1, 1), 0.15f)
		addSmelting(circuitWood, ItemStack(coal, 1, 1), 0.15f)
		addSmelting(barrierWood, ItemStack(coal, 1, 1), 0.15f)
		addSmelting(lightningPlanks, ThunderwoodSplinters.stack(2), 0.1f)
		addSmelting(netherPlanks, NetherwoodSplinters.stack(2), 0.1f)
		*/
		
		// PORT: ModItems.manaResource 9 (драконий камень), 7 (элементиум), ModItems.quartz 5 (эльфийский кварц),
		// краситель ванилы 4 (лазурит) → предметы 1.20.1 (MAPPING.md, «Botania»)
		addSmelting(ItemStack(elvenOre[0]), ItemStack(dragonstone), 1f)
		addSmelting(ItemStack(elvenOre[1]), ItemStack(elementium), 1f)
		addSmelting(ItemStack(elvenOre[2]), ItemStack(BotaniaItems.elfQuartz), 1f)
		addSmelting(ItemStack(elvenOre[3]), ItemStack(Items.GOLD_INGOT), 1f)
		addSmelting(ItemStack(elvenOre[4]), IffesalDust.stack, 1f)
		addSmelting(ItemStack(elvenOre[5]), ItemStack(Items.LAPIS_LAZULI), 0.2f)
		
		addSmelting(elvenSand, ItemStack(elfGlass), 1f)
		addSmelting(elvenSandstone, ItemStack(elvenSandstone[4]), 1f)
		/* PORT: КТ-2 — рецепты вещей этой КТ, включаются вместе с ними
		addSmelting(NiflheimBlockMetas.BRICKS.stack, NiflheimBlockMetas.CRACKED.stack, 0f)
		*/
	}
	
	/* PORT: КТ-3 — инфузор маны, древесная кузня (RecipeTreeCrafting)
	private fun registerManaInfuserRecipes() {
		addInfuserRecipe(InterdimensionalGatewayCore.stack,
						 TilePool.MAX_MANA,
						 MANA_PEARL,
						 ELVORIUM_INGOT,
						 ManaInfusionCore.stack,
						 RUNE[8], // mana
						 TERRA_STEEL,
						 ItemStack(bifrostPerm))
		
		// terrasteel
		addInfuserRecipe(ItemStack(manaResource, 1, 4),
						 TilePool.MAX_MANA / 2,
						 MANA_STEEL,
						 MANA_PEARL,
						 MANA_DIAMOND)
		
		recipeElvorium = addInfuserRecipe(ElvoriumIngot.stack,
										  TilePool.MAX_MANA / 2,
										  ELEMENTIUM,
										  PIXIE_DUST,
										  DRAGONSTONE)
		
		recipeMauftrium = addInfuserRecipe(MauftriumIngot.stack,
										   TilePool.MAX_MANA,
										   GAIA_INGOT,
										   MUSPELHEIM_POWER_INGOT,
										   NIFLHEIM_POWER_INGOT)
		
		recipeManaStone = addInfuserRecipe(ItemStack(manaStone, 1, 1000),
										   TilePool.MAX_MANA,
										   DRAGONSTONE,
										   IffesalDust.stack(4))
		
		recipeManaStoneGreater = addInfuserRecipe(ItemStack(manaStoneGreater, 1, 1000),
												  TilePool.MAX_MANA * 4,
												  ItemStack(manaStone, 1, WILDCARD_VALUE).also { ItemNBTHelper.setBoolean(it, TAG_ASJIGNORENBT, true) },
												  MuspelheimEssence.stack,
		                                          ItemStack(manaResource, 4, 5),
												  NiflheimEssence.stack)
	}
	
	private fun registerDendrology() {
		recipeLightningTree = AlfheimAPI.addTreeRecipe(50000,
													   ItemStack(lightningSapling),
		                                               null,
													   ItemStack(irisSapling),
													   350,
													   MANA_STEEL, MANA_STEEL, MANA_STEEL,
													   RUNE[13], // Wrath
													   LEAVES(Purple), LEAVES(Purple), LEAVES(Purple),
													   ItemStack(teruTeruBozu))
		
		recipeInfernalTree = AlfheimAPI.addTreeRecipe(10000,
													  ItemStack(netherSapling),
													  null,
													  ItemStack(irisSapling),
													  70,
													  "ingotBrickNether", "ingotBrickNether", "ingotBrickNether",
													  RUNE[1], // Fire
													  LEAVES(Orange), LEAVES(Orange), LEAVES(Orange),
													  BLAZE_BLOCK)
		
		recipeCalicoTree = AlfheimAPI.addTreeRecipe(50000,
													ItemStack(calicoSapling),
													null,
													ItemStack(irisSapling),
													70,
													ItemStack(soul_sand), ItemStack(soul_sand), ItemStack(soul_sand),
													RUNE[11], // Greed
													LEAVES(Orange), LEAVES(White), LEAVES(Brown),
													ItemStack(obsidian))
		
		recipeCircuitTree = AlfheimAPI.addTreeRecipe(10000,
													 ItemStack(circuitSapling),
													 null,
													 ItemStack(irisSapling),
													 70,
													 ItemStack(repeater), ItemStack(comparator), ItemStack(repeater),
													 RUNE[9], // Lust
													 LEAVES(Red), LEAVES(Red), LEAVES(Red),
													 "blockRedstone")
		
		recipeSealingTree = AlfheimAPI.addTreeRecipe(50000,
													 ItemStack(sealingSapling),
													 null,
													 ItemStack(irisSapling),
													 350,
													 ELEMENTIUM, ELEMENTIUM, ELEMENTIUM,
													 RUNE[7], // Winter
													 LEAVES(White), LEAVES(White), LEAVES(White),
													 ItemStack(wool, 1, WILDCARD_VALUE))
		
		recipeBarrierTree = AlfheimAPI.addTreeRecipe(30000,
													 ItemStack(barrierSapling),
													 null,
													 ItemStack(irisSapling),
													 70,
													 ItemStack(piston), ItemStack(pistonRelay), ItemStack(piston),
													 RUNE[3], // AIR
													 LEAVES(Cyan),
													 LEAVES(Green),
													 LEAVES(LightBlue),
													 RUNE[15] // PRIDE
		)
		
		recipeKudzu = AlfheimAPI.addTreeRecipe(
			666_666,
			ItemStack(kudzuVine, 1, 15).also { ItemNBTHelper.initNBT(it) },
			"${ModInfo.MODID}:KudzuVine",
			ItemStack(cellBlock).also { ItemNBTHelper.setBoolean(it, TAG_ASJIGNORENBT, true) },
			666,
			ItemStack(tallgrass, 1, 1), ItemStack(subspacian), ItemStack(tallgrass, 1, 2),
			ItemStack(grassSeeds), ItemStack(vineBall), ItemStack(fertilizer), ItemStack(deadbush),
		)
		
		if (Botania.thaumcraftLoaded && AlfheimConfigHandler.thaumTreeSuffusion)
			ThaumcraftSuffusionRecipes.initRecipes()
	}
	*/
	
	private fun registerRecipes() {
		/* PORT: КТ-2, КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		val costTier1 = 5200
		val costTier2 = 8000
		val costTier3 = 12000
		
		recipeAquapanthus = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("aquapanthus"),
		                                                   PETAL[11], PETAL[11], // Blue
		                                                   PETAL[3], // Light Blue
		                                                   PETAL[9], // Cyan
		                                                   PETAL[13]) // Green
		
		recipeBud = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("budOfYggdrasil"),
		                                           RAINBOW_PETAL,
		                                           PETAL[5], // Lime
		                                           PETAL[12], // Brown
		                                           PETAL[13], // Green
		                                           RUNE[8], // Mana
		                                           RUNE[11], // Greed
		                                           RUNE[12], // Sloth
		                                           IFFESAL_DUST,
		                                           LIFE_ESSENCE,
		                                           ItemStack(irisSeeds, 1, 16)) // Bifrost seed
		
		recipeCrysanthermum = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("crysanthermum"),
															 PETAL[1], PETAL[1], // Orange
															 PETAL[15], // Black
															 PETAL[3], PETAL[3], // Light Blue
															 RUNE[7], // Winter
															 RUNE[5]) // Summer
		
		recipeOrechidAlfarem = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("orechidAlfarem"),
															  PETAL[8], PETAL[8],  // Light Gray
															  RAINBOW_PETAL,
															  PETAL[5], PETAL[5], // Lime
															  RUNE[15],  // Pride
															  RUNE[11],  // Greed
															  REDSTONE_ROOT,
															  PIXIE_DUST)
		
		recipeOrechidEndium = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("orechidEndium"),
															 PETAL[4],  // Yellow
															 PETAL[10], // Purple
															 ItemStack(manaResource, 1, 15), // Ender Air
															 PETAL[10], // Purple
															 PETAL[6],  // Pink
															 RUNE[15],  // Pride
															 RUNE[11],  // Greed
															 REDSTONE_ROOT,
															 PIXIE_DUST)
		
		recipePetronia = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("petronia"),
														REDSTONE_ROOT,
														RUNE[0],   // Water
														RUNE[1],   // Fire
														PETAL[1],  // Orange
														PETAL[15], // Black
														PETAL[12], // Brown
														DRAGONSTONE)
		
		recipeRainFlower = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("rainFlower"),
														  *Array(4) { PETAL[11] }, // Blue
														  PETAL[3], PETAL[3], // Light Blue
														  PETAL[4]) // Yellow
		
		recipeRattlerose = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("rattlerose"),
		                                                  LIFE_ESSENCE,
														  RUNE[0], // Water
														  RUNE[2], // Earth
														  RUNE[7], // Winter
														  RUNE[13], // Wrath
														  PETAL[Lime.I],
														  PETAL[Green.I],
														  PETAL[Cyan.I],
														  PETAL[Red.I],
														  PETAL[Yellow.I])
		
		recipeSnowFlower = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("snowFlower"),
														  *Array(4) { PETAL[3] }, // Light Blue
														  *Array(3) { PETAL[0] }) // White
		
		recipeStormFlower = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("stormFlower"),
														   *Array(4) { PETAL[3] }, // Light Blue
														   PETAL[11], // Blue
														   RUNE[13])  // Wrath
		
		recipeTradescantia = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("tradescantia"),
		                                                    PIXIE_DUST,
		                                                    PETAL[2], // Magenta
		                                                    PETAL[4], // Yellow
		                                                    PETAL[5], PETAL[5], // Lime
		                                                    PETAL[10], // Purple
		                                                    PETAL[13], // Green
															RUNE[0], // Water
															RUNE[11]) // Greed
		
		recipeWindFlower = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("windFlower"),
														  PETAL[4], PETAL[4], // Yellow
														  PETAL[5], PETAL[5], // Lime
														  RUNE[6]) // Autumn
		
		recipeWitherAconite = BotaniaAPI.registerPetalRecipe(BotaniaAPI.internalHandler.getSubTileAsStack("witherAconite"),
		                                                     ItemStack(wiltedLotus, 1, 1),
		                                                     PETAL[15], PETAL[15], // Black
		                                                     RUNE[15]) // Pride
		
		recipeRealityRune = RecipeRuneAltarFull(PrimalRune.stack, costTier3,
												RUNE[0], RUNE[1], RUNE[2], RUNE[3], RUNE[8], ItemStack(manaResource, 1, 15), MAUFTRIUM_INGOT)
		BotaniaAPI.runeAltarRecipes.add(recipeRealityRune)
		
		recipeMuspelheimRune = BotaniaAPI.registerRuneAltarRecipe(MuspelheimRune.stack, costTier3,
																  RUNE[1], RUNE[2], MuspelheimEssence.stack, MuspelheimEssence.stack, IFFESAL_DUST)
		recipeNiflheimRune = BotaniaAPI.registerRuneAltarRecipe(NiflheimRune.stack, costTier3,
																RUNE[0], RUNE[3], NiflheimEssence.stack, NiflheimEssence.stack, IFFESAL_DUST)
		recipeSnakeEgg = BotaniaAPI.registerRuneAltarRecipe(ItemStack(snakeObject), costTier3,
		                                                    DRAGONSTONE, PRISMARINE_SHARD, ItemStack(apple), *Array(4) { ItemStack(cellBlock) })
		
		ModRuneRecipes.recipeSummerRune?.let { BotaniaAPI.runeAltarRecipes.remove(it) }
		ModRuneRecipes.recipeSummerRune = BotaniaAPI.registerRuneAltarRecipe(ItemStack(rune, 1, 5), costTier2, RUNE[2], RUNE[3], "sand", "sand", ItemStack(slime_ball), ItemStack(melon))
		
		ModRuneRecipes.recipesEarthRune?.add(BotaniaAPI.registerRuneAltarRecipe(ItemStack(rune, 2, 2), costTier1, MANA_POWDER, MANA_STEEL, ItemStack(livingcobble), ItemStack(obsidian), ItemStack(brown_mushroom)))
		ModRuneRecipes.recipesEarthRune?.add(BotaniaAPI.registerRuneAltarRecipe(ItemStack(rune, 2, 2), costTier1, MANA_POWDER, MANA_STEEL, ItemStack(livingcobble), ItemStack(obsidian), ItemStack(red_mushroom)))
		
		recipeInterdimensional = BotaniaAPI.registerElvenTradeRecipe(InterdimensionalGatewayCore.stack, ItemStack(nether_star))
		BotaniaAPI.registerElvenTradeRecipe(ItemStack(alfglassPane), ItemStack(managlassPane))
		//recipeStoryToken = BotaniaAPI.registerElvenTradeRecipe(ItemStack(storyToken, 1, 1), ItemStack(storyToken))
		
		recipeDreamwood = BotaniaAPI.registerPureDaisyRecipe(DREAM_WOOD_LOG, dreamwood, 0)
		BotaniaAPI.registerPureDaisyRecipe("cobblestone", livingcobble, 0)
		BotaniaAPI.registerPureDaisyRecipe("endstone", cobblestone, 0)
		BotaniaAPI.registerPureDaisyRecipe(IRIS_DIRT, dirt, 0)
		BotaniaAPI.pureDaisyRecipes.add(RecipePureDaisyMeta(manaFluidBlock, 0, manaIce, 0))
		
		// fix for only source block
		BotaniaAPI.pureDaisyRecipes.removeAll { it.input === water && it.output === snow }
		BotaniaAPI.pureDaisyRecipes.add(RecipePureDaisyMeta(water, 0, snow, 0))
		
		recipeIrisSapling = RecipePureDaisyExclusion("treeSapling", irisSapling, 0)
		BotaniaAPI.pureDaisyRecipes.add(recipeIrisSapling)
		
		recipeInfusedDreamTwig = BotaniaAPI.registerManaInfusionRecipe(InfusedDreamwoodTwig.stack, ItemStack(manaResource, 1, 13), 10000)
		ModManaInfusionRecipes.manaPowderRecipes?.add(BotaniaAPI.registerManaInfusionRecipe(ItemStack(manaResource, 1, 23), RainbowDust.stack, 400))
		BotaniaAPI.registerManaInfusionRecipe(ItemStack(managlassPane), ItemStack(glass_pane), 56)
		
		recipesPastoralSeeds = (Array(16) { ItemStack(irisGrass, 1, it) } + Array(2) { ItemStack(rainbowGrass, 1, it) })
			.mapIndexed { id, it -> BotaniaAPI.registerManaInfusionRecipe(ItemStack(irisSeeds, 1, id), it, 2500) }
		addShapelessRecipe(ItemStack(irisSeeds, 1, ItemColorSeeds.SNOW), ItemStack(grassSeeds), ItemStack(snowball))
		
		recipeRedstoneRelay = BotaniaAPI.registerManaInfusionRecipe(ItemStack(redstoneRelay), ItemStack(redstone_block), 15000)
		
		BotaniaAPI.registerManaAlchemyRecipe(ItemStack(skullPlacer, 1, 2), ItemStack(skullPlacer, 1, 3), 66666)
		BotaniaAPI.registerManaAlchemyRecipe(ItemStack(skullPlacer, 1, 0), ItemStack(skullPlacer, 1, 2), 6666)
		BotaniaAPI.registerManaAlchemyRecipe(ItemStack(skullPlacer, 1, 1), ItemStack(skullPlacer, 1, 0), 66666)
		BotaniaAPI.registerManaAlchemyRecipe(ItemStack(skullPlacer, 1, 4), ItemStack(skullPlacer, 1, 1), 6666)
		BotaniaAPI.registerManaAlchemyRecipe(ItemStack(skullPlacer, 1, 3), ItemStack(skullPlacer, 1, 4), 666)
		
		recipeRiftShard = BotaniaAPI.registerManaAlchemyRecipe(RiftShardEmpty.stack, ItemStack(bifrostPerm), 12000)
		
		BotaniaAPI.registerManaAlchemyRecipe(ItemStack(red_mushroom), ItemStack(brown_mushroom), 120)
		BotaniaAPI.registerManaAlchemyRecipe(ItemStack(brown_mushroom), ItemStack(red_mushroom), 120)
		
		attributionSkull(getCurrentNickname("yrsegal"), irisSeeds, 16) // Bifrost Seeds
		// Wire - I just love rainbows, what can I say?
		attributionSkull(getCurrentNickname("l0nekitsune"), elvenResource, NetherwoodCoal.I)
		// Lone - "hot stuff" (because I'm classy like that)
		attributionSkull(getCurrentNickname("Tristaric"), coatOfArms, 6) // Irish Shield
		// Tris - The only item that remotely fits me.
		*/
		
		recipeSplashPotions = ShapelessOreRecipe(ItemStack(splashPotion), brewVial, Items.GUNPOWDER)
		
		/* PORT: КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		addRecipe(RecipeRingDyes)
		RecipeSorter.register("${ModInfo.MODID}:ringdye", RecipeRingDyes::class.java, RecipeSorter.Category.SHAPELESS, "")
		addRecipe(RecipeRainbowLensDye)
		RecipeSorter.register("${ModInfo.MODID}:lensdye", RecipeRainbowLensDye::class.java, RecipeSorter.Category.SHAPELESS, "")
		addRecipe(RecipeLootInterceptor)
		RecipeSorter.register("${ModInfo.MODID}:looter", RecipeLootInterceptor::class.java, RecipeSorter.Category.SHAPELESS, "")
		addRecipe(RecipeLootInterceptorClear)
		RecipeSorter.register("${ModInfo.MODID}:looterclean", RecipeLootInterceptorClear::class.java, RecipeSorter.Category.SHAPELESS, "")
		addRecipe(RecipeCleanRelic)
		RecipeSorter.register("${ModInfo.MODID}:cleanrelic", RecipeCleanRelic::class.java, RecipeSorter.Category.SHAPELESS, "")
		addRecipe(RecipeClearLoki)
		RecipeSorter.register("${ModInfo.MODID}:clearloki", RecipeClearLoki::class.java, RecipeSorter.Category.SHAPELESS, "")
		*/
		addRecipe(RecipeThrowablePotion)
		RecipeSorter.register("${ModInfo.MODID}:throwpotion", RecipeThrowablePotion::class.java, RecipeSorter.Category.SHAPELESS, "")
		addRecipe(RecipeElvenWeed)
		RecipeSorter.register("${ModInfo.MODID}:elvenweed", RecipeElvenWeed::class.java, RecipeSorter.Category.SHAPELESS, "")
		/* PORT: КТ-2, КТ-3, КТ-4 — рецепты вещей этих КТ, включаются вместе с ними
		addRecipe(RecipeAesirCloak)
		RecipeSorter.register("${ModInfo.MODID}:aesirCloak", RecipeAesirCloak::class.java, RecipeSorter.Category.SHAPED, "")
		addRecipe(RecipeLensSplit)
		RecipeSorter.register("${ModInfo.MODID}:lenssplit", RecipeLensSplit::class.java, RecipeSorter.Category.SHAPELESS, "")
		addRecipe(RecipeSpecialFloatingFlower)
		RecipeSorter.register("${ModInfo.MODID}:floatingSpecialFlower", RecipeSpecialFloatingFlower::class.java, RecipeSorter.Category.SHAPELESS, "")
		addRecipe(RecipeSaveIvy)
		RecipeSorter.register("${ModInfo.MODID}:saveIvy", RecipeSaveIvy::class.java, RecipeSorter.Category.SHAPELESS, "")
		addRecipe(RecipeResonatorTipping)
		RecipeSorter.register("${ModInfo.MODID}:resonatorTipping", RecipeResonatorTipping::class.java, RecipeSorter.Category.SHAPELESS, "")
		addRecipe(RecipeStencil)
		RecipeSorter.register("${ModInfo.MODID}:stencil", RecipeStencil::class.java, RecipeSorter.Category.SHAPELESS, "")
		
		AlfheimAPI.barrelRecipes.add(RecipeWine(RedGrapes.stack, RedWine.stack))
		AlfheimAPI.barrelRecipes.add(RecipeWineWhite)
		AlfheimAPI.barrelRecipes.add(RecipeBeer)
		*/
	}
	
	/* PORT: КТ-3 — тюнер маны, обменный портал; КТ-7 — рецепты режима MMO
	private fun registerTuning() {
		IncantationEquipmentElementalTuning(ElementalDamage.FIRE.name,		"o ken e ni: kon seli li kama jo e tomo lon insa ijo")
		IncantationEquipmentElementalTuning(ElementalDamage.WATER.name,		"ijo ni o kama poki telo")
		IncantationEquipmentElementalTuning(ElementalDamage.AIR.name,		"kon pi sike musi o sike o awen lon ijo ni")
		IncantationEquipmentElementalTuning(ElementalDamage.EARTH.name,		"o wan e ijo ni e wawa pi kiwen ali")
		IncantationEquipmentElementalTuning(ElementalDamage.ICE.name,		"o lete e ijo ni kepeken kon lete sina")
		IncantationEquipmentElementalTuning(ElementalDamage.ELECTRIC.name,	"o wawa e ijo ni kepeken wawa pi sewi Tola")
		IncantationEquipmentElementalTuning(ElementalDamage.NATURE.name,	"o pona e ijo ni kepeken sona pi linja kasi laso")
		IncantationEquipmentElementalTuning(ElementalDamage.LIGHTNESS.name,	"suno o walo e insa pi ijo ni")
		IncantationEquipmentElementalTuning(ElementalDamage.DARKNESS.name,	"pimeja o moku e suno ali lon ijo ni")
		IncantationEquipmentElementalTuning(ElementalDamage.PSYCHIC.name,	"ijo ni li wile ala e sijelo. o pakala e ni")
		
		tuningElementalSeer = AlfheimAPI.registerIncantation<ItemStack>(
			"o ken e ni: mi kama sona e ijo ali",
		    *EntityElementalSlime.allowedElements.map(ItemElvenResource.Companion::ballForElement).take(16).toTypedArray()
		) {
			if (it.item !is IBurstViewerBauble) return@registerIncantation false
			if (ItemNBTHelper.getBoolean(it, ElementalDamageHandler.TAG_ELEMENTAL_SEER, false)) return@registerIncantation false
			
			ItemNBTHelper.setBoolean(it, ElementalDamageHandler.TAG_ELEMENTAL_SEER, true)
			
			true
		}
		
		
		tuningResonatorDillation = AlfheimAPI.registerIncantation<ItemStack>("o ante e tenpo pali pi ilo ni") {
			if (it.item !== resonator || it.dilated) return@registerIncantation false
			it.dilated = true
			true
		}
		
		tuningResonatorPersistence = AlfheimAPI.registerIncantation<ItemStack>("o pana e awen tawa ijo pakala pi ilo ni") {
			if (it.item !== resonator || it.persistent) return@registerIncantation false
			it.persistent = true
			true
		}
		
		tuningResonatorUnlimit = AlfheimAPI.registerIncantation<ItemStack>("o ken e ni: ilo ni li pana e ijo pakala mute mute a") {
			if (it.item !== resonator || it.unlimited) return@registerIncantation false
			it.unlimited = true
			true
		}
		
		tuningAnomalyStabilization = AlfheimAPI.registerIncantation<TileAnomaly>("o lawa e nasa pi ma ali e nasa pi tenpo ali") {
			if (it.stable) return@registerIncantation false
			it.stable = true
			ASJUtilities.dispatchTEToNearbyPlayers(it)
			true
		}
		
		tuningAnomalyPackaging = AlfheimAPI.registerIncantation<TileAnomaly>("o pana ali tawa poka", *Array(4) { RiftShardGinnungagap.stack }) {
			if (!it.stable || it.subTileName.isEmpty()) return@registerIncantation false
			it.worldObj.setBlockToAir(it.xCoord, it.yCoord, it.zCoord)
			val stack = RiftDrive.stack
			ItemNBTHelper.setString(stack, TileAnomaly.TAG_SUBTILE_NAME, it.subTileName)
			EntityItem(it.worldObj, it.xCoord + 0.5, it.yCoord + 0.5, it.zCoord + 0.5, stack).apply { setMotion(0.0) }.spawn()
			true
		}
		
		tuningSaplings = arrayOf(
			registerItemCraftTuning("kasi kiwen pi kili loje o kama suli",				ItemStack(tunedSapling), ItemStack(sapling)),
			registerItemCraftTuning("kasi kiwen pi kili loje o kama sike",				ItemStack(tunedSapling, 1, 1), ItemStack(sapling)),
			registerItemCraftTuning("kasi kiwen walo o kama suli",						ItemStack(tunedSapling, 1, 2), ItemStack(sapling, 1, 2)),
			registerItemCraftTuning("kasi kiwen seli o kama lili",						ItemStack(tunedSapling, 1, 3), ItemStack(sapling, 1, 3)),
			registerItemCraftTuning("kasi kiwen pi kili loje o kama jo e kasi linja",	ItemStack(tunedSapling, 1, 4), ItemStack(sapling)),
			registerItemCraftTuning("kasi kiwen pi linja mute laso o kama suli",		ItemStack(tunedSapling, 1, 5), ItemStack(sapling, 1, 1)),
			registerItemCraftTuning("kasi kiwen pimeja pi kili loje o kama lili",		ItemStack(tunedSapling, 1, 6), ItemStack(sapling, 1, 5)),
			registerItemCraftTuning("kasi kiwen pi kili loje o kama jo e luka mute",	ItemStack(tunedSapling, 1, 7), ItemStack(sapling)),
		)
		
		AlfheimAPI.registerIncantation<EntityOcelot>("oiiaiuiiiiai") { it.entityData.setBoolean(AlfheimHookHandler.TAG_OIIA, true); true }
		
		tuningGrowthStop = AlfheimAPI.registerIncantation<EntityAgeable>("o awen e lili ona lon tenpo ale") {
			if (!it.isChild) return@registerIncantation false
			
			it.ageLocked = true
			it.ageLockedValue = it.growingAge
			true
		}
		
//		tuningCats = mapOf(
//			"soweli pi linja uta o kama kule ike" to arrayOf(3, 11, 14, 15).map { ItemStack(dye, 1, it) },
//			"soweli pi linja uta o kama loje jelo" to arrayOf(0, 0, 4, 15).map { ItemStack(dye, 1, it) },
//			"soweli pi linja uta o kama pimeja" to arrayOf(14, 14, 14, 15).map { ItemStack(dye, 1, it) },
//			"soweli pi linja uta o kama walo" to arrayOf(3, 7, 8, 15).map { ItemStack(dye, 1, it) },
//		).entries.withIndex().map { (id, parts) ->
//			AlfheimAPI.registerIncantation<EntityOcelot>(parts.key, *parts.value.toTypedArray()) { cat ->
//				if (!cat.isTamed || cat.tameSkin == id) return@registerIncantation false
//				
//				cat.tameSkin = id
//				true
//			}
//		}
//		
//		tuningCow = AlfheimAPI.registerIncantation<EntityCow>("kasi kili o kama lon selo pi soweli ni", *Array(5) { ItemStack(red_mushroom) }) {
//			if (it is EntityMooshroom) return@registerIncantation false
//			
//			it.setDead()
//			val moo = EntityMooshroom(it.worldObj)
//			moo.setLocationAndAngles(it.posX, it.posY, it.posZ, it.rotationYaw, it.rotationPitch)
//			moo.health = it.health
//			moo.renderYawOffset = it.renderYawOffset
//			moo.spawn()
//		}
		
		tuningSlimeSize = registerSlimeGrowthTune<EntitySlime>(ItemStack(slime_ball))
		tuningMagmaSize = registerSlimeGrowthTune<EntityMagmaCube>(ItemStack(magma_cream))
		tuningElementalSlimeSize = IncantationElementalSlimeGrowth(slimeGrowthApplication<EntityElementalSlime>())
		
		if (Botania.thaumcraftLoaded) {
			tuningTaintSize = registerSlimeGrowthTune<EntityThaumicSlime>(ItemStack(ConfigItems.itemResource, 1, 11))
			
			val wand = ItemStack(ConfigItems.itemWandCasting)
			tuningThaumWand = TunerIncantationIO(IncantationThaumWandOvercharge, wand, wand)
		}
		
		if (AlfheimCore.TiCLoaded) tuningGelatSize = registerSlimeGrowthTune<BlueSlime>(ItemStack(TinkerWorld.strangeFood))
		
		val smiteSword = ItemStack(golden_sword).apply { addEnchantment(Enchantment.smite, 5) }
		
		val diceStack = ItemStack(dice).apply { ItemNBTHelper.setBoolean(this, TAG_ASJIGNORENBT, true) }
		
		tuningAkashicRecords	= registerItemCraftTuning("lipu pi sona ali",								akashicRecords,		shimmerrock, book, bookshelf, monocle, lexicon, RUNE[11])
		tuningDaolos			= registerItemCraftTuning("ilo utala alasa telo",							daolos,				manasteelAxe, MUSPELHEIM_ESSENCE, water_bucket, JellyBottle.stack, fish, RUNE[0], RUNE[3])
		tuningExcaliber			= registerItemCraftTuning("ilo utala sewi kiwen",							excaliber,			terraSword, MAUFTRIUM_NUGGET, smiteSword, GLOWSTONE_DUST, ItemStack(shrineLight, 1, 4), diceStack.copy())
		tuningFlugelEye			= registerItemCraftTuning("oko pi ilo sewi ki moli e sewi a",	        	flugelEye,			ender_eye, RUNE[8], diceStack.copy(), ender_pearl, worldSeed, PIXIE_DUST)
		tuningFlugelSoul		= registerItemCraftTuning("kon pi ilo sewi ki moli e sewi a",	        	flugelSoul,			ItemStack(manaResource, 1, 5), *Array(8) { LIFE_ESSENCE }, *Array(8) { RUNE[it + 8] })
		tuningGjallarhorn		= registerItemCraftTuning("ilo kalama uta pi sewi Kejemetale",				gjallarhorn,		grassHorn, NIFLHEIM_ESSENCE, amplifier, RUNE[8], golden_horse_armor, skullPlacer)
		tuningGleipnir			= registerItemCraftTuning("ilo linja ki ken awen e soweli suli a",			gleipnir,			lead, amplifier, DasRheingold.stack, RUNE[2], FenrirFur.stack, fish, feather)
		tuningGungnir			= registerItemCraftTuning("ilo palisa utala pi sewi Oten",					gungnir,			manaGun, FenrirFur.stack, arrow, ItemStack(ancientWill, 1, 4), ItemStack(lens, 1, SUPERCONDUCTOR.meta))
		tuningHeimdallRing		= registerItemCraftTuning("sike pi palisa luka pi sewi Kejemetale",		    priestRingHeimdall,	pixieRing, MAUFTRIUM_NUGGET, RUNE[15], monocle, itemFinder, bifrostPerm, ender_eye)
		tuningInfiniteFruit		= registerItemCraftTuning("kili ki ken ala pini a pi sewi Sipe",			infiniteFruit,		golden_apple, RUNE[8], diceStack.copy(), manaCookie, manaBottle, RUNE[10])
		tuningKingKey			= registerItemCraftTuning("ilo open pi jan ki lawa e jan lawa mute a",		kingKey,			missileRod, RUNE[8], diceStack.copy(), golden_axe, golden_hoe, golden_sword)
		tuningLokiRing			= registerItemCraftTuning("sike pi palisa luka pi sewi Loki",			    lokiRing,			pixieRing, diceStack.copy(), RUNE[8], RUNE[1], enderHand, sextant)
		tuningMjolnir			= registerItemCraftTuning("ilo utala wawa pi sewi Tola",					mjolnir,			elvenChakram, NIFLHEIM_ESSENCE, RUNE[13], SPLINTERS_THUNDERWOOD, SPLINTERS_THUNDERWOOD, TWIG_THUNDERWOOD, teruTeruBozu)
		tuningMoonlightBow		= registerItemCraftTuning("ilo alasa palisa pi sewi Popepu",				moonlightBow,		crystalBow, MAUFTRIUM_NUGGET, RUNE[8], RUNE[5], colorOverride, noteblock, QUARTZ[6])
		tuningNjordRing			= registerItemCraftTuning("sike pi palisa luka pi sewi Nijete",			    priestRingNjord,	pixieRing, MAUFTRIUM_NUGGET, RUNE[0], RUNE[3], ItemStack(fish, 1, 3), overgrowthSeed)
		tuningOdinRing			= registerItemCraftTuning("sike pi palisa luka pi sewi Oten",			    odinRing,			pixieRing, diceStack.copy(), spider_eye, magma_cream, RUNE[13])
		tuningSifRing			= registerItemCraftTuning("sike pi palisa luka pi sewi Sipe",			    priestRingSif,		pixieRing, MAUFTRIUM_NUGGET, RUNE[5], RUNE[2], DasRheingold.stack, overgrowthSeed)
		tuningSpearSubspace		= registerItemCraftTuning("ilo palisa utala ki lon ma ali pi pilin ala a",	subspaceSpear,		InfusedDreamwoodTwig.stack, RUNE[8], MUSPELHEIM_ESSENCE, subspacian, enlighter, manaBomb)
		tuningTankMask			= registerItemCraftTuning("len kiwen nasa pi sinpin lawa",					mask,				ItemStack(cosmetic, 1, 22), MAUFTRIUM_NUGGET, SaveIvy.stack, leather_helmet, RUNE[13], manaVoid)
		tuningThorRing			= registerItemCraftTuning("sike pi palisa luka pi sewi Tola",			    thorRing,			pixieRing, diceStack.copy(), TERRA_STEEL, temperanceStone, RUNE[13], TWIG_THUNDERWOOD)
	}
	
	private inline fun <reified T: EntityLivingBase> registerSlimeGrowthTune(item: Any): TunerIncantation<EntityLivingBase> {
		return AlfheimAPI.registerIncantation<T>("jaki o kama suli", *Array(4) { item }, application = slimeGrowthApplication<T>())
	}
	
	private inline fun <reified T: EntityLivingBase> slimeGrowthApplication(): (T) -> Boolean = application@ {
		if (it::class.java != T::class.java) return@application false // no other types for this
		val nbt = NBTTagCompound()
		it.writeEntityToNBT(nbt)
		val newSize = nbt.getInteger("Size") + 1
		if (newSize > Byte.MAX_VALUE) return@application false // no overflow
		nbt.setInteger("Size", newSize)
		it.readEntityFromNBT(nbt)
		
		if (newSize == Byte.MAX_VALUE.I) getEntitiesWithinAABB(it.worldObj, EntityPlayer::class.java, it.boundingBox(8)).forEach { p ->
			p.triggerAchievement(AlfheimAchievements.slime)
		}
		
		true
	}
	
	fun registerItemCraftTuning(incantation: String, result: Item, acore: Any, vararg inputs: Any) =
		registerItemCraftTuning(incantation, ItemStack(result), acore, *inputs)
	
	fun registerItemCraftTuning(incantation: String, result: ItemStack, acore: Any, vararg inputs: Any): TunerIncantationIO {
		val core = when (acore) {
			is Block -> ItemStack(acore)
			is Item -> ItemStack(acore)
			is ItemStack -> acore
			else -> throw IllegalArgumentException("Tuning core $acore of type ${acore::class.java.name} is unexpected")
		}
		
		val tuning = AlfheimAPI.registerIncantation<ItemStack>(incantation, *inputs.map {
			when (it) {
				is Block -> ItemStack(it)
				is Item -> ItemStack(it)
				is ItemStack -> it
				is String -> it
				else -> throw IllegalArgumentException("Tuning input $it of type ${it::class.java.name} is unexpected")
			}
		}.toTypedArray()) {
			if (!core.isItemEqual(it)) return@registerIncantation false
			
			it.func_150996_a(result.item)
			it.stackSize = result.stackSize
			it.meta = result.meta
			it.tagCompound = result.tagCompound ?: NBTTagCompound()
			true
		}
		
		return TunerIncantationIO(tuning, core, result)
	}
	
	private fun banRetrades() {
		//AlfheimAPI.banRetrade(recipeStoryToken.output)
		AlfheimAPI.banRetrade(ItemStack(iron_ingot))
		AlfheimAPI.banRetrade(ItemStack(iron_block))
		AlfheimAPI.banRetrade(ItemStack(ender_pearl))
		AlfheimAPI.banRetrade(ItemStack(diamond))
		AlfheimAPI.banRetrade(ItemStack(diamond_block))
	}
	
	fun addMMORecipes() {
		CraftingManager.getInstance().recipeList.add(recipePaperBreak)
		CraftingManager.getInstance().recipeList.add(recipePeacePipe)
	}
	
	fun removeMMORecipes() {
		ASJUtilities.removeRecipe(paperBreak, 4)
		ASJUtilities.removeRecipe(peacePipe)
	}
	*/
	
	// PORT: варианты кварца — массив блоков (SPEC, Р-5): block[0] — блок, [1] — резной, [2] — колонна. Массив в рецепте по
	// шаблону — любой вариант, как Block 1.7.10 (MAPPING.md, «Рецепты»)
	private fun addQuartzRecipes(block: Array<Block>, stairs: Block, slab: Block): IRecipe {
//	private fun addQuartzRecipes(block: Block, stairs: Block, slab: Block): IRecipe {
		addRecipe(ItemStack(block[0]),
//		addRecipe(ItemStack(block),
				  "QQ",
				  "QQ",
				  'Q', RainbowQuartz.stack)
		
		// PORT: КТ-3 — рецепты типов Botania (алхимия маны)
//		BotaniaAPI.registerManaAlchemyRecipe(RainbowQuartz.stack(4), ItemStack(block, 1, 32767), 25)
		
		addRecipe(ItemStack(block[2], 2),
//		addRecipe(ItemStack(block, 2, 2),
				  "Q",
				  "Q",
				  'Q', block)
		
		addRecipe(ItemStack(block[1], 1),
//		addRecipe(ItemStack(block, 1, 1),
				  "Q",
				  "Q",
				  'Q', slab)
		addStairsAndSlabs(block[0], 0, stairs, slab)
//		addStairsAndSlabs(block, 0, stairs, slab)
		
		addRecipe(ShapedOreRecipe(RainbowQuartz.stack(8),
								  "QQQ",
								  "QCQ",
								  "QQQ",
								  'Q', "gemQuartz",
								  'C', DYES[16]))
		return BotaniaAPI.getLatestAddedRecipe()
	}
	
	private fun addStairsAndSlabs(block: Block, meta: Int, stairs: Block, slab: Block) {
		// PORT: вариант metadata — сам блок (block), номер варианта остаётся параметром (MAPPING.md, «Блоки и предметы»)
		addRecipe(ItemStack(slab, 6), "QQQ", 'Q', ItemStack(block))
		addRecipe(ItemStack(stairs, 4), "Q  ", "QQ ", "QQQ", 'Q', ItemStack(block))
	}
	
	/* PORT: КТ-3 — головы для лепестков; КТ-7 — расширенный режим ESM
	fun skullStack(name: String): ItemStack {
		val stack = ItemStack(skullPlacer, 1, 3)
		ItemNBTHelper.setString(stack, "SkullOwner", name)
		return stack
	}
	
	private fun attributionSkull(name: String, item: Item, meta: Int) =
		BotaniaAPI.registerPetalRecipe(skullStack(name), *Array(16) { ItemStack(item, 1, meta) })
	
	fun extendESM() {
		if (!AlfheimConfigHandler.extendedElvenStory) return
		
		addInfuserRecipe(ItemStack(blaze_rod),
		                 12000,
		                 INFUSED_DREAM_TWIG,
		                 ItemStack(blaze_powder, 3))
		
		addInfuserRecipe(ItemStack(netherrack),
		                 666,
		                 ItemStack(blaze_powder),
		                 ItemStack(livingcobble))
		
		addOreDictRecipe(ItemStack(Items.brewing_stand),
		                 " R ", "CCC",
		                 'R', blaze_rod,
		                 'C', ItemStack(livingcobble))
		
		addOreDictRecipe(ItemStack(runeAltar),
		                 "SSS", "SDS",
		                 'S', LIVING_ROCK,
		                 'D', DRAGONSTONE)
		
		addSmelting(ItemStack(manaResource, 1, 0), ItemStack(iron_ingot), 0f)
		addSmelting(ItemStack(manaResource, 1, 1), ItemStack(ender_pearl), 0f)
		addSmelting(ItemStack(manaResource, 1, 2), ItemStack(diamond), 0f)
		addSmelting(manaGlass, ItemStack(glass), 0f)
		addSmelting(managlassPane, ItemStack(glass_pane), 0f)
	}
	*/
}
