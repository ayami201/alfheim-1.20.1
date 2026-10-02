package alfheim.common.block.alt

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*
import net.minecraft.core.*
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState

// PORT: вариант metadata — отдельный блок (BlockModMeta). IFuelHandler автор не регистрировал (registerFuelHandler
// нет), а печь 1.7.10 и так жгла деревянный блок 300 тиков, раньше обработчиков: топливо — по материалу (Fuel1710)
class BlockYggDecor(meta: Int): BlockModMeta(Material.wood, 3, ModInfo.MODID, "Wisdomwood", AlfheimTab, 100f, resist = 1000f, folder = "decor/", meta = meta)/*, IFuelHandler*/ {
	
	// PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates): у варианта 1 верх и низ — decor/Wisdomwood1Top
	/*
	lateinit var topIcon: IIcon
	
	override fun registerBlockIcons(reg: IIconRegister) {
		super.registerBlockIcons(reg)
		
		topIcon = reg.registerIcon("$modid:$folder${name}1Top")
	}
	
	override fun getIcon(side: Int, meta: Int) = if (meta == 1 && side in 0..1) topIcon else super.getIcon(side, meta)
	*/
	
	// PORT: сигнатуры 1.20.1 (IForgeBlock)
	override fun getFireSpreadSpeed(state: BlockState, world: BlockGetter, pos: BlockPos, face: Direction) = 0
	override fun isFlammable(state: BlockState, world: BlockGetter, pos: BlockPos, face: Direction) = false
//	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
//	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = false
	
//	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) Int.MAX_VALUE / 13 / 4 else 0
}
