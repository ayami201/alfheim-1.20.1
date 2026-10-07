package alexsocol.asjlib.extendables

// PORT: материал 1.7.10 — alfheim.port.legacy.Material (свойства блока 1.20.1, MAPPING.md): его свойства — свойства
// Kotlin, а не методы
import alfheim.port.legacy.Material
import net.minecraft.world.level.material.MapColor
//import net.minecraft.block.material.*

class MaterialPublic(color: MapColor, val blocksWater: Boolean = true, val blocksLight: Boolean = true, val liquid: Boolean = false, val opaque: Boolean = true, val solid: Boolean = true, val allowBreakInAdventureMode: Boolean = false, val burnable: Boolean = false, val replaceable: Boolean = false, val requiresNoTool: Boolean = true): Material(color) {
	
	override fun blocksMovement() = blocksWater
	
	/* PORT: по мере надобности — у материала порта нет травы под блоком
	override fun getCanBlockGrass() = blocksLight
	*/
	
	override val isLiquid get() = liquid
//	override fun isLiquid() = liquid
	
	override val isOpaque get() = opaque
//	override fun isOpaque() = opaque
	
	override val isSolid get() = solid
//	override fun isSolid() = solid
	
	/* PORT: по мере надобности — у материала порта нет поломки в режиме приключений
	override fun isAdventureModeExempt() = allowBreakInAdventureMode
	*/
	
	override val canBurn get() = burnable
//	override fun getCanBurn() = burnable
	
	override val isReplaceable get() = replaceable
//	override fun isReplaceable() = replaceable
	
	override val requiresTool get() = !requiresNoTool
//	override fun isToolNotRequired() = requiresNoTool
	
	public override fun setNoPushMobility() = super.setNoPushMobility()
	
	public override fun setImmovableMobility() = super.setImmovableMobility()
}
