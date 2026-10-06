package alfheim.port.test

import alexsocol.asjlib.component1
import alexsocol.asjlib.component2
import alexsocol.asjlib.component3
import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.tile.TileTreeBerry
import alfheim.port.legacy.*
import alfheim.port.registry.AlfheimSounds
import alfheim.port.registry.LegacyIds
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.DirectionalPlaceContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import vazkii.botania.common.block.BotaniaBlocks
import java.util.UUID

/**
 * КТ-1: прослойка `alfheim.port.legacy` (SPEC, Р-4) на мире сервера: блоки по координатам, звуки и частицы по
 * именам 1.7.10; КТ-2: материалы и замена блока 1.7.10, блок-сущности 1.7.10.
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

	/**
	 * Материал 1.7.10 любого блока (`block.material`, `Materials1710`): земля и трава ванилы и Botania — как в 1.7.10, блок
	 * 1.20.1 с тегом `minecraft:dirt` — земля, блок порта — свой; воздух, вода, лава; прочие — камень. Заменить блок
	 * (`isReplaceable` 1.7.10) можно, если так решил его класс (ягода — нет, хотя материал лиан заменяем), у прочих —
	 * если заменяем материал; это же спрашивает установка блока 1.20.1 (`canBeReplaced`)
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun materialsAndReplacement(helper: GameTestHelper) {
		for (block in listOf(Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.ROOTED_DIRT, Blocks.MUD)) helper.assertTrue(block.material === Material.ground, "$block: ground")
		for (block in listOf(Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.HAY_BLOCK, BotaniaBlocks.enchantedSoil, BotaniaBlocks.vividGrass)) helper.assertTrue(block.material === Material.grass, "$block: grass")
		helper.assertTrue(Blocks.AIR.material === Material.air && Blocks.CAVE_AIR.material === Material.air && Blocks.WATER.material === Material.water && Blocks.LAVA.material === Material.lava, "air and liquids")
		helper.assertTrue(Blocks.STONE.material === Material.rock && AlfheimBlocks.irisDirt[3].material === Material.ground && AlfheimBlocks.circuitBerry.material === Material.vine, "stone, port blocks")

		val pos = BlockPos(1, 1, 1)
		val abs = helper.absolutePos(pos)
		fun replaceable() = helper.getBlockState(pos).block.isReplaceable(helper.level, abs.x, abs.y, abs.z)
		fun placeable() = helper.getBlockState(pos).canBeReplaced(DirectionalPlaceContext(helper.level, abs, Direction.DOWN, ItemStack(Blocks.STONE), Direction.UP))
		helper.setBlock(pos, Blocks.GRASS)
		helper.assertTrue(replaceable() && placeable(), "tall grass is replaceable")
		helper.setBlock(pos.above(), AlfheimBlocks.circuitLeaves)
		helper.setBlock(pos, AlfheimBlocks.circuitBerry)
		helper.assertTrue(!replaceable() && !placeable(), "a tree berry is not replaceable")
		helper.setBlock(pos, Blocks.STONE)
		helper.assertTrue(!replaceable() && !placeable(), "stone is not replaceable")
		helper.succeed()
	}

	/**
	 * Блок-сущность 1.7.10 (`alfheim.port.legacy.TileEntity`): создаётся в своей точке со своим типом, id типа — имя
	 * автора в snake_case, старое имя — в `legacy_ids.json`; тип считает своим блок, в котором она создана; тик
	 * блок-сущности даёт блок (`ITileEntityProvider`), только если она тикает (`canUpdate`); данные она сохраняет со своим
	 * id
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun tileEntities(helper: GameTestHelper) {
		val pos = BlockPos(1, 1, 1)
		helper.setBlock(pos.above(), AlfheimBlocks.netherLeaves)
		helper.setBlock(pos, AlfheimBlocks.netherBerry)
		val tile = helper.getBlockEntity(pos) as? TileTreeBerry ?: throw GameTestAssertException("no berry tile: ${helper.getBlockEntity(pos)}")
		val type = legacyTileType<TileTreeBerry>()
		helper.assertTrue(tile.type === type && tile.berryType == 4 && tile.getBlockType() === AlfheimBlocks.netherBerry, "berry tile: $tile")
		helper.assertTrue(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type) == ResourceLocation(MODID, "tree_berry"), "block entity id")
		helper.assertTrue(LegacyIds.blockEntities["$MODID:TreeBerry"]?.get("*")?.id == ResourceLocation(MODID, "tree_berry"), "legacy block entity id")
		val state = helper.getBlockState(pos)
		helper.assertTrue(type.isValid(state) && !type.isValid(Blocks.STONE.defaultBlockState()), "the type knows its blocks")
		// ягода не тикает (canUpdate = false): 1.7.10 не ставил такую блок-сущность в список тикающих
		helper.assertTrue(!tile.canUpdate() && state.getTicker(helper.level, type) == null, "a tile entity that does not update has no ticker")
		helper.assertTrue(tile.saveWithFullMetadata().getString("id") == "$MODID:tree_berry", "saved id")
		helper.succeed()
	}
}
