package com.example.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.R
import com.example.instagramclone.databinding.PostsLayoutBinding
import com.example.instagramclone.databinding.UserItemLayoutBinding
import com.example.model.Post
import com.example.model.User
import com.example.utils.Constans.Companion.JaDeiLike
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(
    private val mContex: Context,
    private val mPost: List<Post>
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
    private var liked : Boolean = false
    private var r: Boolean = false

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
        var comments: TextView = binding.comments

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
        checkLikesCount(holder.likeButton,holder.likes,post.postid)
        checkLikesBtn(holder.likeButton, holder.likes, post.postid)
        checkDrawable(holder.likeButton)



        holder.likeButton.setOnClickListener {
            checkDrawable(holder.likeButton)
            if (checkLikesBtn(holder.likeButton, holder.likes, post.postid) && checkDrawable(holder.likeButton)){
                unlike(holder.likeButton, holder.likes, post.postid)
                checkLikesCount(holder.likeButton, holder.likes, post.postid)
                checkLikesBtn(holder.likeButton, holder.likes, post.postid)


            }

            else if (!checkLikesBtn(holder.likeButton, holder.likes, post.postid) || !checkDrawable(holder.likeButton)){
                checkLikesCount(holder.likeButton, holder.likes, post.postid)
                addLike(post.postid,holder.likeButton)
                checkLikesBtn(holder.likeButton, holder.likes, post.postid)


            }

            checkLikesCount(holder.likeButton, holder.likes, post.postid)
            checkLikesBtn(holder.likeButton, holder.likes, post.postid)

                                }




    }

    private fun checkLikesCount(likeBtn:ImageView,likes: TextView,postId:String) {
        val userUsing = firebaseAuth.currentUser?.uid
        var post = db2.whereEqualTo("postid",postId)
        post.get().addOnSuccessListener {
            if (!it.isEmpty){
                for (document in it){
                    var like = document.reference.collection("Likes").get()
                    like.addOnSuccessListener {
                        if (it.count().toInt() > 0) {
                            likes.text = it.count().toString()
                        }
                        else if (it.isEmpty){
                            likes.text = null
                    }
                }


            }
            }

        }
    }
    private fun checkLikesBtn(likeBtn:ImageView,likes: TextView,postId:String):Boolean{
        val userUsing = firebaseAuth.currentUser?.uid

        var post = db2.whereEqualTo("postid",postId)
        post.get().addOnSuccessListener {
            if (!it.isEmpty){
                for (document in it){
                    var like = document.reference.collection("Likes").whereEqualTo(
                        "likedby",userUsing
                    )
                    like.get().addOnSuccessListener {
                        for (document2 in it) {
                            var string = document2.get("likedby").toString()
                            if (it.contains(document2)) {
                                    likeBtn.setImageResource(R.drawable.heart_clicked)
                                    this.liked = true



                                } else {
                                likeBtn.setImageResource(R.drawable.heart_not_clicked)
                                this.liked = false
                                }

                            }
                        }
                    }

                }

            }


        return this.liked

    }

    private fun unlike(likeBtn:ImageView,likes: TextView,postId:String): Boolean {
        val userUsing = firebaseAuth.currentUser?.uid
        var post = db2.whereEqualTo("postid",postId)
        post.get().addOnSuccessListener {
            if (!it.isEmpty){
                for (document in it){
                    var like = document.reference.collection("Likes").whereEqualTo(
                        "likedby",userUsing
                    )
                    like.get().addOnSuccessListener {
                        for (document2 in it) {
                            var string = document2.get("likedby").toString()
                            if (it.contains(document2) && this.liked) {
                                    document.reference.collection("Likes")
                                        .document(document2.id).delete().addOnSuccessListener {
                                            this.liked = false
                                            likeBtn.setImageResource(R.drawable.heart_not_clicked)
                                            checkLikesCount(likeBtn,likes,postId)


                                        }
                                }
                                else if (!it.contains(document2)) {
                                    likeBtn.setImageResource(R.drawable.heart_not_clicked)
                                    this.liked = false
                                }
                            }

                        }
                }
            }

        }
        return this.liked
    }

    private fun addLike(postId: String,likeBtn: ImageView) {
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
                            "postimage" to postInside.postimage

                        )
                        likes.document(idDoc).collection("Likes").document().set(mapId)
                        this.liked = true
                        if (likeBtn.equals(R.drawable.heart_not_clicked)){
                            this.liked = false
                        }


                    }
                }
            }
        }
    }
    private fun checkDrawable(likebtn:ImageView): Boolean{

        if (likebtn.resources.equals(R.drawable.heart_not_clicked)){
            this.liked = false
            likebtn.setImageResource(R.drawable.heart_not_clicked)
           r = false
        }

        else if (likebtn.resources.equals(R.drawable.heart_clicked)){
            r = true
            likebtn.setImageResource(R.drawable.heart_clicked)
        }
        return r



    }

    override fun getItemCount(): Int {
        return mPost.size
    }
    private fun publisherInfo(
        profileImage: CircleImageView,
        userName: TextView,
        publisher: TextView,
        publisherID: String
    ) {
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