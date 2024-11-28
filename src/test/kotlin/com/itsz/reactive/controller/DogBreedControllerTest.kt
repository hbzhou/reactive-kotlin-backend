package com.itsz.reactive.controller

import com.itsz.reactive.model.DogBreed
import com.itsz.reactive.service.DogBreedService
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DogBreedControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var dogBreedService: DogBreedService


    @Test
    fun getAllDogBreeds() {
        coEvery { dogBreedService.getBreeds() } returns listOf(
            DogBreed(1L, "australian", "kelpie,shepherd", null),
            DogBreed(2L, "bulldog", "boston,england,french", null),
            DogBreed(3L, "hound", "", null),
        )
        webTestClient.get().uri("/v1/breeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").value<List<DogBreed>>(hasSize(3))
            .jsonPath("$.[0].breed").isEqualTo("australian")
            .jsonPath("$.[0].subBreeds").isEqualTo(listOf("kelpie", "shepherd"))
            .jsonPath("$.[1].breed").value(`is`("bulldog"))
            .jsonPath("$.[1].subBreeds").isEqualTo(listOf("boston", "england", "french"))
            .jsonPath("$.[2].breed").value(`is`("hound"))
            .jsonPath("$.[2].subBreeds").value(equalTo(listOf<String>()))
    }

    @Test
    fun `get all dog breeds including the  sub breeds is not empty`() {
        coEvery { dogBreedService.getBreeds() } returns listOf(
            DogBreed(1L, "australian", "kelpie,shepherd", null),
            DogBreed(2L, "bulldog", "boston,england,french", null),
            DogBreed(3L, "hound", "", null),
        )
        webTestClient.get().uri { it.path("/v1/breeds").queryParam("isSubBreedEmpty", false).build() }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").value<List<DogBreed>>(hasSize(2))
            .jsonPath("$.[0].breed").isEqualTo("australian")
            .jsonPath("$.[0].subBreeds").isEqualTo(listOf("kelpie", "shepherd"))
            .jsonPath("$.[1].breed").value(`is`("bulldog"))
            .jsonPath("$.[1].subBreeds").isEqualTo(listOf("boston", "england", "french"))
    }

    @Test
    fun `get all dog breeds including the  sub breeds is empty`() {
        coEvery { dogBreedService.getBreeds() } returns listOf(
            DogBreed(1L, "australian", "kelpie,shepherd", null),
            DogBreed(2L, "bulldog", "boston,england,french", null),
            DogBreed(3L, "hound", "", null),
        )
        webTestClient.get().uri { it.path("/v1/breeds").queryParam("isSubBreedEmpty", true).build() }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").value<List<DogBreed>>(hasSize(1))
            .jsonPath("$.[0].breed").isEqualTo("hound")
            .jsonPath("$.[0].subBreeds").isEmpty()
    }

    @Test
    fun getAllDogSubBreeds() {
        coEvery { dogBreedService.getBreeds() } returns listOf(
            DogBreed(1L, "australian", "kelpie,shepherd", null),
            DogBreed(2L, "bulldog", "boston,england,french", null),
        )

        webTestClient.get().uri("/v1/breeds/subBreeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .json(
                """{
                          "subBreeds": [
                              "kelpie",
                              "shepherd",
                              "boston",
                              "england",
                              "french"
                          ]
                        }
                    """.trimIndent()
            )
    }


    @Test
    fun getSubBreedsOfDog() {
        coEvery { dogBreedService.getSubBreeds("australian") } returns listOf("kelpie", "shepherd")
        webTestClient.get().uri("/v1/breeds/australian/subBreeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .json(
                """
                        {
                          "subBreeds": [
                              "kelpie",
                              "shepherd"
                          ]
                        }
                    """.trimIndent()
            )
    }

    @Test
    fun getBreedImage() {
        val breed = "australian"
        val imageBytes = ByteArray(20)
        coEvery { dogBreedService.getBreedImage("australian") } returns imageBytes
        webTestClient.get().uri("/v1/breeds/$breed/image").accept(MediaType.APPLICATION_OCTET_STREAM)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
            .expectHeader().value(HttpHeaders.CONTENT_DISPOSITION, `is`("attachment; filename=$breed.jpg"))
            .expectBody().consumeWith { it.responseBody.shouldBe(imageBytes) }
    }

}