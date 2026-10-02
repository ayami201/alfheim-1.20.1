package alexsocol.asjlib.extendables

// PORT: импорты 1.7.10 заменены на 1.20.1
import net.minecraft.world.item.*
import net.minecraft.world.level.block.Block

// PORT: вариант metadata в 1.20.1 — отдельный блок со своим предметом (SPEC, Р-5): номер варианта уже в id блока
// (alf_storage4), а с ним и в ключе перевода; предмет-блок 1.20.1 без metadata
open class ItemBlockMetaName(block: Block): BlockItem(block, Properties()) {
	
	/*
	init {
		setHasSubtypes(true)
	}
	
	override fun getUnlocalizedName(stack: ItemStack) = "${super.getUnlocalizedName(stack)}${if (((field_150939_a as? BlockModMeta)?.subtypes ?: 16) > 1) stack.meta else ""}"
	
	override fun getMetadata(meta: Int) = meta
	*/
}
