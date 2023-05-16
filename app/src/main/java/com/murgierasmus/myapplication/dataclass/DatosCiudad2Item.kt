package com.murgierasmus.myapplication.dataclass

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DatosCiudadResponse(@SerializedName("Ciudad") val ciudades: List<DatosCiudad2Item>)

@Parcelize
data class DatosCiudad2Item(
    var id:Int,
    var Introduccion: String,
    var CulturaLugares: String,
    var DescripcionCulturaLugares: String,
    var DescripcionActividad: String,
    var DescripcionComida: String,
    var DescripcionRestaurante: String,
    var Foto:String ,
    var Idioma: String,
    var ImagenActividad: String,
    var ImagenComida: String,
    var ImagenCultura: String,
    var ImagenRestaurante: String,
    var Nombre: String,
    var Pais: String,
    var TituloActividad: String,
    var TituloComida: String,
    var TituloRestaurante: String,
    var UbicacionLng1: Double,
    var UbicacionLng2: Double,
    var UbicacionString: String
):Parcelable