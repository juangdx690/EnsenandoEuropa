package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.FragmentLoginBinding
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.ui.activities.MainActivity
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import com.murgierasmus.myapplication.utils.methods.Methods
import kotlinx.coroutines.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private lateinit var btnBack: ImageView

@SuppressLint("StaticFieldLeak")
private lateinit var btnLogin: Button

@SuppressLint("StaticFieldLeak")
private lateinit var btnRegister: Button

@SuppressLint("StaticFieldLeak")
private lateinit var correo: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtCorreo: TextInputEditText

@SuppressLint("StaticFieldLeak")
private lateinit var clave: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtClave: TextInputEditText

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentLoginBinding? = null
private val binding get() = bindingA!!

@SuppressLint("StaticFieldLeak")
private lateinit var methods: Methods

@SuppressLint("StaticFieldLeak")
private lateinit var database: Database

@SuppressLint("StaticFieldLeak")
private lateinit var dialogos: Dialogs
@SuppressLint("StaticFieldLeak")
private lateinit var btnResetClave: Button

@SuppressLint("StaticFieldLeak")
private lateinit var animation: LottieAnimationView

class LoginFragment : Fragment() {
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
        (requireActivity() as MainActivity).setToolbarTitle(requireContext().getString(R.string.loginfragment_inicio))
        bindingA = FragmentLoginBinding.inflate(inflater, container, false)

        methods = Methods(requireContext())

        btnResetClave= binding.btnResetClave
        btnLogin = binding.btnloginlOGIN
        btnRegister = binding.btnloginREGISTER
        btnBack = binding.btnloginBACK
        correo = binding.Correo
        clave = binding.Clave
        txtCorreo = binding.txtCorreo
        txtClave = binding.txtClave
        animation = binding.animationView

        database = Database()
        dialogos = Dialogs(requireContext())

        listeners()

        return binding.root
    }

    private fun listeners() {

        btnResetClave.setOnClickListener{

            val builder = AlertDialog.Builder(requireContext())
            builder.setIcon(R.drawable.restpass)
            builder.setTitle(R.string.resetpass)
            var input = EditText(requireContext())
            input.hint = requireContext().getString(R.string.ingresecorreo)
            builder.setView(input)

            builder.setPositiveButton(R.string.aceptar) { _, _ ->
                val correo = input.text.toString()
                FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), R.string.resetocorrecto, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), R.string.resetoincorrecto, Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            builder.setNegativeButton(R.string.cancelar) { dialog, _ ->
                dialog.cancel()
            }
            builder.show()

        }

        txtCorreo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                comprobarCampos()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                comprobarCampos()

            }

            override fun afterTextChanged(s: Editable?) {
                comprobarCampos()
            }
        })
        txtClave.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                comprobarCampos()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                comprobarCampos()

            }

            override fun afterTextChanged(s: Editable?) {
                comprobarCampos()
            }
        })
        btnBack.setOnClickListener {
            methods.replaceFragment(
                LoginRegisterFragment(),
                requireActivity().supportFragmentManager
            )
        }
        btnRegister.setOnClickListener {
            methods.replaceFragment(RegisterFragment(), requireActivity().supportFragmentManager)
        }
        btnLogin.setOnClickListener {
            animation.visibility = View.VISIBLE
            animation.playAnimation()
            database.iniciarSesion(
                txtCorreo.text.toString(),
                txtClave.text.toString()
            )
            val handler = Handler()
            FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                handler.postDelayed({
                    animation.cancelAnimation()
                    animation.visibility = View.GONE
                    dialogos.falloInicio()
                }, 5000)
                if (user != null) {
                    Log.i("entrar", "entra")

                    handler.removeCallbacksAndMessages(null)
                    lifecycleScope.launch {
                        val datosPerfil = async(Dispatchers.IO) {
                            (requireActivity() as MainActivity).cargarDatos()
                        }.await()
                        animation.cancelAnimation()
                        animation.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            requireContext().getString(R.string.loginfragment_logincorrecto),
                            Toast.LENGTH_LONG
                        ).show()

                        methods.replaceFragment(
                            ProfileFragment(),
                            requireActivity().supportFragmentManager
                        )
                    }

                }
            }

        }
    }

    fun comprobarCampos() {
        val color = ContextCompat.getColor(requireContext(), R.color.botones_background)
        val colorLight = ContextCompat.getColor(requireContext(), R.color.botones_disable)

        if (Patterns.EMAIL_ADDRESS.matcher(txtCorreo.text.toString())
                .matches() && txtClave.text.toString()
                .isNotEmpty() && txtClave.text.toString().length >= 6
        ) {
            btnLogin.isEnabled = true
            btnLogin.setBackgroundColor(color)
        } else {
            btnLogin.isEnabled = false
            btnLogin.setBackgroundColor(colorLight)
        }

    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}