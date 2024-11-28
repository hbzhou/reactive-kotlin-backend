package com.itsz.reactive.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun dogBreedWebClient(@Value("\${dogBreed.api.baseUrl}") url:String): WebClient = WebClient.builder().baseUrl(url).build()
}