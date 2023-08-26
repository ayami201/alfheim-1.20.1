package alfheim.client.render.entity

import alexsocol.asjlib.F
import alexsocol.asjlib.render.ModelBipedNew
import com.google.common.collect.Maps
import com.mojang.authlib.GameProfile
import net.minecraft.block.Block
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer
import net.minecraft.entity.*
import net.minecraft.init.Items
import net.minecraft.item.*
import net.minecraft.nbt.*
import net.minecraft.util.*
import net.minecraftforge.client.*
import org.lwjgl.opengl.GL11
import java.util.*

abstract class RenderBipedNew(var modelBipedMain: ModelBipedNew, shadowSize: Float): RenderLiving(modelBipedMain, shadowSize) {
	
	protected var armorModel = ModelBiped(1.0f)
	protected var armorModelPants = ModelBiped(0.5f)
	
	/**
	 * Queries whether should render the specified pass or not.
	 */
	fun shouldRenderPass(entity: EntityLivingBase, path: Int): Int {
		val stack = entity.getEquipmentInSlot(3 - path + 1) ?: return -1
		val item = stack.item as? ItemArmor ?: return -1
		
		bindTexture(getArmorResource(entity, stack, path, null))
		var armorModel = if (path == 2) armorModelPants else armorModel
		
		armorModel.bipedHead.showModel = path == 0
		armorModel.bipedHeadwear.showModel = path == 0
		armorModel.bipedBody.showModel = path == 1 || path == 2
		armorModel.bipedRightArm.showModel = path == 1
		armorModel.bipedLeftArm.showModel = path == 1
		armorModel.bipedRightLeg.showModel = path == 2 || path == 3
		armorModel.bipedLeftLeg.showModel = path == 2 || path == 3
		armorModel = ForgeHooksClient.getArmorModel(entity, stack, path, armorModel)
		setRenderPassModel(armorModel)
		armorModel.onGround = mainModel.onGround
		armorModel.isRiding = mainModel.isRiding
		armorModel.isChild = mainModel.isChild
		
		val j = item.getColor(stack)
		
		if (j != -1) {
			val r = (j shr 16 and 255).F / 255.0f
			val g = (j shr 8 and 255).F / 255.0f
			val b = (j and 255).F / 255.0f
			GL11.glColor3f(r, g, b)
			return if (stack.isItemEnchanted) 31 else 16
		}
		
		GL11.glColor3f(1f, 1f, 1f)
		return if (stack.isItemEnchanted) 15 else 1
	}
	
	// bindArmorTexture
	override fun func_82408_c(entity: EntityLivingBase, path: Int, ticks: Float) {
		val stack = entity.getEquipmentInSlot(3 - path + 1) ?: return
		bindTexture(getArmorResource(entity, stack, path, "overlay"))
		GL11.glColor3f(1f, 1f, 1f)
	}
	
	override fun doRender(entity: EntityLiving, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		GL11.glColor3f(1f, 1f, 1f)
		renderHeldItem(entity, entity.heldItem)
		
		var yOffset = y - entity.yOffset.toDouble()
		if (entity.isSneaking) yOffset -= 0.125
		super.doRender(entity, x, yOffset, z, yaw, ticks)
		
		armorModelPants.aimedBow = false
		armorModel.aimedBow = false
		armorModelPants.isSneak = false
		armorModel.isSneak = false
		armorModelPants.heldItemRight = 0
		armorModel.heldItemRight = 0
	}
	
	abstract fun getEntityTexture(entity: EntityLiving): ResourceLocation?
	
	protected fun renderHeldItem(entity: EntityLiving, stack: ItemStack?) {
		modelBipedMain.heldItemRight = if (stack != null) 1 else 0
		armorModelPants.heldItemRight = modelBipedMain.heldItemRight
		armorModel.heldItemRight = armorModelPants.heldItemRight
		modelBipedMain.isSneak = entity.isSneaking
		armorModelPants.isSneak = modelBipedMain.isSneak
		armorModel.isSneak = armorModelPants.isSneak
	}
	
	protected open fun renderEquippedItems(p_77029_1_: EntityLiving, p_77029_2_: Float) {
		GL11.glColor3f(1.0f, 1.0f, 1.0f)
		super.renderEquippedItems(p_77029_1_, p_77029_2_)
		val itemstack = p_77029_1_.heldItem
		val itemstack1 = p_77029_1_.func_130225_q(3)
		var item: Item
		var f1: Float
		if (itemstack1 != null) {
			GL11.glPushMatrix()
			modelBipedMain.bipedHead.postRender(0.0625f)
			item = itemstack1.item
			val customRenderer = MinecraftForgeClient.getItemRenderer(itemstack1, IItemRenderer.ItemRenderType.EQUIPPED)
			val is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, itemstack1, IItemRenderer.ItemRendererHelper.BLOCK_3D)
			if (item is ItemBlock) {
				if (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(item).renderType)) {
					f1 = 0.625f
					GL11.glTranslatef(0.0f, -0.25f, 0.0f)
					GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f)
					GL11.glScalef(f1, -f1, -f1)
				}
				renderManager.itemRenderer.renderItem(p_77029_1_, itemstack1, 0)
			} else if (item === Items.skull) {
				f1 = 1.0625f
				GL11.glScalef(f1, -f1, -f1)
				var gameprofile: GameProfile? = null
				if (itemstack1.hasTagCompound()) {
					val nbttagcompound = itemstack1.tagCompound
					if (nbttagcompound.hasKey("SkullOwner", 10)) {
						gameprofile = NBTUtil.func_152459_a(nbttagcompound.getCompoundTag("SkullOwner"))
					} else if (nbttagcompound.hasKey("SkullOwner", 8) && !StringUtils.isNullOrEmpty(nbttagcompound.getString("SkullOwner"))) {
						gameprofile = GameProfile(null as UUID?, nbttagcompound.getString("SkullOwner"))
					}
				}
				TileEntitySkullRenderer.field_147536_b.func_152674_a(-0.5f, 0.0f, -0.5f, 1, 180.0f, itemstack1.getItemDamage(), gameprofile)
			}
			GL11.glPopMatrix()
		}
		if (itemstack != null && itemstack.item != null) {
			item = itemstack.item
			GL11.glPushMatrix()
			if (mainModel.isChild) {
				f1 = 0.5f
				GL11.glTranslatef(0.0f, 0.625f, 0.0f)
				GL11.glRotatef(-20.0f, -1.0f, 0.0f, 0.0f)
				GL11.glScalef(f1, f1, f1)
			}
			modelBipedMain.bipedRightArm.postRender(0.0625f)
			GL11.glTranslatef(-0.0625f, 0.4375f, 0.0625f)
			val customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, IItemRenderer.ItemRenderType.EQUIPPED)
			val is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, itemstack, IItemRenderer.ItemRendererHelper.BLOCK_3D)
			if (item is ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(item).renderType))) {
				f1 = 0.5f
				GL11.glTranslatef(0.0f, 0.1875f, -0.3125f)
				f1 *= 0.75f
				GL11.glRotatef(20.0f, 1.0f, 0.0f, 0.0f)
				GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f)
				GL11.glScalef(-f1, -f1, f1)
			} else if (item === Items.bow) {
				f1 = 0.625f
				GL11.glTranslatef(0.0f, 0.125f, 0.3125f)
				GL11.glRotatef(-20.0f, 0.0f, 1.0f, 0.0f)
				GL11.glScalef(f1, -f1, f1)
				GL11.glRotatef(-100.0f, 1.0f, 0.0f, 0.0f)
				GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f)
			} else if (item.isFull3D) {
				f1 = 0.625f
				if (item.shouldRotateAroundWhenRendering()) {
					GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f)
					GL11.glTranslatef(0.0f, -0.125f, 0.0f)
				}
				func_82422_c()
				GL11.glScalef(f1, -f1, f1)
				GL11.glRotatef(-100.0f, 1.0f, 0.0f, 0.0f)
				GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f)
			} else {
				f1 = 0.375f
				GL11.glTranslatef(0.25f, 0.1875f, -0.1875f)
				GL11.glScalef(f1, f1, f1)
				GL11.glRotatef(60.0f, 0.0f, 0.0f, 1.0f)
				GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f)
				GL11.glRotatef(20.0f, 0.0f, 0.0f, 1.0f)
			}
			var f2: Float
			var i: Int
			var f5: Float
			if (itemstack.item.requiresMultipleRenderPasses()) {
				i = 0
				while (i < itemstack.item.getRenderPasses(itemstack.getItemDamage())) {
					val j = itemstack.item.getColorFromItemStack(itemstack, i)
					f5 = (j shr 16 and 255).toFloat() / 255.0f
					f2 = (j shr 8 and 255).toFloat() / 255.0f
					val f3 = (j and 255).toFloat() / 255.0f
					GL11.glColor4f(f5, f2, f3, 1.0f)
					renderManager.itemRenderer.renderItem(p_77029_1_, itemstack, i)
					++i
				}
			} else {
				i = itemstack.item.getColorFromItemStack(itemstack, 0)
				val f4 = (i shr 16 and 255).toFloat() / 255.0f
				f5 = (i shr 8 and 255).toFloat() / 255.0f
				f2 = (i and 255).toFloat() / 255.0f
				GL11.glColor4f(f4, f5, f2, 1.0f)
				renderManager.itemRenderer.renderItem(p_77029_1_, itemstack, 0)
			}
			GL11.glPopMatrix()
		}
	}
	
	protected open fun func_82422_c() {
		GL11.glTranslatef(0.0f, 0.1875f, 0.0f)
	}
	
	/**
	 * Queries whether should render the specified pass or not.
	 */
	override fun shouldRenderPass(p_77032_1_: EntityLivingBase, p_77032_2_: Int, p_77032_3_: Float): Int {
		return this.shouldRenderPass(p_77032_1_ as EntityLiving, p_77032_2_)
	}
	
	override fun renderEquippedItems(p_77029_1_: EntityLivingBase, p_77029_2_: Float) {
		this.renderEquippedItems(p_77029_1_ as EntityLiving, p_77029_2_)
	}
	
	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1, double d2, float f, float f1). But JAD is pre 1.5 so doesn></T>'t do that.
	 */
	override fun doRender(p_76986_1_: EntityLivingBase, p_76986_2_: Double, p_76986_4_: Double, p_76986_6_: Double, p_76986_8_: Float, p_76986_9_: Float) {
		this.doRender(p_76986_1_ as EntityLiving, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_)
	}
	
	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	override fun getEntityTexture(p_110775_1_: Entity): ResourceLocation {
		return this.getEntityTexture(p_110775_1_ as EntityLiving)
	}
	
	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1, double d2, float f, float f1). But JAD is pre 1.5 so doesn></T>'t do that.
	 */
	override fun doRender(p_76986_1_: Entity, p_76986_2_: Double, p_76986_4_: Double, p_76986_6_: Double, p_76986_8_: Float, p_76986_9_: Float) {
		this.doRender(p_76986_1_ as EntityLiving, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_)
	}
	
	companion object {
		
		private val field_110859_k: MutableMap<*, *> = Maps.newHashMap<Any, Any>()
		
		/** List of armor texture filenames.  */
		var bipedArmorFilenamePrefix = arrayOf("leather", "chainmail", "iron", "diamond", "gold")
		private const val __OBFID = "CL_00001001"
		
		@Deprecated("") //Use the more sensitive version getArmorResource below
		fun func_110857_a(p_110857_0_: ItemArmor, p_110857_1_: Int): ResourceLocation {
			return func_110858_a(p_110857_0_, p_110857_1_, null as String?)
		}
		
		@Deprecated("") //Use the more sensitive version getArmorResource below
		fun func_110858_a(p_110858_0_: ItemArmor, p_110858_1_: Int, p_110858_2_: String?): ResourceLocation {
			val s1 = String.format("textures/models/armor/%s_layer_%d%s.png", *arrayOf<Any>(bipedArmorFilenamePrefix[p_110858_0_.renderIndex], if (p_110858_1_ == 2) 2 else 1, if (p_110858_2_ == null) "" else String.format("_%s", *arrayOf<Any>(p_110858_2_))))
			var resourcelocation = field_110859_k[s1] as ResourceLocation?
			if (resourcelocation == null) {
				resourcelocation = ResourceLocation(s1)
				field_110859_k[s1] = resourcelocation
			}
			return resourcelocation
		}
		/*=================================== FORGE START =========================================*/
		/**
		 * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
		 *
		 * @param entity Entity wearing the armor
		 * @param stack ItemStack for the armor
		 * @param slot Slot ID that the item is in
		 * @param type Subtype, can be null or "overlay"
		 * @return ResourceLocation pointing at the armor's texture
		 */
		fun getArmorResource(entity: Entity?, stack: ItemStack, slot: Int, type: String?): ResourceLocation {
			val item = stack.item as ItemArmor
			var s1: String? = String.format(
				"textures/models/armor/%s_layer_%d%s.png",
				bipedArmorFilenamePrefix[item.renderIndex], if (slot == 2) 2 else 1, if (type == null) "" else String.format("_%s", type)
			)
			s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type)
			var resourcelocation = field_110859_k[s1] as ResourceLocation?
			if (resourcelocation == null) {
				resourcelocation = ResourceLocation(s1)
				field_110859_k[s1] = resourcelocation
			}
			return resourcelocation
		} /*=================================== FORGE END ===========================================*/
	}
}