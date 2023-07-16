package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.block.base.BlockMod
import alfheim.common.core.handler.*
import alfheim.common.crafting.recipe.AlfheimRecipes
import alfheim.common.entity.EntityVoidCreeper
import alfheim.common.item.ItemIridescent
import alfheim.common.network.*
import alfheim.common.network.packet.Message0dC
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.material.Material
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.item.*
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.potion.Potion
import net.minecraft.server.MinecraftServer
import net.minecraft.util.*
import net.minecraft.world.*
import thaumcraft.common.config.Config
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.item.lens.*
import kotlin.streams.toList

class BlockLootbox: BlockMod(Material.rock) {
	
	lateinit var iconsTop: Array<IIcon>
	lateinit var iconsSide: Array<IIcon>
	
	init {
		setBlockBounds(0.0625f, 0f, 0.0625f, 0.9375f, 0.875f, 0.9375f)
		setBlockName("Lootbox")
		setLightOpacity(0)
		setHardness(2f)
		setResistance(10f)
		setStepSound(soundTypeStone)
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (world.isRemote) return true
		
		val scam = isScambox(world, x, y, z)
		if (scam) {
			if (player is EntityPlayerMP) getScammed(player)
		} else
			getRewarded(player, getRarity(world, x, y, z))
		
		hooray(world, x + 0.5, y + 0.5, z + 0.5, scam)
		
		return true
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		iconsTop = Array(3) { IconHelper.forBlock(reg, this, "Top$it") }
		iconsSide = Array(3) { IconHelper.forBlock(reg, this, it) }
	}
	
	override fun isOpaqueCube() = false
	
	override fun renderAsNormalBlock() = false
	
	override fun getIcon(side: Int, meta: Int): IIcon {
		val i = meta % 3
		return if (side < 2) iconsTop[i] else iconsSide[i]
	}
	
	companion object {
		
		val scarySounds = listOf(
			"ambient.cave.cave",
			"mob.blaze.breathe",
			"mob.cat.hiss",
			"mob.enderdragon.growl",
			"mob.endermen.scream",
			"mob.ghast.scream",
			"mob.wither.idle",
			"mob.wither.spawn",
			"mob.wolf.growl",
			"mob.zombie.say",
//			"random.breath",
			"creeper.primed",
		)
		
		val effects by lazy {
			val list = mutableListOf(
				Potion.moveSlowdown.id,
				Potion.digSlowdown.id,
				Potion.confusion.id,
				Potion.blindness.id,
				Potion.hunger.id,
				Potion.weakness.id,
				Potion.poison.id,
				Potion.wither.id,
				AlfheimConfigHandler.potionIDBleeding,
				AlfheimConfigHandler.potionIDDecay,
				AlfheimConfigHandler.potionIDManaVoid,
				AlfheimConfigHandler.potionIDSoulburn,
			)
			
			if (Botania.thaumcraftLoaded)
				list += listOf(
					Config.potionVisExhaustID,
					Config.potionInfVisExhaustID,
					Config.potionBlurredID,
					Config.potionThaumarhiaID,
					Config.potionTaintPoisonID,
					Config.potionUnHungerID,
					Config.potionSunScornedID,
					Config.potionDeathGazeID
				)
			
			list
		}
		
		private val rewards = arrayOf("lootboxCommon", "lootboxUncommon", "lootboxRare").map { file ->
			this::class.java.getResourceAsStream("/assets/${ModInfo.MODID}/loot/$file")!!.use {
				it.bufferedReader().lines().map { line ->
					val entries = line.split(',')
					val (modid, name, chance, min, max) = entries
					val metaStart = entries.getOrNull(5) ?: "0"
					val metaEnd = entries.getOrNull(6) ?: metaStart
					Reward(modid, name, chance.toDouble(), min.toInt(), max.toInt(), metaStart.toInt(), metaEnd.toInt())
				}.toList()
			}
		}
		
		fun isScambox(world: IBlockAccess, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z) < 3
		
		fun getRarity(world: IBlockAccess, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z).minus(3).div(3)
		
		fun getRewarded(player: EntityPlayer, rarity: Int) {
			rewards[rarity].forEach { (modid, name, chance, min, max, metaStart, metaEnd) ->
				if (!ASJUtilities.chance(chance)) return@forEach
				
				val count = ASJUtilities.randInBounds(min, max, player.rng)
				if (count < 1) return@forEach
				
				val item = GameRegistry.findItem(modid, name) ?: return@forEach
				val stack = ItemStack(item, count, ASJUtilities.randInBounds(metaStart, metaEnd, player.rng))
				
				if (stack.item == null) return@forEach
				
				if (!player.inventory.addItemStackToInventory(stack))
					player.dropPlayerItemWithRandomChoice(stack, false)
			}
		}
		
		fun getScammed(player: EntityPlayerMP) {
			 scams.random(player.rng)!!(player)
		}
		
		fun hooray(world: World, x: Double, y: Double, z: Double, scam: Boolean) {
			if (scam)
				return VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.ENDER, world.provider.dimensionId, x, y, z)
			
			EntityFireworkRocket(world, x, y, z, (ItemLens.getLens(ItemLens.FIREWORK) as LensFirework).generateFirework(ItemIridescent.rainbowColor())).apply {
				spawn()
				worldObj.setEntityState(this, 17.toByte())
				setDead()
			}
		}
		
		private data class Reward(val modid: String, val name: String, val chance: Double, val min: Int, val max: Int, val metaStart: Int, val metaEnd: Int)
		
		private val scams = listOf<(EntityPlayerMP) -> Unit>(
			{
				it.addPotionEffect(PotionEffectU(effects.random(it.rng)!!, it.rng.nextInt(200) + 200, it.worldObj.difficultySetting.difficultyId))
			},
			{
				ASJUtilities.say(it, "alfheimmisc.scam${it.rng.nextInt(28)}", EnumChatFormatting.ITALIC)
			},
			{
				it.heldItem?.let { s ->
					it.inventory[it.inventory.currentItem] = null
					
					EntityItem(it.worldObj).apply {
						setEntityItemStack(s.copy())
						setPosition(it, oY = 10.5)
						setMotion(0.0)
						spawn()
					}
				}
			},
			{
				val old = Vector3.fromEntity(it)
				var tries = 50
				while(--tries > 0) {
					val (x, y, z) = Vector3().rand().sub(0.5, 0, 0.5).normalize().mul(ASJUtilities.randInBounds(8, 16, it.rng)).add(it)
					it.setPositionAndUpdate(x, y, z)
					
					if (it.worldObj.getCollidingBoundingBoxes(it, it.boundingBox()).isEmpty()) return@listOf
				}
				val (x, y, z) = old
				it.setPositionAndUpdate(x, y, z)
			},
			{
				it.motionY += 3
				it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
			},
			{
				NetworkService.sendTo(Message0dC(M0dc.SEEME), it)
			},
			{
				it.inventory.mainInventory.apply { for (i in indices) get(i) ?: set(i, ItemStack(Blocks.tallgrass)) }
			},
			{
				it.playSoundAtEntity(scarySounds.random(it.rng)!!, 1f, 1f)
			},
			{
				NetworkService.sendTo(Message0dC(M0dc.SSS), it)
			},
			{
				it.addExperienceLevel(-30)
				it.inventory.addItemStackToInventory(when (it.rng.nextInt(3)) {
					0 -> ItemStack(Items.wooden_shovel, 1, Items.wooden_shovel.maxDamage).apply { addEnchantment(Enchantment.efficiency, 5) }
					1 -> ItemStack(ModBlocks.livingrock).apply { addEnchantment(Enchantment.respiration, 3) }
					2 -> ItemStack(Items.paper).apply { addEnchantment(Enchantment.fireProtection, 4) }
					else -> null
				})
			},
			{
				EntityFireworkRocket(it.worldObj, it.posX, it.posY + 2, it.posZ, (ItemLens.getLens(ItemLens.FIREWORK) as LensFirework).generateFirework(ItemIridescent.rainbowColor())).spawn()
			},
			{
				it.inventory.addItemStackToInventory(AlfheimRecipes.skullStack(it.commandSenderName).setStackDisplayName(StatCollector.translateToLocal("alfheimmisc.hatstand")))
			},
			{
				NetworkService.sendTo(Message0dC(M0dc.ROLL), it)
			},
			{
				val (x, _, z) = Vector3(64, 0, 0).rotateOY(it.rng.nextInt(360)).add(it)
				it.setPositionAndUpdate(x, 256.0, z)
			},
			{
				if (AlfheimConfigHandler.enableElvenStory) {
					CardinalSystem.ElvenStoryModeSystem.setGender(it, !CardinalSystem.ElvenStoryModeSystem.getGender(it))
					MinecraftServer.getServer().configurationManager.playerEntityList.forEach { p -> CardinalSystem.ElvenStoryModeSystem.transfer(p as EntityPlayerMP) }
				}
			},
			{
				EntityVoidCreeper(it.worldObj).apply { setPosition(it) }.spawn()
			},
			{
				it.worldObj.createExplosion(null, it.posX, it.posY, it.posZ, 2f, true)
			},
		)
	}
}
