package alfheim.common.item.material

// PORT: импорты 1.20.1; призыв Флюгеля (EntityFlugel) — КТ-8
import alexsocol.asjlib.meta
import alfheim.common.item.ItemMod
import net.minecraft.world.item.ItemStack
//import alfheim.common.entity.boss.EntityFlugel
//import net.minecraft.entity.player.EntityPlayer
//import net.minecraft.world.World

class ItemSoulHorn: ItemMod("SoulHorn") {
	
	init {
		maxStackSize = 1
	}
	
	// PORT: КТ-8 — призыв Флюгеля (EntityFlugel); в 1.20.1 — useOn
	/*
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		// Stupid Et Futurum
		if (player.isSneaking && stack.meta == 1) {
			val success = EntityFlugel.spawn(player, stack, world, x, y, z, true, true)
			if (success && !player.capabilities.isCreativeMode) stack.meta = 0
			return success
		}
		return false
	}
	*/
	
	override fun getColorFromItemStack(stack: ItemStack, renderPass: Int) = if (stack.meta == 1) -0x1 else -0x222223 // 0xFFDDDDDD
}