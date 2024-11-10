package alfheim.common.item.equipment.tool.terrasteel

import alexsocol.asjlib.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.equipment.tool.manasteel.ItemManasteelHoe
import alfheim.common.item.relic.*
import com.google.common.collect.*
import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.eventhandler.Event
import cpw.mods.fml.common.registry.GameRegistry
import ic2.core.crop.TileEntityCrop
import net.minecraft.block.*
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.*
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import net.minecraftforge.common.*
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.event.entity.player.BonemealEvent
import thaumcraft.common.blocks.BlockCustomPlant
import thaumcraft.common.tiles.TileManaPod
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.equipment.tool.ToolCommons

class ItemTerraHoe: ItemManasteelHoe(BotaniaAPI.terrasteelToolMaterial, "TerrasteelHoe") {
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		var did = super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ)
		if (did) return true
		
		if (tryIC2Crop(stack, player, world, x, y, z) || tryThaumTree(stack, player, world, x, y, z)) {
			for (i in 0..2)
				Botania.proxy.sparkleFX(world, x - 0.1 + Math.random() * 1.2, y - 0.1 + Math.random() * 1.2, z - 0.1 + Math.random() * 1.2, 0.1f, 1f, 0.1f, 1f, 1)
			
			return true
		}
		
		val extraRange = if (player.isSneaking) 0 else getExtraRange(stack, player)
		
		outer@ for (xOffset in 0.bidiRange(extraRange))
			for (zOffset in 0.bidiRange(extraRange)) {
				if (world.isAirBlock(x + xOffset, y, z + zOffset)) continue
				if (!ManaItemHandler.requestManaExactForTool(stack, player, MANA_PER_ACTION, false) && stack.getItemDamage() >= stack.maxDamage) break@outer
				
				var sparkle = false
				
				if (applyBonemeal(stack, world, x + xOffset, y, z + zOffset, player)) {
					if (!world.isRemote)
						world.playAuxSFX(2005, x + xOffset, y, z + zOffset, 0)
					
					did = true
				} else if (replant(stack, world, x + xOffset, y, z + zOffset, player)) {
					did = true
					sparkle = true
				} else if (plantAvailableSeeds(stack, world, x + xOffset, y, z + zOffset, player)) {
					did = true
					sparkle = true
				}
				
				if (sparkle)
					for (i in 0..2)
						Botania.proxy.sparkleFX(world, x + xOffset - 0.1 + Math.random() * 1.2, y - 0.1 + Math.random() * 1.2, z + zOffset - 0.1 + Math.random() * 1.2, 0.1f, 1f, 0.1f, 1f, 1)
			}
		
		return did
	}
	
	fun applyBonemeal(stack: ItemStack, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): Boolean {
		val block = world.getBlock(x, y, z)
		
		val event = BonemealEvent(player, world, block, x, y, z)
		if (MinecraftForge.EVENT_BUS.post(event))
			return false
		
		if (event.result == Event.Result.ALLOW) {
			ToolCommons.damageItem(stack, 1, player, MANA_PER_ACTION)
			return true
		}
		
		if (block === Blocks.nether_wart) {
			val meta = world.getBlockMetadata(x, y, z)
			if (meta >= 3) return false
			
			world.setBlockMetadataWithNotify(x, y, z, meta + 1, 2)
		} else if (GameRegistry.findUniqueIdentifierFor(block).toString() == "ExtraUtilities:plant/ender_lilly") {
			val meta = world.getBlockMetadata(x, y, z)
			if (meta >= 7) return false 
			
			world.setBlockMetadataWithNotify(x, y, z, meta + 1, 2)
		} else if (block is IGrowable) {
			if (!block.func_149851_a(world, x, y, z, world.isRemote)) {
				block.updateTick(world, x, y, z, world.rand)
				return false
			}
			
			if (!world.isRemote && block.func_149852_a(world, world.rand, x, y, z))
				block.func_149853_b(world, world.rand, x, y, z)
		} else // unsupported type?
			return false
		
		ToolCommons.damageItem(stack, 1, player, MANA_PER_ACTION)
		
		return true
	}
	
	fun replant(stack: ItemStack, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): Boolean {
		val block = world.getBlock(x, y, z)
		val meta = world.getBlockMetadata(x, y, z)
		
		// check if mature
		if (block is BlockCrops) {
			if (block.func_149851_a(world, x, y, z, world.isRemote))
				return false
		} else if (block === Blocks.nether_wart) {
			if (meta < 3)
				return false
		} else // unsupported type?
			return false
		
		val drops = block.getDrops(world, x, y, z, meta, if (ItemSifRing.getSifRing(player) != null) 3 else 0) ?: return false // wtf?
		if (drops.isEmpty()) return false // ok... nothing to replant with so skip
		
		val seed = drops.firstOrNull { it.stackSize > 0 && (it.item as? IPlantable)?.getPlant(world, x, y, z) == block } ?: return false // no same block. Breaking in this case may be added in the future
		val newMeta = (seed.item as IPlantable).getPlantMetadata(world, x, y, z)
		world.setBlockMetadataWithNotify(x, y, z, newMeta, 3)
		--seed.stackSize
		
		if (!world.isRemote)
			for (drop in drops) {
				if (drop.item == null || drop.stackSize < 1) continue
				
				EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drop).spawn()
			}
		
		ToolCommons.damageItem(stack, 1, player, MANA_PER_ACTION)
		return true
	}
	
	fun plantAvailableSeeds(stack: ItemStack, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): Boolean {
		if (!world.isAirBlock(x, y + 1, z)) return false // no place
		
		val soil = world.getBlock(x, y, z)
		var seed: ItemStack? = null
		for (slot in player.inventory.mainInventory.indices) {
			val at = player.inventory[slot] ?: continue
			if (at.stackSize < 1 || at.item !is IPlantable) continue
			if (at.item === Items.melon_seeds || at.item === Items.pumpkin_seeds) continue // those should be planted manually
			
			if (!soil.canSustainPlant(world, x, y, z, ForgeDirection.UP, at.item as IPlantable)) continue
			
			seed = at
			break
		}
		
		seed ?: return false
		
		val crop = (seed.item as IPlantable).getPlant(world, x, y + 1, z)
		val meta = (seed.item as IPlantable).getPlantMetadata(world, x, y + 1, z)
		world.setBlock(x, y + 1, z, crop, meta, 3)
		
		if (!player.capabilities.isCreativeMode) {
			--seed.stackSize
			ToolCommons.damageItem(stack, 1, player, MANA_PER_ACTION)
		}
		
		return true
	}
	
	fun tryIC2Crop(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Boolean {
		if (!IC2Loaded) return false
		
		val crop = world.getTileEntity(x, y, z) as? TileEntityCrop ?: return false
		if (!ManaItemHandler.requestManaExactForTool(stack, player, MANA_PER_ACTION * 8, false) && stack.getItemDamage() + 8 >= stack.maxDamage) return false
		
		val prevEx = crop.exStorage
		val prevNut = crop.nutrientStorage
		val prevWater = crop.waterStorage
		
		repeat (64) {
			crop.tick()
			
			if (world.isRemote)
				Botania.proxy.sparkleFX(world, x - 0.1 + Math.random() * 1.2, y - 0.1 + Math.random() * 1.2, z - 0.1 + Math.random() * 1.2, 0.1f, 1f, 0.1f, 1f, 1)
		}
		
		crop.exStorage = prevEx
		crop.nutrientStorage = prevNut
		crop.waterStorage = prevWater
		
		ToolCommons.damageItem(stack, 8, player, MANA_PER_ACTION)
		
		return true
	}
	
	private fun tryThaumTree(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Boolean {
		if (!Botania.thaumcraftLoaded) return false
		
		run tree@ {
			val tree = world.getBlock(x, y, z) as? BlockCustomPlant ?: return@tree
			val meta = world.getBlockMetadata(x, y, z)
			
			if (meta == 0) {
				if (!ManaItemHandler.requestManaExactForTool(stack, player, MANA_PER_ACTION * 5, false) && stack.getItemDamage() + 5 >= stack.maxDamage) return false
				
				tree.growGreatTree(world, x, y, z, world.rand)
				ToolCommons.damageItem(stack, 5, player, MANA_PER_ACTION)
				
				return true
			} else if (meta == 1) {
				if (!ManaItemHandler.requestManaExactForTool(stack, player, MANA_PER_ACTION * 25, false) && stack.getItemDamage() + 25 >= stack.maxDamage) return false
				
				tree.growSilverTree(world, x, y, z, world.rand)
				ToolCommons.damageItem(stack, 25, player, MANA_PER_ACTION)
				
				return true
			}
			
			return false
		}
		
		run pod@ {
			val pod = world.getTileEntity(x, y, z) as? TileManaPod ?: return@pod
			val meta = world.getBlockMetadata(x, y, z)
			
			if (!ManaItemHandler.requestManaExactForTool(stack, player, MANA_PER_ACTION, false) && stack.getItemDamage() >= stack.maxDamage) return false
			if (meta >= 7) return false
			
			pod.checkGrowth()
			ToolCommons.damageItem(stack, 1, player, MANA_PER_ACTION)
			return true
		}
		
		return false
	}
	
	override fun onBlockStartBreak(stack: ItemStack, x: Int, y: Int, z: Int, player: EntityPlayer): Boolean {
		if (player.isSneaking) return false
		
		val world = player.worldObj
		val block = world.getBlock(x, y, z)
		if (block !is BlockCrops) return false
		
		val range = getExtraRange(stack, player)
		for (i in x.bidiRange(range))
			for (k in z.bidiRange(range))
				removeBlockWithDrops(player, stack, world, i, y, k)
		
		return false
	}
	
	fun removeBlockWithDrops(player: EntityPlayer, stack: ItemStack, world: World, x: Int, y: Int, z: Int) {
		if (!world.blockExists(x, y, z)) return
		val block = world.getBlock(x, y, z)
		
		if (block !is BlockCrops) return
		if (block.func_149851_a(world, x, y, z, world.isRemote))
			return // not mature
		
		val meta = world.getBlockMetadata(x, y, z)
		
		if (world.isRemote || block.getPlayerRelativeBlockHardness(player, world, x, y, z) <= 0) return
		if (!block.canHarvestBlock(player, meta)) return
		
		if (!player.capabilities.isCreativeMode) {
			val localMeta = world.getBlockMetadata(x, y, z)
			block.onBlockHarvested(world, x, y, z, localMeta, player)
			
			if (block.removedByPlayer(world, player, x, y, z, true)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, localMeta)
				block.harvestBlock(world, player, x, y, z, localMeta)
			}
			
			ToolCommons.damageItem(stack, 1, player, MANA_PER_DAMAGE)
		} else
			world.setBlockToAir(x, y, z)
		
		if (!world.isRemote && ConfigHandler.blockBreakParticles && ConfigHandler.blockBreakParticlesTool)
			world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta shl 12))
	}
	
	override fun getExtraRange(stack: ItemStack, player: EntityPlayer) = if (!RagnarokHandler.blockedPowers[1] && ItemSifRing.getSifRing(player) != null) 4 else 2
	
	override fun getIsRepairable(stack: ItemStack?, material: ItemStack) =
		material.item === ModItems.manaResource && material.meta == 4
	
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, info: MutableList<Any?>, extra: Boolean) {
		info.add(StatCollector.translateToLocal("item.ElementiumHoe.desc"))
	}
	
	override fun getAttributeModifiers(stack: ItemStack?): Multimap<String, AttributeModifier> {
		val multimap = HashMultimap.create<String, AttributeModifier>()
		multimap.put(SharedMonsterAttributes.attackDamage.attributeUnlocalizedName, AttributeModifier(ItemExcaliber.uuid, "Weapon modifier", theToolMaterial.damageVsEntity + 2.5, 0))
		return multimap
	}
	
	companion object {
		const val MANA_PER_ACTION = 300
		val IC2Loaded = Loader.isModLoaded("IC2")
	}
}
