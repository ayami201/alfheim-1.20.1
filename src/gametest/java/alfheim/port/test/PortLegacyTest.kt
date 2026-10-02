package alfheim.port.test

import alfheim.api.ModInfo.MODID
import alfheim.port.legacy.*
import alfheim.port.registry.AlfheimSounds
import com.google.gson.JsonParser
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate

/**
 * КТ-1: прослойка `alfheim.port.legacy` (SPEC, Р-4) на мире сервера: блоки по координатам, звуки и частицы по
 * именам 1.7.10.
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortLegacyTest {

	@JvmStatic
	@GameTest(template = "empty")
	fun blocksByCoordinates(helper: GameTestHelper) {
		val level = helper.level
		val pos = helper.absolutePos(BlockPos(1, 2, 1))
		val x = pos.x; val y = pos.y; val z = pos.z

		helper.assertTrue(level.isAirBlock(x, y, z), "no air at start")
		helper.assertTrue(level.setBlock(x, y, z, Blocks.STONE) && level.getBlock(x, y, z) === Blocks.STONE && !level.isAirBlock(x, y, z), "setBlock(block)")

		level.setBlock(x, y, z, Blocks.CHEST.defaultBlockState(), 3)
		helper.assertTrue(level.getTileEntity(x, y, z) is ChestBlockEntity, "getTileEntity: ${level.getTileEntity(x, y, z)}")

		helper.assertTrue(level.setBlockToAir(x, y, z) && level.isAirBlock(x, y, z) && level.getTileEntity(x, y, z) == null, "setBlockToAir")

		level.setBlock(x, y, z, Blocks.STONE)
		level.scheduleBlockUpdate(x, y, z, Blocks.STONE, 5)
		helper.assertTrue(level.blockTicks.hasScheduledTick(pos, Blocks.STONE), "scheduleBlockUpdate")
		level.notifyBlocksOfNeighborChange(x, y, z, Blocks.STONE)
		level.setBlockToAir(x, y, z)

		helper.assertTrue(!level.isRemote, "server level is remote")
		helper.succeed()
	}

	@JvmStatic
	@GameTest(template = "empty")
	fun soundsByLegacyName(helper: GameTestHelper) {
		// каждая запись таблицы — событие в реестре 1.20.1
		val json = LegacySounds::class.java.getResourceAsStream("/$MODID/legacy_sounds.json")!!.reader().use { JsonParser.parseReader(it).asJsonObject }
		for (name in json.getAsJsonObject("sounds").keySet()) LegacySounds.event(name)

		helper.assertTrue(LegacySounds.event("random.fizz") === SoundEvents.FIRE_EXTINGUISH && LegacySounds.source("random.fizz") == SoundSource.BLOCKS, "random.fizz")
		helper.assertTrue(LegacySounds.event("mob.endermen.portal") === SoundEvents.ENDERMAN_TELEPORT && LegacySounds.source("mob.endermen.portal") == SoundSource.HOSTILE, "mob.endermen.portal")
		helper.assertTrue(BuiltInRegistries.SOUND_EVENT.getKey(LegacySounds.event("botania:enchanterBlock")) == ResourceLocation("botania", "enchanter_form"), "botania:enchanterBlock")
		helper.assertTrue(LegacySounds.event("alfheim:quad") === AlfheimSounds.events["quad"]!!.get() && LegacySounds.source("alfheim:quad") == SoundSource.PLAYERS, "alfheim:quad")
		helper.assertTrue(runCatching { LegacySounds.event("random.nonexistent") }.isFailure, "unknown sound must fail")

		// на сервере звук уходит игрокам рядом; здесь их нет — вызов просто не падает
		val pos = helper.absolutePos(BlockPos(1, 2, 1))
		helper.level.playSoundEffect(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, "fire.ignite", 1f, 1f)
		helper.level.playSoundAtEntity(helper.spawn(EntityType.PIG, BlockPos(1, 2, 1)), "alfheim:redexp", 1f, 1f)
		helper.succeed()
	}

	@JvmStatic
	@GameTest(template = "empty")
	fun particlesByLegacyName(helper: GameTestHelper) {
		helper.assertTrue(LegacyParticles["explode"] === ParticleTypes.POOF && LegacyParticles["lava"] === ParticleTypes.LAVA, "explode, lava")
		// у цветных частиц 1.20.1 цвет — параметр, их переносят на месте вызова
		helper.assertTrue(runCatching { LegacyParticles["reddust"] }.isFailure, "reddust must be ported at the call site")

		val pos = helper.absolutePos(BlockPos(1, 2, 1))
		helper.level.spawnParticle("explode", pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 0.0, 0.0, 0.0)
		helper.succeed()
	}
}
