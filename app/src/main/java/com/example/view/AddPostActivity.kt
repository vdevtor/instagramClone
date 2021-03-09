package com.example.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.instagramclone.databinding.ActivityAddPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.theartofdev.edmodo.cropper.CropImage

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddPostBinding
    private var storagePostPicRef: StorageReference? = null
    private var myUrl = ""
    private var imageUri: Uri? = null
    private val firebaseAuth by lazy {
        Firebase.auth
    }
    private val db by lazy {
        Firebase.firestore.collection("users")
    }
    private val fireStorage by lazy {
        Firebase.firestore
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storagePostPicRef = FirebaseStorage.getInstance()?.reference.child("Posts Pictures")
        binding.saveAddPostBtn.setOnClickListener { uploadImage() }
        binding.closeAddPostBtn.setOnClickListener {
            startActivity(Intent(this@AddPostActivity,Main2Activity::class.java))
        }

        CropImage.activity()
            .setAspectRatio(1, 1)
            .start(this@AddPostActivity)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            binding.imagePost.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {
        when{
            imageUri == null -> Toast.makeText(this,"Por favor,Selecione uma imagem",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(binding.descriptionPost.text.toString()) -> Toast.makeText(this,"Por favor,Adicione uma Descrição",Toast.LENGTH_SHORT).show()
            else ->{
                var pd = ProgressDialog(this)
                pd.setTitle("Carregando Postagem")
                pd.show()
                val userUid = firebaseAuth.currentUser?.uid
                val fileRef = storagePostPicRef?.child(System.currentTimeMillis().toString() + ".jpg")
                val uploadTask: StorageTask<*>?
                uploadTask = fileRef?.putFile((imageUri ?: "") as Uri)?.addOnSuccessListener {

                    pd.dismiss()
                    Toast.makeText(this, "Envio Concluído", Toast.LENGTH_SHORT).show()
                }?.addOnFailureListener {
                    pd.dismiss()
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }?.addOnProgressListener {
                    var progress: Double = (100.0 * it.bytesTransferred) / it.totalByteCount
                    pd.setMessage("Enviado ${progress.toInt()}%")
                }
                uploadTask?.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    fileRef?.downloadUrl
                }?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        myUrl = downloadUri.toString()
                        val userUsing = firebaseAuth.currentUser?.uid
                        val ref = fireStorage.collection("Posts").document()
                        val postMap = hashMapOf(
                            "postid" to ref.toString(),
                            "description" to binding.descriptionPost.text.toString(),
                            "publisher" to userUsing,
                            "postimage" to myUrl
                        )
                        ref.set(postMap)
                        val intent = Intent(this, Main2Activity::class.java)
                        startActivity(intent)
                    }

                }
            }
        }

    }
}