package com.qbo.appfak2funcionesfirebase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.qbo.appfak2funcionesfirebase.adapter.PersonaAdapter
import com.qbo.appfak2funcionesfirebase.model.Persona


class ListaPersonaFragment : Fragment() {

    private lateinit var rvfirestore : RecyclerView
    private lateinit var firestore : FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val vista: View = inflater.inflate(R.layout.fragment_lista_persona, container, false)
        rvfirestore = vista.findViewById(R.id.rvfirestore)
        val lstpersonas : ArrayList<Persona> = ArrayList()
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("Persona")
            .addSnapshotListener { snapshots, error ->
                if(error != null){
                    Log.e("ErrorListFirestore", error.message.toString())
                }
                for(doc in snapshots!!.documentChanges){
                    if(doc.type == DocumentChange.Type.ADDED){
                        lstpersonas.add(Persona(
                            doc.document.data["nombre"].toString(),
                            doc.document.data["apellido"].toString(),
                            doc.document.data["edad"].toString().toInt(),
                        ))
                    }
                }
                rvfirestore.adapter = PersonaAdapter(lstpersonas)
                rvfirestore.layoutManager = LinearLayoutManager(vista.context)
            }
        return vista
    }

}