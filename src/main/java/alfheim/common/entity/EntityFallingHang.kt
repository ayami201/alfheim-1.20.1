package alfheim.common.entity

// PORT: импорты 1.20.1 (существо 1.7.10 — alfheim.port.legacy.Entity1710, MAPPING.md)
import alexsocol.asjlib.*
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import kotlin.math.max

// PORT: тип существа 1.20.1 — первый аргумент конструктора (legacyType)
class EntityFallingHang(world: World): Entity1710(legacyType<EntityFallingHang>(), world) {
//class EntityFallingHang(world: World): Entity(world) {
	
	var meta
		get() = air
		set(value) {
			air = value
		}
	
	// PORT: DataWatcher 1.7.10 (номер 2) → SynchedEntityData 1.20.1 (ключ BLOCK); имя блока — id в реестре 1.20.1,
	// неизвестное имя — камень, как у getBlockFromName 1.7.10
	var block: Block
		get() = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(entityData.get(BLOCK))).orElse(null) ?: Blocks.STONE
		set(value) = entityData.set(BLOCK, BuiltInRegistries.BLOCK.getKey(value).toString())
//		get() = Block.getBlockFromName(dataWatcher.getWatchableObjectString(2)) ?: Blocks.stone
//		set(value) = dataWatcher.updateObject(2, Block.blockRegistry.getNameForObject(value))
	
	var blockName: String
		get() = entityData.get(BLOCK)
		set(value) = entityData.set(BLOCK, value)
//		get() = dataWatcher.getWatchableObjectString(2)
//		set(value) = dataWatcher.updateObject(2, value)
	
	init {
		setSize(1f, 1f)
	}
	
	override fun onEntityUpdate() {
		super.onEntityUpdate()
		
		motionY -= 0.04
		
		moveEntity(motionX, motionY, motionZ)
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, getBoundingBox(posX, posY, posZ, lastTickPosX, lastTickPosY, lastTickPosZ).expand(0.5)).apply {
			forEach {
				// PORT: DamageSource.fallingBlock — тип урона 1.20.1 «падающий блок», без виновника, как в 1.7.10
				it.attackEntityFrom(worldObj.damageSource(DamageTypes.FALLING_BLOCK), max(fallDistance * 2, 40f))
//				it.attackEntityFrom(DamageSource.fallingBlock, max(fallDistance * 2, 40f))
			}
			
			if (isNotEmpty()) isCollided = true
		}
		
		if (!isCollided) return
		
		setDead()
		// PORT: playAuxSFX 2001 (звук и частицы поломки блока) → levelEvent 2001 с состоянием блока: блок и metadata —
		// блок своего варианта (variant1710)
		worldObj.levelEvent(2001, BlockPos(posX.mfloor(), posY.mfloor(), posZ.mfloor()), Block.getId(block.variant1710(meta).defaultBlockState()))
//		worldObj.playAuxSFX(2001, posX.mfloor(), posY.mfloor(), posZ.mfloor(), Block.getIdFromBlock(block) + (meta shl 12))
	}
	
	// PORT: entityInit → defineSynchedData (Entity1710)
	override fun entityInit() {
		entityData.define(BLOCK, "minecraft:stone")
//		dataWatcher.addObject(2, "minecraft:stone")
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey(TAG_BLOCK))
			blockName = nbt.getString(TAG_BLOCK)
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setString(TAG_BLOCK, blockName)
	}
	
	companion object {
		const val TAG_BLOCK = "block"
		
		// PORT: ключ данных существа 1.20.1 — вместо номера DataWatcher 2
		private val BLOCK = SynchedEntityData.defineId(EntityFallingHang::class.java, EntityDataSerializers.STRING)
	}
}
