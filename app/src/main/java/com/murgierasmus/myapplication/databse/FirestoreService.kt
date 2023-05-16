package com.murgierasmus.myapplication.databse

import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.ui.activities.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FirestoreService : Service() {
    private lateinit var docRef: DocumentReference
    private lateinit var db: FirebaseFirestore
    private var activity: MainActivity? = null
    private var contador = 0
    private lateinit var handler: Handler
    private var dialogShown = false
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getActivityInstance(): MainActivity? {
        return if (MainActivity.instance != null) {
            MainActivity.instance
        } else {
            null
        }
    }


    override fun onCreate() {
        super.onCreate()
        activity = getActivityInstance()
        Log.d(TAG, "onCreate")
        db = FirebaseFirestore.getInstance()
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        if (userID != null) {
            docRef = db.collection("users").document(userID)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val codigo = snapshot.getString("TIPO_CUENTA")
                    if (codigo != null) {
                        if (contador > 0 && !dialogShown) { // Mostramos el diálogo si contador es 0 y el diálogo no ha sido mostrado antes
                            Log.i("pito", "El valor de TIPO_CUENTA ha cambiado a $codigo")
                            admin()
                        }
                        dialogShown = false // Restablecemos dialogShown a false
                    }
                    contador++
                } else {
                    Log.d(TAG, "El documento no existe")
                }

            }
        }
        handler = Handler()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(broadcastReceiver, IntentFilter("RECREATE_ACTIVITY"))
        registerReceiver(stopServiceReceiver, IntentFilter("STOP_FIRESTORE_SERVICE"))
        return START_STICKY
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "RECREATE_ACTIVITY") {
                activity = getActivityInstance()
            }
        }
    }
    private val stopServiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "STOP_FIRESTORE_SERVICE") {
                stopFirestoreService()
            }
        }
    }

    fun stopFirestoreService() {
        stopSelf()
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(stopServiceReceiver)
        unregisterReceiver(broadcastReceiver)
        Log.d("destroy", "onDestroy")
    }

    fun admin() {
        if (!dialogShown) { // Solo mostramos el diálogo si no ha sido mostrado antes
            handler.post {
                AlertDialog.Builder(activity!!).apply {
                    setTitle(context.getString(R.string.firestoreservice_permiso))
                    setMessage(context.getString(R.string.firestoreservice_permisoOtorgado))
                    setCancelable(false)
                    setPositiveButton(android.R.string.ok) { _, _ ->
                        // Llamamos al método cargarDatos de la actividad MainActivity

                        GlobalScope.launch(Dispatchers.Main) {
                            delay(500L)
                            getActivityInstance()?.cargarDatos()
                            getActivityInstance()?.recargarProfileActivo()
                        }

                    }
                    create()
                    show()
                }
            }
            dialogShown = true // Marcamos que el diálogo ha sido mostrado
        }
    }



    fun isForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        return appProcesses.any { it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && it.processName == packageName }
    }

    // Comprueba si la actividad está en ejecución
    fun isActivityRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfos = activityManager.getRunningTasks(Int.MAX_VALUE)
        for (taskInfo in taskInfos) {
            if (taskInfo.topActivity?.className == "com.example.guiaviaje.MainActivity") {
                return true
            }
        }
        return false
    }


    companion object {
        private const val TAG = "FirestoreService"
    }
}
