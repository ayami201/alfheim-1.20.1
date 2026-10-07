package alfheim.port.legacy.botania

import alfheim.port.legacy.ForgeDirection

/** `vazkii.botania.common.lib.LibMisc` 1.7.10 — то, что из него берёт автор: в Botania 1.20.1 остались только имя и id мода */
object LibMisc {

	/** Стороны света в порядке Botania 1.7.10: север, юг, восток, запад */
	@JvmField
	val CARDINAL_DIRECTIONS = arrayOf(ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST)
}
