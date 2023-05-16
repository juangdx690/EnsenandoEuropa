package com.murgierasmus.myapplication.utils.methods

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.dataclass.DatosPerfil
import com.murgierasmus.myapplication.ui.activities.MainActivity
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import java.util.*

class Methods(private val context: Context) {

    private val dialogos: Dialogs = Dialogs(context)
    private val database: Database = Database()

    @RequiresApi(Build.VERSION_CODES.M)
    fun abrirApp(activity: Activity, supportFragment: FragmentManager, codigoNotification: String) {
        if (!comprobarInternet()) {
            dialogos.dialogoInternet()
        } else {
          checkPermision(activity)

        }
    }

     suspend fun devolverUsuarioActual(): DatosPerfil {

        return database.perfilActual(context)

    }

    private fun checkPermision(activity: Activity) {
        val cameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        val storagePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val ubicationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (cameraPermission != PackageManager.PERMISSION_GRANTED ||
            storagePermission != PackageManager.PERMISSION_GRANTED ||
            ubicationPermission != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(activity)
        } else {
            openApp()
            Log.i("abrir app", "la abre desde checkpermision")
        }
    }



    fun openApp() {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }
    fun openAppWithCodigo(codigo:String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("CODIGO", codigo)
        }
        context.startActivity(intent)
    }

    private fun requestPermission(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            dialogos.dialogoFaltaPermiso()
        }
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            777
        )
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun comprobarInternet(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun generarCodigo(): String {
        val random = Random()
        return String.format("%06d", random.nextInt(999999))
    }
    fun replaceFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    fun replaceFragmentCiudad(fragment: Fragment, fragmentManager: FragmentManager) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contenedorCiudad, fragment)
        fragmentTransaction.commit()
    }
   fun replaceFragmentAdd(fragment: Fragment, fragmentManager: FragmentManager) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contenedorAgregar, fragment)
        fragmentTransaction.commit()
    }


    fun mostrarNotificacion(codigoNotification: String) {
        val titulo = context.resources.getString(R.string.methods_verification)
        val contenido = context.resources.getString(R.string.methods_codigoverification) + "$codigoNotification"
        val icono = R.drawable.ic_launcher_foreground

        val pattern = longArrayOf(0, 500, 250, 500, 250, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.resources.getString(R.string.methods_canalnotificacion)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", channelName, importance).apply {
                description = context.resources.getString(R.string.methods_descripcioncanalnotificacion)
            }
            // Registrar el canal con el sistema
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(context, "1")
            .setSmallIcon(icono)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, icono))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentText(codigoNotification)

        // Vibrar y sonar la notificación

        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(
                VibrationEffect.createWaveform(
                    pattern,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            // Deprecated en API 26
            v.vibrate(pattern, -1)
        }
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        builder.setSound(defaultSoundUri)

        // Mostrar notificación
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, builder.build())
        }
    }


}