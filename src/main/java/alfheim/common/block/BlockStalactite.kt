package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.Level as World

// PORT: стадия (metadata) — отдельный блок (BlockHang), создают массивом `Array(8) { BlockStalactite(it) }`
class BlockStalactite(meta: Int): BlockHang(Material.rock, "Stalactite", 8, meta = meta) {
//class BlockStalactite: BlockHang(Material.rock, "Stalactite", 8) {
	
	init {
		setBlockBounds(0.25f, 0.1f, 0.25f, 0.75f, 1.0f, 0.75f)
	}

	// PORT: лут — таблица (alfheim.port.data.AlfheimBlockLoot): ничего
	fun getItemDropped(meta: Int, random: Random, fortune: Int) = null
//	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = null
	
	override fun canBlockStay(world: World, i: Int, j: Int, k: Int) = world.getBlock(i, j + 1, k).material === Material.rock
}