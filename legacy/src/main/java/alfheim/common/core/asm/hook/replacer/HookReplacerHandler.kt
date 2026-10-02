package alfheim.common.core.asm.hook.replacer

import alexsocol.asjlib.get
import alexsocol.asjlib.set
import alexsocol.asjlib.setMotion
import alexsocol.asjlib.spawn
import alfheim.common.block.*
import alfheim.common.core.handler.*
import alfheim.common.core.util.*
import cofh.thermalfoundation.fluid.*
import com.KAIIIAK.classManipulators.*
import com.KAIIIAK.classManipulators.HookReplacer.Replacer.*
import net.minecraft.block.*
import net.minecraft.entity.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.monster.*
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.inventory.*
import net.minecraft.item.*
import net.minecraft.item.crafting.IRecipe
import net.minecraft.network.play.server.*
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.oredict.*
import vazkii.botania.common.block.*
import vazkii.botania.common.block.subtile.generating.*
import vazkii.botania.common.block.tile.*
import vazkii.botania.common.core.handler.*
import vazkii.botania.common.core.handler.SheddingHandler.*
import vazkii.botania.common.core.helper.*
import vazkii.botania.common.crafting.recipe.*
import vazkii.botania.common.entity.*
import vazkii.botania.common.item.equipment.bauble.*
import vazkii.botania.common.item.rod.*
import java.util.*

//@formatter:off
@HookReplacer(targetMethod = "hatch")
fun replaceSpecialChance(thiz: TileCocoon) {
	startFROM()
	POP(0.05f)
	startTO()
	POP(getCocoonSpecialChance(thiz))
	stop()
}

fun getCocoonSpecialChance(thiz: TileCocoon): Float {
	return thiz.alfheim_synthetic_essenceGiven * 0.05f
}

@HookReplacer(targetMethod = "hatch")
fun replaceMooshroomChance(thiz: TileCocoon) {
	startFROM()
	POP(0.01)
	startTO()
	POP(getCocoonMooshroomChance(thiz))
	stop()
}

fun getCocoonMooshroomChance(thiz: TileCocoon): Double {
	return thiz.alfheim_synthetic_essenceGiven * 0.01
}

@HookReplacer(targetMethod = "attackEntityFrom", removePop = true)
fun allowSpellDamage(thiz: EntityDoppleganger, src: DamageSource, dmg: Float): Boolean {
	startFROM()
	src.damageType.equals("player")
	startTO()
	checkDamage(src)
	stop()
	
	return false
}

fun checkDamage(src: DamageSource): Boolean {
	return src.damageType == "player" || src is DamageSourceSpell
}

@HookReplacer
fun leftClick(static: ItemGravityRod, player: EntityPlayer) {
	startFROM()
	ItemNBTHelper.setInt(ALOAD("1"), "ticksCooldown", 10)
	startTO()
	ItemNBTHelper.setInt(ALOAD("1"), "ticksCooldown", 10)
	sendVelocityPacket(ALOAD("3"))
	stop()
}

fun sendVelocityPacket(entity: Entity?) {
	if (entity is EntityPlayerMP) entity.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(entity))
}

@HookReplacer(correctStaticIndexes = true, removePop = true)
fun getShedPattern(static: SheddingHandler, entity: Entity): ShedPattern? {
	// ALOAD 2 because *for* creates Iterator variable
	startFROM()
	ALOAD<ShedPattern>("2").EntityClass.isInstance(entity)
	startTO()
	ALOAD<ShedPattern>("2").EntityClass.equals(entity::class.java)
	stop()
	
	return null
}

@HookReplacer(removePop = true)
fun getCanSpawnHere(entity: EntityAnimal): Boolean {
	startFROM()
	entity.worldObj.getBlock(ILOAD("1"), ILOAD("2") - 1, ILOAD("3"))
	startTO()
	checkBlockForSpawn(entity, ILOAD("1"), ILOAD("2") - 1, ILOAD("3"))
	stop()
	
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
	POP("mana")
	startTO()
	POP("primalmana")
	stop()
}

@HookReplacer
fun updateTick(block: BlockDynamicLiquid, world: World, x: Int, y: Int, z: Int, rand: Random) {
	startFROM()
	POP(Blocks.stone)
	startTO()
	POP(getStoneBlock(world))
	stop()
}

fun getStoneBlock(world: World): Block = 
	if (world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim) ModBlocks.livingrock else Blocks.stone

@HookReplacer
fun func_149805_n(block: BlockLiquid, world: World, x: Int, y: Int, z: Int) {
	startFROM()
	POP(Blocks.cobblestone)
	startTO()
	POP(getCobblestoneBlock(world))
	stop()
}

fun getCobblestoneBlock(world: World): Block =
	if (world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim) AlfheimBlocks.livingcobble else Blocks.cobblestone

@HookReplacer
fun onWornTick(item: ItemWaterRing, stack: ItemStack?, player: EntityLivingBase?) {
	startFROM()
	POP(-42)
	startTO()
	POP(0)
	stop()
}

@HookReplacer(targetMethod = "onWornTick")
fun constantPotionApply(item: ItemWaterRing, stack: ItemStack?, player: EntityLivingBase) {
	startFROM()
	POP(player.getActivePotionEffect(Potion.nightVision))
	startTO()
	POP(null)
	stop()
}

@HookReplacer
fun onUnequipped(item: ItemWaterRing, stack: ItemStack?, player: EntityLivingBase?) {
	startFROM()
	POP(-42)
	startTO()
	POP(0)
	stop()
}

@HookReplacer(removePop = true, onlyNthMatches = [2])
fun matches(recipe: CompositeLensRecipe, inv: InventoryCrafting?, world: World?): Boolean {
	startFROM()
	ALOAD<ItemStack>("7").item
	startTO()
	asSlimeBall(ALOAD("7"))
	stop()
	
	return false
}

fun asSlimeBall(stack: ItemStack): Item {
	for (i in OreDictionary.getOreIDs(stack)) {
		if (OreDictionary.getOreName(i) == "slimeball")
			return Items.slime_ball
	}
	
	return Items.cauldron
}

@HookReplacer(removePop = true)
fun onUpdate(tile: SubTileNarslimmus) {
	startFROM()
	ALOAD<EntitySlime>("3").entityData.getBoolean(SubTileNarslimmus.TAG_WORLD_SPAWNED)
	startTO()
	canEat(ALOAD("3"))
	stop()
}

fun canEat(slime: EntitySlime) = slime.entityData.getBoolean(SubTileNarslimmus.TAG_WORLD_SPAWNED) && slime.slimeSize <= 4

@HookReplacer.CreateHRG(name = "craftyCrateUnclog")
@HookReplacer(targetMethod = "craft", removePop = true, mandatoryGroups = ["craftyCrateUnclog"])
fun craftVanilla(tile: TileCraftCrate, fullCheck: Boolean): Boolean {
	startFROM()
	tile.setInventorySlotContents(9, ALOAD<IRecipe>("5").getCraftingResult(ALOAD("2")))
	startTO()
	combineOrEject(tile, ALOAD<IRecipe>("5").getCraftingResult(ALOAD("2")))
	stop()
	
	return false
}

@HookReplacer(targetMethod = "craft", removePop = true, mandatoryGroups = ["craftyCrateUnclog"])
fun craftGTNH(tile: TileCraftCrate, fullCheck: Boolean): Boolean {
	startFROM()
	tile.setInventorySlotContents(9, ALOAD("3"))
	startTO()
	combineOrEject(tile, ALOAD("3"))
	stop()
	
	return false
}

fun combineOrEject(tile: TileCraftCrate, craftingResult: ItemStack) {
	val at = tile[9]
	if (at == null) {
		tile[9] = craftingResult
		return
	}
	
	if (at.stackSize + craftingResult.stackSize <= at.maxStackSize && at.isItemEqual(craftingResult) && ItemStack.areItemStackTagsEqual(craftingResult, at)) {
		at.stackSize += craftingResult.stackSize
		return
	}
	
	if (!tile.worldObj.isRemote) EntityItem(tile.worldObj, tile.xCoord + 0.5, tile.yCoord - 0.5, tile.zCoord + 0.5, at).apply { setMotion(0.0) }.spawn()
	tile[9] = craftingResult
}
//@formatter:on