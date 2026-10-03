package alfheim.port.data

import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block

/**
 * Лут блоков автора. В 1.7.10 блок по умолчанию ронял сам себя (`getItemDropped` — свой предмет, `damageDropped` —
 * metadata варианта), при взрыве — с шансом 1 / сила взрыва; `dropSelf` 1.20.1 делает то же. Блок без предмета
 * ничего не роняет. Двойная плита 1.7.10 роняла две одинарные (`BlockSlab.quantityDropped`), как плита 1.20.1 в
 * состоянии `type=double`. Блоки со своим `getDrops` автора получают свою таблицу здесь же, вместе со своей партией.
 */
class AlfheimBlockLoot: BlockLootSubProvider(emptySet(), FeatureFlags.REGISTRY.allFlags()) {

	override fun generate() {
		for (block in knownBlocks) when {
			block.asItem() == Items.AIR            -> add(block, noDrop())
			block is Slab1710                      -> add(block, createSlabItemTable(block))
			// BlockPane: canDrop = false — ничего, а с шёлковым касанием — себя (canSilkHarvest)
			block is Pane1710 && !block.canDrop    -> dropWhenSilkTouch(block)
			else                                   -> dropSelf(block)
		}
	}

	override fun getKnownBlocks(): Iterable<Block> = LegacyRegistration.blocks.keys
}
