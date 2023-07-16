package alfheim.common.block

import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.util.AlfheimTab
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.*
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.common.lexicon.LexiconData

class BlockLivingCobble: BlockModMeta(Material.rock, 4, ModInfo.MODID, "LivingCobble", AlfheimTab, 2f, resist = 60f), ILexiconable {
	
	lateinit var iconAlt: IIcon
	
	override fun registerBlockIcons(reg: IIconRegister) {
		super.registerBlockIcons(reg)
		iconAlt = IconHelper.forBlock(reg, this, "3Alt")
	}
	
	override fun getIcon(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): IIcon {
		val meta = world.getBlockMetadata(x, y, z)
		if (meta == 3 && "$x$y$z".hashCode() % 2 == 0) return iconAlt
		
		return super.getIcon(world, x, y, z, side)
	}
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = when (world.getBlockMetadata(x, y, z)) {
		0 -> AlfheimLexiconData.worldgen
		1, 2 -> LexiconData.decorativeBlocks
		3 -> LexiconData.vineBall
		else -> null
	}
}
