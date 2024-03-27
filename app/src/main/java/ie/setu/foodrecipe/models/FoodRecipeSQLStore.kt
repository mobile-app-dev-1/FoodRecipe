package ie.setu.foodrecipe.models

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import timber.log.Timber.i

// SQLite database constants
private const val DATABASE_NAME = "recipes.db"
private const val TABLE_NAME = "recipes"
private const val COLUMN_ID = "id"
private const val COLUMN_TITLE = "title"
private const val COLUMN_DESCRIPTION = "description"
private const val COLUMN_INGREDIENTS = "ingredients"
private const val COLUMN_CUISINE = "cuisine"
private const val COLUMN_RATINGS = "ratings"
private const val COLUMN_IMAGE = "image"
private const val COLUMN_CREATION_TIMESTAMP = "creation_timestamp"
private const val COLUMN_LAST_EDITED_TIMESTAMP = "last_edited_timestamp"
private const val COLUMN_CREATED_BY_USER = "created_by_user"


class FoodRecipeSQLStore(private val context: Context) : FoodRecipeStore {

    private var database: SQLiteDatabase

    init {
        // Set up local database connection
        database = FoodRecipeDbHelper(context).writableDatabase
    }

    @SuppressLint("Range")
    override suspend fun findAll(): List<RecipeModel> {
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = database.rawQuery(query, null)

        val recipes = ArrayList<RecipeModel>()

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndex(COLUMN_ID))
                val title = it.getString(it.getColumnIndex(COLUMN_TITLE))
                val description = it.getString(it.getColumnIndex(COLUMN_DESCRIPTION))
                val ingredientsString = it.getString(it.getColumnIndex(COLUMN_INGREDIENTS))
                val ingredients = ingredientsString.split(",")
                val cuisine = it.getString(it.getColumnIndex(COLUMN_CUISINE))
                val ratingsString = it.getString(it.getColumnIndex(COLUMN_RATINGS))
                val ratings = ratingsString.split(",").map { rating -> rating.toFloat() }
                val imageUri = Uri.parse(it.getString(it.getColumnIndex(COLUMN_IMAGE)))
                val creationTimestamp = it.getLong(it.getColumnIndex(COLUMN_CREATION_TIMESTAMP))
                val lastEditedTimestamp = it.getLong(it.getColumnIndex(COLUMN_LAST_EDITED_TIMESTAMP))
                val createdByUser = it.getString(it.getColumnIndex(COLUMN_CREATED_BY_USER))

                recipes.add(
                    RecipeModel(
                        id = id,
                        title = title,
                        description = description,
                        ingredients = ingredients.toMutableList(),
                        cuisine = cuisine,
                        ratings = ratings.toMutableList(),
                        image = imageUri,
                        creationTimestamp = creationTimestamp,
                        lastEditedTimestamp = lastEditedTimestamp,
                        createdByUser = createdByUser
                    )
                )
            }
        }

        i("SQL findAll() called -> $recipes")

        return recipes
    }

    @SuppressLint("Range")
    override suspend fun findAllByUserID(uid: String): List<RecipeModel> {
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_CREATED_BY_USER = ?"
        val cursor = database.rawQuery(query, arrayOf(uid))

        val recipes = ArrayList<RecipeModel>()

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndex(COLUMN_ID))
                val title = it.getString(it.getColumnIndex(COLUMN_TITLE))
                val description = it.getString(it.getColumnIndex(COLUMN_DESCRIPTION))
                val ingredientsString = it.getString(it.getColumnIndex(COLUMN_INGREDIENTS))
                val ingredients = ingredientsString.split(",")
                val cuisine = it.getString(it.getColumnIndex(COLUMN_CUISINE))
                val ratingsString = it.getString(it.getColumnIndex(COLUMN_RATINGS))
                val ratings = ratingsString.split(",").map { rating -> rating.toFloat() }
                val imageUri = Uri.parse(it.getString(it.getColumnIndex(COLUMN_IMAGE)))
                val creationTimestamp = it.getLong(it.getColumnIndex(COLUMN_CREATION_TIMESTAMP))
                val lastEditedTimestamp = it.getLong(it.getColumnIndex(COLUMN_LAST_EDITED_TIMESTAMP))
                val createdByUser = it.getString(it.getColumnIndex(COLUMN_CREATED_BY_USER))

                recipes.add(
                    RecipeModel(
                        id = id,
                        title = title,
                        description = description,
                        ingredients = ingredients.toMutableList(),
                        cuisine = cuisine,
                        ratings = ratings.toMutableList(),
                        image = imageUri,
                        creationTimestamp = creationTimestamp,
                        lastEditedTimestamp = lastEditedTimestamp,
                        createdByUser = createdByUser
                    )
                )
            }
        }

        i("SQL findAllByUserID() called -> $recipes")

        return recipes
    }

    override fun create(recipe: RecipeModel) {
        val values = ContentValues()

        values.put(COLUMN_ID, recipe.id)
        values.put(COLUMN_TITLE, recipe.title)
        values.put(COLUMN_DESCRIPTION, recipe.description)
        values.put(COLUMN_INGREDIENTS, recipe.ingredients.joinToString(","))
        values.put(COLUMN_CUISINE, recipe.cuisine)
        values.put(COLUMN_RATINGS, recipe.ratings.joinToString(","))
        values.put(COLUMN_IMAGE, recipe.image.toString())
        values.put(COLUMN_CREATION_TIMESTAMP, recipe.creationTimestamp)
        values.put(COLUMN_LAST_EDITED_TIMESTAMP, recipe.lastEditedTimestamp ?: 0)
        values.put(COLUMN_CREATED_BY_USER, recipe.createdByUser)

        database.insert(TABLE_NAME, null, values)
    }

    override suspend fun update(recipe: RecipeModel) {
        val values = ContentValues()


        values.put(COLUMN_TITLE, recipe.title)
        values.put(COLUMN_DESCRIPTION, recipe.description)
        values.put(COLUMN_INGREDIENTS, recipe.ingredients.joinToString(","))
        values.put(COLUMN_CUISINE, recipe.cuisine)
        values.put(COLUMN_RATINGS, recipe.ratings.joinToString(","))
        values.put(COLUMN_IMAGE, recipe.image.toString())
        values.put(COLUMN_LAST_EDITED_TIMESTAMP, System.currentTimeMillis())
        values.put(COLUMN_CREATED_BY_USER, recipe.createdByUser)


        database.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(recipe.id))

        i("SQL create called")
    }

    override suspend fun deleteById(id: String) {
        database.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id))

        i("SQL deleteById() called - id -> $id")
    }

    private class FoodRecipeDbHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

        private val createTableSQL =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_TITLE TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_IMAGE TEXT, " +
                    "$COLUMN_CUISINE TEXT, $COLUMN_RATINGS TEXT, $COLUMN_INGREDIENTS TEXT, " +
                    "$COLUMN_CREATION_TIMESTAMP INTEGER, $COLUMN_LAST_EDITED_TIMESTAMP INTEGER, " +
                    "$COLUMN_CREATED_BY_USER TEXT)"

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(createTableSQL)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // Handle database schema upgrades if needed
        }
    }
}