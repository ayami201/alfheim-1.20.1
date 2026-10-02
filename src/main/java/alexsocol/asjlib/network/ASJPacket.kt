package alexsocol.asjlib.network

import alexsocol.asjlib.I
// PORT: IMessage и ByteBufUtils (FML 1.7.10) → FriendlyByteBuf 1.20.1; NBTTagCompound → CompoundTag
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.item.ItemStack
import sun.misc.Unsafe
import java.lang.reflect.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Auto-completable packet. **Doesn't** require:<br></br>
 *
 *  * default constructor
 *  * fromBytes implementation
 *  * toBytes implementation
 *
 */
abstract class ASJPacket {
	
	// PORT: в 1.7.10 конструктор без аргументов, fromBytes и toBytes каждому пакету дописывал coremod ASJCore
	// (ASJPacketCompleter): сначала fromCustomBytes / toCustomBytes, потом поля самого класса пакета — не static,
	// не final, поддерживаемых типов, в порядке объявления. Coremod в 1.20.1 нет, то же делают эти методы
	// отражением: autoFields и create ниже
	open fun fromBytes(buf: ByteBuf) {
		fromCustomBytes(buf)
		for (field in autoFields(javaClass)) field.set(this, read(buf, field.type))
	}
	
	open fun toBytes(buf: ByteBuf) {
		toCustomBytes(buf)
		for (field in autoFields(javaClass)) write(buf, field.type, field.get(this))
	}
	
	open fun fromCustomBytes(buf: ByteBuf) {}
	
	open fun toCustomBytes(buf: ByteBuf) {}
	
	companion object {
		
		@JvmStatic
		fun write(buf: ByteBuf, w: Boolean) {
			buf.writeBoolean(w)
		}
		
		@JvmStatic
		fun write(buf: ByteBuf, w: Byte) {
			buf.writeByte(w.I)
		}
		
		@JvmStatic
		fun write(buf: ByteBuf, w: Char) {
			buf.writeChar(w.code)
		}
		
		@JvmStatic
		fun write(buf: ByteBuf, w: Double) {
			buf.writeDouble(w)
		}
		
		@JvmStatic
		fun write(buf: ByteBuf, w: Float) {
			buf.writeFloat(w)
		}
		
		@JvmStatic
		fun write(buf: ByteBuf, w: Int) {
			buf.writeInt(w)
		}
		
		@JvmStatic
		fun write(buf: ByteBuf, w: Long) {
			buf.writeLong(w)
		}
		
		@JvmStatic
		fun write(buf: ByteBuf, w: Short) {
			buf.writeShort(w.I)
		}
		
		@JvmStatic
		fun write(buf: ByteBuf, w: String?) {
			FriendlyByteBuf(buf).writeUtf(w)
		}
		
		// PORT: в 1.20.1 пустой предмет — ItemStack.EMPTY, а не null
		@JvmStatic
		fun write(buf: ByteBuf, w: ItemStack?) {
			FriendlyByteBuf(buf).writeItem(w ?: ItemStack.EMPTY)
		}
		
		@JvmStatic
		fun write(buf: ByteBuf, w: CompoundTag?) {
			FriendlyByteBuf(buf).writeNbt(w)
		}
		
		@JvmStatic
		fun readZ(buf: ByteBuf): Boolean {
			return buf.readBoolean()
		}
		
		@JvmStatic
		fun readB(buf: ByteBuf): Byte {
			return buf.readByte()
		}
		
		@JvmStatic
		fun readC(buf: ByteBuf): Char {
			return buf.readChar()
		}
		
		@JvmStatic
		fun readD(buf: ByteBuf): Double {
			return buf.readDouble()
		}
		
		@JvmStatic
		fun readF(buf: ByteBuf): Float {
			return buf.readFloat()
		}
		
		@JvmStatic
		fun readI(buf: ByteBuf): Int {
			return buf.readInt()
		}
		
		@JvmStatic
		fun readJ(buf: ByteBuf): Long {
			return buf.readLong()
		}
		
		@JvmStatic
		fun readS(buf: ByteBuf): Short {
			return buf.readShort()
		}
		
		@JvmStatic
		fun readLjavalangString(buf: ByteBuf): String? {
			return FriendlyByteBuf(buf).readUtf()
		}
		
		@JvmStatic
		fun readLnetminecraftitemItemStack(buf: ByteBuf): ItemStack? {
			return FriendlyByteBuf(buf).readItem()
		}
		
		@JvmStatic
		fun readLnetminecraftnbtNBTTagCompound(buf: ByteBuf): CompoundTag? {
			return FriendlyByteBuf(buf).readNbt()
		}
		
		// PORT: дальше — замена ASJPacketCompleter (см. fromBytes)
		
		/** Типы полей, которые ASJPacketCompleter читал и писал сам */
		private val autoTypes = setOf(java.lang.Boolean.TYPE, java.lang.Byte.TYPE, java.lang.Character.TYPE, java.lang.Double.TYPE, java.lang.Float.TYPE, java.lang.Integer.TYPE, java.lang.Long.TYPE, java.lang.Short.TYPE, String::class.java, ItemStack::class.java, CompoundTag::class.java)
		
		private val autoFields = ConcurrentHashMap<Class<*>, List<Field>>()
		
		/** Поля класса пакета, которые сериализуются сами. Порядок — как в файле класса: в нём их обходил ASJPacketCompleter, в нём же их отдаёт getDeclaredFields у HotSpot */
		private fun autoFields(clazz: Class<*>) = autoFields.getOrPut(clazz) {
			clazz.declaredFields.filter { !Modifier.isStatic(it.modifiers) && !Modifier.isFinal(it.modifiers) && it.type in autoTypes }.onEach { it.isAccessible = true }
		}
		
		private fun read(buf: ByteBuf, type: Class<*>): Any? = when (type) {
			java.lang.Boolean.TYPE   -> readZ(buf)
			java.lang.Byte.TYPE      -> readB(buf)
			java.lang.Character.TYPE -> readC(buf)
			java.lang.Double.TYPE    -> readD(buf)
			java.lang.Float.TYPE     -> readF(buf)
			java.lang.Integer.TYPE   -> readI(buf)
			java.lang.Long.TYPE      -> readJ(buf)
			java.lang.Short.TYPE     -> readS(buf)
			String::class.java       -> readLjavalangString(buf)
			ItemStack::class.java    -> readLnetminecraftitemItemStack(buf)
			else                     -> readLnetminecraftnbtNBTTagCompound(buf)
		}
		
		private fun write(buf: ByteBuf, type: Class<*>, value: Any?) = when (type) {
			java.lang.Boolean.TYPE   -> write(buf, value as Boolean)
			java.lang.Byte.TYPE      -> write(buf, value as Byte)
			java.lang.Character.TYPE -> write(buf, value as Char)
			java.lang.Double.TYPE    -> write(buf, value as Double)
			java.lang.Float.TYPE     -> write(buf, value as Float)
			java.lang.Integer.TYPE   -> write(buf, value as Int)
			java.lang.Long.TYPE      -> write(buf, value as Long)
			java.lang.Short.TYPE     -> write(buf, value as Short)
			String::class.java       -> write(buf, value as String?)
			ItemStack::class.java    -> write(buf, value as ItemStack?)
			else                     -> write(buf, value as CompoundTag?)
		}
		
		private val unsafe = Unsafe::class.java.getDeclaredField("theUnsafe").apply { isAccessible = true }.get(null) as Unsafe
		
		/** Пакет для чтения из сети. Как конструктор без аргументов от ASJPacketCompleter: поля не инициализируются, их заполнит fromBytes */
		@JvmStatic
		fun <T: ASJPacket> create(clazz: Class<T>): T = clazz.cast(unsafe.allocateInstance(clazz))
	}
}
