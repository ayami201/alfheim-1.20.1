package alfheim.port.test

import alexsocol.asjlib.meta
import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.BlockElvenOre
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.*
import alfheim.common.item.material.ElvenResourcesMetas.*
import alfheim.port.legacy.botania.Botania
import alfheim.port.registry.*
import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.*
import net.minecraft.world.effect.*
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.SoundType
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import vazkii.botania.common.item.BotaniaItems
import java.util.UUID

/**
 * КТ-2, партия 3: материалы — эльфийские ресурсы (`ElvenItems`), ресурсы событий, увядший лотос — и эльфийская руда.
 * Значения — из кода автора: `ItemElvenResource`, `ItemEventResource`, `ItemWiltedLotus`, `BlockElvenOre`
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortMaterialsTest {

	/** Варианты metadata — отдельные предметы и блоки под именами варианта у автора (SPEC, Р-5) */
	@JvmStatic
	@GameTest(template = "empty")
	fun thirdBatchIds(helper: GameTestHelper) {
		val items = ElvenResourcesMetas.entries.map { AlfheimRegisters.snakeCase(it.name) } + listOf("snow_relic", "volcano_relic", "lava_melon", "wilted_lotus0", "wilted_lotus1")
		helper.assertTrue(items.size == 47, "items: ${items.size}")
		for (id in items) helper.assertTrue(BuiltInRegistries.ITEM.containsKey(ResourceLocation(MODID, id)), "$MODID:$id is not registered")
		for (meta in 0..5) helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(ResourceLocation(MODID, "elven_ore$meta")), "$MODID:elven_ore$meta is not registered")
		helper.assertTrue(BuiltInRegistries.ITEM.getKey(AlfheimItems.elvenResource[ElvoriumIngot.I]) == ResourceLocation(MODID, "elvorium_ingot"), "ElvenItems 3 is elvorium_ingot")
		helper.assertTrue(ItemStack(AlfheimItems.elvenResource[ElvoriumIngot.I]).meta == ElvoriumIngot.I && ItemStack(AlfheimBlocks.elvenOre[4]).meta == 4, "ItemStack.meta is the variant")

		val problems = ArrayList<String>()
		for ((item, entry) in LegacyRegistration.items) {
			if (item is BlockItem) continue
			if (BuiltInRegistries.ITEM.getKey(item) != entry.id) problems += "${entry.id} is registered as ${BuiltInRegistries.ITEM.getKey(item)}"
			val target = LegacyIds.item("$MODID:${entry.oldName}", entry.oldMeta ?: 0)
			if (target?.id != entry.id) problems += "legacy_ids.json: $MODID:${entry.oldName}:${entry.oldMeta} -> ${target?.id}, expected ${entry.id}"
		}
		helper.assertTrue(problems.isEmpty(), problems.toString())
		val materials = LegacyRegistration.items.keys.count { it is ItemElvenResource || it is ItemEventResource || it is ItemWiltedLotus }
		helper.assertTrue(materials == 47, "materials of the author: $materials")
		helper.succeed()
	}

	/**
	 * Имя — перевод ключа автора: ключ вещи без NBT переименован в ключ 1.20.1, ключ, который автор собирает по NBT, —
	 * его собственный; подсказки — строки автора
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun materialNames(helper: GameTestHelper) {
		fun name(stack: ItemStack) = stack.hoverName.string
		val elvorium = ItemStack(AlfheimItems.elvenResource[ElvoriumIngot.I])
		helper.assertTrue(elvorium.descriptionId == "item.alfheim.elvorium_ingot", "elvorium key ${elvorium.descriptionId}")
		helper.assertTrue(name(elvorium) == "Elvorium Ingot", "elvorium: ${name(elvorium)}")
		helper.assertTrue(name(ItemStack(AlfheimItems.wiltedLotus[1])) == "Deathly Lotus", "wilted lotus 1: ${name(ItemStack(AlfheimItems.wiltedLotus[1]))}")
		helper.assertTrue(name(ItemStack(AlfheimItems.eventResource[EventResourcesMetas.SnowRelic])) == "Relic of the Blizzard", "snow relic")
		helper.assertTrue(name(ItemStack(AlfheimBlocks.elvenOre[5])) == "Lapis Lazuli Ore", "elven ore 5: ${name(ItemStack(AlfheimBlocks.elvenOre[5]))}")

		val fire = ElementalSlimeBall.stack
		fire.getOrCreateTag().putString(ItemElvenResource.TAG_ELEMENT, "FIRE")
		helper.assertTrue(name(fire) == "Fire Slimeball" && name(ElementalSlimeBall.stack) == "Elemental Slimeball", "slime balls: ${name(fire)}, ${name(ElementalSlimeBall.stack)}")

		fun tooltip(stack: ItemStack) = ArrayList<Component>().also { stack.item.appendHoverText(stack, null, it, TooltipFlag.NORMAL) }.map { it.string }
		helper.assertTrue(tooltip(DomainKey.stack) == listOf("Creative"), "domain key: ${tooltip(DomainKey.stack)}")
		helper.assertTrue(tooltip(ItemStack(AlfheimItems.wiltedLotus[0])) == listOf("It emits discordant magical vibes"), "wilted lotus: ${tooltip(ItemStack(AlfheimItems.wiltedLotus[0]))}")
		helper.succeed()
	}

	/** Размер стопки, блеск, топливо — `getItemStackLimit`, `hasEffect`, `getBurnTime` автора */
	@JvmStatic
	@GameTest(template = "empty")
	fun materialProperties(helper: GameTestHelper) {
		for (type in ElvenResourcesMetas.entries) {
			val stack = type.stack
			val single = type == WisdomBottle || type == DomainKey || type == Stencil
			helper.assertTrue(stack.maxStackSize == if (single) 1 else 64, "$type stack size ${stack.maxStackSize}")
			helper.assertTrue(stack.hasFoil() == (type == WisdomBottle || type == YggFruit), "$type glint")
			val burn = when (type) {
				InfusedDreamwoodTwig, ThunderwoodTwig     -> 600
				NetherwoodTwig                            -> 4000
				MuspelheimEssence                         -> 12800
				NetherwoodSplinters, ThunderwoodSplinters -> 100
				NetherwoodCoal                            -> 2400
				else                                      -> 0
			}
			helper.assertTrue(ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) == burn, "$type burns ${ForgeHooks.getBurnTime(stack, RecipeType.SMELTING)}, expected $burn")
		}
		helper.assertTrue(!ItemStack(AlfheimItems.wiltedLotus[0]).hasFoil() && ItemStack(AlfheimItems.wiltedLotus[1]).hasFoil(), "only the deathly lotus glints")
		helper.assertTrue(!ElvoriumIngot.stack.isDamageableItem, "materials have no durability")
		// ItemStack.meta — вариант; Botania.proxy.worldElapsedTicks на сервере — время основного мира
		helper.assertTrue(Botania.proxy.worldElapsedTicks == helper.level.server.overworld().gameTime, "worldElapsedTicks on the server")
		helper.succeed()
	}

	/** Эльфийская трава даёт 5 эффектов на 30 секунд; плод Иггдрасиля лечит, кормит и снимает вредные эффекты */
	@JvmStatic
	@GameTest(template = "empty")
	fun usableMaterials(helper: GameTestHelper) {
		val player = FakePlayerFactory.get(helper.level, GameProfile(UUID.randomUUID(), "alfheim-materials"))

		val weed = ElvenWeed.stack
		helper.assertTrue(weed.useDuration == 40 && weed.useAnimation == UseAnim.BOW, "elven weed use")
		weed.item.finishUsingItem(weed, helper.level, player)
		for (effect in listOf(MobEffects.MOVEMENT_SPEED, MobEffects.REGENERATION, MobEffects.JUMP, MobEffects.HUNGER, MobEffects.CONFUSION))
			helper.assertTrue(player.getEffect(effect)?.duration == 600, "elven weed: ${BuiltInRegistries.MOB_EFFECT.getKey(effect)}")
		helper.assertTrue(weed.isEmpty, "elven weed is used up")

		player.removeAllEffects()
		player.health = 1f
		player.foodData.foodLevel = 2
		player.addEffect(MobEffectInstance(MobEffects.POISON, 200))
		player.addEffect(MobEffectInstance(MobEffects.DIG_SPEED, 200))
		val fruit = YggFruit.stack
		helper.assertTrue(fruit.useAnimation == UseAnim.EAT, "ygg fruit use")
		fruit.item.finishUsingItem(fruit, helper.level, player)
		helper.assertTrue(player.health == player.maxHealth, "ygg fruit heals: ${player.health}")
		helper.assertTrue(player.foodData.foodLevel == 20, "ygg fruit feeds: ${player.foodData.foodLevel}")
		helper.assertTrue(!player.hasEffect(MobEffects.POISON) && player.hasEffect(MobEffects.DIG_SPEED), "ygg fruit removes only harmful effects")
		helper.assertTrue(fruit.isEmpty, "ygg fruit is used up")

		// Das Rheingold запоминает ник игрока, по которому ударили
		val gold = DasRheingold.stack
		helper.assertTrue(gold.item.onLeftClickEntity(gold, player, player) && gold.tag?.getString("nick") == "alfheim-materials", "Das Rheingold nick")
		helper.succeed()
	}

	/**
	 * `BlockElvenOre`: предмет — `getItemDropped`, без удачи — один, с удачей — `quantityDropped` автора, с шёлковым
	 * касанием — руда; опыт 3–7, если руда роняет не себя, и без шёлкового касания
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun elvenOreDrops(helper: GameTestHelper) {
		val pos = helper.absolutePos(BlockPos(0, 1, 0))
		val pickaxe = ItemStack(Items.IRON_PICKAXE)
		val fortune = ItemStack(Items.IRON_PICKAXE).apply { enchant(Enchantments.BLOCK_FORTUNE, 3) }
		val silk = ItemStack(Items.IRON_PICKAXE).apply { enchant(Enchantments.SILK_TOUCH, 1) }
		val drops = listOf(BotaniaItems.dragonstone, AlfheimBlocks.elvenOre[1].asItem(), BotaniaItems.elfQuartz, AlfheimBlocks.elvenOre[3].asItem(), IffesalDust.stack.item, Items.LAPIS_LAZULI)

		for (meta in 0..5) {
			val ore = AlfheimBlocks.elvenOre[meta] as BlockElvenOre
			val state = ore.defaultBlockState()
			fun drop(tool: ItemStack) = net.minecraft.world.level.block.Block.getDrops(state, helper.level, pos, null, null, tool)

			val plain = drop(pickaxe)
			helper.assertTrue(plain.size == 1 && plain[0].item == drops[meta] && plain[0].count == 1, "elven ore $meta drops $plain")
			val silky = drop(silk)
			helper.assertTrue(silky.size == 1 && silky[0].item == ore.asItem() && silky[0].count == 1, "elven ore $meta with silk touch drops $silky")

			// quantityDropped: 0, 2 — 1..4 с удачей III, 5 — (1..4) × (4..8), прочие — 1
			val range = when (meta) {
				0, 2 -> 1..4
				5    -> 4..32
				else -> 1..1
			}
			val counts = (1..64).map { drop(fortune).single().count }
			helper.assertTrue(counts.all { it in range }, "elven ore $meta with fortune III: ${counts.toSortedSet()}, expected $range")
			if (meta == 5) helper.assertTrue(counts.any { it > 8 }, "lapis with fortune is multiplied: ${counts.toSortedSet()}")

			val xp = (1..32).map { state.getExpDrop(helper.level, helper.level.random, pos, 0, 0) }
			val xpRange = if (meta == 1 || meta == 3) 0..0 else 3..7
			helper.assertTrue(xp.all { it in xpRange }, "elven ore $meta xp ${xp.toSortedSet()}, expected $xpRange")
			helper.assertTrue(state.getExpDrop(helper.level, helper.level.random, pos, 0, 1) == 0, "elven ore $meta gives no xp with silk touch")
		}
		helper.succeed()
	}

	/** Твёрдость 2, внутренняя взрывоустойчивость 30, камень; железная кирка, элементиевая руда — каменная */
	@JvmStatic
	@GameTest(template = "empty")
	fun elvenOreHarvest(helper: GameTestHelper) {
		for ((meta, ore) in AlfheimBlocks.elvenOre.withIndex()) {
			val state = ore.defaultBlockState()
			helper.assertTrue(state.getDestroySpeed(helper.level, BlockPos.ZERO) == 2f && ore.explosionResistance == 30f / 5f && state.soundType == SoundType.STONE, "elven ore $meta properties")
			helper.assertTrue(state.requiresCorrectToolForDrops() && state.`is`(BlockTags.MINEABLE_WITH_PICKAXE), "elven ore $meta: pickaxe")
			val stone = meta == 1
			helper.assertTrue(state.`is`(BlockTags.NEEDS_STONE_TOOL) == stone && state.`is`(BlockTags.NEEDS_IRON_TOOL) == !stone, "elven ore $meta tier")
			helper.assertTrue(ItemStack(Items.STONE_PICKAXE).isCorrectToolForDrops(state) == stone && ItemStack(Items.IRON_PICKAXE).isCorrectToolForDrops(state), "elven ore $meta tools")
		}
		helper.succeed()
	}

	/** Ore Dictionary автора → теги (MAPPING.md, «Ore Dictionary») */
	@JvmStatic
	@GameTest(template = "empty")
	fun materialTags(helper: GameTestHelper) {
		fun item(tag: String) = ItemTags.create(ResourceLocation(tag))
		fun block(tag: String) = BlockTags.create(ResourceLocation(tag))
		helper.assertTrue(ElvoriumIngot.stack.`is`(item("forge:ingots/elvorium")) && MauftriumNugget.stack.`is`(item("forge:nuggets/mauftrium")), "ingots and nuggets")
		helper.assertTrue(IffesalDust.stack.`is`(item("forge:dusts/iffesal")) && ElementalSlimeBall.stack.`is`(item("forge:slimeballs")), "dust and slime ball")
		helper.assertTrue(MuspelheimEssence.stack.`is`(item("alfheim:essence_muspelheim")) && PrimalRune.stack.`is`(item("alfheim:rune_primal_a")), "names without a common tag")
		helper.assertTrue(RainbowPetal.stack.`is`(item("alfheim:petal_mystic")) && ItemStack(BotaniaItems.redPetal).`is`(item("alfheim:petal_mystic")), "petals")
		helper.assertTrue(RainbowDust.stack.`is`(item("alfheim:dye_floral_powder")) && ItemStack(Items.RED_DYE).`is`(item("alfheim:dye_floral_powder")), "floral powder")
		val gold = AlfheimBlocks.elvenOre[3]
		helper.assertTrue(gold.defaultBlockState().`is`(block("forge:ores/gold")) && ItemStack(gold).`is`(item("forge:ores/gold")) && gold.defaultBlockState().`is`(block("alfheim:ore_gold_alfheim")), "elven gold ore")
		helper.assertTrue(AlfheimBlocks.elvenOre[0].defaultBlockState().`is`(block("forge:ores/dragonstone")) && AlfheimBlocks.elvenOre[5].defaultBlockState().`is`(block("forge:ores/lapis")), "elven ores")
		helper.succeed()
	}
}
