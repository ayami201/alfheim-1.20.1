package alfheim.api.item.equipment

import alfheim.common.core.helper.ElementalDamage
import net.minecraft.item.ItemStack

/**
 * Elemental items are providing data about their elemental attunement
 * for changes in either elemental damage or elemental resistance of wearer
 */
interface IElementalItem {
	
	/**
	 * @return the element the [stack] is attuned to.
	 * [COMMON][ElementalDamage.COMMON] means no attunement
	 */
	fun getElement(stack: ItemStack): ElementalDamage
	
	/**
	 * Common levels for present elements are from 1 to 4
	 * @return the level of the [stack] elemental attunement.
	 */
	fun getElementLevel(stack: ItemStack): Int
}