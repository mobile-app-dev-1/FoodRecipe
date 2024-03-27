package ie.setu.foodrecipe.models

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import ie.setu.foodrecipe.helpers.exists
import ie.setu.foodrecipe.helpers.read
import ie.setu.foodrecipe.helpers.write
import timber.log.Timber.i
import java.util.UUID
import java.util.*

const val JSON_FILE = "recipes.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<RecipeModel>>() {}.type

class FoodRecipeJSONStore(private val context: Context) : FoodRecipeStore {

    var recipes = mutableListOf<RecipeModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override suspend fun findAll(): MutableList<RecipeModel> {
        logAll()
        return recipes
    }

    override suspend fun findAllByUserID(uid: String): List<RecipeModel> {
        return recipes.filter { it.createdByUser == uid }
    }

    override fun create(recipe: RecipeModel) {
        recipe.id = UUID.randomUUID().toString()
        recipes.add(recipe)
        serialize()
    }

    override suspend fun update(recipe: RecipeModel) {
        val index = recipes.indexOfFirst { it.id == recipe.id }
        if (index != -1) {
            recipes[index] = recipe
            serialize()
        }
    }

    override suspend fun deleteById(id: String) {
        recipes.removeIf { it.id == id }
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(recipes, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        recipes = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        recipes.forEach { i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>, JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}