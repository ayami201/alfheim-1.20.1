package alfheim.common.core.asm.hook.integration

import alfheim.common.item.rod.*
import gloomyfolken.hooklib.asm.*
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import kotlin.math.max

object RedstoneRodHookHandler {
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ReturnCondition.ALWAYS)
	fun isBlockProvidingPowerTo(world: World, x: Int, y: Int, z: Int, direction: Int, @Hook.ReturnValue result: Int): Int {
		val d = ForgeDirection.entries[direction].opposite
		val power = RedstoneSignalHandler.get().getPower(world, x + d.offsetX, y + d.offsetY, z + d.offsetZ)
		return if (power.second == RedstoneSignal.EnumRedstoneType.STRONG) max(power.first, result) else result
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ReturnCondition.ALWAYS)
	fun getIndirectPowerLevelTo(world: World, x: Int, y: Int, z: Int, direction: Int, @Hook.ReturnValue result: Int): Int {
		val d = ForgeDirection.entries[direction].opposite
		val power = RedstoneSignalHandler.get().getPower(world, x + d.offsetX, y + d.offsetY, z + d.offsetZ).first
		return max(power, result)
	}
}