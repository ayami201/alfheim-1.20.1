package alfheim.common.block

// PORT: импорты 1.7.10 заменены на 1.20.1 (блок 1.7.10 — alfheim.port.legacy.BlockFalling1710, MAPPING.md)
import alexsocol.asjlib.*
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState

// PORT: КТ-9 — лексикон (ILexiconable, LexiconEntry): запись лексикона пока любого типа. Вкладка в 1.20.1 не нужна блоку
open class BlockPatternLexicon(modid: String, material: Material, name: String, tab: Any? = null, lightlvl: Float = 0f, lightOpacity: Int = 255, hardness: Float = 1f, harvTool: String = "pickaxe", harvLvl: Int = 1, resistance: Float = 5f, sound: SoundType? = ASJUtilities.soundFromMaterial(material), private val isOpaque: Boolean = true, private val isBeacon: Boolean = false, private val isFalling: Boolean = false, private val entry: Any? = null): BlockFalling1710(material)/*, ILexiconable*/ {
	
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
	}
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
		return super.setBlockName(name)
	}
	
	override fun isOpaqueCube() = isOpaque
	
	override fun isBeaconBase(world: IBlockAccess?, x: Int, y: Int, z: Int, beaconX: Int, beaconY: Int, beaconZ: Int) = isBeacon
	
	// PORT: updateTick → tick; падает FallingBlock 1.20.1 — так же, как fall() ниже: если под блоком пусто
	override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, rand: RandomSource) {
		if (!world.isRemote && isFalling) super.tick(state, world, pos, rand)
	}
	
	/*
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random?) {
		if (!world.isRemote && isFalling) fall(world, x, y, z)
	}
	
	private fun fall(world: World, x: Int, y: Int, z: Int) {
		var y = y
		if (func_149831_e(world, x, y - 1, z) && y >= 0) {
			val b0: Byte = 32
			
			if (!fallInstantly && world.checkChunksExist(x - b0, y - b0, z - b0, x + b0, y + b0, z + b0)) {
				if (!world.isRemote) {
					val efb = EntityFallingBlock(world, (x.F + 0.5f).D, (y.F + 0.5f).D, (z.F + 0.5f).D, this, world.getBlockMetadata(x, y, z))
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
	
	/* PORT: КТ-9 — лексикон
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = entry
	*/
}