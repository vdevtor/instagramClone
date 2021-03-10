package com.example.view.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adapter.UserAdapter
import com.example.model.UserList
import com.example.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SearchViewModel: ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference: CollectionReference = db.collection("users")
    private  var mUserList : UserList? = null
    private  var mUser : MutableList<User>? = null
    var list : MutableList<User> = mutableListOf()
    private var userAdapter: UserAdapter? = null
    private val firestorage by lazy {
        Firebase.storage
    }
    private var query = ""
    val onResultSearch: MutableLiveData<UserList> = MutableLiveData()
    val onResultSearch2: MutableLiveData<User> = MutableLiveData()


    fun retrieveUser(){
            db.collection("users").get()
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mUser?.clear()
                        for (document in task.result!!) {
                            val user = document.toObject<User>()
                            mUser?.add(user)
                            mUserList?.userList = mUser as List<User>

                        }

                    }


                })

        }

    fun getResult(search: String) {
        list.clear()
    var query1 = collectionReference
        .whereGreaterThanOrEqualTo("username", search)
        .orderBy("username").startAt(search).endAt(search + "\uf8ff")

    query1.get().addOnSuccessListener {
        list.clear()
        onResultSearch.postValue(UserList(list))
        if (!search.isNullOrEmpty()) {
            for (document in it) {
                var user = document.toObject<User>()
                list.add(user)
                onResultSearch2.postValue(user)
                onResultSearch.postValue(UserList(list))
            }
        }



    }.addOnFailureListener {
        list.clear()
        onResultSearch.postValue(UserList(list))
        Log.e("fail","Falhou")
    }

}


    fun setQuery(newQuery: String) {
            this.query = newQuery


        }

        fun getQuery(): String {
            return query
        }

}