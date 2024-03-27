package ie.setu.foodrecipe.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeModel(var id: String = "",
                       var title: String = "",
                       var description: String = "",
                       var ingredients: MutableList<String> = mutableListOf(),
                       var cuisine: String = "",
                       var ratings: MutableList<Float> = mutableListOf(),
                       var image: Uri = Uri.EMPTY,
                       var creationTimestamp: Long = System.currentTimeMillis(),
                       var lastEditedTimestamp: Long? = null,
                       var createdByUser: String = "") : Parcelable