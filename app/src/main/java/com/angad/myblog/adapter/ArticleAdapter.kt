package com.angad.myblog.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.angad.myblog.databinding.ArticleItemBinding
import com.angad.myblog.model.BlogItemModel
import com.bumptech.glide.Glide
import java.util.ArrayList

class ArticleAdapter(
    private val context: Context,
    private var blogList: List<BlogItemModel>,
    private val itemClickListener: OnItemClickListener
): RecyclerView.Adapter<ArticleAdapter.BlogViewHolder>() {

//    Creating an interface
    interface OnItemClickListener{
        fun onClickReadMoreButton(blogItem: BlogItemModel)
        fun onClickEditButton(blogItem: BlogItemModel)
        fun onClickDeleteButton(blogItem: BlogItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val itemView = ArticleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = blogList[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(blogSavedList: ArrayList<BlogItemModel>) {
        this.blogList = blogSavedList
        notifyDataSetChanged()
    }

    //    View holder class
   inner class BlogViewHolder(private val binding: ArticleItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItem: BlogItemModel) {

            binding.heading.text = blogItem.heading
            Glide.with(binding.profileImage.context)
                .load(blogItem.profileImage)
                .into(binding.profileImage)

            binding.userName.text = blogItem.userName
            binding.date.text = blogItem.date
            binding.post.text = blogItem.post

        //    Read more button functionality
            binding.readMoreBtn.setOnClickListener {
                itemClickListener.onClickReadMoreButton(blogItem)
            }

        //    Edit button functionality
            binding.editButton.setOnClickListener {
                itemClickListener.onClickEditButton(blogItem)
            }

        //    Delete button functionality
            binding.deleteButton.setOnClickListener {
                itemClickListener.onClickDeleteButton(blogItem)
            }
        }

    }
}