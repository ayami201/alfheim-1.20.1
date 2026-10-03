package alfheim.api.lib

// PORT: импорты 1.7.10 — вместе с beacons (КТ-6)
//import cpw.mods.fml.common.registry.GameRegistry
//import net.minecraft.block.Block
//import net.minecraft.init.Blocks

object LibOreDict {
	
	const val DRAGON_ORE = "oreDragonstone"
	const val ELEMENTIUM_ORE = "oreElementium"
	const val ELVEN_QUARTZ_ORE = "oreQuartzElven"
	const val GOLD_ORE = "oreGold"
	const val IFFESAL_ORE = "oreIffesal"
	const val LAPIS_ORE = "oreLapis"
	const val NIFLEUR_ORE = "oreNifleur"
	const val ELVORIUM_INGOT = "ingotElvorium"
	const val MAUFTRIUM_INGOT = "ingotMauftrium"
	const val MUSPELHEIM_POWER_INGOT = "ingotMuspelheimPower"
	const val NIFLHEIM_POWER_INGOT = "ingotNiflheimPower"
	const val ELVORIUM_NUGGET = "nuggetElvorium"
	const val MAUFTRIUM_NUGGET = "nuggetMauftrium"
	const val MUSPELHEIM_ESSENCE = "essenceMuspelheim"
	const val NIFLHEIM_ESSENCE = "essenceNiflheim"
	const val IFFESAL_DUST = "dustIffesal"
	const val FENRIR_FUR = "furFenrir"
	val ARUNE = arrayOf("runePrimalA", "runeMuspelheimA", "runeNiflheimA")
	const val INFUSED_DREAM_TWIG = "twigDreamwoodInfused"
	const val DREAM_WOOD_LOG = "logDeamwood"
	
	// Iridescence
	const val TWIG_THUNDERWOOD = "twigThunderwood"
	const val TWIG_NETHERWOOD = "twigNetherwood"
	const val SPLINTERS_THUNDERWOOD = "splinterThunderwood"
	const val SPLINTERS_NETHERWOOD = "splinterNetherwood"
	const val COAL_NETHERWOOD = "coalFlame"
	const val HOLY_PENDANT = "holyPendant"
	const val IRIS_WOOD = "irisWood"
	const val IRIS_LEAVES = "irisLeaves"
	const val IRIS_DIRT = "irisDirt"
	val WOOD = Color.entries.map { "$IRIS_WOOD${it}" }.toTypedArray()
	fun WOOD(color: Color) = WOOD[color.ordinal]
	val LEAVES = Color.entries.map { "$IRIS_LEAVES${it}" }.toTypedArray()
	fun LEAVES(color: Color) = LEAVES[color.ordinal]
	val DIRT = Color.entries.map { "$IRIS_DIRT${it}" }.toTypedArray()
	fun DIRT(color: Color) = DIRT[color.ordinal]
	val DYES = Color.entries.mapTo(ArrayList(Color.entries.size)) { "dye${it}" }.apply { removeLast() }.toTypedArray()
	fun DYES(color: Color) = DYES[color.ordinal]
	const val FLORAL_POWDER = "dyeFloralPowder"
	const val PETAL_ANY = "petalMystic"
	const val MUSHROOM = "mushroomShimmer"
	const val RAINBOW_PETAL = "petalRainbow"
	const val RAINBOW_FLOWER = "mysticFlowerRainbow"
	const val RAINBOW_DOUBLE_FLOWER = "${RAINBOW_FLOWER}Double"
	const val RAINBOW_QUARTZ = "quartzRainbow"
	const val RAINBOW_QUARTZ_BLOCK = "blockQuartzRainbow"
	
	val ALT_TYPES = arrayOf("Dry", "Golden", "Vivid", "Scorched", "Infused", "Mutated", "Wisdom", "Dreamwood")
	
	const val EMERALD = "gemEmerald"
	const val GLOWSTONE_DUST = "dustGlowstone"
	const val REDSTONE_DUST = "dustRedstone"
	
	enum class Color {
		White, Orange, Magenta, LightBlue, Yellow, Lime, Pink, Gray, LightGray, Cyan, Purple, Blue, Brown, Green, Red, Black, Rainbow, Aurora;
		
		val I get() = ordinal
	}
	
	// PORT: КТ-6 — маяки для призыва Гайи в Альвхейме (ItemElvenResource.onItemUse)
//	val beacons: Array<Block?>
//		get() = arrayOf(Blocks.beacon, GameRegistry.findBlock("etfuturum", "beacon"), GameRegistry.findBlock("chisel", "beacon"))
}