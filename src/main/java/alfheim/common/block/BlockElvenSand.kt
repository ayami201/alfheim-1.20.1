package alfheim.common.block

// PORT: импорты 1.7.10 заменены на 1.20.1
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
//import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction as ForgeDirection
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.*

class BlockElvenSand: BlockPatternLexicon(ModInfo.MODID, Material.sand, "ElvenSand", AlfheimTab, harvTool = "shovel", harvLvl = 0, isFalling = true/* PORT: КТ-9 — лексикон, entry = AlfheimLexiconData.worldgen*/) {
	
	// PORT: в 1.20.1 почву спрашивают по состоянию и BlockPos, EnumPlantType → PlantType; материал воды 1.7.10 был
	// только у стоячей и текущей воды — в 1.20.1 это один блок water
	override fun canSustainPlant(state: BlockState, world: IBlockAccess, pos: BlockPos, direction: ForgeDirection, plantable: IPlantable): Boolean { val x = pos.x; val y = pos.y; val z = pos.z; return when (plantable.getPlantType(world, pos)) {
		PlantType.DESERT -> true
		PlantType.BEACH  -> world.getBlock(x - 1, y, z) === Blocks.WATER || world.getBlock(x + 1, y, z) === Blocks.WATER || world.getBlock(x, y, z - 1) === Blocks.WATER || world.getBlock(x, y, z + 1) === Blocks.WATER
		else             -> super.canSustainPlant(state, world, pos, direction, plantable)
	} }
}