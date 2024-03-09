package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.subtile.RadiusDescriptor.Square
import vazkii.botania.api.subtile.SubTileGenerating
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.lexicon.LexiconData
import java.util.*
import kotlin.math.max

class SubTileRattlerose: SubTileGenerating() {
	
	var snake: LinkedList<Pair<Int, Int>> = LinkedList()
	var food = -1 to -1
	var fail: Boolean? = null
		set(value) {
			field = value
			if (field != null) lastMove = ForgeDirection.UNKNOWN
		}
	var lastMove = ForgeDirection.UNKNOWN
	
	val _x get() = supertile.xCoord - RADIUS
	val _y get() = supertile.yCoord + 1
	val _z get() = supertile.zCoord - RADIUS
	val world get() = supertile.worldObj!!
	val speed: Int
		get() {
			val s = AlfheimConfigHandler.rattleroseSpeed
			if (s <= 5) return s
			
			return max(5, s - snake.size / 10)
		}
	
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
			
			if (!fail!!) addMana(COST_PER_BLOCK * snake.size)
			
			return
		}
		
		if (snake.isEmpty()) {
			out@ for (z in 0 until RANGE)
				for (x in 0 until RANGE) {
					if (world.getBlock(_x + x, _y, _z + z) !== headBlock) continue
					
					snake.addFirst(x to z)
					break@out
				}
			
			if (snake.isEmpty()) return
		}
		
		if (food == -1 to -1) {
			generateFood()
		}
		
		move(getMoveDir())
	}
	
	private fun move(direction: ForgeDirection) {
		if (direction == ForgeDirection.UNKNOWN) return
		
		val curHead = snake.first
		val newHead = curHead.first + direction.offsetX to curHead.second + direction.offsetZ
		snake.addFirst(newHead)
		
		if (isCollision()) {
			snake.removeFirst()
			fail = true
			return
		}
		
		tryToReplaceBlock(_x + curHead.first, _y, _z + curHead.second, tailBlock)
		tryToReplaceBlock(_x + newHead.first, _y, _z + newHead.second, headBlock, when (direction) {
			ForgeDirection.NORTH -> 2
			ForgeDirection.SOUTH -> 0
			ForgeDirection.WEST -> 1
			ForgeDirection.EAST -> 3
			else -> 0
		})
		
		if (newHead != food) {
			val last = snake.removeLast()
			tryToReplaceBlock(_x + last.first, _y, _z + last.second, Blocks.air)
			
			if (newHead == WIN_POS) {
				fail = false
				return
			}
		} else {
			generateFood()
		}
	}
	
	private fun generateFood() {
		if (snake.size >= 223) {
			food = -1 to -1
			return
		}
		
		val rand = world.rand
		
		var pos: Pair<Int, Int>
		do {
			pos = rand.nextInt(RANGE) to rand.nextInt(RANGE)
		} while (pos in snake || pos == WIN_POS)
		
		food = pos
		tryToReplaceBlock(_x + pos.first, _y, _z + pos.second, foodBlock)
	}
	
	private fun isCollision(): Boolean {
		val head = snake.first
		if (head.first < 0 || head.first >= RANGE || head.second < 0 || head.second >= RANGE) return true
		val body = snake.subList(1, snake.size)
		return body.contains(head)
	}
	
	val dirs = arrayOf(ForgeDirection.DOWN to true, ForgeDirection.UP to true, ForgeDirection.NORTH to false, ForgeDirection.SOUTH to false, ForgeDirection.WEST to false, ForgeDirection.EAST to false)
	
	private fun getMoveDir(): ForgeDirection {
		var d = ForgeDirection.UNKNOWN
		
		redstoneSignal = 0
		for ((dir, repeat) in dirs) {
			val redstoneSide = supertile.worldObj.getIndirectPowerLevelTo(supertile.xCoord + dir.offsetX, supertile.yCoord + dir.offsetY, supertile.zCoord + dir.offsetZ, dir.ordinal)
			if (redstoneSide > redstoneSignal) {
				redstoneSignal = redstoneSide
				d = if (repeat) lastMove else dir
			}
		}
		
		if (snake.size > 1 && lastMove.opposite == d) d = if (speed > 0) lastMove else ForgeDirection.UNKNOWN
		
		if (d != ForgeDirection.UNKNOWN) lastMove = d
		
		return if (speed > 0) if (ticksExisted % speed == 0) lastMove else ForgeDirection.UNKNOWN else d
	}
	
	override fun writeToPacketNBT(nbt: NBTTagCompound) {
		super.writeToPacketNBT(nbt)
		
		nbt.setString(TAG_FAIL, fail.toString())
		nbt.setString(TAG_FOOD, "${food.first} ${food.second}")
		nbt.setInteger(TAG_LAST, lastMove.ordinal)
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
		
		lastMove = ForgeDirection.entries[nbt.getInteger(TAG_LAST)]
		
		val size = nbt.getInteger(TAG_SIZE)
		if (size == 0) return
		
		snake.clear()
		
		for (i in 0 until size) {
			val (x, z) = nbt.getString(TAG_SNAKE + i).split(" ")
			snake.add(x.toInt() to z.toInt())
		}
	}
	
	fun tryToReplaceBlock(x: Int, y: Int, z: Int, block: Block, meta: Int = 0) {
		if (world.getBlock(x, y, z) inln gameBlocks) {
			if (fail == null) fail = true
			return
		}
		
		world.setBlock(x, y, z, block, meta, 3)
	}
	
	override fun getRadius() = Square(toChunkCoordinates(), RADIUS)
	override fun getMaxMana() = 27261 * COST_PER_BLOCK
	override fun getColor() = 0xFFD400
	override fun getEntry() = LexiconData.dandelifeon // TODO
	
	companion object {
		const val COST_PER_BLOCK = 1000
		const val RANGE = 15
		const val RADIUS = 7
		
		const val TAG_FAIL = "fail"
		const val TAG_FOOD = "food"
		const val TAG_LAST = "last"
		const val TAG_SIZE = "size"
		const val TAG_SNAKE = "snake_"
		
		val WIN_POS = RADIUS to RADIUS
		
		val headBlock get() = Blocks.lit_pumpkin!!
		val tailBlock get() = ModBlocks.blazeBlock!!
		val foodBlock get() = ModBlocks.cellBlock!!
		
		val gameBlocks get() = arrayOf(headBlock, tailBlock, foodBlock, Blocks.air)
	}
}
