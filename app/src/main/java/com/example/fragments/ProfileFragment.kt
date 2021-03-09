package com.example.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.instagramclone.R
import com.example.view.AccountSettingsActivity
import com.example.instagramclone.databinding.FragmentProfileBinding
import com.example.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {
    private  var binding: FragmentProfileBinding? = null
    private lateinit var profileId: String
    private  var userAux: User? = null
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var getButtonText : String
    private val firebaseAuth by lazy {
        Firebase.auth
    }
    private val userUsing = firebaseAuth.currentUser?.uid

    private val db by lazy{
        Firebase.firestore.collection("users")
    }
    private val db2: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference: CollectionReference = db2.collection("Posts")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseUser = firebaseAuth.currentUser
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none").toString()
        }
        if (profileId == firebaseUser?.uid) {
            binding?.editAccountSettingsBtn?.text = "Editar Perfil"
            getButtonText = binding?.editAccountSettingsBtn?.text.toString()


        } else if (profileId != firebaseUser?.uid) {
            checkingFollowingStatus(profileId)
        }
        var getU = getUser()
        userInfo()


        binding?.editAccountSettingsBtn?.setOnClickListener {
            var getU = getUser()
            if (getButtonText == "Editar Perfil") {
                startActivity(Intent(context, AccountSettingsActivity::class.java))
            }
            else if (getButtonText == "seguir") {

                getU?.let { it1 ->
                    if (userUsing != null) {
                        db.document(userUsing).collection("seguindo").document(profileId).set(
                            it1
                        ).addOnCompleteListener {
                            checkingFollowingStatus(profileId)
                            getFollowers()
                            getFollowing()
                            userInfo()

                        }
                    }
                    getUser2()?.let { it2 ->
                        if (userUsing != null) {
                            db.document(profileId).collection("seguidores").document(userUsing).set(
                                it2
                            )
                        }
                    }
                }

            }
            else if(getButtonText == "seguindo"){
                    val userUsing = firebaseAuth.currentUser?.uid
                    profileId?.let { it1 ->
                        if (userUsing != null) {
                            db.document(userUsing).collection("seguindo").document(it1.toString())
                                .delete().addOnCompleteListener { task->
                                    checkingFollowingStatus(profileId)
                                    getFollowers()
                                    getFollowing()
                                    userInfo()

                                }
                            db.document(profileId).collection("seguidores").document(userUsing).delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                }
                                }
                        }
                    }
                }
            }



        getFollowers()
        getFollowing()
        userInfo()
        getPosts()
    }

    private fun checkingFollowingStatus(uid: String) {
        val userUsing = firebaseAuth.currentUser?.uid
        val followingRef1 = userUsing?.let { db.document(it).collection("seguindo").document(uid).get() }

        followingRef1?.addOnCompleteListener {
            it.result.let { snapshot ->
                if (snapshot != null) {
                    if (snapshot.exists()) {
                        binding?.editAccountSettingsBtn?.text = "seguindo"
                        getButtonText = "seguindo"
                    } else {
                        binding?.editAccountSettingsBtn?.text = "seguir"
                        getButtonText = "seguir"
                    }
                } else {
                    binding?.editAccountSettingsBtn?.text = "seguir"
                    getButtonText = "seguir"
                }
            }

        }
    }
    private fun getUser(): User? {

        db.document(profileId).get().addOnCompleteListener {
            if (it.isSuccessful){
                it.result.let {
                     userAux = it?.toObject<User>()
                }
            }
        }
            return userAux
    }

    private fun getUser2(): User? {

        firebaseAuth.currentUser?.uid?.let {
            db.document(it).get().addOnCompleteListener {
                if (it.isSuccessful){
                    it.result.let {
                        userAux = it?.toObject<User>()
                    }
                }
            }
        }
        return userAux
    }
    private fun checkFollowAndFollowingButtonStatus() {
        val userUsing = firebaseAuth.currentUser?.uid
        val followingRef1 = userUsing?.let { db.document(it).collection("seguindo").document(profileId).get() }
        followingRef1?.addOnCompleteListener {
            it.result.let {snapshot ->
                if (snapshot != null) {
                    if (snapshot.exists()){
                        binding?.editAccountSettingsBtn?.text = "seguindo"
                        getButtonText = "seguindo"

                    } else{
                        binding?.editAccountSettingsBtn?.text = "seguir"
                        getButtonText = "seguir"
                    }
                }
            }

        }
    }
    private fun getFollowers(){
        val userUsing = profileId
        val followingRef1 = userUsing?.let { db.document(it).collection("seguidores").get() }


        followingRef1?.addOnCompleteListener {
            it.result.let {snapshot ->
                if (snapshot != null) {
                    if (!snapshot.isEmpty){
                        binding?.totalFollowers?.text = snapshot.count().toString()

                    } else{
                        binding?.totalFollowers?.text = snapshot.count().toString()

                    }
                }
            }

        }


    }
    private fun getFollowing(){
        val userUsing = profileId
        val followingRef1 = userUsing?.let { db.document(it).collection("seguindo").get() }

        followingRef1?.addOnCompleteListener {
            it.result.let {snapshot ->
                if (snapshot != null) {
                    if (!snapshot.isEmpty){
                        binding?.totalFollowing?.text = snapshot.count().toString()

                    } else{

                    }
                }
            }

        }


    }

    private fun userInfo(){
        val userUsing = firebaseAuth.currentUser?.uid
        val followingRef1 = db.document(profileId)


        followingRef1?.get()?.addOnCompleteListener {
            if (it.isSuccessful){
                it.result.let {
                    var user = it?.toObject<User>()
                    binding?.fullNameProfileFragment?.text = user?.name_completo
                    binding?.bioProfileFrag?.text = user?.bio
                    binding?.profileFragmentUsername?.text = user?.username
                    binding?.proImageProfileFragment?.let { it1 ->
                        Glide.with(this).load(user?.image).placeholder(R.drawable.profile).into(
                            it1
                        )
                    }
                }
            }
        }

    }
    private fun getPosts(){
        val userUsing = profileId
        var query1 = collectionReference.whereEqualTo("publisher",userUsing)



        query1.get()?.addOnSuccessListener {
            if (it.isEmpty) {
                binding?.totalPost?.text = it.count().toString()
            } else {
                for (document in it) {
                    binding?.totalPost?.text = it.count().toString()
                }

            }
        }
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser?.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser?.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser?.uid)
        pref?.apply()
    }



}