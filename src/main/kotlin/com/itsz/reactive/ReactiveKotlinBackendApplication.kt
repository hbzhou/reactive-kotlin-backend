package com.itsz.reactive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactiveKotlinBackendApplication

fun main(args: Array<String>) {
	runApplication<ReactiveKotlinBackendApplication>(*args)
}
