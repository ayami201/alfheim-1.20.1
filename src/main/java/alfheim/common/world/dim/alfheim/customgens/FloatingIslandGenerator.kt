package alfheim.common.world.dim.alfheim.customgens

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import net.minecraft.init.Blocks
import net.minecraft.world.World
import vazkii.botania.common.block.ModBlocks
import kotlin.math.sin
import kotlin.random.Random

object FloatingIslandGenerator {
	
	fun generateSubstrate(world: World, xOff: Int, zOff: Int) {
		val peaks = mutableListOf(Peak(0, 0, 0.0, 1.5, 1.25))
		
		val rand = Random(world.seed)
		
		addPeaks(peaks, rand, 16..32, 32..96, 2.5f, 1.5..2.0)
		addPeaks(peaks, rand, 32..48, 96..128, 4f, 2.0..2.5)
		
		for (x in -384 until 384)
			for (z in -384 until 384) {
				val range = 240 downTo heightAt(peaks, x, z).I + 128
				if (range.isEmpty()) continue
				for (y in range)
					world.setBlock(x + xOff, y, z + zOff, ModBlocks.livingrock, 0, 0)
				
				println("$x $z done")
			}
	}
	
	private fun heightAt(peaks: MutableList<Peak>, i: Int, k: Int): Double {
		return peaks.minOf { (x, z, offset, width, curves) ->
			val dist = Vector3.pointDistancePlane(x, z, i, k) + offset
			dist * width - sin(dist) * curves
		}
	}
	
	private fun addPeaks(peaks: MutableList<Peak>, rand: Random, counts: IntProgression, dists: IntProgression, mod: Float, ws: ClosedFloatingPointRange<Double>) {
		val max = rand.nextInt(counts.first, counts.last)
		var i = 0
		while (i <= max) {
			val (x, _, z) = Vector3().set(rand.nextDouble() - 0.5, 0, rand.nextDouble() - 0.5).normalize().mul(rand.nextInt(dists.first, dists.last)).I
			val dist = Vector3.pointDistancePlane(0, 0, x, z)
			if (peaks.any { (i, k) -> Vector3.pointDistancePlane(i, k, x, z) < 16 }) continue
			val w = rand.nextDouble(ws.start, ws.endInclusive)
			peaks += Peak(x, z, dist / mod, w, rand.nextDouble(0.5, w - 0.25))
			i++
		}
	}
	
	private data class Peak(val x: Int, val z: Int, val offset: Double, val width: Double, val curves: Double)
}