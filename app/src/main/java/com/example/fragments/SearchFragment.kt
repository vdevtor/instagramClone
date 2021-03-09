package com.example.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapter.UserAdapter
import com.example.instagramclone.databinding.FragmentSearchBinding
import com.example.model.User
import com.example.view.viewmodel.SearchViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class SearchFragment : Fragment(),OnClickListenerInterface {
    private var binding: FragmentSearchBinding? = null
    private lateinit var viewModel: SearchViewModel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference: CollectionReference = db.collection("users")
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        binding?.recyclerVieSearch?.setHasFixedSize(false)
        mUser = ArrayList()
        searchUser()


//        userAdapter = context?.let {
//            UserAdapter(it, mUser as ArrayList<User>, true)}
//            recyclerView?.adapter = userAdapter


    }

    private fun searchUser() {

        val searchView: SearchView? = binding?.searchFragmentIcon
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.length >= 2) {
                    viewModel.setQuery(query.toLowerCase())
                    viewModel.getResult(viewModel.getQuery())
                    LoadRecyclerView()
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.setQuery(newText)
                    viewModel.getResult(viewModel.getQuery())
                    LoadRecyclerView()


                return true
            }
        })

        //*****************************************************
    }

    private fun retrieveUsers() {
        db.collection("users").get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (binding?.searchEditText?.text.toString() == "") {
                        mUser?.clear()
                        userAdapter?.notifyDataSetChanged()
                        for (document in task.result!!) {
                            val user = document.toObject<User>()
                            if (user != null) {
                                mUser?.add(user)

                            }
                        }
                        userAdapter?.notifyDataSetChanged()

                    }
                }
            })


    }

    fun LoadRecyclerView() {

        viewModel.onResultSearch.observe(this, {

            it?.let {

                    binding?.recyclerVieSearch?.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = it.userList?.let { it1 -> UserAdapter(context, it1) }

                    }
                }

        })

    }

    override fun onItemClicked(documentSnapshot: DocumentSnapshot, position: Int) {

    }


}