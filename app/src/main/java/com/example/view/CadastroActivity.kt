package com.example.view

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.instagramclone.databinding.ActivityCadastroBinding
import com.example.view.viewmodel.CadastroViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding
    private lateinit var viewModel: CadastroViewModel
    private lateinit var email : EditText
    private lateinit var senha : EditText
    private lateinit var nome : EditText
    private lateinit var username : EditText

    private val firebaseAuth by lazy{
        Firebase.auth
    }
    private val fireStorage by lazy {
        Firebase.firestore
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(CadastroViewModel::class.java)
        email = binding.emailCadastro
        senha = binding.passwordCadastro
        nome = binding.fullNameCadastro
        username = binding.usernameCadastro
        var allSet = viewModel.valida(nome,username,email,senha)

        binding.signinLinkBtn.setOnClickListener {
            startActivity(Intent(this, TelaLoginActivity::class.java))
        }

        binding.cadastrarBtn.setOnClickListener {
            var allSet = viewModel.valida(nome,username,email,senha)
            if (allSet)
            cadastrar(email.text.toString(),senha.text.toString(),
                nome.text.toString(),username.text.toString())
        }

    }
    private fun cadastrar(email: String, senha: String,nome:String,username:String) {
        val progressDialog = ProgressDialog(this@CadastroActivity)
        progressDialog.setTitle("Cadastro")
        progressDialog.setMessage("Por favor Aguarde, pode levar um tempo")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserInfo(nome,username,email)
                    val intent = Intent(this@CadastroActivity,Main2Activity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else{
                    val message = task.exception.toString()
                    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
                    firebaseAuth.signOut()
                    progressDialog.dismiss()
                }
            }
    }

    private fun saveUserInfo(nome:String,username:String,email:String) {
        val currentUserId = firebaseAuth.currentUser?.uid
        var user = hashMapOf(
            "name_completo" to nome.toLowerCase(),
            "username" to username.toLowerCase(),
            "email" to email,
            "uid" to currentUserId,
            "bio" to "Hey eu estou usando o Instacloene by vdevtor",
            "image" to "https://firebasestorage.googleapis.com/v0/b/instagramclone-c3e69.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=3a3887e3-91e1-4217-be2c-6c61179b1533"
        )

        fireStorage.collection("users").document(currentUserId?: "")
            .set(user).addOnSuccessListener{

            }

       fireStorage.collection("users").document(currentUserId?:"")
           .collection("seguindo").document(currentUserId?:"")
           .set(user)

    }
}