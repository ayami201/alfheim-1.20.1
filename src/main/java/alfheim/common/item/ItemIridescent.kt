package alfheim.common.item

import alexsocol.asjlib.meta
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.AlfheimBlocks
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import vazkii.botania.common.Botania
import java.awt.Color

open class ItemIridescent(name: String): ItemMod(name) {
	
	companion object {
		
		const val TYPES = 18
		
		const val MAX_COLORED_META = 15
		const val RAINBOW = 16
		const val AURORA = 17
		
		fun rainbowColor() = Color.HSBtoRGB(Botania.proxy.worldElapsedTicks * 2 % 360 / 360F, 1F, 1F)
		
		fun colorFromItemStack(stack: ItemStack) = colorFromMeta(stack.meta)
		
		fun colorFromMeta(meta: Int): Int {
			if (meta == 1000)
				return 0x63CC2F // overgrowth
			
			if (meta == RAINBOW)
				return rainbowColor()
			
			if (meta >= EntitySheep.fleeceColorTable.size)
				return 0xFFFFFF
			
			val color = EntitySheep.fleeceColorTable[meta]
			return Color(color[0], color[1], color[2]).rgb
		}
		
		fun dirtFromMeta(meta: Int): Block {
			return when (meta) {
				in 0..MAX_COLORED_META -> AlfheimBlocks.irisDirt
				RAINBOW                -> AlfheimBlocks.rainbowDirt
				AURORA                 -> AlfheimBlocks.auroraDirt
				else                   -> Blocks.air
			}
		}
		
		fun dirtStack(meta: Int): ItemStack {
			val block = when (meta) {
				in 0..MAX_COLORED_META -> AlfheimBlocks.irisDirt
				RAINBOW                -> AlfheimBlocks.rainbowDirt
				AURORA                 -> AlfheimBlocks.auroraDirt
				else                   -> Blocks.air
			}
			
			return ItemStack(block, 1, if (meta > 15) 0 else meta)
		}
	}
	
	init {
		setHasSubtypes(true)
	}
	
	lateinit var overlayIcon: IIcon
	
	override fun requiresMultipleRenderPasses() = true
	
	override fun getIconFromDamageForRenderPass(meta: Int, pass: Int) =
		if (pass == 1) overlayIcon else super.getIconFromDamageForRenderPass(meta, pass)!!
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		super.registerIcons(reg)
		overlayIcon = IconHelper.forItem(reg, this, "Overlay")
	}
	
	override fun getColorFromItemStack(stack: ItemStack, pass: Int): Int =
		if (pass > 0) 0xFFFFFF else colorFromItemStack(stack)
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, tooltip: MutableList<Any?>, adv: Boolean) {
		if (stack.meta >= TYPES) return
		
		tooltip += ("${EnumChatFormatting.GRAY}${StatCollector.translateToLocal("misc.${ModInfo.MODID}.color." + stack.meta)}")
	}
}
