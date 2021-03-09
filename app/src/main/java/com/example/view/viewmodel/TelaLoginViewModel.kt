package com.example.view.viewmodel

import android.widget.EditText
import androidx.lifecycle.ViewModel
import com.example.model.business.TelaLoginBusiness

class TelaLoginViewModel: ViewModel() {
    private val telaloginbusiness by lazy {
        TelaLoginBusiness()
    }
    fun valida(email:EditText?,senha:EditText?):Boolean{
      return  telaloginbusiness.valida(email,senha)
    }
}