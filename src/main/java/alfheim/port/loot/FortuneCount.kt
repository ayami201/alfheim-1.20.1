package alfheim.port.loot

import alfheim.port.registry.AlfheimRegisters
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import net.minecraft.util.GsonHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import net.minecraft.world.level.storage.loot.parameters.LootContextParam
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraftforge.registries.RegistryObject

/**
 * Функция лута `alfheim:fortune_count`: вещей — уровень удачи инструмента + [add]. Так считал `getDrops` автора
 * (`fortune + 1` у руды Нифльхейма); у функций лута ванилы 1.20.1 прибавка от удачи случайная. Без инструмента или без
 * удачи (взрыв, рог Botania) — [add]: удачу 1.7.10 брал только у инструмента игрока
 */
class FortuneCount(conditions: Array<LootItemCondition>, val add: Int): LootItemConditionalFunction(conditions) {

	override fun getType(): LootItemFunctionType = TYPE.get()

	override fun getReferencedContextParams(): Set<LootContextParam<*>> = super.getReferencedContextParams() + LootContextParams.TOOL

	override fun run(stack: ItemStack, context: LootContext): ItemStack {
		val tool = context.getParamOrNull(LootContextParams.TOOL)
		stack.count = (if (tool == null) 0 else EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool)) + add
		return stack
	}

	class Serializer: LootItemConditionalFunction.Serializer<FortuneCount>() {

		override fun serialize(json: JsonObject, function: FortuneCount, context: JsonSerializationContext) {
			super.serialize(json, function, context)
			json.addProperty("add", function.add)
		}

		override fun deserialize(json: JsonObject, context: JsonDeserializationContext, conditions: Array<LootItemCondition>) =
			FortuneCount(conditions, GsonHelper.getAsInt(json, "add", 0))
	}

	companion object {

		val TYPE: RegistryObject<LootItemFunctionType> = AlfheimRegisters.LOOT_FUNCTION_TYPES.register("fortune_count") { LootItemFunctionType(Serializer()) }

		/** Для таблиц генерации данных */
		fun fortuneCount(add: Int): Builder<*> = simpleBuilder { FortuneCount(it, add) }
	}
}
