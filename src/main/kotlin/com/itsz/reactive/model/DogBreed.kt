package com.itsz.reactive.model

import com.itsz.reactive.controller.DogBreedResponse
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class DogBreed(
    @Id
    val id: Long?,
    val breed: String,
    val subBreed: String?,
    var image: ByteArray?
)

fun DogBreed.dto() = DogBreedResponse(
    breed,
    subBreed?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toList() ?: emptyList()
)