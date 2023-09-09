package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ItemTwigWand

open class TileDoubleCamo: ASJTile() {
	
	var blockBottom = Blocks.log!!
	var blockBottomMeta = 0
	var blockTop = Blocks.planks!!
	var blockTopMeta = 0
	
	var locked = false
	
	override fun updateEntity() {
		if (locked || ASJUtilities.isServer || mc.thePlayer.heldItem?.item !is ItemTwigWand) return
		
		Botania.proxy.setWispFXDepthTest(false)
		Botania.proxy.wispFX(worldObj, xCoord + 0.5, yCoord + 1.0, zCoord + 0.5, 1f, 0f, 0f, 0.5f, -0.01f)
		Botania.proxy.setWispFXDepthTest(true)
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setString(TAG_BLOCK_BOTTOM, GameRegistry.findUniqueIdentifierFor(blockBottom).toString())
		nbt.setInteger(TAG_BLOCK_BOTTOM_META, blockBottomMeta)
		nbt.setString(TAG_BLOCK_TOP, GameRegistry.findUniqueIdentifierFor(blockTop).toString())
		nbt.setInteger(TAG_BLOCK_TOP_META, blockTopMeta)
		nbt.setBoolean(TAG_LOCKED, locked)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey(TAG_BLOCK_BOTTOM)) blockBottom = Block.getBlockFromName(nbt.getString(TAG_BLOCK_BOTTOM)) ?: Blocks.log
		if (nbt.hasKey(TAG_BLOCK_BOTTOM_META)) blockBottomMeta = nbt.getInteger(TAG_BLOCK_BOTTOM_META)
		if (nbt.hasKey(TAG_BLOCK_TOP)) blockTop = Block.getBlockFromName(nbt.getString(TAG_BLOCK_TOP)) ?: Blocks.planks
		if (nbt.hasKey(TAG_BLOCK_TOP_META)) blockTopMeta = nbt.getInteger(TAG_BLOCK_TOP_META)
		if (nbt.hasKey(TAG_LOCKED)) locked = nbt.getBoolean(TAG_LOCKED)
	}
	
	companion object {
		const val TAG_BLOCK_BOTTOM = "blockBottom"
		const val TAG_BLOCK_BOTTOM_META = "blockBottomMeta"
		const val TAG_BLOCK_TOP = "blockTop"
		const val TAG_BLOCK_TOP_META = "blockTopMeta"
		const val TAG_LOCKED = "locked"
	}
}