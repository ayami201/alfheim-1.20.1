package alfheim.common.block.colored

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md); цвета шерсти 1.7.10 — Sheep1710
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemSubtypedBlockMod
import alfheim.port.legacy.*
import alfheim.port.legacy.Sheep1710 as EntitySheep
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.block.Block
import net.minecraftforge.api.distmarker.*
import java.awt.Color

// PORT: вариант metadata (цвет) — отдельный блок (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(16) { BlockColoredPlanks(it) }`. КТ-9 — лексикон (ILexiconable); КТ-3 — посох превращает доски в древесную
// кузню (IWandable, TileTreeCrafter)
class BlockColoredPlanks(val meta: Int): BlockMod(Material.wood)/*, ILexiconable, IWandable*/ {
	
	private val name = "irisPlanks"
	private val TYPES = 16
	
	override val variant get() = meta
	
	init {
		blockHardness = 2F
		setLightLevel(0f)
		stepSound = soundTypeWood
		
		setBlockName(name)
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun getBlockColor() = 0xFFFFFF
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	/* PORT: КТ-3 — древесная кузня (TileTreeCrafter)
	override fun onUsedByWand(p0: EntityPlayer?, p1: ItemStack?, p2: World?, p3: Int, p4: Int, p5: Int, p6: Int): Boolean {
		if (p2 != null) {
			if (TileTreeCrafter.canEnchanterExist(p2, p3, p4, p5)) {
				val meta = p2.getBlockMetadata(p3, p4, p5)
				p2.setBlock(p3, p4, p5, AlfheimBlocks.treeCrafterBlock, meta, 3)
				p2.playSoundEffect(p3.D, p4.D, p5.D, "botania:enchanterBlock", 0.5F, 0.6F)
				
				return true
			}
		}
		
		return false
	}
	*/
	
	/**
	 * Returns the color this block should be rendered. Used by leaves.
	 */
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int): Int {
		if (meta >= EntitySheep.fleeceColorTable.size)
			return 0xFFFFFF
		
		val color = EntitySheep.fleeceColorTable[meta]
		return Color(color[0], color[1], color[2]).rgb
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess?, x: Int, y: Int, z: Int): Int {
		// PORT: metadata — номер варианта блока (SPEC, Р-5)
		val meta = this.meta
//		val meta = world!!.getBlockMetadata(x, y, z)
		return getRenderColor(meta)
	}
	
	override fun shouldRegisterInNameSet() = false
	
	// PORT: лут — сам блок (alfheim.port.data.AlfheimBlockLoot): вариант — сам блок
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
		GameRegistry.registerBlock(this, ItemSubtypedBlockMod::class.java, name)
	}
	
	/* PORT: выбор колёсиком — предмет блока (getCloneItemStack 1.20.1); варианты во вкладке — отдельные блоки (AlfheimTab)
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (list != null && item != null)
			for (i in 0 until TYPES) {
				list.add(ItemStack(item, 1, i))
			}
	}
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.irisSapling
}
