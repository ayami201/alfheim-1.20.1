package alfheim.api.trees

// PORT: импорты 1.20.1
import net.minecraft.world.level.block.Block

// PORT: блок с вариантами metadata 1.7.10 в порту — массив блоков-вариантов (SPEC, Р-5): metadata почвы — номер её
// варианта, getWood и getLeaves отдают сам блок варианта, а metadata того, что ставит генератор (getMeta), — 0
interface IIridescentSaplingVariant {
	
	val acceptableSoils: List<Block>
	fun getMeta(soil: Block, meta: Int, toPlace: Block): Int
	
	fun matchesSoil(soil: Block, meta: Int): Boolean
	
	fun getLeaves(soil: Block, meta: Int): Block
	
	fun getWood(soil: Block, meta: Int): Block
}
