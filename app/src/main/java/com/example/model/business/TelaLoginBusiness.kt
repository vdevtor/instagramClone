package com.example.model.business

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.utils.Constans.Companion.isOkayLogin

class TelaLoginBusiness {
    fun valida(email: EditText?, senha: EditText?) : Boolean{

        email?.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isOkayLogin = android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()
            }

            override fun afterTextChanged(s: Editable?) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                    isOkayLogin = true
                }else{
                    email.setError("Digite um email valido")
                    isOkayLogin = false
                }
            }

        })

        senha?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               if (!senha.text.toString().isNullOrEmpty()){
                   isOkayLogin =true
               }else{
                   isOkayLogin = false
                   senha.setError("Digite Sua Senha")
               }
            }

            override fun afterTextChanged(s: Editable?) {
                if (!senha.text.toString().isNullOrEmpty()){
                    isOkayLogin =true
                }else{
                    isOkayLogin = false
                    senha.setError("Digite Sua Senha")
                }
            }

        })
        return isOkayLogin

    }
}