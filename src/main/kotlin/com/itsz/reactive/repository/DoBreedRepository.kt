package com.itsz.reactive.repository

import com.itsz.reactive.model.DogBreed
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DoBreedRepository:CoroutineCrudRepository<DogBreed, Long>{

    fun findByBreed(breed: String): Flow<DogBreed>
}