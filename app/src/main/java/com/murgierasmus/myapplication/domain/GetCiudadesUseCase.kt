package com.murgierasmus.myapplication.domain

import com.murgierasmus.myapplication.data.CiudadesRepositorio
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item


class GetCiudadesUseCase {
    private val repository = CiudadesRepositorio()

    suspend operator fun invoke(): List<DatosCiudad2Item>? = repository.getAllCiudades()

}