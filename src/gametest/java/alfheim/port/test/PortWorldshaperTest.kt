package alfheim.port.test

import alexsocol.asjlib.meta
import alfheim.api.ModInfo.MODID
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.AlfheimItems
import alfheim.common.item.ItemArmilla
import alfheim.common.item.ItemTriquetrum
import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import vazkii.botania.common.item.BotaniaItems
import java.util.UUID

/**
 * КТ-2, партия 5б: трикветр и браслет миротворца. Значения — из кода автора: `ItemTriquetrum`, `ItemArmilla`
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortWorldshaperTest {

	private fun player(helper: GameTestHelper, name: String): FakePlayer =
		FakePlayerFactory.get(helper.level, GameProfile(UUID.randomUUID(), name)).apply { removeAllEffects(); inventory.clearContent(); isShiftKeyDown = false }

	/** ПКМ предметом по блоку [pos] со стороны [side] */
	private fun useOn(player: FakePlayer, stack: ItemStack, pos: BlockPos, side: Direction): InteractionResult {
		player.setItemInHand(InteractionHand.MAIN_HAND, stack)
		return stack.useOn(UseOnContext(player, InteractionHand.MAIN_HAND, BlockHitResult(Vec3.atCenterOf(pos), side, pos, false)))
	}

	private val triquetrum get() = AlfheimItems.triquetrum as ItemTriquetrum

	/** Расчистить место над площадкой: мир тестов сохраняется между запусками */
	private fun clear(helper: GameTestHelper, from: BlockPos, to: BlockPos) {
		for (pos in BlockPos.betweenClosed(from, to)) helper.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
	}

	@JvmStatic
	@GameTest(template = "empty")
	fun worldshaperItems(helper: GameTestHelper) {
		for ((item, id) in mapOf(AlfheimItems.triquetrum to "triquetrum", AlfheimItems.armilla to "armilla")) {
			helper.assertTrue(BuiltInRegistries.ITEM.getKey(item) == ResourceLocation(MODID, id), "$id is ${BuiltInRegistries.ITEM.getKey(item)}")
			helper.assertTrue(ItemStack(item).maxStackSize == 1, "$id stack size")
		}
		helper.assertTrue(ItemStack(AlfheimItems.triquetrum).hoverName.string == "Worldshaper's Triquetrum" && ItemStack(AlfheimItems.armilla).hoverName.string == "Worldshaper's Armilla", "names")
		val armilla = ItemStack(AlfheimItems.armilla)
		helper.assertTrue(armilla.useAnimation == UseAnim.BOW && armilla.useDuration == 72000, "armilla is held like a bow up to an hour")
		helper.succeed()
	}

	/** Две точки — ПКМ по блокам, поворот — ПКМ в воздух, Shift+ПКМ — сброс; точка может быть и на высоте −1 */
	@JvmStatic
	@GameTest(template = "empty")
	fun triquetrumSelection(helper: GameTestHelper) {
		val player = player(helper, "alfheim-triquetrum")
		val base = helper.absolutePos(BlockPos.ZERO)
		val first = BlockPos(base.x, -1, base.z)
		val second = first.east()
		helper.level.setBlock(first, Blocks.STONE.defaultBlockState(), 3)
		helper.level.setBlock(second, Blocks.STONE.defaultBlockState(), 3)

		val stack = ItemStack(AlfheimItems.triquetrum)
		player.setItemInHand(InteractionHand.MAIN_HAND, stack)
		helper.assertTrue(triquetrum.getFirstPosition(stack) == null && triquetrum.getRotation(stack) == -1, "nothing is selected")
		helper.assertTrue(stack.use(helper.level, player, InteractionHand.MAIN_HAND).result == InteractionResult.CONSUME && stack.tag?.getInt(ItemTriquetrum.TAG_ROTATION) == 1, "right click in the air turns by 90 degrees")

		helper.assertTrue(useOn(player, stack, first, Direction.UP).consumesAction() && triquetrum.getFirstPosition(stack) == first, "first point below zero: ${triquetrum.getFirstPosition(stack)}")
		helper.assertTrue(useOn(player, stack, second, Direction.UP).consumesAction() && triquetrum.getSecondPosition(stack) == second, "second point")
		helper.assertTrue(triquetrum.getRotation(stack) == 1, "rotation with both points")

		val tooltip = ArrayList<Component>()
		stack.item.appendHoverText(stack, helper.level, tooltip, TooltipFlag.NORMAL)
		helper.assertTrue(tooltip.map { it.string } == listOf("2 blocks selected", "Rotation 90 degrees counterclockwise"), "tooltip: ${tooltip.map { it.string }}")

		player.isShiftKeyDown = true
		stack.use(helper.level, player, InteractionHand.MAIN_HAND)
		player.isShiftKeyDown = false
		helper.assertTrue(triquetrum.getFirstPosition(stack) == null && triquetrum.getSecondPosition(stack) == null && triquetrum.getRotation(stack) == -1, "shift + right click clears the points")

		helper.level.setBlock(first, Blocks.AIR.defaultBlockState(), 3)
		helper.level.setBlock(second, Blocks.AIR.defaultBlockState(), 3)
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
		helper.succeed()
	}

	/** Перенос в выживании: мана из инвентаря (60 за блок, 100 за блок-сущность), блок-сущность — с содержимым */
	@JvmStatic
	@GameTest(template = "empty")
	fun triquetrumMovesBlocks(helper: GameTestHelper) {
		val player = player(helper, "alfheim-triquetrum-move")
		val base = helper.absolutePos(BlockPos(0, 40, 0))
		clear(helper, base.offset(-1, -1, -1), base.offset(6, 3, 2))
		helper.level.setBlock(base, Blocks.STONE.defaultBlockState(), 3)
		helper.level.setBlock(base.east(), Blocks.CHEST.defaultBlockState(), 3)
		(helper.level.getBlockEntity(base.east()) as ChestBlockEntity).setItem(0, ItemStack(Items.DIAMOND, 3))
		val target = base.offset(4, 0, 0)
		helper.level.setBlock(target, Blocks.STONE.defaultBlockState(), 3)

		val tablet = ItemStack(BotaniaItems.manaTablet).also { it.orCreateTag.putInt("mana", 1000) }
		player.inventory.setItem(9, tablet)
		val stack = ItemStack(AlfheimItems.triquetrum)
		useOn(player, stack, base, Direction.UP)
		useOn(player, stack, base.east(), Direction.UP)
		helper.assertTrue(triquetrum.getRotation(stack) == 0, "both points selected")

		helper.assertTrue(useOn(player, stack, target, Direction.UP).consumesAction(), "third click moves")
		val moved = target.above()
		helper.assertTrue(helper.level.getBlockState(moved).`is`(Blocks.STONE) && helper.level.getBlockState(moved.east()).`is`(Blocks.CHEST), "blocks are on top of the target: ${helper.level.getBlockState(moved)}, ${helper.level.getBlockState(moved.east())}")
		val chest = helper.level.getBlockEntity(moved.east()) as? ChestBlockEntity
		helper.assertTrue(chest != null && chest.getItem(0).`is`(Items.DIAMOND) && chest.getItem(0).count == 3, "chest keeps its contents: ${chest?.getItem(0)}")
		helper.assertTrue(helper.level.getBlockState(base).isAir && helper.level.getBlockState(base.east()).isAir, "old place is empty")
		helper.assertTrue(helper.level.getEntitiesOfClass(ItemEntity::class.java, AABB(base).inflate(3.0)).isEmpty(), "nothing dropped from the old chest")
		helper.assertTrue(tablet.tag?.getInt("mana") == 1000 - 60 - 100, "mana: ${tablet.tag?.getInt("mana")}")
		helper.assertTrue(triquetrum.getFirstPosition(stack) == null && stack.tag?.getInt(ItemTriquetrum.TAG_ROTATION) == 0, "selection is cleared after the move")

		clear(helper, base.offset(-1, -1, -1), base.offset(6, 3, 2))
		player.inventory.clearContent()
		helper.succeed()
	}

	/** Поворот 1 (90°): смещение по x уходит в −z; ограничение объёма; заряд 1 (metadata) копирует, не перенося */
	@JvmStatic
	@GameTest(template = "empty")
	fun triquetrumRotationAndLimit(helper: GameTestHelper) {
		val player = player(helper, "alfheim-triquetrum-turn")
		player.abilities.instabuild = true
		val base = helper.absolutePos(BlockPos(0, 44, 0))
		clear(helper, base.offset(-1, -1, -3), base.offset(6, 3, 3))
		helper.level.setBlock(base, Blocks.STONE.defaultBlockState(), 3)
		helper.level.setBlock(base.east(), Blocks.DIRT.defaultBlockState(), 3)
		val target = base.offset(4, 0, 0)
		helper.level.setBlock(target, Blocks.STONE.defaultBlockState(), 3)

		val stack = ItemStack(AlfheimItems.triquetrum)
		useOn(player, stack, base, Direction.UP)
		useOn(player, stack, base.east(), Direction.UP)
		stack.use(helper.level, player, InteractionHand.MAIN_HAND)
		helper.assertTrue(triquetrum.getRotation(stack) == 1, "rotation 1")
		useOn(player, stack, target, Direction.UP)
		helper.assertTrue(helper.level.getBlockState(target.above()).`is`(Blocks.STONE) && helper.level.getBlockState(target.above().north()).`is`(Blocks.DIRT), "rotated by 90 degrees: dirt goes north")

		// metadata 1 — копия: старые блоки остаются
		val copy = ItemStack(AlfheimItems.triquetrum).also { it.meta = 1 }
		val target2 = base.offset(4, 0, 2)
		helper.level.setBlock(target2, Blocks.STONE.defaultBlockState(), 3)
		useOn(player, copy, target.above(), Direction.UP)
		useOn(player, copy, target.above().north(), Direction.UP)
		useOn(player, copy, target2, Direction.UP)
		// точки — камень (юг) и земля (север): обход от меньшей z, поворот 0 — земля ложится первой
		helper.assertTrue(helper.level.getBlockState(target2.above()).`is`(Blocks.DIRT) && helper.level.getBlockState(target2.above().south()).`is`(Blocks.STONE), "copy is placed")
		helper.assertTrue(helper.level.getBlockState(target.above()).`is`(Blocks.STONE) && helper.level.getBlockState(target.above().north()).`is`(Blocks.DIRT), "originals stay")

		// объём автора — |dx|·|dy|·|dz| без +1
		val limit = AlfheimConfigHandler.triquetrumMaxVolume
		try {
			AlfheimConfigHandler.triquetrumMaxVolume = 1
			val big = ItemStack(AlfheimItems.triquetrum)
			useOn(player, big, base.offset(0, -1, 0), Direction.UP)
			helper.assertTrue(useOn(player, big, target2.above(), Direction.UP) == InteractionResult.PASS && triquetrum.getSecondPosition(big) == null, "selection over the volume limit is refused")
		} finally {
			AlfheimConfigHandler.triquetrumMaxVolume = limit
		}

		clear(helper, base.offset(-1, -1, -3), base.offset(6, 3, 3))
		helper.succeed()
	}

	/** Браслет: источник — блок под лучом до 128 блоков; радиус — от источника до точки взгляда на его высоте */
	@JvmStatic
	@GameTest(template = "empty")
	fun armillaSourceAndRadius(helper: GameTestHelper) {
		val player = player(helper, "alfheim-armilla")
		val source = helper.absolutePos(BlockPos(0, 50, 0))
		clear(helper, source.offset(-1, 0, -1), source.offset(5, 8, 5))
		helper.level.setBlock(source, Blocks.STONE.defaultBlockState(), 3)

		val stack = ItemStack(AlfheimItems.armilla)
		player.setItemInHand(InteractionHand.MAIN_HAND, stack)
		player.moveTo(source.x + 0.5, source.y + 3.0, source.z + 0.5, 0f, 90f)
		helper.assertTrue(stack.use(helper.level, player, InteractionHand.MAIN_HAND).result == InteractionResult.CONSUME && player.isUsingItem, "armilla is being used")
		val tag = stack.tag
		helper.assertTrue(tag != null && tag.getInt("sourceX") == source.x && tag.getInt("sourceY") == source.y && tag.getInt("sourceZ") == source.z, "source is the block under the ray: $tag")

		// взгляд вниз из точки (x + 3,5; z + 4,5): точка на высоте источника — (x + 3, z + 4), радиус 5
		player.moveTo(source.x + 3.5, source.y + 6.0, source.z + 4.5, 0f, 90f)
		helper.assertTrue(ItemArmilla.calculateRadius(stack, player) == 5.0, "radius: ${ItemArmilla.calculateRadius(stack, player)}")
		stack.releaseUsing(helper.level, player, 100)
		player.stopUsingItem()

		player.isShiftKeyDown = true
		stack.use(helper.level, player, InteractionHand.MAIN_HAND)
		player.isShiftKeyDown = false
		helper.assertTrue(stack.tag?.getInt("sourceY") == source.y && !player.isUsingItem, "shift + right click only clears the highlight")

		player.moveTo(source.x + 0.5, source.y + 3.0, source.z + 0.5, 0f, -90f)
		clear(helper, source.above(), source.above(140)) // луч вверх — 128 блоков
		stack.use(helper.level, player, InteractionHand.MAIN_HAND)
		helper.assertTrue(stack.tag?.getInt("sourceY") == Int.MIN_VALUE, "no block under the ray — no source: ${stack.tag}")
		player.stopUsingItem()

		helper.level.setBlock(source, Blocks.AIR.defaultBlockState(), 3)
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
		helper.succeed()
	}
}
