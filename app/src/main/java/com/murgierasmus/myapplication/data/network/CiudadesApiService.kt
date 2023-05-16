package com.murgierasmus.myapplication.data.network


import com.murgierasmus.myapplication.dataclass.CitiesResponse
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.dataclass.DatosCiudadResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface CiudadesApiService {

    @GET
     fun getCiudades(@Url url:String):Call<DatosCiudadResponse>

    @GET
    fun getMovies(@Url url:String): Call<CitiesResponse>
    @PUT
    fun agregarCiudad(@Url url:String, @Body ciudad: DatosCiudad2Item): Call<DatosCiudad2Item>

    @DELETE
    fun deleteCiudad(@Url url:String): Call<Void>
}