package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.block.AlfheimBlocks.snakeObject
import alfheim.common.core.handler.*
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.Block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.IIcon
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.common.util.ForgeDirection.*
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.subtile.RadiusDescriptor.Square
import vazkii.botania.api.subtile.SubTileGenerating
import java.util.*
import kotlin.math.max
import alfheim.common.block.AlfheimBlocks.snakeBody as snakeBlock

class SubTileRattlerose: SubTileGenerating() {
	
	var snake: LinkedList<Pair<Int, Int>> = LinkedList()
	var food = -1 to -1
	var fail: Boolean? = null
		set(value) {
			field = value
			if (field != null) lastMove = UNKNOWN
		}
	var lastMove = UNKNOWN
	var prevDir = UNKNOWN
	var owner = ""
	
	val _x get() = supertile.xCoord - RADIUS
	val _y get() = supertile.yCoord + 1
	val _z get() = supertile.zCoord - RADIUS
	val world get() = supertile.worldObj!!
	val speed: Int
		get() {
			val cfg = AlfheimConfigHandler.rattleroseSpeed
			if (cfg <= 5) return cfg
			
			return max(5, cfg - snake.size / 10)
		}
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (world.isRemote) return
		
		if (fail != null) {
			if (ticksExisted % 5 != 0) return
			
			if (ASJUtilities.isServer && snake.size >= MAX_SIZE && owner.isNotEmpty()) {
				MinecraftServer.getServer().configurationManager.func_152612_a(owner)?.triggerAchievement(AlfheimAchievements.midgardsormr)
			}
			
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
					if (world.getBlock(_x + x, _y, _z + z) !== snakeObject) continue
					if (world.getBlockMetadata(_x + x, _y, _z + z) != 0) continue
					
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
	
	private fun move(dir: ForgeDirection) {
		if (dir == UNKNOWN) return
		
		val curHead = snake.first()
		val newHead = curHead.first + dir.offsetX to curHead.second + dir.offsetZ
		snake.addFirst(newHead)
		
		val last = if (newHead != food) snake.removeLast() else null
		
		if (isCollision()) {
			snake.addLast(last!!)
			snake.removeFirst()
			fail = true
			return
		}
		
		val tailMeta =
						if (dir == NORTH && prevDir == NORTH) 4 else
						if (dir == SOUTH && prevDir == SOUTH) 5 else
						if (dir == WEST && prevDir == WEST) 6 else
						if (dir == EAST && prevDir == EAST) 7 else
						
						if (dir == WEST && prevDir == SOUTH) 8 else
						if (dir == WEST && prevDir == NORTH) 9 else
						if (dir == SOUTH && prevDir == EAST) 12 else
						if (dir == SOUTH && prevDir == WEST) 10 else
						if (dir == EAST && prevDir == NORTH) 13 else
						if (dir == EAST && prevDir == SOUTH) 11 else
						if (dir == NORTH && prevDir == WEST) 14 else
						if (dir == NORTH && prevDir == EAST) 15 else
						
						4
		tryToReplaceBlock(_x + curHead.first, _y, _z + curHead.second, snakeBlock, tailMeta)
		tryToReplaceBlock(_x + newHead.first, _y, _z + newHead.second, snakeBlock, dir.ordinal - 2)
		
		if (newHead != food) {
			if (last != newHead) tryToReplaceBlock(_x + last!!.first, _y, _z + last.second, Blocks.air)
			
			if (newHead == WIN_POS) {
				fail = false
				return
			}
		} else {
			generateFood()
		}
		
		prevDir = dir
	}
	
	private fun generateFood() {
		if (snake.size >= MAX_SIZE) {
			food = -1 to -1
			return
		}
		
		val rand = world.rand
		
		var pos: Pair<Int, Int>
		do {
			pos = rand.nextInt(RANGE) to rand.nextInt(RANGE)
		} while (pos in snake || pos == WIN_POS)
		
		food = pos
		tryToReplaceBlock(_x + pos.first, _y, _z + pos.second, snakeObject, 1)
	}
	
	private fun isCollision(): Boolean {
		val head = snake.first()
		if (head.first < 0 || head.first >= RANGE || head.second < 0 || head.second >= RANGE) return true
		val body = snake.subList(1, snake.size)
		return body.contains(head)
	}
	
	private val dirs = arrayOf(DOWN to true, UP to true, NORTH to false, SOUTH to false, WEST to false, EAST to false)
	
	private fun getMoveDir(): ForgeDirection {
		var d = UNKNOWN
		
		redstoneSignal = 0
		for ((dir, repeat) in dirs) {
			val redstoneSide = supertile.worldObj.getIndirectPowerLevelTo(supertile.xCoord + dir.offsetX, supertile.yCoord + dir.offsetY, supertile.zCoord + dir.offsetZ, dir.ordinal)
			if (redstoneSide > redstoneSignal) {
				redstoneSignal = redstoneSide
				d = if (repeat) lastMove else dir
			}
		}
		
		if (snake.size > 1 && lastMove.opposite == d) d = if (speed > 0) lastMove else UNKNOWN
		
		if (d != UNKNOWN) lastMove = d
		
		return if (speed > 0) if (ticksExisted % speed == 0) lastMove else UNKNOWN else d
	}
	
	private fun tryToReplaceBlock(x: Int, y: Int, z: Int, block: Block, meta: Int = 0) {
		if (world.getBlock(x, y, z) inln gameBlocks) {
			if (fail == null) fail = true
			return
		}
		
		world.setBlock(x, y, z, block, meta, 3)
	}
	
	override fun onBlockPlacedBy(world: World?, x: Int, y: Int, z: Int, entity: EntityLivingBase?, stack: ItemStack?) {
		super.onBlockPlacedBy(world, x, y, z, entity, stack)
		if (entity is EntityPlayer) owner = entity.commandSenderName
	}
	
	override fun writeToPacketNBT(nbt: NBTTagCompound) {
		super.writeToPacketNBT(nbt)
		
		nbt.setString(TAG_OWNER, owner)
		nbt.setString(TAG_FAIL, fail.toString())
		nbt.setString(TAG_FOOD, "${food.first} ${food.second}")
		nbt.setInteger(TAG_LAST, lastMove.ordinal)
		nbt.setInteger(TAG_PREV, prevDir.ordinal)
		nbt.setInteger(TAG_SIZE, snake.size)
		for ((id, s) in snake.withIndex()) {
			nbt.setString(TAG_SNAKE + id, "${s.first} ${s.second}")
		}
	}
	
	override fun readFromPacketNBT(nbt: NBTTagCompound) {
		super.readFromPacketNBT(nbt)
		
		owner = nbt.getString(TAG_OWNER)
		
		fail = nbt.getString(TAG_FAIL).toBooleanStrictOrNull()
		
		if (nbt.hasKey(TAG_FOOD)) {
			val (x, z) = nbt.getString(TAG_FOOD).split(" ")
			food = x.toInt() to z.toInt()
		} else {
			food = -1 to -1
		}
		
		lastMove = ForgeDirection.entries[nbt.getInteger(TAG_LAST)]
		prevDir = ForgeDirection.entries[nbt.getInteger(TAG_PREV)]
		
		val size = nbt.getInteger(TAG_SIZE)
		if (size == 0) return
		
		snake.clear()
		
		for (i in 0 until size) {
			val (x, z) = nbt.getString(TAG_SNAKE + i).split(" ")
			snake.add(x.toInt() to z.toInt())
		}
	}
	
	override fun getComparatorInputOverride(side: Int) = if (snake.size >= MAX_SIZE) 15 else 0
	override fun getRadius() = Square(toChunkCoordinates(), RADIUS)
	override fun getMaxMana() = 24976 * COST_PER_BLOCK
	override fun getColor() = 0x4DB799
	override fun getEntry() = AlfheimLexiconData.flowerRattlerose
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("rattlerose").getIconForStack(null)
	
	companion object {
		const val MAX_SIZE = 224
		const val COST_PER_BLOCK = 1000
		const val RANGE = 15
		const val RADIUS = 7
		
		const val TAG_FAIL = "fail"
		const val TAG_FOOD = "food"
		const val TAG_LAST = "last"
		const val TAG_OWNER = "owner"
		const val TAG_PREV = "prev"
		const val TAG_SIZE = "size"
		const val TAG_SNAKE = "snake_"
		
		val WIN_POS = RADIUS to RADIUS
		
		val gameBlocks get() = arrayOf(snakeBlock, snakeObject, Blocks.air)
	}
}
