package com.murgierasmus.myapplication.ui.activities


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.ActivityMainBinding
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.databse.FirestoreService
import com.murgierasmus.myapplication.dataclass.DatosPerfil
import com.murgierasmus.myapplication.ui.fragments.*
import com.murgierasmus.myapplication.ui.viemodel.MainViewModel
import com.murgierasmus.myapplication.ui.viemodel.MainViewModelFactory
import com.murgierasmus.myapplication.utils.methods.Methods
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private var methods: Methods = Methods(this)
    private var database: Database = Database()
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())


    companion object {
        var instance: MainActivity? = null
        lateinit var context: Context
            private set
    }

    private var datosPerfil: DatosPerfil = DatosPerfil("", "", Uri.EMPTY, null)
    private lateinit var fragmentPantallaPerfil: Fragment
    private val recreateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.guiaviaje.action.RECREATE_ACTIVITY") {
                recreate()
            }
        }
    }

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        context=this
        instance = this
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intentFilter = IntentFilter("com.example.guiaviaje.action.RECREATE_ACTIVITY")
        registerReceiver(recreateReceiver, intentFilter)
        lifecycleScope.launch {
            cargarDatos()
        }
        saberCodigo()
        saberUser()
        toolbar = binding.toolbar
        listeners()


    }

    fun saberCodigo() {
        viewModel.fragmentToOpen.observe(this, Observer { fragmentName ->
            // Aquí actualizas el fragment que se muestra en la actividad
            when (fragmentName) {
                "HomeFragment()" -> methods.replaceFragment(HomeFragment(), supportFragmentManager)
                "VerificationFragment()" -> methods.replaceFragment(
                    VerificationFragment(),
                    supportFragmentManager
                )
                else -> Log.d("MainActivity", "Fragment name not recognized")
            }
        })
    }


    fun saberUser() {
        viewModel.openFragment.observe(this) { fragment ->
            fragment?.let {
                fragmentPantallaPerfil = fragment

            }
        }
    }


    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.example.guiaviaje.action.RECREATE_ACTIVITY") {
                recreate()
            }
        }
    }

    suspend fun cargarDatos() {
        datosPerfil = coroutineScope.async(Dispatchers.IO) {
            methods.devolverUsuarioActual()
        }.await()
    }

    override fun onDestroy() {
        stopFirestoreService()
        super.onDestroy()
    }



    override fun recreate() {
        super.recreate()
        val intent = Intent("RECREATE_ACTIVITY")
        sendBroadcast(intent)
    }

    suspend fun devolverDatos(): DatosPerfil {
        return datosPerfil
    }


    fun stopFirestoreService() {
        val intent = Intent(this, FirestoreService::class.java)
        stopService(intent)
    }

    fun setToolbarTitle(title: String) {

        val textView = TextView(this)

        textView.text = title
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textView.textSize = 20f
        textView.typeface = ResourcesCompat.getFont(this, R.font.title)
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)
        toolbar.removeAllViews()
        toolbar.addView(textView)
        val params = textView.layoutParams as Toolbar.LayoutParams
        params.gravity = Gravity.CENTER
        textView.layoutParams = params
    }

    override fun onStart() {
        super.onStart()
        Log.i("codigo", FirebaseAuth.getInstance().currentUser?.email.toString())

    }

    fun recargarProfileActivo() {

        if (binding.bottomNavigationView.menu.findItem(R.id.profile).isChecked) {
            methods.replaceFragment(ProfileFragment(), supportFragmentManager)
        }

    }

    private fun listeners() {

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {methods.replaceFragment(HomeFragment(), supportFragmentManager)
               }
                R.id.profile ->
                {
                    if (FirebaseAuth.getInstance().currentUser!=null){
                        methods.replaceFragment(ProfileFragment(), supportFragmentManager)
                    }else{
                        methods.replaceFragment(LoginRegisterFragment(), supportFragmentManager)
                    }

                }

                R.id.about -> {
                    methods.replaceFragment(AboutFragment(), supportFragmentManager)

                }
                else -> {
                    methods.replaceFragment(HomeFragment(), supportFragmentManager)

                }
            }
            true
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
            if (fragment is HomeFragment) {
                binding.bottomNavigationView.menu.findItem(R.id.home).isChecked = true
            } else if (fragment is LoginRegisterFragment || fragment is RegisterFragment
                || fragment is LoginFragment || fragment is VerificationFragment
                || fragment is ProfileFragment
            ) {
                binding.bottomNavigationView.menu.findItem(R.id.profile).isChecked = true
            } else if (fragment is AboutFragment) {
                binding.bottomNavigationView.menu.findItem(R.id.about).isChecked = true
            }

        }


    }
     fun recreateFragment(){
         methods.replaceFragment(ProfileFragment(),supportFragmentManager)
     }
}