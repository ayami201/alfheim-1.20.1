package alfheim.common.block.colored.rainbow

// PORT: импорты 1.20.1; BlockMushroom 1.7.10 — alfheim.port.legacy.Mushroom1710 (MAPPING.md, «Растения»); рог Botania
// 1.7.10 — alfheim.port.legacy.botania.IHornHarvestable; прокси Botania 1.7.10 — alfheim.port.legacy.botania.Botania
import alexsocol.asjlib.*
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import alfheim.port.legacy.Sheep1710 as EntitySheep
import alfheim.port.legacy.botania.*
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.*
import vazkii.botania.xplat.BotaniaConfig

// PORT: Thaumcraft выпал (SPEC, п. 7) — IInfusionStabiliser; КТ-9 — лексикон (ILexiconable)
//@Interface(modid = "Thaumcraft", iface = "thaumcraft.api.crafting.IInfusionStabiliser", striprefs = true)
class BlockRainbowMushroom: Mushroom1710(), IHornHarvestable/*, IInfusionStabiliser, ILexiconable*/ {
//class BlockRainbowMushroom: BlockMushroom(), IInfusionStabiliser, IHornHarvestable, ILexiconable {
	
	var originalLight: Int = 0
	
	init {
		setBlockName("rainbowMushroom")
		setCreativeTab(AlfheimTab)
		setLightLevel(0.2f)
		setHardness(0f)
		setStepSound(soundTypeGrass)
		setBlockBounds(0.3f, 0f, 0.3f, 0.8f, 1f, 0.8f)
		tickRandomly = false
		// PORT: анимированную текстуру 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random) = Unit
	
	override fun canBlockStay(world: World, x: Int, y: Int, z: Int): Boolean {
		// PORT: высота мира 1.7.10 (0–255) — границы мира 1.20.1; земля с metadata 2 (подзол) — блок 1.20.1, поля ванилы
		// 1.20.1 — заглавными
		return if (y in world.minBuildHeight until world.maxBuildHeight) {
//		return if (y in 0..255) {
			val block = world.getBlock(x, y - 1, z)
			block === Blocks.MYCELIUM || block === Blocks.PODZOL || block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this)
//			block === Blocks.mycelium || block === Blocks.dirt && world.getBlockMetadata(x, y - 1, z) == 2 || block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this)
		} else {
			false
		}
	}
	
	// PORT: во вкладке — сам блок (AlfheimTab)
//	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
//		list.add(ItemStack(item))
//	}
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
		return super.setBlockName(name)
	}
	
	/* PORT: иконка → модель (alfheim.port.data.AlfheimBlockStates): крест с текстурой rainbowMushroom; анимация — .mcmeta
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0)
			blockIcon = InterpolatedIconHelper.forBlock(event.map, this)
	}
	*/
	
	override fun setLightLevel(lvl: Float): Block {
		originalLight = (lvl * 15).I
		return super.setLightLevel(lvl)
	}
	
	// PORT: цветной свет Easy Colored Lights: мода нет на 1.20.1, метод не вызывается — как у автора без мода; белый свет
	// гриба даёт мод Colorful Lighting по файлу light/emitters.json (alfheim.port.data.ColoredLights)
//	@Method(modid = "easycoloredlights")
//	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int) = ColoredLightHelper.getPackedColor(world.getBlockMetadata(x, y, z), originalLight)
	
	/* PORT: иконка — модель (выше); лут — сам гриб (alfheim.port.data.AlfheimBlockLoot)
	override fun getIcon(side: Int, meta: Int) = blockIcon!!
	
	override fun damageDropped(meta: Int) = meta
	*/
	
	override fun randomDisplayTick(world: World, x: Int, y: Int, z: Int, rand: Random) {
		// PORT: генератор мира 1.20.1 — random
		val color = EntitySheep.fleeceColorTable[world.random.nextInt(16)]
//		val color = EntitySheep.fleeceColorTable[world.rand.nextInt(16)]
		// PORT: настройка клиента Botania 1.20.1
		if (rand.nextDouble() < BotaniaConfig.client().flowerParticleFrequency() * 0.25) {
//		if (rand.nextDouble() < ConfigHandler.flowerParticleFrequency * 0.25) {
			Botania.proxy.sparkleFX(world, x.D + 0.3 + rand.nextFloat().D * 0.5, y.D + 0.5 + rand.nextFloat().D * 0.5, z.D + 0.3 + rand.nextFloat().D * 0.5, color[0], color[1], color[2], rand.nextFloat(), 5)
		}
	}
	
	// PORT: Thaumcraft выпал (SPEC, п. 7)
//	override fun canStabaliseInfusion(world: World, x: Int, y: Int, z: Int) = ConfigHandler.enableThaumcraftStablizers
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = LexiconData.mushrooms!!
	
	override fun canHornHarvest(world: World, x: Int, y: Int, z: Int, stack: ItemStack, hornType: IHornHarvestable.EnumHornType) = false
	
	override fun hasSpecialHornHarvest(world: World, x: Int, y: Int, z: Int, stack: ItemStack, hornType: IHornHarvestable.EnumHornType) = false
	
	override fun harvestByHorn(world: World, x: Int, y: Int, z: Int, stack: ItemStack, hornType: IHornHarvestable.EnumHornType) = Unit
}