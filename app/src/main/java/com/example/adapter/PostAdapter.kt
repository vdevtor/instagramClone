package com.example.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fragments.ProfileFragment
import com.example.instagramclone.R
import com.example.instagramclone.databinding.PostsLayoutBinding
import com.example.model.Post
import com.example.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(
    private val mContex: Context,
    private val mPost: List<Post>,
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private val firebaseAuth by lazy {
        Firebase.auth
    }
    private val db by lazy {
        Firebase.firestore.collection("users")
    }
    private val db2 by lazy {
        Firebase.firestore.collection("Posts")
    }
    private var lAU: String = ""
    private var user_id : String = ""

    class ViewHolder(private val binding: PostsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var profileImage: CircleImageView = binding.userProfileImageSearch
        var postImage: ImageView = binding.postImageHome
        var likeButton: ImageView = binding.postImageLikeBtn
        var commentButton: ImageView = binding.postImageCommentBtn
        var saveButton: ImageView = binding.postSaveCommentBtn
        var userName: TextView = binding.userNameSearch
        var likes: TextView = binding.likes
        var publisher: TextView = binding.publisher
        var description: TextView = binding.description

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PostsLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mPost[position]
        val userUsing = firebaseAuth.currentUser?.uid
        Glide.with(this.mContex).load(post.postimage).into(holder.postImage)
        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.publisher)
        getDescriptions(holder.description, post.publisher)
        checkLikesCount(holder.likeButton, holder.likes, post.postid)
        checkLikesBtn(holder.likeButton, holder.likes, post.postid)

        holder.profileImage.setOnClickListener{

            val users = db.document(post.publisher)
            users.get().addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject<User>()

                    val pref = mContex.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                    pref.putString("profileId",user?.uid)
                    pref.apply()
                    (mContex as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                        ProfileFragment()
                    )
                        .commit()
                }

                }
            }



        holder.likeButton.setOnClickListener {
            var ref = db2.whereEqualTo("postid", post.postid)
            ref.get().addOnSuccessListener {
                for (document in it) {
                    var aux = document.reference.collection("Likes")
                    aux.whereEqualTo("likedby", userUsing).get().addOnSuccessListener {
                        if (it.isEmpty || it.count() <= 0) {
                            addLike(post.postid, holder.likeButton)
                            checkLikesBtn(holder.likeButton, holder.likes, post.postid)
                            checkLikesCount(holder.likeButton, holder.likes, post.postid)
                        } else if (!it.isEmpty) {
                            for (document2 in it) {
                                var aux1 = document2.get("postid")
                                var aux2 = document2.get("likedby")
                                if (aux1 == post.postid) {
                                    unlike(holder.likeButton, holder.likes, post.postid)
                                    checkLikesBtn(holder.likeButton, holder.likes, post.postid)
                                    checkLikesCount(holder.likeButton, holder.likes, post.postid)

                                } else {
                                    Toast.makeText(mContex, "Hulk", Toast.LENGTH_SHORT).show()

                                }
                            }

                        }
                    }.addOnFailureListener {
                        Toast.makeText(mContex, "Hulk", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }



    }

    private fun checkLikesCount(likeBtn: ImageView, likes: TextView, postId: String) {
        val userUsing = firebaseAuth.currentUser?.uid
        var post = db2.whereEqualTo("postid", postId)
        post.get().addOnSuccessListener {
            if (!it.isEmpty) {
                for (document in it) {
                    var like = document.reference.collection("Likes").get()
                    like.addOnSuccessListener {
                        if (it.count().toInt() > 0) {
                            likes.text = it.count().toString()
                        } else if (it.isEmpty) {
                            likes.text = null
                        }
                    }


                }
            }

        }
    }
    private fun checkLikesBtn(likeBtn: ImageView, likes: TextView, postId: String) {
        val userUsing = firebaseAuth.currentUser?.uid

        var post = db2.whereEqualTo("postid", postId)
        post.get().addOnSuccessListener {
            if (!it.isEmpty) {
                for (document in it) {
                    var like = document.reference.collection("Likes").whereEqualTo(
                        "likedby", userUsing
                    )
                    like.get().addOnSuccessListener {
                        for (document2 in it) {
                            var string = document2.get("likedby").toString()
                            if (it.contains(document2)) {
                                likeBtn.setImageResource(R.drawable.heart_clicked)
                                checkLikesCount(likeBtn, likes, postId)


                            } else {
                                likeBtn.setImageResource(R.drawable.heart_not_clicked)
                                this.lAU = "unliked"

                            }

                        }
                    }
                }

            }

        }


    }
    private fun unlike(likeBtn: ImageView, likes: TextView, postId: String) {
        val userUsing = firebaseAuth.currentUser?.uid
        var post = db2.whereEqualTo("postid", postId)
        post.get().addOnSuccessListener {
            if (!it.isEmpty) {
                for (document in it) {
                    var like = document.reference.collection("Likes").whereEqualTo(
                        "likedby", userUsing
                    )
                    like.get().addOnSuccessListener {
                        for (document2 in it) {
                            var string = document2.get("likedby").toString()
                            if (it.contains(document2)) {
                                document.reference.collection("Likes")
                                    .document(document2.id).delete().addOnSuccessListener {

                                        likeBtn.setImageResource(R.drawable.heart_not_clicked)
                                        checkLikesCount(likeBtn, likes, postId)
                                    }
                            } else if (!it.contains(document2)) {
                                likeBtn.setImageResource(R.drawable.heart_not_clicked)

                            }
                        }

                    }
                }
            }

        }

    }
    private fun addLike(postId: String, likeBtn: ImageView) {
        val userUsing = firebaseAuth.currentUser?.uid
        var likes = db2
        val posts = db2.whereEqualTo("postid", postId)
        posts.get().addOnSuccessListener {

            if (!it.isEmpty) {

                for (document in it) {
                    var postInside = document.toObject<Post>()
                    var idDoc = document.id

                    if (userUsing != null) {
                        var mapId = hashMapOf(
                            "likedby" to userUsing,
                            "postid" to postId,
                            "publisher" to postInside.publisher,
                            "postimage" to postInside.postimage,
                            "status" to "liked"

                        )
                        likes.document(idDoc).collection("Likes").document().set(mapId)


                    }
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return mPost.size
    }
    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String) {
        val users = db.document(publisherID)
        users.get().addOnSuccessListener {
            if (it.exists()) {
                val user = it.toObject<User>()
                Glide.with(mContex).load(user?.image).into(profileImage)
                userName.setText(user?.username)
                publisher.setText(user?.name_completo)


            }
        }
    }
    private fun getDescriptions(description: TextView, publisherID: String) {
        val posts = db2

        posts.get().addOnSuccessListener {
            if (it != null) {
                for (id in it) {
                    val post = id.toObject<Post>()
                    if (post.publisher == publisherID) {
                        description.setText(post.description)
                    }
                }
            }
        }

    }


}