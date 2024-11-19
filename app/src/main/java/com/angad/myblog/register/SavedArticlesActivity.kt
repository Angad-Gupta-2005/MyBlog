package com.angad.myblog.register

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.angad.myblog.adapter.BlogAdapter
import com.angad.myblog.databinding.ActivitySavedArticlesBinding
import com.angad.myblog.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SavedArticlesActivity : AppCompatActivity() {

//    Creating an instance of binding
    private lateinit var binding: ActivitySavedArticlesBinding

//    Creating an mutable list of saved article
    private val savedBlogsArticles = mutableListOf<BlogItemModel>()
//    Creating an instance of blogAdapter and initialised the firebaseAuth
    private lateinit var blogAdapter: BlogAdapter
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        Initialised the binding
        binding = ActivitySavedArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        Calling the function that perform back button functionality
        onClickBackButton()

//        Initialised the blogAdapter
        blogAdapter = BlogAdapter(savedBlogsArticles.filter { it.isSaved }.toMutableList())

//        Initialised the recyclerView and its adapter
        val recyclerView = binding.savedArticleRecyclerView
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

//        Getting the saved article data of a user and save it into the mutableList
        val userId = auth.currentUser?.uid
        if (userId != null){
        //    Getting the firebase databaseReference
            val userReference = FirebaseDatabase.getInstance("https://my-blog-70ebf-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users").child(userId).child("saveBlogPosts")
            userReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children){
                        val postId = postSnapshot.key
                        val isSaved = postSnapshot.value as Boolean
                        if (postId != null && isSaved){
                    //      Fetch the corresponding blog item on postId using coroutine that fetch data in IO thread
                            CoroutineScope(Dispatchers.IO).launch {
                                val blogItem = fetchBlogItem(postId)
                                if (blogItem != null){
                                    savedBlogsArticles.add(blogItem)
                            //    Back to the main thread
                                    launch(Dispatchers.Main) {
                                        blogAdapter.updateData(savedBlogsArticles)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SavedArticlesActivity, "Error ${error.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }

    }

    private suspend fun fetchBlogItem(postId: String): BlogItemModel? {
        val blogReference = FirebaseDatabase.getInstance("https://my-blog-70ebf-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("blogs")
        return try {
            val dataSnapshot = blogReference.child(postId).get().await()
            val blogData = dataSnapshot.getValue(BlogItemModel::class.java)
            blogData
        } catch (e: Exception){
            null
        }
    }

    private fun onClickBackButton() {
        binding.savedBackBtn.setOnClickListener {
            finish()
        }
    }
}