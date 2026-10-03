package alfheim.port.legacy

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.level.Level

/**
 * `net.minecraft.item.ItemFood` 1.7.10 (SPEC, Р-4; MAPPING.md, «Блоки и предметы»).
 *
 * В 1.20.1 еда — свойство предмета (`FoodProperties`), одно на предмет, и съедает её `LivingEntity.eat`. В 1.7.10
 * сытость и насыщение спрашивались у предмета для каждого стака (`func_150905_g(stack)`, `func_150906_h(stack)`), а
 * съедал стак сам предмет (`onEaten`). Еда автора меняет их по варианту, поэтому прослойка повторяет `ItemFood`
 * 1.7.10: правый клик начинает есть, конец — `finishUsingItem` (`onEaten` 1.7.10).
 *
 * `player.canEat` — метод 1.20.1: в творческом режиме есть можно всегда (в 1.7.10 — только то, что едят всегда).
 */
open class ItemFood1710(val healAmount: Int, val saturationModifier: Float, val isWolfsFavoriteMeat: Boolean): Item1710() {

	/** `setAlwaysEdible` 1.7.10: есть можно и сытым */
	var alwaysEdible = false
		private set

	fun setAlwaysEdible(): ItemFood1710 {
		alwaysEdible = true
		return this
	}

	/** `func_150905_g(stack)` 1.7.10 — сколько единиц голода восстанавливает стак */
	open fun func_150905_g(stack: ItemStack) = healAmount

	/** `func_150906_h(stack)` 1.7.10 — множитель насыщения стака */
	open fun func_150906_h(stack: ItemStack) = saturationModifier

	/** `getMaxItemUseDuration` 1.7.10 */
	override fun getUseDuration(stack: ItemStack) = 32

	/** `getItemUseAction` 1.7.10 */
	override fun getUseAnimation(stack: ItemStack) = UseAnim.EAT

	/** `onItemRightClick` 1.7.10: начать есть, если можно */
	override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		if (!player.canEat(alwaysEdible)) return InteractionResultHolder.fail(stack)
		player.startUsingItem(hand)
		return InteractionResultHolder.consume(stack)
	}

	/**
	 * `onEaten` 1.7.10: минус один в стаке, сытость и насыщение стака (`FoodStats.func_151686_a` — то же, что
	 * `FoodData.eat` 1.20.1), отрыжка, [onFoodEaten]. В 1.7.10 ест только игрок. Пустой стак 1.20.1 теряет свой предмет,
	 * поэтому сытость и насыщение берутся до того, как стак уменьшится
	 */
	override fun finishUsingItem(stack: ItemStack, world: Level, entity: LivingEntity): ItemStack {
		val player = entity as? Player ?: return stack
		val food = func_150905_g(stack)
		val saturation = func_150906_h(stack)
		stack.shrink(1)
		player.foodData.eat(food, saturation)
		world.playSoundAtEntity(player, "random.burp", 0.5f, world.random.nextFloat() * 0.1f + 0.9f)
		onFoodEaten(stack, world, player)
		return stack
	}

	/** `onFoodEaten` 1.7.10; зелье еды (`setPotionEffect`) — по мере надобности */
	protected open fun onFoodEaten(stack: ItemStack, world: Level, player: Player) = Unit
}
