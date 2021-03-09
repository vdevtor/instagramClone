package com.example.view.viewmodel

import android.widget.EditText
import androidx.lifecycle.ViewModel
import com.example.model.business.CadastroBusiness

class CadastroViewModel: ViewModel() {
    private val cadastroBusiness by lazy {
        CadastroBusiness()
    }
     fun valida(nome: EditText?,username:EditText?,email:EditText,senha:EditText?): Boolean{
        return cadastroBusiness.valida(nome,username,email,senha)
    }
}