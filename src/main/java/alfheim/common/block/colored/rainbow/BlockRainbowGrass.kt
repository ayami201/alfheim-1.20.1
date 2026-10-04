package alfheim.common.block.colored.rainbow

// PORT: импорты 1.20.1; BlockTallGrass 1.7.10 — alfheim.port.legacy.TallGrass1710 (MAPPING.md, «Растения»); прокси
// Botania 1.7.10 — alfheim.port.legacy.botania.Botania
import alexsocol.asjlib.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockAuroraDirt
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.*
import alfheim.common.item.block.ItemRainbowGrassMod
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.Botania
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.*
import net.minecraftforge.api.distmarker.*
import vazkii.botania.xplat.BotaniaConfig
import java.awt.Color

// PORT: вариант metadata — отдельный блок (SPEC, Р-5): номер варианта — meta (GRASS … BURIED ниже), создают массивом
// `Array(5) { BlockRainbowGrass(it) }`. Thaumcraft выпал (SPEC, п. 7) — IInfusionStabiliser; КТ-9 — лексикон
// (ILexiconable); IPickupAchievement — ниже, у getAchievementOnPickup
//@Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.crafting.IInfusionStabiliser", striprefs = true)
class BlockRainbowGrass(val meta: Int): TallGrass1710()/*, ILexiconable, IPickupAchievement, IInfusionStabiliser*/ {
	
	// PORT: иконки вариантов → модели (alfheim.port.data.AlfheimBlockStates); анимация — .mcmeta
//	var flowerIcon: IIcon? = null
//	var glowingIcon: IIcon? = null
	
	override val variant get() = meta
	
	companion object {
		
		const val GRASS = 0
		const val AURORA = 1
		const val FLOWER = 2
		const val GLIMMER = 3
		const val BURIED = 4
	}
	
	init {
		setBlockName("rainbowGrass")
		setCreativeTab(AlfheimTab)
		setStepSound(soundTypeGrass)
		// PORT: анимированные текстуры 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
		
		// PORT: рамка и свечение по metadata у варианта постоянные — пишутся при создании блока (ниже)
		setBlockBoundsBasedOnState()
		for (state in stateDefinition.possibleStates) state.lightEmission = getLightValue()
	}
	
	// PORT: Thaumcraft выпал (SPEC, п. 7)
//	override fun canStabaliseInfusion(world: World, x: Int, y: Int, z: Int): Boolean {
//		return if (world.getBlockMetadata(x, y, z) == GLIMMER) ConfigHandler.enableThaumcraftStablizers else false
//	}
	
	fun setBlockBoundsBasedOnState() {
		when (meta) {
//	override fun setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int) {
//		when (world.getBlockMetadata(x, y, z)) {
			GRASS, AURORA   -> setBlockBounds(0.1f, 0f, 0.1f, 0.9f, 0.8f, 0.9f)
			FLOWER, GLIMMER -> setBlockBounds(0.3f, 0f, 0.3f, 0.8f, 1f, 0.8f)
			BURIED          -> setBlockBounds(0f, 0f, 0f, 1f, 0.1f, 1f)
			else            -> setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f)
		}
	}
	
	override fun randomDisplayTick(world: World, x: Int, y: Int, z: Int, rand: Random) {
		// PORT: metadata — номер варианта блока (SPEC, Р-5)
		val meta = this.meta
//		val meta = world.getBlockMetadata(x, y, z)
		val color = Color(ItemIridescent.rainbowColor())
		
		Botania.proxy.setSparkleFXNoClip(true)
		
		when (meta) {
			FLOWER, GLIMMER -> {
				// PORT: настройка клиента Botania 1.20.1
				if (rand.nextDouble() < BotaniaConfig.client().flowerParticleFrequency())
//				if (rand.nextDouble() < ConfigHandler.flowerParticleFrequency)
					Botania.proxy.sparkleFX(world, x.D + 0.3 + rand.nextFloat() * 0.5, y.D + 0.5 + rand.nextFloat() * 0.5, z.D + 0.3 + rand.nextFloat() * 0.5, color.red / 255f, color.green / 255f, color.blue / 255f, rand.nextFloat(), 5)
			}
			
			BURIED          -> Botania.proxy.sparkleFX(world, x.D + 0.3 + rand.nextFloat() * 0.5, y.D + 0.1 + rand.nextFloat() * 0.1, z.D + 0.3 + rand.nextFloat() * 0.5, color.red / 255f, color.green / 255f, color.blue / 255f, rand.nextFloat(), 5)
		}
		
		Botania.proxy.setSparkleFXNoClip(false)
	}
	
	fun getLightValue() = when (meta) {
//	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int) = when (world.getBlockMetadata(x, y, z)) {
		GLIMMER -> 15
		BURIED  -> 3
		else    -> 0
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun getBlockColor() = 0xFFFFFF
	
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int) = 0xFFFFFF
	
	// PORT: metadata — номер варианта блока (SPEC, Р-5)
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int) = if (meta == AURORA) BlockAuroraDirt.getBlockColor(x, y, z) else 0xFFFFFF
//	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int) = if (world.getBlockMetadata(x, y, z) == AURORA) BlockAuroraDirt.getBlockColor(x, y, z) else 0xFFFFFF
	
	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, remote: Boolean): Boolean {
		val meta = this.meta
//		val meta = world.getBlockMetadata(x, y, z)
		return meta == GRASS || meta == AURORA || meta == FLOWER || meta == BURIED
	}
	
	override fun func_149853_b(world: World, random: Random, x: Int, y: Int, z: Int) {
		var meta = this.meta
//		var meta = world.getBlockMetadata(x, y, z)
		if (meta == GRASS || meta == AURORA || meta == FLOWER || meta == BURIED) {
			// PORT: двойная трава — массив вариантов (SPEC, Р-5); у двойного цветка вариант один, metadata FLOWER (2) в нём не
			// хранится (BUGS.md, «Уже не воспроизводится в порту»). Обе половины ставит DoublePlantBlock.placeAt, флаги те же
			var block = AlfheimBlocks.rainbowTallGrass.getOrNull(meta)
//			var block = AlfheimBlocks.rainbowTallGrass
			
			if (meta == FLOWER || meta == BURIED) {
				block = AlfheimBlocks.rainbowTallFlower
				meta = FLOWER
			}
			
			if (block!!.canPlaceBlockAt(world, x, y, z)) {
				DoublePlantBlock.placeAt(world, block.defaultBlockState(), BlockPos(x, y, z), 2)
//				world.setBlock(x, y, z, block, meta, 2)
//				world.setBlock(x, y + 1, z, block, 8, 2)
			}
		}
	}
	
	internal fun register(name: String) {
		GameRegistry.registerBlock(this, ItemRainbowGrassMod::class.java, name)
	}
	
	override fun setBlockName(par1Str: String): Block {
		register(par1Str)
		return super.setBlockName(par1Str)
	}
	
	/* PORT: лут — таблица варианта (alfheim.port.data.AlfheimBlockLoot): трава и авроровая трава — как трава (с ножницами —
	   сама, иначе семена), цветок и мерцающий цветок — сами, закопанные лепестки — радужный лепесток
	override fun getDrops(world: World?, x: Int, y: Int, z: Int, meta: Int, fortune: Int): ArrayList<ItemStack>? {
		return when (meta) {
			GRASS, AURORA -> super.getDrops(world, x, y, z, meta, fortune)
			BURIED        -> arrayListOf(ElvenResourcesMetas.RainbowPetal.stack)
			else          -> arrayListOf(ItemStack(this, 1, meta))
		}
	}
	
	override fun getItemDropped(meta: Int, rand: Random?, fortune: Int) = when (meta) {
		GRASS, AURORA -> null
		BURIED        -> AlfheimItems.elvenResource
		else          -> this.toItem()
	}
	
	override fun onSheared(item: ItemStack, world: IBlockAccess, x: Int, y: Int, z: Int, fortune: Int): ArrayList<ItemStack> {
		val ret = ArrayList<ItemStack>()
		ret.add(ItemStack(this, 1, world.getBlockMetadata(x, y, z)))
		return ret
	}
	
	override fun isShearable(item: ItemStack?, world: IBlockAccess, x: Int, y: Int, z: Int) = run {
		val meta = world.getBlockMetadata(x, y, z)
		meta == GRASS || meta == AURORA
	}
	
	*/
	
	/* PORT: варианты во вкладке — отдельные блоки (AlfheimTab); иконки → модели вариантов (alfheim.port.data.AlfheimBlockStates):
	   авроровая трава — текстура травы ириса, закопанные лепестки — без граней, одни искры: текстура закопанных лепестков
	   Botania 1.7.10 полностью прозрачная, как и модель Botania 1.20.1
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in 0..3)
			list.add(ItemStack(item, 1, i))
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(iconRegister: IIconRegister) = Unit
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0) {
			blockIcon = InterpolatedIconHelper.forBlock(event.map, this)
			flowerIcon = InterpolatedIconHelper.forBlock(event.map, this, "Flower")
			glowingIcon = InterpolatedIconHelper.forBlock(event.map, this, "FlowerGlimmer")
		}
	}
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int): IIcon? {
		return when (meta) {
			AURORA  -> AlfheimBlocks.irisGrass.getIcon(side, 0)
			FLOWER  -> flowerIcon
			GLIMMER -> glowingIcon
			BURIED  -> ModBlocks.buriedPetals.getIcon(side, 0)
			else    -> blockIcon
		}
	}
	*/
	
	/* PORT: КТ-9 — лексикон
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, stack: ItemStack?) =
		when (world.getBlockMetadata(x, y, z)) {
			GRASS, AURORA  -> AlfheimLexiconData.pastoralSeeds
			FLOWER, BURIED -> AlfheimLexiconData.rainbowFlora
			GLIMMER        -> LexiconData.shinyFlowers
			else           -> null
		}
	*/
	
	// PORT: в 1.7.10 не срабатывало: Botania спрашивает IPickupAchievement у предмета, а предмет травы
	// (ItemRainbowGrassMod) его не реализует — достижения за подобранный радужный цветок не было (BUGS.md)
//	override fun getAchievementOnPickup(stack: ItemStack, player: EntityPlayer?, item: EntityItem?) = if (stack.meta == FLOWER) ModAchievements.flowerPickup else null
}