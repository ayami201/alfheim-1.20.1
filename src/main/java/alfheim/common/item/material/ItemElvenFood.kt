package alfheim.common.item.material

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenFoodMetas.*
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.potion.*
import net.minecraft.util.IIcon
import net.minecraft.world.World
import vazkii.botania.common.item.ModItems
import kotlin.math.*

class ItemElvenFood: ItemFood(0, 0f, false) {
	
	lateinit var icons: List<IIcon>
	
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
	
	override fun getItemStackDisplayName(stack: ItemStack) =
		super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		getUnlocalizedName(stack).replace("item\\.".toRegex(), "item.${ModInfo.MODID}:") + ".${entries[stack.meta].name}"
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		icons = entries.map { IconHelper.forName(reg, it.name, "materials/food") }
	}
	
	override fun getIconFromDamage(meta: Int) = icons.safeGet(meta)
	
	// #### ItemFood ####
	
	// foodLevel
	override fun func_150905_g(stack: ItemStack): Int {
		return when (entries.getOrNull(stack.meta)) {
			Lembas                        -> 20
			RedGrapes, WhiteGrapes        -> 2
			Nectar                        -> 1
			RedWine, WhiteWine, Champagne -> 3
			JellyBottle                   -> 3
			JellyBread                    -> 6
			JellyCod                      -> 9
			DreamCherry,
			TreeBerryBarrier,
			TreeBerryCalico,
			TreeBerryCircuit,
			TreeBerryLightning,
			TreeBerryNether,
			TreeBerrySealing              -> 2
			
			null                          -> 0
		}
	}
	
	// foodSaturationLevel
	override fun func_150906_h(stack: ItemStack): Float {
		return when (entries.getOrNull(stack.meta)) {
			Lembas                        -> 5f
			RedGrapes, WhiteGrapes        -> 0.3f
			Nectar                        -> 0.15f
			RedWine, WhiteWine, Champagne -> 0.1f
			JellyBottle                   -> 0.5f
			JellyBread                    -> 0.8f
			JellyCod                      -> 1.2f
			DreamCherry,
			TreeBerryBarrier,
			TreeBerryCalico,
			TreeBerryCircuit,
			TreeBerryLightning,
			TreeBerryNether,
			TreeBerrySealing              -> 0.3f
			
			null                          -> 0f
		}
	}
	
	val drinkables = arrayOf(RedWine.I, WhiteWine.I, Champagne.I, JellyBottle.I)
	
	fun isAlwaysEdible(stack: ItemStack) = stack.meta in drinkables || stack.meta in TreeBerryBarrier.I..TreeBerrySealing.I
	
	override fun getItemUseAction(stack: ItemStack) = if (stack.meta in drinkables) EnumAction.drink else EnumAction.eat
	
	override fun onItemRightClick(stack: ItemStack, world: World?, player: EntityPlayer): ItemStack {
		if (isAlwaysEdible(stack) || player.canEat(false))
			player.setItemInUse(stack, getMaxItemUseDuration(stack))
		
		return stack
	}
	
	override fun onEaten(stack: ItemStack, world: World?, player: EntityPlayer): ItemStack {
		if (!world!!.isRemote) {
			ElvenFoodMetas.entries[stack.meta].potion?.let {
				if (it == Potion.regeneration.id) {
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
				
				player.addPotionEffect(PotionEffectU(id, 7200, amp))
			}
		}
		
		val ret = super.onEaten(stack, world, player)
		
		return getContainerItem(stack) ?: ret
	}
	
	// #### Item ####
	
	override fun hasContainerItem(stack: ItemStack) = true
	
	override fun getContainerItem(stack: ItemStack): ItemStack? {
		return when (entries.getOrNull(stack.meta)) {
			RedWine, WhiteWine, Champagne -> ElvenResourcesMetas.Jug.stack
			JellyBottle                   -> ItemStack(ModItems.vial, 1, 1)
			else                          -> null
		}
	}
	
	override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) =
		ElvenFoodMetas.entries.indices.forEach { list.add(ItemStack(item, 1, it)) }
	
	override fun getItemStackLimit(stack: ItemStack) =
		if (stack.meta in drinkables) 1 else super.getItemStackLimit(stack)
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
	;
	
	val I get() = ordinal
	
	val stack get() = stack()
	fun stack(size: Int = 1) = ItemStack(AlfheimItems.elvenFood, size, I)
}