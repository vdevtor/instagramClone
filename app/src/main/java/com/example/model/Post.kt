package com.example.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    var description : String = "",
    var postid : String = "",
    var postimage : String = "",
    var publisher : String = ""

):Parcelable{

}