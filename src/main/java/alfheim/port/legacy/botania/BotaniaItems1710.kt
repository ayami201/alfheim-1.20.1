package alfheim.port.legacy.botania

import net.minecraft.world.item.Item
import vazkii.botania.common.item.BotaniaItems

/*
 * Предметы Botania r1.8-249 (1.7.10) с вариантами metadata, которые код автора перебирает по номеру. В Botania 1.20.1
 * вариант — отдельный предмет; здесь они — массив по номеру варианта 1.7.10: `ItemStack(ancientWill, 1, i)` →
 * `ItemStack(ancientWill[i])` (MAPPING.md, «Botania»)
 */

/** `ModItems.ancientWill`: 0 — воля Ахрима, 1 — Дхарока, 2 — Гутана, 3 — Торага, 4 — Верака, 5 — Карила */
val ancientWill: Array<Item> by lazy {
	arrayOf(BotaniaItems.ancientWillAhrim, BotaniaItems.ancientWillDharok, BotaniaItems.ancientWillGuthan, BotaniaItems.ancientWillTorag, BotaniaItems.ancientWillVerac, BotaniaItems.ancientWillKaril)
}
