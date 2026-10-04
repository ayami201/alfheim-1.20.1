package alfheim.common.block.base

// PORT: импорты 1.20.1; BlockSlab 1.7.10 — Slab1710 (MAPPING.md, «Блоки и предметы»)
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemColoredSlabMod
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock as BlockSlab

// PORT: двойная плита 1.7.10 (full) — состояние type=double одинарной (Slab1710): getFullBlock и getSingleBlock
// возвращают одну и ту же плиту. КТ-9 — лексикон (ILexiconable)
abstract class BlockSlabMod(val full: Boolean, val meta: Int, val source: Block, val name: String):
	Slab1710(full, legacyProps(source).material)/*, ILexiconable*/ {
	
	init {
		setBlockName(name)
		setCreativeTab(AlfheimTab)
		setStepSound(source.stepSound)
		// PORT: useNeighborBrightness — свет неполных блоков 1.20.1 считает сама
//		if (!full) {
//			useNeighborBrightness = true
//		}
		// PORT: твёрдость блока-источника (getBlockHardness ниже) 1.20.1 берёт у состояний плиты: она пишется в них с
		// создания плиты; взрывоустойчивость у плиты своя, как в 1.7.10
		blockHardness = legacyProps(source).blockHardness
	}
	
//	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) =
//		source.getBlockHardness(world, x, y, z)
	
	/* PORT: модель — по текстурам блока-источника (alfheim.port.data.AlfheimBlockStates); выбор колёсиком и шёлковое
	   касание — сама плита (её предмет); лут — таблица плиты (alfheim.port.data.AlfheimBlockLoot)
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int) = source.getIcon(side, meta)!!
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer) = ItemStack(getSingleBlock(), 1, world.getBlockMetadata(x, y, z))
	
	override fun getItemDropped(meta: Int, random: Random?, fortune: Int) = getSingleBlock().toItem()
	
	public override fun createStackedBlock(par1: Int) = ItemStack(getSingleBlock())
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(par1IconRegister: IIconRegister) = Unit
	*/
	
	open fun register() {
		GameRegistry.registerBlock(this, ItemColoredSlabMod::class.java, name)
		// PORT: двойная плита 1.7.10 — состояние type=double этой плиты. Её старое имя у наследников автора — имя
		// одинарной с «SlabFull» вместо «Slab» (irisPlanksSlab3 — irisPlanksSlabFull3)
		LegacyRegistration.alias(name.replaceFirst("Slab", "SlabFull"), this, "type=double")
	}
	
	// PORT: имя предмета плиты (func_150002_b) — ключ блока
//	override fun func_150002_b(i: Int) = name
	
	abstract fun getFullBlock(): BlockSlab
	
	abstract fun getSingleBlock(): BlockSlab
}
