package alfheim.common.block

// PORT: импорты 1.20.1 — первыми; импорты автора закомментированы до КТ, в которых появятся их блоки
import net.minecraft.world.level.block.Block
//import alexsocol.asjlib.extendables.block.*
//import alfheim.api.ModInfo
//import alfheim.client.core.helper.IconHelper
//import alfheim.common.block.AlfheimBlocks.setHarvestLevelI
//import alfheim.common.block.alt.BlockYggDecor
//import alfheim.common.block.base.BlockStairsMod
//import alfheim.common.core.handler.WorkInProgressItemsHandler.WIP
//import alfheim.common.core.util.AlfheimTab
//import alfheim.common.item.block.ItemBlockLeavesMod
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
	
//	val dreamwoodFence: Block // PORT: КТ-2
//	val dreamwoodFenceGate: Block // PORT: КТ-2
//	val dreamwoodBarkFence: Block // PORT: КТ-2
//	val dreamwoodBarkFenceGate: Block // PORT: КТ-2
//	val dwarfLantern: Block // PORT: КТ-2
//	val dwarfPlanks: Block // PORT: КТ-2
//	val dwarfPlanksStairs: Block // PORT: КТ-2
//	val dwarfPlanksSlab: Block // PORT: КТ-2
//	val dwarfPlanksSlabFull: Block // PORT: КТ-2
//	val dwarfTrapDoor: Block // PORT: КТ-2
//	val elfQuartzWall: Block // PORT: КТ-2
	val elvenSandstone: Array<Block>
//	val elvenSandstoneStairs: List<Block> // PORT: КТ-2
//	val elvenSandstoneSlab: Block // PORT: КТ-2
//	val elvenSandstoneSlabFull: Block // PORT: КТ-2
//	val elvenSandstoneSlab2: Block // PORT: КТ-2
//	val elvenSandstoneSlab2Full: Block // PORT: КТ-2
//	val elvenSandstoneWalls: List<Block> // PORT: КТ-2
//	val livingcobbleStairs: Block // PORT: КТ-2
//	val livingcobbleStairs1: Block // PORT: КТ-2
//	val livingcobbleStairs2: Block // PORT: КТ-2
//	val livingcobbleSlab: Block // PORT: КТ-2
//	val livingcobbleSlabFull: Block // PORT: КТ-2
//	val livingcobbleSlab1: Block // PORT: КТ-2
//	val livingcobbleSlabFull1: Block // PORT: КТ-2
//	val livingcobbleSlab2: Block // PORT: КТ-2
//	val livingcobbleSlabFull2: Block // PORT: КТ-2
//	val livingcobbleWall: Block // PORT: КТ-2
//	val livingMountain: Block // PORT: КТ-2
//	val livingMountainSlab: Block // PORT: КТ-2
//	val livingMountainSlabFull: Block // PORT: КТ-2
//	val livingrockBrickWall: Block // PORT: КТ-2
//	val livingrockDark: Block // PORT: КТ-2
//	val livingrockDarkStairs: List<Block> // PORT: КТ-2
//	val livingrockDarkSlabs: List<Block> // PORT: КТ-2
//	val livingrockDarkSlabsFull: List<Block> // PORT: КТ-2
//	val livingrockDarkWalls: List<Block> // PORT: КТ-2
//	val livingwoodFence: Block // PORT: КТ-2
//	val livingwoodFenceGate: Block // PORT: КТ-2
//	val livingwoodBarkFence: Block // PORT: КТ-2
//	val livingwoodBarkFenceGate: Block // PORT: КТ-2
//	val roofTile: Block // PORT: КТ-2
//	val roofTileSlabs: List<Block> // PORT: КТ-2
//	val roofTileSlabsFull: List<Block> // PORT: КТ-2
//	val roofTileStairs: List<Block> // PORT: КТ-2
//	val shrineLight: Block // PORT: КТ-2
//	val shrineGlass: Block // PORT: КТ-2
//	val shrinePanel: Block // PORT: КТ-2
//	val shrinePillar: Block // PORT: КТ-2
//	val shrineRock: Block // PORT: КТ-2
//	val shrineRockWhiteSlab: Block // PORT: КТ-2
//	val shrineRockWhiteSlabFull: Block // PORT: КТ-2
//	val shrineRockWhiteStairs: Block // PORT: КТ-2
//	val yggDecor: Block // PORT: КТ-2
	
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
		
//		yggDecor = BlockYggDecor() // PORT: КТ-2
//		shrineRock = BlockModMeta(Material.rock, 16, ModInfo.MODID, "ShrineRock", AlfheimTab, 10f, harvLvl = 2, resist = 10000f, folder = "decor/") // PORT: КТ-2
//		shrinePillar = BlockShrinePillar() // PORT: КТ-2
//		shrineRockWhiteStairs = object: BlockStairsMod(shrineRock, 0, "ShrineRockWhiteStairs") { // PORT: КТ-2
//			override fun register() {
//				GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
//			}
//			
//			override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = null
//		}
//		shrineRockWhiteSlab = BlockRockShrineWhiteSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f) // PORT: КТ-2
//		shrineRockWhiteSlabFull = BlockRockShrineWhiteSlab(true).setCreativeTab(null).setHardness(1.5f) // PORT: КТ-2
//		(shrineRockWhiteSlab as BlockModSlab).register() // PORT: КТ-2
//		(shrineRockWhiteSlabFull as BlockModSlab).register() // PORT: КТ-2
		
		val roofs = 3
//		roofTile = BlockModMeta(Material.rock, 3, ModInfo.MODID, "CustomRoof", AlfheimTab, 2f, resist = 5f, folder = "decor/").setStepSound(Block.soundTypeStone) // PORT: КТ-2
//		roofTileSlabs = (0 until roofs).map { BlockRoofTileSlab(false, it).setCreativeTab(AlfheimTab) } // PORT: КТ-2
//		roofTileSlabsFull = (0 until roofs).map { BlockRoofTileSlab(true, it).setCreativeTab(AlfheimTab) } // PORT: КТ-2
//		roofTileSlabs.forEach { (it as BlockModSlab).register() } // PORT: КТ-2
//		roofTileSlabsFull.forEach { (it as BlockModSlab).register() } // PORT: КТ-2
//		roofTileStairs = (0 until roofs).map { BlockModStairs(roofTile, it, "CustomRoofStairs$it").setCreativeTab(AlfheimTab) } // PORT: КТ-2
		
//		livingMountain = BlockLivingMountain() // PORT: КТ-2
//		livingMountainSlab = BlockLivingMountainSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f) // PORT: КТ-2
//		livingMountainSlabFull = BlockLivingMountainSlab(true).setCreativeTab(null).setHardness(1.5f) // PORT: КТ-2
//		(livingMountainSlab as BlockModSlab).register() // PORT: КТ-2
//		(livingMountainSlabFull as BlockModSlab).register() // PORT: КТ-2
		
		val metas = (0..3) - 2
//		livingrockDark = BlockModMeta(Material.rock, 4, ModInfo.MODID, "DarkLivingRock", AlfheimTab, 2f, resist = 10f, folder = "decor/") // PORT: КТ-2
//		livingrockDarkStairs = metas.map { BlockModStairs(livingrockDark, it, "DarkLivingRockStairs$it").setCreativeTab(AlfheimTab) } // PORT: КТ-2
		
//		livingrockDarkSlabs = metas.map { BlockLivingrockDarkSlab(false, it).setCreativeTab(AlfheimTab) } // PORT: КТ-2
//		livingrockDarkSlabsFull = metas.map { BlockLivingrockDarkSlab(true, it).setCreativeTab(AlfheimTab) } // PORT: КТ-2
//		livingrockDarkSlabs.forEach { (it as BlockModSlab).register() } // PORT: КТ-2
//		livingrockDarkSlabsFull.forEach { (it as BlockModSlab).register() } // PORT: КТ-2
		
//		livingrockDarkWalls = (0..1).map { // PORT: КТ-2
//			BlockModWall(livingrockDark, it)
//				.setCreativeTab(AlfheimTab)
//				.setHarvestLevelI("pickaxe", 2)
//		}
		
//		dwarfLantern = BlockDwarfLantern() // PORT: КТ-2
		
//		shrineLight = BlockModMeta(Material.glass, 6, ModInfo.MODID, "ShrineLight", AlfheimTab, resist = 6000f, folder = "decor/").setLightLevel(1f).setLightOpacity(0) // PORT: КТ-2
//		shrineGlass = BlockShrineGlass() // PORT: КТ-2
//		shrinePanel = object: BlockPaneMeta(Material.glass, 4, "ShrinePanel", "decor/") { // PORT: КТ-2
//			override fun getRenderBlockPass() = 1
//			override fun canPaneConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, dir: ForgeDirection) = super.canPaneConnectTo(world, x, y, z, dir) || world.getBlock(x, y, z) == shrineGlass
//		}.setBlockName("ShrinePanel")
//			.setCreativeTab(AlfheimTab)
//			.setLightOpacity(0)
//			.setHardness(1f)
//			.setHarvestLevelI("pickaxe", 1)
//			.setResistance(600f)
//			.setStepSound(Block.soundTypeGlass)
		
//		elfQuartzWall = BlockModWall(ModFluffBlocks.elfQuartz, 0) // PORT: КТ-2
//			.setCreativeTab(AlfheimTab)
//			.setHarvestLevelI("pickaxe", 2)
		
//		dwarfPlanks = BlockModMeta(Material.wood, 1, ModInfo.MODID, "DwarfPlanks", AlfheimTab, 3f, "axe", 1, 100f, "decor/") // PORT: КТ-2
//		dwarfPlanksStairs = BlockModStairs(dwarfPlanks, 0, "DwarfPlanksStairs").setCreativeTab(AlfheimTab) // PORT: КТ-2
//		dwarfPlanksSlab = BlockDwarfPlanksSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f) // PORT: КТ-2
//		dwarfPlanksSlabFull = BlockDwarfPlanksSlab(true).setCreativeTab(null).setHardness(1.5f) // PORT: КТ-2
//		(dwarfPlanksSlab as BlockModSlab).register() // PORT: КТ-2
//		(dwarfPlanksSlabFull as BlockModSlab).register() // PORT: КТ-2
		
		elvenSandstone = Array(5) { BlockElvenSandstone(it) }
//		elvenSandstoneStairs = arrayOf(0, 2).map { // PORT: КТ-2
//			BlockModStairs(elvenSandstone, it, "ElvenSandstoneStairs$it")
//				.setCreativeTab(AlfheimTab)
//		}
		
//		elvenSandstoneSlab = BlockElvenSandstoneSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f) // PORT: КТ-2
//		elvenSandstoneSlabFull = BlockElvenSandstoneSlab(true).setCreativeTab(null).setHardness(1.5f) // PORT: КТ-2
//		(elvenSandstoneSlab as BlockModSlab).register() // PORT: КТ-2
//		(elvenSandstoneSlabFull as BlockModSlab).register() // PORT: КТ-2
		
//		elvenSandstoneSlab2 = BlockElvenSandstoneSlab2(false).setCreativeTab(AlfheimTab).setHardness(1.5f) // PORT: КТ-2
//		elvenSandstoneSlab2Full = BlockElvenSandstoneSlab2(true).setCreativeTab(null).setHardness(1.5f) // PORT: КТ-2
//		(elvenSandstoneSlab2 as BlockModSlab).register() // PORT: КТ-2
//		(elvenSandstoneSlab2Full as BlockModSlab).register() // PORT: КТ-2
		
//		elvenSandstoneWalls = arrayOf(0, 2).map { // PORT: КТ-2
//			BlockModWall(elvenSandstone, it)
//				.setCreativeTab(AlfheimTab)
//		}
		
//		livingcobbleStairs = BlockModStairs(AlfheimBlocks.livingcobble, 0, "LivingCobbleStairs").setCreativeTab(AlfheimTab) // PORT: КТ-2
//		livingcobbleStairs1 = BlockModStairs(AlfheimBlocks.livingcobble, 1, "LivingCobbleStairs1").setCreativeTab(AlfheimTab) // PORT: КТ-2
//		livingcobbleStairs2 = BlockModStairs(AlfheimBlocks.livingcobble, 2, "LivingCobbleStairs2").setCreativeTab(AlfheimTab) // PORT: КТ-2
		
//		livingcobbleSlab = BlockLivingCobbleSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f) // PORT: КТ-2
//		livingcobbleSlabFull = BlockLivingCobbleSlab(true).setCreativeTab(null).setHardness(1.5f) // PORT: КТ-2
//		(livingcobbleSlab as BlockModSlab).register() // PORT: КТ-2
//		(livingcobbleSlabFull as BlockModSlab).register() // PORT: КТ-2
		
//		livingcobbleSlab1 = BlockLivingCobbleSlab1(false).setCreativeTab(AlfheimTab).setHardness(1.5f) // PORT: КТ-2
//		livingcobbleSlabFull1 = BlockLivingCobbleSlab1(true).setCreativeTab(null).setHardness(1.5f) // PORT: КТ-2
//		(livingcobbleSlab1 as BlockModSlab).register() // PORT: КТ-2
//		(livingcobbleSlabFull1 as BlockModSlab).register() // PORT: КТ-2
		
//		livingcobbleSlab2 = BlockLivingCobbleSlab2(false).setCreativeTab(AlfheimTab).setHardness(1.5f) // PORT: КТ-2
//		livingcobbleSlabFull2 = BlockLivingCobbleSlab2(true).setCreativeTab(null).setHardness(1.5f) // PORT: КТ-2
//		(livingcobbleSlab2 as BlockModSlab).register() // PORT: КТ-2
//		(livingcobbleSlabFull2 as BlockModSlab).register() // PORT: КТ-2
		
//		livingcobbleWall = BlockModWall(AlfheimBlocks.livingcobble, 0) // PORT: КТ-2
//			.setCreativeTab(AlfheimTab)
//			.setHarvestLevelI("pickaxe", 2)
		
//		livingrockBrickWall = BlockModWall(ModBlocks.livingrock, 1) // PORT: КТ-2
//			.setCreativeTab(AlfheimTab)
//			.setHarvestLevelI("pickaxe", 2)
		
//		livingwoodBarkFenceGate = BlockModFenceGate(ModBlocks.livingwood, 0) // PORT: КТ-2
//			.setCreativeTab(AlfheimTab)
//			.setBlockName("LivingwoodBarkFenceGate")
//			.setHardness(2f)
//			.setResistance(5f)
//			.setStepSound(Block.soundTypeWood)
		
//		livingwoodBarkFence = BlockModFence(LibResources.PREFIX_MOD + "livingwood0", Material.wood, livingwoodBarkFenceGate) // PORT: КТ-2
//			.setBlockName("LivingwoodBarkFence")
//			.setCreativeTab(AlfheimTab)
//			.setHardness(2f)
//			.setResistance(5f)
//			.setStepSound(Block.soundTypeWood)
		
//		dreamwoodBarkFenceGate = BlockModFenceGate(ModBlocks.dreamwood, 0) // PORT: КТ-2
//			.setBlockName("DreamwoodBarkFenceGate")
//			.setCreativeTab(AlfheimTab)
//			.setHardness(2f)
//			.setResistance(5f)
//			.setStepSound(Block.soundTypeWood)
		
//		dreamwoodBarkFence = BlockModFence(LibResources.PREFIX_MOD + "dreamwood0", Material.wood, dreamwoodBarkFenceGate) // PORT: КТ-2
//			.setBlockName("DreamwoodBarkFence")
//			.setCreativeTab(AlfheimTab)
//			.setHardness(2f)
//			.setResistance(5f)
//			.setStepSound(Block.soundTypeWood)
		
//		livingwoodFenceGate = BlockModFenceGate(ModBlocks.livingwood, 1) // PORT: КТ-2
//			.setBlockName("LivingwoodFenceGate")
//			.setCreativeTab(AlfheimTab)
//			.setHardness(2f)
//			.setResistance(5f)
//			.setStepSound(Block.soundTypeWood)
		
//		livingwoodFence = BlockModFence(LibResources.PREFIX_MOD + "livingwood1", Material.wood, livingwoodFenceGate) // PORT: КТ-2
//			.setBlockName("LivingwoodFence")
//			.setCreativeTab(AlfheimTab)
//			.setHardness(2f)
//			.setResistance(5f)
//			.setStepSound(Block.soundTypeWood)
		
//		dreamwoodFenceGate = BlockModFenceGate(ModBlocks.dreamwood, 1) // PORT: КТ-2
//			.setBlockName("DreamwoodFenceGate")
//			.setCreativeTab(AlfheimTab)
//			.setHardness(2f)
//			.setResistance(5f)
//			.setStepSound(Block.soundTypeWood)
		
//		dreamwoodFence = BlockModFence(LibResources.PREFIX_MOD + "dreamwood1", Material.wood, dreamwoodFenceGate) // PORT: КТ-2
//			.setBlockName("DreamwoodFence")
//			.setCreativeTab(AlfheimTab)
//			.setHardness(2f)
//			.setResistance(5f)
//			.setStepSound(Block.soundTypeWood)
		
//		dwarfTrapDoor = object: BlockModTrapDoor(Material.wood, "DwarfTrapDoor") { // PORT: КТ-2
//			override fun registerBlockIcons(reg: IIconRegister) {
//				blockIcon = IconHelper.forBlock(reg, this, "", "decor")
//			}
//		}
//			.setHardness(3f)
//			.setStepSound(Block.soundTypeWood)
	}
}