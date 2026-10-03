package alfheim.port.data

import alfheim.common.block.BlockElvenOre
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.advancements.critereon.*
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.*
import net.minecraft.world.level.storage.loot.predicates.MatchTool
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator

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
			block is BlockElvenOre                 -> add(block, elvenOre(block))
			else                                   -> dropSelf(block)
		}
	}

	/**
	 * BlockElvenOre: предмет — `getItemDropped`, сколько — `quantityDropped`. Варианты 0, 2, 5 с удачей роняют
	 * `max(0, rand(удача + 2) - 1) + 1` — это формула `ApplyBonusCount.addOreBonusCount`; лазурит (5) с удачей — ещё
	 * в 4–8 раз больше, без удачи — один, как у автора. С шёлковым касанием — сам блок (`canSilkHarvest` 1.7.10). При
	 * взрыве 1.7.10 ронял каждую вещь с шансом 1 / сила взрыва — `applyExplosionDecay`
	 */
	private fun elvenOre(block: BlockElvenOre): LootTable.Builder {
		val drop = block.getItemDropped(block.meta, null, 0)!!
		if (drop == block.asItem()) return createSingleItemTable(block)

		val item = LootItem.lootTableItem(drop)
		if (block.meta == 5) item.apply(SetItemCountFunction.setCount(UniformGenerator.between(4f, 8f)).`when`(HAS_FORTUNE))
		if (block.meta in arrayOf(0, 2, 5)) item.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
		return createSilkTouchDispatchTable(block, applyExplosionDecay(block, item))
	}

	override fun getKnownBlocks(): Iterable<Block> = LegacyRegistration.blocks.keys

	companion object {

		/** Инструмент с удачей: `fortune > 0` в `quantityDropped` */
		private val HAS_FORTUNE = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(EnchantmentPredicate(Enchantments.BLOCK_FORTUNE, MinMaxBounds.Ints.atLeast(1))))
	}
}
