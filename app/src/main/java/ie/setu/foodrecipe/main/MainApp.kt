package ie.setu.foodrecipe.main

import android.app.Application
import ie.setu.foodrecipe.models.RecipeModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val recipes = ArrayList<RecipeModel>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("FoodRecipe started")

        val myMutableList: MutableList<String> = mutableListOf("Beef Mince", "Spuds", "Carrots")
        recipes.add(RecipeModel("Pie", "Beef Mince Pie", myMutableList))
    }
}