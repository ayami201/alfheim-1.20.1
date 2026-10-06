package alfheim.common.block.magtrees.nether

// PORT: импорты 1.20.1 (BlockSlab 1.7.10 — Slab1710, MAPPING.md)
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.rainbow.*
import alfheim.common.item.block.*
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock as BlockSlab

// PORT: двойная плита — состояние type=double одинарной (BlockSlabMod)
class BlockNetherWoodSlab(full: Boolean, source: Block = AlfheimBlocks.netherPlanks): BlockRainbowWoodSlab(full, source) {
	
	init {
		setLightLevel(0.5f)
	}
	
	override fun getFullBlock() = AlfheimBlocks.netherSlabFull as BlockSlab
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemSlabMod::class.java, name)
		// PORT: двойная плита 1.7.10 — состояние type=double этой плиты (BlockSlabMod); её старое имя — netherPlanksSlabFull
		LegacyRegistration.alias(name.replaceFirst("Slab", "SlabFull"), this, "type=double")
	}
	
	override fun getSingleBlock() = AlfheimBlocks.netherSlabs as BlockSlab
	
	// PORT: горит ли блок, 1.20.1 решает по таблице огня (FireBlock), её заполняет registerBurnables (AlfheimBlocks):
	// адских блоков в ней нет — они не горят и без этих методов
//	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = false
//
//	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	
	// PORT: field_150004_a (двойная ли плита) — full. Печь 1.7.10 сжигала деревянный блок 300 тиков раньше обработчиков
	// модов, поэтому 1000 и 2000 не срабатывали (Fuel1710; BUGS.md, B-027)
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) if (full) 2000 else 1000 else 0
}

class BlockNetherWoodStairs(source: Block = AlfheimBlocks.netherPlanks): BlockRainbowWoodStairs(source), IFuelHandler {
	
	init {
		setLightLevel(0.5f)
		
		GameRegistry.registerFuelHandler(this)
	}
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	// PORT: горит ли блок, 1.20.1 решает по таблице огня (FireBlock), её заполняет registerBurnables (AlfheimBlocks):
	// адских блоков в ней нет — они не горят и без этих методов
//	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = false
//
//	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	
	// PORT: печь 1.7.10 сжигала деревянный блок 300 тиков раньше обработчиков модов, поэтому 2000 не срабатывало
	// (Fuel1710; BUGS.md, B-027)
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 2000 else 0
}
