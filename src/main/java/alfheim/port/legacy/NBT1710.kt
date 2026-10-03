package alfheim.port.legacy

import net.minecraft.nbt.CompoundTag
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
