package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); мана в предметах и аксессуарах (КТ-3) закомментирована вместе со своими строками
import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.AlfheimItems
import alfheim.port.legacy.*
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import vazkii.botania.common.item.BotaniaItems as ModItems
//import net.minecraft.inventory.IInventory
//import vazkii.botania.api.BotaniaAPI
//import vazkii.botania.api.mana.IManaItem

object PotionManaVoid: PotionAlfheim(AlfheimConfigHandler.potionIDManaVoid, "manaVoid", true, 0x0000C0) {
	
	init {
		eventForge()
	}
	
	// PORT: LivingUpdateEvent → LivingTickEvent
	@SubscribeEvent
	fun onEntityUpdate(event: LivingEvent.LivingTickEvent) {
		val e = event.entityLiving
		if (!this.hasEffect(e)) return
		if (e !is EntityPlayer) return
		val mainInv = e.inventory
		// PORT: КТ-3 — мана в предметах (ManaItem Botania 1.20.1) и аксессуарах (Curios)
		/*
		val baublesInv = BotaniaAPI.internalHandler.getBaublesInventory(e)
		val invSize = mainInv.sizeInventory
		var size = invSize
		
		if (baublesInv != null) {
			size = invSize + baublesInv.sizeInventory
		}
		var mana = 1040 // Will drain about 1/4 of a tablet in 5 seconds
		
		for (i in 0..size) {
			val useBaubles = i >= invSize
			val inv = if (useBaubles) baublesInv else mainInv
			
			val slot = i - (if (useBaubles) invSize else 0)
			val stackInSlot = (inv as IInventory)[slot]
			if (stackInSlot == null || stackInSlot.item !is IManaItem) continue
			val manaItemSlot = stackInSlot.item as IManaItem
			
			val hasMana = manaItemSlot.getMana(stackInSlot)
			
			if (hasMana > mana) {
				manaItemSlot.addMana(stackInSlot, -mana)
				
				if (useBaubles) {
					BotaniaAPI.internalHandler.sendBaubleUpdatePacket(e, slot)
				}
				
				break
			} else if (hasMana == mana) {
				manaItemSlot.addMana(stackInSlot, -manaItemSlot.getMana(stackInSlot))
				
				if (useBaubles) {
					BotaniaAPI.internalHandler.sendBaubleUpdatePacket(e, slot)
				}
				
				break
			}
			
			val rest = manaItemSlot.getMana(stackInSlot)
			
			manaItemSlot.addMana(stackInSlot, -rest)
			
			mana -= rest
		}
		*/
		val invSize = mainInv.containerSize
		
		// PORT: чёрный лотос metadata 0 и 1 — blackLotus и blackerLotus Botania 1.20.1, увядший лотос — предмет с тем же
		// номером; предмет стака 1.20.1 не меняется (func_150996_a) — стак заменяется новым с тем же числом и NBT
		for (slot in 0 until invSize) {
			val stackInSlot = mainInv.getItem(slot).takeUnless { it.isEmpty } ?: continue
			if (stackInSlot.item !== ModItems.blackLotus && stackInSlot.item !== ModItems.blackerLotus) continue
			mainInv.setItem(slot, ItemStack(AlfheimItems.wiltedLotus[if (stackInSlot.item === ModItems.blackerLotus) 1 else 0], stackInSlot.count).also { it.tag = stackInSlot.tag })
		}
//		for (slot in 0 until invSize) {
//			val stackInSlot = mainInv[slot] ?: continue
//			if (stackInSlot.item !== ModItems.blackLotus) continue
//			stackInSlot.func_150996_a(AlfheimItems.wiltedLotus)
//		}
	}
}
