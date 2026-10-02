package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.helper.IconHelper
import alfheim.client.model.block.ModelSpreaderFrame
import alfheim.common.compat.AngelicaCompat
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.helper.ContributorsPrivacyHelper
import alfheim.common.integration.tinkersconstruct.TinkersConstructAlfheimConfig
import alfheim.common.lexicon.AlfheimLexiconData
import com.KAIIIAK.classManipulators.HookReplacer
import com.KAIIIAK.classManipulators.HookReplacer.Replacer.*
import gloomyfolken.hooklib.asm.*
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.texture.*
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.api.lexicon.LexiconEntry
import vazkii.botania.api.mana.BurstProperties
import vazkii.botania.client.core.proxy.ClientProxy
import vazkii.botania.client.model.ModelSpreader
import vazkii.botania.client.render.block.RenderSpreader
import vazkii.botania.client.render.tile.RenderTileSpreader
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.mana.BlockSpreader
import vazkii.botania.common.block.tile.mana.TileSpreader
import vazkii.botania.common.entity.EntityManaBurst

@Suppress("NAME_SHADOWING", "unused", "FunctionName")
object ManaSpreaderExtender {
	
	lateinit var iconGolden: IIcon
	
	var staticLebe = false
	var staticMauf = false
	
	// ######## BlockSpreader
	
	@JvmStatic
	@Hook
	fun registerBlockIcons(spreader: BlockSpreader, reg: IIconRegister) {
		iconGolden = IconHelper.forName(reg, "UberSpreaderGolden")
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
	fun getIcon(spreader: BlockSpreader, side: Int, meta: Int): IIcon? = when (meta) {
		4    -> if (isGolden()) iconGolden else ModBlocks.dreamwood.getIcon(side, 0)
//		5    -> BlockListAB.lebethron.getIcon(side, 0) TODO back
		else -> null
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun getSubBlocks(spreader: BlockSpreader, item: Item?, tabs: CreativeTabs?, list: MutableList<Any>) {
		list.add(ItemStack(item, 1, 4))
//		list.add(ItemStack(item, 1, 5)) TODO back
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
	fun getEntry(spreader: BlockSpreader, world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack): LexiconEntry? {
		return when (world.getBlockMetadata(x, y, z)) {
			4 -> AlfheimLexiconData.uberSpreader
//			5 -> RecipeListAB.lebethronSpreader TODO back
			else -> null
		}
	}
	
	// ######## TileSpreader
	
	var burstPropTile: TileSpreader? = null
	
	@JvmStatic
	@Hook
	fun getBurst(tile: TileSpreader, fake: Boolean): EntityManaBurst? {
		if (isLebe(tile) || isMauf(tile))
			burstPropTile = tile
		
		return null
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<init>")
	fun `BurstProperties$init`(bp: BurstProperties, maxMana: Int, ticksBeforeManaLoss: Int, manaLossPerTick: Float, gravity: Float, motionModifier: Float, color: Int) {
		if (burstPropTile == null) return
		
		if (isMauf(burstPropTile!!)) {
			bp.maxMana = AlfheimConfigHandler.spreaderSpeedMauf
			bp.color = 0xFFD400
			bp.ticksBeforeManaLoss = 180
			bp.manaLossPerTick = 32f
			bp.motionModifier = 3f
		} else {
			bp.maxMana = AlfheimConfigHandler.spreaderSpeedLebe
			bp.color = 0xcdd419
			bp.ticksBeforeManaLoss = 35
			bp.manaLossPerTick = AlfheimConfigHandler.spreaderSpeedLebe / 4.5f
			bp.motionModifier = 2.5f
		}
		
		burstPropTile = null
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun getMaxMana(tile: TileSpreader) = when {
		isLebe(tile)          -> AlfheimConfigHandler.spreaderCapacityLebe
		isMauf(tile)          -> AlfheimConfigHandler.spreaderCapacityMauf
		tile.isULTRA_SPREADER -> TileSpreader.ULTRA_MAX_MANA
		else                  -> TileSpreader.MAX_MANA
	}
	
//	fun isLebe(tile: TileSpreader) = if (tile.worldObj == null) staticLebe else tile.getBlockMetadata() == 5 TODO back
	fun isLebe(tile: TileSpreader) = false
	fun isMauf(tile: TileSpreader) = if (tile.worldObj == null) staticMauf else tile.getBlockMetadata() == 4
	
	@JvmStatic
	@HookReplacer(removePop = true)
	fun renderHUD(tile: TileSpreader, mc: Minecraft, res: ScaledResolution) {
		startFROM()
		ILOAD("4")
		startTO()
		getHudColor(tile, ILOAD("4"))
		stop()
	}
	
	@JvmStatic
	fun getHudColor(tile: TileSpreader, prev: Int) = if (isLebe(tile)) 0xCDD419 else if (isMauf(tile)) 0xFFD400 else prev
	
	// ######## RenderSpreader
	
	@JvmStatic
	@Hook
	fun renderInventoryBlock(render: RenderSpreader, block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks) {
		staticMauf = metadata == 4
//		staticLebe = metadata == 5 TODO back
	}
	
	// ######## RenderTileSpreader
	
	var textureHook = false
	var modelHook = false
	
	@JvmStatic
	@HookReplacer(removePop = true)
	fun renderTileEntityAt(render: RenderTileSpreader, tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
		startFROM()
		ALOAD<ResourceLocation>("10")
		startTO()
		selectTexture(tile, ALOAD("10"))
		stop()
	}
	
	@JvmStatic
	fun selectTexture(tile: TileEntity, prev: ResourceLocation): ResourceLocation = when {
		isMauf(tile as TileSpreader) -> if (isGolden())
			if (ClientProxy.dootDoot) LibResourceLocations.spreaderMaufHalloweenGolden else LibResourceLocations.spreaderMaufGolden
		else
			if (ClientProxy.dootDoot) LibResourceLocations.spreaderMaufHalloween else LibResourceLocations.spreaderMauf
		
		isLebe(tile)                 -> if (ClientProxy.dootDoot) LibResourceLocations.spreaderLebeHalloween else LibResourceLocations.spreaderLebe
		else                         -> prev
	}
	
	@JvmStatic
	@Hook(targetMethod = "renderTileEntityAt")
	fun switchModel(render: RenderTileSpreader, tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
		if (isMauf(tile as? TileSpreader ?: return))
			modelHook = !isGolden()
	}
	
	// ######## ModelSpreader
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun render(model: ModelSpreader) {
		if (!modelHook) return
		modelHook = false

		mc.renderEngine.bindTexture(LibResourceLocations.spreaderMaufFrame)
		var s = 1.15f
		val t = s - 1
		AngelicaCompat.glTranslatef(0f, -t, 0f)
		glScalef(s)
		ModelSpreaderFrame.render()
		s = 1 / s
		glScalef(s)
		AngelicaCompat.glTranslatef(0f, t, 0f)

		// core has same texture so no need to check
		mc.renderEngine.bindTexture(LibResourceLocations.spreaderMauf)
	}
	
	fun isGolden(): Boolean {
		val bakasobaka = mc.thePlayer?.let { ContributorsPrivacyHelper.isCorrect(it, "GedeonGrays") } ?: false
		val casting = AlfheimCore.TiCLoaded && !AlfheimCore.stupidMode && AlfheimConfigHandler.materialIDs[TinkersConstructAlfheimConfig.MAUFTRIUM] != -1
		
		return bakasobaka || casting
	}
}