package alfheim.common.block

import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.IIcon
import net.minecraft.world.*
import vazkii.botania.api.lexicon.ILexiconable

class BlockSnakeBody: BlockModMeta(Material.gourd, 16, ModInfo.MODID, "Snake", CreativeTabs.tabAllSearch, 0f, "", 0, 0f), ILexiconable {
	
	lateinit var HeadFront: IIcon
	lateinit var HeadLeft: IIcon
	lateinit var HeadRight: IIcon
	lateinit var HeadTop: Array<IIcon>
	lateinit var HeadBottom: Array<IIcon>
	
	lateinit var Side: Array<IIcon>
	lateinit var Top: Array<IIcon>
	lateinit var Bottom: Array<IIcon>
	
	init {
		stepSound = soundTypeCloth
	}
	
	override fun canProvidePower() = true
	
	override fun isProvidingWeakPower(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int {
		return if (world.getBlockMetadata(x, y, z) < 4) 15 else 0
	}
	
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) = Unit
	
	override fun getDrops(world: World?, x: Int, y: Int, z: Int, metadata: Int, fortune: Int) = ArrayList<ItemStack>()
	
	override fun registerBlockIcons(reg: IIconRegister) {
		HeadFront = IconHelper.forName(reg, "snake/HeadFront")
		HeadLeft = IconHelper.forName(reg, "snake/HeadLeft")
		HeadRight = IconHelper.forName(reg, "snake/HeadRight")
		HeadTop = Array(4) { IconHelper.forName(reg, "snake/HeadTop$it") }
		HeadBottom = Array(4) { IconHelper.forName(reg, "snake/HeadBottom$it") }
		
		Side = Array(3) { IconHelper.forName(reg, "snake/Side$it") }
		Top = Array(12) { IconHelper.forName(reg, "snake/Top${it+4}") }
		Bottom = Array(12) { IconHelper.forName(reg, "snake/Bottom${it+4}") }
	}
	
	// fuck you GedeonGrays
	override fun getIcon(side: Int, meta: Int): IIcon {
		val no = AlfheimBlocks.snakeObject.getIcon(0, 0)
		
		return when (meta) {
			0 -> when (side) {
				0 -> HeadBottom[0]
				1 -> HeadTop[0]
				2 -> HeadFront
				4 -> HeadLeft
				5 -> HeadRight
				else -> Side[0]
			}
			1 -> when (side) {
				0 -> HeadBottom[1]
				1 -> HeadTop[1]
				3 -> HeadFront
				4 -> HeadRight
				5 -> HeadLeft
				else -> Side[0]
			}
			2 -> when (side) {
				0 -> HeadBottom[2]
				1 -> HeadTop[2]
				2 -> HeadRight
				3 -> HeadLeft
				4 -> HeadFront
				else -> Side[0]
			}
			3 -> when (side) {
				0 -> HeadBottom[3]
				1 -> HeadTop[3]
				2 -> HeadLeft
				3 -> HeadRight
				5 -> HeadFront
				else -> Side[0]
			}
			4 -> when (side) {
				0 -> Bottom[0]
				1 -> Top[0]
				2 -> Side[0]
				3 -> Side[0]
				4 -> Side[2]
				5 -> Side[1]
				else -> no
			}
			5   -> when (side) {
				0 -> Bottom[1]
				1 -> Top[1]
				2 -> Side[0]
				3 -> Side[0]
				4 -> Side[1]
				5 -> Side[2]
				else -> no
			}
			6   -> when (side) {
				0 -> Bottom[2]
				1 -> Top[2]
				2 -> Side[1]
				3 -> Side[2]
				4 -> Side[0]
				5 -> Side[0]
				else -> no
			}
			7   -> when (side) {
				0 -> Bottom[3]
				1 -> Top[3]
				2 -> Side[2]
				3 -> Side[1]
				4 -> Side[0]
				5 -> Side[0]
				else -> no
			}
			8   -> when (side) {
				0 -> Bottom[4]
				1 -> Top[4]
				2 -> Side[0]
				3 -> Side[2]
				4 -> Side[0]
				5 -> Side[2]
				else -> no
			}
			9   -> when (side) {
				0 -> Bottom[5]
				1 -> Top[5]
				2 -> Side[1]
				3 -> Side[0]
				4 -> Side[0]
				5 -> Side[1]
				else -> no
			}
			10 -> when (side) {
				0 -> Bottom[6]
				1 -> Top[6]
				2 -> Side[1]
				3 -> Side[0]
				4 -> Side[1]
				5 -> Side[0]
				else -> no
			}
			11 -> when (side) {
				0 -> Bottom[7]
				1 -> Top[7]
				2 -> Side[0]
				3 -> Side[1]
				4 -> Side[1]
				5 -> Side[0]
				else -> no
			}
			12 -> when (side) {
				0 -> Bottom[8]
				1 -> Top[8]
				2 -> Side[2]
				3 -> Side[0]
				4 -> Side[0]
				5 -> Side[2]
				else -> no
			}
			13 -> when (side) {
				0 -> Bottom[9]
				1 -> Top[9]
				2 -> Side[2]
				3 -> Side[0]
				4 -> Side[2]
				5 -> Side[0]
				else -> no
			}
			14 -> when (side) {
				0 -> Bottom[10]
				1 -> Top[10]
				2 -> Side[0]
				3 -> Side[2]
				4 -> Side[2]
				5 -> Side[0]
				else -> no
			}
			15 -> when (side) {
				0 -> Bottom[11]
				1 -> Top[11]
				2 -> Side[0]
				3 -> Side[1]
				4 -> Side[0]
				5 -> Side[1]
				else -> no
			}
			else -> no
		}
	}
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.flowerRattlerose
}
