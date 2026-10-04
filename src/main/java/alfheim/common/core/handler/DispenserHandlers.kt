package alfheim.common.core.handler

// PORT: импорты 1.20.1 (MAPPING.md); раздатчик и существа 1.7.10 — alfheim.port.legacy (Dispenser1710, Entity1710)
import alexsocol.asjlib.*
import alfheim.common.entity.*
import alfheim.common.item.AlfheimItems
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.entity.DispenserBlockEntity as TileEntityDispenser
import vazkii.botania.common.item.BotaniaItems as ModItems
import alfheim.common.block.AlfheimBlocks
import alfheim.common.item.material.ElvenResourcesMetas
import vazkii.botania.common.lib.BotaniaTags
//import vazkii.botania.common.block.ModBlocks

/**
 * @author WireSegal
 * Created at 9:28 PM on 2/15/16.
 */
object BifrostFlowerDispenserHandler: IBehaviorDispenseItem {
	
	private val defaultBehavior = BehaviorDefaultDispenseItem()
	
	// PORT: реестр поведения раздатчика 1.20.1 — DispenserBlock.registerBehavior; вариант metadata — отдельный предмет
	// (SPEC, Р-5): поведение — у каждого эльфийского ресурса, как у предмета 1.7.10 со всеми metadata
	init {
		AlfheimItems.elvenResource.forEach { DispenserBlock.registerBehavior(it, this) }
//		BlockDispenser.dispenseBehaviorRegistry.putObject(AlfheimItems.elvenResource, this)
	}
	
	// PORT: сторона раздатчика 1.20.1 — в состоянии блока, а не в metadata; номера сторон те же
	override fun dispense(block: IBlockSource, stack: ItemStack): ItemStack {
//	override fun dispense(block: IBlockSource, stack: ItemStack): ItemStack? {
		if (stack.meta != ElvenResourcesMetas.RainbowDust.I) return defaultBehavior.dispense(block, stack)
		
		val facing = ForgeDirection.getOrientation(block.blockState.getValue(DispenserBlock.FACING).ordinal)
//		val facing = ForgeDirection.getOrientation(BlockDispenser.func_149937_b(block.blockMetadata).ordinal)
		val x = block.xInt + facing.offsetX
		val y = block.yInt + facing.offsetY
		val z = block.zInt + facing.offsetZ
		
		// PORT: мистический цветок Botania (ModBlocks.flower) — 16 блоков с тегом botania:mystical_flowers; вариант радужной
		// травы — блок массива (SPEC, Р-5)
		if (!block.world.getBlock(x, y, z).defaultBlockState().`is`(BotaniaTags.Blocks.MYSTICAL_FLOWERS)) return stack
//		if (block.world.getBlock(x, y, z) !== ModBlocks.flower) return stack
		
		block.world.setBlock(x, y, z, AlfheimBlocks.rainbowGrass[2].defaultBlockState(), 3)
//		block.world.setBlock(x, y, z, AlfheimBlocks.rainbowGrass, 2, 3)
		block.world.playSoundEffect(x.D, y.D, z.D, "botania:enchanterEnchant", 1f, 1f)
		stack.shrink(1)
//		stack.stackSize--
		return stack
	}
}

object ThrownPotionDispenserHandler: IBehaviorDispenseItem {
	
	// PORT: реестр поведения раздатчика 1.20.1 — DispenserBlock.registerBehavior
	init {
		DispenserBlock.registerBehavior(AlfheimItems.splashPotion, this)
//		BlockDispenser.dispenseBehaviorRegistry.putObject(AlfheimItems.splashPotion, this)
	}
	
	// PORT: сторона раздатчика 1.20.1 — в состоянии блока, а не в metadata; номера сторон те же
	override fun dispense(block: IBlockSource, stack: ItemStack): ItemStack {
		val facing = ForgeDirection.getOrientation(block.blockState.getValue(DispenserBlock.FACING).ordinal)
//		val facing = ForgeDirection.getOrientation(BlockDispenser.func_149937_b(block.blockMetadata).ordinal)
		
		val x = block.xInt + facing.offsetX + 0.5
		val y = block.yInt + facing.offsetY + 0.5
		val z = block.zInt + facing.offsetZ + 0.5
		
		val yaw = when (facing) {
			ForgeDirection.SOUTH -> 0f
			ForgeDirection.WEST  -> 90f
			ForgeDirection.NORTH -> 180f
			ForgeDirection.EAST  -> -90f
			else                 -> 0f
		}
		
		val pitch = when (facing) {
			ForgeDirection.UP   -> -90f
			ForgeDirection.DOWN -> 90f
			else                -> 0f
		}
		
		val potion = EntityThrownPotion(block.world, stack)
		
		stack.shrink(1)
//		--stack.stackSize
		
		potion.setLocationAndAngles(x, y, z, yaw, pitch)
		potion.posX -= (MathHelper.cos(potion.rotationYaw / 180f * Math.PI.F) * 0.16f).D
		potion.posY -= 0.10000000149011612
		potion.posZ -= (MathHelper.sin(potion.rotationYaw / 180f * Math.PI.F) * 0.16f).D
		potion.setPosition(potion.posX, potion.posY, potion.posZ)
		// PORT: yOffset 1.7.10 (сдвиг рисунка по высоте) в 1.20.1 нет; у EntityThrowable он и так 0
//		potion.yOffset = 0f
		val f = 0.4f
		potion.motionX = (-MathHelper.sin(potion.rotationYaw / 180f * Math.PI.F) * MathHelper.cos(potion.rotationPitch / 180f * Math.PI.F) * f).D
		potion.motionZ = (MathHelper.cos(potion.rotationYaw / 180f * Math.PI.F) * MathHelper.cos(potion.rotationPitch / 180f * Math.PI.F) * f).D
		potion.motionY = (-MathHelper.sin((potion.rotationPitch + potion.func_70183_g()) / 180f * Math.PI.F) * f).D
		potion.setThrowableHeading(potion.motionX, potion.motionY, potion.motionZ, potion.func_70182_d(), 1f)
		potion.spawn()
		
		return stack
	}
}

object ThrownItemDispenserHandler: IBehaviorDispenseItem {
	
	// PORT: реестр поведения раздатчика 1.20.1 — DispenserBlock.registerBehavior
	init {
		DispenserBlock.registerBehavior(AlfheimItems.fireGrenade, this)
//		BlockDispenser.dispenseBehaviorRegistry.putObject(AlfheimItems.fireGrenade, this)
	}
	
	// PORT: сторона раздатчика 1.20.1 — в состоянии блока, а не в metadata; номера сторон те же
	override fun dispense(block: IBlockSource, stack: ItemStack): ItemStack {
		val facing = ForgeDirection.getOrientation(block.blockState.getValue(DispenserBlock.FACING).ordinal)
//		val facing = ForgeDirection.getOrientation(BlockDispenser.func_149937_b(block.blockMetadata).ordinal)
		
		val x = block.xInt + facing.offsetX + 0.5
		val y = block.yInt + facing.offsetY + 0.5
		val z = block.zInt + facing.offsetZ + 0.5
		
		val yaw = when (facing) {
			ForgeDirection.SOUTH -> 0f
			ForgeDirection.WEST  -> 90f
			ForgeDirection.NORTH -> 180f
			ForgeDirection.EAST  -> -90f
			else                 -> 0f
		}
		
		val pitch = when (facing) {
			ForgeDirection.UP   -> -90f
			ForgeDirection.DOWN -> 90f
			else                -> 0f
		}
		
		val potion = EntityThrowableItem(block.world)
		
		stack.shrink(1)
//		--stack.stackSize
		
		potion.setLocationAndAngles(x, y, z, yaw, pitch)
		potion.posX -= (MathHelper.cos(potion.rotationYaw / 180f * Math.PI.F) * 0.16f).D
		potion.posY -= 0.10000000149011612
		potion.posZ -= (MathHelper.sin(potion.rotationYaw / 180f * Math.PI.F) * 0.16f).D
		potion.setPosition(potion.posX, potion.posY, potion.posZ)
		// PORT: yOffset 1.7.10 (сдвиг рисунка по высоте) в 1.20.1 нет; у EntityThrowable он и так 0
//		potion.yOffset = 0f
		val f = 0.4f
		potion.motionX = (-MathHelper.sin(potion.rotationYaw / 180f * Math.PI.F) * MathHelper.cos(potion.rotationPitch / 180f * Math.PI.F) * f).D
		potion.motionZ = (MathHelper.cos(potion.rotationYaw / 180f * Math.PI.F) * MathHelper.cos(potion.rotationPitch / 180f * Math.PI.F) * f).D
		potion.motionY = (-MathHelper.sin((potion.rotationPitch + potion.func_70183_g()) / 180f * Math.PI.F) * f).D
		potion.setThrowableHeading(potion.motionX, potion.motionY, potion.motionZ, potion.func_70182_d(), 1f)
		potion.spawn()
		
		return stack
	}
}

object WaterBowlDispenserHandler: BehaviorDefaultDispenseItem() {
	
	private val field_150840_b = BehaviorDefaultDispenseItem()
	
	// PORT: реестр поведения раздатчика 1.20.1 — DispenserBlock.registerBehavior
	init {
		DispenserBlock.registerBehavior(Items.BOWL, this)
//		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bowl, this)
	}
	
	// PORT: dispenseStack → execute; сторона раздатчика — в состоянии блока; источник воды 1.20.1 — вода с уровнем 0
	// (стоячей и текучей воды 1.7.10 в 1.20.1 нет — один блок); предмет стака в 1.20.1 не меняется — вместо
	// func_150996_a возвращается новый стак, его раздатчик кладёт в тот же слот
	override fun execute(block: IBlockSource, stack: ItemStack): ItemStack {
		val enumfacing = block.blockState.getValue(DispenserBlock.FACING)
//		val enumfacing = BlockDispenser.func_149937_b(block.blockMetadata)
		val world = block.world
		val i = block.xInt + enumfacing.frontOffsetX
		val j = block.yInt + enumfacing.frontOffsetY
		val k = block.zInt + enumfacing.frontOffsetZ
		val target = world.getBlock(i, j, k)
		val l = world.getBlockState(BlockPos(i, j, k)).getOptionalValue(LiquidBlock.LEVEL).orElse(-1)
//		val l = world.getBlockMetadata(i, j, k)
		val item = if (target === Blocks.WATER && l == 0) { // no need in check for static water because of block update
//		val item = if (target === Blocks.flowing_water && l == 0) { // no need in check for static water because of block update
			ModItems.waterBowl
		} else {
			return super.execute(block, stack)
//			return super.dispenseStack(block, stack)
		}
		
		stack.shrink(1)
		if (stack.isEmpty) {
			return ItemStack(item)
		} else if ((block.blockTileEntity as TileEntityDispenser).addItem(ItemStack(item)) < 0) {
			this.field_150840_b.dispense(block, ItemStack(item))
		}
//		if (--stack.stackSize == 0) {
//			stack.func_150996_a(item)
//			stack.stackSize = 1
//		} else if ((block.blockTileEntity as TileEntityDispenser).func_146019_a(ItemStack(item)) < 0) {
//			this.field_150840_b.dispense(block, ItemStack(item))
//		}
		
		return stack
	}
}