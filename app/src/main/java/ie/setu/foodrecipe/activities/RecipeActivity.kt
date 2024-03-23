package ie.setu.foodrecipe.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.setu.foodrecipe.R
import ie.setu.foodrecipe.adapters.IngredientAdapter
import ie.setu.foodrecipe.databinding.ActivityRecipeBinding
import ie.setu.foodrecipe.helpers.showImagePicker
import ie.setu.foodrecipe.main.MainApp
import ie.setu.foodrecipe.models.RecipeModel
import kotlinx.coroutines.launch

import timber.log.Timber.i
import java.io.File
import java.io.FileOutputStream
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class RecipeActivity : AppCompatActivity(), IngredientAdapter.OnDeleteListener {

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
        ingredientAdapter = IngredientAdapter(ingredientList, this)
        binding.recyclerView.adapter = ingredientAdapter

        // Dropdown spinner for cuisine types
        val cuisineTypes = listOf("Pick a cuisine", "English", "Italian", "Japanese", "Mexican", "Indian", "Chinese")

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

            // Show the delete recipe button
            binding.btnDeleteRecipe.visibility = View.VISIBLE

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

            //delete click listener
            binding.btnDeleteRecipe.setOnClickListener {
                lifecycleScope.launch {
                    app.recipes.deleteById(recipe.id) // Call deleteById with the recipe ID
                    setResult(RESULT_OK)
                    finish()
                }
                i("delete Button Pressed: ${recipe.title}")
                //(binding.recyclerView.adapter as? FoodRecipeAdapter)?.notifyDataSetChanged()
                //setResult(RESULT_OK)
                //finish()
            }
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
            if(selectedRating != 0f){
                recipe.ratings.add(selectedRating)
            }

            // Check if the new ingredient is not empty before adding it
            if (newIngredient.isNotEmpty()) {
                recipe.ingredients.add(newIngredient)
            }

            if (recipe.title.isNotEmpty()) {
                if (edit) {
                    lifecycleScope.launch {
                        recipe.id = intent.getStringExtra("recipeId")!!
                        app.recipes.update(recipe.copy())
                        // Add a delay, the update might take a little time to write in firebase,
                        // once the intent is returned to List view the recyclerview will be updated with findAll(),
                        // so the updates need to be written before calling finish()
                        sleep(3000)
                        //(binding.recyclerView.adapter as? FoodRecipeAdapter)?.notifyDataSetChanged()
                    }
                    // Update lastEditedTimestamp when the save button is pressed and recipe is actually updated
                    recipe.lastEditedTimestamp = System.currentTimeMillis()
                    i("update Button Pressed: ${recipe.title}")
                } else {
                    app.recipes.create(recipe.copy())
                    i("add Button Pressed: ${recipe.title}")
                }
                // Refresh the recyler view because a new recipe could have been added or a recipe could have been updated
                //(binding.recyclerView.adapter as? FoodRecipeAdapter)?.notifyDataSetChanged()
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(it, "Please Enter a title", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDeleteItem(position: Int) {
        recipe.ingredients.removeAt(position)
        ingredientAdapter.notifyItemRemoved(position)
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
                            val imageUri = result.data!!.data
                            imageUri?.let { uri ->
                                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                                val file = createImageFile()
                                saveBitmapToFile(bitmap, file)
                                recipe.image = file.toUri()
                                Picasso.get().load(recipe.image).into(binding.recipeImage)
                            }
                        }
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    // The image picker in the labs are only temporary images, meaning that once the app is closed, the content:// URI pointing to the image the user loaded no longer exists
    // I've implemented my own way of getting the real image URI that's located on the device, instead of it starting with content:// it starts with file:// which loads the real file path of the image

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }
}