package alfheim.common.block

// PORT: импорты 1.20.1; BlockPane 1.7.10 — Pane1710 (MAPPING.md, «Блоки и предметы»)
import alexsocol.asjlib.extendables.ItemBlockMetaName
import alfheim.port.legacy.*
import net.minecraft.world.level.block.Block

// PORT: вариант metadata — отдельный блок (SPEC, Р-5): номер варианта — meta, создают их массивом
// `Array(subtypes) { BlockPaneMeta(…, meta = it) }`
open class BlockPaneMeta @JvmOverloads constructor(mat: Material, val subtypes: Int, val texName: String, val folder: String = "", val meta: Int = 0): Pane1710(texName, "${texName}Top", mat, true) {
	
	override val variant get() = if (subtypes > 1) meta else null
	
	// PORT: иконки → модели панели (alfheim.port.data.AlfheimBlockStates): плоскость — "$MODID:$folder$texName$meta",
	// торец — "$MODID:$folder$texName${meta}Top"; рендер RenderBlockShrinePanel 1.7.10 — модель панели 1.20.1
//	lateinit var texture: Array<IIcon>
//	lateinit var textureTop: Array<IIcon>
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockMetaName::class.java, name)
		return super.setBlockName(name)
	}
	
	/*
	override fun registerBlockIcons(reg: IIconRegister) {
		texture = Array(subtypes) {
			reg.registerIcon("${ModInfo.MODID}:$folder$texName$it")
		}
		
		textureTop = Array(subtypes) {
			reg.registerIcon("${ModInfo.MODID}:$folder$texName${it}Top")
		}
	}
	
	override fun getSubBlocks(block: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		texture.indices.mapTo(list) { ItemStack(block, 1, it) }
	}
	
	override fun damageDropped(meta: Int) = meta
	
	override fun getIcon(side: Int, meta: Int) = texture[max(0, min(meta, texture.size - 1))]
	
	fun getTopIcon(meta: Int) = textureTop[max(0, min(meta, texture.size - 1))]
	
	override fun getRenderType() = LibRenderIDs.idShrinePanel
	*/
}