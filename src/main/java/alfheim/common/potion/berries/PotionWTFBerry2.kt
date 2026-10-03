package alfheim.common.potion.berries

// PORT: импорты 1.20.1; сигналы красного камня жезла (КТ-4) закомментированы вместе со своими строками
import alexsocol.asjlib.ASJUtilities
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.potion.PotionAlfheim
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
//import alexsocol.asjlib.math.Vector3
//import alfheim.common.item.rod.RedstoneSignalHandler

object PotionWTFBerry2: PotionAlfheim(AlfheimConfigHandler.potionIDWtfBerry2, "WTFBerry2", false, 0x730606) {
	
	override fun isReady(dur: Int, amp: Int) = ASJUtilities.isServer
	
	// PORT: КТ-4 — сигнал красного камня вокруг игрока (RedstoneSignalHandler жезла ItemRedstoneRod)
	override fun performEffect(target: EntityLivingBase, amp: Int) {
		/*
		val (x, y, z) = Vector3.fromEntity(target).mf()
		RedstoneSignalHandler.get().addSignal(target.worldObj, x, y, z, 2, 15)
		RedstoneSignalHandler.get().addSignal(target.worldObj, x + 1, y, z, 2, 15)
		RedstoneSignalHandler.get().addSignal(target.worldObj, x - 1, y, z, 2, 15)
		RedstoneSignalHandler.get().addSignal(target.worldObj, x, y, z + 1, 2, 15)
		RedstoneSignalHandler.get().addSignal(target.worldObj, x, y, z - 1, 2, 15)
		*/
	}
}
