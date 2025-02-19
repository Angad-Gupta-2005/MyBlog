package com.angad.myblog.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angad.myblog.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

//    Creating an instance of binding
    private lateinit var binding: ActivityWelcomeBinding

//    Declare an instance of FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        //    check if the user already logged in
        val currentUser = auth.currentUser
        if (currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        Initialised binding
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //    Initialised the FirebaseAuth
        auth = FirebaseAuth.getInstance()

//        On click login button
        onClickLoginButton()

//        On click register button
        onClickRegisterButton()
    }

    private fun onClickLoginButton() {
        binding.loginActivity.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun onClickRegisterButton() {
        binding.registerActivity.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}