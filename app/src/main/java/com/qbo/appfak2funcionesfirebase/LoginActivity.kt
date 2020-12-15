package com.qbo.appfak2funcionesfirebase

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import java.io.IOException
import java.math.BigInteger
import java.security.SecureRandom

class LoginActivity : AppCompatActivity() {

    private val callbackManager = CallbackManager.Factory.create()
    private val aleatorio: SecureRandom = SecureRandom()
    private val URL_CALLBACK = "qbogit://git.oauth2token"


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
        //3. Autenticación Firebase con Facebook
        btnloginfacebook.setOnClickListener {
            pblogin.visibility = View.VISIBLE
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email")
            )
            LoginManager.getInstance().registerCallback(callbackManager,
            object: FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    result?.let {
                        val token = it.accessToken
                        val credencial: AuthCredential = FacebookAuthProvider
                            .getCredential(token.token)
                        FirebaseAuth.getInstance().signInWithCredential(credencial)
                            .addOnCompleteListener { resultado->
                                if(resultado.isSuccessful){
                                    guardarPreferenciaIrAlHome(
                                        resultado.result?.user?.email.toString(),
                                        TipoAutenticacion.FACEBOOK.name,
                                        resultado.result?.user?.displayName.toString(),
                                        resultado.result?.user?.photoUrl.toString()
                                    )
                                }else{
                                    enviarMensaje(obtenerVista(),
                                    getString(R.string.valerrorloginfb2))
                                }
                            }
                    }
                }
                override fun onCancel() {
                    enviarMensaje(obtenerVista(), getString(R.string.valcancelloginfb))
                }
                override fun onError(error: FacebookException?) {
                    enviarMensaje(obtenerVista(), getString(R.string.valerrorloginfb))
                }
            })
        }
        //4. Autenticación a Firebase con GitHub
        btnlogingithub.setOnClickListener {
            pblogin.visibility = View.GONE
            val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host("github.com")
                .addPathSegment("login")
                .addPathSegment("oauth")
                .addPathSegment("authorize")
                .addQueryParameter("client_id", "48f22557f074b249bdac")
                .addQueryParameter("redirect_uri", URL_CALLBACK)
                .addQueryParameter("state", getRandomString())
                .addQueryParameter("scope", "user:email")
                .build()
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse(httpUrl.toString()))
            startActivity(intent)
        }
        val uri = intent.data
        if(uri != null && uri.toString().startsWith(URL_CALLBACK)){
            val codigo = uri.getQueryParameter("code")
            val estado = uri.getQueryParameter("state")
            if (codigo != null && estado != null){
                obtenerCredencial(codigo, estado)
            }
        }

    }

    private fun obtenerCredencial(codigo: String, estado: String) {
        val okhttpclient = OkHttpClient()
        val mensaje = FormBody.Builder()
            .add("client_id", "48f22557f074b249bdac")
            .add("client_secret", "990dc71cd1b981ee3dfa35dc191e8b028dd3a346")
            .add("code", codigo)
            .add("redirect_uri", URL_CALLBACK)
            .add("state", estado)
            .build()
        val peticion = Request.Builder()
            .url("https://github.com/login/oauth/access_token")
            .post(mensaje)
            .build()
        okhttpclient.newCall(peticion)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    enviarMensaje(obtenerVista(), "Error en la autenticación")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody: String = response.body!!.string()
                    val token = responseBody.split("=|&".toRegex()).toTypedArray()
                    if(token[0].equals("access_token", ignoreCase = true)){
                        obtenerInformacionGitHubToken(token[1])
                    }else{
                        enviarMensaje(obtenerVista(), "Canceló la autenticación GitHub")
                    }
                }
            })

    }

    private fun obtenerInformacionGitHubToken(token: String) {
        val credencial = GithubAuthProvider.getCredential(token)
        FirebaseAuth.getInstance().signInWithCredential(credencial)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    guardarPreferenciaIrAlHome(
                        it.result?.additionalUserInfo?.username.toString(),
                        TipoAutenticacion.GITHUB.name,
                        it.result?.user?.displayName.toString(),
                        it.result?.user?.photoUrl.toString()
                    )
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode, data)
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

    private fun getRandomString(): String{
        return BigInteger(130, aleatorio).toString(32)
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