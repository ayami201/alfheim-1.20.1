package alfheim.common.block

import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.world.World
import vazkii.botania.api.lexicon.ILexiconable

class BlockSnakeObject: BlockModMeta(Material.gourd, 2, ModInfo.MODID, "SnakeObject", AlfheimTab, 0f, "", 0, 0f, "snake/"), ILexiconable {
	
	init {
		stepSound = soundTypeCloth
	}
	
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		list.add(ItemStack(item))
	}
	
	override fun getDrops(world: World?, x: Int, y: Int, z: Int, metadata: Int, fortune: Int) = ArrayList<ItemStack>()
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.flowerRattlerose
}
