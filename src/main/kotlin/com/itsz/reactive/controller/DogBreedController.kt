package com.itsz.reactive.controller

import com.itsz.reactive.model.DogBreed
import com.itsz.reactive.model.dto
import com.itsz.reactive.service.DogBreedService
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayInputStream

@Tag(name = "Dog Breed APIs", description = "Breed APIs for demo purpose")
@RestController
@RequestMapping("v1/breeds")
class DogBreedController(val dogBreedService: DogBreedService) {

    @GetMapping
    suspend fun getAllDogBreeds(
        @RequestParam(value = "isSubBreedEmpty") isSubBreedEmpty: Boolean?,
    ): List<DogBreedResponse> = dogBreedService.getBreeds()
        .filter { predicate(isSubBreedEmpty, it) }
        .map { it.dto() }

    private fun predicate(isSubBreedEmpty: Boolean?, dogBreed: DogBreed) =
        if (isSubBreedEmpty == null) true
        else if (isSubBreedEmpty) dogBreed.subBreed?.isEmpty() ?: true
        else dogBreed.subBreed?.isNotEmpty() ?: false

    @GetMapping("/subBreeds")
    suspend fun getAllDogSubBreeds(): DogSubBreedsResponse {
        val subBreeds = dogBreedService.getBreeds()
            .flatMap { (it.subBreed?.split(",") ?: emptyList()).asIterable() }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        return DogSubBreedsResponse(subBreeds)
    }

    @GetMapping("/{breed}/subBreeds")
    suspend fun getSubBreedsOfDog(@PathVariable breed: String): DogSubBreedsResponse {
        val subBreeds = dogBreedService.getSubBreeds(breed)
        return DogSubBreedsResponse(subBreeds)
    }

    @GetMapping("/{breed}/image")
    suspend fun getBreedImage(@PathVariable breed: String) : ResponseEntity<InputStreamResource> {
        val breedImage = dogBreedService.getBreedImage(breed)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$breed.jpg")
            .body(InputStreamResource(ByteArrayInputStream(breedImage)))
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DogBreedResponse(val breed: String, val subBreeds: List<String>)

data class DogSubBreedsResponse(val subBreeds: List<String>)
