package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.google.firebase.storage.FirebaseStorage
import com.murgierasmus.myapplication.databinding.FragmentCulturasYLugaresBinding
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.ui.activities.CiudadActivity


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private lateinit var datosCiudad: DatosCiudad2Item

@SuppressLint("StaticFieldLeak")
private lateinit var idioma: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var cultura: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var descripcionCultura: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var imgCultura: ImageView

@SuppressLint("StaticFieldLeak")
private lateinit var actividad: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var descripcionActividad: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var imgActividad: ImageView

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentCulturasYLugaresBinding? = null
private val binding get() = bindingA!!

class CulturasYLugaresFragment : Fragment() {
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
        bindingA = FragmentCulturasYLugaresBinding.inflate(inflater, container, false)

        idioma = binding.tvIdioma
        descripcionCultura = binding.tvDescripcionCulturaLugar
        imgCultura = binding.imgCulturaLugar
        cultura = binding.tvCulturaLugar
        descripcionActividad = binding.tvDescripcionActividad
        imgActividad = binding.imgActividad
        actividad = binding.tvActividad

        return binding.root
    }

    fun getScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val screenSize = getScreenSize(requireContext())
        val screenWidth = (screenSize.x / 1.50).toInt()
        val screenHeight = screenSize.y
        super.onViewCreated(view, savedInstanceState)
        datosCiudad = (requireActivity() as CiudadActivity).getCity()
        idioma.text = datosCiudad.Idioma
        cultura.text = datosCiudad.CulturaLugares
        descripcionCultura.text = datosCiudad.DescripcionCulturaLugares
        actividad.text = datosCiudad.TituloActividad
        descripcionActividad.text = datosCiudad.DescripcionActividad


        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val filename = "${datosCiudad.ImagenCultura}.png"
        val fileRef = storageRef.child(filename)

        fileRef.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()

            Glide.with(requireContext())
                .asBitmap()
                .centerCrop()
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        // Redimensionar la imagen al tamaño deseado
                        val resizedBitmap =
                            Bitmap.createScaledBitmap(resource, screenWidth, 550, false)
                        // Crear un RoundedBitmapDrawable a partir de la imagen redimensionada
                        val roundedBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(resources, resizedBitmap)
                        // Establecer la esquina redondeada
                        roundedBitmapDrawable.cornerRadius = 20f

                        // Establecer el drawable en un ImageView
                        imgCultura.setImageDrawable(roundedBitmapDrawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        }.addOnFailureListener { exception ->
            // Maneja el error de obtener la URL de descarga
            Log.e("CAMPOS", "Error al obtener la URL de descarga del archivo: $exception")
        }

        val filename2 = "${datosCiudad.ImagenActividad}.png"
        val fileRef2 = storageRef.child(filename2)

        fileRef2.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()

            Glide.with(requireContext())
                .asBitmap()
                .centerCrop()
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        // Redimensionar la imagen al tamaño deseado
                        val resizedBitmap =
                            Bitmap.createScaledBitmap(resource, screenWidth, 550, false)
                        // Crear un RoundedBitmapDrawable a partir de la imagen redimensionada
                        val roundedBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(resources, resizedBitmap)
                        // Establecer la esquina redondeada
                        roundedBitmapDrawable.cornerRadius = 20f

                        // Establecer el drawable en un ImageView
                        imgActividad.setImageDrawable(roundedBitmapDrawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        }.addOnFailureListener { exception ->
            // Maneja el error de obtener la URL de descarga
            Log.e("CAMPOS", "Error al obtener la URL de descarga del archivo: $exception")
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CulturasYLugaresFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}