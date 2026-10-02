package alfheim.port.test

import alfheim.AlfheimCore
import alfheim.api.ModInfo.MODID
import alfheim.common.core.helper.ContributorsPrivacyHelper
import alfheim.common.core.proxy.CommonProxy
import net.minecraft.core.BlockPos
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import java.util.UUID

/**
 * КТ-1: прокси автора выбирается по стороне (DistExecutor), обработчики событий автора подписаны на шину Forge.
 * Проверка через то, что в КТ-1 уже работает: две особенности для участников команды автора в EventHandler.
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortProxyTest {

	@JvmStatic
	@GameTest(template = "empty")
	fun serverUsesCommonProxy(helper: GameTestHelper) {
		// на выделенном сервере ClientProxy и клиентские классы не загружаются
		helper.assertTrue(AlfheimCore.proxy.javaClass == CommonProxy::class.java, "proxy: ${AlfheimCore.proxy.javaClass.name}")
		helper.succeed()
	}

	/** Участник из списка автора садится верхом на существо, по которому щёлкнул палкой (EventHandler.onInteract) */
	@JvmStatic
	@GameTest(template = "empty")
	fun contributorRidesWithStick(helper: GameTestHelper) {
		val player = helper.makeMockPlayer()
		val pig = helper.spawn(EntityType.PIG, BlockPos(1, 2, 1))
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack(Items.STICK))

		ContributorsPrivacyHelper.contributors["PortProxyTest"] = player.gameProfile.name
		try {
			MinecraftForge.EVENT_BUS.post(PlayerInteractEvent.EntityInteract(player, InteractionHand.MAIN_HAND, pig))
		} finally {
			ContributorsPrivacyHelper.contributors.remove("PortProxyTest")
		}

		helper.assertTrue(player.vehicle === pig, "player rides ${player.vehicle}")
		player.stopRiding()
		helper.succeed()
	}

	/** Ручной волк переходит к GedeonGrays, когда тот щёлкает по нему (EventHandler.worowalaSobak) */
	@JvmStatic
	@GameTest(template = "empty")
	fun gedeonGraysTakesTamedWolf(helper: GameTestHelper) {
		val player = helper.makeMockPlayer()
		val wolf = helper.spawn(EntityType.WOLF, BlockPos(1, 2, 1))
		wolf.setTame(true)
		wolf.ownerUUID = UUID.randomUUID()

		val previous = ContributorsPrivacyHelper.contributors["GedeonGrays"]
		ContributorsPrivacyHelper.contributors["GedeonGrays"] = player.gameProfile.name
		try {
			MinecraftForge.EVENT_BUS.post(PlayerInteractEvent.EntityInteract(player, InteractionHand.MAIN_HAND, wolf))
		} finally {
			if (previous == null) ContributorsPrivacyHelper.contributors.remove("GedeonGrays") else ContributorsPrivacyHelper.contributors["GedeonGrays"] = previous
		}

		helper.assertTrue(wolf.ownerUUID == player.uuid, "wolf owner ${wolf.ownerUUID}, player ${player.uuid}")
		helper.succeed()
	}
}
