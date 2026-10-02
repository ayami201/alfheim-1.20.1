package alexsocol.asjlib.extendables.block

// PORT: импорты 1.7.10 заменены на 1.20.1 (блок 1.7.10 — alfheim.port.legacy.Block1710, MAPPING.md)
import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.ItemBlockMetaName
import alfheim.port.legacy.*
import net.minecraft.world.level.block.Block
import kotlin.math.max

// PORT: metadata = разные вещи → в 1.20.1 каждый вариант — отдельный блок (SPEC, Р-5): номер варианта — meta,
// создают их массивом `Array(subtypes) { BlockX(it) }`. Вкладка в 1.20.1 не нужна блоку (tab — любой объект)
open class BlockModMeta @JvmOverloads constructor(mat: Material, val subtypes: Int, val modid: String, val name: String, tab: Any? = null, hard: Float = 1f, harvTool: String? = "pickaxe", harvLvl: Int = 1, resist: Float = 5f, val folder: String = "", val meta: Int = 0): Block1710(mat) {
	
	// PORT: иконки 1.7.10 → модели блоков (alfheim.port.data); текстура варианта — "$modid:$folder$name$meta"
//	lateinit var icons: Array<IIcon>
	
	override val variant get() = if (subtypes > 1) meta else null
	
	init {
		setBlockName(name)
		setCreativeTab(tab)
		setHardness(hard)
		setHarvestLevel(harvTool, harvLvl)
		setResistance(max(resist, hard * 5f))
		setStepSound(ASJUtilities.soundFromMaterial(mat))
	}
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockMetaName::class.java, name)
		return super.setBlockName(name)
	}
	
	/* PORT: иконки → модели (alfheim.port.data); варианты во вкладке — отдельные блоки; предмет и есть вариант
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = Array(subtypes) { reg.registerIcon("$modid:$folder$name${if (subtypes > 1) it else ""}") }
	}
	
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in 0 until subtypes) list.add(ItemStack(item, 1, i))
	}
	
	override fun getIcon(side: Int, meta: Int) = icons.safeGet(meta)
	
	override fun damageDropped(meta: Int) = meta
	
	override fun getDamageValue(world: World, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z)
	*/
}
