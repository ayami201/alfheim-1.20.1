package alfheim.port.test

import alexsocol.asjlib.component1
import alexsocol.asjlib.component2
import alexsocol.asjlib.component3
import alfheim.api.ModInfo.MODID
import alfheim.port.legacy.*
import alfheim.port.registry.AlfheimSounds
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import java.util.UUID

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

	/** Стороны и координаты 1.7.10, флаги игрока, имя в реестре, пустой NBT (партия 5б) */
	@JvmStatic
	@GameTest(template = "empty")
	fun coordsAndCapabilities(helper: GameTestHelper) {
		for (id in 0..5) {
			val dir = ForgeDirection.getOrientation(id)
			val vanilla = Direction.from3DDataValue(id)
			helper.assertTrue(dir.direction == vanilla && dir.offsetX == vanilla.stepX && dir.offsetY == vanilla.stepY && dir.offsetZ == vanilla.stepZ, "side $id: $dir")
		}
		for (id in listOf(-1, 6)) {
			val dir = ForgeDirection.getOrientation(id)
			helper.assertTrue(dir == ForgeDirection.UNKNOWN && dir.direction == null && dir.offsetX == 0 && dir.offsetY == 0 && dir.offsetZ == 0, "side $id is UNKNOWN")
		}

		val (x, y, z) = ChunkCoordinates(1, -1, 2)
		helper.assertTrue(x == 1 && y == -1 && z == 2 && ChunkCoordinates(1, -1, 2).posY == -1, "ChunkCoordinates")

		val player = FakePlayerFactory.get(helper.level, GameProfile(UUID.randomUUID(), "alfheim-capabilities"))
		player.capabilities.isCreativeMode = true
		player.capabilities.allowFlying = true
		player.capabilities.isFlying = true
		player.capabilities.disableDamage = true
		helper.assertTrue(player.abilities.instabuild && player.abilities.mayfly && player.abilities.flying && player.abilities.invulnerable, "capabilities set the abilities")
		player.capabilities.isCreativeMode = false
		player.capabilities.allowFlying = false
		player.capabilities.isFlying = false
		player.capabilities.disableDamage = false
		helper.assertTrue(!player.abilities.instabuild && !player.abilities.mayfly && !player.abilities.flying && !player.abilities.invulnerable, "capabilities clear the abilities")

		helper.assertTrue(GameRegistry.findUniqueIdentifierFor(Blocks.STONE).toString() == "minecraft:stone" && GameRegistry.findUniqueIdentifierFor(Items.STICK).toString() == "minecraft:stick", "registry names")
		helper.assertTrue(CompoundTag().hasNoTags() && !CompoundTag().apply { putInt("a", 1) }.hasNoTags(), "hasNoTags")
		helper.succeed()
	}
}
