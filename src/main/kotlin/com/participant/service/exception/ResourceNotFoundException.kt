package com.participant.service.exception

class ResourceNotFoundException(
    override val message: String
) : RuntimeException(message)