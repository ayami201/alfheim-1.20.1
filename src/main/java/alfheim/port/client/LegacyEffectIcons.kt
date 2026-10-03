package alfheim.port.client

import alfheim.port.legacy.Potion1710
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.*
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen
import net.minecraft.world.effect.MobEffectInstance
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions

/**
 * Иконка зелья 1.7.10 (MAPPING.md, «Предметы, сущности, эффекты»): 1.7.10 рисовал её из листа иконок 256×256,
 * клетка 18×18 номер `getStatusIconIndex()` — столбец `index % 8`, строка `index / 8`, с высоты 198. Лист зелья —
 * [Potion1710.iconSheet]; так же он рисуется в инвентаре и в углу экрана (в 1.20.1 иконки эффектов есть и там).
 * Зелье без иконки ничего не рисует, как в 1.7.10
 */
object LegacyEffectIcons {

	fun extensions(potion: Potion1710): IClientMobEffectExtensions = object: IClientMobEffectExtensions {

		override fun renderInventoryIcon(instance: MobEffectInstance, screen: EffectRenderingInventoryScreen<*>, graphics: GuiGraphics, x: Int, y: Int, blitOffset: Int): Boolean {
			draw(potion, graphics, x, y + 7)
			return true
		}

		override fun renderGuiIcon(instance: MobEffectInstance, gui: Gui, graphics: GuiGraphics, x: Int, y: Int, z: Float, alpha: Float): Boolean {
			graphics.setColor(1f, 1f, 1f, alpha)
			draw(potion, graphics, x + 3, y + 3)
			graphics.setColor(1f, 1f, 1f, 1f)
			return true
		}
	}

	private fun draw(potion: Potion1710, graphics: GuiGraphics, x: Int, y: Int) {
		val sheet = potion.iconSheet ?: return
		if (!potion.hasStatusIcon()) return
		val index = potion.statusIconIndex
		RenderSystem.enableBlend()
		graphics.blit(sheet, x, y, index % 8 * 18, 198 + index / 8 * 18, 18, 18)
	}
}
