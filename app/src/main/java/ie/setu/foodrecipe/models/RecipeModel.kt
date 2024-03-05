package ie.setu.foodrecipe.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeModel(var id: Long = 0,
                       var title: String = "",
                       var description: String = "",
                       var ingredients: MutableList<String> = mutableListOf()) : Parcelable