package com.example.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var name_completo: String = "",
    var username: String = "",
    var email: String = "",
    var bio: String = "",
     var image: String = "",
     var uid: String = ""
):Parcelable {
    constructor(name_completo: String,username: String,email: String,
    bio: String,image: String) : this(name_completo,username,email,bio,image,uid = "")
}


