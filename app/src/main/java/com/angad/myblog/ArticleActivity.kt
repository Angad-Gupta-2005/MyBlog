package com.angad.myblog

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.angad.myblog.adapter.ArticleAdapter
import com.angad.myblog.databinding.ActivityArticleBinding
import com.angad.myblog.model.BlogItemModel
import com.angad.myblog.register.ReadMoreActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticleActivity : AppCompatActivity() {

//    Create an instance of binding
    private lateinit var binding: ActivityArticleBinding

//    Creating an instance of firebaseAuth and databaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

//    Creating an instance of adapter
    private lateinit var adapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        Initialised the binding
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        Initialised the auth, databaseReference, adapter
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://my-blog-70ebf-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("blogs")

//        Initialised the adapter
        adapter = ArticleAdapter(this, emptyList(), object :ArticleAdapter.OnItemClickListener{

        //    Function that perform read more functionality
            override fun onClickReadMoreButton(blogItem: BlogItemModel) {
                val intent = Intent(this@ArticleActivity, ReadMoreActivity::class.java)
                intent.putExtra("blogItem", blogItem)
                startActivity(intent)
            }

        //    Function that perform edit functionality
            override fun onClickEditButton(blogItem: BlogItemModel) {
                editBlogPost(blogItem)
            }

        //    Function that perform delete functionality
            override fun onClickDeleteButton(blogItem: BlogItemModel) {
                val postId = blogItem.postId
                val blogPostReference = databaseReference.child(postId)

                //    Remove the post
                blogPostReference.removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this@ArticleActivity, "Blog post deleted successfully 😊", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@ArticleActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }

        })

    //    getting the current user
        val currentUser = auth.currentUser?.uid

    //    Initialised the recyclerView
        val recyclerView = binding.articleRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

//        Getting the data from firebase realtimeDatabase
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
            //    Creating a arrayList
                val blogSavedList = ArrayList<BlogItemModel>()
            //    Fetch each blog
                for (i in snapshot.children){
                    val blogSaved = i.getValue(BlogItemModel::class.java)
                    if(blogSaved != null && currentUser == blogSaved.userId){
                        blogSavedList.add(blogSaved)
                    }
                }
                adapter.setData(blogSavedList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ArticleActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun editBlogPost(blogItem: BlogItemModel) {
        TODO("Not yet implemented")
    }

//    private fun deleteBlogPost(blogItem: BlogItemModel) {
//
//    }
}