package alfheim.port.test

import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.entity.EntityThrowableItem
import alfheim.common.entity.EntityThrownPotion
import alfheim.common.item.AlfheimItems
import alfheim.common.item.ItemSplashPotion
import alfheim.common.item.material.ElvenResourcesMetas.RainbowDust
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.gametest.framework.*
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.block.entity.DispenserBlockEntity
import net.minecraft.world.phys.AABB
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import vazkii.botania.common.block.BotaniaBlocks
import vazkii.botania.common.brew.BotaniaBrews
import vazkii.botania.common.item.BotaniaItems

/**
 * КТ-2, партия 6б: раздатчики автора (`DispenserHandlers`) — бросок склянки и гранаты в сторону раздатчика, миска у
 * источника воды. Раздатчик срабатывает от блока красного камня рядом, как в игре
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortDispenserTest {

	/**
	 * Раздатчик, смотрящий в [facing], на высоте [dy] над точкой теста, с [stack] в первом слоте; вокруг — воздух
	 * (мир тестов сохраняется между запусками). Высота у каждого теста своя, выше построек других тестов
	 */
	private fun dispenser(helper: GameTestHelper, dy: Int, facing: Direction, stack: ItemStack): Pair<BlockPos, DispenserBlockEntity> {
		val pos = helper.absolutePos(BlockPos(0, dy, 0))
		clear(helper, pos)
		helper.level.setBlock(pos, Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, facing), 3)
		val dispenser = helper.level.getBlockEntity(pos) as DispenserBlockEntity
		dispenser.setItem(0, stack)
		return pos to dispenser
	}

	private fun clear(helper: GameTestHelper, pos: BlockPos, vararg entities: Entity) {
		entities.forEach { it.discard() }
		for (i in -2..2) for (j in -2..2) for (k in -2..2) helper.level.setBlock(pos.offset(i, j, k), Blocks.AIR.defaultBlockState(), 3)
	}

	/** Склянка из раздатчика на восток: летящее зелье с воздействиями варева, без бросившего, летит на восток; склянка тратится */
	@JvmStatic
	@GameTest(template = "empty", timeoutTicks = 100)
	fun dispenserThrowsPotion(helper: GameTestHelper) {
		val vial = (AlfheimItems.splashPotion as ItemSplashPotion).getItemForBrew(BotaniaBrews.healing, null)
		val (pos, dispenser) = dispenser(helper, 120, Direction.EAST, vial)
		helper.level.setBlock(pos.west(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3)
		helper.succeedWhen {
			val thrown = helper.level.getEntitiesOfClass(EntityThrownPotion::class.java, AABB(pos).inflate(8.0))
			helper.assertTrue(thrown.size == 1, "thrown potions: ${thrown.size}")
			val potion = thrown[0]
			helper.assertTrue(potion.thrower == null && potion.effects == BotaniaBrews.healing.getPotionEffects(vial.copyWithCount(1)), "no thrower, effects of the brew")
			helper.assertTrue(potion.deltaMovement.x > 0.5 && kotlin.math.abs(potion.deltaMovement.z) < 0.1, "flies east: ${potion.deltaMovement}")
			helper.assertTrue(dispenser.isEmpty, "the vial is used")
			clear(helper, pos, potion)
		}
	}

	/** Граната из раздатчика вверх: летит вверх, без бросившего (при ударе — ничего, BUGS.md, B-011); одна из двух тратится */
	@JvmStatic
	@GameTest(template = "empty", timeoutTicks = 100)
	fun dispenserThrowsGrenade(helper: GameTestHelper) {
		val (pos, dispenser) = dispenser(helper, 130, Direction.UP, ItemStack(AlfheimItems.fireGrenade, 2))
		helper.level.setBlock(pos.below(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3)
		helper.succeedWhen {
			val thrown = helper.level.getEntitiesOfClass(EntityThrowableItem::class.java, AABB(pos).inflate(8.0))
			helper.assertTrue(thrown.size == 1, "thrown grenades: ${thrown.size}")
			val grenade = thrown[0]
			helper.assertTrue(grenade.thrower == null && grenade.deltaMovement.y > 0.5, "flies up without a thrower: ${grenade.deltaMovement}")
			helper.assertTrue(dispenser.getItem(0).count == 1, "one grenade is used")
			clear(helper, pos, grenade)
		}
	}

	/** Миски у источника воды: из двух — одна остаётся, миска с водой Botania ложится в раздатчик; последняя миска становится миской с водой */
	@JvmStatic
	@GameTest(template = "empty", timeoutTicks = 100)
	fun dispenserFillsBowl(helper: GameTestHelper) {
		val (pos, dispenser) = dispenser(helper, 140, Direction.NORTH, ItemStack(Items.BOWL, 2))
		helper.level.setBlock(pos.north(), Blocks.WATER.defaultBlockState(), 3)
		helper.level.setBlock(pos.south(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3)
		helper.runAfterDelay(10) {
			helper.assertTrue(dispenser.getItem(0).`is`(Items.BOWL) && dispenser.getItem(0).count == 1, "one bowl is left: ${dispenser.getItem(0)}")
			helper.assertTrue((0 until dispenser.containerSize).count { dispenser.getItem(it).`is`(BotaniaItems.waterBowl) } == 1, "a water bowl is in the dispenser")
			// второй раз — последняя миска; раздатчик берёт случайный непустой слот, поэтому миска с водой вынимается
			for (slot in 1 until dispenser.containerSize) dispenser.setItem(slot, ItemStack.EMPTY)
			helper.level.setBlock(pos.south(), Blocks.AIR.defaultBlockState(), 3)
			helper.runAfterDelay(4) {
				helper.level.setBlock(pos.south(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3)
				helper.runAfterDelay(10) {
					helper.assertTrue(dispenser.getItem(0).`is`(BotaniaItems.waterBowl) && dispenser.getItem(0).count == 1, "the last bowl becomes a water bowl: ${dispenser.getItem(0)}")
					clear(helper, pos)
					helper.succeed()
				}
			}
		}
	}

	/**
	 * Раздатчик моста Бифрост (`BifrostFlowerDispenserHandler`): радужная пыль перед мистическим цветком Botania делает его
	 * радужным цветком, пыль тратится
	 */
	@JvmStatic
	@GameTest(template = "empty", timeoutTicks = 100)
	fun dispenserBifrostFlower(helper: GameTestHelper) {
		val (pos, dispenser) = dispenser(helper, 150, Direction.EAST, RainbowDust.stack(2))
		helper.level.setBlock(pos.east().below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3)
		helper.level.setBlock(pos.east(), BotaniaBlocks.getFlower(DyeColor.BLUE).defaultBlockState(), 3)
		helper.level.setBlock(pos.west(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3)
		helper.runAfterDelay(10) {
			helper.assertTrue(helper.level.getBlockState(pos.east()).`is`(AlfheimBlocks.rainbowGrass[2]), "the Botania flower became a rainbow flower: ${helper.level.getBlockState(pos.east())}")
			helper.assertTrue(dispenser.getItem(0).count == 1, "one rainbow dust is used: ${dispenser.getItem(0)}")
			clear(helper, pos)
			helper.succeed()
		}
	}
}
