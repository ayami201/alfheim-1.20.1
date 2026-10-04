package alfheim.common.item.block

// PORT: импорты 1.20.1; ItemBlock 1.7.10 — alfheim.port.legacy.ItemBlock
import alfheim.api.ModInfo
import alfheim.common.block.colored.BlockAuroraDirt
import alfheim.port.legacy.*
import net.minecraft.network.chat.Component
import net.minecraft.world.item.*
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraftforge.api.distmarker.*

class ItemBlockAurora(block: Block): ItemBlock(block) {
	
	// PORT: поставленная игроком листва не опадает — свойство persistent (Leaves1710)
	/*
	override fun getMetadata(meta: Int): Int {
		if (field_150939_a is BlockLeavesMod) return meta or (field_150939_a as BlockLeavesMod).decayBit()
		return meta
	}
	*/
	
	// PORT: цвет вещи нужен только клиенту: он — по положению игрока клиента (BlockAuroraDirt.getItemColor)
	@OnlyIn(Dist.CLIENT)
	override fun getColorFromItemStack(stack: ItemStack, pass: Int) = BlockAuroraDirt.getItemColor()
	
	// PORT: строка подсказки 1.20.1 — Component
	fun addStringToTooltip(s: String, tooltip: MutableList<Component>?) {
		tooltip!!.add(Component.literal(s.replace("&".toRegex(), "\u00a7")))
	}
	
	// PORT: addInformation → appendHoverText
	override fun appendHoverText(par1ItemStack: ItemStack?, par2World: World?, par3List: MutableList<Component>, par4: TooltipFlag) {
		addStringToTooltip("&7" + StatCollector.translateToLocal("misc.${ModInfo.MODID}.color.17") + "&r", par3List)
	}
}