package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.FragmentEditarCiudad1Binding
import com.murgierasmus.myapplication.dataclass.Paises
import com.murgierasmus.myapplication.ui.activities.EditCityActivity
import com.murgierasmus.myapplication.ui.adapters.SpinnerPaisesAdapter
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import com.murgierasmus.myapplication.utils.methods.Methods
import java.io.ByteArrayOutputStream


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditarCiudad1Fragment : Fragment() {

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
    private var bindingA: FragmentEditarCiudad1Binding? = null
    private val binding get() = bindingA!!

    private lateinit var nombrePais: String

    private var listaPaises = Paises.paises

    private lateinit var actividad: Activity

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
        bindingA = FragmentEditarCiudad1Binding.inflate(inflater, container, false)

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

        rellenarCampos()

        listeners()
        return binding.root
    }


    private fun rellenarCampos() {
        actividad = requireActivity() as EditCityActivity
        val nombre = (actividad as EditCityActivity).datosCiudad.Nombre
        val descripcion = (actividad as EditCityActivity).datosCiudad.Introduccion
        txtnombreCiudad.setText(nombre)
        txtdescripcionCiudad.setText(descripcion)
        spinnerPaises.setSelection(indexPais())

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val filename = "${(actividad as EditCityActivity).datosCiudad.Foto}.png"
        val fileRef = storageRef.child(filename)

        fileRef.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()

            Glide.with(requireContext())
                .asBitmap()
                .centerCrop()
                .load(url)
                .into(imgCiudad)

        }.addOnFailureListener { exception ->
            // Maneja el error de obtener la URL de descarga
            Log.e("CAMPOS", "Error al obtener la URL de descarga del archivo: $exception")
        }



    }

    fun indexPais(): Int {
        actividad = requireActivity() as EditCityActivity
        return when ((actividad as EditCityActivity).datosCiudad.Pais) {
            "España" -> 0
            "Francia" -> 1
            "Portugal" -> 2
            "Alemania" -> 3
            "Irlanda" -> 4
            "Reino Unido" -> 5
            "Italia" -> 6
            else -> 0
        }
    }



    private fun listeners() {
        val actividad = requireActivity() as EditCityActivity

        imgCiudad.setOnClickListener {
            openGallery()
        }
        btnNext.setOnClickListener {
            if (comprobarCampos()) {
                actividad.datosCiudad.Nombre = txtnombreCiudad.text.toString()
                actividad.datosCiudad.Introduccion = txtdescripcionCiudad.text.toString()

                val drawable = imgCiudad.drawable
                val bitmap = if (drawable is BitmapDrawable) {
                    drawable.bitmap
                } else {
                    null
                }
                val stream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                actividad.imagenesCiudad.Foto=byteArray

                actividad.datosCiudad.Foto = actividad.datosCiudad.Nombre + "_image"
                actividad.datosCiudad.Pais = obtenerNombrePais()



                methods.replaceFragmentAdd(
                   EditarCiudad2Fragment(),
                    requireActivity().supportFragmentManager
                )
                Log.i("DOLBAIOB", actividad.imagenesCiudad.toString())
                Log.i("CAMPOS", "CORRECTO")
                Log.i("CAMPOS", actividad.datosCiudad.toString())
            } else {
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

        if (imgCiudad.drawable.constantState == ContextCompat.getDrawable(
                requireContext(),
                R.drawable.imgfoto
            )?.constantState
        ) {
            comprobacion = false
        }


        return comprobacion
    }

    fun obtenerNombrePais(): String {
        val paisSeleccionado = spinnerPaises.selectedItem as Paises
        nombrePais = paisSeleccionado.nombre

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
            EditarCiudad1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}