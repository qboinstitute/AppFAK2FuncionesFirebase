package com.qbo.appfak2funcionesfirebase.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qbo.appfak2funcionesfirebase.model.Imagen

class ImagenAdapter(private var lstimagenes: List<Imagen>,
                    private val context: Context)
    :RecyclerView.Adapter<ImagenAdapter.ViewHolder>()

{
    class ViewHolder(itemView : View)
        : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ImagenAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}