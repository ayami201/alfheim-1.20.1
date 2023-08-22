package alfheim.common.potion.berries

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.block.tile.TileTreeWind
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.potion.PotionAlfheim
import net.minecraft.entity.*
import net.minecraft.entity.item.*
import net.minecraft.entity.player.*
import net.minecraft.network.play.server.S12PacketEntityVelocity
import kotlin.math.abs

object PotionWTFBerry0: PotionAlfheim(AlfheimConfigHandler.potionIDWtfBerry0, "WTFBerry0", false, 0x99D9BC) {
	
	override fun isReady(dur: Int, amp: Int) = true
	
	override fun performEffect(target: EntityLivingBase, amp: Int) {
		getEntitiesWithinAABB(target.worldObj, Entity::class.java, target.boundingBox(TileTreeWind.RANGE)).forEach {
			if (it === target) return@forEach
			if (abs(it.motionX) < 0.0001 && abs(it.motionZ) < 0.0001) return@forEach
			if (it is EntityItem || it is EntityXPOrb) return@forEach
			
			if (it is EntityPlayer && (AlfheimConfigHandler.berrierTreeAllowAnyPlayer || it.capabilities.isCreativeMode)) return@forEach
			
			val (x, y, z) = Vector3.fromEntity(it).sub(Vector3.fromEntity(target)).normalize()
			
			it.motionX += x
			it.motionY += y
			it.motionZ += z
			
			if (it is EntityPlayerMP)
				it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
		}
	}
}
