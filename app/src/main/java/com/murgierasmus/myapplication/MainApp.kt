package com.murgierasmus.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.ui.activities.MainActivity
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import com.murgierasmus.myapplication.utils.interfaces.OnCodigoObtenidoListener
import com.murgierasmus.myapplication.utils.methods.Methods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainApp : AppCompatActivity() {
    private lateinit var methods: Methods
    private lateinit var dialogos: Dialogs
    private lateinit var database: Database
    override fun onStart() {
        super.onStart()
        methods = Methods(this)
        dialogos = Dialogs(this)
        database = Database()




        var codigoNotification: String = ""
        database.codigoUser(object :
            OnCodigoObtenidoListener {
            override fun onCodigoObtenido(codigo: String) {
                codigoNotification = codigo
            }
        })

        methods.abrirApp(this, supportFragmentManager, codigoNotification)
    }

    private suspend fun getDataFromFirestore(): MainActivity {
        return withContext(Dispatchers.IO) {
            val result = FirebaseFirestore.getInstance().collection("myCollection").document("myDocument").get().await()
            val myData = result.toObject(MainActivity::class.java)
            myData ?: throw Exception("No se pudo obtener los datos de Firebase Firestore")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        checkPermision(this)


    }

    private fun checkPermision(activity: Activity) {
        val cameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        val storagePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val ubicationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (cameraPermission != PackageManager.PERMISSION_GRANTED ||
            storagePermission != PackageManager.PERMISSION_GRANTED ||
            ubicationPermission != PackageManager.PERMISSION_GRANTED
        ) {
            dialogos.dialogoFaltaPermiso()
        } else {
            methods.openApp()
            Log.i("abrir app", "la abre desde checkpermision")
        }
    }

}