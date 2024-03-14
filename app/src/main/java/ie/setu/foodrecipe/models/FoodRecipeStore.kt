package ie.setu.foodrecipe.models

interface FoodRecipeStore {
    fun findAll(): List<RecipeModel>
    fun create(recipe: RecipeModel)
    fun update(recipe: RecipeModel)
    fun deleteById(id: Long)
}