package alfheim.common.block.colored.rainbow

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemIridescentBlockMod
import alfheim.port.legacy.*
import net.minecraft.world.level.block.Block

// PORT: переливающаяся текстура — .mcmeta. КТ-9 — лексикон (ILexiconable); КТ-3 — посох превращает доски в древесную
// кузню (IWandable, TileTreeCrafter)
class BlockRainbowPlanks: BlockMod(Material.wood)/*, ILexiconable, IWandable*/ {
	
	private val name = "rainbowPlanks"
	
	init {
		blockHardness = 2F
		setLightLevel(0f)
		stepSound = soundTypeWood
		
		setBlockName(name)
	}
	
	override fun isInterpolated() = true
	
	/* PORT: КТ-3 — древесная кузня (TileTreeCrafter)
	override fun onUsedByWand(p0: EntityPlayer?, p1: ItemStack?, p2: World?, p3: Int, p4: Int, p5: Int, p6: Int): Boolean {
		if (p2 != null) {
			if (TileTreeCrafter.canEnchanterExist(p2, p3, p4, p5)) {
				p2.setBlock(p3, p4, p5, AlfheimBlocks.treeCrafterBlockRB, p6, 3)
				p2.playSoundEffect(p3.D, p4.D, p5.D, "botania:enchanterBlock", 0.5F, 0.6F)
				
				return true
			}
		}
		
		return false
	}
	*/
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	override fun shouldRegisterInNameSet() = false
	
	// PORT: лут — сам блок (alfheim.port.data.AlfheimBlockLoot)
//	override fun damageDropped(par1: Int) = par1
	
	override fun setBlockName(name: String): Block {
		register(name)
		return super.setBlockName(name)
	}
	
	// PORT: лут — сам блок, один (alfheim.port.data.AlfheimBlockLoot)
//	override fun quantityDropped(random: Random) = 1
//
//	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = this.toItem()
	
	internal fun register(name: String) {
		GameRegistry.registerBlock(this, ItemIridescentBlockMod::class.java, name)
	}
	
	/* PORT: выбор колёсиком — предмет блока (getCloneItemStack 1.20.1)
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.irisSapling
}
