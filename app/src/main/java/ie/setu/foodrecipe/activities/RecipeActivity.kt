package ie.setu.foodrecipe.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.setu.foodrecipe.R
import ie.setu.foodrecipe.adapters.IngredientAdapter
import ie.setu.foodrecipe.databinding.ActivityRecipeBinding
import ie.setu.foodrecipe.helpers.showImagePicker
import ie.setu.foodrecipe.main.MainApp
import ie.setu.foodrecipe.models.RecipeModel

import timber.log.Timber.i


class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeBinding
    var recipe = RecipeModel()
    lateinit var app: MainApp

    private lateinit var ingredientAdapter: IngredientAdapter
    private val ingredientList: MutableList<String> = mutableListOf()

    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>

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

        // Dropdown spinner for cuisine types
        val cuisineTypes = listOf("Pick a cuisine", "Irish", "Italian", "Japanese", "Mexican", "Indian", "Chinese")

        val cuisineSpinner = binding.cuisineSpinner

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cuisineTypes)
        // Layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        cuisineSpinner.adapter = adapter

        // Set a listener to handle the selected item
        cuisineSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                // Handle the selected item here, for example, save it to your data model
                val selectedCuisine = cuisineTypes[position]
                // Now you can use 'selectedCuisine' in your data model
                recipe.cuisine = selectedCuisine
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                recipe.cuisine = ""
            }
        }


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

        // Set up the listener for the RatingBar
        var selectedRating = 0.0f

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // Update the selectedRating when the user adjusts the rating
            selectedRating = rating
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

            // Set up the spinner selection based on the recipe's cuisine
            val cuisineIndex = cuisineTypes.indexOf(recipe.cuisine)
            if (cuisineIndex != -1) {
                cuisineSpinner.setSelection(cuisineIndex)
                i("Selected cuisine index: $cuisineIndex")
            } else {
                i("Cuisine index not found for ${recipe.cuisine}")
            }

            cuisineSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                    // Update the data model's cuisine property when an item is selected
                    recipe.cuisine = cuisineTypes[position]
                    i("Selected cuisine: ${recipe.cuisine}")
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // Do nothing when nothing is selected
                }
            }

            // Call Picasso to load the image URI into the image view
            Picasso.get()
                .load(recipe.image)
                .into(binding.recipeImage)

            // Update Button text for updating an image
            binding.chooseImage.setText(R.string.button_updateImage)
            // Update Button Text for Saving Recipe
            binding.btnAddRecipe.setText(R.string.button_saveRecipe)
        }

        // Button listener for adding an image
        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }
        registerImagePickerCallback()

        // new button listner for (updating) not creating a new one
        // Click Listener for adding the recipe
        binding.btnAddRecipe.setOnClickListener() {
            recipe.title = binding.recipeTitle.text.toString().trim()
            recipe.description = binding.recipeDescription.text.toString()

            val newIngredient = binding.recipeIngredient.text.toString().trim()

            // Include the selectedRating while updating or creating a recipe
            recipe.ratings.add(selectedRating)

            // Check if the new ingredient is not empty before adding it
            if (newIngredient.isNotEmpty()) {
                recipe.ingredients.add(newIngredient)
            }

            if (recipe.title.isNotEmpty()) {
                if (edit) {
                    app.recipes.update(recipe.copy())
                    // Update lastEditedTimestamp when the save button is pressed and recipe is actually updated
                    recipe.lastEditedTimestamp = System.currentTimeMillis()
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

    // Callback function for image selection
    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            recipe.image = result.data!!.data!!
                            Picasso.get().load(recipe.image).into(binding.recipeImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}