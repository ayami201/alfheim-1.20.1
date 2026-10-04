package alfheim.common.item.material

// PORT: импорты 1.20.1 (MAPPING.md); Botania.proxy — alfheim.port.legacy.botania.Botania. Импорты механик других КТ
// закомментированы вместе с их строками
import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.api.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.rainbow.BlockRainbowGrass
import alfheim.common.item.*
import alfheim.common.item.material.ElvenResourcesMetas.*
import alfheim.common.item.material.ElvenResourcesMetas.Companion.of
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.Botania
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.effect.*
import net.minecraft.world.entity.*
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level as World
import vazkii.botania.common.lib.BotaniaTags
import java.awt.Color
import kotlin.math.sin
//import alexsocol.patcher.asm.hook.ASJSuperWrapperHandler
//import alfheim.api.lib.LibOreDict
//import alfheim.common.block.*
//import alfheim.common.block.tile.*
//import alfheim.common.block.tile.TileKudzuVine.Companion.EnumMutation
//import alfheim.common.core.handler.*
//import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem
//import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge
//import alfheim.common.core.handler.ragnarok.RagnarokHandler
//import alfheim.common.core.helper.ElementalDamage
//import alfheim.common.entity.EntityElementalSlime
//import alfheim.common.entity.boss.EntityFlugel
//import alfheim.common.item.relic.ItemTankMask.Companion.limboCounter
//import alfheim.common.world.dim.niflheim.ChunkProviderNiflheim
//import vazkii.botania.common.block.ModBlocks
//import vazkii.botania.common.entity.EntityDoppleganger

// PORT: вариант metadata — отдельный предмет (SPEC, Р-5): номер варианта — meta, имя варианта — имя из
// ElvenResourcesMetas (ElvoriumIngot → alfheim:elvorium_ingot), создают их массивом `Array(entries.size) { ItemElvenResource(it) }`.
// КТ-3 — портал Botania (IElvenItem) и аптекарь лепестков (IFlowerComponent); топливо (IFuelHandler) — getBurnTime ниже
class ItemElvenResource(val meta: Int): ItemMod("ElvenItems")/*, IElvenItem, IFlowerComponent, IFuelHandler*/ {
	
	override val variant get() = meta
	
	override val variantName get() = of(meta).toString()
	
	// PORT: иконки → модели предметов (alfheim.port.data.AlfheimItemModels)
//	val texture = arrayOfNulls<IIcon>(entries.size)
	
	init {
		setHasSubtypes(true)
		// PORT: анимированные текстуры 1.20.1 рисует сама по .mcmeta; топливо 1.20.1 — метод предмета
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
//		
//		GameRegistry.registerFuelHandler(this)
	}
	
	// PORT: проходы рендера → слои модели предмета (alfheim.port.data.AlfheimItemModels)
	/*
	override fun getRenderPasses(meta: Int) =
		when (meta) {
			ElvenWeed.I, RiftDrive.I -> 2
			else                     -> 1
		}
	
	override fun requiresMultipleRenderPasses() = true
	*/
	
	// PORT: КТ-3 — портал Botania
//	override fun isElvenItem(stack: ItemStack) = stack.meta == InterdimensionalGatewayCore.I
	
	fun isInterpolated(meta: Int) = when (of(meta)) {
		ThunderwoodTwig, NetherwoodCoal, RainbowQuartz, Nifleur -> true
		else                                                    -> false
	}
	
	fun isFlowerComponent(meta: Int) = when (of(meta)) {
		NetherwoodCoal, RainbowPetal, IffesalDust -> true
		else                                      -> false
	}
	
	// PORT: КТ-3 — аптекарь лепестков Botania
	/*
	override fun canFit(stack: ItemStack, inventory: IInventory) = isFlowerComponent(stack.meta)
	
	override fun getParticleColor(stack: ItemStack): Int {
		return when (of(stack.meta)) {
			NetherwoodCoal -> 0x6B2406
			IffesalDust    -> 0x0519E2
			RainbowPetal   -> ItemIridescent.rainbowColor()
			else           -> 0xFFFFFF
		}
	}
	*/
	
	override fun getColorFromItemStack(stack: ItemStack, pass: Int) =
		if ((stack.meta == ElvenWeed.I && pass == 1) || stack.meta == RiftShardEmpty.I)
			Color.HSBtoRGB(Botania.proxy.worldElapsedTicks * 2 % 360 / 360f, 0.25f, 1f)
		else if (stack.meta == ElementalSlimeBall.I && ItemNBTHelper.getBoolean(stack, TAG_RAINBOW, false))
			ItemIridescent.rainbowColor()
		else if ((stack.meta == RiftDrive.I && pass == 1)) {
			// PORT: КТ-3 — аномалии (AlfheimAPI.getAnomaly, TileAnomaly); до неё цвет любой аномалии — цвет заглушки
			// fallbackAnomalyData, 0
			val color = 0
//			val color = AlfheimAPI.getAnomaly(ItemNBTHelper.getString(stack, TileAnomaly.TAG_SUBTILE_NAME, "")).color
			if (color == -1) Color.HSBtoRGB(Botania.proxy.worldElapsedTicks * 2 % 360 / 360f, 1f, 1f) else color
		} else when (stack.meta) {
			RiftShardGinnungagap.I        -> Color.HSBtoRGB(0f, 0f, (sin(Botania.proxy.worldElapsedTicks / 36.0).F + 1) / 20 + 0.05F)
			RiftShardMuspelheim.I         -> Color.HSBtoRGB(0.05f, (sin(Botania.proxy.worldElapsedTicks / 36.0).F + 1) / 8 + 0.75f, 1f)
			RiftShardNiflheim.I           -> Color.HSBtoRGB(2 / 3f, (sin(Botania.proxy.worldElapsedTicks / 36.0).F + 1) / 8 + 0.75f, 1f)
			RainbowPetal.I, RainbowDust.I -> ItemIridescent.rainbowColor()
			// PORT: КТ-4 — стихии (ElementalDamage); пока шарик слизи без цвета стихии
//			ElementalSlimeBall.I          -> stack.element.color
			else                          -> super.getColorFromItemStack(stack, pass)
		}
	
	val riftIcons = arrayOf(RiftShardGinnungagap.I, RiftShardMuspelheim.I, RiftShardNiflheim.I)
	
	// PORT: иконки → модели предметов (alfheim.port.data.AlfheimItemModels): текстура варианта — materials/<имя>, у
	// осколков разлома — materials/RiftShardEmpty, второй слой ElvenWeed и RiftDrive — …1; на праздник у прутика —
	// CandyCane (модель alfheim:item/infused_candy подставляет alfheim.port.client.AlfheimModels)
	/*
	override fun registerIcons(reg: IIconRegister) {
		for (type in entries)
			if (!isInterpolated(type.I) && type.I !in riftIcons)
				texture[type.I] = IconHelper.forName(reg, type.toString(), "materials")
		
		for (meta in riftIcons)
			texture[meta] = texture[RiftShardEmpty.I]
		
		amulet = reg.registerIcon(ModInfo.MODID + ":misc/amulet")
		candy = IconHelper.forName(reg, "CandyCane", "materials")
		flugel = reg.registerIcon(ModInfo.MODID + ":misc/flugelBack")
		kitty = reg.registerIcon(ModInfo.MODID + ":misc/kitty")
		harp = reg.registerIcon(ModInfo.MODID + ":misc/harp")
		mine = reg.registerIcon(ModInfo.MODID + ":misc/mine")
		wind = reg.registerIcon(ModInfo.MODID + ":misc/wind")
		wing = reg.registerIcon(ModInfo.MODID + ":misc/wing")
		
		drive1 = IconHelper.forName(reg, "materials/${RiftDrive}1")
		weed1 = IconHelper.forName(reg, "materials/${ElvenWeed}1")
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 1)
			for (type in entries)
				if (isInterpolated(type.I))
					texture[type.I] = InterpolatedIconHelper.forName(event.map, type.toString(), "materials")
	}
	
	override fun getIconFromDamage(meta: Int) = texture.safeGet(meta)
	
	override fun getIcon(stack: ItemStack, pass: Int) =
		if (stack.meta == RiftDrive.I && pass == 1)
			drive1
		else if (stack.meta == ElvenWeed.I && pass == 1)
			weed1
		else if (stack.meta in riftIcons)
			texture[RiftShardEmpty.I]
		else if (AlfheimCore.jingleTheBells && stack.meta == InfusedDreamwoodTwig.I)
			candy
		else
			texture.safeGet(stack.meta)
	*/
	
	override fun getUnlocalizedName(stack: ItemStack) =
		if (AlfheimCore.jingleTheBells && stack.meta == InfusedDreamwoodTwig.I)
			"item.InfusedCandy"
		else {
			var name = "item.${of(stack.meta).toString()}"
			// PORT: КТ-4 — стихия (ElementalDamage); пока — имя стихии из NBT, без неё — COMMON
			if (stack.meta == ElementalSlimeBall.I) name += ".${ItemNBTHelper.getString(stack, TAG_ELEMENT, "COMMON")}"
//			if (stack.meta == ElementalSlimeBall.I) name += ".${stack.element.name}"
			name
		}
	
	// PORT: вариант — отдельный предмет: каждый выдаёт во вкладку только свои стаки
	override fun getSubItems(item: Item, tab: Any?, list: MutableList<Any?>) {
		for (type in entries) {
			if (type.I != meta) continue
			if (type in ElvenResourcesMetas.displayBlackList) continue
			
			when (type) {
				// PORT: КТ-3 — аномалии (TileAnomaly); до неё накопителей разлома во вкладке нет
				RiftDrive          -> Unit
				/*
				RiftDrive          -> AlfheimAPI.anomalies.keys.forEach {
					if (!AlfheimAPI.anomalyBehaviors.containsKey(it)) return@forEach
					
					val stack = RiftDrive.stack
					ItemNBTHelper.setString(stack, TileAnomaly.TAG_SUBTILE_NAME, it)
					list += stack
				}
				*/
				// PORT: КТ-5 — шарики слизи всех стихий (EntityElementalSlime); пока — один, без стихии
				ElementalSlimeBall -> list += type.stack
//				ElementalSlimeBall -> EntityElementalSlime.allowedElements.mapTo(list) { ballForElement(it) }
				else               -> list += type.stack
			}
		}
	}
	
	override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, target: Entity): Boolean {
		// PORT: commandSenderName игрока → gameProfile.name
		return if (stack.meta == DasRheingold.I && target is EntityPlayer)
			ItemNBTHelper.setString(stack, "nick", target.gameProfile.name).let { true }
		else
			super.onLeftClickEntity(stack, player, target)
	}
	
	// PORT: номера зелий → эффекты 1.20.1
	val ids = arrayOf(MobEffects.MOVEMENT_SPEED, MobEffects.REGENERATION, MobEffects.JUMP, MobEffects.HUNGER, MobEffects.CONFUSION)
//	val ids = arrayOf(Potion.moveSpeed.id, Potion.regeneration.id, Potion.jump.id, Potion.hunger.id, Potion.confusion.id)
	
	val usable = arrayOf(ElvenWeed.I, WisdomBottle.I, YggFruit.I)
	
	// PORT: onItemRightClick → use; setItemInUse → startUsingItem. Бутылка мудрости работает только в Гиннунгагапе
	// Рагнарёка (КТ-8): до тех пор она, как у автора вне Рагнарёка, не используется
	override fun use(world: World, player: EntityPlayer, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		if (stack.meta in usable) {
			if (stack.meta == WisdomBottle.I) return InteractionResultHolder.pass(stack) // PORT: КТ-8
//			if (stack.meta == WisdomBottle.I && (!RagnarokHandler.ginnungagap || player is EntityPlayerMP && KnowledgeSystem.know(player, Knowledge.ABYSS_TRUTH))) return stack
			player.startUsingItem(hand)
			return InteractionResultHolder.consume(stack)
//			player.setItemInUse(stack, getMaxItemUseDuration(stack))
		}
		/* PORT: КТ-8 — осколки разлома наполняются в Рагнарёк (Гиннунгагап, Муспельхейм, Нифльхейм)
		else
		// rift shard filling
		if (stack.meta == RiftShardEmpty.I) {
			if (!RagnarokHandler.ginnungagap || player !is EntityPlayerMP) return stack
			
			val mop = ASJUtilities.getSelectedBlock(player, player.theItemInWorldManager.blockReachDistance, true)
			if (mop?.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return stack
			val (x, y, z) = intArrayOf(mop.blockX, mop.blockY, mop.blockZ)
			
			val give = if (world.getBlock(x, y, z) === AlfheimBlocks.rift) {
				val nextMeta = world.getBlockMetadata(x, y, z) + 1
				if (nextMeta > 15) return stack
				world.setBlockMetadataWithNotify(x, y, z, nextMeta, 0)
				
				RiftShardGinnungagap.stack
			} else {
				when (player.dimension) {
					-1 -> {
						if (y > 31) return stack
						
						for (i in x.bidiRange(5))
							for (j in (y - 5)..y)
								for (k in z.bidiRange(5))
									if (world.getBlock(i, j, k) != Blocks.lava)
										return stack
						
						RiftShardMuspelheim.stack
					}
					
					AlfheimConfigHandler.dimensionIDNiflheim -> {
						if (y != 127 || ChunkProviderNiflheim.f(x) !in z.bidiRange(6)) return stack
						
						RiftShardNiflheim.stack
					}
					
					else -> return stack
				}
			}
			
			if (--stack.stackSize <= 0)
				return give
			
			if (!player.inventory.addItemStackToInventory(give))
				player.dropPlayerItemWithRandomChoice(give, false)
		}
		*/
		
		return InteractionResultHolder.pass(stack)
	}
	
	// PORT: getMaxItemUseDuration → getUseDuration, getItemUseAction → getUseAnimation
	override fun getUseDuration(stack: ItemStack) = if (stack.meta in usable) 40 else 0
	
	override fun getUseAnimation(stack: ItemStack) = when (of(stack.meta)) {
		ElvenWeed    -> UseAnim.BOW
		WisdomBottle -> UseAnim.DRINK
		YggFruit     -> UseAnim.EAT
		else         -> UseAnim.NONE
	}
	
	// PORT: onEaten → finishUsingItem; эффект — MobEffectInstance
	override fun finishUsingItem(stack: ItemStack, world: World, entity: LivingEntity): ItemStack {
		val player = entity
		if (ASJUtilities.isClient || player !is EntityPlayerMP) return stack
		
		when (of(stack.meta)) {
			ElvenWeed -> for (i in ids) player.addEffect(MobEffectInstance(i, 600))
			/* PORT: КТ-8 — бутылка мудрости (Рагнарёк)
			WisdomBottle -> {
				if (!RagnarokHandler.ginnungagap) return stack
				
				val usages = ItemNBTHelper.getInt(stack, TAG_USAGES, 0) + 1
				CardinalSystem.forPlayer(player).wisdom = usages
				ItemNBTHelper.setInt(stack, TAG_USAGES, usages)
				return if (usages >= 3) ItemStack(Items.glass_bottle) else stack
			}
			*/
			YggFruit -> {
				// PORT: КТ-4 — счётчик Лимба маски (ItemTankMask), КТ-7 — потерянные сердца (CardinalSystem)
//				player.limboCounter = 0
//				CardinalSystem.CommonSystem.loseHearts(player, -1)
				player.heal(player.maxHealth)
				// PORT: foodStats.addStats → foodData.eat; эффекты — activeEffects, вредный — категория HARMFUL
				player.foodData.eat(20, 20f)
				player.activeEffects.toList().forEach {
					if (it.effect.category != MobEffectCategory.HARMFUL) return@forEach
					player.removeEffect(it.effect)
				}
//				player.foodStats.addStats(20, 20f)
//				player.activePotionEffects.iterator().onEach { it as PotionEffect
//					if (!Potion.potionTypes[it.potionID].isBadEffect) return@onEach
//					remove()
//					player.onFinishedPotionEffect(it)
//				}
			}
			else -> Unit
		}
		
		stack.shrink(1)
//		stack.stackSize--
		return stack
	}
	
	override fun hasEffect(stack: ItemStack, pass: Int) = stack.meta == WisdomBottle.I || stack.meta == YggFruit.I
	
	override fun hasContainerItem(stack: ItemStack) = stack.meta == Stencil.I && ItemNBTHelper.getInt(stack, TAG_USAGES, 0) < MAX_STENCIL_USES
	
	override fun getContainerItem(stack: ItemStack): ItemStack? {
		val uses = ItemNBTHelper.getInt(stack, TAG_USAGES, 0)
		if (uses == MAX_STENCIL_USES) return null
		
		val copy = stack.copy()
		ItemNBTHelper.setInt(stack, TAG_USAGES, uses + 1)
		
		return copy
	}
	
	// PORT: в 1.20.1 остаток всегда остаётся в сетке крафта; у трафарета у автора — так же
//	override fun doesContainerItemLeaveCraftingGrid(stack: ItemStack) = stack.meta != Stencil.I
	
	// PORT: addInformation → appendHoverText
	override fun appendHoverText(stack: ItemStack, world: World?, tooltip: MutableList<Component>, advanced: TooltipFlag) {
		when (stack.meta) {
			DomainKey.I -> addStringToTooltip(tooltip, "alfheimmisc.creative")
			RiftDrive.I -> {
				// PORT: КТ-3 — TileAnomaly.TAG_SUBTILE_NAME; пока — её значение
				val sub = ItemNBTHelper.getString(stack, /*TileAnomaly.TAG_SUBTILE_NAME*/ "subTileName", "")
				if (sub.isNotEmpty())
					addStringToTooltip(tooltip, "tile.Anomaly.$sub.name")
			}
			// PORT: КТ-3 — мутации кудзу (TileKudzuVine)
			/*
			KudzuSeed.I -> {
				val mutations = if (ItemNBTHelper.verifyExistance(stack, TAG_MUTATIONS)) ItemNBTHelper.getIntArray(stack, TAG_MUTATIONS) else return
				mutations.forEach { addStringToTooltip(tooltip, "item.alfheim:KudzuSeed.mutation.${EnumMutation.entries[it].name}") }
			}
			*/
		}
	}
	
	val singles = arrayOf(WisdomBottle.I, DomainKey.I, Stencil.I)
	
	override fun getItemStackLimit(stack: ItemStack) = if (stack.meta in singles) 1 else 64
	
	// PORT: КТ-3 — бассейн маны, композит и кудзу; КТ-6 — призыв Гайи в Альвхейме: их ветки ниже закомментированы
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val block = world.getBlock(x, y, z)
		/*
		// Fabulous manapool
		if (block === ModBlocks.pool && world.getBlockMetadata(x, y, z) == 0 && stack.meta == RainbowDust.I) {
			world.setBlockMetadataWithNotify(x, y, z, 3, 2)
			stack.stackSize--
			return true
		} else
		*/
		// Rainbow flower
		// PORT: мистический цветок Botania (ModBlocks.flower) — 16 блоков с тегом botania:mystical_flowers; вариант радужной
		// травы — блок массива (SPEC, Р-5)
		if (block.defaultBlockState().`is`(BotaniaTags.Blocks.MYSTICAL_FLOWERS) && stack.meta == RainbowDust.I) {
//		if (block === ModBlocks.flower && stack.meta == RainbowDust.I) {
			world.setBlock(x, y, z, AlfheimBlocks.rainbowGrass[BlockRainbowGrass.FLOWER].defaultBlockState(), 3)
//			world.setBlock(x, y, z, AlfheimBlocks.rainbowGrass, BlockRainbowGrass.FLOWER, 3)
			for (i in 0..40) {
				val color = Color.getHSBColor(Math.random().F + 1f / 2f, 1f, 1f)
				Botania.proxy.wispFX(world,
									 x.D + Math.random(), y.D + Math.random(), z.D + Math.random(),
									 color.red / 255f, color.green / 255f, color.blue / 255f,
									 0.5f, 0f, 0.125f, 0f)
			}
			world.playSoundEffect(x.D, y.D, z.D, "botania:enchanterEnchant", 1f, 1f)
			stack.shrink(1)
//			stack.stackSize--
			return true
		} else
		// Burying petal
		if (side == 1 && world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z) && AlfheimBlocks.rainbowGrass[BlockRainbowGrass.BURIED].canBlockStay(world, x, y + 1, z) && stack.meta == RainbowPetal.I) {
//		if (side == 1 && world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z) && AlfheimBlocks.rainbowGrass.canBlockStay(world, x, y + 1, z) && stack.meta == RainbowPetal.I) {
			if (!world.setBlock(x, y + 1, z, AlfheimBlocks.rainbowGrass[BlockRainbowGrass.BURIED].defaultBlockState(), 3)) return false
//			if (!world.setBlock(x, y + 1, z, AlfheimBlocks.rainbowGrass, BlockRainbowGrass.BURIED, 3)) return false
			
			stack.shrink(1)
//			stack.stackSize--
			return true
		} else
		/*
		// summon Gaia in Alfheim
		if (block inl LibOreDict.beacons && stack.meta == ElvoriumIngot.I) {
			return if (world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim) {
				EntityDoppleganger.spawn(player, stack, world, x, y, z, false)
			} else {
				if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.gaia.wrongitem")
				false
			}
		} else
		// copy composite
		if (stack.meta == Stencil.I && block === AlfheimFluffBlocks.composite) {
			ItemNBTHelper.setCompound(stack, TAG_STENCIL, NBTTagCompound().apply {
				val tile = world.getTileEntity(x, y, z) as? TileComposite ?: return false
				tile.writeCustomNBT(this)
			})
			return true
		} else
		// plant Kudzu
		if (stack.meta == KudzuSeed.I && side == 1 && world.getBlock(x, y + 1, z).isReplaceable(world, x, y + 1, z)) {
			if (ASJUtilities.isServer && EntityFlugel.isTruePlayer(player)) {
				if (!world.setBlock(x, y + 1, z, AlfheimBlocks.kudzuVine, world.rand.nextInt(BlockKudzuVine.ICON_VARS), 3)) return false
				(world.getTileEntity(x, y + 1, z) as? TileKudzuVine)?.startup(ItemNBTHelper.getIntArray(stack, TAG_MUTATIONS), player.commandSenderName)
			}
			
			stack.stackSize--
			return true
		}
		*/
		return false
	}
	
	
	/* PORT: КТ-3 — радужная овца (LensPaintExtender)
	override fun itemInteractionForEntity(stack: ItemStack, player: EntityPlayer?, sheep: EntityLivingBase?): Boolean {
		if (stack.meta != RainbowDust.I ||
		    sheep !is EntitySheep ||
		    sheep.sheared || 
		    ASJSuperWrapperHandler.getFlag(sheep, AlfheimConfigHandler.flagIdSheepRainbow))
			return false
		
		sheep.fleeceColor = 0
		ASJSuperWrapperHandler.setFlag(sheep, AlfheimConfigHandler.flagIdSheepRainbow, true)
		--stack.stackSize
		
		return true
	}
	*/
	
	// PORT: IFuelHandler → время горения предмета 1.20.1
	override fun getBurnTime(stack: ItemStack, recipeType: RecipeType<*>?) = getBurnTime(stack)
	
	// PORT: предмет с вариантами — массив предметов
	fun getBurnTime(fuel: ItemStack): Int {
		if (fuel.item in AlfheimItems.elvenResource) {
			return when (of(fuel.meta)) {
				InfusedDreamwoodTwig, ThunderwoodTwig     -> 600 // 2
				NetherwoodTwig                            -> 4000 // 20
				MuspelheimEssence                         -> 12800 // 64
				NetherwoodSplinters, ThunderwoodSplinters -> 100 // 0.5
				NetherwoodCoal                            -> 2400 // 12
				else                                      -> 0
			}
		}
		return 0
	}
	
	companion object {
		
		// PORT: иконки, которые рисуют другие предметы и эффекты, переносятся с ними (КТ-4, КТ-7)
		/*
		lateinit var amulet: IIcon
		lateinit var candy: IIcon
		lateinit var flugel: IIcon
		lateinit var harp: IIcon
		lateinit var kitty: IIcon
		lateinit var mine: IIcon
		lateinit var wind: IIcon
		lateinit var wing: IIcon
		
		lateinit var drive1: IIcon
		lateinit var weed1: IIcon
		*/
		
		const val MAX_STENCIL_USES = 100
		
		const val TAG_ELEMENT = "element"
		const val TAG_RAINBOW = "rainbow"
		const val TAG_STENCIL = "stencil"
		const val TAG_USAGES = "usages"
		const val TAG_MUTATIONS = "mutations"
		
		/* PORT: КТ-4 — стихии (ElementalDamage)
		private val ItemStack.element get() = ElementalDamage.valueOf(ItemNBTHelper.getString(this, TAG_ELEMENT, ElementalDamage.COMMON.name))
		
		fun ballForElement(element: ElementalDamage?, size: Int = 1): ItemStack {
			val stack = ElementalSlimeBall.stack(size)
			if (element != null)
				ItemNBTHelper.setString(stack, TAG_ELEMENT, element.name)
			else {
				ItemNBTHelper.setString(stack, TAG_ELEMENT, ElementalDamage.COMMON.name)
				ItemNBTHelper.setBoolean(stack, TAG_RAINBOW, true)
			}
			return stack
		}
		*/
	}
}

enum class ElvenResourcesMetas {
	
	InterdimensionalGatewayCore,
	ManaInfusionCore,
	DasRheingold,
	ElvoriumIngot,
	MauftriumIngot,
	MuspelheimPowerIngot,
	NiflheimPowerIngot,
	ElvoriumNugget,
	MauftriumNugget,
	MuspelheimEssence,
	NiflheimEssence,
	RainbowQuartz,
	RainbowPetal,
	RainbowDust,
	IffesalDust,
	PrimalRune,
	MuspelheimRune,
	NiflheimRune,
	InfusedDreamwoodTwig,
	ThunderwoodTwig,
	NetherwoodTwig,
	ThunderwoodSplinters,
	NetherwoodSplinters,
	NetherwoodCoal,
	ElvenWeed,
	Jug,
	GrapeLeaf,
	FenrirFur,
	WisdomBottle,
	Nifleur,
	YggFruit,
	RiftShardEmpty,
	RiftShardGinnungagap,
	RiftShardMuspelheim,
	RiftShardNiflheim,
	RiftDrive,
	DomainKey,
	SaveIvy,
	ElementalSlimeBall,
	Stencil,
	KudzuSeed,
	KudzuSprout,
	;
	
	val I get() = ordinal
	
	val stack get() = stack(1)
	
	// PORT: вариант — отдельный предмет (SPEC, Р-5)
	fun stack(size: Int) = ItemStack(AlfheimItems.elvenResource[I], size)
//	fun stack(size: Int) = ItemStack(AlfheimItems.elvenResource, size, I)
	
	companion object {
		
		val displayBlackList = arrayOf(ElvenWeed, WisdomBottle)
		
		fun of(meta: Int) = entries.getOrNull(meta)
	}
}
