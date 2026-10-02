package alexsocol.asjlib.extendables.block

// PORT: импорты 1.20.1; BlockFence 1.7.10 — Fence1710 (MAPPING.md, «Блоки и предметы»)
import alfheim.port.legacy.*
import net.minecraft.core.Direction
import net.minecraft.world.item.BlockItem as ItemBlock
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState

open class BlockModFence(texture: String, mat: Material, val gate: Block?): Fence1710(texture, mat) {
	
	init {
		setCreativeTab(null)
	}
	
	// PORT: canConnectFenceTo(world, x, y, z) → connectsTo(state, sideSolid, direction) 1.20.1. Соседа с твёрдой
	// гранью (непрозрачный нормальный куб 1.7.10, не тыкву и не арбуз) super 1.20.1 проверяет сам, как и калитку,
	// повёрнутую к забору
	override fun connectsTo(state: BlockState, sideSolid: Boolean, direction: Direction): Boolean {
		val block = state.block
		if (super.connectsTo(state, sideSolid, direction)) return true
		return if (block is FenceBlock) true else if (block !== this && block !== gate) false else true
	}
//	override fun canConnectFenceTo(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean {
//		val block = world.getBlock(x, y, z)
//		if (super.canConnectFenceTo(world, x, y, z)) return true
//		return if (block is BlockFence) true else if (block !== this && block !== gate) if (block.material.isOpaque && block.renderAsNormalBlock()) block.material !== Material.gourd else false else true
//	}
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlock::class.java, name)
		return super.setBlockName(name)
	}
}
