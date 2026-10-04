package alfheim.common.item.block

// PORT: импорты 1.7.10 заменены на 1.20.1; ItemBlock 1.7.10 — alfheim.port.legacy.ItemBlock
import alfheim.port.legacy.ItemBlock
import net.minecraft.world.level.block.Block

// PORT: ключ перевода предмета-блока в 1.20.1 — ключ блока (block.alfheim.<id>); старые ключи «tile.alfheim:…»
// переименованы по legacy_ids.json. Бит опадания листвы — свойство persistent листвы (Leaves1710): поставленная
// игроком листва не опадает и в 1.20.1
open class ItemBlockLeavesMod(block: Block): ItemBlock(block) {
	
	/*
	override fun setUnlocalizedName(name: String): ItemBlock? {
		(this as Item).unlocalizedName = name
		return this
	}
	
	override fun getMetadata(meta: Int): Int {
		if (field_150939_a is BlockLeavesMod) return meta or (field_150939_a as BlockLeavesMod).decayBit()
		return meta
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		getUnlocalizedNameInefficiently_(stack).replace("tile.", "tile.${ModInfo.MODID}:")
	
	fun getUnlocalizedNameInefficiently_(stack: ItemStack) = super.getUnlocalizedNameInefficiently(stack)!!
	*/
}
