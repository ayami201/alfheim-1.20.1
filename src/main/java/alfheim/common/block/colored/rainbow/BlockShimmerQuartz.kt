package alfheim.common.block.colored.rainbow

// PORT: импорты 1.20.1; кварц Botania 1.7.10 (блок, плита, лестница) — alfheim.port.legacy.botania (MAPPING.md, «Botania»)
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.*
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock as BlockSlab

/**
 * @author WireSegal
 * Created at 7:59 PM on 2/13/16.
 */
// PORT: вариант metadata — отдельный блок (SPEC, Р-5): 0 — блок, 1 — резной, 2 — колонна; создают массивом
// `Array(3) { BlockShimmerQuartz(it) }`
class BlockShimmerQuartz(meta: Int): BlockSpecialQuartz("Shimmer", meta) {
//class BlockShimmerQuartz: BlockSpecialQuartz("Shimmer") {
	
	init {
		setCreativeTab(AlfheimTab)
		// PORT: анимированные текстуры 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
	}
	
	/* PORT: иконки → модели вариантов (alfheim.port.data.AlfheimBlockStates): бока — iconNames; торцы блока — его же
	   иконка, резного и колонны — chiseledShimmerQuartz1 и pillarShimmerQuartz1; анимация — .mcmeta
	@SubscribeEvent
	fun registerIcons(e: TextureStitchEvent.Pre) {
		if (e.map.textureType == 0) {
			this.specialQuartzIcons = arrayOfNulls(this.iconNames.size)
			
			for (i in this.specialQuartzIcons.indices) {
				if (this.iconNames[i] == null)
					this.specialQuartzIcons[i] = this.specialQuartzIcons[i - 1]
				else
					this.specialQuartzIcons[i] = InterpolatedIconHelper.forName(e.map, this.iconNames[i].replace("decor/", ""), "decor")
			}
			
			this.specialQuartzTopIcon = this.specialQuartzIcons[0]
			this.chiseledSpecialQuartzIcon = InterpolatedIconHelper.forName(e.map, "chiseled${type}Quartz1", "decor")
			this.pillarSpecialQuartzIcon = InterpolatedIconHelper.forName(e.map, "pillar${type}Quartz1", "decor")
		}
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(par1IconRegister: IIconRegister) = Unit
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.shimmer
}

// PORT: двойная плита 1.7.10 (full) — состояние type=double одинарной (BlockModSlab): getFullBlock и getSingleBlock
// возвращают одну и ту же плиту
class BlockShimmerQuartzSlab(val block: BlockShimmerQuartz, val full: Boolean): BlockSpecialQuartzSlab(block, full) {
	
	init {
		setCreativeTab(AlfheimTab)
	}
	
	override fun getSingleBlock() = AlfheimBlocks.shimmerQuartzSlab as BlockSlab
	
	override fun getFullBlock() = AlfheimBlocks.shimmerQuartzSlabFull as BlockSlab
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = block.getEntry(world, x, y, z, player, lexicon)
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemShimmerSlabMod::class.java, "quartzSlab${block.type}${if (full) "Full" else "Half"}")
		// PORT: двойная плита 1.7.10 — состояние type=double этой плиты (BlockModSlab); её старое имя — quartzSlabShimmerFull
		LegacyRegistration.alias("quartzSlab${block.type}Full", this, "type=double")
	}
}

class BlockShimmerQuartzStairs(val block: BlockShimmerQuartz): BlockSpecialQuartzStairs(block) {
	
	lateinit var unlocName: String
	
	init {
		setCreativeTab(AlfheimTab)
	}
	
	override fun setBlockName(par1Str: String): Block {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, par1Str)
		unlocName = "tile.$par1Str"
		return this
	}
	
	override fun getUnlocalizedName() = unlocName
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = block.getEntry(world, x, y, z, player, lexicon)
}


