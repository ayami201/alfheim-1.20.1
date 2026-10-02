package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ItemTwigWand
import kotlin.math.max

open class TileDoubleCamo: ASJTile() {
	
	/**
	 * For the fuck's sake, why can't there just be a tile when Block#getDrops -_-
	 * Mojang, I hate you sooo much
	 */
	var noDrop = false
	var blockBottom = Blocks.log!!
	var blockBottomMeta = 0
	var blockTop = Blocks.planks!!
	var blockTopMeta = 0
	
	var locked = false
	
	var light = 0
	var lightCheckAntiStackOverflow = false
	
	override fun updateEntity() {
		if (locked || ASJUtilities.isServer || mc.thePlayer.heldItem?.item !is ItemTwigWand) return
		
		Botania.proxy.setWispFXDepthTest(false)
		Botania.proxy.wispFX(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 1f, 0f, 0f, 0.5f, -0.01f)
		Botania.proxy.setWispFXDepthTest(true)
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setString(TAG_BLOCK_BOTTOM, GameRegistry.findUniqueIdentifierFor(blockBottom).toString())
		nbt.setInteger(TAG_BLOCK_BOTTOM_META, blockBottomMeta)
		nbt.setString(TAG_BLOCK_TOP, GameRegistry.findUniqueIdentifierFor(blockTop).toString())
		nbt.setInteger(TAG_BLOCK_TOP_META, blockTopMeta)
		nbt.setBoolean(TAG_LOCKED, locked)
		nbt.setBoolean(TAG_NO_DROP, noDrop)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey(TAG_BLOCK_BOTTOM)) blockBottom = Block.getBlockFromName(nbt.getString(TAG_BLOCK_BOTTOM)) ?: Blocks.log
		if (nbt.hasKey(TAG_BLOCK_BOTTOM_META)) blockBottomMeta = nbt.getInteger(TAG_BLOCK_BOTTOM_META)
		if (nbt.hasKey(TAG_BLOCK_TOP)) blockTop = Block.getBlockFromName(nbt.getString(TAG_BLOCK_TOP)) ?: Blocks.planks
		if (nbt.hasKey(TAG_BLOCK_TOP_META)) blockTopMeta = nbt.getInteger(TAG_BLOCK_TOP_META)
		if (nbt.hasKey(TAG_LOCKED)) locked = nbt.getBoolean(TAG_LOCKED)
		if (nbt.hasKey(TAG_NO_DROP)) noDrop = nbt.getBoolean(TAG_NO_DROP)
		
		recalculateLight()
	}
	
	override fun onDataPacket(net: NetworkManager?, packet: S35PacketUpdateTileEntity) {
		super.onDataPacket(net, packet)
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
	}
	
	fun getLightValue(): Int {
		if (light == -1 && !lightCheckAntiStackOverflow) {
			light = calculateLight()
			worldObj?.func_147451_t(xCoord, yCoord, zCoord)
		}
		
		return light
	}
	
	private fun calculateLight(): Int {
		var max: Int
		
		lightCheckAntiStackOverflow = true
		
		val oldMeta = getBlockMetadata()
		
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, blockTopMeta, 4)
		val topLight = blockTop.getLightValue(worldObj, xCoord, yCoord, zCoord)
		max = if (topLight > 0) topLight else blockTop.lightValue
		
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, blockBottomMeta, 4)
		val botLight = blockBottom.getLightValue(worldObj, xCoord, yCoord, zCoord)
		max = max(max, if (botLight > 0) botLight else blockBottom.lightValue)
		
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, oldMeta, 4)
		
		lightCheckAntiStackOverflow = false
		
		return max
	}
	
	fun recalculateLight() {
		if (worldObj != null) {
			light = -1
			light = calculateLight()
			worldObj.func_147451_t(xCoord, yCoord, zCoord)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
		}
	}
	
	companion object {
		const val TAG_BLOCK_BOTTOM = "blockBottom"
		const val TAG_BLOCK_BOTTOM_META = "blockBottomMeta"
		const val TAG_BLOCK_TOP = "blockTop"
		const val TAG_BLOCK_TOP_META = "blockTopMeta"
		const val TAG_LOCKED = "locked"
		const val TAG_NO_DROP = "noDrop"
	}
}