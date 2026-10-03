package alfheim.port.client

import alexsocol.asjlib.meta
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.RandomSource
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

/*
 * Иконки предметов 1.7.10 на клиенте (MAPPING.md, «Рендер»). В 1.20.1 иконок нет: проход рендера 1.7.10 — слой модели
 * предмета с тем же номером (MAPPING.md, «Блоки и предметы»), его текстура — спрайт атласа блоков и предметов.
 */

/** `item.getIcon(stack, pass)` 1.7.10 — текстура слоя [pass] модели предмета; у прохода 0 — частица модели (слой 0) */
fun Item.getIcon(stack: ItemStack, pass: Int): TextureAtlasSprite? {
	val model = Minecraft.getInstance().itemRenderer.getModel(stack, null, null, 0)
	if (pass == 0) return model.particleIcon
	return model.getQuads(null, null, RandomSource.create()).firstOrNull { it.tintIndex == pass }?.sprite
}

/** `item.getIconFromDamage(meta)` 1.7.10 — иконка прохода 0 вещи с этой metadata */
fun Item.getIconFromDamage(meta: Int) = getIcon(ItemStack(this).also { it.meta = meta }, 0)
