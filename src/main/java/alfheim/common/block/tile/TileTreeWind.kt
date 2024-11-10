package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraftforge.common.util.Constants

class TileTreeWind: ASJTile() {

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
