package com.example.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapter.PostAdapter
import com.example.instagramclone.databinding.FragmentHomeBinding
import com.example.model.Post
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
    private  var binding: FragmentHomeBinding? = null
    private  var firebaseUser: FirebaseUser? = null
    private val firebaseAuth by lazy {
        Firebase.auth
    }
    private val db2 by lazy{
        Firebase.firestore.collection("Posts")
    }




    private val db by lazy{
        Firebase.firestore.collection("users")
    }

    private var postAdapter : PostAdapter? = null
    private var postList : MutableList<Post>? = null
    private var followingList : MutableList<Post>? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseUser = firebaseAuth.currentUser

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser?.uid)
        pref?.apply()


        binding?.recyclerVieHome?.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.reverseLayout = true
            linearLayoutManager.stackFromEnd =true
            layoutManager =  linearLayoutManager

            postList = ArrayList()
            postAdapter = context?.let{
                PostAdapter(it,postList as ArrayList<Post>)
            }
            adapter = postAdapter
            checkingFollowing()


        }
    }

    private fun checkingFollowing() {
        followingList = ArrayList()
        val userUsing = firebaseAuth.currentUser?.uid
        val followingRef1 = userUsing?.let { db.document(it).collection("seguindo").get()}
        followingRef1?.addOnSuccessListener {
                if (it != null) {
                    (followingList as ArrayList<String>).clear()

                    for (documents in it) {

                        (followingList as ArrayList<String>).add(documents.id)
                    }
                }
            retrievePosts()
        }
    }

    private fun retrievePosts() {
        val ref = db2

        ref.addSnapshotListener { value, error ->
                postList?.clear()
            if (value != null) {
                if (!value.isEmpty)
                    for (document in value) {

                        val post = document.toObject<Post>()

                            for (id in followingList as ArrayList<String>){
                                if(post.publisher == id) {
                                    postList?.add(post)
                                    }
                            postAdapter?.notifyDataSetChanged()
                        }
            }
        }
        }
    }
}