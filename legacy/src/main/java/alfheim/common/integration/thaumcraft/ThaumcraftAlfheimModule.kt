package alfheim.common.integration.thaumcraft

import alexsocol.asjlib.*
import alexsocol.asjlib.ASJUtilities.register
import alfheim.api.*
import alfheim.api.event.*
import alfheim.api.lib.LibOreDict.ELEMENTIUM_ORE
import alfheim.api.lib.LibOreDict.ELVORIUM_NUGGET
import alfheim.api.lib.LibOreDict.IFFESAL_DUST
import alfheim.api.lib.LibOreDict.INFUSED_DREAM_TWIG
import alfheim.api.lib.LibOreDict.MAUFTRIUM_NUGGET
import alfheim.client.render.block.*
import alfheim.common.block.*
import alfheim.common.block.compat.thaumcraft.*
import alfheim.common.core.handler.*
import alfheim.common.item.compat.thaumcraft.*
import alfheim.common.item.material.*
import alfheim.common.lexicon.*
import cpw.mods.fml.client.registry.RenderingRegistry.*
import cpw.mods.fml.common.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.registry.GameRegistry.*
import net.minecraft.block.*
import net.minecraft.client.gui.*
import net.minecraft.creativetab.*
import net.minecraft.item.*
import net.minecraft.item.crafting.*
import net.minecraft.util.*
import net.minecraftforge.event.entity.player.*
import net.minecraftforge.oredict.*
import net.minecraftforge.oredict.OreDictionary.*
import thaumcraft.api.ThaumcraftApi.*
import thaumcraft.api.aspects.*
import thaumcraft.api.crafting.*
import thaumcraft.api.research.*
import thaumcraft.api.wands.*
import thaumcraft.common.blocks.*
import thaumcraft.common.config.*
import thaumcraft.common.lib.crafting.*
import thaumcraft.common.lib.utils.*
import thaumcraft.common.lib.utils.Utils.*
import vazkii.botania.api.lexicon.*
import vazkii.botania.common.*
import vazkii.botania.common.block.*
import vazkii.botania.common.item.*
import vazkii.botania.common.lexicon.page.*
import vazkii.botania.common.lib.LibOreDict.*

@Suppress("ConstPropertyName")
object ThaumcraftAlfheimModule {
	
	lateinit var alfheimThaumOre: Block
	
	lateinit var naturalWandCap: Item
	lateinit var naturalWandRod: Item
	
	lateinit var recipeElementiumWandCap: IRecipe
	
	var renderIDOre = -1
	
	val tcnTab = object: CreativeTabs("NTC") {
		override fun getTabIconItem(): Item {
			return naturalWandCap
		}
		
		override fun func_151243_f(): Int {
			return 1
		}
	}.setNoTitle().setBackgroundImageName("NTC.png")!!
	
	const val capManasteelName = ModInfo.MODID + "Manasteel"
	const val capManasteelRecipe = ModInfo.MODID + "WandCapManasteel"
	const val capManasteelResearch = "CAP_$capManasteelName"
	
	const val capTerrasteelName = ModInfo.MODID + "Terrasteel"
	const val capTerrasteelRecipe = ModInfo.MODID + "WandCapTerrasteel"
	const val capTerrasteelResearch = "CAP_$capTerrasteelName"
	
	const val capElementiumName = ModInfo.MODID + "Elementium"
	const val capElementiumRecipe = ModInfo.MODID + "WandCapElementium"
	const val capElementiumResearch = "CAP_$capElementiumName"
	
	const val capElvoriumName = ModInfo.MODID + "Elvorium"
	const val capElvoriumRecipe = ModInfo.MODID + "WandCapElvorium"
	const val capElvoriumResearch = "CAP_$capElvoriumName"
	
	const val capMauftriumName = ModInfo.MODID + "Mauftrium"
	const val capMauftriumRecipe = ModInfo.MODID + "WandCapMauftrium"
	const val capMauftriumResearch = "CAP_$capMauftriumName"
	
	const val rodLivingwoodName = ModInfo.MODID + "Livingwood"
	const val rodLivingwoodRecipe = ModInfo.MODID + "WandRodLivingwood"
	const val rodLivingwoodResearch = "ROD_$rodLivingwoodName"
	
	const val rodDreamwoodName = ModInfo.MODID + "Dreamwood"
	const val rodDreamwoodRecipe = ModInfo.MODID + "WandRodDreamwood"
	const val rodDreamwoodResearch = "ROD_$rodDreamwoodName"
	
	const val rodSpiritualName = ModInfo.MODID + "Spiritual"
	const val rodSpiritualStaff = rodSpiritualName + "_staff"
	const val rodSpiritualRecipe = ModInfo.MODID + "WandRodSpiritual"
	const val rodSpiritualResearch = "ROD_$rodSpiritualStaff"
	
	const val rodYggdrasilName = ModInfo.MODID + "Yggdrasil"
	const val rodYggdrasilStaff = rodYggdrasilName + "_staff"
	const val rodYggdrasilRecipe = ModInfo.MODID + "WandRodYggdrasil"
	const val rodYggdrasilResearch = "ROD_$rodYggdrasilStaff"
	
	const val pureElementiumRecipe = ModInfo.MODID + "PureElementium"
	const val pureElementiumResearch = ModInfo.MODID + "PUREELEMENTIUM"
	
	const val transElementiumRecipe = ModInfo.MODID + "TransElementium"
	const val transElementiumResearch = ModInfo.MODID + "TRANSELEMENTIUM"
	
	init {
		eventForge()
	}
	
	fun preInit() {
		constructBlocks()
		constructItems()
		registerBlocks()
		registerItems()
		if (ASJUtilities.isClient) registerRenders()
	}
	
	fun constructBlocks() {
		alfheimThaumOre = BlockAlfheimThaumOre()
	}
	
	fun constructItems() {
		naturalWandCap = ItemAlfheimWandCap()
		naturalWandRod = ItemAlfheimWandRod()
		
		WandCap(capManasteelName, 0.95f, ItemStack(naturalWandCap, 1, 0), 5)
		WandCap(capTerrasteelName, 0.85f, ItemStack(naturalWandCap, 1, 1), 8)
		WandCap(capElementiumName, 0.95f, ItemStack(naturalWandCap, 1, 2), 5)
		WandCap(capElvoriumName, 0.85f, ItemStack(naturalWandCap, 1, 3), 8)
		WandCap(capMauftriumName, 0.75f, ItemStack(naturalWandCap, 1, 4), 11)
		
		WandRod(rodLivingwoodName, 35, ItemStack(naturalWandRod, 1, 0), 2)
		WandRod(rodDreamwoodName, 65, ItemStack(naturalWandRod, 1, 1), 5)
		StaffRod(rodSpiritualName, 85, ItemStack(naturalWandRod, 1, 2), 6, NaturalWandRodOnUpdate).isGlowing = true
		StaffRod(rodYggdrasilName, 500, ItemStack(naturalWandRod, 1, 3), 45, YggWandRodOnUpdate).apply { setRunes(true); isGlowing = true }
	}
	
	fun registerBlocks() {
		registerBlock(alfheimThaumOre, BlockCustomOreItem::class.java, "AlfheimThaumOre")
	}
	
	fun registerItems() {
		register(naturalWandCap)
		register(naturalWandRod)
	}
	
	fun registerRenders() {
		renderIDOre = getNextAvailableRenderId()
		registerBlockHandler(RenderBlockAlfheimThaumOre())
	}
	
	fun postInit() {
		registerRecipes()
		registerResearches()
		registerOreDict()
		
		if (Botania.thaumcraftLoaded) {
			ModItems.elementiumHelmRevealing.creativeTab = tcnTab
			ModItems.manasteelHelmRevealing.creativeTab = tcnTab
			ModItems.terrasteelHelmRevealing.creativeTab = tcnTab
		}
		
		addSpecialMiningResult(ItemStack(alfheimThaumOre, 1, 0), ItemStack(ConfigItems.itemNugget, 1, 21), 0.9f)
		addSpecialMiningResult(ItemStack(AlfheimBlocks.elvenOre, 1, 1), ItemStack(ConfigItems.itemNugget, 1, AlfheimPreConfigHandler.elementiumClusterMeta), 1f)
		
		CropUtils.addClickableCrop(ItemStack(AlfheimBlocks.grapesRed[2]), 32767)
		CropUtils.addClickableCrop(ItemStack(AlfheimBlocks.grapesRedPlanted), 4)
		CropUtils.addClickableCrop(ItemStack(AlfheimBlocks.grapesWhite), 2)
		
		AlfheimLexiconData.ores.setLexiconPages(PageText("TC"))
		repeat(8) {
			LexiconRecipeMappings.map(ItemStack(alfheimThaumOre, 1, it), AlfheimLexiconData.ores, 3)
		}
	}
	
	fun registerRecipes() {
		var cost: Int
		
		if (botaniaCaps) {
			cost = WandCap.caps[capManasteelName]!!.craftCost
			ConfigResearch.recipes[capManasteelRecipe] = addArcaneCraftingRecipe(capManasteelResearch,
																				 ItemStack(naturalWandCap, 1, 0),
																				 AspectList()
																					 .add(Aspect.AIR, cost)
																					 .add(Aspect.FIRE, cost)
																					 .add(Aspect.ORDER, cost),
																				 "NNN", "N N",
																				 'N', MANASTEEL_NUGGET
			)
			
			cost = WandCap.caps[capTerrasteelName]!!.craftCost
			ConfigResearch.recipes[capTerrasteelRecipe] = addArcaneCraftingRecipe(capTerrasteelResearch,
			                                                                      ItemStack(naturalWandCap, 1, 1),
			                                                                      AspectList()
																					  .add(Aspect.AIR, cost)
																					  .add(Aspect.FIRE, cost)
																					  .add(Aspect.ORDER, cost),
			                                                                      "NNN", "N N",
			                                                                      'N', TERRASTEEL_NUGGET
			)
			
			cost = WandCap.caps[capElementiumName]!!.craftCost
			ConfigResearch.recipes[capElementiumRecipe] = addArcaneCraftingRecipe(capElementiumResearch,
			                                                                      ItemStack(naturalWandCap, 1, 2),
			                                                                      AspectList()
																					  .add(Aspect.AIR, cost)
																					  .add(Aspect.FIRE, cost)
																					  .add(Aspect.ORDER, cost),
			                                                                      "NNN", "N N",
			                                                                      'N', ELEMENTIUM_NUGGET
			)
		}
		
		cost = WandCap.caps[capElvoriumName]!!.craftCost
		ConfigResearch.recipes[capElvoriumRecipe] = addArcaneCraftingRecipe(capElvoriumResearch,
		                                                                    ItemStack(naturalWandCap, 1, 3),
		                                                                    AspectList()
																				.add(Aspect.AIR, cost)
																				.add(Aspect.FIRE, cost)
																				.add(Aspect.ORDER, cost),
		                                                                    "NNN", "N N",
		                                                                    'N', ELVORIUM_NUGGET
		)
		
		cost = WandCap.caps[capMauftriumName]!!.craftCost
		ConfigResearch.recipes[capMauftriumRecipe] = addArcaneCraftingRecipe(capMauftriumResearch,
		                                                                     ItemStack(naturalWandCap, 1, 4),
		                                                                     AspectList()
																				 .add(Aspect.AIR, cost)
																				 .add(Aspect.FIRE, cost)
																				 .add(Aspect.WATER, cost)
																				 .add(Aspect.EARTH, cost)
																				 .add(Aspect.ORDER, cost)
																				 .add(Aspect.ENTROPY, cost),
		                                                                     "NNN", "N N",
		                                                                     'N', MAUFTRIUM_NUGGET
		)
		
		cost = WandRod.rods[rodLivingwoodName]!!.craftCost
		ConfigResearch.recipes[rodLivingwoodRecipe] = addArcaneCraftingRecipe(rodLivingwoodResearch,
		                                                                      ItemStack(naturalWandRod, 1, 0),
		                                                                      AspectList()
																				  .add(Aspect.AIR, cost)
																				  .add(Aspect.EARTH, cost),
		                                                                      "  T", " T ", "T  ",
		                                                                      'T', LIVINGWOOD_TWIG
		)
		
		cost = WandRod.rods[rodDreamwoodName]!!.craftCost
		ConfigResearch.recipes[rodDreamwoodRecipe] = addArcaneCraftingRecipe(rodDreamwoodResearch,
		                                                                     ItemStack(naturalWandRod, 1, 1),
		                                                                     AspectList()
																				 .add(Aspect.AIR, cost)
																				 .add(Aspect.EARTH, cost)
																				 .add(Aspect.ORDER, cost),
		                                                                     "  I", " I ", "I  ",
		                                                                     'I', INFUSED_DREAM_TWIG
		)
		
		cost = WandRod.rods[rodSpiritualStaff]!!.craftCost
		ConfigResearch.recipes[rodSpiritualRecipe] = addArcaneCraftingRecipe(rodSpiritualResearch,
		                                                                     ItemStack(naturalWandRod, 1, 2),
		                                                                     AspectList()
																				 .add(Aspect.AIR, cost)
																				 .add(Aspect.FIRE, cost)
																				 .add(Aspect.WATER, cost)
																				 .add(Aspect.EARTH, cost)
																				 .add(Aspect.ORDER, cost)
																				 .add(Aspect.ENTROPY, cost),
		                                                                     "DSP", " RS", "R D",
		                                                                     'R', ItemStack(naturalWandRod, 1, 1),
		                                                                     'S', LIFE_ESSENCE,
		                                                                     'D', DRAGONSTONE,
		                                                                     'P', ItemStack(ConfigItems.itemResource, 1, 15)
		)
		
		cost = WandRod.rods[rodYggdrasilStaff]!!.craftCost
		ConfigResearch.recipes[rodYggdrasilRecipe] = addInfusionCraftingRecipe(rodYggdrasilResearch,
		                                                                       ItemStack(naturalWandRod, 1, 3),
		                                                                       6,
		                                                                       AspectList()
			                                                                       .add(Aspect.HEAL, cost)
			                                                                       .add(Aspect.TREE, cost)
			                                                                       .add(Aspect.AURA, cost)
			                                                                       .add(Aspect.PLANT, cost)
			                                                                       .add(Aspect.LIFE, cost)
			                                                                       .add(Aspect.TOOL, cost)
			                                                                       .add(Aspect.MAGIC, cost)
			                                                                       .add(Aspect.SENSES, cost)
			                                                                       .add(Aspect.MIND, cost),
		                                                                       ItemStack(AlfheimBlocks.altWood1, 1, 2),
		                                                                       arrayOf(
			                                                                       ItemStack(ConfigItems.itemEldritchObject, 1, 3),
			                                                                       ItemStack(ConfigItems.itemResource, 1, 14),
			                                                                       ItemStack(ConfigItems.itemResource, 1, 9),
			                                                                       ElvenResourcesMetas.MauftriumIngot.stack,
			                                                                       ElvenResourcesMetas.YggFruit.stack,
			                                                                       ElvenResourcesMetas.PrimalRune.stack,
			                                                                       ElvenResourcesMetas.MauftriumIngot.stack,
			                                                                       ItemStack(ConfigItems.itemResource, 1, 9),
			                                                                       ItemStack(ConfigItems.itemResource, 1, 14)
																			   )
		)
		
		ConfigResearch.recipes[pureElementiumRecipe] = addCrucibleRecipe(pureElementiumResearch,
																		 ItemStack(ConfigItems.itemNugget, 1, AlfheimPreConfigHandler.elementiumClusterMeta),
																		 ELEMENTIUM_ORE,
																		 AspectList()
																			 .merge(Aspect.METAL, 1)
																			 .merge(Aspect.ORDER, 1)
		)
		
		ConfigResearch.recipes[transElementiumRecipe] = addCrucibleRecipe(transElementiumResearch,
																		  ItemStack(ModItems.manaResource, 3, 19),
																		  ELEMENTIUM_NUGGET,
																		  AspectList()
																			  .merge(Aspect.METAL, 2)
																			  .merge(Aspect.MAGIC, 2)
		)
		
		recipeElementiumWandCap = ShapedOreRecipe(ItemStack(naturalWandCap, 1, 2),
												  "NNN", "NIN",
												  'N', ELEMENTIUM_NUGGET,
												  'I', IFFESAL_DUST)
		
		if (AlfheimConfigHandler.enableElvenStory) addESMRecipes()
		
		addSmelting(ItemStack(alfheimThaumOre, 1, 0),
					ItemStack(ConfigItems.itemResource, 1, 3), // Cinnabar
					1f
		)
		
		addSmelting(ItemStack(alfheimThaumOre, 1, 7),
					ItemStack(ConfigItems.itemResource, 1, 6), // Amber
					1f
		)
		
		addSmelting(ItemStack(ConfigItems.itemNugget, 1, AlfheimPreConfigHandler.elementiumClusterMeta),
					ItemStack(ModItems.manaResource, 2, 7), // Elementium
					1f
		)
		
		addSmeltingBonus(ELEMENTIUM_ORE,
						 ItemStack(ModItems.manaResource, 0, 19)        // from ore
		)
		
		addSmeltingBonus(ItemStack(ConfigItems.itemNugget, 1, AlfheimPreConfigHandler.elementiumClusterMeta),
						 ItemStack(ModItems.manaResource, 0, 19)        // from cluster
		)
	}
	
	@SubscribeEvent
	fun onModeChanged(e: AlfheimModeChangedEvent) {
		if (e.esm && !e.esmOld) addESMRecipes()
		else if (!e.esm && e.esmOld) removeESMRecipes()
	}
	
	fun addESMRecipes() {
		CraftingManager.getInstance().recipeList.add(recipeElementiumWandCap)
		
		var cap = WandCap.caps[capElementiumName]!!
		cap.baseCostModifier = 0.95f
		cap.craftCost = 5
		
		cap = WandCap.caps[capElvoriumName]!!
		cap.baseCostModifier = 0.85f
		cap.craftCost = 8
	}
	
	fun removeESMRecipes() {
		CraftingManager.getInstance().recipeList.remove(recipeElementiumWandCap)
		
		var cap = WandCap.caps[capElementiumName]!!
		cap.baseCostModifier = 0.9f
		cap.craftCost = 6
		
		cap = WandCap.caps[capElvoriumName]!!
		cap.baseCostModifier = 0.8f
		cap.craftCost = 9
	}
	
	fun registerResearches() {
		val CATEGORY = "ALFHEIM"
		ResearchCategories.registerCategory(CATEGORY, ResourceLocation(ModInfo.MODID, "textures/gui/categories/Alfheim.png"), ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"))
		
		copy(ResearchCategories.getResearchList("THAUMATURGY").research["CAP_thaumium"]!!, "ALF.CAP_thaumium", CATEGORY, 1, -1)
		copy(ResearchCategories.getResearchList("ELDRITCH").research["CAP_void"]!!, "ALF.CAP_void", CATEGORY, 0, -2).setConcealed()
		copy(ResearchCategories.getResearchList("THAUMATURGY").research["ROD_greatwood"]!!, "ALF.ROD_greatwood", CATEGORY, 0, 6)
		copy(ResearchCategories.getResearchList("THAUMATURGY").research["ROD_silverwood"]!!, "ALF.ROD_silverwood", CATEGORY, 0, 4)
		copy(ResearchCategories.getResearchList("ALCHEMY").research["PUREIRON"]!!, "ALF.PUREIRON", CATEGORY, -2, 1).setConcealed()
		copy(ResearchCategories.getResearchList("ALCHEMY").research["TRANSIRON"]!!, "ALF.TRANSIRON", CATEGORY, 2, 1).setConcealed()
		
		if (botaniaCaps) {
			copy(ResearchCategories.getResearchList("THAUMATURGY").research["CAP_gold"]!!, "ALF.CAP_gold", CATEGORY, -1, -1)
			
			ResearchItem(capManasteelResearch, CATEGORY,
			             AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 3).add(Aspect.TOOL, 3),
			             -3, -3, 1,
			             ItemStack(naturalWandCap, 1, 0))
				
				.setPages(ResearchPage("tc.research_page.$capManasteelResearch.1"),
						  ResearchPage(ConfigResearch.recipes[capManasteelRecipe] as IArcaneRecipe))
				
				.setParents("ALF.CAP_gold").setItemTriggers(ItemStack(ModItems.manaResource, 1, 0)).registerResearchItem()
			
			
			
			ResearchItem(capTerrasteelResearch, CATEGORY,
			             AspectList().add(Aspect.METAL, 6).add(Aspect.MAGIC, 6).add(Aspect.TOOL, 3).add(Aspect.AURA, 3),
			             3, -3, 2,
			             ItemStack(naturalWandCap, 1, 1))
				
				.setPages(ResearchPage("tc.research_page.$capTerrasteelResearch.1"),
						  ResearchPage(ConfigResearch.recipes[capTerrasteelRecipe] as IArcaneRecipe))
				
				.setParents("ALF.CAP_thaumium").setItemTriggers(ItemStack(ModItems.manaResource, 1, 4)).registerResearchItem()
			
			
			
			ResearchItem(capElementiumResearch, CATEGORY,
			             AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 3).add(Aspect.TOOL, 3),
			             -4, -1, 1,
			             ItemStack(naturalWandCap, 1, 2))
				
				.setPages(ResearchPage("tc.research_page.$capElementiumResearch.1"),
						  ResearchPage(ConfigResearch.recipes[capElementiumRecipe] as IArcaneRecipe))
				
				.setParents("ALF.CAP_gold").setItemTriggers(ItemStack(ModItems.manaResource, 1, 7)).registerResearchItem()
		}	
		
		
		
		ResearchItem(capElvoriumResearch, CATEGORY,
		             AspectList().add(Aspect.METAL, 6).add(Aspect.MAGIC, 6).add(Aspect.TOOL, 3).add(Aspect.AURA, 3),
		             4, -1, 2,
		             ItemStack(naturalWandCap, 1, 3))
			
			.setPages(ResearchPage("tc.research_page.$capElvoriumResearch.1"),
					  ResearchPage(ConfigResearch.recipes[capElvoriumRecipe] as IArcaneRecipe))
			
			.setParents("ALF.CAP_thaumium").setItemTriggers(ElvenResourcesMetas.ElvoriumIngot.stack).registerResearchItem()
		
		
		
		ResearchItem(capMauftriumResearch, CATEGORY,
		             AspectList().add(Aspect.VOID, 5).add(Aspect.ELDRITCH, 5).add(Aspect.TOOL, 3).add(Aspect.MAGIC, 3).add(Aspect.AURA, 3),
		             0, -4, 3,
		             ItemStack(naturalWandCap, 1, 4))
			
			.setPages(ResearchPage("tc.research_page.$capMauftriumResearch.1"),
					  ResearchPage(ConfigResearch.recipes[capMauftriumRecipe] as IArcaneRecipe))
			
			.setParents("ALF.CAP_void").setConcealed().setItemTriggers(ElvenResourcesMetas.MauftriumIngot.stack).registerResearchItem()
		
		
		
		ResearchItem(rodLivingwoodResearch, CATEGORY,
		             AspectList().add(Aspect.TOOL, 3).add(Aspect.TREE, 6).add(Aspect.MAGIC, 3),
		             -2, 7, 1,
		             ItemStack(naturalWandRod, 1, 0))
			
			.setPages(ResearchPage("tc.research_page.$rodLivingwoodResearch.1"),
					  ResearchPage(ConfigResearch.recipes[rodLivingwoodRecipe] as IArcaneRecipe))
			
			.setParents("ALF.ROD_greatwood").setItemTriggers(ItemStack(ModBlocks.livingwood, 1, 0)).registerResearchItem()
		
		
		
		ResearchItem(rodDreamwoodResearch, CATEGORY,
		             AspectList().add(Aspect.TOOL, 4).add(Aspect.TREE, 6).add(Aspect.MAGIC, 5),
		             2, 7, 2,
		             ItemStack(naturalWandRod, 1, 1))
			
			.setPages(ResearchPage("tc.research_page.$rodDreamwoodResearch.1"),
					  ResearchPage(ConfigResearch.recipes[rodDreamwoodRecipe] as IArcaneRecipe))
			
			.setParents("ALF.ROD_greatwood").setItemTriggers(ItemStack(ModBlocks.dreamwood, 1, 0)).registerResearchItem()
		
		
		
		ResearchItem(rodSpiritualResearch, CATEGORY,
		             AspectList().add(Aspect.TOOL, 6).add(Aspect.TREE, 6).add(Aspect.MAGIC, 12),
		             0, 2, 2,
		             ItemStack(naturalWandRod, 1, 2))
			
			.setPages(ResearchPage("tc.research_page.$rodSpiritualResearch.1"),
					  ResearchPage(ConfigResearch.recipes[rodSpiritualRecipe] as IArcaneRecipe))
			
			.setParents("ALF.ROD_silverwood").setParentsHidden(rodDreamwoodResearch).setItemTriggers(ItemStack(ModItems.manaResource, 1, 5), ItemStack(ModItems.manaResource, 1, 9)).registerResearchItem()
		
		
		
		ResearchItem(rodYggdrasilResearch, CATEGORY,
		             AspectList().add(Aspect.TOOL, 8).add(Aspect.TREE, 8).add(Aspect.MAGIC, 16).add(Aspect.HEAL, 8).add(Aspect.AURA, 8).add(Aspect.SENSES, 8),
		             0, 0, 3,
		             ItemStack(naturalWandRod, 1, 3))
			
			.setPages(ResearchPage("tc.research_page.$rodYggdrasilResearch.1"), ResearchPage("tc.research_page.$rodYggdrasilResearch.2"),
			          ResearchPage(ConfigResearch.recipes[rodYggdrasilRecipe] as InfusionRecipe))
			
			.setParents(rodSpiritualResearch, "PRIMPEARL").setConcealed().setItemTriggers(ElvenResourcesMetas.YggFruit.stack, ElvenResourcesMetas.MauftriumIngot.stack).registerResearchItem()
		
		
		
		ResearchItem(pureElementiumResearch, CATEGORY,
		             AspectList().add(Aspect.METAL, 3).add(Aspect.ORDER, 2).add(Aspect.MAGIC, 1),
		             -4, 2, 1, ItemStack(ConfigItems.itemNugget, 1, AlfheimPreConfigHandler.elementiumClusterMeta))
			
			.setPages(ResearchPage("tc.research_page.$pureElementiumResearch.1"),
					  ResearchPage(ConfigResearch.recipes[pureElementiumRecipe] as CrucibleRecipe))
			
			.setConcealed().setSecondary().setParents("ALF.PUREIRON").setItemTriggers(ItemStack(ModItems.manaResource, 1, 7)).registerResearchItem()
		
		
		
		ResearchItem(transElementiumResearch, CATEGORY,
		             AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 3),
		             4, 2, 1, ItemStack(ModItems.manaResource, 1, 19))
			
			.setPages(ResearchPage("tc.research_page.$transElementiumResearch.1"),
					  ResearchPage(ConfigResearch.recipes[transElementiumRecipe] as CrucibleRecipe))
			
			.setConcealed().setSecondary().setParents("ALF.TRANSIRON").setItemTriggers(ItemStack(ModItems.manaResource, 1, 7)).registerResearchItem()
		
		addWarpToResearch(capMauftriumResearch, 2)
	}
	
	fun registerOreDict() {
		registerOreDict("")
		registerOreDict("Alfheim")
		
		registerOre("clusterElvenElementium", ItemStack(ConfigItems.itemNugget, 1, AlfheimPreConfigHandler.elementiumClusterMeta))
		registerOre("ingotCinnabar", ItemStack(ConfigItems.itemResource, 1, 3))
		
		AlfheimAPI.addOreWeightAlfheim("oreCinnabarAlfheim", 4275)
		AlfheimAPI.addOreWeightAlfheim("oreInfusedAirAlfheim", 1548)
		AlfheimAPI.addOreWeightAlfheim("oreInfusedFireAlfheim", 1327)
		AlfheimAPI.addOreWeightAlfheim("oreInfusedWaterAlfheim", 1432)
		AlfheimAPI.addOreWeightAlfheim("oreInfusedEarthAlfheim", 1460)
		AlfheimAPI.addOreWeightAlfheim("oreInfusedOrderAlfheim", 1377)
		AlfheimAPI.addOreWeightAlfheim("oreInfusedEntropyAlfheim", 1455)
		AlfheimAPI.addOreWeightAlfheim("oreAmberAlfheim", 4261)
	}
	
	fun registerOreDict(postfix: String) {
		registerOre("oreCinnabar$postfix", ItemStack(alfheimThaumOre, 1, 0))
		registerOre("oreInfusedAir$postfix", ItemStack(alfheimThaumOre, 1, 1))
		registerOre("oreInfusedFire$postfix", ItemStack(alfheimThaumOre, 1, 2))
		registerOre("oreInfusedWater$postfix", ItemStack(alfheimThaumOre, 1, 3))
		registerOre("oreInfusedEarth$postfix", ItemStack(alfheimThaumOre, 1, 4))
		registerOre("oreInfusedOrder$postfix", ItemStack(alfheimThaumOre, 1, 5))
		registerOre("oreInfusedEntropy$postfix", ItemStack(alfheimThaumOre, 1, 6))
		registerOre("oreAmber$postfix", ItemStack(alfheimThaumOre, 1, 7))
	}
	
	fun postPostInit() {
		if (!botaniaCaps) return
		
		getCraftingRecipes().removeAll { (it as? ShapedArcaneRecipe ?: return@removeAll false).research in arrayOf("CAP_manasteel", "CAP_elementium") }
		
		val fmCapItem = findItem("ForbiddenMagic", "WandCaps")
		CraftingManager.getInstance().recipeList.removeAll {
			val result = (it as? IRecipe)?.recipeOutput ?: return@removeAll false
			result.item === fmCapItem && result.meta == 2
		}
	}
	
	val botaniaCaps get() = !Loader.isModLoaded("ForbiddenMagic") || AlfheimConfigHandler.overrideFMCaps
	
	// utility function from ThaumicBases by Modbder
	@Suppress("UsePropertyAccessSyntax") // тупое ты дерьмо блядь
	fun copy(original: ResearchItem, newKey: String, newCat: String, column: Int, row: Int): ResearchItem {
		val copy = if (original.icon_resource != null) ResearchItem(newKey, newCat, original.tags, column, row, original.complexity, original.icon_resource)
		else ResearchItem(newKey, newCat, original.tags, column, row, original.complexity, original.icon_item)
		
		if (original.parents.isNullOrEmpty()) {
			copy.setParents(original.key)
		} else {
			copy.setParents(*original.parents, original.key)
		}
		
		copy.parentsHidden = original.parentsHidden
		copy.siblings = original.siblings
		copy.setPages(*original.pages)
		copy.setAspectTriggers(*arrayOfNulls<Aspect>(0))
		copy.setEntityTriggers(*arrayOfNulls<String>(0))
		copy.setItemTriggers(*arrayOfNulls<ItemStack>(0))
		
		if (original.isSpecial) copy.setSpecial()
		if (original.isSecondary) copy.setSecondary()
		if (original.isRound) copy.setRound()
		if (original.isConcealed) copy.setConcealed()
		if (original.isHidden) copy.setHidden()
		if (original.isLost) copy.setLost()
		if (original.isAutoUnlock) copy.setAutoUnlock()
		
		if (original.siblings.isNullOrEmpty()) {
			original.setSiblings(newKey)
		} else {
			original.setSiblings(*original.siblings, newKey)
		}
		
		return copy.setStub().registerResearchItem()
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun showAspects(e: ItemTooltipEvent) {
		if (!ModInfo.DEV) return
		if (!GuiScreen.isCtrlKeyDown()) return
		
		val stack = e.itemStack.copy()
		val key = groupedObjectTags[listOf(stack.item, stack.meta)]
		if (key != null) stack.meta = key[0]
		
		e.toolTip.add("Aspects:")
		var aspects: AspectList? = ThaumcraftCraftingManager.getObjectTags(stack)
		aspects = ThaumcraftCraftingManager.getBonusTags(stack, aspects)
		if (aspects == null || aspects.size() == 0)
			e.toolTip.add("None")
		else
			aspects.aspects.forEach { (asp, amt) ->
				e.toolTip.add("${asp.name} x$amt")
			}
	}
}
