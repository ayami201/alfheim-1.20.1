package alexsocol.asjlib.extendables.block

// PORT: импорты 1.20.1 (блок-сущность 1.7.10 — alfheim.port.legacy.TileEntity, MAPPING.md)
import alfheim.port.legacy.TileEntity
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.Connection as NetworkManager
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket as S35PacketUpdateTileEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

// PORT: блок-сущность 1.20.1 создаётся сразу в своей точке и со своим типом (alfheim.port.legacy.TileEntity)
open class ASJTile(type: BlockEntityType<*>, pos: BlockPos, state: BlockState): TileEntity(type, pos, state) {
//open class ASJTile: TileEntity() {
	
	override fun writeToNBT(nbt: NBTTagCompound) {
		super.writeToNBT(nbt)
		writeCustomNBT(nbt)
	}
	
	override fun readFromNBT(nbt: NBTTagCompound) {
		super.readFromNBT(nbt)
		readCustomNBT(nbt)
	}
	
	open fun writeCustomNBT(nbt: NBTTagCompound) = Unit
	
	open fun readCustomNBT(nbt: NBTTagCompound) = Unit
	
	// PORT: пакет описания 1.7.10 (getDescriptionPacket) — данные для клиента 1.20.1: те же данные идут с чанком
	// (getUpdateTag) и при обновлении блока (getUpdatePacket)
	override fun getUpdateTag() = NBTTagCompound().also(::writeCustomNBT)
	
	override fun getUpdatePacket(): S35PacketUpdateTileEntity = S35PacketUpdateTileEntity.create(this)
//	override fun getDescriptionPacket(): Packet {
//		val nbt = NBTTagCompound()
//		writeCustomNBT(nbt)
//		return S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, blockMetadata, nbt)
//	}
	
	// PORT: данные для клиента 1.20.1 приходят и с чанком (handleUpdateTag), и пакетом (onDataPacket). onDataPacket 1.7.10
	// по умолчанию ничего не делал, 1.20.1 — загружает тег целиком (load): super не зовётся, чтобы не читать его дважды
	override fun handleUpdateTag(tag: NBTTagCompound) = readCustomNBT(tag)
	
	override fun onDataPacket(net: NetworkManager, packet: S35PacketUpdateTileEntity) {
		readCustomNBT(packet.tag ?: return)
//		super.onDataPacket(net, packet)
//		readCustomNBT(packet.func_148857_g())
	}
}