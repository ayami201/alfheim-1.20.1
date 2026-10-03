package alfheim.common.block

// PORT: импорты 1.20.1 — первыми; импорты автора закомментированы до КТ, в которых появятся их блоки. Блок 1.7.10 —
// alfheim.port.legacy, базовые классы декора Botania 1.7.10 — alfheim.port.legacy.botania (MAPPING.md)
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.*
import net.minecraft.world.level.block.Block
import vazkii.botania.client.lib.ResourcesLib as LibResources
import vazkii.botania.common.block.BotaniaBlocks
import alexsocol.asjlib.extendables.block.*
import alfheim.api.ModInfo
//import alfheim.client.core.helper.IconHelper
import alfheim.common.block.AlfheimBlocks.setHarvestLevelI
import alfheim.common.block.alt.BlockYggDecor
import alfheim.common.block.base.BlockStairsMod
//import alfheim.common.core.handler.WorkInProgressItemsHandler.WIP
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemBlockLeavesMod
//import cpw.mods.fml.common.registry.GameRegistry
//import net.minecraft.block.Block
//import net.minecraft.block.material.Material
//import net.minecraft.client.renderer.texture.IIconRegister
//import net.minecraft.entity.player.EntityPlayer
//import net.minecraft.item.ItemStack
//import net.minecraft.world.*
//import net.minecraftforge.common.util.ForgeDirection
//import vazkii.botania.client.lib.LibResources
//import vazkii.botania.common.block.*
//import vazkii.botania.common.block.decor.slabs.BlockModSlab
//import vazkii.botania.common.block.decor.stairs.BlockModStairs
//import vazkii.botania.common.block.decor.walls.BlockModWall

// PORT: строки с «// PORT: КТ-n» в конце — блоки, которые переносит КТ-n; пока они закомментированы.
// Блок с вариантами metadata — массив блоков, номер варианта — индекс (SPEC, Р-5)
object AlfheimFluffBlocks {
	
	val dreamwoodFence: Block
	val dreamwoodFenceGate: Block
	val dreamwoodBarkFence: Block
	val dreamwoodBarkFenceGate: Block
	val dwarfLantern: Block
	val dwarfPlanks: Block
	val dwarfPlanksStairs: Block
	val dwarfPlanksSlab: Block
	val dwarfPlanksSlabFull: Block
	val dwarfTrapDoor: Block
	val elfQuartzWall: Block
	val elvenSandstone: Array<Block>
	val elvenSandstoneStairs: List<Block>
	val elvenSandstoneSlab: Block
	val elvenSandstoneSlabFull: Block
	val elvenSandstoneSlab2: Block
	val elvenSandstoneSlab2Full: Block
	val elvenSandstoneWalls: List<Block>
	val livingcobbleStairs: Block
	val livingcobbleStairs1: Block
	val livingcobbleStairs2: Block
	val livingcobbleSlab: Block
	val livingcobbleSlabFull: Block
	val livingcobbleSlab1: Block
	val livingcobbleSlabFull1: Block
	val livingcobbleSlab2: Block
	val livingcobbleSlabFull2: Block
	val livingcobbleWall: Block
	val livingMountain: Block
	val livingMountainSlab: Block
	val livingMountainSlabFull: Block
	val livingrockBrickWall: Block
	val livingrockDark: Array<Block>
	val livingrockDarkStairs: List<Block>
	val livingrockDarkSlabs: List<Block>
	val livingrockDarkSlabsFull: List<Block>
	val livingrockDarkWalls: List<Block>
	val livingwoodFence: Block
	val livingwoodFenceGate: Block
	val livingwoodBarkFence: Block
	val livingwoodBarkFenceGate: Block
	val roofTile: Array<Block>
	val roofTileSlabs: List<Block>
	val roofTileSlabsFull: List<Block>
	val roofTileStairs: List<Block>
	val shrineLight: Array<Block>
	val shrineGlass: Array<Block>
	val shrinePanel: Array<Block>
	val shrinePillar: Block
	val shrineRock: Array<Block>
	val shrineRockWhiteSlab: Block
	val shrineRockWhiteSlabFull: Block
	val shrineRockWhiteStairs: Block
	val yggDecor: Array<Block>
	
//	val chair: Block // PORT: КТ-3
//	val table: Block // PORT: КТ-3
//	val doubleBlock: Block // PORT: КТ-3
//	val secretGlass: Block // PORT: КТ-3
//	val curtainPlacer: Block // PORT: КТ-3
//	val floodLight: Block // PORT: КТ-3
//	val composite: Block // PORT: КТ-3
	
	init {
//		chair = BlockChair() // PORT: КТ-3
//		table = BlockTable() // PORT: КТ-3
//		doubleBlock = BlockDoubleBlock() // PORT: КТ-3
//		secretGlass = BlockSecretGlass() // PORT: КТ-3
//		curtainPlacer = BlockCurtainPlacer() // PORT: КТ-3
//		floodLight = BlockFloodLight().WIP() // PORT: КТ-3
//		composite = BlockComposite() // PORT: КТ-3
		
		yggDecor = Array(3) { BlockYggDecor(it) }
		shrineRock = Array(16) { BlockModMeta(Material.rock, 16, ModInfo.MODID, "ShrineRock", AlfheimTab, 10f, harvLvl = 2, resist = 10000f, folder = "decor/", meta = it) }
		shrinePillar = BlockShrinePillar()
		shrineRockWhiteStairs = object: BlockStairsMod(shrineRock[0], 0, "ShrineRockWhiteStairs") {
			override fun register() {
				GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
			}
			
			// PORT: КТ-9 — лексикон
//			override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = null
		}
		shrineRockWhiteSlab = BlockRockShrineWhiteSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		shrineRockWhiteSlabFull = shrineRockWhiteSlab // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		shrineRockWhiteSlabFull = BlockRockShrineWhiteSlab(true).setCreativeTab(null).setHardness(1.5f)
		(shrineRockWhiteSlab as BlockModSlab).register()
//		(shrineRockWhiteSlabFull as BlockModSlab).register()
		
		val roofs = 3
		roofTile = Array(3) { BlockModMeta(Material.rock, 3, ModInfo.MODID, "CustomRoof", AlfheimTab, 2f, resist = 5f, folder = "decor/", meta = it).setStepSound(Block1710.soundTypeStone) }
		roofTileSlabs = (0 until roofs).map { BlockRoofTileSlab(false, it).setCreativeTab(AlfheimTab) }
		roofTileSlabsFull = roofTileSlabs // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		roofTileSlabsFull = (0 until roofs).map { BlockRoofTileSlab(true, it).setCreativeTab(AlfheimTab) }
		roofTileSlabs.forEach { (it as BlockModSlab).register() }
//		roofTileSlabsFull.forEach { (it as BlockModSlab).register() }
		roofTileStairs = (0 until roofs).map { BlockModStairs(roofTile[it], it, "CustomRoofStairs$it").setCreativeTab(AlfheimTab) }
		
		livingMountain = BlockLivingMountain()
		livingMountainSlab = BlockLivingMountainSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		livingMountainSlabFull = livingMountainSlab // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		livingMountainSlabFull = BlockLivingMountainSlab(true).setCreativeTab(null).setHardness(1.5f)
		(livingMountainSlab as BlockModSlab).register()
//		(livingMountainSlabFull as BlockModSlab).register()
		
		val metas = (0..3) - 2
		livingrockDark = Array(4) { BlockModMeta(Material.rock, 4, ModInfo.MODID, "DarkLivingRock", AlfheimTab, 2f, resist = 10f, folder = "decor/", meta = it) }
		livingrockDarkStairs = metas.map { BlockModStairs(livingrockDark[it], it, "DarkLivingRockStairs$it").setCreativeTab(AlfheimTab) }
		
		livingrockDarkSlabs = metas.map { BlockLivingrockDarkSlab(false, it).setCreativeTab(AlfheimTab) }
		livingrockDarkSlabsFull = livingrockDarkSlabs // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		livingrockDarkSlabsFull = metas.map { BlockLivingrockDarkSlab(true, it).setCreativeTab(AlfheimTab) }
		livingrockDarkSlabs.forEach { (it as BlockModSlab).register() }
//		livingrockDarkSlabsFull.forEach { (it as BlockModSlab).register() }
		
		livingrockDarkWalls = (0..1).map {
			BlockModWall(livingrockDark[it], it)
				.setCreativeTab(AlfheimTab)
				.setHarvestLevelI("pickaxe", 2)
		}
		
		dwarfLantern = BlockDwarfLantern()
		
		shrineLight = Array(6) { BlockModMeta(Material.glass, 6, ModInfo.MODID, "ShrineLight", AlfheimTab, resist = 6000f, folder = "decor/", meta = it).setLightLevel(1f).setLightOpacity(0) }
		shrineGlass = Array(5) { BlockShrineGlass(it) }
		shrinePanel = Array(4) { object: BlockPaneMeta(Material.glass, 4, "ShrinePanel", "decor/", it) {
			override fun getRenderBlockPass() = 1
			// PORT: IronBarsBlock.attachsTo 1.20.1 нельзя переопределить; к стеклу святилища панель тянется и так — у него твёрдые грани
//			override fun canPaneConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, dir: ForgeDirection) = super.canPaneConnectTo(world, x, y, z, dir) || world.getBlock(x, y, z) == shrineGlass
		}.setBlockName("ShrinePanel")
			.setCreativeTab(AlfheimTab)
			.setLightOpacity(0)
			.setHardness(1f)
			.setHarvestLevelI("pickaxe", 1)
			.setResistance(600f)
			.setStepSound(Block1710.soundTypeGlass) }
		
		elfQuartzWall = BlockModWall(BotaniaBlocks.elfQuartz, 0)
			.setCreativeTab(AlfheimTab)
			.setHarvestLevelI("pickaxe", 2)
		
		dwarfPlanks = BlockModMeta(Material.wood, 1, ModInfo.MODID, "DwarfPlanks", AlfheimTab, 3f, "axe", 1, 100f, "decor/")
		dwarfPlanksStairs = BlockModStairs(dwarfPlanks, 0, "DwarfPlanksStairs").setCreativeTab(AlfheimTab)
		dwarfPlanksSlab = BlockDwarfPlanksSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		dwarfPlanksSlabFull = dwarfPlanksSlab // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		dwarfPlanksSlabFull = BlockDwarfPlanksSlab(true).setCreativeTab(null).setHardness(1.5f)
		(dwarfPlanksSlab as BlockModSlab).register()
//		(dwarfPlanksSlabFull as BlockModSlab).register()
		
		elvenSandstone = Array(5) { BlockElvenSandstone(it) }
		elvenSandstoneStairs = arrayOf(0, 2).map {
			BlockModStairs(elvenSandstone[it], it, "ElvenSandstoneStairs$it")
				.setCreativeTab(AlfheimTab)
		}
		
		elvenSandstoneSlab = BlockElvenSandstoneSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		elvenSandstoneSlabFull = elvenSandstoneSlab // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		elvenSandstoneSlabFull = BlockElvenSandstoneSlab(true).setCreativeTab(null).setHardness(1.5f)
		(elvenSandstoneSlab as BlockModSlab).register()
//		(elvenSandstoneSlabFull as BlockModSlab).register()
		
		elvenSandstoneSlab2 = BlockElvenSandstoneSlab2(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		elvenSandstoneSlab2Full = elvenSandstoneSlab2 // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		elvenSandstoneSlab2Full = BlockElvenSandstoneSlab2(true).setCreativeTab(null).setHardness(1.5f)
		(elvenSandstoneSlab2 as BlockModSlab).register()
//		(elvenSandstoneSlab2Full as BlockModSlab).register()
		
		elvenSandstoneWalls = arrayOf(0, 2).map {
			BlockModWall(elvenSandstone[it], it)
				.setCreativeTab(AlfheimTab)
		}
		
		livingcobbleStairs = BlockModStairs(AlfheimBlocks.livingcobble[0], 0, "LivingCobbleStairs").setCreativeTab(AlfheimTab)
		livingcobbleStairs1 = BlockModStairs(AlfheimBlocks.livingcobble[1], 1, "LivingCobbleStairs1").setCreativeTab(AlfheimTab)
		livingcobbleStairs2 = BlockModStairs(AlfheimBlocks.livingcobble[2], 2, "LivingCobbleStairs2").setCreativeTab(AlfheimTab)
		
		livingcobbleSlab = BlockLivingCobbleSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		livingcobbleSlabFull = livingcobbleSlab // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		livingcobbleSlabFull = BlockLivingCobbleSlab(true).setCreativeTab(null).setHardness(1.5f)
		(livingcobbleSlab as BlockModSlab).register()
//		(livingcobbleSlabFull as BlockModSlab).register()
		
		livingcobbleSlab1 = BlockLivingCobbleSlab1(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		livingcobbleSlabFull1 = livingcobbleSlab1 // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		livingcobbleSlabFull1 = BlockLivingCobbleSlab1(true).setCreativeTab(null).setHardness(1.5f)
		(livingcobbleSlab1 as BlockModSlab).register()
//		(livingcobbleSlabFull1 as BlockModSlab).register()
		
		livingcobbleSlab2 = BlockLivingCobbleSlab2(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		livingcobbleSlabFull2 = livingcobbleSlab2 // PORT: двойная плита 1.7.10 — состояние type=double той же плиты (SlabBlock 1.20.1); её старое имя регистрирует register() одинарной
//		livingcobbleSlabFull2 = BlockLivingCobbleSlab2(true).setCreativeTab(null).setHardness(1.5f)
		(livingcobbleSlab2 as BlockModSlab).register()
//		(livingcobbleSlabFull2 as BlockModSlab).register()
		
		livingcobbleWall = BlockModWall(AlfheimBlocks.livingcobble[0], 0)
			.setCreativeTab(AlfheimTab)
			.setHarvestLevelI("pickaxe", 2)
		
		// PORT: ModBlocks.livingrock, 1 (кирпичи живого камня) → BotaniaBlocks.livingrockBrick (MAPPING.md, «Botania»)
		livingrockBrickWall = BlockModWall(BotaniaBlocks.livingrockBrick, 1)
			.setCreativeTab(AlfheimTab)
			.setHarvestLevelI("pickaxe", 2)
		
		// PORT: ModBlocks.livingwood, ModBlocks.dreamwood: вариант 0 (кора) → BotaniaBlocks.livingwood, BotaniaBlocks.dreamwood,
		// вариант 1 (доски) → …Planks (MAPPING.md, «Botania»)
		livingwoodBarkFenceGate = BlockModFenceGate(BotaniaBlocks.livingwood, 0)
			.setCreativeTab(AlfheimTab)
			.setBlockName("LivingwoodBarkFenceGate")
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block1710.soundTypeWood)
		
		livingwoodBarkFence = BlockModFence(LibResources.PREFIX_MOD + "livingwood0", Material.wood, livingwoodBarkFenceGate)
			.setBlockName("LivingwoodBarkFence")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block1710.soundTypeWood)
		
		dreamwoodBarkFenceGate = BlockModFenceGate(BotaniaBlocks.dreamwood, 0)
			.setBlockName("DreamwoodBarkFenceGate")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block1710.soundTypeWood)
		
		dreamwoodBarkFence = BlockModFence(LibResources.PREFIX_MOD + "dreamwood0", Material.wood, dreamwoodBarkFenceGate)
			.setBlockName("DreamwoodBarkFence")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block1710.soundTypeWood)
		
		livingwoodFenceGate = BlockModFenceGate(BotaniaBlocks.livingwoodPlanks, 1)
			.setBlockName("LivingwoodFenceGate")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block1710.soundTypeWood)
		
		livingwoodFence = BlockModFence(LibResources.PREFIX_MOD + "livingwood1", Material.wood, livingwoodFenceGate)
			.setBlockName("LivingwoodFence")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block1710.soundTypeWood)
		
		dreamwoodFenceGate = BlockModFenceGate(BotaniaBlocks.dreamwoodPlanks, 1)
			.setBlockName("DreamwoodFenceGate")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block1710.soundTypeWood)
		
		dreamwoodFence = BlockModFence(LibResources.PREFIX_MOD + "dreamwood1", Material.wood, dreamwoodFenceGate)
			.setBlockName("DreamwoodFence")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block1710.soundTypeWood)
		
		dwarfTrapDoor = object: BlockModTrapDoor(Material.wood, "DwarfTrapDoor") {
			// PORT: иконка → модель люка (alfheim.port.data.AlfheimBlockStates): текстура — decor/DwarfTrapDoor
//			override fun registerBlockIcons(reg: IIconRegister) {
//				blockIcon = IconHelper.forBlock(reg, this, "", "decor")
//			}
		}
			.setHardness(3f)
			.setStepSound(Block1710.soundTypeWood)
	}
}