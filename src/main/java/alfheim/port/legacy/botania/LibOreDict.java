package alfheim.port.legacy.botania;

/**
 * {@code vazkii.botania.common.lib.LibOreDict} r1.8-249: имена Ore Dictionary, под которыми Botania 1.7.10 регистрировала
 * свои материалы и которые автор ставит в рецепты. В Botania 1.20.1 этих имён нет — её материалы в тегах. Здесь
 * только имена, которые нужны коду автора; ингредиент 1.20.1 для каждого — в таблице {@code Ingredients1710}
 * (MAPPING.md, «Ore Dictionary»).
 * <p>
 * Класс написан на Java, как в Botania 1.7.10: код автора берёт имена через {@code import …LibOreDict.*}.
 */
public final class LibOreDict {

	public static final String PESTLE_AND_MORTAR = "pestleAndMortar";
	public static final String LIVING_WOOD = "livingwood";
	public static final String LIVING_ROCK = "livingrock";
	public static final String MANA_STEEL = "ingotManasteel";
	public static final String MANA_PEARL = "manaPearl";
	public static final String MANA_DIAMOND = "manaDiamond";
	public static final String LIVINGWOOD_TWIG = "livingwoodTwig";
	public static final String TERRA_STEEL = "ingotTerrasteel";
	public static final String LIFE_ESSENCE = "eternalLifeEssence";
	public static final String REDSTONE_ROOT = "redstoneRoot";
	public static final String DREAM_WOOD = "dreamwood";
	public static final String ELEMENTIUM = "ingotElvenElementium";
	public static final String PIXIE_DUST = "elvenPixieDust";
	public static final String DRAGONSTONE = "elvenDragonstone";
	public static final String PRISMARINE_SHARD = "shardPrismarine";
	public static final String PLACEHOLDER = "bPlaceholder";
	public static final String RED_STRING = "bRedString";
	public static final String DREAMWOOD_TWIG = "dreamwoodTwig";
	public static final String GAIA_INGOT = "gaiaIngot";
	public static final String ENDER_AIR_BOTTLE = "bEnderAirBottle";
	public static final String MANA_STRING = "manaString";
	public static final String MANASTEEL_NUGGET = "nuggetManasteel";
	public static final String TERRASTEEL_NUGGET = "nuggetTerrasteel";
	public static final String ELEMENTIUM_NUGGET = "nuggetElvenElementium";
	public static final String ROOT = "livingRoot";
	public static final String MANAWEAVE_CLOTH = "clothManaweave";
	public static final String MANA_POWDER = "powderMana";
	public static final String PRISMARINE_BLOCK = "blockPrismarine";
	public static final String BLAZE_BLOCK = "blockBlaze";

	/** Цвета в порядке metadata 1.7.10 — порядок {@code DyeColor} */
	private static final String[] COLORS = { "White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray",
		"LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };

	public static final String[] FLOWER = names("mysticFlower", "");
	public static final String[] DOUBLE_FLOWER = names("mysticFlower", "Double");
	public static final String[] PETAL = names("petal", "");
	public static final String[] DYE = names("dye", "");

	/** Руны в порядке metadata 1.7.10 */
	public static final String[] RUNE = { "runeWaterB", "runeFireB", "runeEarthB", "runeAirB", "runeSpringB", "runeSummerB",
		"runeAutumnB", "runeWinterB", "runeManaB", "runeLustB", "runeGluttonyB", "runeGreedB", "runeSlothB", "runeWrathB",
		"runeEnvyB", "runePrideB" };

	/** Кварц в порядке metadata 1.7.10 */
	public static final String[] QUARTZ = { "quartzDark", "quartzMana", "quartzBlaze", "quartzLavender", "quartzRed",
		"quartzElven", "quartzSunny" };

	private static String[] names(String prefix, String suffix) {
		String[] names = new String[COLORS.length];
		for (int i = 0; i < COLORS.length; i++) names[i] = prefix + COLORS[i] + suffix;
		return names;
	}

	private LibOreDict() {}
}
