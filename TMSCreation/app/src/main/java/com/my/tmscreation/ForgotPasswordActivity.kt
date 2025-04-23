package com.my.tmscreation

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ForgotPasswordActivity : BottomMessageActivity() {

    private lateinit var emailField: EditText
    private lateinit var submitButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        emailField = findViewById(R.id.email)
        submitButton = findViewById(R.id.submit_btn)

        submitButton.setOnClickListener {
            val email = emailField.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                showBottomMessage("Please enter your email address", true)
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showBottomMessage("Password reset email sent to $email", false)
                    } else {
                        showBottomMessage("Failed to send reset email: ${task.exception?.message}", true)
                    }
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
