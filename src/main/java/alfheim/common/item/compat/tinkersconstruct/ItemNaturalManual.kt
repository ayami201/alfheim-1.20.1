package alfheim.common.item.compat.tinkersconstruct

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.integration.tinkersconstruct.TinkersConstructAlfheimModule
import alfheim.common.item.ItemMod
import alfheim.common.item.material.ElvenResourcesMetas
import cpw.mods.fml.client.FMLClientHandler
import cpw.mods.fml.relauncher.*
import mantle.books.BookData
import mantle.client.MProxyClient
import mantle.client.gui.GuiManual
import mantle.lib.client.MantleClientRegistry.registerManualIcon
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import org.w3c.dom.Document
import tconstruct.TConstruct
import tconstruct.library.TConstructRegistry
import tconstruct.library.client.TConstructClientRegistry.registerManualModifier
import tconstruct.library.crafting.ToolBuilder
import tconstruct.tools.TinkerTools
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.item.ModItems
import javax.xml.parsers.DocumentBuilderFactory

class ItemNaturalManual: ItemMod("NaturalManual") {
	
	init {
		creativeTab = TConstructRegistry.materialTab
		
		if (ASJUtilities.isClient) registerIcons()
	}
	
	fun registerIcons() {
		val pick = ToolBuilder.instance.buildTool(ItemStack(TinkerTools.pickaxeHead, 1, 6), ItemStack(TinkerTools.toolRod, 1, 2), ItemStack(TinkerTools.binding, 1, 6), "")
		registerManualModifier("manarepair", pick, ElvenResourcesMetas.ManaInfusionCore.stack)
		
		registerManualIcon("elementium", ItemStack(ModItems.manaResource, 1, 7))
		registerManualIcon("elvorium", ElvenResourcesMetas.ElvoriumIngot.stack)
		registerManualIcon("manasteel", ItemStack(ModItems.manaResource))
		registerManualIcon("mauftrium", ElvenResourcesMetas.MauftriumIngot.stack)
		registerManualIcon("terrasteel", ItemStack(ModItems.manaResource, 1, 4))
		registerManualIcon("dreamwood", ItemStack(ModItems.manaResource, 1, 13))
		registerManualIcon("livingwood", ItemStack(ModItems.manaResource, 1, 3))
		registerManualIcon("livingrock", ItemStack(ModBlocks.livingrock))
		registerManualIcon("manarepair", ElvenResourcesMetas.ManaInfusionCore.stack)
	}
	
	override fun onItemRightClick(stack: ItemStack?, world: World, player: EntityPlayer): ItemStack? {
		if (world.isRemote) openBook(stack, world, player)
		return stack
	}
	
	@SideOnly(Side.CLIENT)
	fun openBook(stack: ItemStack?, world: World?, player: EntityPlayer) {
		player.openGui(TConstruct.instance, MProxyClient.manualGuiID, world, 0, 0, 0)
		FMLClientHandler.instance().displayGuiScreen(player, GuiManual(stack, naturalManualData()))
	}
	
	@SideOnly(Side.CLIENT)
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		list.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("alfheimmisc.naturalmanual.tooltip"))
	}
	
	companion object {
		
		var naturalManualData = HashMap<String, BookData>()
		
		fun naturalManualData(): BookData {
			val lang = Minecraft.getMinecraft().languageManager.currentLanguage.languageCode
			
			return naturalManualData.computeIfAbsent(lang) {
				val data = BookData()
				
				val xmlDoc = readOrDefault("/assets/tinker/manuals/NaturalManual_%s.xml", it, DocumentBuilderFactory.newInstance())
				
				data.unlocalizedName = TinkersConstructAlfheimModule.naturalManual.unlocalizedName
				data.toolTip = mutableListOf<String>().apply { TinkersConstructAlfheimModule.naturalManual.addInformation(null, null, this, false) }[0]
				data.modID = ModInfo.MODID
				data.itemImage = ResourceLocation(ModInfo.MODID, "NaturalManual")
				data.doc = xmlDoc
				
				data
			}
		}
		
		fun readOrDefault(path: String, lang: String, db: DocumentBuilderFactory): Document? {
			return readManual(path.format(lang), db) ?: readManual(path.format("en_US"), db)
		}
		
		fun readManual(location: String, dbFactory: DocumentBuilderFactory): Document? {
			return try {
				val stream = TConstruct::class.java.getResourceAsStream(location)
				val dBuilder = dbFactory.newDocumentBuilder()
				val doc = dBuilder.parse(stream)
				doc.documentElement.normalize()
				doc
			} catch (e: Exception) {
				e.printStackTrace()
				null
			}
		}
	}
}
