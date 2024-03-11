package ie.setu.foodrecipe.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import ie.setu.foodrecipe.R
import ie.setu.foodrecipe.databinding.ActivityFoodRecipeListBinding
import ie.setu.foodrecipe.main.MainApp
import ie.setu.foodrecipe.adapters.FoodRecipeAdapter
import ie.setu.foodrecipe.adapters.FoodRecipeListener
import ie.setu.foodrecipe.models.RecipeModel

class FoodRecipeListActivity : AppCompatActivity(), FoodRecipeListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityFoodRecipeListBinding

    // Nav Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // reference to the main application (parent of app)
        app = application as MainApp

        // Initialize DrawerLayout and NavigationView
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        // toggle the Actionbar menu (ham burger icon)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.topAppBar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.topAppBar.title = title
        setSupportActionBar(binding.topAppBar)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = FoodRecipeAdapter(app.recipes.findAll(), this)

        // Set up NavigationView (drawer) item click listener
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dark_mode -> {
                    val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                    val isNightModeEnabled = currentNightMode == Configuration.UI_MODE_NIGHT_YES

                    if (isNightModeEnabled) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    // Recreate the activity to apply the new theme
                    recreate()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    Toast.makeText(this, "Dark Mode Toggle", Toast.LENGTH_SHORT).show()
                }
            }
            // return true, The event has been handled and no further action is needed
            true
        }
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
            android.R.id.home -> {
                // Handle the Navigation Drawer toggle icon
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
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
                notifyItemRangeChanged(0,app.recipes.findAll().size)
            }
            if(it.resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(binding.root, "Recipe Add Cancelled", Snackbar.LENGTH_LONG).show()
            }
        }

    override fun onFoodRecipeClick(recipe: RecipeModel) {
        val launcherIntent = Intent(this, RecipeActivity::class.java)
        launcherIntent.putExtra("foodrecipe_edit", recipe)
        getResult.launch(launcherIntent)
    }
}
