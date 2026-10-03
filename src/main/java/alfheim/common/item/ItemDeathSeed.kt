package alfheim.common.item

// PORT: импорты 1.20.1 (MAPPING.md); Botania.proxy — alfheim.port.legacy.botania.Botania, плющ верности Botania 1.7.10
// (ItemKeepIvy) в 1.20.1 — ResoluteIvyItem с тем же тегом
import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.port.legacy.*
import alfheim.port.legacy.Potion1710 as Potion
import alfheim.port.legacy.botania.Botania
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.*
import vazkii.botania.common.item.ResoluteIvyItem as ItemKeepIvy

class ItemDeathSeed: ItemMod("DeathSeed") {
	
	init {
		maxStackSize = 1
	}
	
	// PORT: onUpdate → inventoryTick
	override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, inHand: Boolean) {
		if (!ItemNBTHelper.getBoolean(stack, ItemKeepIvy.TAG_KEEP, false))
			ItemNBTHelper.setBoolean(stack, ItemKeepIvy.TAG_KEEP, true)
	}
	
	// PORT: onItemRightClick → use. Измерение — id строкой (номеров измерений в 1.20.1 нет). «Места нет» у автора —
	// y < 0, ниже мира 1.7.10: семя без записи или смерть в пустоте. В 1.20.1 мир бывает ниже нуля, поэтому «места
	// нет» — нет записи высоты (смерть ниже мира её не пишет, onPlayerDied). posY своего игрока на клиенте 1.7.10 был
	// на уровне глаз (отсюда − 1.6), в 1.20.1 posY — у ног
	override fun use(world: World, player: EntityPlayer, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		val d = ItemNBTHelper.getString(stack, TAG_D, World.OVERWORLD.location().toString())
		val x = ItemNBTHelper.getDouble(stack, TAG_X, 0.0)
		val y = ItemNBTHelper.getDouble(stack, TAG_Y, -1.0)
		val z = ItemNBTHelper.getDouble(stack, TAG_Z, 0.0)
		
		if (!ItemNBTHelper.verifyExistance(stack, TAG_Y)) return InteractionResultHolder.pass(stack)
//		val d = ItemNBTHelper.getInt(stack, TAG_D, 0)
//		if (y < 0) return stack
		
		player.addPotionEffect(PotionEffectU(Potion.resistance.id, 100, 4))
		ASJUtilities.sendToDimensionWithoutPortal(player, dimensionKey(d), x, y, z)
		
		world.playSoundAtEntity(player, "mob.endermen.portal", 1f, 1f)
		for (i in 0..49)
			Botania.proxy.sparkleFX(player.worldObj, player.posX + Math.random() * player.width, player.posY + Math.random() * player.height, player.posZ + Math.random() * player.width, 0.25f, 1f, 0.25f, 1f, 10)
//			Botania.proxy.sparkleFX(player.worldObj, player.posX + Math.random() * player.width, player.posY - 1.6 + Math.random() * player.height, player.posZ + Math.random() * player.width, 0.25f, 1f, 0.25f, 1f, 10)
		
		stack.shrink(1)
//		--stack.stackSize
		
		return InteractionResultHolder.consume(stack)
	}
	
	companion object {
		
		const val TAG_D = "d"
		const val TAG_X = "x"
		const val TAG_Y = "y"
		const val TAG_Z = "z"
		
		init {
			eventForge()
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun onPlayerDied(e: LivingDeathEvent) {
			val player = e.entityLiving as? EntityPlayer ?: return
			val slot = ASJUtilities.getSlotWithItem(AlfheimItems.deathSeed, player.inventory)
			if (slot == -1) return
			val stack = player.inventory[slot] ?: return
			val (x, y, z) = Vector3.fromEntity(player)
			ItemNBTHelper.setString(stack, TAG_D, player.level().dimensionId)
//			ItemNBTHelper.setInt(stack, TAG_D, player.dimension)
			ItemNBTHelper.setDouble(stack, TAG_X, x)
			// PORT: смерть ниже мира (в пустоте) у автора записывалась с y < 0 — «места нет». Низ мира 1.20.1 у
			// измерений свой (у основного мира −64): такая смерть записывается без высоты
			if (y < player.level().minBuildHeight) stack.tag?.remove(TAG_Y) else
			ItemNBTHelper.setDouble(stack, TAG_Y, y)
			ItemNBTHelper.setDouble(stack, TAG_Z, z)
		}
	}
	
	// PORT: имена полей 1.7.10
	private val Entity.worldObj get() = level()
	private val Entity.posX get() = x
	private val Entity.posY get() = y
	private val Entity.posZ get() = z
	private val Entity.width get() = bbWidth
	private val Entity.height get() = bbHeight
}
