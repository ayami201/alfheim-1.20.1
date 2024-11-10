package alfheim.common.block.tile

import net.minecraft.tileentity.TileEntity
import vazkii.botania.api.mana.IManaCollisionGhost

class TileManaReflector: TileEntity(), IManaCollisionGhost {
	override fun isGhost() = true // fuck you Vazkii
	override fun canUpdate() = false
}
