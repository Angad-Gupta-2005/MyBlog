package com.angad.myblog

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angad.myblog.databinding.ActivityProfileBinding
import com.angad.myblog.register.AddArticleActivity
import com.angad.myblog.register.LoginActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

//    Creating an instance of binding
    private lateinit var binding: ActivityProfileBinding

//    Creating an instance of firebaseAuth and firebase databaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        Initialised the binding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        Initialised the firebaseAuth and databaseReference
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://my-blog-70ebf-default-rtdb.asia-southeast1.firebasedatabase.app")
            .reference
            .child("users")

    //    Getting the current user and load data
        val userID = auth.currentUser?.uid
        if (userID != null){
            loadUserProfileData(userID)
        }

    //    Calling the function that goto the Add New Article
        onClickAddNewArticle()

    //    Calling the function to goto the Your Article
        onClickYourArticle()

    //    Calling the function for logout
        onClickLogout()
    }

    private fun onClickAddNewArticle() {
        binding.addNewBlogBtn.setOnClickListener {
            startActivity(Intent(this, AddArticleActivity::class.java))
        }
    }

    private fun onClickYourArticle() {
        binding.articlesBtn.setOnClickListener {
            startActivity(Intent(this, ArticleActivity::class.java))
        }
    }

    private fun onClickLogout() {
        binding.logOutButton.setOnClickListener {
            auth.signOut()
        //    Goto the logging page after logout
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
            Toast.makeText(this, "Logout Successful🙂", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadUserProfileData(userID: String) {
        val userReference = databaseReference.child(userID)

    //    load user profile image
        userReference.child("profileImage").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImage = snapshot.getValue(String::class.java)
                if (profileImage != null){
                    Glide.with(this@ProfileActivity)
                        .load(profileImage)
                        .into(binding.userProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Error loading profile ${error.message}", Toast.LENGTH_SHORT).show()
            }

        })

    //    load user name
        userReference.child("name").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.getValue(String::class.java)
                if (name != null){
                    binding.userName.text = name
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}