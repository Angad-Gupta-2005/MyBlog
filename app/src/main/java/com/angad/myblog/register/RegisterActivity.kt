package com.angad.myblog.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angad.myblog.model.UserData
import com.angad.myblog.databinding.ActivityRegisterBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {

    //    Creating an instance of binding
    private lateinit var binding: ActivityRegisterBinding

    //    Declare an instance of FirebaseAuth
    private lateinit var auth: FirebaseAuth

    //    Declare an instance of Firebase Database
    private lateinit var database: FirebaseDatabase

    //    Declare an instance of Firebase Storage
    private lateinit var storage: FirebaseStorage

    //    For fetching the image
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        Initialised the binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Initialised firebaseAuth
        auth = FirebaseAuth.getInstance()

//        Initialised the firebase database
        database = FirebaseDatabase.getInstance("https://my-blog-70ebf-default-rtdb.asia-southeast1.firebasedatabase.app")

//        Initialised the firebase storage
        storage = FirebaseStorage.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        Calling the function that perform functionality of register
        onClickRegisterButton()

//        Calling the function that start login activity if user already register
        onClickLoginButton()

//        Calling the function that change the image of cardView
        onClickProfileImage()
    }

//    For Image change
    private fun onClickProfileImage() {
        binding.profile.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                PICK_IMAGE_REQUEST
            )
        }
    }

//    For Image change
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){
            imageUri = data.data
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.userImage)
        }
    }

    private fun onClickRegisterButton() {
        binding.registerButton.setOnClickListener {

//            Taking input data from input field
            val name = binding.registerName.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPass.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT)
                                .show()
                            val user = auth.currentUser
                            user?.let {
                                //  Save user data in firebase realtime database
                                val userReference = database.getReference("users")
                                val userId = user.uid
                                val userData = UserData(name, email)
                                userReference.child(userId).setValue(userData)
                                    .addOnSuccessListener {
                                        Log.d("TAG", "onClickRegisterButton: data saved")
                                        Toast.makeText(this, "Successfully save data in database", Toast.LENGTH_LONG).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("TAG", "onClickRegisterButton: Error data ${e.message}" )
                                        Toast.makeText(this, "Fails to save data in database", Toast.LENGTH_LONG).show()
                                    }

                                // Upload image to firebase storage
                                if (imageUri != null){
                                    val storageReference = storage.reference.child("profile_image/$userId.jpg")
                                    storageReference.putFile(imageUri!!)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Profile update successful", Toast.LENGTH_SHORT).show()
                                            storageReference.downloadUrl.addOnCompleteListener { imageUri ->
                                                val imageUrl = imageUri.result.toString()
                                                //    Saving the image url to the realtime database
                                                userReference.child(userId).child("profileImage").setValue(imageUrl)
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Profile update fails: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                }
                            }
                            //   After registration go to login page
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Registration Fails: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    private fun onClickLoginButton() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}