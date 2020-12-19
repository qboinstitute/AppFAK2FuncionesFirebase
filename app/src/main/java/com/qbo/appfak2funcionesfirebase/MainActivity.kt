package com.qbo.appfak2funcionesfirebase

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView: NavigationView
    var tipo :String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_foto, R.id.nav_galeria, R.id.nav_registro_persona,
        R.id.nav_lista_persona), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        mostrarInformacionLogin()
    }

    private fun mostrarInformacionLogin(){
        val preferencia : SharedPreferences =
            getSharedPreferences("appFirebaseQBO", Context.MODE_PRIVATE)
        val email = preferencia.getString("email","").toString()
        tipo = preferencia.getString("tipo","").toString()
        val nombre = preferencia.getString("nombre","").toString()
        val urlimagen = preferencia.getString("urlimagen","").toString()
        val tvnombreusuario : TextView = navView.getHeaderView(0)
            .findViewById(R.id.tvnombreusuario)
        val tvemailusuario : TextView = navView.getHeaderView(0)
            .findViewById(R.id.tvemailusuario)
        val ivusuario : ImageView = navView.getHeaderView(0)
            .findViewById(R.id.ivusuario)

        tvnombreusuario.text = nombre
        tvemailusuario.text = email
        if(tipo != TipoAutenticacion.FIREBASE.name){
            Picasso.get().load(urlimagen).into(ivusuario)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemsalir = item.itemId
        if(itemsalir == R.id.action_salir)
        {
            val preferencias : SharedPreferences.Editor =
                getSharedPreferences("appFirebaseQBO",
                    Context.MODE_PRIVATE).edit()
            preferencias.clear()
            preferencias.apply()
            if(tipo == TipoAutenticacion.FACEBOOK.name){
                LoginManager.getInstance().logOut()
            }
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}