package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import net.minecraft.item.*
import net.minecraft.nbt.*
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.WeightedRandomChestContent
import net.minecraft.world.World
import net.minecraftforge.common.ChestGenHooks
import java.util.*
import alfheim.common.block.AlfheimBlocks as alfheim
import alfheim.common.item.AlfheimItems as alfheimi
import net.minecraft.init.Blocks as minecraft
import net.minecraft.init.Items as minecrafti
import vazkii.botania.common.block.ModBlocks as Botania
import vazkii.botania.common.item.ModItems as Botaniai

object WorldGenIglu {
	
	private const val y = 32
	
	fun generate(world: World, random: Random, x: Int, z: Int) {
		val type = when(random.nextInt(10)) {
			in 0..1 -> 0 // broken
			in 2..6 -> 1 // small
			else    -> 2 // big
		}
		
		when (type) {
			0 -> {
				for (i in x.bidiRange(13))
					for (k in z.bidiRange(13))
						for (j in y..y+9)
							if (!world.isAirBlock(i, j, k))
								return
				
				SchemaUtils.generate(world, x, y, z, "${ModInfo.MODID}/schemas/niflheim/worldgen_5", true, true, true, random.nextInt(3)) // broken
				searchAndGenChests(world, random, x, z, 13, 9)
			}
			1 -> {
				for (i in x.bidiRange(5))
					for (k in z.bidiRange(5))
						for (j in y..y+4)
							if (!world.isAirBlock(i, j, k))
								return
				
				SchemaUtils.generate(world, x, y, z, "${ModInfo.MODID}/schemas/niflheim/worldgen_6", true, true, true, random.nextInt(3)) // small
				searchAndGenChests(world, random, x, z, 5, 4)
			}
			2 -> {
				for (i in x.bidiRange(8))
					for (k in z.bidiRange(8))
						for (j in y..y+5)
							if (!world.isAirBlock(i, j, k))
								return
				
				SchemaUtils.generate(world, x, y, z, "${ModInfo.MODID}/schemas/niflheim/worldgen_7", true, true, true, random.nextInt(3)) // big
				searchAndGenChests(world, random, x, z, 8, 5)
			}
		}
	}
	
	private fun searchAndGenChests(world: World, random: Random, x: Int, z: Int, xzR: Int, yR: Int) {
		for (i in x.bidiRange(xzR))
			for (k in z.bidiRange(xzR))
				for (j in y..y+yR)
					(world.getTileEntity(i, j, k) as? TileEntityChest)?.let {
						WeightedRandomChestContent.generateChestContents(random, ChestGenHooks.getItems(NIFLHEIM_IGLU, random), it, ChestGenHooks.getCount(NIFLHEIM_IGLU, random))
					}
	}
	
	const val NIFLHEIM_IGLU = "niflheimIglu"
	private val category = ChestGenHooks.getInfo(NIFLHEIM_IGLU)
	
	init {
		category.min = 4
		category.max = 8
		
		add(ItemStack(alfheim.niflheimBlock, 1, 1), 1, 2, 80)
		add(ItemStack(alfheim.niflheimBlock, 1, 1), 2, 4, 60)
		add(ItemStack(alfheim.niflheimBlock, 1, 1), 4, 8, 40)
		add(ItemStack(Botania.customBrick, 1, 2), 1, 2, 80)
		add(ItemStack(Botania.customBrick, 1, 2), 2, 4, 60)
		add(ItemStack(Botania.customBrick, 1, 2), 4, 8, 40)
		add(ItemStack(minecraft.log), 1, 1, 80)
		add(ItemStack(minecraft.log), 1, 2, 60)
		add(ItemStack(minecraft.log), 2, 4, 40)
		add(ItemStack(alfheim.altWood1, 1, 3), 1, 1, 80)
		add(ItemStack(alfheim.altWood1, 1, 3), 1, 2, 60)
		add(ItemStack(alfheim.altWood1, 1, 3), 2, 4, 40)
		
		add(ItemStack(alfheimi.elvenResource, 1, 29), 1, 3, 60)
		add(ItemStack(alfheimi.elvenResource, 1, 29), 2, 6, 30)
		add(ItemStack(alfheimi.elvenResource, 1, 29), 3, 9, 10)
		add(ItemStack(Botaniai.manaBottle), 1, 1, 50)
		add(ItemStack(Botaniai.manaBottle), 1, 2, 25)
		add(ItemStack(Botaniai.manaTablet) { "mana:100000" }, 1, 1, 15)
		add(ItemStack(Botaniai.manaTablet) { "mana:150000" }, 1, 1, 10)
		add(ItemStack(Botaniai.manaTablet) { "mana:200000" }, 1, 1, 5)
		add(ItemStack(Botaniai.manaGun), 1, 1, 10)
		add(ItemStack(Botaniai.lens, 1, 15), 1, 1, 10)
		add(ItemStack(minecrafti.leather), 1, 4, 80)
		add(ItemStack(minecrafti.leather), 2, 6, 60)
		add(ItemStack(minecrafti.leather), 4, 8, 40)
		add(ItemStack(minecrafti.flint_and_steel), 1, 1, 10)
		add(ItemStack(minecrafti.fire_charge), 1, 1, 90)
		add(ItemStack(minecrafti.fire_charge), 1, 2, 60)
		add(ItemStack(minecrafti.fire_charge), 2, 4, 30)
		add(ItemStack(minecrafti.flint), 1, 1, 30)
		add(ItemStack(minecrafti.flint), 1, 2, 15)
		add(ItemStack(minecrafti.iron_ingot), 1, 1, 30)
		add(ItemStack(minecrafti.iron_ingot), 1, 2, 15)
		add(ItemStack(minecrafti.iron_ingot), 2, 4, 5)
		add(ItemStack(Botaniai.manaResource, 1, 2), 1, 1, 40)
		add(ItemStack(Botaniai.manaResource, 1, 2), 1, 2, 20)
		add(ItemStack(minecrafti.diamond), 1, 1, 30)
		add(ItemStack(minecrafti.diamond), 1, 2, 15)
		
		add(ItemStack(Botaniai.manaResource), 1, 1, 40)
		add(ItemStack(Botaniai.manaResource), 1, 2, 25)
		add(ItemStack(Botaniai.manaResource), 2, 4, 10)
		add(ItemStack(minecrafti.gunpowder), 1, 3, 40)
		add(ItemStack(minecrafti.gunpowder), 2, 6, 25)
		add(ItemStack(minecrafti.gunpowder), 3, 9, 10)
		add(ItemStack(minecrafti.coal), 1, 3, 40)
		add(ItemStack(minecrafti.coal), 2, 6, 25)
		add(ItemStack(minecrafti.coal), 3, 9, 10)
		add(ItemStack(Botaniai.manaResource, 1, 16), 1, 2, 40)
		add(ItemStack(Botaniai.manaResource, 1, 16), 2, 4, 25)
		add(ItemStack(Botaniai.manaResource, 1, 16), 3, 6, 10)
		add(ItemStack(Botaniai.manaResource, 1, 1), 1, 1, 50)
		add(ItemStack(Botaniai.manaResource, 1, 1), 1, 2, 35)
		add(ItemStack(Botaniai.manaResource, 1, 1), 2, 4, 20)
		add(ItemStack(minecrafti.blaze_powder), 1, 1, 50)
		add(ItemStack(minecrafti.blaze_powder), 1, 2, 25)
		
		add(ItemStack(minecraft.torch), 1, 2, 30)
		add(ItemStack(minecraft.torch), 2, 4, 15)
		add(ItemStack(minecraft.torch), 4, 8, 5)
		add(ItemStack(alfheimi.elvenFood, 1, 15), 1, 1, 30)
		add(ItemStack(alfheimi.elvenFood, 1, 15), 1, 2, 15)
		add(ItemStack(minecrafti.leather_helmet), 1, 1, 10)
		add(ItemStack(minecrafti.leather_helmet, 1, 25), 1, 1, 10)
		add(ItemStack(minecrafti.leather_helmet, 1, 50), 1, 1, 10)
		add(ItemStack(minecrafti.leather_chestplate), 1, 1, 10)
		add(ItemStack(minecrafti.leather_chestplate, 1, 40), 1, 1, 10)
		add(ItemStack(minecrafti.leather_chestplate, 1, 70), 1, 1, 10)
		add(ItemStack(minecrafti.leather_leggings), 1, 1, 10)
		add(ItemStack(minecrafti.leather_leggings, 1, 35), 1, 1, 10)
		add(ItemStack(minecrafti.leather_leggings, 1, 65), 1, 1, 10)
		add(ItemStack(minecrafti.leather_boots), 1, 1, 10)
		add(ItemStack(minecrafti.leather_boots, 1, 25), 1, 1, 10)
		add(ItemStack(minecrafti.leather_boots, 1, 55), 1, 1, 10)
		add(ItemStack(alfheimi.elfIcePendant), 1, 1, 5)
		
		add(ItemStack(minecrafti.potionitem, 1, 8226), 1, 1, 20)
		add(ItemStack(minecrafti.potionitem, 1, 8229), 1, 1, 20)
		add(ItemStack(minecrafti.potionitem, 1, 8233), 1, 1, 20)
		add(ItemStack(minecrafti.potionitem, 1, 8257), 1, 1, 20)
		add(ItemStack(minecrafti.potionitem, 1, 8262), 1, 1, 20)
		add(ItemStack(minecrafti.potionitem, 1, 8269), 1, 1, 20)
		add(ItemStack(minecrafti.potionitem, 1, 16424), 1, 1, 20)
		add(ItemStack(minecrafti.potionitem, 1, 16426), 1, 1, 20)
		add(ItemStack(Botaniai.brewVial) { "brewKey:\"speed\"" }, 1, 1, 15)
		add(ItemStack(Botaniai.brewVial) { "brewKey:\"strength\"" }, 1, 1, 15)
		add(ItemStack(Botaniai.brewVial) { "brewKey:\"regenWeak\"" }, 1, 1, 15)
		add(ItemStack(Botaniai.brewVial) { "brewKey:\"jumpBoost\"" }, 1, 1, 10)
		add(ItemStack(Botaniai.brewVial) { "brewKey:\"haste\"" }, 1, 1, 10)
		add(ItemStack(Botaniai.brewVial) { "brewKey:\"resistance\"" }, 1, 1, 10)
		add(ItemStack(Botaniai.brewVial) { "brewKey:\"absorption\"" }, 1, 1, 10)
		add(ItemStack(alfheimi.splashPotion) { "brewKey:\"healing\"" }, 1, 1, 15)
		
		add(ItemStack(alfheimi.elvenFood, 1, 4), 1, 1, 50)
		add(ItemStack(alfheimi.elvenFood, 1, 4), 1, 2, 40)
		add(ItemStack(alfheimi.elvenFood, 1, 5), 1, 1, 50)
		add(ItemStack(alfheimi.elvenFood, 1, 5), 1, 2, 40)
		add(ItemStack(alfheimi.elvenFood, 1, 6), 1, 1, 50)
		add(ItemStack(alfheimi.elvenFood, 1, 6), 1, 2, 40)
		add(ItemStack(minecrafti.cooked_fished), 1, 3, 40)
		add(ItemStack(minecrafti.cooked_fished), 2, 4, 30)
		add(ItemStack(minecrafti.bread), 1, 3, 40)
		add(ItemStack(minecrafti.bread), 2, 4, 30)
		add(ItemStack(minecrafti.mushroom_stew), 1, 3, 40)
		add(ItemStack(minecrafti.mushroom_stew), 2, 4, 30)
		add(ItemStack(minecrafti.cooked_chicken), 1, 3, 40)
		add(ItemStack(minecrafti.cooked_chicken), 2, 4, 30)
		add(ItemStack(minecrafti.baked_potato), 1, 3, 40)
		add(ItemStack(minecrafti.baked_potato), 2, 4, 30)
		add(ItemStack(alfheimi.elvenFood, 1, 8), 1, 3, 40)
		add(ItemStack(alfheimi.elvenFood, 1, 8), 2, 4, 30)
		add(ItemStack(minecrafti.cooked_fished, 1, 1), 1, 3, 40)
		add(ItemStack(minecrafti.cooked_fished, 1, 1), 2, 4, 30)
		add(ItemStack(minecrafti.pumpkin_pie), 1, 2, 35)
		add(ItemStack(minecrafti.pumpkin_pie), 1, 3, 30)
		add(ItemStack(minecrafti.pumpkin_pie), 2, 4, 25)
		add(ItemStack(minecrafti.cooked_porkchop), 1, 2, 30)
		add(ItemStack(minecrafti.cooked_porkchop), 1, 3, 25)
		add(ItemStack(minecrafti.cooked_beef), 1, 2, 30)
		add(ItemStack(minecrafti.cooked_beef), 1, 3, 25)
		add(ItemStack(alfheimi.elvenFood, 1, 9), 1, 1, 25)
		add(ItemStack(alfheimi.elvenFood, 1, 9), 1, 2, 20)
		add(ItemStack(alfheimi.elvenFood), 1, 1, 15)
		add(ItemStack(alfheimi.elvenFood), 1, 2, 10)
		add(ItemStack(Botaniai.manaCookie), 1, 1, 10)
		add(ItemStack(Botaniai.manaCookie), 1, 2, 5)
		
		add(ItemStack(minecrafti.iron_helmet, 1, 50), 1, 1, 10)
		add(ItemStack(minecrafti.iron_helmet, 1, 150), 1, 1, 10)
		add(ItemStack(minecrafti.chainmail_chestplate, 1, 120), 1, 1, 10)
		add(ItemStack(minecrafti.chainmail_chestplate, 1, 220), 1, 1, 10)
		add(ItemStack(minecrafti.chainmail_leggings, 1, 100), 1, 1, 10)
		add(ItemStack(minecrafti.chainmail_leggings, 1, 200), 1, 1, 10)
		add(ItemStack(minecrafti.iron_boots, 1, 75), 1, 1, 10)
		add(ItemStack(minecrafti.iron_boots, 1, 175), 1, 1, 10)
		add(ItemStack(minecrafti.compass), 1, 1, 20)
		add(ItemStack(minecrafti.fishing_rod), 1, 1, 20)
		add(ItemStack(minecrafti.fishing_rod, 1, 20), 1, 1, 20)
		add(ItemStack(minecrafti.fishing_rod, 1, 45), 1, 1, 20)
		add(ItemStack(minecrafti.stone_sword), 1, 1, 20)
		add(ItemStack(minecrafti.stone_sword, 1, 60), 1, 1, 20)
		add(ItemStack(minecrafti.stone_sword, 1, 110), 1, 1, 20)
		add(ItemStack(minecrafti.iron_sword), 1, 1, 10)
		add(ItemStack(minecrafti.iron_sword, 1, 100), 1, 1, 10)
		add(ItemStack(minecrafti.iron_sword, 1, 200), 1, 1, 10)
		add(ItemStack(Botaniai.elementiumSword), 1, 1, 5)
		add(ItemStack(Botaniai.elementiumSword, 1, 250), 1, 1, 5)
		add(ItemStack(Botaniai.elementiumSword, 1, 500), 1, 1, 5)
		add(ItemStack(minecrafti.stone_pickaxe), 1, 1, 20)
		add(ItemStack(minecrafti.stone_pickaxe, 1, 60), 1, 1, 20)
		add(ItemStack(minecrafti.stone_pickaxe, 1, 110), 1, 1, 20)
		add(ItemStack(minecrafti.iron_pickaxe), 1, 1, 10)
		add(ItemStack(minecrafti.iron_pickaxe, 1, 100), 1, 1, 10)
		add(ItemStack(minecrafti.iron_pickaxe, 1, 200), 1, 1, 10)
		add(ItemStack(Botaniai.elementiumPick), 1, 1, 5)
		add(ItemStack(Botaniai.elementiumPick, 1, 250), 1, 1, 5)
		add(ItemStack(Botaniai.elementiumPick, 1, 500), 1, 1, 5)
		add(ItemStack(minecrafti.arrow), 2, 4, 40)
		add(ItemStack(minecrafti.arrow), 4, 8, 25)
		add(ItemStack(minecrafti.arrow), 8, 16, 10)
		add(ItemStack(minecrafti.bucket), 1, 1, 30)
		add(ItemStack(minecrafti.bucket), 1, 2, 20)
		add(ItemStack(minecrafti.lava_bucket), 1, 1, 15)
		
		add(ItemStack(minecrafti.book), 1, 2, 45)
		add(ItemStack(minecrafti.book), 1, 3, 35)
		add(ItemStack(minecrafti.book), 2, 4, 25)
		add(ItemStack(minecrafti.bone), 1, 1, 50)
		add(ItemStack(minecrafti.bone), 1, 2, 45)
		add(ItemStack(minecrafti.bone), 1, 3, 40)
		add(ItemStack(minecrafti.rotten_flesh), 1, 2, 60)
		add(ItemStack(minecrafti.rotten_flesh), 2, 4, 50)
		add(ItemStack(minecrafti.rotten_flesh), 3, 6, 40)
		add(ItemStack(Botaniai.signalFlare) { "color:4" }, 1, 1, 10)
		add(ItemStack(Botaniai.signalFlare) { "color:5" }, 1, 1, 10)
		add(ItemStack(Botaniai.signalFlare) { "color:6" }, 1, 1, 10)
		add(ItemStack(Botaniai.signalFlare) { "color:14" }, 1, 1, 10)
		add(ItemStack(minecrafti.bow), 1, 1, 25)
		add(ItemStack(minecrafti.bow, 1, 160), 1, 1, 25)
		add(ItemStack(minecrafti.bow, 1, 320), 1, 1, 25)
		add(ItemStack(minecrafti.bow)         { "ench:[{lvl:1s,id:34s},{lvl:1s,id:48s}]" }, 1, 1, 25)
		add(ItemStack(minecrafti.bow, 1, 160) { "ench:[{lvl:1s,id:34s},{lvl:1s,id:48s}]" }, 1, 1, 25)
		add(ItemStack(minecrafti.bow, 1, 320) { "ench:[{lvl:1s,id:34s},{lvl:1s,id:48s}]" }, 1, 1, 25)
		add(ItemStack(minecrafti.bow)         { "ench:[{lvl:3s,id:48s},{lvl:1s,id:50s}]" }, 1, 1, 25)
		add(ItemStack(minecrafti.bow, 1, 160) { "ench:[{lvl:3s,id:48s},{lvl:1s,id:50s}]" }, 1, 1, 25)
		add(ItemStack(minecrafti.bow, 1, 320) { "ench:[{lvl:3s,id:48s},{lvl:1s,id:50s}]" }, 1, 1, 25)
		add(ItemStack(alfheimi.coatOfArms, 1, 5), 1, 1, 1)
		add(ItemStack(alfheimi.warBanner), 1, 1, 1)
		add(ItemStack(alfheimi.warBanner, 1, 1), 1, 1, 1)
		add(ItemStack(Botania.tinyPotato), 1, 1, 1)
	}
	
	private fun add(stack: ItemStack, min: Int, max: Int, weight: Int) {
		category.addItem(WeightedRandomChestContent(stack, min, max, weight))
	}
	
	private fun ItemStack(item: Item, count: Int = 1, meta: Int = 0, nbt: () -> String? = { null }): ItemStack {
		val stack = net.minecraft.item.ItemStack(item, count, meta)
		if (nbt() == null) return stack
		stack.tagCompound = JsonToNBT.func_150315_a("{${nbt()}}") as? NBTTagCompound ?: return stack
		return stack
	}
}
