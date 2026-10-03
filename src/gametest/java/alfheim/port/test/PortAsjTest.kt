package alfheim.port.test

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo.MODID
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate

/** КТ-1: части ASJCore, которые нужны КТ-2, дают то же, что в 1.7.10 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortAsjTest {

	@JvmStatic
	@GameTest(template = "empty")
	fun blockItemConversions(helper: GameTestHelper) {
		helper.assertTrue(Blocks.STONE.toItem() === Items.STONE, "stone toItem")
		helper.assertTrue(Blocks.WATER.toItem() == null, "block without item gives null, as in 1.7.10")
		helper.assertTrue(Items.STONE.toBlock() === Blocks.STONE, "stone toBlock")
		helper.assertTrue(Items.SUGAR_CANE.toBlock() === Blocks.SUGAR_CANE, "ItemReed of 1.7.10 gives its block")
		helper.assertTrue(Items.STICK.toBlock() === Blocks.AIR, "item without block gives air, as in 1.7.10")
		helper.assertTrue(ItemStack(Items.OAK_PLANKS).block === Blocks.OAK_PLANKS, "ItemStack.block")
		helper.succeed()
	}

	/** `Vector3.isInside` (PORT-FIX): у автора всегда давал false */
	@JvmStatic
	@GameTest(template = "empty")
	fun vectorInsideBox(helper: GameTestHelper) {
		val box = AABB(0.0, 0.0, 0.0, 1.0, 2.0, 1.0)
		helper.assertTrue(Vector3(0.5, 1.5, 0.5).isInside(box), "point inside the box")
		helper.assertTrue(!Vector3(0.5, 2.5, 0.5).isInside(box) && !Vector3(0.5, -0.5, 0.5).isInside(box), "points above and below the box")
		helper.succeed()
	}
}
