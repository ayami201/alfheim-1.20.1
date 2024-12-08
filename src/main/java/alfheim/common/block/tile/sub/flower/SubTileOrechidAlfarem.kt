package alfheim.common.block.tile.sub.flower

import alfheim.api.AlfheimAPI
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.util.IIcon
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.subtile.functional.SubTileOrechid

class SubTileOrechidAlfarem: SubTileOrechid() {
	
	private val COST = 25000
	
	override fun canOperate(): Boolean {
		return supertile.worldObj.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim
	}
	
	override fun getOreMap() = AlfheimAPI.oreWeightsAlfheim
	
	override fun getSourceBlock() = ModBlocks.livingrock
	
	override fun getCost() = COST
	
	override fun getColor() = 0x08F500
	
	override fun getEntry() = AlfheimLexiconData.flowerAlfchid
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("orechidAlfarem").getIconForStack(null)
}