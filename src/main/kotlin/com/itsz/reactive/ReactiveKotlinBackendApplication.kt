package com.itsz.reactive

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition(info = Info(title = "Dog Breed Service", version = "1.0", description = "Template for Kotlin for Backend Developers mentoring programme"))
@SpringBootApplication
class ReactiveKotlinBackendApplication

fun main(args: Array<String>) {
	runApplication<ReactiveKotlinBackendApplication>(*args)
}
