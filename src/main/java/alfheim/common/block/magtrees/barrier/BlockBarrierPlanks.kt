package alfheim.common.block.magtrees.barrier

import alfheim.common.block.base.BlockMod
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import vazkii.botania.api.lexicon.ILexiconable

class BlockBarrierPlanks: BlockMod(Material.wood), ILexiconable {
	
	private val name = "barrierPlanks"
	
	init {
		blockHardness = 2F
		
		stepSound = soundTypeWood
		setBlockName(name)
		
		tickRandomly = true
	}
	
	override fun isInterpolated() = true
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.barrierSapling
}