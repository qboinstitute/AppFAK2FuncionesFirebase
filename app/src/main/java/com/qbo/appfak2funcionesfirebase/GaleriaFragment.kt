package com.qbo.appfak2funcionesfirebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.qbo.appfak2funcionesfirebase.adapter.ImagenAdapter
import com.qbo.appfak2funcionesfirebase.model.Imagen
import kotlinx.android.synthetic.main.fragment_galeria.*


class GaleriaFragment : Fragment() {

    private lateinit var rvimagenes : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val vista : View
        = inflater.inflate(R.layout.fragment_galeria, container, false)
        rvimagenes = vista.findViewById(R.id.rvimagenes)
        ListarImagenes(vista)
        return vista
    }

    private fun ListarImagenes(vista: View) {
        val storage = FirebaseStorage.getInstance()
        val storageref = storage.reference.child("/imagenesqbo/")
        val lstimagenes : ArrayList<Imagen> = ArrayList()
        val listarTareas : Task<ListResult> = storageref.listAll()
        listarTareas.addOnCompleteListener { result->
            val items: List<StorageReference> = result.result!!.items
            items.forEachIndexed { index, item ->
                item.downloadUrl.addOnSuccessListener {
                    lstimagenes.add(Imagen(it.toString()))
                }.addOnCompleteListener {
                    rvimagenes.adapter = ImagenAdapter(lstimagenes,
                    vista.context)
                    rvimagenes.layoutManager = GridLayoutManager(vista.context,
                    2)
                }
            }
        }
    }

}