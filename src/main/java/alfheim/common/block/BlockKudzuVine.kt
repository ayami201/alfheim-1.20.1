package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.MaterialPublic
import alexsocol.asjlib.math.*
import alfheim.api.*
import alfheim.client.core.helper.*
import alfheim.common.block.base.*
import alfheim.common.block.tile.*
import alfheim.common.block.tile.TileKudzuVine.Companion.ActiveKudzus
import alfheim.common.block.tile.TileKudzuVine.Companion.EnumMutation
import alfheim.common.block.tile.TileKudzuVine.Companion.EnumMutation.*
import alfheim.common.block.tile.TileKudzuVine.Companion.KudzuBrood
import alfheim.common.core.handler.*
import alfheim.common.core.util.*
import alfheim.common.entity.EntityFlowerBud
import alfheim.common.item.material.*
import alfheim.common.lexicon.*
import alfheim.common.potion.*
import cpw.mods.fml.common.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.registry.*
import net.minecraft.block.*
import net.minecraft.block.material.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.item.*
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.item.*
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.common.*
import net.minecraftforge.common.util.*
import net.minecraftforge.event.entity.*
import net.minecraftforge.oredict.*
import vazkii.botania.api.lexicon.*
import vazkii.botania.common.item.equipment.bauble.*
import vazkii.botania.common.lib.LibOreDict.*
import java.util.*
import kotlin.math.*

class BlockKudzuVine: BlockContainerMod(material), IShearable, ILexiconable {
	
	lateinit var icons: Array<IIcon>
	
	init {
		setBlockName("KudzuVine")
		setCreativeTab(null)
		setHardness(4f)
		setLightOpacity(3)
		setStepSound(soundTypeGrass)
		setTickRandomly(true)
		Blocks.fire.setFireInfo(this, 15, 100)
	}
	
	override fun getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB? {
		if (getBlocksMovement(world, x, y, z)) return null
		return super.getCollisionBoundingBoxFromPool(world, x, y, z)
	}
	
	override fun getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?): Int {
		val original = super.getFlammability(world, x, y, z, face)
		
		val tile = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return original
		return if (tile.hasMutation(FIREPROOF)) 0 else original
	}
	
	override fun getFireSpreadSpeed(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?): Int {
		val original = super.getFireSpreadSpeed(world, x, y, z, face)
		
		val tile = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return original
		return if (tile.hasMutation(FIREPROOF)) 0 else original
	}
	
	override fun onEntityCollidedWithBlock(world: World, x: Int, y: Int, z: Int, entity: Entity) {
		if (entity is IKudzuIgnoredEntity) return
		
		val tile = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return
		if (tile.hasMutation(HARDENED)) entity.setInWeb()
		
		if (world.isRemote) return
		
		val distance = Vector3.pointDistanceSpace(entity.posX, entity.posY, entity.posZ, entity.prevPosX, entity.prevPosY, entity.prevPosZ)
		if (entity.kudzuDistanceCheck != world.totalWorldTime && distance > 0) {
			entity.kudzuDistance += distance
			entity.kudzuDistanceCheck = world.totalWorldTime
		}
		
		if (entity.kudzuDistance > 1) {
			onCrossed(tile, entity, world, x, y, z)
			entity.kudzuDistance = 0.0
		}
	}
	
	fun onCrossed(tile: TileKudzuVine, entity: Entity, world: World, x: Int, y: Int, z: Int) {
		if (ASJUtilities.chance(10)) damage(tile, entity, entity.kudzuDistance)
		
		if ((tile.hasMutation(FLOWERING) || world.getBlockMetadata(x, y, z).stage == LAST_STAGE) && ASJUtilities.chance(25))
			onEntangle(tile, entity, world, x, y, z)
	}
	
	fun onEntangle(tile: TileKudzuVine, entity: Entity, world: World, x: Int, y: Int, z: Int) {
		if (entity is EntityLivingBase)
			entity.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 100, PotionEternity.STUN and PotionEternity.IRREMOVABLE))
		
		if (tile.hasMutation(EXPLOSIVE) && world.getBlockMetadata(x, y, z).stage == LAST_STAGE)
			world.createExplosion(null, x + 0.5, y + 0.5, z + 0.5, 2f, false)
	}
	
	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int): Float {
		val original = super.getBlockHardness(world, x, y, z)
		
		val tile = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return original
		return if (tile.hasMutation(HARDENED)) original * 4 else original
	}
	
	override fun getPlayerRelativeBlockHardness(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Float {
		val result = super.getPlayerRelativeBlockHardness(player, world, x, y, z)
		
		val tile = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return result
		if (tile.hasMutation(THORNY)) player.attackEntityFrom(DamageSource.cactus, 1f)
		if (tile.hasMutation(FIERY)) player.setFire(1)
		
		return result
	}
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block?, meta: Int) {
		if (world.isRemote) return super.breakBlock(world, x, y, z, block, meta)
		
		val tile = world.getTileEntity(x, y, z) as? TileKudzuVine
		
		tile?.apply {
			try {
				if (tile.hasMutation(EXPLOSIVE))
					world.createExplosion(null, x + 0.5, y + 0.5, z + 0.5, 1.75f, true)
			} catch (_: StackOverflowError) {}
			
			if (world.gameRules.getGameRuleBooleanValue("doTileDrops"))
				drops.forEach { (mutation, chance, variants) ->
					if (!tile.hasMutation(mutation) || !ASJUtilities.chance(chance)) return@forEach
					
					variants.shuffled().any { variant ->
						val drop = if (':' in variant)
								dropsCache.computeIfAbsent(variant) { 
									val (modid, name, meta) = variant.split(':')
									if (!Loader.isModLoaded(modid)) return@computeIfAbsent null
									val item = GameRegistry.findItem(modid, name) ?: return@computeIfAbsent null
									ItemStack(item, 1, meta.toInt())
								}?.copy() ?: return@any false
							else
								(
									OreDictionary.getOres(variant.replaceFirst("ingot", "cluster")).randomOrNull() ?:
									OreDictionary.getOres(variant).randomOrNull()
								)?.copy() ?: return@any false
						
						if (OreDictionary.getOreIDs(drop).any { OreDictionary.getOreName(it) in AlfheimConfigHandler.kudzuDropBlacklist })
							return@any false
						
						drop.stackSize = 3
						if (drop.meta == OreDictionary.WILDCARD_VALUE) drop.meta = 0
						
						EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drop).spawn()
						
						return@any true
					}
				}
			
			ActiveKudzus[this]?.let {
				check(it.size > 0)
				if (tile.summed && --it.size > 0) return@let
				
				val stack = ElvenResourcesMetas.KudzuSeed.stack
				val mutations = EnumMutation.entries.mapNotNull { m -> if (tile.hasMutation(m)) m.ordinal else null }.toIntArray()
				if (mutations.isNotEmpty())
					ItemNBTHelper.setIntArray(stack, ItemElvenResource.TAG_MUTATIONS, mutations)
				EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack).spawn()
				
				ActiveKudzus.remove(this)
			}
		}
		
		super.breakBlock(world, x, y, z, block, meta)
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random?) {
		if (STOPPED) return
		
		if (tryUpdate(world, x, y, z)) {
			world.setBlockToAir(x, y, z)
			return
		}
		
		val size = run {
			val tile = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return@run null
			ActiveKudzus[tile]?.size
		} ?: 1
		
		planTick(world, x, y, z, size)
	}
	
	fun tryUpdate(world: World, x: Int, y: Int, z: Int): Boolean {
		if (world.isRemote) return false
		
		val tile = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return true
		tile.startup()
		
		val brood = ActiveKudzus[tile] ?: return true
		
		val aggressive = tile.hasMutation(AGGRESSIVE)
		val spreading = tile.hasMutation(SPREADING)
		val bluespace = tile.hasMutation(BLUESPACE)
		
		for (dir in ForgeDirection.VALID_DIRECTIONS.shuffled()) {
			val i = x + dir.offsetX
			val j = y + dir.offsetY
			val k = z + dir.offsetZ
			
			val i2 = x + dir.offsetX * 2
			val j2 = y + dir.offsetY * 2
			val k2 = z + dir.offsetZ * 2
			
			var boom = false
			var boom2 = false
			
			fun canGoBoom(a: Int, b: Int, c: Int) =
				world.getBlock(a, b, c).getBlockHardness(world, a, b, c) >= 0f &&
				world.getBlock(a, b, c).getExplosionResistance(null, world, a, b, c, a.D, b.D, c.D) <= 500f &&
				world.getBlock(a, b, c) !== this &&
				!world.getBlock(a, b, c).isReplaceable(world, a, b, c)
			
			if (aggressive && canGoBoom(i, j, k))
				boom = true
			
			val obstacle = world.getBlock(i, j, k)
			val air = obstacle.isReplaceable(world, i, j, k)
			
			if (obstacle !== this && !air && bluespace) {
				var looseBS = true
				
				if (aggressive && canGoBoom(i2, j2, k2)) {
					boom2 = true
					looseBS = false
				}
				
				if (trySpread(tile, brood, world, i2, j2, k2, looseBS, boom2) || !spreading)
					break
			}
			
			if (trySpread(tile, brood, world, i, j, k, false, boom) || !spreading)
				break
		}
		
		return false
	}
	
	fun trySpread(parent: TileKudzuVine, brood: KudzuBrood, world: World, x: Int, y: Int, z: Int, looseBS: Boolean, boom: Boolean): Boolean {
		if (Vector3.pointDistanceSpace(x, y, z, brood.x, brood.y, brood.z) > AlfheimConfigHandler.kudzuRadius) return false
		
		val at = world.getBlock(x, y, z)
		if (at === this) {
			val meta = world.getBlockMetadata(x, y, z)
			val stage = meta.stage
			
			if (stage < LAST_STAGE) world.setBlockMetadataWithNotify(x, y, z, (stage + 1) * STAGES + meta.iconVar, 3)
			
			val new = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return true // should not happen
			
			if (new.hasMutation(FLOWERING) && getEntitiesWithinAABB(new.worldObj, EntityFlowerBud::class.java, new.boundingBox(5)).isEmpty() && ASJUtilities.chance(10))
				EntityFlowerBud(new.worldObj).apply {
					var (x, y, z) = Vector3.fromTileEntity(new).mf()
					while (world.getBlock(x, y - 1, z) === this@BlockKudzuVine) --y 
					setPosition(x + 0.5, y.D, z + 0.5)
				}.spawn()
			
			if (parent.hasMutation(CANNIBAL) && !new.hasMutation(CANNIBAL))
				new.mutations = parent.mutations
			
			return true
		}
		
		if (boom) {
			val ex = Explosion(world, null, x + 0.5, y + 0.5, z + 0.5, 1.75f)
			world.playSoundEffect(ex.explosionX, ex.explosionY, ex.explosionZ, "random.eat", 0.5f + 0.5f * world.rand.nextInt(2).F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f + 1f)
			
			if (AlfheimConfigHandler.kudzuDropBlocks && at.canDropFromExplosion(ex))
				at.dropBlockAsItemWithChance(world, x, y, z, world.getBlockMetadata(x, y, z), 1f, 0)
			
			at.onBlockExploded(world, x, y, z, ex)
		}
		
		if (!world.getBlock(x, y, z).isReplaceable(world, x, y, z))
			return false
		
		if (AlfheimConfigHandler.kudzuEasy && ForgeDirection.VALID_DIRECTIONS.none { d1 ->
			val i = x + d1.offsetX
			val j = y + d1.offsetY
			val k = z + d1.offsetZ
			
			if (world.getBlock(i, j, k).isSideSolid(world, i, j, k, d1.opposite)) return@none true
			
			!ForgeDirection.VALID_DIRECTIONS.none { d2 ->
				val a = i + d2.offsetX
				val b = j + d2.offsetY
				val c = k + d2.offsetZ

				world.getBlock(a, b, c).isSideSolid(world, a, b, c, d2.opposite)
			}
		}) return false
		
		if (!world.setBlock(x, y, z, this, world.rand.nextInt(ICON_VARS), 3)) return false // out of world or something
		
		val new = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return true
		new.mutations = parent.mutations
		new.coreUUID = parent.coreUUID
		brood.size++
		new.summed = true
		
		planTick(world, x, y, z, brood.size)
		
		if (looseBS) {
			parent.setMutation(BLUESPACE, false)
			new.setMutation(BLUESPACE, false)
			world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "mob.endermen.portal", 1f, world.rand.nextFloat() * 0.1f + 0.9f)
		}
		
		if (ASJUtilities.chance(AlfheimConfigHandler.kudzuMutatability))
			EnumMutation.entries.filter { !new.hasMutation(it) }.randomOrNull()?.let { new.setMutation(it, true) }
		
		return true
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = Array(ICONS) { IconHelper.forBlock(reg, this, it) }
	}
	
	override fun getIcon(side: Int, meta: Int) = icons.safeGet(meta)
	
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int): Int {
		var result = 0xFFFFFF
		val tile = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return result
		EnumMutation.entries.forEach { if (tile.hasMutation(it)) result = mix(result, it.color) }
		return result
	}
	
	override fun getLightOpacity(world: IBlockAccess, x: Int, y: Int, z: Int) = super.getLightOpacity(world, x, y, z) * (world.getBlockMetadata(x, y, z) + 1)
	
	override fun shouldSideBeRendered(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Boolean {
		if (world.getBlock(x, y, z) === this) return if (ModInfo.DEV) mc.gameSettings.fancyGraphics else true
		
		return super.shouldSideBeRendered(world, x, y, z, side)
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (!ItemMonocle.hasMonocle(player) || !ASJUtilities.isServer || player.heldItem != null) return false
		val tile = world.getTileEntity(x, y, z) as? TileKudzuVine ?: return false
		val mutations = EnumMutation.entries.mapNotNull { if (tile.hasMutation(it)) it else null }
		
		if (ModInfo.DEV) ASJUtilities.say(player, "[DEV info] ${ActiveKudzus[tile]?.size} kudzu vines in this brood")
		
		if (mutations.isEmpty())
			ASJUtilities.say(player, "alfheimmisc.kudzu.nomutations")
		else
			ASJUtilities.say(player, "alfheimmisc.kudzu.mutations", mutations.joinToString(" ") { StatCollector.translateToLocal("item.alfheim:KudzuSeed.mutation.$it") }.lowercase())
		
		return true
	}
	
	// FUCKING STUPID MCP DIE BITCH | that is get__NOT__BlocksMovement
	override fun getBlocksMovement(world: IBlockAccess, x: Int, y: Int, z: Int) =
		world.getBlockMetadata(x, y, z).stage == 0 || (world.getTileEntity(x, y, z) as? TileKudzuVine)?.hasMutation(WOODEN) != true
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileKudzuVine()
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
	override fun isLadder(world: IBlockAccess?, x: Int, y: Int, z: Int, entity: EntityLivingBase?) = true
	override fun getItemDropped(meta: Int, random: Random?, fortune: Int) = null
	override fun quantityDropped(random: Random?) = 0
	override fun canSilkHarvest() = false
	override fun isReplaceable(world: IBlockAccess?, x: Int, y: Int, z: Int) = false
	override fun isShearable(item: ItemStack?, world: IBlockAccess, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z).stage == LAST_STAGE
	override fun onSheared(item: ItemStack?, world: IBlockAccess?, x: Int, y: Int, z: Int, fortune: Int) = arrayListOf(ElvenResourcesMetas.KudzuSprout.stack)
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.kudzu
	
	fun planTick(world: World, x: Int, y: Int, z: Int, size: Int) {
		if (STOPPED) return
		
		val delay = max(1, size * AlfheimConfigHandler.kudzuDelay)
		world.scheduleBlockUpdate(x, y, z, this, delay)
	}
	
	companion object {
		
		private const val STAGES = 3
		private const val LAST_STAGE = STAGES - 1
		const val ICON_VARS = 3
		private const val ICONS = STAGES * ICON_VARS
		
		val material = MaterialPublic(MapColor.foliageColor, solid = false, blocksLight = false, burnable = true, opaque = false)
		var STOPPED = false
		
		val drops = listOf(
			METALLIC   to 20 with /* simple metals   */ listOf("kudzuDropMetallic", "ingotChromium", "ingotMagnesium", "ingotConstraintMetal", "ingotSpinel", "ingotLimpium", "ingotIron", "ingotTin", "ingotFzDarkIron", "ingotAluminium", "ingotDemonite", "ingotBismuth", "ingotMagnetit", "ingotLithium", "ingotGiganium", "ingotNickel", "ingotObsigmite", "ingotZinc", "ingotTitanium", "ingotCobalt", "ingotFuzzium", "ingotLead", "ingotManganese", "ingotSodium", "ingotChromite", "ingotTritanium", "ingotAluminum", "ingotTanzanite", "ingotArdite", "ingotCopper", "ingotSkyium", "NamiumcraftID:Coneticingot:0", "Xenorite:shadowboronIngot:0", "Xenorite:finoriteIngot:0", "Xenorite:xenoriteIngot:0", "rexalite:ingotRexalite:0", "Xenorite:heavenlyglintIngot:0", "starwarstheclonewarsmod:item.duraniumingot:0", "outerrim:karniteIngot:0", "Xenorite:coreoriteIngot:0", "ingotAntimony", "ingotXancium", "ingotRutile", "ingotKlangite", "ingotPherithium", "ingotVanite", "ingotChrome", "ingotSapphire", "ingotMystite", "ingotRainbowSteel", "ingotThulium", "ingotShadowSecretGold", "ingotGhastly", "ingotBaronyte", "ingotBlazium", "ingotBiliha", "ingotMysteriousSoul", "ingotShyrestone", "ingotRainbowOpal", "ingotSoulGold", "ingotOrichalcum", "ingotAmethyst", "ingotElecanium", "ingotWolfram", "ingotSyrmorite", "ingotCerium", "ingotStartleSilver", "ingotVanadium", "ingotPreciousIron", "ingotEmberstone", "ingotTitan", "ingotRosite", "ingotVarsium", "ingotPalladium", "ingotMolybdenum", "ingotShyregem", "ingotOctine", "ingotYttrium", "ingotLyon", "ingotYamagata", "ingotManaSpiritSteel", "ingotGhoulish", "ingotLimonite", "ingotScandium", "ingotIndium", "ingotCadmium", "ingotBendezium", "ingotChozo", "ingotIgnatius", "ingotSanguinite", "ingotDeepIron", "ingotAtlarus", "ingotKalendrite", "ingotShadowIron", "ingotRubracium", "ingotOureclase", "ingotToslotrium", "ingotMeutoite", "ingotVyroxeres", "ingotCarmot", "ingotCeruclase", "ingotMortium", "ingotVulcanite", "ingotCharredLead", "ingotAlduorite", "ingotColdiron", "ingotMithral", "ingotShadow", "ingotMidasium", "ingotPrometheum", "ingotSkyIron", "ingotTerrium", "ingotEximite", "ingotLemurite", "ingotInfuscolium", "ingotVividium", "ingotElectrimite", "ingotAstralSilver", "tm:itemDarksteel:0", "mysticaltrinkets:Mystical_Ingot:0", "dm:ItemDarkIngot:0", "bicbiome:penoiumingot:0", "viridiumandostrium:viridiumandostrium_viridiumIngot:0", "bicbiome:harmuraingot:0", "AdventureTime:DemonIngot:0", "pixelmoncore:aquaIngot:0", "pixelmoncore:orichulumIngot:0", "ma:shadowingot:0", "SynthiteMod:item.MegamiteIngot:0", "extraores:item.GIngot:0", "SynthiteMod:item.SynthiteIngot:0", "dm:ItemLightIngot:0", "pixelmoncore:eridiumIngot:0", "bicbiome:whlopmoreingot:0", "TheDimensions:OxilioIngot:0", "bicbiome:tungingot:0", "bicbiome:bicboiniumingot:0", "pixelmoncore:hellstoneIngot:0", "xtracraftmod:xtracraftmod TitarackIngot:0"),
			GLIMMERING to 20 with /* valuable metals */ listOf("kudzuDropGlimmering", ELEMENTIUM, "ingotRadite", "ingotSilver", "ingotMikhail", "ingotTungsten", "ingotNeptunium", "ingotPlutonium", "ingotOsmium", "ingotTurquoise", "ingotMeteoricIron", "ingotDraconium", "ingotZogite", "ingotUranium", "ingotPlatinum", "ingotDesh", "ingotMithril", "ingotAdamantium", "ingotPromethium", "ingotGold", "ingotElnTungsten", "ingotAmericium", "ingotGermanium", "ingotNeodymium", "ingotThorium", "ingotCurium", "ingotIridium", "ingotVanady", "ingotBeryllium", "starwarstheclonewarsmod:item.phrikingot:0", "paladium:paladium_ingot:0", "ingotSchrabidium", "ingotRareEarth", "divinerpg:realmiteIngot:0", "divinerpg:rupeeIngot:0", "divinerpg:netheriteIngot:0", "divinerpg:arlemiteIngot:0", "manametalmod:ingotMeteorite:0", "ingotHolyCopper", "ingotDeepiron", "ingotAdamantine", "ingotMoltenGold", "ingotHighlycrystal", "ingotBadyala", "ingotStarSilver", "ingotFantasygold", "ingotMysteriousIron", "ingotSoulstone", "ingotSoulSteel", "ingotEuropium", "ingotCentaurium", "ingotHeeEndium", "ingotMercurianIron", "ingotYellorium", "ingotRhodium", "SpiritOresMod:teleniumIngot:0", "viridiumandostrium:viridiumandostrium_ostriumIngot:0", "pixelmoncore:unobtainiumIngot:0", "StargateTech2:naquadah:0", "ingotLiquifiedCoralium", "ingotAbyssalnite", "xtracraftmod:xtracraftmod ElementiumIngot:0"),
			GLASSY     to 20 with /* gems            */ listOf("kudzuDropGlassy", DRAGONSTONE, QUARTZ[5], "gemStaria", "gemTanzanite", "ingotObsidian", "gemJasper", "gemBlackdiamond", "gemViolet", "gemOnyx", "gemRhodochrosite", "gemBlackOpal", "gemResonantCrystal", "gemAmericium", "gemBeryl", "gemAquamarine", "gemOpal", "gemGreenSapphire", "gemHexoriumWhite", "gemNeptunium", "gemHexoriumGreen", "gemAluminum", "gemHeliodor", "gemRetium", "gemMalachite", "ingotMalachite", "gemAmethyst", "gemLapis", "gemLila", "gemPeridot", "gemChaos", "gemWhiteopal", "gemAlexandrite", "gemMorganite", "gemIolite", "gemOlivine", "gemGlowtit", "gemClinohumite", "gemTurquoise", "gemRoseQuartz", "gemDiamond", "gemJet", "gemCurium", "gemHexorium", "gemZircon", "gemHellfire", "gemBloodDiamond", "gemEmery", "gemQuartzBlack", "gemCitrine", "gemDilithium", "gemAmber", "gemImagCrystalLow", "gemHexoriumBlack", "gemTourmaline", "gemApatite", "gemNeon", "gemSapphire", "gemStarGarnet", "gemEmerald", "gemRuby", "gemZanium", "gemGarnet", "gemAmazonite", "gemVoidDiamond", "gemQuartz", "gemJade", "gemSugilite", "gemThorium", "gemBioterium", "gemHexoriumBlue", "gemSpinel", "gemTopaz", "ao:ItemThanite:0", "ao:ItemLunite:0", "crystalic_void:crystallium:0", "paladium:findium:0", "appliedenergistics2:item.ItemMultiMaterial:0", "Mo' Shiz:scarletemeraldgem:0", "appliedenergistics2:item.ItemMultiMaterial:1", "paladium:trixium:0", "gemDark", "gemSodalite", "gemVolcanic", "gemLignite", "gemCinnabar", "divinerpg:edenGem:0", "divinerpg:mortumGem:0", "aether_legacy:zanite_gemstone:0", "divinerpg:wildwoodGem:0", "nova_craft:tophinite_gemstone:0", "erebus:materials:12", "divinerpg:skythernGem:0", "divinerpg:apalachiaGem:0", "alexandriteandmore:item.endergem:0", "divinerpg:bloodgem:0", "gemOrnamyte", "gemBlackDiamond", "gemBlueTopaz", "gemCordierite", "gemFlyGem", "gemCrystallite", "gemLightScrap", "gemGemenyte", "gemMoonstone", "gemSunstone", "gemLunaStone", "gemJewelyte", "gemChimerite", "gemGoslarda", "gemEnergyCrystal", "gemRainbowDiamond", "gemBloodstone", "BiomesOPlenty:gems:0", "thebetweenlands:greenMiddleGem:0", "thebetweenlands:crimsonMiddleGem:0", "thebetweenlands:aquaMiddleGem:0", "gemCalcite", "gemFluorite", "gemMagnetite", "gemDarkCrystal", "gemLightCrystal", "tropicraft:ore:0", "tropicraft:ore:1", "tropicraft:ore:2", "gemBituminousCoal", "gemChalcedony", "gemPinkQuartz", "gemMimichite", "gemYellowGarnet", "gemPetalite", "gemDarkness", "gemAnthraciteCoal", "gemLigniteCoal", "gemInfusedAzurite", "gemInfused", "gemFakediamond", "gemCarnelian", "gemRedGarnet", "gemWuerfelium", "gemLilithite", "gemIridium", "ma:mithrilgem:0", "AdventureTime:CrystalGem:0", "mcplus:ItemSuperGem:0", "gemCoralium", "xtracraftmod:xtracraftmod InfernalGem:0", "xtracraftmod:xtracraftmod EmeraldeGem:0", "xtracraftmod:xtracraftmod SapphireGem:0", "xtracraftmod:xtracraftmod RubyGem:0", "xtracraftmod:xtracraftmod PyriteGem:0"),
			PLASTIC    to 20 with /* some shit       */ listOf("kudzuDropPlastic", "minecraft:flint:0", "ingotImagSilicon", "ingotBoron", "ingotButter", "ingotRedstone", "ingotSilicon", "ingotGraphit", "ingotCarbon"),
			WOODEN     to 20 with /* wood products   */ listOf("kudzuDropWooden", "logWood", "plankWood", "stickWood", LIVING_WOOD, DREAM_WOOD),
		)
		
		private val dropsCache = HashMap<String, ItemStack?>()
		
		init {
			eventForge()
		}
		
		fun mix(src: Int, dst: Int) = ((src and 0xFEFEFE) ushr 1) + ((dst and 0xFEFEFE) ushr 1)
		
		const val TAG_KUDZU_DISTANCE = "${ModInfo.MODID}.kudzuDistance"
		const val TAG_KUDZU_DISTANCE_CHECK = "${ModInfo.MODID}.kudzuDistanceCheck"
		
		var Entity.kudzuDistance
			get() = entityData.getDouble(TAG_KUDZU_DISTANCE)
			set(value) = entityData.setDouble(TAG_KUDZU_DISTANCE, value)
		
		var Entity.kudzuDistanceCheck
			get() = entityData.getLong(TAG_KUDZU_DISTANCE_CHECK)
			set(value) = entityData.setLong(TAG_KUDZU_DISTANCE_CHECK, value)
		
		private val Int.stage get() = if (this == 15) 0 else this / STAGES
		private val Int.iconVar get() = if (this == 15) 0 else this % STAGES
		
		fun damage(tile: TileKudzuVine, entity: Entity, amount: Double) {
			if (tile.hasMutation(THORNY))
				entity.attackEntityFrom(DamageSource.cactus, amount.F * 2)
			
			if (tile.hasMutation(FIERY))
				entity.setFire(MathHelper.ceiling_double_int(amount * 2))
			
			if (tile.hasMutation(TOXIC))
				if (entity is EntityLivingBase)
					entity.addPotionEffect(PotionEffect(Potion.poison.id, MathHelper.ceiling_double_int(amount * 2) * 20, 1))
				else
					entity.attackEntityFrom(DamageSourceSpell.poison, amount.F * 2)
		}
		
		@SubscribeEvent
		fun makeSheepEatKudzu(e: EntityJoinWorldEvent) {
			val sheep = e.entity as? EntitySheep ?: return
			if (sheep.tasks.taskEntries.none { (it as EntityAITasks.EntityAITaskEntry).action is SheepAIEatKudzu })
				sheep.tasks.addTask(5, SheepAIEatKudzu(sheep))
		}
	}
}

class SheepAIEatKudzu(private val sheep: EntitySheep): EntityAIBase() {
	
	var eatingTimer = 0
	
	init {
		mutexBits = 7
	}
	
	override fun shouldExecute() = ForgeDirection.entries.any { sheep.worldObj.getBlock(sheep, it.offsetX, it.offsetY, it.offsetZ) === AlfheimBlocks.kudzuVine }
	
	override fun startExecuting() {
		eatingTimer = 40
		sheep.worldObj.setEntityState(sheep, 10.toByte())
		sheep.navigator.clearPathEntity()
	}
	
	override fun continueExecuting() = eatingTimer > 0
	
	override fun updateTask() {
		sheep.sheepTimer = eatingTimer
		eatingTimer = max(0, eatingTimer - 1)
		
		if (eatingTimer != 4) return
		
		ForgeDirection.entries.shuffled().forEach {
			val tile = sheep.worldObj.getTileEntity(sheep, it.offsetX, it.offsetY, it.offsetZ) as? TileKudzuVine ?: return@forEach
			BlockKudzuVine.damage(tile, sheep, 2.0)
			sheep.worldObj.setBlock(sheep, Blocks.air, it.offsetX, it.offsetY, it.offsetZ)
			sheep.eatGrassBonus()
			
			return
		}
	}
	
	override fun resetTask() {
		eatingTimer = 0
	}
}

interface IKudzuIgnoredEntity