package com.itsz.reactive.service

import com.itsz.reactive.external.DogBreedApiClient
import com.itsz.reactive.model.DogBreed
import com.itsz.reactive.repository.DoBreedRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class DogBreedServiceTest {

    lateinit var service: DogBreedService
    lateinit var dogRepository: DoBreedRepository
    lateinit var dogApiClient: DogBreedApiClient

    @BeforeEach
    fun setup() {
        dogRepository = mockk<DoBreedRepository>()
        dogApiClient = mockk<DogBreedApiClient>()
        service = DogBreedService(dogRepository, dogApiClient)
    }


    @Test
    fun `test save dog breed service`(): Unit = runBlocking {
        val slot = slot<List<DogBreed>>()
        every { dogRepository.saveAll(capture(slot)) } returns flow {}
        service.save(mapOf("australian" to listOf("kelpie", "shepherd")))
        val captured = slot.captured
        captured shouldHaveSize 1
        captured[0].id shouldBe null
        captured[0].breed shouldBe "australian"
        captured[0].subBreed shouldBe "kelpie,shepherd"
        captured[0].image shouldBe null
    }

    @Test
    fun `test get subBreeds`(): Unit = runBlocking {
        every { dogRepository.findByBreed("australian") } returns flow {
            emit(
                DogBreed(
                    1L,
                    "australian",
                    "kelpie,shepherd",
                    null
                )
            )
        }
        val subBreeds = service.getSubBreeds("australian")
        subBreeds shouldHaveSize 2
        subBreeds shouldBe listOf("kelpie", "shepherd")
    }

    @Test
    fun `test get Breed Image`(): Unit = runBlocking {
        val dogBreed = DogBreed(
            1L,
            "australian",
            "kelpie,shepherd",
            null
        )
        val image = ByteArray(10)
            every { dogRepository.findByBreed("australian") } returns flow {
                emit(dogBreed)
            }
            coEvery { dogRepository.save(any<DogBreed>()) } returns dogBreed.apply { this.image = image }
            coEvery { dogApiClient.getBreedImage("australian") } returns image

            service.getBreedImage("australian") shouldBe image
        }
}