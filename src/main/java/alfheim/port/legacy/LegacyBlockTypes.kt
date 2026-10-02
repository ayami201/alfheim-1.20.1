package alfheim.port.legacy

import alfheim.port.legacy.botania.Botania1710
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.*
import java.util.function.Supplier

/*
 * Блоки 1.7.10 особой формы — лестница, плита, стена, забор, калитка, люк, панель (MAPPING.md, «Блоки и предметы»).
 *
 * Базовый класс порта для каждого из них наследует такой же блок 1.20.1 и сеттеры 1.7.10 ([LegacyBlock]). Значения,
 * которые задавал конструктор блока 1.7.10 (твёрдость, взрывоустойчивость и звук лестницы и стены — от блока-источника),
 * задаются так же. Механика формы — 1.20.1: как лестница сворачивает за угол, как плиты складываются в двойную, к чему
 * тянутся стена и панель, какой свет пропускают неполные блоки — так ведут себя и блоки ванилы 1.20.1.
 */

/** Свойства 1.7.10 блока-источника: блок порта или блок Botania ([Botania1710]) */
fun legacyProps(block: Block): BlockProps = (block as? LegacyBlock)?.legacy ?: Botania1710.props(block)

/** `net.minecraft.block.BlockStairs` 1.7.10: материал, твёрдость, взрывоустойчивость и звук — от блока-источника */
open class Stairs1710(val legacySource: Block, @Suppress("UNUSED_PARAMETER") meta: Int): StairBlock(Supplier { legacySource.defaultBlockState() }, legacyProps(legacySource).material.properties()), LegacyBlock {

	final override val legacy = BlockProps(legacyProps(legacySource).material)

	init {
		val props = legacyProps(legacySource)
		setHardness(props.blockHardness)
		setResistance(props.blockResistance / 3f)
		setStepSound(props.stepSound)
	}

	// взрывоустойчивость, случайные тики и эффекты лестница берёт у блока-источника — и в 1.7.10, и в 1.20.1

	override fun isOpaqueCube() = false

	override fun getFriction() = legacy.slipperiness

	@Deprecated("Deprecated in Java")
	override fun getSoundType(state: BlockState) = legacy.stepSound ?: SoundType.STONE

	companion object: SoundTypes1710
}

/**
 * `net.minecraft.block.BlockSlab` 1.7.10. Двойная плита 1.7.10 была отдельным блоком ([full]); в 1.20.1 это состояние
 * `type=double` той же плиты, поэтому создаётся только одинарная
 */
open class Slab1710(@Suppress("UNUSED_PARAMETER") full: Boolean, material: Material): SlabBlock(material.properties()), LegacyBlock {

	final override val legacy = BlockProps(material)

	override fun isOpaqueCube() = false

	override fun getExplosionResistance() = legacy.blockResistance / 5f

	override fun getFriction() = legacy.slipperiness

	@Deprecated("Deprecated in Java")
	override fun getSoundType(state: BlockState) = legacy.stepSound ?: SoundType.STONE

	override fun isRandomlyTicking(state: BlockState) = legacy.needsRandomTick

	companion object: SoundTypes1710
}

/**
 * `net.minecraft.block.BlockWall` 1.7.10: материал, твёрдость, взрывоустойчивость и звук — от блока-источника. Стена
 * лежит в теге `minecraft:walls` (генерация данных): без него стена 1.20.1 не соединяется даже сама с собой
 */
open class Wall1710(val legacySource: Block): WallBlock(legacyProps(legacySource).material.properties()), LegacyBlock {

	final override val legacy = BlockProps(legacyProps(legacySource).material)

	init {
		val props = legacyProps(legacySource)
		setHardness(props.blockHardness)
		setResistance(props.blockResistance / 3f)
		setStepSound(props.stepSound)
	}

	override fun isOpaqueCube() = false

	override fun getExplosionResistance() = legacy.blockResistance / 5f

	override fun getFriction() = legacy.slipperiness

	@Deprecated("Deprecated in Java")
	override fun getSoundType(state: BlockState) = legacy.stepSound ?: SoundType.STONE

	override fun isRandomlyTicking(state: BlockState) = legacy.needsRandomTick

	companion object: SoundTypes1710
}

/**
 * `net.minecraft.block.BlockFence` 1.7.10; [texture] — имя иконки, её читает генерация моделей. Забор лежит в теге
 * `minecraft:fences`, но не в `minecraft:wooden_fences`: к нему, как в 1.7.10, не тянутся заборы ванилы, а поводок
 * привязывается к нему, как к любому забору
 */
open class Fence1710(texture: String, material: Material): FenceBlock(material.properties()), LegacyBlock {

	final override val legacy = BlockProps(material)

	init {
		setBlockTextureName(texture)
	}

	override fun isOpaqueCube() = false

	override fun getExplosionResistance() = legacy.blockResistance / 5f

	override fun getFriction() = legacy.slipperiness

	@Deprecated("Deprecated in Java")
	override fun getSoundType(state: BlockState) = legacy.stepSound ?: SoundType.STONE

	override fun isRandomlyTicking(state: BlockState) = legacy.needsRandomTick

	companion object: SoundTypes1710
}

/**
 * `net.minecraft.block.BlockFenceGate` 1.7.10, материал — дерево. Звук открытия — калитки 1.20.1: `random.door_open`
 * 1.7.10 в 1.20.1 не играет ни одно событие
 */
open class FenceGate1710: FenceGateBlock(Material.wood.properties(), WoodType.OAK), LegacyBlock {

	final override val legacy = BlockProps(Material.wood)

	override fun isOpaqueCube() = false

	override fun getExplosionResistance() = legacy.blockResistance / 5f

	override fun getFriction() = legacy.slipperiness

	@Deprecated("Deprecated in Java")
	override fun getSoundType(state: BlockState) = legacy.stepSound ?: SoundType.STONE

	override fun isRandomlyTicking(state: BlockState) = legacy.needsRandomTick

	companion object: SoundTypes1710
}

/**
 * `net.minecraft.block.BlockTrapDoor` 1.7.10: люк из железа рукой не открывается. Звук — люка 1.20.1 (дерево — дубового,
 * железо — железного); подпорка не нужна, как всем люкам 1.20.1
 */
open class TrapDoor1710(material: Material): TrapDoorBlock(material.properties().noOcclusion(), if (material == Material.iron) BlockSetType.IRON else BlockSetType.OAK), LegacyBlock {

	final override val legacy = BlockProps(material)

	override fun isOpaqueCube() = false

	override fun getExplosionResistance() = legacy.blockResistance / 5f

	override fun getFriction() = legacy.slipperiness

	@Deprecated("Deprecated in Java")
	override fun getSoundType(state: BlockState) = legacy.stepSound ?: SoundType.STONE

	override fun isRandomlyTicking(state: BlockState) = legacy.needsRandomTick

	companion object: SoundTypes1710
}

/**
 * `net.minecraft.block.BlockPane` 1.7.10 (стеклянная панель, железная решётка): [texture] и [topTexture] — иконки
 * плоскости и торца, [canDrop] — роняет ли себя. Панель тянется к соседу с твёрдой гранью и к другой панели
 * (`IronBarsBlock.attachsTo` 1.20.1 нельзя переопределить)
 */
open class Pane1710(texture: String, val topTexture: String, material: Material, val canDrop: Boolean): IronBarsBlock(material.properties().noOcclusion()), LegacyBlock {

	final override val legacy = BlockProps(material)

	init {
		setBlockTextureName(texture)
	}

	override fun isOpaqueCube() = false

	override fun getExplosionResistance() = legacy.blockResistance / 5f

	override fun getFriction() = legacy.slipperiness

	@Deprecated("Deprecated in Java")
	override fun getSoundType(state: BlockState) = legacy.stepSound ?: SoundType.STONE

	override fun isRandomlyTicking(state: BlockState) = legacy.needsRandomTick

	companion object: SoundTypes1710
}

