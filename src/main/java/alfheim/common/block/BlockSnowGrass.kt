package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.common.block.base.BlockMod
import alfheim.common.core.asm.superwrapper.SuperWrapperHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.util.AlfheimTab
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.world.*
import net.minecraftforge.common.*
import net.minecraftforge.common.util.ForgeDirection
import ru.vamig.worldengine.WE_PerlinNoise
import vazkii.botania.api.item.IHornHarvestable
import vazkii.botania.common.lib.LibMisc
import java.util.*

class BlockSnowGrass: BlockMod(Material.grass), IGrowable, IHornHarvestable {
	
	init {
		setBlockName("SnowGrass")
		setCreativeTab(AlfheimTab)
		setHardness(0.6f)
		setHarvestLevel("shovel", 0)
		setStepSound(soundTypeGrass)
		
		tickRandomly = true
	}
	
	override fun getIcon(side: Int, meta: Int) = when (side) {
		0       -> Blocks.dirt.getIcon(0, 0)
		1       -> Blocks.snow.getIcon(1, 0)
		else    -> Blocks.grass.field_149993_M
	}!!
	
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, isRemote: Boolean) = true
	override fun func_149852_a(world: World?, random: Random?, x: Int, y: Int, z: Int) = true
	override fun func_149853_b(world: World?, random: Random?, x: Int, y: Int, z: Int) = Unit
	override fun getItemDropped(meta: Int, random: Random?, fortune: Int) = Blocks.dirt.toItem()
	
	override fun canSustainPlant(world: IBlockAccess, x: Int, y: Int, z: Int, direction: ForgeDirection, plantable: IPlantable): Boolean {
		if (plantable is BlockBush && SuperWrapperHandler.canPlaceBlockOn(plantable, Blocks.grass)) {
			return true
		}
		
		return when (plantable.getPlantType(world, x, y + 1, z)) {
			EnumPlantType.Plains -> true
			EnumPlantType.Beach  -> LibMisc.CARDINAL_DIRECTIONS.any { d -> world.getBlock(x + d.offsetX, y, z + d.offsetZ).material === Material.water }
			else                 -> false
		}
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		val above = world.getBlock(x, y + 1, z)
		val meta = world.getBlockMetadata(x, y, z)
		val metaAbove = world.getBlockMetadata(x, y + 1, z)
		
		if (AlfheimCore.winter) {
			if (above === Blocks.snow_layer)
				world.setBlock(x, y + 1, z, AlfheimBlocks.snowLayer, metaAbove, 3)
			
			// from BlockGrass:
			if (world.getBlockLightValue(x, y + 1, z) < 4 && world.getBlockLightOpacity(x, y + 1, z) > 2) {
				world.setBlock(x, y, z, Blocks.dirt)
				return
			}
			
			if (!world.isRaining || world.getPrecipitationHeight(x, z) < y)
				return
			
			if (above === Blocks.air) {
				world.setBlock(x, y + 1, z, AlfheimBlocks.snowLayer)
			} else if (above === AlfheimBlocks.snowLayer) {
				val upMeta = WE_PerlinNoise.PerlinNoise2D(world.seed, x.D, z.D, 1.0, 1).times(15).I.and(7).div(2)
				
				if (metaAbove < upMeta) world.setBlockMetadataWithNotify(x, y + 1, z, metaAbove + 1, 1 or 2)
			}
			
			repeat(4) {
				val i = x + random.nextInt(3) - 1
				val j = y + random.nextInt(4) - 3
				val k = z + random.nextInt(3) - 1
				val block = world.getBlock(i, j, k)
				
				if ((block === Blocks.dirt || block === Blocks.grass) && world.getBlockMetadata(i, j, k) == 0 && world.getPrecipitationHeight(i, k) <= k) world.setBlock(i, j, k, this)
			}
		} else {
			if (meta == 1 || world.rand.nextInt(meltDelay) != 0) return
			
			if (above === AlfheimBlocks.snowLayer || above === Blocks.snow_layer)
				world.setBlockToAir(x, y + 1, z)
			
			world.setBlock(x, y, z, Blocks.grass)
		}
	}
	
	override fun canHornHarvest(world: World, x: Int, y: Int, z: Int, stack: ItemStack?, hornType: IHornHarvestable.EnumHornType) =
		hornType == IHornHarvestable.EnumHornType.COVERING && world.getBlockMetadata(x, y, z) != 1
	
	override fun hasSpecialHornHarvest(world: World, x: Int, y: Int, z: Int, stack: ItemStack?, hornType: IHornHarvestable.EnumHornType) =
		canHornHarvest(world, x, y, z, stack, hornType)
	
	override fun harvestByHorn(world: World, x: Int, y: Int, z: Int, stack: ItemStack?, hornType: IHornHarvestable.EnumHornType) {
		if (!canHornHarvest(world, x, y, z, stack, hornType)) return
		
		world.setBlock(x, y, z, Blocks.grass)
	}
	
	companion object {
		val meltDelay get() = if (RagnarokHandler.summer) 1 else 20
	}
}