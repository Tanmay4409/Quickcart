package com.my.tmscreation

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import java.io.Serializable

class InvoiceActivity : AppCompatActivity() {

    private lateinit var productNameTextView: TextView
    private lateinit var productPriceTextView: TextView
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var finishButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        productNameTextView = findViewById(R.id.product_name)
        productPriceTextView = findViewById(R.id.product_price)
        userNameTextView = findViewById(R.id.user_name)
        userEmailTextView = findViewById(R.id.user_email)
        finishButton = findViewById(R.id.finish_button)

        val userName = intent.getStringExtra("userName")
        val userEmail = intent.getStringExtra("userEmail")
        val cartItems = intent.getSerializableExtra("cartItems") as? ArrayList<CartItem>

        userNameTextView.text = "Name: $userName"
        userEmailTextView.text = "Email: $userEmail"

        if (cartItems != null) {
            val names = cartItems.joinToString(", ") { it.name }
            val total = cartItems.sumOf { it.price.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 0 }
            productNameTextView.text = names
            productPriceTextView.text = "₹$total"
        } else {
            val productName = intent.getStringExtra("name")
            val productPrice = intent.getStringExtra("price")
            productNameTextView.text = productName
            productPriceTextView.text = productPrice
        }

        finishButton.setOnClickListener {
            // Clear cart from Firebase
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val cartRef = FirebaseDatabase.getInstance()
                    .getReference("carts")
                    .child(currentUser.uid)

                cartRef.removeValue()
                    .addOnSuccessListener {
                        val intent = Intent(this, HomePageActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to clear cart", Toast.LENGTH_SHORT).show()
                    }
            } else {
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(Intent(this, HomePageActivity::class.java))
                finish()
            }
        }
    }
}
