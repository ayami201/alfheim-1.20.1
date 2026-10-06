package alfheim.port.data

import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.BlockElvenOre
import alfheim.common.block.colored.BlockColoredDoubleGrass
import alfheim.common.block.colored.rainbow.*
import alfheim.common.block.magtrees.barrier.BlockBarrierWood
import alfheim.common.block.magtrees.lightning.BlockLightningWood
import alfheim.common.block.magtrees.nether.BlockNetherWood
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.advancements.critereon.*
import net.minecraft.core.BlockPos
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.util.RandomSource
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoublePlantBlock
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.level.storage.loot.*
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.*
import net.minecraft.world.level.storage.loot.predicates.*
import net.minecraft.world.level.storage.loot.providers.number.*
import net.minecraftforge.common.ToolActions
import net.minecraftforge.common.loot.CanToolPerformAction

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
			block is Leaves1710                    -> add(block, leaves(block))
			block is BlockRainbowGrass             -> add(block, rainbowGrass(block))
			block is TallGrass1710                 -> add(block, tallGrass(block))
			block is BlockColoredDoubleGrass       -> add(block, irisDoubleGrass(block))
			block is BlockRainbowDoubleGrass       -> add(block, shearedDoublePlant(block, AlfheimBlocks.rainbowGrass[block.meta], 2f, true))
			// onSheared — сам цветок, у любой половины
			block is BlockRainbowDoubleFlower      -> add(block, shearedDoublePlant(block, block, 1f, false))
			// бревно барьерного, грозового и адского деревьев — вариант damageDropped: сердцевина роняет обычное бревно
			block is BlockBarrierWood              -> add(block, createSingleItemTable(AlfheimBlocks.barrierWood[block.damageDropped(block.meta)]))
			block is BlockLightningWood            -> add(block, createSingleItemTable(AlfheimBlocks.lightningWood[block.damageDropped(block.meta)]))
			block is BlockNetherWood               -> add(block, createSingleItemTable(AlfheimBlocks.netherWood[block.damageDropped(block.meta)]))
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

	/**
	 * Листва 1.7.10 (`BlockLeavesMod.getDrops`, `IShearable`): с ножницами или шёлковым касанием — сама листва, иначе —
	 * `getItemDropped` с шансом 1/20, с удачей I, II, III — 1/16, 1/12, 1/10 (`20 - (2 shl удача)`, не меньше 10). Взрыв
	 * лут не уменьшает (`dropBlockAsItemWithChance` с шансом 1); палок и яблок, как у листвы ванилы 1.20.1, нет
	 */
	private fun leaves(block: Leaves1710): LootTable.Builder {
		val shearsOrSilkTouch = SHEARS.or(HAS_SILK_TOUCH)
		val drop = block.getItemDropped(block.variant ?: 0, RandomSource.create(), 0)
		if (drop == null || drop == Items.AIR)
			return LootTable.lootTable().withPool(LootPool.lootPool().`when`(shearsOrSilkTouch).add(LootItem.lootTableItem(block)))
		val sapling = LootItem.lootTableItem(drop).`when`(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 1f / 20, 1f / 16, 1f / 12, 1f / 10))
		return createSelfDropDispatchTable(block, shearsOrSilkTouch, sapling)
	}

	/**
	 * Трава 1.7.10 (`BlockTallGrass`, Forge): с ножницами — сама трава (`onSheared`); иначе с шансом 1/8 — семена
	 * (`ForgeHooks.getGrassSeed`: пшеничные), удача не влияет; при взрыве — с шансом 1 / сила взрыва. Шёлковое касание
	 * трава 1.7.10 не брала (`canSilkHarvest`: не обычный куб)
	 */
	private fun tallGrass(block: TallGrass1710) =
		createSelfDropDispatchTable(block, SHEARS, applyExplosionDecay(block, LootItem.lootTableItem(Items.WHEAT_SEEDS).`when`(LootItemRandomChanceCondition.randomChance(1f / 8))))

	/**
	 * `BlockColoredDoubleGrass.onSheared`: с ножницами — две травы ириса цвета растения; верхняя половина — если под ней
	 * нижняя. Без ножниц — ничего (`getItemDropped` — null). Ломается одна половина, вторую 1.20.1 убирает без лута
	 */
	private fun irisDoubleGrass(block: BlockColoredDoubleGrass) =
		shearedDoublePlant(block, AlfheimBlocks.irisGrass[block.TYPES * block.colorSet + block.meta], 2f, true)

	/**
	 * Двойное растение автора: с ножницами — [count] × [item] (`onSheared`), у верхней половины — только если под ней
	 * нижняя ([lowerBelow], проверка из `onSheared` автора). Без ножниц — ничего (`getItemDropped` — null). Ломается одна
	 * половина, вторую 1.20.1 убирает без лута
	 */
	private fun shearedDoublePlant(block: Block, item: ItemLike, count: Float, lowerBelow: Boolean): LootTable.Builder {
		val entry = LootItem.lootTableItem(item)
		if (count != 1f) entry.apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)))
		val drop = entry.`when`(SHEARS)
		if (!lowerBelow) return LootTable.lootTable().withPool(LootPool.lootPool().add(drop))
		fun half(half: DoubleBlockHalf) = LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, half))
		val lower = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER).build()).build()), BlockPos(0, -1, 0))
		return LootTable.lootTable()
			.withPool(LootPool.lootPool().add(drop).`when`(half(DoubleBlockHalf.LOWER)))
			.withPool(LootPool.lootPool().add(drop).`when`(half(DoubleBlockHalf.UPPER)).`when`(lower))
	}

	/**
	 * `BlockRainbowGrass.getDrops`, `onSheared`: трава и авроровая трава — как трава ([tallGrass]); цветок и мерцающий
	 * цветок — сами; закопанные лепестки — радужный лепесток. При взрыве — с шансом 1 / сила взрыва
	 */
	private fun rainbowGrass(block: BlockRainbowGrass) = when (block.meta) {
		BlockRainbowGrass.GRASS, BlockRainbowGrass.AURORA -> tallGrass(block)
		BlockRainbowGrass.BURIED                          -> createSingleItemTable(AlfheimItems.elvenResource[ElvenResourcesMetas.RainbowPetal.I])
		else                                              -> createSingleItemTable(block)
	}

	override fun getKnownBlocks(): Iterable<Block> = LegacyRegistration.blocks.keys

	companion object {

		/** Инструмент с удачей: `fortune > 0` в `quantityDropped` */
		private val HAS_FORTUNE = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(EnchantmentPredicate(Enchantments.BLOCK_FORTUNE, MinMaxBounds.Ints.atLeast(1))))

		/**
		 * Ножницы 1.7.10 (`IShearable` срабатывал у любых `ItemShears`) — инструмент, который режет как ножницы; так и
		 * в таблицах ванилы у Forge 1.20.1
		 */
		private val SHEARS = CanToolPerformAction.canToolPerformAction(ToolActions.SHEARS_DIG)
	}
}
