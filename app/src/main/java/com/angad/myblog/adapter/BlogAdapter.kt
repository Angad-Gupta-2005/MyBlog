package com.angad.myblog.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.angad.myblog.databinding.BlogItemBinding
import com.angad.myblog.model.BlogItemModel
import com.angad.myblog.register.ReadMoreActivity
import com.bumptech.glide.Glide

class BlogAdapter(private val items:List<BlogItemModel>): RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

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

    class BlogViewHolder(private val binding:BlogItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItemModel: BlogItemModel) {
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
        }

    }
}