package alfheim.port.legacy

/**
 * Цвета шерсти овцы 1.7.10 по номеру красителя (`EntitySheep.fleeceColorTable`), из кода игры 1.7.10. В 1.20.1 цвета
 * красителей другие (`DyeColor.getTextureDiffuseColors`): автор красил ими свои вещи, поэтому таблица — старая
 */
object Sheep1710 {
	
	@JvmField
	val fleeceColorTable = arrayOf(
		floatArrayOf(1f, 1f, 1f), floatArrayOf(0.85f, 0.5f, 0.2f), floatArrayOf(0.7f, 0.3f, 0.85f), floatArrayOf(0.4f, 0.6f, 0.85f),
		floatArrayOf(0.9f, 0.9f, 0.2f), floatArrayOf(0.5f, 0.8f, 0.1f), floatArrayOf(0.95f, 0.5f, 0.65f), floatArrayOf(0.3f, 0.3f, 0.3f),
		floatArrayOf(0.6f, 0.6f, 0.6f), floatArrayOf(0.3f, 0.5f, 0.6f), floatArrayOf(0.5f, 0.25f, 0.7f), floatArrayOf(0.2f, 0.3f, 0.7f),
		floatArrayOf(0.4f, 0.3f, 0.2f), floatArrayOf(0.4f, 0.5f, 0.2f), floatArrayOf(0.6f, 0.2f, 0.2f), floatArrayOf(0.1f, 0.1f, 0.1f),
	)
}
