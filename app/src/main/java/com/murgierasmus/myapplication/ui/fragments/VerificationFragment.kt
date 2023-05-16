package com.murgierasmus.myapplication.ui.fragments

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.FragmentVerificationBinding
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.ui.activities.MainActivity
import com.murgierasmus.myapplication.utils.interfaces.OnCodigoObtenidoListener
import com.murgierasmus.myapplication.utils.methods.Methods
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private lateinit var animation: LottieAnimationView

@SuppressLint("StaticFieldLeak")
private lateinit var btnCerrar: Button

@SuppressLint("StaticFieldLeak")
private lateinit var btnComprobar: Button


@SuppressLint("StaticFieldLeak")
private lateinit var btnReenviar: Button

@SuppressLint("StaticFieldLeak")
private lateinit var contenedorButtons: LinearLayout


@SuppressLint("StaticFieldLeak")
private lateinit var database: Database

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentVerificationBinding? = null

@SuppressLint("StaticFieldLeak")
private lateinit var codigoa: TextInputLayout

@SuppressLint("StaticFieldLeak")
private lateinit var txtCodigo: TextInputEditText

@SuppressLint("StaticFieldLeak")
private lateinit var methods: Methods
private val binding get() = bindingA!!

private var codigoNotification: String = ""

class VerificationFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).setToolbarTitle(requireContext().getString(R.string.verificationfragment_verification))
        bindingA = FragmentVerificationBinding.inflate(inflater, container, false)
        methods = Methods(requireContext())
        database = Database()

        database.codigoUser(object : OnCodigoObtenidoListener {
            override fun onCodigoObtenido(codigo: String) {
                codigoNotification = codigo
                Log.i("codigo", "codigo agregago")
                Log.i("codigo", codigoNotification)
            }
        })


        CoroutineScope(Dispatchers.Default).launch {
            delay(2000L)
            withContext(Dispatchers.Main) {
                showData()
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            delay(2500L)
            withContext(Dispatchers.Main) {
                Log.i("codigo", "entra en la noti")

                mostrarNotificacion()
                delayButton()
            }
        }

        var container = binding.viewContainer

        animation = container.findViewById(R.id.animation_view)
        btnCerrar = container.findViewById(R.id.btnVerificacionCerrar)
        btnComprobar = container.findViewById(R.id.btnComprobarCodigo)
        btnReenviar = container.findViewById(R.id.btnReenviarCodigo)
        contenedorButtons = container.findViewById(R.id.contenedorButtons)
        codigoa = container.findViewById(R.id.Codigo)
        txtCodigo = container.findViewById(R.id.txtCodigo)

        listeners()

        return binding.root
    }

    private fun showData() {
        binding.viewLoading.isVisible = false
        binding.viewContainer.isVisible = true
    }

    private fun delayButton() {
        btnReenviar.isEnabled = false
        btnReenviar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.botones_background))
        val color = ContextCompat.getColor(requireContext(), R.color.botones_disable)
        CoroutineScope(Dispatchers.Default).launch {
            delay(5000L)
            withContext(Dispatchers.Main) {
                btnReenviar.isEnabled = true
                btnReenviar.setBackgroundColor(color)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun mostrarNotificacion() {
        methods.mostrarNotificacion(codigoNotification)
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val activeNotifications = notificationManager.activeNotifications


        val contentNotification =
            activeNotifications.firstOrNull { it.notification.extras.containsKey(Notification.EXTRA_TEXT) }


        val contentMessage =
            contentNotification?.notification?.extras?.get(Notification.EXTRA_TEXT) as? String
        Handler(Looper.getMainLooper()).postDelayed({
            txtCodigo.setText(contentMessage)
        }, 1500)
    }

    private fun listenerAnimacionTerminada() {

        animation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                btnCerrar.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {

            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }

        })


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun listeners() {
        btnCerrar.setOnClickListener {

            lifecycleScope.launch {
                val datosPerfil = async(Dispatchers.IO) {
                    database.actualizarCodigo()
                    (requireActivity() as MainActivity).cargarDatos()


                }.await()


                methods.replaceFragment(ProfileFragment(), requireActivity().supportFragmentManager)
            }


        }

        btnComprobar.setOnClickListener {

            if (codigoNotification.equals(txtCodigo.text.toString())) {
                listenerAnimacionTerminada()
                animation.setAnimation(R.raw.tickfinish)
                animation.repeatCount = 0
                animation.playAnimation()
                btnReenviar.visibility = View.GONE
                btnComprobar.visibility = View.GONE


            }

        }

        btnReenviar.setOnClickListener {
            mostrarNotificacion()
            delayButton()
        }
        txtCodigo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                codigoa.error = null

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VerificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
