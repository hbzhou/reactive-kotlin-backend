package com.itsz.reactive.external

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.nio.ByteBuffer
import kotlin.random.Random

@Component
class DogBreedApiClient(val dogBreedWebClient: WebClient) {

    suspend fun getBreeds(): Map<String, List<String>> = dogBreedWebClient.get()
        .uri("/breeds/list/all")
        .retrieve()
        .awaitBody<DogBreedApiResponse>()
        .message

    suspend fun getBreedImage(breed: String): ByteArray {
        val images = dogBreedWebClient.get().uri("/breed/${breed}/images")
            .retrieve()
            .awaitBody<BreedImageApiResponse>()
            .message

        val imageUrl = images[Random.nextInt(images.size)]
        val byteBuffer = dogBreedWebClient.get()
            .uri(imageUrl)
            .retrieve()
            .awaitBody<ByteBuffer>()
        return byteBuffer.array()
    }
}


data class DogBreedApiResponse(val message: Map<String, List<String>>)

data class BreedImageApiResponse(val message: List<String>)