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
import com.murgierasmus.myapplication.databinding.FragmentEditarCiudad4Binding
import com.murgierasmus.myapplication.ui.activities.EditCityActivity
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import com.murgierasmus.myapplication.utils.methods.Methods
import java.io.ByteArrayOutputStream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentEditarCiudad4Binding? = null
private val binding get() = bindingA!!

@SuppressLint("StaticFieldLeak")
private lateinit var nombreActividad: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtnombreActividad: TextInputEditText

@SuppressLint("StaticFieldLeak")
private lateinit var descripcionActividad: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtdescripcionActividad: TextInputEditText

@SuppressLint("StaticFieldLeak")
private lateinit var imgActividad: ImageView

@SuppressLint("StaticFieldLeak")
private lateinit var btnNext: Button

@SuppressLint("StaticFieldLeak")
private lateinit var dialogs: Dialogs

@SuppressLint("StaticFieldLeak")
private lateinit var methods: Methods

class EditarCiudad4Fragment : Fragment() {
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
        bindingA = FragmentEditarCiudad4Binding.inflate(inflater, container, false)

        dialogs = Dialogs(requireContext())
        methods = Methods(requireContext())
        nombreActividad = binding.Actividad
        txtnombreActividad = binding.txtActividad
        descripcionActividad = binding.DescripcionActividad
        txtdescripcionActividad = binding.txtDescActividad
        imgActividad = binding.imgActividad
        btnNext = binding.btnNextPage
        rellenarCampos()
        listeners()
        return binding.root
    }

    private fun rellenarCampos() {
        val actividad = requireActivity() as EditCityActivity
        val nombre = (actividad as EditCityActivity).datosCiudad.TituloActividad
        val descripcion = (actividad as EditCityActivity).datosCiudad.DescripcionActividad
        txtnombreActividad.setText(nombre)
        txtdescripcionActividad.setText(descripcion)

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val filename = "${(actividad as EditCityActivity).datosCiudad.ImagenActividad}.png"
        val fileRef = storageRef.child(filename)

        fileRef.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()

            Glide.with(requireContext())
                .asBitmap()
                .centerCrop()
                .load(url)
                .into(imgActividad)

        }.addOnFailureListener { exception ->
            // Maneja el error de obtener la URL de descarga
            Log.e("CAMPOS", "Error al obtener la URL de descarga del archivo: $exception")
        }


    }

    private fun listeners() {
        val actividad = requireActivity() as EditCityActivity
        imgActividad.setOnClickListener {
            openGallery()
        }

        btnNext.setOnClickListener {
            if (comprobarCampos()) {
                actividad.datosCiudad.TituloActividad = txtnombreActividad.text.toString()
                actividad.datosCiudad.DescripcionActividad = txtdescripcionActividad.text.toString()
                actividad.datosCiudad.ImagenActividad = actividad.datosCiudad.Nombre + "_actividad"

                val drawable = imgActividad.drawable
                val bitmap = if (drawable is BitmapDrawable) {
                    drawable.bitmap
                } else {
                    null
                }

                val stream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()

                actividad.imagenesCiudad.ImagenActividad=byteArray

                methods.replaceFragmentAdd(
                    EditarCiudad5Fragment(),
                    requireActivity().supportFragmentManager
                )

                Log.i("CAMPOS", "CORRECTO")
                Log.i("CAMPOS", actividad.datosCiudad.toString())
            } else {
                dialogs.dialogoCamposDefault()
            }
        }
    }

    fun comprobarCampos(): Boolean {
        var comprobacion = false

        if (txtnombreActividad.text!!.isNotEmpty() &&
            txtdescripcionActividad.text!!.isNotEmpty()

        ) {

            comprobacion = true
        }

        if (imgActividad.drawable.constantState == ContextCompat.getDrawable(
                requireContext(),
                R.drawable.imgfoto
            )?.constantState
        ) {
            comprobacion = false
        }


        return comprobacion
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
            imgActividad.setImageURI(imageUri)

            Glide.with(requireContext())
                .load(imageUri)
                .override(150, 150) // Redimensiona la imagen a 150 x 150 dp
                .centerCrop() // Centra la imagen y recorta los bordes si es necesario
                .into(imgActividad); // Carga la imagen en el ImageView especificado


        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditarCiudad4Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}