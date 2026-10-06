package alfheim.common.potion.berries

// PORT: импорты 1.20.1 (MAPPING.md)
import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.block.tile.TileTreeWind
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.potion.PotionAlfheim
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket as S12PacketEntityVelocity
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ExperienceOrb as EntityXPOrb
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.item.ItemEntity as EntityItem
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.phys.Vec3
import kotlin.math.abs

object PotionWTFBerry0: PotionAlfheim(AlfheimConfigHandler.potionIDWtfBerry0, "WTFBerry0", false, 0x99D9BC) {
	
	override fun isReady(dur: Int, amp: Int) = true
	
	override fun performEffect(target: EntityLivingBase, amp: Int) {
		getEntitiesWithinAABB(target.worldObj, Entity::class.java, target.boundingBox(TileTreeWind.RANGE)).forEach {
			if (it === target) return@forEach
			if (abs(it.motionX) < 0.0001 && abs(it.motionZ) < 0.0001) return@forEach
			if (it is EntityItem || it is EntityXPOrb) return@forEach
			
			if (it is EntityPlayer && (AlfheimConfigHandler.barrierTreeAllowAnyPlayer || it.capabilities.isCreativeMode)) return@forEach
			
			val (x, y, z) = Vector3.fromEntity(it).sub(Vector3.fromEntity(target)).normalize()
			
			it.motionX += x
			it.motionY += y
			it.motionZ += z
			
			if (it is EntityPlayerMP)
				it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
		}
	}
	
	// PORT: имена полей 1.7.10; скорость существа 1.20.1 — вектор deltaMovement, пакет — через connection
	private val Entity.worldObj get() = level()
	private val EntityPlayer.capabilities get() = abilities
	private val net.minecraft.world.entity.player.Abilities.isCreativeMode get() = instabuild
	private val EntityPlayerMP.playerNetServerHandler get() = connection
	private fun net.minecraft.server.network.ServerGamePacketListenerImpl.sendPacket(packet: net.minecraft.network.protocol.Packet<*>) = send(packet)
	private var Entity.motionX: Double
		get() = deltaMovement.x
		set(value) {
			deltaMovement = Vec3(value, deltaMovement.y, deltaMovement.z)
		}
	private var Entity.motionY: Double
		get() = deltaMovement.y
		set(value) {
			deltaMovement = Vec3(deltaMovement.x, value, deltaMovement.z)
		}
	private var Entity.motionZ: Double
		get() = deltaMovement.z
		set(value) {
			deltaMovement = Vec3(deltaMovement.x, deltaMovement.y, value)
		}
}