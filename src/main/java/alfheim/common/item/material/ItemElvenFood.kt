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
import net.minecraft.init.Items
import net.minecraft.item.*
import net.minecraft.nbt.*
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.IIcon
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants

class ItemElvenFood: ItemFood(0, 0f, false) {
	
	val subItems = entries.size
	
	lateinit var icons: Array<IIcon>
	
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
		getUnlocalizedName(stack).replace("item\\.".toRegex(), "item.${ModInfo.MODID}:") + stack.meta
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		icons = Array(subItems) { IconHelper.forItem(reg, this, it, "materials/food") }
	}
	
	override fun getIconFromDamage(meta: Int) = icons.safeGet(meta)
	
	// #### ItemFood ####
	
	// foodLevel
	override fun func_150905_g(stack: ItemStack): Int {
		return when (entries.getOrNull(stack.meta)) {
			Lembas                 -> 20
			RedGrapes, WhiteGrapes -> 2
			Nectar                 -> 1
			RedWine, WhiteWine     -> 3
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
			
			else                   -> 0
		}
	}
	
	// foodSaturationLevel
	override fun func_150906_h(stack: ItemStack): Float {
		return when (entries.getOrNull(stack.meta)) {
			Lembas                 -> 5f
			RedGrapes, WhiteGrapes -> 0.3f
			Nectar                 -> 0.15f
			RedWine, WhiteWine     -> 0.1f
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
			
			else                   -> 0f
		}
	}
	
	val drinkables = arrayOf(RedWine.I, WhiteWine.I, JellyBottle.I)
	
	override fun getItemUseAction(stack: ItemStack) = if (stack.meta in drinkables) EnumAction.drink else EnumAction.eat
	
	override fun getMaxItemUseDuration(stack: ItemStack): Int {
		return super.getMaxItemUseDuration(stack)
	}
	
	override fun onEaten(stack: ItemStack, world: World?, player: EntityPlayer): ItemStack {
		if (!world!!.isRemote) {
			getPotions(stack).forEach {
				it ?: return@forEach
				
				if (!player.isPotionActive(it.potionID))
					player.addPotionEffect(it)
				val eff = player.getActivePotionEffect(it.potionID) ?: return@forEach
				eff.duration = it.duration
				eff.amplifier = it.amplifier
			}
			
			ElvenFoodMetas.entries[stack.meta].potion?.let {
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
			RedWine, WhiteWine -> ElvenResourcesMetas.Jug.stack
			JellyBottle        -> ItemStack(Items.glass_bottle)
			else               -> null
		}
	}
	
	override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		(0 until subItems).forEach { list.add(ItemStack(item, 1, it)) }
	}
	
	override fun getItemStackLimit(stack: ItemStack): Int {
		return if (stack.meta in drinkables) 1 else super.getItemStackLimit(stack)
	}
	
	companion object {
		
		const val TAG_POTIONS = "potions"
		
		fun addPotion(stack: ItemStack, effect: PotionEffect) {
			ItemNBTHelper.getList(stack, TAG_POTIONS, Constants.NBT.TAG_COMPOUND).appendTag(NBTTagCompound().apply { effect.writeCustomPotionEffectToNBT(this) })
		}
		
		fun getPotions(stack: ItemStack): List<PotionEffect?> {
			return ItemNBTHelper.getList(stack, TAG_POTIONS, Constants.NBT.TAG_COMPOUND).tagList.map {
				PotionEffect.readCustomPotionEffectFromNBT(it as NBTTagCompound)
			}
		}
	}
}

enum class ElvenFoodMetas(val potion: Int? = null) {
	
	Lembas,
	RedGrapes,
	WhiteGrapes,
	Nectar,
	RedWine,
	WhiteWine,
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