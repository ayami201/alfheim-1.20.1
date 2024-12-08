package alfheim.common.block.magtrees

import alfheim.client.core.helper.IconHelper
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.item.block.ItemBlockMetaSapling
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.world.dim.alfheim.biome.BiomeAlfheim
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.util.IIcon
import net.minecraft.world.World
import net.minecraft.world.gen.feature.*
import ru.vamig.worldengine.standardcustomgen.help.WE_TreeGen
import java.util.*

class BlockTunedSapling: BlockColoredSapling("TunedSapling") {
	
	lateinit var icons: List<IIcon>
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockMetaSapling::class.java, name)
		return super.setBlockName(name)
	}
	
	override fun shouldRegisterInNameSet() = false
	
	override fun damageDropped(meta: Int) = meta and 7
	
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		repeat(8) { list.add(ItemStack(item, 1, it)) }
	}
	
	override fun getGenerator(meta: Int): WorldGenerator {
		return when (meta and 7) {
			0, 1 -> WorldGenBigTree(true)
			2    -> WorldGenForest(true, true)
			3    -> WorldGenShrub(3, 3)
			4    -> WorldGenSwamp()
			5    -> WorldGenTaiga1()
			6    -> WE_TreeGen(false).apply { bWood = Blocks.log2; metaWood = 1; bLeaves = Blocks.leaves2; metaLeaves = 1; minTreeHeight = 5 }
			7    -> object: WorldGenerator() {
				override fun generate(world: World, random: Random, x: Int, y: Int, z: Int) = BiomeAlfheim.sadOak.generate(world, random, x, y, z, null)
			}
			// stupid kotlin -_-
			else -> throw IllegalArgumentException("`meta & 7` not in 0..7 range - WTF is wrong with your JVM?")
		}
	}
	
	override fun getRandomForGenerating(meta: Int, default: Random) = if (meta % 8 == 1) Random(61648L) else default
	
	override fun canGrowHere(block: Block) =
		block.material == Material.ground || block.material == Material.grass
	
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = (0..7).map { IconHelper.forBlock(reg, this, it) }
	}
	
	override fun getIcon(side: Int, meta: Int) = icons[meta and 7]
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.tunedSaplings
}
