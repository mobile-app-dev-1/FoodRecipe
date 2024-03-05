package ie.setu.foodrecipe.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import ie.setu.foodrecipe.databinding.ActivityRecipeBinding
import ie.setu.foodrecipe.main.MainApp
import ie.setu.foodrecipe.models.RecipeModel

import timber.log.Timber.i


class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeBinding
    var recipe = RecipeModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        app = application as MainApp
        i("Recipe Activity started..")
        binding.btnAdd.setOnClickListener() {
            recipe.title = binding.recipeTitle.text.toString()
            recipe.description = binding.recipeDescription.text.toString()
            recipe.ingredients.add(binding.recipeIngredient.text.toString())
            if (recipe.title.isNotEmpty()) {
                app.recipes.add(recipe.copy())
                i("add Button Pressed: ${recipe.title}")
                for( i in app.recipes.indices)
                { i("Recipe[$i]:$(this.app!!.recipes[i])")}
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}