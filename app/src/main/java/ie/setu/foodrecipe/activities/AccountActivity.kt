package ie.setu.foodrecipe.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import ie.setu.foodrecipe.R
import ie.setu.foodrecipe.databinding.ActivityAccountBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

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
    }
}