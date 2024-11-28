package com.itsz.reactive.service

import com.itsz.reactive.external.DogBreedApiClient
import com.itsz.reactive.model.DogBreed
import com.itsz.reactive.repository.DoBreedRepository
import kotlinx.coroutines.flow.*
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class DogBreedService(val dogBreedRepository: DoBreedRepository, val dogBreedApiClient: DogBreedApiClient) {

    suspend fun save(breeds: Map<String, List<String>>) {
        val dogBreeds = breeds.entries.map { (key, value) -> DogBreed(null, key, value.joinToString(","), null) }
        dogBreedRepository.saveAll(dogBreeds).collect()
    }

    @Cacheable
    suspend fun getBreeds() = dogBreedRepository.findAll().toList()

    suspend fun getSubBreeds(breed: String): List<String> {
        val dogBreed = dogBreedRepository.findByBreed(breed).toList().firstOrNull()
        val subBreeds = dogBreed?.subBreed?.split(",")?.filter { it.isNotEmpty() }?.map { it.trim() } ?: emptyList()
        return subBreeds
    }

    suspend fun getBreedImage(breed: String): ByteArray? {
        val dogBreed = dogBreedRepository.findByBreed(breed).toList().firstOrNull()
        dogBreed?.run {
            if (image == null) {
                val dogImage = dogBreedApiClient.getBreedImage(breed)
                this.image = dogImage
                dogBreedRepository.save(this)
            }
        }
        return dogBreed?.image
    }
}