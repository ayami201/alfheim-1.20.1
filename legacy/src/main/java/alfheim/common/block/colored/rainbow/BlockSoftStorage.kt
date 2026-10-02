package alfheim.common.block.colored.rainbow

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.client.core.helper.*
import alfheim.common.core.util.AlfheimTab
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.common.lexicon.LexiconData

class BlockSoftStorage: BlockModMeta(Material.cloth, 4, ModInfo.MODID, "softStorage", AlfheimTab, 0.4f, null, 0), ILexiconable {
	
	init {
		setStepSound(soundTypeCloth)
		if (ASJUtilities.isClient)
			MinecraftForge.EVENT_BUS.register(this)
	}
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = when (world.getBlockMetadata(x, y, z)) {
		0    -> AlfheimLexiconData.rainbowFlora
		1    -> LexiconData.elvenResources
		2    -> AlfheimLexiconData.ores
		3    -> LexiconData.gaiaRitual
		else -> null
	}
	
	fun isInterpolated(meta: Int) = meta == 0 || meta == 3
	
	@Suppress("UNCHECKED_CAST")
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = arrayOfNulls<IIcon?>(subtypes) as Array<IIcon>
		
		repeat(subtypes) {
			if (!isInterpolated(it))
				icons[it] = IconHelper.forBlock(reg, this, it)
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
