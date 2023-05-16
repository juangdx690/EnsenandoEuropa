package com.murgierasmus.myapplication.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.ActivityCiudadBinding
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.ui.Maps.MapaAeropuertos
import com.murgierasmus.myapplication.ui.Maps.MapaFinal
import com.murgierasmus.myapplication.ui.fragments.CiudadHomeFragment
import com.murgierasmus.myapplication.ui.fragments.ComidaRestauranteFragment
import com.murgierasmus.myapplication.ui.fragments.CulturasYLugaresFragment
import com.murgierasmus.myapplication.ui.fragments.TransporteFragment
import com.murgierasmus.myapplication.utils.methods.Methods


class CiudadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCiudadBinding
    private var methods: Methods = Methods(this)

    private lateinit var datosCiudad: DatosCiudad2Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCiudadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        methods.replaceFragmentCiudad(CiudadHomeFragment(), supportFragmentManager)


        datosCiudad = intent.getParcelableExtra<DatosCiudad2Item>("datosCiudad")!!


    }

    override fun onBackPressed() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.contenedorCiudad)
        if (fragment is TransporteFragment || fragment is ComidaRestauranteFragment ||
            fragment is CulturasYLugaresFragment
        ) {
            methods.replaceFragmentCiudad(CiudadHomeFragment(), supportFragmentManager)
        } else {
            super.onBackPressed()
            finish()
        }


    }

    fun getCity(): DatosCiudad2Item {
        return datosCiudad
    }

    fun enviarMapa() {

        val intent = Intent(this, MapaFinal::class.java).apply {
            putExtra("UBI", datosCiudad.UbicacionString)
            putExtra("LATITUD", datosCiudad.UbicacionLng1)
            putExtra("LONGITUD", datosCiudad.UbicacionLng2)
        }
        startActivity(intent)

    }

    fun enviarAeropuerto() {

        val intent = Intent(this, MapaAeropuertos::class.java)
        startActivity(intent)

    }

}