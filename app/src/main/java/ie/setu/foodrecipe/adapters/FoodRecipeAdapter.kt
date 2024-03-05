package ie.setu.foodrecipe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.setu.foodrecipe.databinding.CardFoodrecipeBinding
import ie.setu.foodrecipe.models.RecipeModel

class FoodRecipeAdapter constructor(private var recipes: List<RecipeModel>) :
    RecyclerView.Adapter<FoodRecipeAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardFoodrecipeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val recipe = recipes[holder.adapterPosition]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    class MainHolder(private val binding : CardFoodrecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeModel) {
            binding.foodRecipeTitle.text = recipe.title
            binding.foodRecipeDescription.text = recipe.description
        }
    }
}