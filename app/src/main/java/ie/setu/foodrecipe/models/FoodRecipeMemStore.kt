package ie.setu.foodrecipe.models

import timber.log.Timber.i

var lastId = 0L
internal fun getId() = lastId++

class FoodRecipeMemStore : FoodRecipeStore{
    val recipes = ArrayList<RecipeModel>()

    override fun findAll(): List<RecipeModel> {
        return recipes
        logAll()
    }

    override fun create(recipe: RecipeModel) {
        recipe.id = getId()
        recipes.add(recipe)
    }

    override fun update(recipe: RecipeModel) {
        val foundRecipe: RecipeModel? = recipes.find { p -> p.id == recipe.id }
        if (foundRecipe != null) {
            foundRecipe.title = recipe.title
            foundRecipe.description = recipe.description
            foundRecipe.image = recipe.image
            foundRecipe.cuisine = recipe.cuisine
            foundRecipe.ratings = recipe.ratings
            foundRecipe.ingredients = recipe.ingredients
            logAll()
        }
    }

    override fun deleteById(id: Long) {
        val iterator = recipes.iterator()
        while (iterator.hasNext()) {
            val recipe = iterator.next()
            if (recipe.id == id) {
                iterator.remove()
                return
            }
        }
    }

    fun logAll() {
        recipes.forEach{ i("${it}") }
    }
}