package alfheim.common.core.asm.hook.replacer;

import alexsocol.asjlib.ExtensionsKt;
import com.KAIIIAK.classManipulators.HookReplacer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.elementium.ItemElementiumPick;
import vazkii.botania.common.item.material.ItemManaResource;

import static com.KAIIIAK.classManipulators.HookReplacer.Replacer.*;

public class HookReplacerHandler {
	
	@HookReplacer
	public static void removeBlockWithDrops(ToolCommons tc, EntityPlayer player, ItemStack stack, World world, int x, int y, int z, int bx, int by, int bz, Block block, Material[] materialsListing, boolean silk, int fortune, float blockHardness, boolean dispose, boolean particles) {
		startFROM();
		POPLine();POP(ItemElementiumPick.isDisposable(ALOAD("17" /*16+1*/)));
		POPLine();startTO();
		POPLine();POP(isDisposable(ALOAD("17" /*16+1*/), world.getBlockMetadata(x, y, z)));
		POPLine();stop();
	}
	
	@HookReplacer(targetMethod = "onHarvestDrops")
	public static void replaceDisposable(ItemElementiumPick item, BlockEvent.HarvestDropsEvent event) {
		startFROM();
		POPLine();POP(ItemElementiumPick.isDisposable(ALOAD("5")));
		POPLine();startTO();
		POPLine();POP(isDisposable(ALOAD("4")));
		POPLine();stop();
	}
	
	@HookReplacer(targetMethod = "onHarvestDrops")
	public static void replaceSemiDisposable(ItemElementiumPick item, BlockEvent.HarvestDropsEvent event) {
		startFROM();
		POPLine();POP(ItemElementiumPick.isSemiDisposable(ALOAD("5")));
		POPLine();startTO();
		POPLine();POP(isSemiDisposable(ALOAD("4")));
		POPLine();stop();
	}
	
	public static boolean isDisposable(Block block, int meta) {
		return isDisposable(new ItemStack(block, 1, meta));
	}
	
	public static boolean isDisposable(ItemStack stack) {
		if (ExtensionsKt.getBlock(stack) == Blocks.air) return false;
		
		for (int id : OreDictionary.getOreIDs(stack)) {
			String name = OreDictionary.getOreName(id);
			if (BotaniaAPI.disposableBlocks.contains(name))
				return true;
		}
		return false;
	}
	
	public static boolean isSemiDisposable(ItemStack stack) {
		if (ExtensionsKt.getBlock(stack) == Blocks.air) return false;
		
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
		POPLine();POP(event.entityPlayer);POP(false);
		POPLine();startTO();
		POPLine();POP(event.entityPlayer);POP(true);
		POPLine();stop();
	}
	
	@HookReplacer(targetMethod = "onPlayerInteract")
	public static void fixRange(ItemManaResource item, PlayerInteractEvent event) {
		startFROM();
		POPLine();POP(5.0D);
		POPLine();startTO();
		POPLine();POP(getReach(event.entityPlayer));
		POPLine();stop();
	}
	
	public static double getReach(EntityPlayer player) {
		return player == null
					   ? 0.0
					   : player instanceof EntityPlayerMP
								 ? ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance()
								 : (double) Minecraft.getMinecraft().playerController.getBlockReachDistance();
	}
}
