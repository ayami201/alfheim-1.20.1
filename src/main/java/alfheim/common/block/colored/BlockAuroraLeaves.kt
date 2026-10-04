package alfheim.common.block.colored

// PORT: импорты 1.20.1
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.item.block.ItemBlockAurora
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraftforge.api.distmarker.*

// PORT: бит опадания — свойство persistent (Leaves1710). КТ-9 — лексикон (ILexiconable)
class BlockAuroraLeaves: BlockLeavesMod()/*, ILexiconable*/ {
	
	init {
		setBlockName("auroraLeaves")
	}
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockAurora::class.java, name)
	}
	
	override fun quantityDropped(random: Random) = if (random.nextInt(20) == 0) 1 else 0
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int) = BlockAuroraDirt.getBlockColor(x, y, z)
	
	// PORT: имена видов листвы BlockLeaves 1.7.10 (func_150125_e) нужны были его иконкам; в 1.20.1 иконки — модели
//	override fun func_150125_e() = arrayOf("auroraLeaves")
	
	override fun decayBit(): Int = 0x8
	
	override fun getItemDropped(p_149650_1_: Int, p_149650_2_: Random?, p_149650_3_: Int) = AlfheimBlocks.irisSapling.toItem()
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.aurora
}