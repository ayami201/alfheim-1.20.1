package alfheim.common.block

import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileFloodLight
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.world.*
import vazkii.botania.common.block.ModBlocks

class BlockFloodLight: BlockContainerMod(Material.iron) {
	
	init {
		setBlockName("FloodLight")
		setHardness(5f)
		setResistance(10f)
		setStepSound(soundTypeMetal)
	}
	
	override fun getIcon(side: Int, meta: Int) = ModBlocks.livingwood.getIcon(0, 4)!!
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
	override fun getRenderType() = LibRenderIDs.idFloodlight
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun getLightValue() = 0
	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int) = 0
	override fun createNewTileEntity(world: World?, meta: Int) = TileFloodLight()
}
