package com.angad.myblog.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angad.myblog.R
import com.angad.myblog.databinding.ActivityAddArticleBinding
import com.angad.myblog.model.BlogItemModel
import com.angad.myblog.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddArticleActivity : AppCompatActivity() {

//    Creating an instance of binding
    private lateinit var binding: ActivityAddArticleBinding

//    Creating an instance of Firebase databaseReference and FirebaseAuth and initialised it
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://my-blog-70ebf-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("blogs")
    private val userReference: DatabaseReference = FirebaseDatabase.getInstance("https://my-blog-70ebf-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        Initialised the binding
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        On click add blog button save title and description into the firebase realtime database
        onClickAddBlogButton()
    }

    private fun onClickAddBlogButton() {
        binding.addBlogButton.setOnClickListener {
            val title = binding.title.text.toString().trim()
            val description = binding.description.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()){
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            }

        //    Getting the current user
            val user = auth.currentUser
            if (user != null){
                val userId = user.uid
                val userName = user.displayName?:"Anonymous"
                val userImageUrl = user.photoUrl?:""

        //        fetch userName and userProfile image from firebase database
                userReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener{
                    @SuppressLint("SimpleDateFormat")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData = snapshot.getValue(UserData::class.java)
                        if (userData != null){
                            val userNameFromDB = userData.name
                            val userImageFromDB = userData.profileImage
                    //    Getting the current date
                            val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                    //    Create a blogItemModel
                            val blogItem = BlogItemModel(
                                title,
                                userNameFromDB,
                                currentDate,
                                description,
                                 0,
                                userImageFromDB

                            )
                        //    Generating a unique key for the blog post
                            val key = databaseReference.push().key
                            if (key != null){
                                val blogReference = databaseReference.child(key)
                                blogReference.setValue(blogItem).addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        finish()
                                    } else {
                                        Toast.makeText( this@AddArticleActivity, "Fails to add blog ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }
}