package com.murgierasmus.myapplication.dataclass
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImagenesSubir(
    var Foto: ByteArray,
    var ImagenActividad: ByteArray,
    var ImagenComida: ByteArray,
    var ImagenCultura: ByteArray,
    var ImagenRestaurante: ByteArray
): Parcelable