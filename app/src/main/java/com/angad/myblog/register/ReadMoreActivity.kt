package com.angad.myblog.register

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angad.myblog.R
import com.angad.myblog.databinding.ActivityReadMoreBinding
import com.angad.myblog.model.BlogItemModel
import com.bumptech.glide.Glide

class ReadMoreActivity : AppCompatActivity() {

//    Creating an instance of binding
    private lateinit var binding: ActivityReadMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        Initialised the binding
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        Getting the data from the main activity
        val blogs = intent.getParcelableExtra<BlogItemModel>("blogItem")

        if (blogs != null){
        //    retrieve the data
            binding.titleText.text = blogs.heading
            binding.userName.text = blogs.userName
            binding.date.text = blogs.date
            binding.blogDescriptionTextView.text = blogs.post

        //    Changing the profile image
            val userImageUrl = blogs.profileImage
            Glide.with(this)
                .load(userImageUrl)
                .into(binding.profileImage)
        } else {
            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
        }
    }
}