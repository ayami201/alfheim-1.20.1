package alfheim.client.model.entity

import alexsocol.asjlib.*
import alfheim.api.lib.*
import alfheim.common.entity.*
import net.minecraft.client.model.*
import net.minecraft.entity.*
import net.minecraft.util.*
import org.lwjgl.opengl.GL11.*
import java.util.*
import kotlin.math.*

/**
 * ModelEntityVenusHumanTrap - AlexSocol
 * Created using Tabula 4.1.1
 */
object ModelEntityVenusHumanTrap: ModelBase() {
	
	var JawTop: ModelRenderer
	var JawBottom: ModelRenderer
	var JawBottomDecor: ModelRenderer
	
	var LegRoot: ModelRenderer
	var StemTop: ModelRenderer
	var StemMid: ModelRenderer
	var StemBottom: ModelRenderer
	var LeafFront: ModelRenderer
	var LeafRight: ModelRenderer
	var LeafLeft: ModelRenderer
	var LeafBack: ModelRenderer
	var LeafFrontRight: ModelRenderer
	var LeafFrontLeft: ModelRenderer
	var LeafBackRight: ModelRenderer
	var LeafBackLeft: ModelRenderer
	
	init {
		textureWidth = 64
		textureHeight = 64
		
		JawTop = ModelRenderer(this, 0, 0)
		JawTop.setRotationPoint(0.0f, 4.0f, 5.0f)
		JawTop.addBox(-6.0f, -4.0f, -10.0f, 12, 6, 10, 0.0f)
		setRotateAngle(JawTop, -0.08726646f, 0.0f, 0.0f)
		JawBottom = ModelRenderer(this, 0, 16)
		JawBottom.setRotationPoint(0.0f, 4.0f, 5.0f)
		JawBottom.addBox(-6.0f, -2.0f, -10.0f, 12, 6, 10, 0.0f)
		setRotateAngle(JawBottom, 0.17453292f, 0.0f, 0.0f)
		
		JawBottomDecor = ModelRenderer(this, 0, 32)
		JawBottomDecor.setRotationPoint(0.0f, 0.0f, 0.0f)
		JawBottomDecor.addBox(-6.0f, 2.0f, -10.0f, 12, 6, 10, 0.0f)
		JawBottom.addChild(JawBottomDecor)
		
		LegRoot = ModelRenderer(this, 0, 0)
		LegRoot.setRotationPoint(0.0f, 8.5f, 0.0f)
		LegRoot.addBox(0.0f, 0.0f, 0.0f, 0, 0, 0, 0.0f)
		
		StemTop = ModelRenderer(this, 44, 0)
		StemTop.setRotationPoint(0.0f, 0.0f, 0.0f)
		StemTop.addBox(-1.5f, 0.0f, -1.5f, 3, 8, 3, 0.0f)
		setRotateAngle(StemTop, 0.3926991f, 0.0f, 0.0f)
		StemMid = ModelRenderer(this, 44, 11)
		StemMid.setRotationPoint(0.0f, 0.0f, 0.0f)
		StemMid.addBox(-2.0f, 4.6f, 3.75f, 4, 8, 3, 0.0f)
		setRotateAngle(StemMid, -0.3926991f, 0.0f, 0.0f)
		StemBottom = ModelRenderer(this, 44, 22)
		StemBottom.setRotationPoint(0.0f, 0.0f, 0.0f)
		StemBottom.addBox(-2.5f, 12.0f, -1.5f, 5, 4, 4, 0.0f)
		
		LeafBack = ModelRenderer(this, 40, 54)
		LeafBack.setRotationPoint(0.0f, 0.0f, 0.0f)
		LeafBack.addBox(-3.0f, 15.0f, -5.5f, 6, 0, 8, 0.0f)
		setRotateAngle(LeafBack, -0.2617994f, 3.1415927f, 0.0f)
		LeafLeft = ModelRenderer(this, 40, 46)
		LeafLeft.setRotationPoint(0.0f, 0.0f, 0.0f)
		LeafLeft.addBox(-3.0f, 15.0f, -5.5f, 6, 0, 8, 0.0f)
		setRotateAngle(LeafLeft, -0.2617994f, -1.5707964f, 0.0f)
		LeafFront = ModelRenderer(this, 40, 30)
		LeafFront.setRotationPoint(0.0f, 0.0f, 0.0f)
		LeafFront.addBox(-3.0f, 15.0f, -5.5f, 6, 0, 8, 0.0f)
		setRotateAngle(LeafFront, -0.2617994f, 0.0f, 0.0f)
		LeafRight = ModelRenderer(this, 40, 38)
		LeafRight.setRotationPoint(0.0f, 0.0f, 0.0f)
		LeafRight.addBox(-3.0f, 15.0f, -5.5f, 6, 0, 8, 0.0f)
		setRotateAngle(LeafRight, -0.2617994f, 1.5707964f, 0.0f)
		
		LeafFrontRight = ModelRenderer(this, -8, 48)
		LeafFrontRight.setRotationPoint(0.0f, 0.0f, 0.0f)
		LeafFrontRight.addBox(-3.0f, 14.5f, -5.5f, 6, 0, 8, 0.0f)
		setRotateAngle(LeafFrontRight, -0.2617994f, 0.7853982f, 0.0f)
		LeafFrontLeft = ModelRenderer(this, 4, 48)
		LeafFrontLeft.setRotationPoint(0.0f, 0.0f, 0.0f)
		LeafFrontLeft.addBox(-3.0f, 14.5f, -5.5f, 6, 0, 8, 0.0f)
		setRotateAngle(LeafFrontLeft, -0.2617994f, -0.7853982f, 0.0f)
		LeafBackRight = ModelRenderer(this, 16, 48)
		LeafBackRight.setRotationPoint(0.0f, 0.0f, 0.0f)
		LeafBackRight.addBox(-3.0f, 14.5f, -5.5f, 6, 0, 8, 0.0f)
		setRotateAngle(LeafBackRight, -0.2617994f, 2.3561945f, 0.0f)
		LeafBackLeft = ModelRenderer(this, 28, 48)
		LeafBackLeft.setRotationPoint(0.0f, 0.0f, 0.0f)
		LeafBackLeft.addBox(-3.0f, 14.5f, -5.5f, 6, 0, 8, 0.0f)
		setRotateAngle(LeafBackLeft, -0.2617994f, -2.3561945f, 0.0f)
		
		LegRoot.addChild(StemTop)
		LegRoot.addChild(StemMid)
		LegRoot.addChild(StemBottom)
		LegRoot.addChild(LeafFront)
		LegRoot.addChild(LeafRight)
		LegRoot.addChild(LeafLeft)
		LegRoot.addChild(LeafBack)
		LegRoot.addChild(LeafFrontRight)
		LegRoot.addChild(LeafFrontLeft)
		LegRoot.addChild(LeafBackRight)
		LegRoot.addChild(LeafBackLeft)
	}
	
	override fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, entity: Entity?) {
		LegRoot.rotateAngleX = MathHelper.cos(f * 0.6662f) * 0.25f * f1
		
		JawTop.rotateAngleX = -0.08726646f - (MathHelper.cos(f2 * 0.09f) * 0.05f + 0.05f)
		JawBottom.rotateAngleX = 0.17453292f + (MathHelper.cos(f2 * 0.09f) * 0.05f + 0.05f)
		
		entity as EntityVenusHumanTrap
		if (!entity.hasTarget) return
		
		JawTop.rotateAngleX -= (0.17453292 * (1.0 + cos(Math.PI * entity.animationTimerBite / 5.0))).F
	}
	
	override fun render(entity: Entity?, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity)
		render(f5)
		
		if (entity !is EntityVenusHumanTrap) return
		if (entity.tongueTicks <= 0) return
		
		mc.renderEngine.bindTexture(LibResourceLocations.flowerBudOld)
		
		val displacer = Random(entity.entityUniqueID.mostSignificantBits)
		val spiker = Random(entity.entityUniqueID.leastSignificantBits)
		
		val rpYOld = ModelEntityFlowerBud.TentacleBase.rotationPointY
		ModelEntityFlowerBud.TentacleBase.rotationPointY = 0f
		
		glPushMatrix()
		glTranslatef(0f, 0.25f, 0f)
		glRotatef(entity.rotationPitch, 1f, 0f, 0f)
		
		repeat(entity.tongueSegments) {
			ModelEntityFlowerBud.TentacleBase.childModels.forEach { spike -> (spike as ModelRenderer).showModel = spiker.nextInt(5) == 0 }
			ModelEntityFlowerBud.TentacleBase.offsetX += (displacer.nextFloat() - 0.5f) * 0.25f
			ModelEntityFlowerBud.TentacleBase.offsetY += (displacer.nextFloat() - 0.5f) * 0.0625f
			ModelEntityFlowerBud.TentacleBase.render(f5)
			ModelEntityFlowerBud.TentacleBase.offsetZ -= 0.25f
		}
		glPopMatrix()
		
		ModelEntityFlowerBud.TentacleBase.offsetX = 0f
		ModelEntityFlowerBud.TentacleBase.offsetY = 0f
		ModelEntityFlowerBud.TentacleBase.offsetZ = 0f
		ModelEntityFlowerBud.TentacleBase.rotationPointY = rpYOld
	}
	
	fun render(f5: Float) {
		JawTop.render(f5)
		JawBottom.render(f5)
		LegRoot.render(f5)
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
