package alfheim.common.block

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.client.core.helper.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.*
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.common.lexicon.LexiconData

class BlockAlfStorage: BlockModMeta(Material.iron, 6, ModInfo.MODID, "alfStorage", AlfheimTab, 5f, resist = 60f), ILexiconable {
	
	init {
		if (ASJUtilities.isClient)
			MinecraftForge.EVENT_BUS.register(this)
	}
	
	override fun isBeaconBase(worldObj: IBlockAccess?, x: Int, y: Int, z: Int, beaconX: Int, beaconY: Int, beaconZ: Int) = true
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) =
		when (world.getBlockMetadata(x, y, z)) {
			0       -> AlfheimLexiconData.elvorium
			in 1..3 -> AlfheimLexiconData.essences
			4       -> LexiconData.gaiaRitualHardmode
			5       -> LexiconData.pool
//			6       -> RecipeListAB.advandedAgglomerationPlate TODO back
			else    -> null
		}
	
	fun isInterpolated(meta: Int) = meta == 4
	
	fun hasNewTexture(meta: Int) = AlfheimConfigHandler.newStorageTexture && (meta == 1 || meta == 2 || meta == 3)
	
	@Suppress("UNCHECKED_CAST")
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = arrayOfNulls<IIcon?>(subtypes) as Array<IIcon>
		
		repeat(subtypes) {
			if (!isInterpolated(it))
				icons[it] = IconHelper.forBlock(reg, this, "${if (hasNewTexture(it)) "New" else ""}$it")
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType != 0) return
		
		repeat(subtypes) {
			if (isInterpolated(it))
				icons[it] = InterpolatedIconHelper.forBlock(event.map, this, it)
		}
	}
}