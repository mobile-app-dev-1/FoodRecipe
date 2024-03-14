package ie.setu.foodrecipe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.setu.foodrecipe.databinding.CardFoodrecipeBinding
import ie.setu.foodrecipe.models.RecipeModel

interface FoodRecipeListener {
    fun onFoodRecipeClick(foodrecipe: RecipeModel)
}

class FoodRecipeAdapter constructor(
    private var recipes: List<RecipeModel>,
    private val listener: FoodRecipeListener
) : RecyclerView.Adapter<FoodRecipeAdapter.MainHolder>(), Filterable {

    private var filteredRecipes: List<RecipeModel> = recipes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardFoodrecipeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val recipe = filteredRecipes[position]
        holder.bind(recipe, listener)
    }

    override fun getItemCount(): Int = filteredRecipes.size

    class MainHolder(private val binding: CardFoodrecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeModel, listener: FoodRecipeListener) {
            binding.foodRecipeTitle.text = recipe.title
            binding.foodRecipeDescription.text = recipe.description
            binding.foodRecipeCardCuisine.text = recipe.cuisine
            binding.ratingBar.rating = recipe.ratings.average().toFloat()
            Picasso.get()
                .load(recipe.image)
                .into(binding.recipeImage)
            binding.root.setOnClickListener { listener.onFoodRecipeClick(recipe) }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<RecipeModel>()
                val query = constraint?.toString()?.trim()?.lowercase()
                if (query.isNullOrEmpty()) {
                    filteredList.addAll(recipes)
                } else {
                    recipes.forEach { recipe ->
                        // check if search query is contained in recipes -> title/description/cuisine
                        if (recipe.title.lowercase().contains(query) || recipe.description.lowercase().contains(query) || recipe.cuisine.lowercase().contains(query)
                        ) {
                            filteredList.add(recipe)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredRecipes = results?.values as? List<RecipeModel> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}
