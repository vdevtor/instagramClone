package com.example.view

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.instagramclone.databinding.ActivityTelaLoginBinding
import com.example.view.viewmodel.TelaLoginViewModel
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TelaLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTelaLoginBinding
    private lateinit var viewModel: TelaLoginViewModel
    private lateinit var email: EditText
    private lateinit var senha: EditText
    private val firebaseAuth by lazy {
        Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(TelaLoginViewModel::class.java)
        email = binding.emailLogin
        senha = binding.passwordLogin


        if (firebaseAuth.currentUser?.uid != null){
            val intent = Intent(this@TelaLoginActivity,Main2Activity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signupLinkBtn.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
        var allSet = viewModel.valida(email,senha)
        binding.loginBtn.setOnClickListener {
          allSet = viewModel.valida(email,senha)
            if (allSet){
                loginWithEmailAndPassword(email.text.toString(),senha.text.toString())
            }else{
                allSet = viewModel.valida(email,senha)
                loginWithEmailAndPassword(email.text.toString(),senha.text.toString())
            }

        }

    }
    private fun loginWithEmailAndPassword(email:String,senha:String){
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logando")
        progressDialog.setMessage("Por favor Aguarde, pode levar um tempo")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email,senha).addOnCompleteListener { task ->
            if (task.isSuccessful){
                startActivity(Intent(this,Main2Activity::class.java))
                finish()
            }else{
                when(task.exception) {
                    is FirebaseAuthInvalidUserException -> {
                        Toast.makeText(this, "Usuario não cadastrado ou invalido", Toast.LENGTH_LONG).show()
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        Toast.makeText(this, "Senha ou Usuario Invalidos", Toast.LENGTH_LONG).show()
                    }
                }

                progressDialog.dismiss()
            }
        }
    }


}