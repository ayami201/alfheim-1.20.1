package alfheim.common.item

// PORT: импорты 1.20.1 (MAPPING.md). Варево Botania 1.7.10 (IBrewItem, IBrewContainer, BotaniaAPI.brewMap) — Botania
// 1.20.1 (BrewItem, BrewContainer, реестр варев); иконки склянки Botania — модель предмета (генерация данных)
import alexsocol.asjlib.*
import alfheim.common.entity.EntityThrownPotion
import alfheim.port.legacy.*
import alfheim.port.legacy.Potion1710 as Potion
import alfheim.port.legacy.botania.Botania
import net.minecraft.ChatFormatting as EnumChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.*
import net.minecraft.world.level.Level as World
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.brew.*
import vazkii.botania.common.brew.BotaniaBrews
import vazkii.botania.common.helper.ItemNBTHelper
import java.awt.Color
import kotlin.math.*

class ItemSplashPotion: ItemMod("splashPotion"), BrewItem, BrewContainer {
	
//	lateinit var itemIconFluid: IIcon
	
	init {
		maxStackSize = 1
	}
	
	// PORT: brewMap 1.7.10 → реестр варев Botania 1.20.1; запасного варева (fallback) в brewMap 1.7.10 не было
	override fun getSubItems(item: Item, tab: Any?, list: MutableList<Any?>) {
		for (brew in BotaniaAPI.instance().brewRegistry!!) {
			if (brew === BotaniaBrews.fallbackBrew) continue
			list.add(getItemForBrew(brew, ItemStack(this)))
		}
//		for (brew in BotaniaAPI.brewMap.keys)
//			list.add(getItemForBrew(BotaniaAPI.brewMap[brew] as Brew, ItemStack(this)))
	}
	
	// PORT: onItemRightClick → use
	override fun use(world: World, player: EntityPlayer, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		if (!world.isRemote) {
			EntityThrownPotion(player, stack).spawn(world)
			
			stack.shrink(1)
//			stack.stackSize--
		}
		
		return InteractionResultHolder.consume(stack)
//		return stack
	}
	
	fun getColor(stack: ItemStack?): Int {
		if (stack != null) {
			val color = Color(getBrew(stack).getColor(stack))
			val add = (sin(Botania.proxy.worldElapsedTicks.D * 0.1) * 16.0).I
			val r = max(0, min(255, color.red + add))
			val g = max(0, min(255, color.green + add))
			val b = max(0, min(255, color.blue + add))
			return (r shl 16) or (g shl 8) or b
		}
		
		return 0xFFFFFF
	}
	
	override fun getColorFromItemStack(stack: ItemStack, pass: Int): Int {
		return if (pass == 0) {
			0xCCCCCFF
		} else getColor(stack)
	}
	
	// PORT: иконки Botania vial0 и vial1_0 — слои 0 и 1 модели предмета (vial и brew_vial_0 Botania 1.20.1)
//	override fun registerIcons(reg: IIconRegister) {
//		itemIcon = IconHelper.forName(reg, "vial" + "0")
//		itemIconFluid = IconHelper.forName(reg, "vial" + "1_0")
//	}
	
	// PORT: addInformation → appendHoverText; строки собираются, как у автора, переводит StatCollector
	override fun appendHoverText(stack: ItemStack, world: World?, list: MutableList<Component>, adv: TooltipFlag) {
		val brew = getBrew(stack)
		addStringToTooltip("${EnumChatFormatting.DARK_PURPLE}${StatCollector.translateToLocalFormatted("botaniamisc.brewOf", StatCollector.translateToLocal(brew.getTranslationKey(stack)))}", list)
//		addStringToTooltip("${EnumChatFormatting.DARK_PURPLE}${StatCollector.translateToLocalFormatted("botaniamisc.brewOf", StatCollector.translateToLocal(brew.getUnlocalizedName(stack)))}", list)
		
		for (effect in brew.getPotionEffects(stack)) {
			val potion = Potion.potionTypes[effect.potionID]
			val format = if (potion.isBadEffect) EnumChatFormatting.RED else EnumChatFormatting.GRAY
			addStringToTooltip("" + format + StatCollector.translateToLocal(effect.effectName) + (if (effect.amplifier == 0) "" else " " + StatCollector.translateToLocal("botania.roman" + (effect.amplifier + 1))) + EnumChatFormatting.GRAY + (if (potion.isInstant) "" else " (" + Potion.getDurationString(effect) + ")"), list)
		}
	}
	
	override fun getItemForBrew(brew: Brew, stack: ItemStack?): ItemStack {
		val brewStack = ItemStack(this)
		setBrew(brewStack, brew)
		return brewStack
	}
	
	// PORT: строка подсказки 1.7.10 → Component
	internal fun addStringToTooltip(s: String, tooltip: MutableList<Component>?) {
		tooltip?.add(Component.literal(s.replace("&".toRegex(), "§")))
	}
//	internal fun addStringToTooltip(s: String, tooltip: MutableList<Any?>?) {
//		tooltip?.add(s.replace("&".toRegex(), "§"))
//	}
	
//	override fun requiresMultipleRenderPasses() = true
//
//	override fun getRenderPasses(metadata: Int) = 2
//
//	override fun getIcon(stack: ItemStack, pass: Int) = (if (pass == 0) itemIcon else itemIconFluid)!!
	
	// PORT: ключ варева — id в реестре Botania 1.20.1 (botania:healing), в 1.7.10 — имя (healing)
	override fun getBrew(stack: ItemStack): Brew {
		val key = ItemNBTHelper.getString(stack, "brewKey", "")
		return BotaniaAPI.instance().brewRegistry!!.get(ResourceLocation.tryParse(key)) ?: BotaniaBrews.fallbackBrew
//		return BotaniaAPI.getBrewFromKey(key)
	}
	
	override fun getManaCost(p0: Brew?, p1: ItemStack?) = p0?.manaCost?.times(1.5)?.I ?: 400
	
	fun setBrew(stack: ItemStack, brew: Brew?) {
		setBrew(stack, BotaniaAPI.instance().brewRegistry!!.getKey(brew ?: BotaniaBrews.fallbackBrew).toString())
//		setBrew(stack, (brew ?: BotaniaAPI.fallbackBrew).key)
	}
	
	fun setBrew(stack: ItemStack, brew: String) {
		ItemNBTHelper.setString(stack, "brewKey", brew)
	}
}
