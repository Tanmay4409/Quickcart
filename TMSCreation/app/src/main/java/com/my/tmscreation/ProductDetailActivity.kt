package com.my.tmscreation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ProductDetailActivity : AppCompatActivity() {
    private lateinit var productImage: ImageView
    private lateinit var productPrice: TextView
    private lateinit var productName: TextView
    private lateinit var productDownloads: TextView
    private lateinit var productRating: TextView
    private lateinit var productDescription: TextView
    private lateinit var orderButton: Button
    private var productImageResId: Int = 0
    private var productPriceStr: String = ""
    private var productNameStr: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val back: ImageView = findViewById(R.id.backBtn)
        back.setOnClickListener {
            onBackPressed()
        }

        val Store: ImageView = findViewById(R.id.store)
        Store.setOnClickListener {
            val intent = Intent(this@ProductDetailActivity, ProductActivity::class.java)
            startActivity(intent)
        }

        val addToCart: Button = findViewById(R.id.add_to_cart_button)
        addToCart.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                val cartItem = CartItem(
                    productNameStr,
                    productPriceStr,
                    productImageResId
                )


                val dbRef = FirebaseDatabase.getInstance().reference
                val userCartRef = dbRef.child("carts").child(currentUser.uid)
                val itemId = userCartRef.push().key ?: return@setOnClickListener

                userCartRef.child(itemId).setValue(cartItem)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please log in to use cart", Toast.LENGTH_SHORT).show()
            }
        }

        productImage = findViewById(R.id.product_image)
        productPrice = findViewById(R.id.product_price)
        productName = findViewById(R.id.product_name)
        productDownloads = findViewById(R.id.product_downloads)
        productRating = findViewById(R.id.product_rating)
        productDescription = findViewById(R.id.product_description)
        orderButton = findViewById(R.id.buy_button)

        // Get data from intent
        productImageResId = intent.getIntExtra("imageResId", 0)
        productPriceStr = intent.getStringExtra("price") ?: "0"
        productNameStr = intent.getStringExtra("name") ?: "Unnamed"
        val downloads = intent.getStringExtra("downloads")
        val rating = intent.getStringExtra("rating")
        val description = intent.getStringExtra("description")

        // Set data to views
        Glide.with(this).load(productImageResId).into(productImage)
        productPrice.text = productPriceStr
        productName.text = productNameStr
        productDownloads.text = downloads
        productRating.text = rating
        productDescription.text = description

        orderButton.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("name", productName.text.toString())
            intent.putExtra("price", productPrice.text.toString())
            startActivity(intent)
        }

    }
}
