package alfheim.common.crafting.recipe

import alfheim.api.AlfheimAPI
import alfheim.api.AlfheimAPI.set
import alfheim.api.crafting.recipe.TunerIncantation
import alfheim.common.entity.EntityElementalSlime
import alfheim.common.item.material.ItemElvenResource

class ElementalSlimeGrowthTune(application: (EntityElementalSlime) -> Boolean): TunerIncantation<EntityElementalSlime>(EntityElementalSlime::class.java, "jaki o kama suli", Array(4) { ItemElvenResource.ballForElement(null) }, application) {
	
	init {
		AlfheimAPI.tunerIncantations[incantation.lowercase()] = this
	}
	
	override fun getInputs(target: EntityElementalSlime) = Array(4) { ItemElvenResource.ballForElement(target.elements.first()) }.toList()
}
