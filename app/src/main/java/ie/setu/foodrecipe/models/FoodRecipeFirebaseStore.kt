package ie.setu.foodrecipe.models

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import timber.log.Timber.i
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FoodRecipeFirebaseStore : FoodRecipeStore {
    private val db = Firebase.firestore
    private val recipeDocuments = db.collection("recipes")

    // This function needs to be a suspended one, as the get() is async and not all
    // results are retrieved by the time the function ends and the screen is drawn.
    suspend override fun findAll(): List<RecipeModel> = suspendCoroutine { continuation ->
        val recipesList = mutableListOf<RecipeModel>()

        recipeDocuments.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    recipesList.add(
                        RecipeModel(
                            id = ((document.data["id"] as? Long)?.toLong() ?: 0L),
                            title = document.data["title"] as? String ?: "",
                            description = document.data["description"] as? String ?: "",
                            ingredients = (document.data["ingredients"] as? List<String>)?.toMutableList() ?: mutableListOf(),
                            cuisine = document.data["cuisine"] as? String ?: "",
                            ratings = (document.data["ratings"] as? List<Float>)?.toMutableList() ?: mutableListOf(),
                            image = Uri.parse(document.data["image"] as? String ?: ""), // Parse the Uri as a String
                            creationTimestamp = (document.data["creationTimestamp"] as? Long) ?: 0,
                            lastEditedTimestamp = (document.data["lastEditedTimestamp"] as? Long)
                        )
                    )
                }
                continuation.resume(recipesList)
            }
            .addOnFailureListener { exception ->
                i("Error getting documents $exception")
                continuation.resumeWithException(exception)
            }
    }

/*    override fun findById(id: Long): RecipeModel? {
        //TODO not done yet
        return RecipeModel()
    }*/

    // Creating a recipe in the Firebase store
    override fun create(recipe: RecipeModel) {

        val data = mutableMapOf<String, Any>().apply {
            // Add fields directly into the map
            put("id", UUID.randomUUID())
            put("title", recipe.title)
            put("description", recipe.description)
            put("ingredients", recipe.ingredients.toMutableList())
            put("cuisine", recipe.cuisine)
            put("ratings", recipe.ratings.toMutableList())
            put("image", recipe.image.toString()) // Store the Uri as a String
            put("creationTimestamp", recipe.creationTimestamp)
            recipe.lastEditedTimestamp?.let { put("lastEditedTimestamp", it) }
        }

        recipeDocuments
            .add(data)
            .addOnSuccessListener { documentReference ->
                i("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                i("Error adding document: $e")
            }
    }

    // updating a recipe in the Firebase store
    override suspend fun update(recipe: RecipeModel) {
        i("START OF UPDATE ID ${recipe}")
        val recipeId = recipe.id.toString()

        // Query Firestore to find the document with the specified id
        val documentSnapshot = recipeDocuments.document(recipeId).get().await()

        if (!documentSnapshot.exists()) {
            i("Document with id $recipeId not found")
            return
        }

        // Create a map of data to update
        val data = mutableMapOf<String, Any>().apply {
            put("title", recipe.title)
            put("description", recipe.description)
            put("ingredients", recipe.ingredients.toMutableList())
            put("cuisine", recipe.cuisine)
            put("ratings", recipe.ratings.toMutableList())
            put("image", recipe.image.toString())
            put("creationTimestamp", recipe.creationTimestamp)
            recipe.lastEditedTimestamp?.let { put("lastEditedTimestamp", it) }
        }

        // Update the document using merge option to update only the specified fields
        recipeDocuments.document(recipeId).set(data, SetOptions.merge()).await()
        i("DocumentSnapshot successfully updated!")
    }



    override fun deleteById(id: Long) {
        val recipeId = id.toString()
        val recipeRef = recipeDocuments.document(recipeId)

        recipeRef
            .delete()
            .addOnSuccessListener {
                i("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                i("Error deleting document: $e")
                throw e
            }
    }


    /*override fun delete(placemark: PlacemarkModel) {
        //  val placemarkReference = databaseReference.child(placemark.id.toString())
        //  placemarkReference.removeValue()
    }*/
}