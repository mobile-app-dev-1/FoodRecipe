package ie.setu.foodrecipe.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ie.setu.foodrecipe.R
import ie.setu.foodrecipe.databinding.ActivityFoodRecipeListBinding
import ie.setu.foodrecipe.main.MainApp
import ie.setu.foodrecipe.adapters.FoodRecipeAdapter
import ie.setu.foodrecipe.adapters.FoodRecipeListener
import ie.setu.foodrecipe.models.RecipeModel
import kotlinx.coroutines.launch

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

        lifecycleScope.launch{
            binding.recyclerView.adapter = FoodRecipeAdapter(app.recipes.findAll(), this@FoodRecipeListActivity)
        }

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
                R.id.nav_logout -> {
                    signOutUser()
                }
            }
            // return true, The event has been handled and no further action is needed
            true
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                (binding.recyclerView.adapter as? FoodRecipeAdapter)?.filter?.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (binding.recyclerView.adapter as? FoodRecipeAdapter)?.filter?.filter(newText)
                return true
            }

        })
    }

    private fun signOutUser() {
        // Sign out from Firebase Authentication
        Firebase.auth.signOut()

        // Revoke access if using Google Sign-In
        val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        signInClient.signOut().addOnCompleteListener {
            // Navigate to the sign-in screen regardless of the outcome
            val intent = Intent(this, SignInScreenActivity::class.java)
            startActivity(intent)
            finish()
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
                lifecycleScope.launch{
                    binding.recyclerView.adapter = FoodRecipeAdapter(app.recipes.findAll(), this@FoodRecipeListActivity)
                }
                // tell the recyclerView that the data (could) have changed, i.e. could delete a recipe
                (binding.recyclerView.adapter as? FoodRecipeAdapter)?.notifyDataSetChanged()

            }
            if(it.resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(binding.root, "Recipe Add Cancelled", Snackbar.LENGTH_LONG).show()
            }
        }

    override fun onFoodRecipeClick(recipe: RecipeModel) {
        val launcherIntent = Intent(this, RecipeActivity::class.java)
        launcherIntent.putExtra("recipeId", recipe.id)
        launcherIntent.putExtra("foodrecipe_edit", recipe)
        getResult.launch(launcherIntent)
    }
}
