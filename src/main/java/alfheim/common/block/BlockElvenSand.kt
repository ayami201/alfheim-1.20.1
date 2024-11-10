package alfheim.common.block

import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.Material
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.*
import net.minecraftforge.common.util.ForgeDirection

class BlockElvenSand: BlockPatternLexicon(ModInfo.MODID, Material.sand, "ElvenSand", AlfheimTab, harvTool = "shovel", harvLvl = 0, isFalling = true, entry = AlfheimLexiconData.worldgen) {
	
	override fun canSustainPlant(world: IBlockAccess, x: Int, y: Int, z: Int, direction: ForgeDirection?, plantable: IPlantable) = when (plantable.getPlantType(world, x, y, z)) {
		EnumPlantType.Desert -> true
		EnumPlantType.Beach  -> world.getBlock(x - 1, y, z).material === Material.water || world.getBlock(x + 1, y, z).material === Material.water || world.getBlock(x, y, z - 1).material === Material.water || world.getBlock(x, y, z + 1).material === Material.water
		else                 -> super.canSustainPlant(world, x, y, z, direction, plantable)
	}
}