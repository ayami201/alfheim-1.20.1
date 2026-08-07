package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.api.entity.*
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.world.*
import net.minecraftforge.oredict.*
import java.awt.*
import java.awt.datatransfer.*
import java.util.*
import kotlin.math.*

@Suppress("KotlinConstantConditions", "ControlFlowWithEmptyBody")
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
			
		} catch (e: Throwable) {
			ASJUtilities.error("Oops!", e)
		}
		
		return false
	}
	
	private fun oreScanner(world: World) {
		val ores = HashMap<String, Int>()
		val chunks = 256
		val range = sqrt(chunks.toDouble()).toInt()
		
		for (x in 0..<(16 * range))
			for (y in 0..150)
				for (z in 0..<(16 * range)) {
					val block = world.getBlock(x, y, z)
					val meta: Int = world.getBlockMetadata(x, y, z)
					val oredict = OreDictionary.getOreName(OreDictionary.getOreID(ItemStack(block, 1, meta)))
					if (oredict.lowercase(Locale.getDefault()).contains("ore")) {
						val count: Int = (if (ores.containsKey(oredict)) ores[oredict] else 0)!!
						ores[oredict] = count + 1
					}
				}
		
		var selectionStr = "Chunks checked: $chunks\n\n"
		for (s in ores.keys) selectionStr = selectionStr + s + " = " + ores[s] + " (avg " + (ores[s]!!.toDouble() / chunks.toDouble()) + " per chunk)\n"
		val selection = StringSelection(selectionStr)
		Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
		
		ASJUtilities.chatLog("Done!")
	}
}
