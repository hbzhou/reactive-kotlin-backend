package com.itsz.reactive.external

import com.itsz.reactive.service.DogBreedService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DogBreedDatabasePopulator(val dogBreedApiClient: DogBreedApiClient, val dogBreedService: DogBreedService) {


    @Async
    @EventListener(ApplicationReadyEvent::class)
    suspend fun initializeDatabase() {
        val existing = dogBreedService.getBreeds()
        if (existing.isEmpty()) {
            val dogBreeds = dogBreedApiClient.getBreeds()
            dogBreedService.save(dogBreeds)
        }
    }
}