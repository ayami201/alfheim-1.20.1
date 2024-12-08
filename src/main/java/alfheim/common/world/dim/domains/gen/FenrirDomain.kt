package alfheim.common.world.dim.domains.gen

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.api.world.domain.Domain
import alfheim.client.render.world.SkyRendererDomains
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.entity.boss.EntityFenrir
import alfheim.common.world.dim.domains.WorldProviderDomains
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World

object FenrirDomain: Domain(ModInfo.MODID, "Fenrir", AlfheimConfigHandler.domainPlayersRequired, getBoundingBox(-63, -1, -32, 63, 31, 94), ChunkCoordinates(0, 0, 31)) {
	
	override val skyRenderer = object: SkyRendererDomains({
		val stage2 = WorldProviderDomains.getDomainAtPlayer(mc.thePlayer)
			?.takeIf { it is FenrirDomain }
			?.let {
				val (x, _, z) = Vector3.fromEntity(mc.thePlayer)
				getEntitiesWithinAABB(mc.theWorld, EntityFenrir::class.java, it.boundBox.copy().offset(x, 64, z)).firstOrNull()
			}
			?.let { it.stage > 0 } ?: false
		
		if (stage2) 0XFF00407FU to 0xFF000000U else 0xFFFFEECCU to 0xFFFFFBF2U
	}) {}
	
	override val firstConquerors = arrayOf("Kompotik")
	override val firstConquerorsUnknown = arrayOf("ᚲᛟᛗᛈᛟᛏᛁᚲ")
	
	override fun isLocked(world: World) = if (RagnarokHandler.finished) false else !RagnarokHandler.canBringBackSunAndMoon()
	
	override fun canEnter(players: List<EntityPlayer>) = true
	
	override fun postRestart(world: World, x: Int, y: Int, z: Int, players: List<EntityPlayer>) {
		// spawn new boss
		EntityFenrir.summon(world, x, y, z + 31)
	}
}
