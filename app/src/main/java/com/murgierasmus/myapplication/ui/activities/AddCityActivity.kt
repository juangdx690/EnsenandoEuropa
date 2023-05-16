package com.murgierasmus.myapplication.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.core.RetrofitHelper
import com.murgierasmus.myapplication.data.network.CiudadesApiService
import com.murgierasmus.myapplication.databinding.ActivityAddCityBinding
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.dataclass.DatosCiudadResponse
import com.murgierasmus.myapplication.dataclass.ImagenesSubir
import com.murgierasmus.myapplication.ui.Maps.AniadirUbicacion
import com.murgierasmus.myapplication.ui.fragments.*
import com.murgierasmus.myapplication.utils.methods.Methods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCityActivity : AppCompatActivity() {

    private var allCiudades = mutableListOf<DatosCiudad2Item>()
    private var methods: Methods = Methods(this)
    var datosCiudad: DatosCiudad2Item = DatosCiudad2Item(
        0,
        "", "", "", "", "", "", "",
        "", "", "", "", "", "", "", "", "", "", 0.0, 0.0, "",
    )
    var imagenesCiudad: ImagenesSubir = ImagenesSubir(
        byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf()
    )
    private lateinit var contenedor: FrameLayout
    private lateinit var binding: ActivityAddCityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        numeroPosicion()
        contenedor = binding.contenedorAgregar
        methods.replaceFragmentAdd(RegistrarCiudad1Fragment(), supportFragmentManager)

    }

    override fun onBackPressed() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.contenedorAgregar)
        if (fragment is RegistrarCiudad1Fragment
        ) {
            super.onBackPressed()
            finish()
        }
        if (fragment is RegistrarCiudad2Fragment
        ) {
            methods.replaceFragmentAdd(RegistrarCiudad1Fragment(),supportFragmentManager)
        }
        if (fragment is RegistrarCiudad3Fragment
        ) {
            methods.replaceFragmentAdd(RegistrarCiudad2Fragment(),supportFragmentManager)
        }
        if (fragment is RegistrarCiudad4Fragment
        ) {
            methods.replaceFragmentAdd(RegistrarCiudad3Fragment(),supportFragmentManager)
        }
        if (fragment is RegistrarCiudad5Fragment
        ) {
            methods.replaceFragmentAdd(RegistrarCiudad4Fragment(),supportFragmentManager)
        }
        if (fragment is RegistrarCiudad6Fragment
        ) {
            methods.replaceFragmentAdd(RegistrarCiudad5Fragment(),supportFragmentManager)
        }

    }
    fun enviarDatos() {
        datosCiudad.id = obtenerId()
        Log.i("CAMPOS CIUDAD", datosCiudad.toString())
        val intent = Intent(this, AniadirUbicacion::class.java).apply {
            putExtra("DATA", datosCiudad)
            putExtra("DATA2", imagenesCiudad)
            putExtra("POSICION", allCiudades.size)
        }
        startActivity(intent)
    }

    private fun obtenerId(): Int {
        if (allCiudades.isEmpty()) {
            return 0
        } else {
            return allCiudades.size
        }
    }

    fun numeroPosicion() {
        val retrofit = RetrofitHelper.getRetrofit()
        val myApi = retrofit.create(CiudadesApiService::class.java)

        myApi.getCiudades("Ciudades.json/").enqueue(object : Callback<DatosCiudadResponse> {
            override fun onResponse(
                call: Call<DatosCiudadResponse>,
                response: Response<DatosCiudadResponse>
            ) {
                val cities = response.body()?.ciudades

                if (cities != null) {
                    Log.i("CAMPOS", allCiudades.size.toString() + " inical")
                    allCiudades.addAll(cities)
                    Log.i("CAMPOS", allCiudades.size.toString() + " final")
                } else {
                    Log.i("CAMPOS", " null")
                }


            }

            override fun onFailure(call: Call<DatosCiudadResponse>, t: Throwable) {
                Log.e("MainActivity", "Error al cargar los datos de la API", t)
            }
        })


    }


}