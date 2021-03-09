package com.example.adapter

import android.os.Parcelable
import com.example.model.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserList(
    var userList: List<User>? = null

): Parcelable
