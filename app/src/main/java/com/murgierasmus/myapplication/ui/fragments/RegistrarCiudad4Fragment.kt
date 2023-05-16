package com.murgierasmus.myapplication.ui.fragments
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.FragmentRegistrarCiudad4Binding
import com.murgierasmus.myapplication.ui.activities.AddCityActivity
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import com.murgierasmus.myapplication.utils.methods.Methods
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentRegistrarCiudad4Binding? = null
private val binding get() = bindingA!!

@SuppressLint("StaticFieldLeak")
private lateinit var nombreActividad: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtnombreActividad : TextInputEditText

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

class RegistrarCiudad4Fragment : Fragment() {
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
        bindingA = FragmentRegistrarCiudad4Binding.inflate(inflater, container, false)

        dialogs = Dialogs(requireContext())
        methods = Methods(requireContext())
        nombreActividad = binding.Actividad
        txtnombreActividad = binding.txtActividad
        descripcionActividad = binding.DescripcionActividad
        txtdescripcionActividad = binding.txtDescActividad
        imgActividad = binding.imgActividad
        btnNext = binding.btnNextPage


        listeners()
        return binding.root
    }




    private fun listeners() {
        val actividad = requireActivity() as AddCityActivity
        imgActividad.setOnClickListener {
            openGallery()
        }

        btnNext.setOnClickListener {
            if (comprobarCampos()){
                actividad.datosCiudad.TituloActividad = txtnombreActividad.text.toString()
                actividad.datosCiudad.DescripcionActividad = txtdescripcionActividad.text.toString()
                actividad.datosCiudad.ImagenActividad = actividad.datosCiudad.Nombre+"_actividad"
                val drawable = imgActividad.drawable
                val bitmap = (drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()

                actividad.imagenesCiudad.ImagenActividad=byteArray

                methods.replaceFragmentAdd(RegistrarCiudad5Fragment(), requireActivity().supportFragmentManager)

                Log.i("CAMPOS", "CORRECTO")
                Log.i("CAMPOS", actividad.datosCiudad.toString())
            }else{
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

        if (imgActividad.drawable.constantState == ContextCompat.getDrawable(requireContext(), R.drawable.imgfoto)?.constantState){
            comprobacion=false
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
            RegistrarCiudad4Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}