package ie.setu.foodrecipe.models

data class RecipeModel(var title: String = "", var description: String = "", var ingredients: MutableList<String> = mutableListOf())