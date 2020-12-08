package com.qbo.appfak2funcionesfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnloginfirebase.setOnClickListener { vista->
            if(etemail.text?.isNotEmpty()!!
                && etpassword.text?.isNotEmpty()!!){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(etemail.text.toString(),
                        etpassword.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            startActivity(Intent(this,
                                MainActivity::class.java))
                        }else{
                            enviarMensaje(vista,
                                "Error en la autenticación a Firebase")
                        }
                    }
            }else{
                enviarMensaje(vista, "Ingrese su email y password")
            }
        }

    }

    private fun enviarMensaje(vista: View, mensaje: String){
        Snackbar.make(vista, mensaje, Snackbar.LENGTH_LONG).show()
    }
}