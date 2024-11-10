package alfheim.common.block

import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.*
import vazkii.botania.api.lexicon.ILexiconable

class BlockAlfStorage: BlockModMeta(Material.iron, 4, ModInfo.MODID, "alfStorage", AlfheimTab, 5f, resist = 60f), ILexiconable {
	
	override fun isBeaconBase(worldObj: IBlockAccess?, x: Int, y: Int, z: Int, beaconX: Int, beaconY: Int, beaconZ: Int) = true
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) =
		when (world.getBlockMetadata(x, y, z)) {
			0       -> AlfheimLexiconData.elvorium
			in 1..3 -> AlfheimLexiconData.essences
			else    -> null
		}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		val name = name + if (AlfheimConfigHandler.newStorageTexture) "New" else ""
		
		icons = Array(subtypes) { reg.registerIcon("$modid:${if (it == 0) this.name else name}$it") }
	}
}