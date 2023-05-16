package com.murgierasmus.myapplication.ui.adapters




import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.core.RetrofitHelper
import com.murgierasmus.myapplication.data.network.CiudadesApiService
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.ui.viewHolder.CiudadViewHolderUsuario
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CiudadAdapterUsuario(
    var listaCiudades: List<DatosCiudad2Item>

) :
    RecyclerView.Adapter<CiudadViewHolderUsuario>(),BorrarCiudad {


    fun setCiudades(ciudades: List<DatosCiudad2Item>) {
        listaCiudades = ciudades.filterNotNull()
        notifyDataSetChanged()
    }

    override fun onCiudadBorrada(id:Int) {

        val retrofit = RetrofitHelper.getRetrofit()
        val myApi = retrofit.create(CiudadesApiService::class.java)


        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        var nombre= listaCiudades[id].Nombre

        storageRef.listAll().addOnSuccessListener { listResult ->
            listResult.items.forEach { item ->
                if (item.name.contains(nombre)) {
                    item.delete().addOnSuccessListener {
                        Log.i("CAMPOS", "LA IMAGEN SE HA ELIMINADO CON EXITO")
                    }.addOnFailureListener {
                        Log.i("CAMPOS", "ERROR AL ELIMINAR LA IMAGEN: "+it.message)
                    }
                }
            }
        }
        myApi.deleteCiudad("Ciudades/Ciudad/"+id+".json").enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.i("City", "Ciudad borrada")
                notifyDataSetChanged()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                //no.visibility = View.VISIBLE
                //no.text="No hay peliculas"
                Log.e("Galindo", "Error al cargar los datos de la API", t)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CiudadViewHolderUsuario {
        val layoutInflater = LayoutInflater.from(parent.context)
        var database = Database()
        var isUserAdmin = false
        CoroutineScope(Dispatchers.IO).launch {
            // Realizar la operación de carga del tipo de usuario aquí
            val database = Database()
            isUserAdmin = database.tipoUser().equals("usuario")
        }

        return CiudadViewHolderUsuario(layoutInflater.inflate(R.layout.item_ciudad_usuario, parent, false))

    }

    override fun getItemCount(): Int = listaCiudades.size

    override fun onBindViewHolder(holder: CiudadViewHolderUsuario, position: Int) {

        val item = listaCiudades[position]
        holder.render(item, position)

    }

}
