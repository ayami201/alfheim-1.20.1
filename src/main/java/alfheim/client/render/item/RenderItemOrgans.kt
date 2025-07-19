package alfheim.client.render.item

import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer

object RenderItemOrgans: IItemRenderer {
	override fun handleRenderType(item: ItemStack?, type: IItemRenderer.ItemRenderType?) = type != IItemRenderer.ItemRenderType.INVENTORY
	override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType?, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?) = false
	override fun renderItem(type: IItemRenderer.ItemRenderType?, item: ItemStack?, vararg data: Any?) = Unit
}
