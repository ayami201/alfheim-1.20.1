package alfheim.common.block.colored

// PORT: импорты 1.20.1; цвета шерсти 1.7.10 — Sheep1710
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockStairsMod
import alfheim.port.legacy.*
import alfheim.port.legacy.Sheep1710 as EntitySheep
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.block.Block
import net.minecraftforge.api.distmarker.*
import java.awt.Color

// PORT: доски-источник — блок варианта (SPEC, Р-5): irisPlanks[meta] вместо irisPlanks с metadata. КТ-9 — лексикон
// (ILexiconable)
class BlockColoredWoodStairs(meta: Int, source: Block = AlfheimBlocks.irisPlanks[meta]):
	BlockStairsMod(source, meta, source.unlocalizedName.replace("tile.".toRegex(), "") + "Stairs" + meta)/*, ILexiconable*/ {
	
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(m: Int): Int {
		if (meta >= EntitySheep.fleeceColorTable.size)
			return 0xFFFFFF
		
		val color = EntitySheep.fleeceColorTable[meta]
		return Color(color[0], color[1], color[2]).rgb
	}
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess?, x: Int, y: Int, z: Int) = getRenderColor(meta)
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.irisSapling
}
