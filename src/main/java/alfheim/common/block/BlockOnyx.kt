package alfheim.common.block

import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.api.lib.LibRenderIDs
import alfheim.common.core.util.AlfheimTab
import net.minecraft.block.material.Material

class BlockOnyx: BlockModMeta(Material.rock, 1, ModInfo.MODID, "Onyx", AlfheimTab, -1f, "", Int.MAX_VALUE, Float.MAX_VALUE) {
	override fun getRenderType() = LibRenderIDs.idOnyx
}
