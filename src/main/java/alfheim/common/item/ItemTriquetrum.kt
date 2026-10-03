package alfheim.common.item

// PORT: импорты 1.20.1 (MAPPING.md); ItemNBTHelper Botania 1.20.1 — те же методы в пакете common.helper
import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.item.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.level.block.state.BlockState
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.helper.ItemNBTHelper.*
import kotlin.math.*

class ItemTriquetrum: ItemMod("Triquetrum"), IDoubleBoundItem, IRotationDisplay {
	
	init {
		maxStackSize = 1
	}
	
	// PORT: addInformation → appendHoverText; строка подсказки — Component.translatable с теми же ключами
	override fun appendHoverText(stack: ItemStack, world: World?, list: MutableList<Component>, adv: TooltipFlag) {
		val first = getFirstPosition(stack) ?: return
		val second = getSecondPosition(stack) ?: return
		
		val (x, y, z) = Vector3(second.posX, second.posY, second.posZ).sub(first.posX, first.posY, first.posZ).add(1)
		list.add(Component.translatable("item.Triquetrum.blocks", abs(x * y * z).I))
		list.add(Component.translatable("item.Triquetrum.rotation", getRotation(stack) * 90))
//		list.add(StatCollector.translateToLocalFormatted("item.Triquetrum.blocks", abs(x * y * z).I))
//		list.add(StatCollector.translateToLocalFormatted("item.Triquetrum.rotation", getRotation(stack) * 90))
	}
	
	// PORT: onItemRightClick → use
	override fun use(world: World, player: EntityPlayer, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		if (player.isShiftKeyDown) {
			setFirstPosition(stack, 0, NO_Y, 0)
			setSecondPosition(stack, 0, NO_Y, 0)
//			setFirstPosition(stack, 0, -1, 0)
//			setSecondPosition(stack, 0, -1, 0)
		} else {
			setInt(stack, TAG_ROTATION, (getInt(stack, TAG_ROTATION, 0) + 1) % 4)
		}
		
		return InteractionResultHolder.consume(stack)
//		return stack
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hX: Float, hY: Float, hZ: Float): Boolean {
		val first = getFirstPosition(stack)
		val second = getSecondPosition(stack)
		
		when {
			first == null  -> setFirstPosition(stack, x, y, z)
			
			second == null -> {
				val (i, j, k) = first
				
				if (AlfheimConfigHandler.triquetrumMaxVolume != -1 && abs(i - x) * abs(j - y) * abs(k - z) > AlfheimConfigHandler.triquetrumMaxVolume) {
					ASJUtilities.say(player, "item.Triquetrum.tooLarge", AlfheimConfigHandler.triquetrumMaxVolume)
					return false
				}
				
				setSecondPosition(stack, x, y, z)
			}
			
			else           -> run {
				val rotation = getRotation(stack)
				if (rotation == -1) {
					world.spawnParticle("explode", x.D, y.D, z.D, 0.D, 0.D, 0.D)
					return@run
				}
				
				val dir = ForgeDirection.getOrientation(side)
				
				val fx = min(first.posX, second.posX)
				val fX = max(first.posX, second.posX)
				
				val fy = min(first.posY, second.posY)
				val fY = max(first.posY, second.posY)
				
				val fz = min(first.posZ, second.posZ)
				val fZ = max(first.posZ, second.posZ)
				
				outer@
				for ((xOff, i) in (fx..fX).withIndex()) {
					for ((yOff, j) in (fy..fY).withIndex()) {
						for ((zOff, k) in (fz..fZ).withIndex()) {
							val survival = !player.capabilities.isCreativeMode
							
							val block = world.getBlock(i, j, k) // block to be moved
							// PORT: блок и metadata 1.7.10 — состояние блока 1.20.1; твёрдость — у состояния
							val state = world.getBlockState(BlockPos(i, j, k))
							if (state.getDestroySpeed(world, BlockPos(i, j, k)) == -1f && survival) continue
//							if (block.getBlockHardness(world, i, j, k) == -1f && survival) continue
							
							if (survival && GameRegistry.findUniqueIdentifierFor(block).toString() in AlfheimConfigHandler.triquetrumBlackList) continue
							
//							val meta = world.getBlockMetadata(i, j, k)
							
							// PORT: writeToNBT(nbt) → saveWithFullMetadata(): данные блок-сущности с её id, как в 1.7.10
							val nbt = world.getTileEntity(i, j, k)?.saveWithFullMetadata() ?: NBTTagCompound()
//							val nbt = NBTTagCompound()
//
//							world.getTileEntity(i, j, k)?.writeToNBT(nbt)
							
							if (!nbt.hasNoTags() && !AlfheimConfigHandler.triquetrumTiles) continue
							
							if (survival && !ManaItemHandler.instance().requestManaExactForTool(stack, player, if (nbt.hasNoTags()) 60 else 100, false)) break@outer
							
							// PORT: metadata → состояние блока; canPlaceBlockAt → canSurvive (место и так проверено на воздух);
							// блок-сущность 1.20.1 создаётся сразу в своей точке (loadStatic) — координаты не переставляются
							fun setBlockTile(world: World, x: Int, y: Int, z: Int, block: Block, state: BlockState, cmp: NBTTagCompound): Boolean {
								if (!world.isAirBlock(x, y, z)) return false // do not replace blocks
								if (world.isAirBlock(i, j, k)) return false // no sense in moving air
								if (!state.canSurvive(world, BlockPos(x, y, z))) return false // no more cactus on bedrock
//								if (!block.canPlaceBlockAt(world, x, y, z)) return false // no more cactus on bedrock
								
								if (!world.setBlock(x, y, z, state, 3)) return false
//								if (!world.setBlock(x, y, z, block, meta, 3)) return false
								
								if (block !is EntityBlock) return true
								val tile = TileEntity.loadStatic(BlockPos(x, y, z), state, cmp) ?: return true
//								if (block !is ITileEntityProvider) return true
//								val tile = TileEntity.createAndLoadEntity(cmp) ?: return true
//
//								tile.xCoord = x
//								tile.yCoord = y
//								tile.zCoord = z
								world.setBlockEntity(tile)
//								world.setTileEntity(x, y, z, tile)
								
								return true
							}
							
							val flag = when (rotation) {
								0    -> setBlockTile(world, x + xOff + dir.offsetX, y + yOff + dir.offsetY, z + zOff + dir.offsetZ, block, state, nbt)
								1    -> setBlockTile(world, x + zOff + dir.offsetX, y + yOff + dir.offsetY, z - xOff + dir.offsetZ, block, state, nbt)
								2    -> setBlockTile(world, x - xOff + dir.offsetX, y + yOff + dir.offsetY, z - zOff + dir.offsetZ, block, state, nbt)
								3    -> setBlockTile(world, x - zOff + dir.offsetX, y + yOff + dir.offsetY, z + xOff + dir.offsetZ, block, state, nbt)
								else -> false
							}
//								0    -> setBlockTile(world, x + xOff + dir.offsetX, y + yOff + dir.offsetY, z + zOff + dir.offsetZ, block, meta, nbt)
//								1    -> setBlockTile(world, x + zOff + dir.offsetX, y + yOff + dir.offsetY, z - xOff + dir.offsetZ, block, meta, nbt)
//								2    -> setBlockTile(world, x - xOff + dir.offsetX, y + yOff + dir.offsetY, z - zOff + dir.offsetZ, block, meta, nbt)
//								3    -> setBlockTile(world, x - zOff + dir.offsetX, y + yOff + dir.offsetY, z + xOff + dir.offsetZ, block, meta, nbt)
							
							if (flag && stack.meta != 1) {
								ManaItemHandler.instance().requestManaExactForTool(stack, player, if (nbt.hasNoTags()) 60 else 100, true)
								
								if (block is EntityBlock) world.removeTileEntity(i, j, k)
//								if (block is ITileEntityProvider) world.removeTileEntity(i, j, k)
								world.setBlockToAir(i, j, k)
							}
						}
					}
				}
				
				setFirstPosition(stack, 0, NO_Y, 0)
				setSecondPosition(stack, 0, NO_Y, 0)
//				setFirstPosition(stack, 0, -1, 0)
//				setSecondPosition(stack, 0, -1, 0)
				setInt(stack, TAG_ROTATION, 0)
			}
		}
		
		return true
	}
	
	fun setFirstPosition(stack: ItemStack, x: Int, y: Int, z: Int) {
		setInt(stack, TAG_BIND_X_1, x)
		setInt(stack, TAG_BIND_Y_1, y)
		setInt(stack, TAG_BIND_Z_1, z)
	}
	
	fun setSecondPosition(stack: ItemStack, x: Int, y: Int, z: Int) {
		setInt(stack, TAG_BIND_X_2, x)
		setInt(stack, TAG_BIND_Y_2, y)
		setInt(stack, TAG_BIND_Z_2, z)
	}
	
	override fun getFirstPosition(stack: ItemStack): ChunkCoordinates? {
		val coords = ChunkCoordinates(getInt(stack, TAG_BIND_X_1, 0), getInt(stack, TAG_BIND_Y_1, NO_Y), getInt(stack, TAG_BIND_Z_1, 0))
		return if (coords.posY == NO_Y) null else coords
//		val coords = ChunkCoordinates(getInt(stack, TAG_BIND_X_1, 0), getInt(stack, TAG_BIND_Y_1, -1), getInt(stack, TAG_BIND_Z_1, 0))
//		return if (coords.posY == -1) null else coords
	}
	
	override fun getSecondPosition(stack: ItemStack): ChunkCoordinates? {
		val coords = ChunkCoordinates(getInt(stack, TAG_BIND_X_2, 0), getInt(stack, TAG_BIND_Y_2, NO_Y), getInt(stack, TAG_BIND_Z_2, 0))
		return if (coords.posY == NO_Y) null else coords
//		val coords = ChunkCoordinates(getInt(stack, TAG_BIND_X_2, 0), getInt(stack, TAG_BIND_Y_2, -1), getInt(stack, TAG_BIND_Z_2, 0))
//		return if (coords.posY == -1) null else coords
	}
	
	override fun getRotation(stack: ItemStack): Int {
		if (getFirstPosition(stack) != null && getSecondPosition(stack) != null)
			return getInt(stack, TAG_ROTATION, 0)
		
		return -1
	}
	
	companion object {
		
		const val TAG_BIND_X_1 = "bindx1"
		const val TAG_BIND_Y_1 = "bindy1"
		const val TAG_BIND_Z_1 = "bindz1"
		const val TAG_BIND_X_2 = "bindx2"
		const val TAG_BIND_Y_2 = "bindy2"
		const val TAG_BIND_Z_2 = "bindz2"
		
		const val TAG_ROTATION = "rotation"
		
		// PORT: «точки нет» у автора — y = −1, ниже мира 1.7.10. Мир 1.20.1 бывает ниже нуля, блок на −1 — обычный;
		// признак — высота, которой у блока не бывает
		const val NO_Y = Int.MIN_VALUE
	}
}
