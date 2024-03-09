package ie.setu.foodrecipe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.setu.foodrecipe.databinding.CardFoodrecipeBinding
import ie.setu.foodrecipe.models.RecipeModel


interface FoodRecipeListener {
    fun onFoodRecipeClick(foodrecipe: RecipeModel)
}

class FoodRecipeAdapter constructor(private var recipes: List<RecipeModel>, private val listener: FoodRecipeListener) :
    RecyclerView.Adapter<FoodRecipeAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardFoodrecipeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val recipe = recipes[holder.adapterPosition]
        holder.bind(recipe, listener)
    }

    override fun getItemCount(): Int = recipes.size

    class MainHolder(private val binding : CardFoodrecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeModel, listener: FoodRecipeListener) {
            binding.foodRecipeTitle.text = recipe.title
            binding.foodRecipeDescription.text = recipe.description
            binding.foodRecipeCardCuisine.text = recipe.cuisine
            Picasso.get()
                .load(recipe.image)
                .into(binding.recipeImage)
            binding.root.setOnClickListener { listener.onFoodRecipeClick(recipe)}
        }
    }
}