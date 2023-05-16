package com.murgierasmus.myapplication.ui.viemodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murgierasmus.myapplication.core.RetrofitHelper
import com.murgierasmus.myapplication.data.network.CiudadesApiService
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.dataclass.DatosCiudadResponse
import com.murgierasmus.myapplication.domain.GetCiudadesUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CiudadViewModel : ViewModel() {
    private val _ciudades = MutableLiveData<List<DatosCiudad2Item>?>()
    val ciudades: MutableLiveData<List<DatosCiudad2Item>?> = _ciudades

    private val getCiudadesUseCase = GetCiudadesUseCase()
    private val retrofit = RetrofitHelper.getRetrofit()
    val myApi = retrofit.create(CiudadesApiService::class.java)

    fun obtenerCiudades() {

        myApi.getCiudades("Ciudades.json/").enqueue(object : Callback<DatosCiudadResponse> {
            override fun onResponse(
                call: Call<DatosCiudadResponse>,
                response: Response<DatosCiudadResponse>
            ) {
                val cities = response.body()?.ciudades ?: emptyList()
                _ciudades.value = cities
            }
            override fun onFailure(call: Call<DatosCiudadResponse>, t: Throwable) {
                Log.e("MainActivity", "Error al cargar los datos de la API", t)
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        _ciudades.value = null
        ciudades.value = null
    }


}
