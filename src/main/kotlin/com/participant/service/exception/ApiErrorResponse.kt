package com.participant.service.exception

import java.time.Instant

data class ApiErrorResponse(

    val timestamp: Instant = Instant.now(),

    val status: Int,

    val error: String,

    val message: String,

    val path: String
)