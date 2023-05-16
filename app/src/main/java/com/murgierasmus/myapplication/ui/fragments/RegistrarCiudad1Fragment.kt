package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.FragmentRegistrarCiudad1Binding
import com.murgierasmus.myapplication.dataclass.Paises
import com.murgierasmus.myapplication.ui.activities.AddCityActivity
import com.murgierasmus.myapplication.ui.adapters.SpinnerPaisesAdapter
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import com.murgierasmus.myapplication.utils.methods.Methods
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private lateinit var spinnerPaises: Spinner


@SuppressLint("StaticFieldLeak")
private lateinit var adapterPaises: SpinnerPaisesAdapter

@SuppressLint("StaticFieldLeak")
private lateinit var nombreCiudad: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtnombreCiudad: TextInputEditText

@SuppressLint("StaticFieldLeak")
private lateinit var descripcionCiudad: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtdescripcionCiudad: TextInputEditText

@SuppressLint("StaticFieldLeak")
private lateinit var imgCiudad: ImageView

@SuppressLint("StaticFieldLeak")
private lateinit var btnNext: Button
@SuppressLint("StaticFieldLeak")
private lateinit var dialogs: Dialogs
@SuppressLint("StaticFieldLeak")
private lateinit var methods: Methods

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentRegistrarCiudad1Binding? = null
private val binding get() = bindingA!!

private lateinit var nombrePais: String

private var listaPaises = Paises.paises


class RegistrarCiudad1Fragment : Fragment() {
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
        bindingA = FragmentRegistrarCiudad1Binding.inflate(inflater, container, false)

        dialogs = Dialogs(requireContext())
        methods = Methods(requireContext())
        spinnerPaises = binding.paisDestino
        nombreCiudad = binding.Ciudad
        txtnombreCiudad = binding.txtCiudad
        descripcionCiudad = binding.Descripcion
        txtdescripcionCiudad = binding.txtDescripcion
        imgCiudad = binding.imgCiudad
        btnNext = binding.btnNextPage

        adapterPaises = SpinnerPaisesAdapter(listaPaises, requireContext())
        spinnerPaises.adapter = adapterPaises


        listeners()
        return binding.root
    }

    private fun listeners() {
        val actividad = requireActivity() as AddCityActivity

        imgCiudad.setOnClickListener {
            openGallery()
        }
        btnNext.setOnClickListener {
            if (comprobarCampos()){
                actividad.datosCiudad.Nombre = txtnombreCiudad.text.toString()
                actividad.datosCiudad.Introduccion = txtdescripcionCiudad.text.toString()


                val drawable = imgCiudad.drawable
                val bitmap = (drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()



                actividad.imagenesCiudad.Foto=byteArray
                actividad.datosCiudad.Foto =  actividad.datosCiudad.Nombre+"_image"
                actividad.datosCiudad.Pais = obtenerNombrePais()

                methods.replaceFragmentAdd(RegistrarCiudad2Fragment(), requireActivity().supportFragmentManager)

                Log.i("CAMPOS", "CORRECTO")
                Log.i("CAMPOS", actividad.datosCiudad.toString())
            }else{
               dialogs.dialogoCamposDefault()
            }
        }
    }

    fun comprobarCampos(): Boolean {
        var comprobacion = false

        if (txtnombreCiudad.text!!.isNotEmpty() &&
            txtdescripcionCiudad.text!!.isNotEmpty()

        ) {

            comprobacion = true
        }

        if (  imgCiudad.drawable.constantState == ContextCompat.getDrawable(requireContext(), R.drawable.imgfoto)?.constantState){
            comprobacion=false
        }


        return comprobacion
    }

    fun obtenerNombrePais(): String {
        val paisSeleccionado = spinnerPaises.selectedItemPosition
        nombrePais = when(paisSeleccionado){
            0-> "España"
            1-> "Francia"
            2-> "Portugal"
            3-> "Alemania"
            4-> "Irlanda"
            5-> "Reino Unido"
            6-> "Italia"
            else->"España"
        }

        return nombrePais
    }

    private fun obtenerBytesImagen(image: ImageView): ByteArray {
        val drawable = image.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()


        return byteArray
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            imgCiudad.setImageURI(imageUri)

            Glide.with(requireContext())
                .load(imageUri)
                .override(150, 150) // Redimensiona la imagen a 150 x 150 dp
                .centerCrop() // Centra la imagen y recorta los bordes si es necesario
                .into(imgCiudad); // Carga la imagen en el ImageView especificado


        }
    }


    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrarCiudad1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}