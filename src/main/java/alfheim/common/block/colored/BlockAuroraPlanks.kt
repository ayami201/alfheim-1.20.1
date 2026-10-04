package alfheim.common.block.colored

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemBlockAurora
import alfheim.port.legacy.*
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.block.Block
import net.minecraftforge.api.distmarker.*

// PORT: КТ-9 — лексикон (ILexiconable); КТ-3 — посох превращает доски в древесную кузню (IWandable, TileTreeCrafter)
class BlockAuroraPlanks: BlockMod(Material.wood)/*, ILexiconable, IWandable*/ {
	
	private val name = "auroraPlanks"
	
	init {
		blockHardness = 2F
		
		stepSound = soundTypeWood
		setBlockName(name)
	}
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockAurora::class.java, name)
		return super.setBlockName(name)
	}
	
	override fun shouldRegisterInNameSet() = false
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int) = BlockAuroraDirt.getBlockColor(x, y, z)
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	/* PORT: КТ-3 — древесная кузня (TileTreeCrafter)
	override fun onUsedByWand(p0: EntityPlayer?, p1: ItemStack?, p2: World?, p3: Int, p4: Int, p5: Int, p6: Int): Boolean {
		if (p2 != null) {
			if (TileTreeCrafter.canEnchanterExist(p2, p3, p4, p5)) {
				p2.setBlock(p3, p4, p5, AlfheimBlocks.treeCrafterBlockAU, p6, 3)
				p2.playSoundEffect(p3.D, p4.D, p5.D, "botania:enchanterBlock", 0.5F, 0.6F)
				
				return true
			}
		}
		
		return false
	}
	*/
	
	/* PORT: выбор колёсиком — предмет блока (getCloneItemStack 1.20.1)
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.aurora
}