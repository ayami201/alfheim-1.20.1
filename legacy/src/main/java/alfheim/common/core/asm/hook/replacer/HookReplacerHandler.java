package alfheim.common.core.asm.hook.replacer;

import alexsocol.asjlib.ExtensionsKt;
import alfheim.common.core.handler.AlfheimConfigHandler;
import com.KAIIIAK.classManipulators.HookReplacer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.block.subtile.functional.SubTileTigerseye;
import vazkii.botania.common.block.subtile.generating.SubTileMunchdew;
import vazkii.botania.common.core.handler.SheddingHandler;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.elementium.ItemElementiumPick;
import vazkii.botania.common.item.material.ItemManaResource;

import static com.KAIIIAK.classManipulators.HookReplacer.Replacer.*;

//@formatter:off
@SuppressWarnings("DataFlowIssue")
public class HookReplacerHandler {
	
	@HookReplacer(correctStaticIndexes = true, removePop = true)
	public static void removeBlockWithDrops(ToolCommons tc, EntityPlayer player, ItemStack stack, World world, int x, int y, int z, int bx, int by, int bz, Block block, Material[] materialsListing, boolean silk, int fortune, float blockHardness, boolean dispose, boolean particles) {
		startFROM();
		ItemElementiumPick.isDisposable(ALOAD("16"));
		startTO();
		isDisposable(ALOAD("16"), world.getBlockMetadata(x, y, z));
		stop();
	}
	
	@HookReplacer(targetMethod = "onHarvestDrops", removePop = true)
	public static void replaceDisposable(ItemElementiumPick item, BlockEvent.HarvestDropsEvent event) {
		startFROM();
		ItemElementiumPick.isDisposable(ALOAD("5"));
		startTO();
		isDisposable(ALOAD("4"));
		stop();
	}
	
	@HookReplacer(targetMethod = "onHarvestDrops", removePop = true)
	public static void replaceSemiDisposable(ItemElementiumPick item, BlockEvent.HarvestDropsEvent event) {
		startFROM();
		ItemElementiumPick.isSemiDisposable(ALOAD("5"));
		startTO();
		isSemiDisposable(ALOAD("4"));
		stop();
	}
	
	public static boolean isDisposable(Block block, int meta) {
		return isDisposable(new ItemStack(block, 1, meta));
	}
	
	public static boolean isDisposable(ItemStack stack) {
		if (stack == null || stack.getItem() == null || ExtensionsKt.getBlock(stack) == Blocks.air) return false;
		
		for (int id : OreDictionary.getOreIDs(stack)) {
			String name = OreDictionary.getOreName(id);
			if (BotaniaAPI.disposableBlocks.contains(name))
				return true;
		}
		return false;
	}
	
	public static boolean isSemiDisposable(ItemStack stack) {
		if (stack == null || ExtensionsKt.getBlock(stack) == Blocks.air) return false;
		
		for (int id : OreDictionary.getOreIDs(stack)) {
			String name = OreDictionary.getOreName(id);
			if (BotaniaAPI.semiDisposableBlocks.contains(name))
				return true;
		}
		return false;
	}
	
	@HookReplacer(targetMethod = "onPlayerInteract")
	public static void fixEnderAirDupe(ItemManaResource item, PlayerInteractEvent event) {
		startFROM();
		POP(event.entityPlayer);POP(false);
		startTO();
		POP(event.entityPlayer);POP(true);
		stop();
	}
	
	@HookReplacer(targetMethod = "onPlayerInteract")
	public static void fixRange(ItemManaResource item, PlayerInteractEvent event) {
		startFROM();
		POP(5.0D);
		startTO();
		POP(getReach(event.entityPlayer));
		stop();
	}
	
	public static double getReach(EntityPlayer player) {
		return player == null
					   ? 0.0
					   : player instanceof EntityPlayerMP
								 ? ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance()
								 : (double) Minecraft.getMinecraft().playerController.getBlockReachDistance();
	}
	
	@HookReplacer(isMandatory = false) // Because Mob Stacker: Integration has a hook replacing all logic
	public static void onLivingUpdate(SheddingHandler target, LivingEvent.LivingUpdateEvent event) {
		// ALOAD 3 because hook (AlfheimHookHandler) has ON_TRUE
		startFROM();
		event.entity.entityDropItem(HookReplacer.Replacer.<SheddingHandler.ShedPattern>ALOAD("3").getItemStack(), 0.0F);
		startTO();
		adjustLifespan(event.entity.entityDropItem(HookReplacer.Replacer.<SheddingHandler.ShedPattern>ALOAD("3").getItemStack(), 0.0F));
		stop();
	}
	
	public static void adjustLifespan(EntityItem entityItem) {
		entityItem.lifespan = AlfheimConfigHandler.INSTANCE.getShedLifespan();
	}
	
	@HookReplacer(targetMethod = "onUpdate", removePop = true)
	public static void onUpdate1(SubTileMunchdew tile) {
		startFROM();
		HookReplacer.Replacer.<Block>ALOAD("12").getMaterial();
		startTO();
		Boolean.valueOf(HookReplacer.Replacer.<Block>ALOAD("12").isLeaves(tile.supertile.getWorldObj(), ILOAD("9"), ILOAD("10"), ILOAD("11")));
		stop();
	}
	
	@HookReplacer(targetMethod = "onUpdate")
	public static void onUpdate2(SubTileMunchdew tile) {
		startFROM();
		POP(Material.leaves);
		startTO();
		POP(Boolean.TRUE);
		stop();
	}
	
	@HookReplacer
	public static void onUpdate(SubTileTigerseye tile) {
		startFROM();
		POP(EntityLiving.class);
		startTO();
		POP(EntityCreeper.class);
		stop();
	}
}
//@formatter:on