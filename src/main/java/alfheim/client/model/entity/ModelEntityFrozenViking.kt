package alfheim.client.model.entity

import net.minecraft.client.model.*

object ModelEntityFrozenViking: ModelBiped() {
	
	val head: ModelRenderer
	val horn1: ModelRenderer
	val horn2: ModelRenderer
	val horn3: ModelRenderer
	val horn4: ModelRenderer
	val body: ModelRenderer
	val rightArm: ModelRenderer
	val leftArm: ModelRenderer
	val rightLeg: ModelRenderer
	val leftLeg: ModelRenderer
	
	init {
		textureWidth = 64
		textureHeight = 64
		
		head = ModelRenderer(this)
		head.setRotationPoint(0f, 0f, 0f)
		head.cubeList.add(ModelBox(head, 0, 0, -4f, -8f, -4f, 8, 8, 8, 0f))
		head.cubeList.add(ModelBox(head, 32, 0, -4f, -8f, -4f, 8, 8, 8, 0.5f))
		
		horn1 = ModelRenderer(this)
		horn1.setRotationPoint(-5.25f, -7.25f, -0.5f)
		head.addChild(horn1)
		setRotationAngle(horn1, 0f, 0f, 0.829f)
		horn1.cubeList.add(ModelBox(horn1, 24, 0, -3f, -0.75f, -1f, 3, 2, 2, 0f))
		horn2 = ModelRenderer(this)
		horn2.setRotationPoint(5.25f, -7.25f, -0.5f)
		head.addChild(horn2)
		setRotationAngle(horn2, 0f, 0f, -0.829f)
		horn2.cubeList.add(ModelBox(horn2, 24, 0, 0f, -0.75f, -1f, 3, 2, 2, 0f))
		horn3 = ModelRenderer(this)
		horn3.setRotationPoint(4f, -7f, 0f)
		head.addChild(horn3)
		setRotationAngle(horn3, 0f, 0f, -0.2618f)
		horn3.cubeList.add(ModelBox(horn3, 24, 0, 0f, -1f, -2f, 2, 3, 3, 0f))
		horn4 = ModelRenderer(this)
		horn4.setRotationPoint(-4f, -7f, 0f)
		head.addChild(horn4)
		setRotationAngle(horn4, 0f, 0f, 0.2618f)
		horn4.cubeList.add(ModelBox(horn4, 24, 0, -2f, -1f, -2f, 2, 3, 3, 0f))
		
		bipedHead = head
		
		body = ModelRenderer(this)
		body.setRotationPoint(0f, 0f, 0f)
		body.cubeList.add(ModelBox(body, 16, 16, -4f, 0f, -2f, 8, 12, 4, 0f))
		body.cubeList.add(ModelBox(body, 16, 32, -4f, 0f, -2f, 8, 12, 4, 0.25f))
		
		bipedBody = body
		
		rightArm = ModelRenderer(this)
		rightArm.setRotationPoint(-5f, 2f, 0f)
		rightArm.cubeList.add(ModelBox(rightArm, 40, 16, -3f, -2f, -2f, 4, 12, 4, 0f))
		rightArm.cubeList.add(ModelBox(rightArm, 40, 32, -3f, -2f, -2f, 4, 12, 4, 0.25f))
		
		bipedRightArm = rightArm
		
		leftArm = ModelRenderer(this)
		leftArm.setRotationPoint(5f, 2f, 0f)
		leftArm.cubeList.add(ModelBox(leftArm, 32, 48, -1f, -2f, -2f, 4, 12, 4, 0f))
		leftArm.cubeList.add(ModelBox(leftArm, 48, 48, -1f, -2f, -2f, 4, 12, 4, 0.25f))
		
		bipedLeftArm = leftArm
		
		rightLeg = ModelRenderer(this)
		rightLeg.setRotationPoint(-2.1f, 12f, 0f)
		rightLeg.cubeList.add(ModelBox(rightLeg, 0, 16, -1.9f, 0f, -2f, 4, 12, 4, 0f))
		rightLeg.cubeList.add(ModelBox(rightLeg, 0, 32, -1.9f, 0f, -2f, 4, 12, 4, 0.25f))
		
		bipedRightLeg = rightLeg
		
		leftLeg = ModelRenderer(this)
		leftLeg.setRotationPoint(2f, 12f, 0f)
		leftLeg.cubeList.add(ModelBox(leftLeg, 16, 48, -2f, 0f, -2f, 4, 12, 4, 0f))
		leftLeg.cubeList.add(ModelBox(leftLeg, 0, 48, -2f, 0f, -2f, 4, 12, 4, 0.25f))
		
		bipedLeftLeg = leftLeg
		
		bipedCloak.showModel = false
		bipedCloak.cubeList.clear()
		bipedEars.showModel = false
		bipedEars.cubeList.clear()
		bipedHeadwear.showModel = false
		bipedHeadwear.cubeList.clear()
	}
	
	fun setRotationAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
		modelRenderer.rotateAngleX = x
		modelRenderer.rotateAngleY = y
		modelRenderer.rotateAngleZ = z
	}
}