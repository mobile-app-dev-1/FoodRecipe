package ie.setu.foodrecipe.main

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import ie.setu.foodrecipe.models.FoodRecipeFirebaseStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var recipes: FoodRecipeFirebaseStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("FoodRecipe started")

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        val auth = FirebaseAuth.getInstance()

        // Initialize recipes after Firebase has been initialized
        recipes = FoodRecipeFirebaseStore()
    }
}
