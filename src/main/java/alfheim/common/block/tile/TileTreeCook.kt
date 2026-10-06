package alfheim.common.block.tile

// PORT: импорты 1.20.1 (блок-сущность и переплавка 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.*
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.item.ItemEntity as EntityItem
import net.minecraft.world.level.block.state.BlockState

// PORT: блок-сущность 1.20.1 создаётся сразу в своей точке и со своим типом (alfheim.port.legacy.TileEntity)
class TileTreeCook(pos: BlockPos, state: BlockState): TileEntity(legacyTileType<TileTreeCook>(), pos, state) {
//class TileTreeCook: TileEntity() {
	
	override fun updateEntity() {
		if (worldObj.totalWorldTime % 20 != 0L) return
		getEntitiesWithinAABB(worldObj, EntityItem::class.java, boundingBox(8)).forEach {
			// PORT: еда 1.7.10 — класс ItemFood, в 1.20.1 — свойство предмета (isEdible); stackSize — count
			if (!it.entityItem.item.isEdible || it.entityItem.count < 1) return@forEach
//			if (it.entityItem.item !is ItemFood || it.entityItem.stackSize < 1) return@forEach
			
			// PORT: рецепты печи 1.20.1 — у мира
			val result = FurnaceRecipes.smelting().getSmeltingResult(it.entityItem, worldObj) ?: return@forEach
//			val result = FurnaceRecipes.smelting().getSmeltingResult(it.entityItem) ?: return@forEach
			if (!result.item.isEdible) return@forEach
//			if (result.item !is ItemFood) return@forEach
			
			if (worldObj.isRemote) {
				for (i in 0..1) worldObj.spawnParticle("lava", it.posX, it.posY, it.posZ, 0.0, 0.0, 0.0)
				
				return
			} else {
				if (EntityItem(worldObj, it.posX, it.posY, it.posZ, result.copy()).spawn()) {
					it.entityItem.shrink(1)
//					--it.entityItem.stackSize
					
					return
				}
			}
		}
	}
}