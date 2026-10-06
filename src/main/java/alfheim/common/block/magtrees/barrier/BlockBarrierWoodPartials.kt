package alfheim.common.block.magtrees.barrier

// PORT: импорты 1.20.1 (BlockSlab 1.7.10 — Slab1710, MAPPING.md)
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.rainbow.*
import alfheim.common.item.block.*
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock as BlockSlab

// PORT: двойная плита — состояние type=double одинарной (BlockSlabMod)
class BlockBarrierWoodSlab(full: Boolean, source: Block = AlfheimBlocks.barrierPlanks): BlockRainbowWoodSlab(full, source) {
	
	override fun getFullBlock() = AlfheimBlocks.barrierSlabFull as BlockSlab
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemSlabMod::class.java, name)
		// PORT: двойная плита 1.7.10 — состояние type=double этой плиты (BlockSlabMod); её старое имя — barrierPlanksSlabFull
		LegacyRegistration.alias(name.replaceFirst("Slab", "SlabFull"), this, "type=double")
	}
	
	override fun getSingleBlock() = AlfheimBlocks.barrierSlabs as BlockSlab
}

class BlockBarrierWoodStairs(source: Block = AlfheimBlocks.barrierPlanks): BlockRainbowWoodStairs(source) {
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
}