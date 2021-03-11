package com.example.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fragments.DisplayPhotoFragment
import com.example.fragments.ProfileFragment
import com.example.instagramclone.R
import com.example.instagramclone.databinding.PostsLayoutBinding
import com.example.instagramclone.databinding.PostsProfileBinding
import com.example.model.Post

class PostProfileAdapter (private val mContex: Context,
private val mPost: List<Post>,
) : RecyclerView.Adapter<PostProfileAdapter.ViewHolder>() {
    companion object{
        public lateinit var fm : FragmentManager
    }

    class ViewHolder(private val binding: PostsProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
            var photo : ImageView = binding.photoProfile
    }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PostProfileAdapter.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PostsProfileBinding.inflate(layoutInflater, parent, false)
            return PostProfileAdapter.ViewHolder(binding)

        }

        override fun onBindViewHolder(holder: PostProfileAdapter.ViewHolder, position: Int) {
            var post = mPost[position]
            Glide.with(mContex).load(post.postimage).into(holder.photo)
            var bundle = Bundle()
            bundle.putString("link",post.postimage)
            var fragment = DisplayPhotoFragment()
            fragment.arguments = bundle
            holder.photo.setOnClickListener {
                (mContex as FragmentActivity).apply {
                    fm = supportFragmentManager
                }
                    fm.beginTransaction().add(
                    R.id.fragment_container,
                    fragment
                ).addToBackStack(null)
                    .commit()
            }

        }

        override fun getItemCount(): Int {
            return mPost.size
        }
    }

