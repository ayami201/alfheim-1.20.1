package alfheim.api.trees

// PORT: импорты 1.20.1
import net.minecraft.world.level.block.Block

// PORT: почва, бревно и листва 1.7.10 — блоки с вариантами metadata, в порту — массивы блоков-вариантов, номер варианта —
// индекс (SPEC, Р-5); у блока без вариантов — массив из одного блока. Номер варианта почвы — её индекс в массиве soil: у
// почвы другого мода (трава Botania) порт номер 1.7.10 иначе не знает
class IridescentSaplingBaseVariant(var soil: Array<out Block>, var wood: Array<out Block>, var leaves: Array<out Block>): IIridescentSaplingVariant {
	
	var metaMin = 0
	var metaMax = 15
	var metaShift = 0
	
	override val acceptableSoils: List<Block>
		get() {
			val soils = ArrayList<Block>()
			soils.addAll(soil)
//			soils.add(soil)
			return soils
		}
	
	constructor(soil: Array<out Block>, wood: Array<out Block>, leaves: Array<out Block>, metaMin: Int, metaMax: Int): this(soil, wood, leaves) {
		this.metaMin = metaMin
		this.metaMax = metaMax
	}
	
	constructor(soil: Array<out Block>, wood: Array<out Block>, leaves: Array<out Block>, metaMin: Int, metaMax: Int, metaShift: Int): this(soil, wood, leaves, metaMin, metaMax) {
		this.metaShift = metaShift
	}
	
	constructor(soil: Array<out Block>, wood: Array<out Block>, leaves: Array<out Block>, meta: Int): this(soil, wood, leaves, meta, meta)
	
	override fun matchesSoil(soil: Block, meta: Int): Boolean {
		val variant = variant(soil)
		return variant >= 0 && variant <= metaMax && variant >= metaMin
	}
//	override fun matchesSoil(soil: Block, meta: Int) =
//		soil === this.soil && meta <= metaMax && meta >= metaMin
	
	// PORT: вариант бревна (meta and 3) и листвы (meta - metaShift) — сам блок (getWood и getLeaves ниже): генератор
	// ставит блок варианта с metadata 0 (alfheim.port.legacy.WorldGenerator)
	override fun getMeta(soil: Block, meta: Int, toPlace: Block): Int {
		return 0
		/*
		if (toPlace === wood) {
			return meta and 3
		} else if (toPlace === leaves) {
			return meta - metaShift
		}
		return 0
		*/
	}
	
	override fun getLeaves(soil: Block, meta: Int) = leaves[variant(soil) - metaShift]
//	override fun getLeaves(soil: Block, meta: Int) = leaves
	
	override fun getWood(soil: Block, meta: Int) = wood[variant(soil) and 3]
//	override fun getWood(soil: Block, meta: Int) = wood
	
	/** PORT: номер варианта почвы — индекс в массиве soil; -1 — не эта почва */
	private fun variant(soil: Block) = this.soil.indexOf(soil)
}
