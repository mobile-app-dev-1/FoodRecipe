package ie.setu.mobileassignment.models

data class RecipeModel(var title: String = "", var description: String = "", var ingredients: MutableList<String> = mutableListOf())