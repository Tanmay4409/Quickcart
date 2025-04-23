package com.my.tmscreation

import android.widget.Button
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val welcomeMessage: TextView = findViewById(R.id.welcomeback)
        val currentUser = FirebaseAuth.getInstance().currentUser
        welcomeMessage.text = "Welcome back, ${currentUser?.displayName ?: "User"}"

        setupBottomNavigationView()

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalTextView = findViewById(R.id.total_price_text)

        recyclerView.layoutManager = LinearLayoutManager(this)
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        cartAdapter = CartAdapter(cartItems, userId) {
            updateTotal()
        }
        recyclerView.adapter = cartAdapter

        fetchCartItemsFromFirebase()
        val checkoutButton: Button = findViewById(R.id.checkout_button)

        checkoutButton.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                val intent = Intent(this, OrderActivity::class.java)
                intent.putExtra("cartItems", ArrayList(cartItems)) // Make sure CartItem implements Serializable
                startActivity(intent)
            } else {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCartItemsFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("carts").child(userId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(CartItem::class.java)
                    item?.key = itemSnapshot.key ?: ""
                    if (item != null) {
                        cartItems.add(item)
                    }
                }
                Log.d("CART_DEBUG", "Fetched ${cartItems.size} items from Firebase")
                cartAdapter.notifyDataSetChanged()
                updateTotal()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Failed to load cart", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTotal() {
        var total = 0
        for (item in cartItems) {
            val price = item.price.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 0
            total += price
        }
        totalTextView.text = "Total: ₹$total"
    }
    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_cart

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomePageActivity::class.java))
                    true
                }
                R.id.navigation_product -> {
                    startActivity(Intent(this, ProductActivity::class.java))
                    true
                }
                R.id.navigation_cart -> {
                    true // Already here
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, UserAccountActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

}
