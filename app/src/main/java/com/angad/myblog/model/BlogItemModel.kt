package com.angad.myblog.model

import android.os.Parcel
import android.os.Parcelable

data class BlogItemModel(
    val heading: String? = "null",
    val userName: String? = "null",
    val date: String? = "null",
    val post: String? = "null",
    var likeCount: Int = 0,
    val profileImage: String? = "null",
    var postId: String = "null",
    var isSaved: Boolean = false,
    val likedBy: MutableList<String>? = null
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()?: "null",
        parcel.readByte() != 0.toByte(),
        parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(heading)
        parcel.writeString(userName)
        parcel.writeString(date)
        parcel.writeString(post)
        parcel.writeInt(likeCount)
        parcel.writeString(profileImage)
        parcel.writeString(postId)
        parcel.writeStringList(likedBy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogItemModel> {
        override fun createFromParcel(parcel: Parcel): BlogItemModel {
            return BlogItemModel(parcel)
        }

        override fun newArray(size: Int): Array<BlogItemModel?> {
            return arrayOfNulls(size)
        }
    }

}
