package alfheim.port.legacy

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block

/*
 * Предмет-блок 1.7.10 (MAPPING.md, «Блоки и предметы»). Классы автора, которые наследовали `ItemBlock` и
 * `ItemBlockWithMetadata`, наследуют эти. Вариант metadata блока в 1.20.1 — свой блок со своим предметом (SPEC, Р-5),
 * поэтому metadata вещи — номер варианта её блока.
 */

/**
 * `net.minecraft.item.ItemBlock` 1.7.10: цвет вещи (`getColorFromItemStack`) — `getRenderColor` блока по metadata
 * вещи; клиент красит им слой модели с номером прохода (alfheim.port.client.AlfheimItemColors)
 */
open class ItemBlock(block: Block): BlockItem(block, Properties()) {

	open fun getColorFromItemStack(stack: ItemStack, pass: Int) = (block as? LegacyBlock)?.let { it.getRenderColor(it.variant ?: 0) } ?: 0xFFFFFF
}

/** `net.minecraft.item.ItemBlockWithMetadata` 1.7.10: предмет блока с вариантами metadata; иконку задаёт модель */
open class ItemBlockWithMetadata(block: Block, @Suppress("UNUSED_PARAMETER") iconBlock: Block): ItemBlock(block)

/**
 * `net.minecraft.item.ItemMultiTexture` 1.7.10: предмет блока с вариантами metadata, имя вещи — имя блока, точка и
 * [names] по metadata (вне массива — первое: `tile.NiflheimBlock.Stone`). Старый ключ перевода по нему читает генерация
 * `legacy_ids.json`; иконку задаёт модель
 */
open class ItemMultiTexture(block: Block, @Suppress("UNUSED_PARAMETER") iconBlock: Block, val names: Array<String>): ItemBlock(block)
