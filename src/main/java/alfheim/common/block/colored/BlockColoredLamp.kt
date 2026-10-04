package alfheim.common.block.colored

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockMod
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.*
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.api.distmarker.*
import java.awt.Color

/**
 * @author WireSegal
 * Created at 4:44 PM on 1/12/16.
 */
// PORT: metadata лампы — сила сигнала красного камня: свойство POWER. КТ-9 — лексикон (ILexiconable)
class BlockColoredLamp: BlockMod(Material.redstoneLight)/*, ILexiconable*/ {
	
	// PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates): при силе 15 — переливающаяся текстура RB (.mcmeta)
//	var rainbowIcon: IIcon? = null
	
	init {
		setBlockName("irisLamp")
		setStepSound(soundTypeGlass)
		setHardness(0.3f)
		// PORT: анимированные текстуры 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
		
		// PORT: свечение по координатам (getLightValue ниже) 1.20.1 берёт у состояний: пишется в них с создания блока
		for (state in stateDefinition.possibleStates) state.lightEmission = getLightValue(state)
	}
	
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(POWER)
	}
	
	/* PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates); анимация — .mcmeta
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	override fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0)
			rainbowIcon = InterpolatedIconHelper.forBlock(event.map, this, "RB")
	}
	
	override fun getIcon(side: Int, meta: Int) = if (meta > 14) rainbowIcon else blockIcon
	*/
	
	// PORT: onNeighborBlockChange → neighborChanged, onBlockAdded → onPlace; metadata — свойство POWER
	@Deprecated("Deprecated in Java")
	override fun neighborChanged(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, isMoving: Boolean) {
		super.neighborChanged(state, world, pos, block, fromPos, isMoving)
		val lvl = world.getStrongestIndirectPower(pos.x, pos.y, pos.z)
		if (state.getValue(POWER) != lvl)
			world.setBlock(pos, state.setValue(POWER, lvl), 3)
	}
//	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) {
//		super.onNeighborBlockChange(world, x, y, z, block)
//		val lvl = world.getStrongestIndirectPower(x, y, z)
//		if (world.getBlockMetadata(x, y, z) != lvl)
//			world.setBlockMetadataWithNotify(x, y, z, lvl, 3)
//	}
	
	@Deprecated("Deprecated in Java")
	override fun onPlace(state: BlockState, world: World, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
		super.onPlace(state, world, pos, oldState, isMoving)
		val lvl = world.getStrongestIndirectPower(pos.x, pos.y, pos.z)
		if (state.getValue(POWER) != lvl)
			world.setBlock(pos, state.setValue(POWER, lvl), 3)
	}
//	override fun onBlockAdded(world: World, x: Int, y: Int, z: Int) {
//		super.onBlockAdded(world, x, y, z)
//		val lvl = world.getStrongestIndirectPower(x, y, z)
//		if (world.getBlockMetadata(x, y, z) != lvl)
//			world.setBlockMetadataWithNotify(x, y, z, lvl, 3)
//	}
	
	/* PORT: лут и выбор колёсиком — сама лампа без сигнала (предмет блока; таблица лута — alfheim.port.data.AlfheimBlockLoot)
	override fun damageDropped(meta: Int) = 0
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer) = ItemStack(this, 1, 0)
	
	override fun createStackedBlock(meta: Int) = ItemStack(this)
	
	override fun getLightValue() = 0
	*/
	
	// PORT: свечение по координатам — по состоянию (init выше). Цветной свет Easy Colored Lights: мода нет на 1.20.1,
	// ветка не выполняется — как у автора без мода (TASKS.md, «Вопросы к владельцу»)
	fun getLightValue(state: BlockState): Int {
		val lvl = state.getValue(POWER)
//	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int): Int {
//		val lvl = world.getBlockMetadata(x, y, z)
//		if (Loader.isModLoaded("easycoloredlights")) {
//			return if (lvl == 15) 0xFFFFFF else powerColor(lvl)
//		}
		return if (lvl > 0) 15 else 0
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int): Int = powerColor(meta)
	
	// PORT: metadata — свойство POWER состояния в точке
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int): Int = getRenderColor(world.getBlockState(BlockPos(x, y, z)).getValue(POWER))
//	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int): Int = getRenderColor(world.getBlockMetadata(x, y, z))
	
	fun powerColor(power: Int): Int {
		if (power == 0) return 0x161616
		else if (power == 15) return 0xFFFFFF
		return Color.HSBtoRGB((power - 1) / 16F, 1F, 1F)
	}
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.lamp
	
	companion object {
		
		/** Metadata лампы 1.7.10 — сила сигнала красного камня, 0–15 */
		val POWER = BlockStateProperties.POWER
	}
}
