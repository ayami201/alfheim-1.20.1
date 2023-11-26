@file:Suppress("UNUSED_PARAMETER")

package alfheim.common.core.superwrapper

import com.KAIIIAK.superwrapper.SuperWrapper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor

object SuperWrapperHandler {
	
	@JvmStatic
	@SuperWrapper
	fun addInformation(item: ItemManasteelArmor, stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) {
		throw NotImplementedError()
	}
}