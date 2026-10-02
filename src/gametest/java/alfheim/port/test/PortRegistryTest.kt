package alfheim.port.test

import alfheim.api.ModInfo.MODID
import alfheim.common.core.util.AlfheimTab
import alfheim.port.registry.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate

/**
 * КТ-1: регистрация. Реестры мода на месте, вкладка зарегистрирована, у каждой записи legacy_ids.json
 * есть вещь в реестре 1.20.1, имена строятся по правилу SPEC, Р-5.
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortRegistryTest {
	
	@JvmStatic
	@GameTest(template = "empty")
	fun creativeTabRegistered(helper: GameTestHelper) {
		val id = ResourceLocation(MODID, "alfheim")
		helper.assertTrue(BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(id), "Creative tab $id is not registered")
		helper.assertTrue(AlfheimTab.tab.isPresent && BuiltInRegistries.CREATIVE_MODE_TAB.getKey(AlfheimTab.tab.get()) == id, "AlfheimTab.tab is not $id")
		helper.succeed()
	}
	
	@JvmStatic
	@GameTest(template = "empty")
	fun legacyIdsPointToRegisteredThings(helper: GameTestHelper) {
		val missing = ArrayList<String>()
		for ((old, metas) in LegacyIds.blocks) for ((meta, target) in metas) {
			val block = BuiltInRegistries.BLOCK.getOptional(target.id)
			if (block.isEmpty) missing += "block $old:$meta -> ${target.id}"
			else for ((name, value) in target.state) {
				val property = block.get().stateDefinition.getProperty(name)
				if (property == null || property.getValue(value).isEmpty) missing += "block $old:$meta -> ${target.id}[$name=$value]"
			}
		}
		for ((old, metas) in LegacyIds.items) for ((meta, target) in metas)
			if (!BuiltInRegistries.ITEM.containsKey(target.id)) missing += "item $old:$meta -> ${target.id}"
		for ((old, metas) in LegacyIds.entities) for ((meta, target) in metas)
			if (!BuiltInRegistries.ENTITY_TYPE.containsKey(target.id)) missing += "entity $old:$meta -> ${target.id}"
		helper.assertTrue(missing.isEmpty(), "legacy_ids.json points to things that are not registered: $missing")
		helper.succeed()
	}
	
	@JvmStatic
	@GameTest(template = "empty")
	fun snakeCaseNames(helper: GameTestHelper) {
		// примеры из SPEC, Р-5 и имена автора с цифрами и сокращениями
		val cases = mapOf("DomainDoor" to "domain_door", "altWood1" to "alt_wood1", "ESMItem" to "esm_item", "manaRingGod" to "mana_ring_god", "irisWood0" to "iris_wood0", "RPC" to "rpc")
		for ((name, expected) in cases)
			helper.assertTrue(AlfheimRegisters.snakeCase(name) == expected, "snakeCase($name) = ${AlfheimRegisters.snakeCase(name)}, expected $expected")
		helper.succeed()
	}
}
