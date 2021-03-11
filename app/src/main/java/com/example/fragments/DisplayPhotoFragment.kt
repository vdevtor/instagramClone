package com.example.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.adapter.PostProfileAdapter.Companion.fm

import com.example.instagramclone.R
import com.example.instagramclone.databinding.FragmentDisplayPhotoBinding
import com.example.instagramclone.databinding.FragmentProfileBinding


class DisplayPhotoFragment : Fragment() {
    private  var binding: FragmentDisplayPhotoBinding? = null

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            binding = FragmentDisplayPhotoBinding.inflate(layoutInflater,container,false)
            return binding?.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var link = arguments?.getString("link")
        context?.let { binding?.displayPhoto?.let { it1 -> Glide.with(it).load(link).into(it1) } }

        binding?.closeDisplayPhoto?.setOnClickListener {
            fm.apply {
                popBackStack()
            }
        }


    }


    }


