package alfheim.client.core.util

import alfheim.api.lib.LibResourceLocations
import vazkii.botania.client.render.tile.RenderTilePylon

object AlfheimBotaniaModifiersClient {
	
	fun postInit() {
		RenderTilePylon.texturePink = LibResourceLocations.gaiaPylon
		RenderTilePylon.texturePinkOld = LibResourceLocations.gaiaPylonOld
	}
}