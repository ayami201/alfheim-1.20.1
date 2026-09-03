package alfheim.client.model.entity

import alexsocol.asjlib.mc
import alfheim.api.*
import alfheim.api.lib.*
import net.minecraft.client.model.*
import net.minecraft.entity.*
import net.minecraft.util.*
import net.minecraftforge.client.model.*
import org.lwjgl.opengl.GL11.*
import java.util.*

/**
 * ModelEntityFlowerBud - AlexSocol
 * Created using Tabula 4.1.1
 */
object ModelEntityFlowerBud: ModelBase() {
	
	val main = AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/FlowerBud.obj"))!!
	
//	var Bud1: ModelRenderer
//	var Bud2: ModelRenderer
//	var Bud3: ModelRenderer
//	var Bud4: ModelRenderer
//	var LeafCorner1: ModelRenderer
//	var LeafCorner2: ModelRenderer
//	var LeafCorner3: ModelRenderer
//	var LeafCorner4: ModelRenderer
//	var LeafTop1: ModelRenderer
//	var LeafTop2: ModelRenderer
//	var LeafTop3: ModelRenderer
//	var LeafTop4: ModelRenderer
//	var LeafBottom1: ModelRenderer
//	var LeafBottom2: ModelRenderer
//	var LeafBottom3: ModelRenderer
//	var LeafBottom4: ModelRenderer
//	var Base: ModelRenderer
	
	var TentacleBase: ModelRenderer
	var Spike1: ModelRenderer
	var Spike2: ModelRenderer
	var Spike3: ModelRenderer
	var Spike4: ModelRenderer
	var Spike5: ModelRenderer
	var Spike6: ModelRenderer
	var Spike7: ModelRenderer
	var Spike8: ModelRenderer
	var Spike9: ModelRenderer
	var Spike10: ModelRenderer
	var Spike11: ModelRenderer
	var Spike12: ModelRenderer
	var Spike13: ModelRenderer
	var Spike14: ModelRenderer
	var Spike15: ModelRenderer
	var Spike16: ModelRenderer
	var Spike17: ModelRenderer
	var Spike18: ModelRenderer
	
	init {
		textureWidth = 64
		textureHeight = 64
		
//		Base = ModelRenderer(this, 12, 42)
//		Base.addBox(-4.0f, 13.0f, -4.0f, 8, 4, 8, 0.0f)
//		Base.setRotationPoint(0.0f, 0.0f, 0.0f)
//		Bud1 = ModelRenderer(this, 0, 0)
//		Bud1.addBox(-2.5f, 0.0f, -2.5f, 5, 14, 5, 0.0f)
//		Bud1.setRotationPoint(0.0f, 0.0f, 0.0f)
//		Bud2 = ModelRenderer(this, 5, 0)
//		Bud2.addBox(-2.5f, 0.0f, -2.5f, 5, 14, 5, 0.0f)
//		Bud2.setRotationPoint(0.0f, 0.0f, 0.0f)
//		Bud3 = ModelRenderer(this, 10, 0)
//		Bud3.addBox(-2.5f, 0.0f, -2.5f, 5, 14, 5, 0.0f)
//		Bud3.setRotationPoint(0.0f, 0.0f, 0.0f)
//		Bud4 = ModelRenderer(this, 15, 0)
//		Bud4.addBox(-2.5f, 0.0f, -2.5f, 5, 14, 5, 0.0f)
//		Bud4.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafBottom1 = ModelRenderer(this, 0, 39)
//		LeafBottom1.addBox(-5.0f, 13.5f, 2.0f, 10, 7, 0, 0.0f)
//		LeafBottom1.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafBottom2 = ModelRenderer(this, 0, 39)
//		LeafBottom2.addBox(-5.0f, 13.5f, 2.0f, 10, 7, 0, 0.0f)
//		LeafBottom2.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafBottom3 = ModelRenderer(this, 0, 39)
//		LeafBottom3.addBox(-5.0f, 13.5f, 2.0f, 10, 7, 0, 0.0f)
//		LeafBottom3.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafBottom4 = ModelRenderer(this, 0, 39)
//		LeafBottom4.addBox(-5.0f, 13.5f, 2.0f, 10, 7, 0, 0.0f)
//		LeafBottom4.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafCorner1 = ModelRenderer(this, 35, 0)
//		LeafCorner1.addBox(-2.6f, 7.5f, -2.6f, 7, 7, 7, 0.0f)
//		LeafCorner1.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafCorner2 = ModelRenderer(this, 28, 14)
//		LeafCorner2.addBox(-2.6f, 7.5f, -4.4f, 7, 7, 7, 0.0f)
//		LeafCorner2.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafCorner3 = ModelRenderer(this, 21, 28)
//		LeafCorner3.addBox(-4.4f, 7.5f, -4.4f, 7, 7, 7, 0.0f)
//		LeafCorner3.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafCorner4 = ModelRenderer(this, 0, 19)
//		LeafCorner4.addBox(-4.4f, 7.5f, -2.6f, 7, 7, 7, 0.0f)
//		LeafCorner4.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafTop1 = ModelRenderer(this, 0, 33)
//		LeafTop1.addBox(-4.0f, 7.0f, -6.25f, 8, 6, 0, 0.0f)
//		LeafTop1.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafTop2 = ModelRenderer(this, 0, 33)
//		LeafTop2.addBox(-4.0f, 7.0f, -6.25f, 8, 6, 0, 0.0f)
//		LeafTop2.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafTop3 = ModelRenderer(this, 0, 33)
//		LeafTop3.addBox(-4.0f, 7.0f, -6.25f, 8, 6, 0, 0.0f)
//		LeafTop3.setRotationPoint(0.0f, 0.0f, 0.0f)
//		LeafTop4 = ModelRenderer(this, 0, 33)
//		LeafTop4.addBox(-4.0f, 7.0f, -6.25f, 8, 6, 0, 0.0f)
//		LeafTop4.setRotationPoint(0.0f, 0.0f, 0.0f)
		
//		setRotateAngle(Bud1, 0.17453292f, 0.017453292f, -0.17453292f)
//		setRotateAngle(Bud2, -0.17453292f, -0.017453292f, -0.17453292f)
//		setRotateAngle(Bud3, -0.17453292f, 0.017453292f, 0.17453292f)
//		setRotateAngle(Bud4, 0.17453292f, -0.017453292f, 0.17453292f)
//		setRotateAngle(LeafBottom1, -0.43633232f, 0.0f, 0.0f)
//		setRotateAngle(LeafBottom2, -0.43633232f, 1.5707964f, 0.0f)
//		setRotateAngle(LeafBottom3, -0.43633232f, 3.1415927f, 0.0f)
//		setRotateAngle(LeafBottom4, -0.43633232f, -1.5707964f, 0.0f)
//		setRotateAngle(LeafCorner1, 0.17453292f, 0.017453292f, -0.17453292f)
//		setRotateAngle(LeafCorner2, -0.17453292f, -0.017453292f, -0.17453292f)
//		setRotateAngle(LeafCorner3, -0.17453292f, 0.017453292f, 0.17453292f)
//		setRotateAngle(LeafCorner4, 0.17453292f, -0.017453292f, 0.17453292f)
//		setRotateAngle(LeafTop1, 0.08726646f, 0.0f, 0.0f)
//		setRotateAngle(LeafTop2, 0.08726646f, 1.5707964f, 0.0f)
//		setRotateAngle(LeafTop3, 0.08726646f, 3.1415927f, 0.0f)
//		setRotateAngle(LeafTop4, 0.08726646f, -1.5707964f, 0.0f)
		
		Spike1 = ModelRenderer(this, 0, 0)
		Spike1.addBox(0.5f, -3.0f, 0.5f, 1, 1, 1, 0.0f)
		Spike1.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike2 = ModelRenderer(this, 0, 0)
		Spike2.addBox(0.0f, -3.0f, -1.25f, 1, 1, 1, 0.0f)
		Spike2.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike3 = ModelRenderer(this, 0, 0)
		Spike3.addBox(-1.5f, -3.0f, -0.5f, 1, 1, 1, 0.0f)
		Spike3.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike4 = ModelRenderer(this, 0, 0)
		Spike4.addBox(2.0f, -1.5f, 0.0f, 1, 1, 1, 0.0f)
		Spike4.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike5 = ModelRenderer(this, 0, 0)
		Spike5.addBox(2.0f, 0.0f, -1.5f, 1, 1, 1, 0.0f)
		Spike5.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike6 = ModelRenderer(this, 0, 0)
		Spike6.addBox(2.0f, 0.5f, 0.5f, 1, 1, 1, 0.0f)
		Spike6.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike7 = ModelRenderer(this, 0, 0)
		Spike7.addBox(0.5f, -1.5f, 2.0f, 1, 1, 1, 0.0f)
		Spike7.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike8 = ModelRenderer(this, 0, 0)
		Spike8.addBox(-1.5f, -0.5f, 2.0f, 1, 1, 1, 0.0f)
		Spike8.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike9 = ModelRenderer(this, 0, 0)
		Spike9.addBox(0.0f, 0.5f, 2.0f, 1, 1, 1, 0.0f)
		Spike9.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike10 = ModelRenderer(this, 0, 0)
		Spike10.addBox(-3.0f, 0.0f, 0.5f, 1, 1, 1, 0.0f)
		Spike10.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike11 = ModelRenderer(this, 0, 0)
		Spike11.addBox(-3.0f, -1.5f, -0.5f, 1, 1, 1, 0.0f)
		Spike11.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike12 = ModelRenderer(this, 0, 0)
		Spike12.addBox(-3.0f, 0.5f, -1.5f, 1, 1, 1, 0.0f)
		Spike12.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike13 = ModelRenderer(this, 0, 0)
		Spike13.addBox(0.5f, -1.5f, -3.0f, 1, 1, 1, 0.0f)
		Spike13.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike14 = ModelRenderer(this, 0, 0)
		Spike14.addBox(-1.5f, -1.0f, -3.0f, 1, 1, 1, 0.0f)
		Spike14.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike15 = ModelRenderer(this, 0, 0)
		Spike15.addBox(-0.5f, 0.5f, -3.0f, 1, 1, 1, 0.0f)
		Spike15.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike16 = ModelRenderer(this, 0, 0)
		Spike16.addBox(-1.5f, 2.0f, 0.5f, 1, 1, 1, 0.0f)
		Spike16.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike17 = ModelRenderer(this, 0, 0)
		Spike17.addBox(-1.0f, 2.0f, -1.5f, 1, 1, 1, 0.0f)
		Spike17.setRotationPoint(0.0f, 0.0f, 0.0f)
		Spike18 = ModelRenderer(this, 0, 0)
		Spike18.addBox(0.5f, 2.0f, 0.0f, 1, 1, 1, 0.0f)
		Spike18.setRotationPoint(0.0f, 0.0f, 0.0f)
		
		TentacleBase = ModelRenderer(this, 36, 42)
		TentacleBase.addBox(-2.0f, -2.0f, -2.0f, 4, 4, 4, 0.0f)
		TentacleBase.setRotationPoint(0.0f, 16.0f, 0.0f)
		
		TentacleBase.addChild(Spike6)
		TentacleBase.addChild(Spike10)
		TentacleBase.addChild(Spike11)
		TentacleBase.addChild(Spike3)
		TentacleBase.addChild(Spike7)
		TentacleBase.addChild(Spike14)
		TentacleBase.addChild(Spike4)
		TentacleBase.addChild(Spike8)
		TentacleBase.addChild(Spike18)
		TentacleBase.addChild(Spike2)
		TentacleBase.addChild(Spike15)
		TentacleBase.addChild(Spike1)
		TentacleBase.addChild(Spike16)
		TentacleBase.addChild(Spike5)
		TentacleBase.addChild(Spike17)
		TentacleBase.addChild(Spike12)
		TentacleBase.addChild(Spike9)
		TentacleBase.addChild(Spike13)
	}
	
	override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		render(entity, f5)
	}
	
	fun render(entity: Entity, f5: Float) {
//		Base.render(f5)
//		Bud1.render(f5)
//		Bud2.render(f5)
//		Bud3.render(f5)
//		Bud4.render(f5)
//		LeafTop1.render(f5)
//		LeafTop2.render(f5)
//		LeafTop3.render(f5)
//		LeafTop4.render(f5)
//		LeafCorner1.render(f5)
//		LeafCorner2.render(f5)
//		LeafCorner4.render(f5)
//		LeafCorner3.render(f5)
//		LeafBottom1.render(f5)
//		LeafBottom2.render(f5)
//		LeafBottom3.render(f5)
//		LeafBottom4.render(f5)
		
		glPushMatrix()
		glRotatef(180f, 1f, 0f, 0f)
		glTranslatef(0f, -1f, 0f)
		main.renderAll()
		glPopMatrix()
		
		if (!entity.isEntityAlive) return
		
		val displacer = Random(entity.entityUniqueID.mostSignificantBits)
		val spiker = Random(entity.entityUniqueID.leastSignificantBits)
		
		mc.renderEngine.bindTexture(LibResourceLocations.flowerBudOld)
		
		glPushMatrix()
		glRotatef(45f, 0f, 1f, 0f)
		
		repeat(4) { angled ->
			glRotatef(90f * angled, 0f, 1f, 0f)
			
			TentacleBase.offsetX += 0.25f
			
			repeat(16) {
				TentacleBase.childModels.forEach { spike -> (spike as ModelRenderer).showModel = spiker.nextInt(5) == 0 }
				TentacleBase.offsetX += 0.25f
				TentacleBase.offsetY += (displacer.nextFloat() - 0.5f) * 0.0625f
				TentacleBase.offsetZ += (displacer.nextFloat() - 0.5f) * 0.25f
				TentacleBase.render(f5)
			}
			
			TentacleBase.offsetX = 0f
			TentacleBase.offsetY = 0f
			TentacleBase.offsetZ = 0f
		}
		
		glPopMatrix()
	}
	
	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	fun setRotateAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
		modelRenderer.rotateAngleX = x
		modelRenderer.rotateAngleY = y
		modelRenderer.rotateAngleZ = z
	}
}
