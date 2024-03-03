package alfheim.common.crafting.recipe

import alexsocol.asjlib.*
import alfheim.api.lib.LibOreDict.IFFESAL_DUST
import alfheim.common.core.asm.hook.extender.ItemLensExtender
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.oredict.*
import vazkii.botania.api.subtile.SubTileEntity
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.ModItems.lens
import vazkii.botania.common.item.block.ItemBlockSpecialFlower
import vazkii.botania.common.lib.LibBlockNames.SUBTILE_PUREDAISY
import vazkii.botania.common.lib.LibOreDict.RUNE

object RecipeLensPurification: ShapedOreRecipe(ItemStack(lens, 1, ItemLensExtender.EnumAlfheimLens.DAISY.meta),
                                               " P ", "RLR", " I ",
                                               'P', ItemBlockSpecialFlower.ofType(SUBTILE_PUREDAISY),
                                               'L', ItemStack(lens),
                                               'R', RUNE[8], // mana
                                               'I', IFFESAL_DUST
) {
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		val daisy = inv[1] ?: return false
		val lens = inv[4] ?: return false
		val iff = inv[7] ?: return false
		val runes = arrayOf(inv[3] ?: return false, inv[5] ?: return false)
		
		val isDaisy = daisy.item === ModBlocks.specialFlower.toItem() && ItemNBTHelper.getString(daisy, SubTileEntity.TAG_TYPE, "") == SUBTILE_PUREDAISY
		val isLens = lens.item == ModItems.lens && lens.meta == 0
		val isIff = OreDictionary.getOreIDs(iff).any { OreDictionary.getOreName(it) == IFFESAL_DUST }
		val areRunes = runes.all { OreDictionary.getOreIDs(it).any { id -> OreDictionary.getOreName(id) == RUNE[8] } }
		val otherEmpty = arrayOf(0, 2, 6, 8).all { inv[it] == null }
		
		return isDaisy && isLens && isIff && areRunes && otherEmpty
	}
}
