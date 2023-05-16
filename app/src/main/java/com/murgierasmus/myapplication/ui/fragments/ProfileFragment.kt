package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.marginEnd
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.FragmentProfileBinding
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.dataclass.DatosPerfil
import com.murgierasmus.myapplication.ui.activities.MainActivity
import com.murgierasmus.myapplication.ui.viemodel.MainViewModel
import com.murgierasmus.myapplication.utils.methods.Methods
import kotlinx.coroutines.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private lateinit var txtUsuario: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var txtCorreo: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var imgPerfil: ImageView

@SuppressLint("StaticFieldLeak")
private lateinit var datosPerfil: DatosPerfil

@SuppressLint("StaticFieldLeak")
private lateinit var tipoPerfil: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var btnEditar: Button

@SuppressLint("StaticFieldLeak")
private lateinit var btnCerrarSesion: Button
@SuppressLint("StaticFieldLeak")
private lateinit var btnContrasena: TextView
@SuppressLint("StaticFieldLeak")
private lateinit var btnOpcionesAdmin: Button

@SuppressLint("StaticFieldLeak")
private lateinit var methods: Methods

private var database: Database = Database()

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentProfileBinding? = null
private val binding get() = bindingA!!
private var editar=false

class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: MainViewModel

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
        bindingA = FragmentProfileBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).setToolbarTitle(requireContext().getString(R.string.profilefragment_perfil))

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            getActivity()?.setTheme(R.style.Theme_GuiaViaje)
        } else {
            getActivity()?.setTheme((R.style.Theme_GuiaViaje))
        }

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        txtCorreo = binding.txtCorreo
        methods = Methods(requireContext())
        txtUsuario = binding.txtUsuario
        imgPerfil = binding.imgPerfil
        imgPerfil.isClickable = false
        imgPerfil.isFocusable = false
        tipoPerfil = binding.txtTipoUsuario


        lifecycleScope.launch {
            datosPerfil = (requireActivity() as MainActivity).devolverDatos()
        }
        txtCorreo.text = datosPerfil.correo
        txtUsuario.text = datosPerfil.usuario
        tipoPerfil.text = datosPerfil.tipoUser

        btnEditar = binding.btnEditar

        lifecycleScope.launch{
            if (database.tipoUser().equals("admin")){
                btnEditar.visibility=View.GONE
            }else{
                btnEditar.visibility=View.VISIBLE
            }
        }

        btnCerrarSesion = binding.btnCerrarSesion
        btnContrasena = binding.btnContrasena
        // Cargar la imagen con Glide
        Glide.with(requireContext())
            .asBitmap()
            .centerCrop()
            .load(datosPerfil.img)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Redimensionar la imagen al tamaño deseado
                    val resizedBitmap = Bitmap.createScaledBitmap(resource, 397, 397, false)
                    // Crear un RoundedBitmapDrawable a partir de la imagen redimensionada
                    val roundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(resources, resizedBitmap)
                    // Establecer la esquina redondeada
                    roundedBitmapDrawable.cornerRadius = 300f

                    // Establecer el drawable en un ImageView
                    imgPerfil.setImageDrawable(roundedBitmapDrawable)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })



        listeners()
        return binding.root
    }




    private fun listeners() {
        btnCerrarSesion.setOnClickListener {
            cerrarSesion()

        }
        btnEditar.setOnClickListener{

            editar = !editar

            if (editar){
                Toast.makeText(requireContext(), R.string.msgHabilitadoOpcionesEditar, Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(), R.string.msgDesHabilitadoOpcionesEditar, Toast.LENGTH_LONG).show()
            }
        }
        imgPerfil.setOnClickListener {
            if (editar){
                openGallery()
            }
        }

        btnContrasena.setOnClickListener {
           if (editar){
               val builder = AlertDialog.Builder(requireContext())
               builder.setIcon(R.drawable.restpass)
               builder.setTitle(R.string.cambiarpass)
               var input = EditText(requireContext())
               input.hint = requireContext().getString(R.string.ingresecorreo)
               builder.setView(input)

               builder.setPositiveButton(R.string.aceptar) { _, _ ->
                   val correo = input.text.toString()
                   if (!correo.equals(txtCorreo)){
                       Toast.makeText(requireContext(), R.string.nocorreo, Toast.LENGTH_SHORT).show()
                   }else{
                       FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
                           .addOnCompleteListener { task ->
                               if (task.isSuccessful) {
                                   Toast.makeText(requireContext(), R.string.resetocorrecto, Toast.LENGTH_SHORT).show()
                               } else {
                                   Toast.makeText(requireContext(), R.string.resetoincorrecto, Toast.LENGTH_SHORT).show()
                               }
                           }
                   }
               }

               builder.setNegativeButton(R.string.cancelar) { dialog, _ ->
                   dialog.cancel()
               }
               builder.show()
           }
        }
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
            imgPerfil.setImageURI(imageUri)
            Glide.with(requireContext())
                .asBitmap()
                .centerCrop()
                .load(imageUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        // Redimensionar la imagen al tamaño deseado
                        val resizedBitmap = Bitmap.createScaledBitmap(resource, 397, 397, false)
                        // Crear un RoundedBitmapDrawable a partir de la imagen redimensionada
                        val roundedBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(resources, resizedBitmap)
                        // Establecer la esquina redondeada
                        roundedBitmapDrawable.cornerRadius = 300f

                        // Establecer el drawable en un ImageView
                        imgPerfil.setImageDrawable(roundedBitmapDrawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

            val user = FirebaseAuth.getInstance().currentUser
            var profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUri)
                .build()
            user?.updateProfile(profileUpdates)?.addOnCompleteListener{
                if (it.isSuccessful){
                    lifecycleScope.launch{
                        delay(500)
                        Toast.makeText(requireContext(), R.string.fotoactualizada, Toast.LENGTH_LONG).show()
                        (requireActivity() as MainActivity).recreate()
                    }
                }
            }
        }
    }

    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            lifecycleScope.launch {
                val datosPerfil = async(Dispatchers.IO) {
                    (requireActivity() as MainActivity).cargarDatos()
                }.await()

                methods.replaceFragment(
                    LoginRegisterFragment(),
                    requireActivity().supportFragmentManager
                )
                detenerListener()
            }

        }
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener!!)
    }

    fun detenerListener() {
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener!!)
    }


    fun Int.dpToPx(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}