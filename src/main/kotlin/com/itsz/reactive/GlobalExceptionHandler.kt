package com.itsz.reactive

import com.itsz.reactive.exception.BadRequestException
import com.itsz.reactive.exception.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.handler.ResponseStatusExceptionHandler
import reactor.core.publisher.Mono


@ControllerAdvice
class GlobalExceptionHandler: ResponseStatusExceptionHandler() {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<String>> {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message))
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException, exchange: ServerWebExchange?): Mono<ResponseEntity<String>> {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, exchange: ServerWebExchange?): Mono<ResponseEntity<String>> {
        return Mono.just(
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.message)
        )
    }
}