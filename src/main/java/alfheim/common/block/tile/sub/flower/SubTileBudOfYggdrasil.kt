package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.*
import alfheim.common.core.handler.*
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.subtile.*
import vazkii.botania.common.item.ItemTwigWand

class SubTileBudOfYggdrasil: SubTileFunctional() {
	
	var creative = false
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (!(creative || mana >= COST) || redstoneSignal > 0) return
		
		if (!ChunkLoadingHandler.requestChunkLoad(supertile.worldObj, supertile.xCoord shr 4, supertile.zCoord shr 4)) return
		
		if (!creative) mana -= COST
	}
	
	override fun onWanded(player: EntityPlayer?, wand: ItemStack?): Boolean {
		val result = super.onWanded(player, wand)
		if (ItemTwigWand.getBindMode(wand)) return result
		if (player?.capabilities?.isCreativeMode != true) return result
		creative = !creative
		return result
	}
	
	override fun renderHUD(mc: Minecraft?, res: ScaledResolution?) {
		var name = StatCollector.translateToLocal("tile.botania:flower.$unlocalizedName.name")
		if (creative) name += " [${StatCollector.translateToLocal("alfheimmisc.creative")}]"
		BotaniaAPI.internalHandler.drawComplexManaHUD(color, knownMana, maxMana, name, res, BotaniaAPI.internalHandler.getBindDisplayForFlowerType(this), isValidBinding)
	}
	
	override fun getIcon() = BotaniaAPI.getSignatureForName("budOfYggdrasil").getIconForStack(null)
	
	override fun readFromPacketNBT(nbt: NBTTagCompound) {
		super.readFromPacketNBT(nbt)
		creative = nbt.getBoolean(TAG_CREATIVE)
	}
	
	override fun writeToPacketNBT(nbt: NBTTagCompound) {
		super.writeToPacketNBT(nbt)
		nbt.setBoolean(TAG_CREATIVE, creative)
	}
	
	override fun acceptsRedstone() = true
	override fun getMaxMana() = 100_000
	override fun getColor() = 0xAAFF44
	override fun getRadius(): RadiusDescriptor = ChunkRadiusDescriptor(toChunkCoordinates())
	override fun getEntry() = AlfheimLexiconData.flowerBud
	
	private class ChunkRadiusDescriptor(coords: ChunkCoordinates): RadiusDescriptor(coords) {
		
		val aabb: AxisAlignedBB
		
		init {
			val (x, y, z) = coords
			val cx = x shr 4
			val cz = z shr 4
			
			aabb = getBoundingBox(cx * 16, y, cz * 16, cx * 16 + 16, y, cz * 16 + 16)
		}
		
		override fun getAABB(): AxisAlignedBB {
			return aabb
		}
	}
	
	companion object {
		const val COST = 10
		const val TAG_CREATIVE = "creative"
	}
}
