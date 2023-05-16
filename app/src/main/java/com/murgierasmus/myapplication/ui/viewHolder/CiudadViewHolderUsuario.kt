package com.murgierasmus.myapplication.ui.viewHolder

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.murgierasmus.myapplication.databinding.ItemCiudadUsuarioBinding
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.dataclass.Paises
import com.murgierasmus.myapplication.ui.activities.CiudadActivity

class CiudadViewHolderUsuario(view: View) : RecyclerView.ViewHolder(view) {


    init {
        itemView.visibility = View.GONE
    }

    val binding = ItemCiudadUsuarioBinding.bind(view)

    val ciudad = binding.tvNombreCiudad
    val fotoCiudad = binding.imgCiudad
    val pais = binding.imgPais
    val card = itemView

    fun render(datosCiudadModel: DatosCiudad2Item, position: Int) {
        ciudad.text = datosCiudadModel.Nombre
        Glide.with(pais.context)
            .load(datosCiudadModel.Foto)
            .into(fotoCiudad)
        //mostrar pais

        Log.i("NUMERO", datosCiudadModel.Nombre + " " + datosCiudadModel.id)

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val filename = "${datosCiudadModel.Foto}.png"
        val fileRef = storageRef.child(filename)

        fileRef.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()
            Glide.with(pais.context)
                .load(url)
                .override(150, 150)
                .centerCrop()
                .into(fotoCiudad)
            itemView.visibility = View.VISIBLE
        }.addOnFailureListener { exception ->
            // Maneja el error de obtener la URL de descarga
            Log.e("CAMPOS", "Error al obtener la URL de descarga del archivo: $exception")
        }

        var paises = Paises.paises
        var fotoPais = when (datosCiudadModel.Pais) {
            "España" -> paises.get(0).foto
            "Francia" -> paises.get(1).foto
            "Portugal" -> paises.get(2).foto
            "Alemania" -> paises.get(3).foto
            "Irlanda" -> paises.get(4).foto
            "Reino Unido" -> paises.get(5).foto
            "Italia" -> paises.get(6).foto
            else->paises.get(0).foto

        }
        Glide.with(pais.context)
            .load(fotoPais)
            .override(25, 25)
            .into(pais)


        card.setOnClickListener {
            val intent: Intent = Intent(card.context, CiudadActivity::class.java).apply {
                putExtra("datosCiudad", datosCiudadModel)
            }
            card.context.startActivity(intent)
        }


    }

    fun Pais(nombre: String): Paises? {
        val pais = Paises.paises.find { it.nombre == nombre }

        return pais
    }


}