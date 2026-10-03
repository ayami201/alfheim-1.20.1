package alfheim.port.test

import alexsocol.asjlib.*
import alfheim.api.ModInfo.MODID
import alfheim.common.core.util.AlfheimTab
import alfheim.common.entity.EntityThrowableItem
import alfheim.common.entity.EntityThrownPotion
import alfheim.common.item.AlfheimItems
import alfheim.common.item.ItemSplashPotion
import alfheim.port.legacy.Item1710
import alfheim.port.registry.LegacyIds
import alfheim.port.registry.LegacyRegistration
import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Pig
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.*
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.common.brew.BotaniaBrews
import java.util.UUID
import kotlin.math.*

/**
 * КТ-2, партия 6а: огненная граната и брызгающее зелье — предметы, летящие существа, бросок и удар. Значения — из
 * кода автора
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortThrowablesTest {

	private fun player(helper: GameTestHelper, name: String): FakePlayer =
		FakePlayerFactory.get(helper.level, GameProfile(UUID.randomUUID(), name)).apply { removeAllEffects(); inventory.clearContent() }

	/**
	 * Каменный пол 9 × 9 на высоте [dy] над точкой теста, над ним — воздух (мир тестов сохраняется между запусками);
	 * возвращает клетку над серединой пола. Высота у каждого теста своя и выше построек других тестов: тесты идут рядом
	 * и одновременно
	 */
	private fun floor(helper: GameTestHelper, dy: Int): BlockPos {
		val center = helper.absolutePos(BlockPos(0, dy, 0))
		for (i in -4..4) for (k in -4..4) {
			helper.level.setBlock(center.offset(i, -1, k), Blocks.STONE.defaultBlockState(), 3)
			for (j in 0..4) helper.level.setBlock(center.offset(i, j, k), Blocks.AIR.defaultBlockState(), 3)
		}
		return center
	}

	private fun clear(helper: GameTestHelper, center: BlockPos, vararg entities: Entity) {
		entities.forEach { it.discard() }
		for (i in -4..4) for (k in -4..4) for (j in -1..4) helper.level.setBlock(center.offset(i, j, k), Blocks.AIR.defaultBlockState(), 3)
	}

	/** Свинья в точке [x], [y], [z] мира с запасом здоровья [health] */
	private fun pig(helper: GameTestHelper, x: Double, y: Double, z: Double, health: Float = 10f): Pig =
		EntityType.PIG.create(helper.level)!!.apply {
			moveTo(x, y, z, 0f, 0f)
			setNoAi(true)
			this.health = health
			helper.level.addFreshEntity(this)
		}

	private fun splashPotion(brew: vazkii.botania.api.brew.Brew) = (AlfheimItems.splashPotion as ItemSplashPotion).getItemForBrew(brew, null)

	/** id и имена предметов и существ, имена 1.7.10 в legacy_ids.json, слежение существ — как `registerModEntity(…, 128, 1, true)` */
	@JvmStatic
	@GameTest(template = "empty")
	fun throwableRegistry(helper: GameTestHelper) {
		helper.assertTrue(BuiltInRegistries.ITEM.getKey(AlfheimItems.fireGrenade) == ResourceLocation(MODID, "fire_grenade"), "fire grenade id")
		helper.assertTrue(BuiltInRegistries.ITEM.getKey(AlfheimItems.splashPotion) == ResourceLocation(MODID, "splash_potion"), "splash potion id")
		helper.assertTrue(ItemStack(AlfheimItems.fireGrenade).hoverName.string == "Fire Grenade" && ItemStack(AlfheimItems.splashPotion).hoverName.string == "Splash Vial", "item names")
		helper.assertTrue(ItemStack(AlfheimItems.fireGrenade).maxStackSize == 64 && ItemStack(AlfheimItems.splashPotion).maxStackSize == 1, "stack sizes")

		val types = mapOf(EntityThrowableItem::class.java to "thrown_item", EntityThrownPotion::class.java to "thrown_potion")
		for ((clazz, id) in types) {
			val type = LegacyRegistration.entityType(clazz)
			helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.getKey(type) == ResourceLocation(MODID, id), "$id is ${BuiltInRegistries.ENTITY_TYPE.getKey(type)}")
			helper.assertTrue(type.clientTrackingRange() * 16 == 128 && type.updateInterval() == 1 && type.trackDeltas(), "$id tracking")
		}
		helper.assertTrue(LegacyIds.entities["$MODID:ThrownItem"]?.get("*")?.id == ResourceLocation(MODID, "thrown_item"), "legacy id of ThrownItem")
		helper.assertTrue(LegacyIds.entities["$MODID:ThrownPotion"]?.get("*")?.id == ResourceLocation(MODID, "thrown_potion"), "legacy id of ThrownPotion")
		helper.assertTrue(Component.translatable(LegacyRegistration.entityType(EntityThrownPotion::class.java).descriptionId).string == "Thrown Potion", "entity name")
		helper.succeed()
	}

	/** Бросок гранаты: из глаз на 0,16 вбок и 0,1 вниз, на 10° выше взгляда, скорость 1; граната 0,25 × 0,25, падает на 0,03 за тик */
	@JvmStatic
	@GameTest(template = "empty")
	fun fireGrenadeThrow(helper: GameTestHelper) {
		val center = floor(helper, 80)
		val player = player(helper, "alfheim-grenade-throw")
		player.moveTo(center.x + 0.5, center.y.D, center.z + 0.5, 0f, 0f)
		val stack = ItemStack(AlfheimItems.fireGrenade, 2)
		player.setItemInHand(InteractionHand.MAIN_HAND, stack)

		helper.assertTrue(stack.use(helper.level, player, InteractionHand.MAIN_HAND).result == InteractionResult.CONSUME && stack.count == 1, "one grenade is thrown")
		val thrown = helper.level.getEntitiesOfClass(EntityThrowableItem::class.java, AABB(center).inflate(3.0))
		helper.assertTrue(thrown.size == 1, "grenades in the air: ${thrown.size}")
		val grenade = thrown[0]
		helper.assertTrue(grenade.thrower === player, "thrower")
		helper.assertTrue(grenade.position().distanceTo(Vec3(player.x - 0.16, player.eyeY - 0.1, player.z)) < 1e-6, "start ${grenade.position()}")
		val motion = grenade.deltaMovement
		helper.assertTrue(abs(motion.x) < 0.05 && abs(motion.y - sin(10.0 / 180 * PI)) < 0.05 && abs(motion.z - cos(10.0 / 180 * PI)) < 0.05, "motion $motion")
		helper.assertTrue(grenade.bbWidth == 0.25f && grenade.bbHeight == 0.25f && grenade.getGravityVelocity() == 0.03f, "size and gravity")
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
		clear(helper, center, grenade)
		helper.succeed()
	}

	/**
	 * Удар гранаты: существа ближе 4 блоков — 3 урона огненным шаром от бросившего и огонь на 10 секунд; огонь в точке удара
	 * и рядом; граната исчезает. Граната без бросившего (из раздатчика) при ударе не делает ничего — так у автора (BUGS.md)
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun fireGrenadeImpact(helper: GameTestHelper) {
		val center = floor(helper, 90)
		val player = player(helper, "alfheim-grenade-impact")
		player.moveTo(center.x + 0.5, center.y + 20.0, center.z + 0.5, 0f, 0f)
		val x = center.x + 0.5
		val y = center.y.D
		val z = center.z + 0.5
		val near = pig(helper, x + 2, y, z)
		val far = pig(helper, x, y, z + 4)

		val idle = EntityThrowableItem(helper.level).apply { moveTo(x, y, z, 0f, 0f); helper.level.addFreshEntity(this) }
		idle.onImpact(BlockHitResult(Vec3(x, y, z), Direction.UP, center.below(), false))
		helper.assertTrue(!idle.isRemoved && near.health == 10f && helper.level.getBlockState(center).isAir, "a grenade without a thrower does nothing")

		val grenade = EntityThrowableItem(player).apply { moveTo(x, y, z, 0f, 0f); helper.level.addFreshEntity(this) }
		grenade.onImpact(BlockHitResult(Vec3(x, y, z), Direction.UP, center.below(), false))
		helper.assertTrue(grenade.isRemoved, "the grenade is gone")
		helper.assertTrue(near.health == 7f && near.remainingFireTicks == 200, "near pig: ${near.health} health, ${near.remainingFireTicks} fire ticks")
		helper.assertTrue(near.lastDamageSource?.let { it.`is`(net.minecraft.world.damagesource.DamageTypes.FIREBALL) && it.entity === player && it.directEntity === grenade } == true, "fireball damage from the thrower: ${near.lastDamageSource}")
		helper.assertTrue(far.health == 10f && far.remainingFireTicks <= 0, "a pig 4 blocks away is not hit")
		helper.assertTrue(helper.level.getBlockState(center).`is`(Blocks.FIRE), "fire at the impact point")

		clear(helper, center, idle, near, far)
		helper.succeed()
	}

	/** Вкладка: по склянке на каждое варево Botania, кроме запасного; цвет склянки, подсказка, мана */
	@JvmStatic
	@GameTest(template = "empty")
	fun splashPotionItem(helper: GameTestHelper) {
		CreativeModeTabs.tryRebuildTabContents(helper.level.enabledFeatures(), true, helper.level.registryAccess())
		val registry = BotaniaAPI.instance().brewRegistry!!
		val inTab = AlfheimTab.tab.get().displayItems.filter { it.item == AlfheimItems.splashPotion }.map { ItemNBTHelper.getString(it, "brewKey", "") }
		val brews = registry.filter { it !== BotaniaBrews.fallbackBrew }.map { registry.getKey(it).toString() }
		helper.assertTrue(inTab == brews && "botania:healing" in inTab, "splash potions in the tab: $inTab")

		val item = AlfheimItems.splashPotion as ItemSplashPotion
		val healing = splashPotion(BotaniaBrews.healing)
		helper.assertTrue(item.getBrew(healing) === BotaniaBrews.healing && item.getBrew(ItemStack(item)) === BotaniaBrews.fallbackBrew, "brew of a stack")
		helper.assertTrue((item as Item1710).getColorFromItemStack(healing, 0) == 0xCCCCCFF, "vial color")
		helper.assertTrue(item.getManaCost(BotaniaBrews.healing, healing) == (BotaniaBrews.healing.manaCost * 1.5).toInt() && item.getManaCost(null, healing) == 400, "mana cost")

		val lines = ArrayList<Component>()
		item.appendHoverText(healing, helper.level, lines, TooltipFlag.NORMAL)
		val heal = BotaniaBrews.healing.getPotionEffects(healing).single()
		val roman = if (heal.amplifier == 0) "" else " " + Component.translatable("botania.roman${heal.amplifier + 1}").string
		helper.assertTrue(heal.effect == MobEffects.HEAL && lines.map { it.string } == listOf("§5Brew of Mending", "§7Instant Health$roman§7"), "healing tooltip: ${lines.map { it.string }}")
		lines.clear()
		item.appendHoverText(splashPotion(BotaniaBrews.speed), helper.level, lines, TooltipFlag.NORMAL)
		val speed = BotaniaBrews.speed.getPotionEffects(ItemStack(item))[0]
		helper.assertTrue(lines.size == 2 && lines[1].string.endsWith("§7 (${net.minecraft.util.StringUtil.formatTickDuration(speed.duration)})"), "speed tooltip: ${lines.map { it.string }}")
		helper.succeed()
	}

	/** Бросок склянки: летящее зелье с воздействиями и цветом варева; склянка тратится; падает на 0,1 за тик */
	@JvmStatic
	@GameTest(template = "empty")
	fun splashPotionThrow(helper: GameTestHelper) {
		val center = floor(helper, 100)
		val player = player(helper, "alfheim-potion-throw")
		player.moveTo(center.x + 0.5, center.y.D, center.z + 0.5, 90f, 0f)
		val stack = splashPotion(BotaniaBrews.healing)
		val color = (AlfheimItems.splashPotion as ItemSplashPotion).getColor(stack)
		player.setItemInHand(InteractionHand.MAIN_HAND, stack)

		helper.assertTrue(stack.use(helper.level, player, InteractionHand.MAIN_HAND).result == InteractionResult.CONSUME && stack.isEmpty, "the vial is thrown")
		val thrown = helper.level.getEntitiesOfClass(EntityThrownPotion::class.java, AABB(center).inflate(3.0))
		helper.assertTrue(thrown.size == 1, "potions in the air: ${thrown.size}")
		val potion = thrown[0]
		helper.assertTrue(potion.thrower === player && potion.effects == BotaniaBrews.healing.getPotionEffects(stack), "thrower and effects")
		helper.assertTrue(potion.color == color, "color ${potion.color}, expected $color")
		val motion = potion.deltaMovement
		// взгляд на запад (yaw 90): по x — минус
		helper.assertTrue(abs(motion.x + cos(10.0 / 180 * PI)) < 0.05 && abs(motion.y - sin(10.0 / 180 * PI)) < 0.05 && abs(motion.z) < 0.05, "motion $motion")
		helper.assertTrue(potion.getGravityVelocity() == 0.1f, "gravity")
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
		clear(helper, center, potion)
		helper.succeed()
	}

	/**
	 * Удар зелья: существа ближе 4 блоков получают воздействия с силой `1 − расстояние / 4`, задетое прямо — полной;
	 * мгновенные (лечение: `4 << уровень` × сила) — сразу, прочие — с длительностью × сила, если она больше секунды
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun thrownPotionImpact(helper: GameTestHelper) {
		val center = floor(helper, 110)
		val player = player(helper, "alfheim-potion-impact")
		player.moveTo(center.x + 0.5, center.y + 20.0, center.z + 0.5, 0f, 0f)
		val x = center.x + 0.5
		val y = center.y.D
		val z = center.z + 0.5

		val direct = pig(helper, x, y, z, 1f)
		val near = pig(helper, x + 2, y, z, 1f)
		val far = pig(helper, x, y, z + 4, 1f)
		val healing = EntityThrownPotion(player, splashPotion(BotaniaBrews.healing)).apply { moveTo(x, y, z, 0f, 0f); helper.level.addFreshEntity(this) }
		val heal = 4 shl healing.effects.single { it.effect == MobEffects.HEAL }.amplifier
		healing.onImpact(EntityHitResult(direct))
		helper.assertTrue(healing.isRemoved, "the potion is gone")
		val expected = listOf(min(10, 1 + heal), min(10, 1 + (0.5 * heal + 0.5).toInt()), 1).map { it.toFloat() }
		helper.assertTrue(listOf(direct.health, near.health, far.health) == expected, "healing: direct ${direct.health}, 2 blocks ${near.health}, 4 blocks ${far.health}; expected $expected")

		val speedStack = splashPotion(BotaniaBrews.speed)
		val effect = BotaniaBrews.speed.getPotionEffects(speedStack)[0]
		val speed = EntityThrownPotion(player, speedStack).apply { moveTo(x, y, z, 0f, 0f); helper.level.addFreshEntity(this) }
		speed.onImpact(BlockHitResult(Vec3(x, y, z), Direction.UP, center.below(), false))
		helper.assertTrue(direct.getEffect(effect.effect)?.let { it.duration == effect.duration && it.amplifier == effect.amplifier } == true, "speed at the impact point: ${direct.getEffect(effect.effect)}")
		helper.assertTrue(near.getEffect(effect.effect)?.let { it.duration == (0.5 * effect.duration + 0.5).toInt() && it.amplifier == effect.amplifier } == true, "speed 2 blocks away: ${near.getEffect(effect.effect)}")
		helper.assertTrue(far.getEffect(effect.effect) == null, "no speed 4 blocks away")

		val empty = EntityThrownPotion(helper.level, ItemStack(AlfheimItems.splashPotion)).apply { moveTo(x, y, z, 0f, 0f); helper.level.addFreshEntity(this) }
		helper.assertTrue(empty.effects.isEmpty(), "the fallback brew has no effects")
		far.removeAllEffects()
		empty.onImpact(EntityHitResult(far))
		helper.assertTrue(empty.isRemoved && far.activeEffects.isEmpty(), "a potion without effects only breaks")

		clear(helper, center, direct, near, far)
		helper.succeed()
	}
}
