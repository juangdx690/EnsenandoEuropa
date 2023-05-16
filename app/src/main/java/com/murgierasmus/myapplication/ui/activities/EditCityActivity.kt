package com.murgierasmus.myapplication.ui.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout

import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.ActivityEditCityBinding
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.dataclass.ImagenesSubir
import com.murgierasmus.myapplication.ui.Maps.EditarUbicacion
import com.murgierasmus.myapplication.ui.fragments.*
import com.murgierasmus.myapplication.utils.methods.Methods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditCityActivity : AppCompatActivity() {

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
    private lateinit var binding: ActivityEditCityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCityBinding.inflate(layoutInflater)
        recogerDatos()
        setContentView(binding.root)
        contenedor = binding.contenedorAgregar
        methods.replaceFragmentAdd(EditarCiudad1Fragment(), supportFragmentManager)

    }

    override fun onBackPressed() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.contenedorAgregar)
        if (fragment is EditarCiudad1Fragment
        ) {
            super.onBackPressed()
            finish()
        }
        if (fragment is EditarCiudad2Fragment
        ) {
            methods.replaceFragmentAdd(EditarCiudad1Fragment(),supportFragmentManager)
        }
        if (fragment is EditarCiudad3Fragment
        ) {
            methods.replaceFragmentAdd(EditarCiudad2Fragment(),supportFragmentManager)
        }
        if (fragment is EditarCiudad4Fragment
        ) {
            methods.replaceFragmentAdd(EditarCiudad3Fragment(),supportFragmentManager)
        }
        if (fragment is EditarCiudad5Fragment
        ) {
            methods.replaceFragmentAdd(EditarCiudad4Fragment(),supportFragmentManager)
        }
        if (fragment is EditarCiudad6Fragment
        ) {
            methods.replaceFragmentAdd(EditarCiudad5Fragment(),supportFragmentManager)
        }

    }

    fun recogerDatos(){

        val bundle = intent
        datosCiudad = bundle.getParcelableExtra<DatosCiudad2Item>("datosCiudad")!!
    }

    fun enviarDatos() {
        datosCiudad.id = obtenerId()
        Log.i("CAMPOS CIUDAD", datosCiudad.toString())
        val intent = Intent(this, EditarUbicacion::class.java).apply {
            putExtra("DATA", datosCiudad)
            putExtra("DATA2", imagenesCiudad)
            putExtra("POSICION", datosCiudad.id)
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





}