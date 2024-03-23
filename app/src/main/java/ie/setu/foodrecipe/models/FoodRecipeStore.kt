package ie.setu.foodrecipe.models

interface FoodRecipeStore {
    suspend fun findAll(): List<RecipeModel>
    suspend fun findAllByUserID(uid: String): List<RecipeModel>
    fun create(recipe: RecipeModel)
    suspend fun update(recipe: RecipeModel)
    suspend fun deleteById(id: String)
}