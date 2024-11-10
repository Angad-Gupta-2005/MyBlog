package com.angad.myblog.model

data class BlogItemModel(
    val heading: String = "null",
    val userName: String = "null",
    val date: String = "null",
    val post: String = "null",
    val likeCount: Int = 0,
    val profileImage: String = "null"
)
