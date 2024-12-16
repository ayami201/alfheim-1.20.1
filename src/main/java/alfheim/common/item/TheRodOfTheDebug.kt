package alfheim.common.item

import alexsocol.asjlib.ASJUtilities
import alfheim.api.ModInfo
import alfheim.api.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import thaumcraft.common.tiles.TileInfusionMatrix

class TheRodOfTheDebug: ItemMod("TheRodOfTheDebug") {
	
	init {
		maxStackSize = 1
		setFull3D()
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (!ModInfo.DEV) return stack
		
		try {
			if (!player.isSneaking) {
				if (!world.isRemote) {
				
				} else {
				
				}
			} else {
				player.raceID = (player.race.ordinal + 1) % EnumRace.entries.size
				ASJUtilities.chatLog("${player.race.ordinal} - ${player.race}", player)
			}
		} catch (e: Throwable) {
			ASJUtilities.error("Oops!", e)
		}
		
		return stack
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (!ModInfo.DEV) return false
		
		try {
			val te = world.getTileEntity(x, y, z) as TileInfusionMatrix
			ASJUtilities.chatLog("${te.symmetry}")
			te.checkSurroundings = true
//			if (te != null) {
//				val nbt = NBTTagCompound()
//				te.writeToNBT(nbt)
//				for (s in ASJUtilities.toString(nbt).split("\n")) ASJUtilities.chatLog(s, world)
//			}
		} catch (e: Throwable) {
			ASJUtilities.error("Oops!", e)
		}
		
		return false
	}
}
