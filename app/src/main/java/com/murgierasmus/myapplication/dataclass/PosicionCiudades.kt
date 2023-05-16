package com.murgierasmus.myapplication.dataclass

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class CitiesResponse(@SerializedName("Ciudad") val cities: List<PosicionCiudades>)

@Parcelize
data class PosicionCiudades(
    var Nombre: String
): Parcelable
