package com.murgierasmus.myapplication.data


import com.murgierasmus.myapplication.data.network.CiudadesService

import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.provider.CiudadesProvider

class CiudadesRepositorio {
    private val api = CiudadesService()

    suspend fun getAllCiudades():List<DatosCiudad2Item>{
        val response = api.getCiudades()
        CiudadesProvider.ciudades = response
        return response
    }
    suspend fun addCity():List<DatosCiudad2Item>{
        val response = api.getCiudades()
        CiudadesProvider.ciudades = response
        return response
    }
}