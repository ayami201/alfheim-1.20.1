package alexsocol.asjlib.extendables.block

// PORT: импорты 1.7.10 заменены на 1.20.1 (блок 1.7.10 — alfheim.port.legacy.BlockFalling1710, MAPPING.md); вкладка в
// 1.20.1 блоку не нужна (её вещи перечисляет она сама) — параметр любого типа, как у BlockPatternLexicon
import alexsocol.asjlib.*
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState

open class BlockPattern(modid: String, material: Material, name: String, tab: Any?, lightlvl: Float = 0f, lightOpacity: Int = 255, hardness: Float = 1f, harvTool: String = "pickaxe", harvLvl: Int = 1, resistance: Float = 5f, sound: SoundType? = ASJUtilities.soundFromMaterial(material), private val isOpaque: Boolean = true, private val isBeacon: Boolean = false, private val isFalling: Boolean = false): BlockFalling1710(material) {
//open class BlockPattern(modid: String, material: Material, name: String, tab: CreativeTabs?, lightlvl: Float = 0f, lightOpacity: Int = 255, hardness: Float = 1f, harvTool: String = "pickaxe", harvLvl: Int = 1, resistance: Float = 5f, sound: SoundType? = ASJUtilities.soundFromMaterial(material), private val isOpaque: Boolean = true, private val isBeacon: Boolean = false, private val isFalling: Boolean = false): BlockFalling(material) {
	
	init {
		setBlockName(name)
		setBlockTextureName("$modid:$name")
		setCreativeTab(tab)
		setLightLevel(lightlvl)
		setLightOpacity(lightOpacity)
		setHardness(hardness)
		setHarvestLevel(harvTool, harvLvl)
		setResistance(resistance)
		setStepSound(sound)
		
		ASJUtilities.register(this)
	}
	
	override fun isOpaqueCube() = isOpaque
	
	override fun isBeaconBase(world: IBlockAccess?, x: Int, y: Int, z: Int, beaconX: Int, beaconY: Int, beaconZ: Int) = isBeacon
//	override fun isBeaconBase(world: IBlockAccess, x: Int, y: Int, z: Int, beaconX: Int, beaconY: Int, beaconZ: Int) = isBeacon
	
	// PORT: updateTick → tick; падает FallingBlock 1.20.1 — так же, как func_149830_m ниже: если под блоком пусто
	override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, rand: Random) {
		if (!world.isRemote && isFalling) super.tick(state, world, pos, rand)
	}
	
	/*
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random) {
		if (!world.isRemote && isFalling) func_149830_m(world, x, y, z)
	}
	
	fun func_149830_m(world: World, x: Int, ay: Int, z: Int) {
		var y = ay
		if (func_149831_e(world, x, y - 1, z) && y >= 0) {
			val b0: Byte = 32
			
			if (!fallInstantly && world.checkChunksExist(x - b0, y - b0, z - b0, x + b0, y + b0, z + b0)) {
				if (!world.isRemote) {
					val efb = EntityFallingBlock(world, x + 0.5, y + 0.5, z + 0.5, this, world.getBlockMetadata(x, y, z))
					func_149829_a(efb)
					efb.spawn()
				}
			} else {
				world.setBlockToAir(x, y, z)
				while (func_149831_e(world, x, y - 1, z) && y > 0) --y
				if (y > 0) world.setBlock(x, y, z, this)
			}
		}
	}
	*/
}