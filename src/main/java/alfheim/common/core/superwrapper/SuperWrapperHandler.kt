@file:Suppress("UNUSED_PARAMETER")

package alfheim.common.core.superwrapper

import com.KAIIIAK.superwrapper.SuperWrapper
import net.minecraft.block.*
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor

object SuperWrapperHandler {
	
	@JvmStatic
	@SuperWrapper
	fun addInformation(item: ItemManasteelArmor, stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) {
		throw NotImplementedError()
	}
	
	@JvmStatic
	@SuperWrapper(callThis = false)
	fun canDespawn(entity: EntityLiving): Boolean {
		throw NotImplementedError()
	}
	
	@JvmStatic
	@SuperWrapper(callThis = false)
	fun setFlag(entity: Entity, id: Int, flag: Boolean) {
		throw NotImplementedError()
	}
	
	@JvmStatic
	@SuperWrapper(callThis = false)
	fun getFlag(entity: Entity, id: Int): Boolean {
		throw NotImplementedError()
	}
	
	@JvmStatic
	@SuperWrapper(callThis = false)
	fun canPlaceBlockOn(block: BlockBush, target: Block): Boolean {
		throw NotImplementedError()
	}
}