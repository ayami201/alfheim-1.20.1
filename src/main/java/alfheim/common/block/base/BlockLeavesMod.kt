package alfheim.common.block.base

import alexsocol.asjlib.*
import alfheim.client.core.helper.*
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemSubtypedBlockMod
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.*
import vazkii.botania.api.lexicon.ILexiconable
import java.util.*

@Suppress("LeakingThis")
abstract class BlockLeavesMod: BlockLeaves(), IShearable, ILexiconable {
	
	internal var decayField: IntArray? = null
	protected var icons: Array<IIcon?> = emptyArray()
	
	init {
		setCreativeTab(AlfheimTab)
		setHardness(0.2f)
		setLightOpacity(1)
		setStepSound(Block.soundTypeGrass)
		if (ASJUtilities.isClient && isInterpolated())
			MinecraftForge.EVENT_BUS.register(this)
	}
	
	override fun setBlockName(par1Str: String): Block {
		register(par1Str)
		return super.setBlockName(par1Str)
	}
	
	abstract override fun quantityDropped(random: Random): Int
	
	open fun register(name: String) {
		GameRegistry.registerBlock(this, ItemSubtypedBlockMod::class.java, name)
	}
	
	@SideOnly(Side.CLIENT)
	override fun getBlockColor() = 0xFFFFFF
	
	@SideOnly(Side.CLIENT)
	override fun getRenderColor(meta: Int) = 0xFFFFFF
	
	@SideOnly(Side.CLIENT)
	override fun colorMultiplier(world: IBlockAccess?, x: Int, y: Int, z: Int) = 0xFFFFFF
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int): IIcon? {
		setGraphicsLevel(mc.gameSettings.fancyGraphics)
		return icons[field_150127_b]
	}
	
	private fun removeLeaves(world: World, x: Int, y: Int, z: Int) {
		this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0)
		world.setBlockToAir(x, y, z)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		if (!isInterpolated())
			icons = Array(2) { i -> IconHelper.forBlock(reg, this, if (i == 0) "" else "_opaque") }
	}
	
	open fun isInterpolated() = false
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	open fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0 && isInterpolated())
			icons = Array(2) { i -> InterpolatedIconHelper.forBlock(event.map, this, if (i == 0) "" else "_opaque") }
	}
	
	override fun isOpaqueCube() = false
	
	override fun isShearable(item: ItemStack?, world: IBlockAccess?, x: Int, y: Int, z: Int) = true
	
	override fun onSheared(item: ItemStack, world: IBlockAccess, x: Int, y: Int, z: Int, fortune: Int): ArrayList<ItemStack> {
		val ret = ArrayList<ItemStack>()
		val meta = world.getBlockMetadata(x, y, z) and decayBit().inv()
		ret.add(ItemStack(this, 1, meta))
		return ret
	}
	
	override fun beginLeavesDecay(world: World?, x: Int, y: Int, z: Int) = Unit
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		if (world.isRemote) return
		val meta = world.getBlockMetadata(x, y, z)
		if (!canDecay(meta)) return
		
		val range = getDecayRange(meta)
		val extraRange = range + 1
		val bufferSize = extraRange * 2 + 1
		val squareBufferSize = bufferSize * bufferSize
		val halfBufferSize = bufferSize / 2
		val arraySize = bufferSize * bufferSize * bufferSize
		
		if (decayField?.run { size == arraySize } != true) {
			decayField = IntArray(arraySize)
		}
		val decayField = decayField!!
		
		if (world.checkChunksExist(x - extraRange, y - extraRange, z - extraRange, x + extraRange, y + extraRange, z + extraRange)) {
			for (i in 0.bidiRange(range)) {
				for (j in 0.bidiRange(range)) {
					for (k in 0.bidiRange(range)) {
						val block = world.getBlock(x + i, y + j, z + k)
						
						if (!block.canSustainLeaves(world, x + i, y + j, z + k)) {
							if (block.isLeaves(world, x + i, y + j, z + k)) {
								decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] = -2
							} else {
								decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] = -1
							}
						} else {
							decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] = 0
						}
					}
				}
			}
			
			for (state in 1..range) {
				for (i in 0.bidiRange(range)) {
					for (j in 0.bidiRange(range)) {
						for (k in 0.bidiRange(range)) {
							if (decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] == state - 1) {
								if (decayField[(i + halfBufferSize - 1) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] == -2) {
									decayField[(i + halfBufferSize - 1) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] = state
								}
								
								if (decayField[(i + halfBufferSize + 1) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] == -2) {
									decayField[(i + halfBufferSize + 1) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] = state
								}
								
								if (decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize - 1) * bufferSize + k + halfBufferSize] == -2) {
									decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize - 1) * bufferSize + k + halfBufferSize] = state
								}
								
								if (decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize + 1) * bufferSize + k + halfBufferSize] == -2) {
									decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize + 1) * bufferSize + k + halfBufferSize] = state
								}
								
								if (decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + (k + halfBufferSize - 1)] == -2) {
									decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + (k + halfBufferSize - 1)] = state
								}
								
								if (decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize + 1] == -2) {
									decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize + 1] = state
								}
							}
						}
					}
				}
			}
		}
		val state = decayField[halfBufferSize * squareBufferSize + halfBufferSize * bufferSize + halfBufferSize]
		
		if (state < 0) removeLeaves(world, x, y, z)
	}
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer) =
		ItemStack(this, 1, world.getBlockMetadata(x, y, z) and decayBit().inv())
	
	abstract fun decayBit(): Int
	
	open fun getDecayRange(meta: Int) = 4
	
	open fun canDecay(meta: Int) = meta and decayBit() == 0
	
	override fun getDamageValue(world: World, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z)
	
	override fun dropBlockAsItemWithChance(world: World, x: Int, y: Int, z: Int, metadata: Int, chance: Float, fortune: Int) {
		super.dropBlockAsItemWithChance(world, x, y, z, metadata, 1f, fortune)
	}
	
	override fun getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): ArrayList<ItemStack> {
		val ret: ArrayList<ItemStack> = ArrayList()
		
		var chance = 20
		
		if (fortune > 0) {
			chance -= 2 shl fortune
			if (chance < 10) chance = 10
		}
		
		if (world.rand.nextInt(chance) == 0)
			ret.add(ItemStack(getItemDropped(metadata, world.rand, fortune), 1, 0))
		
		chance = 200
		if (fortune > 0) {
			chance -= 10 shl fortune
			if (chance < 40) chance = 40
		}
		
		this.captureDrops(true)
		func_150124_c(world, x, y, z, metadata, chance)
		ret.addAll(this.captureDrops(false))
		return ret
	}
}
