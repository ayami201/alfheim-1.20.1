package alfheim.common.block.magtrees.sealing

// PORT: импорты 1.20.1 (BlockSlab 1.7.10 — Slab1710, MAPPING.md)
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.rainbow.*
import alfheim.common.item.block.*
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock as BlockSlab

// PORT: двойная плита — состояние type=double одинарной (BlockSlabMod)
class BlockSealingWoodSlab(full: Boolean, source: Block = AlfheimBlocks.sealingPlanks): BlockRainbowWoodSlab(full, source), ISoundSilencer {
	
	init {
		setStepSound(soundTypeCloth)
	}
	
	override fun getFullBlock() = AlfheimBlocks.sealingSlabFull as BlockSlab
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemSlabMod::class.java, name)
		// PORT: двойная плита 1.7.10 — состояние type=double этой плиты (BlockSlabMod); её старое имя — sealingPlanksSlabFull
		LegacyRegistration.alias(name.replaceFirst("Slab", "SlabFull"), this, "type=double")
	}
	
	override fun getSingleBlock() = AlfheimBlocks.sealingSlabs as BlockSlab
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double) = dist <= 8
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double) = 0.5f
}

class BlockSealingWoodStairs(source: Block = AlfheimBlocks.sealingPlanks): BlockRainbowWoodStairs(source), ISoundSilencer {
	
	init {
		setStepSound(soundTypeCloth)
	}
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double) = dist <= 8
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double) = 0.5f
}
