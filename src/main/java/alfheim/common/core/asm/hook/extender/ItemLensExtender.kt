package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.*
import alfheim.common.item.lens.*
import cpw.mods.fml.relauncher.*
import gloomyfolken.hooklib.asm.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.*
import net.minecraft.util.IIcon
import vazkii.botania.api.mana.IManaSpreader
import vazkii.botania.client.core.helper.IconHelper
import vazkii.botania.common.item.lens.*

@Suppress("unused", "UNUSED_PARAMETER")
object ItemLensExtender {
	
	private const val PROP_NONE = 0
	private const val PROP_POWER = 1
	private const val PROP_ORIENTATION = 2
	private const val PROP_TOUCH = 4
	private const val PROP_INTERACTION = 8
	private const val PROP_DAMAGE = 16
	private const val PROP_CONTROL = 32
	
	enum class EnumAlfheimLens(val meta: Int, val unlocalizedName: String, val prop: Int, val instance: Lens) {
		// Botania
		MESSENGER(22, "lensMessenger", PROP_POWER, LensMessenger()),
		TRIPWIRE(23, "lensTripwire", PROP_CONTROL, LensTripwire()),
		// ExtraBotany
		PUSH(24, "lensPush", PROP_NONE, LensPush()),
		SMELT(25, "lensSmelt", PROP_NONE, LensSmelt()),
		SUPERCONDUCTOR(26, "lensSuperconductor", PROP_DAMAGE, LensSuperconductor()),
		TRACK(27, "lensTrack", PROP_CONTROL, LensTrack()),
		// new
		DAISY(28, "lensDaisy", PROP_INTERACTION or PROP_TOUCH, LensDaisy()),
		LINKBACK(29, "lensLinkback", PROP_NONE, LensLinkback()),
		UNLINK(30, "lensUnlink", PROP_NONE, LensUnlink()),
	}
	
	/**
	 * Add name in [alfheim.common.core.asm.AlfheimClassTransformer].LibItemNames$ClassVisitor.LibItemNames$clinit$MethodVisitor
	 */
	@JvmStatic
	@Hook(injectOnExit = true, isMandatory = true, targetMethod = "<clinit>")
	fun `ItemLens$clinit`(lens: ItemLens?) {
		EnumAlfheimLens.entries.forEach {
			ItemLens.setProps(it.meta, it.prop)
			ItemLens.setLens(it.meta, it.instance)
		}
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun getSubItems(thiz: ItemLens, item: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>) {
		list.add(ItemStack(item, 1, ItemLens.STORM))
	}
	
	private var lensStormIcon: IIcon? = null
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(injectOnExit = true)
	fun registerIcons(thiz: ItemLens, reg: IIconRegister) {
		lensStormIcon = IconHelper.forName(reg, "lensStorm")
	}
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ReturnCondition.ON_NOT_NULL)
	fun getIconFromDamageForRenderPass(thiz: ItemLens, meta: Int, pass: Int) = if (pass == 1 && meta == ItemLens.STORM) lensStormIcon else null
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun allowBurstShooting(thiz: ItemLens, stack: ItemStack, spreader: IManaSpreader?, redstone: Boolean): Boolean {
		return ItemLens.getLens(stack.getItemDamage()).allowBurstShooting(stack, spreader, redstone)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun onControlledSpreaderTick(thiz: ItemLens, stack: ItemStack, spreader: IManaSpreader?, redstone: Boolean) {
		ItemLens.getLens(stack.getItemDamage()).onControlledSpreaderTick(stack, spreader, redstone)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun onControlledSpreaderPulse(thiz: ItemLens, stack: ItemStack, spreader: IManaSpreader?, redstone: Boolean) {
		ItemLens.getLens(stack.getItemDamage()).onControlledSpreaderPulse(stack, spreader, redstone)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE, intReturnConstant = 28)
	fun getProps(thiz: ItemLens, stack: ItemStack): Boolean {
		return stack.meta == ItemLens.STORM
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
	fun getUnlocalizedName(thiz: ItemLens, stack: ItemStack) = if (stack.meta == 5000) "item.lensStorm" else null
	
	private const val TAG_COMPOSITE_LENS = "compositeLens"
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun doesContainerItemLeaveCraftingGrid(thiz: ItemLens, stack: ItemStack) = !hasContainerItem(thiz, stack)
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun hasContainerItem(thiz: ItemLens, stack: ItemStack) = ItemNBTHelper.getNBT(stack).hasKey(TAG_COMPOSITE_LENS)
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun getContainerItem(thiz: ItemLens, stack: ItemStack): ItemStack? {
		if (!hasContainerItem(thiz, stack)) return null
		
		val result = stack.copy()
		ItemNBTHelper.getNBT(result).removeTag(TAG_COMPOSITE_LENS)
		
		return result
	}
}