package alfheim.port.test

import alexsocol.asjlib.*
import alfheim.api.ModInfo.MODID
import alfheim.common.item.AlfheimItems
import alfheim.common.item.ItemDeathSeed
import alfheim.common.item.ItemHyperBucket
import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import vazkii.botania.common.item.BotaniaItems
import vazkii.botania.common.item.ResoluteIvyItem
import java.util.UUID

/**
 * КТ-2, партия 5а: мел, семя смерти, рог души, гиперведро и функции ASJCore, на которых они стоят (луч от взгляда,
 * перенос между мирами). Значения — из кода автора
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortToolsTest {

	private fun player(helper: GameTestHelper, name: String): FakePlayer =
		FakePlayerFactory.get(helper.level, GameProfile(UUID.randomUUID(), name)).apply { removeAllEffects(); inventory.clearContent() }

	/** id, имена, стопка по одному; рог души: цвет по metadata, во вкладке — обычный и заряженный */
	@JvmStatic
	@GameTest(template = "empty")
	fun toolItems(helper: GameTestHelper) {
		val items = mapOf(AlfheimItems.chalk to "chalk", AlfheimItems.deathSeed to "death_seed", AlfheimItems.soulHorn to "soul_horn", AlfheimItems.hyperBucket to "hyperpolated_bucket")
		for ((item, id) in items) {
			helper.assertTrue(BuiltInRegistries.ITEM.getKey(item) == ResourceLocation(MODID, id), "$id is ${BuiltInRegistries.ITEM.getKey(item)}")
			helper.assertTrue(ItemStack(item).maxStackSize == 1, "$id stack size")
		}
		helper.assertTrue(ItemStack(AlfheimItems.chalk).hoverName.string == "Runic Chalk" && ItemStack(AlfheimItems.soulHorn).hoverName.string == "Horn of Souls", "names")

		val horn = ItemStack(AlfheimItems.soulHorn)
		val charged = ItemStack(AlfheimItems.soulHorn).also { it.meta = 1 }
		val color = alfheim.port.legacy.Item1710::class.java.cast(AlfheimItems.soulHorn)
		helper.assertTrue(color.getColorFromItemStack(horn, 0) == -0x222223 && color.getColorFromItemStack(charged, 0) == -1, "soul horn colors")
		helper.assertTrue(charged.meta == 1 && !charged.isDamageableItem, "charged soul horn keeps its metadata")
		helper.succeed()
	}

	/** Семя смерти: плющ верности, место смерти, возврат с «Сопротивлением V» на 5 секунд */
	@JvmStatic
	@GameTest(template = "empty")
	fun deathSeed(helper: GameTestHelper) {
		val player = player(helper, "alfheim-death-seed")
		val seed = ItemStack(AlfheimItems.deathSeed)
		player.setItemInHand(InteractionHand.MAIN_HAND, seed)
		seed.item.inventoryTick(seed, helper.level, player, 0, true)
		helper.assertTrue(ItemNBTHelper.getBoolean(seed, ResoluteIvyItem.TAG_KEEP, false), "death seed keeps itself with Botania's ivy tag")

		helper.assertTrue(seed.use(helper.level, player, InteractionHand.MAIN_HAND).result == InteractionResult.PASS && seed.count == 1, "no place recorded — nothing happens")

		// смерть в пустоте — ниже мира, как y < 0 у автора: места нет
		val void = helper.absoluteVec(Vec3(0.5, 0.0, 0.5))
		player.moveTo(void.x, helper.level.minBuildHeight - 70.0, void.z)
		MinecraftForge.EVENT_BUS.post(LivingDeathEvent(player, player.damageSources().fellOutOfWorld()))
		helper.assertTrue(ItemNBTHelper.getString(seed, ItemDeathSeed.TAG_D, "") == "minecraft:overworld" && !ItemNBTHelper.verifyExistance(seed, ItemDeathSeed.TAG_Y), "death in the void is no place")
		helper.assertTrue(seed.use(helper.level, player, InteractionHand.MAIN_HAND).result == InteractionResult.PASS && seed.count == 1, "death in the void — nothing happens")

		// мир 1.20.1 ниже нуля: такая высота — место
		val place = Vec3(void.x, -50.0, void.z)
		helper.assertTrue(place.y > helper.level.minBuildHeight, "the test world goes below -50")
		player.moveTo(place.x, place.y, place.z)
		MinecraftForge.EVENT_BUS.post(LivingDeathEvent(player, player.damageSources().generic()))
		helper.assertTrue(ItemNBTHelper.getString(seed, ItemDeathSeed.TAG_D, "") == "minecraft:overworld", "dimension of death: ${ItemNBTHelper.getString(seed, ItemDeathSeed.TAG_D, "")}")
		helper.assertTrue(ItemNBTHelper.getDouble(seed, ItemDeathSeed.TAG_Y, 0.0) == place.y, "height of death, below zero: ${ItemNBTHelper.getDouble(seed, ItemDeathSeed.TAG_Y, 0.0)}")

		helper.assertTrue(seed.use(helper.level, player, InteractionHand.MAIN_HAND).result == InteractionResult.CONSUME && seed.isEmpty, "death seed is used up")
		helper.assertTrue(player.getEffect(MobEffects.DAMAGE_RESISTANCE)?.let { it.amplifier == 4 && it.duration == 100 } == true, "resistance V for 5 seconds")
		player.removeAllEffects()
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
		helper.succeed()
	}

	/** `ASJUtilities.sendToDimensionWithoutPortal`: в своём мире — в точку, в другой мир — та же сущность (тот же UUID) в точке */
	@JvmStatic
	@GameTest(template = "empty", timeoutTicks = 600)
	fun sendToDimension(helper: GameTestHelper) {
		val pig = helper.spawn(EntityType.PIG, BlockPos(0, 2, 0))
		val target = helper.absoluteVec(Vec3(0.5, 4.0, 0.5))
		ASJUtilities.sendToDimensionWithoutPortal(pig, Level.OVERWORLD, target.x, target.y, target.z)
		helper.assertTrue(pig.position() == target, "same dimension: ${pig.position()}, expected $target")

		val nether = helper.level.server.getLevel(Level.NETHER)!!
		nether.setChunkForced(0, 0, true)
		ASJUtilities.sendToDimensionWithoutPortal(pig, Level.NETHER, 0.5, 100.0, 0.5)
		helper.assertTrue(pig.isRemoved, "the pig left the overworld")
		helper.succeedWhen {
			val moved = nether.getEntity(pig.uuid)
			helper.assertTrue(moved != null && moved.type == EntityType.PIG && moved.position() == Vec3(0.5, 100.0, 0.5), "pig in the nether: ${moved?.position()}")
			moved!!.discard()
			nether.setChunkForced(0, 0, false)
		}
	}

	/** Гиперведро: луч до источника жидкости, вычерпывает куб радиусом `range`; Shift — радиус по кругу 1 → 0 → 1 */
	@JvmStatic
	@GameTest(template = "empty")
	fun hyperBucket(helper: GameTestHelper) {
		val player = player(helper, "alfheim-hyper-bucket")
		val center = helper.absolutePos(BlockPos(0, 30, 0))
		// мир тестов (run/world) сохраняется между запусками: над бассейном — только воздух
		for (i in -2..2) for (k in -2..2) {
			helper.level.setBlock(center.offset(i, -1, k), Blocks.STONE.defaultBlockState(), 3)
			helper.level.setBlock(center.offset(i, 0, k), if (i in -1..1 && k in -1..1) Blocks.WATER.defaultBlockState() else Blocks.STONE.defaultBlockState(), 3)
			for (j in 1..6) helper.level.setBlock(center.offset(i, j, k), Blocks.AIR.defaultBlockState(), 3)
		}
		val bucket = ItemStack(AlfheimItems.hyperBucket)
		player.setItemInHand(InteractionHand.MAIN_HAND, bucket)

		player.isShiftKeyDown = true
		bucket.use(helper.level, player, InteractionHand.MAIN_HAND)
		helper.assertTrue(ItemHyperBucket.getRange(bucket) == 0, "shift: range 1 → 0")
		bucket.use(helper.level, player, InteractionHand.MAIN_HAND)
		helper.assertTrue(ItemHyperBucket.getRange(bucket) == 1, "shift: range 0 → 1")
		player.isShiftKeyDown = false

		player.moveTo(center.x + 0.5, center.y + 1.0, center.z + 0.5, 0f, 90f)
		val hit = ASJUtilities.getSelectedBlock(player, player.blockReach, true)
		helper.assertTrue(hit?.blockPos == center, "the ray stops at the water source: ${hit?.blockPos?.let { helper.level.getBlockState(it) }} at ${hit?.blockPos}, expected $center")
		bucket.use(helper.level, player, InteractionHand.MAIN_HAND)
		for (i in -1..1) for (k in -1..1)
			helper.assertTrue(helper.level.getBlockState(center.offset(i, 0, k)).isAir, "water at $i $k is drained")
		helper.assertTrue(helper.level.getBlockState(center.offset(2, 0, 0)).`is`(Blocks.STONE), "blocks around are kept")

		for (i in -2..2) for (k in -2..2) for (j in -1..0) helper.level.setBlock(center.offset(i, j, k), Blocks.AIR.defaultBlockState(), 3)
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
		helper.succeed()
	}

	/** Мел: каждый тик использования — 1 маны из предмета с маной в инвентаре */
	@JvmStatic
	@GameTest(template = "empty")
	fun chalkUsesMana(helper: GameTestHelper) {
		val player = player(helper, "alfheim-chalk")
		val tablet = ItemStack(BotaniaItems.manaTablet).also { it.orCreateTag.putInt("mana", 100) }
		player.inventory.setItem(9, tablet)
		val chalk = ItemStack(AlfheimItems.chalk)
		helper.assertTrue(chalk.useDuration == 72000, "chalk is held up to an hour")
		chalk.item.onUseTick(helper.level, player, chalk, 72000)
		helper.assertTrue(tablet.tag?.getInt("mana") == 99, "chalk takes 1 mana per tick: ${tablet.tag?.getInt("mana")}")
		player.inventory.clearContent()
		helper.succeed()
	}
}
