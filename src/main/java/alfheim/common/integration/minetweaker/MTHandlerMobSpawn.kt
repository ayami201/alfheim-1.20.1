package alfheim.common.integration.minetweaker

import alexsocol.asjlib.mfloor
import alfheim.api.ModInfo
import alfheim.common.world.mobspawn.MobSpawnHandler
import minetweaker.*
import net.minecraft.entity.Entity
import ru.vamig.worldengine.*
import stanhebben.zenscript.annotations.*

@ZenClass("mods." + ModInfo.MODID + ".MobSpawn")
object MTHandlerMobSpawn {
	
	@ZenMethod
	@JvmStatic
	fun addMob(name: String, maxPerPlayer: Int, minGroup: Int, maxGroup: Int, dimID: Int) {
		MineTweakerAPI.apply(SpawnEntry(name, maxPerPlayer, minGroup, maxGroup, dimID))
	}
	
	@ZenMethod
	@JvmStatic
	fun addMob(name: String, maxPerPlayer: Int, minGroup: Int, maxGroup: Int, dimID: Int, biomes: Array<String>) {
		MineTweakerAPI.apply(SpawnEntry(name, maxPerPlayer, minGroup, maxGroup, dimID) {
			WE_Biome.getBiomeAt((it.worldObj.provider as? WE_WorldProvider)?.chunkProvider ?: return@SpawnEntry false, it.posX.mfloor(), it.posZ.mfloor()).biomeName in biomes
		})
	}
	
	@ZenMethod
	@JvmStatic
	fun addMob(name: String, maxPerPlayer: Int, minGroup: Int, maxGroup: Int, dimID: Int, biomes: IntArray) {
		MineTweakerAPI.apply(SpawnEntry(name, maxPerPlayer, minGroup, maxGroup, dimID) {
			it.worldObj.getBiomeGenForCoords(it.posX.mfloor(), it.posZ.mfloor()).biomeID in biomes
		})
	}
	
	private class SpawnEntry(val name: String, val maxPerPlayer: Int, val minGroup: Int, val maxGroup: Int, val dimID: Int, val spawnCheck: ((Entity) -> Boolean)? = null): IUndoableAction {
		
		override fun apply() = MobSpawnHandler.registerMob(name, maxPerPlayer, minGroup, maxGroup, dimID, spawnCheck)
		
		override fun canUndo() = true
		
		override fun undo() = MobSpawnHandler.unregisterMob(name, maxPerPlayer, minGroup, maxGroup, dimID)
		
		override fun describe() = "Adding spawn entry of $name to $dimID with params ($maxPerPlayer, $minGroup, $maxGroup)"
		
		override fun describeUndo() = "Removed spawn entry of $name from $dimID with params ($maxPerPlayer, $minGroup, $maxGroup)"
		
		override fun getOverrideKey() = null
	}
}