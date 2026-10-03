package alfheim.common.item.material

// PORT: импорты 1.20.1; импорты механик КТ-3 закомментированы вместе с их строками
import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.item.ItemMod
import alfheim.port.legacy.StatCollector
import net.minecraft.network.chat.Component
import net.minecraft.world.item.*
import net.minecraft.world.level.Level as World
//import net.minecraft.entity.item.EntityItem
//import net.minecraft.inventory.IInventory
//import net.minecraft.tileentity.TileEntity
//import vazkii.botania.api.internal.VanillaPacketDispatcher
//import vazkii.botania.api.item.IManaDissolvable
//import vazkii.botania.api.mana.IManaPool
//import vazkii.botania.api.recipe.IFlowerComponent
//import vazkii.botania.common.Botania

// PORT: вариант metadata — отдельный предмет (SPEC, Р-5): 0 — увядший лотос, 1 — увядший чёрный лотос
// (alfheim:wilted_lotus0, alfheim:wilted_lotus1). КТ-3 — бассейн маны (IManaDissolvable) и аптекарь лепестков (IFlowerComponent)
class ItemWiltedLotus(val meta: Int): ItemMod("wiltedLotus")/*, IManaDissolvable, IFlowerComponent*/ {
	
	override val variant get() = meta
	
	init {
		setHasSubtypes(true)
	}
	
	// PORT: вариант — отдельный предмет: каждый выдаёт во вкладку только свою вещь
	override fun getSubItems(item: Item, tab: Any?, list: MutableList<Any?>) {
		for (i in 0..1) if (i == meta) list.add(ItemStack(item))
//		for (i in 0..1) list.add(ItemStack(item, 1, i))
	}
	
	override fun hasEffect(par1ItemStack: ItemStack, pass: Int) = par1ItemStack.meta > 0
	
	// PORT: addInformation → appendHoverText, строка подсказки — Component
	override fun appendHoverText(stack: ItemStack, world: World?, list: MutableList<Component>, adv: TooltipFlag) {
		list.add(Component.literal(StatCollector.translateToLocal("misc.${ModInfo.MODID}:lotusDesc")))
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		super.getUnlocalizedNameInefficiently(stack) + stack.meta
	
	/* PORT: КТ-3 — аптекарь лепестков и бассейн маны Botania
	override fun canFit(stack: ItemStack?, apothecary: IInventory?) = true
	
	override fun getParticleColor(stack: ItemStack?) = 0
	
	override fun onDissolveTick(pool: IManaPool, stack: ItemStack, item: EntityItem) {
		if (pool.isFull || pool.currentMana == 0) return
		
		val tile = pool as TileEntity
		val t2 = stack.meta > 0
		
		val mult = if (item.worldObj.rand.nextBoolean()) 2 else -1
		val mana = if (t2) MANA_PER_T2 else MANA_PER
		
		if (mult == -1 && pool.currentMana < mana)
			return
		
		if (!item.worldObj.isRemote) {
			pool.recieveMana(mult * mana)
			stack.stackSize--
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(item.worldObj, tile.xCoord, tile.yCoord, tile.zCoord)
		}
		
		for (i in 0..49) {
			val r = Math.random().F * 0.25f
			val g = 0f
			val b = Math.random().F * 0.25f
			val s = 0.45f * Math.random().F * 0.25f
			val m = 0.045f
			val mx = (Math.random().F - 0.5f) * m
			val my = Math.random().F * m
			val mz = (Math.random().F - 0.5f) * m
			Botania.proxy.wispFX(item.worldObj, item.posX, tile.yCoord + 0.5, item.posZ, r, g, b, s, mx, my, mz)
		}
		
		item.playSoundAtEntity("botania:blackLotus", 0.5f, if (t2) 0.1f else 1f)
	}
	*/
	
	companion object {
		
		const val MANA_PER = 8000
		const val MANA_PER_T2 = 100000
	}
}
