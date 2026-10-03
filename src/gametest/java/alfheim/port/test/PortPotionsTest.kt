package alfheim.port.test

import alexsocol.asjlib.meta
import alfheim.api.ModInfo.MODID
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.*
import alfheim.common.item.material.ElvenFoodMetas.*
import alfheim.common.potion.*
import alfheim.common.potion.berries.PotionWTFBerry0
import alfheim.port.legacy.*
import alfheim.port.registry.*
import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.effect.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.*
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import vazkii.botania.common.item.BotaniaItems
import java.util.UUID

/**
 * КТ-2, партия 4: зелья автора (Potion1710 — id и методы 1.7.10) и эльфийская еда. Значения — из кода автора:
 * `AlfheimRegistry.registerPotions`, классы зелий, `ItemElvenFood`, врезки H-008 и H-009 (HOOKS.md)
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortPotionsTest {

	private fun player(helper: GameTestHelper, name: String): FakePlayer =
		FakePlayerFactory.get(helper.level, GameProfile(UUID.randomUUID(), name)).apply { removeAllEffects() }

	/** Зелье — эффект реестра под именем из `setPotionName`; id 1.7.10 — из конфига, `Potion.potionTypes[id]` — само зелье */
	@JvmStatic
	@GameTest(template = "empty")
	fun potionIds(helper: GameTestHelper) {
		helper.assertTrue(LegacyRegistration.effects.size == 43, "potions of the author: ${LegacyRegistration.effects.size}")
		val problems = ArrayList<String>()
		for ((effect, entry) in LegacyRegistration.effects) {
			val potion = effect as Potion1710
			if (BuiltInRegistries.MOB_EFFECT.getKey(effect) != entry.id) problems += "${entry.id} is registered as ${BuiltInRegistries.MOB_EFFECT.getKey(effect)}"
			if (Potion1710.potionTypes[potion.id] !== effect || Potion1710.idOf(effect) != potion.id) problems += "${entry.id}: id ${potion.id}"
			if (!potion.name.startsWith("alfheim.potion.")) problems += "${entry.id}: name ${potion.name}"
			if (!potion.hasStatusIcon() || potion.iconSheet == null) problems += "${entry.id} has no icon"
		}
		helper.assertTrue(problems.isEmpty(), problems.toString())

		fun key(effect: MobEffect) = BuiltInRegistries.MOB_EFFECT.getKey(effect)
		helper.assertTrue(key(PotionBeer) == ResourceLocation(MODID, "beer") && PotionBeer.id == AlfheimConfigHandler.potionIDBeer, "beer: ${key(PotionBeer)} ${PotionBeer.id}")
		helper.assertTrue(key(PotionWhiteWine) == ResourceLocation(MODID, "white_wine") && key(PotionWTFBerry0) == ResourceLocation(MODID, "wtf_berry0"), "white wine and barrier berry")
		helper.assertTrue(Potion1710.byId(AlfheimConfigHandler.potionIDStoneSkin)?.let { key(it) } == ResourceLocation(MODID, "stone_skin"), "stone skin")
		helper.assertTrue(!PotionBeer.isBadEffect && PotionBleeding.isBadEffect && PotionBeer.color == 0xFF8000, "beer is good, bleeding is bad")

		// id ванилы — номера 1.7.10; прочие эффекты реестра получают свободные номера, у всех разные
		helper.assertTrue(MobEffects.REGENERATION.id == 10 && Potion1710.byId(10) === MobEffects.REGENERATION, "regeneration is 10")
		helper.assertTrue(MobEffects.MOVEMENT_SPEED.id == 1 && MobEffects.HEALTH_BOOST.id == 21 && MobEffects.SATURATION.id == 23, "vanilla ids")
		val ids = BuiltInRegistries.MOB_EFFECT.map { it.id }
		helper.assertTrue(ids.all { it > 0 } && ids.toSet().size == ids.size, "every effect has its own id: $ids")
		helper.succeed()
	}

	/** Имя зелья — ключ автора `alfheim.potion.<имя>`, переименованный в ключ эффекта */
	@JvmStatic
	@GameTest(template = "empty")
	fun potionNames(helper: GameTestHelper) {
		helper.assertTrue(PotionBeer.descriptionId == "effect.alfheim.beer", "beer key ${PotionBeer.descriptionId}")
		helper.assertTrue(Component.translatable(PotionBeer.descriptionId).string == "Beer", "beer: ${Component.translatable(PotionBeer.descriptionId).string}")
		helper.assertTrue(Component.translatable(PotionButterShield.descriptionId).string == "Butterfly Shield", "butterfly shield")
		helper.assertTrue(StatCollector.translateToLocal(PotionWhiteWine.name) == "White Wine", "white wine by the key of the author: ${StatCollector.translateToLocal(PotionWhiteWine.name)}")
		helper.succeed()
	}

	/** H-008, H-009: «Танк» засчитывается как «Сопротивление» и прибавляет к нему свою силу; ещё он замедляет */
	@JvmStatic
	@GameTest(template = "empty")
	fun tankCountsAsResistance(helper: GameTestHelper) {
		val player = player(helper, "alfheim-tank")
		val speed = player.getAttribute(Attributes.MOVEMENT_SPEED)!!
		player.addEffect(MobEffectInstance(PotionTank, 200, 1))
		helper.assertTrue(player.hasEffect(MobEffects.DAMAGE_RESISTANCE), "tank is resistance")
		helper.assertTrue(player.getEffect(MobEffects.DAMAGE_RESISTANCE)?.amplifier == 1, "resistance of tank: ${player.getEffect(MobEffects.DAMAGE_RESISTANCE)?.amplifier}")
		helper.assertTrue(speed.getModifier(PotionTank.uuid)?.amount == -0.2, "tank slows down")

		player.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0))
		helper.assertTrue(player.getEffect(MobEffects.DAMAGE_RESISTANCE)?.amplifier == 1, "resistance 0 + tank 1")

		player.removeAllEffects()
		helper.assertTrue(!player.hasEffect(MobEffects.DAMAGE_RESISTANCE) && speed.getModifier(PotionTank.uuid) == null, "tank is removed")
		helper.succeed()
	}

	/** Пиво: здоровье +3/5/8/13/21 и столько же лечит; берсерк — −20 % здоровья; камень (берсерк, ниндзя, танк…) один */
	@JvmStatic
	@GameTest(template = "empty")
	fun attributePotions(helper: GameTestHelper) {
		val player = player(helper, "alfheim-beer")
		player.health = 10f
		player.addEffect(MobEffectInstance(PotionBeer, 200, 0))
		helper.assertTrue(player.maxHealth == 23f && player.health == 13f, "beer 0: ${player.health}/${player.maxHealth}")
		player.removeAllEffects()
		player.addEffect(MobEffectInstance(PotionBeer, 200, 4))
		helper.assertTrue(player.maxHealth == 41f, "beer 4: ${player.maxHealth}")
		player.removeAllEffects()
		helper.assertTrue(player.maxHealth == 20f, "beer is removed: ${player.maxHealth}")

		player.health = 20f
		player.addEffect(MobEffectInstance(PotionBerserk, 200, 0))
		helper.assertTrue(player.maxHealth == 16f && player.health == 16f, "berserk: ${player.health}/${player.maxHealth}")
		player.addEffect(MobEffectInstance(PotionNinja, 200, 0))
		helper.assertTrue(player.hasEffect(PotionNinja) && !player.hasEffect(PotionBerserk), "only one stone effect")
		helper.assertTrue(player.maxHealth == 20f, "berserk is removed: ${player.maxHealth}")
		player.removeAllEffects()
		helper.succeed()
	}

	/** Шампанское снимает вредные эффекты; белое вино сажает игрока верхом на того, по кому он щёлкнул */
	@JvmStatic
	@GameTest(template = "empty")
	fun drinkPotions(helper: GameTestHelper) {
		val player = player(helper, "alfheim-champagne")
		player.addEffect(MobEffectInstance(MobEffects.POISON, 200))
		player.addEffect(MobEffectInstance(MobEffects.DIG_SPEED, 200))
		player.addEffect(MobEffectInstance(PotionChampagne, 200))
		PotionChampagne.applyEffectTick(player, 0)
		helper.assertTrue(!player.hasEffect(MobEffects.POISON) && player.hasEffect(MobEffects.DIG_SPEED), "champagne removes only harmful effects")
		player.removeAllEffects()

		val pig = helper.spawn(EntityType.PIG, BlockPos(1, 2, 1))
		val event = PlayerInteractEvent.EntityInteract(player, InteractionHand.MAIN_HAND, pig)
		MinecraftForge.EVENT_BUS.post(event)
		helper.assertTrue(player.vehicle == null, "no ride without white wine")
		player.addEffect(MobEffectInstance(PotionWhiteWine, 200))
		MinecraftForge.EVENT_BUS.post(PlayerInteractEvent.EntityInteract(player, InteractionHand.MAIN_HAND, pig))
		helper.assertTrue(player.vehicle === pig, "white wine: ride the pig")
		player.stopRiding()
		player.removeAllEffects()
		helper.succeed()
	}

	/** Каждое зелье автора накладывается, действует 40 тиков и снимается без ошибок (механики других КТ — закомментированы) */
	@JvmStatic
	@GameTest(template = "empty")
	fun everyPotionTicks(helper: GameTestHelper) {
		for (effect in LegacyRegistration.effects.keys) {
			val player = player(helper, "alfheim-${(effect as Potion1710).id}")
			player.setPos(helper.absoluteVec(net.minecraft.world.phys.Vec3(1.5, 2.0, 1.5)))
			player.addEffect(MobEffectInstance(effect, 40, 1))
			val instance = player.getEffect(effect)!!
			while (instance.tick(player) {}) Unit
			player.removeAllEffects()
			helper.assertTrue(!player.hasEffect(effect), "${BuiltInRegistries.MOB_EFFECT.getKey(effect)} is not removed")
		}
		helper.succeed()
	}

	/** Еда — 18 предметов под именами вариантов; имя, стопка, время и вид еды — из `ItemElvenFood` */
	@JvmStatic
	@GameTest(template = "empty")
	fun elvenFoodItems(helper: GameTestHelper) {
		helper.assertTrue(AlfheimItems.elvenFood.size == 18, "food: ${AlfheimItems.elvenFood.size}")
		for (type in ElvenFoodMetas.entries) {
			val stack = type.stack
			val id = ResourceLocation(MODID, AlfheimRegisters.snakeCase(type.name))
			helper.assertTrue(BuiltInRegistries.ITEM.getKey(stack.item) == id && stack.meta == type.I, "$type is ${BuiltInRegistries.ITEM.getKey(stack.item)}")
			val drink = type in listOf(RedWine, WhiteWine, Champagne, Beer, JellyBottle)
			helper.assertTrue(stack.maxStackSize == if (drink) 1 else 64, "$type stack size ${stack.maxStackSize}")
			helper.assertTrue(stack.useAnimation == if (drink) UseAnim.DRINK else UseAnim.EAT, "$type use animation")
			val duration = when (type) {
				DreamCherry, Nectar -> 8
				Lembas              -> 64
				else                -> 32
			}
			helper.assertTrue(stack.useDuration == duration, "$type use duration ${stack.useDuration}")
		}
		helper.assertTrue(Lembas.stack.hoverName.string == "Elven Lembas" && Beer.stack.hoverName.string == "Beer", "names: ${Lembas.stack.hoverName.string}")

		val cc = Beer.stack.setHoverName(Component.literal(" cerveza cristal "))
		helper.assertTrue(ItemElvenFood.isCC(cc) && !ItemElvenFood.isCC(Beer.stack), "Cerveza Cristal")
		helper.succeed()
	}

	/** Сытость и насыщение — `func_150905_g`, `func_150906_h` автора; вино оставляет кувшин, желе — колбу */
	@JvmStatic
	@GameTest(template = "empty")
	fun elvenFoodEating(helper: GameTestHelper) {
		val player = player(helper, "alfheim-food")
		fun eat(type: ElvenFoodMetas, count: Int = 1): ItemStack {
			player.foodData.foodLevel = 0
			player.foodData.setSaturation(0f)
			val stack = type.stack(count)
			return stack.item.finishUsingItem(stack, helper.level, player)
		}

		val lembas = eat(Lembas, 2)
		helper.assertTrue(player.foodData.foodLevel == 20 && player.foodData.saturationLevel == 20f, "lembas: ${player.foodData.foodLevel} ${player.foodData.saturationLevel}")
		helper.assertTrue(lembas.item == AlfheimItems.elvenFood[Lembas.I] && lembas.count == 1, "lembas is eaten: $lembas")
		eat(RedGrapes)
		helper.assertTrue(player.foodData.foodLevel == 2 && player.foodData.saturationLevel == 2 * 0.3f * 2f, "grapes: ${player.foodData.foodLevel} ${player.foodData.saturationLevel}")
		eat(JellyCod)
		helper.assertTrue(player.foodData.foodLevel == 9 && player.foodData.saturationLevel == 9f, "jelly cod: ${player.foodData.foodLevel} ${player.foodData.saturationLevel}")

		val jug = eat(RedWine)
		helper.assertTrue(jug.item == ElvenResourcesMetas.Jug.stack.item && jug.count == 1, "red wine leaves a jug: $jug")
		helper.assertTrue(player.getEffect(MobEffects.REGENERATION)?.let { it.duration == 2400 && it.amplifier == 0 } == true, "red wine: regeneration")
		eat(RedWine)
		helper.assertTrue(player.getEffect(MobEffects.REGENERATION)?.amplifier == 1, "red wine again: regeneration II")
		val flask = eat(JellyBottle)
		helper.assertTrue(flask.item == BotaniaItems.flask, "jelly leaves a flask: $flask")

		eat(Beer)
		helper.assertTrue(player.getEffect(PotionBeer)?.duration == 2400, "beer")
		eat(TreeBerryBarrier)
		helper.assertTrue(player.getEffect(PotionWTFBerry0)?.let { it.duration == 7200 && it.amplifier == 0 && it.curativeItems.isEmpty() } == true, "barrier berry")
		player.removeAllEffects()

		// пёстрый плод: случайное зелье силы III на 6 минут; мгновенное не накладывается
		repeat(16) {
			eat(TreeBerryCalico)
			val effects = player.activeEffects.toList()
			helper.assertTrue(effects.size <= 1 && effects.all { it.duration == 7200 && it.amplifier == 2 && !it.effect.isInstantenous && it.curativeItems.isEmpty() }, "calico berry: $effects")
			player.removeAllEffects()
		}
		helper.succeed()
	}

	/** Сытый игрок не ест лембас, но пьёт вино и ест плоды деревьев (`isAlwaysEdible`) */
	@JvmStatic
	@GameTest(template = "empty")
	fun elvenFoodUse(helper: GameTestHelper) {
		val player = player(helper, "alfheim-full")
		player.foodData.foodLevel = 20
		fun use(type: ElvenFoodMetas): InteractionResult {
			player.stopUsingItem()
			player.setItemInHand(InteractionHand.MAIN_HAND, type.stack)
			return player.mainHandItem.use(helper.level, player, InteractionHand.MAIN_HAND).result
		}
		helper.assertTrue(use(Lembas) == InteractionResult.FAIL && !player.isUsingItem, "full player does not eat lembas")
		helper.assertTrue(use(WhiteWine) == InteractionResult.CONSUME && player.isUsingItem, "full player drinks wine")
		helper.assertTrue(use(TreeBerrySealing) == InteractionResult.CONSUME, "full player eats tree berries")
		player.foodData.foodLevel = 10
		helper.assertTrue(use(Lembas) == InteractionResult.CONSUME, "hungry player eats lembas")
		player.stopUsingItem()
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
		helper.succeed()
	}
}
