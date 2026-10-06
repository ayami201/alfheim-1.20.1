package alfheim.common.block.magtrees.sealing

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockMod
import alfheim.port.legacy.*
import net.minecraft.world.level.Level as World

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockSealingPlanks: BlockMod(Material.wood)/*, ILexiconable*/, ISoundSilencer {
	
	private val name = "sealingPlanks"
	
	init {
		blockHardness = 2F
		setLightLevel(0f)
		setStepSound(soundTypeCloth)
		
		setBlockName(name)
	}
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double) = dist <= 8
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double) = 0.5f
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	// PORT: лут — сам блок (alfheim.port.data.AlfheimBlockLoot)
//	override fun damageDropped(par1: Int) = par1
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.silencer
}
