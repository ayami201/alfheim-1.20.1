package alfheim.port.test

import alfheim.api.ModInfo.MODID
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraftforge.fml.ModList
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import org.apache.logging.log4j.LogManager

/**
 * КТ-0: мод загружается на сервере вместе с зависимостями (ROADMAP, КТ-0, «Готово, когда»).
 * Структура `alfheim:empty` — пустой блок воздуха, тестам порта площадка не нужна.
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortSmokeTest {
	
	@JvmStatic
	@GameTest(template = "empty")
	fun dependenciesLoaded(helper: GameTestHelper) {
		for (id in listOf(MODID, "botania", "patchouli", "curios", "kotlinforforge")) {
			val mod = ModList.get().getModContainerById(id)
			helper.assertTrue(mod.isPresent, "Mod $id is not loaded")
			LogManager.getLogger(MODID).info("PortSmokeTest: loaded {} {}", id, mod.get().modInfo.version)
		}
		helper.succeed()
	}
}
