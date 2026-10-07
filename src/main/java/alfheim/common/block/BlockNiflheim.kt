package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.*
import alfheim.common.block.base.BlockMod
//import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.RotatedPillarBlock.AXIS
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition

// PORT: вариант metadata — отдельный блок (SPEC, Р-5): 0–6, колонна 7 и руническая колонна 10, номер варианта — meta;
// массив AlfheimBlocks.niflheimBlock — по metadata 1.7.10 (create ниже). Поворот колонны (metadata 8, 9 и 11, 12) —
// свойство axis, как у колонны кварца
class BlockNiflheim(val meta: Int): BlockMod(material(meta)) {
//class BlockNiflheim: BlockMod(Material.rock) {
	
	override val variant get() = meta
	
	// PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates)
//	lateinit var icons: Array<IIcon>
//	lateinit var iconsPillar: Array<IIcon>
//	lateinit var iconsRunic: Array<IIcon>
	
	init {
		setBlockName("NiflheimBlock")
		setHardness(3f)
		setHarvestLevel("pickaxe", 1)
		setStepSound(soundTypeStone)
		// PORT: колонна по умолчанию стоит (metadata 7, 10)
		if (meta == PILLAR || meta == RUNIC) registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Y))
	}
	
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		if (creatingPillar) builder.add(AXIS)
	}
	
	// PORT: metadata колонны по стороне, на которую её ставят (onBlockPlaced), — ось этой стороны: 2, 3 (север, юг) —
	// вдоль Z (metadata + 2), 4, 5 (запад, восток) — вдоль X (+ 1), верх и низ — стоячая
	override fun getStateForPlacement(context: BlockPlaceContext): BlockState? =
		if (meta == PILLAR || meta == RUNIC) defaultBlockState().setValue(AXIS, context.clickedFace.axis) else defaultBlockState()
	/*
	override fun onBlockPlaced(world: World?, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, meta: Int): Int {
		return if (meta == 7 || meta == 10) {
			when (side) {
				2, 3 -> meta + 2
				4, 5 -> meta + 1
				else -> meta
			}
		} else meta
	}
	*/
	
	// PORT: поворот постройки 1.20.1 поворачивает и колонну, как колонну кварца
	@Deprecated("Deprecated in Java")
	override fun rotate(state: BlockState, rotation: Rotation): BlockState = if (meta == PILLAR || meta == RUNIC) RotatedPillarBlock.rotatePillar(state, rotation) else state
	
	// PORT: колёсиком — предмет своего блока (getCloneItemStack 1.20.1): у руды — руда, у повёрнутой колонны — колонна, как
	// createStackedBlock; с шёлковым касанием — то же (лут, alfheim.port.data.AlfheimBlockLoot)
	/*
	override fun getPickBlock(target: MovingObjectPosition, world: World, x: Int, y: Int, z: Int, player: EntityPlayer) =
		createStackedBlock(world.getBlockMetadata(x, y, z))
	
	override fun createStackedBlock(meta: Int) = ItemStack(this, 1, if (meta == 2) 2 else damageDropped(meta))
	*/
	
	override fun shouldRegisterInNameSet() = false
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockNiflheim::class.java, name)
		return super.setBlockName(name)
	}
	
	/* PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates): NiflheimBlock<вид>, колонны — NiflheimBlockPillarSide и
	   NiflheimBlockPillarTop, рунические — NiflheimBlockRunicSide и NiflheimBlockRunicTop; вкладка — AlfheimTab: варианты
	   по порядку, повёрнутых колонн нет
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = Array(NiflheimBlockMetas.entries.size - 6) { IconHelper.forBlock(reg, this, NiflheimBlockMetas.entries[it].modname) }
		arrayOf("Side", "Top").apply {
			iconsPillar = map { IconHelper.forBlock(reg, this@BlockNiflheim, "Pillar$it") }.toTypedArray()
			iconsRunic = map { IconHelper.forBlock(reg, this@BlockNiflheim, "Runic$it") }.toTypedArray()
		}
	}
	
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		var prev: String? = null
		
		NiflheimBlockMetas.entries.forEachIndexed { id, it ->
			val s = it.modname
			if (s != prev)
				list.add(ItemStack(item, 1, id))
			prev = s
		}
		
//		subtypes.forEachIndexed { i, s -> if (s.isNotEmpty()) list.add(ItemStack(item, 1, i)) }
	}
	
	override fun getIcon(side: Int, meta: Int) = when (meta) {
		in 0..6 -> icons[meta]
		7, 10   -> (if (meta < 10) iconsPillar else iconsRunic)[if (side in 0..1) 1 else 0]
		8, 11   -> (if (meta < 10) iconsPillar else iconsRunic)[if (side in 4..5) 1 else 0]
		9, 12   -> (if (meta < 10) iconsPillar else iconsRunic)[if (side in 2..3) 1 else 0]
		else    -> null
	}
	*/
	
	// PORT: лут — таблица (alfheim.port.data.AlfheimBlockLoot) по этим правилам
	fun damageDropped(meta: Int) = when (meta) {
//	override fun damageDropped(meta: Int) = when (meta) {
		2         -> ElvenResourcesMetas.Nifleur.I
		8, 9      -> 7
		11, 12    -> 10
		in 13..15 -> 0
		else      -> meta
	}
	
	/*
	override fun getDrops(world: World, x: Int, y: Int, z: Int, meta: Int, fortune: Int): ArrayList<ItemStack> {
		val ret = ArrayList<ItemStack>()
		val item = if (meta == 2) AlfheimItems.elvenResource else this.toItem()
		ret.add(ItemStack(item, if (meta == 2) fortune + 1 else 1, damageDropped(meta)))
		return ret
	}
	
	override fun getDamageValue(world: World, x: Int, y: Int, z: Int) = damageDropped(world.getBlockMetadata(x, y, z))
	*/
	
	// PORT: рендер колонн по оси (RenderBlockNiflheimSet) — модели бревна (cube_column, cube_column_horizontal)
//	override fun getRenderType() = LibRenderIDs.idNiflheim
	
	enum class NiflheimBlockMetas {
		STONE, COBBLESTONE, ORE, BRICKS, CRACKED, CHISELED, POLISHED, PILLAR, PILLAR_1, PILLAR_2, RUNIC, RUNIC_1, RUNIC_2;
		
		val modname get() = name.lowercase().capitalized().substringBefore("_")
		val I get() = ordinal
		// PORT: вариант metadata — блок массива AlfheimBlocks.niflheimBlock (по metadata 1.7.10)
		val stack get() = ItemStack(AlfheimBlocks.niflheimBlock[ordinal])
		fun stack(size: Int) = ItemStack(AlfheimBlocks.niflheimBlock[ordinal], size)
//		val stack get() = ItemStack(AlfheimBlocks.niflheimBlock, 1, ordinal)
//		fun stack(size: Int) = ItemStack(AlfheimBlocks.niflheimBlock, size, ordinal)
	}
	
	class ItemBlockNiflheim(block: Block): ItemMultiTexture(block, block, NiflheimBlockMetas.entries.map { it.modname }.toTypedArray())
	
	companion object {
		
		const val PILLAR = 7
		const val RUNIC = 10
		
		/**
		 * Колонна ли блок, который сейчас создаётся. Свойства состояний блока 1.20.1 задаёт конструктор `Block` — раньше,
		 * чем у наследника появляется номер варианта; блоки создаются по одному, в событии регистрации
		 */
		private var creatingPillar = false
		
		private fun material(meta: Int): Material {
			creatingPillar = meta == PILLAR || meta == RUNIC
			return Material.rock
		}
		
		/**
		 * Массив `AlfheimBlocks.niflheimBlock` — по metadata 1.7.10: варианты 0–6, 7, 10 — свои блоки, повёрнутые колонны
		 * (8, 9 и 11, 12) — те же блоки 7 и 10; старое имя с их metadata — состояние `axis` (`legacy_ids.json`)
		 */
		fun create(): Array<Block> {
			val variants = LinkedHashMap<Int, Block>()
			val blocks = Array(NiflheimBlockMetas.entries.size) { variants.getOrPut(dropped(it)) { BlockNiflheim(dropped(it)) } }
			for ((meta, axis) in listOf(8 to "x", 9 to "z", 11 to "x", 12 to "z"))
				LegacyRegistration.alias("NiflheimBlock", blocks[meta], "axis=$axis", meta)
			return blocks
		}
		
		/** Вариант блока для metadata 1.7.10: повёрнутая колонна — колонна (damageDropped, кроме руды) */
		private fun dropped(meta: Int) = when (meta) {
			8, 9   -> PILLAR
			11, 12 -> RUNIC
			else   -> meta
		}
	}
}
