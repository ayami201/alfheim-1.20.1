package alexsocol.asjlib

// PORT: NBTTagCompound → CompoundTag, NBTTagList → ListTag, методы setX → putX (MAPPING.md); смысл тот же
import net.minecraft.nbt.*
import net.minecraft.world.item.ItemStack

/** @author Vazkii
 */
@Suppress("unused")
object ItemNBTHelper {
	
	/** Checks if an ItemStack has a Tag Compound  */
	fun detectNBT(stack: ItemStack): Boolean {
		return stack.hasTag()
	}
	
	/** Injects an NBT Tag Compound to an ItemStack, no checks
	 * are made previously  */
	fun injectNBT(stack: ItemStack, nbt: CompoundTag?) {
		stack.tag = nbt
	}
	
	/** Tries to initialize an NBT Tag Compound in an ItemStack,
	 * this will not do anything if the stack already has a tag
	 * compound  */
	fun initNBT(stack: ItemStack) {
		if (!detectNBT(stack)) injectNBT(stack, CompoundTag())
	}
	
	/** Gets the NBTTagCompound in an ItemStack. Tries to init it
	 * previously in case there isn't one present  */
	fun getNBT(stack: ItemStack): CompoundTag {
		initNBT(stack)
		return stack.tag!!
	}
	
	// SETTERS ///////////////////////////////////////////////////////////////////
	fun setBoolean(stack: ItemStack, tag: String, b: Boolean) {
		getNBT(stack).putBoolean(tag, b)
	}
	
	fun setByte(stack: ItemStack, tag: String, b: Byte) {
		getNBT(stack).putByte(tag, b)
	}
	
	fun setByteArray(stack: ItemStack, tag: String, array: ByteArray) {
		getNBT(stack).putByteArray(tag, array)
	}
	
	fun setShort(stack: ItemStack, tag: String, s: Short) {
		getNBT(stack).putShort(tag, s)
	}
	
	fun setInt(stack: ItemStack, tag: String, i: Int) {
		getNBT(stack).putInt(tag, i)
	}
	
	fun setIntArray(stack: ItemStack, tag: String, array: IntArray) {
		getNBT(stack).putIntArray(tag, array)
	}
	
	fun setLong(stack: ItemStack, tag: String, l: Long) {
		getNBT(stack).putLong(tag, l)
	}
	
	fun setFloat(stack: ItemStack, tag: String, f: Float) {
		getNBT(stack).putFloat(tag, f)
	}
	
	fun setDouble(stack: ItemStack, tag: String, d: Double) {
		getNBT(stack).putDouble(tag, d)
	}
	
	fun setCompound(stack: ItemStack, tag: String, cmp: CompoundTag?) {
		// PORT: в 1.20.1 зачарования лежат в теге "Enchantments", а не "ench"
		if (!tag.equals("Enchantments", ignoreCase = true)) // not override the enchantments
			getNBT(stack).put(tag, cmp)
	}
	
	fun setString(stack: ItemStack, tag: String, s: String) {
		getNBT(stack).putString(tag, s)
	}
	
	fun setList(stack: ItemStack, tag: String, list: ListTag?) {
		getNBT(stack).put(tag, list)
	}
	
	// GETTERS ///////////////////////////////////////////////////////////////////
	fun verifyExistance(stack: ItemStack?, tag: String): Boolean {
		return stack?.tag?.contains(tag) == true
	}
	
	fun getBoolean(stack: ItemStack?, tag: String, defaultExpected: Boolean): Boolean {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getBoolean(tag) else defaultExpected
	}
	
	fun getByte(stack: ItemStack?, tag: String, defaultExpected: Byte): Byte {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getByte(tag) else defaultExpected
	}
	
	fun getByteArray(stack: ItemStack?, tag: String, defaultExpected: ByteArray = ByteArray(0)): ByteArray {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getByteArray(tag) else defaultExpected
	}
	
	fun getShort(stack: ItemStack?, tag: String, defaultExpected: Short): Short {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getShort(tag) else defaultExpected
	}
	
	fun getInt(stack: ItemStack?, tag: String, defaultExpected: Int): Int {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getInt(tag) else defaultExpected
	}
	
	fun getIntArray(stack: ItemStack?, tag: String, defaultExpected: IntArray = IntArray(0)): IntArray {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getIntArray(tag) else defaultExpected
	}
	
	fun getLong(stack: ItemStack?, tag: String, defaultExpected: Long): Long {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getLong(tag) else defaultExpected
	}
	
	fun getFloat(stack: ItemStack?, tag: String, defaultExpected: Float): Float {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getFloat(tag) else defaultExpected
	}
	
	fun getDouble(stack: ItemStack?, tag: String, defaultExpected: Double): Double {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getDouble(tag) else defaultExpected
	}
	
	fun getCompound(stack: ItemStack?, tag: String): CompoundTag {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getCompound(tag) else CompoundTag()
	}
	
	/** If nullifyOnFail is true it'll return null if it doesn't find any
	 * compounds, otherwise it'll return a new one.  */
	fun getCompound(stack: ItemStack?, tag: String, nullifyOnFail: Boolean): CompoundTag? {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getCompound(tag) else if (nullifyOnFail) null else CompoundTag()
	}
	
	fun getString(stack: ItemStack?, tag: String, defaultExpected: String): String {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getString(tag) else defaultExpected
	}
	
	fun getList(stack: ItemStack?, tag: String, objtype: Int): ListTag {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getList(tag, objtype) else ListTag()
	}
	
	/** If nullifyOnFail is true it'll return null if it doesn't find any
	 * compounds, otherwise it'll return a new one.  */
	fun getList(stack: ItemStack?, tag: String, objtype: Int, nullifyOnFail: Boolean): ListTag? {
		return if (verifyExistance(stack, tag)) getNBT(stack!!).getList(tag, objtype) else if (nullifyOnFail) null else ListTag()
	}
}