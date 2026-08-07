package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.*
import alfheim.api.ModInfo
import alfheim.common.block.*
import alfheim.common.world.data.CustomWorldData
import alfheim.common.world.data.CustomWorldData.Companion.customData
import net.minecraft.nbt.*
import net.minecraft.util.EnumChatFormatting
import java.util.*

class TileKudzuVine: ASJTile() {
	
	var coreUUID = EMPTY_UUID
	var mutations = 0
		set(value) {
			field = value
			worldObj?.markBlockForUpdate(xCoord, yCoord, zCoord)
		}
	var summed = false
	
	override fun canUpdate() = false
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setBoolean("summed", summed)
		nbt.setInteger(TAG_MUTATIONS, mutations)
		nbt.setLong(TAG_UUID_MOST, coreUUID.mostSignificantBits)
		nbt.setLong(TAG_UUID_LEAST, coreUUID.leastSignificantBits)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		summed = nbt.getBoolean("summed")
		mutations = nbt.getInteger(TAG_MUTATIONS)
		
		val uuid = UUID(nbt.getLong(TAG_UUID_MOST), nbt.getLong(TAG_UUID_LEAST))
		if (coreUUID == EMPTY_UUID && uuid != EMPTY_UUID && ASJUtilities.isServer) {
			coreUUID = uuid
			if (!summed) { // this is not called at all ???
				requireNotNull(worldObj) { ASJUtilities.sayToAllOPs("${EnumChatFormatting.DARK_RED}[${ModInfo.MODID}] Kudzu error, please check logs and report") }
				ActiveKudzus[this]?.let { ++it.size }
			}
			summed = true
		}
	}
	
	override fun validate() { // for ticking after crafting
		if (!worldObj.blockExists(xCoord, yCoord, zCoord)) return
		if (!ASJUtilities.isServer || getBlockMetadata() != 15) return
		
		worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockType(), 1)
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.rand.nextInt(BlockKudzuVine.ICON_VARS), 3)
	}
	
	fun hasMutation(m: EnumMutation) = ASJBitwiseHelper.getBit(mutations, m.ordinal)
	
	fun setMutation(m: EnumMutation, has: Boolean) {
		mutations = ASJBitwiseHelper.setBit(mutations, m.ordinal, has)
	}
	
	fun startup(mutationIds: IntArray? = null, planter: String? = null) {
		if (coreUUID != EMPTY_UUID) return
		
		mutationIds?.forEach { setMutation(EnumMutation.entries[it], true) }
		
		coreUUID = UUID.randomUUID()
		ActiveKudzus.initFor(this, planter)
		worldObj.getBlock(xCoord, yCoord, zCoord).updateTick(worldObj, xCoord, yCoord, zCoord, worldObj.rand)
		summed = true
	}
	
	companion object {
		
		private val EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")!!
		
		private const val TAG_UUID_LEAST = "uuidl"
		private const val TAG_UUID_MOST = "uuidm"
		private const val TAG_MUTATIONS = "mutations"
		
		enum class EnumMutation(val color: Int) {
			AGGRESSIVE (0x333333), // explode adjusting blocks before spreading    DONE
			EXPLOSIVE  (0xff0000), // explode on break and pass                    DONE
			TOXIC      (0xFF00FF), // poisons passers and eaters                   DONE
			FIERY      (0xFF3F34), // sets passers and breakers on fire            DONE
			HARDENED   (0x997700), // hardness x4 + sets in web                    DONE
			GLASSY     (0x8888FF), // drops gems                                   DONE
			METALLIC   (0x444444), // drops blunt metals                           DONE
			FIREPROOF  (0xff8888), // no burning                                   DONE
			THORNY     (0x666666), // damages passers and breakers                 DONE
			FLOWERING  (0x0A480D), // 10% to spawn plantera on grow                PARTIALLY TODO
			GLIMMERING (0x888800), // drops precious metals                        DONE
			SPREADING  (0xff8080), // spread radius++                              DONE
			CANNIBAL   (0xff7700), // replace other kudzu                          DONE
			WOODEN     (0x442200), // drops wood                                   DONE
			PLASTIC    (0x222288), // drops some shit                              DONE
			BLUESPACE  (0x3333ff), // can go through blocks                        DONE
		}
		
		class KudzuBrood(val data: IntArray, private val cd: CustomWorldData) {
			var size: Int
				get() = data[0]
				set(value) {
					data[0] = value
					onMod()
				}
			
			val x: Int get() = data[1]
			val y: Int get() = data[2]
			val z: Int get() = data[3]
			
			private fun onMod() = cd.markDirty()
		}
		
		object ActiveKudzus {
			
			const val TAG_ACTIVE_KUDZUS = "activeKudzus"
			
			operator fun get(tile: TileKudzuVine): KudzuBrood? {
				val cd = tile.worldObj.customData
				val activeKudzus = cd.activeKudzus
				val key = tile.coreUUID.toString()
				if (!activeKudzus.hasKey(key)) return null
				return KudzuBrood(activeKudzus.getIntArray(key), cd)
			}
			
			fun initFor(tile: TileKudzuVine, planter: String?) {
				val cd = tile.worldObj.customData
				cd.activeKudzus.setIntArray(tile.coreUUID.toString(), intArrayOf(1, tile.xCoord, tile.yCoord, tile.zCoord))
				cd.activeKudzus.setString("name" + tile.coreUUID.toString(), planter ?: "~nodata~")
				cd.markDirty()
			}
			
			fun remove(tile: TileKudzuVine) {
				val cd = tile.worldObj.customData
				cd.activeKudzus.removeTag(tile.coreUUID.toString())
				cd.activeKudzus.removeTag("name" + tile.coreUUID.toString())
				cd.markDirty()
			}
			
			val CustomWorldData.activeKudzus: NBTTagCompound
				get() = nbtData.tagMap.computeIfAbsent(TAG_ACTIVE_KUDZUS) { NBTTagCompound() } as NBTTagCompound
		}
	}
}
