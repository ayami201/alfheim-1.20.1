package alfheim.common.integration

import Reika.ChromatiCraft.Registry.*
import alfheim.*
import alfheim.common.core.handler.*
import appeng.spatial.StorageWorldProvider
import com.gildedgames.the_aether.*
import com.rwtema.extrautils.*
import com.teammetallurgy.atum.handler.*
import cpw.mods.fml.common.*
import lumien.randomthings.Configuration.*
import net.gtn.dimensionalpocket.common.lib.*
import net.minecraft.server.MinecraftServer
import org.dave.CompactMachines.handler.*
import org.dimdev.dimdoors.config.*
import twilightforest.*
import vazkii.botania.common.*
import com.yurtmod.main.Config as YurtConfig
import ec3.utils.cfg.Config as EC3Config
import erebus.core.handler.configs.ConfigHandler as ErebusConfig
import thaumcraft.common.config.Config as ThaumcraftConfig
import thebetweenlands.utils.confighandler.ConfigHandler as BetweenlandsConfig
import com.emoniph.witchery.util.Config as WitcheryConfig

object ModdedDimensionsIntegration {
	
	private val aetherExitID = if (Loader.isModLoaded("aether_legacy")) AetherConfig.getTravelDimensionID() else null
	val aetherID = if (Loader.isModLoaded("aether_legacy")) AetherConfig.getAetherDimensionID() else null
	val atumID = if (Loader.isModLoaded("atum")) AtumConfig.DIMENSION_ID else null
	val betweenlandsID = if (Loader.isModLoaded("thebetweenlands")) BetweenlandsConfig.DIMENSION_ID else null
	val chromaID = if (Loader.isModLoaded("ChromatiCraft")) ExtraChromaIDs.DIMID.value else null
	val compactMachinesID = if (Loader.isModLoaded("CompactMachines")) ConfigurationHandler.dimensionId else null
	val dimensionalPocketsID = if (Loader.isModLoaded("dimensionalPockets")) Reference.DIMENSION_ID else null
	val deepDarkID = if (Loader.isModLoaded("ExtraUtilities")) ExtraUtils.underdarkDimID else null
	val erebusID = if (Loader.isModLoaded("erebus")) ErebusConfig.INSTANCE.erebusDimensionID else null
	val hoannaID = if (Loader.isModLoaded("EssentialCraftIII") || Loader.isModLoaded("essentialcraft")) EC3Config.dimensionID else null
	val limboID = if (Loader.isModLoaded("dimdoors")) DDProperties.instance().LimboDimensionID else null
	val mirrorID = if (Loader.isModLoaded("witchery")) WitcheryConfig.instance().dimensionMirrorID else null
	val outerLandsID = if (Botania.thaumcraftLoaded) ThaumcraftConfig.dimensionOuterId else null
	val spectreDimensonID = if (Loader.isModLoaded("RandomThings")) Settings.SPECTRE_DIMENSON_ID else null
	val twilightForestID = if (AlfheimCore.TwilightForestLoaded) TwilightForestMod.dimensionID else null
	val yurtsID = if (Loader.isModLoaded("yurtmod")) YurtConfig.DIMENSION_ID else null
	
	fun canLeaveAlfheimTo(dimTo: Int): Boolean {
		// should not cause problems ???
		if (dimTo == aetherID && aetherExitID == AlfheimConfigHandler.dimensionIDAlfheim) return true 
		
		// pocket dimensions
		if (MinecraftServer.getServer()?.worldServerForDimension(dimTo)?.provider is StorageWorldProvider) return true
		when (dimTo) {
			compactMachinesID,
			dimensionalPocketsID,
			mirrorID,
			spectreDimensonID,
			yurtsID,
				-> return true
		}
		
		return false
	}
}