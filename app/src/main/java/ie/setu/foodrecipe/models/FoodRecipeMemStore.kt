package ie.setu.foodrecipe.models

import timber.log.Timber.i

class FoodRecipeMemStore : FoodRecipeStore{
    val recipes = ArrayList<RecipeModel>()

    override fun findAll(): List<RecipeModel> {
        return recipes
        logAll()
    }

    override fun create(recipe: RecipeModel) {
        recipes.add(recipe)
    }

    fun logAll() {
        recipes.forEach{ i("${it}") }
    }
}