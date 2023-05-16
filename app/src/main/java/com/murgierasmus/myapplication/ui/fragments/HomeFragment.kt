package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.FragmentHomeBinding
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.ui.activities.AddCityActivity
import com.murgierasmus.myapplication.ui.activities.MainActivity
import com.murgierasmus.myapplication.ui.adapters.CiudadAdapter
import com.murgierasmus.myapplication.ui.adapters.CiudadAdapterUsuario
import com.murgierasmus.myapplication.ui.viemodel.CiudadViewModel
import kotlinx.coroutines.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private lateinit var recyclerViewCiudades: RecyclerView
@SuppressLint("StaticFieldLeak")
private lateinit var txtNohayCiudades: TextView
@SuppressLint("StaticFieldLeak")
private lateinit var txtTitulo: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var btnAddCity: FloatingActionButton

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentHomeBinding? = null
private val binding get() = bindingA!!

private var myCoroutine: Job? = null


class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }



    private val viewModel: CiudadViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).setToolbarTitle(requireContext().getString(R.string.homefragment_inicio))

        bindingA = FragmentHomeBinding.inflate(inflater, container, false)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            getActivity()?.setTheme(R.style.Theme_GuiaViaje)
        } else {
            getActivity()?.setTheme((R.style.Theme_GuiaViaje))
        }
        btnAddCity = binding.addCity
        recyclerViewCiudades = binding.recyclerViewCiudades

        txtNohayCiudades= binding.listaVacia
        txtTitulo= binding.titleCiudades

        if (user().equals(requireContext().getString(R.string.framgent_profile_usuario))){
            binding.addCity.visibility=View.GONE
        }else{
            binding.addCity.visibility=View.VISIBLE
        }

        listeners()
        return binding.root
    }

    fun listeners(){
        btnAddCity.setOnClickListener{

            val intent = Intent(requireContext(), AddCityActivity::class.java )
            startActivity(intent)

        }
    }
    fun user():String{
        var isUserAdmin=false
        var database= Database()
        myCoroutine =  viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            // Realizar la operación de carga del tipo de usuario aquí
            val database = Database()
            isUserAdmin = database.tipoUser().equals(requireContext().getString(R.string.homefragment_usuario))
        }
        return if (isUserAdmin) {
            requireContext().getString(R.string.framgent_profile_usuario)
        } else {
            requireContext().getString(R.string.homefragment_admin)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val ciudadAdapterAdmin = CiudadAdapter(emptyList())
        val ciudadAdapterUsuario = CiudadAdapterUsuario(emptyList())

        var database = Database()

        myCoroutine= lifecycleScope.launch{
            if(FirebaseAuth.getInstance().currentUser!=null){
                if (database.tipoUser().equals("usuario")){
                    recyclerViewCiudades.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = ciudadAdapterUsuario
                    }

                    myCoroutine= lifecycleScope.launch{
                        viewModel.ciudades.observe(viewLifecycleOwner) { ciudades ->
                            if (ciudades != null) {
                                ciudadAdapterUsuario.setCiudades(ciudades)
                            }
                        }
                    }
                    btnAddCity.visibility=View.GONE
                }else{
                    recyclerViewCiudades.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = ciudadAdapterAdmin
                    }

                    myCoroutine= lifecycleScope.launch {
                        viewModel.ciudades.observe(viewLifecycleOwner) { ciudades ->
                            if (ciudades != null) {
                                ciudadAdapterAdmin.setCiudades(ciudades)
                            }
                        }
                    }


                    btnAddCity.visibility=View.VISIBLE
                }
            }else{
                recyclerViewCiudades.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = ciudadAdapterUsuario
                }

                myCoroutine=lifecycleScope.launch{
                    viewModel.ciudades.observe(viewLifecycleOwner) { ciudades ->
                        if (ciudades != null) {
                            ciudadAdapterUsuario.setCiudades(ciudades)
                        }
                    }
                }
                btnAddCity.visibility=View.GONE
            }
        }



        myCoroutine=  lifecycleScope.launch{

            delay(2500)
            if (recyclerViewCiudades.size==0){
                txtNohayCiudades.visibility= View.VISIBLE
                txtTitulo.visibility=View.GONE
            }else{
                txtNohayCiudades.visibility= View.GONE
                txtTitulo.visibility=View.VISIBLE
            }

        }

        myCoroutine=    lifecycleScope.launch{

            delay(1000)
            if (recyclerViewCiudades.size==0){
                txtNohayCiudades.visibility= View.VISIBLE
                txtTitulo.visibility=View.GONE
            }else{
                txtNohayCiudades.visibility= View.GONE
                txtTitulo.visibility=View.VISIBLE
            }

        }

        viewModel.obtenerCiudades()
    }

    override fun onDestroy() {

        super.onDestroy()
        // Detener la corutina
        myCoroutine?.cancel()
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}