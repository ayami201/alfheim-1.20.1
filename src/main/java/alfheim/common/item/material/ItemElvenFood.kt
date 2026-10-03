package alfheim.common.item.material

// PORT: импорты 1.20.1 (MAPPING.md); ItemFood и зелья 1.7.10 — alfheim.port.legacy
import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenFoodMetas.*
import alfheim.port.legacy.*
import alfheim.port.legacy.Potion1710 as Potion
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.*
import net.minecraft.world.level.Level as World
import vazkii.botania.common.item.BotaniaItems as ModItems
import kotlin.math.*

// PORT: вариант metadata — отдельный предмет (SPEC, Р-5): номер варианта — meta, имя варианта — имя из ElvenFoodMetas
// (Lembas → alfheim:lembas), создают их массивом `Array(entries.size) { ItemElvenFood(it) }`
class ItemElvenFood(val meta: Int): ItemFood1710(0, 0f, false) {
	
	override val variant get() = meta
	
	override val variantName get() = entries[meta].name
	
	// PORT: иконки → модели предметов (alfheim.port.data.AlfheimItemModels)
//	lateinit var icons: List<IIcon>
	
	// #### ItemMod ####
	
	init {
		setHasSubtypes(true)
		creativeTab = AlfheimTab
		unlocalizedName = "ElvenFood"
	}
	
	override fun setUnlocalizedName(name: String): Item {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	// PORT: getItemStackDisplayName → getName, как в ItemMod
	override fun getName(stack: ItemStack): Component = Component.literal(
		StatCollector.translateToLocal(getDescriptionId(stack)).trim().replace("&".toRegex(), "\u00a7"))
//	override fun getItemStackDisplayName(stack: ItemStack) =
//		super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		getUnlocalizedName(stack).replace("item\\.".toRegex(), "item.${ModInfo.MODID}:") + ".${entries[stack.meta].name}"
	
	// PORT: иконки → модели предметов (alfheim.port.data.AlfheimItemModels): текстура варианта — materials/food/<имя>
	/*
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		icons = entries.map { IconHelper.forName(reg, it.name, "materials/food") }
		iconCC = IconHelper.forName(reg, "cc", "materials/food")
	}
	
	override fun getIconFromDamage(meta: Int) = icons.safeGet(meta)
	*/
	
	// #### ItemFood ####
	
	// PORT: getMaxItemUseDuration → getUseDuration
	override fun getUseDuration(stack: ItemStack) = when (entries.getOrNull(stack.meta)) {
		DreamCherry,
		Nectar       -> 8
		Lembas       -> 64
		else         -> super.getUseDuration(stack)
	}
	
	// foodLevel
	override fun func_150905_g(stack: ItemStack): Int {
		return when (entries.getOrNull(stack.meta)) {
			Lembas                 -> 20
			RedGrapes, WhiteGrapes -> 2
			Nectar                 -> 1
			RedWine,
			WhiteWine,
			Champagne,
			Beer                   -> 3
			JellyBottle            -> 3
			JellyBread             -> 6
			JellyCod               -> 9
			DreamCherry,
			TreeBerryBarrier,
			TreeBerryCalico,
			TreeBerryCircuit,
			TreeBerryLightning,
			TreeBerryNether,
			TreeBerrySealing       -> 2
			
			null                   -> 0
		}
	}
	
	// foodSaturationLevel
	override fun func_150906_h(stack: ItemStack): Float {
		return when (entries.getOrNull(stack.meta)) {
			Lembas                 -> 5f
			RedGrapes, WhiteGrapes -> 0.3f
			Nectar                 -> 0.15f
			RedWine,
			WhiteWine,
			Champagne,
			Beer                   -> 0.1f
			JellyBottle            -> 0.5f
			JellyBread             -> 0.8f
			JellyCod               -> 1.2f
			DreamCherry,
			TreeBerryBarrier,
			TreeBerryCalico,
			TreeBerryCircuit,
			TreeBerryLightning,
			TreeBerryNether,
			TreeBerrySealing       -> 0.3f
			
			null                   -> 0f
		}
	}
	
	val drinkables = arrayOf(RedWine.I, WhiteWine.I, Champagne.I, Beer.I, JellyBottle.I)
	
	fun isAlwaysEdible(stack: ItemStack) = stack.meta in drinkables || stack.meta in TreeBerryBarrier.I..TreeBerrySealing.I
	
	// PORT: getItemUseAction → getUseAnimation
	override fun getUseAnimation(stack: ItemStack) = if (stack.meta in drinkables) UseAnim.DRINK else UseAnim.EAT
//	override fun getItemUseAction(stack: ItemStack) = if (stack.meta in drinkables) EnumAction.drink else EnumAction.eat
	
	// PORT: onItemRightClick → use; setItemInUse → startUsingItem
	override fun use(world: World, player: EntityPlayer, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		if (isAlwaysEdible(stack) || player.canEat(false)) {
			player.startUsingItem(hand)
			return InteractionResultHolder.consume(stack)
		}
//			player.setItemInUse(stack, getMaxItemUseDuration(stack))
		
		return InteractionResultHolder.fail(stack)
	}
	
	// PORT: onEaten → finishUsingItem; в 1.7.10 ест только игрок
	override fun finishUsingItem(stack: ItemStack, world: World, entity: LivingEntity): ItemStack {
		val player = entity as? EntityPlayer ?: return stack
		if (!world.isRemote) {
			ElvenFoodMetas.entries[stack.meta].potion?.let {
				if (it == Potion.regeneration.id || it == AlfheimConfigHandler.potionIDBeer) {
					if (!player.isPotionActive(it))
						player.addPotionEffect(PotionEffect(it, 2400, 0))
					else {
						val pe = player.getActivePotionEffect(it)!!
						player.addPotionEffect(PotionEffect(it, max(2400, pe.duration), min(4, pe.amplifier + 1)))
					}
					
					return@let
				}
				
				var amp = 0
				val id = if (it == -1) {
					amp = 2
					Potion.potionTypes.shuffled().filterNotNull().random(player.rng)!!.id
				} else it
				
				// PORT: мгновенное зелье (лечение, урон) эффектом в 1.7.10 ничего не делало (isReady — false), в 1.20.1 оно
				// срабатывает каждый тик; поэтому такое зелье не накладывается
				if (Potion.byId(id)!!.isInstantenous) return@let
				player.addPotionEffect(PotionEffectU(id, 7200, amp))
			}
			
			if (isCC(stack) && !ItemNBTHelper.getBoolean(stack, TAG_NO_MEME, false))
				player.playSoundAtEntity("${ModInfo.MODID}:ccj", 10f, 1f)
		}
		
		// PORT: пустой стак 1.20.1 теряет свой предмет (и вариант), поэтому остаток определяется до того, как стак съеден
		val container = getContainerItem(stack)
		val ret = super.finishUsingItem(stack, world, player)
		
		return container ?: ret
//		val ret = super.onEaten(stack, world, player)
//		
//		return getContainerItem(stack) ?: ret
	}
	
	// #### Item ####
	
	override fun hasContainerItem(stack: ItemStack) = getContainerItem(stack) != null
	
	override fun getContainerItem(stack: ItemStack) = when (entries.getOrNull(stack.meta)) {
		RedWine, WhiteWine, Champagne, Beer -> ElvenResourcesMetas.Jug.stack
		JellyBottle                         -> ItemStack(ModItems.flask)
//		JellyBottle                         -> ItemStack(ModItems.vial, 1, 1)
		else                                -> null
	}
	
	// PORT: вариант — отдельный предмет: свой стак во вкладку выдаёт Item1710.getSubItems
//	override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) =
//		ElvenFoodMetas.entries.indices.forEach { list.add(ItemStack(item, 1, it)) }
	
	override fun getItemStackLimit(stack: ItemStack) =
		if (stack.meta in drinkables) 1 else super.getItemStackLimit(stack)
	
	// #### Meme ####
	
	// PORT: иконка → свойство модели alfheim:cc: модель пива подменяет иконку на materials/food/cc
	// (alfheim.port.data.AlfheimItemModels, значение свойства — alfheim.port.client.AlfheimModels)
	/*
	lateinit var iconCC: IIcon
	
	override fun getIcon(stack: ItemStack, pass: Int) = if (isCC(stack)) iconCC else super.getIcon(stack, pass)
	
	override fun getIconIndex(stack: ItemStack) = if (isCC(stack)) iconCC else super.getIconIndex(stack)
	*/
	
	companion object {
		const val TAG_NO_MEME = "nomeme"
		// PORT: displayName 1.7.10 (имя стака, своё — если его дали на наковальне) → hoverName
		fun isCC(stack: ItemStack) = stack.meta == Beer.I && stack.hoverName.string.trim().equals("Cerveza Cristal", ignoreCase = true)
//		fun isCC(stack: ItemStack) = stack.meta == Beer.I && stack.displayName.trim().equals("Cerveza Cristal", ignoreCase = true)
	}
	
	// PORT: имена 1.7.10
	private val EntityPlayer.rng get() = random
}

enum class ElvenFoodMetas(val potion: Int? = null) {
	
	Lembas,
	RedGrapes,
	WhiteGrapes,
	Nectar,
	RedWine(Potion.regeneration.id),
	WhiteWine(AlfheimConfigHandler.potionIDWhiteWine),
	Champagne(AlfheimConfigHandler.potionIDChampagne),
	JellyBottle,
	JellyBread,
	JellyCod,
	DreamCherry,
	TreeBerryBarrier(AlfheimConfigHandler.potionIDWtfBerry0),
	TreeBerryCalico(-1),
	TreeBerryCircuit(AlfheimConfigHandler.potionIDWtfBerry2),
	TreeBerryLightning(AlfheimConfigHandler.potionIDWtfBerry3),
	TreeBerryNether(AlfheimConfigHandler.potionIDWtfBerry4),
	TreeBerrySealing(AlfheimConfigHandler.potionIDWtfBerry5),
	Beer(AlfheimConfigHandler.potionIDBeer),
	;
	
	val I get() = ordinal
	
	val stack get() = stack()
	fun stack(size: Int = 1) = ItemStack(AlfheimItems.elvenFood[I], size)
//	fun stack(size: Int = 1) = ItemStack(AlfheimItems.elvenFood, size, I)
}