@file:Suppress("LocalVariableName")

package alfheim.common.integration.thaumcraft

import alfheim.api.ModInfo
import alfheim.api.lib.LibOreDict
import alfheim.api.lib.LibResourceLocations.ResourceLocationIL
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.AlfheimBlocks.alfheimPortal
import alfheim.common.block.AlfheimBlocks.alfheimPylon
import alfheim.common.block.AlfheimBlocks.altLeaves
import alfheim.common.block.AlfheimBlocks.altWood1
import alfheim.common.block.AlfheimBlocks.animatedTorch
import alfheim.common.block.AlfheimBlocks.anomaly
import alfheim.common.block.AlfheimBlocks.anyavil
import alfheim.common.block.AlfheimBlocks.barrier
import alfheim.common.block.AlfheimBlocks.barrierBerry
import alfheim.common.block.AlfheimBlocks.calicoBerry
import alfheim.common.block.AlfheimBlocks.circuitBerry
import alfheim.common.block.AlfheimBlocks.domainDoor
import alfheim.common.block.AlfheimBlocks.dreamSapling
import alfheim.common.block.AlfheimBlocks.elvenOre
import alfheim.common.block.AlfheimBlocks.elvenSand
import alfheim.common.block.AlfheimBlocks.grapesRed
import alfheim.common.block.AlfheimBlocks.grapesWhite
import alfheim.common.block.AlfheimBlocks.helheimBlock
import alfheim.common.block.AlfheimBlocks.icicle
import alfheim.common.block.AlfheimBlocks.icyGeyser
import alfheim.common.block.AlfheimBlocks.lightningBerry
import alfheim.common.block.AlfheimBlocks.livingcobble
import alfheim.common.block.AlfheimBlocks.lootbox
import alfheim.common.block.AlfheimBlocks.manaAccelerator
import alfheim.common.block.AlfheimBlocks.manaFluidBlock
import alfheim.common.block.AlfheimBlocks.manaInfuser
import alfheim.common.block.AlfheimBlocks.netherBerry
import alfheim.common.block.AlfheimBlocks.nidhoggTooth
import alfheim.common.block.AlfheimBlocks.niflheimBlock
import alfheim.common.block.AlfheimBlocks.niflheimPortal
import alfheim.common.block.AlfheimBlocks.poisonIce
import alfheim.common.block.AlfheimBlocks.powerStone
import alfheim.common.block.AlfheimBlocks.raceSelector
import alfheim.common.block.AlfheimBlocks.rainbowGrass
import alfheim.common.block.AlfheimBlocks.rainbowMushroom
import alfheim.common.block.AlfheimBlocks.rainbowTallFlower
import alfheim.common.block.AlfheimBlocks.redFlame
import alfheim.common.block.AlfheimBlocks.redstoneRelay
import alfheim.common.block.AlfheimBlocks.rift
import alfheim.common.block.AlfheimBlocks.sadOakLeaves
import alfheim.common.block.AlfheimBlocks.schemaAnnihilator
import alfheim.common.block.AlfheimBlocks.schemaController
import alfheim.common.block.AlfheimBlocks.schemaFiller
import alfheim.common.block.AlfheimBlocks.schemaGenerator
import alfheim.common.block.AlfheimBlocks.schemaMarker
import alfheim.common.block.AlfheimBlocks.sealingBerry
import alfheim.common.block.AlfheimBlocks.shimmerQuartzSlabFull
import alfheim.common.block.AlfheimBlocks.snakeBody
import alfheim.common.block.AlfheimBlocks.snakeObject
import alfheim.common.block.AlfheimBlocks.snowGrass
import alfheim.common.block.AlfheimBlocks.snowLayer
import alfheim.common.block.AlfheimBlocks.softStorage
import alfheim.common.block.AlfheimBlocks.stalactite
import alfheim.common.block.AlfheimBlocks.stalagmite
import alfheim.common.block.AlfheimBlocks.subspacian
import alfheim.common.block.AlfheimBlocks.tradePortal
import alfheim.common.block.AlfheimBlocks.treeCrafterBlockAU
import alfheim.common.block.AlfheimBlocks.tunedSapling
import alfheim.common.block.AlfheimBlocks.yggFlower
import alfheim.common.block.AlfheimFluffBlocks.chair
import alfheim.common.block.AlfheimFluffBlocks.composite
import alfheim.common.block.AlfheimFluffBlocks.doubleBlock
import alfheim.common.block.AlfheimFluffBlocks.dwarfPlanks
import alfheim.common.block.AlfheimFluffBlocks.dwarfPlanksSlab
import alfheim.common.block.AlfheimFluffBlocks.dwarfPlanksSlabFull
import alfheim.common.block.AlfheimFluffBlocks.dwarfPlanksStairs
import alfheim.common.block.AlfheimFluffBlocks.dwarfTrapDoor
import alfheim.common.block.AlfheimFluffBlocks.elfQuartzWall
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstone
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneSlab2
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneSlab2Full
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneSlabFull
import alfheim.common.block.AlfheimFluffBlocks.livingMountainSlab
import alfheim.common.block.AlfheimFluffBlocks.livingMountainSlabFull
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlab
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlab1
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlab2
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlabFull
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlabFull1
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlabFull2
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleStairs1
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleStairs2
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleWall
import alfheim.common.block.AlfheimFluffBlocks.livingrockDark
import alfheim.common.block.AlfheimFluffBlocks.livingrockDarkSlabs
import alfheim.common.block.AlfheimFluffBlocks.livingrockDarkSlabsFull
import alfheim.common.block.AlfheimFluffBlocks.livingrockDarkStairs
import alfheim.common.block.AlfheimFluffBlocks.roofTile
import alfheim.common.block.AlfheimFluffBlocks.roofTileSlabs
import alfheim.common.block.AlfheimFluffBlocks.roofTileSlabsFull
import alfheim.common.block.AlfheimFluffBlocks.roofTileStairs
import alfheim.common.block.AlfheimFluffBlocks.secretGlass
import alfheim.common.block.AlfheimFluffBlocks.shrineGlass
import alfheim.common.block.AlfheimFluffBlocks.shrinePanel
import alfheim.common.block.AlfheimFluffBlocks.shrinePillar
import alfheim.common.block.AlfheimFluffBlocks.shrineRock
import alfheim.common.block.AlfheimFluffBlocks.shrineRockWhiteSlab
import alfheim.common.block.AlfheimFluffBlocks.shrineRockWhiteSlabFull
import alfheim.common.block.AlfheimFluffBlocks.yggDecor
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.integration.thaumcraft.ForbiddenMagicIntegration.ENVY
import alfheim.common.integration.thaumcraft.ForbiddenMagicIntegration.GLUTTONY
import alfheim.common.integration.thaumcraft.ForbiddenMagicIntegration.LUST
import alfheim.common.integration.thaumcraft.ForbiddenMagicIntegration.NETHER
import alfheim.common.integration.thaumcraft.ForbiddenMagicIntegration.PRIDE
import alfheim.common.integration.thaumcraft.ForbiddenMagicIntegration.SLOTH
import alfheim.common.integration.thaumcraft.ForbiddenMagicIntegration.WRATH
import alfheim.common.integration.thaumcraft.TCHandlerShadowFoxAspects.COLOR
import alfheim.common.integration.thaumcraft.ThaumcraftAlfheimModule.alfheimThaumOre
import alfheim.common.item.*
import alfheim.common.item.AlfheimItems.astrolabe
import alfheim.common.item.AlfheimItems.auraRingElven
import alfheim.common.item.AlfheimItems.auraRingGod
import alfheim.common.item.AlfheimItems.cloudPendant
import alfheim.common.item.AlfheimItems.cloudPendantSuper
import alfheim.common.item.AlfheimItems.creativeReachPendant
import alfheim.common.item.AlfheimItems.discFlugel
import alfheim.common.item.AlfheimItems.dodgeRing
import alfheim.common.item.AlfheimItems.elementalBoots
import alfheim.common.item.AlfheimItems.elementalChestplate
import alfheim.common.item.AlfheimItems.elementalHelmet
import alfheim.common.item.AlfheimItems.elementalHelmetRevealing
import alfheim.common.item.AlfheimItems.elementalLeggings
import alfheim.common.item.AlfheimItems.elfFirePendant
import alfheim.common.item.AlfheimItems.elfIcePendant
import alfheim.common.item.AlfheimItems.elvoriumBoots
import alfheim.common.item.AlfheimItems.elvoriumChestplate
import alfheim.common.item.AlfheimItems.elvoriumHelmet
import alfheim.common.item.AlfheimItems.elvoriumHelmetRevealing
import alfheim.common.item.AlfheimItems.elvoriumLeggings
import alfheim.common.item.AlfheimItems.excaliber
import alfheim.common.item.AlfheimItems.flugelSoul
import alfheim.common.item.AlfheimItems.invisibilityCloak
import alfheim.common.item.AlfheimItems.livingrockPickaxe
import alfheim.common.item.AlfheimItems.lootInterceptor
import alfheim.common.item.AlfheimItems.manaRingElven
import alfheim.common.item.AlfheimItems.manaRingGod
import alfheim.common.item.AlfheimItems.manaStone
import alfheim.common.item.AlfheimItems.manaStoneGreater
import alfheim.common.item.AlfheimItems.mask
import alfheim.common.item.AlfheimItems.paperBreak
import alfheim.common.item.AlfheimItems.peacePipe
import alfheim.common.item.AlfheimItems.pixieAttractor
import alfheim.common.item.AlfheimItems.realitySword
import alfheim.common.item.AlfheimItems.rodGrass
import alfheim.common.item.AlfheimItems.rodMuspelheim
import alfheim.common.item.AlfheimItems.rodNiflheim
import alfheim.common.item.material.ElvenResourcesMetas
import cpw.mods.fml.common.Loader
import net.minecraft.block.Block
import net.minecraft.init.*
import net.minecraft.init.Blocks.dirt
import net.minecraft.item.*
import net.minecraft.util.ResourceLocation
import net.minecraftforge.oredict.OreDictionary
import org.lwjgl.opengl.GL11
import thaumcraft.api.ThaumcraftApi
import thaumcraft.api.aspects.*
import thaumcraft.api.aspects.Aspect.*
import vazkii.botania.common.block.ModBlocks.*
import vazkii.botania.common.block.ModFluffBlocks.*
import vazkii.botania.common.item.ModItems.*
import vazkii.botania.common.lib.LibEntityNames
import vazkii.botania.common.block.ModBlocks.cacophonium as cacoblock
import vazkii.botania.common.item.ModItems.gaiaHead as gaiaHeadItem
import vazkii.botania.common.item.ModItems.tinyPlanet as tinyPlanetItem

private fun AspectList.a(asp: Aspect?, n: Int = 1): AspectList {
	if (asp != null)
		this.add(asp, n)
	return this
}

private fun wildStack(i: Block): ItemStack = ItemStack(i, 1, OreDictionary.WILDCARD_VALUE)
private fun wildStack(i: Item): ItemStack = ItemStack(i, 1, OreDictionary.WILDCARD_VALUE)

object ForbiddenMagicIntegration {
	private val forbidden = Loader.isModLoaded("ForbiddenMagic")
	
	val NETHER:   Aspect = if (forbidden) getAspect("infernus") else FIRE
	val LUST:     Aspect = if (forbidden) getAspect("luxuria") else FLESH
	val PRIDE:    Aspect = if (forbidden) getAspect("superbia") else COLOR
	val GLUTTONY: Aspect = if (forbidden) getAspect("gula") else HUNGER
	val ENVY:     Aspect = if (forbidden) getAspect("invidia") else TRAP
	val WRATH:    Aspect = if (forbidden) getAspect("ira") else WEAPON
	val SLOTH:    Aspect = if (forbidden) getAspect("desidia") else CLOTH
}

object TCHandlerAlfheimAspects {
	
	fun addAspects() {
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimThaumOre, 1, 0), AspectList().a(EARTH).a(METAL, 2).a(EXCHANGE, 2).a(POISON))
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimThaumOre, 1, 1), AspectList().a(EARTH).a(CRYSTAL, 2).a(AIR, 3))
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimThaumOre, 1, 2), AspectList().a(EARTH).a(CRYSTAL, 2).a(FIRE, 3))
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimThaumOre, 1, 3), AspectList().a(EARTH).a(CRYSTAL, 2).a(WATER, 3))
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimThaumOre, 1, 4), AspectList().a(EARTH).a(CRYSTAL, 2).a(EARTH, 3))
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimThaumOre, 1, 5), AspectList().a(EARTH).a(CRYSTAL, 2).a(ORDER, 3))
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimThaumOre, 1, 6), AspectList().a(EARTH).a(CRYSTAL, 2).a(ENTROPY, 3))
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimThaumOre, 1, 7), AspectList().a(EARTH).a(CRYSTAL, 2).a(TRAP, 3))
		
		ThaumcraftApi.registerObjectTag(ItemStack(elvenOre, 1, 0), AspectList().a(MAGIC).a(EARTH).a(GREED).a(CRYSTAL, 2))    // dragonstone
		ThaumcraftApi.registerObjectTag(ItemStack(elvenOre, 1, 1), AspectList().a(MAGIC).a(EARTH).a(METAL, 3))               // elementium
		ThaumcraftApi.registerObjectTag(ItemStack(elvenOre, 1, 2), AspectList().a(MAGIC).a(EARTH).a(CRYSTAL, 3))             // quartz
		ThaumcraftApi.registerObjectTag(ItemStack(elvenOre, 1, 3), AspectList().a(MAGIC).a(EARTH).a(GREED).a(METAL, 2))      // gold
		ThaumcraftApi.registerObjectTag(ItemStack(elvenOre, 1, 4), AspectList().a(MAGIC).a(EARTH).a(SENSES, 3))              // iffesal
		ThaumcraftApi.registerObjectTag(ItemStack(elvenOre, 1, 5), AspectList().a(MAGIC).a(EARTH).a(COLOR, 3))               // lapis
		
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimPortal), AspectList().a(AURA, 2).a(TREE, 8).a(TRAVEL, 4).a(METAL, 4).a(MAGIC, 8))
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimPylon, 1, 0), AspectList().a(METAL, 6).a(MAGIC, 12).a(CRYSTAL, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimPylon, 1, 1), AspectList().a(AURA, 2).a(METAL, 6).a(ENERGY, 2).a(MAGIC, 12).a(CRYSTAL, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(alfheimPylon, 1, 2), AspectList().a(SOUL, 2).a(METAL, 6).a(ENERGY, 6).a(MAGIC, 12).a(CRYSTAL, 4).a(ELDRITCH, 3))
		ThaumcraftApi.registerObjectTag(ItemStack(altWood1, 1, 2), AspectList().a(TRAVEL, 8).a(MIND, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(altWood1, 1, 3), AspectList().a(TREE, 4).a(SENSES))
		ThaumcraftApi.registerObjectTag(ItemStack(altLeaves), intArrayOf(6, 14), AspectList().a(LIFE).a(TRAVEL))
		ThaumcraftApi.registerObjectTag(ItemStack(altLeaves), intArrayOf(7, 15), AspectList().a(PLANT).a(LIGHT))
		ThaumcraftApi.registerObjectTag(ItemStack(animatedTorch), AspectList().a(MOTION).a(ENERGY).a(MECHANISM))
		ThaumcraftApi.registerObjectTag(ItemStack(anomaly), AspectList().a(ELDRITCH, 8).a(ENERGY, 2).a(ENTROPY, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(anyavil), AspectList().a(GREED, 10).a(METAL, 46).a(MAGIC, 52))
		ThaumcraftApi.registerObjectTag(ItemStack(dreamSapling), AspectList().a(TREE).a(PLANT, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(dwarfPlanks), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(ItemStack(dwarfPlanksSlab), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(ItemStack(dwarfPlanksSlabFull), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(ItemStack(dwarfPlanksStairs), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(ItemStack(dwarfTrapDoor), AspectList().a(MOTION).a(TREE, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(elvenSand), AspectList().a(ENTROPY).a(EARTH))
		ThaumcraftApi.registerObjectTag(ItemStack(manaAccelerator), AspectList().a(EARTH, 4).a(ENERGY, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(manaInfuser), AspectList().a(CRAFT, 5).a(METAL, 10).a(ORDER, 4).a(EXCHANGE, 4).a(MAGIC, 16).a(EARTH, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(poisonIce), AspectList().a(COLD, 16).a(ORDER, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(redFlame), AspectList().a(FIRE, 16).a(ENTROPY, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(tradePortal), AspectList().a(TRAVEL, 4).a(METAL).a(MAGIC, 4).a(EARTH, 6))
		
		ThaumcraftApi.registerObjectTag(wildStack(yggDecor),                          AspectList().a(TREE,      1))
		ThaumcraftApi.registerObjectTag(wildStack(sadOakLeaves),                      AspectList().a(PLANT,     1))
		ThaumcraftApi.registerObjectTag(wildStack(dirt),                              AspectList().a(EARTH,     2))
		ThaumcraftApi.registerObjectTag(wildStack(snowLayer),                         AspectList().a(COLD,      1))
		ThaumcraftApi.registerObjectTag(wildStack(livingMountainSlab),                AspectList().a(EARTH,     1))
		ThaumcraftApi.registerObjectTag(wildStack(livingMountainSlabFull),            AspectList().a(EARTH,     1))
		ThaumcraftApi.registerObjectTag(wildStack(roofTile),                          AspectList().a(EARTH,     1). a(FIRE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(wildStack(roofTileSlabs[0]),                  AspectList().a(EARTH,     1). a(FIRE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(wildStack(roofTileSlabs[1]),                  AspectList().a(EARTH,     1). a(FIRE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(wildStack(roofTileSlabs[2]),                  AspectList().a(EARTH,     1). a(FIRE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(wildStack(roofTileSlabsFull[0]),              AspectList().a(EARTH,     1). a(FIRE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(wildStack(roofTileSlabsFull[1]),              AspectList().a(EARTH,     1). a(FIRE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(wildStack(roofTileSlabsFull[2]),              AspectList().a(EARTH,     1). a(FIRE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(wildStack(roofTileStairs[0]),                 AspectList().a(EARTH,     1). a(FIRE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(wildStack(roofTileStairs[1]),                 AspectList().a(EARTH,     1). a(FIRE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(wildStack(roofTileStairs[2]),                 AspectList().a(EARTH,     1). a(FIRE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(wildStack(shrineRock),                        AspectList().a(EARTH,     2). a(COLOR,    1))
		ThaumcraftApi.registerObjectTag(wildStack(shrineRockWhiteSlab),               AspectList().a(EARTH,     1))
		ThaumcraftApi.registerObjectTag(wildStack(shrineRockWhiteSlabFull),           AspectList().a(EARTH,     1))
		ThaumcraftApi.registerObjectTag(wildStack(shrinePillar),                      AspectList().a(EARTH,     2). a(COLOR,    1))
		ThaumcraftApi.registerObjectTag(ItemStack(snakeObject,                1, 0),  AspectList().a(LIFE,      4). a(BEAST,    4))
		ThaumcraftApi.registerObjectTag(ItemStack(snakeObject,                1, 1),  AspectList().a(HUNGER,    24))
		ThaumcraftApi.registerObjectTag(ItemStack(chair,                      1, 6),  AspectList().a(TREE,      1))
		ThaumcraftApi.registerObjectTag(wildStack(composite),                         AspectList().a(CRAFT,     1))
		ThaumcraftApi.registerObjectTag(wildStack(manaFluidBlock),                    AspectList().a(MAGIC,     4))
		ThaumcraftApi.registerObjectTag(wildStack(snowGrass),                         AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(livingcobble,               1, 0),  AspectList().a(EARTH,     1). a(ENTROPY,  1))
		ThaumcraftApi.registerObjectTag(ItemStack(livingcobble,               1, 1),  AspectList().a(EARTH,     2). a(LIFE,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(livingcobble,               1, 2),  AspectList().a(EARTH,     2). a(LIFE,     1))
		ThaumcraftApi.registerObjectTag(wildStack(livingcobbleSlab),                  AspectList().a(ENTROPY,   1). a(EARTH,    1))
		ThaumcraftApi.registerObjectTag(wildStack(livingcobbleSlab1),                 AspectList().a(EARTH,     2). a(LIFE, 1))
		ThaumcraftApi.registerObjectTag(wildStack(livingcobbleSlab2),                 AspectList().a(EARTH,     1))
		ThaumcraftApi.registerObjectTag(wildStack(livingcobbleSlabFull),              AspectList().a(ENTROPY,   1). a(EARTH,    1))
		ThaumcraftApi.registerObjectTag(wildStack(livingcobbleSlabFull1),             AspectList().a(EARTH,     2). a(LIFE,     1))
		ThaumcraftApi.registerObjectTag(wildStack(livingcobbleSlabFull2),             AspectList().a(EARTH,     2). a(LIFE,     1))
		ThaumcraftApi.registerObjectTag(wildStack(livingcobbleStairs1),               AspectList().a(EARTH,     2). a(LIFE,     1))
		ThaumcraftApi.registerObjectTag(wildStack(livingcobbleStairs2),               AspectList().a(EARTH,     2). a(LIFE,     1))
		ThaumcraftApi.registerObjectTag(wildStack(livingcobbleWall),                  AspectList().a(ENTROPY,   1). a(EARTH,    1))
		
		// TODO 
		ThaumcraftApi.registerObjectTag(ItemStack(livingrockDarkSlabsFull[0], 1, 0),  AspectList().a(ENERGY,    1). a(FIRE,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(livingrockDarkSlabsFull[1], 1, 0),  AspectList().a(ENERGY,    1). a(FIRE,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(elvenSandstone,             1, 1),  AspectList().a(ENTROPY,   1). a(EARTH,    1))
		ThaumcraftApi.registerObjectTag(ItemStack(elvenSandstone,             1, 4),  AspectList().a(ENTROPY,   3). a(EARTH,    3))
		ThaumcraftApi.registerObjectTag(ItemStack(elvenSandstoneSlab2,        1, 0),  AspectList().a(ENTROPY,   2). a(EARTH,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(elvenSandstoneSlabFull,     1, 0),  AspectList().a(ENTROPY,   3). a(EARTH,    3))
		ThaumcraftApi.registerObjectTag(ItemStack(elvenSandstoneSlab2Full,    1, 0),  AspectList().a(ENTROPY,   2). a(EARTH,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(shimmerQuartzSlabFull,      1, 0),  AspectList().a(CRYSTAL,   1). a(ENERGY,   1))
		ThaumcraftApi.registerObjectTag(ItemStack(elfQuartzWall,              1, 0),  AspectList().a(CRYSTAL,   3). a(ENERGY,   3))
		ThaumcraftApi.registerObjectTag(ItemStack(shrineGlass,                1, 0),  AspectList().a(CRYSTAL,   1). a(ELDRITCH, 1))
		ThaumcraftApi.registerObjectTag(ItemStack(shrineGlass,                1, 1),  AspectList().a(CRYSTAL,   1). a(ELDRITCH, 1))
		ThaumcraftApi.registerObjectTag(ItemStack(shrineGlass,                1, 2),  AspectList().a(CRYSTAL,   1). a(ELDRITCH, 1))
		ThaumcraftApi.registerObjectTag(ItemStack(shrineGlass,                1, 3),  AspectList().a(CRYSTAL,   1). a(ELDRITCH, 1))
		ThaumcraftApi.registerObjectTag(ItemStack(shrineGlass,                1, 4),  AspectList().a(CRYSTAL,   1). a(ELDRITCH, 1))
		ThaumcraftApi.registerObjectTag(ItemStack(shrinePanel,                1, 0),  AspectList().a(CRYSTAL,   1). a(ELDRITCH, 1))
		ThaumcraftApi.registerObjectTag(ItemStack(shrinePanel,                1, 1),  AspectList().a(CRYSTAL,   1). a(ELDRITCH, 1))
		ThaumcraftApi.registerObjectTag(ItemStack(shrinePanel,                1, 2),  AspectList().a(CRYSTAL,   1). a(ELDRITCH, 1))
		ThaumcraftApi.registerObjectTag(ItemStack(shrinePanel,                1, 3),  AspectList().a(CRYSTAL,   1). a(ELDRITCH, 1))
		ThaumcraftApi.registerObjectTag(ItemStack(tunedSapling,               1, 0),  AspectList().a(TREE,      1). a(PLANT,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(tunedSapling,               1, 1),  AspectList().a(TREE,      1). a(PLANT,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(tunedSapling,               1, 2),  AspectList().a(TREE,      1). a(PLANT,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(tunedSapling,               1, 3),  AspectList().a(TREE,      1). a(PLANT,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(tunedSapling,               1, 4),  AspectList().a(TREE,      1). a(PLANT,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(tunedSapling,               1, 5),  AspectList().a(TREE,      1). a(PLANT,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(tunedSapling,               1, 6),  AspectList().a(TREE,      1). a(PLANT,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(tunedSapling,               1, 7),  AspectList().a(TREE,      1). a(PLANT,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(grapesRed[0],               1, 0),  AspectList().a(PLANT,     1). a(HUNGER,   1))
		ThaumcraftApi.registerObjectTag(ItemStack(grapesRed[1],               1, 0),  AspectList().a(PLANT,     1). a(HUNGER,   1))
		ThaumcraftApi.registerObjectTag(ItemStack(grapesRed[2],               1, 0),  AspectList().a(PLANT,     1). a(HUNGER,   1))
		ThaumcraftApi.registerObjectTag(ItemStack(niflheimBlock,              1, 0),  AspectList().a(EARTH,     2). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(niflheimBlock,              1, 3),  AspectList().a(EARTH,     2). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(niflheimBlock,              1, 4),  AspectList().a(EARTH,     2). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(niflheimBlock,              1, 5),  AspectList().a(EARTH,     2). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(niflheimBlock,              1, 6),  AspectList().a(EARTH,     2). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(niflheimBlock,              1, 7),  AspectList().a(EARTH,     2). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(niflheimBlock,              1, 10), AspectList().a(EARTH,     2). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(stalactite,                 1, 0),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalactite,                 1, 1),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalactite,                 1, 2),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalactite,                 1, 3),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalactite,                 1, 4),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(doubleBlock,                1, 0),  AspectList().a(CRAFT,     1). a(ENTROPY,  1))
		ThaumcraftApi.registerObjectTag(ItemStack(schemaController,           1, 0),  AspectList().a(MECHANISM, 1). a(ORDER,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(schemaFiller,               1, 0),  AspectList().a(MECHANISM, 1). a(WATER,    2))
		ThaumcraftApi.registerObjectTag(ItemStack(schemaMarker,               1, 0),  AspectList().a(MECHANISM, 1). a(SENSES,   2))
		ThaumcraftApi.registerObjectTag(ItemStack(schemaAnnihilator,          1, 0),  AspectList().a(MECHANISM, 1). a(ENTROPY,  2))
		ThaumcraftApi.registerObjectTag(ItemStack(stalactite,                 1, 5),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalactite,                 1, 6),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalactite,                 1, 7),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalagmite,                 1, 0),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalagmite,                 1, 1),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalagmite,                 1, 2),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalagmite,                 1, 3),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalagmite,                 1, 4),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalagmite,                 1, 5),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalagmite,                 1, 6),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(stalagmite,                 1, 7),  AspectList().a(EARTH,     1). a(COLD,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(icicle,                     1, 0),  AspectList().a(MOTION,    1). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(icicle,                     1, 1),  AspectList().a(MOTION,    1). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(icicle,                     1, 2),  AspectList().a(MOTION,    1). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(domainDoor,                 1, 0),  AspectList().a(EARTH,     2). a(ELDRITCH, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(domainDoor,                 1, 1),  AspectList().a(EARTH,     2). a(ELDRITCH, 4) .a(TRAVEL,    4))
		ThaumcraftApi.registerObjectTag(ItemStack(domainDoor,                 1, 2),  AspectList().a(EARTH,     2). a(ELDRITCH, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(domainDoor,                 1, 3),  AspectList().a(EARTH,     2). a(ELDRITCH, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(domainDoor,                 1, 4),  AspectList().a(EARTH,     2). a(ELDRITCH, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(icicle,                     1, 3),  AspectList().a(MOTION,    1). a(COLD,     2))
		ThaumcraftApi.registerObjectTag(ItemStack(livingrockDark,             1, 3),  AspectList().a(EARTH,     1). a(ENERGY,   1). a(FIRE,      1))
		ThaumcraftApi.registerObjectTag(ItemStack(livingrockDarkSlabs[2],     1, 0),  AspectList().a(EARTH,     1). a(ENERGY,   1). a(FIRE,      1))
		ThaumcraftApi.registerObjectTag(ItemStack(livingrockDarkSlabsFull[2], 1, 0),  AspectList().a(EARTH,     1). a(ENERGY,   1). a(FIRE,      1))
		ThaumcraftApi.registerObjectTag(ItemStack(niflheimBlock,              1, 1),  AspectList().a(EARTH,     1). a(ENTROPY,  1). a(COLD,      2))
		ThaumcraftApi.registerObjectTag(ItemStack(livingrockDarkStairs[2],    1, 0),  AspectList().a(EARTH,     1). a(ENERGY,   1). a(FIRE,      1))
		ThaumcraftApi.registerObjectTag(ItemStack(grapesWhite,                1, 0),  AspectList().a(PLANT,     2). a(WATER,    1). a(HUNGER,    1))
		ThaumcraftApi.registerObjectTag(ItemStack(rainbowGrass,               1, 2),  AspectList().a(PLANT,     2). a(LIFE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(rainbowTallFlower,          1, 0),  AspectList().a(PLANT,     2). a(LIFE,     1). a(COLOR,     1))
		ThaumcraftApi.registerObjectTag(ItemStack(yggFlower,                  1, 0),  AspectList().a(PLANT,     6). a(MAGIC,    8). a(LIFE,      12))
		ThaumcraftApi.registerObjectTag(ItemStack(barrierBerry,               1, 0),  AspectList().a(PLANT,     1). a(LIFE,     2). a(MAGIC,     4))
		ThaumcraftApi.registerObjectTag(ItemStack(calicoBerry,                1, 0),  AspectList().a(PLANT,     1). a(LIFE,     2). a(MAGIC,     4))
		ThaumcraftApi.registerObjectTag(ItemStack(circuitBerry,               1, 0),  AspectList().a(PLANT,     1). a(LIFE,     2). a(MAGIC,     4))
		ThaumcraftApi.registerObjectTag(ItemStack(lightningBerry,             1, 0),  AspectList().a(PLANT,     1). a(LIFE,     2). a(MAGIC,     4))
		ThaumcraftApi.registerObjectTag(ItemStack(netherBerry,                1, 0),  AspectList().a(PLANT,     1). a(LIFE,     2). a(MAGIC,     4))
		ThaumcraftApi.registerObjectTag(ItemStack(sealingBerry,               1, 0),  AspectList().a(PLANT,     1). a(LIFE,     2). a(MAGIC,     4))
		ThaumcraftApi.registerObjectTag(ItemStack(schemaGenerator,            1, 0),  AspectList().a(TREE,      1). a(PLANT,    2). a(EXCHANGE,  2))
		ThaumcraftApi.registerObjectTag(ItemStack(helheimBlock,               1, 0),  AspectList().a(EARTH,     2). a(ELDRITCH, 2). a(DARKNESS,  2))
		ThaumcraftApi.registerObjectTag(ItemStack(redstoneRelay,              1, 0),  AspectList().a(MAGIC,     1). a(ENERGY,   8). a(MECHANISM, 12))
		ThaumcraftApi.registerObjectTag(ItemStack(secretGlass,                1, 0),  AspectList().a(EARTH,     1). a(CRYSTAL,  1). a(DARKNESS,  2))
		ThaumcraftApi.registerObjectTag(ItemStack(nidhoggTooth,               1, 0),  AspectList().a(BEAST,     2). a(HUNGER,   4). a(ENTROPY,   8))
		ThaumcraftApi.registerObjectTag(ItemStack(rift,                       1, 0),  AspectList().a(ENTROPY,   6). a(ELDRITCH, 6). a(DARKNESS,  6))
		ThaumcraftApi.registerObjectTag(ItemStack(barrier,                    1, 0),  AspectList().a(AIR,       1). a(MAGIC,    2). a(DARKNESS,  2))
		ThaumcraftApi.registerObjectTag(ItemStack(niflheimPortal,             1, 0),  AspectList().a(WATER,     4). a(COLD,     4). a(ELDRITCH,  4). a(TRAVEL, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(icyGeyser,                  1, 0),  AspectList().a(EARTH,     1). a(ENERGY,   2). a(COLD,      2). a(WATER,  3))
		ThaumcraftApi.registerObjectTag(ItemStack(softStorage,                1, 0),  AspectList().a(AURA,      18).a(MAGIC,    96).a(EARTH,     12).a(FIRE,   72))
		ThaumcraftApi.registerObjectTag(ItemStack(niflheimBlock,              1, 2),  AspectList().a(EARTH,     1). a(GREED,    2). a(CRYSTAL,   2). a(COLD,   1))
		ThaumcraftApi.registerObjectTag(ItemStack(rainbowMushroom,            1, 0),  AspectList().a(PLANT,     1). a(DARKNESS, 1). a(EARTH,     1). a(COLOR,  2))
		ThaumcraftApi.registerObjectTag(ItemStack(lootbox,                    1, 0),  AspectList().a(EARTH,     1). a(DARKNESS, 1). a(VOID,      4). a(GREED,  7))
		ThaumcraftApi.registerObjectTag(ItemStack(treeCrafterBlockAU,         1, 0),  AspectList().a(TREE,      1). a(MAGIC,    2). a(MECHANISM, 2). a(CRAFT,  4))
		ThaumcraftApi.registerObjectTag(ItemStack(raceSelector,               1, 0),  AspectList().a(MECHANISM, 1). a(METAL,    4). a(EXCHANGE,  4). a(MAGIC,  8))
		ThaumcraftApi.registerObjectTag(ItemStack(snakeBody,                  1, 4),  AspectList().a(EARTH,     1). a(MOTION,   2). a(FLESH,     3). a(BEAST,  4))
		ThaumcraftApi.registerObjectTag(ItemStack(snakeBody,                  1, 5),  AspectList().a(EARTH,     1). a(MOTION,   2). a(FLESH,     3). a(BEAST,  4))
		ThaumcraftApi.registerObjectTag(ItemStack(snakeBody,                  1, 6),  AspectList().a(EARTH,     1). a(MOTION,   2). a(FLESH,     3). a(BEAST,  4))
		ThaumcraftApi.registerObjectTag(ItemStack(snakeBody,                  1, 7),  AspectList().a(EARTH,     1). a(MOTION,   2). a(FLESH,     3). a(BEAST,  4))
		ThaumcraftApi.registerObjectTag(ItemStack(powerStone,                 1, 1),  AspectList().a(EARTH,     2). a(EXCHANGE, 4). a(ELDRITCH,  8). a(MAGIC,  8). a(WEAPON, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(powerStone,                 1, 2),  AspectList().a(EARTH,     2). a(EXCHANGE, 4). a(ELDRITCH,  8). a(MAGIC,  8). a(ARMOR,  16))
		ThaumcraftApi.registerObjectTag(ItemStack(powerStone,                 1, 3),  AspectList().a(EARTH,     2). a(EXCHANGE, 4). a(ELDRITCH,  8). a(MAGIC,  8). a(MOTION, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(powerStone,                 1, 4),  AspectList().a(EARTH,     2). a(EXCHANGE, 4). a(ELDRITCH,  8). a(MAGIC,  8). a(AURA,   16))
		ThaumcraftApi.registerObjectTag(ItemStack(subspacian,                 1, 0),  AspectList().a(PLANT,     1). a(EARTH,    1). a(TRAP,      4). a(MAGIC,  8). a(TRAVEL, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(snakeBody,                  1, 0),  AspectList().a(EARTH,     1). a(HUNGER,   2). a(MOTION,    2). a(FLESH,  3). a(BEAST,  4))
		ThaumcraftApi.registerObjectTag(ItemStack(snakeBody,                  1, 1),  AspectList().a(EARTH,     1). a(HUNGER,   2). a(MOTION,    2). a(FLESH,  3). a(BEAST,  4))
		ThaumcraftApi.registerObjectTag(ItemStack(snakeBody,                  1, 2),  AspectList().a(EARTH,     1). a(HUNGER,   2). a(MOTION,    2). a(FLESH,  3). a(BEAST,  4))
		ThaumcraftApi.registerObjectTag(ItemStack(snakeBody,                  1, 3),  AspectList().a(EARTH,     1). a(HUNGER,   2). a(MOTION,    2). a(FLESH,  3). a(BEAST,  4))
		
		// TODO rest of resources
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.InterdimensionalGatewayCore.stack, AspectList().a(ELDRITCH, 8).a(TRAVEL, 4).a(LIGHT, 8).a(ORDER, 8).a(MAGIC, 8))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.ManaInfusionCore.stack, AspectList().a(GREED, 4).a(METAL, 6).a(ENERGY, 4).a(MAGIC, 6))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.ElvoriumIngot.stack, AspectList().a(ELDRITCH).a(GREED, 3).a(METAL, 4).a(MAGIC, 8))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.MauftriumIngot.stack, AspectList().a(AURA, 20).a(ELDRITCH, 16).a(GREED, 20).a(METAL, 8).a(MAGIC, 50).a(ENERGY, 16))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.MuspelheimPowerIngot.stack, AspectList().a(AURA, 10).a(ELDRITCH, 8).a(GREED, 10).a(METAL, 4).a(MAGIC, 20).a(FIRE, 24))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.NiflheimPowerIngot.stack, AspectList().a(AURA, 10).a(ELDRITCH, 8).a(GREED, 10).a(METAL, 4).a(MAGIC, 20).a(COLD, 24))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.ElvoriumNugget.stack, AspectList().a(GREED).a(METAL).a(MAGIC, 2))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.MauftriumNugget.stack, AspectList().a(AURA, 2).a(ELDRITCH, 2).a(GREED, 2).a(METAL).a(MAGIC, 5).a(ENERGY, 2))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.MuspelheimEssence.stack, AspectList().a(AURA, 4).a(ELDRITCH, 3).a(GREED, 3).a(MAGIC, 5).a(FIRE, 8))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.NiflheimEssence.stack, AspectList().a(AURA, 4).a(ELDRITCH, 3).a(GREED, 3).a(MAGIC, 5).a(COLD, 8))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.IffesalDust.stack, AspectList().a(AURA).a(ENERGY).a(MAGIC))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.PrimalRune.stack, AspectList().a(AURA, 12).a(GREED, 6).a(MAGIC, 64).a(EARTH, 8))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.MuspelheimRune.stack, AspectList().a(AURA, 3).a(MAGIC, 16).a(EARTH, 2).a(FIRE, 12))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.NiflheimRune.stack, AspectList().a(AURA, 3).a(MAGIC, 16).a(EARTH, 2).a(COLD, 12))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.InfusedDreamwoodTwig.stack, AspectList().a(TREE).a(MAGIC, 2))         
		
		ThaumcraftApi.registerObjectTag(ItemStack(elementalHelmet), AspectList().a(METAL, 15).a(MAGIC, 12).a(ARMOR, 5).a(WATER, 8))
		
		elementalHelmetRevealing?.let {
			ThaumcraftApi.registerObjectTag(ItemStack(it), AspectList().a(METAL, 15).a(MAGIC, 12).a(ARMOR, 5).a(WATER, 8).a(SENSES, 4))
		}
		
		ThaumcraftApi.registerObjectTag(ItemStack(elementalChestplate), AspectList().a(METAL, 24).a(MAGIC, 18).a(ARMOR, 8).a(EARTH, 8))
		ThaumcraftApi.registerObjectTag(ItemStack(elementalLeggings), AspectList().a(METAL, 21).a(MAGIC, 16).a(ARMOR, 7).a(FIRE, 8))
		ThaumcraftApi.registerObjectTag(ItemStack(elementalBoots), AspectList().a(METAL, 12).a(MAGIC, 10).a(ARMOR, 4).a(AIR, 8))
		ThaumcraftApi.registerObjectTag(ItemStack(elvoriumHelmet), AspectList().a(AURA, 12).a(METAL, 35).a(MAGIC, 64).a(ARMOR, 15).a(ENERGY, 16).a(GREED, 30))
		
		elvoriumHelmetRevealing?.let {
			ThaumcraftApi.registerObjectTag(ItemStack(it), AspectList().a(AURA, 12).a(METAL, 35).a(MAGIC, 64).a(ARMOR, 15).a(ENERGY, 16).a(GREED, 30))
		}
		
		ThaumcraftApi.registerObjectTag(ItemStack(elvoriumChestplate), AspectList().a(AURA, 12).a(METAL, 56).a(MAGIC, 64).a(ARMOR, 24).a(ENERGY, 16).a(GREED, 30))
		ThaumcraftApi.registerObjectTag(ItemStack(elvoriumLeggings), AspectList().a(AURA, 12).a(METAL, 49).a(MAGIC, 64).a(ARMOR, 21).a(ENERGY, 16).a(GREED, 30))
		ThaumcraftApi.registerObjectTag(ItemStack(elvoriumBoots), AspectList().a(AURA, 12).a(METAL, 28).a(MAGIC, 64).a(ARMOR, 12).a(ENERGY, 16).a(GREED, 30))
		ThaumcraftApi.registerObjectTag(ItemStack(realitySword), AspectList().a(AURA, 54).a(GREED, 49).a(METAL, 18).a(ENERGY, 36).a(MAGIC, 64).a(WEAPON, 12))
		ThaumcraftApi.registerObjectTag(ItemStack(livingrockPickaxe), AspectList().a(TREE).a(ENTROPY, 2).a(MINE, 2).a(EARTH, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(rodMuspelheim), AspectList().a(AURA, 32).a(MAGIC, 64).a(ENERGY, 24).a(TOOL, 8).a(METAL, 8).a(FIRE, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(rodNiflheim), AspectList().a(AURA, 32).a(MAGIC, 64).a(ENERGY, 24).a(TOOL, 8).a(METAL, 8).a(COLD, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(excaliber), AspectList().a(MOTION, 16).a(ENERGY, 16).a(MAGIC, 16).a(WEAPON, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(mask), AspectList().a(ELDRITCH, 16).a(HUNGER, 16).a(HEAL, 16).a(DARKNESS, 16).a(ARMOR, 16).a(VOID, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(flugelSoul), AspectList().a(AURA, 16).a(ELDRITCH, 16).a(TRAVEL, 16).a(ORDER, 16).a(MAGIC, 16).a(SOUL, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(discFlugel), AspectList().a(AIR, 4).a(GREED, 4).a(SENSES, 4).a(LIGHT, 2).a(AURA, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(elfFirePendant), AspectList().a(MAGIC, 24).a(AURA, 8).a(ARMOR, 6).a(FIRE, 8))
		ThaumcraftApi.registerObjectTag(ItemStack(elfIcePendant), AspectList().a(MAGIC, 24).a(AURA, 8).a(ARMOR, 6).a(COLD, 8))
		ThaumcraftApi.registerObjectTag(ItemStack(pixieAttractor), AspectList().a(MAGIC, 12).a(METAL, 12).a(CRYSTAL, 4).a(HUNGER, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(creativeReachPendant), AspectList().a(AIR, 64).a(WATER, 64).a(FIRE, 64).a(ORDER, 64).a(ENTROPY, 64).a(EARTH, 64))
		ThaumcraftApi.registerObjectTag(ItemStack(manaStone), AspectList().a(AURA, 4).a(ENERGY, 4).a(MAGIC, 10).a(CRYSTAL, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(manaStoneGreater), AspectList().a(AURA, 12).a(ENERGY, 4).a(MAGIC, 20).a(CRYSTAL, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(manaRingElven), AspectList().a(AURA, 4).a(METAL, 12).a(MAGIC, 30).a(CRYSTAL, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(manaRingGod), AspectList().a(AURA, 32).a(METAL, 24).a(ENERGY, 24).a(MAGIC, 64).a(CRYSTAL, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(auraRingElven), AspectList().a(AURA, 4).a(GREED, 4).a(METAL, 12).a(MAGIC, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(auraRingGod), AspectList().a(AURA, 16).a(ELDRITCH, 4).a(GREED, 8).a(METAL, 16).a(ENERGY, 12).a(MAGIC, 40))
		ThaumcraftApi.registerObjectTag(ItemStack(peacePipe), AspectList().a(SOUL, 4).a(TREE, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(paperBreak), AspectList().a(SOUL, 4).a(SENSES, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.flugelHead), AspectList().a(ELDRITCH, 8).a(MAN, 4).a(MOTION, 8).a(SOUL, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(astrolabe), AspectList().a(TOOL, 4).a(TRAVEL, 2).a(METAL, 8).a(MAGIC, 6))
		ThaumcraftApi.registerObjectTag(ItemStack(invisibilityCloak), AspectList().a(CLOTH, 6).a(CRAFT).a(SENSES, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(dodgeRing), AspectList().a(CRYSTAL, 3).a(METAL, 8).a(GREED, 3).a(MOTION, 6))
		ThaumcraftApi.registerObjectTag(ItemStack(lootInterceptor), AspectList().a(MAGIC, 5).a(METAL, 4).a(AURA, 2).a(ENERGY, 2).a(HUNGER, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(rodGrass), AspectList().a(PLANT, 7).a(EARTH, 3).a(HEAL, 2).a(MAGIC, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(cloudPendant), AspectList().a(WEATHER, 2).a(FLIGHT, 4).a(MOTION, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(cloudPendantSuper), AspectList().a(WEATHER, 2).a(FLIGHT, 6).a(MOTION, 8))
	}
}

/**
 * @author WireSegal
 * Created at 4:07 PM on 1/19/16.
 */
object BotaniaTCAspects {
	
	fun addAspects() {
		ThaumcraftApi.registerObjectTag(wildStack(flower), AspectList().a(PLANT, 2).a(LIFE).a(COLOR))
		ThaumcraftApi.registerObjectTag(wildStack(altar), AspectList().a(EARTH, 8).a(CRAFT, 4))
		ThaumcraftApi.registerObjectTag(wildStack(livingrock), AspectList().a(EARTH, 2).a(LIFE))
		ThaumcraftApi.registerObjectTag(wildStack(livingwood), AspectList().a(TREE, 4).a(LIFE))
		ThaumcraftApi.registerObjectTag(wildStack(specialFlower), AspectList().a(PLANT, 2).a(MAGIC, 2).a(LIFE))
		ThaumcraftApi.registerObjectTag(ItemStack(spreader, 1, 0), AspectList().a(MAGIC, 2).a(MOTION, 2)) // Spreader
		ThaumcraftApi.registerObjectTag(ItemStack(spreader, 1, 1), AspectList().a(MAGIC, 2).a(MOTION, 2).a(MECHANISM)) // Pulse
		ThaumcraftApi.registerObjectTag(ItemStack(spreader, 1, 2), AspectList().a(MAGIC, 2).a(MOTION, 2).a(ELDRITCH)) // Dreamwood
		ThaumcraftApi.registerObjectTag(ItemStack(spreader, 1, 3), AspectList().a(MAGIC, 2).a(MOTION, 2).a(ELDRITCH).a(COLOR).a(PRIDE)) // Gaia
		ThaumcraftApi.registerObjectTag(wildStack(pool), AspectList().a(EARTH, 8).a(MAGIC, 2).a(VOID, 2))
		ThaumcraftApi.registerObjectTag(wildStack(runeAltar), AspectList().a(MAGIC, 8).a(CRAFT, 4).a(EARTH, 4))
		ThaumcraftApi.registerObjectTag(wildStack(unstableBlock), AspectList().a(MAGIC, 2).a(ENTROPY, 2).add(COLOR, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(pylon, 1, 0), AspectList().a(MAGIC, 4).a(GREED, 4).a(METAL, 2).a(CRYSTAL, 2)) // Mana Pylon
		ThaumcraftApi.registerObjectTag(ItemStack(pylon, 1, 1), AspectList().a(MAGIC, 8).a(EARTH, 2).a(METAL, 2).a(ELDRITCH, 2).a(CRYSTAL, 2).a(ENVY, 2)) // Natura Pylon
		ThaumcraftApi.registerObjectTag(ItemStack(pylon, 1, 2), AspectList().a(MAGIC, 8).a(METAL, 2).a(ELDRITCH, 2).a(CRYSTAL, 2)) // Gaia Pylon
		ThaumcraftApi.registerObjectTag(wildStack(pistonRelay), AspectList().a(MOTION, 2).a(MECHANISM, 4).a(MAGIC))
		ThaumcraftApi.registerObjectTag(wildStack(distributor), AspectList().a(VOID, 2).a(MOTION, 2).a(MAGIC, 2))
		ThaumcraftApi.registerObjectTag(wildStack(manaBeacon), AspectList().a(MAGIC, 2).a(ENTROPY, 2).a(COLOR, 2))
		ThaumcraftApi.registerObjectTag(wildStack(manaVoid), AspectList().a(VOID, 6).a(MAGIC, 2))
		ThaumcraftApi.registerObjectTag(wildStack(manaDetector), AspectList().a(MECHANISM, 6).a(MAGIC, 2))
		ThaumcraftApi.registerObjectTag(wildStack(enchanter), AspectList().a(MAGIC, 8).a(CRAFT, 4).a(SENSES, 2))
		ThaumcraftApi.registerObjectTag(wildStack(turntable), AspectList().a(MOTION, 2).a(MECHANISM, 4))
		ThaumcraftApi.registerObjectTag(wildStack(tinyPlanet), AspectList().a(EARTH, 64).a(HUNGER, 12).a(MAGIC, 2)) // It's REALLY heavy.
		ThaumcraftApi.registerObjectTag(wildStack(alchemyCatalyst), AspectList().a(MAGIC, 8).a(EXCHANGE, 8))
		ThaumcraftApi.registerObjectTag(wildStack(openCrate), AspectList().a(TREE, 4).a(VOID, 2))
		ThaumcraftApi.registerObjectTag(wildStack(forestEye), AspectList().a(SENSES, 4).a(MECHANISM, 2))
		ThaumcraftApi.registerObjectTag(wildStack(forestDrum), AspectList().add(HARVEST, 12))
		ThaumcraftApi.registerObjectTag(wildStack(shinyFlower), AspectList().a(PLANT, 2).a(LIFE).a(COLOR).a(LIGHT))
		
		val list1 = AspectList().a(TREE, 2).a(SOUL, 2).a(ELDRITCH)
		ThaumcraftApi.registerObjectTag(ItemStack(platform, 1, 0), list1)
		ThaumcraftApi.registerObjectTag(ItemStack(platform, 1, 1), list1)
		// Nothing for the Infrangible Platform.
		
		ThaumcraftApi.registerObjectTag(wildStack(alfPortal), AspectList().a(ELDRITCH, 4).a(MECHANISM, 4).a(TRAVEL, 4))
		ThaumcraftApi.registerObjectTag(wildStack(dreamwood), AspectList().a(TREE, 4).a(ELDRITCH))
		ThaumcraftApi.registerObjectTag(wildStack(conjurationCatalyst), AspectList().a(MAGIC, 8).a(ORDER, 8).a(ELDRITCH))
		ThaumcraftApi.registerObjectTag(wildStack(bifrost), AspectList().a(COLOR, 8))
		ThaumcraftApi.registerObjectTag(wildStack(solidVines), AspectList().a(PLANT))
		ThaumcraftApi.registerObjectTag(wildStack(buriedPetals), AspectList().a(PLANT).a(COLOR))
		ThaumcraftApi.registerObjectTag(wildStack(prismarine), AspectList().a(WATER, 2).a(CRYSTAL, 2))
		ThaumcraftApi.registerObjectTag(wildStack(seaLamp), AspectList().a(WATER, 2).a(CRYSTAL, 2).a(LIGHT, 2))
		ThaumcraftApi.registerObjectTag(wildStack(floatingFlower), AspectList().a(PLANT, 2).a(LIFE).a(LIGHT).a(AIR).a(EARTH))
		ThaumcraftApi.registerObjectTag(wildStack(tinyPotato), AspectList().a(LIFE, 2).a(CROP, 2))
		ThaumcraftApi.registerObjectTag(wildStack(spawnerClaw), AspectList().a(LIFE, 16).a(MAGIC, 8))
		ThaumcraftApi.registerObjectTag(ItemStack(customBrick, 1, 0), AspectList().a(EARTH, 2).a(NETHER)) // Hellish Brick
		ThaumcraftApi.registerObjectTag(ItemStack(customBrick, 1, 1), AspectList().a(EARTH, 2).a(SOUL)) // Soul Brick
		ThaumcraftApi.registerObjectTag(ItemStack(customBrick, 1, 2), AspectList().a(EARTH, 2).a(COLD)) // Frosty Brick
		ThaumcraftApi.registerObjectTag(wildStack(enderEye), AspectList().a(SENSES, 4).a(MECHANISM, 2).a(ENVY, 2))
		ThaumcraftApi.registerObjectTag(wildStack(starfield), AspectList().a(LIGHT, 8).a(METAL, 4))
		ThaumcraftApi.registerObjectTag(wildStack(rfGenerator), AspectList().a(MECHANISM, 16).a(ENERGY, 8).a(SLOTH, 8).a(GLUTTONY, 4)) // If you can't tell, I don't like RF.
		ThaumcraftApi.registerObjectTag(wildStack(elfGlass), AspectList().a(CRYSTAL).a(ELDRITCH))
		ThaumcraftApi.registerObjectTag(wildStack(brewery), AspectList(ItemStack(Items.brewing_stand)).a(MAGIC, 2))
		ThaumcraftApi.registerObjectTag(wildStack(manaGlass), AspectList().a(CRYSTAL).a(MAGIC))
		ThaumcraftApi.registerObjectTag(wildStack(terraPlate), AspectList().a(CRAFT, 4).a(FIRE, 2).a(WATER, 2).a(EARTH, 2).a(AIR, 2).a(MAGIC, 2))
		
		val list2 = AspectList().a(MECHANISM, 4).a(ELDRITCH, 2)
		ThaumcraftApi.registerObjectTag(wildStack(redStringContainer), list2)
		ThaumcraftApi.registerObjectTag(wildStack(redStringDispenser), list2)
		ThaumcraftApi.registerObjectTag(wildStack(redStringFertilizer), list2)
		ThaumcraftApi.registerObjectTag(wildStack(redStringComparator), list2)
		ThaumcraftApi.registerObjectTag(wildStack(redStringRelay), list2)
		
		ThaumcraftApi.registerObjectTag(wildStack(floatingSpecialFlower), AspectList().a(PLANT, 2).a(MAGIC, 2).a(LIFE).a(COLOR).a(LIGHT).a(AIR).a(EARTH)) // So many aspects
		ThaumcraftApi.registerObjectTag(wildStack(manaFlame), AspectList().a(LIGHT, 2).a(COLOR, 2))
		ThaumcraftApi.registerObjectTag(wildStack(prism), AspectList().a(SOUL, 2).a(CRYSTAL, 2))
		ThaumcraftApi.registerObjectTag(wildStack(dirtPath), AspectList(ItemStack(dirt)))
		ThaumcraftApi.registerObjectTag(wildStack(enchantedSoil), AspectList().a(EARTH, 2).a(LIFE, 16).a(MAGIC, 8))
		
		val list3 = AspectList().a(ELDRITCH, 4).a(VOID, 2)
		ThaumcraftApi.registerObjectTag(wildStack(corporeaIndex), list3)
		ThaumcraftApi.registerObjectTag(wildStack(corporeaFunnel), list3)
		ThaumcraftApi.registerObjectTag(wildStack(mushroom), AspectList(ItemStack(Blocks.brown_mushroom)).a(COLOR, 2))
		ThaumcraftApi.registerObjectTag(wildStack(pump), AspectList().a(MECHANISM, 4).a(MOTION, 2).a(MAGIC, 2).a(WATER, 2).a(VOID, 2))
		
		val list4 = AspectList().a(PLANT, 2).a(LIFE).a(COLOR)
		ThaumcraftApi.registerObjectTag(wildStack(doubleFlower1), list4)
		ThaumcraftApi.registerObjectTag(wildStack(doubleFlower2), list4)
		ThaumcraftApi.registerObjectTag(wildStack(corporeaInterceptor), AspectList().a(ELDRITCH, 4).a(VOID, 2))
		ThaumcraftApi.registerObjectTag(wildStack(corporeaCrystalCube), AspectList().a(ELDRITCH, 4).a(VOID, 2).a(SENSES))
		ThaumcraftApi.registerObjectTag(wildStack(incensePlate), AspectList().a(TREE, 4).a(FIRE, 2))
		ThaumcraftApi.registerObjectTag(wildStack(hourglass), AspectList(ItemStack(Items.clock)).a(CRYSTAL).a(MECHANISM))
		ThaumcraftApi.registerObjectTag(wildStack(ghostRail), AspectList(ItemStack(Blocks.rail)).a(SOUL))
		ThaumcraftApi.registerObjectTag(wildStack(sparkChanger), AspectList().a(MECHANISM, 3).a(MAGIC))
		ThaumcraftApi.registerObjectTag(wildStack(root), AspectList().a(TREE, 4).a(LIFE))
		ThaumcraftApi.registerObjectTag(wildStack(felPumpkin), AspectList(ItemStack(Blocks.pumpkin)).a(DARKNESS, 2))
		ThaumcraftApi.registerObjectTag(wildStack(cocoon), AspectList().a(LIFE, 4).a(BEAST, 8))
		ThaumcraftApi.registerObjectTag(wildStack(lightRelay), AspectList().a(TRAVEL, 3).a(MOTION, 2).a(LIGHT, 2))
		ThaumcraftApi.registerObjectTag(wildStack(lightLauncher), AspectList().a(TRAVEL, 3).a(MECHANISM, 2))
		ThaumcraftApi.registerObjectTag(wildStack(manaBomb), AspectList().a(ENTROPY, 16).a(WRATH, 8).a(MAGIC, 4))
		ThaumcraftApi.registerObjectTag(wildStack(cacoblock), AspectList(ItemStack(Blocks.noteblock)).a(GREED, 2).a(AIR, 3))
		ThaumcraftApi.registerObjectTag(wildStack(bellows), AspectList().a(MOTION, 2).a(AIR, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(bifrostPerm), AspectList().a(COLOR, 8))
		ThaumcraftApi.registerObjectTag(wildStack(cellBlock), AspectList().a(PLANT, 4).a(LIFE, 4))
		ThaumcraftApi.registerObjectTag(wildStack(redStringInterceptor), AspectList().a(MECHANISM, 4).a(ELDRITCH, 2))
		ThaumcraftApi.registerObjectTag(wildStack(gaiaHead), AspectList(ItemStack(Items.skull, 1, 3)).a(ELDRITCH, 4).a(EARTH, 4))
		ThaumcraftApi.registerObjectTag(wildStack(corporeaRetainer), AspectList().a(ELDRITCH, 4).a(VOID, 2).a(MIND, 2))
		ThaumcraftApi.registerObjectTag(wildStack(teruTeruBozu), AspectList().a(WEATHER, 2))
		ThaumcraftApi.registerObjectTag(wildStack(shimmerrock), AspectList().a(EARTH, 2).a(COLOR))
		ThaumcraftApi.registerObjectTag(wildStack(shimmerwoodPlanks), AspectList().a(TREE).a(COLOR))
		ThaumcraftApi.registerObjectTag(wildStack(avatar), AspectList().a(MAN, 4).a(MAGIC, 2))
		ThaumcraftApi.registerObjectTag(wildStack(altGrass), AspectList().a(EARTH).a(PLANT).a(COLOR))
		
		val list5 = AspectList(ItemStack(Blocks.quartz_block))
		if (darkQuartz != null)
			ThaumcraftApi.registerObjectTag(wildStack(darkQuartz), list5)
		ThaumcraftApi.registerObjectTag(wildStack(manaQuartz), list5)
		ThaumcraftApi.registerObjectTag(wildStack(blazeQuartz), list5)
		ThaumcraftApi.registerObjectTag(wildStack(lavenderQuartz), list5)
		ThaumcraftApi.registerObjectTag(wildStack(redQuartz), list5)
		ThaumcraftApi.registerObjectTag(wildStack(elfQuartz), list5)
		ThaumcraftApi.registerObjectTag(wildStack(sunnyQuartz), list5)
		
		val list6 = AspectList().a(EARTH, 2)
		ThaumcraftApi.registerObjectTag(wildStack(biomeStoneA), list6)
		ThaumcraftApi.registerObjectTag(wildStack(biomeStoneB), list6)
		ThaumcraftApi.registerObjectTag(wildStack(stone), list6)
		ThaumcraftApi.registerObjectTag(wildStack(pavement), AspectList().a(EARTH, 2).a(COLOR))
		
		/////// ITEMS!
		
		ThaumcraftApi.registerObjectTag(wildStack(lexicon), AspectList(ItemStack(Items.book)).a(PLANT))
		
		val list7 = AspectList().a(PLANT).a(COLOR)
		ThaumcraftApi.registerObjectTag(wildStack(petal), list7)
		ThaumcraftApi.registerObjectTag(wildStack(dye), list7)
		ThaumcraftApi.registerObjectTag(wildStack(twigWand), AspectList(ItemStack(twigWand)).a(TOOL, 3))
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 0), AspectList().a(METAL, 4).a(MAGIC)) // Manasteel
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 1), AspectList().a(ELDRITCH, 4).a(MAGIC, 6).a(TRAVEL, 2)) // Manapearl
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 2), AspectList().a(CRYSTAL, 4).a(GREED, 4).a(MAGIC, 4)) // Manadiamond
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 4), AspectList().a(METAL, 4).a(MAGIC, 2).a(EARTH, 2).a(ELDRITCH, 2).a(GREED, 2)) // Terrasteel
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 5), AspectList().a(ELDRITCH, 16).a(PRIDE, 8).a(LIFE, 4).a(COLOR, 4)) // Gaia Spirit
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 7), AspectList().a(METAL, 4).a(ELDRITCH).a(MAGIC)) // Elementium
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 8), AspectList().a(ELDRITCH, 6).a(MAGIC, 6)) // Pixie Dust
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 9), AspectList().a(CRYSTAL, 4).a(GREED, 4).a(MAGIC, 4).a(ELDRITCH, 2)) // Dragonstone
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 10), AspectList().a(CRYSTAL).a(WATER)) // Prismarine
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 11), AspectList().a(CRAFT)) // Crafting Placeholder
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 12), AspectList().a(ELDRITCH, 2).a(CLOTH, 2)) // Red String
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 15), AspectList().a(CRYSTAL).a(ELDRITCH)) // Ender Air
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 16), AspectList().a(CLOTH).a(MAGIC)) // Mana String
		
		val list8 = AspectList().a(METAL)
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 17), list8) // Manasteel Nugget
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 18), list8) // Terrasteel Nugget
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 19), list8) // Elementium Nugget
		
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 20), AspectList().a(TREE)) // Livingroot
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 21), AspectList().a(EARTH)) // Pebble
		ThaumcraftApi.registerObjectTag(ItemStack(manaResource, 1, 23), AspectList().a(MAGIC, 2)) // Mana Powder
		
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 0), AspectList().a(WATER, 16)) // Rune of Water
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 1), AspectList().a(FIRE, 16)) // Rune of Fire
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 2), AspectList().a(EARTH, 16)) // Rune of Earth
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 3), AspectList().a(AIR, 16)) // Rune of Air
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 4), AspectList().a(LIFE, 16)) // Rune of Spring
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 5), AspectList().a(CROP, 16)) // Rune of Summer
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 6), AspectList().a(HARVEST, 16)) // Rune of Autumn
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 7), AspectList().a(COLD, 16)) // Rune of Winter
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 8), AspectList().a(AURA, 16)) // Rune of Mana
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 9), AspectList().a(LUST, 16)) // Rune of Lust
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 10), AspectList().a(GLUTTONY, 16)) // Rune of Gluttony
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 11), AspectList().a(GREED, 16)) // Rune of Greed
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 12), AspectList().a(SLOTH, 16)) // Rune of Sloth
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 13), AspectList().a(WRATH, 16)) // Rune of Wrath
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 14), AspectList().a(ENVY, 16)) // Rune of Envy
		ThaumcraftApi.registerObjectTag(ItemStack(rune, 1, 15), AspectList().a(PRIDE, 16)) // Rune of Pride
		
		ThaumcraftApi.registerObjectTag(wildStack(manaTablet), AspectList().a(VOID, 2).a(MAGIC, 8))
		ThaumcraftApi.registerObjectTag(wildStack(manaCookie), AspectList().a(HUNGER, 64).a(CROP, 64)) // why did i do this
		ThaumcraftApi.registerObjectTag(ItemStack(grassSeeds, 1, 0), AspectList().a(PLANT).a(EXCHANGE)) // grass
		ThaumcraftApi.registerObjectTag(ItemStack(grassSeeds, 1, 1), AspectList().a(EARTH).a(EXCHANGE)) // podzol
		ThaumcraftApi.registerObjectTag(ItemStack(grassSeeds, 1, 2), AspectList().a(DARKNESS).a(EXCHANGE)) // mycelium
		
		val list9 = AspectList().a(PLANT).a(EXCHANGE).a(COLOR)
		ThaumcraftApi.registerObjectTag(ItemStack(grassSeeds, 1, 3), list9) // dry
		ThaumcraftApi.registerObjectTag(ItemStack(grassSeeds, 1, 4), list9) // golden
		ThaumcraftApi.registerObjectTag(ItemStack(grassSeeds, 1, 5), list9) // vivid
		ThaumcraftApi.registerObjectTag(ItemStack(grassSeeds, 1, 6), list9) // scorched
		ThaumcraftApi.registerObjectTag(ItemStack(grassSeeds, 1, 7), list9) // infused
		ThaumcraftApi.registerObjectTag(ItemStack(grassSeeds, 1, 8), list9) // mutated
		
		ThaumcraftApi.registerObjectTag(wildStack(manaMirror), AspectList().a(VOID, 2).a(MAGIC, 8).a(ELDRITCH, 8))
		ThaumcraftApi.registerObjectTag(wildStack(manasteelHelmRevealing), AspectList(ItemStack(manasteelHelm)).a(SENSES, 4))
		ThaumcraftApi.registerObjectTag(wildStack(terrasteelHelmRevealing), AspectList(ItemStack(terrasteelHelm)).a(SENSES, 4))
		ThaumcraftApi.registerObjectTag(wildStack(tinyPlanetItem), AspectList().a(EARTH, 12).a(HUNGER, 12).a(MAGIC, 2))
		ThaumcraftApi.registerObjectTag(wildStack(manaRing), AspectList().a(VOID, 2).a(MAGIC, 8).a(METAL, 8))
		ThaumcraftApi.registerObjectTag(wildStack(manaRingGreater), AspectList().a(VOID, 4).a(MAGIC, 16).a(METAL, 16))
		ThaumcraftApi.registerObjectTag(wildStack(travelBelt), AspectList().a(CLOTH, 4).a(TRAVEL, 8))
		ThaumcraftApi.registerObjectTag(wildStack(quartz), AspectList(ItemStack(Items.quartz)))
		ThaumcraftApi.registerObjectTag(wildStack(elementiumHelmRevealing), AspectList(ItemStack(elementiumHelm)).a(SENSES, 4))
		ThaumcraftApi.registerObjectTag(wildStack(openBucket), AspectList().a(METAL, 4).a(VOID, 8))
		ThaumcraftApi.registerObjectTag(wildStack(spawnerMover), AspectList().a(LIFE, 16).a(MAGIC, 8).a(TRAVEL, 4))
		ThaumcraftApi.registerObjectTag(wildStack(manaBottle), AspectList().a(CRYSTAL).a(ENTROPY, 8).a(MAGIC, 4))
		ThaumcraftApi.registerObjectTag(wildStack(itemFinder), AspectList().a(METAL, 15).a(GREED, 3).a(SENSES, 3))
		ThaumcraftApi.registerObjectTag(wildStack(manaInkwell), AspectList().a(FLIGHT).a(MAGIC))
		ThaumcraftApi.registerObjectTag(ItemStack(vial, 1, 0), AspectList().a(CRYSTAL).a(MAGIC))
		ThaumcraftApi.registerObjectTag(ItemStack(vial, 1, 1), AspectList().a(CRYSTAL).a(ELDRITCH))
		ThaumcraftApi.registerObjectTag(wildStack(brewVial), AspectList().a(CRYSTAL).a(MAGIC, 8))
		ThaumcraftApi.registerObjectTag(wildStack(brewFlask), AspectList().a(CRYSTAL).a(MAGIC, 8))
		ThaumcraftApi.registerObjectTag(wildStack(craftingHalo), AspectList().a(CRAFT, 4).a(ELDRITCH, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(blackLotus, 1, 0), AspectList().a(MAGIC, 16).a(ELDRITCH, 4).a(ORDER, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(blackLotus, 1, 1), AspectList().a(MAGIC, 20).a(ELDRITCH, 8).a(ORDER, 6))
		ThaumcraftApi.registerObjectTag(wildStack(monocle), AspectList().a(SENSES, 8).a(CRYSTAL, 4))
		ThaumcraftApi.registerObjectTag(wildStack(worldSeed), AspectList().a(TRAVEL, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(thornChakram, 1, 0), AspectList().a(PLANT, 8).a(METAL, 4).a(WEAPON, 4).a(WRATH, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(thornChakram, 1, 1), AspectList().a(PLANT, 8).a(METAL, 4).a(WEAPON, 4).a(WRATH, 4).a(FIRE, 2))
		ThaumcraftApi.registerObjectTag(wildStack(overgrowthSeed), AspectList().a(LIFE, 32).a(MAGIC, 16))
		ThaumcraftApi.registerObjectTag(wildStack(ancientWill), AspectList().a(ELDRITCH, 64).a(MIND, 32).a(SOUL, 32))
		ThaumcraftApi.registerObjectTag(wildStack(phantomInk), AspectList().a(VOID))
		ThaumcraftApi.registerObjectTag(wildStack(pinkinator), AspectList().a(COLOR, 32).a(LIFE, 16).a(HEAL, 16))
		ThaumcraftApi.registerObjectTag(wildStack(blackHoleTalisman), AspectList().a(VOID, 64))
		
		val list10 = AspectList().a(SENSES, 4).a(AIR, 4).a(GREED, 4).a(COLOR, 2).a(PRIDE, 4)
		ThaumcraftApi.registerObjectTag(wildStack(recordGaia1), list10)
		ThaumcraftApi.registerObjectTag(wildStack(recordGaia2), list10)
		
		ThaumcraftApi.registerObjectTag(wildStack(terraAxe), AspectList(ItemStack(terraAxe)).a(TOOL, 3))
		ThaumcraftApi.registerObjectTag(wildStack(waterBowl), AspectList().a(TREE).a(WATER))
		ThaumcraftApi.registerObjectTag(wildStack(starSword), AspectList().a(ELDRITCH, 16).a(WEAPON, 4))
//		ThaumcraftApi.registerObjectTag(wildStack(exchangeRod), list) // TODO where?
		ThaumcraftApi.registerObjectTag(wildStack(thunderSword), AspectList().a(WEATHER, 16).a(WEAPON, 4))
		ThaumcraftApi.registerObjectTag(wildStack(gaiaHeadItem), AspectList(ItemStack(gaiaHead)))
		
		/// ENTITIES!
		ThaumcraftApi.registerEntityTag(LibEntityNames.MANA_BURST, AspectList().a(MAGIC, 3).a(AURA, 3))
		ThaumcraftApi.registerEntityTag(LibEntityNames.SIGNAL_FLARE, AspectList().a(MAGIC, 3).a(LIGHT, 3).a(COLOR, 3))
		ThaumcraftApi.registerEntityTag(LibEntityNames.PIXIE, AspectList().a(LIGHT, 2).a(FLIGHT, 2).a(ELDRITCH))
		ThaumcraftApi.registerEntityTag(LibEntityNames.FLAME_RING, AspectList().a(FIRE, 8))
		ThaumcraftApi.registerEntityTag(LibEntityNames.VINE_BALL, AspectList().a(PLANT, 7))
		ThaumcraftApi.registerEntityTag(LibEntityNames.DOPPLEGANGER, AspectList().a(MAN, 16).a(EARTH, 8).a(ELDRITCH, 8).a(DARKNESS, 4))
		ThaumcraftApi.registerEntityTag(LibEntityNames.MAGIC_LANDMINE, AspectList().a(MAGIC, 2).a(TRAP, 2).a(POISON, 2))
		ThaumcraftApi.registerEntityTag(LibEntityNames.SPARK, AspectList(ItemStack(spark)))
		ThaumcraftApi.registerEntityTag(LibEntityNames.THROWN_ITEM, AspectList().a(MOTION, 4))
		ThaumcraftApi.registerEntityTag(LibEntityNames.MAGIC_MISSILE, AspectList().a(WEAPON, 4).a(ELDRITCH, 2))
		ThaumcraftApi.registerEntityTag(LibEntityNames.THORN_CHAKRAM, AspectList(ItemStack(thornChakram)))
		ThaumcraftApi.registerEntityTag(LibEntityNames.CORPOREA_SPARK, AspectList(ItemStack(corporeaSpark)))
		ThaumcraftApi.registerEntityTag(LibEntityNames.ENDER_AIR_BOTTLE, AspectList(ItemStack(manaResource, 1, 15)))
		ThaumcraftApi.registerEntityTag(LibEntityNames.POOL_MINECART, AspectList(ItemStack(poolMinecart)))
		ThaumcraftApi.registerEntityTag(LibEntityNames.PINK_WITHER, AspectList().a(UNDEAD, 20).a(ORDER, 20).a(FIRE, 15).a(HEAL, 15))
		ThaumcraftApi.registerEntityTag(LibEntityNames.PLAYER_MOVER, AspectList().a(MOTION, 4).a(TRAVEL, 4))
		ThaumcraftApi.registerEntityTag(LibEntityNames.MANA_STORM, AspectList().a(ENTROPY, 32).a(WRATH, 16))
		ThaumcraftApi.registerEntityTag(LibEntityNames.BABYLON_WEAPON, AspectList().a(ELDRITCH, 16).a(WEAPON, 16))
		ThaumcraftApi.registerEntityTag(LibEntityNames.FALLING_STAR, AspectList().a(ELDRITCH, 8))
	}
}

object TCHandlerShadowFoxAspects {
	
	class RainbowAspect(name: String, components: Array<Aspect>, texture: ResourceLocation, blend: Int): Aspect(name, 0xFFFFFF, components, texture, blend) {
		override fun getColor(): Int = ItemIridescent.rainbowColor()
	}
	
	lateinit var COLOR: Aspect
	
	fun initAspects() {
		COLOR = if (AlfheimConfigHandler.addTincturaAspect)
			RainbowAspect("tinctura", arrayOf(LIGHT, ORDER), ResourceLocationIL(ModInfo.MODID, "textures/misc/tinctura.png"), GL11.GL_ONE_MINUS_SRC_ALPHA)
		else
			SENSES
	}
	
	fun replaceAspect(stack: ItemStack, a1: Aspect, a2: Aspect) {
		val list = AspectList(stack)
		val amount = list.getAmount(a1)
		list.remove(a1)
		list.add(a2, amount)
		ThaumcraftApi.registerObjectTag(stack, list)
	}
	
	fun replaceAspect(key: String, a1: Aspect, a2: Aspect) {
		val list = AspectList(OreDictionary.getOres(key)[0])
		val amount = list.getAmount(a1)
		list.remove(a1)
		list.add(a2, amount)
		ThaumcraftApi.registerObjectTag(key, list)
	}
	
	fun addAspects() {
		if (AlfheimConfigHandler.addAspectsToBotania)
			BotaniaTCAspects.addAspects()
		if (AlfheimConfigHandler.addTincturaAspect)
			overrideVanillaAspects()
		
		val splinterlist = AspectList().a(TREE).a(ENTROPY)
		
		val list1 = AspectList().a(EARTH, 2).a(COLOR)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.irisDirt), list1)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.rainbowDirt), list1)
		
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.irisSapling), AspectList().a(PLANT, 2).a(TREE).a(COLOR))
		
		val list2 = AspectList().a(PLANT).a(AIR).a(COLOR)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.irisGrass), list2)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.rainbowGrass), list2)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.irisTallGrass0), list2)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.irisTallGrass1), list2)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.rainbowTallGrass), list2)
		
		val list3 = AspectList().a(TREE, 4).a(COLOR)
		repeat(4) { ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.irisWood0, 1, it), list3) }
		repeat(4) { ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.irisWood1, 1, it), list3) }
		repeat(4) { ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.irisWood2, 1, it), list3) }
		repeat(4) { ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.irisWood3, 1, it), list3) }
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.rainbowWood), list3)
		repeat(4) { ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.altWood0, 1, it), list3) }
		repeat(2) { ThaumcraftApi.registerObjectTag(ItemStack(altWood1, 1, it), list3) }
		
		repeat (16) { ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.irisSlabsFull[it]), AspectList().a(TREE)) }
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.rainbowSlabFull), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.auroraSlabFull), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.barrierSlabFull), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.calicoSlabFull), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.circuitSlabFull), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.lightningSlabFull), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.netherSlabFull), AspectList().a(TREE))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.sealingSlabFull), AspectList().a(TREE))
		
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.itemDisplay), AspectList().a(TREE).a(METAL))
		
		val list4 = AspectList().a(TREE, 8).a(MAGIC, 8).a(COLOR, 2).a(CRAFT, 4)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.treeCrafterBlock), list4)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.treeCrafterBlockRB), list4)
		
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.lightningSapling), AspectList().a(PLANT, 2).a(TREE).a(WEATHER))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.lightningWood), AspectList().a(TREE, 4).a(WEATHER))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.livingwoodFunnel), AspectList().a(TREE, 20).a(MECHANISM).a(EXCHANGE).a(VOID, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.netherSapling), AspectList().a(PLANT, 2).a(TREE).a(NETHER))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.netherWood), AspectList().a(TREE, 4).a(NETHER))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.kindling), AspectList().a(CLOTH, 4).a(FIRE, 2).a(MAGIC, 2))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.irisSeeds), AspectList().a(PLANT).a(EXCHANGE).a(COLOR))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.rodColorfulSkyDirt), AspectList().a(TOOL, 8).a(EARTH, 4).a(AIR, 2).a(COLOR, 2).a(MAGIC, 4))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.rodPrismatic), AspectList().a(TOOL, 8).a(LIGHT, 6).a(COLOR, 2).a(MAGIC, 4))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.rodLightning), AspectList().a(TOOL, 8).a(WEATHER, 8).a(MAGIC, 4))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.rodInterdiction), AspectList().a(TOOL, 8).a(AIR, 4).a(WEATHER, 2).a(PRIDE, 2).a(MAGIC, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.priestEmblem, 1, 0), AspectList().a(ELDRITCH, 5).a(WEATHER, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.priestEmblem, 1, 1), AspectList().a(ELDRITCH, 5).a(EARTH, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.priestEmblem, 1, 2), AspectList().a(ELDRITCH, 5).a(PRIDE, 16))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.priestEmblem, 1, 3), AspectList().a(ELDRITCH, 5).a(NETHER, 16))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.coatOfArms), AspectList().a(CLOTH, 4).a(COLOR, 2))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.colorOverride), AspectList().a(COLOR, 8).a(METAL, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.attributionBauble, 1, 0), AspectList().a(CLOTH, 2).a(GREED, 2).a(ELDRITCH, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.attributionBauble, 1, 1), AspectList().a(CLOTH, 2).a(GREED, 2).a(ELDRITCH, 2).a(CROP, 64).a(BEAST, 64)) // memes
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.wiltedLotus, 1, 0), AspectList().a(MAGIC, 16).a(TAINT, 4).a(ENTROPY, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.wiltedLotus, 1, 1), AspectList().a(MAGIC, 20).a(DEATH, 4).a(ELDRITCH, 4).a(TAINT, 4).a(ENTROPY, 2))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.ThunderwoodTwig.stack, AspectList().a(TOOL, 2).a(TREE, 2).a(WEATHER, 2))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.ThunderwoodSplinters.stack, splinterlist)
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.NetherwoodTwig.stack, AspectList().a(TOOL, 2).a(TREE, 2).a(NETHER, 2))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.NetherwoodSplinters.stack, splinterlist)
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.NetherwoodCoal.stack, AspectList().a(FIRE, 4).a(ENERGY, 2).a(NETHER, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.toolbelt), AspectList().a(TOOL, 3).a(VOID, 12).a(CLOTH, 4).a(GREED, 2).a(SLOTH, 2))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.rainbowFlame), AspectList(ItemStack(manaFlame)))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimItems.invisibleFlameLens), AspectList(ItemStack(lens, 1, 17)).a(VOID))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.irisLamp), AspectList().a(LIGHT, 4).a(MECHANISM, 2).a(COLOR, 4))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.sealingWood), AspectList().a(TREE, 4).a(VOID))
		ThaumcraftApi.registerObjectTag(ItemStack(AlfheimBlocks.sealingSapling), AspectList().a(PLANT, 2).a(TREE).a(VOID))
		
		val list5 = AspectList().a(ELDRITCH, 2).a(LIGHT, 2).a(COLOR, 2)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.starPlacer), list5)
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.starBlock), list5)
		
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.rodFlameStar), AspectList().a(TOOL, 8).a(FIRE, 4).a(NETHER, 4))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.wireAxe), AspectList().a(ELDRITCH, 3).a(METAL, 9).a(MAGIC, 4).a(WEAPON, 10).a(ENTROPY, 5))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimItems.trisDagger), AspectList().a(ELDRITCH, 3).a(METAL, 8).a(MAGIC, 4).a(ARMOR, 10).a(WEAPON).a(ORDER, 5))
		ThaumcraftApi.registerObjectTag(wildStack(AlfheimBlocks.shimmerQuartz), AspectList(ItemStack(Blocks.quartz_block)))
		ThaumcraftApi.registerObjectTag(ElvenResourcesMetas.RainbowQuartz.stack, AspectList(ItemStack(Items.quartz)))
		ThaumcraftApi.registerObjectTag(LibOreDict.DYES(LibOreDict.Color.Rainbow), AspectList().a(COLOR))
		ThaumcraftApi.registerEntityTag("${ModInfo.MODID}:voidCreeper", AspectList().a(TAINT, 2).a(ENTROPY, 2).a(PLANT, 2).a(WRATH, 2))
		ThaumcraftApi.registerEntityTag("${ModInfo.MODID}:grieferCreeper", AspectList().a(ENTROPY, 16).a(PLANT, 2).a(WRATH, 8)) // don't fool around with these guys they will mess. you. up.
	}
	
	fun overrideVanillaAspects() {
		replaceAspect(wildStack(Blocks.stained_hardened_clay), SENSES, COLOR)
		replaceAspect(wildStack(Blocks.red_flower), SENSES, COLOR)
		replaceAspect(wildStack(Blocks.yellow_flower), SENSES, COLOR)
		
		for (i in LibOreDict.DYES)
			replaceAspect(i, SENSES, COLOR)
		
		replaceAspect("oreLapis", SENSES, COLOR)
	}
}
