package com.example.fragments

import com.google.firebase.firestore.DocumentSnapshot

interface OnClickListenerInterface {

    fun onItemClicked(documentSnapshot: DocumentSnapshot, position: Int)
}