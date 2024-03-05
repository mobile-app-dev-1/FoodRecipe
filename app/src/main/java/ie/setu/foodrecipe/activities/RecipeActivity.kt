package ie.setu.foodrecipe.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ie.setu.foodrecipe.R
import ie.setu.foodrecipe.adapters.IngredientAdapter
import ie.setu.foodrecipe.databinding.ActivityRecipeBinding
import ie.setu.foodrecipe.main.MainApp
import ie.setu.foodrecipe.models.RecipeModel

import timber.log.Timber.i


class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeBinding
    var recipe = RecipeModel()
    lateinit var app: MainApp

    private lateinit var ingredientAdapter: IngredientAdapter
    private val ingredientList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.topAppBar.title = title
        setSupportActionBar(binding.topAppBar)

        app = application as MainApp
        i("Recipe Activity started..")


        // setting u[ the adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false).apply {
            stackFromEnd = true
        }
        ingredientAdapter = IngredientAdapter(ingredientList)
        binding.recyclerView.adapter = ingredientAdapter

        // Click listener for adding a ingredient
        binding.ingredientAdd.setOnClickListener {
            val newIngredient = binding.recipeIngredient.text.toString()
            if (newIngredient.isNotEmpty()) {
                ingredientList.add(newIngredient)
                ingredientAdapter.notifyDataSetChanged()

                i("IngredientList" + ingredientList.toString())
                // Clear the EditText after adding the ingredient
                binding.recipeIngredient.text.clear()
            }
        }


        // Click Listener for adding the recipe
        binding.btnAddRecipe.setOnClickListener() {
            recipe.title = binding.recipeTitle.text.toString()
            recipe.description = binding.recipeDescription.text.toString()
            recipe.ingredients.add(binding.recipeIngredient.text.toString())
            if (recipe.title.isNotEmpty()) {
                app.recipes.add(recipe.copy())
                i("add Button Pressed: ${recipe.title}")
                for( i in app.recipes.indices)
                { i("Recipe[$i]:$(this.app!!.recipes[i])")}
                setResult(RESULT_OK)
                finish()
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    // inflate the new menu for this activity
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_foodrecipe, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Handling the cancel button in the menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}