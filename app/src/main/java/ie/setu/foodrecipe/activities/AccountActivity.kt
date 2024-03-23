package ie.setu.foodrecipe.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import ie.setu.foodrecipe.R
import ie.setu.foodrecipe.adapters.FoodRecipeAdapter
import ie.setu.foodrecipe.adapters.FoodRecipeListener
import ie.setu.foodrecipe.databinding.ActivityAccountBinding
import ie.setu.foodrecipe.main.MainApp
import ie.setu.foodrecipe.models.RecipeModel
import kotlinx.coroutines.launch
import timber.log.Timber.i
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AccountActivity : AppCompatActivity(), FoodRecipeListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        // reference to the main application (parent of app)
        app = application as MainApp

        binding.topAppBar.title = getString(R.string.myAccount)

        // Enable the back button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set click listener for the toolbar back button
        binding.topAppBar.setNavigationOnClickListener {
            // Handle back button click here
            val intent = Intent(this, FoodRecipeListActivity::class.java)
            startActivity(intent)
            finish()
        }

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            if(currentUser.photoUrl != null) {
                Picasso.get()
                    .load(currentUser.photoUrl)
                    .into(binding.accountPhoto)
            }
            binding.accountName.text = currentUser.displayName
            binding.accountEmail.text = currentUser.email
            binding.accountUID.text = currentUser.uid
            val creationTimestamp = currentUser?.metadata?.creationTimestamp
            if (creationTimestamp != null) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = sdf.format(Date(creationTimestamp))
                binding.accountCreated.text = formattedDate
            } else {
                binding.accountCreated.text = "N/A"
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager


        lifecycleScope.launch{
            if (currentUser != null) {
                i("USER RECIPES: ${app.recipes.findAllByUserID(currentUser.uid)}")
                binding.recyclerView.adapter = FoodRecipeAdapter(app.recipes.findAllByUserID(currentUser.uid.toString()), this@AccountActivity)
            }
        }
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch{
                    binding.recyclerView.adapter = FoodRecipeAdapter(app.recipes.findAllByUserID(
                        FirebaseAuth.getInstance().currentUser?.uid.toString()), this@AccountActivity)
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