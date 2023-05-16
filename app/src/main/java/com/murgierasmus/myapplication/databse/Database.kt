package com.murgierasmus.myapplication.databse

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.dataclass.DatosPerfil
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import com.murgierasmus.myapplication.utils.interfaces.OnCodigoObtenidoListener
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Database {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user = getAuth().currentUser
    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var dialogos: Dialogs
    fun getAuth(): FirebaseAuth {
        return auth
    }

    fun getUserId(): String? {
        return user?.uid
    }


    fun getDatabase(): FirebaseFirestore {
        return database
    }

    fun crearUsuario(
        email: String,
        clave: String,
        usuario: String,
        codigo: String,
        context: Context
    ) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            email,
            clave
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                user = auth.currentUser

                val user = FirebaseAuth.getInstance().currentUser

                val imageId = R.drawable.logo_app

                val uri = Uri.parse("android.resource://" + context.packageName + "/" + imageId)


                var profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(usuario)
                    .setPhotoUri(uri)
                    .build()
                user?.updateProfile(profileUpdates)


                val bd = getDatabase()
                val userCollection = bd.collection("users")
                val userData = hashMapOf(
                    "TIPO_CUENTA" to "usuario",
                    "NOMBRE_USUARIO" to usuario,
                    "CODIGO" to codigo
                )
                userCollection.document(FirebaseAuth.getInstance().uid.toString())
                    .set(userData)
                    .addOnSuccessListener {
                        Log.i("usuario", "usuario creado")
                    }
                    .addOnFailureListener { e ->
                        Log.i("usuario", "usuario no creado: " + e.message.toString())
                    }
            }
        }


    }

    fun comprobarEmailExiste(email: String, onComplete: (Boolean) -> Unit) {
        getAuth().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task: Task<SignInMethodQueryResult> ->
                if (task.isSuccessful) {
                    val result = task.result
                    onComplete(result?.signInMethods?.isNotEmpty() ?: false)
                } else {
                    onComplete(false)
                }
            }
    }

    fun codigoUser(codigoListener: OnCodigoObtenidoListener) {
        Log.i("codigo", "entra al codigouser")
        val db = getDatabase()
        val collection = db.collection("users")
        user = auth.currentUser
        collection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val uid = document.id
                    Log.i(
                        "codigo",
                        "uid recorrido: " + uid + " uid user: " + FirebaseAuth.getInstance().uid.toString()
                    )
                    if (uid.equals(user?.uid)) {
                        val codigo = document.getString("CODIGO")
                        if (codigo != null) {
                            Log.i("codigo", "el codigo es: " + codigo)
                            codigoListener.onCodigoObtenido(codigo)
                            return@addOnSuccessListener
                        }
                    }
                }

                codigoListener.onCodigoObtenido("")
            }
            .addOnFailureListener { exception ->
                Log.i("TAG", "Error getting documents: ", exception)
                codigoListener.onCodigoObtenido("")
            }
    }


    suspend fun subirArchivo(ruta: String, byteArray: ByteArray): String = suspendCoroutine { continuation ->
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val filename = "$ruta.png"
        val fileRef = storageRef.child(filename)

        val uploadTask = fileRef.putBytes(byteArray)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result?.storage?.downloadUrl.toString()
                continuation.resume(downloadUrl)
            } else {
                val exception = task.exception
                continuation.resumeWithException(exception ?: Exception("Error desconocido al subir archivo"))
            }
        }
    }


    fun getCodigoUser(): Deferred<String> {
        val deferred = CompletableDeferred<String>()

        val db = getDatabase()
        val collection = db.collection("users")
        user = auth.currentUser
        collection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val uid = document.id
                    if (uid == user?.uid) {
                        val codigo = document.getString("CODIGO")
                        if (codigo != null) {
                            deferred.complete(codigo)
                            return@addOnSuccessListener
                        }
                    }
                }
                deferred.complete("")
            }
            .addOnFailureListener { exception ->
                deferred.completeExceptionally(exception)
            }

        return deferred
    }


    fun iniciarSesion(email: String, clave: String): Boolean {
        var isSuccessful = false
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, clave).addOnCompleteListener {
            if (it.isSuccessful) {
                isSuccessful = true
            } else {
                isSuccessful = false;
            }
        }
        return isSuccessful
    }

    fun isUserAuthenticated(): Boolean {
        // Obtener la instancia actual de FirebaseAuth
        val auth = FirebaseAuth.getInstance()

        // Obtener el usuario actualmente autenticado
        val user = auth.currentUser

        // Verificar si el usuario es diferente de nulo
        return user != null
    }

    suspend fun perfilActual(context: Context): DatosPerfil {
        val email = FirebaseAuth.getInstance().currentUser?.email
        val usuario = FirebaseAuth.getInstance().currentUser?.displayName
        val imgUri = FirebaseAuth.getInstance().currentUser?.photoUrl

        val imageId = R.drawable.cristiano

        // Obtener la URL de la imagen de perfil del recurso drawable
        val defaultImageUri = Uri.parse("android.resource://" + context.packageName + "/" + imageId)
        val imageUri = imgUri ?: defaultImageUri

        var tipoUsuario = ""

        tipoUsuario = tipoUser()

        return DatosPerfil(usuario ?: "", email ?: "", imageUri, tipoUsuario)
    }


    suspend fun tipoUser(): String {
        Log.i("codigo", "entra al codigouser")
        val db = getDatabase()
        val collection = db.collection("users")
        val user = auth.currentUser

        return try {
            val result = collection.get().await()
            for (document in result) {
                val uid = document.id
                Log.i("codigo", "uid recorrido: $uid uid user: ${FirebaseAuth.getInstance().uid}")
                if (uid == user?.uid) {
                    val codigo = document.getString("TIPO_CUENTA")
                    if (codigo != null) {
                        Log.i("codigo", "el codigo es: $codigo")
                        return codigo
                    }
                }
            }
            ""
        } catch (e: Exception) {
            Log.i("TAG", "Error getting documents: ", e)
            ""
        }
    }

    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null

    fun cerrarSesion(context: Context) {
        FirebaseAuth.getInstance().signOut()
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->

            detenerListener()
        }
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener!!)
    }

    fun detenerListener() {
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener!!)
    }

    fun actualizarCodigo() {
        val db = getDatabase()
        val collection = db.collection("users")
        user = auth.currentUser
        if (user != null) {
            val uid = user?.uid
            val documentRef = collection.document(uid.toString())
            val data: Map<String, Any> = hashMapOf(
                "CODIGO" to ""
            )
            documentRef.update(data)
                .addOnSuccessListener {
                    Log.i("galindo", "Codigo actualizado correctamente")
                }
                .addOnFailureListener { exception ->
                    Log.i("galindo", "Error updating document: ", exception)
                }
        } else {
            Log.i("TAG", "User is null")
        }
    }


}