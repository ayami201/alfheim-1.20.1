package alfheim.common.block.tile

import alexsocol.asjlib.extendables.block.TileImmobile
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.spawn
import alfheim.api.block.tile.SubTileAnomalyBase
import alfheim.common.core.handler.ragnarok.RagnarokHandler.isProtected
import alfheim.common.item.equipment.bauble.ItemSpatiotemporalRing
import alfheim.common.world.dim.alfheim.biome.*
import net.minecraft.entity.EntityList
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import vazkii.botania.common.Botania

class TileAnomaly: TileImmobile() {
	
	var seed = 0L
	var stable = false
	var subTile: SubTileAnomalyBase? = null
	var subTileName = ""
		set(value) {
			field = value
			subTile = SubTileAnomalyBase.forName(field)
			subTile?.superTile = this
		}
	
	override fun updateEntity() {
		super.updateEntity()
		
		if (stable) return
		if (worldObj.isProtected(xCoord, yCoord, zCoord, false)) return
		
		if (seed == 0L)
			seed = worldObj.rand.nextLong()
		
		val sub = subTile ?: return
		
		val targets = sub.targets.filter {
			it !is EntityPlayer || !ItemSpatiotemporalRing.hasProtection(it)
		}.toMutableList()
		
		sub.updateEntity(targets)
		
		if (Botania.thaumcraftLoaded && worldObj.rand.nextInt(6000) == 0) spawnWisps()
	}
	
	fun spawnWisps() {
		if (worldObj.isRemote || !worldObj.getBiomeGenForCoords(xCoord, zCoord).let { it is BiomeField || it is BiomePitGiantFlowers || it is BiomeIslandForest || it is BiomePitForest }) return
		if (subTileName != "Warp" && subTileName != "Lightning") return
		
		for (i in 0..worldObj.rand.nextInt(3))
			EntityList.createEntityByName("Thaumcraft.Wisp", worldObj)?.apply {
				val (x, y, z) = Vector3.fromTileEntity(this@TileAnomaly).add(0.5)
				setPosition(x, y, z)
				spawn()
			}
	}
	
	fun onActivated(stack: ItemStack?, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Boolean {
		return subTile?.onActivated(stack, player, world, x, y, z) ?: false
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		nbt.setLong(TAG_SEED, seed)
		nbt.setBoolean(TAG_STABLE, stable)
		
		if (subTile == null) return
		
		nbt.setString(TAG_SUBTILE_NAME, subTileName)
		
		val subTag = NBTTagCompound()
		subTile!!.writeToNBT(subTag)
		nbt.setTag(TAG_SUBTILE_DATA, subTag)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		
		seed = nbt.getLong(TAG_SEED)
		stable = nbt.getBoolean(TAG_STABLE)
		
		if (!nbt.hasKey(TAG_SUBTILE_NAME)) return
		
		subTileName = nbt.getString(TAG_SUBTILE_NAME)
		subTile?.readFromNBT(nbt.getCompoundTag(TAG_SUBTILE_DATA))
	}
	
	companion object {
		const val TAG_SEED = "seed"
		const val TAG_STABLE = "stable"
		const val TAG_SUBTILE_DATA = "subTileData"
		const val TAG_SUBTILE_NAME = "subTileName"
	}
}