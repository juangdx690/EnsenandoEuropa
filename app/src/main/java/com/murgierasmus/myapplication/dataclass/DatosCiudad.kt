package com.murgierasmus.myapplication.dataclass

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize




@Parcelize
data class DatosCiudad(
    @SerializedName("Foto")val foto: String,
    @SerializedName("Nombre")val nombre: String,
    @SerializedName("Pais")val pais: String
):Parcelable

