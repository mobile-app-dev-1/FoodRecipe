package ie.setu.foodrecipe

import android.net.Uri
import ie.setu.foodrecipe.models.FoodRecipeMemStore
import ie.setu.foodrecipe.models.RecipeModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.runBlocking


class FoodRecipeMemStoreTest {

    private lateinit var memStore: FoodRecipeMemStore

    @Before
    fun setUp() {
        memStore = FoodRecipeMemStore()
    }

    @Test
    fun testCreate() = runBlocking {
        val recipe = RecipeModel(
            id = "1",
            title = "Test Recipe",
            description = "Test Description",
            ingredients = mutableListOf("Ingredient 1", "Ingredient 2"),
            cuisine = "Test Cuisine",
            ratings = mutableListOf(4.5f, 5.0f),
            image = Uri.parse("content://test"),
            createdByUser = "user1"
        )
        memStore.create(recipe)
        assertEquals(1, memStore.recipes.size)
    }

    @Test
    fun testUpdate() = runBlocking {
        val recipe = RecipeModel(
            id = "1",
            title = "Test Recipe",
            description = "Test Description",
            ingredients = mutableListOf("Ingredient 1", "Ingredient 2"),
            cuisine = "Test Cuisine",
            ratings = mutableListOf(4.5f, 5.0f),
            image = Uri.parse("content://test"),
            createdByUser = "user1"
        )
        memStore.create(recipe)

        val updatedRecipe = RecipeModel(
            id = "1",
            title = "Updated Recipe",
            description = "Updated Description",
            ingredients = mutableListOf("New Ingredient 1", "New Ingredient 2"),
            cuisine = "Updated Cuisine",
            ratings = mutableListOf(3.5f, 4.0f),
            image = Uri.parse("content://updated"),
            createdByUser = "user1"
        )
        memStore.update(updatedRecipe)

        val storedRecipe = memStore.recipes.firstOrNull { it.id == "1" }
        assertEquals("Updated Recipe", storedRecipe?.title)
        assertEquals("Updated Description", storedRecipe?.description)
        assertEquals("Updated Cuisine", storedRecipe?.cuisine)
        assertEquals(2, storedRecipe?.ingredients?.size)
        assertEquals("New Ingredient 1", storedRecipe?.ingredients?.get(0))
        assertEquals("New Ingredient 2", storedRecipe?.ingredients?.get(1))
        assertEquals(2, storedRecipe?.ratings?.size)
        assertEquals(3.5f, storedRecipe?.ratings?.get(0))
        assertEquals(4.0f, storedRecipe?.ratings?.get(1))
        assertEquals(Uri.parse("content://updated"), storedRecipe?.image)
    }

    @Test
    fun testFindAll() = runBlocking {
        val recipes = listOf(
            RecipeModel(
                id = "1",
                title = "Recipe 1",
                description = "Description 1",
                ingredients = mutableListOf("Ingredient 1", "Ingredient 2"),
                cuisine = "Cuisine 1",
                ratings = mutableListOf(4.0f),
                image = Uri.parse("content://recipe1"),
                createdByUser = "user1"
            ),
            RecipeModel(
                id = "2",
                title = "Recipe 2",
                description = "Description 2",
                ingredients = mutableListOf("Ingredient 3", "Ingredient 4"),
                cuisine = "Cuisine 2",
                ratings = mutableListOf(4.5f),
                image = Uri.parse("content://recipe2"),
                createdByUser = "user2"
            )
        )
        recipes.forEach { memStore.create(it) }

        val storedRecipes = memStore.findAll()
        assertEquals(2, storedRecipes.size)
        assertTrue(storedRecipes.containsAll(recipes))
    }

    @Test
    fun testFindAllByUserID() = runBlocking {
        val uid = "user1"
        val recipes = listOf(
            RecipeModel(
                id = "1",
                title = "Recipe 1",
                description = "Description 1",
                ingredients = mutableListOf("Ingredient 1", "Ingredient 2"),
                cuisine = "Cuisine 1",
                ratings = mutableListOf(4.0f),
                image = Uri.parse("content://recipe1"),
                createdByUser = uid
            ),
            RecipeModel(
                id = "2",
                title = "Recipe 2",
                description = "Description 2",
                ingredients = mutableListOf("Ingredient 3", "Ingredient 4"),
                cuisine = "Cuisine 2",
                ratings = mutableListOf(4.5f),
                image = Uri.parse("content://recipe2"),
                createdByUser = "user2"
            )
        )
        recipes.forEach { memStore.create(it) }

        val user1Recipes = memStore.findAllByUserID(uid)
        assertEquals(1, user1Recipes.size)
        assertEquals("Recipe 1", user1Recipes.firstOrNull()?.title)
    }

    @Test
    fun testDeleteById() = runBlocking {
        val recipes = listOf(
            RecipeModel(
                id = "1",
                title = "Recipe 1",
                description = "Description 1",
                ingredients = mutableListOf("Ingredient 1", "Ingredient 2"),
                cuisine = "Cuisine 1",
                ratings = mutableListOf(4.0f),
                image = Uri.parse("content://recipe1"),
                createdByUser = "user1"
            ),
            RecipeModel(
                id = "2",
                title = "Recipe 2",
                description = "Description 2",
                ingredients = mutableListOf("Ingredient 3", "Ingredient 4"),
                cuisine = "Cuisine 2",
                ratings = mutableListOf(4.5f),
                image = Uri.parse("content://recipe2"),
                createdByUser = "user2"
            )
        )
        recipes.forEach { memStore.create(it) }

        memStore.deleteById("1")
        assertEquals(1, memStore.recipes.size)
        assertEquals("Recipe 2", memStore.recipes.firstOrNull()?.title)
    }
}

