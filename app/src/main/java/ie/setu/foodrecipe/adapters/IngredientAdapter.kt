package ie.setu.foodrecipe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.setu.foodrecipe.databinding.IngredientItemBinding

class IngredientAdapter(private val ingredients: MutableList<String>) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = IngredientItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[holder.adapterPosition]
        holder.bind(ingredient)
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    fun removeIngredient(position: Int) {
        ingredients.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class IngredientViewHolder(private val binding: IngredientItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: String) {
            binding.editTextIngredientItem.setText(ingredient)

            binding.ingredientDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    removeIngredient(position)
                }
            }
        }
    }

}