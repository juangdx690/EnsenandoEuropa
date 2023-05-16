package com.murgierasmus.myapplication.ui.viemodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.databse.FirestoreService
import com.murgierasmus.myapplication.ui.fragments.LoginRegisterFragment
import com.murgierasmus.myapplication.ui.fragments.ProfileFragment
import com.murgierasmus.myapplication.utils.methods.Methods
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(application:Application) : ViewModel() {

    private var database: Database = Database()
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private var methods: Methods = Methods(context)


    private val _fragmentToOpen = MutableLiveData<String>()
    val fragmentToOpen: LiveData<String> = _fragmentToOpen
    private val _openFragment = MutableLiveData<Fragment?>()
    val openFragment: LiveData<Fragment?> = _openFragment





    init {
        if (database.isUserAuthenticated()){
            iniciarServicio()
            viewModelScope.launch {
                val codigoNotificacion =codigoUser()
                abrirFragment(codigoNotificacion)
                fragmentProfile(ProfileFragment())
            }

        }else{
            abrirFragment("")
            fragmentProfile(LoginRegisterFragment())

        }


    }







    fun fragmentProfile(fragment: Fragment) {
        _openFragment.value = fragment
    }



    fun abrirFragment(codigo:String){
        if (codigo == ""){
            _fragmentToOpen.value="HomeFragment()"
        }else{
            _fragmentToOpen.value="VerificationFragment()"
        }
    }

    suspend fun codigoUser():String{
        return database.getCodigoUser().await()
    }

    fun iniciarServicio(){
        val intent = Intent(context, FirestoreService::class.java)
        context.startService(intent)
    }


}




@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val application: Application):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}
