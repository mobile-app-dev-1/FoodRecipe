package ie.setu.foodrecipe.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ie.setu.foodrecipe.R
import ie.setu.foodrecipe.databinding.ActivityFoodRecipeListBinding
import ie.setu.foodrecipe.main.MainApp
import ie.setu.foodrecipe.adapters.FoodRecipeAdapter

class FoodRecipeListActivity : AppCompatActivity() {

    lateinit var app: MainApp
    private lateinit var binding: ActivityFoodRecipeListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodRecipeListBinding.inflate(layoutInflater)

        binding.topAppBar.title = title
        setSupportActionBar(binding.topAppBar)

        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = FoodRecipeAdapter(app.recipes)
    }

    // Override the method to the load the new menu xml
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Handling the menu create button to launch into the other activity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, RecipeActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // The result of the create activity (was it created "Activity.RESULT_OK" or canceled "")
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.
                notifyItemRangeChanged(0,app.recipes.size)
            }
            if(it.resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(binding.root, "Recipe Add Cancelled", Snackbar.LENGTH_LONG).show()
            }
        }
}
