package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.Level as World

// PORT: стадия (metadata) — отдельный блок (BlockHang), создают массивом `Array(4) { BlockIcicle(it) }`
class BlockIcicle(meta: Int): BlockHang(Material.ice, "Icicle", 4, meta = meta) {
//class BlockIcicle: BlockHang(Material.ice, "Icicle", 4) {
	
	init {
		setBlockBounds(0.25f, 0.1f, 0.25f, 0.75f, 1.0f, 0.75f)
	}
	
	// PORT: лут — таблица (alfheim.port.data.AlfheimBlockLoot): ничего
	fun getItemDropped(meta: Int, random: Random, fortune: Int) = null
//	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = null
	
	override fun canBlockStay(world: World, x: Int, y: Int, z: Int): Boolean {
		val mat = world.getBlock(x, y + 1, z).material
		return mat === Material.ice || mat === Material.packedIce
	}
}