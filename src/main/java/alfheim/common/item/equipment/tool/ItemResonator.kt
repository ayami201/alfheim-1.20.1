package alfheim.common.item.equipment.tool

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.*
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.client.core.helper.IconHelper
import alfheim.client.gui.ItemsRemainingRenderHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.entity.EntityResonance
import alfheim.common.item.AlfheimItems
import com.google.common.collect.HashMultimap
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import vazkii.botania.api.mana.*
import vazkii.botania.common.item.equipment.tool.terrasteel.ItemTerraPick

class ItemResonator: ItemPickaxe(AlfheimAPI.elvoriumToolMaterial), IManaUsingItem {
	
	lateinit var iconTipped: IIcon
	
	init {
		creativeTab = AlfheimTab
		maxDamage = 2400
		unlocalizedName = "Resonator"
		eventForge()
	}
	
	override fun setUnlocalizedName(name: String): Item {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (player.isSneaking) {
			stack.target = (stack.target + 1) % 3
			ItemsRemainingRenderHandler.set(stack, StatCollector.translateToLocal("alfheimmisc.resonator.target${stack.target}.short"))
		} else {
			stack.mode = (stack.mode + 1) % 3
			ItemsRemainingRenderHandler.set(stack, StatCollector.translateToLocal("alfheimmisc.resonator.mode${stack.mode}.short"))
		}
		
		player.playSoundAtEntity("random.click", 0.6f, (1f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f)
		
		return stack
	}
	
	override fun onUpdate(stack: ItemStack, world: World, player: Entity?, par4: Int, par5: Boolean) {
		if (!world.isRemote && player is EntityPlayer && stack.getItemDamage() > 0 && ManaItemHandler.requestManaExactForTool(stack, player, MANA_PER_DAMAGE * 2, true))
			stack.setItemDamage(stack.getItemDamage() - 1)
	}
	
	override fun onLeftClickEntity(resonator: ItemStack, player: EntityPlayer, entity: Entity): Boolean {
		val (x, y, z) = Vector3.fromEntityCenter(entity).mf()
		EntityResonance(entity.worldObj, player, x, y, z, resonator.mode, resonator.target, ItemTerraPick.isTipped(resonator), resonator.persistent, resonator.dilated, 1).spawn(resonator.limit)
		
		return true
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	fun onLeftClickBlock(e: PlayerInteractEvent) {
		if (e.action !== PlayerInteractEvent.Action.LEFT_CLICK_BLOCK || e.world.isRemote) return
		
		val resonator = e.entityPlayer.heldItem ?: return
		if (resonator.item !== AlfheimItems.resonator) return
		
		EntityResonance(e.entityPlayer.worldObj, e.entityPlayer, e.x, e.y, e.z, resonator.mode, resonator.target, ItemTerraPick.isTipped(resonator), resonator.persistent, resonator.dilated, 1).spawn(resonator.limit)
		
		e.isCanceled = true
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	fun onLeftClickAir(e: PlayerInteractAdequateEvent.LeftClick) {
		if (e.action !== PlayerInteractAdequateEvent.LeftClick.Action.LEFT_CLICK_AIR || e.player.worldObj.isRemote) return
		
		val resonator = e.player.heldItem ?: return
		if (resonator.item !== AlfheimItems.resonator) return
		
		val playerVec = Vector3.fromEntityCenter(e.player)
		val lookVec = Vector3(e.player.lookVec).mul(2.0)
		val placeVec = playerVec.copy().add(lookVec)
		
		val x = placeVec.x.mfloor()
		val y = placeVec.y.mfloor() + 1
		val z = placeVec.z.mfloor()
		
		EntityResonance(e.player.worldObj, e.player, x, y, z, resonator.mode, resonator.target, ItemTerraPick.isTipped(resonator), resonator.persistent, resonator.dilated, 1).spawn(resonator.limit)
	}
	
	@SideOnly(Side.CLIENT)
	override fun addInformation(stack: ItemStack, player: EntityPlayer, list: MutableList<Any?>, adv: Boolean) {
		addStringToTooltip(list, "alfheimmisc.resonator.mode${stack.mode}")
		addStringToTooltip(list, "alfheimmisc.resonator.target${stack.target}")
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
		iconTipped = IconHelper.forItem(reg, this, "Tipped")
	}
	
	override fun getIcon(stack: ItemStack, pass: Int) = if (ItemTerraPick.isTipped(stack)) iconTipped else itemIcon
	
	// workaround for fucked-up getIcon methods 
	@SideOnly(Side.CLIENT)
	override fun requiresMultipleRenderPasses() = true
	override fun getRenderPasses(metadata: Int) = 1
	
	@Suppress("OVERRIDE_DEPRECATION")
	override fun getItemAttributeModifiers() = HashMultimap.create<Any?, Any?>()!!
	
	override fun getHarvestLevel(stack: ItemStack?, toolClass: String?) = 30
	override fun getToolClasses(stack: ItemStack?) = emptySet<String>()
	override fun getDigSpeed(stack: ItemStack?, block: Block?, meta: Int) = 0.001f
	
	override fun func_150897_b(block: Block?) = true
	override fun func_150893_a(stack: ItemStack?, block: Block?) = 0.001f
	
	override fun usesMana(stack: ItemStack?) = true

	companion object {
		
		const val MANA_PER_DAMAGE = 100
		const val MAX_FOR_PLAYER = 5
		
		const val TAG_UNLIMITED = "unlimited"
		
		var ItemStack.dilated
			get() = ItemNBTHelper.getBoolean(this, EntityResonance.TAG_PERSISTENT, false)
			set(value) = ItemNBTHelper.setBoolean(this, EntityResonance.TAG_PERSISTENT, value)
		
		private var ItemStack.mode
			get() = ItemNBTHelper.getInt(this, EntityResonance.TAG_MODE, 0)
			set(value) = ItemNBTHelper.setInt(this, EntityResonance.TAG_MODE, value)
		
		var ItemStack.persistent
			get() = ItemNBTHelper.getBoolean(this, EntityResonance.TAG_PERSISTENT, false)
			set(value) = ItemNBTHelper.setBoolean(this, EntityResonance.TAG_PERSISTENT, value)
		
		private var ItemStack.target
			get() = ItemNBTHelper.getInt(this, EntityResonance.TAG_TARGET, 0)
			set(value) = ItemNBTHelper.setInt(this, EntityResonance.TAG_TARGET, value)
		
		var ItemStack.unlimited
			get() = ItemNBTHelper.getBoolean(this, TAG_UNLIMITED, false)
			set(value) = ItemNBTHelper.setBoolean(this, TAG_UNLIMITED, value)
		
		private val ItemStack.limit
			get() = if (unlimited) -1 else MAX_FOR_PLAYER
	}
}