package alfheim.common.lexicon.page

import alfheim.common.crafting.recipe.TunerIncantationIO

class PageTuningIORecipe(unlocalizedName: String, io: TunerIncantationIO): PageTuningRecipe(unlocalizedName, io.tunerIncantation, io.core, io.output)