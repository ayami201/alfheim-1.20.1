@file:Suppress("UNUSED_PARAMETER")

package alfheim.common.core.asm.hook.replacer

import alexsocol.asjlib.render.ASJRenderHelper.glColor1u
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockAuroraDirt
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.*
import cofh.thermalfoundation.fluid.TFFluids
import com.KAIIIAK.classManipulators.HookReplacer
import com.KAIIIAK.classManipulators.HookReplacer.Replacer.*
import net.minecraft.block.*
import net.minecraft.client.renderer.entity.RenderWolf
import net.minecraft.entity.Entity
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.*
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import org.lwjgl.opengl.GL11.glColor3f
import vazkii.botania.client.render.tile.RenderTileFloatingFlower
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.decor.IFloatingFlower
import vazkii.botania.common.block.tile.TileCocoon
import vazkii.botania.common.core.handler.SheddingHandler
import vazkii.botania.common.core.handler.SheddingHandler.ShedPattern
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.entity.EntityDoppleganger
import vazkii.botania.common.item.rod.ItemGravityRod
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3

@HookReplacer(targetMethod = "hatch")
fun replaceSpecialChance(thiz: TileCocoon) {
	startFROM()
	POPLine();POP(0.05f)
	POPLine();startTO()
	POPLine();POP(getCocoonSpecialChance(thiz))
	POPLine();stop()
}

fun getCocoonSpecialChance(thiz: TileCocoon): Float {
	return thiz.alfheim_synthetic_essenceGiven * 0.05f
}

@HookReplacer(targetMethod = "hatch")
fun replaceMooshroomChance(thiz: TileCocoon) {
	startFROM()
	POPLine();POP(0.01)
	POPLine();startTO()
	POPLine();POP(getCocoonMooshroomChance(thiz))
	POPLine();stop()
}

fun getCocoonMooshroomChance(thiz: TileCocoon): Double {
	return thiz.alfheim_synthetic_essenceGiven * 0.01
}

@HookReplacer(targetMethod = "attackEntityFrom")
fun allowSpellDamage(thiz: EntityDoppleganger, src: DamageSource, dmg: Float): Boolean {
	startFROM()
	POPLine();POP(src.damageType.equals("player"))
	POPLine();startTO()
	POPLine();POP(checkDamage(src))
	POPLine();stop()
	
	return false
}

fun checkDamage(src: DamageSource): Boolean {
	return src.damageType == "player" || src is DamageSourceSpell
}

@HookReplacer(correctStaticIndexes = true)
fun leftClick(static: ItemGravityRod, player: EntityPlayer) {
	startFROM()
	POPLine();ItemNBTHelper.setInt(ALOAD("1"), "ticksCooldown", 10)
	POPLine();startTO()
	POPLine();ItemNBTHelper.setInt(ALOAD("1"), "ticksCooldown", 10)
	POPLine();sendVelocityPacket(ALOAD("3"))
	POPLine();stop()
}

fun sendVelocityPacket(item: Entity?) {
	if (item is EntityPlayerMP) item.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(item))
}

@HookReplacer
fun getShedPattern(static: SheddingHandler, entity: Entity): ShedPattern? {
	startFROM()
	POPLine();POP(ALOAD<ShedPattern>("3").EntityClass.isInstance(entity))
	POPLine();startTO()
	POPLine();POP(ALOAD<ShedPattern>("3").EntityClass == entity::class.java)
	POPLine();stop()
	
	return null
}

@HookReplacer
fun shouldRenderPass(render: RenderWolf, wolf: EntityWolf?, pass: Int, ticks: Float): Int {
	startFROM()
	POPLine();glColor3f(EntitySheep.fleeceColorTable[ILOAD("4")][0], EntitySheep.fleeceColorTable[ILOAD("4")][1], EntitySheep.fleeceColorTable[ILOAD("4")][2])
	POPLine();startTO()
	POPLine();applyCollarColor(ILOAD("4"))
	POPLine();stop()
	
	return -1
}

fun applyCollarColor(colorIndex: Int) {
	if (colorIndex == -1) {
		val color = ItemIridescent.rainbowColor()
		glColor1u(color)
	} else {
		val (r, g, b) = EntitySheep.fleeceColorTable[colorIndex and 15]
		glColor3f(r, g, b)
	}
}

@HookReplacer
fun getCanSpawnHere(entity: EntityAnimal): Boolean {
	startFROM()
	POPLine();POP(entity.worldObj.getBlock(ILOAD("1"), ILOAD("2") - 1, ILOAD("3")))
	POPLine();startTO()
	POPLine();POP(checkBlockForSpawn(entity, ILOAD("1"), ILOAD("2") - 1, ILOAD("3")))
	POPLine();stop()
	
	return true
}

fun checkBlockForSpawn(entity: EntityAnimal, x: Int, y: Int, z: Int): Block {
	val block = entity.worldObj.getBlock(x, y, z)
	
	if (entity.worldObj.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim)
		if (block === AlfheimBlocks.snowGrass || block === Blocks.snow_layer || block === AlfheimBlocks.snowLayer)
			return Blocks.grass
	
	return block
}

@HookReplacer
fun preInit(static: TFFluids?) {
	startFROM()
	POPLine();POP("mana")
	POPLine();startTO()
	POPLine();POP("primalmana")
	POPLine();stop()
}

@HookReplacer
fun updateTick(block: BlockDynamicLiquid, world: World, x: Int, y: Int, z: Int, rand: Random) {
	startFROM()
	POPLine();POP(Blocks.stone)
	POPLine();startTO()
	POPLine();POP(getStoneBlock(world))
	POPLine();stop()
}

fun getStoneBlock(world: World): Block = 
	if (world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim) ModBlocks.livingrock else Blocks.stone

@HookReplacer
fun func_149805_n(block: BlockLiquid, world: World, x: Int, y: Int, z: Int) {
	startFROM()
	POPLine();POP(Blocks.cobblestone)
	POPLine();startTO()
	POPLine();POP(getCobblestoneBlock(world))
	POPLine();stop()
}

fun getCobblestoneBlock(world: World): Block =
	if (world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim) AlfheimBlocks.livingcobble else Blocks.cobblestone

@HookReplacer
fun renderTileEntityAt(render: RenderTileFloatingFlower, tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
	val flower = tile as IFloatingFlower
	
	startFROM()
	POPLine();POP(flower.islandType.color)
	POPLine();startTO()
	POPLine();POP(getColor(tile, flower))
	POPLine();stop()
}

fun getColor(tile: TileEntity, flower: IFloatingFlower) =
	if (flower.islandType === ItemColorSeeds.islandTypes.last())
		BlockAuroraDirt.getBlockColor(tile.xCoord, tile.yCoord, tile.zCoord)
	else
		flower.islandType.color