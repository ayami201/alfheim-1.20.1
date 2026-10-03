package alfheim.common.item

// PORT: импорты 1.20.1 (MAPPING.md); Botania.proxy — alfheim.port.legacy.botania.Botania
import alexsocol.asjlib.meta
import alfheim.api.ModInfo
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.Botania
import net.minecraft.ChatFormatting as EnumChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
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
			
			// PORT: цвета шерсти 1.7.10 — Sheep1710 (в 1.20.1 цвета красителей другие)
			if (meta >= Sheep1710.fleeceColorTable.size)
				return 0xFFFFFF
			
			val color = Sheep1710.fleeceColorTable[meta]
			return Color(color[0], color[1], color[2]).rgb
		}
		
		/* PORT: КТ-2 — цветная земля (irisDirt, rainbowDirt, auroraDirt) переносится с цветными блоками
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
		*/
	}
	
	init {
		setHasSubtypes(true)
	}
	
	// PORT: второй проход рендера (overlayIcon) — второй слой модели предмета (alfheim.port.data), без окраски
	/*
	lateinit var overlayIcon: IIcon
	
	override fun requiresMultipleRenderPasses() = true
	
	override fun getIconFromDamageForRenderPass(meta: Int, pass: Int) =
		if (pass == 1) overlayIcon else super.getIconFromDamageForRenderPass(meta, pass)!!
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		super.registerIcons(reg)
		overlayIcon = IconHelper.forItem(reg, this, "Overlay")
	}
	*/
	
	override fun getColorFromItemStack(stack: ItemStack, pass: Int): Int =
		if (pass > 0) 0xFFFFFF else colorFromItemStack(stack)
	
	// PORT: addInformation → appendHoverText, строка подсказки — Component
	override fun appendHoverText(stack: ItemStack, world: Level?, tooltip: MutableList<Component>, adv: TooltipFlag) {
		if (stack.meta >= TYPES) return
		
		tooltip += Component.literal("${EnumChatFormatting.GRAY}${StatCollector.translateToLocal("misc.${ModInfo.MODID}.color." + stack.meta)}")
	}
}
