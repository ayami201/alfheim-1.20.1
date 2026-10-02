package alexsocol.asjlib.extendables.block

// PORT: импорты 1.20.1; BlockFenceGate 1.7.10 — FenceGate1710 (MAPPING.md, «Блоки и предметы»)
import alfheim.port.legacy.*
import net.minecraft.world.item.BlockItem as ItemBlock
import net.minecraft.world.level.block.Block

// PORT: вариант блока-источника — отдельный блок (SPEC, Р-5): src — блок варианта, meta — его номер в 1.7.10
open class BlockModFenceGate(val src: Block, val meta: Int): FenceGate1710() {
	
	init {
		setCreativeTab(null)
	}
	
	// PORT: иконка блока-источника → модель калитки (alfheim.port.data.AlfheimBlockStates)
//	override fun getIcon(side: Int, meta: Int) = src.getIcon(side, this.meta)!!
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlock::class.java, name)
		return super.setBlockName(name)
	}
}
