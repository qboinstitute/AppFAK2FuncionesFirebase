package com.qbo.appfak2funcionesfirebase

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //1. Autenticación Firebase con email y password
        btnloginfirebase.setOnClickListener { vista->
            pblogin.visibility = View.VISIBLE
            if(etemail.text?.isNotEmpty()!!
                && etpassword.text?.isNotEmpty()!!){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(etemail.text.toString(),
                        etpassword.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            guardarPreferenciaIrAlHome(
                                it.result?.user?.email?: "",
                                TipoAutenticacion.FIREBASE.name,
                                "", ""
                            )
                        }else{
                            enviarMensaje(vista,
                                "Error en la autenticación a Firebase")
                            pblogin.visibility = View.GONE
                        }
                    }
            }else{
                enviarMensaje(vista, "Ingrese su email y password")
                pblogin.visibility = View.GONE
            }
        }
        //2. Autenticación Firebase con Google
        btnlogingoogle.setOnClickListener {
            pblogin.visibility = View.VISIBLE
            val configLogin = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val cliente : GoogleSignInClient = GoogleSignIn.getClient(
                this, configLogin
            )
            startActivityForResult(cliente.signInIntent, 777)
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 777){
            val task : Task<GoogleSignInAccount> = GoogleSignIn
                .getSignedInAccountFromIntent(data)
            try{
                val cuenta : GoogleSignInAccount? = task
                    .getResult(ApiException::class.java)
                if(cuenta != null){
                    val credencial : AuthCredential = GoogleAuthProvider
                        .getCredential(cuenta.idToken, null)
                    FirebaseAuth.getInstance()
                        .signInWithCredential(credencial)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                guardarPreferenciaIrAlHome(
                                    cuenta.email.toString(),
                                    TipoAutenticacion.GOOGLE.name,
                                    cuenta.displayName.toString(),
                                    cuenta.photoUrl.toString()
                                )
                            }else{
                                enviarMensaje(obtenerVista(), getString(R.string.errorLoginGoogle))
                                pblogin.visibility = View.GONE
                            }
                        }
                }
            }catch (e: ApiException){
                enviarMensaje(obtenerVista(), getString(R.string.errorLoginGoogle))
                pblogin.visibility = View.GONE
            }
        }
    }

    private fun guardarPreferenciaIrAlHome(
        email: String, tipo: String,
        nombre: String, urlimagen: String
    ){
        val preferencia : SharedPreferences.Editor =
            getSharedPreferences("appFirebaseQBO",
                Context.MODE_PRIVATE).edit()
        preferencia.putString("email", email)
        preferencia.putString("tipo", tipo)
        preferencia.putString("nombre", nombre)
        preferencia.putString("urlimagen", urlimagen)
        preferencia.apply()
        pblogin.visibility = View.GONE
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun obtenerVista(): View{
        return findViewById(android.R.id.content)
    }
    private fun enviarMensaje(vista: View, mensaje: String){
        Snackbar.make(vista, mensaje, Snackbar.LENGTH_LONG).show()
    }
}