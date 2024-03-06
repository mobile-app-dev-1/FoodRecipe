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
        var edit = false
        i("Recipe Activity started..")

        // setting up the adapter
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

                // Add the ingredient to the recipe.ingredients list
                recipe.ingredients.add(newIngredient)
            }

        }

        // Check if activity is in edit (update mode)
        if(intent.hasExtra("foodrecipe_edit")) {
            edit = true
            recipe = intent.extras?.getParcelable("foodrecipe_edit")!!
            binding.recipeTitle.setText(recipe.title)
            binding.recipeDescription.setText(recipe.description)

            // Set up RecyclerView with LinearLayoutManager
            ingredientList.addAll(recipe.ingredients)
            ingredientAdapter.notifyDataSetChanged()  // Notify the adapter of the initial data
            binding.recyclerView.adapter = ingredientAdapter

            // Update Button Text for Saving Recipe
            binding.btnAddRecipe.setText(R.string.button_saveRecipe)
        }

        // new button listner for (updating) not creating a new one
        // Click Listener for adding the recipe
        binding.btnAddRecipe.setOnClickListener() {
            recipe.title = binding.recipeTitle.text.toString().trim()
            recipe.description = binding.recipeDescription.text.toString()

            val newIngredient = binding.recipeIngredient.text.toString().trim()

            // Check if the new ingredient is not empty before adding it
            if (newIngredient.isNotEmpty()) {
                recipe.ingredients.add(newIngredient)
            }

            if (recipe.title.isNotEmpty()) {
                if (edit) {
                    app.recipes.update(recipe.copy())
                    i("update Button Pressed: ${recipe.title}")
                } else {
                    app.recipes.create(recipe.copy())
                    i("add Button Pressed: ${recipe.title}")
                }
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(it, "Please Enter a title", Snackbar.LENGTH_LONG).show()
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