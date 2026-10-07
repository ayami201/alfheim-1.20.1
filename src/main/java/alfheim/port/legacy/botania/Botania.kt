package alfheim.port.legacy.botania

import alexsocol.asjlib.math.Vector3
import net.minecraft.client.Minecraft
import net.minecraft.client.ParticleStatus
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.server.ServerLifecycleHooks
import vazkii.botania.client.core.handler.ClientTickHandler
import vazkii.botania.client.fx.SparkleParticleData
import vazkii.botania.client.fx.WispParticleData

/**
 * `vazkii.botania.common.Botania` r1.8-249: то, что автор берёт у `Botania.proxy`. В Botania 1.20.1 прокси нет; часть
 * его методов — здесь, с той же работой
 */
object Botania {
	
	val proxy = Proxy
	
	object Proxy {
		
		/**
		 * `getWorldElapsedTicks`: на клиенте — тики игры Botania (`ClientTickHandler.ticksInGame`), на выделенном
		 * сервере — время основного мира, как у прокси 1.7.10 своей стороны
		 */
		val worldElapsedTicks: Long
			get() = if (FMLEnvironment.dist.isClient) ClientTicks.ticks() else ServerLifecycleHooks.getCurrentServer()?.overworld()?.gameTime ?: 0L
		
		/**
		 * `lightningFX(world, start, end, ticksPerMeter, colorOuter, colorInner)` — молния Botania 1.20.1. Вектор
		 * Botania 1.7.10 (`vazkii.botania.common.core.helper.Vector3`) в 1.20.1 не сохранился — у автора его место
		 * занимает `Vector3` ASJCore с теми же методами
		 */
		fun lightningFX(world: Level, start: Vector3, end: Vector3, ticksPerMeter: Float, colorOuter: Int, colorInner: Int) =
			vazkii.botania.common.proxy.Proxy.INSTANCE.lightningFX(world, start.toVec3(), end.toVec3(), ticksPerMeter, colorOuter, colorInner)
		
		/** Флаг `setSparkleFXNoClip` прокси 1.7.10: пока он стоит, искры летят сквозь блоки */
		private var sparkleNoClip = false
		
		/** `setSparkleFXNoClip(noclip)`: следующие искры — сквозь блоки (`SparkleParticleData.noClip`), пока флаг не снят */
		fun setSparkleFXNoClip(noclip: Boolean) {
			sparkleNoClip = noclip
		}
		
		/**
		 * `sparkleFX(world, x, y, z, r, g, b, size, m)` — искра Botania 1.20.1 (`SparkleParticleData`). Как у прокси
		 * 1.7.10, частица появляется только у клиента: мир сервера частиц не рисует
		 */
		fun sparkleFX(world: Level, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, m: Int) =
			world.addParticle(if (sparkleNoClip) SparkleParticleData.noClip(size, r, g, b, m) else SparkleParticleData.sparkle(size, r, g, b, m), x, y, z, 0.0, 0.0, 0.0)
		
		/** `wispFX(world, x, y, z, r, g, b, size, gravity)` — огонёк Botania 1.20.1; «гравитация» 1.7.10 — скорость вниз */
		fun wispFX(world: Level, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, gravity: Float) =
			wisp(world, WispParticleData.wisp(size, r, g, b, 1f), x, y, z, 0.0, -gravity.toDouble(), 0.0)
		
		/** `wispFX(world, x, y, z, r, g, b, size, motionX, motionY, motionZ)` — огонёк Botania 1.20.1 с заданной скоростью */
		fun wispFX(world: Level, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, motionX: Float, motionY: Float, motionZ: Float) =
			wispFX(world, x, y, z, r, g, b, size, motionX, motionY, motionZ, 1f)
		
		/**
		 * `wispFX(world, x, y, z, r, g, b, size, motionX, motionY, motionZ, maxAgeMul)` — огонёк с заданной скоростью;
		 * [maxAgeMul] — во сколько раз дольше обычного он живёт
		 */
		fun wispFX(world: Level, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, motionX: Float, motionY: Float, motionZ: Float, maxAgeMul: Float) =
			wisp(world, WispParticleData.wisp(size, r, g, b, maxAgeMul), x, y, z, motionX.toDouble(), motionY.toDouble(), motionZ.toDouble())
		
		/** Флаг `setWispFXDistanceLimit` прокси 1.7.10: пока он снят, огоньки видны на любом расстоянии */
		private var wispDistanceLimit = true
		
		/**
		 * `setWispFXDistanceLimit(limit)`: в 1.7.10 огонёк дальше 50 блоков от игрока (25 при «быстрой» графике) сразу
		 * гас; без ограничения — нет. Следующие огоньки, пока флаг снят, появляются на любом расстоянии ([wisp])
		 */
		fun setWispFXDistanceLimit(limit: Boolean) {
			wispDistanceLimit = limit
		}
		
		/**
		 * Огонёк в мире. С ограничением расстояния — как любая частица 1.20.1: не дальше 32 блоков от камеры, по
		 * настройке «Частицы». Без ограничения — на любом расстоянии; настройку «Частицы» тогда применяет порт, как это
		 * делает 1.20.1: Botania 1.7.10 прореживала по ней свои частицы и без ограничения расстояния. Мир сервера частиц
		 * не рисует
		 */
		private fun wisp(world: Level, data: WispParticleData, x: Double, y: Double, z: Double, motionX: Double, motionY: Double, motionZ: Double) {
			if (wispDistanceLimit) world.addParticle(data, x, y, z, motionX, motionY, motionZ)
			else if (world.isClientSide && ClientParticles.shown(world.random)) world.addParticle(data, true, x, y, z, motionX, motionY, motionZ)
		}
		
		/**
		 * `removeSextantMultiblock` — убрать подсветку секстанта: в 1.7.10 — структуру класса `MultiblockSextant`, в
		 * 1.20.1 — структуру Patchouli с id секстанта (`WorldshaperssSextantItem.MULTIBLOCK_ID`). На сервере — ничего
		 */
		fun removeSextantMultiblock() = vazkii.botania.common.proxy.Proxy.INSTANCE.clearSextantMultiblock()
	}
}

/** Отдельный класс: на выделенном сервере не загружается, а с ним и классы клиента Botania */
private object ClientTicks {
	
	fun ticks() = ClientTickHandler.ticksInGame.toLong()
}

/** Отдельный класс: на выделенном сервере не загружается, а с ним и классы клиента игры */
private object ClientParticles {
	
	/** Появится ли частица при настройке «Частицы», как решает 1.20.1 (`LevelRenderer`): «меньше» — 2 из 3, «минимум» — нет */
	fun shown(random: RandomSource) = when (Minecraft.getInstance().options.particles().get()) {
		ParticleStatus.MINIMAL   -> false
		ParticleStatus.DECREASED -> random.nextInt(3) != 0
		else                     -> true
	}
}
