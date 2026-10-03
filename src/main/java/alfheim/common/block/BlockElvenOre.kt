package alfheim.common.block

// PORT: импорты 1.20.1; лексикон (КТ-9) закомментирован вместе со своей строкой
import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.item.*
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.state.BlockState
import vazkii.botania.common.item.BotaniaItems as ModItems
import java.util.*
import kotlin.math.max
import net.minecraft.world.level.BlockGetter as IBlockAccess
//import alfheim.common.lexicon.AlfheimLexiconData
//import vazkii.botania.api.lexicon.ILexiconable

// PORT: вариант metadata — отдельный блок (BlockModMeta): 0 — драконий камень, 1 — элементиум, 2 — эльфийский кварц,
// 3 — золото, 4 — иффесаль, 5 — лазурит. Лут — таблица alfheim.port.data.AlfheimBlockLoot по getItemDropped и
// quantityDropped ниже; КТ-9 — лексикон (ILexiconable)
class BlockElvenOre(meta: Int): BlockModMeta(Material.rock, 6, ModInfo.MODID, "ElvenOre", AlfheimTab, 2f, harvLvl = 2, meta = meta)/*, ILexiconable*/ {
	
	/**
	 * Meta of dropped item
	 */
	val metas = arrayOf(9, 1, 5, 3, ElvenResourcesMetas.IffesalDust.I, 4)
	val rand = Random()
	
	init {
		setHarvestLevel("pickaxe", 1, 1)
	}
	
	// PORT: предмет 1.7.10 с metadata из metas — свой предмет 1.20.1: ресурс маны 9 — драконий камень, кварц 5 —
	// эльфийский кварц, ресурс 14 — пыль иффесаль, краситель 4 — лазурит. Таблицу лута по нему строит генерация данных
	fun getItemDropped(meta: Int, rand: Random?, fortune: Int): Item? = when (meta) {
		0    -> ModItems.dragonstone
		2    -> ModItems.elfQuartz
		4    -> AlfheimItems.elvenResource[metas[4]]
		5    -> Items.LAPIS_LAZULI
		else -> this.toItem()
	}
//	override fun getItemDropped(meta: Int, rand: Random?, fortune: Int): Item? = when (meta) {
//		0    -> ModItems.manaResource
//		2    -> ModItems.quartz
//		4    -> AlfheimItems.elvenResource
//		5    -> Items.dye
//		else -> this.toItem()
//	}
	
	// PORT: metadata выпавшего предмета выбирает getItemDropped
//	override fun damageDropped(meta: Int) = metas.safeGet(meta)
	
	fun getExpDrop(world: IBlockAccess?, meta: Int, fortune: Int) =
		if (this.toItem() !== getItemDropped(meta, rand, fortune)) rand.nextInt(5) + 3 else 0
	
	// PORT: getExpDrop 1.20.1; с шёлковым касанием опыта нет, как у Forge 1.7.10 (BlockEvent.BreakEvent)
	override fun getExpDrop(state: BlockState, level: LevelReader, randomSource: RandomSource, pos: BlockPos, fortuneLevel: Int, silkTouchLevel: Int) =
		if (silkTouchLevel == 0) getExpDrop(level, meta, fortuneLevel) else 0
	
	// PORT: количество 1.20.1 считает таблица лута (AlfheimBlockLoot) — по этой же формуле
	fun quantityDropped(meta: Int, fortune: Int, random: Random) = when (meta) {
		0, 2, 5 -> {
			if (fortune > 0) {
				var j = max(0, rand.nextInt(fortune + 2) - 1) + 1
				if (meta == 5) j *= 4 + rand.nextInt(5)
				j
			} else 1
		}
		
		else    -> 1
	}
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.ores
}