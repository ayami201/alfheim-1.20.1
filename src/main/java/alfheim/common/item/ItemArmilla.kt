package alfheim.common.item

// PORT: импорты 1.20.1 (MAPPING.md); подсветка сферы — структура Patchouli через прокси Botania, как у секстанта
// Botania 1.20.1 (в 1.7.10 — MultiblockRenderHandler Botania)
import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.Botania
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RenderGuiEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import vazkii.botania.common.item.WorldshaperssSextantItem
import vazkii.botania.common.item.equipment.tool.ToolCommons
import vazkii.botania.common.proxy.Proxy
import vazkii.patchouli.api.IStateMatcher
import vazkii.patchouli.api.PatchouliAPI
import kotlin.math.*

class ItemArmilla: ItemMod("Armilla") {
	
	init {
		maxStackSize = 1
	}
	
	// PORT: getItemUseAction → getUseAnimation, getMaxItemUseDuration → getUseDuration
	override fun getUseAnimation(par1ItemStack: ItemStack) = UseAnim.BOW
	
	override fun getUseDuration(par1ItemStack: ItemStack) = 72000
	
	// PORT: onUsingTick → onUseTick (держит любое существо — браслет у автора только у игрока)
	override fun onUseTick(level: World, entity: LivingEntity, stack: ItemStack, count: Int) {
		val player = entity as? EntityPlayer ?: return
		if (count % 10 != 0 || getUseDuration(stack) - count < 10) return
//		if (count % 10 != 0 || getMaxItemUseDuration(stack) - count < 10) return
		
		val y = ItemNBTHelper.getInt(stack, TAG_SOURCE_Y, NO_SOURCE)
		if (y == NO_SOURCE) return
//		val y = ItemNBTHelper.getInt(stack, TAG_SOURCE_Y, -1)
//		if (y == -1) return
		
		val x = ItemNBTHelper.getInt(stack, TAG_SOURCE_X, 0)
		val z = ItemNBTHelper.getInt(stack, TAG_SOURCE_Z, 0)
		
		val world = player.worldObj
		val source = Vector3(x, y, z)
		val radius = calculateRadius(stack, player)
		
		for (i in 0..359) {
			val radian = i * Math.PI / 180
			val xp = x + cos(radian) * radius
			val zp = z + sin(radian) * radius
			Botania.proxy.wispFX(world, xp + 0.5, source.y + 1, zp + 0.5, 0f, 1f, 1f, 0.3f, -0.01f)
		}
	}
	
	// PORT: onPlayerStoppedUsing → releaseUsing
	override fun releaseUsing(stack: ItemStack, world: World, entity: LivingEntity, time: Int) {
		val player = entity as? EntityPlayer ?: return
		if (ASJUtilities.isServer) return
		
		val radius = calculateRadius(stack, player).I
		if (radius <= 1) return
		
		val y = ItemNBTHelper.getInt(stack, TAG_SOURCE_Y, NO_SOURCE)
		if (y == NO_SOURCE) return
//		val y = ItemNBTHelper.getInt(stack, TAG_SOURCE_Y, -1)
//		if (y == -1) return
		
		val x = ItemNBTHelper.getInt(stack, TAG_SOURCE_X, 0)
		val z = ItemNBTHelper.getInt(stack, TAG_SOURCE_Z, 0)
		
		setMultiblock(x, y, z, radius, Blocks.COBBLESTONE)
	}
	
	// PORT: структура секстанта Botania 1.7.10 (MultiblockSextant из AnyComponent — любой блок, кроме воздуха; рисуется
	// block) → разреженная структура Patchouli с id секстанта Botania 1.20.1: её так же убирает removeSextantMultiblock.
	// Patchouli пишет над структурой название — радиус, как у секстанта Botania 1.20.1
	fun setMultiblock(x: Int, y: Int, z: Int, radius: Int, block: Block?) {
		val mb = HashMap<ChunkCoordinates, IStateMatcher>()
		val matcher = PatchouliAPI.get().predicateMatcher(block ?: Blocks.AIR) { !it.isAir }
//		val mb = MultiblockSextant()
		val radius1 = radius + 1
		
		for (i in 0 until radius1 * 2 + 1)
			for (j in 0 until radius1 * 2 + 1)
				for (k in 0 until radius1 * 2 + 1) {
				val xp = x + i - radius1
				val yp = y + j - radius1
				val zp = z + k - radius1
				
				if (floor(Vector3.pointDistanceSpace(xp, yp, zp, x, y, z)).I == radius1 - 1)
					mb[ChunkCoordinates(xp - x, yp - y, zp - z)] = matcher
//					mb.addComponent(AnyComponent(ChunkCoordinates(xp - x, yp - y, zp - z), block, 0))
		}
		
		Proxy.INSTANCE.showMultiblock(PatchouliAPI.get().makeSparseMultiblock(mb).setId(WorldshaperssSextantItem.MULTIBLOCK_ID), Component.literal("r = $radius"), ChunkCoordinates(x, y, z), Rotation.NONE)
//		MultiblockRenderHandler.setMultiblock(mb.makeSet())
//		MultiblockRenderHandler.anchor = ChunkCoordinates(x, y, z)
	}
	
	// PORT: onItemRightClick → use; луч ToolCommons Botania 1.20.1 при промахе — MISS, а не null
	override fun use(world: World, player: EntityPlayer, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		Botania.proxy.removeSextantMultiblock()
		
		if (player.isShiftKeyDown) return InteractionResultHolder.consume(stack)
//		if (player.isSneaking) return stack
		
		val pos = ToolCommons.raytraceFromEntity(player, 128.0, false)
//		val pos = ToolCommons.raytraceFromEntity(world, player, false, 128.0)
		
		if (pos.typeOfHit == MovingObjectType.BLOCK) {
//		if (pos != null && pos.entityHit == null) {
			if (!world.isRemote) {
				ItemNBTHelper.setInt(stack, TAG_SOURCE_X, pos.blockX)
				ItemNBTHelper.setInt(stack, TAG_SOURCE_Y, pos.blockY)
				ItemNBTHelper.setInt(stack, TAG_SOURCE_Z, pos.blockZ)
			}
		} else
			ItemNBTHelper.setInt(stack, TAG_SOURCE_Y, NO_SOURCE)
//			ItemNBTHelper.setInt(stack, TAG_SOURCE_Y, -1)
		
		player.startUsingItem(hand)
//		player.setItemInUse(stack, getMaxItemUseDuration(stack))
		
		return InteractionResultHolder.consume(stack)
//		return stack
	}
	
	companion object {
		
		private const val TAG_SOURCE_X = "sourceX"
		private const val TAG_SOURCE_Y = "sourceY"
		private const val TAG_SOURCE_Z = "sourceZ"
		
		// PORT: «источника нет» у автора — y = −1, ниже мира 1.7.10. Мир 1.20.1 бывает ниже нуля, блок на −1 — обычный;
		// признак — высота, которой у блока не бывает (так же у секстанта Botania 1.20.1)
		private const val NO_SOURCE = Int.MIN_VALUE
		
		init {
			if (ASJUtilities.isClient) eventForge()
		}
		
		// PORT: RenderGameOverlayEvent.Post (ALL) → RenderGuiEvent.Post — после всего интерфейса
		@SubscribeEvent
		fun onDrawScreenPost(event: RenderGuiEvent.Post) {
//			if (event.type != RenderGameOverlayEvent.ElementType.ALL) return
			
			val stack = mc.player?.mainHandItem?.takeUnless { it.isEmpty } ?: return
//			val stack = mc.thePlayer.heldItem ?: return
			if (stack.item !is ItemArmilla) return
			
			renderHUD(event.guiGraphics, mc.player!!, stack)
//			renderHUD(event.resolution, mc.thePlayer, stack)
		}
		
		// PORT: ScaledResolution → GuiGraphics, размеры — окно. Круг GL_LINE_STRIP — шейдер линий ванилы (толщина 3 —
		// RenderSystem.lineWidth), направление линии у вершины — касательная к кругу
		@OnlyIn(Dist.CLIENT)
		fun renderHUD(gui: GuiGraphics, player: EntityPlayer, stack: ItemStack) {
			val onUse = player.useItem
			val time = player.useItemRemainingTicks
//			val onUse = player.getItemInUse()
//			val time = player.getItemInUseCount()
			
			if (onUse !== stack || stack.item.getUseDuration(stack) - time < 10) return
//			if (onUse != stack || stack.item.getMaxItemUseDuration(stack) - time < 10) return
			
			var radius = calculateRadius(stack, player)
			val font = mc.font
			val x = mc.window.guiScaledWidth / 2 + 30
			val y = mc.window.guiScaledHeight / 2
			val s = "${radius.I}"
			gui.drawString(font, s, x - font.width(s) / 2, y - 4, 0xFFFFFF)
//			val font = Minecraft.getMinecraft().fontRenderer
//			val x = resolution.scaledWidth / 2 + 30
//			val y = resolution.scaledHeight / 2
//			val s = "${radius.I}"
//			font.drawStringWithShadow(s, x - font.getStringWidth(s) / 2, y - 4, 0xFFFFFF)
			if (radius <= 0) return
			
			radius += 4.0
			RenderSystem.setShader(GameRenderer::getRendertypeLinesShader)
			RenderSystem.lineWidth(3f)
			val tes = Tesselator.getInstance()
			val pose = gui.pose().last()
			tes.builder.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR_NORMAL)
			for (i in 0..360) {
				val radian = i * Math.PI / 180
				val xp = x + cos(radian) * radius
				val yp = y + sin(radian) * radius
				tes.builder.vertex(pose.pose(), xp.F, yp.F, 0f).color(0f, 1f, 1f, 1f).normal(pose.normal(), -sin(radian).F, cos(radian).F, 0f).endVertex()
			}
			tes.end()
			RenderSystem.lineWidth(1f)
//			GL11.glDisable(GL11.GL_TEXTURE_2D)
//			GL11.glLineWidth(3f)
//			GL11.glBegin(GL11.GL_LINE_STRIP)
//			GL11.glColor4f(0f, 1f, 1f, 1f)
//			for (i in 0..360) {
//				val radian = i * Math.PI / 180
//				val xp = x + cos(radian) * radius
//				val yp = y + sin(radian) * radius
//				GL11.glVertex2d(xp, yp)
//			}
//			GL11.glEnd()
//			GL11.glEnable(GL11.GL_TEXTURE_2D)
		}
		
		fun calculateRadius(stack: ItemStack, player: EntityPlayer): Double {
			val x = ItemNBTHelper.getInt(stack, TAG_SOURCE_X, 0).D
			val y = ItemNBTHelper.getInt(stack, TAG_SOURCE_Y, NO_SOURCE).D
//			val y = ItemNBTHelper.getInt(stack, TAG_SOURCE_Y, -1).D
			val z = ItemNBTHelper.getInt(stack, TAG_SOURCE_Z, 0).D
			
			val world = player.worldObj
			val source = Vector3(x, y, z)
			Botania.proxy.wispFX(world, source.x + 0.5, source.y + 1, source.z + 0.5, 1f, 0f, 0f, 0.2f, -0.1f)
			
			val centerVec = Vector3.fromEntityCenter(player)
			val diffVec = source.copy().sub(centerVec)
			val lookVec = Vector3(player.lookAngle)
//			val lookVec = Vector3(player.lookVec)
			val mul = diffVec.y / lookVec.y
			lookVec.mul(mul).add(centerVec)
			lookVec.x = lookVec.x.mfloor().D
			lookVec.z = lookVec.z.mfloor().D
			
			val radius = Vector3.pointDistancePlane(source.x, source.z, lookVec.x, lookVec.z)
			
			return min(radius, 256.0)
		}
	}
}

// PORT: имя поля 1.7.10
private val Entity.worldObj get() = level()
