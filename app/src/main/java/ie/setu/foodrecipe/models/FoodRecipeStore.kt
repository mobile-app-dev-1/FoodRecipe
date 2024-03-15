package ie.setu.foodrecipe.models

interface FoodRecipeStore {
    suspend fun findAll(): List<RecipeModel>
    fun create(recipe: RecipeModel)
    suspend fun update(recipe: RecipeModel)
    fun deleteById(id: Long)
}