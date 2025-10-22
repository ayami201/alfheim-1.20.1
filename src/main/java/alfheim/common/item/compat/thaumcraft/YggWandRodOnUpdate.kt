package alfheim.common.item.compat.thaumcraft

import alfheim.api.ModInfo
import net.minecraft.entity.player.*
import net.minecraft.item.ItemStack
import thaumcraft.api.aspects.*
import thaumcraft.api.wands.IWandRodOnUpdate
import thaumcraft.common.Thaumcraft
import thaumcraft.common.items.wands.ItemWandCasting
import thaumcraft.common.lib.network.PacketHandler
import thaumcraft.common.lib.network.playerdata.PacketAspectPool
import thaumcraft.common.lib.research.ResearchManager
import vazkii.botania.api.mana.ManaItemHandler

object YggWandRodOnUpdate: IWandRodOnUpdate {
	
	const val COST = 100
	
	val primals = Aspect.getPrimalAspects()
	val forHealing = AspectList().add(Aspect.WATER, 100).add(Aspect.EARTH, 100).add(Aspect.ORDER, 100)
	
	override fun onUpdate(stack: ItemStack, player: EntityPlayer) {
		if (player is EntityPlayerMP && player.ticksExisted % 1200 == 0 && player.rng.nextInt(10) == 0) {
			val aspect = primals.random()
			val amount = (player.rng.nextInt(2) + 1).toShort()
			Thaumcraft.proxy.playerKnowledge.addAspectPool(player.commandSenderName, aspect, amount)
			ResearchManager.scheduleSave(player)
			PacketHandler.INSTANCE.sendTo(PacketAspectPool(aspect.getTag(), amount, Thaumcraft.proxy.playerKnowledge.getAspectPoolFor(player.commandSenderName, aspect)), player)
		}
		
		val wand = stack.item as ItemWandCasting
		
		if (player.shouldHeal() && wand.consumeAllVis(stack, player, forHealing, true, false)) {
			player.heal(1f)
		}
		
		val forTool = wand.getCap(stack).tag.startsWith(ModInfo.MODID)
		
		for (primal in primals)
			while (wand.getVis(stack, primal) < wand.getMaxVis(stack) && (if (forTool) ManaItemHandler.requestManaExactForTool(stack, player, COST, true) else ManaItemHandler.requestManaExact(stack, player, COST, true)))
				wand.addVis(stack, primal, 1, true)
	}
}