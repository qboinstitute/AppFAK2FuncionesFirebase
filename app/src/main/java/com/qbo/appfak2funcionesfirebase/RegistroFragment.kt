package com.qbo.appfak2funcionesfirebase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore


class RegistroFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var etnombre: EditText
    private lateinit var etapellido: EditText
    private lateinit var etedad: EditText
    private lateinit var btnregistrarfirestore: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val vista: View = inflater.inflate(R.layout.fragment_registro, container, false)
        etnombre = vista.findViewById(R.id.etnombre)
        etapellido = vista.findViewById(R.id.etapellido)
        etedad = vista.findViewById(R.id.etedad)
        btnregistrarfirestore = vista.findViewById(R.id.btnregistrarfirestore)
        firestore = FirebaseFirestore.getInstance()
        btnregistrarfirestore.setOnClickListener {
            if(etnombre.text?.isNotEmpty()!! &&
                    etapellido.text?.isNotEmpty()!! &&
                    etedad.text?.isNotEmpty()!!){
                registrarPersona(it)
            }else{
                enviarMensaje(it, "Ingrese todos los datos")
            }
        }

        return vista
    }

    private fun registrarPersona(vista: View) {
        val persona = hashMapOf(
            "apellido" to etapellido.text.toString(),
            "edad" to etedad.text.toString(),
            "nombre" to etnombre.text.toString()
        )
        firestore.collection("Persona")
            .add(persona)
            .addOnSuccessListener { document->
                enviarMensaje(vista, "El id del registro es: ${document.id}")
            }
            .addOnFailureListener {
                Log.e("ErrorFirestore", it.message.toString())
            }
    }

    private fun enviarMensaje(vista: View, mensaje: String){
        Snackbar.make(vista, mensaje, Snackbar.LENGTH_LONG).show()
    }

}