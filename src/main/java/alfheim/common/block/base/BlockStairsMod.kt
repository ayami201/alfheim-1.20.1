package alfheim.common.block.base

// PORT: импорты 1.20.1; BlockStairs 1.7.10 — Stairs1710 (MAPPING.md, «Блоки и предметы»)
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemIridescentBlockMod
import alfheim.port.legacy.*
import net.minecraft.world.level.block.Block

// PORT: КТ-9 — лексикон (ILexiconable)
abstract class BlockStairsMod(val source: Block, val meta: Int, val name: String): Stairs1710(source, meta)/*, ILexiconable*/ {
	
	init {
		setCreativeTab(AlfheimTab)
		setBlockName(name)
		setStepSound(source.stepSound)
		// PORT: useNeighborBrightness — свет неполных блоков 1.20.1 считает сама
//		useNeighborBrightness = true
	}
	
	// PORT: твёрдость блока-источника — у Stairs1710 с создания, как BlockStairs 1.7.10
//	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) =
//		source.getBlockHardness(world, x, y, z)
	
	override fun setBlockName(par1Str: String): Block {
		register()
		return super.setBlockName(name)
	}
	
	open fun register() {
		GameRegistry.registerBlock(this, ItemIridescentBlockMod::class.java, name)
	}
	
}
