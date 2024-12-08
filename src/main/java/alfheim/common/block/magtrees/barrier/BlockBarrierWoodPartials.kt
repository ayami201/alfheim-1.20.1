package alfheim.common.block.magtrees.barrier

import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.rainbow.*
import alfheim.common.item.block.*
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.*

class BlockBarrierWoodSlab(full: Boolean, source: Block = AlfheimBlocks.barrierPlanks): BlockRainbowWoodSlab(full, source) {
	
	override fun getFullBlock() = AlfheimBlocks.barrierSlabsFull as BlockSlab
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemSlabMod::class.java, name)
	}
	
	override fun getSingleBlock() = AlfheimBlocks.barrierSlabs as BlockSlab
}

class BlockBarrierWoodStairs(source: Block = AlfheimBlocks.barrierPlanks): BlockRainbowWoodStairs(source) {
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
}