package com.example.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.fragments.ProfileFragment
import com.example.instagramclone.R
import com.example.instagramclone.databinding.ActivityAccountSettingsBinding
import com.example.model.User
import com.example.utils.Constans.Companion.alterado
import com.example.utils.Constans.Companion.alteradoPic
import com.example.utils.Constans.Companion.estiveaqui
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlin.coroutines.Continuation

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding
    private val firebaseAuth by lazy {
        Firebase.auth
    }
    private val db by lazy {
        Firebase.firestore.collection("users")
    }
    private lateinit var name: String
    private lateinit var usersame: String
    private lateinit var bio: String
    private var storageProfilePicRef: StorageReference? = null
    private var myUrl = ""
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alterado = false
        alteradoPic = false

        storageProfilePicRef = FirebaseStorage.getInstance()?.reference.child("Profile Pictures")

        binding.fullNameProfileFragment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.fullNameProfileFragment.text.toString().isNullOrEmpty()) {

                } else {
                    name = binding.fullNameProfileFragment.text.toString().toLowerCase()
                    alterado = true
                }
            }
        })

        binding.bioProfileFrag.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.bioProfileFrag.text.toString().isNullOrEmpty()) {

                } else {
                    bio = binding.bioProfileFrag.text.toString()
                    alterado = true
                }
            }

        })

        binding.usernameProfileFrag.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.usernameProfileFrag.text.toString().isNullOrEmpty()) {

                } else {
                    usersame = binding.usernameProfileFrag.text.toString().toLowerCase()
                    alterado = true
                }
            }

        })

        binding.changeImageTextBtn.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@AccountSettingsActivity)
        }

        binding.saveInfoProfileBtn.setOnClickListener {

            if (alteradoPic && !alterado) {
                uploadImage()
                estiveaqui = true


            } else if (alterado && alteradoPic) {
                uploadImage()
                updateInfo(name, usersame, bio)
                estiveaqui = true

            } else {
                updateInfo(name, usersame, bio)
                val intent = Intent(this, Main2Activity::class.java)
                estiveaqui = true
                startActivity(intent)
            }
        }
        binding.logoutBtnProfileFrag.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, TelaLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.closeProfileBtn.setOnClickListener {
            val intent = Intent(this, Main2Activity::class.java)
            estiveaqui = true
            startActivity(intent)
        }
        userInfo()
    }


    private fun uploadImage() {
        val userUid = firebaseAuth.currentUser?.uid
        var pd = ProgressDialog(this)
        pd.setTitle("Carregando")
        pd.show()
        val fileRef = storageProfilePicRef?.child("$userUid.jpg")
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
                db.document(userUsing?:"").update("image",myUrl)
                val intent = Intent(this, Main2Activity::class.java)
                startActivity(intent)

            } else {

            }
        }





    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            binding.profileImageViewSettings.setImageURI(imageUri)
            alteradoPic =true

        }
}
    private fun userInfo() {
        val userUsing = firebaseAuth.currentUser?.uid
        val followingRef1 = userUsing?.let { db.document(it) }

        followingRef1?.get()?.addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.let {
                    var user = it?.toObject<User>()
                    binding.fullNameProfileFragment.setText(user?.name_completo)
                    binding.usernameProfileFrag.setText(user?.username)
                    binding.bioProfileFrag.setText(user?.bio)
                    Glide.with(this).load(user?.image).into(binding.profileImageViewSettings)
                }
            }
        }
    }

    private fun updateInfo(name: String,username: String,bio:String){
    var userUsing = firebaseAuth.currentUser
        if (userUsing != null) {
            db.document(userUsing.uid).update("name_completo",name)
            db.document(userUsing.uid).update("bio",bio)
            db.document(userUsing.uid).update("username",username)

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,Main2Activity::class.java)
        estiveaqui = true
        startActivity(intent)


    }
}