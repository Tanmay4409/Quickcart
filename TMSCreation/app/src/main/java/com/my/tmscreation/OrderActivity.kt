package com.my.tmscreation

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable

class OrderActivity : AppCompatActivity() {
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var fileTypeSpinner: Spinner
    private lateinit var userName: EditText
    private lateinit var userPhone: EditText
    private lateinit var userEmail: EditText
    private lateinit var cardNumber: EditText
    private lateinit var cardHolderName: EditText
    private lateinit var cardExpiryDate: EditText
    private lateinit var cardCvv: EditText
    private lateinit var buyNowButton: Button

    // Variables to detect mode
    private var isCartMode = false
    private var cartItems: ArrayList<CartItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val back: ImageView = findViewById(R.id.backBtn)
        back.setOnClickListener { onBackPressed() }

        productName = findViewById(R.id.product_name)
        productPrice = findViewById(R.id.product_price)
        fileTypeSpinner = findViewById(R.id.file_type_spinner)
        userName = findViewById(R.id.user_name)
        userPhone = findViewById(R.id.user_phone)
        userEmail = findViewById(R.id.user_email)
        cardNumber = findViewById(R.id.card_number)
        cardHolderName = findViewById(R.id.card_holder_name)
        cardExpiryDate = findViewById(R.id.card_expiry_date)
        cardCvv = findViewById(R.id.card_cvv)
        buyNowButton = findViewById(R.id.buy_now_button)

        // Detect mode
        isCartMode = intent.hasExtra("cartItems")
        if (isCartMode) {
            cartItems = intent.getSerializableExtra("cartItems") as ArrayList<CartItem>
            val total = cartItems.sumOf { it.price.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 0 }
            productName.text = "Multiple Items"
            productPrice.text = "Total: ₹$total"
        } else {
            val name = intent.getStringExtra("name") ?: "N/A"
            val price = intent.getStringExtra("price") ?: "N/A"
            productName.text = name
            productPrice.text = price
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.file_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            fileTypeSpinner.adapter = adapter
        }

        buyNowButton.setOnClickListener {
            if (validateInput()) {
                val intent = Intent(this, InvoiceActivity::class.java).apply {
                    putExtra("userName", userName.text.toString())
                    putExtra("userEmail", userEmail.text.toString())

                    if (isCartMode) {
                        putExtra("cartItems", cartItems)
                    } else {
                        putExtra("name", productName.text.toString())
                        putExtra("price", productPrice.text.toString())
                    }
                }
                startActivity(intent)
                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (userName.text.isBlank()) {
            userName.error = "Name is required"
            isValid = false
        }

        val phonePattern = Regex("^[6-9]\\d{9}$")
        if (userPhone.text.isBlank()) {
            userPhone.error = "Phone number is required"
            isValid = false
        } else if (!phonePattern.matches(userPhone.text)) {
            userPhone.error = "Enter a valid 10-digit phone number"
            isValid = false
        }

        if (userEmail.text.isBlank()) {
            userEmail.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail.text).matches()) {
            userEmail.error = "Enter a valid email"
            isValid = false
        }

        if (cardNumber.text.isBlank()) {
            cardNumber.error = "Card number is required"
            isValid = false
        } else if (!cardNumber.text.matches(Regex("^\\d{16}$"))) {
            cardNumber.error = "Enter a valid 16-digit card number"
            isValid = false
        }

        if (cardHolderName.text.isBlank()) {
            cardHolderName.error = "Card holder name is required"
            isValid = false
        }

        val expiryPattern = Regex("^(0[1-9]|1[0-2])/\\d{2}$")
        if (cardExpiryDate.text.isBlank()) {
            cardExpiryDate.error = "Expiry date is required"
            isValid = false
        } else if (!expiryPattern.matches(cardExpiryDate.text)) {
            cardExpiryDate.error = "Enter expiry as MM/YY"
            isValid = false
        }

        if (cardCvv.text.isBlank()) {
            cardCvv.error = "CVV is required"
            isValid = false
        } else if (!cardCvv.text.matches(Regex("^\\d{3}$"))) {
            cardCvv.error = "CVV must be 3 digits"
            isValid = false
        }

        return isValid
    }
}
