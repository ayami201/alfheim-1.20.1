package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemBlockSubspacian
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.BlockBush
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.*
import net.minecraftforge.common.*
import vazkii.botania.api.lexicon.ILexiconable

class BlockSubspacian: BlockBush(Material.plants), ILexiconable {
	
	init {
		setBlockName("Subspacian")
		setCreativeTab(AlfheimTab)
		setLightLevel(0.25f)
		setStepSound(soundTypeGrass)
		GameRegistry.registerBlock(this, ItemBlockSubspacian::class.java, "Subspacian")
	}
	
	override fun onEntityCollidedWithBlock(world: World, x: Int, y: Int, z: Int, entity: Entity?) {
		if (entity is EntityEnderman) return
		
		doBadThings(entity as? EntityLivingBase ?: return, true)
	}
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.subshroom
	
	override fun registerBlockIcons(reg: IIconRegister) {
		blockIcon = IconHelper.forBlock(reg, this)
	}
	
	override fun getPlantType(world: IBlockAccess?, x: Int, y: Int, z: Int) = EnumPlantType.Plains
	
	companion object {
		
		fun doBadThings(entity: EntityLivingBase, collide: Boolean = false) {
			if (ASJUtilities.isClient) return
			
			val world = entity.worldObj
			val chance = entity.rng.nextDouble() * 100
			
			var dimTo = world.provider.dimensionId
			var target: Entity = entity
			
			val (x, y, z) = when {
				AlfheimConfigHandler.interdimensionalSubspacian && !collide && chance < 1 -> {
					dimTo = DimensionManager.getStaticDimensionIDs().toList().random(entity.rng)!!
					Vector3(MinecraftServer.getServer().worldServerForDimension(dimTo)?.spawnPoint ?: ChunkCoordinates(0, 64, 0))
				}
				chance < 26                                                               -> {
					Vector3.fromEntity(world.loadedEntityList.random(entity.rng) as Entity)
				}
				!collide && chance < 51                                                   -> {
					target = world.loadedEntityList.random(entity.rng) as Entity
					Vector3.fromEntity(entity)
				}
				else                                                                      -> {
					Vector3().rand().sub(0.5).normalize().mul((Math.random() * if (collide) 64 else 256) + 16).add(entity)
				}
			}
			
			ASJUtilities.sendToDimensionWithoutPortal(target, dimTo, x, y, z)
		}
	}
}
