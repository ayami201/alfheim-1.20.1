package alfheim.port.test

import alexsocol.asjlib.*
import alfheim.api.ModInfo.MODID
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
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
}
