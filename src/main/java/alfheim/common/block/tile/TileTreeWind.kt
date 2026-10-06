package alfheim.common.block.tile

// PORT: импорты 1.20.1 (блок-сущность 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket as S12PacketEntityVelocity
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.state.BlockState

// PORT: блок-сущность 1.20.1 создаётся сразу в своей точке и со своим типом (alfheim.port.legacy.TileEntity)
class TileTreeWind(pos: BlockPos, state: BlockState): ASJTile(legacyTileType<TileTreeWind>(), pos, state) {
//class TileTreeWind: ASJTile() {

	var firstTick = true
	var friends = HashSet<String>()
	
	override fun updateEntity() {
		if (firstTick) {
			firstTick = false
			getEntitiesWithinAABB(worldObj, EntityPlayerMP::class.java, boundingBox(RANGE)).forEach { friends += it.commandSenderName }
			
			ASJUtilities.dispatchTEToNearbyPlayers(this)
		}
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(RANGE)).forEach {
			if (it is EntityPlayer && (AlfheimConfigHandler.barrierTreeAllowAnyPlayer || it.commandSenderName in friends || it.capabilities.isCreativeMode)) return@forEach
			
			val (x, y, z) = Vector3.fromEntity(it).sub(Vector3.fromTileEntityCenter(this)).normalize()
			
			it.motionX += x
			it.motionY += y
			it.motionZ += z
			
			if (it is EntityPlayerMP)
				it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
		}
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		firstTick = nbt.getBoolean("firstTick")
		
		friends.clear()
		val fList = nbt.getTagList("friends", Constants.NBT.TAG_STRING)
		for (i in 0 until fList.tagCount()) {
			friends += fList.getStringTagAt(i)
		}
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setBoolean("firstTick", firstTick)
		
		val fList = NBTTagList()
		friends.map(::NBTTagString).forEach(fList::appendTag)
		nbt.setTag("friends", fList)
	}
	
	companion object {
		const val RANGE = 10
	}
}

// PORT: имя игрока 1.7.10 (commandSenderName) — имя его профиля (MAPPING.md)
private val EntityPlayer.commandSenderName: String get() = gameProfile.name
