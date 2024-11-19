package com.angad.myblog.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.angad.myblog.ProfileActivity
import com.angad.myblog.adapter.BlogAdapter
import com.angad.myblog.databinding.ActivityMainBinding
import com.angad.myblog.model.BlogItemModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    //    Creating an instance of view binding
    private lateinit var binding: ActivityMainBinding

    //    Declare an instance of firebaseAuth and database reference
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private val blogItems = mutableListOf<BlogItemModel>()

    //    Creating an instance of recyclerView and Adapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var blogAdapter: BlogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        Initialised view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        Calling the function that go to the saved article page
        onClickSaveArticleButton()

//        Calling the function that go to the profile page
        onClickProfileImage()


//        Initialised the firebaseAuth and databaseReference
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://my-blog-70ebf-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("blogs")

        //    Getting current user id
        val userId = auth.currentUser?.uid

    //    Calling the function that set user profile
        if (userId != null) {
            loadUserProfileImage(userId)
        }

//        Set blog post into recyclerView
    //    Initialised the recyclerView and set adapter
        recyclerView = binding.blogRecyclerView
        blogAdapter = BlogAdapter(blogItems)

        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    //    Fetching the data from firebase database reference
        databaseReference.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

            //    Solving the problem of repeat data in recyclerView by clearing the snapshot while loading the data into the snapshot
                blogItems.clear()
                for(i in snapshot.children){
            //    Fetch data from firebase realtime database and store into the mutableListOf of blogItems one at a time
                    val blogItem = i.getValue(BlogItemModel::class.java)
                    if (blogItem != null){
                        blogItems.add(blogItem)
                    }
                }
            //    Reverse the blogItems data so that new added data will on the top
                blogItems.reverse()

            //    Notify the adapter that the data has changed
                blogAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Blog loading fails ${error.message}", Toast.LENGTH_LONG).show()
            }

        })

//        On click Floating Action Button
        onClickFloatingActionButton()
    }

    private fun onClickProfileImage() {
        binding.profileImage.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun onClickSaveArticleButton() {
        binding.saveArticleButton.setOnClickListener {
            startActivity(Intent(this, SavedArticlesActivity::class.java))
        }
    }

    //    Function that load the profile image from firebase realtime database set to profile
    private fun loadUserProfileImage(userId: String) {
        val userReference =
            FirebaseDatabase.getInstance("https://my-blog-70ebf-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child(
                "users"
            ).child(userId)
        userReference.child("profileImage").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImage = snapshot.getValue(String::class.java)
                if (profileImage != null) {
                    Glide.with(this@MainActivity)
                        .load(profileImage)
                        .into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Error loading profile ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun onClickFloatingActionButton() {
        binding.floatingAddArticleButton.setOnClickListener {
            startActivity(Intent(this, AddArticleActivity::class.java))
        }
    }
}