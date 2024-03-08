package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.D
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.subtile.RadiusDescriptor.Square
import vazkii.botania.api.subtile.SubTileGenerating
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.lexicon.LexiconData
import java.util.*

@Suppress("PropertyName")
class SubTileRattlerose: SubTileGenerating() {
	
	var snake: LinkedList<Pair<Int, Int>> = LinkedList()
	var food = -1 to -1
	var fail: Boolean? = null
	
	val _x get() = supertile.xCoord - RADIUS
	val _y get() = supertile.yCoord
	val _z get() = supertile.zCoord - RADIUS
	val world get() = supertile.worldObj!!
	
	val headBlock get() = Blocks.emerald_block!!
	val tailBlock get() = Blocks.melon_block!!
	val foodBlock get() = ModBlocks.cellBlock!!
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (world.isRemote) return
		
		if (fail != null) {
			if (ticksExisted % 5 != 0) return
			
			val last = snake.removeLastOrNull() ?: run {
				fail = null
				if (food != -1 to -1) tryToReplaceBlock(_x + food.first, _y, _z + food.second, Blocks.air)
				food = -1 to -1
				return
			}
			
			tryToReplaceBlock(_x + last.first, _y, _z + last.second, Blocks.air)
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FIREWORK, world.provider.dimensionId, _x + last.first + 0.5, _y + 0.5, _z + last.second + 0.5, (if (fail!!) 0xFF0000 else 0x00FF00).D)
			
			if (!fail!!) addMana(COST_PER_BLOCK)
			
			return
		}
		
		if (snake.isEmpty()) {
			out@ for (z in 1 until RANGE - 1)
				for (x in 1 until RANGE - 1) {
					if (world.getBlock(_x + x, _y, _z + z) !== headBlock) continue
					
					snake.addFirst(x to z)
					break@out
				}
			
			if (snake.isEmpty()) return
		}
		
		if (food == -1 to -1) {
			generateFood()
		}
		
		val moveDir = getMoveDir() ?: return
		
		move(moveDir)
	}
	
	private fun move(direction: ForgeDirection) {
		val oldHead = snake.first
		val newHead = oldHead.first + direction.offsetX to oldHead.second + direction.offsetZ
		snake.addFirst(newHead)
		
		if (isCollision()) {
			snake.removeFirst()
			fail = true
			return
		}
		
		if (snake.first == WIN_POS) {
			snake.removeFirst()
			fail = false
			return
		}
		
		tryToReplaceBlock(_x + oldHead.first, _y, _z + oldHead.second, tailBlock)
		tryToReplaceBlock(_x + newHead.first, _y, _z + newHead.second, headBlock)
		
		if (newHead != food) {
			val last = snake.removeLast()
			tryToReplaceBlock(_x + last.first, _y, _z + last.second, Blocks.air)
		} else {
			generateFood()
		}
	}
	
	private fun generateFood() {
		if (snake.size > 50) {
			food = -1 to -1
			return
		}
		
		val rand = world.rand
		
		var x: Int
		var z: Int
		do {
			x = rand.nextInt(RANGE)
			z = rand.nextInt(RANGE)
		} while (x to z in snake || x to z in NO_FOOD_HERE)
		
		food = x to z
		tryToReplaceBlock(_x + x, _y, _z + z, foodBlock)
	}
	
	private fun isCollision(): Boolean {
		val head = snake.first
		// Check collision with walls
		if (head.first < 0 || head.first >= RANGE || head.second < 0 || head.second >= RANGE) {
			return true
		}
		// Check collision with itself
		val body = snake.subList(1, snake.size)
		return body.contains(head)
	}
	
	val dirs = arrayOf(ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST)
	
	private fun getMoveDir(): ForgeDirection? {
		var d: ForgeDirection? = null
		
		redstoneSignal = 0
		for (dir in dirs) {
			val redstoneSide = supertile.worldObj.getIndirectPowerLevelTo(supertile.xCoord + dir.offsetX, supertile.yCoord + dir.offsetY, supertile.zCoord + dir.offsetZ, dir.ordinal)
			if (redstoneSide > redstoneSignal) {
				redstoneSignal = redstoneSide
				d = dir
			}
		}
		
		return d
	}
	
	override fun writeToPacketNBT(nbt: NBTTagCompound) {
		super.writeToPacketNBT(nbt)
		
		nbt.setString(TAG_FAIL, fail.toString())
		
		nbt.setString(TAG_FOOD, "${food.first} ${food.second}")
		
		nbt.setInteger(TAG_SIZE, snake.size)
		
		for ((id, s) in snake.withIndex()) {
			nbt.setString(TAG_SNAKE + id, "${s.first} ${s.second}")
		}
	}
	
	override fun readFromPacketNBT(nbt: NBTTagCompound) {
		super.readFromPacketNBT(nbt)
		
		fail = nbt.getString(TAG_FAIL).toBooleanStrictOrNull()
		
		if (nbt.hasKey(TAG_FOOD)) {
			val (x, z) = nbt.getString(TAG_FOOD).split(" ")
			food = x.toInt() to z.toInt()
		} else {
			food = -1 to -1
		}
		
		val size = nbt.getInteger(TAG_SIZE)
		if (size == 0) return
		
		snake.clear()
		
		for (i in 0 until size) {
			val (x, z) = nbt.getString(TAG_SNAKE + i).split(" ")
			snake.add(x.toInt() to z.toInt())
		}
	}
	
	fun tryToReplaceBlock(x: Int, y: Int, z: Int, block: Block) {
		if (world.getBlock(x, y, z).getBlockHardness(world, x, y, z) < 0) {
			if (fail == null) fail = true
			return
		}
		
		world.setBlock(x, y, z, block)
	}
	
	override fun getRadius() = Square(toChunkCoordinates(), RADIUS)
	override fun getMaxMana() = 50000
	override fun getColor() = 0xFFD400
	override fun getEntry() = LexiconData.dandelifeon // TODO
	
	companion object {
		const val COST_PER_BLOCK = 1000
		const val RANGE = 15
		const val RADIUS = 7
		val WIN_POS = RADIUS to RADIUS
		val NO_FOOD_HERE = arrayOf(WIN_POS, RADIUS + 1 to RADIUS, RADIUS - 1 to RADIUS, RADIUS to RADIUS + 1, RADIUS to RADIUS - 1)
		
		const val TAG_FAIL = "fail"
		const val TAG_FOOD = "food"
		const val TAG_SIZE = "size"
		const val TAG_SNAKE = "snake_"
	}
}
