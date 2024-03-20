package ie.setu.foodrecipe.activities
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import ie.setu.foodrecipe.databinding.ActivitySignInScreenBinding
import timber.log.Timber.i


class SignInScreenActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 123
    private lateinit var binding: ActivitySignInScreenBinding

    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ActivityResultLauncher
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(RC_SIGN_IN, result.resultCode, result.data)
        }

        // Check if the user is already signed in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is already signed in, navigate to the FoodRecipeListActivity
            val intent = Intent(this, FoodRecipeListActivity::class.java)
            startActivity(intent)
            finish() // Finish the SignInActivity to prevent the user from going back to it
        } else {
            // User is not signed in, launch FirebaseUI Auth UI
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                        AuthUI.IdpConfig.EmailBuilder().build()
                    )
                )
                .build()

            // Start the sign-in activity using the ActivityResultLauncher
            signInLauncher.launch(signInIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    // Print user information (optional)
                    i("Username: ${currentUser.displayName}")
                    i("Photo URL: ${currentUser.photoUrl}")
                    i("Email: ${currentUser.email}")
                }
                // Navigate to the FoodRecipeListActivity
                val intent = Intent(this, FoodRecipeListActivity::class.java)
                startActivity(intent)
                finish() // Finish the SignInActivity to prevent the user from going back to it
            } else {
                // Sign in failed or user canceled
                if (response != null) {
                    // Handle the error
                    i("Sign-in error: ${response.error?.message}")
                } else {
                    // User canceled the sign-in operation
                    i( "Sign-in canceled")
                }
            }
        }
    }
}