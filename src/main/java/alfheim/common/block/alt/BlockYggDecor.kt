package alfheim.common.block.alt

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import cpw.mods.fml.common.IFuelHandler
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection

class BlockYggDecor: BlockModMeta(Material.wood, 3, ModInfo.MODID, "Wisdomwood", AlfheimTab, 100f, resist = 1000f, folder = "decor/"), IFuelHandler {
	
	lateinit var topIcon: IIcon
	
	override fun registerBlockIcons(reg: IIconRegister) {
		super.registerBlockIcons(reg)
		
		topIcon = reg.registerIcon("$modid:$folder${name}1Top")
	}
	
	override fun getIcon(side: Int, meta: Int) = if (meta == 1 && side in 0..1) topIcon else super.getIcon(side, meta)
	
	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = false
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) Int.MAX_VALUE / 13 / 4 else 0
}
