package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.FragmentRegisterBinding
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.ui.activities.MainActivity
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import com.murgierasmus.myapplication.utils.methods.Methods
import java.util.regex.Pattern

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private lateinit var btnBack: ImageView

@SuppressLint("StaticFieldLeak")
private lateinit var btnLogin: Button

@SuppressLint("StaticFieldLeak")
private lateinit var btnNext: Button

@SuppressLint("StaticFieldLeak")
private lateinit var titleText: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var usuario: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtUsuario: TextInputEditText

@SuppressLint("StaticFieldLeak")
private lateinit var correo: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtCorreo: TextInputEditText

@SuppressLint("StaticFieldLeak")
private lateinit var contrasena: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var animation: LottieAnimationView

@SuppressLint("StaticFieldLeak")
private lateinit var txtContrasena: TextInputEditText

@SuppressLint("StaticFieldLeak")
private lateinit var confirmarContrasena: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtCofirmarContrasena: TextInputEditText

private val PASSWORD_PATTER: Pattern = Pattern.compile(
    "^" +
            "(?=.*\\d)" +
            "(?=.*[a-z])" +
            "(?=.*[A-Z])" +
            "(?=.*[a-zA-Z])" +
            "(?=.*[@#$%^&+=])" +
            "(?=\\S+$)" +
            ".{4,}" +
            "$"
)

@SuppressLint("StaticFieldLeak")
private lateinit var methods: Methods

@SuppressLint("StaticFieldLeak")
private lateinit var dialogs: Dialogs

@SuppressLint("StaticFieldLeak")
private lateinit var database: Database


@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentRegisterBinding? = null
private val binding get() = bindingA!!


class RegisterFragment : Fragment() {
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
        (requireActivity() as MainActivity).setToolbarTitle(requireContext().getString(R.string.registerfragment_registro))
        bindingA = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        methods = Methods(requireContext())
        database = Database()
        dialogs = Dialogs(requireContext())

        btnLogin = binding.btnRegisterL
        animation = binding.animationView
        btnNext = binding.btnRegisterNEXT
        titleText = binding.tvRegisterTitle
        btnBack = binding.btnregisterBACK

        usuario = binding.Usuario
        txtUsuario = binding.txtUsuario
        correo = binding.Correo
        txtCorreo = binding.txtCorreo
        contrasena = binding.Contrasena
        txtContrasena = binding.txtContrasena
        confirmarContrasena = binding.ConfirmarContrasena
        txtCofirmarContrasena = binding.txtConfirmarContrasena

        listeners()
    }

    private fun listeners() {
        btnBack.setOnClickListener {
            methods.replaceFragment(
                LoginRegisterFragment(),
                requireActivity().supportFragmentManager
            )
        }

            txtUsuario.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    comprobarCampos()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    comprobarCampos()
                    if (txtUsuario.text!!.isEmpty()) {

                        usuario.error = requireContext().getString(R.string.registerfragment_camponovacio)

                    }
                    usuario.error = null

                }

                override fun afterTextChanged(s: Editable?) {
                    comprobarCampos()
                }
            })
            txtCorreo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    comprobarCampos()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    comprobarCampos()
                    correo.error = null

                }

                override fun afterTextChanged(s: Editable?) {
                    comprobarCampos()
                }
            })
            txtContrasena.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    comprobarCampos()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    comprobarCampos()

                    contrasena.error = null

                }

                override fun afterTextChanged(s: Editable?) {
                    comprobarCampos()
                }
            })
            txtCofirmarContrasena.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    comprobarCampos()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    comprobarCampos()
                    confirmarContrasena.error = null

                }

                override fun afterTextChanged(s: Editable?) {
                    comprobarCampos()
                }
            })


        btnLogin.setOnClickListener {
            methods.replaceFragment(
                LoginFragment(),
                requireActivity().supportFragmentManager
            )
        }

        btnNext.setOnClickListener {

            if (txtUsuario.text!!.isEmpty()) {

                usuario.error = requireContext().getString(R.string.registerfragment_camponovacio)

            } else if (txtCorreo.text!!.isEmpty()) {

                correo.error = requireContext().getString(R.string.registerfragment_camponovacio)


            } else if (!isEmailValid(txtCorreo.text.toString())) {

                correo.error = requireContext().getString(R.string.registerfragment_formatoemail)

            } else if (txtContrasena.text!!.isEmpty()) {

                contrasena.error = requireContext().getString(R.string.registerfragment_camponovacio)

            } else if (!isPasswordValid(txtContrasena.text.toString())) {
                contrasena.error = requireContext().getString(R.string.registerfragment_formatocontra)
            } else if (txtCofirmarContrasena.text!!.isEmpty()) {

                confirmarContrasena.error = requireContext().getString(R.string.registerfragment_camponovacio)

            } else if (txtCofirmarContrasena.text.toString() != txtContrasena.text.toString()) {

                confirmarContrasena.error = requireContext().getString(R.string.registerfragment_coincidircontra)

            } else {
                animation.visibility = View.VISIBLE
                animation.playAnimation()
                database.comprobarEmailExiste(txtCorreo.text.toString()) { emailExiste ->
                    if (emailExiste) {
                        dialogs.dialogEmailExiste()
                    } else {
                        database.crearUsuario(
                            txtCorreo.text.toString(),
                            txtContrasena.text.toString(),
                            txtUsuario.text.toString(),
                            methods.generarCodigo(), requireContext()
                        )

                        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
                            val user = firebaseAuth.currentUser
                            if (user != null) {
                                animation.cancelAnimation()
                                animation.visibility = View.INVISIBLE
                                methods.replaceFragment(
                                    VerificationFragment(),
                                    requireActivity().supportFragmentManager
                                )
                            }
                        }

                    }
                }


            }
        }
    }

    fun comprobarCampos() {
        val color = ContextCompat.getColor(requireContext(), R.color.botones_background)
        val colorLight = ContextCompat.getColor(requireContext(), R.color.botones_disable)

        if (
            isEmailValid(txtCorreo.text.toString()) &&
            isPasswordValid(txtContrasena.text.toString()) &&
            txtUsuario.text!!.isNotEmpty() &&
            txtCofirmarContrasena.text.toString() == txtContrasena.text.toString()
        ) {

        } else {

        }

    }

    fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z](.*)(@)(.+)(\\.)(.+)")
        return emailRegex.matches(email)
    }

    fun isPasswordValid(clave: String): Boolean {

        return PASSWORD_PATTER.matcher(clave).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingA = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}