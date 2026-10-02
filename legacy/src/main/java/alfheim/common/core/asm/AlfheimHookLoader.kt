package alfheim.common.core.asm

import alexsocol.asjlib.asm.ASJASM
import alexsocol.patcher.asm.ASJHookLoader
import alexsocol.patcher.asm.worker.InterfaceAppenderWorker.registerAdditionalInterface
import alfheim.api.ModInfo
import alfheim.common.core.asm.transformer.AlfheimClassTransformer
import alfheim.common.core.handler.AlfheimPreConfigHandler
import com.KAIIIAK.classManipulators.HookReplacerWorker.registerGroupRegistry
import com.KAIIIAK.classManipulators.HookReplacerWorker.registerHookReplacerContainer
import com.KAIIIAK.superwrapper.SuperWrapperTransformer
import cpw.mods.fml.relauncher.FMLRelaunchLog
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*
import gloomyfolken.hooklib.minecraft.HookLoader
import java.io.File

// -Dfml.coreMods.load=alfheim.common.core.asm.AlfheimHookLoader
// -username=AlexSocol
@MCVersion(value = "1.7.10")
@TransformerExclusions(
	"alfheim.common.core.asm.hook",
	"alfheim.common.core.asm.transformer",
)
class AlfheimHookLoader: HookLoader() {
	
	init {
		AlfheimPreConfigHandler.loadPreConfig(File("config/Alfheim/core.cfg"))
		
		System.setProperty("forge.forceDisplayStencil", "true")
	}
	
	override fun getASMTransformerClass() = arrayOf(AlfheimClassTransformer::class.java.name)
	
	override fun registerHooks() {
		FMLRelaunchLog.info("[${ModInfo.MODID.uppercase()}] Loaded coremod. Registering hooks...")
		
		registerHookContainer("alfheim.common.core.asm.hook.AlfheimHookHandler")
		if (AlfheimPreConfigHandler.hpHooks) registerHookContainer("alfheim.common.core.asm.hook.AlfheimHPHooks")
		registerHookContainer("alfheim.common.core.asm.hook.Botania18AndUpBackport")
		registerHookContainer("alfheim.common.core.asm.hook.ElementalDamageAdapter")
		
		registerHookContainer("alfheim.common.core.asm.hook.extender.FlowerBagExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.FurnaceExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.ItemAuraRingExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.ItemLensExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.ItemTwigWandExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.LensPaintExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.LightRelayExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.ManaSpreaderExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.PureDaisyExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.QuartzExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.RelicHooks")
		registerHookContainer("alfheim.common.core.asm.hook.extender.SparkExtender")
		
		registerHookContainer("alfheim.common.core.asm.hook.fixes.BotaniaGlowingRenderFixes")
		registerHookContainer("alfheim.common.core.asm.hook.fixes.CorporeaInputFix")
		registerHookContainer("alfheim.common.core.asm.hook.fixes.FlightTiaraFix")
		registerHookContainer("alfheim.common.core.asm.hook.fixes.GodAttributesHooks")
		registerHookContainer("alfheim.common.core.asm.hook.fixes.RecipeAncientWillsFix")
		
		registerHookContainer("alfheim.common.core.asm.hook.integration.BotaniaVisDiscountHooks")
		registerHookContainer("alfheim.common.core.asm.hook.integration.RedstoneRodHookHandler")
		registerHookContainer("alfheim.common.core.asm.hook.integration.TGHandlerBotaniaAdapterHooks")
		registerHookContainer("alfheim.common.core.asm.hook.integration.TraitFairySpawner")
		
		if (ASJHookLoader.OBF) ASJASM.registerFieldHookContainer("alfheim.common.core.asm.hook.AlfheimFieldHookHandler")
		
		SuperWrapperTransformer.registerSuperWrapperContainer("alfheim.common.core.asm.superwrapper.SuperWrapperHandler")
		
		registerGroupRegistry("alfheim.common.core.asm.hook.replacer.HookReplacerHandler")
		registerGroupRegistry("alfheim.common.core.asm.hook.replacer.HookReplacerHandlerKt")
		
		registerHookReplacerContainer("alfheim.common.compat.TransformableHookReplacerHandlerKt")
		registerHookReplacerContainer("alfheim.common.core.asm.hook.extender.ManaSpreaderExtender")
		registerHookReplacerContainer("alfheim.common.core.asm.hook.replacer.HookReplacerHandler")
		registerHookReplacerContainer("alfheim.common.core.asm.hook.replacer.HookReplacerHandlerKt")
		
		registerAdditionalInterfaces()
	}
	
	fun registerAdditionalInterfaces() {
		registerAdditionalInterface("net/minecraft/entity/monster/EntityCreeper", "alfheim/common/core/helper/IElementalEntity")
		registerAdditionalInterface("net/minecraft/entity/monster/EntitySkeleton", "alfheim/api/entity/IMuspelheimEntity")
		registerAdditionalInterface("net/minecraft/entity/monster/EntitySkeleton", "alfheim/api/entity/INiflheimEntity")
		registerAdditionalInterface("thaumcraft/common/entities/golems/EntityGolemBase", "alfheim/common/core/helper/IElementalEntity")
		registerAdditionalInterface("thaumcraft/common/entities/monster/EntityWisp", "alfheim/common/core/helper/IElementalEntity")
		registerAdditionalInterface("vazkii/botania/common/item/equipment/bauble/ItemAuraRing", "vazkii/botania/api/mana/IManaItem")
		registerAdditionalInterface("vazkii/botania/common/item/relic/ItemAesirRing", "alfheim/api/item/IStepupItem")
		registerAdditionalInterface("vazkii/botania/common/entity/EntityDoppleganger", "net/minecraft/entity/monster/IMob")
	}
}