package com.murgierasmus.myapplication.data.network

import android.util.Log
import com.murgierasmus.myapplication.core.RetrofitHelper
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

class CiudadesService {

    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun getCiudades(): List<DatosCiudad2Item> {

        val myApi = retrofit.create(CiudadesApiService::class.java)

        return withContext(Dispatchers.IO) {
            val response = myApi.getCiudades("Ciudades.json/").execute()
            if (response.isSuccessful) {
                response.body()?.ciudades ?: emptyList()
            } else {
                Log.e("MainActivity", "Error al cargar los datos de la API")
                emptyList()
            }
        }
    }



}
