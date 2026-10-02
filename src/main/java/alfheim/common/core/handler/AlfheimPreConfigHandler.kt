package alfheim.common.core.handler

import alexsocol.asjlib.extendables.ASJPreConfigHandler
import net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL

object AlfheimPreConfigHandler: ASJPreConfigHandler() {
	
	var elementiumClusterMeta = 22
	var enableElvenStory = false
	var enableMMO = false
	var gaiaBarOffset = 1
	var gaiaNameColor = 0x00D5FF
	var hpHooks = true
	var overrideCoFHCollisionCheck = true
	
	override fun readProperties() {
		elementiumClusterMeta = loadProp(CATEGORY_GENERAL, "elementiumClusterMeta", elementiumClusterMeta, true, "Effective only if Thaumcraft is installed. Change this if some other mod adds own clusters (max value is 63); also please, edit and spread modified .lang files")
		enableElvenStory = loadProp(CATEGORY_GENERAL, "enableElvenStory", enableElvenStory, true, "Set this to false to disable ESM and MMO")
		enableMMO = enableElvenStory && loadProp(CATEGORY_GENERAL, "enableMMO", enableMMO, true, "Set this to false to disable MMO")
		gaiaBarOffset = loadProp(CATEGORY_GENERAL, "gaiaBarOffset", gaiaBarOffset, true, "Gaia hp and bg boss bar variant (from default texture pairs)")
		gaiaNameColor = loadProp(CATEGORY_GENERAL, "gaiaNameColor", gaiaNameColor, false, "Gaia name color on boss bar")
		hpHooks = loadProp(CATEGORY_GENERAL, "hpHooks", hpHooks, true, "Toggles hooks to vanilla health system. Set this to false if you have any issues with other systems")
		overrideCoFHCollisionCheck = loadProp(CATEGORY_GENERAL, "overrideCoFHCollisionCheck", overrideCoFHCollisionCheck, false, "Set this to false to disable override of CoFHCore hook to entity collisions. This will make small entities to fall through floating islands")
	}
}