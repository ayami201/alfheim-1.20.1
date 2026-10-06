package alfheim.common.block.magtrees.nether

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.toItem
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.*

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockNetherPlanks: BlockMod(Material.wood)/*, ILexiconable*/, IFuelHandler {
	
	private val name = "netherPlanks"
	
	init {
		blockHardness = 2F
		setBlockName(name)
		setLightLevel(0.5f)
		stepSound = soundTypeWood
		GameRegistry.registerFuelHandler(this)
	}
	
	override fun isInterpolated() = true
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	override fun shouldRegisterInNameSet() = false
	
	// PORT: лут — сам блок (alfheim.port.data.AlfheimBlockLoot)
//	override fun damageDropped(par1: Int) = par1
	
	override fun setBlockName(name: String): Block {
		register(name)
		return super.setBlockName(name)
	}
	
	// PORT: лут — сам блок, один (alfheim.port.data.AlfheimBlockLoot)
//	override fun quantityDropped(random: Random) = 1
//
//	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = this.toItem()
	
	// PORT: горит ли блок, 1.20.1 решает по таблице огня (FireBlock), её заполняет registerBurnables (AlfheimBlocks):
	// адских блоков в ней нет — они не горят и без этих методов
//	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = false
//
//	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	
	internal fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
		// PORT: поля ванилы 1.20.1 — заглавными
		Blocks.NETHERRACK
//		Blocks.netherrack
	}
	
	/* PORT: выбор колёсиком — предмет блока (getCloneItemStack 1.20.1)
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.netherSapling
	
	// PORT: печь 1.7.10 сжигала деревянный блок 300 тиков раньше обработчиков модов, поэтому 2000 не срабатывало
	// (Fuel1710; BUGS.md, B-027)
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 2000 else 0
}
