package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.MaterialPublic
import alfheim.api.*
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileAnomaly
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemBlockAnomaly
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.client.particle.EffectRenderer
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.*
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.ItemNBTHelper.getNBT
import java.awt.Color
import java.util.*

class BlockAnomaly: BlockContainerMod(anomaly), ILexiconable {
	
	init {
		setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f)
		setBlockName("Anomaly")
		setBlockUnbreakable()
		setCreativeTab(AlfheimTab)
		setLightLevel(1f)
		setLightOpacity(0)
		setStepSound(Block.soundTypeCloth)
	}
	
	override fun shouldRegisterInNameSet() = false
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockAnomaly::class.java, name)
		return super.setBlockName(name)
	}
	
	override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase?, stack: ItemStack?) {
		val player = placer as? EntityPlayer ?: return
		if (!player.capabilities.isCreativeMode) return
		
		val te = world.getTileEntity(x, y, z) as? TileAnomaly ?: return
		te.readCustomNBT(getNBT(stack))
		te.lock(x, y, z, world.provider.dimensionId)
		
		if (world.isRemote)
			return
		
		world.markBlockForUpdate(x, y, z)
		ASJUtilities.dispatchTEToNearbyPlayers(te)
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		return (world.getTileEntity(x, y, z) as TileAnomaly).onActivated(player!!.currentEquippedItem, player, world, x, y, z)
	}
	
	override fun getSubBlocks(block: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (name in AlfheimAPI.anomalies.keys)
			list.add(ItemBlockAnomaly.ofType(name))
	}
	
	@Suppress("OVERRIDE_DEPRECATION") // stupid WAILA uses deprecated method -_-
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int): ItemStack {
		return getPickBlock(target, world, x, y, z, null)
	}
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer?): ItemStack {
		val anomaly = ItemStack(AlfheimBlocks.anomaly)
		(world.getTileEntity(x, y, z) as TileAnomaly).writeCustomNBT(getNBT(anomaly))
		return anomaly
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		iconUndefined = reg.registerIcon(ModInfo.MODID + ":undefined")
		blockIcon = iconUndefined
	}
	
	override fun addHitEffects(world: World, mop: MovingObjectPosition, er: EffectRenderer?): Boolean {
		val tile = world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) as? TileAnomaly ?: return true
		
		var color = AlfheimAPI.getAnomaly(tile.subTileName).color
		if (color == -1) color = Color.HSBtoRGB(Math.random().F, 1F, 1F)
		val (r, g, b) = Color(color).getRGBColorComponents(null)
		
		Botania.proxy.sparkleFX(world, mop.blockX + Math.random(), mop.blockY + Math.random(), mop.blockZ + Math.random(), r, g, b, 2f, 5)
		
		return true
	}
	
	override fun addDestroyEffects(world: World, x: Int, y: Int, z: Int, meta: Int, er: EffectRenderer?): Boolean {
		repeat(32) {
			addHitEffects(world, MovingObjectPosition(x, y, z, 0, Vec3(0, 0, 0)), er)
		}
		
		return true
	}
	
	override fun getBlocksMovement(world: IBlockAccess?, x: Int, y: Int, z: Int) = false
	override fun getExplosionResistance(entity: Entity?) = Float.MAX_VALUE / 3f
	override fun createNewTileEntity(world: World, meta: Int) = TileAnomaly()
	override fun getCollisionBoundingBoxFromPool(world: World?, x: Int, y: Int, z: Int) = null
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
	override fun getRenderType() = -1
	override fun getItemDropped(meta: Int, rand: Random?, fortune: Int) = null
	override fun quantityDropped(random: Random?) = 0
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.anomaly
	
	companion object {
		
		val anomaly = MaterialPublic(MapColor.airColor, blocksLight = false, opaque = false, solid = false).setImmovableMobility()
		lateinit var iconUndefined: IIcon
	}
}