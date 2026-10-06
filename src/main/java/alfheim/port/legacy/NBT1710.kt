package alfheim.port.legacy

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

/*
 * Методы `NBTTagCompound` 1.7.10 у `CompoundTag` 1.20.1 (SPEC, Р-4; MAPPING.md, «Прослойка `alfheim.port.legacy`»):
 * смысл тот же, метод только переименован. NBT существа (`entityData` 1.7.10) — `persistentData` Forge 1.20.1.
 */

fun CompoundTag.hasKey(key: String) = contains(key)

fun CompoundTag.hasNoTags() = isEmpty

fun CompoundTag.removeTag(key: String) = remove(key)

fun CompoundTag.setTag(key: String, tag: Tag) {
	put(key, tag)
}

fun CompoundTag.getCompoundTag(key: String): CompoundTag = getCompound(key)

fun CompoundTag.setString(key: String, value: String) = putString(key, value)

fun CompoundTag.setInteger(key: String, value: Int) = putInt(key, value)

fun CompoundTag.getInteger(key: String) = getInt(key)

fun CompoundTag.setDouble(key: String, value: Double) = putDouble(key, value)

fun CompoundTag.setFloat(key: String, value: Float) = putFloat(key, value)

fun CompoundTag.setBoolean(key: String, value: Boolean) = putBoolean(key, value)

/** `getTagList(key, type)` 1.7.10: список тегов вида [type] (`Constants.NBT`); нет такого списка — пустой */
fun CompoundTag.getTagList(key: String, type: Int): ListTag = getList(key, type)

fun ListTag.tagCount() = size

fun ListTag.getStringTagAt(index: Int): String = getString(index)

fun ListTag.appendTag(tag: Tag) {
	add(tag)
}

/** `new NBTTagString(value)` 1.7.10; в 1.20.1 строковый тег создаёт `StringTag.valueOf` */
fun NBTTagString(value: String): StringTag = StringTag.valueOf(value)

/** `Constants` Forge 1.7.10 */
object Constants {

	/** Номера видов тегов NBT — те же, что `Tag.TAG_…` 1.20.1 */
	object NBT {
		const val TAG_END = 0
		const val TAG_BYTE = 1
		const val TAG_SHORT = 2
		const val TAG_INT = 3
		const val TAG_LONG = 4
		const val TAG_FLOAT = 5
		const val TAG_DOUBLE = 6
		const val TAG_BYTE_ARRAY = 7
		const val TAG_STRING = 8
		const val TAG_LIST = 9
		const val TAG_COMPOUND = 10
		const val TAG_INT_ARRAY = 11
		const val TAG_ANY_NUMERIC = 99
	}
}
