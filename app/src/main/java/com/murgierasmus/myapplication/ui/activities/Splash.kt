package com.murgierasmus.myapplication.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.murgierasmus.myapplication.MainApp
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import com.murgierasmus.myapplication.utils.interfaces.OnCodigoObtenidoListener
import com.murgierasmus.myapplication.utils.methods.Methods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Splash:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       lifecycleScope.launch{
           delay(500)
           val intent=Intent(this@Splash, MainApp::class.java)
           startActivity(intent)
       }
    }

}