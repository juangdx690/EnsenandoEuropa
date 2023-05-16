package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.murgierasmus.myapplication.core.RetrofitHelper
import com.murgierasmus.myapplication.data.network.CiudadesApiService
import com.murgierasmus.myapplication.databinding.FragmentRegistrarCiudad6Binding
import com.murgierasmus.myapplication.dataclass.CitiesResponse
import com.murgierasmus.myapplication.dataclass.PosicionCiudades
import com.murgierasmus.myapplication.ui.activities.AddCityActivity
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import com.murgierasmus.myapplication.utils.methods.Methods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentRegistrarCiudad6Binding? = null
private val binding get() = bindingA!!

@SuppressLint("StaticFieldLeak")
private lateinit var Idioma: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtIdioma: TextInputEditText

@SuppressLint("StaticFieldLeak")
private lateinit var btnAdd: Button


@SuppressLint("StaticFieldLeak")
private lateinit var imgUbicacion: ImageView

@SuppressLint("StaticFieldLeak")
private lateinit var dialogs: Dialogs

@SuppressLint("StaticFieldLeak")
private lateinit var methods: Methods

private var allCiudades = mutableListOf<PosicionCiudades>()


class RegistrarCiudad6Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingA = FragmentRegistrarCiudad6Binding.inflate(inflater, container, false)

        numeroPosicion()

        dialogs = Dialogs(requireContext())
        methods = Methods(requireContext())
        Idioma = binding.Idioma
        txtIdioma = binding.txtIdioma
        imgUbicacion = binding.imgUbicacion


        listeners()
        return binding.root
    }

    private fun listeners() {
        val actividad = requireActivity() as AddCityActivity
        imgUbicacion.setOnClickListener{

            if (comprobarCampos()) {
                actividad.datosCiudad.Idioma = txtIdioma.text.toString()

              actividad.enviarDatos()

                actividad.enviarDatos()
                Log.i("CAMPOS", "CORRECTO")
            } else {
            dialogs.dialogoCamposDefault()
        }

        }
        val retrofit = RetrofitHelper.getRetrofit()
        val myApi = retrofit.create(CiudadesApiService::class.java)
        var position = allCiudades.size

    }

    fun numeroPosicion() {
        val retrofit = RetrofitHelper.getRetrofit()
        val myApi = retrofit.create(CiudadesApiService::class.java)

        myApi.getMovies("Ciudades.json/").enqueue(object : Callback<CitiesResponse> {
            override fun onResponse(
                call: Call<CitiesResponse>,
                response: Response<CitiesResponse>
            ) {
                val cities = response.body()?.cities

                if (cities != null) {
                    Log.i("CAMPOS", allCiudades.size.toString()+ " inical")
                    allCiudades.addAll(cities)
                    Log.i("CAMPOS", allCiudades.size.toString()+ " final")
                }else{
                    Log.i("CAMPOS",  " null")
                }


            }

            override fun onFailure(call: Call<CitiesResponse>, t: Throwable) {
                Log.e("MainActivity", "Error al cargar los datos de la API", t)
            }
        })


    }

    fun comprobarCampos(): Boolean {
        var comprobacion = false

        if (txtIdioma.text!!.isNotEmpty()

        ) {

            comprobacion = true
        }


        return comprobacion
    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrarCiudad6Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}