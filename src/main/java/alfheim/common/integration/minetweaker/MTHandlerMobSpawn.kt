package alfheim.common.integration.minetweaker

import alfheim.api.ModInfo
import alfheim.common.world.mobspawn.MobSpawnHandler
import minetweaker.IUndoableAction
import minetweaker.MineTweakerAPI
import stanhebben.zenscript.annotations.*

@ZenClass("mods." + ModInfo.MODID + ".MobSpawn")
object MTHandlerMobSpawn {
	
	@ZenMethod
	@JvmStatic
	fun addMob(name: String, maxPerPlayer: Int, minGroup: Int, maxGroup: Int, dimID: Int) {
		MineTweakerAPI.apply(SpawnEntry(name, maxPerPlayer, minGroup, maxGroup, dimID))
	}
	
	private class SpawnEntry(val name: String, val maxPerPlayer: Int, val minGroup: Int, val maxGroup: Int, val dimID: Int): IUndoableAction {
		
		override fun apply() = MobSpawnHandler.registerMob(name, maxPerPlayer, minGroup, maxGroup, dimID)
		
		override fun canUndo() = true
		
		override fun undo() = MobSpawnHandler.unregisterMob(name, dimID, maxPerPlayer, minGroup, maxGroup)
		
		override fun describe() = "Adding spawn entry of $name to $dimID with params ($maxPerPlayer, $minGroup, $maxGroup)"
		
		override fun describeUndo() = "Removed spawn entry of $name from $dimID with params ($maxPerPlayer, $minGroup, $maxGroup)"
		
		override fun getOverrideKey() = null
	}
}