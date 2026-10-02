package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.api.lib.*
import alfheim.client.core.helper.*
import alfheim.common.block.*
import alfheim.common.block.colored.*
import alfheim.common.core.handler.ragnarok.*
import alfheim.common.item.relic.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.*
import net.minecraft.block.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.creativetab.*
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.inventory.*
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.*
import vazkii.botania.api.recipe.*
import vazkii.botania.common.*
import vazkii.botania.common.block.*
import vazkii.botania.common.block.decor.*
import vazkii.botania.common.item.*
import java.awt.*
import java.util.*

class ItemColorSeeds: ItemIridescent("irisSeeds"), IFlowerComponent, IFloatingFlowerVariant {
	
	lateinit var snowIcon: IIcon
	
	override fun getIslandType(stack: ItemStack) = if (stack.meta == SNOW) IFloatingFlower.IslandType.SNOW!! else irisIslandTypes.safeGet(stack.meta)
	
	override fun canFit(stack: ItemStack, inventory: IInventory) = stack.meta == RAINBOW
	
	override fun getParticleColor(stack: ItemStack) = rainbowColor()
	
	override fun getSubItems(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in 0..<TYPES)
			list.add(ItemStack(item, 1, i))
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer?, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val block = world.getBlock(x, y, z)
		val bmeta = world.getBlockMetadata(x, y, z)
		val meta = stack.meta
		
		if (!(block === Blocks.dirt || block === Blocks.grass) || bmeta != 0) return false
		
		addBlockSwapper(world, player, x, y, z, meta)
		
		val color = Color(if (meta == AURORA) BlockAuroraDirt.getBlockColor(x, y, z) else colorFromMeta(meta))
		val r = color.red / 255F
		val g = color.green / 255F
		val b = color.blue / 255F
		
		val velMul = 0.025f
		repeat(50) {
			val px = (Math.random() - 0.5) * 3
			val py = Math.random() - 0.5 + 1
			val pz = (Math.random() - 0.5) * 3
			Botania.proxy.wispFX(world, x + 0.5 + px, y + 0.5 + py, z + 0.5 + pz, r, g, b, Math.random().F * 0.15f + 0.15f, (-px).F * velMul, (-py).F * velMul, (-pz).F * velMul)
		}
		stack.stackSize--
		
		return true
	}
	
	override fun registerIcons(reg: IIconRegister) {
		super.registerIcons(reg)
		snowIcon = IconHelper.forItem(reg, this, "Snow")
	}
	
	override fun getColorFromItemStack(stack: ItemStack, pass: Int) =
		if (stack.meta == SNOW) 0xFFFFFF else super.getColorFromItemStack(stack, pass)
	
	override fun getIconFromDamageForRenderPass(meta: Int, pass: Int) =
		if (pass == 0 && meta == SNOW) snowIcon else super.getIconFromDamageForRenderPass(meta, pass)
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, tooltip: MutableList<Any?>, adv: Boolean) {
		if (stack.meta != SNOW) super.addInformation(stack, player, tooltip, adv)
	}
	
	override fun getUnlocalizedName(stack: ItemStack): String {
		var result = super.getUnlocalizedName(stack)
		if (stack.meta == SNOW) result += "Snowy"
		return result
	}
	
	companion object {
		
		const val TYPES = ItemIridescent.TYPES + 1
		const val SNOW = ItemIridescent.TYPES
		const val OVERGROWTH_HACK = 1000
		
		private val blockSwappers = HashMap<Int, MutableList<BlockSwapper>>()
		
		val islandOvergrowth = IFloatingFlower.IslandType("OVERGROWTH", LibResourceLocations.miniIslandOvergrowth)
		val irisIslandTypes = Array(ItemIridescent.TYPES) { IridescentIslandType("IRIDESCENT$it", LibResourceLocations.miniIsland, it) }
		
		var worldGen = false
		
		init {
			eventFML()
		}
		
		fun addBlockSwapper(world: World, player: EntityPlayer?, x: Int, y: Int, z: Int, meta: Int) {
			val swapper = BlockSwapper(world, player, ChunkCoordinates(x, y, z), meta, worldGen)
			world.setBlock(x, y, z, swapper.blockToSet, swapper.metaToSet, 3)
			blockSwappers.computeIfAbsent(world.provider.dimensionId) { ArrayList() }.add(swapper)
			
			if (meta == OVERGROWTH_HACK || meta == SNOW) return
			
			val aBlock = world.getBlock(x, y + 1, z)
			val aMeta = world.getBlockMetadata(x, y + 1, z)
			
			if (aBlock == Blocks.tallgrass && aMeta == 1) {
				if (meta >= 16)
					world.setBlock(x, y + 1, z, AlfheimBlocks.rainbowGrass, meta - 16, 4)
				else
					world.setBlock(x, y + 1, z, AlfheimBlocks.irisGrass, swapper.metaToSet, 4)
			} else if (aBlock == Blocks.double_plant && aMeta == 2) {
				if (meta >= 16) {
					world.setBlock(x, y + 1, z, AlfheimBlocks.rainbowTallGrass, meta - 16, 2)
					world.setBlock(x, y + 2, z, AlfheimBlocks.rainbowTallGrass, meta - 8, 2)
				} else if (swapper.metaToSet < 8) {
					world.setBlock(x, y + 1, z, AlfheimBlocks.irisTallGrass0, swapper.metaToSet, 2)
					world.setBlock(x, y + 2, z, AlfheimBlocks.irisTallGrass0, 8, 2)
				} else {
					world.setBlock(x, y + 1, z, AlfheimBlocks.irisTallGrass1, swapper.metaToSet - 8, 2)
					world.setBlock(x, y + 2, z, AlfheimBlocks.irisTallGrass1, 8, 2)
				}
			}
		}
		
		@SubscribeEvent
		fun onTickEnd(event: TickEvent.WorldTickEvent) {
			if (event.phase != TickEvent.Phase.END) return
			blockSwappers[event.world.provider.dimensionId]?.removeAll { !it.tick() }
		}
		
		class IridescentIslandType(name: String, rs: ResourceLocation, val colorIndex: Int): IFloatingFlower.IslandType(name, rs) {
			
			override fun getColor(): Int {
				if (colorIndex == RAINBOW)
					return Color(rainbowColor()).darker().rgb
				
				if (colorIndex >= EntitySheep.fleeceColorTable.size)
					return Color.WHITE.darker().rgb
				
				val color = EntitySheep.fleeceColorTable[colorIndex]
				return Color(color[0], color[1], color[2]).darker().rgb
			}
		}
		
		private class BlockSwapper(var world: World, player: EntityPlayer?, coords: ChunkCoordinates, meta: Int, worldGen: Boolean) {
			
			var rand: Random
			var blockToSet: Block
			var metaToSet: Int
			
			var grassBlock: Block
			var tallGrassMeta: Int
			var tallGrassBlock: Block
			
			var startCoords: ChunkCoordinates
			var ticksExisted = 0
			
			val range: Int
			val TICK_RANGE = 1
			
			init {
				val seed = coords.posX xor coords.posY xor coords.posZ
				rand = Random(seed.toLong())
				blockToSet = if (meta == OVERGROWTH_HACK) ModBlocks.enchantedSoil else if (meta == SNOW) AlfheimBlocks.snowGrass else dirtFromMeta(meta)
				metaToSet = if (meta == OVERGROWTH_HACK) 0 else if (meta == SNOW) 1 else meta % 16
				
				val useRainbowGrassBlock = meta == RAINBOW || meta == AURORA
				
				grassBlock = if (useRainbowGrassBlock) AlfheimBlocks.rainbowGrass else AlfheimBlocks.irisGrass
				tallGrassMeta = metaToSet % 8
				tallGrassBlock = if (useRainbowGrassBlock) AlfheimBlocks.rainbowTallGrass else (if (meta > 8) AlfheimBlocks.irisTallGrass1 else AlfheimBlocks.irisTallGrass0)
				
				startCoords = coords
				
				range = if (worldGen && meta != OVERGROWTH_HACK) rand.nextInt(8) + 16 else if (player != null && !RagnarokHandler.blockedPowers[1] && ItemSifRing.getSifRing(player) != null) 6 else 3
			}
			
			fun tick(): Boolean {
				ticksExisted++
				for (i in -range..range) {
					for (j in -2..2) {
						for (k in -range..range) {
							val x = startCoords.posX + i
							val y = startCoords.posY + j
							val z = startCoords.posZ + k
							val block = world.getBlock(x, y, z)
							val meta = world.getBlockMetadata(x, y, z)
							
							if (block === blockToSet && meta == metaToSet) {
								// Only make changes every 20 ticks
								if (ticksExisted % 20 != 0) continue
								
								tickBlock(x, y, z)
							}
						}
					}
				}
				
				return ticksExisted < 80
			}
			
			fun tickBlock(x: Int, y: Int, z: Int) {
				val validCoords = ArrayList<ChunkCoordinates>()
				
				for (xOffset in -TICK_RANGE..TICK_RANGE) {
					for (yOffset in -TICK_RANGE..TICK_RANGE) {
						for (zOffset in -TICK_RANGE..TICK_RANGE) {
//							if (xOffset == 0 && yOffset == 0 && zOffset == 0) continue
							
							if (isValidSwapPosition(x + xOffset, y + yOffset, z + zOffset))
								validCoords.add(ChunkCoordinates(x + xOffset, y + yOffset, z + zOffset))
						}
					}
				}
				
				if (validCoords.isEmpty() || world.isRemote) return
				
				val (tX, tY, tZ) = validCoords.random(rand)!!
				world.setBlock(tX, tY, tZ, blockToSet, metaToSet, 3)
				if (blockToSet == ModBlocks.enchantedSoil || blockToSet == AlfheimBlocks.snowGrass) return
				
				val blockAbove = world.getBlock(tX, tY + 1, tZ)
				val metaAbove = world.getBlockMetadata(tX, tY + 1, tZ)
				
				if (blockAbove == Blocks.tallgrass && metaAbove == 1) {
					world.setBlock(tX, tY + 1, tZ, grassBlock, metaToSet, 1 or 2)
				} else if (blockAbove == Blocks.double_plant && metaAbove == 2) {
					world.setBlock(tX, tY + 1, tZ, tallGrassBlock, tallGrassMeta, 2)
					world.setBlock(tX, tY + 2, tZ, tallGrassBlock, 8, 2)
				}
			}
			
			fun isValidSwapPosition(x: Int, y: Int, z: Int): Boolean {
				val block = world.getBlock(x, y, z)
				val meta = world.getBlockMetadata(x, y, z)
				val aboveBlock = world.getBlock(x, y + 1, z)
				
				return (block == Blocks.dirt || block == Blocks.grass)
						&& (meta == 0)
						&& (aboveBlock.getLightOpacity(world, x, y, z) <= 1)
			}
		}
	}
}
