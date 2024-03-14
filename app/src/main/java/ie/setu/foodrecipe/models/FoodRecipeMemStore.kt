package ie.setu.foodrecipe.models

import timber.log.Timber.i

/**
 * A singleton object to generate unique IDs for recipe objects.
 */
var lastId = 0L

/**
 * Retrieves the next available ID and increments the [lastId] by 1.
 *
 * @return The next available ID.
 */
internal fun getId() = lastId++

/**
 * An in-memory implementation of [FoodRecipeStore] interface to store recipe data.
 */
class FoodRecipeMemStore : FoodRecipeStore {
    /**
     * A list to store recipe objects.
     */
    val recipes = ArrayList<RecipeModel>()

    /**
     * Retrieves all recipes stored in this memory store.
     *
     * @return A list of all recipes.
     */
    override fun findAll(): List<RecipeModel> {
        return recipes
        logAll()
    }

    /**
     * Adds a new recipe to the memory store.
     *
     * @param recipe The recipe to be added.
     */
    override fun create(recipe: RecipeModel) {
        recipe.id = getId()
        recipes.add(recipe)
    }

    /**
     * Updates an existing recipe in the memory store.
     *
     * @param recipe The updated recipe.
     */
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

    /**
     * Deletes a recipe from the memory store by its ID.
     *
     * @param id The ID of the recipe to be deleted.
     */
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

    /**
     * Logs all recipes stored in the memory store.
     */
    fun logAll() {
        recipes.forEach { i("${it}") }
    }
}
