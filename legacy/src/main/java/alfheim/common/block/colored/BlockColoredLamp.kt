package alfheim.common.block.colored

import alexsocol.asjlib.ASJUtilities
import alfheim.client.core.helper.InterpolatedIconHelper
import alfheim.common.block.base.BlockMod
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import vazkii.botania.api.lexicon.ILexiconable
import java.awt.Color

/**
 * @author WireSegal
 * Created at 4:44 PM on 1/12/16.
 */
class BlockColoredLamp: BlockMod(Material.redstoneLight), ILexiconable {
	
	var rainbowIcon: IIcon? = null
	
	init {
		setBlockName("irisLamp")
		setStepSound(soundTypeGlass)
		setHardness(0.3f)
		if (ASJUtilities.isClient)
			MinecraftForge.EVENT_BUS.register(this)
		
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	override fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0)
			rainbowIcon = InterpolatedIconHelper.forBlock(event.map, this, "RB")
	}
	
	override fun getIcon(side: Int, meta: Int) = if (meta > 14) rainbowIcon else blockIcon
	
	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) {
		super.onNeighborBlockChange(world, x, y, z, block)
		val lvl = world.getStrongestIndirectPower(x, y, z)
		if (world.getBlockMetadata(x, y, z) != lvl)
			world.setBlockMetadataWithNotify(x, y, z, lvl, 3)
	}
	
	override fun onBlockAdded(world: World, x: Int, y: Int, z: Int) {
		super.onBlockAdded(world, x, y, z)
		val lvl = world.getStrongestIndirectPower(x, y, z)
		if (world.getBlockMetadata(x, y, z) != lvl)
			world.setBlockMetadataWithNotify(x, y, z, lvl, 3)
	}
	
	override fun damageDropped(meta: Int) = 0
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer) = ItemStack(this, 1, 0)
	
	override fun createStackedBlock(meta: Int) = ItemStack(this)
	
	override fun getLightValue() = 0
	
	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int): Int {
		val lvl = world.getBlockMetadata(x, y, z)
		if (Loader.isModLoaded("easycoloredlights")) {
			return if (lvl == 15) 0xFFFFFF else powerColor(lvl)
		}
		return if (lvl > 0) 15 else 0
	}
	
	@SideOnly(Side.CLIENT)
	override fun getRenderColor(meta: Int): Int = powerColor(meta)
	
	@SideOnly(Side.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int): Int = getRenderColor(world.getBlockMetadata(x, y, z))
	
	fun powerColor(power: Int): Int {
		if (power == 0) return 0x161616
		else if (power == 15) return 0xFFFFFF
		return Color.HSBtoRGB((power - 1) / 16F, 1F, 1F)
	}
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.lamp
}
