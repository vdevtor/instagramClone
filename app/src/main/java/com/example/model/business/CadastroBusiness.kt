package com.example.model.business

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.utils.Constans.Companion.isokay

class CadastroBusiness {

    fun valida(nome: EditText?,username:EditText?,email:EditText?,senha:EditText?): Boolean{
        nome?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!nome.text.toString().isNullOrEmpty()){
                    isokay = true
                }else{
                    nome.setError("Preencha o Campo nome")
                    isokay=false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (!nome.text.toString().isNullOrEmpty()){
                    isokay = true
                }else{
                    nome.setError("Preencha o Campo nome")
                    isokay=false
                }
            }

        })

        username?.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!username?.text.toString().isNullOrEmpty()){
                    isokay = true
                }else{
                    username?.setError("Preencha o Campo Nome de Usuario")
                    isokay=false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (!nome?.text.toString().isNullOrEmpty()){
                    isokay = true
                }else{
                    nome?.setError("Preencha o Campo Nome de Usuario")
                    isokay =false
                }
            }

        })

        email?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                    isokay = true
                }else{

                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (
                    android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                    isokay = true
                }else{
                    email.setError("Use um email Valido")
                }
            }

        })

        senha?.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!senha.text.toString().isNullOrEmpty()){
                    isokay = true
                }else{
                    senha.setError("Preencha o Campo nome")
                    isokay = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (!senha.text.toString().isNullOrEmpty()){
                    isokay = true
                }else{
                    senha.setError("Preencha o Campo nome")
                    isokay=false
                }
            }

        })

        return isokay


    }

}