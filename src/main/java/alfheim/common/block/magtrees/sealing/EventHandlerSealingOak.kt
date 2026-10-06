package alfheim.common.block.magtrees.sealing

// PORT: импорты 1.20.1; звук клиента 1.7.10 (ISound) — SoundInstance, событие PlaySoundEvent17 — PlaySoundEvent
// (MAPPING.md)
import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.port.legacy.*
import net.minecraft.client.resources.sounds.*
import net.minecraft.client.sounds.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.util.concurrent.CompletableFuture

/**
 * @author WireSegal
 * Created at 8:49 AM on 1/27/16.
 */
object EventHandlerSealingOak {
	
	const val MAXRANGE = 16
	
	// PORT: только клиент: с этой пометкой класс грузится и на сервере — расчёт множителя (calculateMultiplier) проверяет
	// GameTest
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	fun onSound(event: PlaySoundEvent) {
		// PORT: звук события 1.20.1 — sound (result 1.7.10; объявлен nullable — читается в переменную один раз), повторяемый
		// звук — TickableSoundInstance (ITickableSound)
		val sound = event.sound
		if (sound == null || sound is TickableSoundInstance) return
//		if (event.result == null || event.result is ITickableSound) return
		
		// PORT: мир клиента 1.20.1 — mc.level, место звука — x, y, z (double; во float 1.7.10 — xPosF…)
		val world = mc.level ?: return
		val x = sound.x.I
		val y = sound.y.I
		val z = sound.z.I
//		val world = mc.theWorld ?: return
//		val x = event.result.xPosF.I
//		val y = event.result.yPosF.I
//		val z = event.result.zPosF.I
		
		val volumeMultiplier = calculateMultiplier(world, x, y, z)
		
		if (volumeMultiplier == 1f) return
		
		event.sound = VolumeModSound(sound, volumeMultiplier)
//		event.result = VolumeModSound(event.result, volumeMultiplier)
	}
	
	fun calculateMultiplier(world: World, x: Int, y: Int, z: Int): Float {
		// PORT-OPT: блоков, которые глушат звук, нет ни в одной секции чанков вокруг — множитель 1 без обхода 33³ блоков на
		// каждый звук (Level.mayContain)
		if (!world.mayContain(x, y, z, MAXRANGE) { it.block is ISoundSilencer }) return 1f
		
		var volumeMultiplier = 1f
		
		for (dx in x.bidiRange(MAXRANGE)) {
			for (dy in y.bidiRange(MAXRANGE)) {
				for (dz in z.bidiRange(MAXRANGE)) {
					val block = world.getBlock(dx, dy, dz)
					if (block !is ISoundSilencer) continue
					
					val distance = Vector3.pointDistanceSpace(dx + 0.5, dy + 0.5, dz + 0.5, x, y, z)
					if (distance > MAXRANGE || !block.canSilence(world, dx, dy, dz, distance)) continue
					
					volumeMultiplier *= block.getVolumeMultiplier(world, dx, dy, dz, distance)
				}
			}
		}
		
		return volumeMultiplier
	}
	
	// PORT: звук 1.20.1 — SoundInstance: методы ISound 1.7.10 — под новыми именами (location, isLooping, delay, x…), новые
	// (resolve, sound, source, isRelative…) — исходного звука; громкость умножается, как у автора
	@OnlyIn(Dist.CLIENT)
	class VolumeModSound(val sound: SoundInstance, val volumeMult: Float): SoundInstance {
		
		override fun getLocation(): ResourceLocation = sound.location
		override fun resolve(manager: SoundManager): WeighedSoundEvents? = sound.resolve(manager)
		override fun getSound(): Sound = sound.sound
		override fun getSource(): SoundSource = sound.source
		override fun isLooping(): Boolean = sound.isLooping
		override fun isRelative(): Boolean = sound.isRelative
		override fun getDelay(): Int = sound.delay
		override fun getVolume(): Float = sound.volume * volumeMult
		override fun getPitch(): Float = sound.pitch
		override fun getX(): Double = sound.x
		override fun getY(): Double = sound.y
		override fun getZ(): Double = sound.z
		override fun getAttenuation(): SoundInstance.Attenuation = sound.attenuation
		override fun canStartSilent(): Boolean = sound.canStartSilent()
		override fun canPlaySound(): Boolean = sound.canPlaySound()
		override fun getStream(buffers: SoundBufferLibrary, sound: Sound, looping: Boolean): CompletableFuture<AudioStream> = this.sound.getStream(buffers, sound, looping)
//		override fun getPositionedSoundLocation(): ResourceLocation = sound.positionedSoundLocation
//		override fun canRepeat(): Boolean = sound.canRepeat()
//		override fun getRepeatDelay(): Int = sound.repeatDelay
//		override fun getVolume(): Float = sound.volume * volumeMult
//		override fun getPitch(): Float = sound.pitch
//		override fun getXPosF(): Float = sound.xPosF
//		override fun getYPosF(): Float = sound.yPosF
//		override fun getZPosF(): Float = sound.zPosF
//		override fun getAttenuationType(): ISound.AttenuationType = sound.attenuationType
	}
}
