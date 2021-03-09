package com.example.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fragments.ProfileFragment
import com.example.instagramclone.R
import com.example.instagramclone.databinding.UserItemLayoutBinding
import com.example.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private var mContext:Context,
private var mUser: List<User>,
private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    private val firebaseAuth by lazy {
        Firebase.auth
    }
    private val db by lazy{
        Firebase.firestore.collection("users")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = UserItemLayoutBinding.inflate(layoutInflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        val userUsing = firebaseAuth.currentUser?.uid
        checkingFollowingStatus(user.uid,holder.followButton)
        holder.userNameTextView.text = user.username
        holder.userFullNameTextView.text = user.name_completo
        Glide.with(this.mContext).load(user.image).placeholder(R.drawable.profile)
            .into(holder.userImageProfile)


       holder.itemView.setOnClickListener(View.OnClickListener {
           val pref = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
           pref.putString("profileId",user.uid)
           pref.apply()
           (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,ProfileFragment())
               .commit()
       })



        holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString() == "seguir") {
                user?.uid.let { it1 ->
                    if (userUsing != null) {
                       // var se7 = hashMapOf("id" to userUsing)
                        db.document(userUsing).collection("seguindo").document(it1.toString())
                            .set(user).addOnCompleteListener { task ->
                                checkingFollowingStatus(user.uid,holder.followButton)
                            }
                        //Pegando dados user atual

                        db.document(userUsing).get().addOnSuccessListener {
                            var userAtual = it.toObject<User>()

                            if (userAtual != null) {
                                db.document(user.uid).collection("seguidores")
                                    .document(userUsing).set(userAtual)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            checkingFollowingStatus(user.uid,holder.followButton)

                                        }
                                    }
                            }
                        }

                        }

                }
            } else {

                user?.uid.let { it1 ->
                    if (userUsing != null) {
                        db.document(userUsing).collection("seguindo").document(it1.toString())
                            .delete().addOnCompleteListener { task->
                                checkingFollowingStatus(user.uid,holder.followButton)

                            }
                        db.document(user.uid).collection("seguidores").document(userUsing).delete()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    checkingFollowingStatus(user.uid,holder.followButton)
                                }
                            }
                    }

                }

            }
            }

    }

    private fun checkingFollowingStatus(uid: String, followButton: AppCompatButton) {
        val userUsing = firebaseAuth.currentUser?.uid
        val followingRef1 = userUsing?.let { db.document(it).collection("seguindo").document(uid).get() }

        followingRef1?.addOnCompleteListener {
          it.result.let {snapshot ->
              if (snapshot != null) {
                  if (snapshot.exists()){
                      followButton.text = "seguindo"
                  } else{
                      followButton.text = "seguir"
                  }
              }
          }

        }


    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    class ViewHolder(private val binding:UserItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        var userNameTextView : TextView = binding.userNameSearch
        var userFullNameTextView : TextView = binding.userFullNameSearch
        var userImageProfile : CircleImageView = binding.userProfileImageSearch
        var followButton : AppCompatButton = binding.followBtnSearch

    }


}