package ie.setu.mobileassignment.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import ie.setu.mobileassignment.databinding.ActivityRecipeBinding
import ie.setu.mobileassignment.models.RecipeModel
import timber.log.Timber
import timber.log.Timber.i


class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeBinding
    var recipe = RecipeModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())

        i("Recipe Activity started..")

        binding.btnAdd.setOnClickListener() {
            recipe.title = binding.recipeTitle.text.toString()
            if (recipe.title.isNotEmpty()) {
                i("add Button Pressed: ${recipe.title}")
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}