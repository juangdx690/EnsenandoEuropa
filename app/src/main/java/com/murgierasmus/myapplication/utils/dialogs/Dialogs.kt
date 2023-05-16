package com.murgierasmus.myapplication.utils.dialogs
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

import com.murgierasmus.myapplication.R

class Dialogs(private val context: Context) {
    fun dialogoInternet() {

        AlertDialog.Builder(context)
            .setMessage(context.resources.getString(R.string.dialogs_nointernet))
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                (context as Activity).finish()
            }
            .setCancelable(false)
            .show()
    }

    fun dialogDesarrolladorJuan() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.devJuan))
        // Crea un LinearLayout para agregar los botones personalizados
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER

        linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))


        // Agrega el primer botón personalizado
        val icon1 = ContextCompat.getDrawable(context, R.drawable.icono_github)
        icon1?.setBounds(0, 0, 40, 40)
        val button1 = Button(context)
        button1.text = "Github"
        button1.setCompoundDrawables(null, icon1, null, null)
        button1.gravity = Gravity.CENTER
        button1.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        button1.setTextColor(ContextCompat.getColor(context, R.color.white))
        button1.setOnClickListener {
            val url = "https://github.com/juanbx690"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }
        linearLayout.addView(button1)

        // Agrega el segundo botón personalizado
        val icon2 = ContextCompat.getDrawable(context, R.drawable.icono_gmail)
        icon2?.setBounds(0, 0, 40, 40)
        val button2 = Button(context)
        button2.text = "Gmail"
        button2.setCompoundDrawables(null, icon2, null, null)
        button2.gravity = Gravity.CENTER
        button2.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        button2.setTextColor(ContextCompat.getColor(context, R.color.white))
        button2.setOnClickListener {
            val recipient = "juanbeasguadix@gmail.com"
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            }
                context.startActivity(intent)
        }

        linearLayout.addView(button2)

        // Agrega el tercer botón personalizado
        val icon3 = ContextCompat.getDrawable(context, R.drawable.icono_linkedin)
        icon3?.setBounds(0, 0, 40, 40)
        val button3 = Button(context)
        button3.text = "LinkedIn"
        button3.setCompoundDrawables(null, icon3, null, null)
        button3.gravity = Gravity.CENTER
        button3.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        button3.setTextColor(ContextCompat.getColor(context, R.color.white))
        button3.setOnClickListener {
            val url = "https://www.linkedin.com/in/juanamezcualopez/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)

        }
        linearLayout.addView(button3)

        builder.setView(linearLayout)


        builder.show()


    }
    fun dialogDesarrolladorDani() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.devDani))
        // Crea un LinearLayout para agregar los botones personalizados
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER

        linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))


        // Agrega el primer botón personalizado
        val icon1 = ContextCompat.getDrawable(context, R.drawable.icono_github)
        icon1?.setBounds(0, 0, 40, 40)
        val button1 = Button(context)
        button1.text = "Github"
        button1.setCompoundDrawables(null, icon1, null, null)
        button1.gravity = Gravity.CENTER
        button1.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        button1.setTextColor(ContextCompat.getColor(context, R.color.white))
        button1.setOnClickListener {
            val url = "https://github.com/DAMR17795"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }
        linearLayout.addView(button1)

        // Agrega el segundo botón personalizado
        val icon2 = ContextCompat.getDrawable(context, R.drawable.icono_gmail)
        icon2?.setBounds(0, 0, 40, 40)
        val button2 = Button(context)
        button2.text = "Gmail"
        button2.setCompoundDrawables(null, icon2, null, null)
        button2.gravity = Gravity.CENTER
        button2.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        button2.setTextColor(ContextCompat.getColor(context, R.color.white))
        button2.setOnClickListener {
            val recipient = "danimarom95@gmail.com"
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            }
                context.startActivity(intent)
        }

        linearLayout.addView(button2)

        // Agrega el tercer botón personalizado
        val icon3 = ContextCompat.getDrawable(context, R.drawable.icono_linkedin)
        icon3?.setBounds(0, 0, 40, 40)
        val button3 = Button(context)
        button3.text = "LinkedIn"
        button3.setCompoundDrawables(null, icon3, null, null)
        button3.gravity = Gravity.CENTER
        button3.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        button3.setTextColor(ContextCompat.getColor(context, R.color.white))
        button3.setOnClickListener {
            val url = "https://www.linkedin.com/in/daniel-alejandro-martín-romero-6a53b1258/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)

        }
        linearLayout.addView(button3)

        builder.setView(linearLayout)


        builder.show()


    }


    fun dialogoCamposDefault() {

        AlertDialog.Builder(context)
            .setMessage(context.resources.getString(R.string.dialogs_nocampo))
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()

            }
            .setCancelable(false)
            .show()

    }

    fun dialogCiudadAgregada() {

        AlertDialog.Builder(context)
            .setMessage("Ciudad agregada con exito.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                (context as Activity).finish()

            }
            .setCancelable(false)
            .show()

    }

    fun dialogEmailExiste() {

        AlertDialog.Builder(context)
            .setMessage(context.resources.getString(R.string.dialogs_emailexiste))
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()

    }

    fun dialogoFaltaPermiso() {

        AlertDialog.Builder(context)
            .setMessage(context.resources.getString(R.string.dialogs_faltanpermisos))
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()

                var packageName = context.packageName
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", packageName, null)
                context.startActivity(intent)
            }
            .setCancelable(false)
            .show()

    }

    fun falloInicio() {
        AlertDialog.Builder(context)
            .setMessage(context.resources.getString(R.string.dialogs_errorinicio))
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }


}


