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
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.storage.FirebaseStorage
import com.murgierasmus.myapplication.databinding.FragmentCiudadHomeBinding
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.ui.activities.CiudadActivity
import com.murgierasmus.myapplication.utils.methods.Methods


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var datosCiudad: DatosCiudad2Item

@SuppressLint("StaticFieldLeak")
private lateinit var btnTransporte: CardView
@SuppressLint("StaticFieldLeak")
private lateinit var btnComida: CardView
@SuppressLint("StaticFieldLeak")
private lateinit var btnCultura: CardView

@SuppressLint("StaticFieldLeak")
private lateinit var descripcion: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var methods: Methods


@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentCiudadHomeBinding? = null
private val binding get() = bindingA!!

class CiudadHomeFragment : Fragment() {
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
        bindingA = FragmentCiudadHomeBinding.inflate(inflater, container, false)
        descripcion = binding.tvDescripcionCiudad
        btnTransporte = binding.cardTransporte
        btnComida = binding.cardComida
        btnCultura = binding.cardOcio
        methods = Methods(requireContext())
        listeners()

        val screenSize = getScreenSize(requireContext())
        val screenWidth = (screenSize.x / 1.50).toInt()
        val screenHeight = screenSize.y



        Log.i("TAMAÑOS PANTALLA", screenWidth.toString() + " - " + screenHeight.toString())
        datosCiudad = (requireActivity() as CiudadActivity).getCity()
        binding.tvNombreCiudad.text = datosCiudad.Nombre


        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val filename = "${datosCiudad.Foto}.png"
        val fileRef = storageRef.child(filename)

        fileRef.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()

            Glide.with(requireContext())
                .asBitmap()
                .centerCrop()
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        // Redimensionar la imagen al tamaño deseado
                        val resizedBitmap = Bitmap.createScaledBitmap(resource, screenWidth, 550, false)
                        // Crear un RoundedBitmapDrawable a partir de la imagen redimensionada
                        val roundedBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(resources, resizedBitmap)
                        // Establecer la esquina redondeada
                        roundedBitmapDrawable.cornerRadius = 20f

                        // Establecer el drawable en un ImageView
                        binding.imgCiudad.setImageDrawable(roundedBitmapDrawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        }.addOnFailureListener { exception ->
            // Maneja el error de obtener la URL de descarga
            Log.e("CAMPOS", "Error al obtener la URL de descarga del archivo: $exception")
        }


        descripcion.text = datosCiudad.Introduccion

        return binding.root
    }

    private fun listeners() {
        btnTransporte.setOnClickListener {

            methods.replaceFragmentCiudad(
                TransporteFragment(),
                requireActivity().supportFragmentManager
            )

        }

        btnComida.setOnClickListener{
            methods.replaceFragmentCiudad(
                ComidaRestauranteFragment(),
                requireActivity().supportFragmentManager
            )
        }
        btnCultura.setOnClickListener{
            methods.replaceFragmentCiudad(
                CulturasYLugaresFragment(),
                requireActivity().supportFragmentManager
            )
        }
    }




    fun getScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CiudadHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}