package com.angad.myblog.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.angad.myblog.R
import com.angad.myblog.databinding.BlogItemBinding
import com.angad.myblog.model.BlogItemModel
import com.angad.myblog.register.ReadMoreActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BlogAdapter(private val items: List<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    //    Getting the databaseReference and currentUser for like and save functionality
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://my-blog-70ebf-default-rtdb.asia-southeast1.firebasedatabase.app").reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        //   create the view in case the layout manager fails to create view for the data
        val itemView = BlogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = items[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class BlogViewHolder(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blogItemModel: BlogItemModel) {
            //    For like
            val postId = blogItemModel.postId
            val context = binding.root.context

            binding.heading.text = blogItemModel.heading
            Glide.with(binding.profileImage.context)
                .load(blogItemModel.profileImage)
                .into(binding.profileImage)

            binding.userName.text = blogItemModel.userName
            binding.date.text = blogItemModel.date
            binding.post.text = blogItemModel.post
            binding.likeCount.text = blogItemModel.likeCount.toString()

            //    Set on click listener that jump to read more activity
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ReadMoreActivity::class.java)
                intent.putExtra("blogItem", blogItemModel)
                context.startActivity(intent)
            }

            //    Like button functionality
            //    Check if the current user has like the post and update the like button image
            val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")
            val currentUserLiked = currentUser?.uid?.let { uid ->
                postLikeReference.child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                binding.likeBtn.setImageResource(R.drawable.ic_red_heart)
                            } else {
                                binding.likeBtn.setImageResource(R.drawable.ic_black_heart)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }

            //    Handle like button clicks
            binding.likeBtn.setOnClickListener {
                if (currentUser != null) {
                    handleLikeButtonClicked(postId, blogItemModel, binding)
                } else {
                    Toast.makeText(context, "You have to login first", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun handleLikeButtonClicked(postId: String, blogItemModel: BlogItemModel, binding: BlogItemBinding) {
            val userReference = databaseReference.child("users").child(currentUser!!.uid)
            val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")

            //    Check if user has already liked the post, so unlike it
            postLikeReference.child(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            userReference.child("likes").child(postId).removeValue()
                                .addOnSuccessListener {
                                    postLikeReference.child(currentUser.uid).removeValue()
                                    blogItemModel.likedBy?.remove(currentUser.uid)
                                    //    calling the function that updating the like button image
                                    updateLikeButtonImage(binding,false)

                                    //    Decrement the like count by 1 in the database
                                    val newLikeCount = blogItemModel.likeCount - 1
                                    blogItemModel.likeCount = newLikeCount
                                    databaseReference.child("blogs").child(postId).child("likeCount").setValue(newLikeCount)
                                    //    Notify that the data is changed
                                    notifyDataSetChanged()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Liked Clicked", "onDataChange: Failed to like ${e.message}" )
                                }
                        } else {
                            //    Check if user has not liked the post, so like it
                            userReference.child("likes").child(postId).setValue(true)
                                .addOnSuccessListener {
                                    postLikeReference.child(currentUser.uid).setValue(true)
                                    blogItemModel.likedBy?.add(currentUser.uid)
                                    //    calling the function that updating the like button image
                                    updateLikeButtonImage(binding, true)
                                    //    Increment the like count by 1 in the database
                                    val newLikeCount = blogItemModel.likeCount + 1
                                    blogItemModel.likeCount = newLikeCount
                                    databaseReference.child("blogs").child(postId).child("likeCount").setValue(newLikeCount)
                                    //    Notify the database that the data is changed
                                    notifyDataSetChanged()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Liked Clicked", "onDataChange: Failed to like ${e.message}")
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }

    private fun updateLikeButtonImage(binding: BlogItemBinding, liked: Boolean) {
        if (liked){
            binding.likeBtn.setImageResource(R.drawable.ic_black_heart)
        } else {
            binding.likeBtn.setImageResource(R.drawable.ic_red_heart)
        }
    }
}