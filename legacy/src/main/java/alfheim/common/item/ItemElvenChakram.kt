package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.client.core.helper.IconHelper
import alfheim.common.entity.EntityElvenChakram
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.*
import net.minecraft.util.IIcon
import net.minecraft.world.World
import vazkii.botania.common.achievement.*

class ItemElvenChakram: ItemMod("ElvenChakram"), ICraftAchievement {
	
	lateinit var iconEnder: IIcon
	
	init {
		maxStackSize = 6
		hasSubtypes = true
	}
	
	override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in 0..1)
			list.add(ItemStack(item, 1, i))
	}
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this, 0)
		iconEnder = IconHelper.forItem(reg, this, 1)
	}
	
	override fun getIconFromDamage(dmg: Int) = if (dmg == 0) itemIcon!! else iconEnder
	
	override fun getUnlocalizedName(stack: ItemStack) = super.getUnlocalizedName() + stack.getItemDamage()
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (world.isRemote) return stack
		
		--stack.stackSize
		player.playSoundAtEntity("random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f))
		
		val c = EntityElvenChakram(world, player, if (stack.meta != 0) stack else null)
		c.spawn(world)
		
		if (stack.meta != 0 && !player.isSneaking && ASJUtilities.isServer)
			player.mountEntity(c)
		
		return stack
	}
	
	override fun getAchievementOnCraft(stack: ItemStack?, player: EntityPlayer?, matrix: IInventory?) = ModAchievements.terrasteelWeaponCraft!!
}
