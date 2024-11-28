package com.itsz.reactive.exception

import java.lang.RuntimeException

class BadRequestException(message: String?) : RuntimeException(message) {

    override val message: String?
        get() = super.message
}