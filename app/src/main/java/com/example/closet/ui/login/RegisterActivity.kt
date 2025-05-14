package com.example.closet.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.closet.MainActivity
import com.example.closet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {

    private lateinit var emailTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var usernameTextView: EditText
    private lateinit var button: Button
    private lateinit var login: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Hide system UI (bottom nav + status bar)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )

        // creating FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        emailTextView = findViewById(R.id.email_edittext)
        passwordTextView = findViewById(R.id.password_edittext)
        usernameTextView = findViewById(R.id.username_edittext)
        button = findViewById(R.id.register_button)
        login = findViewById(R.id.login_text)

        login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        button.setOnClickListener {
            registerNewUser()
        }
    }

    private fun registerNewUser() {
        val username = usernameTextView.text.toString().trim()
        val email = emailTextView.text.toString().trim()
        val password = passwordTextView.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            return
        }

        val userRef = FirebaseDatabase.getInstance().getReference("users")

        // Proceed with account creation (no username uniqueness check)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userMap = mapOf(
                        "username" to username,
                        "email" to email,
                        "profilePictureUrl" to "" // optional default field
                    )
                    userId?.let {
                        userRef.child(it).setValue(userMap).addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Registration failed. Try again later.", Toast.LENGTH_LONG).show()
                }
            }
    }


}