package alfheim.common.core.asm.transformer

import alexsocol.patcher.asm.ASJHookLoader.Companion.OBF
import alexsocol.patcher.asm.transformer.ASJAbstractClassTransformer
import alfheim.api.ModInfo
import alfheim.common.core.asm.hook.extender.ItemLensExtender
import alfheim.common.core.handler.AlfheimConfigHandler
import gloomyfolken.hooklib.asm.HookLogger.Log4JLogger
import org.lwjgl.opengl.GL11
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import vazkii.botania.api.subtile.SubTileEntity
import vazkii.botania.common.block.tile.TileSpecialFlower

@Suppress("NAME_SHADOWING", "ClassName", "unused", "LocalVariableName", "PrivatePropertyName")
class AlfheimClassTransformer: ASJAbstractClassTransformer() {
	
	override val logger = Log4JLogger(ModInfo.MODID)
	
	override fun transform(transformedName: String, basicClass: ByteArray): ByteArray {
		return when (transformedName) {
			"net.minecraft.client.renderer.RenderGlobal"                       -> core { `RenderGlobal$ClassVisitor`(it) }
			"net.minecraft.entity.EntityLivingBase"                            -> core { `EntityLivingBase$ClassVisitor`(it) }
			"net.minecraft.entity.EntityTrackerEntry"                          -> core { `EntityTrackerEntry$ClassVisitor`(it) }
			"net.minecraft.potion.Potion"                                      -> core { `Potion$ClassVisitor`(it) }
			"thaumcraft.common.items.ItemNugget"                               -> core { `ItemNugget$ClassVisitor`(it) }
			"vazkii.botania.client.core.handler.BaubleRenderHandler"           -> core { `BaubleRenderHandler$ClassVisitor`(it) }
			"vazkii.botania.client.core.handler.LightningHandler"              -> core { `LightningHandler$ClassVisitor`(it) }
			"vazkii.botania.client.core.handler.TooltipAdditionDisplayHandler" -> core { `TooltipAdditionDisplayHandler$ClassVisitor`(it) }
			"vazkii.botania.client.core.helper.RenderHelper"                   -> core { `RenderHelper$ClassVisitor`(it) }
			"vazkii.botania.client.render.tile.RenderTileFloatingFlower"       -> core { `RenderTileFloatingFlower$ClassVisitor`(it) }
			
			"vazkii.botania.common.block.decor.IFloatingFlower\$IslandType"    -> tree {
				if (OBF || it.methods.any { m -> m.name == "getColor" && m.desc == "()I"}) return@tree
				
				val mn = MethodNode(ACC_PUBLIC, "getColor", "()I", null, null)
				mn.instructions.add(LdcInsnNode(Integer(16777215)))
				mn.instructions.add(InsnNode(IRETURN))
				it.methods.add(mn)
			}
			
			"vazkii.botania.common.block.tile.TileManaFlame"                   -> core { `TileManaFlame$ClassVisitor`(it) }
			"vazkii.botania.common.block.tile.TileSpecialFlower"               -> core { `TileSpecialFlower$ClassVisitor`(it) }
			"vazkii.botania.common.entity.EntityDoppleganger"                  -> core(ClassReader.SKIP_FRAMES) { `EntityDoppleganger$ClassVisitor`(it) }
			"vazkii.botania.common.item.ItemFlowerBag"                         -> core { `ItemFlowerBag$ClassVisitor`(it) }
			"vazkii.botania.common.item.equipment.bauble.ItemMiningRing",
			"vazkii.botania.common.item.equipment.bauble.ItemWaterRing"        -> core { `ItemInfiniEffect$ClassVisitor`(transformedName.split("\\.".toRegex())[6], it) }
			"vazkii.botania.common.item.lens.ItemLens"                         -> core { `ItemLens$ClassVisitor`(it) }
			"vazkii.botania.common.item.relic.ItemAesirRing"                   -> core { `ItemAesirRing$ClassVisitor`(it) }
			"vazkii.botania.common.item.rod.ItemTerraformRod"                  -> core { `ItemTerraformRod$ClassVisitor`(it) }
			"vazkii.botania.common.lib.LibItemNames"                           -> core { `LibItemNames$ClassVisitor`(it) }
			// fixes for stupid coders:
			"cofh.thermalfoundation.fluid.TFFluids"                            -> core { `TFFluids$ClassVisitor`(it) }
			"com.emoniph.witchery.client.ClientEvents\$GUIOverlay"             -> core { `ClientEvents$GUIOverlay$ClassVisitor`(it) }
			else                                                               -> this.basicClass
		}
	}
	
	// Gleipnir hook
	private inner class `RenderGlobal$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "renderEntities" || name == "a" && desc == "(Lsv;Lbmv;F)V") {
				logger.debug("Visiting RenderGlobal#renderEntities: $name$desc")
				return `RenderGlobal$addEffect$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `RenderGlobal$addEffect$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var inject = true
			
			override fun visitVarInsn(opcode: Int, `var`: Int) {
				super.visitVarInsn(opcode, `var`)
				
				if (inject && opcode == ISTORE && `var` == 21) {
					inject = false
					
					mv.visitFieldInsn(GETSTATIC, "alfheim/common/item/relic/LeashingHandler", "INSTANCE", "Lalfheim/common/item/relic/LeashingHandler;")
					mv.visitVarInsn(ILOAD, 21)
					mv.visitVarInsn(ALOAD, 20)
					mv.visitVarInsn(ALOAD, 2)
					mv.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/item/relic/LeashingHandler", "isBoundInRender", if (OBF) "(ZLsa;Lbmv;)Z" else "(ZLnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;)Z", false)
					mv.visitVarInsn(ISTORE, 21)
				}
			}
		}
	}
	
	private inner class `EntityLivingBase$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == (if (OBF) "e" else "moveEntityWithHeading") && desc == "(FF)V") {
				logger.debug("Visiting EntityLivingBase#moveEntityWithHeading: $name$desc")
				return `EntityLivingBase$moveEntityWithHeading$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `EntityLivingBase$moveEntityWithHeading$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, desc: String?) {
				if (opcode == GETFIELD && owner == (if (OBF) "aji" else "net/minecraft/block/Block") && name == (if (OBF) "K" else "slipperiness") && desc == "F") {
					mv.visitVarInsn(ALOAD, 0)
					mv.visitTypeInsn(CHECKCAST, if (OBF) "sa" else "net/minecraft/entity/Entity")
					mv.visitMethodInsn(INVOKEVIRTUAL, if (OBF) "aji" else "net/minecraft/block/Block", "getRelativeSlipperiness", if (OBF) "(Lsa;)F" else "(Lnet/minecraft/entity/Entity;)F", false)
				} else
					super.visitFieldInsn(opcode, owner, name, desc)
			}
		}
	}
	
	private inner class `EntityTrackerEntry$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "tryStartWachingThis" || name == "b" && desc == "(Lmw;)V") {
				logger.debug("Visiting EntityTrackerEntry#tryStartWachingThis: $name$desc")
				return `EntityTrackerEntry$tryStartWachingThis$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `EntityTrackerEntry$tryStartWachingThis$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			private var sended = false
			
			override fun visitVarInsn(opcode: Int, `var`: Int) {
				super.visitVarInsn(opcode, `var`)
				if (opcode == ALOAD && `var` == 6 && !sended) {
					sended = true
					visitVarInsn(ALOAD, 0)
					visitFieldInsn(GETFIELD, if (OBF) "my" else "net/minecraft/entity/EntityTrackerEntry", if (OBF) "a" else "myEntity", if (OBF) "Lsa;" else "Lnet/minecraft/entity/Entity;")
					visitMethodInsn(INVOKESTATIC, "alfheim/common/core/handler/CardinalSystem\$PartySystem", "notifySpawn", if (OBF) "(Lsa;)V" else "(Lnet/minecraft/entity/Entity;)V", false)
				}
			}
		}
	}
	
	private inner class `Potion$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "performEffect" || name == "a" && desc == "(Lsv;I)V") {
				logger.debug("Visiting Potion#performEffect: $name$desc")
				return `Potion$performEffect$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `Potion$performEffect$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var flag = false
			
			override fun visitFieldInsn(opcode: Int, owner: String, name: String, desc: String) {
				if (flag && opcode == GETSTATIC && (owner == "net/minecraft/util/DamageSource" || owner == "ro") && (name == "magic" || name == "k") && (desc == "Lnet/minecraft/util/DamageSource;" || desc == "Lro;")) {
					flag = false
					super.visitFieldInsn(GETSTATIC, "alfheim/common/core/util/DamageSourceSpell", "Companion", "Lalfheim/common/core/util/DamageSourceSpell\$Companion;")
					super.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/core/util/DamageSourceSpell\$Companion", "getPoison", if (OBF) "()Lro;" else "()Lnet/minecraft/util/DamageSource;", false)
					return
				} else if (opcode == GETSTATIC && (owner == "net/minecraft/potion/Potion" || owner == "rv") && (name == "poison" || name == "u") && (desc == "Lnet/minecraft/potion/Potion;" || desc == "Lrv;")) flag = true
				
				super.visitFieldInsn(opcode, owner, name, desc)
			}
		}
	}
	
	private inner class `ItemNugget$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "registerIcons" || (name == "func_94581_a")) {
				logger.debug("Visiting ItemNugget#registerIcons: $name$desc")
				return `ItemNugget$registerIcons$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			if (name == "getSubItems" || (name == "func_150895_a")) {
				logger.debug("Visiting ItemNugget#getSubItems: $name$desc")
				return `ItemNugget$getSubItems$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `ItemNugget$registerIcons$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitInsn(opcode: Int) {
				if (opcode == RETURN) {
					visitVarInsn(ALOAD, 0)
					visitFieldInsn(GETFIELD, "thaumcraft/common/items/ItemNugget", "icon", if (OBF) "[Lrf;" else "[Lnet/minecraft/util/IIcon;")
					visitIntInsn(BIPUSH, AlfheimConfigHandler.elementiumClusterMeta)
					visitVarInsn(ALOAD, 1)
					visitLdcInsn("thaumcraft:clusterelementium")
					visitMethodInsn(INVOKEINTERFACE, if (OBF) "rg" else "net/minecraft/client/renderer/texture/IIconRegister", if (OBF) "a" else "registerIcon", if (OBF) "(Ljava/lang/String;)Lrf;" else "(Ljava/lang/String;)Lnet/minecraft/util/IIcon;", true)
					visitInsn(AASTORE)
					val l15_5 = Label()
					visitLabel(l15_5)
					visitLineNumber(47, l15_5)
				}
				super.visitInsn(opcode)
			}
		}
		
		private inner class `ItemNugget$getSubItems$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitInsn(opcode: Int) {
				if (opcode == RETURN) {
					visitVarInsn(ALOAD, 3)
					visitTypeInsn(NEW, if (OBF) "add" else "net/minecraft/item/ItemStack")
					visitInsn(DUP)
					visitVarInsn(ALOAD, 0)
					visitInsn(ICONST_1)
					visitIntInsn(BIPUSH, AlfheimConfigHandler.elementiumClusterMeta)
					visitMethodInsn(INVOKESPECIAL, if (OBF) "add" else "net/minecraft/item/ItemStack", "<init>", if (OBF) "(Ladb;II)V" else "(Lnet/minecraft/item/Item;II)V", false)
					visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true)
					visitInsn(POP)
				}
				super.visitInsn(opcode)
			}
		}
	}
	
	private inner class `LightningHandler$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "onRenderWorldLast") {
				logger.debug("Visiting LightningHandler#onRenderWorldLast: $name$desc")
				return `LightningHandler$onRenderWorldLast$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `LightningHandler$onRenderWorldLast$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitMethodInsn(opcode: Int, owner: String, name: String, desc: String, itf: Boolean) {
				super.visitMethodInsn(opcode, owner, name, desc, itf)
				
				if (opcode == INVOKESTATIC) {
					if (name == "glPushMatrix") {
						mv.visitIntInsn(SIPUSH, GL11.GL_CULL_FACE)
						mv.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glDisable", "(I)V", false)
					} else if (name == "glPopmatrix") {
						mv.visitIntInsn(SIPUSH, GL11.GL_CULL_FACE)
						mv.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glEnable", "(I)V", false)
					}
				}
			}
		}
	}
	
	private inner class `TooltipAdditionDisplayHandler$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "render") {
				logger.debug("Visiting TooltipAdditionDisplayHandler#render: $name$desc")
				return `TooltipAdditionDisplayHandler$render$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `TooltipAdditionDisplayHandler$render$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var gets = 0
			
			override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, desc: String?) {
				if (opcode == GETFIELD && name == "entry" && ++gets == 2) return
				super.visitFieldInsn(opcode, owner, name, desc)
			}
			
			override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, desc: String?, itf: Boolean) {
				var newName = name
				var newDesc = desc
				
				if (name == "setEntryToOpen") {
					newName = "setEntryDataToOpen"
					newDesc = "(Lvazkii/botania/api/lexicon/LexiconRecipeMappings\$EntryData;)V"
				}
				
				super.visitMethodInsn(opcode, owner, newName, newDesc, itf)
			}
		}
	}
	
	// Fix for progress pie integrity on full progress
	private inner class `RenderHelper$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "renderProgressPie") {
				logger.debug("Visiting RenderHelper#renderProgressPie: $name$desc")
				return `RenderHelper$renderProgressPie$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `RenderHelper$renderProgressPie$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitJumpInsn(opcode: Int, label: Label?) {
				if (opcode != IFLE) return super.visitJumpInsn(opcode, label)
				
				super.visitInsn(ICONST_M1)
				super.visitJumpInsn(IF_ICMPLE, label)
			}
		}
	}
	
	private inner class `BaubleRenderHandler$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "renderManaTablet") {
				logger.debug("Visiting BaubleRenderHandler#renderManaTablet: $name$desc")
				return `BaubleRenderHandler$renderManaTablet$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `BaubleRenderHandler$renderManaTablet$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitLdcInsn(cst: Any) {
				var cst = cst
				if (cst is Float && cst == 0.2f) cst = 0.33f
				super.visitLdcInsn(cst)
			}
		}
	}
	
	private inner class `RenderTileFloatingFlower$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "renderTileEntityAt") {
				logger.debug("Visiting RenderTileFloatingFlower#renderTileEntityAt: $name$desc")
				return `RenderTileFloatingFlower$renderTileEntityAt$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `RenderTileFloatingFlower$renderTileEntityAt$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var before = false
			var after = true
			
			override fun visitMethodInsn(opcode: Int, owner: String, name: String, desc: String?, itf: Boolean) {
				if (name == "glPushMatrix") {
					if (before) {
						mv.visitTypeInsn(NEW, "java/awt/Color")
						mv.visitInsn(DUP)
						mv.visitVarInsn(ALOAD, 9)
						mv.visitMethodInsn(INVOKEINTERFACE, "vazkii/botania/common/block/decor/IFloatingFlower", "getIslandType", "()Lvazkii/botania/common/block/decor/IFloatingFlower\$IslandType;", true)
						mv.visitMethodInsn(INVOKEVIRTUAL, "vazkii/botania/common/block/decor/IFloatingFlower\$IslandType", "getColor", "()I", false)
						mv.visitMethodInsn(INVOKESPECIAL, "java/awt/Color", "<init>", "(I)V", false)
						mv.visitVarInsn(ASTORE, 12)
						
						mv.visitVarInsn(ALOAD, 12)
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Color", "getRed", "()I", false)
						mv.visitInsn(I2F)
						mv.visitLdcInsn(java.lang.Float("255.0"))
						mv.visitInsn(FDIV)
						mv.visitVarInsn(ALOAD, 12)
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Color", "getGreen", "()I", false)
						mv.visitInsn(I2F)
						mv.visitLdcInsn(java.lang.Float("255.0"))
						mv.visitInsn(FDIV)
						mv.visitVarInsn(ALOAD, 12)
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Color", "getBlue", "()I", false)
						mv.visitInsn(I2F)
						mv.visitLdcInsn(java.lang.Float("255.0"))
						mv.visitInsn(FDIV)
						mv.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glColor3f", "(FFF)V", false)
					} else before = true
				} else if (name == "glPopMatrix") {
					if (after) {
						mv.visitInsn(FCONST_1)
						mv.visitInsn(FCONST_1)
						mv.visitInsn(FCONST_1)
						mv.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glColor3f", "(FFF)V", false)
					} else after = false
				}
				
				super.visitMethodInsn(opcode, owner, name, desc, itf)
			}
		}
	}
	
	private inner class `TileManaFlame$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
			val mv = super.visitMethod(access, name, desc, signature, exceptions)
			return if (name != "getColor" && name != "writeCustomNBT") `TileManaFlame$MethodVisitor`(mv) else mv
		}
		
		// Вазки ты еблан :з
		private inner class `TileManaFlame$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, desc: String?) {
				if (opcode == GETFIELD && name == "color")
					super.visitMethodInsn(INVOKEVIRTUAL, "vazkii/botania/common/block/tile/TileManaFlame", "getColor", "()I", false)
				else
					super.visitFieldInsn(opcode, owner, name, desc)
			}
		}
	}
	
	private inner class `TileSpecialFlower$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {

		override fun visitField(access: Int, name: String?, desc: String?, signature: String?, value: Any?): FieldVisitor {
			val newVal = if (value == TileSpecialFlower.TAG_SUBTILE_NAME) SubTileEntity.TAG_TYPE else value
			return super.visitField(access, name, desc, signature, newVal)
		}

		override fun visitMethod(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
			return `TileSpecialFlower$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
		}

		private inner class `TileSpecialFlower$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {

			override fun visitLdcInsn(cst: Any?) {
				val newCst = if (cst == TileSpecialFlower.TAG_SUBTILE_NAME) SubTileEntity.TAG_TYPE else cst

				super.visitLdcInsn(newCst)
			}
		}
	}
	
	private inner class `EntityDoppleganger$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
			super.visit(version, access, name, signature, superName, arrayOf("alfheim/api/boss/IBotaniaBossWithShaderAndName"))
		}
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "getBossBarTextureRect") {
				logger.debug("Visiting EntityDoppleganger#getBossBarTextureRect: $name$desc")
				return `EntityDoppleganger$getBossBarTextureRect$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `EntityDoppleganger$getBossBarTextureRect$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var inject = 2
			
			override fun visitInsn(opcode: Int) {
				if (opcode == ICONST_0 && --inject == 0)
					super.visitIntInsn(BIPUSH, AlfheimConfigHandler.gaiaBarOffset * 22)
				else
					super.visitInsn(opcode)
			}
		}
	}
	
	private inner class `ItemFlowerBag$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "loadStacks") {
				logger.debug("Visiting ItemFlowerBag#loadStacks: $name$desc")
				return `ItemFlowerBag$loadStacks$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `ItemFlowerBag$loadStacks$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitIntInsn(opcode: Int, operand: Int) {
				val oper = if (opcode == BIPUSH && operand == 16) 34 else operand
				super.visitIntInsn(opcode, oper)
			}
		}
	}
	
	private inner class `ItemInfiniEffect$ClassVisitor`(val className: String, cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "onWornTick") {
				logger.debug("Visiting $className#onWornTick: $name$desc")
				return `ItemInfiniEffect$onWornTick$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `ItemInfiniEffect$onWornTick$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitLdcInsn(cst: Any?) {
				if (cst == Integer.MAX_VALUE)
					super.visitLdcInsn(20)
				else
					super.visitLdcInsn(cst)
			}
		}
	}
	
	private inner class `ItemLens$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		var left = 2
		
		override fun visitField(access: Int, name: String, desc: String, signature: String?, value: Any?): FieldVisitor {
			var value = value
			if (name == "SUBTYPES") {
				value = 22 + moreLenses
			}
			return super.visitField(access, name, desc, signature, value)
		}
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			val mv = super.visitMethod(access, name, desc, signature, exceptions)
			
			logger.debug("Visiting ItemLens#$name: $name$desc")
			return `ItemLens$MethodVisitor`(mv)
		}
		
		private inner class `ItemLens$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitIntInsn(opcode: Int, operand: Int) {
				var operand = operand
				if (opcode == BIPUSH) {
					if (operand == 22) {        // 4 injections for #SUBTYPES
						operand += moreLenses
					} else if (operand == 21) { // 2 injections for #SUBTYPES-1
						if (left-- > 0) {       // 4 injections total
							operand += moreLenses
						}
					}
				}
				
				super.visitIntInsn(opcode, operand)
			}
		}
	}
	
	private inner class `ItemAesirRing$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "onDropped") {
				logger.debug("Visiting ItemAesirRing#onDropped: $name$desc")
				return `ItemAesirRing$onDropped$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `ItemAesirRing$onDropped$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitInsn(opcode: Int) {
				if (opcode == ICONST_3)
					super.visitIntInsn(BIPUSH, 6)
				else
					super.visitInsn(opcode)
			}
			
			override fun visitVarInsn(opcode: Int, `var`: Int) {
				if (opcode == ASTORE && `var` == 4) {
					mv.visitInsn(DUP)
					mv.visitInsn(ICONST_3)
					mv.visitFieldInsn(GETSTATIC, "alfheim/common/item/AlfheimItems", "INSTANCE", "Lalfheim/common/item/AlfheimItems;")
					mv.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/item/AlfheimItems", "getPriestRingSif", if (OBF) "()Ladb;" else "()Lnet/minecraft/item/Item;", false)
					mv.visitInsn(AASTORE)
					mv.visitInsn(DUP)
					mv.visitInsn(ICONST_4)
					mv.visitFieldInsn(GETSTATIC, "alfheim/common/item/AlfheimItems", "INSTANCE", "Lalfheim/common/item/AlfheimItems;")
					mv.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/item/AlfheimItems", "getPriestRingNjord", if (OBF) "()Ladb;" else "()Lnet/minecraft/item/Item;", false)
					mv.visitInsn(AASTORE)
					mv.visitInsn(DUP)
					mv.visitInsn(ICONST_5)
					mv.visitFieldInsn(GETSTATIC, "alfheim/common/item/AlfheimItems", "INSTANCE", "Lalfheim/common/item/AlfheimItems;")
					mv.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/item/AlfheimItems", "getPriestRingHeimdall", if (OBF) "()Ladb;" else "()Lnet/minecraft/item/Item;", false)
					mv.visitInsn(AASTORE)
				}
				
				super.visitVarInsn(opcode, `var`)
			}
		}
	}
	
	private inner class `ItemTerraformRod$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "terraform") {
				logger.debug("Visiting ItemTerraformRod#terraform: $name$desc")
				return `ItemTerraformRod$terraform$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `ItemTerraformRod$terraform$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var put = true
			
			override fun visitVarInsn(opcode: Int, `var`: Int) {
				super.visitVarInsn(opcode, `var`)
				
				if (opcode == ISTORE && `var` == 4 && put) {
					put = false
					
					mv.visitFieldInsn(GETSTATIC, "alfheim/common/core/asm/hook/fixes/GodAttributesHooks", "INSTANCE", "Lalfheim/common/core/asm/hook/fixes/GodAttributesHooks;")
					mv.visitVarInsn(ALOAD, 3)
					mv.visitVarInsn(ILOAD, 4)
					mv.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/core/asm/hook/fixes/GodAttributesHooks", "getRange", "(Lnet/minecraft/entity/player/EntityPlayer;I)I", false)
					mv.visitVarInsn(ISTORE, 4)
				}
			}
		}
	}
	
	private inner class `LibItemNames$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "<clinit>") {
				logger.debug("Visiting LibItemNames#<clinit>: $name$desc")
				return `LibItemNames$clinit$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `LibItemNames$clinit$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			private var twotwo_twofour = true
			private var add = false
			private var twoone = true
			
			override fun visitIntInsn(opcode: Int, operand: Int) {
				var operand = operand
				if (opcode == BIPUSH) {
					if (operand == 22) {
						if (twotwo_twofour) {
							twotwo_twofour = false
							operand += moreLenses
						}
					}
					
					if (operand == 21) {
						if (twoone) {
							twoone = false
							add = true
						}
					}
				}
				
				super.visitIntInsn(opcode, operand)
			}
			
			override fun visitInsn(opcode: Int) {
				super.visitInsn(opcode)
				
				if (opcode == AASTORE) {
					if (add) {
						add = false
						
						ItemLensExtender.EnumAlfheimLens.entries.forEach {
							mv.visitInsn(DUP)
							mv.visitIntInsn(BIPUSH, it.meta)
							mv.visitLdcInsn(it.unlocalizedName)
							mv.visitInsn(AASTORE)
						}
					}
				}
			}
		}
	}
	
	private inner class `TFFluids$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "registerFluid") {
				logger.debug("Visiting ThermalFoundation's TFFluids#registerFluid: $name$desc")
				return `TFFluids$registerFluid$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `TFFluids$registerFluid$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitVarInsn(opcode: Int, i: Int) {
				if (opcode == ASTORE && i == 0) {
					super.visitInsn(POP)
					
					super.visitVarInsn(ALOAD, 1)
					super.visitMethodInsn(INVOKESTATIC, "alfheim/common/integration/ThermalFoundationIntegration\$Hooks", "postRegisterFluid", "(Ljava/lang/String;)V", false)
					return
				}
				
				super.visitVarInsn(opcode, i)
			}
		}
	}
	
	private inner class `ClientEvents$GUIOverlay$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "renderHotbar") {
				logger.debug("Visiting witchery's ClientEvents\$GUIOverlay#renderHotbar: $name$desc")
				return `ClientEvents$GUIOverlay$renderHotbar$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		private inner class `ClientEvents$GUIOverlay$renderHotbar$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var aload1 = false
			
			override fun visitVarInsn(opcode: Int, operand: Int) {
				if (opcode == ALOAD) {
					aload1 = operand == 1
				}
				
				super.visitVarInsn(opcode, operand)
			}
			
			override fun visitInsn(opcode: Int) {
				super.visitInsn(if (opcode == ICONST_1) ICONST_0 else opcode)
			}
		}
	}
	
	companion object {
		val moreLenses get() = ItemLensExtender.EnumAlfheimLens.entries.size
	}
}